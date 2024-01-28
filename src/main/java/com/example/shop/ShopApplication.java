package com.example.shop;

import jakarta.annotation.PostConstruct;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.sql.DataSource;


@SpringBootApplication
public class ShopApplication {

	@Autowired
	private DataSource dataSource;

	public static void main(String[] args) {
		SpringApplication.run(ShopApplication.class, args);
	}
	@PostConstruct
	public void migrateDatabase() {
		Flyway flyway = Flyway.configure()
				.dataSource(dataSource)
				.locations("classpath:db/migration")
				.baselineOnMigrate(true)
				.load();
		flyway.migrate();
	}
}
