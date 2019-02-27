package fr.voltariuss.dornacraft.playermanager.features.rank;

import java.util.ArrayList;

import org.bukkit.ChatColor;

import fr.voltariuss.dornacraft.playermanager.features.permission.PermissionGroup;
import fr.voltariuss.dornacraft.playermanager.features.prefix.Prefix;

/**
 * Énumération des rangs
 * 
 * @author Voltariuss
 * @version 1.0
 *
 */
public enum Rank {

	JOUEUR(1, "Joueur", ChatColor.GRAY, ChatColor.GRAY, PermissionGroup.getJoueurPermissions()), 
	GUIDE(2, "Guide", ChatColor.BLUE, ChatColor.WHITE, PermissionGroup.getGuidePermissions()), 
	MODERATEUR(3, "Modérateur", ChatColor.GOLD, ChatColor.WHITE, PermissionGroup.getModerateurPermissions()), 
	ADMINISTRATEUR(4,"Administrateur", ChatColor.DARK_RED, ChatColor.RED, PermissionGroup.getAdministrateurPermissions());

	/**
	 * @return Le rang par défaut, non null
	 */
	public static Rank getDefault() {
		return Rank.JOUEUR;
	}

	/**
	 * @param power
	 *            Le pouvoir du rang à rechercher
	 * @return Le rang possédant le nombre de power spécifié, peut être null
	 */
	public static Rank fromPower(int power) {
		for (Rank r : Rank.values()) {
			if (r.getPower() == power) {
				return r;
			}
		}
		return null;
	}

	/**
	 * @param string
	 *            Le nom du rank à rechercher, non null
	 * @return Le rang possédant le nom spécifié, peut être null
	 */
	public static Rank fromString(String string) {
		for (Rank r : Rank.values()) {
			if (r.name().equalsIgnoreCase(string)) {
				return r;
			}
		}
		return null;
	}

	private int power;
	private String name;
	private ChatColor color, messageColor;
	private ArrayList<String> permissions;

	/**
	 * Constructeur
	 * 
	 * @param power
	 *            Le pouvoir du rang
	 * @param name
	 *            Le nom du rang, non null
	 * @param color
	 *            La couleur du rang, non null
	 * @param messageColor
	 *            La couleur des messages du rang, non null
	 * @param permissions
	 *            La liste des permissions associées, non null
	 */
	private Rank(int power, String name, ChatColor color, ChatColor messageColor, ArrayList<String> permissions) {
		this.setPower(power);
		this.setName(name);
		this.setColor(color);
		this.setMessageColor(messageColor);
		this.setPermissions(permissions);
	}

	/**
	 * @return La pouvoir du rang, c'est à dire son importance. Plus le nombre est
	 *         grand, plus il l'est
	 */
	public int getPower() {
		return power;
	}

	/**
	 * Définit le pouvoir du rang.
	 * 
	 * @param power
	 *            Le nouveau pouvoir du rang
	 */
	private void setPower(int power) {
		this.power = power;
	}

	/**
	 * @return Le nom du rang, non null
	 */
	public String getName() {
		return name;
	}

	/**
	 * Définit le nom du rang.
	 * 
	 * @param name
	 *            Le nouveau nom du rang, non null
	 */
	private void setName(String name) {
		this.name = name;
	}

	/**
	 * @return La couleur du rang, non null
	 */
	public ChatColor getColor() {
		return color;
	}

	/**
	 * Définit la couleur du rang.
	 * 
	 * @param color
	 *            La nouvelle couleur du rang, non null
	 */
	private void setColor(ChatColor color) {
		this.color = color;
	}

	/**
	 * @return La couleur des messages envoyés par le joueur possédant le rang, non
	 *         null
	 */
	public ChatColor getMessageColor() {
		return messageColor;
	}

	/**
	 * Définit la couleur des messages envoyés par le joueur possédant le rang.
	 * 
	 * @param messageColor
	 *            La nouvelle couleur des messages, non null
	 */
	private void setMessageColor(ChatColor messageColor) {
		this.messageColor = messageColor;
	}

	/**
	 * @return La liste des permissions associée au rang, non null
	 */
	public ArrayList<String> getPermissions() {
		return permissions;
	}

	/**
	 * Définit les permissions du rang.
	 * 
	 * @param permissions
	 *            La nouvelle liste de permissions du rang, non null
	 */
	private void setPermissions(ArrayList<String> permissions) {
		this.permissions = permissions;
	}

	/**
	 * @return Le préfixe du rang, non null
	 */
	public Prefix getPrefix() {
		return Prefix.valueOf(this.name());
	}

	/**
	 * @return Le nom du rang coloré, non null
	 */
	public String getColoredName() {
		return this.getColor() + this.getName();
	}
}
