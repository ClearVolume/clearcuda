package clearcuda;

import static jcuda.driver.JCudaDriver.cuModuleLoad;
import static jcuda.driver.JCudaDriver.cuModuleUnload;

import java.io.File;

import jcuda.CudaException;
import jcuda.driver.CUmodule;
import coremem.interfaces.HasPeer;

public class CudaModule implements CudaCloseable, HasPeer<CUmodule>
{

	private CUmodule mCUmodule;
	private final File mPTXFile;

	private CudaModule(File pPTXFile)
	{
		super();
		mPTXFile = pPTXFile;
		mCUmodule = new CUmodule();
	}

	public static CudaModule moduleFromPTX(File pPTXFile)
	{
		CudaModule lCudaModule = new CudaModule(pPTXFile);
		cuModuleLoad(lCudaModule.getPeer(), pPTXFile.getAbsolutePath());
		return lCudaModule;
	}

	@Override
	public void close() throws CudaException
	{
		if (mCUmodule != null)
		{
			cuModuleUnload(mCUmodule);
			mCUmodule = null;
		}
	}

	public CudaFunction getFunction(String pFunctionSignature)
	{

		CudaFunction lCudaFunction = new CudaFunction(this,
																									pFunctionSignature);
		return lCudaFunction;
	}

	public CudaTextureReference getTexture(String pTextureName)
	{
		CudaTextureReference lCudaTexture = new CudaTextureReference(	this,
																																	pTextureName);
		return lCudaTexture;
	}

	public CudaSurfaceReference getSurface(String pSurfaceName)
	{
		CudaSurfaceReference lCudaSurface = new CudaSurfaceReference(	this,
																																	pSurfaceName);
		return lCudaSurface;
	}

	public CudaDevicePointer getGlobal(String pGlobalName)
	{
		CudaDevicePointer lCudaGlobal = new CudaGlobal(this, pGlobalName);
		return lCudaGlobal;
	}

	public CUmodule getPeer()
	{
		return mCUmodule;
	}

	public File getPTXFile()
	{
		return mPTXFile;
	}

	@Override
	public String toString()
	{
		return "CudaModule [mPTXFile=" + mPTXFile + "]";
	}

}
