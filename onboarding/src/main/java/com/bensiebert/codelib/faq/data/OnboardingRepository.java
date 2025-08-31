package com.bensiebert.codelib.faq.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface OnboardingRepository extends JpaRepository<Onboarding, String>, JpaSpecificationExecutor<Onboarding> {
}
