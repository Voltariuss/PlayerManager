package fr.voltariuss.dornacraftplayermanager.features.prefix;

import fr.voltariuss.dornacraftplayermanager.features.rank.Rank;

public enum Prefix {
	
	//Préfixes des niveaux
	VAGABOND("§8[§7Vagabond§8] ", 1),
	GUERRIER("§8[§7Guerrier§8] ", 5),
	CHEVALIER("§8[§7Chevalier§8] ", 10),
	BARON("§7[§3Baron§7] ", 15),
	COMTE("§7[§3Comte§7] ", 20),
	MARQUIS("§7[§3Marquis§7] ", 25),
	DUC("§7[§3Duc§7] ", 30),
	PRINCE("§f[§cPrince§f] ", 35),
	ROI("§f[§cRoi§f] ", 40),
	EMPEREUR("§f[§5Empereur§f] ", 50),
	HEROS("§f[§aHéros§f] ", 60),
	LEGENDE("§f[§bLégende§f] ", 70),
	DIVINITE("§f[§6§lDivinité§f] ", 75),
	
	//Préfixes des rangs
	GUIDE("§7[§9G§7] ", 0),
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
	
	@Override
	public String toString() {
		return prefix;
	}
	
	public static String getDefault() {
		return "DEFAULT";
	}
	
	public static Prefix fromString(String prefixType, Rank rank, int level) {
		Prefix prefix = Prefix.VAGABOND;
		
		if(rank == Rank.JOUEUR || rank == Rank.GUIDE) {
			if(prefixType.equalsIgnoreCase(getDefault())) {
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
}
