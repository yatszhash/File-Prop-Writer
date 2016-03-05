package data;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.Optional;

import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

//TODO Test for ExcelWriter
public abstract class AbstractFileInfoExcelWrter extends AbstractFileInfoWriter {

	protected Workbook workbook;
	protected Row currentRow;
	protected CellStyle style;
	private int currentCellNo;

	public AbstractFileInfoExcelWrter() {
		super();
	}

	public AbstractFileInfoExcelWrter(FileInfo root, File outputFile) {
		super(root, outputFile);
	}

	protected void initialize()
	{
		assert workbook != null;

		Font font = workbook.createFont();

		style = workbook.createCellStyle();
		style.setFont(font);
	}


	//TODO Replace IOExcepiton with UncheckedIOException
	@Override
	public void execute() {
		try {
			writeContents(Files.newOutputStream(outputFile.toPath()));
		} catch(IOException e)
		{
			throw new UncheckedIOException(e);
		}
	}

	@Override
	protected String createLine(FileInfo fileInfo) {
		return null;
	}

	@Override
	protected void writeContents(OutputStream outPutStream)
	{
		Sheet sheet = workbook.createSheet();
		Iterator<FileInfo> iterator = root.iterator();

		for(int i=0; iterator.hasNext(); i++)
		{
			currentRow = sheet.createRow(i);
			setPropertiesInRow(iterator.next());
		}

		try
		{
			workbook.write(outPutStream);
		}
		catch(IOException e)
		{
			//TODO 例外詳細化
			throw new UncheckedIOException(e);
		} finally
		{
			try
			{
				outPutStream.close();
				workbook.close();
			} catch(IOException e)
			{
				throw new UncheckedIOException(e);
			}
		}
	}

	protected void setPropertiesInRow(FileInfo fileInfo) {
		currentCellNo = 0;
		option.getTargetProperties().keySet().stream().filter(key -> option.getTargetProperties().get(key))
							.forEachOrdered(key -> setPropertyInCell(fileInfo.getPropertyString(key)));
	}

	protected void setPropertyInCell(String value, int col) {
		Cell cell = currentRow.createCell(col);
		cell.setCellStyle(style);
		cell.setCellValue(toRichTextString(value));
	}

	protected void setPropertyInCell(String value) {
		Cell cell = currentRow.createCell(currentCellNo);
		cell.setCellStyle(style);
		cell.setCellValue(toRichTextString(value));
		currentCellNo++;
	}

	abstract protected RichTextString toRichTextString(String value);

}