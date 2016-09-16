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
	/** La hitbox de cette entité. */
	protected Hitbox hitbox = new HitboxCircle(0);
	
	protected int wallCollisionCount = 0;
	protected ArrayList<Entity> collidedEntities = new ArrayList<Entity>();
	
	//RENDU
	
	protected RenderManager renderManager = new RenderManager();
	protected boolean createdRenderManager = false;
	/** Indique l'apparence de l'entité (sa texture). Si c'est une chaine de
	 * caractère vide, la texture par défaut sera choisie.
	 * <p>Les skins ne sont pas gérés par la classe {@link Entity}, il
	 * appartient au sous-classes d'implémenter le changement d'apparence. */
	protected String skin = "";
	
	//AUTRE

	/** Ce champ vaut {@code true} si l'entité à déjà été mise à jour à
	 * ce tour de boucle, {@code false} sinon.<p> Attention, la variable
	 * se remet à {@code false} toute seule, mais il appartient au sous-classes 
	 * d'implémenter sa mise à {@code true}.*/
	protected boolean updated = false;
	/** La priorité est un nombre allant de 1 à infini. Plus la priorité est petite,
	 *  plus le composant sera dessiné tôt. */
	protected int priority = 1;
	
	/** l'entité peut stocker le jeu, si elle en a besoin, dans ce champs. */
	protected GamePlayingDefault game = null;
	
	/** Une case en unité de base (256). */
	public static final int CASE = 256;
	/** La demi-case, qui n'est parfois pas une véritable demi-case ^^ */
	public static final int DEMI_CASE = CASE / 2;
	/** La demi-case en cases (unité). */
	public static final float DEMI_CASE_F = (float) DEMI_CASE / (float) CASE;
	/** Si deux entités sont séparées par cette distance, on est sûr qu'elles ne
	 * peuvent communiquer, ou interagir entre elles. Cette valeur est utilisée pour
	 * accélerer les calculs, en éliminant d'office les entités trop lointaines. */
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
	 * Donne l'image qui représente l'entité dans l'éditeur de
	 * niveau. Généralement une image fixe.
	 * <p>Attention les dimensions de l'image prises en compte dans
	 * l'éditeur de niveau (pour l'affichage) ne sont pas les dimensions
	 * de l'image réelle, mais celles de la méthode {@link #getImgDimensions()}.
	 * @return L'image sous forme d'objet {@link Image} (Le plus souvent
	 * {@link BufferedImage})*/
	public abstract Image getImage();
	
	/**
	 * Donne les dimensions de l'image qui représente l'entité
	 * dans l'éditeur de niveau.
	 * @return Les dimensions de l'image en pixels. Il est conseillé
	 * de donner les dimensions réelles de l'image en question.
	 * @see #getImage() */
	public DimensionsInt getImgDimensions() {
		return getDimensions();
	}
	
	public TextureQuad getMinimapIcon() {
		return TexturesLoader.TEXTURE_NULL;
	}
	
	/** @return l'objet qui va dessiner l'entité à l'écran. Cette
	 * méthode est utilisée pour créer l'objet "rendu" de l'entité,
	 * afin de le stocker en mémoire. */
	protected abstract void createRender();
	
	/** Recharge le rendu. A la différence de la méthode {@link #recreateRender()},
	 * cette méthode libère toutes les ressources avant de recréer
	 * le rendu, ce qui permet de recharger les textures lorsqu'elles
	 * ont été supprimées. */
	public void reloadRender() {
		this.renderManager = new RenderManager();
		this.createdRenderManager = false;
	}
	
	/** @return l'objet "rendu" de l'entité, qui la dessinera à
	 * l'écran. Par défaut, cet objet est stocké dans la classe
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
	
	/** @return les coordonnées de l'entité, en cases. */
	public Vec2f getPositionf() {
		return this.hitbox.getPosition();
	}

	/** @return les coordonnees de l'entité, en unité de base (une case = {@value #CASE}) */
	public Vec2i getPositioni() {
		return new Vec2i(
				(int)(this.hitbox.getPositionX() * CASE),
				(int)(this.hitbox.getPositionY() * CASE));
	}
	
	/** @return Les coordonnées du coin en haut à gauche de l'image dessinée du
	 * composant. Ces coordonnées sont valables si l'échelle est 1 pixel = 1 unité de base.
	 * (rappel : avec l'unité de base, une case = {@value #CASE}) */
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
	
	/** Cette méthode définit les coordonnées exactes de cette entité.
	 * <p>{@literal <!>} Attention : toutes les autres méthode de la
	 * classe qui définissent les coordonnées de l'entité font appel
	 * à cette méthode-ci. Si elle est implémentée, elle ne doit pas
	 * modifier les coordonnées en appelant une méthode tierce
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
			throw new IllegalArgumentException("La priorité ne peut être négative ou nulle");
		this.priority = priority;
	}
	
	/**
	 * Donne les dimensions de l'entité dans le jeu. Ces dimensions sont
	 * utilisées pour calculer la hitbox prédéfinie, entre autres.
	 * @return Les dimensions de l'entité dans le jeu, en unité de base
	 * (avec cette unité, une case = {@value #CASE}. Si une entité n'a pas
	 * besoin de hitbox, elle pourra tout de même définir ses dimensions 
	 * à (32;32) pour pouvoir être cliquable dans l'éditeur de jeu.*/
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
	
	/** Cette méthode doit être appellée avant le début de chaque tour, pour 
	 * que le champ {@link #lastHitbox} contiennent bien la hitbox de cette
	 * entité avant la mise à jour de la physique.*/
	public void updateLastHitbox() {
		this.lastHitbox = this.hitbox.clone();
	}
	
	/**
	 * Cette méthode trouve si l'entité est sur le point passé en paramètre.
	 * Elle est considérée sur le point si ses limites comprenne le point
	 * passé en paramètre.
	 * @param p - Le point à tester.
	 * @return {@code true} si l'entité est sur le point, {@code false} sinon.
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
	 * Donne la distance entre cette entité et l'entité passée 
	 * en paramètre, en unité de base (une case = {@value #CASE}).
	 * Les points pris en référence pour le calcul sont les centres
	 * respectifs des entités.
	 * @param entity - la seconde entité.
	 * @return La distance voulue */
	public double getDistance(Entity entity){
		return MathUtil.getDistance(getPositioni().asDouble(), entity.getPositioni().asDouble());
	}
	
	/** 
	 * Donne la distance entre cette entité et l'entité passée 
	 * en paramètre, en cases. Les points pris en référence sont
	 * les centres respectifs des entités.
	 * @param entity - la seconde entité.
	 * @return La distance voulue */
	public double getDistancef(Entity entity) {
		return MathUtil.getDistance(getPositionf().asDouble(), entity.getPositionf().asDouble());
	}
	
	/** 
	 * {@link #getDistance(Entity)} appliquée à un décor. Le
	 * centre de la case est pris en référence.
	 * @param land - voir {@link #getDistance(Entity)}
	 * @return La distance voulue, en unité de base (1 case = 256).
	 */
	public double getDistance(LandscapeTile land) {
		return MathUtil.getDistance(land.getCoordonnéesCentre().asDouble(), getPositioni().asDouble());
	}
	
	public double getDistancef(LandscapeTile land) {
		Vec2d landCentre = land.getCoordonnéesCase().clone().asDouble();
		landCentre.x += DEMI_CASE_F;
		landCentre.y += DEMI_CASE_F;
		
		return MathUtil.getDistance(landCentre, getPositionf().asDouble());
	}
	
	/**
	 * @return Si cette entité est solide ou non. Si elle n'est pas solide, les
	 * différentes entités du jeu peuvent la traverser, sans tenir compte de sa
	 * hitbox.
	 */
	public boolean isSolid() {
		return this.hitbox.isSolid();
	}
	
	/** Permet de savoir si l'entité est visible sur la minimap.
	 *  @return {@code true} si l'entité est affichée sur la
	 *  minimap, {@code false} sinon. */
	public boolean isMapVisible() {
		return true;
	}
	
	public boolean canPassVirtualWall() {
		return false;
	}
	
	/**
	 * Donne la direction vers laquelle il faut aller pour rejoindre l'entité
	 * passée en paramêtre. Le nord (PI / 2) est considéré vers le haut.
	 * @param other - L'autre entité.
	 * @return La direction en radians, selon la convention de la {@link net.bynaryscode.util.Boussole}.
	 * @see Boussole
	 */
	public float getDirection(Entity other) {
		double d = getDistancef(other);
		double dX = other.hitbox.getPositionX() - this.hitbox.getPositionX();
		double dY = this.hitbox.getPositionY() - other.hitbox.getPositionY();
		return (float) MathUtil.angle(dX / d, dY / d);
	}
	
	/** Met à jour la physique de l'entité.  */
	public void updatePhysic(long dT, GamePlayingDefault game) {
		
	}
	
	/**
	 * Met à jour la logique de l'entité. Cela comprend toutes les
	 * interactions non physiques (ramassage, création de checkpoint,
	 * timer, état...).
	 * <p>Il est à noter que la première mise à jour est appelée avant
	 * même que les objets de rendu soient créés. Anisi, des données
	 * nécessaires pour le rendu de cette entité (le type de décor par
	 * exemple) peuvent être définies dans cette méthode.
	 * @param dT - Le temps écoulé depuis la dernière mise à jour.
	 * @param game - Le jeu dans lequelle est situé l'entité.
	 */
	public void updateLogic(long dT, GamePlayingDefault game) {
		for (Entity collided : this.collidedEntities) {
			this.onEntityCollision(dT, game, collided);
		}
		
		if (this.wallCollisionCount > 0) {
			this.onLandscapeCollision(dT, game);
		}
		
		clearCollisions();
		
		//création du rendu si ce n'est pas déjà fait.
		getRenderManager();
		
		this.updated = true;
	}
	
	/** L'entité enregistre les collisions détectées lors de sa mise
	 * à jour physique afin de les traiter lors de sa mise à jour
	 * logique.<p>
	 * Cette méthode ajoute une collision avec une entité. */
	public void addEntityCollision(Entity other) {
		this.collidedEntities.add(other);
	}
	
	/** L'entité enregistre les collisions détectées lors de sa mise
	 * à jour physique afin de les traiter lors de sa mise à jour
	 * logique.<p>
	 * Cette méthode ajoute une collision avec un mur */
	public void addWallCollision() {
		this.wallCollisionCount++;
	}
	
	/** Cette méthode supprime les collisions enregistrées par
	 * {@link #addEntityCollision(Entity)} ou {@link #addWallCollision()}. */
	public void clearCollisions() {
		this.collidedEntities.clear();
		this.wallCollisionCount = 0;
	}
	
	/**
	 * Effectue les conséquences d'une collision avec une autre entité.
	 * @param delta le temps écoulé depuis la dernière mise à jour
	 * @param game le moteur de jeu qui contient toutes les entités et
	 * le décor.
	 * @param collided l'autre entité impliquées dans la collision.
	 */
	public void onEntityCollision(long delta, GamePlayingDefault game, Entity collided) {}
	public void onLandscapeCollision(long delta, GamePlayingDefault game) {}
	
	/** 
	 * Vérifie s'il y a collision avec l'entité passée en paramètres.
	 * @param other L'entité susceptible de percuter celle-ci.
	 * @return <code>true</code> si une collision est en cours
	 * avec cette entité, <code>false</code> sinon
	 */
	public boolean hasCollision(Entity other) {
		if (other == this) return false;
		
		if (getDistancef(other) > MAX_DISTANCE) {
			return false;
		}
		
		if (!getHitbox().canCollide(other.getHitbox())) return false;

		//Si aucun des cas particuliers ne s'est présenté, teste la collision des hitbox.
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
