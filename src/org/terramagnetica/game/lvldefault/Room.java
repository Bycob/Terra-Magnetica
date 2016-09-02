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
 * Une {@link Room} repr�sente un �tage d'un niveau de type {@link
 * LevelDefault}. De nombreuses m�thodes permettent de la modifier,
 * en revanche, pour le jeu � proprement parler, voir la classe
 * {@link GamePlayingDefault}.
 * 
 * <p>La position d'origine du joueur est g�r�e automatiquement par l'�tage.
 * Lorsque le joueur ne peut pas �tre plac� au sein de l'�tage, il
 * disparait. D�s lors, d�s qu'une nouvelle modification permet au 
 * joueur d'�tre plac� quelque part, il y est plac� automatiquement.
 * Si le joueur ne peut rester � sa place, suite � une modification
 * du d�cor par exemple, il est automatiquement d�plac�.
 * @author Louis JEAN
 */
public class Room implements Serializable, Cloneable, Codable {
	
	/**
	 * Retourne une version optimis�e de la salle en param�tre.
	 * <p>Tous les secteurs inutilis�s sont supprim�s pour r�duire
	 * la place qu'occupe l'�tage en m�moire.
	 * <p>Attention, la salle retourn�e est pr�vue pour �tre utilis�e � la
	 * place de celle pass�e en param�tre. Toutes les entit�s, unit�s de
	 * paysages ect... ne seront pas clon�s (Si on modifie les entit�s de
	 * la salle d'origine cela aura des r�percussions sur les entit�s de la
	 * salle ainsi cr��e).
	 * @param r - La salle � optimiser
	 * @return Une version optimis�e de la salle, plus petite.
	 * @throws UnusedRoomException Si la salle enti�re n'est pas utilis�e.
	 */
	public static Room scaledRoom(Room r) throws UnusedRoomException {
		if (r == null) return null;
		
		//ETAPE 1 : d�termination des bords de la nouvelle salle.
		
		RectangleInt bounds = new RectangleInt(
				0, 0,
				r.getDimensions().getWidth() - 1,
				r.getDimensions().getHeight() - 1);
		LandscapeTile[][] d�cor = r.getLandscape();
		
		for (int i = 0 ; i <= bounds.xmax ; i++) {
			bounds.xmin = i;
			if (!isLineUnused(d�cor, i, true)) {
				break;
			}
		}
		
		if (bounds.xmin == bounds.xmax) throw new UnusedRoomException();
		
		for (int i = bounds.xmax; i >= bounds.xmin; i--) {
			if (!isLineUnused(d�cor, i, true)) {
				bounds.xmax = i;
				break;
			}
		}
		
		for (int i = 0 ; i <= bounds.ymax ; i++) {
			if (!isLineUnused(d�cor, i, false)) {
				bounds.ymin = i;
				break;
			}
		}
		
		for (int i = bounds.ymax ; i >= bounds.ymin ; i--) {
			if (!isLineUnused(d�cor, i, false)) {
				bounds.ymax = i;
				break;
			}
		}
		
		//ETAPE 2 : cr�ation d'une nouvelle salle, adaptation des param�tres.
		
		Room result = new Room(r.getDecorType(), bounds.getWidth() + 1, bounds.getHeight() + 1);
		
		for (int i = 0 ; i < result.getDimensions().getWidth() ; i++) {
			for (int j = 0 ; j < result.getDimensions().getHeight() ; j++) {
				result.decor[i][j] = r.getLandscapeAt(i + bounds.xmin, j + bounds.ymin);
				result.decor[i][j].setCoordonn�esCase(i, j);
			}
		}
		
		result.checkRange();
		r.removePlayer();
		
		for (Entity e : r.entities) {
			Vec2f c = e.getCoordonn�esf();
			e.setCoordonn�esf(c.x - bounds.xmin, c.y - bounds.ymin);
			result.entities.add(e);
		}
		
		Vec2f cperso = r.player.getCoordonn�esf();
		r.player.setCoordonn�esf(cperso.x - bounds.xmin, cperso.y - bounds.ymin);
		result.setPlayer(r.player);
		
		result.id = r.id;
		
		return result;
	}
	
