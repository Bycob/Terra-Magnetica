package org.terramagnetica.opengl.engine;

import org.terramagnetica.opengl.engine.GLConfiguration.GLProperty;

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
