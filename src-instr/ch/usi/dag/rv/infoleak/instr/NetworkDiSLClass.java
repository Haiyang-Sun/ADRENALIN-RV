package ch.usi.dag.rv.infoleak.instr;

import java.io.FileDescriptor;
import java.net.InetAddress;
import java.nio.ByteBuffer;

import ch.usi.dag.disl.annotation.Before;
import ch.usi.dag.disl.marker.BodyMarker;
import ch.usi.dag.disl.processorcontext.ArgumentProcessorContext;
import ch.usi.dag.disl.processorcontext.ArgumentProcessorMode;
import ch.usi.dag.disl.staticcontext.DexStaticContext;
import ch.usi.dag.rv.infoleak.DataLeakMonitorState;
import ch.usi.dag.rv.infoleak.events.datasink.NetworkSinkEvent;
import ch.usi.dag.rv.utils.DefaultLog;

public class NetworkDiSLClass {
	
	@Before(marker = BodyMarker.class, order = 1, scope = "libcore.io.IoBridge.sendto(java.io.FileDescriptor,byte[],int,int,int,java.net.InetAddress,int)")
	public static void sendto(final ArgumentProcessorContext apc, final DexStaticContext dsc) {
		final Object[] args = apc.getArgs(ArgumentProcessorMode.METHOD_ARGS);
		final FileDescriptor fd = (FileDescriptor) args[0];
		final byte[] buffer = (byte[]) args[1];
		final int byteOffset = (int) args[2];
		final int byteCount = (int)args[3];
		final int flags = (int) args[4];
		final InetAddress address = (InetAddress) args[5];
		final int port = (int) args[6];
		DataLeakMonitorState.getInstance().newEvent(
				new NetworkSinkEvent(dsc.getDexShortName(), fd, buffer, byteOffset, byteCount,
						flags, address, port));
	}
	@Before(marker = BodyMarker.class, order = 1, scope = "libcore.io.IoBridge.sendto(java.io.FileDescriptor,java.nio.ByteBuffer,int,java.net.InetAddress,int)")
	public static void sendto_bytebuffer(final ArgumentProcessorContext apc, final DexStaticContext dsc){
		final Object[] args = apc.getArgs(ArgumentProcessorMode.METHOD_ARGS);
		final FileDescriptor fd = (FileDescriptor) args[0];
		final ByteBuffer buffer = (ByteBuffer) args[1];
		final int flags = (int) args[2];
		final InetAddress address = (InetAddress) args[3];
		final int port = (int) args[4];
		if (buffer != null) {
			DataLeakMonitorState.getInstance().newEvent(
					new NetworkSinkEvent(dsc.getDexShortName(), fd, buffer.array(), buffer.position(), buffer.remaining(), flags, address, port));
		}
	}
	
	 @Before(marker=BodyMarker.class,
	    order = 1,
	    scope="libcore.io.IoBridge.connect(java.io.FileDescriptor,java.net.InetAddress,int,int)")
	    public static void network_connect(final ArgumentProcessorContext apc){
	        final Object [] args = apc.getArgs (ArgumentProcessorMode.METHOD_ARGS);
	        final FileDescriptor fd = (FileDescriptor)args[0];
	        final InetAddress address = (InetAddress)args[1];
	        final int port = (int)args[2];
	        final int timeoutMs = (int) args[3];
	        NetworkSinkEvent.registerFd(fd, address);
	    }
}
