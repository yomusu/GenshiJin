package jp.yom.yglib;

import jp.yom.yglib.gl.GLFieldView.TouchListener;
import jp.yom.yglib.vector.FVector;
import android.view.MotionEvent;


/*************************************************
 * 
 * 
 * Touchイベントをキャッシュしておくクラス
 * 
 * 
 * @author matsumoto
 *
 */
public class SlideWatcher implements TouchListener {
	
	
	float	down_x;
	float	down_y;
	
	float	slide_x;
	float	slide_y;
	
	
	
	/******************************************
	 * 
	 * 最後のスライドを取得する
	 * 取得したのちスライド量はリセットする
	 * 
	 * @return
	 */
	public FVector popLastSlide() {
		
		FVector	result = new FVector( slide_x, slide_y, 0f );
		
		slide_x = 0;
		slide_y = 0;
		
		return result;
	}
	
	/******************************************
	 * 
	 * タッチイベントを受信
	 * 
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		switch( event.getAction() ) {
		case MotionEvent.ACTION_DOWN:
			down_x = event.getX();
			down_y = event.getY();
			return true;
			
		case MotionEvent.ACTION_MOVE:
			slide_x = event.getX() - down_x;
			slide_y = event.getY() - down_y;
			return true;
			
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:		// キャンセル
		case MotionEvent.ACTION_OUTSIDE:	// 領域外
			return true;
		}
		
		return false;
	}
	
}
