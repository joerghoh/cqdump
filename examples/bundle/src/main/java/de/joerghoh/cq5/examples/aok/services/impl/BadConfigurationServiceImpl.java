package de.joerghoh.cq5.examples.aok.services.impl;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;

import de.joerghoh.cq5.examples.oak.services.ConfigurationService;


/**
 * This implementation of the ConfigurationService is not recommended to use, because data of a single
 * repository session can be shared to multiple resources.
 * 
 * The problem lies in the fact, that the session is opened with the thread executing the activate method,
 * so a data read thorugh that session "belong" to this thread. When other threads access data read through
 * this session, these other threads are serialized.
 * 
 *
 */

@Component()
@Service()
public class BadConfigurationServiceImpl implements ConfigurationService {

	
	private static final String basepath = "/etc/config/";
	
	@Reference
	ResourceResolverFactory rrf;
	
	ResourceResolver adminResolver;
	
	
	@Activate
	protected void activate() throws LoginException {
		adminResolver = rrf.getAdministrativeResourceResolver(null);
	}
	
	@Deactivate
	protected void deactivate() {
		if (adminResolver != null) {
			adminResolver.close();
		}
	}
	
	
	public Resource getConfiguration(String module) {
		return adminResolver.getResource(basepath + module);
	}

}
