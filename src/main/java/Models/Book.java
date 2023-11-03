/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Models;

import java.time.LocalDateTime;
import lombok.Data;

/**
 *
 * @author dntn
 */
@Data
public class Book {

    private Long id;
    private String book;
    private String autor;
    private Long ISBN;
    private Integer no_pages;
    private Long genre;
    private Integer status;
    private LocalDateTime created;
    private LocalDateTime updated;

    public Book() {
    }

    public Book(Long id, String name, String autor, Long ISBN,
            Integer noPages, Long genre, Integer status) {
        this.id = id;
        this.book = name;
        this.autor = autor;
        this.ISBN = ISBN;
        this.no_pages = noPages;
        this.genre = genre;
        this.status = status;
    }
}
