package com.transaction.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;
import com.account.domain.Account;
import com.account.service.AccountService;
import com.transaction.domain.Transaction;

@WebService()
public class TransactionService {
    @WebMethod
    public Integer transfer(String src, String dest, Integer amount) {
        if (src == dest || amount <= 0) {
            return 400;
        }
        
        AccountService accService = new AccountService();
        Account srcAcc = accService.getAccountByNum(src);
        Account destAcc = new Account();
        
        //Check target account validity
        if (dest.length() <= 10) {
            destAcc = accService.getAccountByNum(dest);
        } else {
            //get account number
            String accNum = "7770000001"; //Engima account number
            destAcc = accService.getAccountByNum(accNum);
        }
        
        if (destAcc.getAccount() != "") {
            //Check balance
            if (srcAcc.getBalance() >= amount) {
                //Debit
                try {
                    //Insert debit transaction in transaction table
                    Class.forName("org.mariadb.jdbc.Driver").newInstance();
                    Connection conn = DriverManager.getConnection("jdbc:mariadb://127.0.0.1:3306/bank_db", "root", "");
                    Statement stmt = conn.createStatement();

                    String query1 = "";

                    if (dest.length() <= 10) {
                        query1 = "INSERT INTO transactions (account, type, amount, destination, time) VALUES ('" + 
                                    srcAcc.getAccount() + "', 'debit', " + amount.toString() + ", '" + destAcc.getAccount() + 
                                    "', CURRENT_TIMESTAMP);";
                    } else {
                        query1 = "INSERT INTO transactions (account, type, amount, destination, time) VALUES ('" + 
                                    srcAcc.getAccount() + "', 'debit', " + amount.toString() + ", '" + dest + 
                                    "', CURRENT_TIMESTAMP);";
                    }

                    stmt.executeQuery(query1);

                    //Update balance in source account table
                    Integer newBalance = srcAcc.getBalance() - amount;

                    String query2 = "UPDATE account SET balance=" + newBalance.toString() + " WHERE account='" + 
                    srcAcc.getAccount() + "';";
                    stmt.executeQuery(query2);

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    return 402; //Debit input failed
                }

                // Credit
                try {
                    //Insert credit transaction in transaction table
                    Class.forName("org.mariadb.jdbc.Driver").newInstance();
                    Connection conn = DriverManager.getConnection("jdbc:mariadb://127.0.0.1:3306/bank_db", "root", "");
                    Statement stmt = conn.createStatement();

                    String query1 = "INSERT INTO transactions (account, type, amount, destination, time) VALUES ('" + 
                    destAcc.getAccount() + "', 'credit', " + amount.toString() + ", '" + srcAcc.getAccount() + 
                    "', CURRENT_TIMESTAMP);";

                    stmt.executeQuery(query1);

                    //Update balance in target account table
                    Integer newBalance = destAcc.getBalance() + amount;

                    String query2 = "UPDATE account SET balance=" + newBalance.toString() + " WHERE account='" + 
                    destAcc.getAccount() + "';";

                    stmt.executeQuery(query2);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    return 403; //Credit input failed
                }

                return 200;
            } else {
                return 401; //Balance not enough
            }
        } else {
            return 400; //Account number not valid
        }
    }

