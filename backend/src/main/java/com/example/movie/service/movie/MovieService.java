package com.example.movie.service.movie;

import com.example.movie.model.movie.Genre;                 // ✅ đúng package
import com.example.movie.repository.movie.GenreRepository;       // ✅ đúng package
import com.example.movie.model.movie.Movie;
import com.example.movie.repository.movie.MovieRepository;
import com.example.movie.repository.movie.MovieGenreRepository;
import com.example.movie.dto.response.movie.MovieDTO;
import com.example.movie.dto.response.movie.UpsertMovie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final MovieGenreRepository movieGenreRepository;

    public MovieService(MovieRepository movieRepository,
                        GenreRepository genreRepository,
                        MovieGenreRepository movieGenreRepository) {
        this.movieRepository = movieRepository;
        this.genreRepository = genreRepository;
        this.movieGenreRepository = movieGenreRepository;
    }

    // ===================== Danh sách + lọc theo thể loại =====================
    @Transactional(readOnly = true)
    public Page<MovieDTO> list(String q, Long genreId, Pageable pageable) {
        return list(q, genreId, null, pageable); // chuyển về method mới
    }

    // MỚI: list có status
    @Transactional(readOnly = true)
    public Page<MovieDTO> list(String q, Long genreId, String status, Pageable pageable) {
        String qNorm = (q == null || q.isBlank()) ? null : q;
        String st    = (status == null || status.isBlank()) ? null : status;

        Page<Movie> page = movieRepository.search(st, genreId, qNorm, pageable);
        return page.map(this::toDTOEnriched); // dùng mapper có sẵn trong file của bạn
    }
    // ========================================================================

    @Transactional(readOnly = true)
    public MovieDTO getById(Long id){
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Movie not found with id=" + id));
        return toDTOEnriched(movie);
    }

    @Transactional
    public MovieDTO create(UpsertMovie req){
        Movie m = new Movie();
        apply(m, req);
        m = movieRepository.save(m);                       // cần id để ghi bảng join
        updateMovieGenres(m.getId(), req.getGenreIds());   // ghi movie_genres
        return toDTOEnriched(m);
    }

    @Transactional
    public MovieDTO update(Long id, UpsertMovie req){
        Movie m = movieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Movie not found with id=" + id));
        apply(m, req);
        m = movieRepository.save(m);
        if (req.getGenreIds() != null) {                   // chỉ cập nhật khi FE gửi lên
            updateMovieGenres(id, req.getGenreIds());
        }
        return toDTOEnriched(m);
    }

    private void updateMovieGenres(Long movieId, List<Long> genreIds) {
        if (movieId == null || genreIds == null) return;
        movieGenreRepository.deleteAllByMovieId(movieId);
        for (Long gid : genreIds) {
            if (gid != null) movieGenreRepository.insertOne(movieId, gid);
        }
    }

    @Transactional
    public void delete(Long id){
        if (!movieRepository.existsById(id)) return;
        movieRepository.deleteById(id);
    }

    /** Map entity -> DTO rồi enrich thêm thể loại. */
    private MovieDTO toDTOEnriched(Movie m) {
        MovieDTO dto = baseDTO(m);

        // Lấy genreIds từ bảng trung gian, rồi map ra genreNames qua GenreRepository
        try {
            List<Long> ids = movieGenreRepository.findGenreIdsByMovieId(m.getId());
            dto.setGenreIds(ids);

            List<Genre> genres = ids == null || ids.isEmpty()
                    ? List.of()
                    : genreRepository.findAllById(ids);
            List<String> names = genres.stream().map(Genre::getName).collect(Collectors.toList());
            dto.setGenreNames(names);
        } catch (Exception ignored) {}

        // Thêm primaryGenreId/Name nếu entity có
        try {
            Method getPrimaryGenreId = m.getClass().getMethod("getPrimaryGenreId");
            Object id = getPrimaryGenreId.invoke(m);
            if (id instanceof Long) dto.setPrimaryGenreId((Long) id);
        } catch (Exception ignored) { }

        try {
            Method getPrimaryGenre = m.getClass().getMethod("getPrimaryGenre");
            Object g = getPrimaryGenre.invoke(m);
            if (g != null) {
                Method getId = g.getClass().getMethod("getId");
                Method getName = g.getClass().getMethod("getName");
                Object gid = getId.invoke(g);
                Object gname = getName.invoke(g);
                if (gid instanceof Long) dto.setPrimaryGenreId((Long) gid);
                if (gname instanceof String) dto.setPrimaryGenreName((String) gname);
            }
        } catch (Exception ignored) { }

        return dto;
    }

    /** Dùng MovieDTO.from(Movie) nếu có; nếu không thì map cơ bản để không lỗi build. */
    private MovieDTO baseDTO(Movie m) {
        try {
            Method from = MovieDTO.class.getMethod("from", Movie.class);
            Object res = from.invoke(null, m);
            if (res instanceof MovieDTO) return (MovieDTO) res;
        } catch (Exception ignored) {}

        MovieDTO dto = new MovieDTO();
        dto.setId(m.getId());
        try { dto.setTitle((String) Movie.class.getMethod("getTitle").invoke(m)); } catch (Exception ignored) {}
        try { dto.setDescription((String) Movie.class.getMethod("getDescription").invoke(m)); } catch (Exception ignored) {}
        try { dto.setDuration((Integer) Movie.class.getMethod("getDuration").invoke(m)); } catch (Exception ignored) {}
        try { dto.setReleaseDate((java.time.LocalDate) Movie.class.getMethod("getReleaseDate").invoke(m)); } catch (Exception ignored) {}
        try { dto.setLanguage((String) Movie.class.getMethod("getLanguage").invoke(m)); } catch (Exception ignored) {}
        try { dto.setCountry((String) Movie.class.getMethod("getCountry").invoke(m)); } catch (Exception ignored) {}
        try { dto.setStatus((String) Movie.class.getMethod("getStatus").invoke(m)); } catch (Exception ignored) {}
        try { dto.setAgeRating((String) Movie.class.getMethod("getAgeRating").invoke(m)); } catch (Exception ignored) {}
        try { dto.setPosterUrl((String) Movie.class.getMethod("getPosterUrl").invoke(m)); } catch (Exception ignored) {}
        try { dto.setYear((Integer) Movie.class.getMethod("getYear").invoke(m)); } catch (Exception ignored) {}
        return dto;
    }

    /** Áp dữ liệu từ UpsertMovie vào entity Movie (an toàn bằng phản xạ). */
    private void apply(Movie m, UpsertMovie req){
        if (req == null) return;

        // Các trường cơ bản
        callSetter(m, "setTitle", getString(req, "getTitle"));
        callSetter(m, "setYear",  getNumber(req, "getYear"));
        callSetter(m, "setDescription", getString(req, "getDescription"));
        callSetter(m, "setDuration",    getNumber(req, "getDuration"));
        callSetter(m, "setLanguage",    getString(req, "getLanguage"));
        callSetter(m, "setCountry",     getString(req, "getCountry"));
        callSetter(m, "setStatus",      getString(req, "getStatus"));
        callSetter(m, "setAgeRating",   getString(req, "getAgeRating"));
        callSetter(m, "setPosterUrl",   getString(req, "getPosterUrl"));
        callSetter(m, "setReleaseDate", getObj(req, "getReleaseDate", java.time.LocalDate.class));

        // primaryGenreId / primaryGenre (nếu entity có)
        Long pgid = (Long) getNumber(req, "getPrimaryGenreId");
        if (pgid != null) {
            // Nếu entity có setPrimaryGenreId(Long)
            if (callSetter(m, "setPrimaryGenreId", pgid)) return;

            // Nếu entity dùng ManyToOne Genre primaryGenre
            Optional<Genre> g = genreRepository.findById(pgid);
            g.ifPresent(genre -> callSetter(m, "setPrimaryGenre", genre));
        }
    }

    // ----------------------- Helpers phản xạ an toàn -----------------------
    private boolean callSetter(Object target, String setterName, Object value) {
        if (value == null) return false;
        Method[] ms = target.getClass().getMethods();
        for (Method method : ms) {
            if (method.getName().equals(setterName) && method.getParameterCount() == 1) {
                try {
                    method.invoke(target, value);
                    return true;
                } catch (Exception ignored) {}
            }
        }
        return false;
    }

    private String getString(Object obj, String getter) {
        try { Object v = obj.getClass().getMethod(getter).invoke(obj); return v == null ? null : v.toString(); }
        catch (Exception ignored) { return null; }
    }

    private Number getNumber(Object obj, String getter) {
        try { Object v = obj.getClass().getMethod(getter).invoke(obj); return (v instanceof Number) ? (Number) v : null; }
        catch (Exception ignored) { return null; }
    }

    @SuppressWarnings("unchecked")
    private <T> T getObj(Object obj, String getter, Class<T> type) {
        try { Object v = obj.getClass().getMethod(getter).invoke(obj); return type.isInstance(v) ? (T) v : null; }
        catch (Exception ignored) { return null; }
    }

    // Thêm hàm trả toàn bộ theo status
    public List<MovieDTO> findAllByStatus(String status) {
        return movieRepository.findByStatusOrderByReleaseDateDesc(status)
                .stream()
                .map(this::toDTOEnriched)   // dùng method có sẵn trong Service
                .toList();
    }
}
