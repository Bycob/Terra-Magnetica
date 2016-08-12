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

public class LauncherCreator {
	
	public static final String BIN = "bin/";
	
	/** les nom des bibliothèques situées dans BIN */
	public static final String[] libraries = new String[] {
		//Le jeu
		"terramagnetica-0.4.2.jar",
		
		//lwjgl
		"lwjgl.jar",
		"lwjgl_util.jar",
		
		//ogg vorbis
		"vorbisspi1.0.3.jar",
		"jogg-0.0.7.jar",
		"jorbis-0.0.15.jar",
		"tritonus_share.jar"
	};
	
	public static void main(String[] args) {
		for (String lib : libraries) {
			LauncherUtil.loadLibrary(BIN + lib);
		}
		
		LauncherUtil.setNativePath(BIN + "natives");
		
		LauncherUtil.startApp("org.terramagnetica.creator.Creator", args);
	}
}