    @WebMethod
    public boolean checkVirtualDebit(String src, String dest, Integer amount, Integer time) {
        AccountService accService = new AccountService();
        boolean exist = false;
        Account srcAcc = accService.getAccountByNum(src);
        Account destAcc = new Account();
        
        //Check target account validity
        if (dest.length() <= 10) {
            destAcc = accService.getAccountByNum(dest);
        } else {
            //get Engima account number
            String accNum = "7770000001";
            destAcc = accService.getAccountByNum(accNum);
        }

        if (srcAcc.getAccount() != "" && destAcc.getAccount() != "") {
            try {
                Integer timeInSeconds = time * 60;
                Class.forName("org.mariadb.jdbc.Driver").newInstance();
                Connection conn = DriverManager.getConnection("jdbc:mariadb://127.0.0.1:3306/bank_db", "root", "");
                Statement stmt = conn.createStatement();

                String query = "SELECT * FROM transactions JOIN virtual_account USING (account) WHERE transactions.account='" + 
                srcAcc.getAccount() + "' AND amount>=" + amount + " AND destination='" + dest + 
                "' AND type='debit' AND TIME_TO_SEC(TIMEDIFF(CURRENT_TIMESTAMP, time)) < " + 
                timeInSeconds.toString() +  " AND virtual_account='" + dest + "';";

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
    
    @WebMethod
    public boolean checkCredit(String src, String dest, Integer amount, Integer time) {
        AccountService accService = new AccountService();
        boolean exist = false;
        Account srcAcc = accService.getAccountByNum(src);
        Account destAcc = new Account();
        
        //Check target account validity
        if (dest.length() <= 10) {
            destAcc = accService.getAccountByNum(dest);
        } else {
            //get Engima account number
            String accNum = "7770000001";
            destAcc = accService.getAccountByNum(accNum);
        }

        if (srcAcc.getAccount() != "" && destAcc.getAccount() != "") {
            if (dest.length() > 10) {
                if (checkVirtualDebit(src, dest, amount, time)) {
                    try {
                        Integer timeInSeconds = time * 60;
                        Class.forName("org.mariadb.jdbc.Driver").newInstance();
                        Connection conn = DriverManager.getConnection("jdbc:mariadb://127.0.0.1:3306/bank_db", "root", "");
                        Statement stmt = conn.createStatement();
        
                        String query = "SELECT * FROM transactions WHERE account='" + 
                        destAcc.getAccount() + "' AND amount>=" + amount + " AND destination='" + srcAcc.getAccount() + 
                        "' AND type='credit' AND TIME_TO_SEC(TIMEDIFF(CURRENT_TIMESTAMP, time)) < " + 
                        timeInSeconds.toString() +  ";";
        
                        ResultSet res = stmt.executeQuery(query);
        
                        if (res.next()) {
                            exist = true;
                        }
        
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                } else {
                    return false;
                }
            } else {
                try {
                    Integer timeInSeconds = time * 60;
                    Class.forName("org.mariadb.jdbc.Driver").newInstance();
                    Connection conn = DriverManager.getConnection("jdbc:mariadb://127.0.0.1:3306/bank_db", "root", "");
                    Statement stmt = conn.createStatement();
    
                    String query = "SELECT * FROM transactions WHERE account='" + 
                    destAcc.getAccount() + "' AND amount>=" + amount + " AND destination='" + srcAcc.getAccount() + 
                    "' AND type='credit' AND TIME_TO_SEC(TIMEDIFF(CURRENT_TIMESTAMP, time)) < " + 
                    timeInSeconds.toString() +  ";";
    
                    ResultSet res = stmt.executeQuery(query);
    
                    if (res.next()) {
                        exist = true;
                    }
    
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        return exist;
    }

    @WebMethod
    public ArrayList<Transaction> getHistory(String acc) {
        ArrayList<Transaction> hist = new ArrayList<Transaction>();
        AccountService accService = new AccountService();
        Account currAcc = accService.getAccountByNum(acc);
        try {
            Class.forName("org.mariadb.jdbc.Driver").newInstance();
            Connection conn = DriverManager.getConnection("jdbc:mariadb://127.0.0.1:3306/bank_db", "root", "");
            Statement stmt = conn.createStatement();

            String query = "SELECT type, amount, destination, time FROM transactions WHERE account='" + 
            currAcc.getAccount() + "';";

            ResultSet res = stmt.executeQuery(query);

            while (res.next()) {
                Transaction tr = new Transaction();
                tr.setAccount(currAcc.getAccount());
                tr.setType(res.getString("type"));
                tr.setAmount(res.getInt("amount"));
                tr.setDestination(res.getString("destination"));
                tr.setTime(res.getString("time"));
                tr.setStatus(200);
                hist.add(tr);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return hist;
    }

	public static void main(String[] argv) {
        Object implementor = new TransactionService ();
        String address = "http://127.0.0.1:8080/ws_bank_war_exploded/TransactionService";
        Endpoint.publish(address, implementor);
    }

}
