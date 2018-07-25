package fr.voltariuss.dornacraftplayermanager.playercache;

import java.util.ArrayList;

import fr.voltariuss.dornacraftplayermanager.DornacraftPlayerManager;
import fr.voltariuss.dornacraftplayermanager.Prefix;
import fr.voltariuss.dornacraftplayermanager.Rank;
import fr.voltariuss.dornacraftplayermanager.SubRank;

public class PlayerCache {
	
	private DornacraftPlayerManager main = DornacraftPlayerManager.getInstance();
	private Rank rank = Rank.getDefault();
	private ArrayList<SubRank> subRanks = new ArrayList<>();
	private int level = 1;
	private String prefixType = "DEFAULT";

	public Rank getRank() {
		return rank;
	}

	public void setRank(Rank rank) {
		this.rank = rank;
	}

	public ArrayList<SubRank> getSubRanks() {
		return subRanks;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		int maxLevel = main.getMaxLevel();
		int minLevel = 1;
		
		if(level >= minLevel && level <= maxLevel) {
			this.level = level;
		}
	}
	
	public Prefix getPrefix() {
		return Prefix.fromString(prefixType, rank, level);
	}
	
	public String getPrefixType() {
		return prefixType;
	}
	
	public void setPrefixType(String prefixType) {
		this.prefixType = prefixType;
	}
}
