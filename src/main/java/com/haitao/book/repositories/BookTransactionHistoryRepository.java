package com.haitao.book.repositories;

import com.haitao.book.entities.BookTransactionHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.Authentication;

public interface BookTransactionHistoryRepository extends JpaRepository<BookTransactionHistory, Integer> , JpaSpecificationExecutor<BookTransactionHistory> {


    @Query("""
            SELECT history
            from BookTransactionHistory  history
            WHERE history.user.id = :userId 
    """)
    Page<BookTransactionHistory> findAllBorrowedBooks(Pageable pageable, Integer userId);

    @Query("""
            SELECT history
            from BookTransactionHistory  history
            WHERE history.book.owner.id = :userId
            
    """)
    Page<BookTransactionHistory> findAllReturnedBook(Pageable pageable, Integer id);

    @Query("""
            SELECT (COUNT(*) > 0) AS isBorrowed
            FROM BookTransactionHistory bookTransactionHistory
            WHERE bookTransactionHistory.user.id =:userId
            AND bookTransactionHistory.book.id =:bookId
            AND bookTransactionHistory.returnApproved = false
    """)
    Boolean isAlreadyBorrowedByUser(Integer bookId, Integer id);
}
