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

package org.terramagnetica.game.lvldefault.rendering;

import org.terramagnetica.game.lvldefault.Entity;
import org.terramagnetica.opengl.engine.AnimatedTexture;
import org.terramagnetica.opengl.engine.Painter;
import org.terramagnetica.opengl.engine.RenderableObject3D;
import org.terramagnetica.opengl.engine.Texture;
import org.terramagnetica.opengl.engine.TextureQuad;
import org.terramagnetica.ressources.TexturesLoader;

import net.bynaryscode.util.maths.geometric.AxisAlignedBox3D;
import net.bynaryscode.util.maths.geometric.Vec3d;

public class RenderEntityTexture extends RenderableObject3D implements Cloneable {
	
	protected TextureQuad texture;
	protected double width;
	protected double height;
	
	/** L'angle que fait le rendu par rapport au sol. {@code 0f}
	 * pour un rendu parallèle au sol, {@code PI / 2} pour un rendu 
	 * perpendiculaire au sol.*/
	private float angle;
	
	/** Indique si l'objet est dessiné au sol ou non. */
	private boolean onGround = false;
	
	/** La taille d'une case en pixels. Utilisée pour convertir les
	 * tailles d'images de pixels à cases. */
	public static final float SIZEREF = Entity.CASE;
	
	protected static final float angleDefault = (float) Math.PI * 1f / 4f;
	
	public RenderEntityTexture() {
		this(new TextureQuad(), angleDefault);
	}
	
	public RenderEntityTexture(String texID) {
		this(texID, angleDefault);
	}
	
	public RenderEntityTexture(Texture texture) {
		this(texture, angleDefault);
	}
	
	public RenderEntityTexture(float radius) {
		this(new TextureQuad(), radius);
	}
	
	public RenderEntityTexture(String texID, float radius) {
		this(TexturesLoader.get(texID), radius);
	}
	
	public RenderEntityTexture(Texture texture, float radius) {
		setRadius(radius);
		setTexture(texture);
	}
	
	protected void calculVertices() {
		removeAllVertice();
		
		if (!this.onGround) {
			double hightY = 0;
			double hightZ = this.height;
			double x1 = - (this.width / 2.0);
			double x2 = this.width / 2.0;
			
			addVertex(new Vec3d(x1, hightY, hightZ));
			addVertex(new Vec3d(x2, hightY, hightZ));
			addVertex(new Vec3d(x2, 0, 0));
			addVertex(new Vec3d(x1, 0, 0));
		}
		else {
			double hWidth = this.width / 2;
			double hHeight = this.height / 2;
			double z = 0.001;
			
			addVertex(new Vec3d(- hWidth, hHeight, z));
			addVertex(new Vec3d(hWidth, hHeight, z));
			addVertex(new Vec3d(hWidth, - hHeight, z));
			addVertex(new Vec3d(- hWidth, - hHeight, z));
		}
	}
	
	protected void defineDimensions() {
		this.width = (double) (this.texture.getWidth()) / SIZEREF;
		this.height = (double) (this.texture.getHeight()) / SIZEREF;
	}
	
	
	
	/**
	 * Permet de définir le rendu comme "au sol". Les coordonnées utilisées
	 * pour le rendu dans la méthode {@link #renderEntity3D(float, float, Painter)}
	 * seront celles du centre de la texture. Celle-ci sera dessinée sur le sol.
	 * <p>Attention cette méthode réinitialise toutes les options de positionnement
	 * indiquées jusqu'alors (translation, élevation, angle...) sauf l'échelle
	 * et la rotation.
	 */
	public RenderEntityTexture setOnGround(boolean onGround) {
		this.onGround = onGround;
		if (this.onGround) {
			this.setRadius(0);
		}
		
		calculVertices();
		
		return this;
	}
	
	public boolean isOnGround() {
		return this.onGround;
	}
	
	
	
	public void setTexture(String texID) {
		setTexture(TexturesLoader.get(texID));
	}
	
	@Override
	public void setTexture(Texture texture) {
		//Cas particulier des textures animées
		if (texture instanceof AnimatedTexture) {
			super.setTexture(texture);
			
			AnimatedTexture anim = (AnimatedTexture) texture;
			Texture tex = anim.get();
			
			if (tex instanceof TextureQuad) {
				this.texture = (TextureQuad) tex;
			}
			else {
				this.texture = new TextureQuad();
			}
		}
		else if (texture instanceof TextureQuad) {
			super.setTexture(texture);
			this.texture = (TextureQuad) texture;
		}
		else {
			throw new IllegalArgumentException("Seules des texture de type TextureQuad ou AnimatedTexture sont supportées sur cet objet");
		}

		defineDimensions();
		calculVertices();
	}
	
	//-----ANGLE PAR RAPPORT A L'HORIZONTALE
	
	/** Donne l'angle de rotation de la texture par rapport à la verticale, en radians. */
	public float getRadius() {
		return (float) - Math.toRadians(this.getRotationOffset().x);
	}
	
	/** Définit l'angle de rotation de la texture par rapport à la verticale, en radians. */
	public void setRadius(float radius) {
		Vec3d rot = this.getRotationOffset();
		this.setRotationOffset(- Math.toDegrees(radius), rot.y, rot.z);
	}
	
	public RenderEntityTexture withRadius(float radius) {
		this.setRadius(radius);
		return this;
	}
	
	/**
	 * Permet d'obtenir l'ordonnée de l'endroit réel au sol où sera
	 * rendue l'entité.
	 * @param x - la position de l'entité en x
	 * @param y - la position de l'entité en y
	 * @return l'ordonnée de l'entité auquel on aura appliqué toutes
	 * les transformations paramètrées via l'objet rendu.
	 */
	public float getRealPositionY(float x, float y) {
		//calcul du différentiel causé par l'élévation.
		float difElev = (float) - (this.posOffset.z / Math.tan(this.getRadius()));
		
		return (float) (y + this.posOffset.y + difElev);
	}
	
	@Override
	public AxisAlignedBox3D getRenderBoundingBox(float x, float y, float z) {
		AxisAlignedBox3D result = super.getRenderBoundingBox(x, y, z);
		
		result.sizeY = Math.sin(this.getRadius());
		return result;
	}
	
	@Override
	public void renderAt(Vec3d position, double rotation, Vec3d up, Vec3d scale, Painter painter) {
		updateAnimation();
		super.renderAt(position, rotation, up, scale, painter);
	}
	
	protected void updateAnimation() {
		if (super.texture instanceof AnimatedTexture) {
			TextureQuad oldTexture = this.texture;
			Texture newTexture = ((AnimatedTexture) super.texture).get();
			this.texture = newTexture instanceof TextureQuad ? (TextureQuad) newTexture : new TextureQuad();
			
			if (!oldTexture.getDimensions().equals(this.texture.getDimensions())) {
				defineDimensions();
				calculVertices();
			}
		}
	}
	
	@Override
	public RenderEntityTexture clone() {
		RenderEntityTexture clone = (RenderEntityTexture) super.clone();
		
		clone.color = this.color.clone();
		
		return clone;
	}
}
