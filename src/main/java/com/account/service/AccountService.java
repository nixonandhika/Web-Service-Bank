package com.account.service;

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
    public Account getAccountByNum(String accNum) {
        Account acc = new Account();
        try {
            Class.forName("org.mariadb.jdbc.Driver").newInstance();
            Connection conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/bank_db", "root", "");
            Statement stmt = conn.createStatement();
            String query = "SELECT * FROM account WHERE account.account=" + accNum + ";";
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
    public String makeVirtualAccount(String accNum, int movieId) {
        return "";
    }

    public static void main(String[] argv) {
        Object implementor = new AccountService ();
        String address = "http://localhost:9000/ws_bank_war_exploded/AccountService";
        Endpoint.publish(address, implementor);
    }

}
