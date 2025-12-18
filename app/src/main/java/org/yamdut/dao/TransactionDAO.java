package org.yamdut.dao;

import java.util.List;

import org.yamdut.model.Transaction;

public interface TransactionDAO {
    boolean createTransaction(Transaction transaction);
    Transaction getTransactionById(int id);
    boolean updateTransaction(Transaction transaction);
    boolean deleteTransaction(int id);
    List<Transaction> getAllTransactions();
    List<Transaction> getTransactionsByStatus(String status);
}
