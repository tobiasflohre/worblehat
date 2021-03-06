package de.codecentric.psd.worblehat.web.controller;

import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.codecentric.psd.worblehat.domain.Book;
import de.codecentric.psd.worblehat.domain.BookFactory;
import de.codecentric.psd.worblehat.domain.BookRepository;
import de.codecentric.psd.worblehat.web.command.BookDataFormData;
import de.codecentric.psd.worblehat.web.validator.ValidateAddBook;

/**
 * Handles requests for the application home page.
 */
@Controller
@RequestMapping("/insertBooks")
public class InsertBookController {

	private static final Logger LOG = LoggerFactory
			.getLogger(InsertBookController.class);

	private BookFactory bookFactory;

	private BookRepository bookRepository;

	private final ValidateAddBook validateAddBook = new ValidateAddBook();

	@Inject
	public InsertBookController(BookFactory bookFactory, BookRepository bookRepository) {
		this.bookFactory = bookFactory;
		this.bookRepository = bookRepository;
	}

	@RequestMapping(method = RequestMethod.GET)
	public void setupForm(ModelMap modelMap) {
		modelMap.put("bookDataFormData", new BookDataFormData());
	}

	@RequestMapping(method = RequestMethod.POST)
	public String processSubmit(ModelMap modelMap,
			@ModelAttribute("bookDataFormData") BookDataFormData cmd,
			BindingResult result) {

		modelMap.put("bookDataFormData", cmd);
		validateAddBook.validate(cmd, result);

		if (result.hasErrors()) {
			return "/insertBooks";
		} else {

			bookFactory.createBook(cmd.getTitle(), cmd.getAuthor(),
					cmd.getEdition(), cmd.getIsbn(),
					Integer.parseInt(cmd.getYear()));
			LOG.debug("new book instance is created: " + cmd.getIsbn());

			List<Book> books = bookRepository.findAllBooks();
			modelMap.addAttribute("books", books);

			return "/bookList";
		}
	}

}
