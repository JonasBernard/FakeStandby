package android.jonas.fakestandby.onboarding;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.jonas.fakestandby.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.appintro.AppIntroBaseFragment;
import com.github.appintro.SlideBackgroundColorHolder;
import com.github.appintro.SlideSelectionListener;

import java.util.Objects;

public class WelcomeFragment extends Fragment implements SlideSelectionListener, SlideBackgroundColorHolder {

    private static final String ARG_TITLE = "title";
    private static final String ARG_DESC = "desc";
    private static final String ARG_DRAWABLE = "drawable";
    private static final String ARG_DESC_COLOR = "desc_color";

    AnimationDrawable animationDrawable;

    Context context;
    String title, description;
    int color;

    public WelcomeFragment(Context context, String title, String description, int imageDrawable, int color) {
        this.context = context;
        this.title = title;
        this.description = description;
        this.color = color;

        animationDrawable = (AnimationDrawable) ContextCompat.getDrawable(context, imageDrawable);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.appintro_fragment_intro, container);
        TextView title = (TextView) view.findViewById(R.id.title);
        ImageView image = (ImageView) view.findViewById(R.id.image);
        TextView description = (TextView) view.findViewById(R.id.description);

        title.setText(this.title);
        image.setImageDrawable(this.animationDrawable);
        description.setText(this.description);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onSlideDeselected() {
        animationDrawable.stop();
    }

    @Override
    public void onSlideSelected() {
        animationDrawable.start();
    }

    @Override
    public int getDefaultBackgroundColor() {
        return this.color;
    }

    @Override
    public void setBackgroundColor(int i) {
        this.color = i;
    }
}
