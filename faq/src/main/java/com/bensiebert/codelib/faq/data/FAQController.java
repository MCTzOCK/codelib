package com.bensiebert.codelib.faq.data;

import com.bensiebert.codelib.auth.annotations.Authenticated;
import com.bensiebert.codelib.crud.GenericCrudController;
import com.bensiebert.codelib.hooks.HookManager;
import com.bensiebert.codelib.ratelimiting.RateLimited;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
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
    @Operation(summary = "Create a new FAQ entry", tags = {"faqs"})
    public ResponseEntity<FAQ> create(FAQ entity) {
        HookManager.fire("faq.created", entity);
        return super.create(entity);
    }

    @Override
    @Authenticated(roles = {"admin"})
    @Operation(summary = "Delete a FAQ entry", tags = {"faqs"})
    public ResponseEntity<Object> delete(String s) {
        HookManager.fire("faq.deleted", s);
        return super.delete(s);
    }

    @Override
    @Authenticated(roles = {"admin"})
    @Operation(summary = "Update a FAQ entry", tags = {"faqs"})
    public ResponseEntity<FAQ> update(String s, FAQ entity) {
        HookManager.fire("faq.updated", entity);
        return super.update(s, entity);
    }

    @Override
    @RateLimited(limit = 10, interval = 60)
    @Operation(summary = "Get a specific FAQ entry", tags = {"faqs"})
    public ResponseEntity<FAQ> getById(String s) {
        return super.getById(s);
    }

    @Override
    @RateLimited(limit = 10, interval = 60)
    @Operation(summary = "Get all FAQ entries", tags = {"faqs"})
    public ResponseEntity<Page<FAQ>> getAll(String search, Pageable pageable) {
        return super.getAll(search, pageable);
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
