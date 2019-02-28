package fr.voltariuss.dornacraft.playermanager.features.permission;

import java.util.ArrayList;

/**
 * Classe de définition des différents groupes de permissions
 * 
 * @author Voltariuss
 * @version 1.4.0
 *
 */
public class PermissionGroup {

	/**
	 * @return La liste des permissions du rang Joueur, non null
	 */
	public static ArrayList<String> getJoueurPermissions() {
		ArrayList<String> permissions = new ArrayList<>();
		permissions.add("creativegates.use");
		permissions.add("dornacraft.playermanager.level");
		permissions.add("dornacraft.playermanager.prefix");
		permissions.add("nte.joueur");
		return permissions;
	}
	
	/**
	 * @return La liste des permissions du rang Guide, non null
	 */
	public static ArrayList<String> getGuidePermissions() {
		ArrayList<String> permissions = new ArrayList<>();
		permissions.addAll(getJoueurPermissions());
		permissions.add("-nte.joueur");
		permissions.add("nte.guide");
		return permissions;
	}
	
	/**
	 * @return La liste des permissions du rang Moderateur, non null
	 */
	public static ArrayList<String> getModerateurPermissions() {
		ArrayList<String> permissions = new ArrayList<>();
		permissions.addAll(getGuidePermissions());
		permissions.add("-nte.guide");
		permissions.add("nte.moderateur");
		return permissions;
	}
	
	/**
	 * @return La liste des permissions du rang Administrateur, non null
	 */
	public static ArrayList<String> getAdministrateurPermissions() {
		ArrayList<String> permissions = new ArrayList<>();
		permissions.addAll(getModerateurPermissions());
		permissions.add("-nte.moderateur");
		permissions.add("nte.administrateur");
		return permissions;
	}
}
