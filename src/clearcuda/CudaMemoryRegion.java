package clearcuda;

import coremem.MemoryRegionBase;
import coremem.MemoryRegionInterface;
import coremem.interfaces.MemoryType;
import coremem.rgc.Cleaner;

public class CudaMemoryRegion<T> extends MemoryRegionBase<T> implements
																														MemoryRegionInterface<T>
{

	private CudaHostPointer mCudaHostPointer;

	public CudaMemoryRegion(CudaHostPointer pCudaHostPointer)
	{
		super();
		mCudaHostPointer = pCudaHostPointer;
	}


	@Override
	public Cleaner getCleaner()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MemoryRegionInterface<T> subRegion(long pOffset,
																				long pLenghInBytes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MemoryType getMemoryType()
	{
		return MemoryType.CPURAMGPUMAPPED;
	}


}
