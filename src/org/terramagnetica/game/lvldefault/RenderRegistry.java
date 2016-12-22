package org.terramagnetica.game.lvldefault;

import java.util.HashMap;

import org.terramagnetica.opengl.engine.Renderable;

public class RenderRegistry {
	
	private HashMap<String, Renderable> renders = new HashMap<String, Renderable>();
	
	public RenderRegistry() {
		
	}
	
	public void registerRender(String key, Renderable render) {
		this.renders.put(key, render);
	}
	
	public Renderable getRender(String key) {
		return this.renders.get(key);
	}
}
