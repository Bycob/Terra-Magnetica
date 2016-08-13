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
		//COMPATIBILIT�
		if (id == 1) {//ancien d�cor "inaccessible" transform� en mur
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
	
	public void setCoordonn�esCase(int x, int y){
		place = new Vec2i(x,y);
	}
	
	public Vec2i getCoordonn�esCase(){
		return new Vec2i(place.x, place.y);
	}
	
	/** Retourne les coordonn�es du centre de la case de d�cor.
	 * @return */
	public Vec2f getCoordonneesf() {
		return new Vec2f(place.x + Entity.DEMI_CASE_F, place.y + Entity.DEMI_CASE_F);
	}
	
	/**
	 * @return Donne les coordonn�es du point auquel dessiner cette
	 * unit� de d�cor, dans l'�diteur de niveau.
	 */
	public Vec2i getCoordonn�esToDraw(){
		return new Vec2i(place.x * Entity.CASE, place.y * Entity.CASE);
	}
	
	public Vec2i getCoordonn�esCentre(){
		return new Vec2i(
				place.x * Entity.CASE + Entity.DEMI_CASE,
				place.y * Entity.CASE + Entity.DEMI_CASE);
	}
	
	/**
	 * @return Donne les dimensions en unit� de base ({@link Entity#CASE})
	 * de l'unit� de paysage. Normalement les dimensions d'une case.
	 */
	public DimensionsInt getDimensions(){
		return new DimensionsInt(Entity.CASE, Entity.CASE);
	}
	
	public abstract Image getImage(DecorType type);
	
	/** @return {@code true} si le morceau de d�cor est praticable, 
	 * {@code false} s'il ne l'est pas.<br> Lorsqu'un fragment de 
	 * paysage est praticable, la plupart des entit�s, dont le joueur,
	 * peuvent marcher dessus. S'il ne l'est pas, toute marche dessus
	 * est impossible. */
	public abstract boolean isEnabled();
	
	/**
	 * @return L'identifiant de l'unit� de paysage. Cet identifiant est
	 * utilis� notamment dans l'�criture et la lecture de niveaux, pour
	 * compresser les donn�es.
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
	 * @return Les m�tadonn�es de l'unit� de paysage, sous forme d'un byte.
	 * Utilis� notamment dans l'�criture et la lecture de niveaux.
	 */
	public byte getMetadata() {
		return 0;
	}
	
	/** Cette m�thode lit la m�tadonn�e de l'unit� de paysage, et
	 * l'applique � ce paysage. Utilis� pour d�compresser les donn�es
	 * lors de la lecture d'un niveau.
	 * @param b - La m�tadonn�e de l'unit� de paysage. */
	public void setMetadata(byte b) {}
	
	/**
	 * Accorde l'unit� de paysage � l'information pass�e en param�tres.
	 * <br>Les coordonn�es ne sont pas v�rifi�es.
	 * @param infos - L'information qui correspond � l'unit� de paysage.
	 */
	public void accordTo(LandscapeInfos infos) {
		if (infos == null) throw new NullPointerException();
		this.skin = infos.getSkin();
	}
	
	/**
	 * Obtient les informations sur l'unit� de paysage.
	 * @return Les informations si elles sont n�cessaires, ou {@code null}
	 * si la m�thode {@link #needInfosObject()} renvoit {@code false}
	 */
	public LandscapeInfos getInfos() {
		if (!needInfosObject()) return null;
		LandscapeInfos result = new LandscapeInfos(this.skin, this.place);
		return result;
	}
	
	/**
	 * Permet de savoir si un objet d'informations du paysage doit �tre s�rialis�
	 * en plus du paysage, pour par exemple contenir le nom du skin...
	 * <p>Si cette m�thode renvoie {@code true} alors la m�thode {@link #getInfos()}
	 * renverra forc�ment un objet non <code>null</code>
	 * @return {@code true} si les infos sont necessaires, {@code false} sinon.
	 */
	public boolean needInfosObject() {
		return !"".equals(this.skin);
	}
	
	/** D�finit si l'unit� de terrain doit �tre rendue ou non.
	 * @param flag - {@code true} si le rendu de cette unit�
	 * doit �tre masqu�, {@code false} sinon. */
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
