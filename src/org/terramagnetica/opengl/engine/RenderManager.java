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

package org.terramagnetica.opengl.engine;

import java.util.ArrayList;
import java.util.HashMap;

import org.terramagnetica.opengl.miscellaneous.Animation;

public class RenderManager implements Animation {
	
	public class RenderEntry {
		private String ID;
		private Renderable render;
		
		private boolean isDefault = false;
		
		private RenderEntry(String ID, Renderable object) {
			this.ID = ID;
			this.render = object;
		}
	}
	
	private String defaultID = null;
	private HashMap<String, RenderEntry> renderMap = new HashMap<String, RenderEntry>();
	
	private RenderEntry currentEntry = null;
	private ArrayList<RenderEntry> effects = new ArrayList<RenderEntry>();
	
	//Animations
	private boolean running = true;
	
	public RenderManager() {
		
	}
	
	private RenderEntry getEntryChecked(String id) {
		RenderEntry entry = this.renderMap.get(id);
		if (entry == null) throw new IllegalArgumentException("No RenderObject matches id : " + id);
		return entry;
	}
	
	/** D�finit le rendu d'identifiant <tt>id</tt>.
	 * <p>Si un rendu poss�dait d�j� cet identifiant, il est remplac�. Si ce rendu
	 * �tait le rendu actif, alors le nouveau rendu restera le rendu actif. Si ce
	 * rendu �tait un effet actif, alors il sera supprim�. */
	public void putRender(String id, Renderable object) {
		if (id == null) throw new NullPointerException("id should be != null");
		if (object == null) throw new NullPointerException("object should be != null");
		
		RenderEntry oldEntry = this.renderMap.get(id);
		RenderEntry newEntry = new RenderEntry(id, object);
		
		//Remplacement
		if (oldEntry != null) {
			removeEffect(id);
			newEntry.isDefault = oldEntry.isDefault;
		}
		
		this.renderMap.put(id, newEntry);
		
		if (this.renderMap.size() == 1) {
			newEntry.isDefault = true;
			this.defaultID = id;
		}
		
		//La nouvelle entr�e devient le rendu actif si :
		//- oldEntry == this.currentEntry == null : pas de rendu actif pr�c�dent
		//- oldEntry == this.currentEntry != null : le rendu actif vient d'�tre remplac�.
		if (this.currentEntry == oldEntry) {
			render(id);
		}
	}
	
	public Renderable getRender(String id) {
		return getEntryChecked(id).render;
	}
	
	/** D�finit le rendu par d�faut. C'est le rendu qui sera activ� lors d'un
	 * appel � {@link #renderDefault()}. Par d�faut le premier rendu ajout� devient
	 * le rendu par d�faut. */
	public void setRenderDefault(String id) {
		RenderEntry newDefault = getEntryChecked(id);
		
		if (this.defaultID != null) {
			RenderEntry oldDefault = this.renderMap.get(this.defaultID);
			oldDefault.isDefault = false;
		}
		
		this.defaultID = id;
		newDefault.isDefault = true;
	}
	
	public String getDefaultRenderID() {
		return this.defaultID;
	}
	
	/** Active le rendu indiqu� en param�tre comme rendu principal. Les effets
	 * ne sont pas affect�s. */
	public void render(String id) {
		RenderEntry rendered = getEntryChecked(id);
		
		if (rendered != this.currentEntry) {
			//D�sactivation du rendu pr�c�dent
			if (this.currentEntry != null && !hasEffect(this.currentEntry.ID)) {
				unrender(this.currentEntry);
			}
			
			//Activation du nouveau rendu.
			this.currentEntry = rendered;
			render(this.currentEntry);
		}
	}
	
	/** @see #render(String) */
	public void renderDefault() {
		render(this.defaultID);
	}
	
	/** Active le rendu pass� en param�tres. */
	private void render(RenderEntry entry) {
		updateAnimation(entry);
	}
	
	/** Desactive le rendu pass� en param�tres. */
	private void unrender(RenderEntry entry) {
		entry.render.stop();
		entry.render.reset();
	}
	
	/** Ajoute un effet au rendu. Un effet consiste en un deuxi�me rendu
	 * en plus du rendu principal, qui est g�n�ralement secondaire et peut
	 * �tre activ� ou d�sactiv� suivant l'�tat de l'objet qu'il repr�sente. */
	public void addEffect(String id) {
		RenderEntry entry = getEntryChecked(id);
		this.effects.add(entry);
		render(entry);
	}
	
	/** D�finit l'�tat d'un effet. Un effet consiste en un deuxi�me rendu
	 * en plus du rendu principal, qui est g�n�ralement secondaire et peut
	 * �tre activ� ou d�sactiv� suivant l'�tat de l'objet qu'il repr�sente.
	 * @param state Si vaut <tt>true</tt> alors l'effet sera ajout� une seule
	 * fois au rendu, s'il ne l'est pas d�j�. Si vaut <tt>false</tt> alors 
	 * l'effet sera supprim� du rendu. */
	public void setEffect(String id, boolean state) {
		RenderEntry effect = getEntryChecked(id);
		if (state) {
			if (!this.effects.contains(effect)) {
				this.effects.add(effect);
				render(effect);
			}
		}
		else {
			boolean unrender = false;
			while (this.effects.remove(effect)) {
				unrender = true;
			}
			
			if (unrender) unrender(effect);
		}
	}
	
	public boolean removeEffect(String id) {
		RenderEntry effect = getEntryChecked(id);
		boolean result = this.effects.remove(getEntryChecked(id));
		if (result) unrender(effect);
		return result;
	}
	
	public boolean hasEffect(String id) {
		return this.effects.contains(getEntryChecked(id));
	}
	
	public boolean isEmpty() {
		return this.renderMap.isEmpty();
	}
	
	@Override
	public void start() {
		this.running = true;
		
		if (this.currentEntry != null) updateAnimation(this.currentEntry);
		for (RenderEntry effect : this.effects) {
			updateAnimation(effect);
		}
	}
	
	@Override
	public void stop() {
		this.running = false;
		
		if (this.currentEntry != null) updateAnimation(this.currentEntry);
		for (RenderEntry effect : this.effects) {
			updateAnimation(effect);
		}
	}
	
	@Override
	public void reset() {
		if (this.currentEntry != null) resetAnimation(this.currentEntry);
		for (RenderEntry effect : this.effects) {
			resetAnimation(effect);
		}
	}
	
	private void updateAnimation(RenderEntry entry) {
		if (this.running) {
			entry.render.start();
		}
		else {
			entry.render.stop();
		}
	}
	
	private void resetAnimation(RenderEntry entry) {
		entry.render.reset();
	}
	
	/** Retourne un objet de rendu pr�t � �tre dessin�. */
	public Renderable getRender() {
		if (this.currentEntry == null) return new RenderableNull();
		
		Renderable result = null;
		
		if (!this.effects.isEmpty()) {
			RenderableCompound render = new RenderableCompound();
			
			render.addRenders(this.currentEntry.render);
			for (RenderEntry entry : this.effects) {
				render.addRenders(entry.render);
			}
			
			result = render;
		}
		else {
			result = this.currentEntry.render;
		}
		return result;
	}
}
