package fr.voltariuss.playermanager.cache;

import java.util.ArrayList;

import fr.voltariuss.playermanager.UtilsPlayerManager;
import fr.voltariuss.playermanager.features.rank.Rank;
import fr.voltariuss.playermanager.features.subrank.SubRank;

public class PlayerCache {

	private Rank rank = Rank.getDefault();
	private int level = UtilsPlayerManager.LEVEL_MIN, xp = 0;
	private String prefixType = UtilsPlayerManager.PREFIX_DEFAULT_TYPE;
	private ArrayList<SubRank> subRanks = new ArrayList<>();
	private ArrayList<String> permissions = new ArrayList<>();
	
	/**
	 * Constructeur
	 * 
	 * @param rank Le rang du joueur, non null
	 * @param level Le niveau du joueur
	 * @param xp La quantité d'xp du joueur
	 * @param prefixType Le type de préfixe du joueur
	 */
	public PlayerCache(Rank rank, int level, int xp, String prefixType) {
		this.setRank(rank);
		this.setLevel(level);
		this.setXp(xp);
		this.setPrefixType(prefixType);
	}

	/**
	 * @return Le rang du joueur, non null
	 */
	public Rank getRank() {
		return rank;
	}

	/**
	 * Définit le rang du joueur.
	 * 
	 * @param rank Le nouveau rang du joueur, non null
	 */
	public void setRank(Rank rank) {
		this.rank = rank;
	}

	/**
	 * @return Le niveau du joueur
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * Définit le niveau du joueur.
	 * 
	 * @param level Le nouveau niveau du joueur
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * @return La quantité d'xp du joueur
	 */
	public int getXp() {
		return xp;
	}

	/**
	 * Définit la quantité d'xp du joueur.
	 * 
	 * @param xp La nouvelle quantité du joueur
	 */
	public void setXp(int xp) {
		this.xp = xp;
	}

	/**
	 * @return Le type de préfixe du joueur, non null
	 */
	public String getPrefixType() {
		return prefixType;
	}

	/**
	 * Définit le type de préfixe du joueur.
	 * 
	 * @param prefixType Le nouveau type de préfixe du joueur, non null
	 */
	public void setPrefixType(String prefixType) {
		this.prefixType = prefixType;
	}

	/**
	 * @return La liste des sous-rangs du joueur, non null
	 */
	public ArrayList<SubRank> getSubRanks() {
		return subRanks;
	}

	/**
	 * Définit la liste des sous-rangs du joueur.
	 * 
	 * @param subRanks La nouvelle liste de sous-rangs du joueur, non null
	 */
	public void setSubRanks(ArrayList<SubRank> subRanks) {
		this.subRanks = subRanks;
	}

	/**
	 * @return La liste des permissions du joueur, non null
	 */
	public ArrayList<String> getPermissions() {
		return permissions;
	}

	/**
	 * Définit la liste des permissions du joueur.
	 * 
	 * @param permissions La nouvelle liste des permissions du joueur, non null
	 */
	public void setPermissions(ArrayList<String> permissions) {
		this.permissions = permissions;
	}
}
