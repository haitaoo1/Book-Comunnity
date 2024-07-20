package com.haitao.book.controllers.models;


import com.haitao.book.entities.Book;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackResponse {

    private Double note;
    private String comment;
    //connected user can se which is his feedback
    private boolean ownFeedback;

}
