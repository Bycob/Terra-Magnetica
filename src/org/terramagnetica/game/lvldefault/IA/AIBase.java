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
along with BynarysCode. If not, see <http://www.gnu.org/licenses/>.
 </LICENSE> */

package org.terramagnetica.game.lvldefault.IA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.terramagnetica.game.lvldefault.GamePlayingDefault;

import net.bynaryscode.util.Util;

/** Cette classe est la classe mère de toute classe définissant une
 * intelligence artificielle spécifique. Elle contient les capteurs et
 * les règles nécessaire à la logique de l'intelligence artificielle,
 * et également toutes les variables dont ont besoin les actions pour
 * s'effectuer.
 * <p>Une IA nécessite un cadre pour s'executer, ainsi, il faudra toujours
 * définir le moteur de jeu dans lequel opère cette intelligence
 * artificielle à l'aide de la méthode {@link #setGame(GamePlayingDefault)}.
 * En revanche, le moteur de jeu peut n'être pas définit pendant le
 * paramètrage de l'IA. */
public abstract class AIBase implements Cloneable {
	
	private ArrayList<Rule> baseRules = new ArrayList<Rule>();
	private HashMap<Class<? extends Sensor>, Sensor> sensors = new HashMap<Class<? extends Sensor>, Sensor>();
	
	private GamePlayingDefault game;
	
	public AIBase(Rule... baseRules) {
		this.addBaseRules(baseRules);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends AIBase> T cast(Class<T> clazz) {
		if (!clazz.isAssignableFrom(this.getClass()))
			throw new IllegalArgumentException("L'intelligence artificielle passée en paramètres n'est pas du bon type.");
		
		return (T) this;
	}
	
	public void addBaseRules(Rule... baseRules) {
		Util.addAll(baseRules, this.baseRules, false);
	}
	
	public boolean isGameDefined() {
		return this.game != null;
	}
	
	/** Définit le moteur de jeu dans lequel va s'executer l'IA. Il est possible de
	 * paramètrer l'IA avant de définir son moteur de jeu, en revanche, celui-ci
	 * est indispensable à l'execution. */
	public void setGame(GamePlayingDefault game) {
		this.game = game;
		
		if (this.game != null) {
			for (Entry<Class<? extends Sensor>, Sensor> entry : this.sensors.entrySet()) {
				entry.getValue().setGame(this.game);
			}
		}
	}
	
	public GamePlayingDefault getGame() {
		return this.game;
	}
	
	public void addSensor(Sensor sensor) {
		if (sensor == null) throw new NullPointerException("sensor == null");
		
		if (this.game != null) sensor.setGame(this.game);
		this.sensors.put(sensor.getClass(), sensor);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Sensor> T getSensor(Class<T> clazz) {
		return (T) this.sensors.get(clazz);
	}
	
	public void update(GamePlayingDefault game) {
		if (game != getGame()) {
			setGame(game);
		}
		
		update();
	}
	
	public void update() {
		if (!isGameDefined()) throw new IllegalStateException("Cette IA n'est connectée à aucun moteur de jeu.");
		
		for (Entry<Class<? extends Sensor>, Sensor> entry : this.sensors.entrySet()) {
			entry.getValue().update(this);
		}
		
		for (Rule rule : this.baseRules) {
			Action action = rule.getAction(this);
			action.execute(this);
		}
	}
	
	@Override
	public AIBase clone() {
		AIBase clone = null;
		
		try {
			clone = (AIBase) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		clone.baseRules = new ArrayList<Rule>(this.baseRules.size());
		for (Rule rule : this.baseRules) {
			clone.baseRules.add(rule.clone());
		}
		
		clone.sensors = new HashMap<Class<? extends Sensor>, Sensor>();
		for (Entry<Class<? extends Sensor>, Sensor> e : this.sensors.entrySet()) {
			clone.sensors.put(e.getKey(), e.getValue().clone());
		}
		
		return clone;
	}
}
