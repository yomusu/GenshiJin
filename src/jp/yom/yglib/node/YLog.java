package jp.yom.yglib.node;



/****************************************************
 * 
 * 
 * ログシステム
 * 
 * @author Yomusu
 *
 */
public class YLog extends TextWindow {

	
	/***********************************************
	 * 
	 * infoレベルのログ出力
	 * 
	 * @param obj
	 */
	static public void info( String type, Object obj ) {
		if( logWindow!=null )
			logWindow.info( type, obj );
	}
	
	
	/***********************************************
	 * 
	 * ログシステムが使用するログウィンドウを設定します
	 * 
	 * @param window
	 */
	static public void setInstance( LogWindow window ) {
		logWindow = window;
	}
	
	static protected LogWindow	logWindow = null;
	
}
