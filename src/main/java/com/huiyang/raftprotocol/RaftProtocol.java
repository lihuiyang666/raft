package com.huiyang.raftprotocol;

import com.huiyang.consensusEngine.Protocol;
import com.huiyang.consensusEngine.Transaction;
import com.huiyang.raftnet.RAccount;
import com.huiyang.rafttrans.RTransaction;
import com.huiyang.utils.JSONUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RaftProtocol implements Protocol {

    public static int RAFT_LEADER=2;

    public static int RAFT_TRANSACTIONS=3;



    @Autowired
    RestTemplate restTemplate;

    public RAccount leader;



    @Autowired
    String getNeturl;


    @Override
    public boolean getNet() {
        try {
            leader=restTemplate.getForObject(getNeturl,RAccount.class);
            if (leader==null){
                return false;
            }
            else return true;

        } catch (RestClientException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void execute(ArrayList<Transaction> trans) {
        ArrayList<RTransaction> transactions=new ArrayList<>();
        //将获取到的事务集合转化为POW事务集合
        for (Transaction temp:trans){
            transactions.add((RTransaction) temp);
        }

        sendToLeader(leader.Ip,leader.host,transactions);

    }


    private void sendToLeader(String IP, int port, ArrayList<RTransaction> transactions){

        try {
            SocketChannel socketChannel=SocketChannel.open();
            boolean connect = socketChannel.connect(new InetSocketAddress(IP, port));
            if (connect){

                ByteBuffer receivebuffer=ByteBuffer.allocate(256);
                int len = 0;
                byte[] res = new byte[128];
                if ((len=socketChannel.read(receivebuffer))!=0){
                    receivebuffer.flip();
                    receivebuffer.get(res,0,len);
                    System.out.println(new String(res,0,len));
                    receivebuffer.clear();
                }
                ByteBuffer sendbuffer = ByteBuffer.allocate(1024*4);
                Thread.sleep(1000);//隔1s
                String s= RAFT_LEADER+" "+ JSONUtils.toJSON(transactions);
                sendbuffer.put(s.getBytes());
                sendbuffer.flip();
                socketChannel.write(sendbuffer);
                if(!sendbuffer.hasRemaining()){
                    System.out.println(Calendar.getInstance().getTime() +" 发送成功"+" 当前线程:"+Thread.currentThread().getName()+Thread.currentThread().getId());
                }else{
                    System.out.println("has remaining!");
                }
                socketChannel.close();
            }
            else {
                System.out.println("connect error!");

            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.out.println("关闭通信");
        }
//        finally {
//            try {
//                socketChannel.close();
//                System.out.println("关闭通信");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

    }


    public static void doRead(SelectionKey key) throws IOException{
        SocketChannel socket = (SocketChannel) key.channel();
        InetSocketAddress address = (InetSocketAddress) socket.getRemoteAddress();
        System.out.println(Calendar.getInstance().getTime() + "\t" + address.getHostString() +
                ":" + address.getPort() + "\t");
        ByteBuffer bf = ByteBuffer.allocate(1024 * 4);
        int len = 0;
        byte[] res = new byte[1024 * 4];
        try {
            if ((len = socket.read(bf)) != 0) {
                bf.flip();
                bf.get(res, 0, len);
                //                                    System.out.println(new String(res, 0, len));
                String re=new String(res, 2, len);

                bf.clear();
                if (res[0]=='1'){
                    System.out.println(re);


                }
                if (res[0]=='2'){
                    System.out.println(Calendar.getInstance().getTime() + "\t" + address.getHostString() +
                            ":" + address.getPort() + "\t");
                    System.out.println("共识引擎处理模块已连接");
                    System.out.println("=========================================================");
                    final ArrayList<RTransaction> a= (ArrayList<RTransaction>) JSONUtils.toObjectList(re,RTransaction.class);
                    if (a!=null){
//                            BufferedReader buf=new BufferedReader(new InputStreamReader(System.in));
                        System.out.println(Calendar.getInstance().getTime()+" 收到事务，发送给flowers");
                        RestTemplate restTemplate=new RestTemplate();
                        AtomicInteger count=new AtomicInteger(0);
                        HashSet<String> flowerlist=restTemplate.getForObject("http://127.0.0.1:8080/raft/accounts/followers",HashSet.class);
                        for (String t:flowerlist) {
                            String[] addr=t.split(":");

                            new Thread(new Runnable() {
                                public void run() {
                                    try {
                                        SocketChannel socketChannel = SocketChannel.open();

                                        boolean connect = socketChannel.connect(new InetSocketAddress(addr[0], Integer.parseInt(addr[1])));
                                        socketChannel.configureBlocking(false);
                                        if (connect) {
                                            ByteBuffer receivebuffer = ByteBuffer.allocate(256);
                                            int len = 0;
                                            byte[] res = new byte[128];
                                            Thread.sleep(1000);
                                            if ((len = socketChannel.read(receivebuffer)) != 0) {
                                                receivebuffer.flip();
                                                receivebuffer.get(res, 0, len);
                                                System.out.println(new String(res, 0, len));
                                                receivebuffer.clear();
                                            }

                                            ByteBuffer sendbuffer = ByteBuffer.allocate(1024);
                                            String s = RAFT_TRANSACTIONS + " " + JSONUtils.toJSON(a);
                                            sendbuffer.put(s.getBytes());
                                            sendbuffer.flip();
                                            socketChannel.write(sendbuffer);
                                            if (!sendbuffer.hasRemaining()) {
                                                System.out.println(Calendar.getInstance().getTime() + " 发送成功" + " 当前线程:" + Thread.currentThread().getName() + Thread.currentThread().getId());
                                            }
                                            receivebuffer = ByteBuffer.allocate(256);
                                            int len2 = 0;
                                            byte[] res2 = new byte[128];
                                            Thread.sleep(10000);
                                            if ((len2 = socketChannel.read(receivebuffer)) != 0) {
                                                receivebuffer.flip();
                                                receivebuffer.get(res2, 0, len2);
                                                receivebuffer.clear();
                                                if (res2[0] == 'y' || res2[0] == 'Y') {
                                                    System.out.println("投赞成票");
                                                    count.incrementAndGet();

                                                } else {
                                                    System.out.println("该flower投反对票");
                                                }
                                            }
                                            socketChannel.close();
                                            System.out.println("线程结束");



                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        }
                        Thread.sleep(12000);
                        if (count.incrementAndGet()>flowerlist.size()){
                            System.out.println("共识成功，写入共识结果模块");
                        }
                        else System.out.println("共识失败");


                    }
                }
                if (res[0]=='3'){
                    System.out.println(Calendar.getInstance().getTime() + "\t" + address.getHostString() +
                            ":" + address.getPort() + "\t");
                    System.out.println("收到leader发来的事务");
                    System.out.println("=========================================================");
                    System.out.println("是否同意事务(y/n)");

                    BufferedReader buf=new BufferedReader(new InputStreamReader(System.in));
                    String s=buf.readLine();
                    ByteBuffer sendbuffer = ByteBuffer.allocate(16);
                    sendbuffer.put(s.getBytes());
                    sendbuffer.flip();
                    socket.write(sendbuffer);












                }


//                    ByteBuffer sendbuffer = ByteBuffer.allocate(1024);
//                    Thread.sleep(3000);

                //                                    sendbuffer.put("lihuiyang".getBytes());
                //                                    sendbuffer.flip();
                //                                    socket.write(sendbuffer);
                //                                    if(!sendbuffer.hasRemaining()){
                //                                        System.out.println(new Date().getTime()+"Send hello to server succeed."+"当前线程:"+Thread.currentThread().getName()+Thread.currentThread().getId());
                //                                    }else{
                //                                        System.out.println("has remaining!");
                //                                    }

            }
            System.out.println("=========================================================");
        } catch (Exception e) {
            //客户端关闭了
            key.cancel();
            socket.close();
            System.out.println(" 客戶端已断开");
            System.out.println("=========================================================");
        }

    }


    public static boolean sendToFlowers(ArrayList<RTransaction> transactions){
        RestTemplate restTemplate=new RestTemplate();
        AtomicInteger count=new AtomicInteger(0);
        HashSet<String> flowerlist=restTemplate.getForObject("http://127.0.0.1:8080/raft/accounts/followers",HashSet.class);
        for (String t:flowerlist){
            String[] map=t.split(":");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        SocketChannel socketChannel=SocketChannel.open();
                        socketChannel.configureBlocking(false);
                        boolean connect=socketChannel.connect(new InetSocketAddress(map[0], Integer.parseInt(map[1])));


                            ByteBuffer sendbuffer = ByteBuffer.allocate(1024);
                            String s= RAFT_TRANSACTIONS+" "+ JSONUtils.toJSON(transactions);
                            sendbuffer.put(s.getBytes());
                            sendbuffer.flip();
                            socketChannel.write(sendbuffer);
                            if(!sendbuffer.hasRemaining()){
                                System.out.println(Calendar.getInstance().getTime() +" 发送成功"+" 当前线程:"+Thread.currentThread().getName()+Thread.currentThread().getId());
                            }
                            ByteBuffer receivebuffer=ByteBuffer.allocate(64);
                            int len = 0;
                            byte[] res = new byte[256];
                            Thread.sleep(15000);
                            if ((len=socketChannel.read(receivebuffer))!=0){
                                receivebuffer.flip();
                                receivebuffer.get(res,0,len);
                                receivebuffer.clear();
                                if (res[0]=='y'||res[0]=='Y'){
                                    count.incrementAndGet();

                                }
                            }



                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();


                    }

                }
            }).start();

        }
        try {
            Thread.sleep(20000);
            return count.incrementAndGet() > flowerlist.size() / 2;

        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }

    }


}
