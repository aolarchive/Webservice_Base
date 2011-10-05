/**
 * 
 */
package com.aol.webservice_base.persistence.support;

import java.util.Date;

/**
 * @author human
 *
 */
public class TstDataNotAll {
	public String string;
	public int i;
	public int ti;
	public Date time;

	public TstDataNotAll() {}	
	public TstDataNotAll(String string, int i, int ti, Date time) {
		this.string = string;
		this.i = i;
		this.ti = ti;
		this.time = new Date(time.getTime());
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("string=").append(string).append(",");
		sb.append("i=").append(i).append(",");
		sb.append("ti=").append(ti).append(",");
		sb.append("time=").append(time);
		return sb.toString();
	}
	
	public String getString() {
		return string;
	}
	public void setString(String string) {
		this.string = string;
	}
	public int getI() {
		return i;
	}
	public void setI(int i) {
		this.i = i;
	}
	public int getTi() {
		return ti;
	}
	public void setTi(int ti) {
		this.ti = ti;
	}
	public Date getTime() {
		return new Date(time.getTime());
	}
	public void setTime(Date time) {
		this.time = new Date(time.getTime());
	}

}
