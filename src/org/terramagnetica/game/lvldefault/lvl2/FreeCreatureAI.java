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

package org.terramagnetica.game.lvldefault.lvl2;

import org.terramagnetica.game.lvldefault.Aimant;
import org.terramagnetica.game.lvldefault.CaseEntity;
import org.terramagnetica.game.lvldefault.GamePlayingDefault;
import org.terramagnetica.game.lvldefault.PlayerDefault;
import org.terramagnetica.game.lvldefault.IA.ActionChoosePath;
import org.terramagnetica.game.lvldefault.IA.ActionFollowPath;
import org.terramagnetica.game.lvldefault.IA.EntityClassSelector;
import org.terramagnetica.game.lvldefault.IA.PathComparatorEntities;
import org.terramagnetica.game.lvldefault.IA.PathSeeker;
import org.terramagnetica.game.lvldefault.IA.Rule;
import org.terramagnetica.game.lvldefault.IA.SensorPathScanner;
import org.terramagnetica.game.lvldefault.lvl2.Level2.CreatureAI;
import org.terramagnetica.ressources.io.BufferedObjectInputStream;
import org.terramagnetica.ressources.io.BufferedObjectOutputStream;
import org.terramagnetica.ressources.io.Codable;
import org.terramagnetica.ressources.io.GameIOException;

import net.bynaryscode.util.Predicate;
import net.bynaryscode.util.maths.geometric.Vec2f;
import net.bynaryscode.util.maths.geometric.Vec2i;
import net.bynaryscode.util.maths.geometric.RectangleInt;

/** L'AI de la cr�ature lorsqu'elle n'est pas encore captur�e. */
public class FreeCreatureAI extends CreatureAI implements Codable {
	
	private static final float DEFAULT_VELOCITY = PlayerDefault.DEFAULT_VELOCITY;
	
	private boolean enabled = true;
	private RectangleInt bounds = null;
	
	private long captureTime = -1;
	/** Le temps que met la cr�ature � se lib�rer */
	private long liberatingDuration = 30000;
	
	/** Seule la case du pi�ge est sauvegard�e, elle sert � retrouver le pi�ge
	 * une fois la cr�ature d�serialis�e. */
	private Vec2i trapCase = null;
	private Trap trapWhichTrappedMeT_T = null;
	
	public FreeCreatureAI(TheCreature entity) {
		super(entity);
		
		//PARAMETRES
		EntityClassSelector selector = new EntityClassSelector();
		selector.setWeight(Trap.class, 1);
		selector.setWeight(PlayerDefault.class, 2);
		selector.setWeight(Aimant.class, 3);
		
		final PathComparatorEntities pathComparator = new PathComparatorEntities(this);
		pathComparator.setEntityComparator(selector);
		
		//SENSORS
		this.addSensor(new SensorPathScanner());

		//CONDITIONS
		Predicate<FreeCreatureAI> IF_Enabled = new Predicate<FreeCreatureAI>() {
			@Override
			public boolean test(FreeCreatureAI t) {
				return enabled;
			}
		};
		
		Predicate<FreeCreatureAI> IF_hasPath_AND_noDanger = new Predicate<FreeCreatureAI>() {
			@Override
			public boolean test(FreeCreatureAI t) {
				if (getPath() != null && getPath().hasNext()) {
					SensorPathScanner scanner = t.getSensor(SensorPathScanner.class);
					return pathComparator.getMaxEntity(scanner.checkPath(getPath())) == null;
				}
				
				return false;
			}
		};
		
		//ACTIONS
		ActionFollowPath DO_followPath = new ActionFollowPath(DEFAULT_VELOCITY);
		ActionChoosePath DO_choosePath = new ActionChoosePath(pathComparator);
		
		//REGLES
		Rule<FreeCreatureAI> mainRule = new Rule<FreeCreatureAI>();
		
			Rule<FreeCreatureAI> rule1 = new Rule<FreeCreatureAI>();
			rule1.addOption(IF_hasPath_AND_noDanger, DO_followPath);
			rule1.addDefaultOption(DO_choosePath, DO_followPath);
		
		mainRule.addOption(IF_Enabled, rule1);
		
		this.addBaseRules(mainRule);
	}
	
