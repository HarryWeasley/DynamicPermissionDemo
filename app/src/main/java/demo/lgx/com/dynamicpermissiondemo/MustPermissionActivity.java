package demo.lgx.com.dynamicpermissiondemo;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by Harry on 2017/6/8.
 */

public class MustPermissionActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {


    ImageView imageView;
    /**
     * 随便赋值的一个唯一标识码
     */
    public static final int WRITE_EXTERNAL_STORAGE = 100;
    private boolean isFirst = false;
    //权限参数
    String[] params = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                Toast.makeText(MustPermissionActivity.this, "保存成功", Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("MustPermissionActivity", "onCreate执行");
        setContentView(R.layout.activity_dynamit);
        isFirst = true;

        Button save = (Button) findViewById(R.id.save);
        imageView = (ImageView) findViewById(R.id.image);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPerm();
            }
        });


    }


    @Override
    protected void onResume() {
        super.onResume();
        if (isFirst) {
            //因为要通过一个Fragment来弹出弹出框，所以activity这里的onResume执行了两次，这里进行判断
            isFirst = false;
            if (!EasyPermissions.hasPermissions(this, params)) {
                EasyPermissions.requestPermissions(this, "需要读写本地权限", WRITE_EXTERNAL_STORAGE, params);
            }
        }
    }


    /**
     * 检查权限
     */
    @AfterPermissionGranted(WRITE_EXTERNAL_STORAGE)
    private void checkPerm() {

        if (EasyPermissions.hasPermissions(this, params)) {
            //已经获取到权限
            Toast.makeText(MustPermissionActivity.this, "获取到权限，正常进入", Toast.LENGTH_LONG).show();
        } else {
            EasyPermissions.requestPermissions(this, "需要读写本地权限", WRITE_EXTERNAL_STORAGE, params);
        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            //这个方法有个前提是，用户点击了“不再询问”后，才判断权限没有被获取到
            new AppSettingsDialog.Builder(this)
                    .setRationale("没有该权限，此应用程序可能无法正常工作。打开应用设置界面以修改应用权限")
                    .setTitle("必需权限")
                    .build()
                    .show();
        } else if (!EasyPermissions.hasPermissions(this, params)) {
            //这里响应的是除了AppSettingsDialog这个弹出框，剩下的两个弹出框被拒绝或者取消的效果
            finish();
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            if (!EasyPermissions.hasPermissions(this, params)) {
                //这里响应的是AppSettingsDialog点击取消按钮的效果
                finish();
            }
        }
    }

}
