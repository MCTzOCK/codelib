package com.bensiebert.codelib.crud;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

public interface CrudService<T, ID> {
    Page<T> findAll(Pageable pageable);
    Page<T> findAll(Specification<T> spec, Pageable pageable);
    Optional<T> findById(ID id);
    T save(T entity);
    void deleteById(ID id);
    T update(ID id, T entity);
}
