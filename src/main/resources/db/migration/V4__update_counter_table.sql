CREATE TABLE short_url_counter (
                                   id BIGINT PRIMARY KEY,
                                   counter_value BIGINT NOT NULL,
                                   range_end BIGINT NOT NULL,
                                   range_size BIGINT NOT NULL
);

INSERT INTO short_url_counter(
    id,
    counter_value,
    range_end,
    range_size
)
VALUES (
           1,
           0,
           30,
           30
       );