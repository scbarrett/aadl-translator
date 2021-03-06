package edu.ksu.cis.projects.mdcf.aadltranslator.model;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import edu.ksu.cis.projects.mdcf.aadltranslator.model.ModelUtil.ComponentType;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.ModelUtil.ProcessType;

public class DeviceModel extends ComponentModel {
	
	private HashBiMap<String, String> inToOutPortNames = HashBiMap.create();
	
	public DeviceModel(){
		super();
		processType = ProcessType.PSEUDODEVICE;
	}
	
	public void setComponentType(String componentType){
		this.componentType = ComponentType.valueOf(componentType.toUpperCase());
	}
	
	public void addOutPortName(String inPortName, String outPortName){
		inToOutPortNames.put(inPortName, outPortName);
	}
	
	public HashBiMap<String, String> getOutPortNames(){
		return inToOutPortNames;
	}
	
	public BiMap<String, String> getInPortNames(){
		return inToOutPortNames.inverse();
	}
}
