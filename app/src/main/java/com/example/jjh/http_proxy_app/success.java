package com.example.jjh.http_proxy_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class success extends AppCompatActivity {

    private String cloudIp;

    private String remotePort;

    private TextView tvAddress;

    private boolean isStartProxy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);
        Intent intent = getIntent();
        cloudIp = intent.getStringExtra("cloudIp");
        remotePort = intent.getStringExtra("remotePort");
        isStartProxy = intent.getBooleanExtra("isStartProxy",false);
        Log.i("cloudIp::",cloudIp);
        Log.i("remotePort::",remotePort);
        String address = cloudIp + ":" + remotePort;
        tvAddress = (TextView) findViewById(R.id.tvAddress);
        tvAddress.setText(address);

        Button back = (Button)findViewById(R.id.btn_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(success.this,MainActivity.class);
                intent.putExtra("isStartProxy",isStartProxy);
                startActivity(intent);
                //跳转完以后关闭本界面
                finish();
            }
        });
    }

}
