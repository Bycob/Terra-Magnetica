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

import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.terramagnetica.game.Level;
import org.terramagnetica.ressources.io.BufferedObjectInputStream;
import org.terramagnetica.ressources.io.BufferedObjectOutputStream;
import org.terramagnetica.ressources.io.GameIOException;

/**
 * Cet objet est une boite de sélection de fichier qui intègre les 
 * méthode pour lire un niveau et pour l'écrire dans un fichier, à
 * l'aide des {@link BufferedObjectInputStream} et {@link BufferedObjectOutputStream}.
 * <p>On peut aussi l'utiliser comme un {@link JFileChooser} classique.
 */
@SuppressWarnings("serial")
public class NiveauSaver extends JFileChooser {
	
	private Component parent;
	private File currentFile = null;
	private ExtensionFileFilter filter;
	private Level lvlSavedCopy = null;
	private boolean isSaving = false;
	
	public NiveauSaver(){
		this(null);
	}
	
	public NiveauSaver(Component parent) {
		super();
		filter = new ExtensionFileFilter(new String[]{".mlv"}, "Niveau Terra Magnetica");
		this.addChoosableFileFilter(filter);
		this.setFileFilter(filter);
		this.parent = parent;
	}
	
	public void reinit(){
		currentFile = null;
	}
	
	public String getCurrentFileName() {
		if (currentFile == null) {
			return "";
		}
		else {
			return currentFile.getName();
		}
	}
	
	public File getCurrentFile() {
		return this.currentFile;
	}
	
	public boolean isSaved(Level lvl){
		if (lvl == null)
			return true;
		return (lvl.equals(lvlSavedCopy));
	}
	
	public void save(Level lvl){
		if (currentFile == null){
			saveAs(lvl);
		} else {
			
			BufferedObjectOutputStream oos = null;
			
			try {
				oos = new BufferedObjectOutputStream(new FileOutputStream(this.currentFile));
				oos.writeLevel(lvl);
				oos.close();
				
				lvlSavedCopy = lvl.clone();
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null,
						"Le fichier en cours n'existe pas/plus.",
						"Erreur",
						JOptionPane.ERROR_MESSAGE);
				return;
				
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null,
						"Impossible d'enregistrer le fichier : IOException",
						"Erreur",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			finally{
				try {
					if (oos != null)
						oos.close();
				} catch(IOException e){
					
				}
			}
			
			JOptionPane.showMessageDialog(null,
					"Votre fichier a bien été enregistré.",
					"Fichier enregistré !",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	public void saveAs(Level lvl){
		this.isSaving = true;
		
		int option = this.showSaveDialog(this.parent);
		
		if (option == JFileChooser.APPROVE_OPTION){
			
			this.currentFile = this.getSelectedFile();
			BufferedObjectOutputStream oos = null;
			
			try {
				
				oos = new BufferedObjectOutputStream(new FileOutputStream(currentFile));
				oos.writeLevel(lvl);
				oos.close();
				
				this.lvlSavedCopy = lvl.clone();
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null,
						"Il y a eu un problème au cours de l'enregistrement de votre fichier.",
						"Erreur",
						JOptionPane.ERROR_MESSAGE);
				return;
				
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null,
						"Exception : IOException",
						"Erreur",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			finally{
				try {
					
					if (oos != null)
						oos.close();
				}catch(IOException e){
					
				}
			}
			

			JOptionPane.showMessageDialog(null,
					"Votre fichier a bien été enregistré.",
					"Fichier enregistré !",
					JOptionPane.INFORMATION_MESSAGE);
		}
		this.isSaving = false;
	}
	
	/**
	 * Affiche le dialogue pour choisir un fichier à ouvrir. Si un
	 * fichier est sélectionné, alors le niveau qu'il contient est
	 * lu et retourné. Sinon, la méthode renvoie {@code null}.
	 * @return
	 */
	public Level open(){
		
		int option = this.showOpenDialog(this.parent);
		
		if (option == JFileChooser.APPROVE_OPTION){
			return this.open(this.getSelectedFile());
		}
		
		return null;
	}
	
	/**
	 * Renvoie le niveau que contient le fichier passé en paramètre.
	 * Si le fichier ne contient aucun niveau, ou qu'il n'existe pas,
	 * la méthode renvoie {@code null} et affiche un message d'erreur.
	 * @param fileName
	 * @return
	 */
	public Level open(File fileName) {

		Level result = null;
		this.currentFile = fileName;
		BufferedObjectInputStream ois = null;
		
		try {
			
			ois = new BufferedObjectInputStream(new FileInputStream(currentFile));
			result = ois.readLevel();
			ois.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,
					"Le fichier sélectionné n'existe pas/plus : " + fileName.getPath(),
					"Erreur",
					JOptionPane.ERROR_MESSAGE);
			
		} catch (GameIOException e) {
			e.printStackTrace();
			ObjectInputStream ois2 = null;
			try  {
				ois2 = new ObjectInputStream(new FileInputStream(this.currentFile));
				result = (Level) ois2.readObject();
				ois2.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			} finally {
				if (ois2 != null) {
					try {
						ois2.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,
					"Exception : IOException",
					"Erreur",
					JOptionPane.ERROR_MESSAGE);
			
		}
		finally{
			try {
				if (ois != null)
					ois.close();
			}catch(IOException e){
				
			}
		}
			
		if (result != null) lvlSavedCopy = result.clone();
		
		return result;
	}
	
	@Override
	public void approveSelection() {
		//Uniquement lors de la sauvegarde.
		if (this.isSaving) {
			if (!this.getSelectedFile().getPath().endsWith(".mlv")) {
				setSelectedFile(new File(getSelectedFile().getPath() + ".mlv"));
			}
			if (getSelectedFile().exists()) {
				int option = JOptionPane.showConfirmDialog(null,
						"Le fichier selectionné existe déjà. Voulez-vous le remplacer ?",
						"Fichier déjà existant",
						JOptionPane.WARNING_MESSAGE);
				if (option == JOptionPane.CANCEL_OPTION) {
					super.cancelSelection();
					return;
				}
			}
		}
		super.approveSelection();
	}
}
