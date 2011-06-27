package jp.yom.yglib.node;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Paint.FontMetrics;
import android.view.MotionEvent;



/*****************************************************
 * 
 * 
 * ウィンドウの基礎クラス
 * 
 * @author Yomusu
 *
 */
public class YWindow extends YNode {

	/** 表示位置 */
	float		x=0, y=0;
	float		width=300, height=200;
	
	/** 中身エリアの周りのスキマ */
	RectF	insets = null;
	
	/** 背景色 */
	public final Paint	backPaint = new Paint();
	/** 枠色 */
	public final Paint	borderPaint = new Paint();
	/** タイトルバー */
	public final Paint	titleBarPaint = new Paint();
	/** タイトル文字 */
	public final Paint	titlePaint = new Paint();
	
	/** タイトル文字列 */
	public String	titleText = "ここにタイトル";
	
	
	public YWindow() {
		
		// 背景の描画設定
		backPaint.setColor( Color.BLUE );
	//	backPaint.setAlpha( 128 );
		backPaint.setStyle( Paint.Style.FILL );
		
		// 枠線の描画設定
		borderPaint.setColor( Color.WHITE );
		borderPaint.setAlpha( 255 );
		borderPaint.setStrokeWidth( 1.0f );
		borderPaint.setStyle( Paint.Style.STROKE );
		
		// タイトルバーの描画設定
		titleBarPaint.setColor( Color.WHITE );
		titleBarPaint.setAlpha( 255 );
		titleBarPaint.setStyle( Paint.Style.FILL );
		
		// タイトルバーの描画設定
		titlePaint.setColor( Color.BLACK );
		
		// スキマをセット
		FontMetrics	fm = titlePaint.getFontMetrics();
		float	th = (fm.bottom - fm.top) + (2*2);

		insets = new RectF( 2f, th, 2f, 2f );
	}
	
	/** タッチによってウィンドウを移動するタッチハンドラ */
	class WindowMoveTouchHandler {
		
		final float	deltaX;
		final float	deltaY;
		
		public WindowMoveTouchHandler( float tx, float ty ) {
			deltaX = tx - YWindow.this.x;
			deltaY = ty - YWindow.this.y;
		}
		
		public void move( float tx, float ty ) {
			setLocation( (int)(tx - deltaX), (int)(ty - deltaY) );
			YLog.info( "wind", String.format( "x=%.2f, y=%.2f   tx=%.2f ty=%.2f", getX(), getY(), tx,ty ) );
		}
		
		public void off( float tx, float ty ) {
			
		}
		
		public void cancel() {
			
		}
	}
	
	/** 稼働中のタッチハンドラ */
	private WindowMoveTouchHandler	touchHandler = null;
	
	
	
	/*********************************************
	 * 
	 * 座標に適合するYNodeがあれば返す
	 * 
	 */
	@Override
	public YNode getTouchableNodeAtPoint(float x, float y) {
		
		// タッチ座標が自身にオンか？
		float	tx = x - this.x;
		float	ty = y - this.y;
		
		if( tx >= 0 && tx < width )
			if( ty >= 0 && ty < height )
				return this;
		
		return super.getTouchableNodeAtPoint(x, y);
	}


