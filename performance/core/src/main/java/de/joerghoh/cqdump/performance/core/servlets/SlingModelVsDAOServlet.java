package de.joerghoh.cqdump.performance.core.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.joerghoh.cqdump.performance.core.models.ModelWith3ValueMaps;
import de.joerghoh.cqdump.performance.core.models.PerformanceTestModel;

@Component(service = { Servlet.class })
@SlingServletPaths(value = "/bin/slingmodel-vs-dao")
@ServiceDescription("Performance Sling Models vs DAO Servlet")
public class SlingModelVsDAOServlet   extends SlingSafeMethodsServlet {

	private static final Logger LOG = LoggerFactory.getLogger(SlingModelVsDAOServlet.class);
	
	
	@Reference
	ResourceResolverFactory rrf;
	
	
	private static final long serialVersionUID = 1L;
	private static final String TEST_ROOT = "/content/cqdump/performance";
	
	private static final int CHILDNODES = 5_000;
	private static final int ITERATIONS = 10;
	
	private static final Map<String,Object> props = Map.of("key1","value1","key2","value2","key3","value3");
	
	
	private static Map<String,Object> resolverProps = new HashMap<>();
	static {
		resolverProps.put(ResourceResolverFactory.SUBSERVICE, "cqdump-writer");
	}
	
	
	@Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        Resource root = null;
        PrintWriter output = response.getWriter();
		try {
        	root = createTestData();
        	response.getWriter().println("test data created below " + root.getPath());
        	Thread.sleep(2000);
        	runTests(root, output);
        	
        } catch (Exception e) {
			LOG.error("Exception", e);
			response.getWriter().write("Exception " + e.getMessage());
		} finally {
        	cleanupTestData(request.getResourceResolver());
        }
    }

	private void cleanupTestData(ResourceResolver resolver) throws PersistenceException {
		Resource root = resolver.getResource(TEST_ROOT);
		for (Resource child : root.getChildren()) {
			if (child.getName().startsWith("child_")) {
				resolver.delete(child);
			}
		}
		resolver.commit();
	}

	private Resource createTestData() throws Exception {
		try (ResourceResolver resourceResolver = rrf.getServiceResourceResolver(resolverProps)) {
			Resource root = resourceResolver.getResource(TEST_ROOT);
			if (root == null) {
				throw new Exception("cannot find root resource at " + TEST_ROOT);
			}
			
			for (int i=0; i < CHILDNODES; i++) {
				String name = "child_" + i;
				Resource child = resourceResolver.create(root, name, props);
				resourceResolver.create(child, "child1", null);
				resourceResolver.create(child, "child2", null);
				resourceResolver.create(child, "child3", null);
			}
			
			resourceResolver.commit();
			return root;
		}
		
	} 
	
	
	private void runTests(Resource testResource, PrintWriter output) throws Exception {
		
		runSingleTest(testResource,output,ModelWith3ValueMaps.class);
		runSingleTestWithDAO(testResource,output);

	}
	
	private void runSingleTest(Resource testResource, PrintWriter output, 
			Class<? extends PerformanceTestModel> modelClass) throws LoginException {
		
		long nanos = 0;
		for (int i=0;i < ITERATIONS; i++) {
			try (ResourceResolver rr = rrf.getServiceResourceResolver(resolverProps);) {
				Resource sut = rr.getResource(testResource.getPath());
				nanos += runSingleTestInternal(sut, modelClass);
			}
		}
		final long duration = TimeUnit.NANOSECONDS.toMicros(nanos / (ITERATIONS * CHILDNODES));
		output.write(String.format("%s:\t single adaption took %s microseconds\n", modelClass.getName(), duration));
		output.flush();
	}
	
	private long runSingleTestInternal (Resource root, Class<? extends PerformanceTestModel> modelClass) {
		StopWatch sw = new StopWatch();
		sw.start();
		for (Resource data: root.getChildren()) {
			if (data.getName().startsWith("child_")) {
				PerformanceTestModel model =  data.adaptTo(modelClass);
				validateResult(model);
			}
			
		}
		sw.stop();
		return sw.getNanoTime();
	}
	
	private static void validateResult(PerformanceTestModel model) {
		if (model == null) {
			throw new RuntimeException("adaption returned null");
		}
		model.validate();
	}
	
	
	// --- DAO code ----
	
	private void runSingleTestWithDAO(Resource testResource, PrintWriter output) throws Exception {
		long nanos = 0;
		for (int i=0;i < ITERATIONS; i++) {
			try (ResourceResolver rr = rrf.getServiceResourceResolver(resolverProps);) {
				Resource sut = rr.getResource(testResource.getPath());
				nanos += runSingleTestInternaDAOl(sut);
			}
		}
		final long duration = TimeUnit.NANOSECONDS.toMicros(nanos / (ITERATIONS * CHILDNODES));
		output.write(String.format("runSingleTestDAO:\t\t\t\t\t\t creating a single DAO took %s microseconds\n",  duration));
		output.flush();
	}
	
	private long runSingleTestInternaDAOl (Resource root) {
		StopWatch sw = new StopWatch();
		sw.start();
		for (Resource data: root.getChildren()) {
			if (data.getName().startsWith("child_")) {
				PerformanceTestDAO dao = buildDAOFromResource(data);
			}
			
		}
		sw.stop();
		return sw.getNanoTime();
	}
	
	
	private PerformanceTestDAO buildDAOFromResource(Resource r) {
		
		ValueMap vm = r.getValueMap();
		PerformanceTestDAO dao = new PerformanceTestDAO();
		dao.key1 = vm.getOrDefault("key1", "").toString();
		dao.key2 = vm.getOrDefault("key2", "").toString();
		dao.key3 = vm.getOrDefault("key3", "").toString();
		dao.validate();
		return dao;
		
	}
	
	
	public class PerformanceTestDAO {
		
		String key1;
		String key2;
		String key3;
		
		String getKey3() {
			return key3;
		}
		
		public void validate() {
			if (getKey3().equals("")) {
				throw new RuntimeException ("validation failed");
			}
		}
		
		
	}
	
	
	
}
