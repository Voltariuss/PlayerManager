package fr.voltariuss.dornacraftplayermanager;

import org.bukkit.Material;

public enum SubRank {

	VIP("VIP","§a[VIP]","§a","§a", Material.EMERALD),
	VIP_PLUS("VIP+","§b[VIP+]","§b","§b"),
	REDACTEUR("Rédacteur","§f[§bRédacteur§f]","","§b"),
	ARCHITECTE("Architecte","§f[§2Architecte§f]","","§2"),
	DEVELOPPEUR("Développeur","§f[§5Développeur§f]","","§5");
	
	private String name,prefix,pseudoColor,msgColor,subRankColor;
	private Material material;

	private SubRank(String name, String prefix, String pseudoColor, String subRankColor, Material material) {
		this.name = name;
		this.prefix = prefix;
		this.pseudoColor = pseudoColor;
		this.subRankColor = subRankColor;
		this.msgColor = "§f";
	}

	public String getName() {
		return name;
	}
	
	private void setName(String name) {
		this.name = name;
	}

	public String getPrefix() {
		return prefix;
	}
	
	private void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	public String getColor() {
		return subRankColor;
	}
	
	private void setColor(String color) {
		this.color = color;
	}

	public String getMsgColor() {
		return msgColor;
	}
	
	private void setMsgColor(String msgColor) {
		this.msgColor = msgColor;
	}
	
	public String getColoredName() {
		return subRankColor + name;
	}
	
	public static SubRank fromString(String subRank) {
		for(SubRank sr : SubRank.values()) {
			if(sr.getName().equalsIgnoreCase(subRank)) {
				return sr;
			}
		}
		return null;
	}
}
