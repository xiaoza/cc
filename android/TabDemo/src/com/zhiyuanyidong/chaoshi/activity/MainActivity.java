package com.zhiyuanyidong.chaoshi.activity;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.zhiyuanyidong.chaoshi.R;
import com.zhiyuanyidong.chaoshi.fragment.CartFragment;
import com.zhiyuanyidong.chaoshi.fragment.HomeFragment;
import com.zhiyuanyidong.chaoshi.fragment.MineFragment;

public class MainActivity extends BaseActivity{
	
	public static final String TAG = "MainActivity";
	
	private boolean mLogShown;
	
	private RadioGroup mTabRadio;
	private TabManager mTabManager;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_with_tab);
        
        mTabRadio=(RadioGroup)findViewById(R.id.main_tab_radio);
        mTabManager = new TabManager(this, mTabRadio, R.id.realtabcontent);
        
        mTabManager.addFragment(getResources().getString(R.string.tab_home), HomeFragment.class, null);
        mTabManager.addFragment(getResources().getString(R.string.tab_cart), CartFragment.class, null);
        mTabManager.addFragment(getResources().getString(R.string.tab_mine), MineFragment.class, null);
        
        if (savedInstanceState != null) {
        	RadioButton rado = (RadioButton)findViewById(savedInstanceState.getInt("tab"));
        	rado.setChecked(true);
        } else {
        	mTabManager.switchFragmen(getResources().getString(R.string.tab_home));
        	mTabRadio.check(R.id.radio_tab_home);
        }
    }
	
	@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("tab", mTabRadio.getCheckedRadioButtonId());
    }
	
	public static class TabManager implements OnCheckedChangeListener {
		
        private final FragmentActivity mActivity;
        private final RadioGroup mRadioGroup;
        private final int mContainerId;
        private final Map<String, TabInfo> mTabs = new HashMap<String, MainActivity.TabManager.TabInfo>();
        private TabInfo mLastTabInfo;

        static final class TabInfo {
        	
            private final String tag;
            private final Class<?> clss;
            private final Bundle args;
            private Fragment fragment;

            TabInfo(String _tag, Class<?> _class, Bundle _args) {
            	tag = _tag;
                clss = _class;
                args = _args;
            }
        }

        public TabManager(FragmentActivity activity, RadioGroup radioGroup, int containerId) {
            mActivity = activity;
            mRadioGroup = radioGroup;
            mContainerId = containerId;
            mRadioGroup.setOnCheckedChangeListener(this);
        }

        public void addFragment(String tag, Class<?> clss, Bundle args) {

            TabInfo info = new TabInfo(tag, clss, args);

            if (info.fragment != null && !info.fragment.isDetached()) {
            	FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction(); 
                ft.detach(info.fragment);
                ft.commit();
            }

            mTabs.put(tag, info);
        }
        
        public void switchFragmen(String tag) {  
            TabInfo curTabInfo = mTabs.get(tag);
        	if(curTabInfo == mLastTabInfo) return;
        	
        	FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
        	
            if (mLastTabInfo != null) {
                if (mLastTabInfo.fragment != null) {
                    ft.detach(mLastTabInfo.fragment);
                }
            }
            if (curTabInfo != null) {
                if (curTabInfo.fragment == null) {
                	curTabInfo.fragment = Fragment.instantiate(mActivity, curTabInfo.clss.getName(), curTabInfo.args);
                    ft.add(mContainerId, curTabInfo.fragment, curTabInfo.tag);
                } else {
                    ft.attach(curTabInfo.fragment);
                }
            }

            mLastTabInfo = curTabInfo;
            ft.commit();
        } 
        
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch(checkedId){
            case R.id.radio_tab_home:
            	switchFragmen(mActivity.getResources().getString(R.string.tab_home));
                break;
            case R.id.radio_tab_cart:
            	switchFragmen(mActivity.getResources().getString(R.string.tab_cart));
                break;
            case R.id.radio_tab_mine:
            	switchFragmen(mActivity.getResources().getString(R.string.tab_mine));
                break;
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem logToggle = menu.findItem(R.id.menu_toggle_log);
//        logToggle.setVisible(findViewById(R.id.sample_output) instanceof ViewAnimator);
//        logToggle.setTitle(mLogShown ? R.string.hide_log : R.string.show_log);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_toggle_log:
                mLogShown = !mLogShown;
//                ViewAnimator output = (ViewAnimator) findViewById(R.id.sample_output);
//                if (mLogShown) {
//                    output.setDisplayedChild(1);
//                } else {
//                    output.setDisplayedChild(0);
//                }
                supportInvalidateOptionsMenu();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
