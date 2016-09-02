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

import java.awt.Point;
import java.awt.Rectangle;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.terramagnetica.ressources.io.BufferedObjectInputStream;
import org.terramagnetica.ressources.io.BufferedObjectOutputStream;
import org.terramagnetica.ressources.io.Codable;
import org.terramagnetica.ressources.io.GameIOException;

import net.bynaryscode.util.Util;
import net.bynaryscode.util.maths.geometric.DimensionsInt;
import net.bynaryscode.util.maths.geometric.RectangleInt;
import net.bynaryscode.util.maths.geometric.Vec2f;
import net.bynaryscode.util.maths.geometric.Vec2i;

/**
 * Une {@link Room} représente un étage d'un niveau de type {@link
 * LevelDefault}. De nombreuses méthodes permettent de la modifier,
 * en revanche, pour le jeu à proprement parler, voir la classe
 * {@link GamePlayingDefault}.
 * 
 * <p>La position d'origine du joueur est gérée automatiquement par l'étage.
 * Lorsque le joueur ne peut pas être placé au sein de l'étage, il
 * disparait. Dès lors, dès qu'une nouvelle modification permet au 
 * joueur d'être placé quelque part, il y est placé automatiquement.
 * Si le joueur ne peut rester à sa place, suite à une modification
 * du décor par exemple, il est automatiquement déplacé.
 * @author Louis JEAN
 */
public class Room implements Serializable, Cloneable, Codable {
	
	/**
	 * Retourne une version optimisée de la salle en paramètre.
	 * <p>Tous les secteurs inutilisés sont supprimés pour réduire
	 * la place qu'occupe l'étage en mémoire.
	 * <p>Attention, la salle retournée est prévue pour être utilisée à la
	 * place de celle passée en paramètre. Toutes les entités, unités de
	 * paysages ect... ne seront pas clonés (Si on modifie les entités de
	 * la salle d'origine cela aura des répercussions sur les entités de la
	 * salle ainsi créée).
	 * @param r - La salle à optimiser
	 * @return Une version optimisée de la salle, plus petite.
	 * @throws UnusedRoomException Si la salle entière n'est pas utilisée.
	 */
	public static Room scaledRoom(Room r) throws UnusedRoomException {
		if (r == null) return null;
		
		//ETAPE 1 : détermination des bords de la nouvelle salle.
		
		RectangleInt bounds = new RectangleInt(
				0, 0,
				r.getDimensions().getWidth() - 1,
				r.getDimensions().getHeight() - 1);
		LandscapeTile[][] décor = r.getLandscape();
		
		for (int i = 0 ; i <= bounds.xmax ; i++) {
			bounds.xmin = i;
			if (!isLineUnused(décor, i, true)) {
				break;
			}
		}
		
		if (bounds.xmin == bounds.xmax) throw new UnusedRoomException();
		
		for (int i = bounds.xmax; i >= bounds.xmin; i--) {
			if (!isLineUnused(décor, i, true)) {
				bounds.xmax = i;
				break;
			}
		}
		
		for (int i = 0 ; i <= bounds.ymax ; i++) {
			if (!isLineUnused(décor, i, false)) {
				bounds.ymin = i;
				break;
			}
		}
		
		for (int i = bounds.ymax ; i >= bounds.ymin ; i--) {
			if (!isLineUnused(décor, i, false)) {
				bounds.ymax = i;
				break;
			}
		}
		
		//ETAPE 2 : création d'une nouvelle salle, adaptation des paramètres.
		
		Room result = new Room(r.getDecorType(), bounds.getWidth() + 1, bounds.getHeight() + 1);
		
		for (int i = 0 ; i < result.getDimensions().getWidth() ; i++) {
			for (int j = 0 ; j < result.getDimensions().getHeight() ; j++) {
				result.decor[i][j] = r.getLandscapeAt(i + bounds.xmin, j + bounds.ymin);
				result.decor[i][j].setCoordonnéesCase(i, j);
			}
		}
		
		result.checkRange();
		r.removePlayer();
		
		for (Entity e : r.entities) {
			Vec2f c = e.getCoordonnéesf();
			e.setCoordonnéesf(c.x - bounds.xmin, c.y - bounds.ymin);
			result.entities.add(e);
		}
		
		Vec2f cperso = r.player.getCoordonnéesf();
		r.player.setCoordonnéesf(cperso.x - bounds.xmin, cperso.y - bounds.ymin);
		result.setPlayer(r.player);
		
		result.id = r.id;
		
		return result;
	}
	
