/**
 * 
 */
package com.aol.webservice_base.persistence.support;

import java.util.Date;

/**
 * @author human
 *
 */
public class TstData extends TstDataNotAll {
	/**
	 * @param ignored
	 * @param string
	 * @param i
	 * @param ti
	 * @param time
	 */

	public TstData() {}	
	public TstData(boolean bool, String string, int i, int ti, Date time) {
		super(string, i, ti, time);
		this.bool = bool;
	}
	public boolean bool;
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("bool=").append(bool).append(",");
		sb.append(super.toString());
		return sb.toString();
	}
	
	public boolean getBool() {
		return bool;
	}
	public void setBool(boolean bool) {
		this.bool = bool;
	}
}
