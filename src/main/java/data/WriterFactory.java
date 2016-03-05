package data;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.regex.Matcher;

//TODO Test
public class WriterFactory {

	//TODO replace File to Path
	public AbstractFileInfoWriter createWriter(FileInfo fileInfo, File outputFile)
	{
		Path path = outputFile.toPath() ;
		AbstractFileInfoWriter writer = null;

		if (path.toString().endsWith(".xls"))
		{
			writer = new XlsFileInfoWriter(fileInfo, outputFile);
		} else if (path.toString().endsWith(".xlsx"))
		{
			writer = new XlsxFileInfoWriter(fileInfo, outputFile);
		}
		else if (path.toString().endsWith(".txt") || path.toString().endsWith(".csv"))
		{
			writer = new TextFileInfoWriter(fileInfo, outputFile);
		}
		writer.setOption(new FileInfoWritingOption());
		return writer;
	}

	public AbstractFileInfoWriter createWriter(FileInfo fileInfo, File outputFile, FileInfoWritingOption option)
	{
		Path path = outputFile.toPath() ;
		AbstractFileInfoWriter writer = null;

		if (path.toString().endsWith(".xls"))
		{
			writer = new XlsFileInfoWriter(fileInfo, outputFile);
		}
		else if (path.toString().endsWith(".txt") || path.toString().endsWith(".csv"))
		{
			writer = new TextFileInfoWriter(fileInfo, outputFile);
		}
		writer.setOption(option);
		return writer;
	}

}
