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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.terramagnetica.game.GameRessources;
import org.terramagnetica.game.lvldefault.rendering.RenderCompound;
import org.terramagnetica.game.lvldefault.rendering.RenderEntity;
import org.terramagnetica.game.lvldefault.rendering.RenderEntityDefault;
import org.terramagnetica.game.lvldefault.rendering.RenderEntityDefaultAnimation;
import org.terramagnetica.game.physic.Hitbox;
import org.terramagnetica.game.physic.HitboxCircle;
import org.terramagnetica.opengl.miscellaneous.AnimationManager;
import org.terramagnetica.ressources.ImagesLoader;
import org.terramagnetica.ressources.TexturesLoader;

import net.bynaryscode.util.Boussole;
import net.bynaryscode.util.Util;
import net.bynaryscode.util.maths.geometric.DimensionsInt;
import net.bynaryscode.util.maths.geometric.Vec2f;
import net.bynaryscode.util.maths.geometric.Vec2i;

public class Lampe extends AbstractLamp implements InfluenceMagnetiqueMajeure {
	
	private static final long serialVersionUID = 1L;
	
	public static final int RAYON = 96;
	public static final float RAYON_F = (float) RAYON / CASE;
	/** Les forces de magn�tisme utilis�es pour calculer les modifications de
	 * trajectoire.
	 * <ul><li>FORCE_IN : la force qui s'exerce vers l'int�rieur.</li>
	 * <li>FORCE_OUT : la force qui s'exerce vers l'ext�rieur, 2x plus forte.</li></ul> */
	public static final double FORCE_IN = 20, FORCE_OUT = FORCE_IN * 2;
	public static final double ROTATING = Math.PI / 2;
	
	private transient boolean[] situation = new boolean[4];
	private transient List<EntityMoving> placed = new ArrayList<EntityMoving>();
	
	//ETAT "PERMANENT"
	/** Cette variable vaut "true" quand l'�tat de la lampe est permanent,
	 * et "false" sinon. 
	 * @see LampState#setPermanentState(boolean)*/
	private boolean permanentMode_On = false;
	/** Les ondes indiquant que les lampes restent rouges et ne s'�teignent
	 * pas. Ce rendu s'ajoute au rendu de la lampe lorsque l'�tat de la lampe
	 * est permanent.
	 * @see LampState#setPermanentState(boolean)*/
	private RenderEntityDefaultAnimation renderPermanentMode = null;
	
	public Lampe(){
		super();
		Arrays.fill(this.situation, true);
	}
	
	public Lampe(int x, int y){
		super(x, y);
		Arrays.fill(this.situation, true);
	}
	
	@Override
	public Image getImage(){
		return ImagesLoader.get(PATH_COMPOSANTS + TEX_LAMP_IN);
	}
	
	@Override
	public RenderEntity createRender() {
		//RENDU DE LA LAMPE
		RenderEntityDefault renderLamp;
		if (this.state) {
			renderLamp =  new RenderEntityDefault(PATH_COMPOSANTS + TEX_LAMP_OUT);
		}
		else {
			renderLamp =  new RenderEntityDefault(PATH_COMPOSANTS + TEX_LAMP_IN);
		}
		
		if (this.renderPermanentMode == null) {
			AnimationManager am = new AnimationManager(
					TexturesLoader.getAnimatedTexture(GameRessources.PATH_ANIM003_PERMANENT_MODE_LAMP));
			
			this.renderPermanentMode = new RenderEntityDefaultAnimation(am);
			this.renderPermanentMode.setScale(1.25, 1.25);
			this.renderPermanentMode.setOnGround(true);
			
			am.start();
		}
		
		if (this.permanentMode_On) {
			RenderCompound render = new RenderCompound();
			render.addRender(renderLamp);
			render.addRender(this.renderPermanentMode);
			return render;
		}
		
		return renderLamp;
	}
	
	@Override
	public DimensionsInt getDimensions(){
		return new DimensionsInt(192, 192);
	}
	
