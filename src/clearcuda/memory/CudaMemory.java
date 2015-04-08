package clearcuda.memory;

import jcuda.CudaException;
import clearcuda.CudaHostPointer;
import clearcuda.utils.JCudaPointerUtils;
import coremem.ContiguousMemoryInterface;
import coremem.MemoryBase;
import coremem.interfaces.MemoryType;
import coremem.rgc.Cleaner;

public class CudaMemory extends MemoryBase implements
																					ContiguousMemoryInterface
{

	private final CudaHostPointer mCudaHostPointer;

	public CudaMemory(CudaHostPointer pCudaHostPointer)
	{
		super(JCudaPointerUtils.getNativeAddress(pCudaHostPointer.getPointer()),
					pCudaHostPointer.getSizeInBytes());
		mCudaHostPointer = pCudaHostPointer;
	}

	public CudaHostPointer getCudaHostPointer()
	{
		return mCudaHostPointer;
	}

	@Override
	public Cleaner getCleaner()
	{
		return null;
	}

	@Override
	public ContiguousMemoryInterface subRegion(	long pOffset,
																							long pLenghInBytes)
	{
		throw new CudaException("Cannot resize CudaHostPointer!");
	}

	@Override
	public MemoryType getMemoryType()
	{
		return MemoryType.CPURAMGPUMAPPED;
	}

}
