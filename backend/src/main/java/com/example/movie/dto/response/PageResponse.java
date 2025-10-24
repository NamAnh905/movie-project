package com.example.movie.dto.response;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * Gói dữ liệu phân trang trả về FE, tránh lộ chi tiết của PageImpl.
 */
public class PageResponse<T> {

    private List<T> content;
    private int page;              // 0-based
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean hasNext;
    private boolean hasPrevious;
    private String sort;           // ví dụ: "title: ASC, year: DESC"

    public PageResponse() {
    }

    public PageResponse(List<T> content, int page, int size,
                        long totalElements, int totalPages,
                        boolean hasNext, boolean hasPrevious, String sort) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.hasNext = hasNext;
        this.hasPrevious = hasPrevious;
        this.sort = sort;
    }

    /**
     * Factory method tiện dụng để map từ Spring Page<T>.
     */
    public static <T> PageResponse<T> of(Page<T> p) {
        String sortStr = toSortString(p.getSort());
        return new PageResponse<>(
                p.getContent(),
                p.getNumber(),
                p.getSize(),
                p.getTotalElements(),
                p.getTotalPages(),
                p.hasNext(),
                p.hasPrevious(),
                sortStr
        );
    }

    private static String toSortString(Sort sort) {
        if (sort == null || sort.isUnsorted()) return "";
        StringBuilder sb = new StringBuilder();
        for (Sort.Order o : sort) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(o.getProperty()).append(": ").append(o.getDirection());
        }
        return sb.toString();
    }

    // getters & setters
    public List<T> getContent() { return content; }
    public void setContent(List<T> content) { this.content = content; }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
    public long getTotalElements() { return totalElements; }
    public void setTotalElements(long totalElements) { this.totalElements = totalElements; }
    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
    public boolean isHasNext() { return hasNext; }
    public void setHasNext(boolean hasNext) { this.hasNext = hasNext; }
    public boolean isHasPrevious() { return hasPrevious; }
    public void setHasPrevious(boolean hasPrevious) { this.hasPrevious = hasPrevious; }
    public String getSort() { return sort; }
    public void setSort(String sort) { this.sort = sort; }
}
