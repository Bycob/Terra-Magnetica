package org.terramagnetica.opengl.engine;

import java.util.ArrayList;
import java.util.HashMap;

import org.terramagnetica.opengl.engine.Shader.ShaderType;

public class ProgramRegistry {
	
	private Painter painter;
	private HashMap<String, Shader> internalShaders = new HashMap<String, Shader>();
	private HashMap<String, Program> programs = new HashMap<String, Program>();
	
	ProgramRegistry(Painter painter) {
		if (painter.getProgramRegistry() != null) {
			throw new UnsupportedOperationException("you shall not create a program registry on your own.");
		}
		
		this.painter = painter;
		
		//Chargement des différents programmes
		addInternalProgram("default3D", "default3D.vs", "default3D.fs", "lighting3D.fs");
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
		return getProgram("default");
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
