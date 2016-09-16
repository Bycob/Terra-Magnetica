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

import org.terramagnetica.game.GameRessources;
import org.terramagnetica.game.lvldefault.CaseEntity;
import org.terramagnetica.game.lvldefault.rendering.RenderEntityTexture;
import org.terramagnetica.opengl.engine.AnimatedTexture;
import org.terramagnetica.opengl.engine.Renderable;
import org.terramagnetica.opengl.engine.RenderableCompound;
import org.terramagnetica.opengl.engine.RenderableFactory;
import org.terramagnetica.ressources.ImagesLoader;
import org.terramagnetica.ressources.TexturesLoader;
import org.terramagnetica.ressources.io.BufferedObjectInputStream;
import org.terramagnetica.ressources.io.BufferedObjectOutputStream;
import org.terramagnetica.ressources.io.GameIOException;

import net.bynaryscode.util.Color4f;
import net.bynaryscode.util.maths.geometric.DimensionsInt;

/**
 * Dans le niveau 2, salle 1, 3 murs de plasmat bloquent le joueur
 * pour passer dans la salle 2. Ces murs sont respectivement bleu,
 * rouge et vert. Le joueur doit aller dans divers endroits de la
 * salle pour trouver les interrupteurs à même de desactiver ces murs
 * et ainsi pouvoir continuer l'aventure.
 * @author Louis JEAN
 */
public class PlasmaticWall extends CaseEntity implements BarrierStateListener {

	private static final long serialVersionUID = 1L;
	
	private int size;
	private Color4f color;
	
	/** Indique l'activation du mur : {@code true} si le mur bloque le passage,
	 * {@code false} sinon. */
	private transient boolean state = true;
	
	public PlasmaticWall() {
		this(5, ControlPaneSystemManager.GREEN);
	}
	
	public PlasmaticWall(int size, Color4f color) {
		setSize(size);
		setColor(color);
	}
	
	@Override
	public void setState(boolean state) {
		boolean oldState = this.state;
		this.state = state;
		
		if (oldState != this.state) {
			if (this.state) {
				this.renderManager.render("nothing");
			}
			else {
				this.renderManager.render("default");
			}
			this.hitbox.setSolid(this.state);
		}
	}
	
	public void setSize(int size) {
		if (size < 1) throw new NullPointerException("Le mur fait au moins 1 de large");
		this.size = size;
		recreateHitbox();
	}
	
	public void setColor(Color4f color) {
		if (color == null) throw new NullPointerException();
		this.color = color;
	}
	
	@Override
	public Color4f getColor() {
		return this.color;
	}
	
	@Override
	public Image getImage() {
		return ImagesLoader.get(GameRessources.PATH_LVL2_TEXTURES + GameRessources.TEX_PLASMATIC_WALL);
	}
	
	@Override
	public DimensionsInt getDimensions() {
		return new DimensionsInt(this.size * CASE, 64);
	}
	
	@Override
	public void createRender() {
		AnimatedTexture texture = TexturesLoader.getAnimatedTexture(GameRessources.PATH_ANIM001_PLASMATIC_WALL);
		
		Renderable mainRender = new RenderEntityTexture(texture).withScaleOffset(1.003, 1, 1).withColor(this.color);
		//rendu des extrémités
		int leftX = - (this.size / 2 + 1);
		int rightX = leftX + 1 + this.size;
		
		Renderable rLeft = new RenderAnimationEndOfWall(true, texture).withPositionOffset(leftX, 0, 0).withColor(this.color);
		Renderable rRight = new RenderAnimationEndOfWall(false, texture).withPositionOffset(rightX, 0, 0).withColor(this.color);
		
		//Assemblage
		RenderableCompound result = RenderableFactory.createCaseArrayRender(mainRender, size, true);
		result.addRenders(rLeft);
		result.addRenders(rRight);
		
		this.renderManager.putRender("default", result);
	}
	
	@Override
	public boolean isMapVisible() {
		return false;
	}
	
	@Override
	public void code(BufferedObjectOutputStream out) throws GameIOException {
		super.code(out);
		
		out.writeIntField(this.size, 200);
		BarrierHandle.writeBarrierColor(out, this.color, 201);
	}
	
	@Override
	public PlasmaticWall decode(BufferedObjectInputStream in) throws GameIOException {
		super.decode(in);
		
		this.setSize(in.readIntField(200));
		this.color = BarrierHandle.readBarrierColor(in, 201);
		
		return this;
	}
}
