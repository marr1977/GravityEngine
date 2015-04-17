package com.gravity.model;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

/**
 * Gravity engine.
 * 
 * @author martinhallerdal
 */
public class Engine {
	
	private static final BigDecimal G = new BigDecimal("0.0000000000667384");
	private final double maxDeltaV;
	private final double minDeltaT;
	private final List<Entity> entities = new ArrayList<>();
	private double tickSeconds;
	private double elapsedTime = 0;
	private int ticks = 0;
	
	
	public Engine() {
		this(80, 0.00001, 30);
	}
	
	/**
	 * Creates the gravity engine
	 * 
	 * @param maxDeltaV		The maximum allowed delta velocity for any object.
	 * @param minDeltaT		The minimum delta time.
	 * @param tickSeconds	The preferred delta time.
	 */
	public Engine(double maxDeltaV, double minDeltaT, double tickSeconds) {
		this.maxDeltaV = maxDeltaV;
		this.minDeltaT = minDeltaT;
		this.tickSeconds = tickSeconds;
	}
	
	public double getTickSeconds() {
		return tickSeconds;
	}
	
	public void setTickSeconds(double tickSeconds) {
		this.tickSeconds = tickSeconds;
	}
	
	public void addEntity(Entity entity) {
		entities.add(entity);
	}
	
	public List<Entity> getEntities() {
		return entities;
	}

	/**
	 * Steps the world forward
	 */
	public void tick() {

		double tickSecondsRemaining = tickSeconds;
		double tickedSeconds = 0;
		int iterations = 0;
		
		// 
		// We want to tick tickSeconds. However, if the derivative of the 
		// force vector field with respect to location for an object is large,
		// then a too large delta T will result in very inaccurate result.
		//
		// Therefor, we will reduce delta T so that the maximum change in velocity
		// for all objects is below some value. As long as it is over that value,
		// delta T will be halved.
		//
		while (tickedSeconds < tickSecondsRemaining) {
			iterations++;
			double tickSecondsCandidate = tickSecondsRemaining;
			
			// Set delta V for all objects as long as max(delta V) is above a threshold
			setDeltaVelocity(tickSecondsCandidate);
			while (maxDeltaVelocity() > maxDeltaV && tickSecondsCandidate > minDeltaT) {
				tickSecondsCandidate /= 2;
				setDeltaVelocity(tickSecondsCandidate);
			}
			
			tickedSeconds += tickSecondsCandidate;
			
			// Apply the delta V and move the objects
			for (Entity entity : entities) {
				entity.setVelocity(
					entity.getVelocity().add(entity.getDeltaVelocity()));
				
				entity.setLocation(
					entity.getLocation().add(entity.getVelocity().scale(tickSeconds)));
			}
		}
		
		log(iterations);
	
		elapsedTime += tickedSeconds;
		ticks++;
	}

	private void log(int iterations) {
		
		if (ticks % 50000 == 0) {
			PeriodFormatter minutesAndSeconds = new PeriodFormatterBuilder()
		     .printZeroAlways()
		     .appendYears()
		     .appendSuffix(" years ")
		     .appendMonths()
		     .appendSuffix(" months ")
		     .appendDays()
		     .appendSuffix(" days ")
		     .appendHours()
		     .appendSuffix(" hours ")
		     .appendMinutes()
		     .appendSuffix(" minutes ")
		     .toFormatter();
			
			Period period = new Period(0, (long) (elapsedTime * 1000));
			String result = minutesAndSeconds.print(period);
			
			System.out.println("Elapsed time: " + result);
		}
	}

	private double maxDeltaVelocity() {
		return entities.stream().mapToDouble(e -> e.getDeltaVelocity().getAbsoluteValue()).max().orElse(0);
	}

	/**
	 * For all entities, set the change in velocity based upon the supplied delta time
	 */
	private void setDeltaVelocity(double tickSeconds) {
		for (Entity entity : entities) {
			Point3D deltaVelocity = Point3D.ZERO;
			
			for (Entity affector : entities) {
				if (affector == entity) {
					continue;
				}
				
				// Calculate the force between the objects
				double force = calculateForce(entity, affector);
				
				// And the acceleration 
				double acceleration = force / entity.getMass();
				
				// Create an delta velocity vector pointing towards the other object
				Point3D deltaVelocityVector = affector.getLocation()
					.subtract(entity.getLocation())
					.scaleToAbsolute(acceleration * tickSeconds);
				
				// Add it to the total delta V vector
				deltaVelocity = deltaVelocity.add(deltaVelocityVector);
			}
			
			entity.setDeltaVelocity(deltaVelocity);
		}
	}

	/**
	 * Calculates the gravitational force between two entities
	 */
	private double calculateForce(Entity entity, Entity affector) {
		
		double distance = entity.getLocation().getDistanceTo(affector.getLocation());
		
		return BigDecimal.valueOf(entity.getMass())
			.multiply(BigDecimal.valueOf(affector.getMass()))
			.divide(BigDecimal.valueOf(distance), MathContext.DECIMAL128)
			.divide(BigDecimal.valueOf(distance), MathContext.DECIMAL128)
			.multiply(G)
			.doubleValue();
	}
}
