package clearcudaj.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.Set;

import org.apache.commons.lang.SystemUtils;

public class NVCC
{
	private static final int cMaxDepth = 4;

	private static volatile Path sFoundPath;

	public static final File find() throws IOException
	{
		return findInternal(null);
	}

	public static final File find(String pRootFolder) throws IOException
	{
		return findInternal(new File(pRootFolder));
	}

	public static final File find(File pRootFolder) throws IOException
	{
		return findInternal(pRootFolder);
	}

	private static final File findInternal(File pRootFolder) throws IOException
	{

		if (pRootFolder == null)
		{
			if (SystemUtils.IS_OS_MAC_OSX)
				pRootFolder = new File("/Developer/");
			else if (SystemUtils.IS_OS_WINDOWS)
				pRootFolder = new File(System.getenv("ProgramFiles"));
			else if (SystemUtils.IS_OS_LINUX)
			{
				File lFindNVCC = find("/usr/local");
				if (lFindNVCC != null)
					return lFindNVCC;
				lFindNVCC = find("/usr/lib");
				return lFindNVCC;
			}
			else
				return null;
		}

		// System.out.println(lStartFolder);

		sFoundPath = null;
		FileVisitor<Path> lVisitor = new SimpleFileVisitor<Path>()
		{

			@Override
			public FileVisitResult visitFile(	Path pFile,
																				BasicFileAttributes pAttrs) throws IOException
			{
				// System.out.println(pFile);
				if (pFile.toFile().getName().equals("nvcc"))
				{
					sFoundPath = pFile;
					System.out.println("Found NVCC at: " + sFoundPath);
					return FileVisitResult.TERMINATE;
				}
				return FileVisitResult.CONTINUE;
			}

		};

		Set<FileVisitOption> lOptions = Collections.emptySet();

		Files.walkFileTree(	pRootFolder.toPath(),
												lOptions,
												cMaxDepth,
												lVisitor);

		return sFoundPath.toFile();
	}
}
