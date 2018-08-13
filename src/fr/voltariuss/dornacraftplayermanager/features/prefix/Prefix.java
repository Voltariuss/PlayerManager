package fr.voltariuss.dornacraftplayermanager.features.prefix;

import org.bukkit.Material;

import fr.voltariuss.dornacraftplayermanager.features.rank.Rank;

public enum Prefix {
	
	//Pr�fixes des niveaux
	VAGABOND("�8[�7Vagabond�8] ", 1, Material.LEATHER_BOOTS),
	GUERRIER("�8[�7Guerrier�8] ", 5, Material.STONE_SWORD),
	CHEVALIER("�8[�7Chevalier�8] ", 10, Material.IRON_BARDING),
	BARON("�7[�3Baron�7] ", 15, Material.IRON_AXE),
	COMTE("�7[�3Comte�7] ", 20, Material.CHAINMAIL_CHESTPLATE),
	MARQUIS("�7[�3Marquis�7] ", 25, Material.BOW),
	DUC("�7[�3Duc�7] ", 30, Material.DIAMOND),
	PRINCE("�f[�cPrince�f] ", 35, Material.GOLD_HELMET),
	ROI("�f[�cRoi�f] ", 40, Material.ENCHANTMENT_TABLE),
	EMPEREUR("�f[�5Empereur�f] ", 50, Material.FIREBALL),
	HEROS("�f[�aH�ros�f] ", 60, Material.EMERALD),
	LEGENDE("�f[�bL�gende�f] ", 70, Material.BEACON),
	DIVINITE("�f[�6�lDivinit�f] ", 80, Material.GOLDEN_APPLE),
	
	//Pr�fixes des rangs
	GUIDE("�7[�9G�7] ", 0, null),
	MODERATEUR("�6�lMod�rateur ", 0, null),
	ADMINISTRATEUR("�4�lAdministrateur ", 0, null),
	
	//Pr�fixes des sous-rangs
	VIP("�a[VIP] ", 0, null),
	VIP_PLUS("�b[VIP+] ", 0, null),
	REDACTEUR("�f[�bR�dacteur�f] ", 0, null),
	ARCHITECTE("�f[�2Architecte�f] ", 0, null),
	DEVELOPPEUR("�f[�5D�veloppeur�f] ", 0, null),
	
	//Autres pr�fixes
	CO_FONDATEUR("�4�lCo-Fondateur ", 0, null),
	FONDATEUR("�4�lFondateur ", 0, null);
	
	public static String getDefault() {
		return "Default";
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
	
	private String prefix;
	private int requieredLevel;
	private Material material;
	
	private Prefix(String prefix, int requieredLevel, Material material) {
		this.setPrefix(prefix);
		this.setRequieredLevel(requieredLevel);
		this.setMaterial(material);
	}
	
	@Override
	public String toString() {
		return prefix;
	}

	private void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	public int getRequieredLevel() {
		return requieredLevel;
	}
	
	private void setRequieredLevel(int requieredLevel) {
		this.requieredLevel = requieredLevel;
	}

	public Material getMaterial() {
		return material;
	}

	private void setMaterial(Material material) {
		this.material = material;
	}
}
