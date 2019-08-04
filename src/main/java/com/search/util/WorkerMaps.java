package com.search.util;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

public class WorkerMaps implements Callable<HashMap<String, Long>> {

	private HashMap<String, Long> storeMap;
	protected BlockingQueue<String> queue;
	private CountDownLatch latch = null;
	
	WorkerMaps(BlockingQueue<String> queue, CountDownLatch latch) {
		this.storeMap = new HashMap<String, Long>();
		this.queue = queue;
		this.latch = latch;
	}

	public HashMap<String, Long> call() {
		while ( !(latch.getCount() == 0 && queue.isEmpty()) ) {
			if (!queue.isEmpty()) {
				String str = queue.poll();
				try {
					if (str != null && str != "") {
						str = str.toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
						if (str.length() > 0) {
							updateMap(str);
						}
					}
				} catch (Exception ex) {
					// Thread.sleep(1000);
					System.out.println("Broke " + Thread.currentThread().getId() + " " + str + " " + queue.size() + " "
							+ queue.peek() + " " + queue.isEmpty());
					ex.printStackTrace();
				}
			}
		}
		return storeMap;
	}

	private void updateMap(String in) {
		String chk = null;
		if (in != null) {
			chk = in.toLowerCase();
			if (storeMap.containsKey(chk)) {
				Long value = storeMap.get(chk);
				AtomicLong temp = new AtomicLong(value);
				storeMap.put(chk, temp.incrementAndGet());
			} else {
				storeMap.put(chk, 1L);
			}
		}
	}

}
