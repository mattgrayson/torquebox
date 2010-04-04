package org.torquebox.rack.core;

import java.io.ByteArrayInputStream;
import java.util.Enumeration;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.jruby.Ruby;
import org.jruby.runtime.builtin.IRubyObject;
import org.junit.Test;
import org.torquebox.test.ruby.AbstractRubyTestCase;

import static org.junit.Assert.*;

import static org.mockito.Mockito.*;

public class RubyRackApplicationTest extends AbstractRubyTestCase {

	@Test
	public void testConstruct() throws Exception {
		Ruby ruby = createRuby();

		String rackup = "run Proc.new {|env| [200, {'Content-Type' => 'text/html'}, env.inspect]}";
		RubyRackApplication rackApp = new RubyRackApplication(ruby, rackup);
		IRubyObject rubyApp = rackApp.getRubyApplication();
		assertNotNil(rubyApp);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testEnvironment() throws Exception {
		Ruby ruby = createRuby();
		String rackup = "run Proc.new {|env| [200, {'Content-Type' => 'text/html'}, env.inspect]}";
		RubyRackApplication rackApp = new RubyRackApplication(ruby, rackup);

		final ServletContext servletContext = mock(ServletContext.class);
		final HttpServletRequest servletRequest = mock(HttpServletRequest.class);

		// final InputStream inputStream = new ByteArrayInputStream(
		// "howdy".getBytes() );
		final ServletInputStream inputStream = new MockServletInputStream(new ByteArrayInputStream("".getBytes()));

		when(servletRequest.getInputStream()).thenReturn(inputStream);
		when(servletRequest.getMethod()).thenReturn("GET");
		when(servletRequest.getContextPath()).thenReturn("/");
		when(servletRequest.getServletPath()).thenReturn("myapp/");
		when(servletRequest.getPathInfo()).thenReturn("the_path");
		when(servletRequest.getQueryString()).thenReturn("cheese=cheddar&bob=mcwhirter");
		when(servletRequest.getServerName()).thenReturn("torquebox.org");
		when(servletRequest.getServerPort()).thenReturn(8080);
		when(servletRequest.getContentType()).thenReturn("text/html");
		when(servletRequest.getContentLength()).thenReturn(0);
		when(servletRequest.getRemoteAddr()).thenReturn("10.42.42.42");
		when(servletRequest.getHeaderNames()).thenReturn(enumeration("header1", "header2"));
		when(servletRequest.getHeader("header1")).thenReturn("header_value1");
		when(servletRequest.getHeader("header2")).thenReturn("header_value2");

		IRubyObject rubyEnv = (IRubyObject) rackApp.createEnvironment(servletContext, servletRequest);
		assertNotNull(rubyEnv);

		Map<String, Object> javaEnv = (Map<String, Object>) rubyEnv.toJava(Map.class);
		assertNotNull(javaEnv);

		for (String key : javaEnv.keySet()) {
			System.err.println("[" + key + "]=[" + javaEnv.get(key) + "]");
		}
		assertEquals("GET", javaEnv.get("REQUEST_METHOD"));
		assertEquals("/myapp/the_path", javaEnv.get("REQUEST_URI"));
		assertEquals("cheese=cheddar&bob=mcwhirter", javaEnv.get("QUERY_STRING"));
		assertEquals("torquebox.org", javaEnv.get("SERVER_NAME"));
		assertEquals( 8080L, javaEnv.get("SERVER_PORT"));
		assertEquals( "text/html", javaEnv.get("CONTENT_TYPE"));
		assertEquals( 0L, javaEnv.get("CONTENT_LENGTH"));
		assertEquals( "10.42.42.42", javaEnv.get("REMOTE_ADDR"));
		
		assertEquals( "header_value1", javaEnv.get( "HTTP_HEADER1" ) );
		assertEquals( "header_value2", javaEnv.get( "HTTP_HEADER2" ) );
		
		assertNotNull( javaEnv.get( "rack.input" ) );
		assertNotNull( javaEnv.get( "rack.errors" ) );
		assertSame( servletRequest, javaEnv.get( "servlet_request" ) );
		assertSame( servletRequest, javaEnv.get( "java.servlet_request" ) );

	}

	@SuppressWarnings("unchecked")
	protected Enumeration enumeration(Object... values) {
		Vector v = new Vector();
		for (Object each : values) {
			v.add(each);
		}
		return v.elements();
	}
}