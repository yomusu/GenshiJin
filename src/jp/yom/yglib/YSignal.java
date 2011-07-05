package jp.yom.yglib;



/******************************************************
 * 
 * 
 * シグナル
 * 
 * 異なるスレッド間でのシグナルのやりとりを支援します
 * 
 * 
 * @author matsumoto
 *
 */
public class YSignal<T> {
	
	/** シグナル */
	private T	sign = null;
	
	
	
	/********************************************
	 * 
	 * シグナルをセットします
	 * 
	 * @param sign
	 */
	synchronized public void setSignal( T sign ) {
		this.sign = sign;
		notifyAll();
	}
	
	/********************************************
	 * 
	 * 現在のシグナル状態を取得します
	 * 
	 * @return
	 */
	public T getSignal() { return sign; }
	
	
	/*******************************************
	 * 
	 * シグナルが何かしらセットされるのを待ちます
	 * 
	 * @param waitTimeMillis
	 */
	synchronized public void waitForSignalOf( final T target, long waitTimeMillis ) throws InterruptedException, TimeOutException {
		
		long	s = System.currentTimeMillis();
		
		do {
			// シグナルがセットされたかどうか
			if( sign!=null ) {
				if( sign.equals(target) )
					return;
			}
			
			// 待ち
			wait( waitTimeMillis );
			System.out.println("do:"+sign);
			
			// 時間オーバーのチェック
		} while( waitTimeMillis > ( System.currentTimeMillis() - s ) );
		
		throw new TimeOutException();
	}
	
	
	
	
	static public void main( String[] args ) {
		
		final YSignal<Boolean>	sig = new YSignal<Boolean>();
		
		Runnable	r1 = new Runnable() {
			
			@Override
			public void run() {
				
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
				}
				
				sig.setSignal( Boolean.TRUE );
				System.out.println( "thread: I set signal True." );
				
				
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
				}
				
				sig.setSignal( Boolean.FALSE );
				System.out.println( "thread: I set False to signal." );
			}
		};
		
		new Thread(r1).start();
		
		
		try {
			sig.waitForSignalOf( false, 10000 );
			
			System.out.println( "sig="+sig.getSignal() );
			
		} catch ( TimeOutException e ) {
			System.out.println("Timeout");
		} catch (InterruptedException e) {
			System.out.println("Interrupt");
		}
	}
}
