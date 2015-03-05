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
import clearcuda.utils.JCudaPointerUtils;
import coremem.interfaces.Copyable;
import coremem.interfaces.CopyableWithSync;
import coremem.interfaces.HasPeer;
import coremem.interfaces.PointerAccessible;

public class CudaDevicePointer extends CopyFromToInterface implements
																													CudaCloseable,
																													HasPeer<CUdeviceptr>,
																													PointerAccessible,
																													Copyable<CudaDevicePointer>,
																													CopyableWithSync<CudaDevicePointer>
{

	protected CUdeviceptr mCUdeviceptr;
	protected long mSizeInBytes;
	protected boolean mExternallyAllocated;


	public static final CudaDevicePointer malloc(final long pSizeInBytes)
	{
		final CudaDevicePointer lCudaDevicePointer = new CudaDevicePointer(	false,
																																	pSizeInBytes);

		cuMemAlloc(lCudaDevicePointer.getPeer(), pSizeInBytes);
		return lCudaDevicePointer;
	}


	public static final CudaDevicePointer mallocManaged(final long pSizeInBytes)
	{
		final CudaDevicePointer lCudaDevicePointer = new CudaDevicePointer(	false,
																																	pSizeInBytes);
		final int lFlags = CUmemAttach_flags.CU_MEM_ATTACH_GLOBAL;
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


	public void setSingleFloat(float pFloat)
	{
		copyFrom(Pointer.to(new float[]
		{ pFloat }), true);
	}

	public void setSingleDouble(double pDouble)
	{
		copyFrom(Pointer.to(new double[]
		{ pDouble }), true);
	}

	public void setSingleByte(byte pByte) {
		copyFrom(Pointer.to(new byte[] { pByte }), true);
	}

	public void setSingleChar(char pChar) {
		copyFrom(Pointer.to(new char[] { pChar }), true);
	}

	public void setSingleShort(short pShort) {
		copyFrom(Pointer.to(new short[] { pShort }), true);
	}

	public void setSingleInt(int pInt) {
		copyFrom(Pointer.to(new int[] { pInt }), true);

	}

	public void setLong(long pLong)
	{
		copyFrom(Pointer.to(new long[]
		{ pLong }), true);
	}

	@Override
	public void copyFrom(CudaDevicePointer pFrom)
	{
		copyFrom(pFrom, true);
	}

	@Override
	public void copyTo(CudaDevicePointer pTo)
	{
		copyTo(pTo, true);
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

	@Override
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

	@Override
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


	public void fillFloat(float pValue, boolean pSync)
	{
		cuMemsetD32Async(mCUdeviceptr, Float.floatToIntBits(pValue),
				mSizeInBytes / 4, null);

		if (pSync)
			cuCtxSynchronize();
	}


	public void fillInt(int pValue, boolean pSync)

	{
		cuMemsetD32Async(mCUdeviceptr, pValue, mSizeInBytes / 4, null);
		if (pSync)
			cuCtxSynchronize();
	}


	public void fillShort(short pValue, boolean pSync)

	{
		cuMemsetD16Async(mCUdeviceptr, pValue, mSizeInBytes / 2, null);
		if (pSync)
			cuCtxSynchronize();
	}


	public void fillByte(byte pValue, boolean pSync)

	{
		cuMemsetD8Async(mCUdeviceptr, pValue, mSizeInBytes, null);
		if (pSync)
			cuCtxSynchronize();
	}

	@Override
	public CUdeviceptr getPeer() {

		return mCUdeviceptr;
	}

	@Override
	public long getAddress()
	{
		return JCudaPointerUtils.getNativeAddress(mCUdeviceptr);
	}

	@Override
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
