package com.example.BookShopApp.services;

import com.example.BookShopApp.annotations.WarningExceptionsLoggable;
import com.example.BookShopApp.data.ResourceStorage;
import com.example.BookShopApp.errs.BookstoreApiWrongParameterException;
import com.example.BookShopApp.model.author.AuthorEntity;
import com.example.BookShopApp.model.book.RatingEntity;
import com.example.BookShopApp.model.book.review.BookReviewEntity;
import com.example.BookShopApp.model.dto.BookDto;
import com.example.BookShopApp.model.dto.ReviewDto;
import com.example.BookShopApp.model.google.api.books.Item;
import com.example.BookShopApp.model.google.api.books.Root;
import com.example.BookShopApp.repositories.BookRepository;
import com.example.BookShopApp.security.BookstoreUser;
import com.example.BookShopApp.security.BookstoreUserRegister;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.example.BookShopApp.model.book.BookEntity;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookstoreUserRegister userRegister;
    private final ResourceStorage storage;
    private final RestTemplate restTemplate;

    public List<BookEntity> getBooksData() {
        return bookRepository.findAll();
    }

    @WarningExceptionsLoggable
    public List<BookDto> getBooksWithTitle(String title) throws BookstoreApiWrongParameterException {
        if (title.length() <= 1){
            throw new BookstoreApiWrongParameterException("Wrong values passed to one or more parameters");
        } else {
            List<BookDto> data = fillBookDtoList(bookRepository.findBookEntitiesByTitleContaining(title));
            if (data.size() > 0){
                return data;
            } else {
                throw new BookstoreApiWrongParameterException("No data found with specified parameters...");
            }
        }
    }
    public List<BookDto> getBooksWithPriceBetween(Integer min, Integer max){
        return fillBookDtoList(bookRepository.findBookEntitiesByPriceBetween(min, max));
    }
    public List<BookEntity> getBooksWithPrice(Integer price){
        return bookRepository.findBookEntitiesByPriceIs(price);
    }
    public List<BookDto> getBooksWithMaxDiscount(){
        return fillBookDtoList(bookRepository.getBookEntitiesWithMaxDiscount());
    }
    public List<BookDto> getBestsellers(){
        return fillBookDtoList(bookRepository.getBestsellers());
    }
    public List<BookDto> getPageOfSearchResultBooks(String searchWord, Integer offset, Integer limit){
        Pageable nextPage = PageRequest.of(offset, limit);
        return fillBookDtoList(bookRepository.findBookEntitiesByTitleContaining(searchWord, nextPage).getContent());
    }
    @WarningExceptionsLoggable
    public List<BookDto> getPageOfRecentBooks(String from, String to, Integer offset, Integer limit) throws Exception {
        Pageable nextPage = PageRequest.of(offset, limit);
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        Date dateFrom = null;
        Date dateTo = null;
        if (!from.isEmpty() && !to.isEmpty()) {
            try {
                dateFrom = format.parse(from);
                dateTo = format.parse(to);
            } catch (ParseException e) {
                throw new BookstoreApiWrongParameterException(e.getLocalizedMessage());
            }
        }
        if (dateFrom == null) {
            dateFrom = getFromDate();
        }
        if (dateTo == null){
            dateTo = new Date();
        }
        return fillBookDtoList(bookRepository.findBookEntitiesByPubDateBetweenOrderByPubDateDesc(dateFrom, dateTo, nextPage).getContent());
    }
    public List<BookDto> getPageOfBooksWithTag(String slug, Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        return fillBookDtoList(bookRepository.findBookEntitiesByTag(slug, nextPage).getContent());
    }
    public List<BookDto> getPageOfBooksWithGenre(String slug, Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        return fillBookDtoList(bookRepository.findBookEntitiesByGenre(slug, nextPage).getContent());
    }
    public List<BookDto> getPageOfBooksWithAuthor(String slug, Integer offset, Integer limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        return fillBookDtoList(bookRepository.findBookEntitiesByAuthor(slug, nextPage).getContent());
    }
    public BookEntity getBookBySlug(String bookId){
        return bookRepository.findBookEntityBySlug(bookId);
    }
    public List<BookDto> fillBookDtoList(List<BookEntity> content) {
        List<BookDto> bookDtoList = new ArrayList<>();
        for (BookEntity book : content){
            Optional<Double> optionalRating = bookRepository.getAverageRating(book.getSlug());
            int rating =  Integer.parseInt(optionalRating.map(Math::round).orElse(0L).toString());
            BookDto bookDto = new BookDto(book, rating, "");
            bookDtoList.add(bookDto);
        }
        return bookDtoList;
    }
    public String getBookSlugPage(Model model, String slug) {
        BookEntity book = bookRepository.findBookEntityBySlug(slug);

        Optional<Double> optionalRating = bookRepository.getAverageRating(slug);
        int rating =  Integer.parseInt(optionalRating.map(Math::round).orElse(0L).toString());
        model.addAttribute("slugBook", new BookDto(book, rating, ""));

        Map<Integer, Integer>  ratings = getRatingsMap(book);
        model.addAttribute("ratings", ratings);

        TreeSet<ReviewDto> reviews = new TreeSet<>();
        for (BookReviewEntity review: book.getReviews()) {
            reviews.add(new ReviewDto(review));
        }
        model.addAttribute("reviews", reviews);
        model.addAttribute("reviewsRating", reviews.stream().mapToDouble(ReviewDto::getRating).average().orElse(0));

        try {
            List<BookEntity> myPaidBooks = bookRepository.findBookEntitiesByUserAndType(((BookstoreUser) userRegister.getCurrentUser()).getId(), "PAID");
            List<BookEntity> myArchivedBooks = bookRepository.findBookEntitiesByUserAndType(((BookstoreUser) userRegister.getCurrentUser()).getId(), "ARCHIVED");
            if (myPaidBooks.contains(book) || myArchivedBooks.contains(book)){
                return "books/slugmy";
            }
        } catch (Exception e) {
            return "/books/slug";
        }
        return "/books/slug";
    }

    public Integer getPostponedCount(Optional<String> postponedContents) {
        try {
            return bookRepository.findBookEntitiesByUserAndType(((BookstoreUser) userRegister.getCurrentUser()).getId(), "KEPT").size();
        } catch (Exception e) {
            return postponedContents.filter(s -> !s.isEmpty()).map(s -> s.split("/").length).orElse(0);
        }
    }

    public Integer getCartCount(Optional<String> cartContents) {
        try {
            return bookRepository.findBookEntitiesByUserAndType(((BookstoreUser) userRegister.getCurrentUser()).getId(), "CART").size();
        } catch (Exception e) {
            return cartContents.filter(s -> !s.isEmpty()).map(s -> s.split("/").length).orElse(0);
        }
    }
    public Integer getBooksCount() {
        try {
            return bookRepository.countBookEntitiesByUserId(((BookstoreUser) userRegister.getCurrentUser()).getId());
        } catch (Exception e) {
            return 0;
        }
    }
    public void saveNewBookImage(MultipartFile file, String slug) throws IOException {
        String savePath = storage.saveNewBookImage(file, slug);
        BookEntity bookToUpdate = bookRepository.findBookEntityBySlug(slug);
        bookToUpdate.setImage(savePath);
        bookRepository.save(bookToUpdate); //save new path in db here
    }

    public ResponseEntity<ByteArrayResource> downloadFile(String hash) throws IOException {
        Path path = storage.getBookFilePath(hash);
        Logger.getLogger(this.getClass().getSimpleName()).info("get file path: " + path);

        MediaType mediaType = storage.getBookFileMime(hash);
        Logger.getLogger(this.getClass().getSimpleName()).info("get file mime type: " + mediaType);

        byte[] data = storage.getBookFileByteArray(hash);
        Logger.getLogger(this.getClass().getSimpleName()).info("get file data len: " + data.length);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + path.getFileName().toString())
                .contentType(mediaType)
                .contentLength(data.length)
                .body(new ByteArrayResource(data));
    }

    private Date getFromDate(){
        Calendar c = new GregorianCalendar();
        c.setTimeInMillis(System.currentTimeMillis());
        c.add(Calendar.MONTH, -2);
        return c.getTime();
    }

    public List<BookDto> getPageOfRecentBooks(Integer offset, Integer limit) throws Exception {
        return getPageOfRecentBooks("", "", offset, limit);
    }

    private Map<Integer, Integer> getRatingsMap(BookEntity book) {
        Map<Integer, List<RatingEntity>> ratingsMap =
                book.getRatings().stream().collect(Collectors.groupingBy(RatingEntity::getValue));
        Map<Integer, Integer>  ratings = new HashMap<>();
        for (int key: ratingsMap.keySet()) {
            ratings.put(key, ratingsMap.get(key).size());
        }
        return ratings;
    }

    @Value("${google.books.api.key}")
    private String apiKey;

    public List<BookDto> getPageOfGoogleBooksApiSearchResult(String searchWord, Integer offset, Integer limit){
        String REQUEST_URL = "https://www.googleapis.com/books/v1/volumes?" +
                "q=" + searchWord +
                "&key=" + apiKey +
                "&filter=paid-ebooks" +
                "&startIndex=" + offset +
                "&maxResults=" + limit;
        Root root = restTemplate.getForEntity(REQUEST_URL, Root.class).getBody();
        ArrayList<BookDto> list = new ArrayList<>();
        if (root != null){
            for (Item item : root.getItems()) {
                BookEntity book = new BookEntity();
                if (item.getVolumeInfo() != null){
                    List<AuthorEntity> authors =  new ArrayList<>();
                    authors.add(new AuthorEntity(item.getVolumeInfo().getAuthors()));
                    book.setAuthors(authors);
                    book.setTitle(item.getVolumeInfo().getTitle());
                    book.setImage(item.getVolumeInfo().getImageLinks().getThumbnail());
                }
                if (item.getSaleInfo() != null){
                    book.setPrice(item.getSaleInfo().getSaleability().length());
                }
                list.add(new BookDto(book, 1, ""));
            }
        }
        return list;
    }
}
