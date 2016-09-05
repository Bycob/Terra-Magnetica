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

import java.awt.Image;
import java.util.ArrayList;

import org.terramagnetica.game.GameInputBuffer.InputKey;
import org.terramagnetica.game.GameRessources;
import org.terramagnetica.game.lvldefault.Bonus;
import org.terramagnetica.game.lvldefault.CaseEntity;
import org.terramagnetica.game.lvldefault.Entity;
import org.terramagnetica.game.lvldefault.GamePlayingDefault;
import org.terramagnetica.game.lvldefault.PlayerDefault;
import org.terramagnetica.game.lvldefault.rendering.RenderEntity;
import org.terramagnetica.game.lvldefault.rendering.RenderEntityDefault;
import org.terramagnetica.game.lvldefault.rendering.RenderEntityDefaultAnimation;
import org.terramagnetica.opengl.miscellaneous.AnimationManager;
import org.terramagnetica.ressources.ImagesLoader;
import org.terramagnetica.ressources.TexturesLoader;
import org.terramagnetica.ressources.io.BufferedObjectInputStream;
import org.terramagnetica.ressources.io.BufferedObjectOutputStream;
import org.terramagnetica.ressources.io.GameIOException;

import net.bynaryscode.util.maths.geometric.DimensionsInt;
import net.bynaryscode.util.maths.geometric.Vec2i;

public class Trap extends CaseEntity {
	
	private static final long DISABLE_DURATION_MILLIS = 10000;
	
	private static final long serialVersionUID = 1L;
	
	private long disabledTime = -1;
	private boolean active = false;
	
	public Trap() {
		this(0, 0);
	}
	
	public Trap(int x, int y) {
		super(x, y);
		this.hitbox.setSolid(false);
	}
	
	public Trap(Vec2i centre) {
		this(centre.x, centre.y);
	}
	
	@Override
	public Image getImage() {
		return ImagesLoader.get(GameRessources.PATH_ANIM004_TRAP_OFF + GameRessources.TEX_TRAP_OFF_IMAGE);
	}
	
	@Override
	public DimensionsInt getImgDimensions() {
		return new DimensionsInt(CASE, CASE);
	}
	
	@Override
	protected RenderEntity createRender() {
		if (!this.active) {
			AnimationManager animation = new AnimationManager(
					TexturesLoader.getAnimatedTexture(GameRessources.PATH_ANIM004_TRAP_OFF));
			
			RenderEntityDefaultAnimation render = new RenderEntityDefaultAnimation(animation);
			render.setOnGround(true);
	
			animation.start();
			return render;
		}
		else {
			RenderEntityDefault render = new RenderEntityDefault(GameRessources.PATH_ANIM004_TRAP_OFF + GameRessources.TEX_TRAP_ACTIVE);
			render.setOnGround(true);
			return render;
		}
	}
	
	@Override
	public DimensionsInt getDimensions() {
		return new DimensionsInt(CASE, CASE);
	}
	
	/** Indique si le pi�ge fonctionne */
	public boolean isWorking() {
		return this.disabledTime == -1;
	}
	
	protected void setActive(boolean active) {
		this.active = active;
		this.recreateRender();
	}
	
	/** D�sactive le pi�ge pour quelques secondes. Le pi�ge ainsi d�sactiv� ne
	 * peut plus plus capturer la cr�ature. Le pi�ge se r�active tout seul apr�s
	 * un petit moment. Ramasser le pi�ge le r�active instantan�ment.
	 * <blockquote><i>"Damn... She got us !"</i></blockquote> */
	public void disable(GamePlayingDefault game) {
		this.disabledTime = game.getTime();
		setActive(false);
	}
	
	@Override
	public void updateLogic(long dT, GamePlayingDefault game) {

		PlayerDefault player = game.getPlayer();
		
		//Si le joueur rammasse le pi�ge
		if (game.getInput().isKeyPressed(InputKey.KEY_TALK)) {
			ArrayList<Bonus> playerBonus = player.getBonusList();
			
			//Recherche de l'objet qui inventorie les pi�ges sur le joueur, si le joueur n'en a pas on lui en donne un.
			BonusTrap trapBonus = null;
			
			for (Bonus b : playerBonus) {
				if (b instanceof BonusTrap) {
					trapBonus = (BonusTrap) b;
				}
			}
			
			if (trapBonus == null) {
				trapBonus = new BonusTrap();
				player.addBonus(trapBonus);
			}
			
			long systemTime = System.currentTimeMillis();
			if (systemTime - trapBonus.lastInput > BonusTrap.INPUT_TIME) {
				
				//Transfert du pi�ge dans l'inventaire du joueur.
				if (player.getDistancef(this) < 0.45 && trapBonus.addTrap()) {
					trapBonus.lastInput = systemTime;
					game.removeEntity(this);
				}
			}
		}
		
		//Mise � jour de la r�paration du pi�ge
		if (this.disabledTime != -1 && this.disabledTime < game.getTime() - DISABLE_DURATION_MILLIS) {
			this.disabledTime = -1;
		}
		
		//Si la cr�ature se prend dans le pi�ge
		if (this.isWorking()) {
			Vec2i cCase = this.getCoordonn�esCase();
			Entity[] onMyTerritory = game.getEntitiesOnCase(cCase.x, cCase.y);
			
			for (Entity kwazzzaaaa : onMyTerritory) {
				if (kwazzzaaaa instanceof TheCreature) {
					
					TheCreature prey = (TheCreature) kwazzzaaaa;
					//CAPTURATION $$$$$$$$ (prey veut dire proie en anglais - note de vocabulaire)
					if (prey.getCreatureAI() instanceof FreeCreatureAI) {
						((FreeCreatureAI) prey.getCreatureAI()).trap(game, this);
						setActive(true);
					}
				}
			}
		}
	}
	
	@Override
	public void code(BufferedObjectOutputStream out) throws GameIOException {
		super.code(out);
		
		out.writeLongField(this.disabledTime, 201);
	}
	
	@Override
	public Trap decode(BufferedObjectInputStream in) throws GameIOException {
		super.decode(in);
		
		this.disabledTime = in.readLongFieldWithDefaultValue(201, this.disabledTime);
		
		return this;
	}
}
