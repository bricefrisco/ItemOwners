package com.bfrisco.itemowners.database;

import java.util.List;

public class ItemEventPage {
    private int currentPage;
    private int totalPages;
    private List<ItemEvent> result;

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public List<ItemEvent> getResult() {
        return result;
    }

    public void setResult(List<ItemEvent> result) {
        this.result = result;
    }
}
