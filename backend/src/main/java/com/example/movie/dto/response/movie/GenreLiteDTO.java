package com.example.movie.dto.response.movie;

/** DTO nhẹ cho thể loại, dùng ở form phim & trang quản lý. */
public class GenreLiteDTO {
    private Long id;
    private String name;
    private String slug;

    public GenreLiteDTO() {}

    public GenreLiteDTO(Long id, String name, String slug) {
        this.id = id;
        this.name = name;
        this.slug = slug;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
}
