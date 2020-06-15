package test1;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.shimizukenta.hokuyoopticalparallel.dme.DME;
import com.shimizukenta.hokuyoopticalparallel.dme.DMEConfig;
import com.shimizukenta.hokuyoopticalparallel.dme.DMEInputData;
import com.shimizukenta.hokuyoopticalparallel.dme.DMEMode;

public class DMETestStab implements Closeable {
	
//	private static final SocketAddress dmeSocketAddess = new InetSocketAddress("192.168.0.1", 10940);
//	private static final SocketAddress pcSocketAddress = new InetSocketAddress("192.168.0.10", 0);
	
	private static final SocketAddress dmeSocketAddress = new InetSocketAddress("127.0.0.1", 10940);
	private static final SocketAddress pcSocketAddress = new InetSocketAddress("127.0.0.1", 0);
	
	private static final byte LF = (byte)0x0A;
	private static final byte M =  (byte)0x4D;
	private static final byte R =  (byte)0x52;
	
	private final ExecutorService execServ = Executors.newCachedThreadPool(r -> {
		Thread th = new Thread(r);
		th.setDaemon(true);
		return th;
	});
	
	private final SocketAddress bindSocketAddress;
	private DatagramChannel channel;
	private SocketAddress lastConnect;
	private byte lastMode;
	
	public DMETestStab(SocketAddress a) {
		this.bindSocketAddress = a;
		this.channel = null;
		this.lastConnect = null;
		this.lastMode = (byte)0x0;
	}
	
	public void open() throws IOException {
		
		this.channel = DatagramChannel.open();
		
		this.channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
		this.channel.bind(bindSocketAddress);
		
		execServ.execute(() -> {
			
			/* reading */
			try {
				final ByteBuffer buffer = ByteBuffer.allocate(1024);
				final ByteBuffer stock = ByteBuffer.allocate(2);
				
				for ( ;; ) {
					
					((Buffer)buffer).clear();
					SocketAddress a = channel.receive(buffer);
					
					((Buffer)buffer).flip();
					
					while ( buffer.hasRemaining() ) {
						
						byte b = buffer.get();
						
						if ( stock.hasRemaining() ) {
							
							stock.put(b);
							
						} else {
							
							if ( b == LF ) {
								
								((Buffer)stock).flip();
								byte[] bs = new byte[stock.remaining()];
								stock.get(bs);
								
								if ( bs[0] == M ) {
									synchronized ( this ) {
										this.lastConnect = a;
										this.lastMode = bs[1];
									}
								}
							}
						}
					}
				}
			}
			catch ( ClosedChannelException ignore ) {
			}
			catch ( IOException e ) {
				echo(e);
			}
		});;
		
		execServ.execute(() -> {
			
			/* sending */
			try {
				
				final AtomicInteger autoNumber = new AtomicInteger();
				
				final byte[] bs = new byte[] {
						R,
						(byte)(autoNumber.get()),
						(byte)(0b01010101),
						this.lastMode,
						LF
				};
				
				final ByteBuffer buffer = ByteBuffer.allocate(bs.length);
				
				for ( ;; ) {
					
					synchronized ( this ) {
						
						if ( this.lastConnect != null ) {
							
							try {
								
								bs[1] = (byte)(autoNumber.incrementAndGet());
								bs[2] = (byte)(~ bs[2]);
								bs[3] = (byte)(this.lastMode ^ 0b00000001);
								
								((Buffer)buffer).clear();
								buffer.put(bs);
								((Buffer)buffer).flip();
								
								while ( buffer.hasRemaining() ) {
									this.channel.send(buffer, lastConnect);
								}
							}
							catch ( ClosedChannelException e ) {
								throw e;
							}
							catch (IOException e) {
								echo(e);
							}
						}
					}
					
					TimeUnit.SECONDS.sleep(2L);
				}
			}
			catch ( IOException | InterruptedException ignore ) {
			}
		});
	}
	
	public void close() throws IOException {
		
		IOException ioExcept = null;
		
		if ( this.channel != null ) {
			try {
				channel.close();
			}
			catch (IOException e) {
				ioExcept = e;
			}
		}
		
		try {
			execServ.shutdown();
			if (! execServ.awaitTermination(1L, TimeUnit.MILLISECONDS)) {
				execServ.shutdownNow();
				if (! execServ.awaitTermination(5L, TimeUnit.SECONDS)) {
					ioExcept = new IOException("ExecutorService#shutdown failed");
				}
			}
		}
		catch ( InterruptedException ignore ) {
		}
		
		if ( ioExcept != null ) {
			throw ioExcept;
		}
	}
	
	
	private static final Object syncEcho = new Object();
	private static void echo(Object o) {
		synchronized ( syncEcho ) {
			if ( o instanceof Throwable ) {
				((Throwable) o).printStackTrace();
			} else {
				System.out.println(o);
				System.out.println();
			}
		}
	}
	
	public static void main(String[] args) {
		
		try (
				DMETestStab stab = new DMETestStab(dmeSocketAddress);
				) {
			
			stab.open();
			
			DMEConfig config = new DMEConfig();
			config.bindSocketAddress(pcSocketAddress);
			config.addConnect(dmeSocketAddress);
			
			try (
					DME pc = new DME(config);
					) {
				
				pc.addIOLogListener(v -> {echo(v);});
				
				pc.addReceiveListener(r -> {
//					echo(r);
//					echo("GO_ON  : " + r.isMode(DMEMode.GO_ON));
//					echo("GO_OFF : " + r.isMode(DMEMode.GO_OFF));
				});
				
				pc.addCommunicateStateChangedListener(f -> {
					
					if ( f ) {
						
						new Thread(() -> {
							
							try {
								TimeUnit.SECONDS.sleep(1L);
								
								pc.send(
										DMEMode.MODE_OFF,
										DMEMode.SELECT_OFF
										);
								
								pc.send(
										DMEInputData.initial()
										);
							}
							catch ( InterruptedException ignore ) {
							}
							
						}).start();
					}
				});
				
				pc.open();
				
				synchronized ( DMETestStab.class ) {
					DMETestStab.class.wait();
				}
			}
			catch ( InterruptedException ignore ) {
			}
		}
		catch ( Throwable t ) {
			echo(t);
		}
	}

}
