package com.shimizukenta.hokuyoopticalparallel;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * This class is Log, includes subuect, timestamp, detail-information.
 * 
 * <p>
 * To get subject, {@link #subject()}.<br />
 * To get timestamp, {@link #timestamp()}.<br />
 * To get detail-information, {@link #value()}.<br />
 * </p>
 * 
 * @author kenta-shimizu
 *
 */
public class IOLog {

	protected static final String BR = System.lineSeparator();
	protected static final String SPACE = "\t";
	
	private static DateTimeFormatter DATETIME = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
	
	private final String subject;
	private final LocalDateTime timestamp;
	private final Object value;
	
	public IOLog(CharSequence subject, LocalDateTime timestamp, Object value) {
		this.subject = subject.toString();
		this.timestamp = timestamp;
		this.value = value;
	}
	
	public IOLog(CharSequence subject, LocalDateTime timestamp) {
		this(subject, timestamp, null);
	}
	
	public IOLog(CharSequence subject, Object value) {
		this(subject, LocalDateTime.now(), value);
	}
	
	public IOLog(CharSequence subject) {
		this(subject, LocalDateTime.now(), null);
	}
	
	public IOLog(Throwable t) {
		this("Throwable", LocalDateTime.now(), t);
	}
	
	/**
	 * Return subject.
	 * 
	 * @return subject-string
	 */
	public String subject() {
		return this.subject;
	}
	
	/**
	 * Returns timestamp.
	 * 
	 * @return timestamp-LocalDateTime
	 */
	public LocalDateTime timestamp() {
		return this.timestamp;
	}
	
	/**
	 * Returns detail-information.
	 * 
	 * @return valur if exist, and {@code Optional.empty()} otherwise
	 */
	public Optional<Object> value() {
		return value == null ? Optional.empty() : Optional.of(value);
	}
	
	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder()
							.append(toStringTimestamp())
							.append(SPACE)
							.append(subject());
		
		String v = toStringValue();
		if ( ! v.isEmpty() ) {
			sb.append(BR).append(v);
		}
		
		return sb.toString();
	}
	
	protected String toStringTimestamp() {
		return timestamp().format( DATETIME );
	}
	
	protected String toStringValue() {
		
		return value().map(o -> {
			
			if ( o instanceof Throwable ) {
				
				try (
						StringWriter sw = new StringWriter();
						) {
					
					try (
							PrintWriter pw = new PrintWriter(sw);
							) {
						
						((Throwable) o).printStackTrace(pw);
						pw.flush();
						
						return sw.toString();
					}
				}
				catch ( IOException e ) {
					return e.toString();
				}
				
			} else {
				
				return o.toString();
			}
			
		})
		.orElse("");
	}
}
