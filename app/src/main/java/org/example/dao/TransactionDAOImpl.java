package org.example.dao;

import org.example.model.Transaction;
import org.example.database.database;
import org.example.database.mySQL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAOImpl implements TransactionDAO {
    private database db = new mySQL();

    @Override
    public boolean createTransaction(Transaction transaction) {
        Connection conn = db.openConnection();
        if (conn == null) return false;
        
        String sql = "INSERT INTO transactions (ride_id, amount, payment_method, status) VALUES (" 
            + transaction.getRideId() + ", " + transaction.getAmount() + ", '" + transaction.getPaymentMethod() 
            + "', '" + transaction.getStatus() + "')";
        
        try {
            int result = db.executeUpdate(conn, sql);
            return result > 0;
        } finally {
            db.closeConnection(conn);
        }
    }

    @Override
    public Transaction getTransactionById(int id) {
        Connection conn = db.openConnection();
        if (conn == null) return null;
        
        String sql = "SELECT * FROM transactions WHERE id = " + id;
        try {
            ResultSet rs = db.runQuery(conn, sql);
            if (rs != null && rs.next()) {
                return mapResultSetToTransaction(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.closeConnection(conn);
        }
        return null;
    }

    @Override
    public boolean updateTransaction(Transaction transaction) {
        Connection conn = db.openConnection();
        if (conn == null) return false;
        
        String sql = "UPDATE transactions SET ride_id = " + transaction.getRideId() + ", amount = " + transaction.getAmount() 
            + ", payment_method = '" + transaction.getPaymentMethod() + "', status = '" + transaction.getStatus() 
            + "' WHERE id = " + transaction.getId();
        
        try {
            int result = db.executeUpdate(conn, sql);
            return result > 0;
        } finally {
            db.closeConnection(conn);
        }
    }

    @Override
    public boolean deleteTransaction(int id) {
        Connection conn = db.openConnection();
        if (conn == null) return false;
        
        String sql = "DELETE FROM transactions WHERE id = " + id;
        try {
            int result = db.executeUpdate(conn, sql);
            return result > 0;
        } finally {
            db.closeConnection(conn);
        }
    }

    @Override
    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        Connection conn = db.openConnection();
        if (conn == null) return transactions;
        
        String sql = "SELECT * FROM transactions";
        try {
            ResultSet rs = db.runQuery(conn, sql);
            if (rs != null) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.closeConnection(conn);
        }
        return transactions;
    }

    @Override
    public List<Transaction> getTransactionsByStatus(String status) {
        List<Transaction> transactions = new ArrayList<>();
        Connection conn = db.openConnection();
        if (conn == null) return transactions;
        
        String sql = "SELECT * FROM transactions WHERE status = '" + status + "'";
        try {
            ResultSet rs = db.runQuery(conn, sql);
            if (rs != null) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.closeConnection(conn);
        }
        return transactions;
    }

    private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setId(rs.getInt("id"));
        transaction.setRideId(rs.getInt("ride_id"));
        transaction.setAmount(rs.getDouble("amount"));
        transaction.setPaymentMethod(rs.getString("payment_method"));
        transaction.setStatus(rs.getString("status"));
        transaction.setTransactionDate(rs.getTimestamp("transaction_date"));
        return transaction;
    }
}
