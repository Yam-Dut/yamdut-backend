package org.example.dao;

import org.example.model.Transaction;
import java.util.List;

public interface TransactionDAO {
    boolean createTransaction(Transaction transaction);
    Transaction getTransactionById(int id);
    boolean updateTransaction(Transaction transaction);
    boolean deleteTransaction(int id);
    List<Transaction> getAllTransactions();
    List<Transaction> getTransactionsByStatus(String status);
}
