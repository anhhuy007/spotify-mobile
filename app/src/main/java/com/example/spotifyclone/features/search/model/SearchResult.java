package com.example.spotifyclone.features.search.model;

import java.util.List;

public class SearchResult {
//    private String _id;
//    private String name;
//    private String img_url;
//    private String type;
//    private List<String> artists;



    private Pagination pagination; //
    private List<SearchItem> items;

    public SearchResult() {};

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public List<SearchItem> getItems() {
        return items;
    }

    public void setItems(List<SearchItem> items) {
        this.items = items;
    }
}
