package data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Data;
import lombok.NonNull;

@Data
public class FileInfo implements Iterable<FileInfo>, Comparable<FileInfo>
{
	//TODO Divide into File and Directory class
	//TODO guiからアクセスされるフィールドはプロパティ化
	private Path path;
	private String type;
	@NonNull
	private String name;

	private String version;
	//TODO @NonNUll
	private String absolutePath;
	//TODO 直接のgetter, setterを除去
	private List<FileInfo> childrenInfos;
	private Iterator<FileInfo> iterator;
	private ArrayList<File> fileList;


	/**
	 *
	 * 外部からプロパティにアクセスしやすくするためのenum。
	 */
	public static enum PROPERTY_NAMES
	{
		NAME,
		ABSOLUTE_PATH,
		TYPE,
		VERSION,
	}

	//TODO after test, make private
	/**
	 * constructor for test
	 */
	public FileInfo()
	{
		this.name = "";
		childrenInfos = new ArrayList<>();
	}

	public FileInfo(String filePath)
	{
		path = Paths.get(filePath);
		absolutePath = retrieveAbsolutePath();
		name = retrieveName();
		type = retrieveType(filePath);

		childrenInfos = retrieveChildren();
	}

	/*
	public FileInfo(String absolutePath, String name, String type)
	{
		this.absolutePath = absolutePath;
		this.name = name;
		this.type = type;
		this.childrenInfos = new ArrayList<>();
	}*/


	protected String retrieveAbsolutePath()
	{
		return path.toAbsolutePath().toString();
	}

	protected String retrieveName()
	{
		return path.getFileName().toString();
	}

	protected ArrayList<FileInfo> retrieveChildren()
	{
		ArrayList<FileInfo> childrenInfos = new ArrayList<>();
		fileList = new ArrayList<>();

		//replace IOException with RuntimeException
		try(Stream<Path> childrenFiles = Files.list(path);)
		{
			childrenInfos.addAll(childrenFiles.filter(p -> p != null).map(Path::toString).map(FileInfo::new).collect(Collectors.toList()));
		} catch (NotDirectoryException e)
		{} catch (IOException e)
		{
			e.printStackTrace();
		}

		childrenInfos.sort(null);
		return childrenInfos;
	}


	protected String retrieveType(String filePath)
	{
		//TODO タイプをLinuxとWindowsで区別する。
		Path path = Paths.get(filePath);
		try
		{
			return Files.probeContentType(path).toString();
		} catch(IOException | NullPointerException e)
		{
			return null;
		}
	}

	protected String retrieveVersion(String filePath)
	{
		return FileOperationWithJna.getFileVersion(new File(filePath));
	}

	public FileInfo(File file)
	{
		this(file.getAbsolutePath());
	}

	/**
	 * interface for test
	 */
	public void addChild(FileInfo fileInfo)
	{
		childrenInfos.add(fileInfo);
	}

	/**
	 * interface for test
	 */
	public void addChildren(List<FileInfo> fileInfos)
	{
		childrenInfos.addAll(fileInfos);
	}

	public boolean hasChild()
	{
		if (childrenInfos != null && !childrenInfos.isEmpty()) return true;
		return false;
	}


	@Override
	public Iterator<FileInfo> iterator()
	{
		return new Itr(this);
	}



	public void sortChildrenRecursive()
	{
		childrenInfos.sort(null);
		childrenInfos.stream().forEachOrdered(f -> f.sortChildrenRecursive());
	}


	public void writeProperties(File outputFile) throws IOException
	{
		//TODO reuse writer
		//TODO adapt to writer option
		//FileInfoWritingOption option = new FileInfoWritingOption(delimiter, isWriteSelf);
		AbstractFileInfoWriter writer = new WriterFactory().createWriter(this, outputFile);
		//writer.setOption(option)
		writer.execute();
	}

	public String getPropertyString(PROPERTY_NAMES propertyName) throws IllegalArgumentException
	{
		switch(propertyName)
		{
			case NAME:
				return getName();
			case TYPE:
				return Optional.ofNullable(getType()).orElse("");
			case ABSOLUTE_PATH:
				return Optional.ofNullable(getAbsolutePath()).orElse("");
			case VERSION:
				return Optional.ofNullable(getVersion()).orElse("");
			default:
				throw new IllegalArgumentException();
		}

	}

	//TODO abstract Pathによる比較に変更
	@Override
	public int compareTo(FileInfo anotherFileInfo)
	{
		//TODO Exceptionの内容
		if (anotherFileInfo == null) { throw new IllegalArgumentException();}

		if (this.iterator().hasNext() && !anotherFileInfo.iterator().hasNext()){return 1;}

		if (!this.iterator().hasNext() && anotherFileInfo.iterator().hasNext()){return -1;}

		int caseInsensitiveResult = this.getName().compareToIgnoreCase(anotherFileInfo.getName());
		if (caseInsensitiveResult != 0) {return caseInsensitiveResult;}

		//WindowsならCaseInSensitiveでも同一ファイルとみなせる(例：TEst, tEst)。
		return this.getName().compareTo(anotherFileInfo.getName());
	}

	//TODO equalsをabstractPathを利用してoverride

	private class Itr implements Iterator<FileInfo>
	{
		private Stack<FileInfo> stack;


		Itr(FileInfo root)
		{
			stack = new Stack<>();
			pushAllChildren(root);
		}


		@Override
		public boolean hasNext() {
			return !stack.isEmpty();
		}

		@Override
		public FileInfo next() {
			if (!hasNext()) {return null;}

			FileInfo head = stack.pop();

			pushAllChildren(head);

			return head;
		}

		private void pushAllChildren(FileInfo fileInfo)
		{
			if(fileInfo.hasChild())
			{
				//TOOD worse Performance
				ListIterator<FileInfo> listItr = fileInfo.getChildrenInfos().listIterator(
													fileInfo.getChildrenInfos().size());

				while(listItr.hasPrevious())
				{
					stack.push(listItr.previous());
				}
			}
		}
	}


}
