package com.zhiyuanyidong.chaoshi.Activity;

import com.zhiyuanyidong.chaoshi.R;
import com.zhiyuanyidong.chaoshi.Callback.OnDialogDoneListener;
import com.zhiyuanyidong.chaoshi.Fragment.AlertDialogFragment;
import com.zhiyuanyidong.chaoshi.Fragment.PromptDialogFragment;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.Toast;
import Vendor.SlidingMenu.SlidingMenu;
import Vendor.SlidingMenu.app.SlidingFragmentActivity;

/**
 * Created by clownfish on 14-4-5.
 */
public class BaseActivity extends SlidingFragmentActivity implements OnDialogDoneListener{
	
	public static String ALERT_DIALOG_TAG = "ALERT_DIALOG_TAG";
    public static String HELP_DIALOG_TAG = "HELP_DIALOG_TAG";
    public static String PROMPT_DIALOG_TAG = "PROMPT_DIALOG_TAG";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set the Behind View
        setBehindContentView(R.layout.sliding_menu_frame);

        // customize the SlidingMenu
        SlidingMenu sm = getSlidingMenu();
        sm.setShadowWidthRes(R.dimen.shadow_width);
        sm.setShadowDrawable(R.drawable.slidingmenu_shadow);
        sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        sm.setFadeDegree(0.35f);
        sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        
//        setContentView(R.layout.content_frame);
//		getSupportFragmentManager()
//		.beginTransaction()
//		.replace(R.id.content_frame, new SampleListFragment())
//		.commit();

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
