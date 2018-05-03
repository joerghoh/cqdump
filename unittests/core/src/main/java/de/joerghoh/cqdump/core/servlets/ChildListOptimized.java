package de.joerghoh.cqdump.core.servlets;

import java.io.IOException;
import java.util.Iterator;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.servlet.ServletException;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.apache.sling.commons.json.jcr.JsonJcrNode;
import org.apache.sling.commons.json.sling.JsonObjectCreator;
import org.osgi.service.component.annotations.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component(immediate=true,
			property = {
					"sling.servlet.selectors=childlist",
					"sling.servlet.methods=GET",
					"sling.servlet.extension=json"
			})

public class ChildListOptimized extends SlingAllMethodsServlet {

	protected final Logger loger = LoggerFactory.getLogger(ChildListOptimized.class);
	private static final long serialVersionUID = 9176255033916949528L;

	@Override
	public void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
			throws ServletException, IOException {

		try {

			Resource resource = request.getResource();
			JSONArray array = new JSONArray();

			Iterator<Resource> children = resource.listChildren();
			while (children.hasNext()) {
				Resource child = children.next();
				JSONObject obj = JsonObjectCreator.create(child, 0);
				array.put(obj);
			}
			response.setContentType("application/json");
			response.getOutputStream().print(array.toString());

		} catch (JSONException e) {
			loger.error("Could not formulate JSON response");
			throw new ServletException("Error", e);
		}

	}
	
	
	private JSONObject convertResourceToJSON (Resource resource) throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put("jcr:path", resource.getPath());
		ValueMap vm = resource.adaptTo(ValueMap.class);
		vm.forEach((key,value) -> {
			vm.put(key, value);	
		});
		
		return obj;
	}

}
