package com.bensiebert.codelib.testapp.crudtest;

import com.bensiebert.codelib.crud.CrudServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ProductService extends CrudServiceImpl<Product, String, ProductRepository> {


    public ProductService(ProductRepository repository) {
        super(repository);
    }
}
