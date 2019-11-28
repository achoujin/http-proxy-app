package com.example.jjh.http_proxy_app;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jjh.http_proxy_app.dao.SuidaoRepo;
import com.example.jjh.http_proxy_app.entity.Suidao;
import com.example.jjh.http_proxy_app.server.HttpProxyServer;
import com.example.jjh.http_proxy_app.server.HttpProxyServerConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import frpclib.Frpclib;
import io.netty.util.internal.StringUtil;


public class MainActivity extends AppCompatActivity {

    private String local_port = "11111";

    private String remote_port = "7071";

    private UserLoginTask mAuthTask = null;

    /**
     * 云端ip
     */
    private EditText etCloudIp;
    /**
     * 云端port
     */
    private EditText etCloudPort;
    /**
     * token
     */
    private String token = "proxy@2019";
//    private EditText etToken;
    /**
     * 应用连接远程端口
     */
    private int remotePort;
//    private EditText etRemotePort;
    /**
     * 用户名
     */
    private String userNm = "userNm";
//    private EditText etUserNm;
    /**
     * 是否开启了网络代理
     */
    private boolean isStartProxy;

    private int _Suidao_ID=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 通过资源索引获得界面控件实例
        etCloudIp = (EditText) findViewById(R.id.etCloudIp);
        etCloudPort = (EditText) findViewById(R.id.etCloudPort);
//        etToken = (EditText) findViewById(R.id.etToken);
//        etRemotePort = (EditText) findViewById(R.id.etRemotePort);
//        etUserNm = (EditText)findViewById(R.id.etUserNm);
        Button login = (Button)findViewById(R.id.btn_login);

