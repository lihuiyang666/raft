package com.huiyang.raftnet;

import com.huiyang.consensusEngine.Account;

public class RAccount implements Account {
    public RAccount() {
    }

    public RAccount(String name, String address, String ip, Integer host) {
        this.name = name;
        this.address = address;
        Ip = ip;
        this.host = host;

    }

    public String name;
    //POW内的账户地址
    public String address;

    // 账户IP
    public String Ip;
    // 账户端口号
    public Integer host;

    //    public String state;
    public State state;

    @Override
    public String toString() {
        return "RAccount{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", Ip='" + Ip + '\'' +
                ", host=" + host +
                ", state=" + state +
                '}';
    }
}