	/** usage interne : {@link Room#scaledRoom(Room)} : permet de vérifier si
	 * une ligne ne contient que du paysage inaccessible (si oui on peut donc
	 * la supprimer. En passant le paramètre {@code column} à {@code true}
	 * on peut faire la même chose avec les colonnes.
	 * <br>déclenche une erreur si le décor ne contient rien, si l'indice est
	 * plus grand que le décor. */
	private static boolean isLineUnused(LandscapeTile[][] décor, int index, boolean column) {
		int max = column ? décor[0].length : décor.length;
		for (int i = 0 ; i < max ; i++) {
			LandscapeTile tested = column ? décor[index][i] : décor[i][index];
			if (!(tested instanceof WallTile)) {
				return false;
			}
			if (((WallTile) tested).getOrientation() != WallTile.PLANE) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Agrandit une salle selon les paramètres.
	 * <p>Attention, impossible de rétrécir la salle en passant des paramètres
	 * négatifs.
	 * <p>La salle passée en paramètre devient inutilisable après l'appel
	 * de cette fonction. Il est donc préférable d'utiliser la nouvelle salle
	 * retournée.
	 * @param r - la salle à agrandir.
	 * @param top - le nombre de lignes à ajouter en haut.
	 * @param bottom - le nombre de lignes à ajouter en bas.
	 * @param left - le nombre de colonnes à ajouter à gauche.
	 * @param right - le nombre de colonnes à ajouter à droite.
	 * @return Une salle qui est la copie du modèle original, mais agrandie.
	 */
	public static Room grownRoom(Room r, int top, int bottom, int left, int right) {
		if (top < 0 || bottom < 0 || left < 0 || right < 0) {
			throw new IllegalArgumentException("Impossible d'enlever des colonnes ou lignes avec cette fonction !");
		}
		
		DimensionsInt dims = r.getDimensions();
		Room result = new Room(r.getDecorType(), dims.getWidth() + left + right, dims.getHeight() + top + bottom);
		
		for (int i = 0 ; i < dims.getWidth() ; i++) {
			for (int j = 0 ; j < dims.getHeight() ; j++) {
				result.decor[i + left][j + top] = r.decor[i][j];
				result.decor[i + left][j + top].setCoordonnéesCase(i + left, j + top);
			}
		}
		
		result.checkRange();
		r.removePlayer();
		
		for (Entity e : r.entities) {
			Vec2f c = e.getCoordonnéesf();
			e.setCoordonnéesf(c.x + left, c.y + top);
			result.entities.add(e);
		}
		
		if (r.player != null) {
			Vec2f cperso = r.player.getCoordonnéesf();
			r.player.setCoordonnéesf(cperso.x + left, cperso.y + top);
			result.setPlayer(r.player);
		}
		
		result.id = r.id;
		
		return result;
	}
	
	private static final long serialVersionUID = 1L;
	
	private int id = 0;
	private String name = "";
	
	/** Informations additionnelles sur la salle, sous forme de
	 * tags.
	 * @see #addTag(String)*/
	private ArrayList<RoomTag> tags = new ArrayList<RoomTag>();
	
	//ENTITES
	
	protected ArrayList<Entity> entities;
	protected LandscapeTile[][] decor;
	protected PlayerDefault player;
	
	protected DecorType decorType;
	protected DimensionsInt size;
	/** Cette variable compte le nombre de cases permises pour le joueur.
	 * @see #checkRange() */
	protected int permitCount;
	
	public static final int TAILLE_DEFAULT = 100;
	
	/** constructeur par défault d'une salle, est appelé lors d'une création de niveau */
	public Room(){
		this(DecorType.MONTS);
	}
	
	public Room(DecorType decorType){
		this(decorType, TAILLE_DEFAULT, TAILLE_DEFAULT);
	}
	
	/**
	 * Crée une nouvelle salle avec les paramètres indiqués. Cette salle
	 * sera remplie avec du décor "inaccessible".
	 * @param decorType - Le type de décor de la salle.
	 * @param width - La largeur de la salle
	 * @param height - La hauteur de la salle
	 */
	public Room(DecorType decorType, int width, int height) {
		this.entities = new ArrayList<Entity>();
		this.decor = new LandscapeTile[width][height];
		this.size = new DimensionsInt(width, height);
		this.decorType = decorType;
		
		//initialisation du décor
		for (int i = 0 ; i < decor.length ; i++){
			for (int j = 0 ; j < decor[i].length ; j++){
				decor[i][j] = new WallTile(i, j, WallTile.PLANE);
			}
		}
	}
	
	/**
	 * constructeur avec des paramètres.
	 * @param entities - la liste des entités de ce niveau
	 * @param decor - le décor du niveau
	 * @param perso - le personnage
	 * @param decorType - le type de décor
	 */
	public Room(ArrayList<Entity> entities, LandscapeTile[][] decor, PlayerDefault perso, DecorType decorType){
		this.entities = entities;
		this.decor = decor;
		this.player = perso;
		this.decorType = decorType;
		
		this.checkRange();
		this.optimizePerso();
	}
	
	public Room(Room other) {
		other = other.clone();
		this.entities = other.entities;
		this.decor = other.decor;
		this.player = other.player;
		this.decorType = other.decorType;
		
		this.checkRange();
		this.optimizePerso();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(decor);
		result = prime * result + decorType.getIndex();
		result = prime * result + ((entities == null) ? 0 : entities.hashCode());
		result = prime * result + ((player == null) ? 0 : player.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Room other = (Room) obj;
		if (!Arrays.deepEquals(decor, other.decor))
			return false;
		if (decorType.getIndex() != other.decorType.getIndex())
			return false;
		if (!Util.listEqualsUnsorted(this.entities, other.entities))
			return false;
		if (player == null) {
			if (other.player != null)
				return false;
		} else if (!player.equals(other.player))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (!Util.listEqualsUnsorted(this.tags, other.tags))
			return false;
		return true;
	}
	
	@Override
	public Room clone(){
		Room result = null;
		try {
			result = (Room) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		result.entities = new ArrayList<Entity>();
		for (Entity entity : this.entities) {
			result.addEntityByPriority(entity.clone());
		}
		
		result.size = this.size.clone();
		result.decor = new LandscapeTile[this.size.getWidth()][this.size.getHeight()];
		
		for (int x = 0 ; x < result.decor.length ; x++) {
			for (int y = 0 ; y < result.decor[0].length ; y++) {
				result.decor[x][y] = this.decor[x][y].clone();
			}
		}
		
		if (result.player != null) result.player = this.player.clone();
		result.removePlayer();
		result.entities.add(result.player);
		result.checkRange();
		
		result.tags = new ArrayList<RoomTag>();
		result.tags.addAll(this.tags);
		
		return result;
	}
	
	public int getID() {
		return id;
	}
	
	/**
	 * L'id de la salle, utilisé par les portails.
	 * @param id - le nouvel ID.
	 */
	void setID(int id) {
		this.id = id;
	}
	
	/**
	 * Donne le nom de la salle tel qu'il est vu par le joueur, en jeu.
	 * @return
	 */
	public String getInGameName() {
		if (!"".equals(this.name)) return this.name;
		return "Salle " + (this.id + 1);
	}
	
	/** Donne le nom de la salle tel qu'il a été donné par son créateur. */
	public String getUserName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * TAGS EXISTANTS :
	 * <ul><li><b>LAMP_STATE_MODE_3</b> : les lampes de la salle fonctionnent
	 * en mode 3 (voir {@link LampState#mode}).
	 * <li><b>LIMITED_VISION</b> : la salle est en mode de vision limitée
	 * </ul>
	 */
	public enum RoomTag {
		LAMP_STATE_MODE_3(0),
		LIMITED_VISION(1);
		
		private int id;
		
		RoomTag(int id) {
			this.id = id;
		}
		
		public int getID() {
			return this.id;
		}
		
		public static RoomTag getByID(int id) {
			for (RoomTag tag : RoomTag.values()) {
				if (tag.id == id) {
					return tag;
				}
			}
			return null;
		}
	}
	
	/**Ajoute un tag à la salle.
	 * */
	public void addTag(RoomTag tag) {
		if (!hasTag(tag)) this.tags.add(tag);
	}
	
	/** @see #addTag(String) */
	public void removeTag(RoomTag tag) {
		this.tags.remove(tag);
	}
	
	/** @see #addTag(String) */
	public boolean hasTag(RoomTag tag) {
		return this.tags.contains(tag);
	}
	
	/** @see #addTag(String) */
	@SuppressWarnings("unchecked")
	public ArrayList<RoomTag> getTags() {
		return (ArrayList<RoomTag>) this.tags.clone();
	}
	
	public LandscapeTile[][] getLandscape(){
		return decor;
	}
	
	/**
	 * retourne l'unité de paysage présente aux coordonnées (x ; y)
	 * @param x - L'abscisse.
	 * @param y - L'ordonnée.
	 * @return L'unité de paysage à l'emplacement indiqué, ou <code>
	 * null</code> si elle n'existe pas.
	 */
	public LandscapeTile getLandscapeAt(int x, int y){
		if (x < 0 || x >= decor.length || y < 0 || y >= decor[0].length){
			return null;
		}
		return decor[x][y];
	}
	
	/**
	 * Donne un tableau des unités de décor autour du point indiqué, dans
	 * l'ordre : <i>NORD, EST, SUD, OUEST</i>.
	 * @param x - abscisse du point indiqué.
	 * @param y - ordonnée du point indiqué.
	 * @return Un tableau de quatre unités de décor.
	 */
	public LandscapeTile[] getLandscapeAround(int x, int y){
		LandscapeTile[] result = {
				getLandscapeAt(x, y - 1),
				getLandscapeAt(x + 1, y),
				getLandscapeAt(x, y + 1),
				getLandscapeAt(x - 1, y)};
		
		return result;
	}
	
	/**
	 * Défini le décor à la case de cordonnées (x, y)
	 * @param newCase - le nouveau décor. Il sera cloné puis
	 * ses coordonnées mises à jour.
	 */
	public void setLandscapeAt(int x, int y, LandscapeTile newCase){
		LandscapeTile oldCase = this.decor[x][y];		
		this.decor[x][y] = newCase.clone();
		this.decor[x][y].setCoordonnéesCase(x, y);
		
		if (newCase.isEnabled() && !(oldCase.isEnabled())) {
			this.permitCount++;
			if (this.permitCount == 1) {
				this.player = new PlayerDefault();
				this.player.setCoordonnéesCase(x, y);
				this.optimizePerso();
				this.entities.add(this.player);
			}
		}
		else if (!(newCase.isEnabled()) && oldCase.isEnabled()){
			this.permitCount--;
			if (this.permitCount != 0) {
				this.optimizePerso();
			}
			else {
				this.removePlayer();
				this.player = null;
			}
		}
	}
	
	public PlayerDefault getPlayer() {
		return player;
	}

	public void setPlayer(PlayerDefault perso) {
		if (perso == null) return;
		if (!this.checkPlayerLocation(perso)) return;
		
		this.removePlayer();
		this.player = perso;
		this.entities.add(this.player);
	}
	
	/**
	 * Place le personnage sur la case en (caseX ; caseY), uniquement
	 * si possible.
	 * @param caseX - abscisse de la case.
	 * @param caseY - ordonnée de la case.
	 */
	public void setPlayerLocation(int caseX, int caseY) {
		if (this.player == null) return;
		Vec2i ol = this.player.getCoordonnéesCase().clone();
		this.player.setCoordonnéesCase(caseX, caseY);
		
		if (!checkPlayerLocation(this.player)) {
			this.player.setCoordonnéesCase(ol.x, ol.y);
		}
		
		if (!this.entities.contains(this.player)) {
			this.removePlayer();
			this.entities.add(this.player);
		}
	}
	
	/**
	 * Vérifie que le personnage envoyé peut être placé
	 * dans la salle, sans aucune modification de ses coordonnées. 
	 * @param perso - Le personnage à vérifier.
	 * @return Si le personnage peut être le nouveau personnage de
	 * la salle.
	 */
	private boolean checkPlayerLocation(PlayerDefault perso) {
		Vec2i checkPoint = perso.getCoordonnéesCase();
		return this.decor[checkPoint.x][checkPoint.y].isEnabled();
	}
	
	/** Retire l'entité "joueur" de la liste des entités du jeu. */
	private void removePlayer() {
		for (int i = 0 ; i < this.entities.size() ; i++) {
			if (this.entities.get(i) instanceof PlayerDefault) {
				this.entities.remove(i);
				i--;
			}
		}
	}
	
	private void optimizePerso() {
		if (this.checkPlayerLocation(this.player)) return;
		
		bcl1 : for (LandscapeTile[] lands : this.decor) {
			for (LandscapeTile land : lands) {
				if (land.isEnabled()) {
					Vec2i c = land.getCoordonnéesCase();
					this.player.setCoordonnéesCase(c.x, c.y);
					break bcl1;
				}
			}
		}
	}
	
	public DecorType getDecorType() {
		return decorType;
	}

	public void setDecorType(DecorType decorType) {
		this.decorType = decorType;
	}
	
	public DimensionsInt getDimensions() {
		return this.size.clone();
	}
	
	public ArrayList<Entity> getEntities(){
		return entities;
	}

	public void setEntities(ArrayList<Entity> newComposants){
		this.entities = newComposants;
	}
	
	/**
	 * Ajoute une entité en fonction de sa priorité sur le dessin.
	 * @param entitee - La nouvelle entité.
	 */
	public void addEntityByPriority(Entity entitee){
		int var1 = entitee.getPriority();
		boolean fait = false;
		
		for (int i = 0 ; entities.size() != 0 && i != entities.size() && !fait ; i++){
			if (entities.get(i).getPriority() <= var1){
				entities.add(i,entitee);
				fait = true;
			}
		}
		
		if (!fait)
			entities.add(entitee);
	}
	
	public void addEntity(Entity entity) {
		if (entity == null) throw new NullPointerException();
		this.entities.add(entity);
	}
	
	public Entity getEntityAt(int x, int y){
		return getEntityAt(new Vec2i(x,y));
	}
	
	public Entity getAndDeleteEntityAt(int x, int y){
		return getAndDeleteEntityAt(new Vec2i(x,y));
	}
	
	public Entity getEntityAt(Vec2i p){
		for (Entity composant : entities){
			Vec2i origine = composant.getCoordonnées();
			DimensionsInt dims = composant.getDimensions();
			
			if (dims.getWidth() < 32) dims.setWidth(32);
			if (dims.getHeight() < 32) dims.setHeight(32);
			
			if (origine.x - dims.getWidth() / 2 < p.x && origine.y - dims.getHeight() / 2< p.y &&
					origine.x + dims.getWidth() / 2 > p.x && origine.y + dims.getHeight() / 2 > p.y){
				return composant;
			}
		}
		return null;
	}
	
	/** Supprime et retourne l'entité à l'endroit indiqué, sauf s'il s'agit du
	 * joueur. */
	public Entity getAndDeleteEntityAt(Vec2i p){
		Entity got = getEntityAt(p);
		if (!(got instanceof PlayerDefault)) {
			this.entities.remove(got);
		}
		return got;
	}
	
	public CaseEntity getCaseEntityAt(int x, int y) {
		for (Entity e : this.entities) {
			if (e instanceof CaseEntity) {
				CaseEntity ce = (CaseEntity) e;
				Vec2i cec = ce.getCoordonnéesCase();
				if (cec.equals(new Vec2i(x, y))) {
					return ce;
				}
			}
		}
		
		return null;
	}
	
	public CaseEntity getCaseEntityAt(Vec2i c) {
		return getCaseEntityAt(c.x, c.y);
	}
	
	public Entity[] getEntityIn(Rectangle r){
		ArrayList<Entity> array = new ArrayList<Entity>();
		for (Entity entity : entities){
			if (r.contains(new Point(entity.getCoordonnées().x, entity.getCoordonnées().y))){
				array.add(entity);
			}
		}
		
		Entity[] result = new Entity[array.size()];
		result = array.toArray(result);
		
		return result;
	}
	
	public void removeEntityIn(Rectangle r){
		ArrayList<Entity> var1 = new ArrayList<Entity>();
		
		for (Entity composant : entities){
			if (r.contains(new Point(composant.getCoordonnées().x, composant.getCoordonnées().y))
					&& !(composant instanceof PlayerDefault)){
				var1.add(composant);
			}
		}
		for (Entity var2 : var1){
			entities.remove(var2);
		}
	}
	
	public void removeEntity(Entity toRemove){
		if (!(toRemove instanceof PlayerDefault)) {
			this.entities.remove(toRemove);
		}
	}
	
	/**
	 * Vérifie le nombre de cases libres (c'est à dire les
	 * cases où le joueur peut aller).
	 * <p>Ce nombre est accessible par le champ {@link #permitCount}.
	 */
	private void checkRange() {
		this.permitCount = 0;
		
		for (LandscapeTile[] lands : this.decor) {
			for (LandscapeTile land : lands) {
				if (land.isEnabled()) this.permitCount++;
			}
		}
	}

	@Override
	public void code(BufferedObjectOutputStream out) throws GameIOException {
		out.writeIntField(this.decorType.ordinal(), 0);
		out.writeIntField(this.id, 1);
		
		out.writeCodableField(this.player, 2);
		out.writeArrayField(this.entities.toArray(new Entity[this.entities.size()]), 3);
		
		out.writeIntField(this.size.getWidth(), 4);
		out.writeIntField(this.size.getHeight(), 5);
		
		out.writeByteArrayField(landToByteArray(this.decor, this.size.getWidth(), this.size.getHeight()), 6);
		out.writeArrayField(getInfos(this.decor).toArray(new LandscapeInfos[0]), 7);
		
		out.writeStringField(this.name, 8);
		
		ArrayList<Integer> tagsID = new ArrayList<Integer>();
		for (RoomTag tag : this.tags) {
			tagsID.add(tag.getID());
		}
		out.writeArrayField(tagsID.toArray(new Integer[0]), 9);
	}

	@Override
	public Room decode(BufferedObjectInputStream in) throws GameIOException {
		
		this.decorType = DecorType.values()[in.readIntField(0)];
		this.id = in.readIntField(1);
		
		try {
			this.player = in.readCodableInstanceField(PlayerDefault.class, 2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			in.readListField(this.entities = new ArrayList<Entity>(), 3);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.size = new DimensionsInt(in.readIntField(4), in.readIntField(5));
		byte[] landscape = new byte[in.readArrayFieldLength(Byte.class, 6)];
		try {
			in.readByteArrayField(landscape, 6);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.decor = byteToLand(landscape, this.size.getWidth(), this.size.getHeight());
		
		//Lecture du décor
		List<LandscapeInfos> infos = new ArrayList<LandscapeInfos>();
		try {
			in.readListField(infos, 7);
		} catch (Exception e) {
			e.printStackTrace();
		}
		putInfos(this.decor, infos);
		
		this.checkRange();
		this.removePlayer();
		if (this.player != null) this.entities.add(this.player);
		
		//Nom de la salle
		this.name = in.readStringFieldWithDefaultValue(8, this.name);
		
		//tags
		try {
			ArrayList<Integer> tagsID = new ArrayList<Integer>();
			in.readListField(tagsID, 9);
			
			this.tags.clear();
			for (int tagID : tagsID) {
				this.tags.add(RoomTag.getByID(tagID));
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.tags = new ArrayList<RoomTag>();
		}
		
		return null;
	}
	
	/**
	 * Transforme un paysage en tableau de bytes, pour compresser les
	 * données lors d'une écriture vers un fichier.
	 * @param theLand - Le paysage.
	 * @param width - Sa largeur.
	 * @param height - Sa hauteur.
	 * @return Un tableau de byte représentant le paysage. Normalement,
	 * si ce tableau est passé à la méthode
	 * {@link Room#byteToLand(LandscapeTile[][], int, int)}
	 * avec les bons paramètres de largeur et de hauteur, on retrouve le
	 * paysage d'origine.
	 */
	static byte[] landToByteArray(LandscapeTile[][] theLand, int width, int height) {
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		DataOutputStream dto = new DataOutputStream(bao);
		
		for (int x = 0 ; x < width ; x++) {
			for (int y = 0 ; y < height ; y++) {
				try {			
					dto.writeByte(theLand[x][y].getID());
					dto.writeByte(theLand[x][y].getMetadata());			
				} catch (Throwable never) {}
			}
		}
		
		return bao.toByteArray();
	}
	
	/**
	 * Retrouve un paysage réel à partir de sa transcription dans un tableau
	 * de bytes. Attention, si de mauvais paramètres de largeur et de hauteur
	 * sont envoyés, la fonction rique de retourner un paysage érroné, voir
	 * déclencher une {@link ArrayIndexOutOfBoundsException}.
	 * @param data - Les données écrites avec la méthode
	 * {@link Room#landToByteArray(LandscapeTile[][], int, int)}, ou une méthode
	 * similaire.
	 * @param width - La largeur présumée du paysage.
	 * @param height - La hauteur présumée du paysage.
	 * @return Si tout s'est déroulé normalement, un tableau de
	 * {@link LandscapeTile} contenant le paysage codé à l'aide de la méthode
	 * {@link Room#landToByteArray(LandscapeTile[][], int, int)}.
	 */
	static LandscapeTile[][] byteToLand(byte[] data, int width, int height) {
		ByteArrayInputStream bai = new ByteArrayInputStream(data);
		DataInputStream dti = new DataInputStream(bai);
		LandscapeTile[][] result = new LandscapeTile[width][height];
		
		for (int x = 0 ; x < width ; x++) {
			for (int y = 0 ; y < height ; y++) {
				try {
					byte id = dti.readByte();
					byte meta = dti.readByte();
					result[x][y] = LandscapeTile.createLandscape(id, meta);
					result[x][y].setCoordonnéesCase(x, y);
				} catch (IOException never) {}
			}
		}
		
		return result;
	}
	
	/**
	 * Construit une liste d'information sur le décor à sérialiser.
	 * @param landscape - Le décor.
	 * @return La liste des informations supplémentaires à sérialiser.
	 */
	static List<LandscapeInfos> getInfos(LandscapeTile[][] landscape) {
		ArrayList<LandscapeInfos> list = new ArrayList<LandscapeInfos>();
		for (LandscapeTile[] array1 : landscape) {
			for (LandscapeTile l : array1) {
				if (l.needInfosObject()) {
					list.add(l.getInfos());
				}
			}
		}
		return list;
	}
	
	/**
	 * Restitue les informations supplémentaires, ajoutées lors de la sérialisation,
	 * au décor.
	 * @param landscape - Le décor.
	 * @param infos - La liste désérialisée contenant les informations nécessaires.
	 */
	static void putInfos(LandscapeTile[][] landscape, List<LandscapeInfos> infos) {
		for (LandscapeInfos info : infos) {
			Vec2i c = info.getLocation();
			landscape[c.x][c.y].accordTo(info);
		}
	}
}
