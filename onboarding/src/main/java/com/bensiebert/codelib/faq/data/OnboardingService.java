package com.bensiebert.codelib.faq.data;

import com.bensiebert.codelib.crud.CrudServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class OnboardingService extends CrudServiceImpl<Onboarding, String, OnboardingRepository> {


    public OnboardingService(OnboardingRepository repository) {
        super(repository);
    }
}
