package it.emperor.intervaltraining.ui.settings;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallbacks;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;
import it.emperor.intervaltraining.R;
import it.emperor.intervaltraining.models.Training;
import it.emperor.intervaltraining.ui.base.BaseActivity;

public class Act_Backup extends BaseActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    @BindView(R.id.backup_progress)
    ProgressBar mBackupProgress;
    @BindView(R.id.restore_progress)
    ProgressBar mRestoreProgress;

    @OnClick(R.id.backup)
    void b() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            backupRealm();
        } else {
            connectToGoogleApi();
        }
    }

    @OnClick(R.id.restore)
    void r1() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            restoreRealm();
        } else {
            connectToGoogleApi();
        }
    }

    // SUPPORT

    @Inject
    Realm realm;

    private GoogleApiClient mGoogleApiClient;
    private String mJsonRealm;

    private static final int REQUEST_RESOLVE_ERROR = 1001;

    // SYSTEM

    @Override
    protected int getLayoutId() {
        return R.layout.act_backup;
    }

    @Override
    protected void initVariables() {
    }

    @Override
    protected void loadParameters(Bundle extras) {

    }

    @Override
    protected void loadInfos(Bundle savedInstanceState) {

    }

    @Override
    protected void saveInfos(Bundle outState) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        connectToGoogleApi();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void initialize() {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_RESOLVE_ERROR:
                if (resultCode == RESULT_OK) {
                    mGoogleApiClient.connect();
                }
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (!connectionResult.hasResolution()) {
            GoogleApiAvailability.getInstance().getErrorDialog(this, connectionResult.getErrorCode(), 0).show();
        } else {
            try {
                connectionResult.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException ex) {
                Snackbar.make(getRootView(), getString(R.string.backup_connection_failed), Snackbar.LENGTH_LONG).show();
            }
        }
    }

    // FUNCTIONS

    private void connectToGoogleApi() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_APPFOLDER)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }

        mGoogleApiClient.connect();
    }

    private boolean getRealmJson() {
        RealmResults<Training> trainings = Training.getAllTraining(realm);
        if (trainings != null && trainings.size() != 0) {
            List<Training> objTraining = realm.copyFromRealm(trainings);
            mJsonRealm = new Gson().toJson(objTraining);
            return true;
        } else {
            mJsonRealm = null;
            return false;
        }
    }

    private DriveId findLastBackup() {
        DriveApi.MetadataBufferResult results = Drive.DriveApi.getAppFolder(mGoogleApiClient).listChildren(mGoogleApiClient).await();
        for (Metadata metadata : results.getMetadataBuffer()) {
            if (metadata.getMimeType() != null && metadata.getMimeType().equalsIgnoreCase("realmdatabase/json")) {
                return metadata.getDriveId();
            }
        }

        return null;
    }

    private void backupRealm() {
        if (!getRealmJson()) {
            Snackbar.make(getRootView(), getString(R.string.backup_no_trainings), Snackbar.LENGTH_LONG);
            return;
        }
        mBackupProgress.setVisibility(View.VISIBLE);
        Drive.DriveApi.newDriveContents(mGoogleApiClient).setResultCallback(new ResultCallbacks<DriveApi.DriveContentsResult>() {
            @Override
            public void onSuccess(@NonNull DriveApi.DriveContentsResult driveContentsResult) {
                if (driveContentsResult.getStatus().isSuccess()) {
                    createFile(driveContentsResult);
                }
            }

            @Override
            public void onFailure(@NonNull Status status) {
                mBackupProgress.setVisibility(View.GONE);
                Snackbar.make(getRootView(), getString(R.string.backup_failed), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void createFile(DriveApi.DriveContentsResult driveContentsResult) {
        final DriveContents driveContents = driveContentsResult.getDriveContents();

        new Thread() {
            @Override
            public void run() {
                final DriveId driveId = findLastBackup();

                OutputStream outputStream = driveContents.getOutputStream();
                Writer writer = new OutputStreamWriter(outputStream);
                try {
                    writer.write(mJsonRealm);
                } catch (IOException ex) {
                    ex.printStackTrace();
                } finally {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                        .setTitle("RealmDbBackup")
                        .setMimeType("realmdatabase/json")
                        .build();

                Drive.DriveApi.getAppFolder(mGoogleApiClient).createFile(mGoogleApiClient, changeSet, driveContents).setResultCallback(new ResultCallbacks<DriveFolder.DriveFileResult>() {
                    @Override
                    public void onSuccess(@NonNull DriveFolder.DriveFileResult driveFileResult) {
                        if (driveFileResult.getStatus().isSuccess()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mBackupProgress.setVisibility(View.GONE);
                                }
                            });
                            Snackbar.make(getRootView(), getString(R.string.backup_done), Snackbar.LENGTH_LONG).show();
                            if (driveId != null) {
                                driveId.asDriveFile().delete(mGoogleApiClient);
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Status status) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mBackupProgress.setVisibility(View.GONE);
                            }
                        });
                        Snackbar.make(getRootView(), getString(R.string.backup_restore_failed), Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        }.start();
    }

    private void restoreRealm() {
        mRestoreProgress.setVisibility(View.VISIBLE);
        readFile();
    }

    private void readFile() {
        new Thread() {
            @Override
            public void run() {
                DriveId driveId = findLastBackup();
                if (driveId != null) {
                    DriveApi.DriveContentsResult driveContentsResult = driveId.asDriveFile().open(mGoogleApiClient, DriveFile.MODE_READ_ONLY, null).await();
                    if (driveContentsResult.getStatus().isSuccess()) {
                        DriveContents driveContents = driveContentsResult.getDriveContents();

                        InputStream inputStream = driveContents.getInputStream();
                        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                        try {
                            final StringBuilder sb = new StringBuilder();

                            String text;
                            while ((text = br.readLine()) != null) {
                                sb.append(text);
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    restoreJson(sb.toString());
                                }
                            });
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        } finally {
                            try {
                                br.close();
                                inputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mRestoreProgress.setVisibility(View.GONE);
                            }
                        });
                        Snackbar.make(getRootView(), getString(R.string.backup_restore_failed), Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mRestoreProgress.setVisibility(View.GONE);
                        }
                    });
                    Snackbar.make(getRootView(), getString(R.string.backup_restore_not_found), Snackbar.LENGTH_LONG).show();
                }
            }
        }.start();
    }

    private void restoreJson(String json) {
        final List<Training> newTrainings = new Gson().fromJson(json, new TypeToken<ArrayList<Training>>() {
        }.getType());

        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<Training> trainings = Training.getAllTraining(realm);
                    if (trainings != null && trainings.size() != 0) {
                        for (Training training : trainings) {
                            training.getRepetitions().deleteAllFromRealm();
                        }
                        trainings.deleteAllFromRealm();
                    }

                    realm.copyToRealm(newTrainings);
                }
            });

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mRestoreProgress.setVisibility(View.GONE);
                }
            });
            Snackbar.make(getRootView(), getString(R.string.backup_restore_done), Snackbar.LENGTH_LONG).show();
        } catch (Exception ex) {
            ex.printStackTrace();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mRestoreProgress.setVisibility(View.GONE);
                }
            });
            Snackbar.make(getRootView(), getString(R.string.backup_restore_failed), Snackbar.LENGTH_LONG).show();
        }
    }
}
