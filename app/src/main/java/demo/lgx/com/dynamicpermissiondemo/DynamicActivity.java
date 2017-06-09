package demo.lgx.com.dynamicpermissiondemo;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by Harry on 2017/6/8.
 */

public class DynamicActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{

    ImageView imageView;
    /**
     * 随便赋值的一个唯一标识码
     */
    public static final int WRITE_EXTERNAL_STORAGE=100;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                Toast.makeText(DynamicActivity.this, "保存成功", Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamit);

        Button save = (Button) findViewById(R.id.save);
        imageView = (ImageView) findViewById(R.id.image);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPerm();
            }
        });


    }

    /**
     * 检查权限
     */
    @AfterPermissionGranted(WRITE_EXTERNAL_STORAGE)
    private void checkPerm() {
        String[] params={Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if(EasyPermissions.hasPermissions(this,params)){
            saveImage();
        }else{
            EasyPermissions.requestPermissions(this,"需要读写本地权限",WRITE_EXTERNAL_STORAGE,params);
        }


    }

    private void saveImage() {

        //请忽略我这个子线程的优化问题，这里主要是为了实现动态权限的功能，哈哈！
        new Thread(new Runnable() {
            @Override
            public void run() {
                //获取到bitmap
                BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                //创建文件夹保存图片
                File file = DynamicActivity.this.getExternalFilesDir(null).getAbsoluteFile();
                String imageName = "test.jpg";
                File imageFile = new File(file, imageName);
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(imageFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                //保存成功后发送message
                handler.sendEmptyMessage(1);


            }
        }).start();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        //如果checkPerm方法，没有注解AfterPermissionGranted，也可以在这里调用该方法。

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            //这里需要重新设置Rationale和title，否则默认是英文格式
            new AppSettingsDialog.Builder(this)
                    .setRationale("没有该权限，此应用程序可能无法正常工作。打开应用设置界面以修改应用权限")
                    .setTitle("必需权限")
                    .build()
                    .show();
        }

    }
}
