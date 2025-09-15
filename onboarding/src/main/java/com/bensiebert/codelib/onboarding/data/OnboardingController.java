package com.bensiebert.codelib.onboarding.data;

import com.bensiebert.codelib.auth.annotations.Authenticated;
import com.bensiebert.codelib.crud.GenericCrudController;
import com.bensiebert.codelib.hooks.HookManager;
import com.bensiebert.codelib.ratelimiting.RateLimited;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
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
@RequestMapping("/onboarding")
public class OnboardingController extends GenericCrudController<Onboarding, String> {

    protected OnboardingController(OnboardingService service) {
        super(service, "/onboarding");
    }

    @Override
    protected String getId(Onboarding entity) {
        return entity.getId();
    }

    @Override
    @Authenticated(roles = {"admin"})
    @Operation(summary = "Create a new Onboarding entry", tags = {"Onboarding"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Onboarding entry created successfully"),
    })
    public ResponseEntity<Onboarding> create(Onboarding entity, HttpServletRequest httpRequest) {
        HookManager.fire("onboarding.created", entity);
        return super.create(entity, httpRequest);
    }

    @Override
    @Operation(summary = "Delete an Onboarding entry", tags = {"Onboarding"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Onboarding entry deleted successfully",
                    content = @Content()
            ),
            @ApiResponse(responseCode = "404", description = "Onboarding entry not found",
                    content = @Content()
            )
    })
    @Authenticated(roles = {"admin"})
    public ResponseEntity<Object> delete(String s, HttpServletRequest httpRequest) {
        HookManager.fire("onboarding.deleted", s);
        return super.delete(s, httpRequest);
    }

    @Override
    @Operation(summary = "Update an Onboarding entry", tags = {"Onboarding"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Onboarding entry updated successfully"),
            @ApiResponse(responseCode = "404", description = "Onboarding entry not found",
                    content = @Content()
            )
    })
    @Authenticated(roles = {"admin"})
    public ResponseEntity<Onboarding> update(String s, Onboarding entity, HttpServletRequest httpRequest) {
        HookManager.fire("onboarding.updated", entity);
        return super.update(s, entity, httpRequest);
    }

    @Override
    @Operation(summary = "Get a specific Onboarding entry", tags = {"Onboarding"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Onboarding entry retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Onboarding entry not found",
                    content = @Content()
            )
    })
    @RateLimited(limit = 10, interval = 60)
    public ResponseEntity<Onboarding> getById(String s, HttpServletRequest httpRequest) {
        return super.getById(s, httpRequest);
    }

    @Override
    @Operation(summary = "Get all Onboarding entry", tags = {"Onboarding"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Onboarding entries retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    @RateLimited(limit = 10, interval = 60)
    public ResponseEntity<Page<Onboarding>> getAll(String search, Pageable pageable, HttpServletRequest httpRequest) {
        return super.getAll(search, pageable, httpRequest);
    }

    @Override
    protected Specification<Onboarding> buildSpecification(String search) {
        if (search == null || search.isEmpty()) {
            return null;
        }
        return (Root<Onboarding> root, CriteriaQuery<?> cq, CriteriaBuilder cb) ->
                cb.or(
                        cb.like(cb.lower(root.get("question")), "%" + search.toLowerCase() + "%")
                );
    }
}
