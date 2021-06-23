package com.huiyang.raft;

import com.huiyang.raftnet.RAccount;
import com.huiyang.rafttrans.RTransaction;
import org.springframework.web.client.RestTemplate;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

public class Main2 {
    public static void main(String[] args) throws InvalidKeySpecException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Node b=new Node(10002);
        new Thread(b::runServer).start();
        RestTemplate restTemplate=new RestTemplate();
        RAccount account=new RAccount();
        account.address=b.address;
        account.name="zhangsan";
        account.Ip="127.0.0.1";
        account.host=b.port;
        System.out.println(account);
        restTemplate.postForObject("http://127.0.0.1:8080/raft/accounts",account,boolean.class);
//        RTransaction t1 =new RTransaction(b.address,b.privateKey,"这是第二个事务");
//        restTemplate.postForObject("http://127.0.0.1:8080/raft/transactions",t1 ,boolean.class);
    }
}
