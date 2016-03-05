package data;

import java.util.Arrays;
import java.util.LinkedHashMap;

import lombok.Data;

/**
 * To Retain options for FileInfoWriter.
 * @author
 *
 */
@Data
public class FileInfoWritingOption
{
	private Boolean isWithHeader;
	private String type;
	private ENCODES encode;
	private String delimiter;
	private int depthLayer;
	private boolean isWriteSelf;

	//TODO encapsulation
	private LinkedHashMap<FileInfo.PROPERTY_NAMES, Boolean> targetProperties;

	public FileInfoWritingOption()
	{
		isWithHeader = false;
		encode = ENCODES.UTF8;
		delimiter = ",";
		isWriteSelf = false;
		targetProperties = new LinkedHashMap<>();
		Arrays.stream(FileInfo.PROPERTY_NAMES.values()).forEach(p -> targetProperties.put(p, false));
		targetProperties.put(FileInfo.PROPERTY_NAMES.NAME, true);
	}


	public static enum ENCODES
	{
		UTF8,
		SJIS,
		UNICODE,
	}

	public void intoWritingItem(FileInfo.PROPERTY_NAMES propertyName)
	{
		targetProperties.put(propertyName, true);
	}

	public void intoWritingItemAll(FileInfo.PROPERTY_NAMES[] propertyNames)
	{
		Arrays.stream(propertyNames).forEach(p -> targetProperties.put(p, true));
	}

	public void outOfWritingItem(FileInfo.PROPERTY_NAMES propertyName)
	{
		targetProperties.put(propertyName, false);

	}

}