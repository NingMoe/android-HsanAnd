package com.hxsn.zzd.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.andbase.library.util.AbToastUtil;
import com.andbase.ssk.entity.User;
import com.andbase.ssk.utils.AndHttpRequest;
import com.andbase.ssk.utils.AndJsonUtils;
import com.andbase.ssk.utils.AndShared;
import com.hxsn.zzd.R;
import com.hxsn.zzd.TApplication;
import com.hxsn.zzd.utils.Const;


public class LoginActivity extends Activity implements View.OnClickListener {

    private Button btnLogin;
    private EditText edtUsername, edtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        addView();
        addListener();
    }

    private void addView() {
        btnLogin = (Button) findViewById(R.id.btn_login);
        edtUsername = (EditText) findViewById(R.id.edt_username);
        edtPassword = (EditText) findViewById(R.id.edt_password);
        if (TApplication.versionType != Const.RELEASE_VERTION) {
            edtUsername.setText("shouji");
            edtPassword.setText("123456");
        } else {
            String username = AndShared.getValue("username");
            edtUsername.setText(username);//显示上次登录过的用户名
        }

    }

    private void addListener() {
        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                String name = edtUsername.getText().toString();
                String psw = edtPassword.getText().toString();
                if (name.length() == 0) {
                    AbToastUtil.showToast(this, "用户名不能为空");
                    break;
                }
                if (psw.length() == 0) {
                    AbToastUtil.showToast(this, "密码不能为空");
                    break;
                }
                request(name, psw);
                break;
        }
    }

    private void request(String name, final String psw) {
        //LoadingDialog.showLoading(this);
        String channelId = AndShared.getValue(Const.CODE_CHANNEL_ID);
        String url = Const.URL_LOGIN + name + "&logpwd=" + psw + "&chid=" + channelId + "&devtype=3";
        new AndHttpRequest(this) {
            @Override
            public void getResponse(String response) {
                String code = AndJsonUtils.getCode(response);
                if(code.equals("200")){
                    User user = AndJsonUtils.getUser(response);
                    user.setPassword(psw);
                    user.setUserRole("");
                    TApplication.user = user;
                    AndShared.saveUser(user);
                    AndShared.setValue("login","1");
                    Intent intent = new Intent();
                    // TApplication.defaultGreenHouse = Shared.getGreenHouse();
                    if(TextUtils.isEmpty(TApplication.defaultGreenHouse.getId()) ){
                        intent.setClass(LoginActivity.this, MoreActivity.class);
                        startActivity(intent);
                    }else {
                        intent.setClass(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }else if(code.equals("301")){
                    AbToastUtil.showToast(LoginActivity.this,"用户名不存在或密码错误");
                }else if(code.equals("302")){
                    AbToastUtil.showToast(LoginActivity.this,"该账号被管理员停用");
                } else {
                    AbToastUtil.showToast(LoginActivity.this,"登录错误");
                }
            }
        }.doPost(url,null);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
