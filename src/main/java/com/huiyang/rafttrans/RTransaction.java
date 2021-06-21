package com.huiyang.rafttrans;

import com.huiyang.consensusEngine.Transaction;
import com.huiyang.utils.RSAUtils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.HashSet;

public class RTransaction extends Transaction {
    public String address;
    public String transactions;
    public long timestamp;
    public byte[] signature;

    public RTransaction(String address, String transactions, long timestamp, byte[] signature) {
        this.address = address;
        this.transactions = transactions;
        this.timestamp = timestamp;
        this.signature = signature;
    }

    public RTransaction() {
    }

    public RTransaction(String address,String privateKey,String transactions) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {
        this.address=address;
        this.transactions=transactions;
        this.timestamp=System.currentTimeMillis();
        this.signature=genSignature(address,transactions,timestamp,privateKey);
    }

    public static byte[] genSignature(String address,String transaction,long timestamp,String privateKey) throws InvalidKeySpecException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        String s=address+transaction+timestamp;

        return RSAUtils.sign(s,privateKey);

    }

    public static boolean verifyTran(RTransaction transaction, HashSet<String> list) throws Exception {
        if (!list.contains(transaction.address))
            return false;
        else {
            String temp = transaction.address + transaction.transactions + transaction.timestamp;
            return RSAUtils.verify(temp, transaction.signature, transaction.address);
        }

    }

    @Override
    public String toString() {
        return "RTransaction{" +
                "address='" + address + '\'' +
                ", transactions='" + transactions + '\'' +
                ", timestamp=" + timestamp +
                ", signature=" + Arrays.toString(signature) +
                '}';
    }
}
