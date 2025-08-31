package com.bensiebert.codelib.onboarding.data;

import com.bensiebert.codelib.auth.annotations.Authenticated;
import com.bensiebert.codelib.crud.GenericCrudController;
import com.bensiebert.codelib.hooks.HookManager;
import com.bensiebert.codelib.ratelimiting.RateLimited;
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
    public ResponseEntity<Onboarding> create(Onboarding entity) {
        HookManager.fire("onboarding.created", entity);
        return super.create(entity);
    }

    @Override
    @Authenticated(roles = {"admin"})
    public ResponseEntity<Object> delete(String s) {
        HookManager.fire("onboarding.deleted", s);
        return super.delete(s);
    }

    @Override
    @Authenticated(roles = {"admin"})
    public ResponseEntity<Onboarding> update(String s, Onboarding entity) {
        HookManager.fire("onboarding.updated", entity);
        return super.update(s, entity);
    }

    @Override
    @RateLimited(limit = 10, interval = 60)
    public ResponseEntity<Onboarding> getById(String s) {
        return super.getById(s);
    }

    @Override
    @RateLimited(limit = 10, interval = 60)
    public ResponseEntity<Page<Onboarding>> getAll(String search, Pageable pageable) {
        return super.getAll(search, pageable);
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
