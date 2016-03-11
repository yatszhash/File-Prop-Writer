package data;

import java.util.Iterator;

public class NullIterator extends FileInfoIterator
{
	public NullIterator() {

	}

	@Override
	public FileInfo next()
	{
		return null;
	}

	@Override
	public boolean hasNext()
	{
		return false;
	}

	public void reStack(){}
}
