package test1;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.shimizukenta.hokuyoopticalparallel.dme.DME;
import com.shimizukenta.hokuyoopticalparallel.dme.DMEConfig;
import com.shimizukenta.hokuyoopticalparallel.dme.DMESendPacket;

public class DMETestStab implements Closeable {
	
//	private static final SocketAddress dmeSocketAddess = new InetSocketAddress("192.168.0.1", 10940);
//	private static final SocketAddress pcSocketAddress = new InetSocketAddress("192.168.0.10", 0);
	
	private static final SocketAddress dmeSocketAddress = new InetSocketAddress("127.0.0.1", 10940);
	private static final SocketAddress pcSocketAddress = new InetSocketAddress("127.0.0.1", 0);
	
	private byte lastMode;
	private final AtomicInteger autoNumber = new AtomicInteger();
	
	public DMETestStab() {
		this.lastMode = (byte)0x0;
	}
	
	public void open() throws IOException {
		
	}
	
	public void close() throws IOException {
		
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
				DMETestStab stab = new DMETestStab();
				) {
			
			stab.open();
			
			DMEConfig conf = new DMEConfig();
			conf.bindSocketAddress(pcSocketAddress);
			conf.addConnect(dmeSocketAddress);
			
			try (
					DME pc = new DME(conf);
					) {
				
				pc.addIOLogListener(v -> {echo(v);});
				
				pc.addReceiveDataListener(r -> {
					
				});
				
				pc.addCommunicateStateChangedListener(f -> {
					
					if ( f ) {
						
						new Thread(() -> {
							
							try {
								TimeUnit.SECONDS.sleep(1L);
								
								pc.send(DMESendPacket.from((byte)0xF));
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
