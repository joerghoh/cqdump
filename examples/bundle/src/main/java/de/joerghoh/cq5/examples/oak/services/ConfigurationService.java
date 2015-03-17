package de.joerghoh.cq5.examples.oak.services;

import java.util.Map;

public interface ConfigurationService {
	
	

	
	/**
	 * Reads the configuration for a given module and returns it
	 * The configuration consists of single key-value pairs, multi-values are not supported
	 * @param module the module
	 * @return the configuration resource or null if there is no configuration for this module
	 */
	Map<String,String> getConfiguration (String module);

}
