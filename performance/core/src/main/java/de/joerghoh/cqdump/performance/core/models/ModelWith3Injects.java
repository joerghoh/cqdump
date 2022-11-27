package de.joerghoh.cqdump.performance.core.models;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;

@Model(adaptables = Resource.class)
public class ModelWith3Injects implements PerformanceTestModel {
	
	@Inject
	@Named("key1")
	@Default(values="")
	protected String key1;
	
	@Inject
	@Named("key2")
	@Default(values = "")
	protected String key2;
	
	@Inject
	@Named("key3")
	@Default(values = "")
	protected String key3;
	
	
	public String getKey3() {
		return key3;
	}
	
	public void validate() {
		if (getKey3().equals("")) {
			throw new RuntimeException (this.getName() + " validation failed");
		}
	}

}
