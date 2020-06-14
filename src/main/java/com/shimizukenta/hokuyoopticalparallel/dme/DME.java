package com.shimizukenta.hokuyoopticalparallel.dme;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.shimizukenta.hokuyoopticalparallel.AbstractHokuyoOpticalParallel;
import com.shimizukenta.hokuyoopticalparallel.CommunicateStateChangedListener;
import com.shimizukenta.hokuyoopticalparallel.IOLog;

public final class DME extends AbstractHokuyoOpticalParallel<DMEReceivePacket, Boolean> {
	
	private final ExecutorService execServ = Executors.newCachedThreadPool(r -> {
		Thread th = new Thread(r);
		th.setDaemon(true);
		return th;
	});
	
	private Collection<DatagramChannel> channels = new CopyOnWriteArrayList<>();
	
	private final DMEConfig config;
	
	private boolean opened;
	private boolean closed;
	private boolean lastCommunicateState;
	
	public DME(DMEConfig config) {
		super();
		this.config = config;
		this.opened = false;
		this.closed = false;
		this.lastCommunicateState = false;
	}
	
	public static DME open(DMEConfig config) throws IOException {
		
		final DME inst = new DME(config);
		
		try {
			inst.open();
		}
		catch ( IOException e ) {
			
			try {
				inst.close();
			}
			catch ( IOException giveup ) {
			}
			
			throw e;
		}
		
		return inst;
	}
	
	@Override
	public void open() throws IOException {
		
		synchronized ( this ) {
			
			if ( this.closed ) {
				throw new IOException("Already closed");
			}
			
			if ( this.opened ) {
				throw new IOException("Already opened");
			}
			
			this.opened = true;
		}
		
		execServ.execute(this.createWriteBytesQueueTask());
		execServ.execute(this.createReceivePacketQueueTask());
		execServ.execute(this.createOpenChannelTask());
	}

	@Override
	public void close() throws IOException {
		
		synchronized ( this ) {
			
			if ( this.closed ) {
				return ;
			}
			
			this.closed = true;
		}
		
		try {
			execServ.shutdown();
			if ( ! execServ.awaitTermination(1L, TimeUnit.MILLISECONDS ) ) {
				execServ.shutdownNow();
				if ( ! execServ.awaitTermination(5L, TimeUnit.SECONDS ) ) {
					throw new IOException("ExecutorService#shutdown failed");
				}
			}
		}
		catch ( InterruptedException ignore ) {
		}
	}
	
	@Override
	public void write(byte[] bs) throws IOException {
		
		final int bufferSize = bs.length;
		
		for ( DatagramChannel channel : channels ) {
			
			for ( SocketAddress remote : config.connects() ) {
				
				final ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
				buffer.put(bs);
				((Buffer)buffer).flip();
				
				while ( buffer.hasRemaining() ) {
					int w = channel.send(buffer, remote);
					if ( w <= 0 ) {
						break;
					}
				}
			}
		}
	}
	
	@Override
	public boolean addCommunicateStateChangedListener(CommunicateStateChangedListener<Boolean> l) {
		synchronized ( this ) {
			l.changed(this.lastCommunicateState);
			return super.addCommunicateStateChangedListener(l);
		}
	}
	
	private void changeCommunicateState() {
		synchronized ( this ) {
			boolean f = ! this.channels.isEmpty();
			if ( f != this.lastCommunicateState ) {
				this.lastCommunicateState = f;
				putCommunicateStateChanged(f);
			}
		}
	}
	
	private void addChannel(DatagramChannel channel) {
		this.channels.add(channel);
		changeCommunicateState();
	}
	
	private void clearChannel() {
		this.channels.clear();
		changeCommunicateState();
	}
	
	private static interface InterruptableRunnable {
		public void run() throws InterruptedException;
	}
	
	private Runnable createLoopTask(InterruptableRunnable r) {
		return new Runnable() {
			@Override
			public void run() {
				try {
					for ( ;; ) {
						r.run();
					}
				}
				catch ( InterruptedException ignore ) {
				}
			}
		};
	}
	
