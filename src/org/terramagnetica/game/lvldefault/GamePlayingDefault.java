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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.terramagnetica.game.GameEngine;
import org.terramagnetica.game.GameRessources;
import org.terramagnetica.game.lvldefault.Room.RoomTag;
import org.terramagnetica.game.lvldefault.rendering.GameRenderingDefault;
import org.terramagnetica.game.lvldefault.rendering.RenderLandscape;
import org.terramagnetica.opengl.miscellaneous.Timer;
import org.terramagnetica.ressources.TextureSet;
import org.terramagnetica.ressources.io.BufferedObjectInputStream;
import org.terramagnetica.ressources.io.BufferedObjectOutputStream;
import org.terramagnetica.ressources.io.GameIOException;

import net.bynaryscode.util.maths.geometric.DimensionsInt;
import net.bynaryscode.util.maths.geometric.Vec2i;

/**
 * {@link GamePlayingDefault} est le type de moteur de jeu qui permet
 * de jouer des niveaux de type {@link LevelDefault}. Il met à jour,
 * automatiquement ou par l'appel de la fonction {@link #update(long)},
 * les composants du jeu, leurs déplacements, interactions, les entrées
 * provenant du joueur...<p>
 * On peut y ajouter des modules qui modifient certains élements du
 * gameplay via la méthode {@link #addAspect(GameAspect)}.
 * @author Louis JEAN
 */
public class GamePlayingDefault extends GameEngine implements Cloneable {
	
	private static final long serialVersionUID = 1L;
	
	/** La distance maximale au joueur  */
	public static final float UPDATE_DISTANCE = 50;
	
	private LevelDefault level;
	private int currentRoom = -1;
	private DecorType decorType;
	private List<Entity> entities;
	private LandscapeTile[][] landscape;
	private PlayerDefault player;
	
	private boolean limitedVision = false;
	
	public final transient GameBufferDefault buffer = new GameBufferDefault();
	public final transient GameRenderingDefault render = new GameRenderingDefault(this);
	private final PhysicEngine physics = new PhysicEngine();
	
	private transient long time;
	/** Indique que le moteur de jeu n'a pas encore été mis à jour. */
	private boolean hasntYetStarted = true;
	
	private transient Set<GameAspect> moduleList = new HashSet<GameAspect>();
	
	private ArrayList<GameEvent> eventOnChangingRoom = new ArrayList<GameEvent>();
	private CheckPoint checkPoint = null;
	
	public GamePlayingDefault(){
		this(new LevelDefault());
	}
	
	/**
	 * Construit un jeu de la pièce principale d'un niveau.
	 * @param lvl - le niveau qui sert à construire le jeu.
	 */
	public GamePlayingDefault(LevelDefault lvl){
		this.setNiveau(lvl);
	}
	
	//accesseurs
	/**
	 * Modifie le niveau de jeu de cet objet, et, par la même occasion,
	 * le réinitialise.
	 * @param lvl - Le nouveau niveau.
	 */
	public void setNiveau(LevelDefault lvl) {
		if (lvl == null) throw new NullPointerException("lvl == null");
		
		if (!this.running) {
			
			this.time = 0;
			this.hasntYetStarted = true;
			
			this.level = lvl.clone();
			this.setRoom(this.level.getMainRoom());
			
			this.isGameOver = false;
			this.hasWon = false;
		}
		else {
			this.gameRunningError();
		}
	}
	
	/**
	 * Définit la salle du jeu.
	 * @param room - la nouvelle salle.
	 */
	protected void setRoom(Room room) {
		if (this.running) this.gameRunningError();
		
		int oldRoomID = this.currentRoom;
		
		Room r = room.clone();
		
		this.entities = r.getEntities();
		this.landscape = r.getLandscape();
		this.decorType = r.getDecorType();
		this.currentRoom = r.getID();
		
		//gestion du personnage
		this.player = r.getPlayer();
		this.removePlayer();
		this.entities.add(this.player);
		this.player.updateTrackPoint();
		
		for (GameAspect aspect : this.moduleList) {
			aspect.init();
		}
		
		//tags
		if (oldRoomID != -1) applyTags();
		
		//déclenche tous les evènements qui doivent se passer lors du changement de salle.
		for (GameEvent gevt : this.eventOnChangingRoom) {
			gevt.trigger(this);
		}
		this.eventOnChangingRoom.clear();
		
		this.protectFromDeath = false;
		
		this.render.onChangingRoom();
	}
	
