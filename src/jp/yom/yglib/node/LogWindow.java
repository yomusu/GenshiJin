package jp.yom.yglib.node;

import java.util.Iterator;
import java.util.LinkedList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;


/****************************************************
 * 
 * 
 * ログを表示するウィンドウ
 * 
 * @author Yomusu
 *
 */
public class LogWindow extends YWindow {

	/** 行データ(最後の行がリストの最初になってます) */
	protected final LinkedList<String>	lines = new LinkedList<String>();
	
	/** 文字色 */
	public final Paint	textPaint = new Paint();
	
	/** 行の高さ */
	int lineHeight;
	
	
	public LogWindow() {
		
		// 文字の描画設定
		textPaint.setColor( Color.WHITE );
		
		// 行の高さ
		FontMetrics	fm = textPaint.getFontMetrics();
		lineHeight = (int)(Math.abs(fm.top) + Math.abs(fm.bottom));
	}
	
	
	/**********************************
	 * 
	 * 情報ログを出力
	 * 
	 * @param type
	 * @param obj
	 */
	synchronized public void info( String type, Object obj ) {
		
		lines.addFirst( String.format("[%s] %s", type, obj ) );
		
		// 画面中の行数を求める
		int	lineCount = (int)Math.ceil( (double)(getHeight() - (insets.top+insets.bottom)) / (double)lineHeight );
		
		// 画面中からはみ出した行は削除
		while( lines.size() > lineCount ) {
			lines.removeLast();
		}
	}
	
	/**********************************
	 * 
	 * ウィンドウの中身を描画する
	 * @param canvas
	 */
	@Override
	synchronized public void paintComponent( Canvas canvas ) {
		
		if( lines.size() > 0 ) {
			
			// 文字の描画Y座標を求める
			float	ty = Math.min( (lines.size()*lineHeight), (getHeight() - (insets.top+insets.bottom)) );
			
			ty -= textPaint.getFontMetrics().bottom;
			
			// 最終行から描画していきます
			Iterator<String>	it = lines.iterator();
			while( it.hasNext() ) {
				
				String s = it.next();
				
				canvas.drawText( s, 0, ty, textPaint );
				
				// 座標を上げる
				ty -= lineHeight;
				
				// 画面外にでたら終了
				if( ty<=0 )
					break;
			}
		}
	}
	

}
