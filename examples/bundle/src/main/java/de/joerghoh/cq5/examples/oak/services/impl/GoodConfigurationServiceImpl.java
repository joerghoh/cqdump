package de.joerghoh.cq5.examples.oak.services.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.LoggerFactory;

import de.joerghoh.cq5.examples.oak.services.ConfigurationService;

/**
 * This is the recommended way to implement the ConfigurationService.
 * 
 * Each thread executing the getConfiguration() method opens a new admin session and then reads the requested
 * nodes from the repository (Sling resources are just wrapped JCR nodes). There is no scalability issue here.
 * 
 * The repeated logins to the repository shouldn't be a problem.
 * 
 *
 */

@Component()
@Service()
public class GoodConfigurationServiceImpl implements ConfigurationService {

	private static final String basepath = "/etc/config/";
	
	@Reference
	ResourceResolverFactory rrf;
	
	
	public Map<String,String> getConfiguration (String module) {
		Map<String,String> result = new HashMap<String,String>();
		ResourceResolver adminResolver = null;
		try {
			adminResolver = rrf.getAdministrativeResourceResolver(null);
			Resource r =  adminResolver.getResource(basepath + module);
			ValueMap vm = r.adaptTo(ValueMap.class);
			for (String k: vm.keySet()) {
				result.put(k, vm.get(k).toString());
			}
			return result;
		} catch (LoginException e) {
			e.printStackTrace();
		} finally {
			if (adminResolver != null) {
				adminResolver.close();
			}
		}
		return result;
		
	}
	
}
