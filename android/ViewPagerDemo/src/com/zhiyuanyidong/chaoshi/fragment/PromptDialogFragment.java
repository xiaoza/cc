package com.zhiyuanyidong.chaoshi.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.zhiyuanyidong.chaoshi.activity.MainActivity;
import com.zhiyuanyidong.chaoshi.callback.OnDialogDoneListener;
import com.zhiyuanyidong.chaoshi.R;

/**
 * Created by clownfish on 14-4-7.
 */
public class PromptDialogFragment extends DialogFragment implements View.OnClickListener{

    public static final String PROMPT = "prompt";
    public static final String INPUT = "input";

    private EditText editText;

    public static PromptDialogFragment newInstance(String prompt){
        PromptDialogFragment dialog = new PromptDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PROMPT,prompt);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onAttach(Activity activity) {
        try{
            OnDialogDoneListener listener = (OnDialogDoneListener)activity;
        } catch (ClassCastException e) {
//            Log.e(MainActivity.TAG, "Activity is not implement Listening");
        }
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setCancelable(true);
        int style = DialogFragment.STYLE_NORMAL;
        int theme = 0;
        this.setStyle(style,theme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_prompt_dialog,container,false);

        TextView tv = (TextView)v.findViewById(R.id.prompt_message);
        tv.setText(getArguments().getString(PROMPT));

        Button disMissBtn = (Button)v.findViewById(R.id.btn_dismiss);
        disMissBtn.setOnClickListener(this);

        Button saveBtn = (Button)v.findViewById(R.id.btn_save);
        saveBtn.setOnClickListener(this);

        Button helpBtn = (Button)v.findViewById(R.id.btn_help);
        helpBtn.setOnClickListener(this);

        editText = (EditText)v.findViewById(R.id.input_text);
        if (savedInstanceState != null){
            editText.setText(savedInstanceState.getCharSequence(INPUT));
        }

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence(INPUT, editText.getText());
    }

    @Override
    public void onClick(View v) {
        OnDialogDoneListener act = (OnDialogDoneListener)getActivity();
        if (v.getId() == R.id.btn_save) {
            TextView tv = (TextView)getView().findViewById(R.id.input_text);
            act.onDialogDone(this.getTag(), false, tv.getText());
            dismiss();
            return;
        }
        if (v.getId() == R.id.btn_dismiss) {
            act.onDialogDone(this.getTag(), true, null);
            dismiss();
            return;
        }
        if (v.getId() == R.id.btn_help){
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.remove(this);

            // in this case, we want to show the help text, but
            // come back to the previous dialog when we're done
            ft.addToBackStack(null);
            //null represents no name for the back stack transaction

//            HelpDialogFragment hdf = HelpDialogFragment.newInstance(R.string.help1);
//            hdf.show(ft, MainActivity.HELP_DIALOG_TAG);
            return;
        }
    }
}
