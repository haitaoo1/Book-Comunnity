package com.haitao.book.services;

import com.haitao.book.controllers.models.BookRequest;
import com.haitao.book.controllers.models.BookResponse;
import com.haitao.book.controllers.models.BorrowedBookResponse;
import com.haitao.book.entities.Book;
import com.haitao.book.entities.BookTransactionHistory;
import com.haitao.book.file.FileUtils;
import org.springframework.stereotype.Service;

@Service
public class BookMaper {

    public Book toBook(BookRequest request) {
        return Book.builder()
                .id(request.id())
                .authorName(request.authorName())
                .synopsis(request.synopsis())
                .archived(false)
                .shareable(request.shareable())
                .build();
    }

    public BookResponse toBookResponse(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .tittle(book.getTittle())
                .authorName(book.getAuthorName())
                .isbn(book.getIsbn())
                .synopsis(book.getSynopsis())
                .rate(book.getRate())
                .archived(book.isArchived())
                .shareable(book.isShareable())
                .owner(book.getOwner().fullName())
                .cover(FileUtils.readFileFromLocation(book.getBookCover()))
                .build();
    }

    public BorrowedBookResponse toBoorrowedBookResponse(BookTransactionHistory history) {
        return BorrowedBookResponse.builder()
                .id(history.getId())
                .tittle(history.getBook().getTittle())
                .authorName(history.getBook().getAuthorName())
                .isbn(history.getBook().getIsbn())
                .returned(history.isReturned())
                .returnedApproved(history.isReturnApproved())
                .build();
    }
}
