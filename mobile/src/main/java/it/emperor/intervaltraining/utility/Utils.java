package it.emperor.intervaltraining.utility;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.Display;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import it.emperor.intervaltraining.R;

public class Utils {

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
    }

    public static float dpToPx(float dp, Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (dp * scale);
    }

    public static boolean isLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int screenWidth(Context context) {
        Display display;
        if (context instanceof Activity)
            display = ((Activity) context).getWindowManager().getDefaultDisplay();
        else
            display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    public static int getColorFromType(Context context, int type) {
        int color = 0;

        switch (type) {
            case Constants.COLOR_RED:
                color = ContextCompat.getColor(context, R.color.colorSelectorRed);
                break;
            case Constants.COLOR_ORANGE:
                color = ContextCompat.getColor(context, R.color.colorSelectorOrange);
                break;
            case Constants.COLOR_PURPLE:
                color = ContextCompat.getColor(context, R.color.colorSelectorPurple);
                break;
            case Constants.COLOR_GREEN:
                color = ContextCompat.getColor(context, R.color.colorSelectorGreen);
                break;
            case Constants.COLOR_BLUE:
                color = ContextCompat.getColor(context, R.color.colorSelectorBlue);
                break;
            case Constants.COLOR_CYAN:
                color = ContextCompat.getColor(context, R.color.colorSelectorCyan);
                break;
            case Constants.COLOR_BROWN:
                color = ContextCompat.getColor(context, R.color.colorSelectorBrown);
                break;
            case Constants.COLOR_GRAY:
                color = ContextCompat.getColor(context, R.color.colorSelectorGray);
                break;
            default:
                color = ContextCompat.getColor(context, R.color.colorSelectorRed);
                break;
        }

        return color;
    }

    public static AdView createAdView(Context context) {
        AdView adView = new AdView(context);
        adView.setAdUnitId(context.getString(R.string.app_banner_id));
        adView.setAdSize(AdSize.SMART_BANNER);

        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice("3D5EFE452407403448FFE848F6F90933")
                .build();

        adView.loadAd(adRequest);

        return adView;
    }
}