	@Override
	protected PathSeeker createPathSeeker(GamePlayingDefault game) {
		PathSeeker seeker = super.createPathSeeker(game);
		
		seeker.setAcceptDeadEnd(false);
		if (this.bounds != null) seeker.getSpaceGraph().setBounds(this.bounds);
		
		return seeker;
	}
	
	public void setBounds(RectangleInt bounds) {
		if (getPathSeeker() != null) getPathSeeker().getSpaceGraph().setBounds(bounds);
		this.bounds = bounds;
	}
	
	@Override
	public void update() {
		super.update();
		//MISE A JOUR DES INTERACTIONS
		
		GamePlayingDefault game = this.getGame();
		float playerDistance = (float) game.getPlayer().getDistancef(this.getCreature());
		
		//Si la cr�ature vient juste d'�tre lue, alors il faut obtenir le pi�ge sur lequel elle est bloqu�e.
		if (isTrapped() && this.trapWhichTrappedMeT_T == null && this.trapCase != null) {
			CaseEntity potentialTrap = game.getCaseEntityAt(this.trapCase.x, this.trapCase.y);
			
			if (potentialTrap != null && potentialTrap instanceof Trap) {
				this.trapWhichTrappedMeT_T = (Trap) potentialTrap;
			}
		}
		
		//Si la cr�ature est captur�e elle est immobilis�e.
		if (isTrapped()) {
			if (this.trapWhichTrappedMeT_T != null) {
				//La cr�ature se centre sur le pi�ge.
				TheCreature creature = this.getCreature();
				Vec2f cpos = creature.getCoordonn�esf();
				Vec2f tpos = this.trapWhichTrappedMeT_T.getCoordonn�esf();
				
				creature.setMovement(tpos.x - cpos.x, tpos.y - cpos.y);
			}
			else {
				getCreature().setMovement(0, 0);
			}
		}
		
		//Lib�ration de la cr�ature si le temps est �coul�.
		if (this.captureTime != -1 && this.captureTime < game.getTime() - this.liberatingDuration) {
			this.captureTime = -1;
			this.trapWhichTrappedMeT_T.disable(game);
			
			this.trapWhichTrappedMeT_T = null;
		}
		
		//Si le temps n'est pas �coul� et que le joueur vient nous sauver, alors passage phase 2
		if (this.isTrapped() && playerDistance <= 0.9f) {
			this.trapWhichTrappedMeT_T.disable(game);
			game.interruptGame(new InterruptionLiberation(game, getCreature()));
		}
	}
	
	public TheCreature getCreature() {
		return this.getEntity();
	}

	/** Capture la cr�ature avec le pi�ge pass� en param�tres. Celle-ci se lib�re
	 * ensuite seule, apr�s un certain temps. */
	public void trap(GamePlayingDefault game, Trap trap) {
		this.captureTime = game.getTime();
		this.trapWhichTrappedMeT_T = trap;
	}
	
	public boolean isTrapped() {
		return this.captureTime != -1;
	}
	

	@Override
	public void code(BufferedObjectOutputStream out) throws GameIOException {
		
		out.writeLongField(this.captureTime, 0);
		out.writeLongField(this.liberatingDuration, 1);
		
		if (this.trapWhichTrappedMeT_T != null) { //[2-3]
			Vec2i trapCase = this.trapWhichTrappedMeT_T.getCoordonn�esCase();
			out.writeIntField(trapCase.x, 2); out.writeIntField(trapCase.y, 3);
		}
	}

	@Override
	public Codable decode(BufferedObjectInputStream in) throws GameIOException {
		this.captureTime = in.readLongFieldWithDefaultValue(0, this.captureTime);
		this.liberatingDuration = in.readLongFieldWithDefaultValue(1, this.liberatingDuration);
		
		int x = in.readIntFieldWithDefaultValue(202, -1);
		int y = in.readIntFieldWithDefaultValue(203, -1);
		if (x != -1 || y != -1) {
			this.trapCase = new Vec2i(x, y);
		}
		return this;
	}
	
	@Override
	public FreeCreatureAI clone() {
		FreeCreatureAI clone = (FreeCreatureAI) super.clone();
		
		clone.bounds = this.bounds == null ? null : this.bounds.clone();
		clone.trapCase = this.trapWhichTrappedMeT_T == null ? (this.trapCase == null ? null : this.trapCase.clone()) : this.trapWhichTrappedMeT_T.getCoordonn�esCase();
		
		return clone;
	}
}
