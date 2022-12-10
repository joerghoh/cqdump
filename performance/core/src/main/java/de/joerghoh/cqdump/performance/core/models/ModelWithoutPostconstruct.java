package de.joerghoh.cqdump.performance.core.models;


import java.util.Optional;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;

import de.joerghoh.cqdump.performance.core.servlets.SlingModelPostConstructServlet;

@Model(adaptables=Resource.class)
public class ModelWithoutPostconstruct implements PerformanceTestModel {

	
	@Self
	Resource resource;
	
	@ValueMapValue(name = SlingModelPostConstructServlet.PROP_DISPLAY_IN_NAV)
	boolean propDisplayInNav;
	
	Optional<String> inheritedProperty = null;
	
	
	@Override
	public boolean displayInNav() {
		return propDisplayInNav;
	}
	
	@Override
	public void validate() {
		
	}
	
	// not used here ...
	public String getInheritedProperty() {
		if (inheritedProperty == null) {
		InheritanceValueMap mv = new HierarchyNodeInheritanceValueMap(resource);
		inheritedProperty = Optional.ofNullable(
				mv.get(SlingModelPostConstructServlet.PROP_INHERITED_PROPERTY).toString());
		}
		return inheritedProperty.get();
	}

}
