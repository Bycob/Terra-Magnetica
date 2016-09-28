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

package org.terramagnetica.opengl.engine;

import org.terramagnetica.opengl.engine.GLConfiguration.GLProperty;

import net.bynaryscode.util.maths.geometric.AxisAlignedBox3D;
import net.bynaryscode.util.maths.geometric.Vec3d;

public class RenderableModel3D extends Renderable {
	
	private Model3D model;
	
	public RenderableModel3D(Model3D model) {
		setModel(model);
	}
	
	public void setModel(Model3D model) {
		this.model = model;
	}
	
	public RenderableModel3D withModel(Model3D model) {
		this.setModel(model);
		return this;
	}
	
	public Model3D getModel() {
		return this.model;
	}
	
	@Override
	public AxisAlignedBox3D getRenderBoundingBox(float x, float y, float z) {
		AxisAlignedBox3D box = this.model.getBoundingBox();
		box.translate(x, y, z);
		applyTransformsToBoundingBox(box);
		return box;
	}
	
	@Override
	public void renderAt(Vec3d position, double rotation, Vec3d up, Vec3d scale, Painter painter) {
		//Si la matrice n'est pas modifiée, pas besoin de vider le tampon du Painter
		painter.pushTransformState();
		applyTransforms(position, rotation, up, scale, painter);
		
		//TODO temporaire
		GLConfiguration config = painter.getConfiguration();
		config.setPropertieEnabled(GLProperty.LIGHTING, true);
		Light light = painter.getLightModel().getLight0();
		light.setPosition(0, 0, 1);
		
		//DESSIN
		this.model.draw(painter);
		
		config.setPropertieEnabled(GLProperty.LIGHTING, false);
		
		painter.popTransformState();
	}
}
