package clearcudaj;

import static java.lang.Math.abs;
import static java.lang.Math.toIntExact;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import clearcudaj.utils.CPPCompiler;
import clearcudaj.utils.NVCC;

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

	public CudaCompiler(CudaDevice pCudaDevice,
											String pCompilationUnitName)
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
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}

	}

	public void setParameter(String pKey, String pValue)
	{
		mKeyValueMap.put(pKey, pValue);
	}

	public ArrayList<File> addFiles(Class<?> pRootClass,
																	String... pRessourcePaths) throws IOException
	{
		ArrayList<File> lFileList = new ArrayList<File>();
		for (String lResourcePath : pRessourcePaths)
		{
			File lFile = addFile(pRootClass, lResourcePath);
			lFileList.add(lFile);
		}
		return lFileList;
	}

	public File addFile(Class<?> pClass, String pRessourcePath) throws IOException
	{
		InputStream lInputStreamCUFile = pClass.getResourceAsStream(pRessourcePath);
		final StringWriter writer = new StringWriter();
		IOUtils.copy(lInputStreamCUFile, writer, Charset.defaultCharset());
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
		FileUtils.write(lCUFile, lCUFileString);

		mFileList.add(lCUFile);

		return lCUFile;
	}

	public File compile(File pPrimaryFile) throws IOException
	{
		long lHash = computeHashForFiles();
		String lHashPrefix = String.format(".%d", lHash);

		mPTXFile = new File(pPrimaryFile.getAbsolutePath()
																		.replace(	".cu",
																							lHashPrefix + ".ptx"));

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
		SimpleFileVisitor<Path> lSimpleFileVisitor = new SimpleFileVisitor<Path>()
		{
			@Override
			public FileVisitResult visitFile(	Path file,
																				BasicFileAttributes attrs) throws IOException
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
		for (File lFile : mFileList)
		{
			long lFileHash = computeFileHash(lFile);
			lFilesHash += lFileHash;
		}
		return abs(lFilesHash);
	}

	private long computeFileHash(File pFile) throws FileNotFoundException,
																					IOException
	{
		final int lFileLength = toIntExact(Files.size(pFile.toPath()));
		byte[] lBuffer = new byte[lFileLength];
		IOUtils.readFully(new FileInputStream(pFile), lBuffer);

		byte[] lDigest = mMessageDigest.digest(lBuffer);

		long lHashCode = 0;
		for (byte lByte : lDigest)
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

	public static void compile(	CudaDevice pCudaDevice,
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
			CudaComputeCapability lComputeCapability = pCudaDevice.getComputeCapability();
			lArchitectureString = String.format("-arch=compute_%d%d -code=sm_%d%d",
																					lComputeCapability.getMajor(),
																					lComputeCapability.getMinor(),
																					lComputeCapability.getMajor(),
																					lComputeCapability.getMinor());
		}

		String lOptimizationLevel = "-O" + mOptimizationLevel;

		String lFastMathString = mUseFastMath ? "--use_fast_math"
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

		int exitValue = waitFor(process);

		if (exitValue != 0)
		{
			System.out.println("nvcc process exitValue " + exitValue);
			System.out.println("errorMessage:\n" + errorMessage);
			System.out.println("outputMessage:\n" + outputMessage);
			throw new IOException("Could not create .ptx file: " + errorMessage);
		}

	}

	private static int waitFor(Process pProcess)
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
