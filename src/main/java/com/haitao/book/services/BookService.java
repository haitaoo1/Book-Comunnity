package com.haitao.book.services;

import com.haitao.book.controllers.models.*;
import com.haitao.book.entities.Book;
import com.haitao.book.entities.BookTransactionHistory;
import com.haitao.book.entities.User;
import com.haitao.book.handler.OperationNotPermittedException;
import com.haitao.book.repositories.BookRepository;
import com.haitao.book.repositories.BookTransactionHistoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookMaper bookMaper;
    private final BookTransactionHistoryRepository historyRepository;

    public Integer save(BookRequest request, Authentication connectedUser){
        User user = ((User) connectedUser.getPrincipal());
        //Convert request to Book object
        Book book = bookMaper.toBook(request);
        book.setOwner(user);
        return bookRepository.save(book).getId();
    }

    public BookResponse findById(Integer bookId) {
        return bookRepository.findById(bookId)
                .map(bookMaper:: toBookResponse)
                .orElseThrow(() -> new EntityNotFoundException("No book found with the ID" + bookId) );
    }


    public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication connectedUser) {

        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAllDisplayableBooks(pageable, user.getId());
        List<BookResponse> bookResponse = books.stream().map(bookMaper::toBookResponse).toList();

        return new PageResponse<>(
                bookResponse,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }

    public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page,size,Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAll(BookSpecification.withOwnerId(user.getId()), pageable);
        List<BookResponse> booksResponse = books.stream()
                .map(bookMaper::toBookResponse)
                .toList();
        return new PageResponse<>(
                booksResponse,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );

    }

    public PageResponse<BorrowedBookResponse> findAllBorrowedBooks(int page, int size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page,size,Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allBorrowedBooks = historyRepository.findAllBorrowedBooks(pageable, user.getId());
        List<BorrowedBookResponse> bookResponses = allBorrowedBooks.stream()
                .map(bookMaper::toBoorrowedBookResponse)
                .toList();
        return new PageResponse<>(
                bookResponses,
                allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                allBorrowedBooks.getTotalElements(),
                allBorrowedBooks.getTotalPages(),
                allBorrowedBooks.isFirst(),
                allBorrowedBooks.isLast()
        );
    }

    public PageResponse<BorrowedBookResponse> findAllReturnedBook(int page, int size, Authentication connectedUser) {

        User user = (User) connectedUser.getPrincipal();

        Pageable pageable = PageRequest.of(page,size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allReturnedBook =historyRepository.findAllReturnedBook(pageable, user.getId());
        List<BorrowedBookResponse> bookResponses = allReturnedBook.stream()
                .map(bookMaper::toBoorrowedBookResponse)
                .toList();
        return new PageResponse<>(
                bookResponses,
                allReturnedBook.getNumber(),
                allReturnedBook.getSize(),
                allReturnedBook.getTotalElements(),
                allReturnedBook.getTotalPages(),
                allReturnedBook.isFirst(),
                allReturnedBook.isLast()
        );

    }

    public Integer updateShareableStatus(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(()-> new EntityNotFoundException("No book found with this id"+ bookId));

        User user = (User) connectedUser.getPrincipal();

        if(!Objects.equals(book.getOwner().getId(), user.getId())){
            throw new OperationNotPermittedException("You cannot update book shareable status");
        }

        //Inverse the status
        book.setShareable(!book.isShareable());

        bookRepository.save(book);
        return bookId;

    }

    public Integer updateArchivedStatus(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(()-> new EntityNotFoundException("No book found with this id"+ bookId));

        User user = (User) connectedUser.getPrincipal();

        if(!Objects.equals(book.getOwner().getId(), user.getId())){
            throw new OperationNotPermittedException("You cannot update book archivable status");
        }

        //Inverse the status
        book.setArchived(!book.isArchived());

        bookRepository.save(book);
        return bookId;
    }

    public Integer borrowBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book with this id" + bookId));

        if(book.isArchived() || !book.isShareable()){
            throw new OperationNotPermittedException("The request cannot be borrowed");
        }

        User user = (User) connectedUser.getPrincipal();

        if(Objects.equals(book.getOwner().getId(), user.getId())){
            throw new OperationNotPermittedException("You cannot borrow your own book");
        }

        final Boolean isAlreadyBorrowed = historyRepository.isAlreadyBorrowedByUser(bookId, user.getId());
        if(isAlreadyBorrowed){
            throw new OperationNotPermittedException("The book is already boorroed");
        }

        BookTransactionHistory bookTransactionHistory = BookTransactionHistory.builder()
                .user(user)
                .book(book)
                .returned(false)
                .returnApproved(false)
                .build();

        return historyRepository.save(bookTransactionHistory).getId();
    }
}
