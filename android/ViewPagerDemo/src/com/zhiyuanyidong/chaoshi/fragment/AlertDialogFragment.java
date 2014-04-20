package com.zhiyuanyidong.chaoshi.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import com.zhiyuanyidong.chaoshi.activity.MainActivity;
import com.zhiyuanyidong.chaoshi.callback.OnDialogDoneListener;

/**
 * Created by clownfish on 14-4-11.
 */
public class AlertDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {

    public static final String ALERT_MESSAGE = "alert_message";

    public static AlertDialogFragment newInstance(String message){
        AlertDialogFragment adf = new AlertDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ALERT_MESSAGE, message);
        adf.setArguments(bundle);

        return adf;
    }

    @Override
    public void onAttach(Activity act) {
        try {
            OnDialogDoneListener test = (OnDialogDoneListener)act;
        } catch(ClassCastException cce) {
        }
        super.onAttach(act);
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        this.setCancelable(true);
        int style = DialogFragment.STYLE_NORMAL, theme = 0;
        setStyle(style,theme);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder b =
                new AlertDialog.Builder(getActivity())
                        .setTitle("Alert!!")
                        .setPositiveButton("Ok", this)
                        .setNegativeButton("Cancel", this)
                        .setMessage(this.getArguments().getString(ALERT_MESSAGE));
        return b.create();
    }

    public void onClick(DialogInterface dialog, int which){
        OnDialogDoneListener act = (OnDialogDoneListener) getActivity();
        boolean cancelled = false;
        if (which == AlertDialog.BUTTON_NEGATIVE){
            cancelled = true;
        }
        act.onDialogDone(getTag(), cancelled, "Alert dismissed");
    }
}
