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
import org.terramagnetica.game.lvldefault.rendering.RenderEntity;
import org.terramagnetica.game.lvldefault.rendering.RenderEntityModel3D;
import org.terramagnetica.opengl.engine.TextureQuad;
import org.terramagnetica.physics.HitboxPolygon;
import org.terramagnetica.ressources.ImagesLoader;
import org.terramagnetica.ressources.TexturesLoader;
import org.terramagnetica.ressources.io.BufferedObjectInputStream;
import org.terramagnetica.ressources.io.BufferedObjectOutputStream;
import org.terramagnetica.ressources.io.GameIOException;

import net.bynaryscode.util.maths.geometric.DimensionsInt;
import net.bynaryscode.util.maths.geometric.RectangleDouble;

public class Rock extends CaseEntity {

	private static final long serialVersionUID = 1L;
	
	private float elevation = 0f;
	
	// RENDU
	
	@Override
	public Image getImage() {
		return ImagesLoader.get(GameRessources.PATH_MAP + GameRessources.TEX_MAP_ROCK);
	}

	@Override
	public DimensionsInt getImgDimensions() {
		return new DimensionsInt(CASE, CASE);
	}
	
	@Override
	public TextureQuad getMinimapIcon() {
		return TexturesLoader.getQuad(GameRessources.PATH_MAP + GameRessources.TEX_MAP_ROCK);
	}
	
	@Override
	protected RenderEntity createRender() {
		return new RenderEntityModel3D(GameRessources.PATH_MODEL_LVL2_ROCKS)
				.withRotation(90)
				.withTranslation(0, 0, this.elevation);
	}
	
	// LOGIQUE / PHYSIQUE
	
	@Override
	public DimensionsInt getDimensions() {
		return new DimensionsInt((int) (CASE * 0.8f), (int) (CASE * 0.8f));
	}
	
	@Override
	public HitboxPolygon createHitbox() {
		return new HitboxPolygon(new RectangleDouble(-0.4, -0.3, 0.4, 0.6));
	}
	
	public void setElevation(float elevation) {
		this.elevation = elevation;
		if (this.render != null) {
			((RenderEntityModel3D) this.render).setTranslation(0, 0, this.elevation);
		}
	}
	
	@Override
	public boolean equals(Object other) {
		return super.equals(other);
	}
	
	@Override
	public Rock clone() {
		Rock clone = (Rock) super.clone();
		
		return clone;
	}
	
	@Override
	public void code(BufferedObjectOutputStream out) throws GameIOException {
		super.code(out);
	}
	
	@Override
	public Rock decode(BufferedObjectInputStream in) throws GameIOException {
		super.decode(in);
		return this;
	}
}
