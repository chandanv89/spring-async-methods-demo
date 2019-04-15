package com.github.chandanv89.springasyncmethodsdemo.service;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.github.chandanv89.springasyncmethodsdemo.model.User;

@Service
public class GithubLookupService {

	private static final Logger LOG = LoggerFactory.getLogger(GithubLookupService.class);

	@Autowired
	private RestTemplate restTemplate;

	/**
	 * Query GitHub for the given username.
	 * 
	 * @param user the user name
	 * @return the promise containing the response
	 * @throws InterruptedException
	 */
	@Async
	public CompletableFuture<User> findUser(String user) throws InterruptedException {
		LOG.info("Looking up " + user);

		String url = String.format("https://api.github.com/users/%s", user);
		User results = new User();

		long start = System.currentTimeMillis();

		try {
			results = restTemplate.getForObject(url, User.class);
		} catch (RestClientException e) {
			LOG.error(e.getMessage());
		}

		LOG.info("$$$ Time taken: {} ms", (System.currentTimeMillis() - start));

		LOG.debug("Response: {}", results);

		return CompletableFuture.completedFuture(results);
	}

}
