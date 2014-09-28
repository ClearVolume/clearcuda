package clearcuda;

import static jcuda.driver.JCudaDriver.CUDA_ARRAY3D_SURFACE_LDST;
import static jcuda.driver.JCudaDriver.cuArray3DCreate;
import static jcuda.driver.JCudaDriver.cuArrayCreate;
import static jcuda.driver.JCudaDriver.cuArrayDestroy;
import static jcuda.driver.JCudaDriver.cuCtxSynchronize;
import static jcuda.driver.JCudaDriver.cuMemcpy2D;
import static jcuda.driver.JCudaDriver.cuMemcpy3D;
import jcuda.CudaException;
import jcuda.Pointer;
import jcuda.driver.CUDA_ARRAY3D_DESCRIPTOR;
import jcuda.driver.CUDA_ARRAY_DESCRIPTOR;
import jcuda.driver.CUDA_MEMCPY2D;
import jcuda.driver.CUDA_MEMCPY3D;
import jcuda.driver.CUarray;
import jcuda.driver.CUarray_format;
import jcuda.driver.CUmemorytype;

public class CudaArray implements CudaCloseable, CopyFromToInterface
{

	private final CUarray mCUarray;
	private final long mNumberOfChannels;
	private final long mWidth;
	private final long mHeight;
	private final long mDepth;
	private final int mBytesPerChannel;
	private final boolean mFloat;
	private final boolean mSigned;
	private final int mFormat;
	private final boolean mSurfaceEnabled;

	public CudaArray(	long pNumberOfChannels,
										long pWidth,
										long pHeight,
										long pDepth,
										int pBytesPerChannel,
										boolean pFloat,
										boolean pSigned,
										boolean pEnableSurface)
	{
		super();
		mNumberOfChannels = pNumberOfChannels;
		mWidth = pWidth;
		mHeight = pHeight;
		mDepth = pDepth;
		mBytesPerChannel = pBytesPerChannel;
		mFloat = pFloat;
		mSigned = pSigned;
		mSurfaceEnabled = pEnableSurface;
		mFormat = getFormat(pBytesPerChannel, pFloat, pSigned);

		mCUarray = new CUarray();
		if (mDepth <= 1)
		{
			final CUDA_ARRAY_DESCRIPTOR lAllocate3DArrayDescriptor = new CUDA_ARRAY_DESCRIPTOR();
			lAllocate3DArrayDescriptor.Width = pWidth;
			lAllocate3DArrayDescriptor.Height = pHeight;
			lAllocate3DArrayDescriptor.Format = mFormat;
			lAllocate3DArrayDescriptor.NumChannels = (int) pNumberOfChannels;
			cuArrayCreate(mCUarray, lAllocate3DArrayDescriptor);
		}
		else
		{
			final CUDA_ARRAY3D_DESCRIPTOR lAllocate3DArrayDescriptor = new CUDA_ARRAY3D_DESCRIPTOR();
			lAllocate3DArrayDescriptor.Width = pWidth;
			lAllocate3DArrayDescriptor.Height = pHeight;
			lAllocate3DArrayDescriptor.Depth = pDepth;
			lAllocate3DArrayDescriptor.Format = mFormat;
			lAllocate3DArrayDescriptor.NumChannels = (int) pNumberOfChannels;
			if (pEnableSurface)
				lAllocate3DArrayDescriptor.Flags = CUDA_ARRAY3D_SURFACE_LDST;
			cuArray3DCreate(mCUarray, lAllocate3DArrayDescriptor);
		}
		cuCtxSynchronize();
	}

	private int getFormat(int pBytesPerChannel,
												boolean pFloat,
												boolean pSigned)
	{
		int lFormat;
		if (pFloat)
		{
			if (pBytesPerChannel == 2)
			{
				lFormat = CUarray_format.CU_AD_FORMAT_HALF;
			}
			else if (pBytesPerChannel == 4)
			{
				lFormat = CUarray_format.CU_AD_FORMAT_FLOAT;
			}
			else
				lFormat = 0;
		}
		else
		{
			if (pSigned)
			{
				if (pBytesPerChannel == 1)
				{
					lFormat = CUarray_format.CU_AD_FORMAT_SIGNED_INT8;
				}
				else if (pBytesPerChannel == 2)
				{
					lFormat = CUarray_format.CU_AD_FORMAT_SIGNED_INT16;
				}
				else if (pBytesPerChannel == 4)
				{
					lFormat = CUarray_format.CU_AD_FORMAT_SIGNED_INT32;
				}
				else
					lFormat = 0;
			}
			else
			{
				if (pBytesPerChannel == 1)
				{
					lFormat = CUarray_format.CU_AD_FORMAT_UNSIGNED_INT8;
				}
				else if (pBytesPerChannel == 2)
				{
					lFormat = CUarray_format.CU_AD_FORMAT_UNSIGNED_INT16;
				}
				else if (pBytesPerChannel == 4)
				{
					lFormat = CUarray_format.CU_AD_FORMAT_UNSIGNED_INT32;
				}
				else
					lFormat = 0;
			}
		}
		return lFormat;
	}

	@Override
	public void close() throws CudaException
	{
		cuArrayDestroy(mCUarray);
	}

	public int getFormat()
	{
		return mFormat;
	}

	public int getBytesPerChannel()
	{
		return mBytesPerChannel;
	}

	public int getNumberOfChannels()
	{
		return (int) mNumberOfChannels;
	}

