package graphics3d;

import com.google.common.collect.ImmutableList;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 Supports only one write at a time, and only one read at a time.
 */
public class TripleBuffering<T> {
	
	private final ImmutableList<T> buffers;
	private T bufferWriting, bufferReading, bufferReadNext;
	
	
	
	public TripleBuffering(T buffer0, T buffer1, T buffer2) {
		buffers = ImmutableList.of(buffer0, buffer1, buffer2);
		bufferReadNext = buffer0;
	}
	
	
	public TripleBuffering(Supplier<T> bufferSupplier) {
		this(bufferSupplier.get(), bufferSupplier.get(), bufferSupplier.get());
	}
	
	
	public void read(Consumer<T> bufferAction) {
		T b = readingStart();
		bufferAction.accept(b);
		readingDone();
	}
	
	
	public void write(Consumer<T> bufferAction) {
		T b = writingStart();
		bufferAction.accept(b);
		writingDone();
	}
	
	
	public synchronized T readingStart() {
		if (bufferReading != null) {
			throw new IllegalStateException("Only one reading at a time allowed.");
		}
		bufferReading = bufferReadNext;
		return bufferReading;
	}
	
	
	public synchronized void readingDone() {
		bufferReading = null;
	}
	
	
	public synchronized T writingStart() {
		if (bufferWriting != null) {
			throw new IllegalStateException("Only one writing at a time allowed.");
		}
		
		for (T b : buffers) {
			if (b != bufferReadNext && b != bufferReading) {
				bufferWriting = b;
				break;
			}
		}
		return bufferWriting;
	}
	
	
	public synchronized void writingDone() {
		bufferReadNext = bufferWriting;
		bufferWriting = null;
	}
	
}
