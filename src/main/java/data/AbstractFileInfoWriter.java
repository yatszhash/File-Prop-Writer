package data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.LinkedHashMap;

import data.FileInfo.PROPERTY_NAMES;
import lombok.Data;

@Data
public abstract class AbstractFileInfoWriter {

	protected FileInfo root;

	protected abstract String createLine(FileInfo fileInfo);

	protected abstract void writeContents(OutputStream outPutStream);

	protected File outputFile;
	protected FileInfoWritingOption option;

	public AbstractFileInfoWriter()
	{
		super();
	}

	public AbstractFileInfoWriter(FileInfo root, File outputFile)
	{
		this.root = root;
		option = new FileInfoWritingOption();
		this.outputFile = outputFile;
		Arrays.stream(PROPERTY_NAMES.values()).forEach(v -> option.getTargetProperties().put(v, true));
	}

	public void execute() throws IOException
	{
		writeContents(Files.newOutputStream(outputFile.toPath()));
	}



}