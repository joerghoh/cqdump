package de.joerghoh.cqdump.samples.unittest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.joerghoh.cqdump.core.servlets.ChildListAsPosted;
import de.joerghoh.cqdump.core.servlets.ChildListOptimized;
import io.wcm.testing.mock.aem.junit.AemContext;

public class ChildListTest {
	
	@Rule
	public final AemContext context = new AemContext(ResourceResolverType.JCR_MOCK);
	
	static final Logger LOG = LoggerFactory.getLogger(ChildListTest.class);
	
	
	@Before
	public void setup() {

		context.load().json(getClass().getResourceAsStream(getClass().getSimpleName() + ".json"),"/content");
		
	}
	
	
	@Test
	public void testOriginalVersion() throws ServletException, IOException, JSONException {
		ChildListAsPosted servlet = new ChildListAsPosted();
		
		context.currentPage(context.pageManager().getPage("/content/mycompany"));
		context.currentResource(context.resourceResolver().getResource("/content/mycompany"));
		context.requestPathInfo().setResourcePath("/content/mycompany");

		servlet.doGet(context.request(), context.response());
		
		assertTrue("Incorrect content type received","application/json".equals(context.response().getContentType()));
		
		LOG.info("output = {}", context.response().getOutputAsString());
		JSONArray output = new JSONArray(context.response().getOutputAsString());
		assertEquals("Received incorrect number of JSON objects in array",3,output.length());
	}
	
	
	@Test
	public void testOptimizedVersion() throws ServletException, IOException, JSONException {
		ChildListOptimized servlet = new ChildListOptimized();
		
		context.currentPage(context.pageManager().getPage("/content/mycompany"));
		context.currentResource(context.resourceResolver().getResource("/content/mycompany"));
		context.requestPathInfo().setResourcePath("/content/mycompany");

		servlet.doGet(context.request(), context.response());
		
		assertTrue("Incorrect content type received","application/json".equals(context.response().getContentType()));
		
		LOG.info("output = {}", context.response().getOutputAsString());
		JSONArray output = new JSONArray(context.response().getOutputAsString());
		assertEquals("Received incorrect number of JSON objects in array",3,output.length());
	}
	
	
}
