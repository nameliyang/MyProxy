import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerTest {
	
	public static void main(String[] args) throws IOException {
		Server server = new Server(8080);
		server.doStart();
	}
	
	static class Server{
		
		Selector selector;
		Logger logger = Logger.getAnonymousLogger();
		
		public Server(int port) throws IOException{
			selector = Selector.open();
			ServerSocketChannel serverChannel = ServerSocketChannel.open();
			serverChannel.configureBlocking(false);
			serverChannel.bind(new InetSocketAddress("127.0.01", port));
			serverChannel.register(selector, SelectionKey.OP_ACCEPT);
		}
		
		public void doStart() throws IOException{
			while(selector.select()>0){
				Set<SelectionKey> selectedKeys = selector.selectedKeys();
				Iterator<SelectionKey> iterator = selectedKeys.iterator();
				while(iterator.hasNext()){
					SelectionKey key = iterator.next();
					iterator.remove();
					handler(key);
				}
			}
		}

		private void handler(SelectionKey key) throws IOException {
			
			if(!key.isValid()){
				return;
			}
			if(key.isAcceptable()){
				ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
				SocketChannel socketChannel = serverChannel.accept();
				socketChannel.configureBlocking(false);
				socketChannel.register(selector, SelectionKey.OP_READ,ByteBuffer.allocate(4));
			}else  if(key.isWritable()){
				System.out.println("writerable ...");
			}else if(key.isReadable()){
				SocketChannel socketChannel = (SocketChannel) key.channel();
				ByteBuffer buffer = (ByteBuffer) key.attachment();
				socketChannel.read(buffer);
				String readBufferAsString = readBufferAsString(buffer);
				logger.log(Level.INFO, "read msg:"+readBufferAsString);
				if("q".equals(readBufferAsString)){
//					key.cancel();
					socketChannel.close();
				}
				buffer.clear();
			}
		}

		private String readBufferAsString(ByteBuffer buffer) {
			StringBuilder sb = new StringBuilder();
			buffer.flip();
			while(buffer.hasRemaining()){
				sb.append((char)buffer.get());
			}
			return sb.toString();
		}
		
	}
	
	class Session{
		
	}
}
