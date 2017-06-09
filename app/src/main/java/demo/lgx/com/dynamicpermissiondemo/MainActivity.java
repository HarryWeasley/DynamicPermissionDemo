package demo.lgx.com.dynamicpermissiondemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }


    public void MyClick(View v){
        switch (v.getId()){
            case R.id.test:
                startActivity(new Intent(this,DynamicActivity.class));
                break;
            case R.id.must:
                startActivity(new Intent(this,MustPermissionActivity.class));
                break;

        }

    }
}
