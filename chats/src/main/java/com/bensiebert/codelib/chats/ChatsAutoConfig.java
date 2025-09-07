package com.bensiebert.codelib.chats;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EntityScan(basePackages = "com.bensiebert.codelib.chats.data")
@EnableJpaRepositories(basePackages = "com.bensiebert.codelib.chats.data")
@EnableTransactionManagement
public class ChatsAutoConfig {
}
