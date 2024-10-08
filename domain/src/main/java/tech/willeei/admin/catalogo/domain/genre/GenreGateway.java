package tech.willeei.admin.catalogo.domain.genre;

import java.util.List;
import java.util.Optional;

import tech.willeei.admin.catalogo.domain.pagination.Pagination;
import tech.willeei.admin.catalogo.domain.pagination.SearchQuery;

public interface GenreGateway {

    Genre create(Genre aGenre);

    void deleteById(GenreID anId);

    List<GenreID> existsByIds(Iterable<GenreID> anIds);

    Optional<Genre> findById(GenreID anId);

    Genre update(Genre aGenre);

    Pagination<Genre> findAll(SearchQuery aSearchQuery);
}
