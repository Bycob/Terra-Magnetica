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

package org.terramagnetica.creator.lvldefault;

import java.awt.Image;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.terramagnetica.creator.Pinceau;
import org.terramagnetica.game.lvldefault.DecorType;
import org.terramagnetica.game.lvldefault.LandscapeTile;

public class PinceauLandscape extends Pinceau {
	
	private Class<? extends LandscapeTile> decor;
	private DecorType decorType = DecorType.MONTS;
	
	public PinceauLandscape(Class<? extends LandscapeTile> decor, String name) {
		super(name);
		this.decor = decor;
	}
	
	public LandscapeTile createInstance(){
		LandscapeTile tmp = null;
		try {
			tmp = (LandscapeTile) decor.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		return tmp;
	}

	@Override
	public Class<? extends LandscapeTile> getElementPaintedClass() {
		return decor;
	}
	
	public void setDecorType(DecorType decType) {
		this.decorType = decType;
	}
	
	public DecorType getDecorType() {
		return this.decorType;
	}
	
	@Override
	public Icon getIcon() {
		ImageIcon result = null;
		DecorType type = this.decorType;
		
		try {
			
			Image img = decor.newInstance().getImage(type);
			
			if (img != null) {
				
				if (img.getHeight(null) > img.getWidth(null)){
					img = img.getScaledInstance(img.getWidth(null) * 32 / img.getHeight(null), 32,
							Image.SCALE_DEFAULT);
				}
				else {
					img = img.getScaledInstance(32, img.getHeight(null) * 32 / img.getWidth(null),
							Image.SCALE_DEFAULT);
				}
				
				result = new ImageIcon(img);
			}
			
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public void setComposant(Class<? extends LandscapeTile> decor) {
		this.decor = decor;
	}
}
