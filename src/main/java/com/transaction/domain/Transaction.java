package com.transaction.domain;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)

@XmlType(name = "", propOrder = {
        "account",
        "type",
        "amount",
        "destination",
        "status"
})

@XmlRootElement(name = "getTransactionResponse")
public class Transaction {
    private final static long serialVersionUID = 1L;
    @XmlElement(name = "account", required = true)
    private String account;
    @XmlElement(name = "type", required = true)
    private String type;
    @XmlElement(name = "amount", required = true)
    private int amount;
    @XmlElement(name = "destination", required = true)
    private String destination;
    @XmlElement(name = "status", required = true)
    private int status = 0;

    public Transaction() {
        this.account = "";
        this.type = "";
        this.amount = 0;
        this.destination = "";
    }

    public Transaction(String account, String type, int amount, String destination) {
        this.account = account;
        this.type = type;
        this.amount = amount;
        this.destination = destination;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
