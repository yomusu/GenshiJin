package jp.yom;

import jp.yom.yglib.GameActivity;
import jp.yom.yglib.YLog;
import jp.yom.yglib.gl.YRendererList;
import jp.yom.yglib.node.YNode;
import jp.yom.yglib.vector.FPoint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;


/*********************************************
 * 
 * 
 * スタート表示
 * 
 * @author Yomusu
 *
 */
public class Start extends YNode {
	
	/** 位置 */
	public final FPoint	pos = new FPoint(100,100);
	
	/** 文字色 */
	public final Paint	textPaint = new Paint();
	
	/** 文字 */
	public String	text = null;
	
	
	
	Runnar	logoAnime = new Runnar() {
		
		@Override
		public void run() {
			
			try {
				textPaint.setAlpha(0);
				textPaint.setAntiAlias( true );
				textPaint.setColor( Color.YELLOW );
				textPaint.setTextSize( 30 );
				textPaint.setTextScaleX( 2.0f );
				
				Start.this.text = "Ready!!";

				//----------------------------------------
				// Readyのアルファが1秒間で、0→255に遷移
				{
					double	term = 1 * 1000;
					double	t = 0;	// 経過時間

					int	start = 0;
					int	end   = 255;


					while( t < term ) {

						// 目標経過時間までの割合(0～1.0)
						double	p = Math.min( (t / term), 1.0 );
						// 値
						double	value = (end * p) + (start * (1.0-p));

						// アルファ更新
						textPaint.setAlpha( (int)value );
						
						// 次のフレームへ
						t += nextFrame();
					}
				}
				
				//----------------------------------------
				// Readyが2秒間
				nop( 2*1000 );
				
				//----------------------------------------
				// Go!!が1秒間
				Start.this.text = "Go!!";
				nop( 1*1000 );
				
				//----------------------------------------
				// 終了
				
			} catch( ScriptFinishException e ) {
				
			}
		}
	};
	
	
	public Start() {
		logoAnime.start();
	}
	
	/*******************************************
	 * 
	 * 描画処理
	 * 
	 */
	@Override
	public void paint(Canvas canvas) {
		
		if( text!=null ) {
			int	width = canvas.getWidth();
			int	height = canvas.getHeight();
			
			Rect	rect = new Rect();
			
			textPaint.getTextBounds( text, 0,text.length()-1, rect );
			
			float	x = (width - rect.width()) / 2.0f;
			float	y = (height - rect.height()) / 2.0f;
			
			YLog.info("draw", rect.toShortString() );
			canvas.drawText( text, x, y, textPaint );
		}
		
		super.paint(canvas);
	}
	
	@Override
	public void process(YNode parent, GameActivity h, YRendererList renderList ) {
		
		
		logoAnime.process();
		
		if( logoAnime.isFinish() ) {
			// 自身をツリーから消す
			parent.removeChild( this );
			
			YLog.info( "start", "finish!!" );
		}
		
		super.process(parent, h, renderList );
	}
	
	
}

abstract class Runnar implements Runnable {
	
	Thread	th;
	
	
	synchronized public void start() {
		th = new Thread( this );
		th.start();
	}
	
	
	/*****************************************
	 * 
	 * シナリオ処理を終了する
	 * 
	 */
	synchronized public void finish() {
		
		// 終了サイン
		th = null;
		
		// waitしているスレッドを起こす
		notifyAll();
	}
	
	/*****************************************
	 * 
	 * 終了したかどうか
	 * 
	 * @return
	 */
	public boolean isFinish() {
		return (th==null);
	}
	
	/*****************************************
	 * 
	 * YNodeのprocess中にこのメソッドを呼ぶこと
	 * 
	 */
	synchronized public void process() {
		if( th!=null ) {
			if( th.isAlive() )
				notifyAll();
			else {
				// 自然終了を検出
				finish();
			}
		}
	}
	
	/*****************************************
	 * 
	 * 時間を経過させる
	 * 
	 * @return	実際の経過時間(ミリ秒)
	 */
	protected long nextFrame() throws ScriptFinishException {
		
		long	start = System.currentTimeMillis();
		
		synchronized (this) {
			
			// すでに終了していたら
			if( th==null )
				throw new ScriptFinishException();
			
			// processかstopを待つ
			try {
				wait();
			} catch( InterruptedException e ){ }
			
			// すでに終了していたら
			if( th==null )
				throw new ScriptFinishException();
		}
		
		// 経過時間を返す
		return System.currentTimeMillis() - start;
	}
	
	/***************************************
	 * 
	 * 指定された時間、待機します
	 * 
	 * @param mills
	 */
	protected void nop( long mills ) throws ScriptFinishException {
		// 経過時間
		long	t = 0;
		while( t < mills )
			t += nextFrame();
	}
	
	/** 外部からのイベントを待つ */
	protected void waitEvent() throws ScriptFinishException {
	}
}

@SuppressWarnings("serial")
class ScriptFinishException extends Exception {
	
}
