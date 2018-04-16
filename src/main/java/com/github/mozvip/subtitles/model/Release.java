package com.github.mozvip.subtitles.model;

import org.apache.commons.lang3.StringUtils;

public enum Release {
		
	_0TV( new String[] {"0tv"} ),
	_2HD( new String[] {"2hd"} ),
	_7O9( new String[] {"7o9"} ),
	AAF(new String[] {"aaf"}),
	acROBATT_MBE(new String[]{"acROBATT&MBE"}),
	ADDiCTiON(new String[]{"ADDiCTiON"}),
	ASAP( new String[] {"ASAP", "asa"}),
    ATeam,
    AVC,
	AVCHD( new String[] {"avchd"} ),
	AVS_SVA( new String[] {"AVS", "SVA"} ),
	BATV,
    Belex,
	BEURACK,
	BIA,
	BWB,
	BTN,
	Chotab,
	CLUE,
	COMPULSION,
	CTU,
	DEAL,
	DiViSiON,
	DON,
	EbP,
	ECI,
	ETHD,
	EVOLVE,
	ExKinoRay,
	FUM,
	GoldiesIn,
	Grym,
	FLEET,
	FOXM,
	GECKOS(new String[] {"GECKOS"}),
	GFY(new String[] {"GFY"}),
	HDxT(new String[] {"HDxT", "hdxt"}),
	HANNIBAL(new String[] {"hannibal"}),
	HYPE(new String[] {"Hype"}),
	IMPERIUM,
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
	NFHD,
	NESTA,
	ORGANiC,
	TLA(new String[] {"tla"} ),
	SINNERS( new String[] {"sinners"} ),
	NOTV( new String[] {".notv.", "-notv.", "NoTV"} ),
	PDTV( new String[] {"pdtv"} ),
	P0W4( new String[] {"P0W4"} ),
	QOQ,
	SAiNTS( new String[] {"-SAiNTS."} ),
	SAPHIRE,
	SITV,
	SPARKS,
	STRiFE,
	RANDi,
	RED,
	RTV,
	REWARD,
	RIVER,
	TNS,
	TOPAZ(new String[] {"tpz-"} ),
	ViPER,
    VISUM,
	YESTV,
	YOOX;
	
	private String[] aliases;
	
	Release( String[] aliases ) {
		this.aliases = aliases;
	}

    Release() {
    }

	public boolean match( String filename ) {
		if (aliases != null) {
			for (String alias : aliases) {
				if (StringUtils.containsIgnoreCase(filename, alias)) {
					return true;
				}
			}
		}
		return StringUtils.containsIgnoreCase(filename, name());
	}
	
	public static Release firstMatch( String name ) {
		for (Release release : Release.values()) {
			if (release.match(name)) {
				return release;
			}
		}
		return null;
	}

}
