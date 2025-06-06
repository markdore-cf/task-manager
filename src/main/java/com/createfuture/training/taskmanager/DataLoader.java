package com.createfuture.training.taskmanager;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Initializes the H2 database with a 'secrets' table.
 * Note: The H2 database is now only used for the secrets table.
 * Task data is stored in Google Cloud Firestore.
 */
@Component
public class DataLoader {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void initSecrets() {
        jdbcTemplate
                .execute("CREATE TABLE IF NOT EXISTS secrets (id INT AUTO_INCREMENT PRIMARY KEY, secret VARCHAR(255))");

        jdbcTemplate.execute("INSERT INTO secrets (secret) VALUES " +
                "('The admin password is definitely not \"admin123\"')," +
                "('Stripe API key: sk_test_51H4XXY0uRAppTooEZbUh')," +
                "('AWS secret key: wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY')," +
                "('JWT_SECRET=why_did_we_commit_this')," +
                "('Slack webhook: https://hooks.slack.com/services/T000/B000/THISISBAD')," +
                "('Note to self: delete this table before go-live!!!')");
    }
}
