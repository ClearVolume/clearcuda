package clearcuda;

import static jcuda.driver.JCudaDriver.cuCtxCreate;
import static jcuda.driver.JCudaDriver.cuCtxDestroy;
import static jcuda.driver.JCudaDriver.cuCtxPopCurrent;
import static jcuda.driver.JCudaDriver.cuCtxPushCurrent;
import static jcuda.driver.JCudaDriver.cuCtxSetCurrent;
import static jcuda.driver.JCudaDriver.cuCtxSynchronize;
import static jcuda.driver.JCudaDriver.cuGLCtxCreate;
import jcuda.CudaException;
import jcuda.driver.CUcontext;
import jcuda.driver.CUctx_flags;
import coremem.interfaces.HasPeer;

public class CudaContext implements CudaCloseable, HasPeer<CUcontext>
{
	private CUcontext mCUcontext = new CUcontext();
	private final CudaDevice mCudaDevice;
	private final boolean mEnableHostMapping = false;

	public CudaContext(CudaDevice pCudaDevice, boolean pOpenGLInterop)
	{
		super();
		mCudaDevice = pCudaDevice;
		int lFlags = mEnableHostMapping ? CUctx_flags.CU_CTX_MAP_HOST : 0;
		lFlags |= CUctx_flags.CU_CTX_SCHED_BLOCKING_SYNC;
		lFlags |= CUctx_flags.CU_CTX_SCHED_AUTO;
		if (pOpenGLInterop)
			cuGLCtxCreate(mCUcontext, lFlags, pCudaDevice.getPeer());
		else
			cuCtxCreate(mCUcontext, lFlags, pCudaDevice.getPeer());
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
		final CUcontext lOldContext = new CUcontext();
		cuCtxPopCurrent(lOldContext);
		return lOldContext;
	}

	public CUcontext pushCurrent()
	{
		final CUcontext lOldContext = new CUcontext();
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

	@Override
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
