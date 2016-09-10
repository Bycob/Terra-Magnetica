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

import static org.terramagnetica.game.GameRessources.*;

import java.awt.Image;

import org.terramagnetica.game.GameRessources;
import org.terramagnetica.game.lvldefault.rendering.RenderEntityTexture;
import org.terramagnetica.opengl.engine.TextureQuad;
import org.terramagnetica.physics.Force;
import org.terramagnetica.physics.Hitbox;
import org.terramagnetica.physics.HitboxCircle;
import org.terramagnetica.ressources.ImagesLoader;
import org.terramagnetica.ressources.TexturesLoader;

import net.bynaryscode.util.maths.geometric.DimensionsInt;
import net.bynaryscode.util.maths.geometric.Vec2f;
import net.bynaryscode.util.maths.geometric.Vec2i;
import net.bynaryscode.util.maths.geometric.Vec3d;

public class Lampe extends AbstractLamp implements InfluenceMagnetiqueMajeure {
	
	private static final long serialVersionUID = 1L;
	
	public static final int RAYON = 96;
	public static final float RAYON_F = (float) RAYON / CASE;
	/** Les forces de magnétisme utilisées pour calculer les modifications de
	 * trajectoire.
	 * <ul><li>FORCE_IN : la force qui s'exerce vers l'intérieur.</li>
	 * <li>FORCE_OUT : la force qui s'exerce vers l'extérieur.</li></ul> */
	public static final double FORCE_IN = 40, FORCE_OUT = - 1.5f * FORCE_IN;
	public static final double ROTATING = Math.PI / 2;
	
	//ETAT "PERMANENT"
	/** Cette variable vaut "true" quand l'état de la lampe est permanent,
	 * et "false" sinon. 
	 * @see LampState#setPermanentState(boolean)*/
	private boolean permanentMode_On = false;
	
	public Lampe(){
		super();
	}
	
	public Lampe(int x, int y){
		super(x, y);
	}
	
	@Override
	public Image getImage(){
		return ImagesLoader.get(PATH_COMPOSANTS + TEX_LAMP_IN);
	}
	
	@Override
	public TextureQuad getMinimapIcon() {
		return TexturesLoader.getQuad(GameRessources.ID_MAP_LAMP);
	}
	
	@Override
	public void createRender() {
		//RENDU DE LA LAMPE
		this.renderManager.putRender("on", new RenderEntityTexture(PATH_COMPOSANTS + TEX_LAMP_OUT));
		this.renderManager.putRender("off", new RenderEntityTexture(PATH_COMPOSANTS + TEX_LAMP_IN));
		
		//Rendu des ondes si la lampe est activée en mode permanent.
		this.renderManager.putRender("permanentMode", 
				new RenderEntityTexture(TexturesLoader.getAnimatedTexture(GameRessources.PATH_ANIM003_PERMANENT_MODE_LAMP))
					.setOnGround(true).withScaleOffset(1.25, 1.25, 0));
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
	public Vec2i getImagePosition() {
		return new Vec2i(
				this.getPositioni().x - this.getImgDimensions().getWidth() / 2,
				this.getPositioni().y - this.getImgDimensions().getHeight() * 2 / 3);
	}

	@Override
	public Hitbox createHitbox() {
		return new HitboxCircle(RAYON_F);
	}
	
	@Override
	public void controls(GamePlayingDefault game, long dT, EntityMoving ent) {
		updateLogic(dT, game);
		Vec2f centre = this.getPositionf();
		Vec2f otherCentre = ent.getPositionf();
		double df = getDistancef(ent);
		
		double dX = centre.x - otherCentre.x;
		double dY = centre.y - otherCentre.y;
		
		Vec3d eF = Vec3d.unitVector(dX, dY);
		double force = (this.state ? FORCE_OUT : FORCE_IN) / (df * df);
		
		ent.hitbox.addForce(new Force((float) (force * eF.x), (float) (force * eF.y)));
	}
	
	@Override
	public boolean hasPermissionForCollision(EntityMoving e) {
		if (e == null) throw new NullPointerException();
		return true;
	}
	
	@Override
	public boolean isAvailableFor(EntityMoving e) {
		return true;
	}
	
	@Override
	public void updateLogic(long delta, GamePlayingDefault game) {
		super.updateLogic(delta, game);
		
		//rendu
		boolean old_permanentMode_On = this.permanentMode_On;
		this.permanentMode_On = game.getAspect(LampState.class).isLampStatePermanent();
		
		//Mise à jour du rendu.
		if (this.didStateChanged || old_permanentMode_On != this.permanentMode_On) {
			this.renderManager.render(this.state ? "on" : "off");
			this.renderManager.setEffect("permanentMode", this.permanentMode_On);
		}
	}
	
	@Override
	public Lampe clone() {
		Lampe result = (Lampe) super.clone();
		return result;
	}
}
