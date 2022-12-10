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
import org.apache.sling.api.resource.ModifiableValueMap;
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

import de.joerghoh.cqdump.performance.core.models.ModelWithPostconstruct;
import de.joerghoh.cqdump.performance.core.models.ModelWithoutPostconstruct;
import de.joerghoh.cqdump.performance.core.models.PerformanceTestModel;

@Component(service = { Servlet.class })
@SlingServletPaths(value = "/bin/slingmodelpostconstruct")
@ServiceDescription("Sling Model Performance Comparison Servlet -- PostConstruct")
public class SlingModelPostConstructServlet extends SlingSafeMethodsServlet {
	
	private static final long serialVersionUID = 1L;

	public static final String PROP_INHERITED_PROPERTY="propertyToInherit";
	public static final String PROP_DISPLAY_IN_NAV="hideInNav";
	
	private static final Logger LOG = LoggerFactory.getLogger(SlingModelPostConstructServlet.class);
	
	private static final int CHILDNODES = 5_000;
	private static final int ITERATIONS = 10;
	
	private static final String TEST_ROOT = "/content/cqdump/performance";
	private static Map<String,Object> resolverProps = new HashMap<>();
	static {
		resolverProps.put(ResourceResolverFactory.SUBSERVICE, "cqdump-writer");
	}
	private static final Map<String,Object> childProps = Map.of(PROP_DISPLAY_IN_NAV,"true");
	

	
	@Reference
	ResourceResolverFactory rrf;
	
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
		ModifiableValueMap mvm = root.adaptTo(ModifiableValueMap.class);
		mvm.remove("propertyToInherit");
		resolver.commit();
	}

	private Resource createTestData() throws Exception {
		try (ResourceResolver resourceResolver = rrf.getServiceResourceResolver(resolverProps)) {
			Resource root = resourceResolver.getResource(TEST_ROOT);
			if (root == null) {
				throw new Exception("cannot find root resource at " + TEST_ROOT);
			}
			ModifiableValueMap mvm = root.adaptTo(ModifiableValueMap.class);
			mvm.put(PROP_INHERITED_PROPERTY,"value");
			
			for (int i=0; i < CHILDNODES; i++) {
				String name = "child_" + i;
				resourceResolver.create(root, name, childProps);
			}
			
			resourceResolver.commit();
			return root;
		}
		
	} 
	
	
	private void runTests(Resource root, PrintWriter output) throws Exception {
		runSingleTest(root,output,ModelWithPostconstruct.class);
		runSingleTest(root,output,ModelWithoutPostconstruct.class);
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



	private long runSingleTestInternal(Resource root,  Class<? extends PerformanceTestModel> modelClass) {
		
		StopWatch sw = new StopWatch();
		sw.start();
		for (Resource data: root.getChildren()) {
			if (data.getName().startsWith("child_")) {
				PerformanceTestModel model =  data.adaptTo(modelClass);
				validateResult(model,data);
				if (model.displayInNav()) {
					// do Nothing here
				}
			}
			
		}
		sw.stop();
		return sw.getNanoTime();
		
	}
	
	private static void validateResult(PerformanceTestModel model, Resource res) {
		if (model == null) {
			ValueMap vm = res.getValueMap();
			String hideInNav = vm.get(PROP_DISPLAY_IN_NAV).toString();
			String msg = String.format("adaption returned null on %s (%s)(%s)", res.getPath(), res.toString(),hideInNav);
			throw new RuntimeException(msg);
		}
	}
	
}
