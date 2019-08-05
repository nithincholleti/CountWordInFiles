package com.search.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import com.search.util.Driver;

public class TestCases {

	@Test
	public void testForValidateUserInput() {
		Driver drive = new Driver();
		String fileName = "C:\\Users\\nithi\\Desktop\\Search_Words\\Sample Files\\Test_File_4.txt";
		assertTrue(drive.validateUserInput(fileName));
		fileName = "abc.txt";
		assertFalse(drive.validateUserInput(fileName));
		fileName = "";
		assertFalse(drive.validateUserInput(fileName));
	}
	
	@Test
	public void testForValidDirectory() {
		Driver drive = new Driver();
		String fileName = "C:\\Users\\nithi\\Desktop\\Search_Words\\Sample Files\\";
		assertTrue(drive.fileIsDirectory(fileName));
		fileName = "X:\\Users\\nitin.cholleti\\Downloads\\TestData\\";
		assertFalse(drive.validateUserInput(fileName));
		fileName = "";
		assertFalse(drive.validateUserInput(fileName));
	}

	@Test
	public void matchWordsWithexpectedResultSingleFile() {

		Driver drive = new Driver();

		HashMap<String, Long> expectedResult = new HashMap<String, Long>();
		expectedResult.put("a", 6L);
		expectedResult.put("be", 3L);
		expectedResult.put("test", 3L);
		expectedResult.put("in", 3L);
		expectedResult.put("this", 6L);
		expectedResult.put("count", 3L);
		expectedResult.put("multiple", 3L);
		expectedResult.put("reading", 3L);
		expectedResult.put("capable", 3L);
		expectedResult.put("is", 3L);
		expectedResult.put("program", 6L);
		expectedResult.put("file", 3L);
		expectedResult.put("strings", 3L);
		expectedResult.put("of", 3L);
		expectedResult.put("files", 3L);
		expectedResult.put("must", 3L);
		expectedResult.put("to", 3L);

		String fileName = "C:\\Users\\nithi\\Desktop\\Search_Words\\Sample Files\\Test_File_4.txt";
		List<String> list = new ArrayList<>();
		list.add(fileName);
		drive.startProcessingFile(list);

		assertTrue(compareMaps(expectedResult, drive.getResult()));
		expectedResult.remove("to");
		assertFalse(compareMaps(expectedResult, drive.getResult()));
	}
	
	@Test
	public void matchWordsWithexpectedResultSingleFilepunctuation() {

		Driver drive = new Driver();

		HashMap<String, Long> expectedResult = new HashMap<String, Long>();
		expectedResult.put("a", 6L);
		expectedResult.put("be", 3L);
		expectedResult.put("test", 3L);
		expectedResult.put("in", 3L);
		expectedResult.put("this", 6L);
		expectedResult.put("count", 3L);
		expectedResult.put("multiple", 3L);
		expectedResult.put("reading", 3L);
		expectedResult.put("capable", 3L);
		expectedResult.put("is", 3L);
		expectedResult.put("program", 6L);
		expectedResult.put("file", 3L);
		expectedResult.put("strings", 3L);
		expectedResult.put("of", 3L);
		expectedResult.put("files", 3L);
		expectedResult.put("must", 3L);
		expectedResult.put("to", 3L);

		String fileName = "C:\\Users\\nithi\\Desktop\\Search_Words\\Sample Files\\Test_File_5.txt";
		List<String> list = new ArrayList<>();
		list.add(fileName);
		drive.startProcessingFile(list);

		assertTrue(compareMaps(expectedResult, drive.getResult()));
		expectedResult.remove("to");
		assertFalse(compareMaps(expectedResult, drive.getResult()));
	}
	
	@Test
	public void runforMultipleFiles() {

		Driver drive = new Driver();

		HashMap<String, Long> expectedResult = new HashMap<String, Long>();
		expectedResult.put("a", 12L);
		expectedResult.put("be", 6L);
		expectedResult.put("test", 6L);
		expectedResult.put("in", 6L);
		expectedResult.put("this", 12L);
		expectedResult.put("count", 6L);
		expectedResult.put("multiple", 6L);
		expectedResult.put("reading", 6L);
		expectedResult.put("capable", 6L);
		expectedResult.put("is", 6L);
		expectedResult.put("program", 12L);
		expectedResult.put("file", 6L);
		expectedResult.put("strings", 6L);
		expectedResult.put("of", 6L);
		expectedResult.put("files", 6L);
		expectedResult.put("must", 6L);
		expectedResult.put("to", 6L);

		String fileName = "C:\\Users\\nithi\\Desktop\\Search_Words\\Sample Files\\Test_File_4.txt";
		List<String> list = new ArrayList<>();
		list.add(fileName);
		list.add(fileName);
		drive.startProcessingFile(list);

		assertTrue(compareMaps(expectedResult, drive.getResult()));
		expectedResult.remove("to");
		assertFalse(compareMaps(expectedResult, drive.getResult()));
	}

	
	@Test
	public void checkForAllKeys() {

		Driver drive = new Driver();
		HashMap<String, Long> expectedResult = new HashMap<String, Long>();
		expectedResult.put("a", 6L);
		expectedResult.put("be", 3L);
		expectedResult.put("test", 3L);
		expectedResult.put("in", 3L);
		expectedResult.put("this", 6L);
		expectedResult.put("count", 3L);
		expectedResult.put("multiple", 3L);
		expectedResult.put("reading", 3L);
		expectedResult.put("capable", 3L);
		expectedResult.put("is", 3L);
		expectedResult.put("program", 6L);
		expectedResult.put("file", 3L);
		expectedResult.put("strings", 3L);
		expectedResult.put("of", 3L);
		expectedResult.put("files", 3L);
		expectedResult.put("must", 3L);
		String fileName = "C:\\Users\\nithi\\Desktop\\Search_Words\\Sample Files\\Test_File_4.txt";
		List<String> list = new ArrayList<>();
		list.add(fileName);
		drive.startProcessingFile(list);
		assertFalse(compareMaps(expectedResult, drive.getResult()));
	}

	@Test
	public void emptyFile() {

		Driver drive = new Driver();
		String fileName = "C:\\Users\\nithi\\Desktop\\Search_Words\\Sample Files\\empty_file.txt";
		List<String> list = new ArrayList<>();
		list.add(fileName);
		drive.startProcessingFile(list);
		assertTrue(drive.getResult().isEmpty());
	}


	public boolean compareMaps(HashMap<String, Long> expectedResult, HashMap<String, Long> output) {
		boolean flag = true;
		Iterator<Entry<String, Long>> itr = output.entrySet().iterator();
		if (output.isEmpty()) {
			return false;
		}
		while (itr.hasNext()) {
			Map.Entry<String, Long> pair = (Map.Entry<String, Long>) itr.next();
			if (expectedResult.containsKey(pair.getKey())) {
				if (pair.getValue() != expectedResult.get(pair.getKey())) {
					flag = false;
					System.out.println("False");
				}
			} else {
				flag = false;
			}
			itr.remove();
		}
		return flag;
	}

}