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

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;

import org.terramagnetica.opengl.engine.RenderManager;
import org.terramagnetica.opengl.engine.Renderable;
import org.terramagnetica.opengl.engine.TextureQuad;
import org.terramagnetica.physics.Hitbox;
import org.terramagnetica.physics.HitboxCircle;
import org.terramagnetica.physics.HitboxFamily;
import org.terramagnetica.physics.HitboxPolygon;
import org.terramagnetica.ressources.TexturesLoader;
import org.terramagnetica.ressources.io.BufferedObjectInputStream;
import org.terramagnetica.ressources.io.BufferedObjectOutputStream;
import org.terramagnetica.ressources.io.Codable;
import org.terramagnetica.ressources.io.GameIOException;

import net.bynaryscode.util.Boussole;
import net.bynaryscode.util.maths.MathUtil;
import net.bynaryscode.util.maths.geometric.DimensionsFloat;
import net.bynaryscode.util.maths.geometric.DimensionsInt;
import net.bynaryscode.util.maths.geometric.RectangleDouble;
import net.bynaryscode.util.maths.geometric.RectangleInt;
import net.bynaryscode.util.maths.geometric.Vec2d;
import net.bynaryscode.util.maths.geometric.Vec2f;
import net.bynaryscode.util.maths.geometric.Vec2i;

public abstract class Entity implements Serializable, Cloneable, Codable {
	
	private static final long serialVersionUID = 4199408739382933790L;

	/** Famille de toutes les hitboxes pouvant traverser les murs virtuels. */
	public static final HitboxFamily PASS_VIRTUAL_WALL_FAMILY = new HitboxFamily("passvirtualwall");
	public static final HitboxFamily VIRTUAL_WALL_FAMILY = new HitboxFamily("virtualwall");
	
	static {
		PASS_VIRTUAL_WALL_FAMILY.setCollisionPermission(VIRTUAL_WALL_FAMILY, false);
	}
	
	//PHYSIQUE
	
	protected Hitbox lastHitbox;
	/** La hitbox de cette entit�. */
	protected Hitbox hitbox = new HitboxCircle(0);
	
	protected int wallCollisionCount = 0;
	protected ArrayList<Entity> collidedEntities = new ArrayList<Entity>();
	
	//RENDU
	
	protected RenderManager renderManager = new RenderManager();
	protected boolean createdRenderManager = false;
	/** Indique l'apparence de l'entit� (sa texture). Si c'est une chaine de
	 * caract�re vide, la texture par d�faut sera choisie.
	 * <p>Les skins ne sont pas g�r�s par la classe {@link Entity}, il
	 * appartient au sous-classes d'impl�menter le changement d'apparence. */
	protected String skin = "";
	
	//AUTRE

	/** Ce champ vaut {@code true} si l'entit� � d�j� �t� mise � jour �
	 * ce tour de boucle, {@code false} sinon.<p> Attention, la variable
	 * se remet � {@code false} toute seule, mais il appartient au sous-classes 
	 * d'impl�menter sa mise � {@code true}.*/
	protected boolean updated = false;
	/** La priorit� est un nombre allant de 1 � infini. Plus la priorit� est petite,
	 *  plus le composant sera dessin� t�t. */
	protected int priority = 1;
	
	/** l'entit� peut stocker le jeu, si elle en a besoin, dans ce champs. */
	protected GamePlayingDefault game = null;
	
	/** Une case en unit� de base (256). */
	public static final int CASE = 256;
	/** La demi-case, qui n'est parfois pas une v�ritable demi-case ^^ */
	public static final int DEMI_CASE = CASE / 2;
	/** La demi-case en cases (unit�). */
	public static final float DEMI_CASE_F = (float) DEMI_CASE / (float) CASE;
	/** Si deux entit�s sont s�par�es par cette distance, on est s�r qu'elles ne
	 * peuvent communiquer, ou interagir entre elles. Cette valeur est utilis�e pour
	 * acc�lerer les calculs, en �liminant d'office les entit�s trop lointaines. */
	public static final double MAX_DISTANCE = 10;
	
	protected Entity() {
		this(0, 0);
	}
	
