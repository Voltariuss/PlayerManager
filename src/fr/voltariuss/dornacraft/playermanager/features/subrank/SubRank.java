package fr.voltariuss.dornacraft.playermanager.features.subrank;

import org.bukkit.Material;

import fr.voltariuss.dornacraft.playermanager.features.prefix.Prefix;

public enum SubRank {

	VIP("VIP", Prefix.VIP, "§a", "§a", Material.EMERALD),
	VIP_PLUS("VIP+", Prefix.VIP_PLUS, "§b", "§b", Material.DIAMOND),
	REDACTEUR("Rédacteur", Prefix.REDACTEUR, "", "§b", Material.BOOK_AND_QUILL),
	ARCHITECTE("Architecte", Prefix.ARCHITECTE, "", "§2", Material.GRASS),
	DEVELOPPEUR("Développeur", Prefix.DEVELOPPEUR, "", "§5", Material.REDSTONE_COMPARATOR);
	
	public static String getMsgColor() {
		return "§f";
	}
	
	private String name,pseudoColor,color;
	private Prefix prefix;
	private Material material;

	private SubRank(String name, Prefix prefix, String pseudoColor, String color, Material material) {
		this.setName(name);
		this.setPrefix(prefix);
		this.setPseudoColor(pseudoColor);
		this.setColor(color);
		this.setMaterial(material);
	}

	public String getName() {
		return name;
	}
	
	private void setName(String name) {
		this.name = name;
	}

	public Prefix getPrefix() {
		return prefix;
	}
	
	private void setPrefix(Prefix prefix) {
		this.prefix = prefix;
	}
	
	public String getPseudoColor() {
		return pseudoColor;
	}
	
	private void setPseudoColor(String pseudoColor) {
		this.pseudoColor = pseudoColor;
	}
	
	public String getColor() {
		return color;
	}
	
	private void setColor(String color) {
		this.color = color;
	}
	
	public Material getMaterial() {
		return material;
	}
	
	private void setMaterial(Material material) {
		this.material = material;
	}
	
	public String getColoredName() {
		return color + name;
	}
	
	public static SubRank fromString(String subRank) {
		SubRank subRankToReturn = null;
		
		for(SubRank sr : SubRank.values()) {
			if(sr.getName().equalsIgnoreCase(subRank)) {
				subRankToReturn = sr;
				break;
			}
		}
		return subRankToReturn;
	}
}
