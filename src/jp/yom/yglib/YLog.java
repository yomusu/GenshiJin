package jp.yom.yglib;

import android.os.Handler;
import android.widget.TextView;



/****************************************************
 * 
 * 
 * ログシステム
 * 
 * @author Yomusu
 *
 */
public class YLog {
	
	
	static private TextViewLog	textViewLog = null;
	
	/***********************************************
	 * 
	 * infoレベルのログ出力
	 * 
	 * @param obj
	 */
	static public void info( String type, Object obj ) {
		
		if( textViewLog!=null )
			textViewLog.info( String.format("[%s] %s\n", type, obj ) );
	}
	
	
	/************************************************
	 * 
	 * ログ出力用のTextViewをセットする
	 * 
	 * @param view
	 */
	static public void setTextView( TextView view ) {
		textViewLog = new TextViewLog( view );
	}
	
	
	/*************************************************
	 * 
	 * 
	 * ログ出力用のTextViewを扱うクラス
	 * 
	 * @author Yomusu
	 *
	 */
	static class TextViewLog {
		
		final Handler	handler;
		TextView	textView;
		
		public TextViewLog( TextView view ) {
			this.textView = view;
			handler = new Handler();
		}
		
		public void info( final String s ) {
			handler.post( new Runnable() {
				@Override
				public void run() {
					textView.append( s );
				}
			});
		}
	}
}
