package com.khrc.caresupport.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;

import org.jetbrains.annotations.NotNull;


@Entity
public class CodeBook {

	@Expose
	@NotNull
	@PrimaryKey
	public String id;

	@Expose
	public String codeFeature;

	@Expose
	public Integer codeValue;

	@Expose
	public String codeLabel;

	public CodeBook() {
	}

	@NotNull
	public String getId() {
		return id;
	}

	public void setId(@NotNull String id) {
		this.id = id;
	}

	public String getCodeFeature() {
		return codeFeature;
	}

	public void setCodeFeature(String codeFeature) {
		this.codeFeature = codeFeature;
	}

	public Integer getCodeValue() {
		return codeValue;
	}

	public void setCodeValue(Integer codeValue) {
		this.codeValue = codeValue;
	}

	public String getCodeLabel() {
		return codeLabel;
	}

	public void setCodeLabel(String codeLabel) {
		this.codeLabel = codeLabel;
	}
}
