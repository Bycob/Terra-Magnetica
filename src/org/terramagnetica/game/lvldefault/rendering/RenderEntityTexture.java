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

import static org.terramagnetica.game.GameRessources.*;

import org.terramagnetica.game.lvldefault.Entity;
import org.terramagnetica.opengl.engine.Painter;
import org.terramagnetica.opengl.engine.TextureQuad;
import org.terramagnetica.opengl.engine.Transform;
import org.terramagnetica.ressources.TexturesLoader;

import net.bynaryscode.util.Color4f;
import net.bynaryscode.util.maths.MathUtil;
import net.bynaryscode.util.maths.geometric.AxisAlignedBox3D;
import net.bynaryscode.util.maths.geometric.Vec2f;
import net.bynaryscode.util.maths.geometric.Vec3d;

public class RenderEntityTexture extends RenderObject implements Cloneable {
	
	protected TextureQuad texture;
	protected double width;
	protected double height;
	
	/** L'angle que fait le rendu par rapport au sol. {@code 0f}
	 * pour un rendu parallèle au sol, {@code PI / 2} pour un rendu 
	 * perpendiculaire au sol.*/
	private float angle;
	private int rotation = 0;
	private float translationX = 0f, translationY = 0f;
	private double scaleX = 1, scaleY = 1;
	private float elevation = 0f;
	
	/** Les vertices de l'image à dessiner, dans l'ordre :
	 * coins haut-gauche, haut-droit, bas-droit, bas-gauche.
	 * Elles représentent les sommets de l'image si elle est
	 * dessinée au point (0 ; 0).
	 * <p>L'angle et l'échelle sont inclus dans le calcul des
	 * vertices. La translation, l'élevation et la rotation
	 * ne sont pas inclues dans le calcul. */
	protected Vec3d[] vertices = new Vec3d[4];
	private boolean onGround = false;
	
	/** La taille d'une case en pixels. Utilisée pour convertir les
	 * tailles d'images de pixels à cases. */
	public static final float SIZEREF = Entity.CASE;
	
	protected static final float angleDefault = (float) Math.PI * 1f / 4f;
	
	public RenderEntityTexture() {
		this(PATH_COMPOSANTS + TEX_CRYSTAL, angleDefault);
	}
	
	public RenderEntityTexture(String texID) {
		this(texID, angleDefault);
	}
	
	public RenderEntityTexture(TextureQuad texture) {
		this(texture, angleDefault);
	}
	
	public RenderEntityTexture(float radius) {
		this(PATH_COMPOSANTS + TEX_CRYSTAL, radius);
	}
	
	public RenderEntityTexture(String texID, float radius) {
		this(TexturesLoader.getQuad(texID), radius);
	}
	
	public RenderEntityTexture(TextureQuad texture, float radius) {
		this.angle = MathUtil.valueInRange_f(radius, 0, (float) Math.PI / 2f);
		setTexture(texture);
	}
	
	protected void calculVertices() {
		if (!this.onGround) {
			double hightY = Math.cos(this.angle) * this.height;
			double hightZ = Math.sin(this.angle) * this.height;
			double x1 = - (this.width / 2.0);
			double x2 = this.width / 2.0;
			
			this.vertices[0] = new Vec3d(x1, hightY, hightZ);
			this.vertices[1] = new Vec3d(x2, hightY, hightZ);
			this.vertices[2] = new Vec3d(x2, 0, 0);
			this.vertices[3] = new Vec3d(x1, 0, 0);
		}
		else {
			double hWidth = this.width / 2;
			double hHeight = this.height / 2;
			double z = 0.001;
			
			this.vertices[0] = new Vec3d(- hWidth, hHeight, z);
			this.vertices[1] = new Vec3d(hWidth, hHeight, z);
			this.vertices[2] = new Vec3d(hWidth, - hHeight, z);
			this.vertices[3] = new Vec3d(- hWidth, - hHeight, z);
		}
	}
	
	protected void defineDimensions() {
		this.width = (double) (this.texture.getWidth()) / SIZEREF * this.scaleX;
		this.height = (double) (this.texture.getHeight()) / SIZEREF * this.scaleY;
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
		
		defineDimensions();
		calculVertices();
		
		return this;
	}
	
