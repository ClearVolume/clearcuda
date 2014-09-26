package clearcudaj;

import static jcuda.driver.JCudaDriver.cuCtxSynchronize;
import static jcuda.driver.JCudaDriver.cuMemcpyDtoHAsync;
import static jcuda.driver.JCudaDriver.cuMemcpyHtoDAsync;
import static jcuda.driver.JCudaDriver.cuMemsetD16Async;
import static jcuda.driver.JCudaDriver.cuMemsetD32Async;
import static jcuda.driver.JCudaDriver.cuMemsetD8Async;
import jcuda.CudaException;
import jcuda.Pointer;
import jcuda.driver.CUdeviceptr;

public class CudaDevicePointer implements CudaCloseable
{

	protected CUdeviceptr mCUdeviceptr;
	protected long mSizeInBytes;

	public CudaDevicePointer()
	{
		super();
		mCUdeviceptr = new CUdeviceptr();
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

	public void copyFloatsFrom(float[] pFloatsArray, boolean pSync)
	{
		Pointer lPointer = Pointer.to(pFloatsArray);
		copyFrom(lPointer, pSync);
	}

	public void copyFrom(Pointer pPointerToHostMemory, boolean pSync)
	{
		cuMemcpyHtoDAsync(mCUdeviceptr,
											pPointerToHostMemory,
											getSizeInBytes(),
											null);/**/
		if (pSync)
			cuCtxSynchronize();
	}

	public void copyTo(Pointer pPointerToHostMemory, boolean pSync)
	{
		cuMemcpyDtoHAsync(pPointerToHostMemory,
											mCUdeviceptr,
											getSizeInBytes(),
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

	}

	@Override
	public String toString()
	{
		return "CudaDevicePointer [mSizeInBytes=" + mSizeInBytes + "]";
	}

}
