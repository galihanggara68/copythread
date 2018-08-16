
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

class Signals{
	private boolean hasDone = false;
	
	public boolean hasDone(){
		return this.hasDone;
	}
	
	public void setHasDone(boolean s){
		this.hasDone = s;
	}
}

class ProgressThread extends Thread{
	private String name;
	private Signals signal;
	private Thread t;
	
	public ProgressThread(String name, Signals signal){
		this.name = name;
		this.signal = signal;
	}
	
	public void run(){
		while(!this.signal.hasDone()){
			System.out.print(".");
			try {
				TimeUnit.MILLISECONDS.sleep(200);
			} catch (InterruptedException e) {
				System.out.println("Thread interrupted");
			}
		}
	}
	
	public void start(){
		if(this.t == null){
			this.t = new Thread(this, this.name);
			t.start();
		}
	}
}

class CopyThread extends Thread{
	private String name;
	private Signals signal;
	private Thread t;
	private FileInputStream fis;
	private FileOutputStream fos;
	
	public CopyThread(String name, Signals signal, String srcName, String destName) throws FileNotFoundException{
		this.name = name;
		this.signal = signal;
		this.fis = new FileInputStream(srcName);
		this.fos = new FileOutputStream(destName);
	}
	
	public void run(){
		int c;
		try {
			while((c = fis.read()) != -1){
				fos.write(c);
			}
		} catch (IOException e) {
			System.out.println("File Corrupt !");
		}finally{
			this.signal.setHasDone(true);
			System.out.print(" Ok");
		}
	}
	
	public void start(){
		System.out.print("Copying File ");
		if(this.t == null){
			this.t = new Thread(this, this.name);
			t.start();
		}
	}
}

public class FileIO {

	public static void main(String[] args) throws FileNotFoundException{
		Signals signal = new Signals();
		CopyThread ct = new CopyThread("Copyer", signal, args[0], args[1]);
		ProgressThread pt = new ProgressThread("ProgressDot", signal);
		ct.start();
		pt.start();
	}
	
}
