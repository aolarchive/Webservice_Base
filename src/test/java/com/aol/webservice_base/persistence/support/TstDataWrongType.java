/**
 * 
 */
package com.aol.webservice_base.persistence.support;

import java.util.Date;

/**
 * @author human
 *
 */
public class TstDataWrongType extends TstDataNotAll {
	
	protected String bool;
	
	public TstDataWrongType() {}
	public TstDataWrongType(String notBoolean, String string, int i, int ti, Date time) {
		super(string, i, ti, time);
		this.bool = notBoolean;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("bool=").append(bool).append(",");
		sb.append(super.toString());
		return sb.toString();
	}
	
	
	public String getBool() {
		return bool;
	}

}
