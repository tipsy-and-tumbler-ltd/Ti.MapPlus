package ti.map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.appcelerator.kroll.common.Log;

import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.PatternItem;

public class MarchingAnts {
	private ArrayList<List<PatternItem>> marchingAnts = new ArrayList<List<PatternItem>>();
	private int ndx = 0;

	public MarchingAnts() {
		ArrayList<List<PatternItem>> marchingAnts = new ArrayList<List<PatternItem>>();
		PatternItem DASH = new Dash(5);
		PatternItem GAP = new Gap(5);
		marchingAnts.add(Arrays.asList(GAP, DASH, DASH, DASH));
		marchingAnts.add(Arrays.asList(DASH, GAP, DASH, DASH));
		marchingAnts.add(Arrays.asList(DASH, DASH, GAP, DASH));
		marchingAnts.add(Arrays.asList(DASH, DASH, DASH, GAP));
		Log.d(MapModule.LCAT, marchingAnts.toString());
	}

	public List<PatternItem> getNextPattern() {
		int length = marchingAnts.size();
		ndx = (ndx + 1) % length;
		return marchingAnts.get(ndx);
	}
}