	public long getWidth()
	{
		return mWidth;
	}

	public long getHeight()
	{
		return mHeight;
	}

	public long getDepth()
	{
		return mDepth;
	}

	public boolean isFloat()
	{
		return mFloat;
	}

	public boolean isSigned()
	{
		return mSigned;
	}



	@Override
	public void copyFrom(Pointer pPointer, boolean pSync)
	{
		if (getDepth() == 1)
		{
			final CUDA_MEMCPY2D lCudaMemCopySpecification = new CUDA_MEMCPY2D();
			lCudaMemCopySpecification.srcMemoryType = CUmemorytype.CU_MEMORYTYPE_HOST;
			lCudaMemCopySpecification.srcHost = pPointer;
			lCudaMemCopySpecification.srcPitch = getWidth() * getBytesPerChannel()
																						* getNumberOfChannels();

			lCudaMemCopySpecification.dstMemoryType = CUmemorytype.CU_MEMORYTYPE_ARRAY;
			lCudaMemCopySpecification.dstArray = mCUarray;

			lCudaMemCopySpecification.WidthInBytes = getWidth() * getBytesPerChannel()
																								* getNumberOfChannels();
			lCudaMemCopySpecification.Height = getHeight();

			cuMemcpy2D(lCudaMemCopySpecification);
		}
		else
		{
			final CUDA_MEMCPY3D lCudaMemCopySpecification = new CUDA_MEMCPY3D();

			lCudaMemCopySpecification.srcMemoryType = CUmemorytype.CU_MEMORYTYPE_HOST;
			lCudaMemCopySpecification.srcHost = pPointer;
			lCudaMemCopySpecification.srcPitch = getWidth() * getBytesPerChannel()
																						* getNumberOfChannels();
			lCudaMemCopySpecification.srcHeight = getHeight();

			lCudaMemCopySpecification.dstMemoryType = CUmemorytype.CU_MEMORYTYPE_ARRAY;
			lCudaMemCopySpecification.dstArray = mCUarray;
			lCudaMemCopySpecification.dstPitch = getWidth() * getBytesPerChannel();
			lCudaMemCopySpecification.dstHeight = getHeight();

			lCudaMemCopySpecification.WidthInBytes = getWidth() * getBytesPerChannel()
																								* getNumberOfChannels();
			lCudaMemCopySpecification.Height = getHeight();
			lCudaMemCopySpecification.Depth = getDepth();

			cuMemcpy3D(lCudaMemCopySpecification);
		}

		if (pSync)
			cuCtxSynchronize();
	}

	@Override
	public void copyTo(Pointer pPointer, boolean pSync)
	{
		if (getDepth() == 1)
		{
			final CUDA_MEMCPY2D lCudaMemCopySpecification = new CUDA_MEMCPY2D();
			lCudaMemCopySpecification.srcMemoryType = CUmemorytype.CU_MEMORYTYPE_ARRAY;
			lCudaMemCopySpecification.srcArray = mCUarray;
			lCudaMemCopySpecification.srcPitch = getWidth() * getBytesPerChannel()
																						* getNumberOfChannels();

			lCudaMemCopySpecification.dstMemoryType = CUmemorytype.CU_MEMORYTYPE_HOST;
			lCudaMemCopySpecification.dstHost = pPointer;

			lCudaMemCopySpecification.WidthInBytes = getWidth() * getBytesPerChannel()
																								* getNumberOfChannels();
			lCudaMemCopySpecification.Height = getHeight();

			cuMemcpy2D(lCudaMemCopySpecification);
		}
		else
		{
			final CUDA_MEMCPY3D lCudaMemCopySpecification = new CUDA_MEMCPY3D();

			lCudaMemCopySpecification.srcMemoryType = CUmemorytype.CU_MEMORYTYPE_ARRAY;
			lCudaMemCopySpecification.srcArray = mCUarray;
			lCudaMemCopySpecification.srcPitch = getWidth() * getBytesPerChannel()
																						* getNumberOfChannels();
			lCudaMemCopySpecification.srcHeight = getHeight();

			lCudaMemCopySpecification.dstMemoryType = CUmemorytype.CU_MEMORYTYPE_HOST;
			lCudaMemCopySpecification.dstHost = pPointer;
			lCudaMemCopySpecification.dstPitch = getWidth() * getBytesPerChannel()
																						* getNumberOfChannels();
			lCudaMemCopySpecification.dstHeight = getHeight();

			lCudaMemCopySpecification.WidthInBytes = getWidth() * getBytesPerChannel()
																								* getNumberOfChannels();
			lCudaMemCopySpecification.Height = getHeight();
			lCudaMemCopySpecification.Depth = getDepth();

			cuMemcpy3D(lCudaMemCopySpecification);
		}

		if (pSync)
			cuCtxSynchronize();
	}

	public CUarray getPeer()
	{
		return mCUarray;
	}

	@Override
	public String toString()
	{
		return "CudaArray [mNumberOfChannels=" + mNumberOfChannels
						+ ", mWidth="
						+ getWidth()
						+ ", mHeight="
						+ getHeight()
						+ ", mDepth="
						+ getDepth()
						+ ", mBytesPerChannels="
						+ getBytesPerChannel()
						+ ", mFloat="
						+ isFloat()
						+ ", mSigned="
						+ isSigned()
						+ "]";
	}

	public boolean isSurfaceEnabled()
	{
		return mSurfaceEnabled;
	}

}
