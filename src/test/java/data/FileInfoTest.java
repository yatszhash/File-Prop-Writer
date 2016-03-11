package data;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.BeforeClass;
import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

@RunWith(Enclosed.class)
public class FileInfoTest {

	@RunWith(Theories.class)
	public static class TestCompareTo
	{
		public FileInfo sut1;
		public FileInfo sut2;

		static class Fixture
		{
			String[] names = {"", ""};
			int[] childrenNumbers = {0, 0};
			int expected;

			Fixture(String name1, int childrenNumber1, String name2, int childrenNumber2, int expected)
			{
				names[0] = name1;
				names[1] = name2;

				childrenNumbers[0] = childrenNumber1;
				childrenNumbers[1] = childrenNumber2;

				this.expected = expected;
			}
		}


		@DataPoints
		public static Fixture[] PARAMS =
		{
			//どちらもファイル（中身がない)
			new Fixture("a", 0, "c", 0, -1),
			//どちらもフォルダで中身を２つもつ
			new Fixture("a", 2, "c", 2, -1),
			//片方がフォルダで、片方はファイル
			new Fixture("a", 2, "c", 0, 1),
			//片方が大文字で、片方は小文字
			new Fixture("a", 0, "C", 0, -1),
			//同じファイル名
			new Fixture("same", 0, "same", 0, 0),
			//同じファイル名だが、大文字小文字
			new Fixture("SAME", 0, "same", 0, -1),
		};

		@Theory
		public void test(Fixture p) throws Exception
		{
			set(p);
			int result = sut1.compareTo(sut2);
			if (p.expected > 0)
			{
				assertThat(result, is(greaterThan(0)));
			} else if (p.expected < 0)
			{
				assertThat(result, is(lessThan(0)));
			} else
			{
				assertThat(result, is(0));
			}

		}

		@Theory
		public void testReflexivity(Fixture p) throws Exception
		{
			set(p);
			assertThat(sut1.compareTo(sut1), is(0));
			assertThat(sut2.compareTo(sut2), is(0));
		}

		@Theory
		public void testSymmetry(Fixture p) throws Exception
		{
			set(p);
			assertThat(sut1.compareTo(sut2), is(- sut2.compareTo(sut1)));
		}

		//
		@Theory
		public void testTransivity(Fixture p) throws Exception
		{
			set(p);

		}

		public void set(Fixture p)
		{
			sut1 = new FileInfo();
			sut2 = new FileInfo();
			sut1.setName(p.names[0]);
			sut2.setName(p.names[1]);
			IntStream.range(0, p.childrenNumbers[0]).forEach(i -> sut1.addChild(new FileInfo()));
			IntStream.range(0, p.childrenNumbers[1]).forEach(i -> sut2.addChild(new FileInfo()));
		}


	}

	public static FileInfo fromJsonToFileInfo(Path dataPath)
	{
		FileInfo root = null;

		try(JsonReader reader = new JsonReader(Files.newBufferedReader(dataPath)))
		{
			Gson gson = new GsonBuilder().create();

			root = gson.fromJson(reader, FileInfo.class);
			root.sortChildrenRecursive();
		} catch (IOException e)
		{
			throw new UncheckedIOException(e);
		}


		return root;
	}

	@RunWith(Theories.class)
	public static class SortRecursiveTest {

		@DataPoints
		public static Fixture[] PARAMS;

		static class Fixture
		{
			String filePath = "";
			String[] expectedNames = new String[0];
			FileInfo root;

			public Fixture(String filePath, String[] names)
			{
				this.filePath = filePath;
				expectedNames = names;
				Path path = Paths.get(filePath);
				if (path.toString().endsWith(".json"))
				{
					root = FileInfoTest.fromJsonToFileInfo(path);
				} else
				{
					root = new FileInfo(path.toFile());
				}
			}
		}

		@BeforeClass
		public static void setUp() throws Exception
		{
			String[] case3_names = new String[]{"2.txt", "4.txt", "3", "6.txt", "5", "7.txt"};
			String[] case4_names = new String[]{"2", "3", "4.txt"};
			String[] case5_names = new String[]{"2.txt"};
			String[] case6_names = new String[]{"2", "3.txt"};
			String[] case7_names = new String[]{"1", "2", "3", "4.txt"};
			String[] case8_names = new String[]{"text1.txt", "folder1", "image1.png", "text2.txt"};

			String default_path = "src/test/resources/cases/data/FileInfoIteratorTest/";

			PARAMS = new Fixture[]
			{
				//case1: ファイルを指定した場合。
				new Fixture(default_path + "case1.json", new String[0]),
				//case2: 空のフォルダーを指定した場合。
				new Fixture(default_path + "case2.json", new String[0]),
				//3層からなるフォルダーを指定。(folderとfileがalphabet順で交互になるように指定）
				new Fixture(default_path + "case3.json",case3_names),
				//case4: 3層からなる
				new Fixture(default_path + "case4.json",case4_names),
				//case5: 1層から
				new Fixture(default_path + "case5.json",case5_names),
				//case5: 2層から
				new Fixture(default_path + "case6.json",case6_names),
				//case7: 実在するファイルから
				new Fixture(default_path + "case7",case7_names),
				//case8:
				new Fixture(default_path + "case8.json",case8_names),
			};
		}

		@Theory
		public void test(Fixture p)
		{

			List<String> names = new ArrayList<>();

			p.root.sortChildrenRecursive();

			for (FileInfo fileInfo: p.root.getChildrenInfos())
			{
				addNames(fileInfo, names);
			}

			assertThat(names.toArray(), is(p.expectedNames));
		}

		public void addNames(FileInfo fileInfo, List<String> names)
		{
			names.add(fileInfo.getName());
			for (FileInfo child: fileInfo.getChildrenInfos())
			{
				addNames(child, names);
			}

		}



	}



}
