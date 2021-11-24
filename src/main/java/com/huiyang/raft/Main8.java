package com.huiyang.raft;

import com.huiyang.raftnet.RAccount;
import com.huiyang.rafttrans.RTransaction;
import org.springframework.web.client.RestTemplate;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

public class Main8 {
    public static void main(String[] args) throws InvalidKeySpecException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Node a=new Node(10008);
        new Thread(a::runServer).start();
        RestTemplate restTemplate=new RestTemplate();
        RAccount account=new RAccount();
        account.address=a.address;
        account.name="lihuiyang8";
        account.Ip="127.0.0.1";
        account.host=a.port;
        System.out.println(account);
        restTemplate.postForObject("http://127.0.0.1:8080/raft/accounts",account,boolean.class);
//        RTransaction t1 =new RTransaction(a.address,a.privateKey,"这是第一个事务");
//        restTemplate.postForObject("http://127.0.0.1:8080/raft/transactions",t1 ,boolean.class);


    }
}
