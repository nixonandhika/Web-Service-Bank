package com.account.service;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;
import com.account.domain.Account;

@WebService()
public class AccountService {
    @WebMethod
    public String getAccountNumById(Integer userId) {
        String accNum = "";
        try {
            Class.forName("org.mariadb.jdbc.Driver").newInstance();
            Connection conn = DriverManager.getConnection("jdbc:mariadb://127.0.0.1:3306/engi_cinema", "root", "");
            Statement stmt = conn.createStatement();
            String query = "SELECT bank_account FROM users WHERE userId=" + userId + " LIMIT 1;";
            ResultSet res = stmt.executeQuery(query);

            if (res.next()) {
                return res.getString("bank_account");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return accNum;
    }

    @WebMethod
    public Account getAccountByNum(String accNum) {
        Account acc = new Account();
        try {
            Class.forName("org.mariadb.jdbc.Driver").newInstance();
            Connection conn = DriverManager.getConnection("jdbc:mariadb://127.0.0.1:3306/bank_db", "root", "");
            Statement stmt = conn.createStatement();
            String query = "SELECT * FROM account WHERE account.account=" + accNum + " LIMIT 1;";
            ResultSet res = stmt.executeQuery(query);

            if (res.next()) {
                acc.setStatus(200);
                acc.setAccount(res.getString("account"));
                acc.setName(res.getString("name"));
                acc.setNum(res.getInt("balance"));
            } else {
                acc.setStatus(400);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return acc;
    }

    @WebMethod
    public String makeVirtualAccount(String accNum) {
        String NUMBER = "0123456789";
        int length = 16;
        StringBuilder sb = new StringBuilder(length);
        SecureRandom random = new SecureRandom();
        sb.append(accNum);
        
        for (int i = 0; i < length; i++) {
            int rnd = random.nextInt(NUMBER.length());
            char rndChar = NUMBER.charAt(rnd);

            sb.append(rndChar);
        }
        return sb.toString();
    }

    public static void main(String[] argv) {
        Object implementor = new AccountService ();
        String address = "http://127.0.0.1:8080/ws_bank_war_exploded/AccountService";
        Endpoint.publish(address, implementor);
    }

}
