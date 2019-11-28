//package com.example.jjh.http_proxy_app.proxy;
//
//import android.util.Log;
//
//import com.example.jjh.http_proxy_app.exception.HttpProxyExceptionHandle;
//import com.example.jjh.http_proxy_app.handler.HttpProxyInitializer;
//import com.example.jjh.http_proxy_app.handler.TunnelProxyInitializer;
//import com.example.jjh.http_proxy_app.intercept.HttpProxyInterceptInitializer;
//import com.example.jjh.http_proxy_app.intercept.HttpProxyInterceptPipeline;
//import com.example.jjh.http_proxy_app.server.HttpProxyServer;
//import com.example.jjh.http_proxy_app.server.HttpProxyServerConfig;
//import com.example.jjh.http_proxy_app.util.ProtoUtil_1;
//
//import java.util.LinkedList;
//import java.util.List;
//
//import io.netty.bootstrap.Bootstrap;
//import io.netty.channel.Channel;
//import io.netty.channel.ChannelFuture;
//import io.netty.channel.ChannelFutureListener;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.channel.ChannelInboundHandlerAdapter;
//import io.netty.channel.ChannelInitializer;
//import io.netty.channel.ChannelPromise;
//import io.netty.channel.socket.nio.NioSocketChannel;
//import io.netty.handler.codec.http.DefaultFullHttpResponse;
//import io.netty.handler.codec.http.HttpContent;
//import io.netty.handler.codec.http.HttpRequest;
//import io.netty.handler.codec.http.HttpResponse;
//import io.netty.handler.codec.http.HttpVersion;
//import io.netty.handler.proxy.ProxyHandler;
//import io.netty.resolver.NoopAddressResolverGroup;
//import io.netty.util.ReferenceCountUtil;
//
//public class NettyClientHandler extends ChannelInboundHandlerAdapter {
//    private NettyClient nettyClient;
//
//    private ChannelFuture cf;
//    private String host;
//    private int port;
//    private boolean isSsl = false;
//    private int status = 0;
//    private HttpProxyServerConfig serverConfig;
//    private ProxyConfig proxyConfig;
//    private HttpProxyInterceptInitializer interceptInitializer;
//    private HttpProxyInterceptPipeline interceptPipeline;
//    private HttpProxyExceptionHandle exceptionHandle;
//    private List requestList;
//    private boolean isConnect;
//
//    private ChannelPromise handshakeFuture;
//
//    public NettyClientHandler(NettyClient nettyClient) {
////        super();
//        this.nettyClient = nettyClient;
//    }
//
//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        ctx.writeAndFlush("from client: hello world");
//        if (msg instanceof HttpRequest) {
//            HttpRequest request = (HttpRequest) msg;
//            //第一次建立连接取host和端口号和处理代理握手
//            if (status == 0) {
//                ProtoUtil_1.RequestProto requestProto = ProtoUtil_1.getRequestProto(request);
//                if (requestProto == null) { //bad request
//                    ctx.channel().close();
//                    return;
//                }
//                status = 1;
//                this.host = requestProto.getHost();
//                this.port = requestProto.getPort();
//                if ("CONNECT".equalsIgnoreCase(request.method().name())) {//建立代理握手
//                    status = 2;
//                    HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
//                            HttpProxyServer.SUCCESS);
//                    ctx.writeAndFlush(response);
//                    ctx.channel().pipeline().remove("httpCodec");
//                    //fix issue #42
//                    ReferenceCountUtil.release(msg);
//                    return;
//                }else {
//                    HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
//                            HttpProxyServer.SUCCESS);
//                    ctx.writeAndFlush(response);
//                    ctx.channel().pipeline().remove("httpCodec");
//                    //fix issue #42
//                    ReferenceCountUtil.release(msg);
//                }
//            }
////            interceptPipeline = buildPipeline();
////            interceptPipeline.setRequestProto(new RequestProto(host, port, isSsl));
////            //fix issue #27
////            if (request.uri().indexOf("/") != 0) {
////                URL url = new URL(request.uri());
////                request.setUri(url.getFile());
////            }
////            interceptPipeline.beforeRequest(ctx.channel(), request);
//        } else if (msg instanceof HttpContent) {
//            if (status != 2) {
////                interceptPipeline.beforeRequest(ctx.channel(), (HttpContent) msg);
//            } else {
//                ReferenceCountUtil.release(msg);
//                status = 1;
//            }
//        } else { //ssl和websocket的握手处理
////            if (serverConfig.isHandleSsl()) {
////                ByteBuf byteBuf = (ByteBuf) msg;
////                if (byteBuf.getByte(0) == 22) {//ssl握手
////                    isSsl = true;
////                    int port = ((InetSocketAddress) ctx.channel().localAddress()).getPort();
////                    SslContext sslCtx = SslContextBuilder
////                            .forServer(serverConfig.getServerPriKey(), CertPool.getCert(port,this.host, serverConfig))
////                            .build();
////                    ctx.pipeline().addFirst("httpCodec", new HttpServerCodec());
////                    ctx.pipeline().addFirst("sslHandle", sslCtx.newHandler(ctx.alloc()));
////                    //重新过一遍pipeline，拿到解密后的的http报文
////                    ctx.pipeline().fireChannelRead(msg);
////                    return;
////                }
////            }
//            handleProxyData(ctx.channel(), msg, false);
//        }
//
//    }
//
//    @Override
//    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
////        Log.d("ClientHandler", "-------重连回调------");
//        System.out.println("\"ClientHandler\", \"-------重连回调------\"");
//        nettyClient.setConnectState(NettyClient.DISCONNECTION);
//        nettyClient.connect();
////        super.channelInactive(ctx);
//    }
//
//    @Override
//    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
////        Log.d("NettyClientHandl", "registered");
//        System.out.println("NettyClientHandl,registered");
////        super.channelRegistered(ctx);
//    }
//
//    @Override
//    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//
////        Log.d("NettyClientHandler", "=====连接成功回调=====");
//        System.out.println("NettyClientHandler=====连接成功回调=====");
////        nettyClient.setConnectState(NettyClient.CONNECTED);
//        ctx.writeAndFlush("from client: hello world");
////        super.channelActive(ctx);
//    }
//
//    @Override
//    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
////        super.channelUnregistered(ctx);
//    }
//
//    @Override
//    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
////        Log.d("NettyClientHandl", "网络异常!");
//        System.out.println("NettyClientHandl网络异常");
//        System.out.println(cause);
////        super.exceptionCaught(ctx, cause);
//        ctx.close();
//    }
//
//
//    private void handleProxyData(final Channel channel, final Object msg, boolean isHttp)
//            throws Exception {
//        if (cf == null) {
//            //connection异常 还有HttpContent进来，不转发
//            if (isHttp && !(msg instanceof HttpRequest)) {
//                return;
//            }
//            ProxyHandler proxyHandler = ProxyHandleFactory.build(proxyConfig);
//      /*
//        添加SSL client hello的Server Name Indication extension(SNI扩展)
//        有些服务器对于client hello不带SNI扩展时会直接返回Received fatal alert: handshake_failure(握手错误)
//        例如：https://cdn.mdn.mozilla.net/static/img/favicon32.7f3da72dcea1.png
//       */
//            ProtoUtil_1.RequestProto requestProto = new ProtoUtil_1.RequestProto(host, port, isSsl);
//            ChannelInitializer channelInitializer =
//                    isHttp ? new HttpProxyInitializer(channel, requestProto, proxyHandler)
//                            : new TunnelProxyInitializer(channel, proxyHandler);
//            Bootstrap bootstrap = new Bootstrap();
//            bootstrap.group(serverConfig.getProxyLoopGroup()) // 注册线程池
//                    .channel(NioSocketChannel.class) // 使用NioSocketChannel来作为连接用的channel类
//                    .handler(channelInitializer);
//            if (proxyConfig != null) {
//                //代理服务器解析DNS和连接
//                bootstrap.resolver(NoopAddressResolverGroup.INSTANCE);
//            }
//            requestList = new LinkedList();
//            cf = bootstrap.connect(host, port);
//            cf.addListener(new ChannelFutureListener() {
//                @Override
//                public void operationComplete(ChannelFuture future) throws Exception {
//                    if (future.isSuccess()) {
//                        future.channel().writeAndFlush(msg);
//                        synchronized (requestList) {
//                            for(Object obj:requestList){
//                                future.channel().writeAndFlush(obj);
//                            }
//                            requestList.clear();
//                            isConnect = true;
//                        }
//                    } else {
//                        for (Object obj:requestList) {
//                            ReferenceCountUtil.release(obj);
//                        }
////                        requestList.forEach(obj -> ReferenceCountUtil.release(obj));
//                        requestList.clear();
//                        future.channel().close();
//                        channel.close();
//                    }
//                }
//            });
////            cf.addListener((ChannelFutureListener) future -> {
////                if (future.isSuccess()) {
////                    future.channel().writeAndFlush(msg);
////                    synchronized (requestList) {
////                        requestList.forEach(obj -> future.channel().writeAndFlush(obj));
////                        requestList.clear();
////                        isConnect = true;
////                    }
////                } else {
////                    requestList.forEach(obj -> ReferenceCountUtil.release(obj));
////                    requestList.clear();
////                    future.channel().close();
////                    channel.close();
////                }
////            });
//        } else {
//            synchronized (requestList) {
//                if (isConnect) {
//                    cf.channel().writeAndFlush(msg);
//                } else {
//                    requestList.add(msg);
//                }
//            }
//        }
//    }
//}