	public boolean isOnGround() {
		return this.onGround;
	}
	
	
	
	public void setTexture(String texID) {
		setTexture(TexturesLoader.getQuad(texID));
	}
	
	public void setTexture(TextureQuad texture) {
		this.texture = texture;

		defineDimensions();
		calculVertices();
	}
	
	
	//-----ROTATION
	
	public int getRotation() {
		return this.rotation;
	}
	
	/**
	 * Définit l'orientation du rendu, en degrés.
	 * @param degrees
	 */
	public void setRotation(int degrees) {
		this.rotation = degrees;
	}
	
	public RenderEntityTexture withRotation(int degrees) {
		setRotation(degrees);
		return this;
	}
	
	
	//-----TRANSLATION
	
	/**
	 * @return La translation que subit l'image de rendu, sous la forme
	 * de coordonnées. Autrement dit, les coordonnées de l'origine ayant
	 * subit une translation identique à celle que subira le rendu.
	 */
	public Vec2f getTranslation() {
		return new Vec2f(this.translationX, this.translationY);
	}
	
	public void setTranslation(float x, float y) {
		this.translationX = x;
		this.translationY = y;
	}
	
	public RenderEntityTexture withTranslation(float x, float y) {
		this.setTranslation(x, y);
		return this;
	}
	
	
	//-----ECHELLE
	
	public void setScale(double scaleX, double scaleY) {
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		defineDimensions();
		calculVertices();
	}
	
	public RenderEntityTexture withScale(double scaleX, double scaleY) {
		setScale(scaleX, scaleY);
		return this;
	}
	
	
	//-----ANGLE PAR RAPPORT A L'HORIZONTALE
	
	public float getRadius() {
		return this.angle;
	}
	
	public void setRadius(float radius) {
		this.angle = MathUtil.valueInRange_f(radius, 0, (float) Math.PI / 2f);
		this.calculVertices();
	}
	
	public RenderEntityTexture withRadius(float radius) {
		this.setRadius(radius);
		return this;
	}
	
	
	//-----COULEUR
	
	public RenderEntityTexture withColor(Color4f color) {
		this.setColor(color);
		return this;
	}
	
	
	//-----HAUTEUR PAR RAPPORT AU SOL
	
	public float getElevation() {
		return this.elevation;
	}
	
	public void setElevation(float elevation) {
		this.elevation = elevation;
	}
	
	public RenderEntityTexture withElevation(float elevation) {
		this.setElevation(elevation);
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
		float difElev = (float) (this.getElevation() / Math.tan(this.getRadius()));
		
		return y + this.translationY + difElev;
	}
	
	@Override
	public AxisAlignedBox3D getRenderBoundingBox(float x, float y) {
		float x1 = (float) (this.vertices[0].x + x + this.translationX);
		float openGLY = - (y + this.translationY);
		return new AxisAlignedBox3D(
				x1, openGLY, this.elevation,
				this.width, this.vertices[0].y, this.vertices[0].z + this.elevation);
	}
	
	@Override
	public void renderEntity3D(float x, float y, Painter painter) {
		painter.setPrimitive(Painter.Primitive.QUADS);
		painter.setTexture(this.texture);
		painter.setColor(this.color == null ? new Color4f(1f, 1f, 1f, 1f) : this.color);
		
		//Si la matrice n'est pas modifiée, pas besoin de vider le tampon du Painter
		painter.pushTransformState();
		
		x += this.translationX;
		y += this.translationY;
		
		if (this.rotation != 0) {//rotation du rendu.
			painter.addTransform(Transform.newRotation(this.getRotation(), new Vec3d(0, 0, 1), new Vec3d(x, -y)));
		}
		
		for (Vec3d vertex : this.vertices) {
			Vec3d v = vertex.clone();
			v.translate(x, - y, this.elevation);
			painter.addVertex(v);
		}
		
		painter.popTransformState();
	}
	
	@Override
	public RenderEntityTexture clone() {
		RenderEntityTexture clone = null;
		try {
			clone = (RenderEntityTexture) super.clone();
		} catch (CloneNotSupportedException e) {
			//n'arrive jamais.
		}
		
		clone.color = this.color.clone();
		clone.texture = this.texture.clone();
		
		return clone;
	}
}
