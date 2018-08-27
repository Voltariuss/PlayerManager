package fr.voltariuss.dornacraft.playermanager.features.rank;

import org.bukkit.ChatColor;

import fr.voltariuss.dornacraft.playermanager.features.prefix.Prefix;

public enum Rank {

	JOUEUR(1, "Joueur", ChatColor.GRAY, ChatColor.GRAY),
	GUIDE(2, "Guide", ChatColor.BLUE, ChatColor.WHITE),
	MODERATEUR(3, "Mod�rateur", ChatColor.GOLD, ChatColor.WHITE),
	ADMINISTRATEUR(4, "Administrateur", ChatColor.DARK_RED, ChatColor.RED);
	
	/**
	 * @return Le rang par d�faut, non null
	 */
	public static Rank getDefault() {
		return Rank.JOUEUR;
	}
	
	/**
	 * @param power Le power du rang � rechercher
	 * @return Le rang poss�dant le nombre de power sp�cifi�, peut �tre null
	 */
	public static Rank fromPower(int power) {
		for(Rank r : Rank.values()) {
			if(r.getPower() == power) {
				return r;
			}
		}
		return null;
	}
	
	private int power;
	private String name;
	private ChatColor color, messageColor;
	
	private Rank(int power, String name, ChatColor color, ChatColor messageColor) {
		this.setPower(power);
		this.setName(name);
		this.setColor(color);
		this.setMessageColor(messageColor);
	}

	/**
	 * @return La power du rang, c'est � dire son importance. Plus le nombre est grand, plus il l'est
	 */
	public int getPower() {
		return power;
	}

	/**
	 * D�finit le power du rang.
	 * 
	 * @param power Le nouveau power du rang
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
	 * D�finit le nom du rang.
	 * 
	 * @param name Le nouveau nom du rang, non null
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
	 * D�finit la couleur du rang.
	 * 
	 * @param color La nouvelle couleur du rang, non null
	 */
	private void setColor(ChatColor color) {
		this.color = color;
	}

	/**
	 * @return La couleur des messages envoy�s par le joueur poss�dant le rang, non null
	 */
	public ChatColor getMessageColor() {
		return messageColor;
	}
	
	/**
	 * D�finit la couleur des messages envoy�s par le joueur poss�dant le rang.
	 * 
	 * @param messageColor La nouvelle couleur des messages, non null
	 */
	private void setMessageColor(ChatColor messageColor) {
		this.messageColor = messageColor;
	}
	
	/**
	 * @return Le pr�fixe du rang, non null
	 */
	public Prefix getPrefix() {
		return Prefix.valueOf(this.name());
	}
	
	/**
	 * @return Le nom du rang color�, non null
	 */
	public String getColoredName() {
		return this.getColor() + this.getName();
	}
}
