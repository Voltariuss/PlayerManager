package fr.voltariuss.dornacraftplayermanager;

public enum SubRank {

	VIP("VIP","§a[VIP]","§a","§a"),
	VIP_PLUS("VIP+","§b[VIP+]","§b","§b"),
	REDACTEUR("Rédacteur","§f[§bRédacteur§f]","","§b"),
	ARCHITECTE("Architecte","§f[§2Architecte§f]","","§2"),
	DEVELOPPEUR("Développeur","§f[§5Développeur§f]","","§5");
	
	private String name,prefix,pseudoColor,msgColor,subRankColor;

	private SubRank(String name, String prefix, String pseudoColor, String subRankColor) {
		this.name = name;
		this.prefix = prefix;
		this.pseudoColor = pseudoColor;
		this.subRankColor = subRankColor;
		this.msgColor = "§f";
	}

	public String getName() {
		return name;
	}

	public String getPrefix() {
		return prefix;
	}

	public String getPseudoColor() {
		return pseudoColor;
	}

	public String getMsgColor() {
		return msgColor;
	}
	
	public String getSubRankColor() {
		return subRankColor;
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
