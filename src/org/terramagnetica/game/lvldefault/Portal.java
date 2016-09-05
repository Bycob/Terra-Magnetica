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

package org.terramagnetica.game.lvldefault;

import java.awt.Image;
import java.util.HashMap;

import org.terramagnetica.game.GameRessources;
import org.terramagnetica.game.lvldefault.rendering.RenderEntity;
import org.terramagnetica.game.lvldefault.rendering.RenderEntityDefault;
import org.terramagnetica.game.lvldefault.rendering.RenderEntityModel3D;
import org.terramagnetica.physics.Hitbox;
import org.terramagnetica.physics.HitboxCircle;
import org.terramagnetica.ressources.ImagesLoader;
import org.terramagnetica.ressources.io.BufferedObjectInputStream;
import org.terramagnetica.ressources.io.BufferedObjectOutputStream;
import org.terramagnetica.ressources.io.GameIOException;

import net.bynaryscode.util.Color4f;
import net.bynaryscode.util.maths.geometric.DimensionsInt;
import net.bynaryscode.util.maths.geometric.Vec2f;
import net.bynaryscode.util.maths.geometric.Vec2i;

public class Portal extends CaseEntity implements IGoal {
	
	private static final HashMap<DecorType, String> modelIDMap = new HashMap<DecorType, String>();
	
	static {
		
	}
	
	private static final long serialVersionUID = 1L;
	
	private boolean isLevelEnd = true;
	private int roomToGoID = -1;
	private int caseX = -1, caseY = -1;
	
	private String name = null;
	private boolean indicated = true;
	
	//VARIABLES UTILISEES POUR LE RENDU
	
	private boolean onWall = false;
	private int orientation = 0;
	private float translationX = 0f, translationY = 0f;
	/** échelle en largeur/hauteur. */
	private double scaleX = 1, scaleY = 0.8;
	
	private transient DecorType type;
	
	public Portal() {
		this.hitbox.setSolid(false);
	}
	
	@Override
	public Image getImage() {
		return ImagesLoader.get(GameRessources.ID_PORTAL);
	}

	@Override
	public DimensionsInt getDimensions() {
		return new DimensionsInt(CASE, CASE);
	}
	
	@Override
	public RenderEntity createRender() {
		//RENDU MODELE
		//Détermination du modèle
		String key = modelIDMap.get(this.type);
		if (key != null && this.skin != null) {
			if (this.onWall) {
				return new RenderEntityModel3D(key).withRotation(this.orientation);
			}
		}
		
		//RENDU NORMAL (TEXTURÉ)
		//Détermination de la texture du rendu.
		String skin = this.skin;
		
		if ("".equals(skin)) {
			skin = this.onWall ? GameRessources.ID_EXIT0 : GameRessources.ID_PORTAL;
		}
		
		//Détermination du rendu.
		if (this.onWall) {
			return new RenderEntityDefault(skin)
				.withRotation(this.orientation)
				.withTranslation(this.translationX, this.translationY)
				.withScale(this.scaleX, this.scaleY);
		}
		else {
			return new RenderEntityDefault(skin).setOnGround(true);
		}
	}
	
	@Override
	public void setSkin(String skin) {
		super.setSkin(skin);
		
		if ("".equals(skin)) {
			this.scaleX = 1;
			this.scaleY = 0.8;
		}
	}
	
	public void setScale(double scaleX, double scaleY) {
		
		if (!"".equals(this.skin)) {
			this.scaleX = scaleX;
			this.scaleY = scaleY;
		}
	}
	
	public double getScaleX() {
		return this.scaleX;
	}
	
	public double getScaleY() {
		return this.scaleY;
	}
	
	@Override
	public Hitbox createHitbox() {
		return new HitboxCircle(0.5f);
	}
	
	public void goToRoom(Room r) {
		this.roomToGoID = r.getID();
		this.isLevelEnd = false;
	}
	
	public void goToEnd() {
		this.isLevelEnd = true;
		this.roomToGoID = 0;
	}
	
	public boolean isLevelEnd() {
		return this.isLevelEnd;
	}
	
	public String getName() {
		if (this.name != null) return name;
		
		if (this.isLevelEnd) return "Sortie";
		
		return "Salle " + (this.roomToGoID + 1);
	}
	
	/**
	 * Définit le portail comme "sur un mur". L'entité sera alors rendue
	 * comme telle dans le jeu.
	 * @param wallType - l'orientation du mur. Les orientations supportées
	 * sont : <i>DROITE, HAUT, GAUCHE, BAS</i>
	 * @see WallTile
	 */
	public void setOnWall(int wallType) {
		this.onWall = true;
		
		this.translationX = 0;
		this.translationY = 0;
		final float unit = 0.45f;
		
		switch (wallType) {
		case WallTile.DROITE :
			this.orientation = 270;
			this.translationX = - unit;
			break;
		case WallTile.HAUT :
			this.orientation = 0;
			this.translationY = unit;
			break;
		case WallTile.GAUCHE :
			this.orientation = 90;
			this.translationX = unit;
			break;
		case WallTile.BAS :
			this.orientation = 180;
			this.translationY = - unit;
			break;
		default :
			this.onWall = false;
		}
	}
	
