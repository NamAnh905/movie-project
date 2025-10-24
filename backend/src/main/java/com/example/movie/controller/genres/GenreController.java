package com.example.movie.controller.genres;

import com.example.movie.dto.response.movie.GenreLiteDTO;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/genres")
public class GenreController {

    private final JdbcTemplate jdbc;

    public GenreController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // ======= LIST (trang quản lý gọi /api/genres?page=0&size=100[&q=...]) =======
    @GetMapping
    @Transactional(readOnly = true)
    public List<GenreLiteDTO> list(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        int offset = Math.max(page, 0) * Math.max(size, 1);
        StringBuilder base = new StringBuilder(" FROM genres WHERE 1=1 ");
        List<Object> args = new ArrayList<>();
        if (q != null && !q.isBlank()) {
            base.append(" AND name LIKE ? ");
            args.add("%" + q.trim() + "%");
        }
        String sql = "SELECT id, name, slug " + base + " ORDER BY name LIMIT ?, ?";
        args.add(offset);
        args.add(size);
        return jdbc.query(sql, args.toArray(), (rs, i) -> {
            GenreLiteDTO d = new GenreLiteDTO();
            d.setId(rs.getLong("id"));
            d.setName(rs.getString("name"));
            d.setSlug(rs.getString("slug"));
            return d;
        });
    }

    // ======= ALL (form thêm/sửa phim) =======
    @GetMapping("/all")
    @Transactional(readOnly = true)
    public List<GenreLiteDTO> all() {
        final String sql = "SELECT id, name, slug FROM genres ORDER BY name";
        return jdbc.query(sql, (rs, i) -> {
            GenreLiteDTO d = new GenreLiteDTO();
            d.setId(rs.getLong("id"));
            d.setName(rs.getString("name"));
            d.setSlug(rs.getString("slug"));
            return d;
        });
    }

    // ======= GET ONE =======
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public GenreLiteDTO getOne(@PathVariable Long id) {
        final String sql = "SELECT id, name, slug FROM genres WHERE id = ?";
        return jdbc.query(sql, ps -> ps.setLong(1, id), rs -> {
            if (rs.next()) {
                GenreLiteDTO d = new GenreLiteDTO();
                d.setId(rs.getLong("id"));
                d.setName(rs.getString("name"));
                d.setSlug(rs.getString("slug"));
                return d;
            }
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Genre not found");
        });
    }

    // ======= CREATE =======
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Transactional
    public GenreLiteDTO create(@RequestBody GenreLiteDTO dto) {
        String name = safe(dto.getName());
        String slug = slugify(emptyToNull(dto.getSlug()), name);
        if (name == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name is required");

        final String sql = "INSERT INTO genres(name, slug) VALUES(?, ?)";
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setString(2, slug);
            return ps;
        }, kh);
        Number key = kh.getKey();
        if (key == null) throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Cannot get id");
        return getOne(key.longValue());
    }

    // ======= UPDATE =======
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    @Transactional
    public GenreLiteDTO update(@PathVariable Long id, @RequestBody GenreLiteDTO dto) {
        String name = safe(dto.getName());
        String slug = slugify(emptyToNull(dto.getSlug()), name);
        if (name == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name is required");

        int n = jdbc.update("UPDATE genres SET name = ?, slug = ? WHERE id = ?", name, slug, id);
        if (n == 0) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Genre not found");
        return getOne(id);
    }

    // ======= DELETE =======
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Transactional
    public void delete(@PathVariable Long id) {
        // Xoá khỏi bảng nối trước để tránh lỗi FK
        jdbc.update("DELETE FROM movie_genres WHERE genre_id = ?", id);
        int n = jdbc.update("DELETE FROM genres WHERE id = ?", id);
        if (n == 0) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Genre not found");
    }

    // -------- helpers --------
    private static String safe(String s) {
        return s == null ? null : s.trim();
    }
    private static String emptyToNull(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
    }
    private static String slugify(String slug, String fallbackName) {
        String base = (slug != null) ? slug : fallbackName;
        if (base == null) return null;
        String nfd = Normalizer.normalize(base, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", ""); // bỏ dấu tiếng Việt
        String out = nfd.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-+$", "");
        return out.isEmpty() ? null : out;
    }
}
