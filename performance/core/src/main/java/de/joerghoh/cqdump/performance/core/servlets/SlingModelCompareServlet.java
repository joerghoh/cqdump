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
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.joerghoh.cqdump.performance.core.models.ModelWith2OptionalInjects;
import de.joerghoh.cqdump.performance.core.models.ModelWith2OptionalValueMaps;
import de.joerghoh.cqdump.performance.core.models.ModelWithOptionalInject;
import de.joerghoh.cqdump.performance.core.models.ModelWithOptionalValueMap;
import de.joerghoh.cqdump.performance.core.models.PerformanceTestModel;
import de.joerghoh.cqdump.performance.core.models.ModelWith3Injects;
import de.joerghoh.cqdump.performance.core.models.ModelWith3ValueMaps;

@Component(service = { Servlet.class })
@SlingServletPaths(value = "/bin/slingmodelcompare")
@ServiceDescription("Sling Model Performance Comparison Servlet")
public class SlingModelCompareServlet  extends SlingSafeMethodsServlet {
	
	private static final Logger LOG = LoggerFactory.getLogger(SlingModelCompareServlet.class);
	
	
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
		
		runSingleTest(testResource,output,ModelWith3Injects.class);
		runSingleTest(testResource,output,ModelWith3ValueMaps.class);
		runSingleTest(testResource,output,ModelWithOptionalValueMap.class);
		runSingleTest(testResource,output,ModelWith2OptionalValueMaps.class);
		runSingleTest(testResource,output,ModelWithOptionalInject.class);
		runSingleTest(testResource,output,ModelWith2OptionalInjects.class);
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
	
	

}
