package com.khrc.caresupport.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


@Entity
public class LogBook {

	@Expose
	@NotNull
	@PrimaryKey(autoGenerate = true)
	public Integer id;

	@Expose
	@NotNull
	public String tel;

	@Expose
	public Date logdate;


	public LogBook() {
	}

	@Ignore
	private transient final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
	@NotNull
	public Integer getId() {
		return id;
	}

	public void setId(@NotNull Integer id) {
		this.id = id;
	}

	@NotNull
	public String getTel() {
		return tel;
	}

	public void setTel(@NotNull String tel) {
		this.tel = tel;
	}

	public String getLogdate() {
		if (logdate == null) return "";
		return f.format(logdate);
	}

	public void setLogdate(String logdate) {
		try {
			this.logdate = f.parse(logdate);
		} catch (ParseException e) {
			System.out.println("Date Error " + e.getMessage());
		}
	}
}
