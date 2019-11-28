package com.example.jjh.http_proxy_app.entity;

public class Suidao {
    //表名
    public static final String TABLE="Suidao";

    //表的各个域名
    public static final String KEY_ID="id";
    public static final String KEY_cloud_ip="cloud_ip";
    public static final String KEY_cloud_port="cloud_port";
    public static final String KEY_token="token";
    public static final String KEY_remote_port="remote_port";
    public static final String KEY_user_nm = "user_nm";

    //属性
    public int suidao_ID;
    public String cloudIp;
    public int cloudPort;
    public String token;
    public int remotePort;
    public String userNm;
}
