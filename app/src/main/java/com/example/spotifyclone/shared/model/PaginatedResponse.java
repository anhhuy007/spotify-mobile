package com.example.spotifyclone.shared.model;

import java.util.List;

class PaginatedResponse<T> {
    private class Pagination {
        private int total;
        private int limit;        
        private int page;
        private int totalPages;
    }   

    private Pagination pagination;
    private List<T> data;
}