	private Runnable createOpenChannelTask() {
		
		return createLoopTask(() -> {
			
			try {
				final SocketAddress local = config.bindSocketAddress()
						.orElseThrow(() -> new IOException("Bind-Address not setted"));
				
				try (
						DatagramChannel channel = DatagramChannel.open();
						) {
					
					channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
					channel.bind(local);
					
					addChannel(channel);
					
					final String localAddrString = channel.getLocalAddress().toString();
					
					final Collection<Callable<Object>> tasks = Arrays.asList(
							this.createReadingTask(channel),
							() -> {
								try {
									synchronized ( this ) {
										channel.wait();
									}
								}
								catch ( InterruptedException ignore ) {
								}
								return null;
							});
					
					
					try {
						putOpenedLog(localAddrString);
						execServ.invokeAny(tasks);
					}
					catch (ExecutionException e ) {
						putIOLog(e.getCause());
					}
					finally {
						putClosedLog(localAddrString);
					}
				}
				finally {
					clearChannel();
				}
			}
			catch ( IOException e ) {
				putIOLog(e);
			}
			
			{
				long t = (long)(config.rebindSeconds() * 1000.0F);
				if ( t > 0 ) {
					TimeUnit.MILLISECONDS.sleep(t);
				}
			}
		});
	}
	
	private Callable<Object> createReadingTask(DatagramChannel channel) {
		
		return new Callable<Object>() {
			
			@Override
			public Object call() {
				
				final Collection<Inner> inners = config.connects().stream()
						.map(a -> new Inner(a))
						.collect(Collectors.toList());
				
				final ByteBuffer buffer = ByteBuffer.allocate(1024);
				
				try {
					for ( ;; ) {
						((Buffer)buffer).clear();
						
						SocketAddress remote = channel.receive(buffer);
						
						((Buffer)buffer).flip();
						byte[] bs = new byte[buffer.remaining()];
						buffer.get(bs);
						
						for ( Inner i : inners ) {
							i.put(remote, bs);
						}
					}
				}
				catch ( InterruptedException ignore ) {
				}
				catch ( ClosedChannelException ignore ) {
				}
				catch ( IOException e ) {
					putIOLog(e);
				}
				
				return null;
			}
		};
	}
	
	private static final byte LF = (byte)0xA;
	
	private class Inner {
		
		private final SocketAddress ref;
		private final ByteBuffer buffer = ByteBuffer.allocate(256);
		
		private Inner(SocketAddress remote) {
			this.ref = remote;
		}
		
		public void put(SocketAddress remote, byte[] bs) throws InterruptedException {
			
			if ( remote != null && ref.equals(remote) ) {
				
				for ( byte b : bs ) {
					
					if ( b == LF ) {
						
						((Buffer)buffer).flip();
						
						byte[] bb = new byte[buffer.remaining()];
						buffer.get(bb);
						
						((Buffer)buffer).clear();
						
						DMEReceivePacket p = new DMEReceivePacket(bs, ref);
						recvPacketQueue.put(p);
						putReceivedLog(p);
						
					} else {
						
						if ( buffer.hasRemaining() ) {
							buffer.put(b);
						}
					}
				}
			}
		}
	}
	
	private final BlockingQueue<DMEReceivePacket> recvPacketQueue = new LinkedBlockingQueue<>();
	
	private Runnable createReceivePacketQueueTask() {
		return createLoopTask(() -> {
			putReceiveData(recvPacketQueue.take());
		});
	}
	
	
	private final BlockingQueue<byte[]> writeBytesQueue = new LinkedBlockingQueue<>();
	
	private Runnable createWriteBytesQueueTask() {
		return createLoopTask(() -> {
			byte[] bs = writeBytesQueue.take();
			try {
				write(bs);
			}
			catch ( ClosedChannelException ignore ) {
			}
			catch ( IOException e ) {
				putIOLog(e);
			}
		});
	}
	
	public void send(DMESendPacket packet) throws InterruptedException {
		putTrySendLog(packet);
		writeBytesQueue.put(packet.getBytes());
	}
	
	public void send(DMEInputData input) throws InterruptedException {
		send(DMESendPacket.from(input));
	}
	
	public void send(DMEInput... inputs) throws InterruptedException {
		send(DMESendPacket.from(inputs));
	}
	
	public void send(DMEModePacket packet) throws InterruptedException {
		putTrySendLog(packet);
		writeBytesQueue.put(packet.getBytes());
	}
	
	public void send(DMEModeData mode) throws InterruptedException {
		send(DMEModePacket.from(mode));
	}
	
	public void send(DMEMode... modes) throws InterruptedException {
		send(DMEModePacket.from(modes));
	}
	
	private void putOpenedLog(Object value) {
		putIOLog(new IOLog("opened", value));
	}
	
	private void putClosedLog(Object value) {
		putIOLog(new IOLog("closed", value));
	}
	
	private void putReceivedLog(Object value) {
		putIOLog(new IOLog("received", value));
	}
	
	private void putTrySendLog(Object value) {
		putIOLog(new IOLog("try-send", value));
	}
	
}
