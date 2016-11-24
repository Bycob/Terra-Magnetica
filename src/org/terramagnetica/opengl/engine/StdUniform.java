package org.terramagnetica.opengl.engine;

public interface StdUniform {
	
	public interface View {
		
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
		
	}
}
