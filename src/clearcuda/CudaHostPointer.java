package clearcuda;

import static jcuda.driver.JCudaDriver.CU_MEMHOSTALLOC_DEVICEMAP;
import static jcuda.driver.JCudaDriver.CU_MEMHOSTALLOC_PORTABLE;
import static jcuda.driver.JCudaDriver.CU_MEMHOSTALLOC_WRITECOMBINED;
import static jcuda.driver.JCudaDriver.cuMemFreeHost;
import static jcuda.driver.JCudaDriver.cuMemHostAlloc;
import static jcuda.driver.JCudaDriver.cuMemHostGetDevicePointer;
import static jcuda.driver.JCudaDriver.cuMemHostRegister;
import static jcuda.driver.JCudaDriver.cuMemHostUnregister;
import jcuda.CudaException;
import jcuda.Pointer;

public class CudaHostPointer extends CudaDevicePointer
{

	private Pointer mPointer;
	protected boolean mGPUMapped;
	protected boolean mCudaAllocated;

	private boolean mFastGPUWriteButSlowCPURead;


	public static CudaHostPointer mallocPinned(long pSizeInBytes)
	{
		return mallocPinned(pSizeInBytes, true, false);
	}

	public static CudaHostPointer mallocPinned(	long pSizeInBytes,
																							boolean pGPUMapped,
																							boolean pFastGPUWriteButSlowCPURead)
	{

		final int lGPUMappedFlag = pGPUMapped ? CU_MEMHOSTALLOC_DEVICEMAP : 0;
		final int lFastGPUWriteButSlowCPURead = pFastGPUWriteButSlowCPURead	? CU_MEMHOSTALLOC_WRITECOMBINED
																																	: 0;
		final int lFlags = CU_MEMHOSTALLOC_PORTABLE | lGPUMappedFlag
												| lFastGPUWriteButSlowCPURead;

		final Pointer lPointer = new Pointer();
		final int lCuMemHostAlloc = cuMemHostAlloc(	lPointer,
																								pSizeInBytes,
																								lFlags);


		final CudaHostPointer lCudaHostPointer = new CudaHostPointer(	true,
																														lPointer,
																														pSizeInBytes,
																														pGPUMapped,
																														pFastGPUWriteButSlowCPURead);

		return lCudaHostPointer;
	}

	public static CudaHostPointer pinHostMemory(Pointer pPointer,
																							long pSizeInBytes,
																							boolean pGPUMapped)
	{
		final int lGPUMappedFlag = pGPUMapped ? CU_MEMHOSTALLOC_DEVICEMAP : 0;
		final int lFlags = lGPUMappedFlag;
		cuMemHostRegister(pPointer, pSizeInBytes, lFlags);

		final CudaHostPointer lCudaHostPointer = new CudaHostPointer(	false,
																														pPointer,
																														pSizeInBytes,
																														pGPUMapped);
		return lCudaHostPointer;
	}

	CudaHostPointer(boolean pCudaAllocated,
									Pointer pPointer,
									long pSizeInBytes,
									boolean pGPUMapped)
	{
		super(true);
		mCudaAllocated = pCudaAllocated;
		mPointer = pPointer;
		cuMemHostGetDevicePointer(this.getPeer(), mPointer, 0);
		mSizeInBytes = pSizeInBytes;
		mGPUMapped = pGPUMapped;
	}

	private CudaHostPointer(boolean pCudaAllocated,
													Pointer pPointer,
													long pSizeInBytes,
													boolean pGPUMapped,
													boolean pFastGPUWriteButSlowCPURead)
	{
		this(pCudaAllocated, pPointer, pSizeInBytes, pGPUMapped);
		mFastGPUWriteButSlowCPURead = pFastGPUWriteButSlowCPURead;
	}

	@Override
	public void close() throws CudaException
	{
		if (mPointer != null)
		{
			if (mCudaAllocated)
				cuMemFreeHost(mPointer);
			else
				cuMemHostUnregister(mPointer);
			mPointer = null;
		}
		super.close();
	}

	public Pointer getPointer()
	{
		return mPointer;
	}


}
