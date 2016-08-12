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

import net.bynaryscode.util.Predicate;

public class Rule<T extends AIBase> implements Cloneable {
	
	public static class Option<T extends AIBase> implements Cloneable {
		
		private Action action;
		/** La condition nécessaire pour que s'effectue l'action. Si vaut <tt>null</tt>,
		 * alors l'action s'effectuera toujours. */
		private Predicate<T> condition;
		
		public Option(Predicate<T> condition, Action action) {
			this.condition = condition;
			this.action = action;
		}
		
		@Override
		@SuppressWarnings("unchecked")
		public Option<T> clone() {
			Option<T> clone = null;
			
			try {
				clone = (Option<T>) super.clone();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
			
			clone.action = this.action.clone();
			
			return clone;
		}
	}
	
	private ArrayList<Option<T>> options = new ArrayList<Option<T>>();
	
	/** Ajoute une option à la règle.
	 * @param condition La condition à valider pour que l'action s'execute.
	 * Si cette condition vaut <tt>null</tt> alors l'action s'effectuera
	 * toujours, dans le cas où toutes les conditions précédentes ne seraient
	 * pas vérifiées.
	 * @param action La ou les actions à effectuer si la condition est validée.
	 * S'il y a plusieurs actions elles s'effectuerons dans l'ordre de leur
	 * passage. S'il n'y a aucune action, alors l'action effectuée sera une
	 * {@link NullAction}. */
	public void addOption(Predicate<T> condition, Action... action) {
		if (action == null) throw new NullPointerException("action == null");
		//Avertissement : si on ajoute des options après les options par défaut, c'est mal.
		if (!this.options.isEmpty() && this.options.get(this.options.size() - 1).condition == null) {
			System.err.println("Attention : cette option ne sera jamais choisie car elle précède une option par défaut");
			Thread.dumpStack();
		}
		
		//Ajout de l'option
		if (action.length == 0) {
			this.options.add(new Option<T>(condition, new NullAction()));
		}
		else if (action.length == 1) {
			this.options.add(new Option<T>(condition, action[0]));
		}
		else {
			MultiAction multiAction = new MultiAction();
			multiAction.addActions(action);
			this.options.add(new Option<T>(condition, multiAction));
		}
	}
	
	public void addOption(Predicate<T> condition, Rule rule) {
		this.addOption(condition, new RuledAction(rule));
	}
	
	public void addDefaultOption(Action... actions) {
		this.addOption(null, actions);
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	private void checkType(AIBase ai) {
		try {
			T t = (T) ai;
		} catch (ClassCastException e) {
			throw new IllegalArgumentException("L'intelligence artificielle passée en paramètres n'est pas du bon type.");
		}
	}
	
	/** Donne l'action choisie pour la situation actuelle, en se basant sur les
	 * informations contenues dans l'objet {@link AIBase} passé en paramètres. */
	@SuppressWarnings("unchecked")
	public Action getAction(AIBase ai) {
		if (this.options.size() == 0) throw new UnsupportedOperationException("Pas d'action à effectuer.");
		checkType(ai);
		
		for (Option<T> option : this.options) {
			if (option.condition != null) {
				if (option.condition.test((T) ai)) return option.action;
			}
			else {
				return option.action;
			}
		}
		//La dernière action ajoutée est l'action à effectuer par défaut si toutes les autres actions ne conviennent pas.
		return this.options.get(this.options.size() - 1).action;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Rule<T> clone() {
		Rule<T> clone = null;
		
		try {
			clone = (Rule<T>) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		clone.options = new ArrayList<Option<T>>(this.options.size());
		for (Option<T> opt : this.options) {
			clone.options.add(opt.clone());
		}
		
		return clone;
	}
}
