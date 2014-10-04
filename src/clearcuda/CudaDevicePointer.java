package clearcuda;

import static java.lang.Math.min;
import static jcuda.driver.JCudaDriver.cuCtxSynchronize;
import static jcuda.driver.JCudaDriver.cuMemAlloc;
import static jcuda.driver.JCudaDriver.cuMemAllocManaged;
import static jcuda.driver.JCudaDriver.cuMemFree;
import static jcuda.driver.JCudaDriver.cuMemcpyAsync;
import static jcuda.driver.JCudaDriver.cuMemcpyDtoHAsync;
import static jcuda.driver.JCudaDriver.cuMemcpyHtoDAsync;
import static jcuda.driver.JCudaDriver.cuMemsetD16Async;
import static jcuda.driver.JCudaDriver.cuMemsetD32Async;
import static jcuda.driver.JCudaDriver.cuMemsetD8Async;
import jcuda.CudaException;
import jcuda.Pointer;
import jcuda.driver.CUdeviceptr;
import jcuda.driver.CUmemAttach_flags;

public class CudaDevicePointer implements
															CudaCloseable,
															CopyFromToInterface,
															PeerInterface<CUdeviceptr>
{

	protected CUdeviceptr mCUdeviceptr;
	protected long mSizeInBytes;
	protected boolean mExternallyAllocated;

	public static final CudaDevicePointer malloc(final long pSizeInBytes)
	{
		CudaDevicePointer lCudaDevicePointer = new CudaDevicePointer(	false,
																																	pSizeInBytes);
		cuMemAlloc(lCudaDevicePointer.getPeer(), pSizeInBytes);
		return lCudaDevicePointer;
	}

	public static final CudaDevicePointer mallocManaged(final long pSizeInBytes)
	{
		CudaDevicePointer lCudaDevicePointer = new CudaDevicePointer(	false,
																																	pSizeInBytes);
		int lFlags = CUmemAttach_flags.CU_MEM_ATTACH_GLOBAL;
		cuMemAllocManaged(lCudaDevicePointer.getPeer(),
											pSizeInBytes,
											lFlags);
		return lCudaDevicePointer;
	}

	CudaDevicePointer(boolean pExternallyAllocated,
										final long pSizeInBytes)
	{
		super();
		mExternallyAllocated = pExternallyAllocated;
		mSizeInBytes = pSizeInBytes;
		mCUdeviceptr = new CUdeviceptr();
	}

	CudaDevicePointer(boolean pExternallyAllocated)
	{
		this(pExternallyAllocated, 0);
	}

	public void setFloat(float pFloat)
	{
		copyFrom(Pointer.to(new float[]
		{ pFloat }), true);
	}

	public void setDouble(double pDouble)
	{
		copyFrom(Pointer.to(new double[]
		{ pDouble }), true);
	}

	public void setByte(byte pByte)
	{
		copyFrom(Pointer.to(new byte[]
		{ pByte }), true);
	}

	public void setChar(char pChar)
	{
		copyFrom(Pointer.to(new char[]
		{ pChar }), true);
	}

	public void setShort(short pShort)
	{
		copyFrom(Pointer.to(new short[]
		{ pShort }), true);
	}

	public void setInt(int pInt)
	{
		copyFrom(Pointer.to(new int[]
		{ pInt }), true);
	}

	public void setLong(long pLong)
	{
		copyFrom(Pointer.to(new long[]
		{ pLong }), true);
	}

	@Override
	public void copyFrom(Pointer pPointer, boolean pSync)
	{
		cuMemcpyHtoDAsync(mCUdeviceptr, pPointer, getSizeInBytes(), null);/**/
		if (pSync)
			cuCtxSynchronize();
	}

	@Override
	public void copyTo(Pointer pPointer, boolean pSync)
	{
		cuMemcpyDtoHAsync(pPointer, mCUdeviceptr, getSizeInBytes(), null);
		if (pSync)
			cuCtxSynchronize();
	}

	public void copyFrom(	CudaDevicePointer pCudaDevicePointer,
												boolean pSync)
	{
		cuMemcpyAsync(mCUdeviceptr,
									pCudaDevicePointer.getPeer(),
									min(getSizeInBytes(),
											pCudaDevicePointer.getSizeInBytes()),
									null);
		if (pSync)
			cuCtxSynchronize();
	}

	public void copyTo(	CudaDevicePointer pCudaDevicePointer,
											boolean pSync)
	{
		cuMemcpyAsync(pCudaDevicePointer.getPeer(),
									mCUdeviceptr,
									min(getSizeInBytes(),
											pCudaDevicePointer.getSizeInBytes()),
									null);
		if (pSync)
			cuCtxSynchronize();
	}

	public void set(float pValue, boolean pSync)
	{
		cuMemsetD32Async(	mCUdeviceptr,
											Float.floatToIntBits(pValue),
											mSizeInBytes / 4,
											null);
		if (pSync)
			cuCtxSynchronize();
	}

	public void set(int pValue, boolean pSync)
	{
		cuMemsetD32Async(mCUdeviceptr, pValue, mSizeInBytes / 4, null);
		if (pSync)
			cuCtxSynchronize();
	}

	public void set(short pValue, boolean pSync)
	{
		cuMemsetD16Async(mCUdeviceptr, pValue, mSizeInBytes / 2, null);
		if (pSync)
			cuCtxSynchronize();
	}

	public void set(byte pValue, boolean pSync)
	{
		cuMemsetD8Async(mCUdeviceptr, pValue, mSizeInBytes, null);
		if (pSync)
			cuCtxSynchronize();
	}

	public CUdeviceptr getPeer()
	{
		return mCUdeviceptr;
	}

	public long getSizeInBytes()
	{
		return mSizeInBytes;
	}

	@Override
	public void close() throws CudaException
	{
		if (!mExternallyAllocated && mCUdeviceptr != null)
		{
			cuMemFree(getPeer());
			mCUdeviceptr = null;
		}
	}

	@Override
	public String toString()
	{
		return "CudaDevicePointer [mSizeInBytes=" + mSizeInBytes + "]";
	}

}