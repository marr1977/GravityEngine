package com.gravity.model;


public class Entity {

	private double mass;
	
	private String label;
	
	private Point3D location = Point3D.ZERO;
	
	private Point3D velocity = Point3D.ZERO;
	
	private Point3D deltaVelocity = Point3D.ZERO;

	private double radius;

	private EntityType entityType;
	
	public Entity(EntityType entityType, double radius, double mass, String label) {
		this.entityType = entityType;
		this.radius = radius;
		this.mass = mass;
		this.label = label;
	}
	
	public EntityType getEntityType() {
		return entityType;
	}
	
	public double getMass() {
		return mass;
	}

	public Point3D getLocation() {
		return location;
	}
	
	public Point3D getVelocity() {
		return velocity;
	}
	
	public void setLocation(Point3D location) {
		this.location = location;
	}
	
	public void setVelocity(Point3D velocity) {
		this.velocity = velocity;
	}
	
	public Point3D getDeltaVelocity() {
		return deltaVelocity;
	}
	
	public void setDeltaVelocity(Point3D deltaVelocity) {
		this.deltaVelocity = deltaVelocity;
	}
	
	@Override
	public String toString() {
		return label + ", Location: " + location + ", Velocity: " + velocity;
	}

	public double getRadius() {
		return radius;
	}
}
