package frc.lib.util;

import edu.wpi.first.wpilibj.DriverStation;

public class MatchData {
	
	public static enum GameFeature {
		SWITCH_NEAR, SCALE, SWITCH_FAR
	}
	
	public static enum OwnedSide {
		UNKNOWN, LEFT, RIGHT
	}
	
	public static OwnedSide getOwnedSide(GameFeature feature) {
		String gsm = DriverStation.getInstance().getGameSpecificMessage();
		if (gsm == null) {
			return OwnedSide.UNKNOWN;	
		}
		if (gsm.length() < 3) {
			return OwnedSide.UNKNOWN;
		}
		
		int index = feature.ordinal();
		
		if (index >= 3 || index < 0) {
			return OwnedSide.UNKNOWN;
		}
		
		char gd = gsm.charAt(index);
        switch (gd) {
            case 'L':
            	return OwnedSide.LEFT;
            case 'l':
                return OwnedSide.LEFT;
            case 'R':
            	return OwnedSide.RIGHT;
            case 'r':
                return OwnedSide.RIGHT;
            default:
                return OwnedSide.UNKNOWN;
         
        }
	}
}