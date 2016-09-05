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

package org.terramagnetica.ressources.io;

import net.bynaryscode.util.Color4f;

public class CodableColor extends Color4f implements Codable {
	
	public static CodableColor from(Color4f color) {
		return new CodableColor(color);
	}
	
	public CodableColor(Color4f color) {
		setAlphaf(color.getAlphaf());
		setRedf(color.getRedf());
		setGreenf(color.getGreenf());
		setBluef(color.getBluef());
	}
	
	@Override
	public void code(BufferedObjectOutputStream out) throws GameIOException {
		out.writeFloatField(getRedf(), 0);
		out.writeFloatField(getGreenf(), 1);
		out.writeFloatField(getBluef(), 2);
		out.writeFloatField(getAlphaf(), 3);
	}
	
	@Override
	public CodableColor decode(BufferedObjectInputStream in) throws GameIOException {
		setRedf(in.readFloatField(0));
		setGreenf(in.readFloatField(1));
		setBluef(in.readFloatField(2));
		setAlphaf(in.readFloatField(3));
		return this;
	}
}
