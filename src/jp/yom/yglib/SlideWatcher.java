package jp.yom.yglib;

import jp.yom.yglib.gl.GLFieldView.TouchListener;
import jp.yom.yglib.vector.FPoint;
import jp.yom.yglib.vector.FVector;
import android.util.Log;
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
	
	FPoint	lastPoint = new FPoint();
	FPoint	beforePoint = new FPoint();
	
	
	
	
	/******************************************
	 * 
	 * 最後のスライドを取得する
	 * 取得したのちスライド量はリセットする
	 * 
	 * @return
	 */
	public FVector popLastSlide() {
		
		FVector	result = new FVector( beforePoint, lastPoint );
		
		beforePoint.set( lastPoint );
		
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
			lastPoint.set( event.getX(), event.getY(), 0 );
			beforePoint.set( lastPoint );
			return true;
			
		case MotionEvent.ACTION_MOVE:
			beforePoint.set( lastPoint );
			lastPoint.set( event.getX(), event.getY(), 0 );
			return true;
			
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:		// キャンセル
		case MotionEvent.ACTION_OUTSIDE:	// 領域外
			return true;
		}
		
		return false;
	}
	
}