	@Override
	public LevelDefault getLevel(){
		return level;
	}
	
	public int getRoomID() {
		return this.currentRoom;
	}
	
	public String getRoomName(int index) {
		Room r = this.level.getRoom(index);
		if (r == null) return "";
		return r.getInGameName();
	}

	public List<Entity> getEntities() {
		return entities;
	}
	
	public CaseEntity getCaseEntityAt(int x, int y) {
		for (Entity e : this.entities) {
			if (e instanceof CaseEntity) {
				CaseEntity ce = (CaseEntity) e;
				Vec2i cec = ce.getCoordonnéesCase();
				
				if (cec.matches(x, y)) {
					return ce;
				}
			}
		}
		
		return null;
	}
	
	public CaseEntity getCaseEntityAt(Vec2i location) {
		return getCaseEntityAt(location.x, location.y);
	}
	
	public Entity[] getEntitiesOnCase(int x, int y) {
		ArrayList<Entity> result = new ArrayList<Entity>();
		
		for (Entity e : this.entities) {
			Vec2i c = e.getCoordonnéesCase();
			
			if (c.matches(x, y)) {
				result.add(e);
			}
		}
		
		return result.toArray(new Entity[0]);
	}

	public void setEntities(ArrayList<Entity> composants) {
		if (!running)
			this.entities = composants;
		else {
			this.gameRunningError();
		}
	}
	
	private ArrayList<Entity> entitiesToAdd = new ArrayList<Entity>();
	private ArrayList<Entity> entitiesToRemove = new ArrayList<Entity>();
	
	public void addEntity(Entity e) {
		if (this.running) {
			this.entitiesToAdd.add(e);
		}
		else {
			this.entities.add(e);
		}
	}
	
