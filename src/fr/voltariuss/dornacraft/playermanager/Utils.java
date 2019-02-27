package fr.voltariuss.dornacraft.playermanager;

/**
 * Classe utilitaire du plugin
 * 
 * @author Voltariuss
 * @version 1.0
 *
 */
public final class Utils {

	/////
	// SQL
	/////

	// Nom des tables de la base de données correspondantes au plugin
	public static final String PREFIX_TABLE = "F1_PlayerManager_";

	public static final String TABLE_NAME_PERMISSIONS = PREFIX_TABLE + "Permissions";
	public static final String TABLE_NAME_PLAYERS = PREFIX_TABLE + "Players";
	public static final String TABLE_NAME_SUBRANKS = PREFIX_TABLE + "SubRanks";
}
