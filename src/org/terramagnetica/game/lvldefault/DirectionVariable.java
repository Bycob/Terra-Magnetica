/* <LICENSE>
Copyright (C) 2013-2016 Louis JEAN

This file is part of Terra Magnetica.

Terra Magnetica is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Terra Magnetica is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with Terra Magnetica. If not, see <http://www.gnu.org/licenses/>.
 </LICENSE> */

package org.terramagnetica.game.lvldefault;

import java.util.Random;

import net.bynaryscode.util.maths.MathUtil;

/**
 * Objet calculant automatiquement une direction changeante de
 * façon aléatoire, un peu à la manière d'une aiguille de boussole
 * lorsqu'on marche autour du pôle Nord magnétique.
 * @author Louis JEAN
 */
public class DirectionVariable {
	
	private static Random seedGenerator = new Random(System.currentTimeMillis());
	
	
	/** La direction prise par cet objet, en radians. */
	private float direction;
	/** Le nombre aléatoire qui détermine les variations de la direction. */
	private Random rand = new Random(seedGenerator.nextLong());
	
	/** La vitesse de variation de la direction, en radians par seconde. */
	private float speed = 0f;
	/** Le sens de variation de la vitesse de variation de la direction.
	 * Autrement dit, ce qui détermine si la vitesse va avoir tendance à
	 * tendre vers un nombre négatif (this.sens = -1) ou un nombre positif
	 * (this.sense = 1). */
	private int sense = this.rand.nextBoolean() ? 1 : -1;
	/** Le temps écoulé depuis le dernier changement de sens.
	 * @see #sense */
	private long totalTimeElapsed = 0;
	/** Le temps qui s'écoulera, depuis le dernier changement de sens,
	 * avant le changement suivant.
	 * @see #sense */
	private long timeToChange = 0;
	
	/** Le temps minimum entre deux changements de sens
	 * @see #sense */
	private static final long MINIMUM_TIME = 100;
	private static final long MAXIMUM_TIME = 2000;
	
	private static final float ACCELERATION = (float) Math.PI * 7f / 15f;
	private static final float MAX_SPEED = (float) Math.PI * 2f / 3f;
	
	public DirectionVariable() {
		this(0f);
	}
	
	public DirectionVariable(float start) {
		this.direction = start;
	}
	
	/**
	 * Met à jour la direction variable en fonction du temps qui est passé :
	 * La direction change au cours du temps à la manière d'une aiguille de
	 * boussole qui s'affole (par exemple quand on marche au pôle Nord...)
	 * @param timeElapsed - Le temps qui vient de passer, en ms.
	 */
	public void update(long timeElapsed) {
		updateSpeed(timeElapsed);
		this.direction = (float) MathUtil.addAngle(this.direction, this.speed * timeElapsed / 1000f);
	}
	
	private void updateSpeed(long timeElapsed) {
		this.totalTimeElapsed += timeElapsed;
		if (this.totalTimeElapsed >= this.timeToChange) {
			this.totalTimeElapsed -= this.timeToChange;
			this.timeToChange = this.rand.nextInt((int) (MAXIMUM_TIME - MINIMUM_TIME)) + MINIMUM_TIME;
			this.sense = - this.sense;
		}
		
		this.speed += this.sense * ACCELERATION * timeElapsed / 1000f;
		if (Math.abs(this.speed) > MAX_SPEED) {
			this.speed = this.sense * MAX_SPEED;
		}
	}
	
	public float getDirection() {
		return this.direction;
	}
}
