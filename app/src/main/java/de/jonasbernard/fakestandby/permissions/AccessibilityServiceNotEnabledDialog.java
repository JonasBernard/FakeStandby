package android.jonas.fakestandby.permissions;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class AccessibilityServiceNotEnabledDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return PermissionUtils.getAccessibilityServiceNotEnabledAlertDialog(getContext());
    }
}
