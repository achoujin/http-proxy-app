//package com.example.jjh.http_proxy_app.util;
//
//import java.io.OutputStream;
//import java.util.Vector;
//
//public class RootCmd {
//    //执行linux命令并且输出结果
//    protected static Vector execRootCmd(String paramString)
//    {
//        Vector localVector = new Vector();
//        try
//        {
//            Process localProcess = Runtime.getRuntime().exec("su ");//经过Root处理的android系统即有su命令
//            OutputStream localOutputStream = localProcess.getOutputStream();
//            DataOutputStream localDataOutputStream = new DataOutputStream(localOutputStream);
//            InputStream localInputStream = localProcess.getInputStream();
//            DataInputStream localDataInputStream = new DataInputStream(localInputStream);
//            String str1 = String.valueOf(paramString);
//            String str2 = str1 + "\n";
//            localDataOutputStream.writeBytes(str2);
//            localDataOutputStream.flush();
//            String str3 = localDataInputStream.readLine();
//            localVector.add(str3);
//            localDataOutputStream.writeBytes("exit\n");
//            localDataOutputStream.flush();
//            localProcess.waitFor();
//            return localVector;
//        }
//        catch (Exception localException)
//        {
//            localException.printStackTrace();
//        }
//    }
//    //执行linux命令但不关注结果输出
//    protected static int execRootCmdSilent(String paramString)
//    {
//        try
//        {
//            Process localProcess = Runtime.getRuntime().exec("su");
//            Object localObject = localProcess.getOutputStream();
//            DataOutputStream localDataOutputStream = new DataOutputStream((OutputStream)localObject);
//            String str = String.valueOf(paramString);
//            localObject = str + "\n";
//            localDataOutputStream.writeBytes((String)localObject);
//            localDataOutputStream.flush();
//            localDataOutputStream.writeBytes("exit\n");
//            localDataOutputStream.flush();
//            localProcess.waitFor();
//            localObject = localProcess.exitValue();
//            return localObject;
//        }
//        catch (Exception localException)
//        {
//            localException.printStackTrace();
//        }
//    }
//    //判断机器Android是否已经root，即是否获取root权限
//    protected static boolean haveRoot()
//    {
//        int i = execRootCmdSilent("echo test"); //通过执行测试命令来检测
//        if (i != -1)  return true;
//        retrun false;
//    }
//}
