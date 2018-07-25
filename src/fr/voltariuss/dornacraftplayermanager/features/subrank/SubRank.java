package fr.voltariuss.dornacraftplayermanager.features.subrank;

import org.bukkit.Material;

public enum SubRank {

	VIP("VIP","§a[VIP]","§a","§a", Material.EMERALD),
	VIP_PLUS("VIP+","§b[VIP+]","§b","§b", Material.DIAMOND),
	REDACTEUR("Rédacteur","§f[§bRédacteur§f]","","§b", Material.BOOK_AND_QUILL),
	ARCHITECTE("Architecte","§f[§2Architecte§f]","","§2", Material.GRASS),
	DEVELOPPEUR("Développeur","§f[§5Développeur§f]","","§5", Material.REDSTONE_COMPARATOR);
	
	public static String getMsgColor() {
		return "§f";
	}
	
	private String name,prefix,pseudoColor,color;
	private Material material;

	private SubRank(String name, String prefix, String pseudoColor, String color, Material material) {
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

	public String getPrefix() {
		return prefix;
	}
	
	private void setPrefix(String prefix) {
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
	
	public String getSubRankColorName() {
		return color + name;
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
