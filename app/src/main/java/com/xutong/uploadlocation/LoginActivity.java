package com.xutong.uploadlocation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by Administrator on 2015/11/23.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener,CompoundButton.OnCheckedChangeListener{

    private EditText mUserET;
    private EditText mPwdET;
    private CheckBox mRemUserCB;
    private CheckBox mRemPwdCB;
    private Button mLoginBtn;
    private ProgressBar mProgressBar;

    private SharedPreferences mPrefs;

    private String mUserId;
    private String mPwd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mPrefs = getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        initView();
    }

    private void initView(){
        mProgressBar = (ProgressBar)findViewById(R.id.progressbar);
        mUserET = (EditText)findViewById(R.id.et_userId);
        mPwdET = (EditText)findViewById(R.id.et_input_pwd);
        mRemUserCB = (CheckBox)findViewById(R.id.remember_userid_cb);
        mRemPwdCB = (CheckBox)findViewById(R.id.remember_pwd_cb);

        mRemUserCB.setOnCheckedChangeListener(this);
        mRemPwdCB.setOnCheckedChangeListener(this);

        mLoginBtn = (Button)findViewById(R.id.login_btn);
        mLoginBtn.setOnClickListener(this);

        boolean isRemUserid = mPrefs.getBoolean(Constants.KEY_REM_USERID,false);
        mRemUserCB.setChecked(isRemUserid);
        if(isRemUserid){
            mUserET.setText(mPrefs.getString(Constants.KEY_USERID,""));
        }
        boolean isRemPwd = mPrefs.getBoolean(Constants.KEY_REM_PWD,false);
        mRemPwdCB.setChecked(isRemPwd);
        if(isRemPwd){
            mPwdET.setText(mPrefs.getString(Constants.KEY_PWD,""));
        }
    }

    private void savePreference(){
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(Constants.KEY_USERID,mUserET.getText().toString());
        editor.putString(Constants.KEY_PWD, mPwdET.getText().toString());
        editor.putBoolean(Constants.KEY_REM_USERID, mRemUserCB.isChecked());
        editor.putBoolean(Constants.KEY_REM_PWD,mRemPwdCB.isChecked());
        editor.commit();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.login_btn){
            login();
        }
    }

    private void login(){
        mUserId = mUserET.getText().toString();
        mPwd = mPwdET.getText().toString();

        if(TextUtils.isEmpty(mUserId)){
            mUserET.setError(getString(R.string.user_empty));
            return;
        }
        if(TextUtils.isEmpty(mPwd)){
            mPwdET.setError(getString(R.string.pwd_empty));
            return;
        }

        new LoginAsyncTask().execute();
    }

    private class LoginAsyncTask extends AsyncTask<Void,Void,Boolean>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean isSuccess = true;
            try {
                Object object = WebserviceUtil.userLogin(mUserId, mPwd);
                if (object != null && object instanceof SoapObject) {
                    String json = ((SoapObject) object).getProperty(0).toString();
                    LogUtil.i("Grace", "userLogin json = " + json);
                }
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return isSuccess;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            mProgressBar.setVisibility(View.GONE);
            Toast.makeText(LoginActivity.this,success?R.string.login_success:R.string.login_fail,Toast.LENGTH_SHORT).show();
            if(success){
                savePreference();
                startActivity(new Intent(LoginActivity.this,MainActivity.class));
                LoginActivity.this.finish();
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        if(id == R.id.remember_userid_cb){

        }else if(id == R.id.remember_pwd_cb){

        }
    }
}
