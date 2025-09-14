package com.bensiebert.codelib.faq.data;

import com.bensiebert.codelib.auth.annotations.Authenticated;
import com.bensiebert.codelib.crud.GenericCrudController;
import com.bensiebert.codelib.hooks.HookManager;
import com.bensiebert.codelib.ratelimiting.RateLimited;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/faqs")
public class FAQController extends GenericCrudController<FAQ, String> {

    protected FAQController(FAQService service) {
        super(service, "/faqs");
    }

    @Override
    protected String getId(FAQ entity) {
        return entity.getId();
    }

    @Override
    @Authenticated(roles = {"admin"})
    @Operation(summary = "Create a new FAQ entry", tags = {"FAQs"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "FAQ entry created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FAQ.class))
            ),
    })
    public ResponseEntity<FAQ> create(FAQ entity, HttpServletRequest httpRequest) {
        HookManager.fire("faq.created", entity);
        return super.create(entity, httpRequest);
    }

    @Override
    @Authenticated(roles = {"admin"})
    @Operation(summary = "Delete a FAQ entry", tags = {"FAQs"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "FAQ entry deleted successfully",
                content = @Content()
            ),
    })
    public ResponseEntity<Object> delete(String s, HttpServletRequest httpRequest) {
        HookManager.fire("faq.deleted", s);
        return super.delete(s, httpRequest);
    }

    @Override
    @Authenticated(roles = {"admin"})
    @Operation(summary = "Update a FAQ entry", tags = {"FAQs"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "FAQ entry updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FAQ.class))
            ),
    })
    public ResponseEntity<FAQ> update(String s, FAQ entity, HttpServletRequest httpRequest) {
        HookManager.fire("faq.updated", entity);
        return super.update(s, entity, httpRequest);
    }

    @Override
    @RateLimited(limit = 10, interval = 60)
    @Operation(summary = "Get a specific FAQ entry", tags = {"FAQs"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "FAQ entry found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FAQ.class))
            ),
    })
    public ResponseEntity<FAQ> getById(String s, HttpServletRequest httpRequest) {
        return super.getById(s, httpRequest);
    }

    @Override
    @RateLimited(limit = 10, interval = 60)
    @Operation(summary = "Get all FAQ entries", tags = {"FAQs"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "FAQ entries found"),
    })
    public ResponseEntity<Page<FAQ>> getAll(String search, Pageable pageable, HttpServletRequest httpRequest) {
        return super.getAll(search, pageable, httpRequest);
    }

    @Override
    protected Specification<FAQ> buildSpecification(String search) {
        if (search == null || search.isEmpty()) {
            return null;
        }
        return (Root<FAQ> root, CriteriaQuery<?> cq, CriteriaBuilder cb) ->
                cb.or(
                        cb.like(cb.lower(root.get("question")), "%" + search.toLowerCase() + "%"),
                        cb.like(cb.lower(root.get("answer")), "%" + search.toLowerCase() + "%")
                );
    }
}
