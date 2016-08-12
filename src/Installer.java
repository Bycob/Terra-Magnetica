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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Installer {
	
	public static void main(String...args) {
		
	}
	
	public static void copyData(InputStream origine, OutputStream destination) {
		byte[] data = new byte[8];
		int nByte = 0; 
		try {
			while ((nByte = origine.read(data)) == 8) {
				destination.write(data);
			}
			for (int i = 0 ; i < nByte ; i++) {
				destination.write(data[i]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				origine.close();
				destination.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
