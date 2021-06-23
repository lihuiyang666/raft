package com.huiyang.raft;

import com.huiyang.raftnet.RAccount;
import org.springframework.web.client.RestTemplate;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

public class Main4 {
    public static void main(String[] args) throws InvalidKeySpecException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Node d=new Node(10004);
        new Thread(d::runServer).start();
        RestTemplate restTemplate=new RestTemplate();
        RAccount account=new RAccount();
        account.address=d.address;
        account.name="wangwu";
        account.Ip="127.0.0.1";
        account.host=d.port;
        System.out.println(account);
        restTemplate.postForObject("http://127.0.0.1:8080/raft/accounts",account,boolean.class);

    }
}
