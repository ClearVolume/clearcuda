package clearcuda.utils;

import org.apache.commons.lang.SystemUtils;

public class CPPCompiler
{

	public static String find()
	{

		if (SystemUtils.IS_OS_MAC_OSX)
		{
			return "/opt/local/bin/gcc-mp-4.6";
		}

		return null;
	}

}
