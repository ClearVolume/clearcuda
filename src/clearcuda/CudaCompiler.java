package clearcuda;

import static java.lang.Math.abs;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;

import clearcuda.utils.NVCC;

public class CudaCompiler
{

	private static final String cHashAlgo = "SHA-256";
	private static final File cCompilationRootFolder = new File(System.getProperty("user.home"),
																															".clearcuda");

	private CudaDevice mCudaDevice;

	private MessageDigest mMessageDigest;
	private final String mCompilationUnitName;
	private final File mCompilationFolder;

	private static int mOptimizationLevel = 3;
	private static boolean mUseFastMath = true;

	private final Map<String, String> mKeyValueMap = new HashMap<String, String>();

	private final ArrayList<File> mFileList = new ArrayList<File>();

	private volatile File mPTXFile;

	public CudaCompiler(final CudaDevice pCudaDevice,
											final String pCompilationUnitName)
	{
		super();
		mCudaDevice = pCudaDevice;
		mCompilationUnitName = pCompilationUnitName;

		mCompilationFolder = new File(cCompilationRootFolder,
																	mCompilationUnitName);
		try
		{
			mMessageDigest = MessageDigest.getInstance(cHashAlgo);

			return;
		}
		catch (final NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}

	}

	public void setParameter(final String pKey, final String pValue)
	{
		mKeyValueMap.put(pKey, pValue);
	}

	public ArrayList<File> addFiles(final Class<?> pRootClass,
																	final boolean pStripPrefixPath,
																	final String... pRessourcePaths) throws IOException
	{
		final ArrayList<File> lFileList = new ArrayList<File>();
		for (final String lResourcePath : pRessourcePaths)
		{
			final File lFile = addFile(	pRootClass,
																	lResourcePath,
																	pStripPrefixPath);
			lFileList.add(lFile);
		}
		return lFileList;
	}

	public ArrayList<File> addFiles(final Class<?> pRootClass,
																	final String... pRessourcePaths) throws IOException
	{
		return addFiles(pRootClass, false, pRessourcePaths);
	}

	public File addFile(final Class<?> pClass,
											final String pRessourcePath) throws IOException
	{
		return addFile(pClass, pRessourcePath, false);
	}

	public File addFile(final Class<?> pClass,
											final String pRessourcePath,
											final boolean pStripPrefixPath) throws IOException
	{
		final InputStream lInputStreamCUFile = pClass.getResourceAsStream(pRessourcePath);
		final StringWriter writer = new StringWriter();

		final InputStreamReader in = new InputStreamReader(	lInputStreamCUFile,
																												Charset.defaultCharset());
		IOUtils.copy(in, writer);

		String lCUFileString = writer.toString();

		if (mKeyValueMap != null)
			for (final Entry<String, String> lEntry : mKeyValueMap.entrySet())
			{
				final String lPattern = lEntry.getKey();
				final String lReplacement = lEntry.getValue();

				lCUFileString = lCUFileString.replaceAll(	lPattern,
																									lReplacement);
			}

		final String lFileName = pRessourcePath.replace("./", "");
		final File lSourceFile = new File(lFileName);
		final File lDestinationFile = new File(	mCompilationFolder,
																						pStripPrefixPath ? lSourceFile.getName()
																														: lSourceFile.getPath());
		// This is the line we wanted to use:
		// FileUtils.write(lDestinationFile, lCUFileString);
		// And this is the block we have to use for Fiji compatibility:
		OutputStream out = null;
		try
		{
			out = openOutputStream(lDestinationFile, false);
			out.write(lCUFileString.getBytes(Charset.defaultCharset()));
			out.close(); // don't swallow close Exception if copy completes normally
		}
		finally
		{
			IOUtils.closeQuietly(out);
		}
		// End: Fiji compatibility block.

		mFileList.add(lDestinationFile);

		return lDestinationFile;
	}

