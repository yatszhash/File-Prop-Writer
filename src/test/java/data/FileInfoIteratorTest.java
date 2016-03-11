package data;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import data.FileInfoTest.SortRecursiveTest.Fixture;

@RunWith(Theories.class)
public class FileInfoIteratorTest {
	/**This test will be fail because of the lack of the test datas.
	 */

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
			root.sortChildrenRecursive();
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
		String[] case9_names = new String[]{"image1.png", "text2.txt"};


		String default_path = "src/test/resources/cases/data/FileInfoIteratorTest/";

		PARAMS = new Fixture[]
		{
			/*//case1: ファイルを指定した場合。
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
			new Fixture(default_path + "case7",case7_names),*/
			//case8
			new Fixture(default_path + "case8.json",case8_names),
			new Fixture(default_path + "case9.json",case9_names),

		};
	}

	@Theory
	public void testNext(Fixture p)
	{

		Iterator<FileInfo> iterator = p.root.iterator();

		List<String> names = new ArrayList<>();


		while(iterator.hasNext())
		{
			FileInfo fileInfo = iterator.next();
			names.add(fileInfo.getName());
		}

		assertThat(names.toArray(), is(p.expectedNames));
	}

	@Theory
	public void testNext2times(Fixture p)
	{

		for (int i= 0; i < 2; i++){
			Iterator<FileInfo> iterator = p.root.iterator();

			List<String> names = new ArrayList<>();


			while(iterator.hasNext())
			{
				FileInfo fileInfo = iterator.next();
				names.add(fileInfo.getName());
			}

			assertThat(names.toArray(), is(p.expectedNames));
		}

	}


}
