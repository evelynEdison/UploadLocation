package com.xutong.uploadlocation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText mIpET;
    private EditText mPortET;
    private Button mButton;
    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(this,UploadService.class));
        mPrefs = getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);

        mIpET = (EditText)findViewById(R.id.ip_et);
        mIpET.setText(mPrefs.getString(Constants.KEY_IP, Constants.DEFAULT_IP_ADDRESS));
        CharSequence ip = mIpET.getText();
        //Debug.asserts(text instanceof Spannable);
        if (ip instanceof Spannable) {
            Spannable spanText = (Spannable)ip;
            Selection.setSelection(spanText, ip.length());
        }


        mPortET = (EditText)findViewById(R.id.port_et);
        mPortET.setText(mPrefs.getString(Constants.KEY_PORT, Constants.DEFAULT_PORT));
        CharSequence port = mPortET.getText();
        if(port instanceof Spannable){
            Spannable spanText = (Spannable)port;
            Selection.setSelection(spanText, port.length());
        }


        mButton = (Button)findViewById(R.id.save_btn);
        mButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String ip = mIpET.getText().toString();
                String port = mPortET.getText().toString();
                if(!TextUtils.isEmpty(ip) && !TextUtils.isEmpty(port)){
                    SharedPreferences.Editor editor = mPrefs.edit();
                    editor.putString(Constants.KEY_IP, ip);
                    editor.putString(Constants.KEY_PORT, port);
                    boolean res = editor.commit();
                    Toast.makeText(MainActivity.this, res ? R.string.save_success : R.string.save_fail
                            , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this,UploadService.class));
    }
}
