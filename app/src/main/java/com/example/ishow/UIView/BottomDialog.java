package com.example.ishow.UIView;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.example.ishow.R;
import com.example.ishow.Utils.PixlesUtils;

/**
 * Created by MRME on 2016-05-08.
 */
public class BottomDialog  {
    private Dialog dialog;
    private Button gallery;
    private Button captrue;
    private Button cancel;

    private void showPopDialog(Context context) {
        if (dialog==null){
            View view = View.inflate(context,R.layout.photo_choose_dialog, null);
            gallery = (Button) view.findViewById(R.id.media_gallery);
            captrue = (Button) view.findViewById(R.id.media_captrue);
            cancel = (Button) view.findViewById(R.id.media_cancel);
            dialog = new Dialog(context, R.style.transparentFrameWindowStyle);
            dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            Window window = dialog.getWindow();
            // 设置显示动画
            window.setWindowAnimations(R.style.main_menu_animstyle);
            WindowManager.LayoutParams wl = window.getAttributes();

            wl.x = 0;
            wl.y = PixlesUtils.getScreenHeightPixels(context);
            // 以下这两句是为了保证按钮可以水平满屏
            wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
            wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;

            // 设置显示位置
            dialog.onWindowAttributesChanged(wl);
            // 设置点击外围解散
            dialog.setCanceledOnTouchOutside(true);
        }
        dialog.show();
    }

}
