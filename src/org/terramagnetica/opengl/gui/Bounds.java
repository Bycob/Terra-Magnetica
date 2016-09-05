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

package org.terramagnetica.opengl.gui;

import net.bynaryscode.util.maths.geometric.RectangleDouble;

/**
 * Permet d'adapter automatiquement les limites du composant enfant
 * à celui du parent. Les dimensions du composant enfant sont respectées,
 * sauf lorsque cela est impossible. Si aucun composant parent n'est
 * défini, alors la référence est la fenêtre d'affichage.
 * <p>Le composant enfant restera ainsi à l'intérieur du composant parent,
 * avec une marge minimum prédéfinie. Celle-ci est fixée par défaut à
 * {@code 0.1}.
 * @author Louis JEAN
 */
public class Bounds implements Behavior {
	
	public static final double DEFAULT_MARGE = 0.1;
	
	
	private GuiComponent childComponent;
	private GuiComponent boundsComponent;
	
	private double marge = DEFAULT_MARGE;
	
	
	public Bounds(GuiComponent childComponent, GuiComponent boundsComponent) {
		if (childComponent == null) throw new NullPointerException();
		this.childComponent = childComponent;
		this.boundsComponent = boundsComponent;
	}
	
	public void setMarge(double marge) {
		if (marge < 0) throw new IllegalArgumentException("La marge ne peut pas être négative.");
		this.marge = marge;
	}
	
	public double getMarge() {
		return this.marge;
	}
	
	@Override
	public void update() {
		this.childComponent.updateBounds();
		RectangleDouble bChild = this.childComponent.getBoundsGL();
		RectangleDouble bParent = this.boundsComponent == null ? GuiWindow.getInstance().getOrtho().getBounds2D() : this.boundsComponent.getBoundsGL();
		
		double oldWidth = bChild.getWidth();
		double oldHeight = bChild.getHeight();
		
		if (bChild.intersects(bParent)) {
			if (bChild.xmin < bParent.xmin) {
				bChild.xmin = bParent.xmin + this.marge;
				bChild.xmax = Math.min(bChild.xmin + oldWidth, bParent.xmax - this.marge);
			}
			if (bChild.xmax > bParent.xmax) {
				bChild.xmax = bParent.xmax - this.marge;
				bChild.xmin = Math.max(bChild.xmax - oldWidth, bParent.xmin + this.marge);
			}
			if (bChild.ymin > bParent.ymin) {
				bChild.ymin = bParent.ymin - this.marge;
				bChild.ymax = Math.max(bChild.ymin - oldHeight, bParent.ymax + this.marge);
			}
			if (bChild.ymax < bParent.ymax) {
				bChild.ymax = bParent.ymax + this.marge;
				bChild.ymin = Math.min(bChild.ymax + oldHeight, bParent.ymin - this.marge);
			}
			
			this.childComponent.setRealBoundsGL(bChild);
		}
		else {
			this.childComponent.setBoundsGL(this.childComponent.getAbsoluteBoundsGL());
		}
	}
}
