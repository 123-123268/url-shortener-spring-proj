package com.kavya.url_shortner.domain.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "short_url_counter")
@Getter
@Setter
public class ShortUrlCounter {

    @Id
    private Long id;

    private Long counterValue;

    private Long rangeEnd;

    private Long rangeSize;
}
