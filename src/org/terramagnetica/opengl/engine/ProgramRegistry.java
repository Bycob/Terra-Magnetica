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

import java.util.ArrayList;
import java.util.HashMap;

import org.terramagnetica.opengl.engine.Shader.ShaderType;

public class ProgramRegistry {
	
	public static final String DEFAULT_PROGRAM_ID = "default";
	
	private Painter painter;
	private HashMap<String, Shader> internalShaders = new HashMap<String, Shader>();
	private HashMap<String, Program> programs = new HashMap<String, Program>();
	
	ProgramRegistry(Painter painter) {
		if (painter.getProgramRegistry() != null) {
			throw new UnsupportedOperationException("you shall not create a program registry on your own.");
		}
		
		this.painter = painter;
		
		//Chargement des différents programmes
		boolean defaultProgramLoaded = addInternalProgram("default3D", "default3D.vs", "default3D.fs", "basics3D.fs", "lighting3D.fs");
		
		if (!defaultProgramLoaded) {
			throw new RuntimeException("Un ou plusieurs shaders n'ont pas été trouvés lors de la création du programme par défaut");
		}
		
		//Positionnement du programme par défaut
		this.programs.put(DEFAULT_PROGRAM_ID, getProgram("default3D"));
	}
	
	public boolean addInternalProgram(String name, String... shaderNames) {
		ArrayList<Shader> shaders = new ArrayList<Shader>();
		
		for (String shaderName : shaderNames) {
			Shader shader = getInternalShader(shaderName);
			
			if (shader == null) {
				return false;
			}
			shaders.add(shader);
		}
		
		return addProgram(name, shaders.toArray(new Shader[0]));
	}
	
	public boolean addProgram(String name, Shader... shaders) {
		try {
			Program program = new Program(shaders);
			this.programs.put(name, program);
			return true;
		} catch (ProgramLinkingException e) {
			System.err.println("programme non linké : " + name);
			e.printStackTrace();
			return false;
		}
	}
	
	public Program getProgram(String name) {
		Program returned = this.programs.get(name);
		return returned;
	}
	
	public Program getDefaultProgram() {
		return getProgram(DEFAULT_PROGRAM_ID);
	}
	
	public Shader getInternalShader(String shaderName) {
		Shader shader;
		
		if ((shader = this.internalShaders.get(shaderName)) == null) {
			String extension = shaderName.substring(shaderName.length() - 2, shaderName.length());
			ShaderType type = extension.equals("fs") ? ShaderType.FRAGMENT_SHADER : (extension.equals("vs") ? ShaderType.VERTEX_SHADER : null);
			
			try {
				shader = Shader.loadInternalShader(shaderName, type);
				this.internalShaders.put(shaderName, shader);
			}
			catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		return shader;
	}
}
