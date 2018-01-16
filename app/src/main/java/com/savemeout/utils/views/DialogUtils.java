package com.savemeout.utils.views;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.TextView;

import com.savemeout.R;

/**
 * Created by binod on 8/1/18.
 */

public class DialogUtils {
    public static ProgressDialog showLoadingDialog(Context context, String text) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.show();
        if (progressDialog.getWindow() != null) {
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        if (text != null || !text.isEmpty()) {
            TextView textView = (TextView) progressDialog.findViewById(R.id.text);
            textView.setText(text);
            textView.setVisibility(View.VISIBLE);
        }
        return progressDialog;
    }
}
