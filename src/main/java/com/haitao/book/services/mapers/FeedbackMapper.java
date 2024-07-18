package com.haitao.book.services.mapers;

import com.haitao.book.controllers.models.FeedbackRequest;
import com.haitao.book.controllers.models.FeedbackResponse;
import com.haitao.book.entities.Book;
import com.haitao.book.entities.Feedback;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class FeedbackMapper {

    public Feedback toFeedback(FeedbackRequest request) {
        return Feedback.builder()
                .note(request.note())
                .comment(request.comment())
                .book(Book.builder()
                        .id(request.bookId())
                        .shareable(false)
                        .archived(false)
                        .build()
                )
                .build();
    }

    public FeedbackResponse toFeedbackResponse(Feedback feedback, Integer id) {

        return FeedbackResponse.builder()
                .comment(feedback.getComment())
                .note(feedback.getNote())
                .ownFeedback(Objects.equals(feedback.getCreatedBy(),id))
                .build();

    }
}
