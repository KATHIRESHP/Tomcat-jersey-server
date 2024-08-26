package com.filter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.util.concurrent.RateLimiter;

/**
 * Servlet Filter implementation class RateLimitingFilter
 */
public class RateLimitingFilter extends HttpFilter implements Filter {

	private final ConcurrentMap<String, RateLimiter> clientRateLimiters = new ConcurrentHashMap<String, RateLimiter>();

	/**
	 * @see HttpFilter#HttpFilter()
	 */
	public RateLimitingFilter() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		RateLimiter rateLimiter = clientRateLimiters.computeIfAbsent(httpRequest.getRemoteAddr(), ip -> RateLimiter.create(1));

		if (rateLimiter.tryAcquire()) {
			chain.doFilter(request, response);
		} else {
			httpResponse.setStatus(429);
			httpResponse.getWriter().write("Too many calls within a second.");
		}
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
