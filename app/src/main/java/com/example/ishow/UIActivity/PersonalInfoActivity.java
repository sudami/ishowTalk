package com.example.ishow.UIActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.ishow.Application.iShowTalkApplication;
import com.example.ishow.BaseComponent.AppBaseCompatActivity;
import com.example.ishow.Bean.UserEntry;
import com.example.ishow.R;
import com.example.ishow.UIView.MaterialDialog;
import com.example.ishow.Utils.PixlesUtils;
import com.example.ishow.Utils.SharePrefrence;
import com.example.ishow.Utils.StorageUtils;
import com.example.ishow.Utils.ToastUtil;
import com.example.ishow.Xutils3.XHttpUtils;
import com.example.ishow.iShowConfig.iShowConfig;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by MRME on 2016-04-18.
 */
public class PersonalInfoActivity extends AppBaseCompatActivity implements View.OnClickListener {
    @Bind(R.id.course_title_name)
    TextView courseTitleName;
    @Bind(R.id.Toolbar)
    android.support.v7.widget.Toolbar Toolbar;
    @Bind(R.id.avart)
    ImageView avart;
    @Bind(R.id.nick)
    EditText nick;
    @Bind(R.id.sex)
    EditText sex;
    @Bind(R.id.phone)
    TextView phone;
    @Bind(R.id.mail)
    EditText mail;
    @Bind(R.id.school)
    TextView school;
    @Bind(R.id.claz)
    TextView claz;
    @Bind(R.id.sing)
    EditText sign;

