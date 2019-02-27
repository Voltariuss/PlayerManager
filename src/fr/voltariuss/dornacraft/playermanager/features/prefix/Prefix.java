package fr.voltariuss.dornacraft.playermanager.features.prefix;

import java.sql.SQLException;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;

import fr.voltariuss.dornacraft.playermanager.features.level.LevelManager;
import fr.voltariuss.dornacraft.playermanager.features.rank.Rank;
import fr.voltariuss.dornacraft.playermanager.features.rank.RankManager;

/**
 * Énumération des différents préfixes
 * 
 * @author Voltariuss
 * @version 1.0
 *
 */
public enum Prefix {

	// Préfixes des niveaux
	VAGABOND("§8[§7Vagabond§8] ", 1, Material.LEATHER_BOOTS), 
	GUERRIER("§8[§7Guerrier§8] ", 5, Material.STONE_SWORD), 
	CHEVALIER("§8[§7Chevalier§8] ", 10, Material.IRON_BARDING), 
	BARON("§7[§3Baron§7] ", 15, Material.IRON_AXE), 
	COMTE("§7[§3Comte§7] ", 20, Material.CHAINMAIL_CHESTPLATE), 
	MARQUIS("§7[§3Marquis§7] ", 25, Material.BOW), 
	DUC("§7[§3Duc§7] ", 30, Material.DIAMOND), 
	PRINCE("§f[§cPrince§f] ", 35, Material.GOLD_HELMET), 
	ROI("§f[§cRoi§f] ", 40, Material.ENCHANTMENT_TABLE), 
	EMPEREUR("§f[§5Empereur§f] ", 50, Material.FIREBALL), 
	HEROS("§f[§aHéros§f] ", 60, Material.EMERALD), 
	LEGENDE("§f[§bLégende§f] ", 70, Material.BEACON), 
	DIVINITE("§f[§6§lDivinité§f] ", 80, Material.GOLDEN_APPLE),

	// Préfixes des rangs
	JOUEUR("", 0, null), 
	GUIDE("§7[§9G§7] ", 0, null), 
	MODERATEUR("§6§lModérateur ", 0, null), 
	ADMINISTRATEUR("§4§lAdministrateur ", 0, null),

	// Préfixes des sous-rangs
	VIP("§a[VIP] ", 0, null), 
	VIP_PLUS("§b[VIP+] ", 0, null), 
	REDACTEUR("§f[§bRédacteur§f] ", 0, null), 
	ARCHITECTE("§f[§2Architecte§f] ", 0, null), 
	DEVELOPPEUR("§f[§5Développeur§f] ", 0, null),

	// Autres préfixes
	CO_FONDATEUR("§4§lCo-Fondateur ", 0, null), 
	FONDATEUR("§4§lFondateur ", 0, null);

	/**
	 * @return Le type de préfixe par défaut, non null
	 */
	public static String getDefault() {
		return "DEFAULT";
	}

	/**
	 * Retourne le préfixe associé au joueur spécifié.
	 * 
	 * @param target
	 *            Le joueur ciblé, non null
	 * @return Le préfixe correspondant au joueur ciblé, non null
	 * @throws SQLException
	 *             Si une erreur avec la base de données est détectée
	 */
	public static Prefix fromPlayer(OfflinePlayer target) throws SQLException {
		Rank rank = RankManager.getRank(target);
		String prefixType = PrefixManager.getPrefixType(target);
		int level = LevelManager.getLevel(target);
		Prefix prefix = Prefix.VAGABOND;

		if (rank == Rank.JOUEUR || rank == Rank.GUIDE) {
			if (prefixType.equalsIgnoreCase(getDefault())) {
				for (Prefix p : values()) {
					if (p.getRequieredLevel() != 0 && p.getRequieredLevel() <= level) {
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

			if (rank.equals(Rank.ADMINISTRATEUR)) {
				if (target.getName().equalsIgnoreCase("Voltariuss")) {
					prefix = Prefix.FONDATEUR;
				} else if (target.getName().equalsIgnoreCase("Glynix")) {
					prefix = Prefix.CO_FONDATEUR;
				}
			}
		}
		return prefix;
	}

	private String prefix;
	private int requieredLevel;
	private Material material;

	/**
	 * Constructeur
	 * 
	 * @param prefix
	 *            Le préfixe associé, non null
	 * @param requieredLevel
	 *            Le niveau requis
	 * @param material
	 *            Le matériel associé au préfixe, non null
	 */
	private Prefix(String prefix, int requieredLevel, Material material) {
		this.setPrefix(prefix);
		this.setRequieredLevel(requieredLevel);
		this.setMaterial(material);
	}

	/**
	 * @return Le préfixe sous la forme d'une chaîne de caractères, non null
	 */
	@Override
	public String toString() {
		return prefix;
	}

	/**
	 * Définit le préfixe de l'instance.
	 * 
	 * @param prefix
	 *            Le nouveau préfixe, non null
	 */
	private void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * @return Le niveau requis pour avoir accès au préfixe
	 */
	public int getRequieredLevel() {
		return requieredLevel;
	}

	/**
	 * Définit le niveau requis pour l'utilisation du préfixe.
	 * 
	 * @param requieredLevel
	 *            Le nouveau niveau requis du préfixe
	 */
	private void setRequieredLevel(int requieredLevel) {
		this.requieredLevel = requieredLevel;
	}

	/**
	 * @return Le matériel représentant le préfixe, peut être null
	 */
	public Material getMaterial() {
		return material;
	}

	/**
	 * Définit le matériel représentant le préfixe.
	 * 
	 * @param material
	 *            Le nouveau matériel représentant le préfixe, peut être null
	 */
	private void setMaterial(Material material) {
		this.material = material;
	}
}
