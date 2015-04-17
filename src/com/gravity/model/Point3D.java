package com.gravity.model;

public class Point3D {

	public static Point3D ZERO = new Point3D(0, 0, 0);
	
	private double x;
	private double y;
	private double z;
	
	public Point3D(double x, double y, double z) {
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
	
	public Point3D add(Point3D point) {
		return new Point3D(x + point.getX(), y + point.getY(), z + point.getZ());
	}
	
	public Point3D subtract(Point3D point) {
		return new Point3D(x - point.getX(), y - point.getY(), z - point.getZ());
	}
	
	public Point3D scaleToAbsolute(double absoluteValue) {
		double scaleFactor = absoluteValue / getAbsoluteValue();
		return scale(scaleFactor);
	}
			
	public double getDistanceTo(Point3D to) {
		return subtract(to).getAbsoluteValue();
	}
	
	public double getAbsoluteValue() {
		return Math.sqrt(x*x + y*y + z*z);
	}

	@Override
	public String toString() {
		return "[x = " + x + ", y = " + y + ", z = " + z + "]";
	}

	public Point3D scale(double multiplier) {
		return new Point3D(x * multiplier, y * multiplier, z * multiplier);
	}
}
