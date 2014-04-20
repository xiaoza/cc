package com.zhiyuanyidong.chaoshi.fragment;

import java.util.ArrayList;
import java.util.List;

import com.zhiyuanyidong.chaoshi.R;

import common.view.SlidingTabLayout;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SlidingTabsBasicFragment extends Fragment {
	
    static final String TAG = "SlidingTabsBasicFragment";

    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;
    private List<SamplePagerItem> mTabs = new ArrayList<SamplePagerItem>();
    
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mTabs.add(new SamplePagerItem(
                getString(R.string.tab_home), Color.BLUE, Color.GRAY
        ));

        mTabs.add(new SamplePagerItem(
                getString(R.string.tab_cart), Color.RED, Color.GRAY
        ));

        mTabs.add(new SamplePagerItem(
                getString(R.string.tab_mine), Color.YELLOW, Color.GRAY
        ));

	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sample, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mViewPager.setAdapter(new SampleFragmentPagerAdapter(getChildFragmentManager()));
        
        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);
        
        mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {

            @Override
            public int getIndicatorColor(int position) {
                return mTabs.get(position).getIndicatorColor();
            }

            @Override
            public int getDividerColor(int position) {
                return mTabs.get(position).getDividerColor();
            }

        });
    }
    
    static class SamplePagerItem {
        private final CharSequence mTitle;
        private final int mIndicatorColor;
        private final int mDividerColor;

        SamplePagerItem(CharSequence title, int indicatorColor, int dividerColor) {
            mTitle = title;
            mIndicatorColor = indicatorColor;
            mDividerColor = dividerColor;
        }

        Fragment createFragment() {
            return ContentFragment.newInstance(mTitle, mIndicatorColor, mDividerColor);
        }

        CharSequence getTitle() {
            return mTitle;
        }

        int getIndicatorColor() {
            return mIndicatorColor;
        }

        int getDividerColor() {
            return mDividerColor;
        }
    }
    
    class SampleFragmentPagerAdapter extends FragmentPagerAdapter {
    	
        public SampleFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
        public int getCount() {
            return mTabs.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
        	return mTabs.get(position).getTitle();
        }

		@Override
		public Fragment getItem(int position) {
			return mTabs.get(position).createFragment();
		}
    }
}
