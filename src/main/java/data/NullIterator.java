package data;

import java.util.Iterator;

public class NullIterator implements Iterator
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

}