	/**
	 * @param lDestinationFile
	 * @param b
	 * @return
	 * @throws IOException
	 */
	private OutputStream openOutputStream(final File file,
																				final boolean append) throws IOException
	{
		if (file.exists())
		{
			if (file.isDirectory())
			{
				throw new IOException("File '" + file
															+ "' exists but is a directory");
			}
			if (file.canWrite() == false)
			{
				throw new IOException("File '" + file
															+ "' cannot be written to");
			}
		}
		else
		{
			final File parent = file.getParentFile();
			if (parent != null)
			{
				if (!parent.mkdirs() && !parent.isDirectory())
				{
					throw new IOException("Directory '" + parent
																+ "' could not be created");
				}
			}
		}
		return new FileOutputStream(file, append);
	}

	public File compile(final File pPrimaryFile) throws IOException
	{
		final long lHash = computeHashForFiles();
		final String lHashPrefix = String.format(".%d", lHash);

		final File lPTXFile = new File(pPrimaryFile.getAbsolutePath()
																								.replace(	".cu",
																													lHashPrefix + ".ptx"));

		mPTXFile = lPTXFile;

		if (mPTXFile.exists())
		{
			System.out.format("PTX already compiled, using the cached version (%s) \n",
												mPTXFile.getName());
			return mPTXFile;
		}

		compile(mCudaDevice, pPrimaryFile, getPTXFile());

		return getPTXFile();
	}

	public void purge() throws IOException
	{
		final SimpleFileVisitor<Path> lSimpleFileVisitor = new SimpleFileVisitor<Path>()
		{
			@Override
			public FileVisitResult visitFile(	final Path file,
																				final BasicFileAttributes attrs) throws IOException
			{
				if (file != null && file.getFileName().endsWith(".ptx"))
					Files.delete(file);
				return FileVisitResult.CONTINUE;
			}
		};

		Files.walkFileTree(	mCompilationFolder.toPath(),
												lSimpleFileVisitor);

	}

	private long computeHashForFiles() throws FileNotFoundException,
																		IOException
	{
		long lFilesHash = 0;
		for (final File lFile : mFileList)
		{
			final long lFileHash = computeFileHash(lFile);
			lFilesHash += lFileHash;
		}
		return abs(lFilesHash);
	}

	private long computeFileHash(final File pFile) throws FileNotFoundException,
																								IOException
	{

		final int lFileLength = (int) (Files.size(pFile.toPath()));
		final byte[] lBuffer = new byte[lFileLength];

		// IOUtils.readFully( new FileInputStream( pFile ), lBuffer );
		final int actual = read(new FileInputStream(pFile),
														lBuffer,
														0,
														lBuffer.length);
		if (actual != lBuffer.length)
		{
			throw new EOFException("Length to read: " + lBuffer.length
															+ " actual: "
															+ actual);
		}

		final byte[] lDigest = mMessageDigest.digest(lBuffer);

		long lHashCode = 0;
		for (final byte lByte : lDigest)
		{
			lHashCode = Long.rotateRight(lHashCode, 3);
			lHashCode += lByte;
		}

		return abs(lHashCode);
	}

	public static int read(	final InputStream input,
													final byte[] buffer,
													final int offset,
													final int length) throws IOException
	{
		final int EOF = -1;
		if (length < 0)
		{
			throw new IllegalArgumentException("Length must not be negative: " + length);
		}
		int remaining = length;
		while (remaining > 0)
		{
			final int location = length - remaining;
			final int count = input.read(	buffer,
																		offset + location,
																		remaining);
			if (EOF == count)
			{ // EOF
				break;
			}
			remaining -= count;
		}
		return length - remaining;
	}

	public static void compile(final File pCUFile, final File pPTXFile) throws IOException
	{
		compile(null, pCUFile, pPTXFile);
	}

