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

package org.terramagnetica.opengl.engine;

import org.terramagnetica.opengl.gui.GuiWindow;

import net.bynaryscode.util.maths.geometric.Vec2d;
import net.bynaryscode.util.maths.geometric.Vec2i;
import net.bynaryscode.util.maths.geometric.DimensionsInt;
import net.bynaryscode.util.maths.geometric.RectangleDouble;

/**
 * Cette classe permet de gérer des textures rectangulaire.
 * Ces textures peuvent être des parties d'images, ou des images
 * complètes. L'objet {@link TextureQuad} peut contenir également 
 * des informations sur l'image sur laquelle la texture est placée,
 * ce qui permet de connaitre exactement les coordonnées en pixels,
 * et non plus relativement à l'image, de la texture.
 * @author Louis JEAN
 *
 */
public class TextureQuad implements Cloneable, Texture {
	
	private Vec2d pointMin;
	private Vec2d pointMax;
	private DimensionsInt dims;
	private int texture;
	
	public TextureQuad() {
		this(0.0, 0.0, 0.0, 0.0, 0);
	}
	
	/**
	 * L'emplacement est indiqué par les coordonnées de la case (256*256) de la texture. La première case
	 * en haut à gauche a pour coordonnées (0;0).
	 * <br> Pratique pour les textures des décors.
	 * @param x - abscisse de la case sur la texture, en case (256*256)
	 * @param y - ordonnées de la case sur la texture, en case (256*256)
	 * @param texWidth - largeur de l'image sur laquelle se trouve la texture, en case (256*256)
	 * @param texHeight - hauteur de l'image sur laquelle se trouve la texture, en case (256*256)
	 * @param texture - ID de la texture opengl
	 */
	public TextureQuad(int x, int y, int texWidth, int texHeight, int texture) {
		pointMin = new Vec2d(
				(double) x * 256.0 / ((double) texWidth * 256.0),
				(double) y * 256.0 / ((double) texHeight * 256.0));
		pointMax = new Vec2d(
				(double) (x + 1) * 256.0 / ((double) texWidth * 256.0),
				(double) (y + 1) * 256.0 / ((double) texHeight * 256.0));
		dims = new DimensionsInt(texWidth * 256, texHeight * 256);
		this.texture = texture;
	}
	
	/**
	 * L'emplacement est indiqué par les coordonnées des coins (haut-gauche, bas-droit).
	 * @param x1 - abscisse du coin supérieur gauche, en pixel
	 * @param y1 - ordonnée du coin supérieur gauche, en pixel
	 * @param x2 - abscisse du coin inferieur droit, en pixel
	 * @param y2 - ordonnée du coin inférieur droit, en pixel
	 * @param texWidth - largeur de l'image sur laquelle est placée la texture, en pixel
	 * @param texHeight - hauteur de l'image sur laquelle est placée la texture, en pixel
	 * @param texture - ID de la texture opengl
	 */
	public TextureQuad(int x1, int y1, int x2, int y2, int texWidth, int texHeight, int texture) {
		pointMin = new Vec2d((double) x1 / (double) texWidth, (double) y1 / (double) texHeight);
		pointMax = new Vec2d((double) x2 / (double) texWidth, (double) y2 / (double) texHeight);
		dims = new DimensionsInt(texWidth, texHeight);
		this.texture = texture;
	}
	
	/**
	 * L'emplacement de la texture est indiqué par les coordonnées utilisés par openGL.
	 * @param s1 - abscisse du coin supérieur gauche.
	 * @param t1 - ordonnée du coin supérieur gauche.
	 * @param s2 - abscisse du coin inférieur droit.
	 * @param t2 - abscisse du coin inférieur droit.
	 * @param texture - ID de la texture opengl.
	 */
	public TextureQuad(double s1, double t1, double s2, double t2, int texture) {
		pointMin = new Vec2d(s1, t1);
		pointMax = new Vec2d(s2, t2);
		dims = new DimensionsInt(1, 1);
		this.texture = texture;
	}
	
	@Override
	public void setTextureID(int texture) {
		this.texture = texture;
	}
	
	@Override
	public TextureQuad withTextureID(int texture) {
		this.texture = texture;
		return this;
	}
	
	@Override
	public int getGLTextureID() {
		return texture;
	}
	
	public Vec2d getCoinHautGaucheST() {
		return pointMin;
	}
	
	public Vec2d getCoinBasDroitST() {
		return pointMax;
	}
	
