package com.pracify.db.tableClasses;

import com.pracify.util.CommonHelpers;

public class FileDetails {

	private String name, desc, path, owner, creation_date, group;
	int id;

	public FileDetails() {
		// Do nothing
	}

	public FileDetails(String name, String desc, String path, String owner,
			String group) {

		this.name = name;
		this.desc = desc;
		this.path = path;
		this.owner = owner;
		this.creation_date = CommonHelpers.getCurrentTimestamp();
		this.group = group;
	}

	public FileDetails(int id, String name, String desc, String path,
			String owner, String creation_date, String group) {

		this.id = id;
		this.name = name;
		this.desc = desc;
		this.path = path;
		this.owner = owner;
		this.creation_date = creation_date;
		this.group = group;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getCreation_date() {
		return creation_date;
	}

	public void setCreation_date(String creation_date) {
		this.creation_date = creation_date;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getId() {
		return Integer.toString(id);
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}
