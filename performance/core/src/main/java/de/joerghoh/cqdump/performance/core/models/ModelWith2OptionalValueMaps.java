package de.joerghoh.cqdump.performance.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class)
public class ModelWith2OptionalValueMaps implements PerformanceTestModel {
	
	
	@ValueMapValue(name="key1")
	@Default(values="")
	protected String key1;
	
	@ValueMapValue(name="key2")
	@Default(values = "")
	protected String key2;
	
	@ValueMapValue(name="key3")
	@Default(values = "")
	protected String key3;
	
	
	@ValueMapValue(name="nonExistingProperty",injectionStrategy = InjectionStrategy.OPTIONAL)
	@Default(values = "")
	protected String nonExistingProperty;
	
	@ValueMapValue(name="nonExistingProperty2", injectionStrategy = InjectionStrategy.OPTIONAL)
	@Default(values = "")
	protected String nonExistingProperty2;
	

	@Override
	public void validate() {
		if (!nonExistingProperty2.equals("")) {
			throw new RuntimeException("failed validation");
		}
		
	}

}
