package com.haitao.book.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Book extends BaseEntity {

    private String tittle;
    private String authorName;
    private String isbn;
    private String synopsis;

    //for th picture
    private String bookCover;
    private boolean archived;
    private boolean shareable;

    @ManyToOne()
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "book")
    private List<Feedback> feedbacks;

    @OneToMany(mappedBy = "book")
    private List<BookTransactionHistory> bookTransactionHistory;

    @Transient
    public Double getRate(){
        if(feedbacks == null || feedbacks.isEmpty()){
            return 0.0;
        }

        var rate = this.feedbacks.stream()
                .mapToDouble(feedback -> feedback.getNote())
                .average()
                .orElse(0.0);

        Double roundedRate = Math.round(rate* 10.0) / 10.0;
        return roundedRate;
    }


}