	public void setOnGround() {
		this.onWall = false;
	}
	
	public void setWherePlayerSent(int caseX, int caseY) {		
		this.caseX = Math.max(-1, caseX);
		this.caseY = Math.max(-1, caseY);
	}
	
	public Vec2i getPlayerSpecificLocation() {
		return new Vec2i(this.caseX, this.caseY);
	}
	
	public boolean hasPlayerSpecificLocation() {
		return this.caseX != -1 && this.caseY != -1;
	}
	
	public void setIndicated(boolean indicated) {
		this.indicated = indicated;
	}
	
	public boolean isIndicated() {
		return this.indicated;
	}
	
	@Override
	public Color4f getIndicationColor() {
		if (!this.indicated) return new Color4f(0, 0, 0, 0);
		if (this.isLevelEnd) return new Color4f(64, 64, 64);
		return new Color4f(192, 192, 192);
	}
	
	@Override
	public void updateLogic(long dT, GamePlayingDefault game) {
		
		//Mise à jour du nom
		if (!this.isLevelEnd) {
			this.name = game.getRoomName(this.roomToGoID);
		}
		
		//Mise à jour du rendu
		if (this.type == null) {
			this.type = game.getDecorType();
			this.recreateRender();
			
			if (this.getRender() instanceof RenderEntityModel3D) {
				Vec2i c = this.getCoordonnéesCase();
				game.getLandscapeAt(c.x, c.y).setUnrendered(true);
			}
		}
		
		//Vérification d'un changement de salle ou d'une fin de niveau.
		//TODO adapter la hitbox à la texture
		if (isPlayerInPortal(game.getPlayer())) {
			if (this.isLevelEnd) {
				game.setEnd();
			}
			else {
				game.goToRoom(this.roomToGoID);
				
				//uniquement dans le cas où le portail envoie à un autre endroit
				//que le point de spawn habituel de la salle.
				if (this.caseX != -1 && this.caseY != -1) {
					game.addEventOnChangingRoom(new PortalPlacingEvent(this.caseX, this.caseY));
				}
			}
		}
	}
	
	private boolean isPlayerInPortal(PlayerDefault player) {
		if (this.onWall) {
			Vec2f pc = player.getCoordonnéesf();
			Vec2f centre = this.getCoordonnéesf();
			
			boolean changeAxis = (this.orientation %= 180) == 90;
			double hDepth = changeAxis ? Math.abs(pc.x - centre.x) : Math.abs(pc.y - centre.y);
			double hWidth = !changeAxis ? Math.abs(pc.x - centre.x) : Math.abs(pc.y - centre.y);
			
			return hDepth < 0.65 && hWidth < this.scaleX / 2;
		}
		else {
			return getDistancef(player) < 0.25;
		}
	}
	
	@Override
	public void code(BufferedObjectOutputStream out) throws GameIOException {
		super.code(out);
		out.writeBoolField(this.isLevelEnd, 200);
		out.writeIntField(this.roomToGoID, 201);
		
		out.writeBoolField(this.onWall, 202);
		out.writeIntField(this.orientation, 203);
		out.writeFloatField(this.translationX, 204);
		out.writeFloatField(this.translationY, 205);
		
		out.writeIntField(this.caseX, 206);
		out.writeIntField(this.caseY, 207);
		
		out.writeDoubleField(this.scaleX, 208);
		out.writeDoubleField(this.scaleY, 209);
		
		out.writeBoolField(this.indicated, 210);
	}
	
	@Override
	public Portal decode(BufferedObjectInputStream in) throws GameIOException {
		super.decode(in);
		this.isLevelEnd = in.readBoolFieldWithDefaultValue(200, this.isLevelEnd);
		this.roomToGoID = in.readIntFieldWithDefaultValue(201, this.roomToGoID);
		
		this.onWall = in.readBoolFieldWithDefaultValue(202, this.onWall);
		this.orientation = in.readIntFieldWithDefaultValue(203, this.orientation);
		this.translationX = in.readFloatFieldWithDefaultValue(204, this.translationX);
		this.translationY = in.readFloatFieldWithDefaultValue(205, this.translationY);
		
		this.caseX = in.readIntFieldWithDefaultValue(206, this.caseX);
		this.caseY = in.readIntFieldWithDefaultValue(207, this.caseY);
		
		this.scaleX = in.readDoubleFieldWithDefaultValue(208, this.scaleX);
		this.scaleY = in.readDoubleFieldWithDefaultValue(209, this.scaleY);
		
		this.indicated = in.readBoolFieldWithDefaultValue(210, true);
		return this;
	}
}
