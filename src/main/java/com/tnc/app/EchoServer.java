package com.tnc.app;

import java.net.InetSocketAddress;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class EchoServer {
	private final int port;

	public EchoServer(int port) {
		this.port = port;
	}

	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.err.println("Usage: " + EchoServer.class.getSimpleName() + "<port>");
		}
		int port = Integer.parseInt(args[0]);
		
		System.out.println("port :" + port);
		
		// port 값을 설정
		new EchoServer(port).start();
		// 서버의 start() 메소드 호출
	}

	public void start() throws Exception {
		EventLoopGroup group = new NioEventLoopGroup();
		// EventLoopGroup 생성
		try {
			ServerBootstrap b = new ServerBootstrap();
			// ServerBootstrap 생성
			b.group(group).channel(NioServerSocketChannel.class)
					// NIO 전송 채널 이용
					.localAddress(new InetSocketAddress(port))
					.handler(new LoggingHandler(LogLevel.INFO))
					// 지정된 포트를 이용해 소켓 주소 설정
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast(new EchoServerHandler());
							// @Shareble 이므로 EchoServerHandler 이용 가능
						}
					});
			ChannelFuture f = b.bind().sync();
			// 서버를 비동기 식으로 바인딩, sync()는 바인딩이 완료되기를 대기
			// sync()로 인해 블로킹 된다.
			f.channel().closeFuture().sync();
			// 채널의 CloseFuture를 얻고 완료될 때까지 현재 스레드를 블로킹
		} finally {
			group.shutdownGracefully().sync();
			// EventLoopGroup을 종료하고 모든 리소스 해제
		}
	}
}
