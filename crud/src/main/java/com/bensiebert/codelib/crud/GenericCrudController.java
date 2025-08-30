package com.bensiebert.codelib.crud;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

public abstract class GenericCrudController<T, ID> {

    protected final CrudService<T, ID> service;
    protected final String basePath;

    protected GenericCrudController(CrudService<T, ID> service, String basePath) {
        this.service = service;
        this.basePath = basePath;
    }

    protected Specification<T> buildSpecification(String filter) {
        return null;
    }

    @GetMapping
    public ResponseEntity<Page<T>> getAll(
            @RequestParam(value = "search", required = false) String search,
            @PageableDefault(size = 20)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "id")
            }) Pageable pageable
    ) {
        Specification<T> spec = buildSpecification(search);
        Page<T> page = (spec == null)
                ? service.findAll(pageable)
                : service.findAll(spec, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<T> getById(@PathVariable ID id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<T> create(@RequestBody T entity) {
        T saved = service.save(entity);
        return ResponseEntity
                .created(URI.create(basePath + "/" + getId(saved)))
                .body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<T> update(
            @PathVariable ID id,
            @RequestBody T entity
    ) {
        return service.findById(id)
                .map(existing -> {
                    T updated = service.save(entity);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable ID id) {
        return service.findById(id)
                .map(existing -> {
                    service.deleteById(id);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    protected abstract ID getId(T entity);


}
