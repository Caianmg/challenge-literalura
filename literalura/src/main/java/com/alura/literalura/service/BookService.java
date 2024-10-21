package com.alura.literalura.service;

import com.alura.literalura.model.Book;
import com.alura.literalura.model.BookResponse;
import com.alura.literalura.model.Person;
import com.alura.literalura.repository.BookRepository;
import com.alura.literalura.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private final String API_URL = "https://gutendex.com/books?search=";

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private PersonRepository personRepository;

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getAndSaveBookByTitle(String title) {
        RestTemplate restTemplate = new RestTemplate();
        String formattedTitle;

        try {
            formattedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Erro ao codificar o título", e);
        }

        String url = API_URL + formattedTitle;

        BookResponse response = restTemplate.getForObject(url, BookResponse.class);

        System.out.println("Resposta da API: " + response);

        if (response != null && response.getBooks() != null && !response.getBooks().isEmpty()) {
            return saveBookFromResponse(response);
        } else {
            return null;
        }
    }

    private Book saveBookFromResponse(BookResponse response) {
        Book book = convertToBook(response.getBooks().get(0));
        Person person = book.getAuthor();

        Optional<Person> existingAuthor = personRepository.findByName(person.getName());
        if (existingAuthor.isPresent()) {
            book.setAuthor(existingAuthor.get());
        } else {
            personRepository.save(person);
            book.setAuthor(person);
        }

        return bookRepository.save(book);
    }

    private Book convertToBook(Book originalBook) {
        Book book = new Book();
        book.setTitle(originalBook.getTitle());
        book.setLanguages(originalBook.getLanguages());
        book.setDownloadCount(originalBook.getDownloadCount());

        if (originalBook.getAuthors() != null && !originalBook.getAuthors().isEmpty()){
            Person person = originalBook.getAuthors().get(0);
            book.setAuthor(person);
        } else {
            book.setAuthor(null);
        }

        return book;
    }

    public List<Person> getLivingAuthorsByYear(int year) {
        return personRepository.findByBirthYearLessThanEqualAndDeathYearIsNullOrDeathYearGreaterThanEqual(year, year);
    }

    public List<Person> getAllAuthors() {
        return personRepository.findAll();
    }

}
