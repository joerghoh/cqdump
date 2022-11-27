package de.joerghoh.cqdump.performance.core.models;

import javax.inject.Named;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class)
public class ModelWith3ValueMaps implements PerformanceTestModel {
	
	@ValueMapValue
	@Named("key1")
	@Default(values="")
	protected String key1;
	
	@ValueMapValue
	@Named("key2")
	@Default(values = "")
	protected String key2;
	
	@ValueMapValue
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
