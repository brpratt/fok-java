package com.brpratt.simplenetes.server;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class SimplenetesServerApplication {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public static void main(String[] args) {
		SpringApplication.run(SimplenetesServerApplication.class, args);
	}

	@GetMapping("/containers")
	public List<Container> getContainers() {
		return jdbcTemplate.query(
			"SELECT name, image FROM container",
			(rs, rowNum) -> new Container(rs.getString("name"), rs.getString("image")));
	}

	@PostMapping("/containers")
	public ResponseEntity<String> createContainer(@RequestBody Container container) {
		try {
			jdbcTemplate.update(
				"INSERT INTO container (name, image) VALUES (?, ?)",
				container.name(),
				container.image());
		} catch (DuplicateKeyException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Container already exists");
		}

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@GetMapping("/containers/{name}")
	public ResponseEntity<Container> getContainer(@PathVariable String name) {
		var containers = jdbcTemplate.query(
			"SELECT name, image FROM container WHERE name = ?",
			(rs, rowNum) -> new Container(rs.getString("name"), rs.getString("image")),
			name);

		if (containers.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		return ResponseEntity.status(HttpStatus.OK).body(containers.get(0));
	}

	@DeleteMapping("/containers/{name}")
	public ResponseEntity<String> deleteContainer(@PathVariable String name) {
		var rowsAffected = jdbcTemplate.update("DELETE FROM container WHERE name = ?", name);

		if (rowsAffected == 0) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Container not found");
		}

		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
