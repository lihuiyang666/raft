package com.huiyang.raftnet;

import com.huiyang.consensusEngine.Account;
import com.huiyang.consensusEngine.Network;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class RaftNet implements Network {

    public CopyOnWriteArrayList<RAccount> accounts;

    public volatile RAccount leader;

    public HashSet<String> followers;



    RaftNet(){
        accounts=new CopyOnWriteArrayList<>();
        followers=new HashSet<>();

    }


    @Override
    public boolean addMember(Account a) {
        try {
            RAccount temp=(RAccount)a;


            if (couldAdd(temp)){
                if (accounts.size()==0||leader==null){
                    temp.state=State.LEADER;
                    accounts.add(temp);
                    leader=temp;
                }
                else {
                    temp.state=State.FOLLOWER;
                    accounts.add(temp);
                    String t=temp.Ip+":"+temp.host;
                    followers.add(t);
                }

                return true;
            }
            else return false;

        } catch (Exception e) {
            e.printStackTrace();
            return false;

        }

    }

    public boolean couldAdd(RAccount a){
        try {
            InetAddress IP=InetAddress.getByName(a.Ip);
            String tmp=a.Ip+":"+a.host;

            if (leader!=null&&tmp.equals(leader.Ip+":"+leader.host)||followers.contains(tmp)){
                return false;
            }
            return a.name != null && a.address != null && a.host != null;
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public Account searchAccount(String name) {
        return null;
    }


    @Scheduled(cron = "0 0/1 * * * ?")
    public void keepLeader(){
        if (!leaderIsAlive()){
            transferLeader();
        }

        else System.out.println("leader正常工作");
    }


    public boolean leaderIsAlive(){
        if (leader==null)
            return false;
        try {


            SocketChannel socketChannel=SocketChannel.open();
            socketChannel.connect(new InetSocketAddress(leader.Ip,leader.host));
            socketChannel.configureBlocking(false);
            if (socketChannel.isConnected()){
               socketChannel.close();
               return true;
            }
            else return false;


        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("leader连接不上");
            accounts.remove(leader);
            leader=null;

            return false;

        }


    }

    private synchronized void transferLeader(){
        try {
            if (followers.size()==0){
                System.out.println("no more nodes");
            }
            else {
                Random random=new Random();
                RAccount temp=accounts.get(random.nextInt(accounts.size()));
                SocketChannel socketChannel=SocketChannel.open();
                socketChannel.connect(new InetSocketAddress(temp.Ip,temp.host));
                socketChannel.configureBlocking(false);
                ByteBuffer sendbuffer = ByteBuffer.allocate(1024);
                ByteBuffer receivebuffer=ByteBuffer.allocate(256);
                int len = 0;
                byte[] res = new byte[128];
                if ((len=socketChannel.read(receivebuffer))!=0){
                    receivebuffer.flip();
                    receivebuffer.get(res,0,len);
                    System.out.println(new String(res,0,len));
                    receivebuffer.clear();
                }
                String s="1 你被选为leader";
                sendbuffer.put(s.getBytes());
                sendbuffer.flip();
                socketChannel.write(sendbuffer);
                if(!sendbuffer.hasRemaining()){
                    System.out.println(Calendar.getInstance().getTime() +" 发送成功,新的leader出现"+"当前线程:"+Thread.currentThread().getName()+Thread.currentThread().getId());
                }else{
                    System.out.println("has remaining!");
                }
                temp.state=State.LEADER;
                leader=temp;
                followers.remove(temp.Ip+":"+temp.host);
                socketChannel.close();


            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("选取leader失败");
        }


    }


    public HashSet<RAccount> getAccounts(){

        return new HashSet<RAccount>(accounts);


    }

    public HashSet<String> getfollowers(){
        return followers;

    }




    @Override
    public void communication(Account a) {

    }
}
