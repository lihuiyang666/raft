package com.huiyang.raftprotocol;

import com.huiyang.consensusEngine.Transaction;
import com.huiyang.rafttrans.RTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequestMapping("/raft")
public class ProtocolController {

    @Autowired
    RaftProtocol raftProtocol;

    @RequestMapping(value = "/process",method = RequestMethod.POST)
    public boolean process(@RequestBody ArrayList<RTransaction> toPoolTrans){
        boolean b=raftProtocol.getNet();

        if (b){
            ArrayList<Transaction> now=new ArrayList<>(toPoolTrans);
            raftProtocol.execute(now);
            System.out.println("成功发送事务给leader");
            return true;
        }
        else {
            System.out.println("发送事务失败,获取leader失败");
            return false;
        }


    }
}
