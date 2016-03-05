package data;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.BeforeClass;
import org.junit.experimental.theories.Theories;
import org.junit.runner.RunWith;


@RunWith(Theories.class)
public class XlsxFileInfoWriterTest extends AbstractExcelFileInfoWriterTest
{
	final String tempXlsxPath = "temp.xlsx";

	public XlsxFileInfoWriterTest() {
	}

	@BeforeClass
	public static void setUp() throws IOException
	{
		PARAMS = new Fixture[resourcePath.length];
		for (int i = 0; i < PARAMS.length; i++)
		{
			Path path = Paths.get(resourcePath[i]);
			PARAMS[i] = new Fixture(FileInfoTest.fromJsonToFileInfo(path));
		}

	}


	@Override
	protected Workbook getWorkbook(String outputPath) throws IOException {
		return new XSSFWorkbook(new FileInputStream(outputPath));
	}

	@Override
	protected String getTempPath() {
		return tempXlsxPath;
	}

}
