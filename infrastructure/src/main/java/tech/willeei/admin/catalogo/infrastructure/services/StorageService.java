package tech.willeei.admin.catalogo.infrastructure.services;

import tech.willeei.admin.catalogo.domain.resource.Resource;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface StorageService {

    void deleteAll(Collection<String> names);

    Optional<Resource> get(String name);

    List<String> list(String prefix);

    void store(String name, Resource resource);
}
