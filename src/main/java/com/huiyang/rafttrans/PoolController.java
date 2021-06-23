package com.huiyang.rafttrans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/raft")
public class PoolController {

    @Autowired
    RaftPool raftPool;

    @RequestMapping(value = "/transactions",method = RequestMethod.POST)
    public boolean addTrans(@RequestBody RTransaction trans){
        System.out.println("接收到交易");
        boolean res= raftPool.addToPool(trans);

        return res;

    }
}
