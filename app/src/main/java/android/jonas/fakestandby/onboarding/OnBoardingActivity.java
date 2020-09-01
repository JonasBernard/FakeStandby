package android.jonas.fakestandby.onboarding;

import android.Manifest;
import android.jonas.fakestandby.R;
import android.os.Bundle;

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

        addSlide(AppIntroFragment.newInstance(
                getString(R.string.visit_website_title),
                getString(R.string.visit_website_summary),
                R.drawable.app_icon_adaptive,
                getColor(R.color.aqua)
        ));
    }

    @Override
    protected void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);

        finish();
    }
}
