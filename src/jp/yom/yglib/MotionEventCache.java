package jp.yom.yglib;

import java.util.LinkedList;

import jp.yom.yglib.gl.GLFieldView.TouchListener;

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
public class MotionEventCache implements TouchListener {
	
	
	private final LinkedList<Event>	eventList = new LinkedList<Event>();
	
	/** イベントスルーフラグ */
	private boolean	isThroughEvent = false;
	
	
	/******************************************
	 * 
	 * タッチイベントを受信
	 * 
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		switch( event.getAction() ) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:		// キャンセル
		case MotionEvent.ACTION_OUTSIDE:	// 領域外
			push( new Event(event) );
			break;
		}
		
		return false;
	}
	
	
	/********************************************
	 * 
	 * イベントを送る
	 * 
	 * @param event
	 */
	synchronized public void push( Event event ) {
		if( isThroughEvent==false ) {
			eventList.addLast( event );
			notifyAll();
		}
	}
	
	/********************************************
	 * 
	 * イベントをクリアする
	 * 
	 */
	synchronized public void clear() {
		eventList.clear();
	}
	
	/********************************************
	 * 
	 * イベントを廃棄処理し、以降のイベント処理を行わなくする
	 * 
	 */
	synchronized public void stopAndDispose() {
		isThroughEvent = true;
		eventList.clear();
	}
	
	/******************************************
	 * 
	 * 先頭のイベントを取得する
	 * 
	 * @return	なければnull
	 */
	synchronized public Event popFirst() {
		
		if( eventList.size()>0 ) {
			Event	event = (Event)eventList.getFirst();
			eventList.removeFirst();
			return event;
		}
		
		return null;
	}
	
	/******************************************
	 * 
	 * イベントを取得する
	 * 取得するまで処理をブロックする
	 * 
	 * @param timeoutmilli
	 * @return
	 */
	synchronized public Event pop() {
		
		Event	event = pop(1000);
		
		while( event==null )
			event = pop(1000);
		
		return event;
	}
	
	
	/******************************************
	 * 
	 * タイムアウト付き
	 * 
	 * @param timeoutmilli
	 * @return
	 */
	synchronized public Event pop( long timeoutmilli ) {
		
		if( eventList.size()==0 ) {
			try {
				wait(timeoutmilli);
			} catch (InterruptedException e) {
			}
		}
		
		if( eventList.size()>0 ) {
			Event	event = (Event)eventList.getFirst();
			eventList.removeFirst();
			return event;
		}
		
		return null;
	}
	
	
	static public class Event {
		
		int		action;
		float	x;
		float	y;
		
		public Event( MotionEvent event ) {
			this.action = event.getAction();
			x = event.getX();
			y = event.getY();
		}
		
		public int getAction() { return action; }
		
		public float getX() { return x; }
		public float getY() { return y; }
		
		@Override
		public boolean equals( Object obj ) {
			
			if( obj instanceof Event ) {
				Event	dst = (Event)obj;
				if( this.action==dst.action )
					if( this.x==dst.x && this.y==dst.y )
							return true;
			}
			
			return false;
		}
	}
	

}
