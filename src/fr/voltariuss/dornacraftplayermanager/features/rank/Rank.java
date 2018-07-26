package fr.voltariuss.dornacraftplayermanager.features.rank;

import fr.voltariuss.dornacraftplayermanager.features.prefix.Prefix;

public enum Rank {

	JOUEUR(1, "Joueur", Prefix.VAGABOND, "§7", "§7"),
	GUIDE(2, "Guide", Prefix.GUIDE, "§9", "§f"),
	MODERATEUR(3, "Modérateur", Prefix.MODERATEUR, "§6", "§e"),
	ADMINISTRATEUR(4, "Administrateur", Prefix.ADMINISTRATEUR, "§4", "§c");
	
	private int power;
	private Prefix prefix;
	private String rankName,rankColor,messageColor;
	
	private Rank(int power, String rankName, Prefix prefix, String rankColor, String messageColor) {
		this.power = power;
		this.rankName = rankName;
		this.prefix = prefix;
		this.rankColor = rankColor;
		this.messageColor = messageColor;
	}

	public int getPower() {
		return power;
	}

	public String getRankName() {
		return rankName;
	}
	public Prefix getPrefix() {
		return prefix;
	}
	
	public String getRankColor() {
		return rankColor;
	}

	public String getMessageColor() {
		return messageColor;
	}
	
	public String getRankNameColor() {
		return getRankColor() + getRankName();
	}
	
	public static Rank getDefault() {
		return Rank.JOUEUR;
	}
	
	public static boolean exist(String string) {
		for(Rank r : Rank.values()) {
			if(r.getRankName().equals(string)) {
				return true;
			}
		}
		return false;
	}
	
	public static Rank fromString(String rank) {
		for(Rank r : Rank.values()) {
			if(r.getRankName().equals(rank)) {
				return r;
			}
		}
		return null;
	}
	
	public static Rank fromPower(int power) {
		for(Rank r : Rank.values()) {
			if(r.getPower() == power) {
				return r;
			}
		}
		return null;
	}
}
