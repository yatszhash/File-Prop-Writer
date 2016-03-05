package data;
import java.io.File;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.VerRsrc;
import com.sun.jna.platform.win32.Version;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;


/**
* JNAを通してネイティブコードのWin32APIを利用し、
* ファイルのバージョンなどの情報を取得するクラス
**/
public class FileOperationWithJna {
	public interface User32 extends StdCallLibrary
	{
		User32 INSTANCE = (User32) Native.loadLibrary("user32", User32.class);
	}

	public FileOperationWithJna(){};
	private final static String noVersion = "0.0.0.0";

	public static String getFileVersion(File file)
	{
		String version = "";
		int size = Version.INSTANCE.GetFileVersionInfoSize(file.getAbsolutePath(), null);

		/*ファイル情報用の領域を確保*/
		Pointer buffer = Kernel32.INSTANCE.LocalAlloc(WinBase.LMEM_DISCARDABLE, size);



		//TODO return "" with file without version
		try
		{
			/*指定の領域にファイルバージョン情報のリソース用のバッファを確保*/
			Version.INSTANCE.GetFileVersionInfo(file.getAbsolutePath(), 0, size, buffer);

			/*ポインターを参照として表現*/
			IntByReference outputsize = new IntByReference();
			PointerByReference pointer = new PointerByReference();

			/*ルートの情報を取り出す。
			 * ファイルバージョン情報のリソースバッファ、
			 * バージョン情報リソースに関する構造体へのポインタ、
			 * バージョン情報へのポインタを格納するバッファ、
			 * バージョン情報の長さの格納先
			 * */
			Version.INSTANCE.VerQueryValue(buffer, "\\", pointer, outputsize);
			/*VerRsrc = version resource ファイル情報構造体を取得*/
			VerRsrc.VS_FIXEDFILEINFO fixedFileInfo = new VerRsrc.VS_FIXEDFILEINFO(pointer.getValue());

			int[] separatedUnits = getVersionElements(fixedFileInfo);

			version = format(separatedUnits);
		} finally
		{
			Kernel32.INSTANCE.GlobalFree(buffer);
		}

		if (version.equals(noVersion))
		{
			return "";
		}

		return version;
	}


	/*構造体から情報を取得
	 * バージョン形式を構成する４つの要素のうち
	 * 上位２つを32bit(MS)、下位２つを32bit(LS)から取り出す
	 * */
	private static int[] getVersionElements(VerRsrc.VS_FIXEDFILEINFO fixedFileInfo)
	{
		int[] elements = new int[4];
		/*符号付きでバージョン値を取得しているので、符号なしへ変換*/
		elements[0] = hiword(Integer.toUnsignedLong(fixedFileInfo.dwFileVersionMS.intValue()));
		elements[1] = loword(Integer.toUnsignedLong(fixedFileInfo.dwFileVersionMS.intValue()));
		elements[2] = hiword(Integer.toUnsignedLong(fixedFileInfo.dwFileVersionLS.intValue()));
		elements[3] = loword(Integer.toUnsignedLong(fixedFileInfo.dwFileVersionLS.intValue()));

		return elements;
	}

	public static int hiword(long number)
	{
		return (int) (number >> 16);
	}

	public static int loword(long number)
	{
		return (int) (number & 0x0000FFFFL );
	}

	/**
	 * 形式x.x.x.xの文字列に変換する。
	 * */
	private static String format(int[] splited)
	{
		assert (splited.length == 4);
		return splited[0] + "." + splited[1] + "." + splited[2] + "."
				+ splited[3];
	}
}
