package android.jonas.fakestandby.onboarding;

import android.jonas.fakestandby.R;
import android.os.Bundle;
import android.util.TypedValue;

import androidx.fragment.app.Fragment;

import com.github.appintro.AppIntro;
import com.github.appintro.AppIntroFragment;
import com.github.appintro.AppIntroPageTransformerType;

public class OnBoardingActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTransformer(new AppIntroPageTransformerType.Parallax(1.0, -1.0, 2.0));
        setColorTransitionsEnabled(true);
        setWizardMode(true);
        setImmersiveMode();
        setSystemBackButtonLocked(true);

        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        int color = typedValue.data;

//        addSlide(new WelcomeFragment(
//                this,
//                getString(R.string.visit_website_title),
//                getString(R.string.visit_website_summary),
//                R.drawable.fakestandby_overlay_preview,
//                color
//        ));

        addSlide(AppIntroFragment.newInstance(
                getString(R.string.visit_website_title),
                getString(R.string.visit_website_summary),
                R.drawable.app_icon_adaptive,
                getColor(R.color.deep_orange)
        ));

        addSlide(AppIntroFragment.newInstance(
                getString(R.string.visit_website_title),
                getString(R.string.visit_website_summary),
                R.drawable.app_icon_adaptive,
                getColor(R.color.light_green)
        ));
    }

    @Override
    protected void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);

        finish();
    }
}
