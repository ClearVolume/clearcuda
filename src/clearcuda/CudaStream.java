package clearcuda;

import static java.lang.Math.round;
import static jcuda.driver.JCudaDriver.cuCtxGetStreamPriorityRange;
import static jcuda.driver.JCudaDriver.cuStreamAttachMemAsync;
import static jcuda.driver.JCudaDriver.cuStreamCreateWithPriority;
import static jcuda.driver.JCudaDriver.cuStreamDestroy;
import static jcuda.driver.JCudaDriver.cuStreamQuery;
import static jcuda.driver.JCudaDriver.cuStreamSynchronize;
import jcuda.CudaException;
import jcuda.driver.CUmemAttach_flags;
import jcuda.driver.CUresult;
import jcuda.driver.CUstream;
import jcuda.driver.CUstream_flags;
import coremem.interfaces.HasPeer;

public class CudaStream implements CudaCloseable, HasPeer<CUstream>
{

	private CUstream mCUStream;
	private final int mPriority;

	public CudaStream(double pPriorityNormalized)
	{
		super();
		final int lFlags = CUstream_flags.CU_STREAM_NON_BLOCKING;

		final int[] lPriorityRange = getPriorityRange();

		mPriority = (int) round(lPriorityRange[0] + pPriorityNormalized
														* (lPriorityRange[1] - lPriorityRange[0]));

		mCUStream = new CUstream();
		cuStreamCreateWithPriority(mCUStream, lFlags, mPriority);
	}

	private static int[] getPriorityRange()
	{
		final int[] lMinPriority = new int[1];
		final int[] lMaxPriority = new int[1];
		cuCtxGetStreamPriorityRange(lMinPriority, lMaxPriority);
		return new int[]
		{ lMinPriority[0], lMaxPriority[0] };
	}

	public boolean isReady()
	{
		final int lCuStreamQuery = cuStreamQuery(mCUStream);
		if (lCuStreamQuery == CUresult.CUDA_SUCCESS)
			return true;
		return false;
	}

	public void synchronize()
	{
		cuStreamSynchronize(mCUStream);
	}

	public void attach(CudaDevicePointer pCudaDevicePointer)
	{
		final int lFlags = CUmemAttach_flags.CU_MEM_ATTACH_SINGLE;
		cuStreamAttachMemAsync(	mCUStream,
														pCudaDevicePointer.getPeer(),
														pCudaDevicePointer.getSizeInBytes(),
														lFlags);
	}

	@Override
	public void close() throws CudaException
	{
		if (mCUStream != null)
		{
			cuStreamDestroy(mCUStream);
			mCUStream = null;
		}
	}

	@Override
	public CUstream getPeer()
	{
		return mCUStream;
	}

}
