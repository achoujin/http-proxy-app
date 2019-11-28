package com.example.jjh.http_proxy_app.test;

import com.example.jjh.http_proxy_app.server.HttpProxyServer;
import com.example.jjh.http_proxy_app.server.HttpProxyServerConfig;

public class MainTest {
    public static void main(String[] args) {
        // 开启线程创建一个网络代理器
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
    }
}
