package com.mysports.android;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.mysports.android.bomb.User;
import com.mysports.android.map.LocationUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;


public class MainActivity extends AppCompatActivity {

    private MapView mapView;

    private LocationUtil locationUtil;

    private EditText account;
    private EditText password;

    private Button login;
    private Button register;

    private CheckBox autoLogin;

    private SharedPreferences preferences;

    private SharedPreferences.Editor editor;

    private void init() {
        account = (EditText) findViewById(R.id.account);
        password = (EditText) findViewById(R.id.password);

        login = (Button) findViewById(R.id.login);
        register = (Button) findViewById(R.id.register);

        autoLogin = (CheckBox) findViewById(R.id.auto_login);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bmob.initialize(this,"4ba682d489877d786b2139b0db2e4af9");
        setContentView(R.layout.activity_main);
        //Bmob.initialize(this,"4ba682d489877d786b2139b0db2e4af9");

        init();
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User();
                user.setUsername(account.getText().toString().trim());
                user.setPassword(password.getText().toString().trim());
                user.signUp(new SaveListener<User>() {
                    @Override
                    public void done(User user, BmobException e) {
                        if (e == null){
                            Toast.makeText(MainActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(MainActivity.this,"注册失败:"+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User();
                user.setUsername(account.getText().toString().trim());
                user.setPassword(password.getText().toString().trim());
                user.login(new SaveListener<User>() {
                    @Override
                    public void done(User user, BmobException e) {
                        if (e == null){
                            editor = preferences.edit();
                            if (autoLogin.isChecked()) {
                                editor.putBoolean("auto",true);
                            }else {
                                editor.clear();
                            }
                            editor.apply();
                            Toast.makeText(MainActivity.this,"登陆成功",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this,IndexActivity.class);
                            intent.putExtra("name",user.getUsername());
                            startActivity(intent);
                            finish();
                        }else {
                            Toast.makeText(MainActivity.this,"登陆失败:"+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        //权限申请
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String [] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
        } else {
            //locationUtil.requestLocation();
        }

        boolean isAuto = preferences.getBoolean("auto",false);
        if (isAuto) {
            autoLogin.setChecked(true);
        }
        if (autoLogin.isChecked()) {
            aleradeLogin();
        }


    }

    private void aleradeLogin() {
        if (BmobUser.isLogin()) {
            User user = BmobUser.getCurrentUser(User.class);
            Intent intent = new Intent(MainActivity.this,IndexActivity.class);
            intent.putExtra("name",user.getUsername());
            Toast.makeText(MainActivity.this,"自动登陆",Toast.LENGTH_SHORT).show();
            startActivity(intent);
            finish();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    //locationUtil.requestLocation();
                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

}
