package fr.voltariuss.dornacraft.playermanager.features.prefix;

import java.sql.SQLException;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;

import fr.voltariuss.dornacraft.playermanager.features.level.LevelManager;
import fr.voltariuss.dornacraft.playermanager.features.rank.Rank;
import fr.voltariuss.dornacraft.playermanager.features.rank.RankManager;

/**
 * �num�ration des diff�rents pr�fixes
 * 
 * @author Voltariuss
 * @version 1.0
 *
 */
public enum Prefix {

	// Pr�fixes des niveaux
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

	// Pr�fixes des rangs
	JOUEUR("", 0, null), 
	GUIDE("�7[�9G�7] ", 0, null), 
	MODERATEUR("�6�lMod�rateur ", 0, null), 
	ADMINISTRATEUR("�4�lAdministrateur ", 0, null),

	// Pr�fixes des sous-rangs
	VIP("�a[VIP] ", 0, null), 
	VIP_PLUS("�b[VIP+] ", 0, null), 
	REDACTEUR("�f[�bR�dacteur�f] ", 0, null), 
	ARCHITECTE("�f[�2Architecte�f] ", 0, null), 
	DEVELOPPEUR("�f[�5D�veloppeur�f] ", 0, null),

	// Autres pr�fixes
	CO_FONDATEUR("�4�lCo-Fondateur ", 0, null), 
	FONDATEUR("�4�lFondateur ", 0, null);

	/**
	 * @return Le type de pr�fixe par d�faut, non null
	 */
	public static String getDefault() {
		return "DEFAULT";
	}

	/**
	 * Retourne le pr�fixe associ� au joueur sp�cifi�.
	 * 
	 * @param target
	 *            Le joueur cibl�, non null
	 * @return Le pr�fixe correspondant au joueur cibl�, non null
	 * @throws SQLException
	 *             Si une erreur avec la base de donn�es est d�tect�e
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
	 *            Le pr�fixe associ�, non null
	 * @param requieredLevel
	 *            Le niveau requis
	 * @param material
	 *            Le mat�riel associ� au pr�fixe, non null
	 */
	private Prefix(String prefix, int requieredLevel, Material material) {
		this.setPrefix(prefix);
		this.setRequieredLevel(requieredLevel);
		this.setMaterial(material);
	}

	/**
	 * @return Le pr�fixe sous la forme d'une cha�ne de caract�res, non null
	 */
	@Override
	public String toString() {
		return prefix;
	}

	/**
	 * D�finit le pr�fixe de l'instance.
	 * 
	 * @param prefix
	 *            Le nouveau pr�fixe, non null
	 */
	private void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * @return Le niveau requis pour avoir acc�s au pr�fixe
	 */
	public int getRequieredLevel() {
		return requieredLevel;
	}

	/**
	 * D�finit le niveau requis pour l'utilisation du pr�fixe.
	 * 
	 * @param requieredLevel
	 *            Le nouveau niveau requis du pr�fixe
	 */
	private void setRequieredLevel(int requieredLevel) {
		this.requieredLevel = requieredLevel;
	}

	/**
	 * @return Le mat�riel repr�sentant le pr�fixe, peut �tre null
	 */
	public Material getMaterial() {
		return material;
	}

	/**
	 * D�finit le mat�riel repr�sentant le pr�fixe.
	 * 
	 * @param material
	 *            Le nouveau mat�riel repr�sentant le pr�fixe, peut �tre null
	 */
	private void setMaterial(Material material) {
		this.material = material;
	}
}
