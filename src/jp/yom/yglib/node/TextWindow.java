package jp.yom.yglib.node;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;



/******************************************************
 * 
 * 
 * テキストを表示するウィンドウ
 * 
 * 
 * @author Yomusu
 *
 */
public class TextWindow extends YWindow {

	/** 文字列各行 */
	private String[]	texts = new String[0];
	
	/** 文字色 */
	public final Paint	textPaint = new Paint();
	
	
	public TextWindow() {
		
		// 文字の描画設定
		textPaint.setColor( Color.WHITE );
	}
	
	
	/**********************************
	 * 
	 * 文字列をセットする
	 * 
	 * @param str
	 */
	public void setText( String str ) {
		this.texts = str.split("\n");
	}
	
	/**********************************
	 * 
	 * ウィンドウの中身を描画する
	 * @param canvas
	 */
	@Override
	public void paintComponent( Canvas canvas ) {
		
		if( texts.length > 0 ) {

			// 文字の描画
			FontMetrics	fm = textPaint.getFontMetrics();

			float	ty = 0 - fm.top;
			float	lineHeight = Math.abs(fm.top) + Math.abs(fm.bottom);

			for( String s : texts ) {
				canvas.drawText( s, 0, ty, textPaint );
				ty += lineHeight;
			}
		}
	}
	

}
