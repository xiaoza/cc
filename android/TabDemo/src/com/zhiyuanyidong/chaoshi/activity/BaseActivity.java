package com.zhiyuanyidong.chaoshi.activity;

import com.zhiyuanyidong.chaoshi.callback.OnDialogDoneListener;
import com.zhiyuanyidong.chaoshi.fragment.AlertDialogFragment;
import com.zhiyuanyidong.chaoshi.fragment.PromptDialogFragment;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

/**
 * Created by clownfish on 14-4-5.
 */
public class BaseActivity extends FragmentActivity implements OnDialogDoneListener{
	
	public static final String TAG = "BaseActivity";
	
	public static String ALERT_DIALOG_TAG = "ALERT_DIALOG_TAG";
    public static String HELP_DIALOG_TAG = "HELP_DIALOG_TAG";
    public static String PROMPT_DIALOG_TAG = "PROMPT_DIALOG_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    
    private void testPromptDialog(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        PromptDialogFragment pdf = PromptDialogFragment.newInstance("Enter Something");
        pdf.show(ft, PROMPT_DIALOG_TAG);
    }

    private void testAlertDialog(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        AlertDialogFragment adf = AlertDialogFragment.newInstance("Alert Message");
        adf.show(ft, ALERT_DIALOG_TAG);
    }

    @Override
    public void onDialogDone(String tag, boolean cancelled, CharSequence message) {
        String s = tag + " responds with: " + message;
        if(cancelled) {
            s = tag + " was cancelled by the user";
        }
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }
}
