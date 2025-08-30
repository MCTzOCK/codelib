package com.bensiebert.codelib.faq;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EntityScan(basePackages = "com.bensiebert.codelib.faq.data")
@EnableJpaRepositories(basePackages = "com.bensiebert.codelib.faq.data")
@EnableTransactionManagement
public class FAQAutoConfig {
}