        // 从数据库捞取数据初始化
        _Suidao_ID = 0;
        Intent intent = getIntent();
        isStartProxy = intent.getBooleanExtra("isStartProxy",false);
        SuidaoRepo repo = new SuidaoRepo(this);
        Suidao suidao;
        List<Suidao> suidaos = repo.getStudentList();
        if(null != suidaos && suidaos.size() > 0 && !StringUtil.isNullOrEmpty(suidaos.get(0).cloudIp)){
            suidao = suidaos.get(0);
            _Suidao_ID = suidao.suidao_ID;
            etCloudIp.setText(String.valueOf(suidao.cloudIp));
            etCloudPort.setText(String.valueOf(suidao.cloudPort));
//            etToken.setText(suidao.token);
//            etRemotePort.setText(String.valueOf(suidao.remotePort));
//            etUserNm.setText(suidao.userNm);
            remotePort = suidao.remotePort;
        }else {
            Log.i("remotePort","初始化，没有数据,随机取remotePort!!!");
            remotePort = (int)(Math.random()*(10000)+40000);
            Log.i("remotePort1",String.valueOf(remotePort));
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cloudIp = etCloudIp.getText().toString().trim();
                String cloudPort = etCloudPort.getText().toString().trim();
//                String token = etToken.getText().toString().trim();
//                String remotePort = etRemotePort.getText().toString().trim();
//                String userNm = etUserNm.getText().toString().trim();
                Pattern pattern = Pattern.compile("^[0-9]*$");
                Matcher matcher = pattern.matcher(cloudPort);
//                Matcher matcher1 = pattern.matcher(remotePort);
                if(!matcher.find()){
                    Toast.makeText(MainActivity.this,"云端端口设置不正确",Toast.LENGTH_SHORT).show();
                    return;
                }
//                if(!matcher1.find()){
//                    Toast.makeText(MainActivity.this,"应用端口设置不正确",Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                Integer remotePortInt = Integer.parseInt(remotePort);
//                if(remotePortInt > 50000 || remotePortInt < 40000){
//                    Toast.makeText(MainActivity.this,"应用端口范围在[40000-50000]",Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                saveToDb(cloudIp,Integer.parseInt(cloudPort),token,remotePortInt,userNm);
                saveToDb(cloudIp,Integer.parseInt(cloudPort),token,remotePort,userNm);
//                copyToSD("config.ini",cloudIp,cloudPort,token,remotePort,userNm);
                copyToSD("config.ini",cloudIp,cloudPort,token,String.valueOf(remotePort),userNm);
                frpStart();

                // 开启线程创建一个网络代理器
                if(!isStartProxy){
                    try{
                        new Thread(new Runnable(){
                            @Override
                            public void run() {
                                HttpProxyServerConfig config =  new HttpProxyServerConfig();
                                config.setHandleSsl(false);
                                new HttpProxyServer()
                                        .serverConfig(config)
                                        .start(11111);
                            }
                        }).start();
                        isStartProxy = true;
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    Log.i("proxy","网络代理已开启");
                }
                Intent intent = new Intent(MainActivity.this,success.class);
                //将参数放入intent
                intent.putExtra("cloudIp", cloudIp);
                intent.putExtra("remotePort", String.valueOf(remotePort));
                intent.putExtra("isStartProxy",isStartProxy);
                startActivity(intent);
            }
        });
    }

    /**
     * 写入配置文件 dbName = config.ini
     * @param dbName
     */
    private void copyToSD(String dbName,String cloudIp,String cloudPort,String vtoken,String remotePort,String userNm) {
        InputStream in = null;
        FileOutputStream out = null;

        //判断如果数据库已经拷贝成功，不需要再次拷贝
        File file = new File(this.getExternalFilesDir(null), dbName);
        if (!file.exists()) {
            try {
                file.createNewFile();
                AssetManager assets = getAssets();
                //2.读取数据资源
                in = assets.open(dbName);
                out = new FileOutputStream(file);
                //3.读写操作
                byte[] b = new byte[1024];//缓冲区域
                int len = -1; //保存读取的长度
                while ((len = in.read(b)) != -1) {
                    out.write(b, 0, len);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            out = new FileOutputStream(file,false);
            // 云端服务信息
            String common = "[common]\r\n";
            String server_addr = "server_addr = " + cloudIp + "\r\n";
            String server_port = "server_port = " + cloudPort + "\r\n";
            String token = "privilege_token = " + vtoken + "\r\n";
            String user = "user = " + userNm + "\r\n";
//            String admin_addr = "admin_addr = 0.0.0.0"+"\r\n";
//            String admin_port = "admin_port = 7400"+"\r\n";
//            String admin_user = "admin_user = admin"+"\r\n";
//            String admin_pwd = "admin_pwd = admin"+"\r\n";
//            String log_file = "log_file = /storage/emulated/0/Android/data/com.example.jjh.http_proxy_app/files/frpc.log"+"\r\n";
//            String log_level = "log_level = info"+"\r\n";
//            String log_max_days = "log_max_days = 3"+"\r\n";
//            String pool_count = "pool_count = 5"+"\r\n";
//            String tcp_mux = "tcp_mux = true"+"\r\n";
//            String login_fail_exit = "login_fail_exit = true"+"\r\n";
//            String protocol = "protocol = tcp"+"\r\n";
            out.write(common.getBytes());
            out.write(server_addr.getBytes());
            out.write(server_port.getBytes());
            out.write(token.getBytes());
            out.write(user.getBytes());
//            out.write(admin_addr.getBytes());
//            out.write(admin_port.getBytes());
//            out.write(admin_user.getBytes());
//            out.write(admin_pwd.getBytes());
//            out.write(log_file.getBytes());
//            out.write(log_level.getBytes());
//            out.write(log_max_days.getBytes());
//            out.write(pool_count.getBytes());
//            out.write(tcp_mux.getBytes());
//            out.write(login_fail_exit.getBytes());
//            out.write(protocol.getBytes());
            Random random = new Random();
            int ranNum = random.nextInt(20) + 1;
            // 端口映射信息
            String socket_name = "[tcp" + ranNum + "]\n";
            String socket_type = "type = tcp\r\n";
            String socket_ip = "local_ip = 127.0.0.1\r\n";
            String socket_port = "local_port =  " + local_port + "\r\n";
            String socket_remoteport = "remote_port = " + remotePort+ "\r\n";
            out.write(socket_name.getBytes());
            out.write(socket_type.getBytes());
            out.write(socket_ip.getBytes());
            out.write(socket_port.getBytes());
            out.write(socket_remoteport.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void frpStart() {
        if (mAuthTask != null) {
            return;
        }
        mAuthTask = new UserLoginTask(getExternalFilesDir(null) + "/config.ini");
        Log.e("path", getExternalFilesDir(null) + "/config.ini");//打印
        mAuthTask.execute((Void) null);//执行异步线程
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mConfigPath;

        UserLoginTask(String email) {
            mConfigPath = email;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Frpclib.run(mConfigPath);
            } catch (Throwable e) {
                if (e != null && e.getMessage() != null) {
                    Log.e("throwable", e.getMessage() + "");
                }

            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            //由于Frpclib.run(mConfigPath)该方法保持了长连接，所以这些方法都走不进去，只是摆设
            if (success) {
            } else {
            }
            mAuthTask = null;
//            finish();
        }

        @Override
        protected void onCancelled() {
            //由于Frpclib.run(mConfigPath)该方法保持了长连接，所以这些方法都走不进去，只是摆设，但退出程序会走这个方法
            Log.e("onCancelled", "+++++++");
            mAuthTask = null;
        }
    }


    private void saveToDb(String cloudIp,Integer cloudPort,String token,Integer remotePort,String userNm){
        try{
            Suidao suidao = new Suidao();
            suidao.cloudIp = cloudIp;
            suidao.cloudPort = cloudPort;
            suidao.token = token;
            suidao.remotePort = remotePort;
            suidao.userNm = userNm;
            suidao.suidao_ID = _Suidao_ID;
            SuidaoRepo repo = new SuidaoRepo(this);
            if(_Suidao_ID==0){
                _Suidao_ID = repo.insert(suidao);

                Toast.makeText(this,"保存成功",Toast.LENGTH_SHORT).show();
            }else{
                repo.update(suidao);
                Toast.makeText(this,"更新成功",Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
