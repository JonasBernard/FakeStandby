package android.jonas.fakestandby.admanagement;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import android.jonas.fakestandby.service.AccessibilityOverlayService;
import android.jonas.fakestandby.utils.Constants;
import android.jonas.fakestandby.utils.Secrets;

public class AdManger {

    public static InterstitialAd ad;
    private static Context context;

    public static void init(Context context) {
        Log.i("AdManager", "Initializing ads");
        AdManger.context = context;

        MobileAds.initialize(context, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                Log.i("AdManager", "Initialized ads");
            }
        });
        ad = new InterstitialAd(context);
        ad.setAdUnitId(Secrets.Ads.BANNER_ID);
        ad.loadAd(new AdRequest.Builder().build());

        ad.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                ad.loadAd(new AdRequest.Builder().build());
            }
        });
    }

    public static void show() {
        if (AdManger.ad.isLoaded()) {
            AdManger.ad.show();
            Log.d("AdManager", "The interstitial shown right now.");
        } else {
            Log.d("AdManager", "The interstitial wasn't loaded yet.");

            Intent intent = new Intent(AdManger.context, AccessibilityOverlayService.class);
            intent.putExtra(Constants.Intent.Extra.OverlayAction.KEY, Constants.Intent.Extra.OverlayAction.SHOW);
            AdManger.context.startService(intent);

            Log.i("AdManager", "Sent intent to show overlay");
        }
    }

}
