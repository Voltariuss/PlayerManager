package fr.voltariuss.dornacraft.playermanager.features.permission;

import java.util.ArrayList;

public class PermissionGroup {

	public static ArrayList<String> getJoueurPermissions() {
		ArrayList<String> permissions = new ArrayList<>();
		permissions.add("creativegates.use");
		permissions.add("dornacraft.playermanager.level");
		permissions.add("dornacraft.playermanager.prefix");
		return permissions;
	}
	
	public static ArrayList<String> getGuidePermissions() {
		ArrayList<String> permissions = new ArrayList<>();
		permissions.addAll(getJoueurPermissions());
		return permissions;
	}
	
	public static ArrayList<String> getModerateurPermissions() {
		ArrayList<String> permissions = new ArrayList<>();
		permissions.addAll(getGuidePermissions());
		return permissions;
	}
	
	public static ArrayList<String> getAdministrateurPermissions() {
		ArrayList<String> permissions = new ArrayList<>();
		permissions.addAll(getModerateurPermissions());
		return permissions;
	}
}
