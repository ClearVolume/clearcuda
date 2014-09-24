package clearcudaj;

import static jcuda.driver.JCudaDriver.cuArray3DCreate;
import static jcuda.driver.JCudaDriver.cuArrayDestroy;
import static jcuda.driver.JCudaDriver.cuCtxSynchronize;
import static jcuda.driver.JCudaDriver.cuMemcpy2D;
import static jcuda.driver.JCudaDriver.cuMemcpy3D;

import java.nio.ByteBuffer;

import jcuda.CudaException;
import jcuda.Pointer;
import jcuda.driver.CUDA_ARRAY3D_DESCRIPTOR;
import jcuda.driver.CUDA_MEMCPY2D;
import jcuda.driver.CUDA_MEMCPY3D;
import jcuda.driver.CUarray;
import jcuda.driver.CUarray_format;
import jcuda.driver.CUmemorytype;
import clearcudaj.utils.JCudaPointerUtils;

public class CudaArray implements CudaCloseable
{

	private final CUarray mCUarray;
	private final long mNumberOfChannels;
	private final long mWidth;
	private final long mHeight;
	private final long mDepth;
	private final int mBytesPerVoxel;
	private final boolean mFloat;
	private final boolean mSigned;
	private final int mFormat;

	public CudaArray(	long pNumberOfChannels,
										long pWidth,
										long pHeight,
										long pDepth,
										int pBytesPerVoxel,
										boolean pFloat,
										boolean pSigned)
	{
		super();
		mNumberOfChannels = pNumberOfChannels;
		mWidth = pWidth;
		mHeight = pHeight;
		mDepth = pDepth;
		mBytesPerVoxel = pBytesPerVoxel;
		mFloat = pFloat;
		mSigned = pSigned;
		final CUDA_ARRAY3D_DESCRIPTOR lAllocate3DArrayDescriptor = new CUDA_ARRAY3D_DESCRIPTOR();
		lAllocate3DArrayDescriptor.Width = pWidth;
		lAllocate3DArrayDescriptor.Height = pHeight;
		lAllocate3DArrayDescriptor.Depth = pDepth;

		if (pFloat)
		{
			if (pBytesPerVoxel == 2)
			{
				mFormat = CUarray_format.CU_AD_FORMAT_HALF;
			}
			else if (pBytesPerVoxel == 4)
			{
				mFormat = CUarray_format.CU_AD_FORMAT_FLOAT;
			}
			else
				mFormat = 0;
		}
		else
		{
			if (pSigned)
			{
				if (pBytesPerVoxel == 1)
				{
					mFormat = CUarray_format.CU_AD_FORMAT_SIGNED_INT8;
				}
				else if (pBytesPerVoxel == 2)
				{
					mFormat = CUarray_format.CU_AD_FORMAT_SIGNED_INT16;
				}
				else if (pBytesPerVoxel == 4)
				{
					mFormat = CUarray_format.CU_AD_FORMAT_SIGNED_INT32;
				}
				else
					mFormat = 0;
			}
			else
			{
				if (pBytesPerVoxel == 1)
				{
					mFormat = CUarray_format.CU_AD_FORMAT_UNSIGNED_INT8;
				}
				else if (pBytesPerVoxel == 2)
				{
					mFormat = CUarray_format.CU_AD_FORMAT_UNSIGNED_INT16;
				}
				else if (pBytesPerVoxel == 4)
				{
					mFormat = CUarray_format.CU_AD_FORMAT_UNSIGNED_INT32;
				}
				else
					mFormat = 0;
			}
		}

		lAllocate3DArrayDescriptor.Format = mFormat;

		lAllocate3DArrayDescriptor.NumChannels = (int) pNumberOfChannels;

		mCUarray = new CUarray();
		cuArray3DCreate(mCUarray, lAllocate3DArrayDescriptor);
		cuCtxSynchronize();
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

	public int getBytesPerVoxel()
	{
		return mBytesPerVoxel;
	}

	public int getNumberOfChannels()
	{
		return mFormat;
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


	public void copyFrom(long pNativeAddress, boolean pSync)
	{
		Pointer lPointer = JCudaPointerUtils.create(pNativeAddress);
		copyFrom(lPointer, pSync);
	}

	public void copyTo(long pNativeAddress, boolean pSync)
	{
		Pointer lPointer = JCudaPointerUtils.create(pNativeAddress);
		copyTo(lPointer, pSync);
	}

	public void copyFrom(ByteBuffer pByteBuffer, boolean pSync)
	{
		copyFrom(Pointer.toBuffer(pByteBuffer), pSync);
	}

	public void copyTo(ByteBuffer pByteBuffer, boolean pSync)
	{
		copyTo(Pointer.toBuffer(pByteBuffer), pSync);
	}

	public void copyFrom(Pointer pPointer, boolean pSync)
	{
		if (getDepth() == 1)
		{
			final CUDA_MEMCPY2D lCudaMemCopySpecification = new CUDA_MEMCPY2D();
			lCudaMemCopySpecification.srcMemoryType = CUmemorytype.CU_MEMORYTYPE_HOST;
			lCudaMemCopySpecification.srcHost = pPointer;
			lCudaMemCopySpecification.srcPitch = getWidth() * getBytesPerVoxel()
												* getNumberOfChannels();
			lCudaMemCopySpecification.dstMemoryType = CUmemorytype.CU_MEMORYTYPE_ARRAY;
			lCudaMemCopySpecification.dstArray = mCUarray;
			lCudaMemCopySpecification.WidthInBytes = getWidth() * getBytesPerVoxel()
														* getNumberOfChannels();
			lCudaMemCopySpecification.Height = getHeight();

			cuMemcpy2D(lCudaMemCopySpecification);
		}
		else
		{
			final CUDA_MEMCPY3D lCudaMemCopySpecification = new CUDA_MEMCPY3D();
			lCudaMemCopySpecification.srcMemoryType = CUmemorytype.CU_MEMORYTYPE_HOST;
			lCudaMemCopySpecification.srcHost = pPointer;
			lCudaMemCopySpecification.srcPitch = getWidth() * getBytesPerVoxel()
											* getNumberOfChannels();
			lCudaMemCopySpecification.srcHeight = getHeight();
			lCudaMemCopySpecification.dstMemoryType = CUmemorytype.CU_MEMORYTYPE_ARRAY;
			lCudaMemCopySpecification.dstArray = mCUarray;
			lCudaMemCopySpecification.dstPitch = getWidth() * getBytesPerVoxel();
			lCudaMemCopySpecification.dstHeight = getHeight();
			lCudaMemCopySpecification.WidthInBytes = getWidth() * getBytesPerVoxel()
													* getNumberOfChannels();
			lCudaMemCopySpecification.Height = getHeight();
			lCudaMemCopySpecification.Depth = getDepth();

			cuMemcpy3D(lCudaMemCopySpecification);
		}

		if (pSync)
			cuCtxSynchronize();
	}

	public void copyTo(Pointer pPointer, boolean pSync)
	{
		if (getDepth() == 1)
		{
			final CUDA_MEMCPY2D lCudaMemCopySpecification = new CUDA_MEMCPY2D();
			lCudaMemCopySpecification.srcMemoryType = CUmemorytype.CU_MEMORYTYPE_ARRAY;
			lCudaMemCopySpecification.srcPitch = getWidth() * getBytesPerVoxel()
																						* getNumberOfChannels();

			lCudaMemCopySpecification.dstHost = pPointer;
			lCudaMemCopySpecification.dstMemoryType = CUmemorytype.CU_MEMORYTYPE_HOST;
			lCudaMemCopySpecification.dstArray = mCUarray;

			lCudaMemCopySpecification.WidthInBytes = getWidth() * getBytesPerVoxel()
																								* getNumberOfChannels();
			lCudaMemCopySpecification.Height = getHeight();

			cuMemcpy2D(lCudaMemCopySpecification);
		}
		else
		{
			final CUDA_MEMCPY3D lCudaMemCopySpecification = new CUDA_MEMCPY3D();

			lCudaMemCopySpecification.srcMemoryType = CUmemorytype.CU_MEMORYTYPE_ARRAY;
			lCudaMemCopySpecification.srcPitch = getWidth() * getBytesPerVoxel()
																						* getNumberOfChannels();
			lCudaMemCopySpecification.srcHeight = getHeight();

			lCudaMemCopySpecification.dstHost = pPointer;
			lCudaMemCopySpecification.dstMemoryType = CUmemorytype.CU_MEMORYTYPE_HOST;
			lCudaMemCopySpecification.dstArray = mCUarray;
			lCudaMemCopySpecification.dstPitch = getWidth() * getBytesPerVoxel()
																						* getNumberOfChannels();
			lCudaMemCopySpecification.dstHeight = getHeight();

			lCudaMemCopySpecification.WidthInBytes = getWidth() * getBytesPerVoxel()
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
						+ ", mBytesPerVoxel="
						+ getBytesPerVoxel()
						+ ", mFloat="
						+ isFloat()
						+ ", mSigned="
						+ isSigned()
						+ "]";
	}


}
