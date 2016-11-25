package org.terramagnetica.opengl.engine;

public interface StdUniform {
	
	String USE_TEXTURES = "useTextures";
	
	public interface View {
		String PROJECTION_MATRIX = "view.projection";
		String CAMERA_MATRIX = "view.camera";
		String MODEL_MATRIX = "view.model";
		String CAMERA_POSITION = "view.cameraPosition";
	}
	
	public interface Light {
		int MAX_LIGHT = 10;
		
		String TYPE = "light[0].type";
		String POSITION = "light[0].position";
		String AMBIENT = "light[0].ambient";
		String DIFFUSE = "light[0].diffuse";
		String SPECULAR = "light[0].specular";
		String ATTENUATION = "light[0].attenuation";
	}
	
	public interface Material {
		String AMBIENT = "material.ambient";
		String DIFFUSE = "material.specular";
		String SPECULAR = "material.specular";
		String SHININESS = "material.shininess";
	}
}
