package data;

import java.io.File;

import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class XlsxFileInfoWriter extends AbstractFileInfoExcelWrter {

	public XlsxFileInfoWriter(FileInfo root, File outputFile)
	{
		super(root, outputFile);
		workbook = new XSSFWorkbook();
		initialize();
	}

	@Override
	protected RichTextString toRichTextString(String value)
	{
		return new XSSFRichTextString(value);
	}

}
