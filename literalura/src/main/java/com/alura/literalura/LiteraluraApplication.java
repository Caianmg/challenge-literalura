package com.alura.literalura;

import com.alura.literalura.model.Book;
import com.alura.literalura.model.Person;
import com.alura.literalura.repository.BookRepository;
import com.alura.literalura.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;
import java.util.Scanner;

@SpringBootApplication
public class LiteraluraApplication implements CommandLineRunner {

	@Autowired
	private BookService bookService;

    @Autowired
    private BookRepository bookRepository;

	public static void main(String[] args) {
		SpringApplication.run(LiteraluraApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Scanner scanner = new Scanner(System.in);
		boolean running = true;

		while (running) {
			System.out.println("Selecione uma opção:");
			System.out.println("1. Buscar livro por título");
			System.out.println("2. Listar todos os livros salvos");
            System.out.println("3. Listar autores registrados");
            System.out.println("4. Listar autores vivos em determinado ano");
            System.out.println("5. Listar livros por idioma");
            System.out.println("0. Sair");

			int option = scanner.nextInt();
			scanner.nextLine();

			switch (option) {
				case 1:
					System.out.println("Digite o título do livro:");
					String title = scanner.nextLine();
					Book book = bookService.getAndSaveBookByTitle(title);

					if (book != null) {
						System.out.println("----- LIVRO -----");
                        System.out.println("Título: " + book.getTitle());
                        System.out.println("Autor: " + book.getAuthor().getName());
                        if (!book.getLanguages().isEmpty()) {
                            System.out.println("Idioma: " + book.getLanguages().get(0));
                        } else {
                            System.out.println("Idioma: Não informado");
                        }
                        System.out.println("Número de downloads: " + book.getDownloadCount());
                        System.out.println("-----------------");
					} else {
						System.out.println("Livro não encontrado.");
					}
					break;
				case 2:
					List<Book> allBooks = bookService.getAllBooks();

                    if (allBooks.isEmpty()) {
                        System.out.println("Nenhum livro registrado no banco de dados.");
                    } else {
                        allBooks.forEach(b -> {
                            System.out.println("----- LIVRO -----");
                            System.out.println("Título: " + b.getTitle());
                            System.out.println("Autor: " + b.getAuthor().getName());
                            if (!b.getLanguages().isEmpty()) {
                                System.out.println("Idioma: " + b.getLanguages().get(0));
                            } else {
                                System.out.println("Idioma: Não informado");
                            }
                            System.out.println("Número de downloads: " + b.getDownloadCount());
                            System.out.println("-----------------");
                        });
                    }
					break;
                case 3:
                    List<Person> allAuthors = bookService.getAllAuthors();

                    if (allAuthors.isEmpty()) {
                        System.out.println("Nenhum autor registrado.");
                    } else {
                        System.out.println("Autores registrados:");

                        allAuthors.forEach(author -> {
                            System.out.println("Autor: " + author.getName());
                            System.out.println("Ano de nascimento: " + (author.getBirthYear() != null ? author.getBirthYear() : "Não informado"));
                            System.out.println("Ano de falecimento: " + (author.getDeathYear() != null ? author.getDeathYear() : "Ainda vivo"));

                            List<Book> booksByAuthor = author.getBooks();
                            if (booksByAuthor.isEmpty()) {
                                System.out.println("Livros: Nenhum livro registrado para este autor.");
                            } else {
                                System.out.println("Livros: ");
                                booksByAuthor.forEach(b -> System.out.println("- " + b.getTitle()));
                            }
                            System.out.println("-----------------");
                        });
                    }
                    break;
				case 4:
                    int year = 0;
                    boolean validYear = false;

                    while (!validYear) {
                        try {
                            System.out.print("Digite o ano: ");
                            year = Integer.parseInt(scanner.nextLine());
                            validYear = true;
                        } catch (NumberFormatException e) {
                            System.out.println("Por favor, insira um ano válido.");
                        }
                    }

                    List<Person> livingAuthors = bookService.getLivingAuthorsByYear(year);

                    if (!livingAuthors.isEmpty()) {
                        System.out.println("Autores vivos no ano " + year + ":");

                        final int finalYear = year;

                        livingAuthors.forEach(author -> {

                            boolean isAlive = (author.getBirthYear() <= finalYear) &&
                                    (author.getDeathYear() == null || author.getDeathYear() >= finalYear);

                            if (isAlive) {
                                System.out.println("Autor: " + author.getName());
                                System.out.println("Ano de nascimento: " + author.getBirthYear());

                                if (author.getDeathYear() != null) {
                                    System.out.println("Ano de falecimento: " + author.getDeathYear());
                                } else {
                                    System.out.println("Autor vivo no ano informado.");
                                }

                                List<Book> books = author.getBooks();
                                if (books.isEmpty()) {
                                    System.out.println("Livros: Nenhum livro registrado.");
                                } else {
                                    System.out.println("Livros: ");
                                    books.forEach(b -> System.out.println("- " + b.getTitle()));
                                }

                                System.out.println("-----------------");
                            }
                        });

                    } else {
                        System.out.println("Nenhum autor encontrado vivo no ano " + year);
                    }
                    break;
                case 5:
                    System.out.println("Insira o idioma para realizar a busca:");
                    System.out.println("es - espanhol");
                    System.out.println("en - inglês");
                    System.out.println("fr - francês");
                    System.out.println("pt - português");

                    String language = scanner.nextLine().toLowerCase();

                    List<Book> booksByLanguage = bookRepository.findByLanguage(language);

                    if (booksByLanguage.isEmpty()) {
                        System.out.println("Não existem livros nesse idioma no banco de dados.");
                    } else {
                        booksByLanguage.forEach(b -> {
                            System.out.println("----- LIVRO -----");
                            System.out.println("Título: " + b.getTitle());

                            if (b.getAuthor() != null) {
                                System.out.println("Autor: " + b.getAuthor().getName());
                            } else {
                                System.out.println("Autor: Não registrado");
                            }

                            System.out.println("Idioma: " + b.getLanguages());

                            System.out.println("Número de downloads: " + b.getDownloadCount());
                            System.out.println("-----------------");
                        });
                    }
                    break;
				case 0:
					System.out.println("Saindo do programa...");
					running = false;
					break;
				default:
					System.out.println("Opção inválida. Tente novamente.");
			}
		}

		scanner.close();
        System.exit(0);
	}
}
