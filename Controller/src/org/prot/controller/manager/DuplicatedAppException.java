package org.prot.controller.manager;

public class DuplicatedAppException extends Exception {

	private String appId; 
	
	public DuplicatedAppException(String appId) {
		this.appId = appId; 
	}
	
	@Override
	public String toString() {
		return "Duplicated App with Id " + this.appId;  
	}
}
