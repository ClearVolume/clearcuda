package clearcuda;

import static jcuda.driver.JCudaDriver.cuCtxDestroy;
import static jcuda.driver.JCudaDriver.cuCtxPopCurrent;
import static jcuda.driver.JCudaDriver.cuCtxPushCurrent;
import static jcuda.driver.JCudaDriver.cuCtxSetCurrent;
import static jcuda.driver.JCudaDriver.cuCtxSynchronize;
import static jcuda.driver.JCudaDriver.cuGLCtxCreate;
import jcuda.CudaException;
import jcuda.driver.CUcontext;
import jcuda.driver.CUctx_flags;

public class CudaContext implements CudaCloseable
{
	private CUcontext mCUcontext = new CUcontext();
	private final CudaDevice mCudaDevice;
	private boolean mEnableHostMapping = true;

	public CudaContext(CudaDevice pCudaDevice)
	{
		super();
		mCudaDevice = pCudaDevice;
		int lFlags = mEnableHostMapping ? CUctx_flags.CU_CTX_MAP_HOST : 0;
		cuGLCtxCreate(mCUcontext, lFlags, pCudaDevice.getPeer());
	}

	@Override
	public void close() throws CudaException
	{
		if (mCUcontext != null)
		{
			cuCtxDestroy(mCUcontext);
			mCUcontext = null;
		}
	}

	public CUcontext popCurrent()
	{
		CUcontext lOldContext = new CUcontext();
		cuCtxPopCurrent(lOldContext);
		return lOldContext;
	}

	public CUcontext pushCurrent()
	{
		CUcontext lOldContext = new CUcontext();
		cuCtxPushCurrent(lOldContext);
		return lOldContext;
	}

	public void setCurrent()
	{
		cuCtxSetCurrent(mCUcontext);
	}

	public void synchronize()
	{
		setCurrent();
		cuCtxSynchronize();
		// cuCtxSetCurrent(null);
	}

	public CUcontext getPeer()
	{
		return mCUcontext;
	}

	public CudaDevice getCudaDevice()
	{
		return mCudaDevice;
	}

	@Override
	public String toString()
	{
		return "CudaContext [getCudaDevice()=" + getCudaDevice() + "]";
	}

}
