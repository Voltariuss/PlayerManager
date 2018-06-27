package fr.voltariuss.dornacraftplayermanager.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.permissions.PermissionAttachment;

import fr.voltariuss.dornacraftapi.DornacraftApi;
import fr.voltariuss.dornacraftplayermanager.DornacraftPlayerManager;

public class SQLPerm {
	
	private DornacraftPlayerManager main = DornacraftPlayerManager.getInstance();
	
	/**
	 * Ajoute une permission au joueur et actualise ses permissions.
	 * 
	 * @param player Le joueur concerné.
	 * @param permission La permission à ajouter.
	 * @throws Exception 
	 */
	public void addPermission(OfflinePlayer player, String permission) throws Exception {
		permission = permission.toLowerCase();
		
		if(!hasPermission(player, permission)) {			
			UUID uuid = player.getUniqueId();
			
			//Ajout de la permission dans la base de données
			PreparedStatement query = DornacraftApi.getSqlConnection().getConnection().prepareStatement("INSERT INTO F1_Perm VALUES(?,?)");
			query.setString(1, uuid.toString());
			query.setString(2, permission);
			query.execute();
			query.close();
			
			//Actualisation des permissions
			HashMap<UUID,PermissionAttachment> permissionAttachmentMap = main.getPermissionAttachmentMap();
			permissionAttachmentMap.get(uuid).setPermission(permission, true);
		}
	}
	
	/**
	 * Retire une permission au joueur et actualise ses permissions.
	 * 
	 * @param player Le joueur concernée.
	 * @param permission La permission a retirer.
	 * @throws Exception 
	 */
	public void removePermission(OfflinePlayer player, String permission) throws Exception {
		permission = permission.toLowerCase();
		
		if(hasPermission(player, permission)) {	
			UUID uuid = player.getUniqueId();
			
			//Suppression de la permission dans la base de données.
			PreparedStatement query = DornacraftApi.getSqlConnection().getConnection().prepareStatement("DELETE FROM F1_Perm WHERE uuid = ? AND permission = ?");
			query.setString(1, uuid.toString());
			query.setString(2, permission);
			query.execute();
			query.close();
			
			//Actualisation des permissions.
			HashMap<UUID,PermissionAttachment> permissionAttachmentMap = main.getPermissionAttachmentMap();
			permissionAttachmentMap.get(uuid).unsetPermission(permission);
		}
	}
	
	/**
	 * Retire toutes les permissions entrées en paramètres au joueur et actualise ses permissions.
	 * 
	 * @param player Le joueur concerné.
	 * @throws Exception
	 */
	public void removePermissions(OfflinePlayer player, ArrayList<String> permissions) throws Exception {
		UUID uuid = player.getUniqueId();
		
		if(!permissions.isEmpty()) {
			//Suppression de la permission dans la base de données.
			PreparedStatement query = DornacraftApi.getSqlConnection().getConnection().prepareStatement("DELETE FROM F1_Perm WHERE uuid = ?");
			query.setString(1, uuid.toString());
			query.execute();
			query.close();
			
			//Actualisation des permissions.
			HashMap<UUID,PermissionAttachment> permissionAttachmentMap = main.getPermissionAttachmentMap();
			
			for(String permission : permissions) {
				permissionAttachmentMap.get(uuid).unsetPermission(permission);
			}
		}
	}
	
	/**
	 * Vérifie si le joueur possède une permission.
	 * 
	 * @param player Le joueur concerné.
	 * @param permission La permission à vérifier par rapport au joueur.
	 * @return "vrai" si le joueur possède la permission.
	 * @throws Exception 
	 */
	public boolean hasPermission(OfflinePlayer player, String permission) throws Exception {
		ArrayList<String> permissions = getPermissions(player);
		boolean hasPermission = false;
		
		if(permissions.contains(permission)) {
			hasPermission = true;
		}
		return hasPermission;
	}
	
	/**
	 * Récupère les permissions spécifiques au joueur dans la base de données et les retourne.
	 * 
	 * @param player Le joueur concerné.
	 * @return La liste des permissions du joueur.
	 * @throws Exception 
	 */
	public ArrayList<String> getPermissions(OfflinePlayer player) throws Exception {
		UUID uuid = player.getUniqueId();
		
		PreparedStatement query = DornacraftApi.getSqlConnection().getConnection().prepareStatement("SELECT permission FROM F1_Perm WHERE uuid = ?");
		query.setString(1, uuid.toString());
		
		ResultSet resultat = query.executeQuery();
		ArrayList<String> permissions = new ArrayList<>();
		
		while(resultat.next()) {
			permissions.add(resultat.getString("permission"));
		}		
		query.close();
		
		return permissions;
	}
}
