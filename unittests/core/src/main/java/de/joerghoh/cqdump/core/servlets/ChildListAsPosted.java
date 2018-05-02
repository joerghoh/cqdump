package de.joerghoh.cqdump.core.servlets;

import java.io.IOException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.servlet.ServletException;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.apache.sling.commons.json.jcr.JsonJcrNode;
import org.osgi.service.component.annotations.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component(immediate=true,
			property = {
					"sling.servlet.selectors=childlist",
					"sling.servlet.methods=GET",
					"sling.servlet.extension=json"
			})

public class ChildListAsPosted extends SlingAllMethodsServlet {

	protected final Logger loger = LoggerFactory.getLogger(ChildListAsPosted.class);

	private static final long serialVersionUID = 9176255033916949528L;

	private JSONArray array;

	@Override
	public void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
			throws ServletException, IOException {

		try{

			ResourceResolver resolver = request.getResourceResolver();
			Resource resource = resolver.getResource(request.getRequestPathInfo().getResourcePath());
			Node node = null;

			if (resource != null) {
				node = resource.adaptTo(Node.class);
			}

			if (node == null){
				throw new RepositoryException();
			}

			NodeIterator it = node.getNodes();
			array = new JSONArray();

			while (it.hasNext()) {
				Node child = it.nextNode();
				if (loger.isDebugEnabled()){
					loger.debug("resource......."+child.getPath());
				}

				JSONObject obj = new JsonJcrNode(child);
				array.put(obj);
			}
			response.setContentType("application/json");
			response.getOutputStream().print(array.toString());
		}

		catch(RepositoryException e) {
			throw new ServletException("404 HTTP ERROR Page Not Found", e);
		}
		catch(JSONException e)
		{
			loger.error("Could not formulate JSON response");
			throw new ServletException("Error", e);

		}

	}

}
