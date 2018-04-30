package de.joerghoh.cqdump.caconfig.workflow;

import org.apache.sling.caconfig.annotation.Configuration;
import org.apache.sling.caconfig.annotation.Property;

@Configuration(label="CA-Config based Participant Step")
public @interface CaConfigParticipantStepChooserConfig {
	
	@Property(label="Approver group")
	String approverGroup() default "admin";

}
