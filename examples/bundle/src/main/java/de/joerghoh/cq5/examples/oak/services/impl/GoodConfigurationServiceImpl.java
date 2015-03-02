package de.joerghoh.cq5.examples.aok.services.impl;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
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
	
	
	public Resource getConfiguration (String module) {
		Resource result = null;
		ResourceResolver adminResolver = null;
		try {
			adminResolver = rrf.getAdministrativeResourceResolver(null);
			result = adminResolver.getResource(basepath + module);
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
