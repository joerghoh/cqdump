package de.joerghoh.cq5.examples.oak.services;

import org.apache.sling.api.resource.Resource;

public interface ConfigurationService {
	
	

	
	/**
	 * Reads the configuration for a given module and returns it
	 * @param module the module
	 * @return the configuration resource or null if there is no configuration for this module
	 */
	Resource getConfiguration (String module);

}
