package com.haitao.book.repositories;


import com.haitao.book.entities.Book;
import com.haitao.book.entities.BookTransactionHistory;
import com.haitao.book.entities.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface BookRepository extends JpaRepository<Book, Integer>, JpaSpecificationExecutor<Book> {

    @Query("""
            SELECT book
            From Book book
            WHERE book.archived = false
            AND book.shareable = true
            AND book.owner.id != :userId
            """)
    Page<Book> findAllDisplayableBooks(Pageable pageable, Integer userId);

    @Query("""
        SELECT feedback
        FROM Feedback feedback
        WHERE feedback.book.id = :bookId
        """)
    Page<Feedback> findAllByBookId(Integer bookId, Pageable pageable);
}
