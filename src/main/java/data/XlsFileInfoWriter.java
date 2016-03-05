package data;

import java.io.File;

import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.RichTextString;

public class XlsFileInfoWriter extends AbstractFileInfoExcelWrter
{
	public XlsFileInfoWriter(FileInfo root, File outputFile)
	{
		super(root, outputFile);
		workbook = new HSSFWorkbook();
		initialize();
	}

	@Override
	protected RichTextString toRichTextString(String value) {
		return new HSSFRichTextString(value);
	}

}
