package me.creese.sport.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

import me.creese.sport.R;

public class DialogFindGps extends DialogFragment {


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialogFindGps = null;

        dialogFindGps = new Dialog(getContext());
        dialogFindGps.setContentView(R.layout.dialog_find_gps);
        setCancelable(false);



        return dialogFindGps;
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
    }
}
