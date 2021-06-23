package com.huiyang.raft;


import com.huiyang.rafttrans.PoolController;
import com.huiyang.rafttrans.RTransaction;
import org.springframework.web.client.RestTemplate;

import java.util.Calendar;

public class Client {
    public static void main(String[] args) {

        RestTemplate restTemplate=new RestTemplate();
        long start=System.currentTimeMillis();
        System.out.println(start);
        for (int i=0;i<10;i++){
            RTransaction t1 =new RTransaction();

            restTemplate.postForObject("http://127.0.0.1:8080/raft/transactions",t1 ,boolean.class);


        }
        System.out.println(System.currentTimeMillis()-start);

    }
}
