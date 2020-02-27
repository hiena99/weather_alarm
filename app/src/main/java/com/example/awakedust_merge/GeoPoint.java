/**
 * 
 */

/**
 * @author aquilegia
 *
 */
package com.example.awakedust_merge;

public class GeoPoint {
	public double x;
	public double y;
	public double z;
	
	/**
	 * 
	 */
	public GeoPoint() {
		super();
	}
	
	/**
	 * @param x
	 * @param y
	 */
	public GeoPoint(double x, double y) {
		super();
		this.x = x;
		this.y = y;
		this.z = 0;
	}
	
	/**
	 * @param x
	 * @param y
	 * @param y
	 */
	public GeoPoint(double x, double y, double z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}
	
	public double getZ() {
		return z;
	}
}
