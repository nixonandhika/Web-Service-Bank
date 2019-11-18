package com.transaction.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;
import com.account.domain.Account;
import com.account.service.AccountService;

@WebService()
public class TransactionService {
    @WebMethod
    public void transfer(String acc, String target, Integer amount) {
        AccountService accService = new AccountService();
        Account sourceAcc = accService.getAccountByNum(acc);
        Account targetAcc = new Account();
        
        //Check target account validity
        if (target.length() <= 10) {
            targetAcc = accService.getAccountByNum(target);
        } else {
            //get account number
            String accNum = "";
            targetAcc = accService.getAccountByNum(accNum);
        }
        if (sourceAcc.getAccount() != "") {
            //Check balance
            if (sourceAcc.getBalance() >= amount) {
                //Debit
                try {
                    //Insert debit transaction in transaction table
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String currentTime = df.format(new Date());
                    Class.forName("org.mariadb.jdbc.Driver").newInstance();
                    Connection conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/bank_db", "root", "");
                    Statement stmt = conn.createStatement();
                    String query1 = "INSERT INTO transactions (account, type, amount, destination, time) VALUES (" + 
                    sourceAcc.getAccount() + ", 'debit'," + amount.toString() + ", " + targetAcc.getAccount() + ", CURRENT_TIME);";
                    ResultSet res1 = stmt.executeQuery(query1);

                    //Update balance in source account table
                    Integer newBalance = sourceAcc.getBalance() - amount;
                    String query2 = "UPDATE account SET balance" + newBalance.toString() + "WHERE account=" + sourceAcc.getAccount() + ";";
                    ResultSet res2 = stmt.executeQuery(query2);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

                //Credit
                try {
                    //Insert credit transaction in transaction table
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String currentTime = df.format(new Date());
                    Class.forName("org.mariadb.jdbc.Driver").newInstance();
                    Connection conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/bank_db", "root", "");
                    Statement stmt = conn.createStatement();
                    String query1 = "INSERT INTO transactions (account, type, amount, destination, time) VALUES (" + 
                    targetAcc.getAccount() + ", 'debit'," + amount.toString() + ", " + sourceAcc.getAccount() + ", CURRENT_TIME);";
                    ResultSet res1 = stmt.executeQuery(query1);

                    //Update balance in target account table
                    Integer newBalance = targetAcc.getBalance() + amount;
                    String query2 = "UPDATE account SET balance" + newBalance.toString() + "WHERE account=" + targetAcc.getAccount();
                    ResultSet res2 = stmt.executeQuery(query2);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    @WebMethod
    public boolean checkCredit(String acc, Integer amount, Integer time) {
        AccountService accService = new AccountService();
        boolean exist = false;
        Account accChecked = new Account();
        
        //Check target account validity
        if (acc.length() <= 10) {
            accChecked = accService.getAccountByNum(acc);
        } else {
            //get account number
            String accNum = "";
            accChecked = accService.getAccountByNum(accNum);
        }

        if (accChecked.getAccount() != "") {
            try {
                Integer timeInSeconds = time * 60;
                Class.forName("org.mariadb.jdbc.Driver").newInstance();
                Connection conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/bank_db", "root", "");
                Statement stmt = conn.createStatement();

                String query = "SELECT account, type, amount FROM transactions WHERE account=" + accChecked.getAccount() + 
                " AND type='credit' AND EXTRACT(second FROM (CURRENT_TIME-time)) < " + timeInSeconds.toString() +  ";";

                ResultSet res = stmt.executeQuery(query);

                if (res.next()) {
                    exist = true;
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        return exist;
    }

	public static void main(String[] argv) {
        Object implementor = new TransactionService ();
        String address = "http://localhost:9000/ws_bank_war_exploded/TransactionService";
        Endpoint.publish(address, implementor);
    }

}
