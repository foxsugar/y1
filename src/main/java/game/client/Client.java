package game.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

/**
 * Created by SunXianping on 2016/6/15 0015.
 */
public class Client implements Runnable{

    ChannelFuture f = null;


    public Client(){
    }


    private void start(){
        String HOST = System.getProperty("host", "127.0.0.1");
        int PORT = Integer.parseInt(System.getProperty("port", "8081"));

        // Configure SSL.git

        // Configure the client.
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new LoggingHandler(LogLevel.INFO));
                            p.addLast(new StringDecoder());
                            p.addLast(new StringEncoder());
//                            p.addLast(new EchoClientHandler());
                        }
                    });

            // Start the client.
            f = b.connect(HOST, PORT).sync();

//            channel = f.awaitUninterruptibly().channel();

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // Shut down the event loop to terminate all threads.
            group.shutdownGracefully();
        }
    }

    public void sendMsg(Object msg) {
        f.channel().writeAndFlush(msg);

    }

    @Override
    public void run() {
        start();
    }
}
