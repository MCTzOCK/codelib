package com.bensiebert.codelib.testapp.crudtest;

import com.bensiebert.codelib.auth.annotations.Authenticated;
import com.bensiebert.codelib.crud.CrudService;
import com.bensiebert.codelib.crud.GenericCrudController;
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
@RequestMapping("/products")
public class ProductController extends GenericCrudController<Product, String> {

    protected ProductController(ProductService service) {
        super(service,"/products");
    }

    @Override
    protected String getId(Product entity) {
        return entity.getId();
    }

    @Override
    @Authenticated(roles = {"admin"})
    public ResponseEntity<Product> create(Product entity) {
        return super.create(entity);
    }

    @Override
    protected Specification<Product> buildSpecification(String search) {
        if(search == null || search.isEmpty()) {
            return null;
        }
        return (Root<Product> root, CriteriaQuery<?> cq, CriteriaBuilder cb) ->
                cb.or(
                        cb.like(cb.lower(root.get("name")), "%" + search.toLowerCase() + "%"),
                        cb.like(cb.lower(root.get("description")), "%" + search.toLowerCase() + "%")
                );
    }
}