	/** usage interne : {@link Room#scaledRoom(Room)} : permet de v�rifier si
	 * une ligne ne contient que du paysage inaccessible (si oui on peut donc
	 * la supprimer. En passant le param�tre {@code column} � {@code true}
	 * on peut faire la m�me chose avec les colonnes.
	 * <br>d�clenche une erreur si le d�cor ne contient rien, si l'indice est
	 * plus grand que le d�cor. */
	private static boolean isLineUnused(LandscapeTile[][] d�cor, int index, boolean column) {
		int max = column ? d�cor[0].length : d�cor.length;
		for (int i = 0 ; i < max ; i++) {
			LandscapeTile tested = column ? d�cor[index][i] : d�cor[i][index];
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
	 * Agrandit une salle selon les param�tres.
	 * <p>Attention, impossible de r�tr�cir la salle en passant des param�tres
	 * n�gatifs.
	 * <p>La salle pass�e en param�tre devient inutilisable apr�s l'appel
	 * de cette fonction. Il est donc pr�f�rable d'utiliser la nouvelle salle
	 * retourn�e.
	 * @param r - la salle � agrandir.
	 * @param top - le nombre de lignes � ajouter en haut.
	 * @param bottom - le nombre de lignes � ajouter en bas.
	 * @param left - le nombre de colonnes � ajouter � gauche.
	 * @param right - le nombre de colonnes � ajouter � droite.
	 * @return Une salle qui est la copie du mod�le original, mais agrandie.
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
				result.decor[i + left][j + top].setCoordonn�esCase(i + left, j + top);
			}
		}
		
		result.checkRange();
		r.removePlayer();
		
		for (Entity e : r.entities) {
			Vec2f c = e.getCoordonn�esf();
			e.setCoordonn�esf(c.x + left, c.y + top);
			result.entities.add(e);
		}
		
