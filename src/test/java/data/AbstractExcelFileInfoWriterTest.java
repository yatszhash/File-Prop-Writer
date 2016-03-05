package data;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theory;
import org.junit.rules.TemporaryFolder;


public abstract class AbstractExcelFileInfoWriterTest {

	protected abstract Workbook getWorkbook(String outputPath) throws IOException;

	protected abstract String getTempPath();

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();
	@DataPoints
	public static Fixture[] PARAMS;
	public static final String[] resourcePath = {"src/test/resources/cases/data/TestExcelFileInfoWriter/case1.json"};

	@BeforeClass
	public static void setUp() throws IOException {
		PARAMS = new Fixture[resourcePath.length];
		for (int i = 0; i < PARAMS.length; i++)
		{
			Path path = Paths.get(resourcePath[i]);
			PARAMS[i] = new Fixture(FileInfoTest.fromJsonToFileInfo(path));
		}

	}

	protected static class Fixture
		{
			FileInfo root;
			FileInfo.PROPERTY_NAMES[] writtenProperties;

			Fixture(FileInfo root)
			{
				this.root = root;
				this.writtenProperties = new FileInfo.PROPERTY_NAMES[]{
					FileInfo.PROPERTY_NAMES.ABSOLUTE_PATH,
					FileInfo.PROPERTY_NAMES.TYPE,
				};
			}

			Fixture(FileInfo root, FileInfo.PROPERTY_NAMES[] writtenProperties)
			{
				this.root = root;
				this.writtenProperties = writtenProperties;
			}
		}

	public AbstractExcelFileInfoWriterTest() {
		super();
	}

	@Theory
	public void testDefault(Fixture params) {
		//TODO very dirty way
		String outputPath = tempFolder.getRoot().getAbsolutePath() + "/" + getTempPath();

		AbstractFileInfoWriter sut = new WriterFactory().createWriter(params.root, new File(outputPath));
		sut.option.intoWritingItemAll(params.writtenProperties);
		List<FileInfo> writternInfos;

		try {
			sut.execute();
			writternInfos = read(outputPath);

		} catch (IOException e)
		{
			throw new UncheckedIOException(e);
		}

		Iterator<FileInfo> iterator = params.root.iterator();

		for (FileInfo actual: writternInfos) {
			if (!iterator.hasNext()){fail();}

			FileInfo expected = iterator.next();

			assertThat(actual.getAbsolutePath(), is(Optional.ofNullable(expected.getAbsolutePath()).orElse("")));
			assertThat(actual.getName(), is(Optional.ofNullable(expected.getName()).orElse("")));
			assertThat(actual.getType(), is(Optional.ofNullable(expected.getType()).orElse("")));
		}

		if (iterator.hasNext())
		{
			fail();
		}

	}

	protected List<FileInfo> read(String outputPath) throws IOException {
		Workbook workbook = getWorkbook(outputPath);

		List<FileInfo> writtenInfos = new ArrayList<FileInfo>();

		readRows(workbook, writtenInfos);

		return writtenInfos;
	}

	protected void readRows(Workbook workbook, List<FileInfo> writtenInfos) {
		Sheet sheet = workbook.getSheet("Sheet0");

		for (int i = 0; i <= sheet.getLastRowNum(); i++)
		{
			FileInfo fileInfo = new FileInfo();
			Row row = sheet.getRow(i);
			Cell nameCell = row.getCell(0);
			fileInfo.setName(nameCell.getStringCellValue());
			System.out.println(nameCell.getStringCellValue());

			Cell absolutePathCell = row.getCell(1);
			fileInfo.setAbsolutePath(absolutePathCell.getStringCellValue());

			Cell typeCell = row.getCell(2);
			fileInfo.setType(typeCell.getStringCellValue());
			writtenInfos.add(fileInfo);
		}
	}

}