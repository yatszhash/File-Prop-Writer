package data;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import data.FileInfo.PROPERTY_NAMES;

@RunWith(Enclosed.class)
public class TextFileInfoWriterTest {

	@RunWith(Theories.class)
	public static class TestCreateLine
	{
		AbstractFileInfoWriter sut;

		static class Fixture
		{
			FileInfo fileInfo;
			String expected;
			FileInfoWritingOption option;
			String delimiter;

			//TODO optionのヴァリエーションに対応
			public Fixture(String dataPath, String delimiter, String expected)
			{
				fileInfo = FileInfoTest.fromJsonToFileInfo(Paths.get(dataPath));

				option = new FileInfoWritingOption();
				option.setDelimiter(delimiter);
				option.intoWritingItem(FileInfo.PROPERTY_NAMES.TYPE);

				this.expected = expected;
			}

			public Fixture(String dataPath, String delimiter, String expected, PROPERTY_NAMES[] writtenProperties)
			{
				fileInfo = FileInfoTest.fromJsonToFileInfo(Paths.get(dataPath));

				option = new FileInfoWritingOption();
				option.setDelimiter(delimiter);
				option.intoWritingItemAll(writtenProperties);

				this.expected = expected;
			}
		}

		@DataPoints
		public static Fixture[] PARAMS;


		@BeforeClass
		public static void setUp() throws Exception
		{
			String case1Expected = "file1.txt" + "," + "text" + System.lineSeparator();
			String case2Expected = "folder2" + "," + System.lineSeparator();
			String case3Expected = "file3.txt" + "\t" + "text" + System.lineSeparator();
			String case4Expected = "file4.txt" + System.lineSeparator();
			String case5Expected = "file5.txt,/file5.txt,text,1.0.0.0" + System.lineSeparator();

			String defaultPath = "src/test/resources/cases/data/FileInfoWriterTest/createLine/";

			PROPERTY_NAMES[] typeOnlyWrittenProperties = {PROPERTY_NAMES.TYPE};
			PROPERTY_NAMES[] allWrittenPropperties = {PROPERTY_NAMES.TYPE, PROPERTY_NAMES.ABSOLUTE_PATH, PROPERTY_NAMES.VERSION};


			PARAMS = new Fixture[] {
				new Fixture(defaultPath + "case1.json", ",", case1Expected, typeOnlyWrittenProperties),
				//type nullがある場合
				new Fixture(defaultPath + "case2.json", ",", case2Expected, typeOnlyWrittenProperties),
				//tab区切り
				new Fixture(defaultPath + "case3.json", "\t", case3Expected, typeOnlyWrittenProperties),
				//なにもプロパティーに指定しなかった場合
				new Fixture(defaultPath + "case4.json", ",", case4Expected, new PROPERTY_NAMES[]{}),
				//全てをプロパティーに指定した場合
				new Fixture(defaultPath + "case5.json", ",", case5Expected, allWrittenPropperties),
			};
		}

		@Theory
		public void test(Fixture p) throws ReflectiveOperationException
		{
			sut = new WriterFactory().createWriter(p.fileInfo, new File("test.txt"), p.option);

			Class[] classArray = {FileInfo.class};
			Object[] args = {p.fileInfo};

			String actual = (String) doPrivateMethod(sut, "createLine", classArray, args);

			assertThat(actual, is(p.expected));
		}

	}

	@RunWith(Theories.class)
	public static class TestWriteContents
	{
		AbstractFileInfoWriter sut;

		static class ContentsFixture
		{
			FileInfo fileInfo;
			String expected;
			FileInfoWritingOption option;
			String delimiter;
			String path;

			//TODO optionに対応。
			ContentsFixture(String dataPath, String expected)
			{
				delimiter = ",";
				option = new FileInfoWritingOption();
				option.setDelimiter(delimiter);
				option.intoWritingItem(FileInfo.PROPERTY_NAMES.TYPE);

				fileInfo = FileInfoTest.fromJsonToFileInfo(Paths.get(dataPath));
				this.expected = expected;
			}
		}

		@DataPoints
		public static ContentsFixture[] PARAMS;

		@BeforeClass
		public static void setUp() throws Exception
		{
			String defaultPath = "src/test/resources/cases/data/FileInfoWriterTest/writeContents/";

			String[] case_expected1 = new String[]{"text1.txt,text/plain", "folder1,", "image1.png,png", "text2.txt,text/plain"};
			StringBuffer output = new StringBuffer();

			Arrays.stream(case_expected1).forEach(s -> output.append(s + System.lineSeparator()));

			PARAMS = new ContentsFixture[]
			{
				new ContentsFixture(defaultPath + "case1.json", output.toString()),
			};
		}

		@Theory
		public void testFromStream(ContentsFixture p) throws ReflectiveOperationException
		{
			sut = new WriterFactory().createWriter(p.fileInfo, new File(".txt"), p.option);

			Class[] classArray = {OutputStream.class};
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

			Object[] args = {outputStream};

			doPrivateMethod(sut, "writeContents", classArray, args);
			String actual = new String(outputStream.toByteArray());
			System.out.println(actual);
			assertThat(actual, is(p.expected));
		}

	}

	public static Object doPrivateMethod(Object target, String method_name, Class[] classArray, Object[] args)
			throws ReflectiveOperationException {
		Class target_class = target.getClass();
		Method method = target_class.getDeclaredMethod(method_name, classArray);
		method.setAccessible(true);

		return method.invoke(target, args);
	}

}
