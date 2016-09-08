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
import java.io.Serializable;
import java.util.ArrayList;

import org.terramagnetica.game.GameInputBuffer;
import org.terramagnetica.game.GameInputBuffer.InputKey;
import org.terramagnetica.game.GameRessources;
import org.terramagnetica.game.lvldefault.rendering.CameraTrackPoint3D;
import org.terramagnetica.game.lvldefault.rendering.RenderObject;
import org.terramagnetica.game.lvldefault.rendering.RenderEntityAnimatedTexture;
import org.terramagnetica.opengl.engine.TextureQuad;
import org.terramagnetica.opengl.miscellaneous.AnimationManager;
import org.terramagnetica.physics.Hitbox;
import org.terramagnetica.physics.HitboxCircle;
import org.terramagnetica.ressources.ImagesLoader;
import org.terramagnetica.ressources.SoundManager;
import org.terramagnetica.ressources.TexturesLoader;

import net.bynaryscode.util.maths.geometric.DimensionsInt;
import net.bynaryscode.util.maths.geometric.Vec2f;

public class PlayerDefault extends EntityMoving implements Serializable, PlayerStates {
	
	private static final long serialVersionUID = 1L;
	
	//Hitbox
	private static final int HEIGHT = 128;
	private static final int WIDTH = 128;
	
	//Image
	private static final int IMG_WIDTH = 192;
	private static final int IMG_HEIGHT = 288;
	
	private static final double SCALE = 0.8;
	
	//AUTRE
	static final boolean DBG_INVINCIBLE = false;
	
	/** La vitesse normale du joueur. (case/s)*/
	public static final float DEFAULT_VELOCITY = DBG_INVINCIBLE ? 10f : 3f;
	/** La vitesse des aimants pour tuer d'un seul coup. (case/s) */
	public static final float CRYSTAL_KILL = 8f;
	
	
	
	private AnimationManager textures;
	
	private ArrayList<Bonus> bonus = new ArrayList<Bonus>();
	
	public PlayerDefault(){
		super();
		init();
	}
	
	public PlayerDefault(int x, int y){
		super(x,y);
		init();
	}
	
	private void init() {
		this.hitbox.setBounceWeighting(0, 1);
		this.hitbox.setBounce(1f);
		this.updateTrackPoint();
	}
	
	//----------------------- CAMERA ----------------------
	
	private CameraTrackPoint3D trackPoint = new CameraTrackPoint3D();
	
	/**
	 * Donne un {@link CameraTrackPoint3D} qui suit le joueur et indique
	 * ainsi à la caméra où elle doit regarder.
	 * @return Un point mobile qui suit le joueur et qui sert de point de
	 * repère à la camera.
	 */
	public CameraTrackPoint3D getCameraTrackPoint() {
		return this.trackPoint;
	}
	
	protected void updateTrackPoint() {
		if (this.trackPoint != null) {
			Vec2f c = getPositionf();
			this.trackPoint.set(c.x, c.y, 0);
		}
	}
	
	//-----
	
	@Override
	public Image getImage(){
		return ImagesLoader.get(GameRessources.PATH_PLAYER);
	}
	
	@Override
	public TextureQuad getMinimapIcon() {
		return TexturesLoader.getQuad(GameRessources.ID_MAP_PLAYER);
	}
	
	@Override
	public RenderObject createRender() {
		if (this.textures == null) {
			this.textures = new AnimationManager();
			this.textures.addState(TexturesLoader.getAnimatedTexture(GameRessources.ID_PLAYER_STANDING));
		}
		return new RenderEntityAnimatedTexture(this.textures).withScale(SCALE, SCALE);
	}
	
	@Override
	public void reloadRender() {
		this.textures = null;
		super.reloadRender();
	}
	
	@Override
	public void setPositionf(float x, float y) {
		super.setPositionf(x, y);
		
		this.updateTrackPoint();
	}
	
	@Override
	public DimensionsInt getDimensions() {
		return new DimensionsInt(WIDTH, HEIGHT);
	}
	
	@Override
	public Hitbox createHitbox() {
		return new HitboxCircle(0.25f);
	}
	
	@Override
	public DimensionsInt getImgDimensions(){
		return new DimensionsInt(IMG_WIDTH, IMG_HEIGHT);
	}
	
	@Override
	public boolean canPassVirtualWall() {
		return true;
	}
	
	//----------------------- BONUS -----------------------
	
	/**
	 * Donne une copie de la liste des bonus du jeu. On peut donc
	 * parcourir cette copie et en même temps supprimer ou ajouter
	 * des objets sur l'originale.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private ArrayList<Bonus> getBonusListShallowCopy() {
		return (ArrayList<Bonus>) this.bonus.clone();
	}

	public void addBonus(Bonus bonus) {
		this.bonus.add(bonus);
		bonus.setPlayer(this);
	}

	public void removeBonus(Bonus bonus) {
		if (this.bonus.remove(bonus)) {
			bonus.setPlayer(null);
		}
	}

	public ArrayList<Bonus> getBonusList() {
		return getBonusListShallowCopy();
	}

	public void attack(float damages, GamePlayingDefault game) {
		ArrayList<Bonus> bonusList = getBonusListShallowCopy();
		for (Bonus bonus : bonusList) {
			damages = bonus.takeDamages(damages);
		}
		if (damages > 0) {
			game.setDead();
		}
	}
	
	//-----
	
	public void onDeath() {
		SoundManager.stopLoopSound(GameRessources.SOUND_STEPS);
	}
	
	@Override
	public void updateLogic(long delta, GamePlayingDefault game) {
		super.updateLogic(delta, game);

		this.input(game.input);
		
		this.updateTrackPoint();
		
		//Effet sonore des pas.
		if (this.getVelocity() != 0 && !game.isGameOver()) {
			//TODO SoundManager.playLoopSound(GameRessources.SOUND_STEPS, false);
		}
		else {
			SoundManager.stopLoopSound(GameRessources.SOUND_STEPS);
		}
		
		//Mise à jour des bonus
		ArrayList<Bonus> bonusList = this.getBonusListShallowCopy();
		for (Bonus b : bonusList) {
			b.updateBonus(delta, game);
		}
	}
	
	//GESTION DES COLLISIONS
	@Override
	public void onEntityCollision(long dT, GamePlayingDefault game, Entity ent) {
		
	}
	
	@Override
	public void onLandscapeCollision(long dT, GamePlayingDefault game) {
		
	}
	
	
	/**
	 * Modifie la vitesse du personnage en fonction des entrées joueur.
	 * @param input
	 */
	protected void input(GameInputBuffer input) {
		float moveX = 0;
		float moveY = 0;
		
		if (input.isKeyPressed(InputKey.KEY_RIGHT)) {
			moveX += DEFAULT_VELOCITY;
		}
		if (input.isKeyPressed(InputKey.KEY_LEFT)) {
			moveX -= DEFAULT_VELOCITY;
		}
		if (input.isKeyPressed(InputKey.KEY_DOWN)) {
			moveY += DEFAULT_VELOCITY;
		}
		if (input.isKeyPressed(InputKey.KEY_UP)) {
			moveY -= DEFAULT_VELOCITY;
		}
		setMovement(moveX, moveY);
		if (getVelocity() > DEFAULT_VELOCITY) setVelocity(DEFAULT_VELOCITY);
	}
	
	@Override
	public PlayerDefault clone() {
		PlayerDefault result = (PlayerDefault) super.clone();
		result.bonus = new ArrayList<Bonus>();
		
		return result;
	}
}
