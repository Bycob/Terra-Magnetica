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

import static org.terramagnetica.game.GameRessources.*;

import java.awt.Image;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.terramagnetica.game.lvldefault.rendering.RenderLandscape;
import org.terramagnetica.game.lvldefault.rendering.RenderLandscapeNothing;
import org.terramagnetica.game.physic.Hitbox;
import org.terramagnetica.game.physic.HitboxPolygon;

import net.bynaryscode.util.Util;
import net.bynaryscode.util.maths.geometric.DimensionsInt;
import net.bynaryscode.util.maths.geometric.RectangleDouble;
import net.bynaryscode.util.maths.geometric.Vec2f;
import net.bynaryscode.util.maths.geometric.Vec2i;

public abstract class LandscapeTile implements Serializable, Cloneable {
	
	private static final long serialVersionUID = 1L;
	protected static final String pathTerrainArray[];
	private static final Map<Byte, LandscapeTile> landIDs = new HashMap<Byte, LandscapeTile>();
	
	public static LandscapeTile createLandscape(byte id, byte metadata) {
		//COMPATIBILITÉ
		if (id == 1) {//ancien décor "inaccessible" transformé en mur
			id = 2;
			metadata = -1;
		}
		
		LandscapeTile l = landIDs.get(id);
		l = l.clone();
		l.setMetadata(metadata);
		
		return l;
	}
	
	static {
		pathTerrainArray = new String[DecorType.values().length];
		
		for (DecorType decorType : DecorType.values()) {
			pathTerrainArray[decorType.ordinal()] = Util.formatDecimal(SPEC_PATH_TERRAIN, decorType.getIndex() + 1);
		}
		
		
		landIDs.put((byte) 0, new GroundTile());
		landIDs.put((byte) 2, new WallTile());
	}
	
	protected Vec2i place;
	
	protected Map<DecorType, RenderLandscape> renders = new HashMap<DecorType, RenderLandscape>();
	
	protected String skin = new String();
	private boolean norender = false;
	
	protected LandscapeTile(){
		super();
		place = new Vec2i(0,0);
	}
	
	protected LandscapeTile(int x, int y){
		place = new Vec2i(x,y);
	}
	
	public void setCoordonnéesCase(int x, int y){
		place = new Vec2i(x,y);
	}
	
	public Vec2i getCoordonnéesCase(){
		return new Vec2i(place.x, place.y);
	}
	
	/** Retourne les coordonnées du centre de la case de décor.
	 * @return */
	public Vec2f getCoordonneesf() {
		return new Vec2f(place.x + Entity.DEMI_CASE_F, place.y + Entity.DEMI_CASE_F);
	}
	
	/**
	 * @return Donne les coordonnées du point auquel dessiner cette
	 * unité de décor, dans l'éditeur de niveau.
	 */
	public Vec2i getCoordonnéesToDraw(){
		return new Vec2i(place.x * Entity.CASE, place.y * Entity.CASE);
	}
	
	public Vec2i getCoordonnéesCentre(){
		return new Vec2i(
				place.x * Entity.CASE + Entity.DEMI_CASE,
				place.y * Entity.CASE + Entity.DEMI_CASE);
	}
	
	/**
	 * @return Donne les dimensions en unité de base ({@link Entity#CASE})
	 * de l'unité de paysage. Normalement les dimensions d'une case.
	 */
	public DimensionsInt getDimensions(){
		return new DimensionsInt(Entity.CASE, Entity.CASE);
	}
	
	public abstract Image getImage(DecorType type);
	
	/** @return {@code true} si le morceau de décor est praticable, 
	 * {@code false} s'il ne l'est pas.<br> Lorsqu'un fragment de 
	 * paysage est praticable, la plupart des entités, dont le joueur,
	 * peuvent marcher dessus. S'il ne l'est pas, toute marche dessus
	 * est impossible. */
	public abstract boolean isEnabled();
	
