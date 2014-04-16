package com.zhiyuanyidong.chaoshi.Activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.zhiyuanyidong.chaoshi.Callback.OnDialogDoneListener;
import com.zhiyuanyidong.chaoshi.Fragment.AlertDialogFragment;
import com.zhiyuanyidong.chaoshi.Fragment.ProductsFragment;
import com.zhiyuanyidong.chaoshi.Fragment.PromptDialogFragment;
import com.zhiyuanyidong.chaoshi.R;

public class MainActivity extends BaseActivity{

    public static final String TAG = "MainActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentManager.enableDebugLogging(true);
        setContentView(R.layout.activity_main);
        //testAlertDialog();

    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        Log.v(TAG, "in MainActivity onAttachFragment. fragment id = "
                + fragment.getId());
        super.onAttachFragment(fragment);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.v(TAG, "in MainActivity onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    public void showProducts(int categoryId){
        Log.v(TAG, "in MainActivity showProducts(" + categoryId + ")");

        // Check what fragment is shown, replace if needed.
        ProductsFragment products = (ProductsFragment)
                getFragmentManager().findFragmentById(R.id.product_list);
        if (products == null || products.getShowCategoryId() != categoryId) {
            // Make new fragment to show this selection.
            products = ProductsFragment.newInstance(categoryId);

            Log.v(TAG, "about to run FragmentTransaction...");
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
            ft.replace(R.id.product_list, products);
            ft.commit();
        }
    }

}
