package jp.yom;

import jp.yom.yglib.AppToolkit;
import jp.yom.yglib.node.YLog;
import jp.yom.yglib.node.YNode;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.LinearLayout;


/*************************************************************
 * 
 * 
 * ボールが転がる地面
 * 
 * @author Yomusu
 *
 */
public class FieldView extends SurfaceView implements SurfaceHolder.Callback {

	protected SurfaceHolder	surfaceHolder;
    
	private int	surfaceWidth;
	private int	surfaceHeight;
	
	private ProcessRunner	processRunner = null;
	private long	intervalMillis = 100;
	
	
	/** ルートオブジェクト */
	public final YNode	root = new YNode();
	
	
	
	public FieldView(Context context) {
		super(context);
		
		// 画面全体に延ばす
		setLayoutParams(
				new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT)
		);
		
		// Surfaceのセットアップ
		surfaceHolder = getHolder();
		surfaceHolder.setSizeFromLayout();
		surfaceHolder.addCallback(this);
		
		setFocusable( true );
		
	// ハードウェアアクセラレーション：うまく動かない
	//	setLayerType(View.LAYER_TYPE_SOFTWARE, null);
	}
	
	
	//====================================================================
	// SurfaceViewの実装
	//====================================================================
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		
		surfaceWidth = width;
		surfaceHeight = height;
		Log.v("App","surfaceChanged : "+width+"  "+height);
		
		YLog.info( "d", isHardwareAccelerated() );
	}
	
	/*************************************************
	 * 
	 * 表示領域が生成されたときに呼び出される
	 * resumeから復帰した時も呼ばれる
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		
		// プロセススレッド開始
		processRunner = new ProcessRunner();
		processRunner.start();
		
		Log.v("App","surfaceCreated");
	}

	/*************************************************
	 * 
	 * 表示領域が破棄されたときに呼び出される
	 * suspendの時も呼ばれる
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		
		// スレッドの停止
		processRunner.stop();
		processRunner = null;
		
		Log.v("App","surfaceDestroyed");
	}
	
	
	
	/** タッチイベントをロックしているノード */
	YNode	touchLockNode = null;
	
	/*************************************************
	 * 
	 * タッチイベント
	 * 
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		StringBuilder	buf = new StringBuilder();
		
		buf.append("onTouch:");
		buf.append(" x=").append( event.getX() );
		buf.append(" y=").append( event.getY() );
		buf.append(" action=").append( event.getAction() );
//		YLog.info( "View",buf.toString() );
		
		switch( event.getAction() ) {
		case MotionEvent.ACTION_DOWN:
			// タッチされているチャイルドを検索…チャイルドのバウンディングボックスを取得しこちらで判定
			// Z順に探す
			// 検索したチャイルドは、UPやキャンセルが呼ばれるまでLockされる
			touchLockNode = root.getTouchableNodeAtPoint( event.getX(), event.getY() );
			if( touchLockNode!=null ) {
				touchLockNode.onTouch( event );
				return true;
			}
			
			break;
			
		case MotionEvent.ACTION_MOVE:
			// Lockしたチャイルドにイベント
			if( touchLockNode!=null ) {
				touchLockNode.onTouch( event );
				return true;
			}
			break;
			
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:		// キャンセル
		case MotionEvent.ACTION_OUTSIDE:	// 領域外
			// Lockしたチャイルドにイベント
			if( touchLockNode!=null ) {
				touchLockNode.onTouch( event );
				touchLockNode = null;
				return true;
			}
			break;
		}
		
		return super.onTouchEvent(event);
	}


	public class OreFieldHandler implements AppToolkit {
		
		public float getGravityX() { return 0f; }
		public float getGravityY() { return 0f; }
		
	}
	
	/************************************************
	 * 
	 * プロセスを行うスレッド
	 * 
	 * @author Yomusu
	 *
	 */
	class ProcessRunner implements Runnable {
		
		private boolean	isFinished = true;
		
		@Override
		public void run() {
			
			AppToolkit	fh = new OreFieldHandler();
			
			while( isFinished==false ) {
				
				// 処理前時間
				long	lastTime = System.currentTimeMillis();
				
				//-----------------------
				// 描画処理
				Canvas	canvas = surfaceHolder.lockCanvas();
				
				// 背景クリア
				canvas.drawColor( Color.BLACK );
				
				// オブジェクト
				root.paint( canvas );
				
				surfaceHolder.unlockCanvasAndPost(canvas);
				
				
				//-----------------------
				// プロセス処理
			//	root.process( null, fh );
				
				//------------------------
				// 待つ
				synchronized (this) {
					if( isFinished==false ) {
						// 処理時間と処理間隔から待ち時間を算出
						long	now = System.currentTimeMillis();
						long	processTime = now - lastTime;
						long	waitTime = intervalMillis - processTime;
						if( waitTime>0 ) {
							try {
								wait(waitTime);
							} catch( InterruptedException e ){
							}
							Log.v("App", Long.toString(waitTime) + "  " + Long.toString(processTime) + " now="+Long.toString(System.currentTimeMillis()) );
						}
					}
				}
				
			}
		}
		
		/** スレッド処理を開始する */
		public void start() {
			if( isFinished ) {
				isFinished = false;
				Thread	th = new Thread(this);
				th.start();
			}
		}
		
		/** スレッド処理を停止する */
		public void stop() {
			synchronized (this) {
				isFinished = true;
				notifyAll();
			}
		}
	}

}
