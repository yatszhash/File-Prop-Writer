package data;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import data.FileInfo;

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

		public Fixture(String filePath, String[] names)
		{
			this.filePath = filePath;
			expectedNames = names;
		}
	}

	@BeforeClass
	public static void setUp() throws Exception
	{
		String[] case3_names = new String[]{"2.txt", "4.txt", "3", "6.txt", "5", "7.txt"};

		String default_path = "src/test/resources/cases/data/FileInfoIteratorTest/";

		PARAMS = new Fixture[]
		{
			//case1: ファイルを指定した場合。
			new Fixture(default_path + "case1/1.txt", new String[0]),
			//case2: 空のフォルダーを指定した場合。
			new Fixture(default_path + "case2/2", new String[0]),
			//2層からなるフォルダーを指定。(folderとfileがalphabet順で交互になるように指定）
			new Fixture(default_path + "case3/1",case3_names),
		};
	}

	@Theory
	public void testNext(Fixture p)
	{
		FileInfo root = null;

		root = new FileInfo(p.filePath);

		Iterator<FileInfo> iterator = root.iterator();

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
		FileInfo root = null;

		root = new FileInfo(p.filePath);

		for (int i= 0; i < 2; i++){
			Iterator<FileInfo> iterator = root.iterator();

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
