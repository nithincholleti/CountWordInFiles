package com.search.util;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

public class Driver {

	static class Control {
		// batch size must be configured accordingly
		static public volatile int batch_size = 10000;
		// number of worker threads are proportional to number of cores in the cpu
		static public volatile int nWorker = 2;
		// one thread per hard disc, if files are sitting on different hard discs
		// threads can be configured accordingly
		static public volatile int nReaderThreads = 1;
	}

	private CountDownLatch latch = null;
	private HashMap<String, Long> result;
	private String outPutFileDirectory;

	public String getOutPutFileDirectory() {
		return outPutFileDirectory;
	}

	public void setOutPutFileDirectory(String outPutFileDirectory) {
		this.outPutFileDirectory = outPutFileDirectory;
	}

	public HashMap<String, Long> getResult() {
		return result;
	}

	public boolean validateUserInput(String inputFileName) {
		boolean isExists = false;
		if (inputFileName.length() > 0) {
			isExists = new File(inputFileName).isFile();
		}
		return isExists;
	}

	/*
	 *  this method writes results to an output file
	 */
	public void writeResultsToOutPutFile() {
		String resultFile = outPutFileDirectory + "\\Result.txt";
		File file = new File(resultFile);
		BufferedOutputStream fos = null;
		BufferedWriter writer = null;
		try {
			fos = new BufferedOutputStream(new FileOutputStream(file));
			writer = new BufferedWriter(new OutputStreamWriter(fos));
			Iterator<Entry<String, Long>> itr = result.entrySet().iterator();
			while (itr.hasNext()) {
				Map.Entry<String, Long> pair = (Map.Entry<String, Long>) itr.next();
				writer.write(pair.getKey() + " = " + pair.getValue());
				writer.newLine();
				itr.remove();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void startProcessingFile(List<String> listFile) {
		/*
		 * optimization 1: We can use dequeue where one worker thread removes from front and second worker thread removes from end
		 * optimization 2: we can use a list to split work among threads using different start and end indexes for each thread 
		 */
		BlockingQueue<String> queue = new LinkedBlockingQueue<String>(Control.batch_size);
		CombineMaps combine = new CombineMaps(new HashMap<String, Long>());

		if (listFile.isEmpty()) {
			System.out.println("No File to process terminating");
			System.exit(1);
		}

		latch = new CountDownLatch(listFile.size());
		List<Future<HashMap<String, Long>>> masterList = new ArrayList<Future<HashMap<String, Long>>>();
		ExecutorService readerService = Executors.newSingleThreadExecutor();
		// submit reader tasks
		for (String fileName : listFile) {
			ReaderThread reader = new ReaderThread(queue, fileName, latch);
			readerService.submit(reader);
		}

		/*
		 * This method also can be further optimized.
		 */
		ExecutorService executorService = Executors.newFixedThreadPool(Control.nWorker);
		for (int i = 0; i < Control.nWorker; i++) {
			WorkerMaps wm = new WorkerMaps(queue, latch);
			Future<HashMap<String, Long>> futureItem = executorService.submit(wm);
			masterList.add(futureItem);
		}
		readerService.shutdown();
		executorService.shutdown();

		while (!readerService.isTerminated() && !executorService.isTerminated()) {
			/*
			 * wait for the threads to finish the task
			 */
		}

		try {
			latch.await();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		/*
		 * This method also can be further optimized.
		 */
		result = combine.aggregateData(masterList);
	}

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		Scanner sc = new Scanner(System.in);
		String loopChar = "Y";
		System.out.println("------------Welcome-------------");
		Driver dr = new Driver();
		List<String> listFile = new ArrayList<String>();
		while (loopChar.equalsIgnoreCase("Y")) {
			System.out.println("Please enter the location of the file, along with file name and extention. (Example: C:\\Folder\\Test_File_4.txt)");
			String fileName = sc.nextLine();
			if (dr.validateUserInput(fileName)) {
				listFile.add(fileName);
			} else {
				System.out.println("Invalid file. Check if the file exists on the disk");
			}
			System.out.println("Do you still want to add one more file to the list? (Y/N) \r\n");
			loopChar = sc.nextLine();
		}
		while (listFile.size() > 0 && true) {
			System.out.println("Enter the outfile directory path. (Example: C:\\output\\) ");
			String outputDirectory = sc.nextLine();
			if (dr.fileIsDirectory(outputDirectory)) {
				dr.setOutPutFileDirectory(outputDirectory);
				break;
			} else {
				System.out.println("Not a valid directory");
			}
		}

		if (listFile.size() > 0) {
			System.out.println("--------Starting the process----------");
			dr.startProcessingFile(listFile);
			dr.writeResultsToOutPutFile();
			System.out.println("Completed. Check For Results.txt file");
			System.exit(0);
		}
	}

	public boolean fileIsDirectory(String outputDirectory) {
		File file = new File(outputDirectory);
		boolean isDirectory = file.isDirectory(); // Check if it's a directory
		return isDirectory;
	}

}
