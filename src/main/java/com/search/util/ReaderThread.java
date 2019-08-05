package com.search.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

public class ReaderThread implements Runnable {

	protected BlockingQueue<String> queue;
	private String fileName;
	private CountDownLatch latch = null;

	ReaderThread(BlockingQueue<String> queue, String fileName,CountDownLatch latch ) {
		this.queue = queue;
		this.fileName = fileName;
		this.latch = latch;
	}

	@Override
	public void run()  {
		FileInputStream inputStream = null;
		Scanner sc = null;
		try {
			inputStream = new FileInputStream(fileName);
			sc = new Scanner(inputStream, "UTF-8");
			while (sc.hasNext()) {
				queue.put(sc.next());
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
				latch.countDown();
				} catch (IOException e) {
				e.printStackTrace();
			}
			sc.close();
		}
	}

}
