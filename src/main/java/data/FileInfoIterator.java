package data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import javafx.collections.transformation.SortedList;

public class FileInfoIterator implements Iterator<FileInfo>
{

	private Stack<Iterator<FileInfo>> stack;
	private List<FileInfo> cacheddList;
	private FileInfo storedRoot;

	protected FileInfoIterator(){}

	//TODO clone時に下の階層の順序が変わる問題
	public FileInfoIterator(List<FileInfo> storedList)
	{
		this.cacheddList = storedList;
		stack = new Stack<>();
		stack.push(storedList.iterator());
	}

	public FileInfoIterator(FileInfo root)
	{
		this.storedRoot = root;
		stack = new Stack<>();
		stack.push(root.getChildrenInfos().iterator());
	}

	public void reStack()
	{
		if (stack.empty())
		{

			stack = new Stack<>();

			stack.push(cacheddList.iterator());

			//stack.push(storedRoot.getChildrenInfos().iterator());
		}
	}

	@Override
	public boolean hasNext()
	{
		if (stack.empty())
		{
			return false;
		} else
		{
			Iterator<FileInfo> iterator = stack.peek();

			if (!iterator.hasNext())
			{
				stack.pop();
				return hasNext();
			} else
			{
				return true;
			}
		}
	}

	@Override
	public FileInfo next()
	{
		if (hasNext())
		{
			Iterator<FileInfo> iterator = stack.peek();
			FileInfo component = iterator.next();

			Iterator<FileInfo> nextIterator = component.iterator();

			stack.push(nextIterator);

			return component;
		} else
		{
			return null;
		}
	}

	/*for debug*/
	public static void main(String[] args)
	{
		String filePath = "test_data/FileInfoIteratorTest/case3/";
		FileInfo root = new FileInfo(filePath);

		Iterator<FileInfo> iterator = root.iterator();

		while(iterator.hasNext())
		{
			FileInfo fileInfo = iterator.next();
			//fileInfo.getChildrenInfos().sort(null);
			String type = fileInfo.getType();

			if (type == null)
			{
				type = "";
			}
			System.out.printf("%s: %s\n", fileInfo.getName(), type);
		}
	}

}
