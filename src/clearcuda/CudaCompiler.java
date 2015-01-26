package clearcuda;

import static java.lang.Math.abs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import clearcuda.utils.CPPCompiler;
import clearcuda.utils.NVCC;

public class CudaCompiler
{

	private static final String cHashAlgo = "SHA-256";
	private static final File cCompilationRootFolder = new File(System.getProperty("user.home"),
																															".clearcudaj");


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
																	final String... pRessourcePaths) throws IOException
	{
		final ArrayList<File> lFileList = new ArrayList<File>();
		for (final String lResourcePath : pRessourcePaths)
		{
			final File lFile = addFile(pRootClass, lResourcePath);
			lFileList.add(lFile);
		}
		return lFileList;
	}

	public File addFile(final Class<?> pClass, final String pRessourcePath) throws IOException
	{
		final InputStream lInputStreamCUFile = pClass.getResourceAsStream(pRessourcePath);
		final StringWriter writer = new StringWriter();
		IOUtils.copy( lInputStreamCUFile, writer );
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
		final File lCUFile = new File(mCompilationFolder, lFileName);
		FileUtils.writeStringToFile( lCUFile, lCUFileString );

		mFileList.add(lCUFile);

		return lCUFile;
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
			System.out.format("PTX already compiled, using the cached version (%s) ",
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
				if (file.getFileName().endsWith(".ptx"))
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
		final int lFileLength = (int)(Files.size(pFile.toPath()));
		final byte[] lBuffer = IOUtils.toByteArray( new FileInputStream( pFile ) );

		final byte[] lDigest = mMessageDigest.digest(lBuffer);

		long lHashCode = 0;
		for (final byte lByte : lDigest)
		{
			lHashCode = Long.rotateRight(lHashCode, 3);
			lHashCode += lByte;
		}

		return abs(lHashCode);
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
		final String lCPPCompilerAbsPath = CPPCompiler.find();
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

		final String lFastMathString = mUseFastMath ? "--use_fast_math"
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

		System.out.println("launching: " + lCommand);

		final Process process = Runtime.getRuntime().exec(lCommand);

		final String errorMessage = new String(IOUtils.toByteArray(process.getErrorStream()));
		final String outputMessage = new String(IOUtils.toByteArray(process.getInputStream()));

		final int exitValue = waitFor(process);

		if (exitValue != 0)
		{
			System.out.println("nvcc process exitValue " + exitValue);
			System.out.println("errorMessage:\n" + errorMessage);
			System.out.println("outputMessage:\n" + outputMessage);
			throw new IOException("Could not create .ptx file: " + errorMessage);
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
