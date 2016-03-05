package data;

import java.util.Iterator;
import java.util.Stack;

public class FileInfoIterator implements Iterator<FileInfo>
{

	private Stack<Iterator<FileInfo>> stack = new Stack<>();

	public FileInfoIterator(Iterator<FileInfo> iterator)
	{
		stack.push(iterator);
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

	/*debug用*/
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