	/*********************************************
	 * 
	 * タッチイベント
	 * 
	 */
	@Override
	public void onTouch(MotionEvent e) {
		
		float	ex = e.getX();
		float	ey = e.getY();
		
		// ウィンドウ座標系のタッチ座標
		float	tx = ex - this.x;
		float	ty = ey - this.y;
		
		// 中身座標系のタッチ座標
		float	cx = tx - insets.left;
		float	cy = ty - insets.top;
		
		
		switch( e.getAction() ) {
		case MotionEvent.ACTION_DOWN:
			
			// ウィンドウか中身か
			if( cx >= 0 && cx < (width-(insets.left+insets.right)) )
				if( cy >= 0 && ty < (height-(insets.top+insets.bottom)) ) {
					// 中身のヒット
					touchHandler = null;
					onTouchComponent( e.getAction(), ex, ey );
					return;
				}
			
			// ウィンドウのタイトルバーか
			if( tx >= 0 && tx < width )
				if( ty >= 0 && ty < height ) {
					touchHandler = new WindowMoveTouchHandler( ex, ey );
					return;
				}
			
			break;
			
		case MotionEvent.ACTION_MOVE:	// 移動イベント
			if( touchHandler!=null )
				touchHandler.move( ex, ey );
			else
				onTouchComponent( e.getAction(),ex, ey );
			
			return;
			
		case MotionEvent.ACTION_UP:
			if( touchHandler!=null )
				touchHandler.off( ex, ey );
			
			return;
			
		case MotionEvent.ACTION_CANCEL:		// キャンセル
		case MotionEvent.ACTION_OUTSIDE:	// 領域外
			if( touchHandler!=null )
				touchHandler.cancel();
			return;
		}
		
		super.onTouch(e);
	}
	
	
	/*****************************************************
	 * 
	 * 中身にタッチイベントあり
	 * 
	 * @param e
	 */
	protected void onTouchComponent( int action, float x, float y ) {
		
	}
	
	/******************************************
	 * 
	 * ウィンドウの表示左上位置を指定
	 * @param x
	 * @param y
	 */
	public void setLocation( float x, float y ) {
		this.x = x;
		this.y = y;
	}
	
	public float getX() { return x; }
	public float getY() { return y; }
	
	/******************************************
	 * 
	 * ウィンドウのサイズを設定
	 * 
	 */
	public void setSize( float w, float h ) {
		this.width = w;
		this.height = h;
	}
	
	/******************************************
	 * 
	 * ウィンドウの中身のサイズでサイズを設定
	 * 
	 */
	public void setSizeByComponent( float w, float h ) {
		this.width = insets.left + insets.right + w;
		this.height = insets.top + insets.bottom + w;
	}
	
	/** ウィンドウの幅を求める */
	public float getWidth() { return width; }
	/** ウィンドウの高さを求める */
	public float getHeight() { return height; }
	
	
	public void setBound( float x, float y, float w, float h ) {
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
	}
	
	/**********************************
	 * 
	 * ウィンドウの中身を描画する
	 * @param canvas
	 */
	@Override
	public final void paint( Canvas canvas ) {

		canvas.save();
		
		// 座標をトランスレート
		canvas.translate( x, y );
		
		// ウィンドウの描画
		canvas.clipRect( 0,0, width, height );
		
		// 背景と枠の描画
		RectF	r = new RectF( 0,0, width-1.0f, height-1.0f );
		canvas.drawRect( r, backPaint );
		canvas.drawRect( r, borderPaint );
		
		// タイトルの描画
		FontMetrics	fm = titlePaint.getFontMetrics();
		float	th = (fm.bottom - fm.top) + (2*2);
		canvas.drawRect( 0,0, width-1f, th, titleBarPaint );
		
		if( titleText!=null ) {
			float	sw = titlePaint.measureText( titleText );
			canvas.drawText( titleText, (width-sw)/2, 2-fm.top, titlePaint );
		}
		
		//-----------------------------
		// ウィンドウの中身の描画
		canvas.translate( insets.left, insets.top );
		
		// 中身にクリップ
		float	w = getWidth() - (insets.left+insets.right);
		float	h = getHeight() - (insets.top+insets.bottom);
		
		canvas.clipRect( 0, 0, w, h, Region.Op.INTERSECT );
		
		paintComponent( canvas );
		
		canvas.restore();
	}
	
	
	/******************************************
	 * 
	 * ウィンドウの中身を描画する
	 * サブクラスはこのメソッドをオーバーライドしてウィンドウ内の描画を実装します
	 * 
	 * このメソッド内ではsaveとrestoreをしないでください。
	 * 呼び出し側で行います。
	 * 
	 * 
	 * @param canvas
	 */
	protected void paintComponent( Canvas canvas ) {
		
	}


}