    UserEntry entry = null;
    @Bind(R.id.toolbar_edit)
    TextView rightText;
    private String teacher_id ="";
    private String classid="";
    private boolean isTeacher;
    private String img;
    private String sdPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_psersonal_info);
        ButterKnife.bind(this);
        bindView2UI();
        subitPersonInfoAvart();
    }

    private void bindView2UI() {
        courseTitleName.setText(getString(R.string.personal_info));
        rightText.setText("提交");
        setSupportActionBar(Toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(PersonalInfoActivity.this,PersonalCenterActivity.class));
                finishActivityResult();
            }
        });
        rightText.setOnClickListener(this);
        avart.setOnClickListener(this);

        entry = SharePrefrence.getInstance().getStudentInfo(getApplicationContext());
        LogUtil.e(entry+"entry");
        if (entry != null) {
            nick.setText(entry.getName());
            sex.setText(entry.getSex());
            phone.setText(entry.getMobile());
            mail.setText(entry.getMail());
            school.setText(entry.getSchool());
            claz.setText(entry.getGrade());
            sign.setText(entry.getSign());

            teacher_id = entry.getTch_id();
            classid = entry.getClass_id();
            if ("0".equals(teacher_id)) {
                isTeacher = false;
            } else {
                isTeacher = true;
            }
            img = entry.getImg();
            if (img!=null)
                x.image().bind(avart,img, iShowTalkApplication.getInstance().getIgetImageOptions(this,60,8));
        }
    }

    private void finishActivityResult() {
        Intent intent =new Intent();
        setResult(100,intent);
        ActivityCompat.finishAfterTransition(PersonalInfoActivity.this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.toolbar_edit:
                checkEditextEmpty();
                break;
            case R.id.avart :
                Intent picImage = new Intent(Intent.ACTION_GET_CONTENT);
                picImage.addCategory(Intent.CATEGORY_OPENABLE);
                picImage.setType("image/*");
                startActivityForResult(picImage, 1);
                break;
        }
    }

    private void checkEditextEmpty() {
        if (TextUtils.equals(sex.getText().toString(),null))
            ToastUtil.makeSnack(sex,sex.getHint().toString(),this);
        else if(TextUtils.equals(mail.getText().toString(),null))
            ToastUtil.makeSnack(sex,mail.getHint().toString(),this);
        else if (TextUtils.equals(sign.getText().toString(),null))
            ToastUtil.makeSnack(sign,mail.getHint().toString(),this);
        else if (img==null||img=="")
            ToastUtil.makeSnack(sign,"亲,求图求真相呐~",this);
        else {
            if (img.contains("http:")){
                submitPnersonIfo();
            }
            else {
                subitPersonInfoAvart();
            }
        }

    }

    private void subitPersonInfoAvart() {
        dialog =new MaterialDialog();
        dialog.showDloag(this,getString(R.string.request_server));
        //ShowConfig.commitePersonAvart
        RequestParams params =new RequestParams(iShowConfig.uploadVideo);

       // Environment.getExternalStorageDirectory().getPath()+"/DCIM/111.mp4";
        File file =new File( Environment.getExternalStorageDirectory().getPath()+"/DCIM/111.mp4");
        LogUtil.e(file.getPath());
        if (file.exists()) {
            params.setMultipart(true);
            params.setAutoResume(true);
            params.addBodyParameter("filename",file);
            params.addBodyParameter("mediaTitle","2016-05-23");
            params.addBodyParameter("mediaDescription","2016-05-23-描述");
            params.addBodyParameter("mediaBelong","15555043403");
            params.addBodyParameter("meidaIsTest","true");
            params.addBodyParameter("mediaTeacherPhone","15555043403");
            x.http().post(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String s) {
                    LogUtil.e("onSuccess"+s);
                    try {
                        JSONObject o =new JSONObject(s);
                        if (o.getInt("code")==1){
                            img =o.getString("url");
                            submitPnersonIfo();
                        }else{
                            ToastUtil.makeSnack(avart,o.getString("msg"),PersonalInfoActivity.this);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        LogUtil.e(e.toString());
                        ToastUtil.makeSnack(avart, e.getMessage(),PersonalInfoActivity.this);
                    }
                }

                @Override
                public void onError(Throwable throwable, boolean b) {
                    LogUtil.e(throwable.getMessage());
                    ToastUtil.makeSnack(avart,throwable.getMessage(),PersonalInfoActivity.this);

                }

                @Override
                public void onCancelled(Callback.CancelledException e) {
                    LogUtil.e( e.getMessage());
                }

                @Override
                public void onFinished() {
                    dialog.cancelDialog();
                }
            });
        }else{
            ToastUtil.makeSnack(avart,getString(R.string.no_storage_permission),this);
        }

    }

    MaterialDialog dialog;
    private void submitPnersonIfo() {
        if (dialog==null){
            dialog =new MaterialDialog();
            dialog.showDloag(this,getString(R.string.request_server));
        }
        JSONObject oj = new JSONObject();
        try {
            oj.put("username", nick.getText().toString());
            oj.put("sex", sex.getText().toString().trim());
            oj.put("campus", school.getText().toString());
            oj.put("school", school.getText().toString());
            oj.put("phone", phone.getText().toString());
            oj.put("mail", mail.getText().toString());
            oj.put("img", img);
            //oj.put("cs_id", 0);
            oj.put("grade", claz.getText().toString());
            oj.put("sign", sign.getText().toString());
            if (isTeacher) {
                oj.put("class_id", classid);
                oj.put("tch_id", teacher_id);
            }

            XHttpUtils.getInstace().getValue(iShowConfig.commitePersonInfo, oj, new XHttpUtils.OnHttpCallBack() {
                @Override
                public void onSuccess(String result) {
                   LogUtil.e(result);
                    try {
                        JSONObject mJSONObject = new JSONObject(result);
                        int code = mJSONObject.getInt("code");
                        if (code == 1) {
                            SharePrefrence.getInstance().putStudentInfo(PersonalInfoActivity.this,mJSONObject.getString("data").toString());
                            ToastUtil.makeSnack(avart,"资料提交成功",PersonalInfoActivity.this);
                        }else{
                            ToastUtil.makeSnack(avart,mJSONObject.getString("msg")+"subimit failed,please reOpen and try again!",PersonalInfoActivity.this);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    dialog.cancelDialog();
                }

                @Override
                public void onError(String errorResson) {
                    dialog.cancelDialog();
                    ToastUtil.makeSnack(avart,errorResson,PersonalInfoActivity.this);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1){
            if (data!=null)
                if(data.getData()!=null)
                    startPhotoZoom(data.getData());
        }else if (requestCode==2){
           if (data!=null)
               if (data.hasExtra("data"))
               {
                   Bundle extras = data.getExtras();
                   if (extras != null) {
                       Bitmap photo = extras.getParcelable("data");
                       avart.setImageBitmap(photo);
                       img = sdPath = StorageUtils.getInstance().saveBtimap2SD(StorageUtils.getInstance().getPathNameDirs("avart"), "avart", this, photo);
                       LogUtil.e(img);
                   }
               }else{
                   ToastUtil.makeSnack(avart,"image too large!",this);
               }

        }

    }
    private void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 0.5);
        intent.putExtra("aspectY", 0.5);

        intent.putExtra("outputX", PixlesUtils.getScreenWidthPixels(this)/3);
        intent.putExtra("outputY",  PixlesUtils.getScreenWidthPixels(this)/3);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 2);
    }

    @Override
    public void onBackPressed() {
        finishActivityResult();
        super.onBackPressed();
    }
}
