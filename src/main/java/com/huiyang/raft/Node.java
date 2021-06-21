package com.huiyang.raft;

import com.huiyang.raftnet.RAccount;
import com.huiyang.raftprotocol.RaftProtocol;
import com.huiyang.utils.RSAUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;

public class Node {


    public InetAddress inetAddress;
    public String ip;
    public int port;
    public String address;
    public String privateKey;

    public Node(int port) {
        try {
            this.inetAddress = InetAddress.getLocalHost();
            this.port = port;
            String[] tmp = RSAUtils.genKeyPair();
            address = tmp[0];
            privateKey = tmp[1];


        } catch (UnknownHostException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }


    Selector selector;
    ServerSocketChannel serverSocketChannel;

    //作为服务器开启channel和selector
    public void initNIO() {
        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().setReuseAddress(true);
            serverSocketChannel.socket().bind(new InetSocketAddress("127.0.0.1", this.port), 1024);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //运行服务器
    public void runServer() {
        this.initNIO();
        try {
            while (true) {
                selector.select();
                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    //处理掉后将键移除，避免重复消费(因为下次选择后，还在已选择键集中)
                    it.remove();
                    try {
                        if (key.isAcceptable()) {
//                            ServerSocketChannel ssc = (ServerSocketChannel)key.channel();
//                            System.out.println(ssc==serverSocketChannel);
                            SocketChannel clientChannel = serverSocketChannel.accept();
                            clientChannel.configureBlocking(false);
                            clientChannel.register(selector, SelectionKey.OP_READ);
                            try {
                                String message = "连接成功,这里是节点,你是第" + (selector.keys().size() - 1) + "个用户";
                                //向客户端发送消息
                                clientChannel.write(ByteBuffer.wrap(message.getBytes()));
                                InetSocketAddress address = (InetSocketAddress) clientChannel.getRemoteAddress();
                                //输出客户端地址
                                System.out.println(Calendar.getInstance().getTime() + "\t" + address.getHostString() +
                                        ":" + address.getPort() + "\t");
                                System.out.println("共识处理模块已连接");
                                System.out.println("=========================================================");
                            } catch (Exception e) {
                                e.printStackTrace();

                                System.out.println(" 客戶端已断开");
                                System.out.println("=========================================================");
                            }


                        }
                        if (key.isReadable()) {
                            try {
                                RaftProtocol.doRead(key);
                            } catch (Exception e) {
                                key.cancel();

                                System.out.println(" 3客戶端已断开");
                                System.out.println("=========================================================");
                            }
                        }
//                        if(key.isWritable()){
//
//                            SocketChannel socket = (SocketChannel) key.channel();
//                            Thread.sleep(5000);
//                            ByteBuffer sendbuffer = ByteBuffer.allocate(1024);
//                            sendbuffer.put("11111111111111111111111111111111111111111111111111111111111!".getBytes());
//                            sendbuffer.flip();
//                            socket.write(sendbuffer);
//                            if(!sendbuffer.hasRemaining()){
//                                System.out.println(Calendar.getInstance().getTime()+"Send 111 to server succeed."+"当前线程:"+Thread.currentThread().getName()+Thread.currentThread().getId());
//                            }else{
//                                System.out.println("has remaining!");
//                            }
//                            key.interestOps(SelectionKey.OP_READ);
//
//                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        key.cancel();
                        System.out.println("断了");
                        if (key.channel() != null) {
                            key.channel().close();
                        }

                    }

                }

            }


        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
