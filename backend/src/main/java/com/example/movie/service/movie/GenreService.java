package com.example.movie.service.movie;

import com.example.movie.model.movie.Genre;
import com.example.movie.repository.movie.GenreRepository;
import com.example.movie.dto.response.movie.GenreDTO;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;

@Service
public class GenreService {
    private final GenreRepository repo;

    public GenreService(GenreRepository repo) { this.repo = repo; }

    /** Trả danh sách thể loại, sort theo name asc */
    public List<Genre> list() {
        return repo.findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

    public Genre create(GenreDTO dto){
        var g = new Genre();
        g.setName(dto.name());
        g.setSlug(dto.slug() != null && !dto.slug().isBlank() ? dto.slug() : slugify(dto.name()));
        return repo.save(g);
    }

    public Genre update(Long id, GenreDTO dto){
        var g = repo.findById(id).orElseThrow();
        g.setName(dto.name());
        g.setSlug(dto.slug() != null && !dto.slug().isBlank() ? dto.slug() : slugify(dto.name()));
        return repo.save(g);
    }

    public void delete(Long id){ repo.deleteById(id); }

    private String slugify(String input){
        String s = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        return s;
    }
}