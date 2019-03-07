package fr.voltariuss.dornacraft.playermanager;

/**
 * Classe utilitaire du plugin
 * 
 * @author Voltariuss
 * @version 1.4.0
 *
 */
public final class UtilsPlayerManager {
	
	public static final String SERVER_OWNER = "Voltariuss";
	public static final String SERVER_CO_OWNER = "Glynix";

	/////
	// SQL
	/////

	// Nom des tables du plugin dans la base de donn�es
	public static final String TABLE_PREFIX = "F1_PlayerManager_";
	public static final String TABLE_NAME_PERMISSIONS = TABLE_PREFIX + "Permissions";
	public static final String TABLE_NAME_PLAYERS = TABLE_PREFIX + "Players";
	public static final String TABLE_NAME_SUBRANKS = TABLE_PREFIX + "SubRanks";
	
	/////
	// Level
	/////
	
	// Bornes
	public static final int LEVEL_MIN = 1;
	public static final int LEVEL_MAX = 80;

	// Messages d'erreur
	public static final String LEVEL_MAX_ALREADY_REACH = "Ce joueur a d�j� atteint le niveau maximum.";
	public static final String LEVEL_ALREADY_MIN = "Ce joueur est d�j� au niveau le plus bas.";
	public static final String LEVEL_ALREADY_MIN_WITHOUT_XP = "Ce joueur est d�j� au niveau le plus bas et ne poss�de pas d'xp.";

	// Autres messages
	public static final String LEVEL_UPDATED = "Le joueur �b%s �rest d�sormais niveau �6%d�r.";
	public static final String LEVEL_AND_XP_UPDATED = "Le joueur �b%s �rest d�sormais niveau �6%d �ravec �6%d xp�r.";
	public static final String LEVEL_XP_UPDATED = "Le joueur �b%s �ra d�sormais �6%d xp�r.";
	
	public static final String LEVEL_SET = "Vous avez d�fini le niveau du joueur �b%s �rsur la valeur �6%d�r.";
	public static final String LEVEL_XP_SET = "Vous avez d�finit l'xp du joueur �b%s �rsur la valeur �6%d�r.";
	public static final String LEVEL_RECEIVED = "Le joueur �b%s �ra re�u �6%d niveau(x)�r.";
	public static final String LEVEL_LOST = "Le joueur �b%s �ra perdu �6%d niveau(x)�r.";
	public static final String LEVEL_XP_RECEIVED = "Le joueur �b%s �ra re�u �6%d xp�r.";
	public static final String LEVEL_XP_LOST = "Le joueur �b%s �ra perdu �6%d xp�r.";
	public static final String LEVEL_RESET_XP_AND_LEVEL = "L'xp et le niveau du joueur �b%s �ront bien �t� r�initialis�s.";
	public static final String LEVEL_INFO = "�6Niveau du joueur �b%s �6: �e%d\n�6Quantit� d'xp : �e%d�7/�e%d �8(�7%d%%�8)";
	public static final String LEVEL_INFO_HIMSELF = "�6Votre niveau : �e%d\n�6Quantit� d'xp : �e%d�7/�e%d �8(�7%d%%�8)";
	
	/////
	// Permission
	/////
	
	// Messages d'erreur
	public static final String PERMISSIONS_EMPTY = "Ce joueur ne poss�de pas de permissions particuli�res.";
	public static final String PERMISSION_ALREADY_OWNED = "Ce joueur poss�de d�j� cette permission.";
	public static final String PERMISSION_MISSING = "Ce joueur ne poss�de pas cette permission.";
	
	// Autres messages
	public static final String PERMISSION_ADDED = "La permission �6%s �ra �t� ajout�e au joueur �b%s�r.";
	public static final String PERMISSION_REMOVED = "La permission �6%s �ra �t� retir�e au joueur �b%s�r.";
	public static final String PERMISSIONS_CLEARED = "Toutes les permissions sp�cifiques du joueur �b%s �rlui ont �t� retir�es.";
	public static final String PERMISSIONS_LIST = "�6Permissions du joueur �b%s �6: %s";
	
	/////
	// Pr�fixe
	/////
	
	// Messages d'erreur
	public static final String PREFIX_ALREADY_IN_USE = "Vous �tes d�j� en train d'utiliser ce pr�fixe.";
	public static final String PREFIX_NOT_OWNED = "Vous ne poss�dez pas ce pr�fixe.";
	
