package com.haitao.book.services;

import com.haitao.book.controllers.models.BookRequest;
import com.haitao.book.controllers.models.BookResponse;
import com.haitao.book.entities.Book;
import com.haitao.book.entities.User;
import com.haitao.book.repositories.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookMaper bookMaper;

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
}
