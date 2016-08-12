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

package org.terramagnetica.opengl.miscellaneous;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.terramagnetica.utile.ImgUtil;

public class Screenshot {
	
	public static BufferedImage takeScreenshot() {
		int h = Display.getHeight();
		int w = Display.getWidth();
		
		ByteBuffer pixels = BufferUtils.createByteBuffer(w * h * 4).order(ByteOrder.nativeOrder());
		GL11.glReadPixels(0, 0, w, h, GL11.GL_RGBA, GL11.GL_BYTE, pixels);
		
		BufferedImage img = ImgUtil.byteBufferToImage(pixels, w, h);
		
		return img;
	}
}
