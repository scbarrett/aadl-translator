package edu.ksu.cis.projects.mdcf.aadltranslator.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.google.common.collect.Maps;

import edu.ksu.cis.projects.mdcf.aadltranslator.exception.DuplicateElementException;

public class SystemModel {
	private String name;
	private HashMap<String, ProcessModel> logicComponents;
	private HashMap<String, DeviceModel> devices;
	private HashMap<String, ConnectionModel> channels;

	// Type name -> Process Model
	private HashMap<String, ComponentModel> typeToComponent;
	
	// Element name -> Element model
	private HashMap<String, StpaPreliminaryModel> stpaPreliminaries; 

	public SystemModel() {
		logicComponents = new HashMap<>();
		typeToComponent = new HashMap<>();
		channels = new HashMap<>();
		devices = new HashMap<>();
		stpaPreliminaries = new HashMap<>();
	}
	
	public void addAccidentLevel(AccidentLevelModel alm) throws DuplicateElementException {
		addStpaPreliminary(alm);
	}
	
	public void addAccident(AccidentModel am) throws DuplicateElementException {
		addStpaPreliminary(am);
	}

	public void addHazard(HazardModel hm) throws DuplicateElementException{
		addStpaPreliminary(hm);
	}

	public void addConstraint(ConstraintModel cm) throws DuplicateElementException{
		addStpaPreliminary(cm);
	}
	
	public AccidentLevelModel getAccidentLevelByName(String name){
		return (AccidentLevelModel) getStpaPreliminary(name);
	}
	
	public Map<String, StpaPreliminaryModel> getAccidentLevels() {
		return Maps.filterValues(stpaPreliminaries, ModelUtil.accidentLevelFilter);
	}
	
	public AccidentModel getAccidentByName(String name){
		return (AccidentModel) getStpaPreliminary(name);
	}
	
	public HazardModel getHazardByName(String name){
		return (HazardModel) getStpaPreliminary(name);
	}
	
	public ConstraintModel getConstraintByName(String name){
		return (ConstraintModel) getStpaPreliminary(name);
	}
	
	private StpaPreliminaryModel getStpaPreliminary(String name){
		return stpaPreliminaries.get(name);
	}
	
	private void addStpaPreliminary(StpaPreliminaryModel prelim) throws DuplicateElementException {
		if(stpaPreliminaries.containsKey(prelim.getName()))
			throw new DuplicateElementException("STPA Preliminaries cannot share names or be redefined");
		stpaPreliminaries.put(prelim.getName(), prelim);
	}

	public ProcessModel getProcessByType(String processTypeName) {
		if (typeToComponent.get(processTypeName) instanceof ProcessModel)
			return (ProcessModel) typeToComponent.get(processTypeName);
		else
			return null;
	}

	public DeviceModel getDeviceByType(String deviceTypeName) {
		if (typeToComponent.get(deviceTypeName) instanceof DeviceModel)
			return (DeviceModel) typeToComponent.get(deviceTypeName);
		else
			return null;
	}

	public ConnectionModel getChannelByName(String connectionName) {
		return channels.get(connectionName);
	}
	
	public void addProcess(String instanceName, ProcessModel pm)
			throws DuplicateElementException {
		if (logicComponents.containsKey(instanceName))
			throw new DuplicateElementException(instanceName
					+ " already exists");
		logicComponents.put(instanceName, pm);
		typeToComponent.put(pm.getName(), pm);
	}

	public void addDevice(String deviceName, DeviceModel dm)
			throws DuplicateElementException {
		if (devices.containsKey(deviceName))
			throw new DuplicateElementException(deviceName + " already exists");
		devices.put(deviceName, dm);
		typeToComponent.put(dm.getName(), dm);
	}

	public void addConnection(String name, ConnectionModel cm) {
		channels.put(name, cm);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public HashMap<String, ProcessModel> getLogicComponents() {
		return logicComponents;
	}

	public HashMap<String, ComponentModel> getLogicAndDevices() {
		HashMap<String, ComponentModel> ret = new HashMap<>();
		HashSet<String> logicComponentNames = new HashSet<>(
				logicComponents.keySet());
		if (logicComponentNames.retainAll(devices.keySet())) {
			ret.putAll(devices);
			ret.putAll(logicComponents);
		} else {
			// TODO: Handle this more gracefully?
			System.err
					.println("Device and Logic components can't have the same name");
		}
		return ret;
	}
	
	public HashMap<String, ConnectionModel> getChannels() {
		return channels;
	}

	public boolean hasProcessType(String typeName) {
		return (typeToComponent.containsKey(typeName) && (typeToComponent
				.get(typeName) instanceof ProcessModel));
	}

	public boolean hasDeviceType(String typeName) {
		return (typeToComponent.containsKey(typeName) && (typeToComponent
				.get(typeName) instanceof DeviceModel));
	}
}