	@Override
	public DimensionsInt getImgDimensions(){
		return new DimensionsInt(256, 384);
	}
	
	@Override
	public Vec2i getCoordonn�esToDraw() {
		return new Vec2i(
				this.getCoordonn�es().x - this.getImgDimensions().getWidth() / 2,
				this.getCoordonn�es().y - this.getImgDimensions().getHeight() * 2 / 3);
	}

	@Override
	public Hitbox createHitbox() {
		return new HitboxCircle(RAYON_F);
	}
	
	@Override
	public void controls(GamePlayingDefault game, long dT, EntityMoving ent) {
		updateLogic(dT, game);
		Vec2f centre = this.getCoordonn�esf();
		double d = getDistance(ent);
		double df = getDistancef(ent);
		Vec2f c = ent.getCoordonn�esf();
		
		double dX = centre.x - c.x;
		double dY = centre.y - c.y;
		
		if (this.state) {//L'entit� est repouss�e par la lampe.
			Arrays.fill(this.situation, true);
			this.updateMagnetPointsSituation(game);
			
			boolean shouldBeExpulsed = false;
			
			if (this.placed.contains(ent)) {
				this.placed.remove(ent);
				shouldBeExpulsed = true;
			}
			
			double invertDX = - dX;
			double invertDY = - dY;
			
			if (shouldBeExpulsed) {
				ent.setMovement((float) invertDX, (float) invertDY);
				ent.setVelocity(ent.getMaxVelocity());
			}
			else {
				double entMoveX = ent.getMovementX() + (FORCE_OUT * invertDX / (df * df)) * dT / 1000;
				double entMoveY = ent.getMovementY() + (FORCE_OUT * invertDY / (df * df)) * dT / 1000;
				
				ent.setMovementX((float) entMoveX);
				ent.setMovementY((float) entMoveY);
			}
		}
		else {//L'entit� est attir�e par la lampe.
			ArrayList<Vec2f> magnetPoints = Util.createList(this.getMagnetsPoints());
			for (int i = 3 ; i >= 0 ; i--) {
				if (!this.situation[i]) {
					magnetPoints.remove(i);
				}
			}
			//Le point vers lequel va converger l'entit�.
			Vec2f mp = Util.findNearestPoint(c, magnetPoints.toArray(new Vec2f[0])).asFloat();
			
			if (d < RAYON * 1.1) {//L'aimant doit maintenant se mettre gentiment � sa place.
				if (this.placed.contains(ent)) {
					return;
				}
				
				ent.setMovementX(0);
				ent.setMovementY(0);
				
				//Changement de rep�re.
				Vec2f invertCentre = new Vec2f(centre.x, - centre.y);
				Vec2f invertMp = new Vec2f(mp.x, - mp.y);
				double dirBut = Boussole.getDirection(invertCentre, invertMp);
				Boussole cpBut = Boussole.getPointCardinalPourAngle(dirBut);
				
				if (MagneticFieldUtil.rotateTo(ent, centre, RAYON_F, dirBut, ROTATING, dT)) {
					this.placed.add(ent);
					this.situation[cpBut.ordinal()] = false;
				}
			}
			else {//L'aimant est attir� normalement.
				
				double mpdX = mp.x - c.x;//vecteur x de l'attirance
				double mpdY = mp.y - c.y;//vecteur y de l'attirance
				
				float coefSpeedRest = 1 - (0.1f * dT / 1000); //Coefficient de perte de vitesse : vitesse restante.
				
				//Le mouvement initial, ralenti, auquel est appliqu� le vecteur magn�tique variant en fonction
				//de la distance au carr�. Le vecteur de d�viation est en case par secondes.
				double entMoveX = ent.getMovementX() * coefSpeedRest + (FORCE_IN * mpdX / (df * df)) * dT / 1000;
				double entMoveY = ent.getMovementY() * coefSpeedRest + (FORCE_IN * mpdY / (df * df)) * dT / 1000;
				
				Vec2f c2 = c.clone();
				c2.translate((float) entMoveX * dT / 1000, (float) entMoveY * dT / 1000);
				double d2 = c2.getDistance(centre);
				
				if (d2 < RAYON_F * 1.1 && this.hasMagnetPointEmpty()) {
					double ratio = (RAYON_F - d2) / d2;
					c2.translate((float) ((c2.x - centre.x) * ratio), (float) ((c2.y - centre.y) * ratio));
					ent.setCoordonn�esf(c2.x, c2.y);
					entMoveX = 0; entMoveY = 0;
				}
				
				ent.setMovementX((float) entMoveX);
				ent.setMovementY((float) entMoveY);
			}
		}
	}
	
