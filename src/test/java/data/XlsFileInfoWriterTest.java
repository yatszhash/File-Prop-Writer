package data;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.Theories;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class XlsFileInfoWriterTest extends AbstractExcelFileInfoWriterTest
{
	public final String tempPath = "temp.xls";
	public XlsFileInfoWriterTest() {}

	@Override
	protected String getTempPath()
	{
		return tempPath;
	}


	@Override
	protected Workbook getWorkbook(String outputPath) throws IOException {
		POIFSFileSystem tempFile = new POIFSFileSystem(new FileInputStream(outputPath));
		return new HSSFWorkbook(tempFile);
	}

}
