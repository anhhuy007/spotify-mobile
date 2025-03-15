package com.example.spotifyclone.shared.model;

import java.util.List;

public class PaginatedResponse<T> {
    private class Pagination {
        private int total;
        private int limit;
        private int page;
        private int totalPages;
    }

    private Pagination pagination;
    private List<T> items;

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }
}