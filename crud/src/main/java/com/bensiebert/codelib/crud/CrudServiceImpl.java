package com.bensiebert.codelib.crud;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.lang.reflect.Field;
import java.util.Optional;

@Transactional
public class CrudServiceImpl<T, ID, R extends JpaRepository<T, ID> & JpaSpecificationExecutor<T>> implements CrudService<T, ID> {

    protected final R repository;

    public CrudServiceImpl(R repository) {
        this.repository = repository;
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public Page<T> findAll(Specification<T> spec, Pageable pageable) {
        return repository.findAll(spec, pageable);
    }

    @Override
    public T save(T entity) {
        return repository.save(entity);
    }

    @Override
    public void deleteById(ID id) {
        repository.deleteById(id);
    }

    @Override
    public Optional<T> findById(ID id) {
        return repository.findById(id);
    }

    @Override
    public T update(ID id, T entity) {
        T e = repository.findById(id).orElse(entity);

        Field[] fields = entity.getClass().getDeclaredFields();

        for(Field field : fields) {
            field.setAccessible(true);
            try {
                if(field.getName().equals("id")) {
                    continue;
                }
                Object value = field.get(entity);
                if(value != null) {
                    field.set(e, value);
                }
            } catch (IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }

        repository.delete(entity);
        return repository.save(e);
    }
}