	// Inventaire
	public static final String PREFIX_DEFAULT_ITEM_NAME = "�ePr�fixe par d�faut";
	public static final String PREFIX_AVAILABLE_TAG = "�a�lDisponible";
	public static final String PREFIX_UNAVAILABLE_TAG = "�c�lIndisponible";
	public static final String PREFIX_LEVEL_REQUIRED_TAG = "�eNiveau requis : %s%d";
	public static final String PREFIX_ACTUAL_TAG = "�a�lPr�fixe actuel";
	public static final String PREFIX_ACTIVATION_INFO_LEFT_TAG = "�7Clic-gauche pour activer";
	public static final String PREFIX_MORE_INFO_RIGHT_TAG = "�7Clic-droit pour plus d'infos";
	public static final String PREFIX_SUBRANK_TERM_TAG = "�7Sous-rang : ";
	public static final String PREFIX_ITEM_NAME = "�6Pr�fixe : ";
	
	// Autres messages
	public static final String PREFIX_DEFAULT_TYPE = "DEFAULT";
	public static final String PREFIX_UPDATED = "Pr�fixe du joueur �b%s �rmodifi� avec succ�s !";
	public static final String PREFIX_UPDATED_HIMSELF = "Pr�fixe modifi� avec succ�s !";
	public static final String PREFIX_CURRENT = "Pr�fixe actuel du joueur �b%s �r: %s";
	public static final String PREFIX_CURRENT_HIMSELF = "�ePr�fixe actuel : %s";
	
	/////
	// Rangs
	/////
	
	// Messages d'erreur
	public static final String RANK_HAS_HIGHEST = "Le joueur poss�de d�j� le rang le plus �lev�.";
	public static final String RANK_HAS_LOWER = "Le joueur poss�de d�j� le rang le plus bas.";
	public static final String RANK_ALREADY_OWNED = "Le joueur poss�de d�j� ce rang.";
	
	// Rangs
	public static final String RANK_PLAYER = "Joueur";
	public static final String RANK_HELPER = "Guide";
	public static final String RANK_MODERATOR = "Mod�rateur";
	public static final String RANK_ADMIN = "Administrateur";
	
	// Inventaires
	public static final String RANK_AWARDING_TAG = "�e�lClique pour attribuer ce rang";
	public static final String RANK_ACTUAL_TAG = "�c�lRang poss�d� par le joueur";
	public static final String RANK_ITEM_NAME = "�cRang: ";
	
	// Autres messages
	public static final String RANK_UPDATED = "Le rang du joueur �b%s �ra �t� modifi� avec succ�s !";
	public static final String RANK_INFO = "Rang du joueur �b%s �r: %s";
	
	/////
	// Sous-rangs
	/////
	
	// Messages d'erreur
	public static final String SUBRANK_EMPTY_OWNED = "Ce joueur ne poss�de pas de sous-rang.";
	public static final String SUBRANK_NOT_OWNED_SPECIFICATION = "Vous ne poss�dez pas le sous-rang : %s�r.";
	public static final String SUBRANK_ALREADY_OWNED = "Ce joueur poss�de d�j� le sous-rang sp�cifi�.";
	public static final String SUBRANK_NOT_OWNED = "Ce joueur ne poss�de pas le sous-rang sp�cifi�.";
	
	// Sous-rangs
	public static final String SUBRANK_VIP = "VIP";
	public static final String SUBRANK_VIP_PLUS = "VIP+";
	public static final String SUBRANK_EDITOR = "R�dacteur";
	public static final String SUBRANK_BUILDER = "Architecte";
	public static final String SUBRANK_DEVELOPER = "D�veloppeur";
	
	// Inventaire
	public static final String SUBRANK_ITEM_NAME = "�cSous-rang: ";
	public static final String SUBRANK_AWARDING_TAG = "�e�lClique pour attribuer ce sous-rang";
	public static final String SUBRANK_REMOVING_TAG = "�e�lClique pour retirer ce sous-rang";
	
	// Autres messages
	public static final String SUBRANK_AWARDED = "Le sous-rang �6%s �ra bien �t� attribu� au joueur �b%s�r.";
	public static final String SUBRANK_REMOVED = "Le sous-rang �6%s �ra bien �t� retir� au joueur �b%s�r.";
	public static final String SUBRANK_ALL_REMOVED = "Tous les sous-rangs ont �t� retir�s au joueur �b%s�r.";
	public static final String SUBRANK_LIST = "Liste des sous-rangs du joueur �b%s �r: %s";
	
	/////
	// Chat
	/////
	
	// Pr�fixes
	public static final String CHAT_PREFIX_MESSAGE = " �8� ";
	public static final String CHAT_PREFIX_STAFF = "�7[�6�lS�7] ";
	
	// Permission
	public static final String CHAT_PERMISSION_COLOR = "dornacraft.chat.color";
}
