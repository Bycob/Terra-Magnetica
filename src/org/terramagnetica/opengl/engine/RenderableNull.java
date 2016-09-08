package org.terramagnetica.opengl.engine;

import net.bynaryscode.util.maths.geometric.Vec3d;

public class RenderableNull extends Renderable {

	@Override
	public void renderAt(Vec3d position, double rotation, Vec3d up, Vec3d scale, Painter painter) {
		return;
	}
}
