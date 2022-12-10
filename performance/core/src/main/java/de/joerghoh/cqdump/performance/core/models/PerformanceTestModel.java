package de.joerghoh.cqdump.performance.core.models;

public interface PerformanceTestModel {

	default String getName() {
		return this.getClass().getName();
	}
	
	default boolean displayInNav() {
		return false;
	}
	
	
	void validate();
	
}
