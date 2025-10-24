package com.example.movie.service.cinema;

import com.example.movie.model.cinema.Cinema;
import com.example.movie.repository.cinema.CinemaRepository;
import com.example.movie.dto.response.cinema.CinemaDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CinemaService {

    private final CinemaRepository repo;

    @Transactional(readOnly = true)
    public Page<CinemaDTO> list(Pageable pageable) {
        return repo.findAll(pageable)
                .map(c -> new CinemaDTO(c.getId(), c.getName(), c.getAddress(), c.getStatus()));
    }

    public List<CinemaDTO> listPublic() {
        return repo.findByStatus("ACTIVE")
                .stream()
                .map(e -> new CinemaDTO(e.getId(), e.getName(), e.getAddress(), e.getStatus()))
                .toList();
    }
    @Transactional
    public CinemaDTO create(CinemaDTO dto) {
        if (dto == null) throw new IllegalArgumentException("Body is required");
        if (!StringUtils.hasText(dto.name()) || !StringUtils.hasText(dto.address()))
            throw new IllegalArgumentException("Name/Address is required");
        if (!StringUtils.hasText(dto.status())) // mặc định
            dto = new CinemaDTO(dto.id(), dto.name(), dto.address(), "ACTIVE");
        if (repo.existsByName(dto.name()))
            throw new IllegalArgumentException("Cinema name already exists");

        var c = new Cinema();
        c.setName(dto.name().trim());
        c.setAddress(dto.address().trim());
        c.setStatus(dto.status().trim());
        c = repo.save(c);

        return new CinemaDTO(c.getId(), c.getName(), c.getAddress(), c.getStatus());
    }

    @Transactional
    public CinemaDTO update(Long id, CinemaDTO dto) {
        var e = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Cinema not found"));

        if (StringUtils.hasText(dto.name())) {
            String newName = dto.name().trim();
            if (!newName.equals(e.getName()) && repo.existsByName(newName))
                throw new IllegalArgumentException("Cinema name already exists");
            e.setName(newName);
        }
        if (StringUtils.hasText(dto.address())) e.setAddress(dto.address().trim());
        if (StringUtils.hasText(dto.status()))  e.setStatus(dto.status().trim());

        e = repo.save(e);
        return new CinemaDTO(e.getId(), e.getName(), e.getAddress(), e.getStatus());
    }

    @Transactional
    public void delete(Long id) {
        // Xóa cứng. Nếu có FK showtimes → cân nhắc ON DELETE CASCADE hoặc xóa mềm:
        // var e = repo.findById(id).orElseThrow(...); e.setStatus("INACTIVE"); repo.save(e);
        repo.deleteById(id);
    }
}
