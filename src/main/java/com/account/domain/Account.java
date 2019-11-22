package com.account.domain;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)

@XmlType(name = "", propOrder = {
        "account",
        "name",
        "balance",
        "status"
})

@XmlRootElement(name = "getAccountResponse")
public class Account {
    private final static long serialVersionUID = 1L;
    @XmlElement(name = "account", required = true)
    private String account;
    @XmlElement(name = "name", required = true)
    private String name;
    @XmlElement(name = "balance", required = true)
    private int balance;
    @XmlElement(name = "status", required = true)
    private int status = 0;

    public Account() {
        this.account = "";
        this.name = "";
        this.balance = 0;
    }

    public Account(String account, String name, int balance) {
        this.account = account;
        this.name = name;
        this.balance = balance;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBalance() {
        return balance;
    }

    public void setNum(int balance) {
        this.balance = balance;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
