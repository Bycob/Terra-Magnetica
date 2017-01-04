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

public interface StdUniform {
	
	/** Option permettant d'écrire des pixels blancs dans le stencil buffer */
	String STENCIL = "stencils";
	
	String USE_TEXTURES = "useTextures";
	String USE_LIGHTS = "useLights";
	String USE_COLOR = "useColor";
	
	String TEXTURE_0 = "tex0";
	
	public interface View {
		String PROJECTION_MATRIX = "view.projection";
		String CAMERA_MATRIX = "view.camera";
		String MODEL_MATRIX = "view.model";
		String CAMERA_POSITION = "view.cameraPosition";
	}
	
	public interface Light {
		int MAX_LIGHT = 10;
		
		String ACTIVATED = "light[%d].activated";
		String TYPE = "light[%d].type";
		String POSITION = "light[%d].position";
		String AMBIENT = "light[%d].ambient";
		String DIFFUSE = "light[%d].diffuse";
		String SPECULAR = "light[%d].specular";
		String ATTENUATION = "light[%d].attenuation";
	}
	
	public interface Material {
		String AMBIENT = "material.ambient";
		String DIFFUSE = "material.diffuse";
		String SPECULAR = "material.specular";
		String SHININESS = "material.shininess";
	}
}
