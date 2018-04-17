package de.joerghoh.cqdump.caconfig.workflow;

import org.apache.sling.caconfig.annotation.Configuration;
import org.apache.sling.caconfig.annotation.Property;

@Configuration(label="CA-Config based Participant Step")
public @interface CaConfiguParticipantChooserConfig {
	
	@Property(label="Approver group")
	String approverGroup() default "admin";

}
