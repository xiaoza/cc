package com.zhiyuanyidong.chaoshi.Fragment;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.zhiyuanyidong.chaoshi.Activity.MainActivity;
import com.zhiyuanyidong.chaoshi.Data.Shakespeare;

/**
 * Created by clownfish on 14-4-5.
 */
public class CategoryFragment extends ListFragment {

    private MainActivity mainActivity = null;
    private int mCurCheckPosition = 0;


    @Override
    public void onInflate(Activity activity, AttributeSet attrs, Bundle savedInstanceState) {
        Log.v(MainActivity.TAG, "in CategoryFragment onInflate. AttributeSet contains:");
        for (int i=0; i<attrs.getAttributeCount(); i++){
            Log.v(MainActivity.TAG, "    " + attrs.getAttributeName(i) +
                    " = " + attrs.getAttributeValue(i));
        }
        super.onInflate(activity, attrs, savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        Log.v(MainActivity.TAG, "in CategoryFragment onAttach; activity is: " + activity);
        super.onAttach(activity);
        this.mainActivity = (MainActivity)activity;
    }

    @Override
    public void onCreate(Bundle icicle) {
        Log.v(MainActivity.TAG, "in CategoryFragment onCreate. Bundle contains:");
        if(icicle != null) {
            for(String key : icicle.keySet()) {
                Log.v(MainActivity.TAG, "    " + key);
            }
        } else {
            Log.v(MainActivity.TAG, "    myBundle is null");
        }

        super.onCreate(icicle);
        if (icicle != null) {
            mCurCheckPosition = icicle.getInt("curChoice", 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater myInflater, ViewGroup container, Bundle icicle) {
        Log.v(MainActivity.TAG, "in CategoryFragment onCreateView. container is " + container);
        return super.onCreateView(myInflater, container, icicle);
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        Log.v(MainActivity.TAG, "in TitlesFragment onActivityCreated. icicle contains:");
        if(bundle != null) {
            for(String key : bundle.keySet()) {
                Log.v(MainActivity.TAG, "    " + key);
            }
        } else {
            Log.v(MainActivity.TAG, "    icicle is null");
        }
        super.onActivityCreated(bundle);

        // Populate list with our static array of titles.
        setListAdapter(new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                Shakespeare.TITLES
        ));

        ListView lv = getListView();
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lv.setSelection(mCurCheckPosition);

        mainActivity.showProducts(mCurCheckPosition);
    }

    @Override
    public void onStart() {
        Log.v(MainActivity.TAG, "in TitlesFragment onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.v(MainActivity.TAG, "in TitlesFragment onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.v(MainActivity.TAG, "in TitlesFragment onPause");
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle icicle) {
        Log.v(MainActivity.TAG, "in TitlesFragment onSaveInstanceState");
        super.onSaveInstanceState(icicle);
        icicle.putInt("curChoice", mCurCheckPosition);
    }

    @Override
    public void onListItemClick(ListView l, View v, int pos, long id) {
        Log.v(MainActivity.TAG, "in TitlesFragment onListItemClick. pos = "
                + pos);
        mainActivity.showProducts(pos);
        mCurCheckPosition = pos;
    }

    @Override
    public void onStop() {
        Log.v(MainActivity.TAG, "in TitlesFragment onStop");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.v(MainActivity.TAG, "in TitlesFragment onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.v(MainActivity.TAG, "in TitlesFragment onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.v(MainActivity.TAG, "in TitlesFragment onDetach");
        super.onDetach();
        mainActivity = null;
    }
}
