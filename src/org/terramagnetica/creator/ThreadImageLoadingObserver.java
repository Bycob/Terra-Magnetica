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

package org.terramagnetica.creator;

import org.terramagnetica.ressources.ImagesLoader;

public class ThreadImageLoadingObserver implements Runnable {
	
	public static final int NB_IMAGES = 77;
	
	private ProgressionObserver observer;
	
	public ThreadImageLoadingObserver() {
		
	}
	
	public void setObserver(ProgressionObserver o) {
		if (o == null) throw new NullPointerException();
		this.observer = o;
	}
	
	public ProgressionObserver getObserver() {
		return this.observer;
	}
	
	public void start() {
		Thread t = new Thread(this);
		t.setName("Editeur de niveau - Progression du chargement des images");
		t.setDaemon(true);
		
		t.start();
	}
	
	public void stop() {
		this.running = false;
	}
	
	private boolean running = false;
	
	@Override
	public void run() {
		this.running = true;
		
		while (this.running) {
			if (this.observer != null) {
				double prop = (double) ImagesLoader.imgLoadedCount() / NB_IMAGES;
				int max = this.observer.getMaximum();
				this.observer.setValue((int) (prop * max));
			}
			
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
