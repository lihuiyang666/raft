package com.huiyang.raft;

import com.huiyang.raftnet.RAccount;
import com.huiyang.rafttrans.RTransaction;
import org.springframework.web.client.RestTemplate;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

public class Main3 {
    public static void main(String[] args) throws InvalidKeySpecException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Node c=new Node(10003);
        new Thread(c::runServer).start();
        RestTemplate restTemplate=new RestTemplate();
        RAccount account=new RAccount();
        account.address=c.address;
        account.name="avcccn";
        account.Ip="127.0.0.1";
        account.host=c.port;
        System.out.println(account);
        restTemplate.postForObject("http://127.0.0.1:8080/raft/accounts",account,boolean.class);

    }
}
