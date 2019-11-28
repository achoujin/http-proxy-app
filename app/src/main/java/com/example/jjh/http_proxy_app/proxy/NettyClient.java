//package com.example.jjh.http_proxy_app.proxy;
//
//import com.example.jjh.http_proxy_app.exception.HttpProxyExceptionHandle;
//import com.example.jjh.http_proxy_app.handler.HttpProxyInitializer;
//import com.example.jjh.http_proxy_app.handler.HttpProxyServerHandle;
//import com.example.jjh.http_proxy_app.intercept.HttpProxyInterceptInitializer;
//import com.example.jjh.http_proxy_app.server.HttpProxyServerConfig;
//import com.example.jjh.http_proxy_app.util.ProtoUtil_1;
//
//import java.net.InetSocketAddress;
//import java.util.concurrent.ArrayBlockingQueue;
//import java.util.concurrent.TimeUnit;
//
//import io.netty.bootstrap.Bootstrap;
//import io.netty.channel.Channel;
//import io.netty.channel.ChannelFuture;
//import io.netty.channel.ChannelFutureListener;
//import io.netty.channel.ChannelInitializer;
//import io.netty.channel.ChannelOption;
//import io.netty.channel.ChannelPipeline;
//import io.netty.channel.EventLoopGroup;
//import io.netty.channel.nio.NioEventLoopGroup;
//import io.netty.channel.socket.SocketChannel;
//import io.netty.channel.socket.nio.NioSocketChannel;
//import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
//import io.netty.handler.codec.LengthFieldPrepender;
//import io.netty.handler.codec.http.HttpClientCodec;
//import io.netty.handler.codec.http.HttpObjectAggregator;
//import io.netty.handler.codec.http.HttpServerCodec;
//import io.netty.handler.codec.string.StringDecoder;
//import io.netty.handler.codec.string.StringEncoder;
//import io.netty.handler.logging.LogLevel;
//import io.netty.handler.logging.LoggingHandler;
//import io.netty.handler.timeout.IdleStateHandler;
//import io.netty.util.CharsetUtil;
//
//public class NettyClient {
//    public static final int DISCONNECTION = 0;
//    public static final int CONNECTING = 1;
//    public static final int CONNECTED = 2;
//
//    private EventLoopGroup group = null;
//    private Bootstrap bootstrap = null;
//    private ChannelFuture channelFuture = null;
//    private static NettyClient nettyClient = null;
//    private ArrayBlockingQueue<String> sendQueue = new ArrayBlockingQueue<String>(5000);
//    private boolean sendFlag = true;
//    private SendThread sendThread = new SendThread();
//
//    private int connectState = DISCONNECTION;
//    private boolean flag = true;
//
//    /** ============= proxyee =========== **/
//    private HttpProxyServerConfig serverConfig;
//    private HttpProxyInterceptInitializer proxyInterceptInitializer = new HttpProxyInterceptInitializer();
//    private HttpProxyExceptionHandle httpProxyExceptionHandle = new HttpProxyExceptionHandle();
//    private ProxyConfig proxyConfig;
//
//    public static NettyClient getInstance() {
//        if (nettyClient == null) {
//            nettyClient = new NettyClient();
//        }
//        return nettyClient;
//    }
//
//    private NettyClient() {
//        init();
//    }
//
//    private void init() {
//        setConnectState(DISCONNECTION);
//        bootstrap = new Bootstrap();
//        group = new NioEventLoopGroup();
//        bootstrap.group(group);
//        bootstrap.channel(NioSocketChannel.class);
//        bootstrap.option(ChannelOption.TCP_NODELAY, true);
//        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
//        ProtoUtil_1.RequestProto requestProto = new ProtoUtil_1.RequestProto();
//        requestProto.setSsl(false);
//        ChannelInitializer channelInitializer = new HttpProxyInitializer(null, requestProto, null);
////        bootstrap.handler(channelInitializer);
//        bootstrap.handler(new ChannelInitializer<Channel>() {
//            @Override
//            protected void initChannel(Channel socketChannel) throws Exception {
//                ChannelPipeline pipeline = socketChannel.pipeline();
//                //心跳包的添加
////                pipeline.addLast("idleStateHandler", new IdleStateHandler(60, 60, 0));
//                pipeline.addLast("httpCodec", new HttpServerCodec());
//                pipeline.addLast("serverHandle",
//                        new HttpProxyServerHandle(serverConfig, proxyInterceptInitializer, proxyConfig,
//                                httpProxyExceptionHandle));
////                pipeline.addLast("httpObject",new HttpObjectAggregator(65536));
//                //对消息格式进行验证（MessageDecoder为自定义的解析验证类因协议规定而定）
////                pipeline.addLast("messageDecoder", new MessageDecoder());
////                pipeline.addLast("clientHandler", new NettyClientHandler(nettyClient));
//
////                pipeline.addLast("LengthFieldBasedFrameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
////                pipeline.addLast("LengthFieldPrepender",new LengthFieldPrepender(4));
////                pipeline.addLast("StringDecoder",new StringDecoder(CharsetUtil.UTF_8));
////                pipeline.addLast("StringEncoder",new StringEncoder(CharsetUtil.UTF_8));
//            }
//        });
////        startSendThread();
//    }
//
//    public void uninit() {
//        stopSendThread();
//        if (channelFuture != null) {
//            channelFuture.channel().closeFuture();
//            channelFuture.channel().close();
//            channelFuture = null;
//        }
//        if (group != null) {
//            group.shutdownGracefully();
//            group = null;
//            nettyClient = null;
//            bootstrap = null;
//        }
//        setConnectState(DISCONNECTION);
//        flag = false;
//    }
//
//    public void insertCmd(String cmd) {
//        sendQueue.offer(cmd);
//    }
//
//    private void stopSendThread() {
//        sendQueue.clear();
//        sendFlag = false;
//        sendThread.interrupt();
//    }
//
//    private void startSendThread() {
//        sendQueue.clear();
//        sendFlag = true;
//        sendThread.start();
//    }
//
//    public void connect() throws InterruptedException {
//        if (getConnectState() != CONNECTED) {
//            setConnectState(CONNECTING);
//            ChannelFuture f = bootstrap.connect(new InetSocketAddress("192.168.0.101", 9990));
//            f.addListener(listener);
//            f.channel().closeFuture().sync();//等待客户端关闭连接
//        }
//    }
//
//    private ChannelFutureListener listener = new ChannelFutureListener() {
//        @Override
//        public void operationComplete(ChannelFuture future) throws Exception {
//            if (future.isSuccess()) {
//                channelFuture = future;
//                setConnectState(CONNECTED);
//            } else {
//                setConnectState(DISCONNECTION);
//                future.channel().eventLoop().schedule(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (flag) {
//                            try {
//                                connect();
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                }, 3L, TimeUnit.SECONDS);
//            }
//        }
//    };
//
//    public void setConnectState(int connectState) {
//        this.connectState = connectState;
//    }
//
//    public int getConnectState() {
//        return connectState;
//    }
//
//    /**
//     * 发送消息的线程
//     */
//    private class SendThread extends Thread {
//        @Override
//        public void run() {
//            while (sendFlag) {
//                try {
//                    String cmd = sendQueue.take();
//                    if (channelFuture != null && cmd != null) {
//                        channelFuture.channel().writeAndFlush(cmd);
//                    }
//                } catch (InterruptedException e) {
//                    sendThread.interrupt();
//                }
//            }
//        }
//    }
//
//    public static void main(String[] args) {
//        new Thread(){
//            @Override
//            public void run() {
//                HttpProxyServerConfig config =  new HttpProxyServerConfig();
//                config.setHandleSsl(false);
//                config.setProxyLoopGroup(new NioEventLoopGroup());
//                NettyClient nettyClient = getInstance();
//                try {
//                    nettyClient.connect();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }.start();
//    }
//}
