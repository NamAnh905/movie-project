package com.example.movie.dto.response.movie;

import java.time.LocalDate;
import java.util.List;

public class MovieDTO {
    private Long id;
    private String title;
    private String description;
    private Integer duration;
    private LocalDate releaseDate;
    private String language;
    private String country;
    private String status;
    private String ageRating;
    private String posterUrl;
    private Integer year;

    // NEW: thể loại chính + toàn bộ thể loại
    private Long primaryGenreId;
    private String primaryGenreName;
    private List<Long> genreIds;
    private List<String> genreNames;

    public MovieDTO() { }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }

    public LocalDate getReleaseDate() { return releaseDate; }
    public void setReleaseDate(LocalDate releaseDate) { this.releaseDate = releaseDate; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAgeRating() { return ageRating; }
    public void setAgeRating(String ageRating) { this.ageRating = ageRating; }

    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public Long getPrimaryGenreId() { return primaryGenreId; }
    public void setPrimaryGenreId(Long primaryGenreId) { this.primaryGenreId = primaryGenreId; }

    public String getPrimaryGenreName() { return primaryGenreName; }
    public void setPrimaryGenreName(String primaryGenreName) { this.primaryGenreName = primaryGenreName; }

    public List<Long> getGenreIds() { return genreIds; }
    public void setGenreIds(List<Long> genreIds) { this.genreIds = genreIds; }

    public List<String> getGenreNames() { return genreNames; }
    public void setGenreNames(List<String> genreNames) { this.genreNames = genreNames; }
}
