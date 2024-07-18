package com.haitao.book.services;

import com.haitao.book.controllers.models.FeedbackRequest;
import com.haitao.book.controllers.models.FeedbackResponse;
import com.haitao.book.controllers.models.PageResponse;
import com.haitao.book.entities.Book;
import com.haitao.book.entities.Feedback;
import com.haitao.book.entities.User;
import com.haitao.book.handler.OperationNotPermittedException;
import com.haitao.book.repositories.BookRepository;
import com.haitao.book.repositories.FeedbackRepository;
import com.haitao.book.services.mapers.FeedbackMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedBackRepository;
    private final BookRepository bookRepository;
    private final FeedbackMapper feedbackMapper;
    public Integer save(FeedbackRequest request, Authentication connectedUser) {

        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(()-> new EntityNotFoundException("No book found with this ID:" + request.bookId()));

        if (book.isArchived() || !book.isShareable()){
            throw new OperationNotPermittedException("You cannot give a feedback for an archived or not sharable book ");
        }
        User user = (User) connectedUser.getPrincipal();
        if (Objects.equals(user.getId(), book.getOwner().getId())){
            throw new OperationNotPermittedException("You cannot give a feedback to your own book");
        }
        Feedback feedback = feedbackMapper.toFeedback(request);


        return feedBackRepository.save(feedback).getId();
    }

    public PageResponse<FeedbackResponse> findAllFeedbackByBook(Integer bookId, int page, int size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());

        Page<Feedback> allFeedback = bookRepository.findAllByBookId(bookId, pageable);

        List<FeedbackResponse>  feedbackResponses = allFeedback.stream()
                .map(f -> feedbackMapper.toFeedbackResponse(f, user.getId()))
                .toList();

        return new PageResponse<>(
                feedbackResponses,
                allFeedback.getNumber(),
                allFeedback.getSize(),
                allFeedback.getTotalElements(),
                allFeedback.getTotalPages(),
                allFeedback.isFirst(),
                allFeedback.isLast()
        );
    }
}
