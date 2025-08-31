package com.bensiebert.codelib.faq.data;

import com.bensiebert.codelib.crud.CrudServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class FAQService extends CrudServiceImpl<FAQ, String, FAQRepository> {


    public FAQService(FAQRepository repository) {
        super(repository);
    }
}
