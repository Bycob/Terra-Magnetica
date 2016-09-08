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

import org.terramagnetica.opengl.engine.GLConfiguration;
import org.terramagnetica.opengl.engine.GLConfiguration.GLProperty;
import org.terramagnetica.opengl.engine.Light;
import org.terramagnetica.opengl.engine.Model3D;
import org.terramagnetica.opengl.engine.Painter;
import org.terramagnetica.opengl.engine.Transform;
import org.terramagnetica.ressources.ModelLoader;

import net.bynaryscode.util.maths.geometric.Vec3d;

/**
 * Dessine une entit� qui est repr�sent�e par un mod�le 3D.
 * <p>Le point 0;0;0 du mod�le sera consid�r� comme le centre
 * de la case sur laquelle est rendue l'entit�. Le mod�le doit
 * �tre orient� de la m�me mani�re que le sera son rendu si
 * la rotatio vaut 0.
 * @author Louis JEAN
 *
 */
public class RenderEntityModel3D extends RenderObject {
	
	private Model3D model;
	
	private float rotation;
	private Vec3d translation = new Vec3d(0, 0, 0);
	
	public RenderEntityModel3D(String modelID) {
		this(modelID, 0f);
	}
	
	public RenderEntityModel3D(String modelID, float rotation) {
		this.model = ModelLoader.getNotNull(modelID);
		this.rotation = rotation;
	}
	
	/**
	 * D�finit l'orientation du mod�le, en degr�s.
	 * @param rotation - L'orientation choisie en degr�s.
	 */
	public void setRotation(float rotation) {
		this.rotation = rotation;
	}
	
	/**
	 * D�finit l'orientation du mod�le en degr�s.
	 * @param rotation - L'orientation choisie en degr�s.
	 * @return {@code this}. Cela permet de d�finir la rotation tout
	 * en cr�ant l'objet, et ainsi ne pas avoir � le stocker dans une
	 * variable temporaire.
	 */
	public RenderEntityModel3D withRotation(float rotation) {
		setRotation(rotation);
		return this;
	}
	
	public void setTranslation(float x, float y, float z) {
		this.translation = new Vec3d(x, y, z);
	}
	
	public RenderEntityModel3D withTranslation(float x, float y, float z) {
		this.setTranslation(x, y, z);
		return this;
	}
	
	/** 
	 * @return L'orientation du rendu en degr�s.
	 */
	public float getRotation() {
		return this.rotation;
	}
	
	@Override
	public void renderEntity3D(float x, float y, Painter painter) {
		GLConfiguration config = painter.getConfiguration();
		config.setPropertieEnabled(GLProperty.LIGHTING, true);
		
		Light light = painter.getLightModel().getLight0();
		
		painter.pushTransformState();
		
		//Permet de dessiner le mod�le dans le bon sens, au bon endroit.
		painter.addTransform(Transform.newTranslation(
				x + (float) this.translation.x, -y - (float) this.translation.y, (float) this.translation.z));
		painter.addTransform(Transform.newScale(1, -1, 1));
		painter.addTransform(Transform.newRotation(this.rotation, new Vec3d(0, 0, 1)));
		
		light.setPosition(0, 0, 1);
		
		this.model.draw(painter);
		
		painter.popTransformState();
		config.setPropertieEnabled(GLProperty.LIGHTING, false);
	}
}
