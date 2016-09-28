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
	
	@Override
	public void start() {
		for (Renderable render : renders) {
			render.start();
		}
	}
	
	@Override
	public void stop() {
		for (Renderable render : renders) {
			render.stop();
		}
	}
	
	@Override
	public void reset() {
		for (Renderable render : renders) {
			render.reset();
		}
	}
}
