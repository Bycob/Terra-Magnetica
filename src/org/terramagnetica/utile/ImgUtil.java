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

package org.terramagnetica.utile;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.terramagnetica.opengl.engine.TextureQuad;

import net.bynaryscode.util.maths.geometric.Vec2i;

public final class ImgUtil {
	
	public static Image getPartOfImage(Image img, int x, int y, int width, int height) {		
		if (img.getWidth(null) < width || img.getHeight(null) < height)
			return img;
		
		BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g2d = result.createGraphics();
		g2d.drawImage(img, 0, 0, result.getWidth(), result.getHeight(), x, y, x + width, y + height, null);
		
		return result;
	}
	
	public static Image getPartOfImage(Image img, TextureQuad clip){
		if (img.getWidth(null) < clip.getWidth() || img.getHeight(null) < clip.getHeight())
			return img;
		
		BufferedImage result = new BufferedImage(clip.getWidth(), clip.getHeight(), BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g2d = result.createGraphics();
		
		Vec2i c1 = clip.getCoinHautGauche();
		Vec2i c2 = clip.getCoinBasDroit();
		g2d.drawImage(img, 0, 0, result.getWidth(), result.getHeight(), c1.x, c1.y, c2.x, c2.y, null);
		
		return result;
	}

	public static ByteBuffer imageToByteBuffer(BufferedImage img){
		ByteBuffer result = null;
		
		int[] pixels = new int[img.getWidth() * img.getHeight()];
		img.getRGB(0, 0, img.getWidth(), img.getHeight(), pixels, 0, img.getWidth());
		
		result = BufferUtils.createByteBuffer(img.getWidth()* img.getHeight()* 4);
		
		for (int i = 0 ; i < pixels.length ; i++){
			int pixel = pixels[i];
			result.put((byte) ((pixel >> 16) & 0xFF));
			result.put((byte) ((pixel >> 8) & 0xFF));
			result.put((byte) ((pixel) & 0xFF));
			result.put((byte) ((pixel >> 24) & 0xFF));
		}
		
		result.flip();
		
		return result;
	}
	
	/**
	 * <i>Méthode établie empiriquement dans un cas précis. à 
	 * retravailler.</i>
	 * @param buf
	 * @param imgWidth
	 * @param imgHeight
	 * @return
	 */
	public static BufferedImage byteBufferToImage(ByteBuffer buf, int imgWidth, int imgHeight) {
		BufferedImage img = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);
		
		int[] rgbArray = new int[imgWidth * imgHeight];
		for (int y = imgHeight - 1 ; y > -1 ; y--) {
			for (int x = 0 ; x < imgWidth ; x++) {
				int argb = 0x00000000;
				argb |= (buf.get() * 2) << 16;
				argb |= (buf.get() * 2) << 8;
				argb |= (buf.get() * 2) << 0;
				argb |= 0xFF << 24; buf.get();
				rgbArray[y * imgWidth + x] = argb;
			}
		}
		
		img.setRGB(0, 0, imgWidth, imgHeight, rgbArray, 0, imgWidth);
		
		return img;
	}
	
	public static BufferedImage toBuffered(Image img){
		BufferedImage result = new BufferedImage(
				img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g = result.createGraphics();
		g.drawImage(img, 0, 0, null);
		g.dispose();
		
		return result;
	}
	
	public static int[] getRGBA(int rgba) {
		int[] arrayRGBA = new int[4];
		
		arrayRGBA[0] = (rgba >> 16) & 0xFF;
		arrayRGBA[1] = (rgba >> 8) & 0xFF;
		arrayRGBA[2] = (rgba) & 0xFF;
		arrayRGBA[3] = (rgba >> 24) & 0xFF;
		
		return arrayRGBA;
	}
	
	public static int[][] getRGBA(BufferedImage img) {
		int[][] arrayRGBA = new int[img.getWidth() * img.getHeight()][4];
		int[] imgRGBA = new int[img.getWidth()* img.getHeight()];
		img.getRGB(0, 0, img.getWidth(), img.getHeight(), imgRGBA, 0, img.getWidth());
		
		for (int i = 0 ; i != imgRGBA.length ; i++) {
			arrayRGBA[i] = getRGBA(imgRGBA[i]);
		}
		
		return arrayRGBA;
	}
	
	public static int[] getRGBA(BufferedImage img, int x, int y) {
		int [] imgRGBA = new int[img.getWidth() * img.getHeight()];
		img.getRGB(0, 0, img.getWidth(), img.getHeight(), imgRGBA, 0, img.getWidth());
		int[] arrayRGBA = getRGBA(imgRGBA[y * img.getWidth() + x]);
		
		return arrayRGBA;
	}
}
