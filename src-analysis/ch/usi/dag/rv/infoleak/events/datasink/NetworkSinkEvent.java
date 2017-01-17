package ch.usi.dag.rv.infoleak.events.datasink;

import java.io.FileDescriptor;
import java.net.InetAddress;
import java.util.HashMap;

public class NetworkSinkEvent extends DataSinkEvent{
	static HashMap<FileDescriptor, InetAddress> fdToAddress = new HashMap<FileDescriptor, InetAddress>();
	public static void registerFd(FileDescriptor fd, InetAddress address){
		if(fd != null && address != null){
			fdToAddress.put(fd, address);
		}
	}
	
    public NetworkSinkEvent (
    		String dexName, final FileDescriptor fd, final byte [] buffer, final int byteOffset,
        final int sentSize, final int flags, final InetAddress address, final int port) {
        super(dexName,
            "NetworkSend to "
            +(!fdToAddress.containsKey(fd)?"":fdToAddress.get(fd).getHostAddress())
            +":"+port
            , buffer, byteOffset, sentSize
            );
    }
}
