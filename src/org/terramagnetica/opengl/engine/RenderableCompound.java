package org.terramagnetica.opengl.engine;

import java.util.ArrayList;

import net.bynaryscode.util.Util;
import net.bynaryscode.util.maths.geometric.Vec3d;

public class RenderableCompound extends Renderable {
	
	private ArrayList<Renderable> renders = new ArrayList<Renderable>();
	
	public RenderableCompound(Renderable... renders) {
		this.addRenders(renders);
	}
	
	public void addRenders(Renderable... renders) {
		Util.addAll(renders, this.renders, false);
	}
	
	public boolean removeRender(Renderable render) {
		return this.renders.remove(render);
	}
	
	public ArrayList<Renderable> getRenders() {
		ArrayList<Renderable> result = new ArrayList<Renderable>();
		result.addAll(this.renders);
		return result;
	}

	@Override
	public void renderAt(Vec3d position, double rotation, Vec3d up, Vec3d scale, Painter painter) {
		for (Renderable render : this.renders) {
			render.renderAt(position, rotation, up, scale, painter);
		}
	}
}
