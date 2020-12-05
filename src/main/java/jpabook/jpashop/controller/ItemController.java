package jpabook.jpashop.controller;

import jpabook.jpashop.domain.item.Album;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.domain.item.Movie;
import jpabook.jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/items/select")
    public String select() {
        return "items/selectItemForm";
    }

    @GetMapping("/items/book")
    public String createBookForm(Model model) {
        model.addAttribute("form", new BookForm());
        //return "items/createItemForm";
        return "items/createBookForm";
    }

    @GetMapping("/items/album")
    public String createAlbumForm(Model model) {
        model.addAttribute("form", new AlbumForm());
        return "items/createAlbumForm";
    }

    @GetMapping("/items/movie")
    public String createMovieForm(Model model) {
        model.addAttribute("form", new MovieForm());
        return "items/createMovieForm";
    }

    @PostMapping("/items/book")
    public String create(BookForm form) {
        Book book = new Book();
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());

        itemService.saveItem(book);
        return "redirect:/";
    }

    @PostMapping("/items/album")
    public String create(AlbumForm form) {
        Album album = new Album();
        album.setName(form.getName());
        album.setPrice(form.getPrice());
        album.setStockQuantity(form.getStockQuantity());
        album.setArtist(form.getWriter());

        itemService.saveItem(album);
        return "redirect:/";
    }

    @PostMapping("/items/movie")
    public String create(MovieForm form) {
        Movie movie = new Movie();
        movie.setName(form.getName());
        movie.setPrice(form.getPrice());
        movie.setStockQuantity(form.getStockQuantity());
        movie.setDirector(form.getDirector());

        itemService.saveItem(movie);
        return "redirect:/";
    }

    @GetMapping("/items")
    public String list(Model model) {
        List<Item> items = itemService.findItems();
        model.addAttribute("items", items);
        return "items/itemList";
    }

}
