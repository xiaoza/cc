package com.zhiyuanyidong.chaoshi.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhiyuanyidong.chaoshi.Activity.MainActivity;
import com.zhiyuanyidong.chaoshi.Data.Shakespeare;
import com.zhiyuanyidong.chaoshi.R;

/**
 * Created by clownfish on 14-4-5.
 */
public class ProductsFragment extends Fragment {

    public static final String BUNDLE_KEY = "categoryId";
    private int categoryId = 0;

    public static ProductsFragment newInstance(int categoryId){
        Log.v(MainActivity.TAG, "in ProductsFragment new Instance(" + categoryId + ")");

        ProductsFragment productsFragment = new ProductsFragment();
        Bundle args = new Bundle();
        args.putInt(BUNDLE_KEY, categoryId);

        productsFragment.setArguments(args);
        return productsFragment;
    }

    public static ProductsFragment newInstance(Bundle bundle){
        int categoryId = bundle.getInt(BUNDLE_KEY);
        return newInstance(categoryId);
    }

    public int getShowCategoryId(){
        return categoryId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(MainActivity.TAG, "in ProductsFragment onCreate");

        if (savedInstanceState != null){
            for (String key : savedInstanceState.keySet()){
                Log.v(MainActivity.TAG, "   "+key);
            }
        } else {
            Log.v(MainActivity.TAG, "savedInstanceState is null");
        }

        super.onCreate(savedInstanceState);

        categoryId = this.getArguments().getInt(BUNDLE_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(MainActivity.TAG, "on ProductsFragment onCreateView. Container ="+container);

        View view = inflater.inflate(R.layout.fragment_products, container, false);
        TextView textView = (TextView)view.findViewById(R.id.text1);
        textView.setText(Shakespeare.DIALOGUE[categoryId]);
        return view;
    }
}
