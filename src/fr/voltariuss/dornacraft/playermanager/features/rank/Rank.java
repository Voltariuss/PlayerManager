package fr.voltariuss.dornacraft.playermanager.features.rank;

import fr.voltariuss.dornacraft.playermanager.features.prefix.Prefix;

public enum Rank {

	JOUEUR(1, "Joueur", Prefix.VAGABOND, "§7", "§7"),
	GUIDE(2, "Guide", Prefix.GUIDE, "§9", "§f"),
	MODERATEUR(3, "Modérateur", Prefix.MODERATEUR, "§6", "§e"),
	ADMINISTRATEUR(4, "Administrateur", Prefix.ADMINISTRATEUR, "§4", "§c");
	
	private int power;
	private Prefix prefix;
	private String rankName,rankColor,msgColor;
	
	private Rank(int power, String rankName, Prefix prefix, String rankColor, String msgColor) {
		this.power = power;
		this.rankName = rankName;
		this.prefix = prefix;
		this.rankColor = rankColor;
		this.msgColor = msgColor;
	}

	public int getPower() {
		return power;
	}

	public String getName() {
		return rankName;
	}
	public Prefix getPrefix() {
		return prefix;
	}
	
	public String getColor() {
		return rankColor;
	}

	public String getMsgColor() {
		return msgColor;
	}
	
	public String getColoredName() {
		return getColor() + getName();
	}
	
	public static Rank getDefault() {
		return Rank.JOUEUR;
	}
	
	public static boolean exist(String string) {
		for(Rank r : Rank.values()) {
			if(r.getName().equals(string)) {
				return true;
			}
		}
		return false;
	}
	
	public static Rank fromString(String rank) {
		for(Rank r : Rank.values()) {
			if(r.getName().equals(rank)) {
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
