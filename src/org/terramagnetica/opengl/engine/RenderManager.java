package org.terramagnetica.opengl.engine;

import java.util.ArrayList;
import java.util.HashMap;

import org.terramagnetica.opengl.miscellaneous.Animation;

public class RenderManager implements Animation {
	
	public class RenderEntry {
		private String ID;
		private Renderable render;
		
		private boolean isDefault = false;
		
		private RenderEntry(String ID, Renderable object) {
			
		}
	}
	
	private String defaultID = null;
	private HashMap<String, RenderEntry> renderMap = new HashMap<String, RenderEntry>();
	
	private RenderEntry currentEntry = null;
	private ArrayList<RenderEntry> effects = new ArrayList<RenderEntry>();
	
	public RenderManager() {
		
	}
	
	private RenderEntry getEntryChecked(String id) {
		RenderEntry entry = this.renderMap.get(id);
		if (entry == null) throw new IllegalArgumentException("No RenderObject matches id : " + id);
		return entry;
	}
	
	public void putRender(String id, Renderable object) {
		if (id == null) throw new NullPointerException("id should be != null");
		if (object == null) throw new NullPointerException("object should be != null");
		
		RenderEntry newEntry = new RenderEntry(id, object);
		this.renderMap.put(id, newEntry);
		
		if (this.renderMap.size() == 1) {
			newEntry.isDefault = true;
			this.defaultID = id;
		}
	}
	
	public void setRenderDefault(String id) {
		RenderEntry newDefault = getEntryChecked(id);
		
		if (this.defaultID != null) {
			RenderEntry oldDefault = this.renderMap.get(this.defaultID);
			oldDefault.isDefault = false;
		}
		
		this.defaultID = id;
		newDefault.isDefault = true;
	}
	
	public void render(String id) {
		RenderEntry rendered = getEntryChecked(id);
	}
	
	public void addEffect(String id) {
		this.effects.add(getEntryChecked(id));
	}
	
	public boolean removeEffect(String id) {
		return this.effects.remove(getEntryChecked(id));
	}
	
	public boolean hasEffect(String id) {
		return this.effects.contains(getEntryChecked(id));
	}
	
	public String getDefaultRenderID() {
		return this.defaultID;
	}
	
	@Override
	public void start() {
		
	}

	@Override
	public void stop() {
		
	}

	@Override
	public void reset() {
		
	}
	
	public Renderable getRender() {
		return null;
	}
}
