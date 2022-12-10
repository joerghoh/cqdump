package de.joerghoh.cqdump.performance.core.models;

import javax.annotation.PostConstruct;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;

import de.joerghoh.cqdump.performance.core.servlets.SlingModelPostConstructServlet;

@Model(adaptables=Resource.class)
public class ModelWithPostconstruct implements PerformanceTestModel {

	
	@Self
	Resource resource;
	
	@ValueMapValue(name = SlingModelPostConstructServlet.PROP_DISPLAY_IN_NAV)
	String propDisplayInNav;
	
	String inheritedProperty;
	
	
	@PostConstruct
	public void postConstruct() {
		InheritanceValueMap ivm = new HierarchyNodeInheritanceValueMap(resource);
		inheritedProperty = ivm.getInherited(SlingModelPostConstructServlet.PROP_INHERITED_PROPERTY,"empty");		
	}
	
	
	@Override
	public boolean displayInNav() {
		return Boolean.valueOf(propDisplayInNav);
	}
	
	@Override
	public void validate() {
		// nothing
		
	}
	
	// not used here ...
	public String getInheritedProperty() {
		return inheritedProperty;
	}

}
