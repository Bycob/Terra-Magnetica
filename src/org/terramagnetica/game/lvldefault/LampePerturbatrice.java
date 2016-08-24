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

import java.awt.Image;

import org.terramagnetica.game.GameRessources;
import org.terramagnetica.game.lvldefault.rendering.RenderCompound;
import org.terramagnetica.game.lvldefault.rendering.RenderEntityDefault;
import org.terramagnetica.physics.Force;
import org.terramagnetica.physics.Hitbox;
import org.terramagnetica.physics.HitboxCircle;
import org.terramagnetica.ressources.ImagesLoader;

import net.bynaryscode.util.Color4f;
import net.bynaryscode.util.maths.geometric.DimensionsInt;

/**
 * Une lampe perturbatrice est un type particulier de lampe.
 * <ul><li>Lorsqu'elle est jaune, elle n'exerce aucun contrôle
 * sur la trajectoire des éléments magnétiques (à part les délivrer
 * de toute autre influence, car une lampe perturbatrice est une
 * {@link InfluenceMagnetiqueMajeure}).
 * <li>Lorsqu'elle est rouge, elle donne une trajectoire aléatoire
 * aux éléments magnétiques. La trajectoire est déterminée par un
 * objet {@link DirectionVariable}.
 * </ul>
 * <p>Dans le jeu elle est appelée : "lampe magnétique à champs".
 * @author Louis JEAN
 *
 */
public class LampePerturbatrice extends AbstractLamp implements InfluenceMagnetiqueMajeure {
	
	private static final long serialVersionUID = 1L;
	
	private static final float FORCE = 40;
	
	/** L'objet qui va déterminer l'influence de la lampe sur les objets
	 * magnétiques sous son contrôle. La direction indiquée par l'objet
	 * est celle qui sera induise aux éléments magnétiques. */
	private DirectionVariable direction = new DirectionVariable();
	
	//RENDUS
	private RenderEntityDefault renderMainIndicator;

	public LampePerturbatrice() {
		super();
	}
	
	public LampePerturbatrice(int x, int y) {
		super(x, y);
	}
	
	@Override
	protected RenderCompound createRender() {
		RenderCompound render = new RenderCompound();
		
		//image principale
		String id = this.state ? GameRessources.ID_LAMP_PERTURBATRICE_ON : GameRessources.ID_LAMP_PERTURBATRICE_OFF;
		render.addRender(new RenderEntityDefault(id));
		
		//indicateurs de polarité
		Color4f color = this.state ? new Color4f(1f, 0f, 0f) : new Color4f(1f, 1f, 0f);
		
		render.addRender(this.renderMainIndicator = new RenderEntityDefault(GameRessources.ID_MAGNETIC_FIELD_INDICATOR)
				.withElevation(0.25f)
				.withColor(color));
		this.updateIndicatorTranslation();
		
		return render;
	}
	
	/** Met à jour la position du rendu de l'indicateur de direction
	 * de la lampe  */
	private void updateIndicatorTranslation() {
		float dir = this.direction.getDirection();
		float tX = (float) Math.cos(dir) * 0.7f;
		float tY = (float) Math.sin(dir) * 0.5f;
		
		if (this.renderMainIndicator != null) this.renderMainIndicator.setTranslation(tX, tY);
	}
	
	@Override
	public Image getImage() {
		return ImagesLoader.get(GameRessources.ID_LAMP_PERTURBATRICE_OFF);
	}
	
	@Override
	public DimensionsInt getDimensions() {
		return new DimensionsInt(192, 192);
	}
	
	@Override
	public DimensionsInt getImgDimensions() {
		return new DimensionsInt(256, 256);
	}
	
	@Override
	public Hitbox createHitbox() {
		return new HitboxCircle(this.getDimensionsf().getHeight() / 2);
	}
	
	@Override
	public void controls(GamePlayingDefault game, long delta, EntityMoving controlled) {
		updateLogic(delta, game);
		
		if (this.state) {
			float d = (float) this.getDistancef(controlled);
			float dir = this.direction.getDirection();
			
			float vX = (float) Math.cos(dir);//vecteur x déviant
			float vY = (float) Math.sin(dir);//vecteur y déviant
			
			controlled.getHitBoxf().addForce(new Force((FORCE * vX) / (d * d), (FORCE * vY) / (d * d)));
		}
		else {
			
		}
	}
	
	@Override
	public boolean hasPermissionForCollision(EntityMoving e) {
		return true;
	}
	
	@Override
	public boolean isAvailableFor(EntityMoving e) {
		return true;
	}
	
	@Override
	public void updateLogic(long dT, GamePlayingDefault game) {
		if (this.updated) return;
		this.updated = true;
		
		super.updateLogic(dT, game);
		
		if (this.didStateChanged) {
			recreateRender();
		}
		
		//mise à jour de la direction
		if (this.state) this.direction.update(dT);
		
		//mise à jour du rendu
		updateIndicatorTranslation();
	}
}
