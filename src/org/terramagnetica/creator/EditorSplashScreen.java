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

package org.terramagnetica.creator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import org.terramagnetica.ressources.ImagesLoader;

@SuppressWarnings("serial")
public class EditorSplashScreen extends JFrame implements ProgressionObserver {
	
	public static final int WIDTH = 500;
	public static final int HEIGHT = 350;
	
	private JProgressBar progressBar = new JProgressBar();
	
	private ThreadImageLoadingObserver threadObserver = new ThreadImageLoadingObserver();
	
	public EditorSplashScreen() {
		super();
		
		this.setSize(WIDTH, HEIGHT);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.setUndecorated(true);
		
		if (ImagesLoader.aimantIcon != null) {
			this.setIconImage(ImagesLoader.aimantIcon);
		}
		
		
		JPanel content = new JPanel();
		
		content.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		content.setLayout(new BorderLayout());
		
		if (ImagesLoader.splashScreen != null) {
			JLabel img = new JLabel(new ImageIcon(ImagesLoader.splashScreen));
			content.add(img, BorderLayout.CENTER);
		}
		
		this.progressBar.setPreferredSize(new Dimension(WIDTH, 18));
		content.add(this.progressBar, BorderLayout.SOUTH);
		
		this.setContentPane(content);
		
		
		this.threadObserver.setObserver(this);
		this.threadObserver.start();
		
		
		this.setVisible(true);
	}
	
	@Override
	public void dispose() {
		this.threadObserver.stop();
		super.dispose();
	}
	
	@Override
	public int getMaximum() {
		return 100;
	}

	@Override
	public void setValue(int v) {
		this.progressBar.setValue(v);
		this.progressBar.repaint();
	}
	
	/**
	 * Cette classe permet d'ajouter un délai avant la fermeture du splash
	 * screen (car il est vraiment trop beau <3)
	 * @author Louis JEAN
	 *
	 */
	private class ThreadClose extends Thread {
		
		@Override
		public void run() {
			long time = System.currentTimeMillis();
			
			while (System.currentTimeMillis() - time < 3000) {
				EditorSplashScreen.this.requestFocus();
				
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			EditorSplashScreen.super.dispose();
		}
	}
}
