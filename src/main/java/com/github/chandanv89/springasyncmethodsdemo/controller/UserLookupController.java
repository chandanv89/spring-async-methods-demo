package com.github.chandanv89.springasyncmethodsdemo.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.github.chandanv89.springasyncmethodsdemo.model.User;
import com.github.chandanv89.springasyncmethodsdemo.service.GithubLookupService;

@RestController
public class UserLookupController {
	private static final Logger LOG = LoggerFactory.getLogger(UserLookupController.class);

	@Autowired
	private GithubLookupService service;

	@PostMapping("/github/users")
	public ResponseEntity<?> getGithubUsers(@RequestBody List<String> userNames) {
		long start = System.currentTimeMillis();

		if (CollectionUtils.isEmpty(userNames)) {
			LOG.error("Please provide at least one username for lookup");
			return new ResponseEntity<>("Please provide at least one username for lookup", HttpStatus.BAD_REQUEST);
		}

		List<User> users = new ArrayList<>();
		CompletableFuture<?>[] futures = new CompletableFuture[userNames.size()];

		try {
			for (int i = 0; i < userNames.size(); i++) {
				futures[i] = service.findUser(userNames.get(i));
			}

			CompletableFuture.allOf(futures);

			for (CompletableFuture<?> cf : futures) {
				users.add((User) cf.get());
			}
		} catch (InterruptedException | ExecutionException e) {
			LOG.error(e.getLocalizedMessage());
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		LOG.info("$$$ Total time taken: {} ms", (System.currentTimeMillis() - start));

		return new ResponseEntity<>(users, HttpStatus.OK);
	}

}
