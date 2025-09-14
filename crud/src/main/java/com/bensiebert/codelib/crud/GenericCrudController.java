package com.bensiebert.codelib.crud;

import com.bensiebert.codelib.auth.springdoc.UnauthorizedResponse401;
import com.bensiebert.codelib.ratelimiting.springdoc.Error429Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.query.Param;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
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
            @ApiResponse(responseCode = "401", description = "(May have) Unauthorized",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = UnauthorizedResponse401.class))
            ),
            @ApiResponse(responseCode = "429", description = "(May have) Too Many Requests",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error429Response.class))
            )
    })
    @GetMapping
    public ResponseEntity<Page<T>> getAll(
            @RequestParam(value = "search", required = false) String search,
            @Parameter(name = "pageable", description = "Pagination and sorting information")
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
            @ApiResponse(responseCode = "401", description = "(May have) Unauthorized",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UnauthorizedResponse401.class))
            ),
            @ApiResponse(responseCode = "429", description = "(May have) Too Many Requests",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error429Response.class))
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<T> getById(@Parameter(name = "ID of the entity") @PathVariable(name = "id") ID id, HttpServletRequest httpRequest) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "(May have) Unauthorized",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UnauthorizedResponse401.class))
            ),
            @ApiResponse(responseCode = "429", description = "(May have) Too Many Requests",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error429Response.class))
            )
    })
    @PostMapping
    public ResponseEntity<T> create(@RequestBody T entity, HttpServletRequest httpRequest) {
        T saved = service.save(entity);
        return ResponseEntity
                .created(URI.create(basePath + "/" + getId(saved)))
                .body(saved);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "(May have) Unauthorized",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UnauthorizedResponse401.class))
            ),
            @ApiResponse(responseCode = "429", description = "(May have) Too Many Requests",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error429Response.class))
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<T> update(
            @Parameter(name = "ID of the Entity") @PathVariable(name = "id") ID id,
            @RequestBody T entity,
            HttpServletRequest httpRequest
    ) {
        return ResponseEntity.ok(service.update(id, entity));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "(May have) Unauthorized",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UnauthorizedResponse401.class))
            ),
            @ApiResponse(responseCode = "429", description = "(May have) Too Many Requests",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error429Response.class))
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@Parameter(name = "ID of the entity") @PathVariable(name = "id") ID id, HttpServletRequest httpRequest) {
        return service.findById(id)
                .map(existing -> {
                    service.deleteById(id);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    protected abstract ID getId(T entity);

}
