package jp.yom.yglib;



/**************************************************************
 * 
 * 
 * 
 * ストップウォッチ
 * 経過時間(ミリ秒)を計測します
 * 
 * 
 * @author matsumoto
 *
 */
public class StopWatch {
	
	
	/** リセットした時間(ミリ秒) */
	protected long	resetTime = 0;
	
	
	/******************************************
	 * 
	 * 経過時間を取得する
	 * @return
	 */
	public long watch() {
		
		return System.currentTimeMillis() - resetTime;
		
	}
	
	
	/******************************************
	 * 
	 * 経過時間をリセットする
	 */
	public void reset() {
		resetTime = System.currentTimeMillis();
	}
	
	
	/*******************************************
	 * 
	 * 経過時間を戻す
	 * 
	 * @param millis
	 */
	public void back( long millis ) {
		resetTime += millis;
	}
	
	
	
	
	/*********************************************
	 * 
	 * 使い方
	 * @param args
	 */
	static public void test() {
		
		StopWatch	t = new StopWatch();
		
		t.reset();
		
		int	n = 0;
		
		while( true ) {
			
			if( t.watch() >= 3000 ) {
				System.out.println("Check!"+System.currentTimeMillis());
				t.back(3000);
				n++;
				if( n>5 )
					break;
			}
			
			try {
				Thread.sleep(1000);
			} catch( InterruptedException e ) {}
		}
	}
}
