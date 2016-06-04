package com.example.ishow.Utils;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

import com.example.ishow.R;

/**
 * Created by MRME on 2016-03-31.
 */
public class ToastUtil {
    static Toast toast =null;
    public static void showToast(Context c, String s) {
        if (toast!=null)
              toast.cancel();
        toast = Toast.makeText(c,s,Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void makeSnack(View oldpassInput,String msg,Context context){
       Snackbar.make(oldpassInput,msg,Snackbar.LENGTH_LONG)
                .setAction(context.getString(R.string.zhen_zhidao), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                })
               .show();
    }
}
