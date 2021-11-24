package com.huiyang.raftnet;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashSet;

@RestController
@RequestMapping("/raft")
public class NetController {

    @Autowired
    RaftNet raftNet;

    @RequestMapping(value ="/accounts",method = RequestMethod.POST)
    public boolean register(@RequestBody RAccount newAccount){

        //        for (RAccount p:raftNet.accounts){
//            System.out.println(p);
//
//        }
        return raftNet.addMember(newAccount);
    }

    @RequestMapping(value ="/accounts",method = RequestMethod.GET)
    public HashSet<RAccount> getAccounts(){
        return raftNet.getAccounts();


    }

    @RequestMapping(value = "/accounts/followers",method = RequestMethod.GET)
    public HashSet<String> getfollowers(){
        return raftNet.getfollowers();
    }


    @RequestMapping(value = "/accounts/leader",method = RequestMethod.GET)
    public RAccount getleader(){
        return raftNet.leader;
    }

    @RequestMapping(value = "test",method = RequestMethod.GET)
    public String test(HttpServletRequest req){
        return req.getRemoteHost()+" "+req.getRemotePort();
    }


}
