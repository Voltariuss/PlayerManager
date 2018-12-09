package fr.voltariuss.dornacraft.playermanager.features.permission;

import java.util.ArrayList;

public class PermissionGroup {

	public static ArrayList<String> getJoueurPermissions() {
		ArrayList<String> permissions = new ArrayList<>();
		permissions.add("creativegates.use");
		permissions.add("dornacraft.playermanager.level");
		permissions.add("dornacraft.playermanager.prefix");
		permissions.add("nte.joueur");
		return permissions;
	}
	
	public static ArrayList<String> getGuidePermissions() {
		ArrayList<String> permissions = new ArrayList<>();
		permissions.addAll(getJoueurPermissions());
		permissions.add("-nte.joueur");
		permissions.add("nte.guide");
		return permissions;
	}
	
	public static ArrayList<String> getModerateurPermissions() {
		ArrayList<String> permissions = new ArrayList<>();
		permissions.addAll(getGuidePermissions());
		permissions.add("-nte.guide");
		permissions.add("nte.moderateur");
		return permissions;
	}
	
	public static ArrayList<String> getAdministrateurPermissions() {
		ArrayList<String> permissions = new ArrayList<>();
		permissions.addAll(getModerateurPermissions());
		permissions.add("-nte.moderateur");
		permissions.add("nte.administrateur");
		return permissions;
	}
}