	/**
	 * @return L'identifiant de l'unité de paysage. Cet identifiant est
	 * utilisé notamment dans l'écriture et la lecture de niveaux, pour
	 * compresser les données.
	 */
	public byte getID() {
		for (Entry<Byte, LandscapeTile> e : landIDs.entrySet()) {
			if (e.getValue().getClass().equals(this.getClass())) {
				return e.getKey();
			}
		}
		return -1;
	}
	
	/**
	 * @return Les métadonnées de l'unité de paysage, sous forme d'un byte.
	 * Utilisé notamment dans l'écriture et la lecture de niveaux.
	 */
	public byte getMetadata() {
		return 0;
	}
	
	/** Cette méthode lit la métadonnée de l'unité de paysage, et
	 * l'applique à ce paysage. Utilisé pour décompresser les données
	 * lors de la lecture d'un niveau.
	 * @param b - La métadonnée de l'unité de paysage. */
	public void setMetadata(byte b) {}
	
	/**
	 * Accorde l'unité de paysage à l'information passée en paramètres.
	 * <br>Les coordonnées ne sont pas vérifiées.
	 * @param infos - L'information qui correspond à l'unité de paysage.
	 */
	public void accordTo(LandscapeInfos infos) {
		if (infos == null) throw new NullPointerException();
		this.skin = infos.getSkin();
	}
	
	/**
	 * Obtient les informations sur l'unité de paysage.
	 * @return Les informations si elles sont nécessaires, ou {@code null}
	 * si la méthode {@link #needInfosObject()} renvoit {@code false}
	 */
	public LandscapeInfos getInfos() {
		if (!needInfosObject()) return null;
		LandscapeInfos result = new LandscapeInfos(this.skin, this.place);
		return result;
	}
	
	/**
	 * Permet de savoir si un objet d'informations du paysage doit être sérialisé
	 * en plus du paysage, pour par exemple contenir le nom du skin...
	 * <p>Si cette méthode renvoie {@code true} alors la méthode {@link #getInfos()}
	 * renverra forcément un objet non <code>null</code>
	 * @return {@code true} si les infos sont necessaires, {@code false} sinon.
	 */
	public boolean needInfosObject() {
		return !"".equals(this.skin);
	}
	
	/** Définit si l'unité de terrain doit être rendue ou non.
	 * @param flag - {@code true} si le rendu de cette unité
	 * doit être masqué, {@code false} sinon. */
	public void setUnrendered(boolean flag) {
		this.norender = flag;
	}
	
	public RenderLandscape getRender(DecorType type) {
		if (this.norender) {
			return new RenderLandscapeNothing();
		}
		
		if (this.renders.get(type) == null) {
			this.renders.put(type, createRender(type));
		}
		
		RenderLandscape render = this.renders.get(type);
		
		return render;
	}
	
	public RenderLandscape createRender(DecorType type) {
		return new RenderLandscape();
	}
	
	public void removeAllRenders() {
		this.renders = new HashMap<DecorType, RenderLandscape>();
	}
	
	public void setSkin(String skin) {
		if (skin == null) skin = "";
		this.skin = skin;
	}
	
	public String getSkin() {
		return this.skin;
	}
	
	public Hitbox getHitboxf() {
		Hitbox result = new HitboxPolygon(getBoundsf());
		result.setPosition(place.x, place.y);
		result.setStatic(true);
		return result;
	}
	
	public RectangleDouble getBoundsf() {
		return new RectangleDouble(place.x, place.y, place.x + 1, place.y + 1);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((place == null) ? 0 : place.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof LandscapeTile)) {
			return false;
		}
		LandscapeTile other = (LandscapeTile) obj;
		if (place == null) {
			if (other.place != null) {
				return false;
			}
		} else if (!place.equals(other.place)) {
			return false;
		}
		if (skin == null) {
			if (other.skin != null) {
				return false;
			}
		}
		else if (!skin.equals(other.skin)) {
			return false;
		}
		return true;
	}
	
	@Override
	public LandscapeTile clone(){
		LandscapeTile result = null;
		try {
			result = (LandscapeTile) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		result.renders = new HashMap<DecorType, RenderLandscape>();
		
		return result;
	}
}
