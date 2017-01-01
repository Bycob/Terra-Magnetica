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

import java.util.ArrayList;
import java.util.List;

import org.terramagnetica.ressources.io.BufferedObjectInputStream;
import org.terramagnetica.ressources.io.BufferedObjectOutputStream;
import org.terramagnetica.ressources.io.GameIOException;

import net.bynaryscode.util.maths.geometric.Vec2d;
import net.bynaryscode.util.maths.geometric.Vec2i;
import net.bynaryscode.util.physic.Trajectoire2D;
import net.bynaryscode.util.physic.TrajectoireDroite2DLinear;
import net.bynaryscode.util.physic.TrajectoireDroite2DSmooth;

public class EventScrolling extends GameEvent {
	
	//à chaque modification, implémenter dans la classe TriggererPanelScrolling, qui gère l'interface graphique.
	public enum ScrollingType {
		/** Associé à une {@link TrajectoireDroite2DSmooth} */
		LISSE,
		/** Déplacement de la caméra selon un mouvement rectiligne
		 * uniforme. */
		RECTILIGNE
	}
	
	private ArrayList<Vec2i> visitedCases = new ArrayList<Vec2i>();
	private ScrollingType type = ScrollingType.LISSE;
	
	//TEMPORAIRE -> à remplacer par une durée spécifique à chaque scrolling
	/** La durée du scrolling */
	private long duration = 1500;
	
	
	/** @deprecated Constructeur utilisé pour la désérialisation.
	 * Utiliser plutôt : {@link #EventScrolling(List)} */
	public EventScrolling() {
		this(new ArrayList<Vec2i>());
	}
	
	public EventScrolling(List<Vec2i> visitedCases) {
		setVisitedCases(visitedCases);
	}
	
	public void setVisitedCases(List<Vec2i> visitedCases) {
		this.visitedCases = new ArrayList<Vec2i>();
		for (Vec2i c : visitedCases) {
			if (c != null) this.visitedCases.add(c);
		}
	}
	
	public void addVisitedCase(Vec2i c) {
		if (c == null) throw new NullPointerException();
		this.visitedCases.add(c);
	}
	
	public void setType(ScrollingType type) {
		this.type = type;
	}
	
	public void setDuration(long duration) {
		this.duration = duration;
	}
	
	@Override
	public void code(BufferedObjectOutputStream out) throws GameIOException {
		super.code(out);
		
		out.writeIntField(this.type.ordinal(), 100);
		
		Integer[] xValues = new Integer[this.visitedCases.size()];
		Integer[] yValues = new Integer[this.visitedCases.size()];
		for (int i = 0 ; i < this.visitedCases.size() ; i++) {
			xValues[i] = this.visitedCases.get(i).x;
			yValues[i] = this.visitedCases.get(i).y;
		}
		out.writeArrayField(xValues, 101);
		out.writeArrayField(yValues, 102);
		
		out.writeLongField(this.duration, 103);
	}

	@Override
	public EventScrolling decode(BufferedObjectInputStream in) throws GameIOException {
		super.decode(in);
		
		this.type = ScrollingType.values()[in.readIntField(100)];
		
		this.visitedCases = new ArrayList<Vec2i>();
		try {
			ArrayList<Integer> xValues = new ArrayList<Integer>(); in.readListField(xValues, 101);
			ArrayList<Integer> yValues = new ArrayList<Integer>(); in.readListField(yValues, 102);
			
			for (int i = 0 ; i < Math.min(xValues.size(), yValues.size()) ; i++) {
				this.visitedCases.add(new Vec2i(xValues.get(i), yValues.get(i)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.duration = in.readLongFieldWithDefaultValue(103, this.duration);
		
		return this;
	}

	@Override
	public void trigger(GamePlayingDefault game) {
		if (!this.isEventRepetable() && this.called) return;
		
		game.interruptGame(new InterruptionScrolling(game));
		
		this.called = true;
	}
	
	@Override
	public EventScrolling clone() {
		EventScrolling clone = (EventScrolling) super.clone();
		
		clone.visitedCases = new ArrayList<Vec2i>();
		for (Vec2i c : this.visitedCases) {
			clone.visitedCases.add(c.clone());
		}
		
		return clone;
	}
	
	public class InterruptionScrolling extends InterruptionPhasesLvlDefault {
		
		private GamePlayingDefault game;
		
		public InterruptionScrolling(GamePlayingDefault game) {
			this.game = game;
			
			addPhase(new PhasePause(500));
			
			for (Vec2i visitedCase : visitedCases) {
				
				Vec2d destination = visitedCase.asDouble();
				destination.x += Entity.DEMI_CASE_F; destination.y += Entity.DEMI_CASE_F;
				
				Trajectoire2D t = null;
				
				switch (type) {
				case LISSE :
					t = new TrajectoireDroite2DSmooth(new Vec2d(), destination);
				case RECTILIGNE :
					t = new TrajectoireDroite2DLinear(new Vec2d(), destination);
				}
				
				addPhase(new PhaseScrolling(t, game).withDuration(duration));
				addPhase(new PhasePause(500));
			}
			
			addPhase(scrollingToPlayer(game));
		}
		
		@Override
		public void start() {
			game.render.setScrolling(true);
		}
		
		@Override
		public void onEnd() {
			game.render.setScrolling(false);
		}
	}
}
