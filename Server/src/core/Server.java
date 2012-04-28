package core;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;



public class Server implements Runnable {
	private static final int port = 9001;
	private Selector selector;
	ByteBuffer writeBuff;
	ByteBuffer readBuff;
	Director director;
	ConcurrentLinkedQueue<PendingWrite> writeQueue;

	public Server() {
		try {
			NetworkInterface ni;
			try {
				ni = NetworkInterface.getByName("eth0");
				Enumeration<InetAddress> a = ni.getInetAddresses();
				InetAddress i = null;
				while (a.hasMoreElements()){
					i = a.nextElement();
					//if (i.IS THE IP ADDRESS??)
					System.out.println(a.nextElement());
				}
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			selector = Selector.open();
			ServerSocketChannel serverSocket = ServerSocketChannel.open();
			serverSocket.configureBlocking(false);
			serverSocket.socket().bind(new InetSocketAddress(port));
			serverSocket.register(selector, SelectionKey.OP_ACCEPT);
			readBuff = ByteBuffer.allocate(1000);
			writeBuff = ByteBuffer.allocate(1000);
			//echo director
			director = new StudentDirector();
			new Thread(director).start();
			writeQueue = new ConcurrentLinkedQueue<PendingWrite>();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new Thread(new Server()).start();
	}

	@Override
	public void run() {
		try {
			while (true) {
				selector.select();
				PendingWrite w;
				Iterator<PendingWrite> queueIter = writeQueue.iterator();
				while(queueIter.hasNext()) {
					w = queueIter.next();
					if (w.key.interestOps() != SelectionKey.OP_WRITE){
						w.key.interestOps(SelectionKey.OP_WRITE);
						w.key.attach(w.data);
						queueIter.remove();
					}
				} 
				
				Iterator<SelectionKey> keyIter = selector.selectedKeys().iterator();
				SelectionKey key;
				while (keyIter.hasNext()) {
					key = keyIter.next();
					keyIter.remove();
					if (!key.isValid()) 
						continue;
					if (key.isAcceptable()) {
						accept(key);
					} else if (key.isReadable()) {
						read(key);
					} else if (key.isWritable()) {
						write(key);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}


	}

	private void write(SelectionKey key) throws IOException {
		System.out.println("writing");
		writeBuff.clear();
		writeBuff.put((byte[])key.attachment());
		System.out.println(((byte[])key.attachment()).length);
		writeBuff.flip();
		((SocketChannel)key.channel()).write(writeBuff);
		key.channel().close();
		key.cancel();
		//key.interestOps(SelectionKey.OP_READ);
	}

	private int read(SelectionKey key) throws IOException {
		System.out.println("reading");
		readBuff.clear();
		int count = ((SocketChannel)key.channel()).read(readBuff);
		if (count == -1) {
			key.channel().close();
			key.cancel();
			System.out.println("closed");
		} else {
			director.parseLater(this, key, readBuff.array(), count);
		}
		return count;
	}

	private SocketChannel accept(SelectionKey key) throws IOException,
	ClosedChannelException {
		System.out.println("accepting");
		SocketChannel pending = ((ServerSocketChannel)key.channel()).accept();
		pending.configureBlocking(false);
		pending.register(selector, SelectionKey.OP_READ);
		return pending;
	}

	public void send(SelectionKey key, byte[] data) {
		writeQueue.add(new PendingWrite(key, data));
		selector.wakeup();
	}

	public class PendingWrite {
		SelectionKey key;
		byte[] data;

		public PendingWrite(SelectionKey k, byte[] d) {
			key = k;
			data = d;
		}
	}
}
