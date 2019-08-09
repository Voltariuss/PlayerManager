package fr.voltariuss.playermanager.features.subrank;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import fr.voltariuss.playermanager.UtilsPlayerManager;
import fr.voltariuss.playermanager.features.prefix.Prefix;

public enum SubRank {

	VIP(UtilsPlayerManager.SUBRANK_VIP, ChatColor.GREEN, ChatColor.GREEN, Material.EMERALD),
	VIP_PLUS(UtilsPlayerManager.SUBRANK_VIP_PLUS, ChatColor.AQUA, ChatColor.AQUA, Material.DIAMOND),
	EDITOR(UtilsPlayerManager.SUBRANK_EDITOR, ChatColor.AQUA, null, Material.BOOK_AND_QUILL),
	BUILDER(UtilsPlayerManager.SUBRANK_BUILDER, ChatColor.DARK_GREEN, null, Material.GRASS),
	DEVELOPER(UtilsPlayerManager.SUBRANK_DEVELOPER, ChatColor.DARK_PURPLE, null, Material.REDSTONE_COMPARATOR);

	/**
	 * @return La couleur des messages, non null
	 */
	public static ChatColor getMessageColor() {
		return ChatColor.WHITE;
	}

	private String name;
	private ChatColor color, pseudoColor;
	private Material material;

	/**
	 * Constructeur
	 * 
	 * @param name        Le nom du sous-rang, non null
	 * @param color       La couleur du sous-rang, non null
	 * @param pseudoColor La couleur du pseudo du joueur possédant le sous-rang, non
	 *                    null
	 * @param material    La matériel associé au sous-rang, non null
	 */
	private SubRank(String name, ChatColor color, ChatColor pseudoColor, Material material) {
		this.setName(name);
		this.setColor(color);
		this.setPseudoColor(pseudoColor);
		this.setMaterial(material);
	}

	/**
	 * @return Le nom du sous-rang, non null
	 */
	public String getName() {
		return name;
	}

	/**
	 * Définit le nom du sous-rang.
	 * 
	 * @param name Le nouveau nom du sous-rang, non null
	 */
	private void setName(String name) {
		this.name = name;
	}

	/**
	 * @return La couleur du sous-rang, non null
	 */
	public ChatColor getColor() {
		return color;
	}

	/**
	 * Définit la couleur du sous-rang.
	 * 
	 * @param color La nouvelle couleur du sous-rang, non null
	 */
	private void setColor(ChatColor color) {
		this.color = color;
	}

	/**
	 * @return La couleur du pseudo du joueur ayant le sous-rang et l'ayant activé
	 *         dans le chat, peut être null
	 */
	public ChatColor getPseudoColor() {
		return pseudoColor;
	}

	/**
	 * Définit la couleur du pseudo du joueur ayant le sous-rang et l'ayant activé
	 * dans le chat.
	 * 
	 * @param pseudoColor La nouvelle couleur du pseudo du joueur, peut être null
	 */
	private void setPseudoColor(ChatColor pseudoColor) {
		this.pseudoColor = pseudoColor;
	}

	/**
	 * @return Le matériel représentant le sous-rang, non null
	 */
	public Material getMaterial() {
		return material;
	}

	/**
	 * Définit le matériel représentant le sous-rang.
	 * 
	 * @param material Le nouveau matériel représentant le sous-rang, non null
	 */
	private void setMaterial(Material material) {
		this.material = material;
	}

	/**
	 * @return Le préfixe du sous-rang, non null
	 */
	public Prefix getPrefix() {
		return Prefix.valueOf(this.name());
	}

	/**
	 * @return Le nom du sous-rang coloré, non null
	 */
	public String getColoredName() {
		return this.getColor() + this.getName();
	}
}