	@Override
	public boolean hasPermissionForCollision(EntityMoving e) {
		if (e == null) throw new NullPointerException();
		return !this.placed.contains(e) && !(getDistancef(e) < RAYON_F * 1.5);
	}
	
	@Override
	public boolean isAvailableFor(EntityMoving e) {
		
		for (boolean b : this.situation) {
			if (b) return true;
		}
		
		if (this.placed.contains(e)) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Donne un tableau des 4 points magn�tiques autour de la lampe,
	 * dans l'ordre : <i>NORD, EST, SUD, OUEST</i>
	 * @return
	 */
	public Vec2f[] getMagnetsPoints() {
		Vec2f c = this.getCoordonn�esf();
		return new Vec2f[] {
				new Vec2f(c.x, (float) (c.y - RAYON_F)),
				new Vec2f((float) (c.x + RAYON_F), c.y),
				new Vec2f(c.x, (float) (c.y + RAYON_F)),
				new Vec2f((float) (c.x - RAYON_F), c.y)
		};
	}
	
	private boolean hasMagnetPointEmpty() {
		for (boolean bool : this.situation) {
			if (bool) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Met � jour la situation des points aimant�s par rapport au d�cor.
	 * <p>La lampe poss�de par d�faut quatre points aimant�s, donc quatre
	 * objets subissant les influences magn�tiques peuvent s'y fixer. Mais
	 * si cette m�me lampe est coll�e � un mur, alors le point magn�tique
	 * du c�t� de ce mur doit �tre desactiv� : aucun objet magn�tique ne
	 * pourra venir s'y fixer !
	 * <p>Attention, cette m�thode met � jour les points magn�tiques, uniquement
	 * en fonction du d�cor. Elle ne prend pas en compte les objets magn�tiques
	 * mobiles, d�j� plac�s autour de la lampe. D'autre part elle ne fait
	 * que d�sactiver les points, ainsi, un point qui n'est pas activ� avant cette
	 * m�thode ne sera pas non plus activ� apr�s.
	 * @param game - Le moteur de jeu o� se trouve la lampe.
	 */
	private void updateMagnetPointsSituation(GamePlayingDefault game) {
		Vec2i cCase = this.getCoordonn�esCase();
		LandscapeTile[] around = game.getLandscapeAround(cCase.x, cCase.y);
		for (int i = 0 ; i < 4 ; i++) {
			if (!around[i].isEnabled()) {
				this.situation[i] = false;
			}
			Vec2i c = around[i].getCoordonn�esCase();
			if (game.getCaseEntityAt(c.x, c.y) != null) {
				this.situation[i] = false;
			}
		}
	}
	
	@Override
	public void updateLogic(long delta, GamePlayingDefault game) {
		super.updateLogic(delta, game);
		
		boolean old_permanentMode_On = this.permanentMode_On;
		this.permanentMode_On = game.getAspect(LampState.class).isLampStatePermanent();
		
		if (this.didStateChanged || old_permanentMode_On != this.permanentMode_On) {
			recreateRender();
		}
		
		this.updateMagnetPointsSituation(game);
	}
	
	@Override
	public Lampe clone() {
		Lampe result = (Lampe) super.clone();
		result.placed = new ArrayList<EntityMoving>();
		result.situation = new boolean[4];
		Arrays.fill(result.situation, true);
		return result;
	}
}
