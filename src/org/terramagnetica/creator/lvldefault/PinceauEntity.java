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
import org.terramagnetica.game.lvldefault.Entity;

public class PinceauEntity extends Pinceau {
	
	private Class<? extends Entity> entityType;
	
	public PinceauEntity(Class<? extends Entity> type, String name) {
		super(name);
		this.entityType = type;
	}
	
	public PinceauEntity(Class<? extends Entity> type, String name, DecorType decorFilter, int levelFilter) {
		this(type, name);
		
		this.setFilter(new PinceauFilterLevelDefault(decorFilter, levelFilter));
	}
	
	public Entity createInstance(int x, int y, boolean isCased){
		Entity result = null;
		try {
			result = entityType.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		if (result != null){
			if (!isCased){
				result.setCoordonnées(x, y);
			}
			else{
				result.setCoordonnéesCase(x, y);
			}
		}
		
		return result;
	}

	@Override
	public Class<? extends Entity> getElementPaintedClass() {
		return entityType;
	}

	public void setEntityType(Class<? extends Entity> entityType) {
		this.entityType = entityType;
	}
	
	@Override
	public Icon getIcon() {
		ImageIcon result = null;
		try {
			Image img = this.entityType.newInstance().getImage();
			
			if (img != null){
				
				if (img.getHeight(null) > img.getWidth(null)) {
					
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
}