	protected Entity(int x, int y) {
		this.setPositionf(x, y);
		
		this.recreateHitbox();
		
		this.hitbox.setStatic(true);
		if (this.canPassVirtualWall()) this.hitbox.setFamily(PASS_VIRTUAL_WALL_FAMILY);
	}
	
	protected Entity(int x, int y, int priority) {
		this(x, y);
		this.setPriority(priority);
	}
	
	@Override
	public Entity clone(){
		Entity result = null;
		try {
			result = (Entity) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		result.renderManager = new RenderManager();
		result.hitbox = this.hitbox.clone();
		
		return result;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.hitbox == null) ? 0 : this.hitbox.hashCode());
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
		if (!(obj.getClass().equals(this.getClass()))) {
			return false;
		}
		Entity other = (Entity) obj;
		if (this.hitbox == null) {
			if (other.hitbox != null) {
				return false;
			}
		} else if (!this.hitbox.hasSamePhysicVariables(other.hitbox)) {
			return false;
		}
		
		if (!this.skin.equals(other.skin)) {
			return false;
		}
		return true;
	}
	
	
	// RENDU
	
	/**
	 * Donne l'image qui repr�sente l'entit� dans l'�diteur de
	 * niveau. G�n�ralement une image fixe.
	 * <p>Attention les dimensions de l'image prises en compte dans
	 * l'�diteur de niveau (pour l'affichage) ne sont pas les dimensions
	 * de l'image r�elle, mais celles de la m�thode {@link #getImgDimensions()}.
	 * @return L'image sous forme d'objet {@link Image} (Le plus souvent
	 * {@link BufferedImage})*/
	public abstract Image getImage();
	
	/**
	 * Donne les dimensions de l'image qui repr�sente l'entit�
	 * dans l'�diteur de niveau.
	 * @return Les dimensions de l'image en pixels. Il est conseill�
	 * de donner les dimensions r�elles de l'image en question.
	 * @see #getImage() */
	public DimensionsInt getImgDimensions() {
		return getDimensions();
	}
	
	public TextureQuad getMinimapIcon() {
		return TexturesLoader.TEXTURE_NULL;
	}
	
	/** @return l'objet qui va dessiner l'entit� � l'�cran. Cette
	 * m�thode est utilis�e pour cr�er l'objet "rendu" de l'entit�,
	 * afin de le stocker en m�moire. */
	protected abstract void createRender();
	
	/** Recharge le rendu. A la diff�rence de la m�thode {@link #recreateRender()},
	 * cette m�thode lib�re toutes les ressources avant de recr�er
	 * le rendu, ce qui permet de recharger les textures lorsqu'elles
	 * ont �t� supprim�es. */
	public void reloadRender() {
		this.renderManager = new RenderManager();
		this.createdRenderManager = false;
	}
	
	/** @return l'objet "rendu" de l'entit�, qui la dessinera �
	 * l'�cran. Par d�faut, cet objet est stock� dans la classe
	 * Entity sous le non de {@link Entity#render}. */
	public Renderable getRender() {
		return this.getRenderManager().getRender();
	}
	
	public RenderManager getRenderManager() {
		if (!this.createdRenderManager) {
			createRender();
			this.createdRenderManager = true;
		}
		return this.renderManager;
	}
	
	public String getSkin() {
		return this.skin;
	}

	public void setSkin(String skin) {
		if (skin == null) skin = "";
		this.skin = skin;
	}
	
	
	// PHYSIQUE / LOGIQUE
	
	/** @return les coordonn�es de l'entit�, en cases. */
	public Vec2f getPositionf() {
		return this.hitbox.getPosition();
	}

	/** @return les coordonnees de l'entit�, en unit� de base (une case = {@value #CASE}) */
	public Vec2i getPositioni() {
		return new Vec2i(
				(int)(this.hitbox.getPositionX() * CASE),
				(int)(this.hitbox.getPositionY() * CASE));
	}
	
	/** @return Les coordonn�es du coin en haut � gauche de l'image dessin�e du
	 * composant. Ces coordonn�es sont valables si l'�chelle est 1 pixel = 1 unit� de base.
	 * (rappel : avec l'unit� de base, une case = {@value #CASE}) */
	public Vec2i getImagePosition(){
		return new Vec2i(
				this.getPositioni().x - this.getImgDimensions().getWidth() / 2,
				this.getPositioni().y - this.getImgDimensions().getHeight() / 2);
	}

	public Vec2i getCasePosition(){
		return new Vec2i(
				(int) this.hitbox.getPositionX(),
				(int) this.hitbox.getPositionY());
	}
	
	/** Cette m�thode d�finit les coordonn�es exactes de cette entit�.
	 * <p>{@literal <!>} Attention : toutes les autres m�thode de la
	 * classe qui d�finissent les coordonn�es de l'entit� font appel
	 * � cette m�thode-ci. Si elle est impl�ment�e, elle ne doit pas
	 * modifier les coordonn�es en appelant une m�thode tierce
	 * (par exemple {@link #setCasePosition(int, int)}) sous risque
	 * de plantage du programme. */
	public void setPositionf(float x, float y){
		this.hitbox.setPosition(x, y);
	}
	
	public void setPositionf(Vec2f c) {
		this.setPositionf(c.x, c.y);
	}

	public void setPositioni(int x, int y){
		this.setPositionf(
				(float) x / (float) CASE,
				(float) y / (float) CASE);
	}
	
	public void setCasePosition(int x, int y){
		this.setPositionf(
				x + Entity.DEMI_CASE_F,
				y + Entity.DEMI_CASE_F);
	}
	
	public int getPriority(){
		return priority;
	}
	
	public void setPriority(int priority){
		if (priority < 1)
			throw new IllegalArgumentException("La priorit� ne peut �tre n�gative ou nulle");
		this.priority = priority;
	}
	
	/**
	 * Donne les dimensions de l'entit� dans le jeu. Ces dimensions sont
	 * utilis�es pour calculer la hitbox pr�d�finie, entre autres.
	 * @return Les dimensions de l'entit� dans le jeu, en unit� de base
	 * (avec cette unit�, une case = {@value #CASE}. Si une entit� n'a pas
	 * besoin de hitbox, elle pourra tout de m�me d�finir ses dimensions 
	 * � (32;32) pour pouvoir �tre cliquable dans l'�diteur de jeu.*/
	public abstract DimensionsInt getDimensions();
	
	public DimensionsFloat getDimensionsf() {
		return new DimensionsFloat(getDimensions().getWidth() / (float) CASE ,
				getDimensions().getHeight() / (float) CASE);
	}
	
	public RectangleInt getBounds() {
		DimensionsInt dim = this.getDimensions();
		int hwidth = dim.getWidth() / 2;
		int hheight = dim.getHeight() / 2;
		Vec2i point = this.getPositioni();
		int x = point.x;
		int y = point.y;
		
		return new RectangleInt(
				x - hwidth, y - hheight,
				x + hwidth, y + hheight);
	}
	
	public RectangleDouble getBoundsf() {
		DimensionsFloat dim = this.getDimensionsf();
		float hwidth = dim.getWidth() / 2;
		float hheight = dim.getHeight() / 2;
		Vec2f point = this.getPositionf();
		float x = point.x;
		float y = point.y;
		
		return new RectangleDouble(
				x - hwidth, y - hheight,
				x + hwidth, y + hheight);
	}
	
	protected Hitbox createHitbox() {
		RectangleDouble shape = getBoundsf();
		shape.translate(- this.hitbox.getPositionX(), - this.hitbox.getPositionY());
		return new HitboxPolygon(shape);
	}
	
	public void recreateHitbox() {
		this.setHitbox(createHitbox());
	}
	
	public void setHitbox(Hitbox hb) {
		if (hb == null) throw new NullPointerException("hb == null");
		
		Hitbox oldHitbox = this.hitbox;
		this.hitbox = hb;
		
		if (oldHitbox != null) {
			this.hitbox.setSamePhysicPropertiesAndVariables(oldHitbox);
		}
		
		if (this.lastHitbox == null) {
			updateLastHitbox();
		}
	}
	
	public final Hitbox getHitbox() {
		return this.hitbox;
	}
	
	public Hitbox getLastHitbox() {
		return this.lastHitbox;
	}
	
	public Entity getLastHitboxAsEntity() {
		Entity clone = null;
		try {
			clone = (Entity) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		clone.hitbox = this.lastHitbox.clone();
		
		return clone;
	}
	
	/** Cette m�thode doit �tre appell�e avant le d�but de chaque tour, pour 
	 * que le champ {@link #lastHitbox} contiennent bien la hitbox de cette
	 * entit� avant la mise � jour de la physique.*/
	public void updateLastHitbox() {
		this.lastHitbox = this.hitbox.clone();
	}
	
	/**
	 * Cette m�thode trouve si l'entit� est sur le point pass� en param�tre.
	 * Elle est consid�r�e sur le point si ses limites comprenne le point
	 * pass� en param�tre.
	 * @param p - Le point � tester.
	 * @return {@code true} si l'entit� est sur le point, {@code false} sinon.
	 */
	public boolean isOn(Vec2i p){
		return this.getBounds().contains(p);
	}
	
	public boolean isOn(int x, int y){
		return isOn(new Vec2i(x,y));
	}
	
	public boolean isIn(RectangleDouble rec) {
		return rec.contains(this.getBoundsf());
	}

	/** 
	 * Donne la distance entre cette entit� et l'entit� pass�e 
	 * en param�tre, en unit� de base (une case = {@value #CASE}).
	 * Les points pris en r�f�rence pour le calcul sont les centres
	 * respectifs des entit�s.
	 * @param entity - la seconde entit�.
	 * @return La distance voulue */
	public double getDistance(Entity entity){
		return MathUtil.getDistance(getPositioni().asDouble(), entity.getPositioni().asDouble());
	}
	
	/** 
	 * Donne la distance entre cette entit� et l'entit� pass�e 
	 * en param�tre, en cases. Les points pris en r�f�rence sont
	 * les centres respectifs des entit�s.
	 * @param entity - la seconde entit�.
	 * @return La distance voulue */
	public double getDistancef(Entity entity) {
		return MathUtil.getDistance(getPositionf().asDouble(), entity.getPositionf().asDouble());
	}
	
	/** 
	 * {@link #getDistance(Entity)} appliqu�e � un d�cor. Le
	 * centre de la case est pris en r�f�rence.
	 * @param land - voir {@link #getDistance(Entity)}
	 * @return La distance voulue, en unit� de base (1 case = 256).
	 */
	public double getDistance(LandscapeTile land) {
		return MathUtil.getDistance(land.getCoordonn�esCentre().asDouble(), getPositioni().asDouble());
	}
	
	public double getDistancef(LandscapeTile land) {
		Vec2d landCentre = land.getCoordonn�esCase().clone().asDouble();
		landCentre.x += DEMI_CASE_F;
		landCentre.y += DEMI_CASE_F;
		
		return MathUtil.getDistance(landCentre, getPositionf().asDouble());
	}
	
	/**
	 * @return Si cette entit� est solide ou non. Si elle n'est pas solide, les
	 * diff�rentes entit�s du jeu peuvent la traverser, sans tenir compte de sa
	 * hitbox.
	 */
	public boolean isSolid() {
		return this.hitbox.isSolid();
	}
	
	/** Permet de savoir si l'entit� est visible sur la minimap.
	 *  @return {@code true} si l'entit� est affich�e sur la
	 *  minimap, {@code false} sinon. */
	public boolean isMapVisible() {
		return true;
	}
	
	public boolean canPassVirtualWall() {
		return false;
	}
	
	/**
	 * Donne la direction vers laquelle il faut aller pour rejoindre l'entit�
	 * pass�e en param�tre. Le nord (PI / 2) est consid�r� vers le haut.
	 * @param other - L'autre entit�.
	 * @return La direction en radians, selon la convention de la {@link net.bynaryscode.util.Boussole}.
	 * @see Boussole
	 */
	public float getDirection(Entity other) {
		double d = getDistancef(other);
		double dX = other.hitbox.getPositionX() - this.hitbox.getPositionX();
		double dY = this.hitbox.getPositionY() - other.hitbox.getPositionY();
		return (float) MathUtil.angle(dX / d, dY / d);
	}
	
	/** Met � jour la physique de l'entit�.  */
	public void updatePhysic(long dT, GamePlayingDefault game) {
		
	}
	
	/**
	 * Met � jour la logique de l'entit�. Cela comprend toutes les
	 * interactions non physiques (ramassage, cr�ation de checkpoint,
	 * timer, �tat...).
	 * <p>Il est � noter que la premi�re mise � jour est appel�e avant
	 * m�me que les objets de rendu soient cr��s. Anisi, des donn�es
	 * n�cessaires pour le rendu de cette entit� (le type de d�cor par
	 * exemple) peuvent �tre d�finies dans cette m�thode.
	 * @param dT - Le temps �coul� depuis la derni�re mise � jour.
	 * @param game - Le jeu dans lequelle est situ� l'entit�.
	 */
	public void updateLogic(long dT, GamePlayingDefault game) {
		for (Entity collided : this.collidedEntities) {
			this.onEntityCollision(dT, game, collided);
		}
		
		if (this.wallCollisionCount > 0) {
			this.onLandscapeCollision(dT, game);
		}
		
		clearCollisions();
		
		//cr�ation du rendu si ce n'est pas d�j� fait.
		getRenderManager();
		
		this.updated = true;
	}
	
	/** L'entit� enregistre les collisions d�tect�es lors de sa mise
	 * � jour physique afin de les traiter lors de sa mise � jour
	 * logique.<p>
	 * Cette m�thode ajoute une collision avec une entit�. */
	public void addEntityCollision(Entity other) {
		this.collidedEntities.add(other);
	}
	
	/** L'entit� enregistre les collisions d�tect�es lors de sa mise
	 * � jour physique afin de les traiter lors de sa mise � jour
	 * logique.<p>
	 * Cette m�thode ajoute une collision avec un mur */
	public void addWallCollision() {
		this.wallCollisionCount++;
	}
	
	/** Cette m�thode supprime les collisions enregistr�es par
	 * {@link #addEntityCollision(Entity)} ou {@link #addWallCollision()}. */
	public void clearCollisions() {
		this.collidedEntities.clear();
		this.wallCollisionCount = 0;
	}
	
	/**
	 * Effectue les cons�quences d'une collision avec une autre entit�.
	 * @param delta le temps �coul� depuis la derni�re mise � jour
	 * @param game le moteur de jeu qui contient toutes les entit�s et
	 * le d�cor.
	 * @param collided l'autre entit� impliqu�es dans la collision.
	 */
	public void onEntityCollision(long delta, GamePlayingDefault game, Entity collided) {}
	public void onLandscapeCollision(long delta, GamePlayingDefault game) {}
	
	/** 
	 * V�rifie s'il y a collision avec l'entit� pass�e en param�tres.
	 * @param other L'entit� susceptible de percuter celle-ci.
	 * @return <code>true</code> si une collision est en cours
	 * avec cette entit�, <code>false</code> sinon
	 */
	public boolean hasCollision(Entity other) {
		if (other == this) return false;
		
		if (getDistancef(other) > MAX_DISTANCE) {
			return false;
		}
		
		if (!getHitbox().canCollide(other.getHitbox())) return false;

		//Si aucun des cas particuliers ne s'est pr�sent�, teste la collision des hitbox.
		return getHitbox().intersects(other.getHitbox());
	}
	
	@Override
	public void code(BufferedObjectOutputStream out) throws GameIOException {
		out.writeIntField(this.priority, 0);
		out.writeFloatField(this.hitbox.getPositionX(), 1);
		out.writeFloatField(this.hitbox.getPositionY(), 2);
		
		out.writeStringField(this.skin, 3);
	}
	
	@Override
	public Entity decode(BufferedObjectInputStream in) throws GameIOException {
		this.priority = in.readIntField(0);
		this.setPositionf(in.readFloatField(1), in.readFloatField(2));
		
		this.skin = in.readStringFieldWithDefaultValue(3, this.skin);
		return this;
	}
}
