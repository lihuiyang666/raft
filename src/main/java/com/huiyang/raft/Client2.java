package com.huiyang.raft;

import com.huiyang.rafttrans.RTransaction;
import com.huiyang.utils.JSONUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Calendar;

public class Client2 {
    public static void main(String[] args) {
        RestTemplate restTemplate=new RestTemplate();
        long start=System.currentTimeMillis();
        for (int i=0;i<100;i++){
            RTransaction r1=new RTransaction();
            ArrayList<RTransaction> a=new ArrayList<>();
            a.add(r1);
            try {
                SocketChannel socketChannel=SocketChannel.open();
                boolean connect = socketChannel.connect(new InetSocketAddress("127.0.0.1", 10001));
                if (connect){

//                ByteBuffer receivebuffer=ByteBuffer.allocate(256);
//                int len = 0;
//                byte[] res = new byte[128];
//                if ((len=socketChannel.read(receivebuffer))!=0){
//                    receivebuffer.flip();
//                    receivebuffer.get(res,0,len);
//                    System.out.println(new String(res,0,len));
//                    receivebuffer.clear();
//                }
                    ByteBuffer sendbuffer = ByteBuffer.allocate(1024*4);
                    Thread.sleep(100);//隔1s
                    String s= 2+" "+ JSONUtils.toJSON(a);
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
//
        }
        System.out.println(System.currentTimeMillis()-start);
    }
}