	public void removeEntity(Entity e) {
		if (this.running) {
			this.entitiesToRemove.add(e);
		}
		else {
			this.entities.remove(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T> T findEntity(Class<T> type) {
		for (Entity e : this.entities) {
			if (type.isAssignableFrom(e.getClass())) {
				return (T) e;
			}
		}
		return null;
	}
	
	public Mark findMark(String id) {
		for (Entity e : this.entities) {
			if (e instanceof Mark && e.getSkin().equals(id)) {
				return (Mark) e;
			}
		}
		
		return null;
	}
	
	public DimensionsInt getLandscapeDimensions() {
		return new DimensionsInt(landscape.length, landscape[0].length);
	}
	
	public LandscapeTile[][] getLandscape() {
		return landscape;
	}
	
	public LandscapeTile getLandscapeAt(int x, int y){
		if (x < 0 || x >= landscape.length || y < 0 || y >= landscape[0].length){
			return new WallTile(x, y, WallTile.PLANE);
		}
		return landscape[x][y];
	}
	
	/**
	 * Donne les paysages les plus proches du point indiqué, aux quatres
	 * points cardinaux, dans l'ordre : <i>NORD, EST, SUD, OUEST</i>.
	 * (C'est à dire, en partant du Nord, et en tournant dans le sens des
	 * aiguilles d'une montre.)
	 * <p>Le point passé en paramètre correspond au coordonnées d'une pièce
	 * de décor (coordonnées entiers et en cases).
	 * @param x - l'abscisse du point indiqué, en cases.
	 * @param y - l'ordonnée du point indiqué, en cases.
	 * @return
	 */
	public LandscapeTile[] getLandscapeAround(int x, int y) {
		LandscapeTile[] result = {
				getLandscapeAt(x, y - 1),
				getLandscapeAt(x + 1, y),
				getLandscapeAt(x, y + 1),
				getLandscapeAt(x - 1, y)};
		
		return result;
	}
	
	public void setLandscape(LandscapeTile[][] decor) {
		if (!this.running)
			this.landscape = decor;
		else {
			this.gameRunningError();
		}
	}
	
	public PlayerDefault getPlayer() {
		return player;
	}

	public void setPlayer(PlayerDefault perso) {
		if (!running) {
			this.removePlayer();
			this.player = perso;
			this.entities.add(this.player);
		}
		else {
			this.gameRunningError();
		}
	}
	
	private void removePlayer() {
		for (int i = 0 ; i < this.entities.size() ; i++) {
			if (this.entities.get(i) instanceof PlayerDefault) {
				this.entities.remove(i);
				i--;
			}
		}
	}
	
	public DecorType getDecorType() {
		return decorType;
	}

	public void setDecorType(DecorType decorType) {
		if (!this.running) {
			this.decorType = decorType;
		}
		else {
			this.gameRunningError();
		}
	}
	
	public void setLimitedVision(boolean b) {
		this.limitedVision = b;
		
		MapUpdater updater;
		if ((updater = this.getAspect(MapUpdater.class)) != null) {
			updater.setMaxDistance(
					b ? MapUpdater.MAX_DISTANCE_LIMITED : MapUpdater.MAX_DISTANCE_NOT_LIMITED);
		}	
	}
	
	public boolean hasLimitedVision() {
		return this.limitedVision;
	}
	
	/**
	 * @return Le temps qui s'est écoulé depuis que le niveau a été
	 * commencé par le joueur, en ms.
	 */
	public long getTime() {
		return this.time;
	}
	
	
	public void setCheckPoint(CheckPoint checkPoint) {
		this.checkPoint = checkPoint;
	}
	
	public CheckPoint getCheckPoint() {
		return this.checkPoint;
	}
	
	/**
	 * Donne l'aspect du jeu correspondant à la classe passé en paramètre.
	 * @param type
	 * @return jamais <code>null</code>
	 * @throws NullPointerException si l'aspect du jeu correspondant n'existe pas
	 * en tant qu'objet.
	 */
	@SuppressWarnings("unchecked")
	public <T extends GameAspect> T getAspect(Class<T> type) {
		for (GameAspect aspect : this.moduleList) {
			if (aspect.getClass().equals(type)) {
				return (T) aspect;
			}
		}
		throw new NullPointerException("aspect inconnu");
	}
	
	/**
	 * Ajoute un aspect particulier au jeu, que pourront utiliser
	 * les entités diverses, lors de leur mise à jour.
	 * <br>Normalement, cette méthode est appellée à la création de
	 * cet objet, par le niveau qui le génère.
	 * @param aspect - un module de jeu avec une mission particulière,
	 * à ajouter au moteur de jeu.
	 */
	public void addAspect(GameAspect aspect) {
		if (aspect != null) {
			for (GameAspect principe : this.moduleList) {
				if (principe.getClass().equals(aspect.getClass())) {
					this.moduleList.remove(principe);
					break;
				}
			}
			aspect.setGame(this);
			aspect.init();
			this.moduleList.add(aspect);
		}
	}
	
	/**
	 * 
	 * @deprecated On préfèrera <tt>getAspect(LampState.class).getLampState()</tt>
	 * @return
	 */
	public boolean getLampState() {
		return getAspect(LampState.class).getLampState();
	}
	
	/** Dans certains cas très particuliers, le joueur ne peut
	 * pas mourrir. Cette variable est alors à true. */
	private boolean protectFromDeath = false;
	
	/** Tue le joueur et stoppe le moteur de jeu. */
	public void setDead() {
		if (PlayerDefault.DBG_INVINCIBLE || this.protectFromDeath) return;
		this.player.onDeath();
		
		this.isGameOver = true;
	}
	
	/** Gagne le niveau et stoppe le moteur de jeu. */
	public void setEnd() {
		this.hasWon = true;
	}
	
	/** Fait apparaitre le joueur dans la salle indiquée. */
	public void goToRoom(int room) {
		Room r = this.level.getRoom(room);
		if (r != null && this.currentRoom != r.getID()) {
			this.render.interruptGame(new InterruptionChangeRoom(this, room));
		}
		
		//Le joueur ne meurt pas au cours de ce tour.
		this.protectFromDeath = true;
		this.isGameOver = false;
	}
	
	/** Déplace le joueur au dernier checkpoint enregistré. */
	public void respawnOnCheckPoint() {
		if (this.checkPoint == null) return;
		
		this.setRoom(this.level.getRoom(this.checkPoint.getRoomID()));
		Vec2i loc = this.checkPoint.getLocation();
		this.player.setCoordonnéesCase(loc.x, loc.y);
	}
	
	public void addEventOnChangingRoom(GameEvent evt) {
		this.eventOnChangingRoom.add(evt);
	}
	
	@Override
	protected void update(long delta) {
		this.time += delta;
		
		if (this.hasntYetStarted) {
			this.hasntYetStarted = false;
			this.render.interruptGame(new InterruptionChangeRoom(this, this.currentRoom, true));
		}
		
		//Mise à jour des mouvements d'entités
		for (Entity entity : this.entities) {
			if (entity instanceof EntityMoving) {
				EntityMoving entMov = (EntityMoving) entity;
				entMov.updatePhysic(delta, this);
			}
		}
		//Mise à jour des entités
		for (Entity entity : this.entities) {
			entity.updateLogic(delta, this);
		}
		
		//Préparation des entités pour le tour prochain
		for (Entity entity : this.entities) {
			entity.updateLastHitbox();
			entity.updated = false;
		}
		
		//Mise à jour des différents modules
		for (GameAspect aspect : this.moduleList) {
			aspect.update(delta);
		}
		
		//Traitement supplémentaires
		for (Entity entity : this.entitiesToAdd) {
			this.entities.add(entity);
		}
		this.entitiesToAdd.clear();
		for (Entity entity : this.entitiesToRemove) {
			this.entities.remove(entity);
		}
		this.entitiesToRemove.clear();
	}
	
	@Override
	public GameBufferDefault getBuffer() {
		return this.buffer;
	}
	
	@Override
	public GameRenderingDefault getRender() {
		return this.render;
	}
	
	@Override
	public void recreateRenders() {
		for (LandscapeTile[] array1 : this.landscape) {
			for (LandscapeTile land : array1) {
				land.removeAllRenders();
			}
		}
		
		for (Entity e : this.entities) {
			e.reloadRender();
		}
		
		RenderLandscape.destroyAllLists();
	}
	
	@Override
	public List<TextureSet> getTextures() {
		ArrayList<TextureSet> result = new ArrayList<TextureSet>();
		
		result.add(GameRessources.gameTextureSet);
		for (Room r : this.level.getRoomList()) {
			result.add(GameRessources.getTextureSetByLandscape(r.getDecorType()));
		}
		
		if (this.level.levelID != 0) {
			TextureSet levelTexSet = GameRessources.getTextureSetByLevel(this.level.levelID);
			if (levelTexSet != null) result.add(levelTexSet);
		}
		
		return result;
	}
	
	@Override
	public List<String> getModels() {
		ArrayList<String> result = new ArrayList<String>();
		
		for (Room r : this.level.getRoomList()) {
			result.addAll(GameRessources.getModelSetByLandscape(r.getDecorType()));
		}
		if (this.level.levelID != 0) {
			result.addAll(GameRessources.getModelSetByLevel(this.level.levelID));
		}
		
		return result;
	}
	
	@Override
	public void startGame() {
		super.startGame();
		
		this.applyTags();
	}
	
	protected void applyTags() {
		setLimitedVision(false);
		
		for (RoomTag tag : this.level.getRoom(this.currentRoom).getTags()) {
			
			switch (tag) {
			case LAMP_STATE_MODE_3 :
				getAspect(LampState.class).setLampState_LikeHell();
				break;
			case LIMITED_VISION :
				this.setLimitedVision(true);
				break;
			}
		}
	}
	
	@Override
	public void stop() {
		super.stop();
	}
	
	@Override
	public void resume() {
		super.startGame();
	}
	
	@Override
	public void respawn() {
		this.setNiveau(this.level);
		
		if (this.checkPoint != null) {
			respawnOnCheckPoint();
		}
	}
	
	@Override
	public GamePlayingDefault clone(){
		GamePlayingDefault result = null;
		result = (GamePlayingDefault) super.clone();
		
		result.level = this.level.clone();
		
		result.entities = new ArrayList<Entity>();
		for (Entity entity : entities) {
			result.entities.add(entity.clone());
		}
		
		result.landscape = landscape.clone();
		
		for (int x = 0 ; x < result.landscape.length ; x++) {
			for (int y = 0 ; y < result.landscape[0].length ; y++) {
				result.landscape[x][y] = result.landscape[x][y].clone();
			}
		}
		
		result.removePlayer();
		result.entities.add(result.player);
		
		result.moduleList = new HashSet<GameAspect>();
		
		for (GameAspect aspect : this.moduleList) {
			result.moduleList.add(aspect.clone());
		}
		
		return result;
	}
	
	@Override
	public void code(BufferedObjectOutputStream out) throws GameIOException {
		//ne sont plus utilisés : 100 à 102
		out.writeLongField(this.time, 103);
		
		out.writeIntField(this.decorType.ordinal(), 104);
		out.writeCodableField(this.level, 105);
		//106 n'est plus utilisé
		out.writeIntField(this.landscape.length, 107);
		out.writeIntField(this.landscape[0].length, 108);
		out.writeByteArrayField(
				Room.landToByteArray(this.landscape, this.landscape.length, this.landscape[0].length), 109);
		out.writeArrayField(this.entities.toArray(new Entity[this.entities.size()]), 110);
		
		out.writeArrayField(this.codablePrincipList().toArray(new GameAspect[0]), 111);
		
		out.writeArrayField(Room.getInfos(this.landscape).toArray(new LandscapeInfos[0]), 112);
		
		if (this.checkPoint != null) {
			Vec2i location = this.checkPoint.getLocation();
			out.writeIntField(location.x, 113); out.writeIntField(location.y, 114);
			out.writeIntField(this.checkPoint.getRoomID(), 115);
		}
	}
	
	private List<GameAspect> codablePrincipList() {
		List<GameAspect> result = new ArrayList<GameAspect>();
		for (GameAspect aspect : this.moduleList) {
			if (aspect.shouldSave()) {
				result.add(aspect);
			}
		}
		return result;
	}
	
	@Override
	public GamePlayingDefault decode(BufferedObjectInputStream in) throws GameIOException {
		getAspect(LampState.class).setLampState(in.readBoolFieldWithDefaultValue(100, false));
		
		//plus utilisé : 101 et 102
		//Temps [103]
		try {
			this.time = in.readLongField(103);
		}
		catch (GameIOException e) {//Support de l'ancienne version
			Timer timer = new Timer(); in.readCodableField(timer, 103);
			this.time = timer.getTime();
		}
		
		this.decorType = DecorType.values()[in.readIntField(104)];
		
		//Niveau [105]
		try {
			this.level = in.readCodableInstanceField(LevelDefault.class, 105);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Plus utilisé : 106
		
		//décor [107-109 + 7]
		//taille
		int width = in.readIntField(107);
		int height = in.readIntField(108);
		//décor
		byte[] landscape = new byte[in.readArrayFieldLength(Byte.class, 109)];
		try {
			in.readByteArrayField(landscape, 109);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.landscape = Room.byteToLand(landscape, width, height);
		//infos sur le décor
		List<LandscapeInfos> infos = new ArrayList<LandscapeInfos>();
		try {
			in.readListField(infos, 114);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Room.putInfos(this.landscape, infos);
		
		//entités [110]
		try {
			in.readListField(this.entities = new ArrayList<Entity>(), 110);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (Entity e : this.entities) {
			if (e instanceof PlayerDefault) {
				this.player = (PlayerDefault) e;
				break;
			}
		}
		
		//Modules [111]
		try {
			List<GameAspect> aspects = new ArrayList<GameAspect>();
			in.readListField(aspects, 111);
			for (GameAspect aspect : aspects) {
				try {
					GameAspect here = this.getAspect(aspect.getClass());
					this.moduleList.remove(here);
					this.moduleList.add(aspect);
					aspect.setGame(this);
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (in.hasField(113)) {
			this.checkPoint = new CheckPoint(in.readIntField(113), in.readIntField(114), in.readIntField(115));
		}
		
		return this;
	}
}
