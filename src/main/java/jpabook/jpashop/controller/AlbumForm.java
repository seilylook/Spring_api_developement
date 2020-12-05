package jpabook.jpashop.controller;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AlbumForm {

    private Long id;

    private String name;
    private int price;

    private int stockQuantity;

    private String writer;
    private String isbn;
}
