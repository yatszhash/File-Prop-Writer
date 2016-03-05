package data;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;


import data.FileOperationWithJna;

@RunWith(Enclosed.class)
public class FileOperationWithJnaTest
{

	public static class FormatTest
	{
		@Test
		public void formatCanConvert4LengthArrayOflongToStringTest() {
			int[] testCase1 = {1, 2, 3, 4};
			String actual = "1.2.3.4";
			FileOperationWithJna FileOperationWithJna = new FileOperationWithJna();

			Method method = null;
			try {
				method = FileOperationWithJna.class.getDeclaredMethod("format", int[].class);
			} catch (NoSuchMethodException e)
			{
				e.printStackTrace();
			}

			method.setAccessible(true);
			String expected  = null;
			try
			{
				expected = (String) method.invoke(FileOperationWithJna, testCase1);
			} catch (IllegalArgumentException e)
			{
				e.printStackTrace();
			} catch (IllegalAccessException e)
			{
				e.printStackTrace();
			}
			catch (InvocationTargetException e)
			{
				e.printStackTrace();
			}

			assertThat(actual, is(expected));
		}
	}

	public static class HiwordTest
	{
		@Test
		public void hiwordCanGetHigher16bitFrom32bitLong()
		{
			long testcase = 0x98FEABCDL;
			int expected = (int) 0x98FE;

			int actual = FileOperationWithJna.hiword(testcase);

			assertThat(actual, is(expected));
		}
	}

	public static class LowordTest
	{
		@Test
		public void lowordCanGetLower16bitFrom32bitLong()
		{
			long testcase = 0x98FEABCDL;
			int expected = (int) 0xABCDL;

			int actual = FileOperationWithJna.loword(testcase);

			assertThat(actual, is(expected));
		}
	}

	@RunWith(Theories.class)
	public static class GetFileVersionTest
	{
		static final String resourceDefaultPath = "src/test/resources/cases/data/getFileVersionTest/";
		@DataPoints
		public static Fixture[] PARAMs =
		{
			//TODO require cases for zip and cab file
			/*dll file (TortoiseGit's file)*/
			new Fixture(resourceDefaultPath + "dbghelp.dll", "6.11.1.404"),
			/*exe file (TortoiseGit's file)*/
			new Fixture(resourceDefaultPath + "TortoisePlink.exe", "0.64.0.9999"),
			/*text file (empty file)*/
			new Fixture(resourceDefaultPath + "test.txt", ""),
			//TODO zip file
		};

		@Theory
		public void add(Fixture p) throws Exception
		{
			File file = new File(p.filePath);
			String actual=FileOperationWithJna.getFileVersion(file);

			assertThat(actual, is(p.expected));
		}

		static class Fixture
		{
			String filePath ;
			String expected;

			public Fixture(String filePath, String expected) {
				this.filePath = filePath;
				this.expected = expected;
			}
		}
	}
}


