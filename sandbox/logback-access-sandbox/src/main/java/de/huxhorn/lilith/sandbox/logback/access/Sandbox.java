package de.huxhorn.lilith.sandbox.logback.access;

import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableAutoConfiguration
public class Sandbox
{
	private final Logger logger = LoggerFactory.getLogger(Sandbox.class);

	@RequestMapping("/")
	String home()
	{
		if (logger.isInfoEnabled()) logger.info("Requested root.");
		return "Hello World!";
	}

	@RequestMapping("/nope")
	String throwSomeStuff()
	{
		if (logger.isWarnEnabled()) logger.warn("Requested /nope.");
		throw new IllegalStateException("Nope nope nope nope nope.");
	}

	@RequestMapping("/foo/**")
	String catchAllFoo(HttpServletRequest request)
	{
		String requestUri = request.getRequestURI();
		if (logger.isInfoEnabled()) logger.info("request.requestURI: {}", requestUri);

		return "Hello World for '" + requestUri + "'!";
	}

	public static void main(String[] args)
			throws Exception
	{
		SpringApplication.run(Sandbox.class, args);
	}
}
