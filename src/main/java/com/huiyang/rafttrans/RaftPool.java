package com.huiyang.rafttrans;

import com.huiyang.consensusEngine.Transaction;
import com.huiyang.consensusEngine.TransactionPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class RaftPool implements TransactionPool {

    public CopyOnWriteArrayList<RTransaction> pool=new CopyOnWriteArrayList<>();

    private volatile boolean sendFlag;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    String protocolurl;

    @Override
    public Constructor[] getConstrucors() {
        try {
            Class clazz=Class.forName("com.huiyang.rafttrans.RTransaction");
            Constructor[] c=clazz.getConstructors();
            return c;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean addToPool(Transaction transaction) {
        try {
            RTransaction temp= (RTransaction) transaction;
            while (sendFlag){

            }
            pool.add(temp);
            this.check();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }


    }

    @Override
    public void check() {
        if (pool.size()>0){
            boolean res=sendToProtocol();
            System.out.println(res);
        }


    }

    @Override
    public boolean sendToProtocol() {
        sendFlag=true;
        try {
            ArrayList<RTransaction> toProTrans=new ArrayList<>(pool);
            boolean res=restTemplate.postForObject(protocolurl,toProTrans,boolean.class);
            if (res){
                clearPool();
                return true;
            }
            else return false;

        } catch (RestClientException e) {
            e.printStackTrace();

            return false;
        } finally {
            sendFlag=false;
        }

    }

    @Override
    public void clearPool() {
        pool.clear();

    }
}