	public static void compile(	final CudaDevice pCudaDevice,
															final File pCUFile,
															final File pPTXFile) throws IOException
	{

		if (!pCUFile.exists())
		{
			throw new IOException("Input file not found: " + pCUFile.getName());
		}

		final String lNVCCAbsPath = NVCC.find().getAbsolutePath();
		final String lCUFileFolderAbsPath = pCUFile.getParentFile()
																								.getAbsolutePath();
		final String lModelString = "-m" + System.getProperty("sun.arch.data.model");
		// final String lCPPCompilerAbsPath = CPPCompiler.find();
		final String lCUFileAbsPath = pCUFile.getAbsolutePath();
		final String lPTXFileAbsPath = pPTXFile.getAbsolutePath();

		final String lBinDirOption = "";/*String.format(	"--compiler-bindir=%s",
																								lCPPCompilerAbsPath);/**/

		String lArchitectureString = "";
		if (pCudaDevice != null)
		{
			final CudaComputeCapability lComputeCapability = pCudaDevice.getComputeCapability();
			lArchitectureString = String.format("-arch=compute_%d%d -code=sm_%d%d",
																					lComputeCapability.getMajor(),
																					lComputeCapability.getMinor(),
																					lComputeCapability.getMajor(),
																					lComputeCapability.getMinor());
		}

		final String lOptimizationLevel = "-O" + mOptimizationLevel;

		final String lFastMathString = mUseFastMath	? "--use_fast_math"
																								: "";

		final String lCommand = String.format("%s  -I. -I %s %s %s %s %s %s -ptx %s -o %s",
																					lNVCCAbsPath,
																					lCUFileFolderAbsPath,
																					lModelString,
																					lBinDirOption,
																					lArchitectureString,
																					lOptimizationLevel,
																					lFastMathString,
																					lCUFileAbsPath,
																					lPTXFileAbsPath);

		final String[] lCommandArray = lCommand.split("\\s+", -1);

		System.out.println("launching: " + lCommand);

		final ProcessBuilder lProcessBuilder = new ProcessBuilder(lCommandArray);
		// lProcessBuilder.inheritIO();
		final String lExistingPath = lProcessBuilder.environment()
																								.get("PATH");
		final String lExtraPaths = ":/Developer/NVIDIA/CUDA-6.5/bin:/opt/local/bin:/opt/local/sbin:/usr/local/bin:/usr/bin";
		lProcessBuilder.environment().put("PATH",
																			lExistingPath + lExtraPaths);
		lProcessBuilder.redirectErrorStream(true);
		lProcessBuilder.inheritIO();

		System.out.println("lProcessBuilder.environment() = " + lProcessBuilder.environment());

		final Process process = lProcessBuilder.start();// Runtime.getRuntime().exec(lCommand);

		// final String errorMessage = new
		// String(IOUtils.toByteArray(process.getErrorStream()));
		// final String outputMessage = new
		// String(IOUtils.toByteArray(process.getInputStream()));

		final int exitValue = waitFor(process);

		if (exitValue != 0)
		{
			System.out.println("nvcc process exitValue " + exitValue);
			// System.out.println("errorMessage:\n" + errorMessage);
			// System.out.println("outputMessage:\n" + outputMessage);
			// throw new IOException("Could not create .ptx file: " + errorMessage);
		}

	}

	private static int waitFor(final Process pProcess)
	{
		try
		{
			return pProcess.waitFor();
		}
		catch (final InterruptedException e)
		{
			return waitFor(pProcess);
		}

	}

	public File getPTXFile()
	{
		return mPTXFile;
	}

	@Override
	public String toString()
	{
		return "CUDACompiler [mCompilationUnitName=" + mCompilationUnitName
						+ ", mCompilationFolder="
						+ mCompilationFolder
						+ ", mKeyValueMap="
						+ mKeyValueMap
						+ ", mFileList="
						+ mFileList
						+ ", mPTXFile="
						+ mPTXFile
						+ "]";
	}

}
