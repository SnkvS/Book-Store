package com.senkiv.bookstore;

import com.senkiv.bookstore.model.Book;
import com.senkiv.bookstore.service.BookService;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BookStoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(BookStoreApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(@Autowired BookService bookService) {
        return (String... args) -> {
            var book = new Book();
            book.setTitle("Game of Thrones");
            book.setAuthor("George Martin");
            book.setIsbn("some_isbn");
            book.setPrice(BigDecimal.ONE);
            bookService.save(book);
            System.out.println(bookService.findAll());
        };
    }

}
