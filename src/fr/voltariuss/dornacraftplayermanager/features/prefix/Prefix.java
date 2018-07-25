package fr.voltariuss.dornacraftplayermanager;

public enum Prefix {
	
	//Préfixes des niveaux
	VAGABOND("§8[§7Vagabond§8] ", 1),
	GUERRIER("§8[§7Guerrier§8] ", 5),
	CHEVALIER("§8[§7Chevalier§8] ", 10),
	BARON("§8[§7Baron§8] ", 15),
	COMTE("§8[§7Comte§8] ", 20),
	MARQUIS("§8[§7Marquis§8] ", 25),
	DUC("§8[§7Duc§8] ", 30),
	PRINCE("§8[§7Prince§8] ", 35),
	ROI("§8[§7Roi§8] ", 40),
	EMPEREUR("§8[§7Empereur§8] ", 50),
	HEROS("§8[§7Héros§8] ", 60),
	LEGENDE("§8[§7Légende§8] ", 70),
	DIVINITE("§8[§7Divinité§8] ", 80),
	
	//Préfixes des rangs
	GUIDE("§9[Guide] ", 0),
	MODERATEUR("§6§lModérateur ", 0),
	ADMINISTRATEUR("§4§lAdministrateur ", 0),
	
	//Préfixes des sous-rangs
	VIP("§a[VIP] ", 0),
	VIP_PLUS("§b[VIP+] ", 0),
	REDACTEUR("§f[§bRédacteur§f] ", 0),
	ARCHITECTE("§f[§2Architecte§f] ", 0),
	DEVELOPPEUR("§f[§5Développeur§f] ", 0),
	
	//Autres préfixes
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
	
	public static Prefix fromString(String prefixType, Rank rank, int level) {
		Prefix prefix = getDefault();
		
		if(rank == Rank.JOUEUR) {
			if(prefixType.equalsIgnoreCase("DEFAULT")) {
				for(Prefix p : values()) {
					if(p.getRequieredLevel() != 0 && p.getRequieredLevel() <= level) {
						prefix = p;
					} else {
						break;
					}
				}				
			} else {
				prefix = valueOf(prefixType.toUpperCase());
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
