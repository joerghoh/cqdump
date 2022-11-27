package de.joerghoh.cqdump.performance.core.models;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class)
public class ModelWith2OptionalInjects implements PerformanceTestModel {
	
	
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
	
	
	@Inject
	@Named("nonExistingProperty")
	@Optional
	@Default(values = "")
	protected String nonExistingProperty;
	
	@Inject
	@Named("nonExistingProperty2")
	@Optional
	@Default(values = "")
	protected String nonExistingProperty2;
	

	@Override
	public void validate() {
		if (!nonExistingProperty2.equals("")) {
			throw new RuntimeException("failed validation");
		}
		
	}

}