	public RectangleDouble getSTBounds() {
		return new RectangleDouble(pointMin, pointMax);
	}
	
	@Override
	public Vec2d[] getSTSommets() {
		RectangleDouble bounds = getSTBounds();
		return new Vec2d[] {
				new Vec2d(bounds.xmin, bounds.ymin),
				new Vec2d(bounds.xmax, bounds.ymin),
				new Vec2d(bounds.xmax, bounds.ymax),
				new Vec2d(bounds.xmin, bounds.ymax)
		};
	}
	
	@Override
	public int getNbSommets() {
		return 4;
	}
	
	/**
	 * @return Les coordonnées du coin en haut à gauche de
	 * la texture par rapport à l'image de référence, en
	 * pixels.
	 */
	public Vec2i getCoinHautGauche(){
		return new Vec2i((int) (pointMin.x * dims.getWidth()), (int) (pointMin.y * dims.getHeight()));
	}
	
	/**
	 * @return Les coordonnées du coin en bas à droite de
	 * la texture par rapport à l'image de référence, en
	 * pixels.
	 */
	public Vec2i getCoinBasDroit(){
		return new Vec2i((int) (pointMax.x * dims.getWidth()), (int) (pointMax.y * dims.getHeight()));
	}
	
	/**
	 * @return La largeur de la texture sur l'image, en pixels.
	 */
	public int getWidth(){
		return (int) ((pointMax.x - pointMin.x) * dims.getWidth());
	}
	
	/**
	 * @return La hauteur de la texture sur l'image, en pixels.
	 */
	public int getHeight(){
		return (int) ((pointMax.y - pointMin.y) * dims.getHeight());
	}
	
	public DimensionsInt getDimensions() {
		return new DimensionsInt(getWidth(), getHeight());
	}

	public void setDimensions(int width, int height) {
		dims = new DimensionsInt(width, height);
	}
	
	public void setTextureEmplacement(int x1, int y1, int x2, int y2){
		pointMin = new Vec2d((double) x1 / (double) dims.getWidth(), (double) y1 / (double) dims.getHeight());
		pointMax = new Vec2d((double) x2 / (double) dims.getWidth(), (double) y2 / (double) dims.getHeight());
	}
	
	public void setTextureEmplacementST(double s1, double t1, double s2, double t2) {
		pointMin = new Vec2d(s1, t1);
		pointMax = new Vec2d(s2, t2);
	}
	
	public void setTextureCaseEmplacement(int x, int y){
		pointMin = new Vec2d(
				(double) (x * 256) / (double) dims.getWidth(),
				(double) (y * 256) / (double) dims.getHeight());
		pointMax = new Vec2d(
				(double) ((x + 1) * 256) / (double) dims.getWidth(),
				(double) ((y + 1) * 256) / (double) dims.getHeight());
	}
	
	public void drawQuad2D(double x1, double y1, double x2, double y2, boolean binding){
		
		Painter painter = Painter.instance;
		
		if (binding)
			painter.setTexture(this);
		
		painter.setPrimitive(Painter.Primitive.QUADS);
		painter.addVertex(x1, y1);
		painter.addVertex(x2, y1);
		painter.addVertex(x2, y2);
		painter.addVertex(x1, y2);
	}
	
	public void fillScreen2D(double scaleX, double scaleY, boolean binding){
		if (scaleX <= 0 || scaleY <= 0)
			throw new IllegalArgumentException();
		
		if (binding)
			Painter.instance.setTexture(this);
		
		GLOrtho r = GuiWindow.getInstance().getOrtho();
		
		int left = (int) (r.left / scaleX) - 1;
		int right = (int) (r.right / scaleX) + 1;
		int top = (int) (r.top / scaleY) + 1;
		int bottom = (int) (r.bottom / scaleY) - 1;
		
		if (right >= left && top >= bottom) {
			for (int x = left ; x <= right ; x++){
				for (int y = bottom ; y <= top ; y++){
					this.drawQuad2D(x * scaleX, (y + 1) * scaleY, 
							(x + 1) * scaleX, y * scaleY, false);
				}
			}
		}
	}
	
	public TextureQuad getPartOfTexture() {
		return null;
	}
	
	@Override
	public TextureQuad clone(){
		TextureQuad result = null;
		try {
			result = (TextureQuad) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		result.setTextureEmplacementST(pointMin.x, pointMin.y, pointMax.x, pointMax.y);
		result.setDimensions(dims.getWidth(), dims.getHeight());
		
		return result;
	}
}
