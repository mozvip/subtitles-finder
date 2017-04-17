package com.github.mozvip.subtitles;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public enum Release {
		
	_0TV( new String[] {"0tv"} ),
	_2HD( new String[] {"2hd"} ),
	_7O9( new String[] {"7o9"} ),
	AAF(new String[] {"aaf"}),
	acROBATT_MBE(new String[]{"acROBATT&MBE"}),
	ADDiCTiON(new String[]{"ADDiCTiON"}),
	ASAP( new String[] {"ASAP", "asa"}),
	AVCHD( new String[] {"avchd"} ),
	AVS(new String[] {"AVS"}),
	BIA( new String[] {"bia", "-bia.", ".bia."} ),
	BWB( new String[] {"BWB"}),
	BTN( new String[] {"BTN"}),
	Chotab(new String[] {"Chotab"}),
	CLUE( new String[] {"CLUE"}),
	COMPULSION( new String[] {"compulsion"}),
	EbP( new String[] {"EbP"}),
	EVOLVE( new String[] {"EVOLVE"}),
	DiViSiON( new String[] {"DiViSiON"}),
	CTU( new String[] {"ctu", "720p-CTU", ".ctu.", "-ctu."} ),
	ECI( new String[] {"ECI"}),
	ETHD( new String[] {"EtHD"}),
	FLEET(new String[] {"FLEET"}),
	FOXM(new String[] {"FOXM"}),
	GFY(new String[] {"GFY"}),
	HDxT(new String[] {"HDxT", "hdxt"}),
	HANNIBAL(new String[] {"hannibal"}),
	HYPE(new String[] {"Hype"}),
	KiNGS(new String[] {"KiNGS"}),
	KILLERS(new String[] {"KILLERS"}),
	LOL_DIMENSION( new String[] {"dimension", ".dim.", "-DIMENSION.", "-DIM.", ".DiMENSiON.", ".lol.", "LOL", "SYS"} ),
	MAGiCDRAGON(new String[] {"MAGiCDRAGON"}),
	MkvCage(new String[] {"MkvCage"}),
	NTB(new String[] {"NTb"}),
	ORENJI( new String[] { "orenji", "ore" }),
	PiLAF(new String[] {"PiLAF"}),
	FEVER( new String[] {"FEVER"} ),
	FOV( new String[] {"fov", "DVDRip.XviD-FoV" } ),
	FQM( new String[] {"FQM"} ),
	HAGGIS( new String[] {".haggis.", "-haggis."} ),
	XOR( new String[] {".xor."} ),
	IMMERSE( new String[] {"immerse", ".IMM."} ),
	CTRLHD( new String[] {"ctrlhd", "CtrlHD", "ctrl"} ),
	NFHD(new String[] {"NFHD"} ),
	NESTA(new String[] {"NESTA"} ),
	TLA(new String[] {"tla"} ),
	SINNERS( new String[] {"sinners"} ),
	NOTV( new String[] {".notv.", "-notv.", "NoTV"} ),
	PDTV( new String[] {"pdtv"} ),
	P0W4( new String[] {"P0W4"} ),
	SAiNTS( new String[] {"-SAiNTS.", "SAiNTS"} ),
	SAPHIRE( new String[] {"SAPHiRE"}),
	SITV( new String[] {"sitv"} ),
	RANDi(new String[] {"RANDi"}),
	RED(new String[] {"RED"}),
	RTV( new String[] {"rtv"} ),
	REWARD(new String[] {"reward"} ),
	RIVER(new String[] {"river"} ),
	SVA(new String[] {"SVA"} ),
	TNS(new String[] {"tns"}),
	TOPAZ(new String[] {"TOPAZ", "tpz-"} ),
	ViPER(new String[] {"ViPER"}),
	YESTV( new String[] {"yestv"} ),
	YOOX(new String[] {"YOOX"} ),
	UNKNOWN(null);
	
	private String[] aliases;
	
	private Release( String[] aliases ) {
		this.aliases = aliases;
	}
	
	public String[] getAliases() {
		return aliases;
	}
	
	public boolean match( String filename ) {
		if (aliases != null) {
			for (String alias : aliases) {
				if (StringUtils.containsIgnoreCase(filename, alias)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static Release firstMatch( String name ) {
		for (Release release : Release.values()) {
			if (release.match(name)) {
				return release;
			}
		}
		return UNKNOWN;
	}

	public static Collection<Release> allMatches(String name) {
		List<Release> groups = new ArrayList<Release>();
		for (Release release : Release.values()) {
			if (release.match(name)) {
				groups.add(release);
			}
		}
		return groups;
	}

}
