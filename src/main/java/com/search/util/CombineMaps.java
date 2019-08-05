package com.search.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class CombineMaps {

	private HashMap<String, Long> masterHashMap;

	CombineMaps(HashMap<String, Long> inputMap) {
		this.masterHashMap = inputMap;
	}

	public HashMap<String, Long> aggregateData(List<Future<HashMap<String, Long>>> masterList) {
		for (Future<HashMap<String, Long>> futureResult : masterList) {
			HashMap<String, Long> hm = null;
			try {
				hm = futureResult.get();
			} catch (ExecutionException | InterruptedException e) {
				e.printStackTrace();
			}
			Iterator<Entry<String, Long>> itr = hm.entrySet().iterator();
			while (itr.hasNext()) {
				Map.Entry<String, Long> pair = (Map.Entry<String, Long>) itr.next();
				if (masterHashMap.containsKey(pair.getKey())) {
					Long newValue = masterHashMap.get(pair.getKey()) + pair.getValue();
					masterHashMap.put(pair.getKey(), newValue);
				} else {
					masterHashMap.put(pair.getKey(), pair.getValue());
				}
				itr.remove();
			}
		}
		return masterHashMap;
	}

	public void displayData() {
		Set<Entry<String, Long>> d = masterHashMap.entrySet();
		Iterator<Entry<String, Long>> itr = d.iterator();
		while (itr.hasNext()) {
			Map.Entry<String, Long> pair = (Map.Entry<String, Long>) itr.next();
			System.out.println(pair.getKey() + " = " + pair.getValue());
			itr.remove();
		}
	}
}