		if (r.player != null) {
			Vec2f cperso = r.player.getCoordonn�esf();
			r.player.setCoordonn�esf(cperso.x + left, cperso.y + top);
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
	
	/** constructeur par d�fault d'une salle, est appel� lors d'une cr�ation de niveau */
	public Room(){
		this(DecorType.MONTS);
	}
	
	public Room(DecorType decorType){
		this(decorType, TAILLE_DEFAULT, TAILLE_DEFAULT);
	}
	
	/**
	 * Cr�e une nouvelle salle avec les param�tres indiqu�s. Cette salle
	 * sera remplie avec du d�cor "inaccessible".
	 * @param decorType - Le type de d�cor de la salle.
	 * @param width - La largeur de la salle
	 * @param height - La hauteur de la salle
	 */
	public Room(DecorType decorType, int width, int height) {
		this.entities = new ArrayList<Entity>();
		this.decor = new LandscapeTile[width][height];
		this.size = new DimensionsInt(width, height);
		this.decorType = decorType;
		
		//initialisation du d�cor
		for (int i = 0 ; i < decor.length ; i++){
			for (int j = 0 ; j < decor[i].length ; j++){
				decor[i][j] = new WallTile(i, j, WallTile.PLANE);
			}
		}
	}
	
	/**
	 * constructeur avec des param�tres.
	 * @param entities - la liste des entit�s de ce niveau
	 * @param decor - le d�cor du niveau
	 * @param perso - le personnage
	 * @param decorType - le type de d�cor
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
	 * L'id de la salle, utilis� par les portails.
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
	
	/** Donne le nom de la salle tel qu'il a �t� donn� par son cr�ateur. */
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
	 * <li><b>LIMITED_VISION</b> : la salle est en mode de vision limit�e
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
	
	/**Ajoute un tag � la salle.
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
	 * retourne l'unit� de paysage pr�sente aux coordonn�es (x ; y)
	 * @param x - L'abscisse.
	 * @param y - L'ordonn�e.
	 * @return L'unit� de paysage � l'emplacement indiqu�, ou <code>
	 * null</code> si elle n'existe pas.
	 */
	public LandscapeTile getLandscapeAt(int x, int y){
		if (x < 0 || x >= decor.length || y < 0 || y >= decor[0].length){
			return null;
		}
		return decor[x][y];
	}
	
	/**
	 * Donne un tableau des unit�s de d�cor autour du point indiqu�, dans
	 * l'ordre : <i>NORD, EST, SUD, OUEST</i>.
	 * @param x - abscisse du point indiqu�.
	 * @param y - ordonn�e du point indiqu�.
	 * @return Un tableau de quatre unit�s de d�cor.
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
	 * D�fini le d�cor � la case de cordonn�es (x, y)
	 * @param newCase - le nouveau d�cor. Il sera clon� puis
	 * ses coordonn�es mises � jour.
	 */
	public void setLandscapeAt(int x, int y, LandscapeTile newCase){
		LandscapeTile oldCase = this.decor[x][y];		
		this.decor[x][y] = newCase.clone();
		this.decor[x][y].setCoordonn�esCase(x, y);
		
		if (newCase.isEnabled() && !(oldCase.isEnabled())) {
			this.permitCount++;
			if (this.permitCount == 1) {
				this.player = new PlayerDefault();
				this.player.setCoordonn�esCase(x, y);
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
	 * @param caseY - ordonn�e de la case.
	 */
	public void setPlayerLocation(int caseX, int caseY) {
		if (this.player == null) return;
		Vec2i ol = this.player.getCoordonn�esCase().clone();
		this.player.setCoordonn�esCase(caseX, caseY);
		
		if (!checkPlayerLocation(this.player)) {
			this.player.setCoordonn�esCase(ol.x, ol.y);
		}
		
		if (!this.entities.contains(this.player)) {
			this.removePlayer();
			this.entities.add(this.player);
		}
	}
	
	/**
	 * V�rifie que le personnage envoy� peut �tre plac�
	 * dans la salle, sans aucune modification de ses coordonn�es. 
	 * @param perso - Le personnage � v�rifier.
	 * @return Si le personnage peut �tre le nouveau personnage de
	 * la salle.
	 */
	private boolean checkPlayerLocation(PlayerDefault perso) {
		Vec2i checkPoint = perso.getCoordonn�esCase();
		return this.decor[checkPoint.x][checkPoint.y].isEnabled();
	}
	
	/** Retire l'entit� "joueur" de la liste des entit�s du jeu. */
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
					Vec2i c = land.getCoordonn�esCase();
					this.player.setCoordonn�esCase(c.x, c.y);
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
	 * Ajoute une entit� en fonction de sa priorit� sur le dessin.
	 * @param entitee - La nouvelle entit�.
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
			Vec2i origine = composant.getCoordonn�es();
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
	
	/** Supprime et retourne l'entit� � l'endroit indiqu�, sauf s'il s'agit du
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
				Vec2i cec = ce.getCoordonn�esCase();
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
			if (r.contains(new Point(entity.getCoordonn�es().x, entity.getCoordonn�es().y))){
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
			if (r.contains(new Point(composant.getCoordonn�es().x, composant.getCoordonn�es().y))
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
	 * V�rifie le nombre de cases libres (c'est � dire les
	 * cases o� le joueur peut aller).
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
		
		//Lecture du d�cor
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
	 * donn�es lors d'une �criture vers un fichier.
	 * @param theLand - Le paysage.
	 * @param width - Sa largeur.
	 * @param height - Sa hauteur.
	 * @return Un tableau de byte repr�sentant le paysage. Normalement,
	 * si ce tableau est pass� � la m�thode
	 * {@link Room#byteToLand(LandscapeTile[][], int, int)}
	 * avec les bons param�tres de largeur et de hauteur, on retrouve le
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
	 * Retrouve un paysage r�el � partir de sa transcription dans un tableau
	 * de bytes. Attention, si de mauvais param�tres de largeur et de hauteur
	 * sont envoy�s, la fonction rique de retourner un paysage �rron�, voir
	 * d�clencher une {@link ArrayIndexOutOfBoundsException}.
	 * @param data - Les donn�es �crites avec la m�thode
	 * {@link Room#landToByteArray(LandscapeTile[][], int, int)}, ou une m�thode
	 * similaire.
	 * @param width - La largeur pr�sum�e du paysage.
	 * @param height - La hauteur pr�sum�e du paysage.
	 * @return Si tout s'est d�roul� normalement, un tableau de
	 * {@link LandscapeTile} contenant le paysage cod� � l'aide de la m�thode
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
					result[x][y].setCoordonn�esCase(x, y);
				} catch (IOException never) {}
			}
		}
		
		return result;
	}
	
	/**
	 * Construit une liste d'information sur le d�cor � s�rialiser.
	 * @param landscape - Le d�cor.
	 * @return La liste des informations suppl�mentaires � s�rialiser.
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
	 * Restitue les informations suppl�mentaires, ajout�es lors de la s�rialisation,
	 * au d�cor.
	 * @param landscape - Le d�cor.
	 * @param infos - La liste d�s�rialis�e contenant les informations n�cessaires.
	 */
	static void putInfos(LandscapeTile[][] landscape, List<LandscapeInfos> infos) {
		for (LandscapeInfos info : infos) {
			Vec2i c = info.getLocation();
			landscape[c.x][c.y].accordTo(info);
		}
	}
}
