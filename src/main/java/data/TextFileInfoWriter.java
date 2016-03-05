package data;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Optional;

//TODO STrategy化
public class TextFileInfoWriter extends AbstractFileInfoWriter
{
	public TextFileInfoWriter(FileInfo root, File outputFile)
	{
		super(root, outputFile);
	}

	@Override
	protected void writeContents(OutputStream outPutStream)
	{

		PrintWriter pw = new PrintWriter(outPutStream);
		Iterator<FileInfo> iterator = root.iterator();

		while(iterator.hasNext())
		{
			FileInfo fileInfo = iterator.next();
			String line = createLine(fileInfo);

			pw.print(line);
			//TODO デバッグ終了後削除
			//System.out.println(fileInfo.getName());
		}
		pw.flush();
		pw.close();
	}

	@Override
	protected String createLine(FileInfo fileInfo)
	{
		StringBuffer line = new StringBuffer();
		option.getTargetProperties().keySet().stream().filter(key -> option.getTargetProperties().get(key)
				).forEachOrdered(key -> {line.append(fileInfo.getPropertyString(key));
										line.append(option.getDelimiter());});
		line.deleteCharAt(line.length() - 1);
		line.append(System.lineSeparator());

		return line.toString();
	}


}