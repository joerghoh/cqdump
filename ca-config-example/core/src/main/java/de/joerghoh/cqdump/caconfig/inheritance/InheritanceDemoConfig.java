package de.joerghoh.cqdump.caconfig.inheritance;

import org.apache.sling.caconfig.annotation.Configuration;
import org.apache.sling.caconfig.annotation.Property;

@Configuration(label="Configuration to demo the inheritance concept")
public @interface InheritanceDemoConfig {
	
	@Property(label="Just some Property")
	String aProperty() default "someValue";

}
