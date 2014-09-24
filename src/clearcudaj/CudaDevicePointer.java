package clearcudaj;

import static jcuda.driver.JCudaDriver.cuCtxSynchronize;
import static jcuda.driver.JCudaDriver.cuMemcpyDtoH;
import static jcuda.driver.JCudaDriver.cuMemcpyHtoD;
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

	public void copyFrom(Pointer pPointerToHostMemory)
	{
		cuMemcpyHtoD(mCUdeviceptr, pPointerToHostMemory, getSizeInBytes());
	}

	public void copyTo(Pointer pPointerToHostMemory)
	{
		cuMemcpyDtoH(pPointerToHostMemory, mCUdeviceptr, getSizeInBytes());
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
