package serverCore;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

import serverCore.serverActions.PendingAction;
import serverCore.serverActions.PendingInitiate;
import serverCore.serverActions.PendingTerminate;
import serverCore.serverActions.PendingWrite;



public class Server implements Runnable {
	private int port = 9001;
	private Selector selector;
	ByteBuffer writeBuff;
	ByteBuffer readBuff;
	Callee receiver;
	ConcurrentLinkedQueue<PendingAction> actionQueue;
	String parentAddress;

	public Server(boolean isChief, String parentAddress) {
		try {
			this.parentAddress = parentAddress;
			NetworkInterface ni;
			try {

				//find eth0
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

			if (!isChief){
				port = 9002;
			}
			//setup server listening socket
			ServerSocketChannel serverSocket = ServerSocketChannel.open();
			serverSocket.configureBlocking(false);
			serverSocket.socket().bind(new InetSocketAddress(port));
			serverSocket.register(selector, SelectionKey.OP_ACCEPT);

			readBuff = ByteBuffer.allocate(8192);
			writeBuff = ByteBuffer.allocate(8192);

			//TODO: dependency inject
			InetSocketAddress address = null;
			if (!isChief) {
				address = new InetSocketAddress(parentAddress, 9001);
			}
			receiver = new ResponseDirector(isChief, address, this);
			new Thread(receiver).start();
			actionQueue = new ConcurrentLinkedQueue<PendingAction>();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		boolean isChief = false;
		String address = null;
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		try {
			System.out.println("is this the head server?");
			if (reader.readLine().equals("y"))
				isChief = true;
			else {
				System.out.println("what is the server address?");
				address = reader.readLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		new Thread(new Server(isChief, address)).start();
	}

	@Override
	public void run() {
		try {
			while (true) {
				selector.select();
				PendingAction a;
				PendingWrite w;
				PendingInitiate i;
				
				//do the actions
				Iterator<PendingAction> queueIter = actionQueue.iterator();
				while(queueIter.hasNext()) {
					a = queueIter.next();

					if (a.isCancelled()){
						queueIter.remove();
						continue;
					} else if (a instanceof PendingWrite) {
						w = (PendingWrite)a;
						if (w.key.interestOps() != SelectionKey.OP_WRITE){
							w.key.interestOps(SelectionKey.OP_WRITE);
							w.key.attach(w);
							queueIter.remove();
						}
					} else if (a instanceof PendingTerminate) {
						a.key.channel().close();
						a.key.cancel();
						queueIter.remove();
					} else if (a instanceof PendingInitiate) {
						i = (PendingInitiate)a;
						i.channel.register(selector, SelectionKey.OP_CONNECT).attach(i.caller);
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
						acceptSelection(key);
					} else if (key.isConnectable()) {
						connectSelection(key);
					} else if (key.isReadable()) {
						readFromSelection(key);
					} else if (key.isWritable()) {
						writeToSelection(key);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}


	}

	private void writeToSelection(SelectionKey key) throws IOException {
		System.out.println("writing");
		PendingWrite w = (PendingWrite)key.attachment();
		writeBuff.clear();
		writeBuff.put(w.data);
		System.out.println(w.data.length);
		writeBuff.flip();
		((SocketChannel)key.channel()).write(writeBuff);
		if (w.terminates){
			key.channel().close();
			key.cancel();
		} else {
			key.interestOps(SelectionKey.OP_READ);
		}
	}

	private int readFromSelection(SelectionKey key) throws IOException {
		System.out.println("reading");
		readBuff.clear();
		int count = ((SocketChannel)key.channel()).read(readBuff);
		if (count == -1) {
			key.channel().close();
			key.cancel();
			System.out.println("--CLOSED--");
		} else {
			receiver.pickupCall(this, key, readBuff.array(), count);
		}
		return count;
	}

	private SocketChannel acceptSelection(SelectionKey key) throws IOException,
	ClosedChannelException {
		System.out.println("accepting");
		SocketChannel pending = ((ServerSocketChannel)key.channel()).accept();
		pending.configureBlocking(false);
		pending.register(selector, SelectionKey.OP_READ);
		return pending;
	}

	public void sendData(SelectionKey key, byte[] data, boolean terminate) {
		actionQueue.add(new PendingWrite(key, data, terminate));
		selector.wakeup();
	}

	public void terminateConnection(SelectionKey key) {
		actionQueue.add(new PendingTerminate(key));
		selector.wakeup();
	}

	public void initiateConnection(InetSocketAddress a, Caller c) throws IOException {
		SocketChannel sc = SocketChannel.open();
		sc.configureBlocking(false);
		sc.connect(a);
		actionQueue.add(new PendingInitiate(sc, c));
		selector.wakeup();
	}
	
	private void connectSelection(SelectionKey key) throws IOException {
		((SocketChannel)key.channel()).finishConnect();
		Caller c = (Caller)key.attachment();
		c.greetCounterparty(this, key);
	}
	
	public void sleep(int milliseconds) {

	}
}
