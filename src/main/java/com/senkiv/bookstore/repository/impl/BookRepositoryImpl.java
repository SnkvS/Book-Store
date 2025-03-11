package com.senkiv.bookstore.repository.impl;

import com.senkiv.bookstore.exception.DataProcessingException;
import com.senkiv.bookstore.model.Book;
import com.senkiv.bookstore.repository.BookRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BookRepositoryImpl implements BookRepository {
    public static final String ERROR_DURING_SAVING_BOOK =
            "Error during saving book -> %s";
    public static final String SELECT_ALL_BOOKS = "FROM Book b";
    public static final String ERROR_DURING_RETRIEVING_ALL_BOOKS =
            "Error during retrieving all books.";
    private final EntityManagerFactory factory;

    @Override
    public Book save(Book book) {
        EntityManager manager = null;
        EntityTransaction transaction = null;
        try {
            manager = factory.createEntityManager();
            transaction = manager.getTransaction();
            transaction.begin();
            manager.persist(book);
            transaction.commit();
            return book;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new DataProcessingException(ERROR_DURING_SAVING_BOOK.formatted(book), e);
        } finally {
            if (manager != null) {
                manager.close();
            }
        }
    }

    @Override
    public List<Book> findAll() {
        try (EntityManager manager = factory.createEntityManager()) {
            return manager.createQuery(SELECT_ALL_BOOKS, Book.class).getResultList();
        } catch (Exception e) {
            throw new DataProcessingException(ERROR_DURING_RETRIEVING_ALL_BOOKS, e);
        }
    }
}
