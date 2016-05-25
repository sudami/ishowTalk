package com.example.ishow.UIView;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ishow.R;
import com.example.ishow.Utils.PixlesUtils;

import butterknife.Bind;

/**
 * Created by MRME on 2016-04-11.
 */
public class MaterialDialog {
    private static MaterialDialog materialDialog;
    TextView dialogTips;
    private Dialog dialog;

    public static MaterialDialog getInstance() {
        if (materialDialog == null)
            materialDialog = new MaterialDialog();
        return materialDialog;
    }

    public void showDloag(Context context, String tips) {
        View v = View.inflate(context, R.layout.progressbar, null);
        dialog = new Dialog(context, R.style.dialog);
        dialog.setContentView(v,new ViewGroup.LayoutParams(PixlesUtils.dip2px(context,160),PixlesUtils.dip2px(context,160)));
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialogTips = (TextView) v.findViewById(R.id.dialog_tips);
        dialogTips.setText(tips);
        dialog.show();
    }

    public boolean isShow(){
        return dialog==null?false:dialog.isShowing();
    }
    public void cancelDialog() {
      if (dialog!=null)
          dialog.dismiss();
    }
    public void setCancelable(boolean enable){
        dialog.setCancelable(enable);
    }
}
