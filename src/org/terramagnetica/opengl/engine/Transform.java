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
along with BynarysCode. If not, see <http://www.gnu.org/licenses/>.
 </LICENSE> */

package org.terramagnetica.opengl.engine;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.bynaryscode.util.maths.geometric.Vec3d;

public abstract class Transform {
	
	public static Transform newRotation(float rad, Vec3d axis, Vec3d point) {
		Transform result = newTranslation((float) point.x, (float) point.y, (float) point.z);
		result = result.combine(newRotation(rad, axis));
		result = result.combine(newTranslation((float) - point.x, (float) - point.y, (float) - point.z));
		return result;
	}
	
	public static Transform newRotation(float deg, Vec3d axis) {
		return new Rotation(deg, axis);
	}
	
	public static Transform newTranslation(float x, float y, float z) {
		return new Translation(new Vec3d(x, y, z));
	}
	
	public static Transform newTranslation(Vec3d vec) {
		return new Translation(vec);
	}
	
	public static Transform newScale(float scaleX, float scaleY, float scaleZ) {
		return new Scale(new Vec3d(scaleX, scaleY, scaleZ));
	}
	
	public static Transform newMultiTransform(final List<Transform> transforms) {
		MultiTransform result = new MultiTransform();
		for (Transform t : transforms) {
			result.transforms.add(t);
		}
		
		return result;
	}
	
	protected Transform() {}
	
	public Transform combine(Transform other) {
		return new MultiTransform(this, other);
	}
	
	public abstract void applyTransform();
	
	private static class Rotation extends Transform {
		Vec3d axis;
		float angle;
		
		private Rotation(float angle, Vec3d axis) {
			this.angle = angle;
			this.axis = axis;
		}
		
		@Override
		public void applyTransform() {
			GL11.glRotated(angle, axis.x, axis.y, axis.z);
		}
	}
	
	private static class Translation extends Transform {
		
		Vec3d move;
		
		private Translation(Vec3d move) {
			this.move = move.clone();
		}
		
		@Override
		public void applyTransform() {
			GL11.glTranslated(move.x, move.y, move.z);
		}
	}
	
	private static class Scale extends Transform {
		
		Vec3d scale;
		
		private Scale(Vec3d scale) {
			this.scale = scale;
		}
		
		@Override
		public void applyTransform() {
			GL11.glScalef((float) scale.x, (float) scale.y, (float) scale.z);
		}
	}
	
	private static class MultiTransform extends Transform {
		
		ArrayList<Transform> transforms = new ArrayList<Transform>();
		
		private MultiTransform() {
			
		}
		
		private MultiTransform(Transform first, Transform second) {
			this.transforms.add(first);
			this.transforms.add(second);
		}
		
		@Override
		@SuppressWarnings("unchecked")
		public Transform combine(Transform other) {
			MultiTransform result = new MultiTransform();
			
			result.transforms = (ArrayList<Transform>) this.transforms.clone();
			result.transforms.add(other);
			
			return result;
		}
		
		@Override
		public void applyTransform() {
			for (Transform t : this.transforms) {
				t.applyTransform();
			}
		}
	}
}
