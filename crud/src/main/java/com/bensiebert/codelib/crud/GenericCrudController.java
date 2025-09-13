package com.bensiebert.codelib.crud;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
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


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "(May have) Unauthorized"),
            @ApiResponse(responseCode = "429", description = "(May have) Too Many Requests")
    })
    @GetMapping
    public ResponseEntity<Page<T>> getAll(
            @RequestParam(value = "search", required = false) String search,
            @PageableDefault(size = 20)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "id")
            }) Pageable pageable,
            HttpServletRequest httpRequest
    ) {
        Specification<T> spec = buildSpecification(search);
        Page<T> page = (spec == null)
                ? service.findAll(pageable)
                : service.findAll(spec, pageable);
        return ResponseEntity.ok(page);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "(May have) Unauthorized"),
            @ApiResponse(responseCode = "429", description = "(May have) Too Many Requests")
    })
    @GetMapping("/{id}")
    public ResponseEntity<T> getById(@PathVariable(name = "id") ID id, HttpServletRequest httpRequest) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data created successfully"),
            @ApiResponse(responseCode = "401", description = "(May have) Unauthorized"),
            @ApiResponse(responseCode = "429", description = "(May have) Too Many Requests")
    })
    @PostMapping
    public ResponseEntity<T> create(@RequestBody T entity, HttpServletRequest httpRequest) {
        T saved = service.save(entity);
        return ResponseEntity
                .created(URI.create(basePath + "/" + getId(saved)))
                .body(saved);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data updated successfully"),
            @ApiResponse(responseCode = "401", description = "(May have) Unauthorized"),
            @ApiResponse(responseCode = "429", description = "(May have) Too Many Requests")
    })
    @PutMapping("/{id}")
    public ResponseEntity<T> update(
            @PathVariable(name = "id") ID id,
            @RequestBody T entity,
            HttpServletRequest httpRequest
    ) {
        return ResponseEntity.ok(service.update(id, entity));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data deleted successfully"),
            @ApiResponse(responseCode = "401", description = "(May have) Unauthorized"),
            @ApiResponse(responseCode = "429", description = "(May have) Too Many Requests")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable(name = "id") ID id, HttpServletRequest httpRequest) {
        return service.findById(id)
                .map(existing -> {
                    service.deleteById(id);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    protected abstract ID getId(T entity);


}
