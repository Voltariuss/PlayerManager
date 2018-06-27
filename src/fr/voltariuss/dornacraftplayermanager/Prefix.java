package fr.voltariuss.dornacraftplayermanager;

public enum Prefix {
	
	VAGABOND("§8[§7Vagabond§8] ", 1),
	GUERRIER("§8[§7Guerrier§8] ", 10),
	CHEVALIER("§8[§7Chevalier§8] ", 20),
	VIP("§a[VIP] ", 0),
	VIP_PLUS("§b[VIP+] ", 0),
	REDACTEUR("§f[§bRédacteur§f] ", 0),
	ARCHITECTE("§f[§2Architecte§f] ", 0),
	DEVELOPPEUR("§f[§5Développeur§f] ", 0),
	GUIDE("§9[Guide] ", 0),
	MODERATEUR("§6§lModérateur ", 0),
	ADMINISTRATEUR("§4§lAdministrateur ", 0),
	CO_FONDATEUR("§4§lCo-Fondateur ", 0),
	FONDATEUR("§4§lFondateur ", 0);
	
	private String prefix;
	private int requieredLevel;
	
	private Prefix(String prefix, int requieredLevel) {
		this.prefix = prefix;
		this.requieredLevel = requieredLevel;
	}
	
	public int getRequieredLevel() {
		return requieredLevel;
	}
	
	public static Prefix getDefault() {
		return Prefix.VAGABOND;
	}
	
	public static Prefix fromString(String string, Rank rank, int level) {
		Prefix prefix = getDefault();
		
		if(rank == Rank.JOUEUR) {
			if(string.equals("Default")) {
				for(Prefix p : values()) {
					if(p.getRequieredLevel() != 0 && p.getRequieredLevel() <= level) {
						prefix = p;
					} else {
						break;
					}
				}				
			} else {
				prefix = valueOf(string.toUpperCase());
			}
		} else {
			prefix = rank.getPrefix();
		}
		
		return prefix;
	}
	
	public String getPrefix() {
		return prefix;
	}
}
