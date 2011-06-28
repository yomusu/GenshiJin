package jp.yom;

import java.io.IOException;
import java.util.List;

import jp.yom.MovingTest.GameAppToolkit;
import jp.yom.MovingTest.GameThread;
import jp.yom.yglib.AppToolkit;
import jp.yom.yglib.StopWatch;
import jp.yom.yglib.gl.GLFieldView;
import jp.yom.yglib.gl.LogView;
import jp.yom.yglib.gl.YRendererList;
import jp.yom.yglib.node.LogWindow;
import jp.yom.yglib.node.TextWindow;
import jp.yom.yglib.node.YLog;
import jp.yom.yglib.node.YNode;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;


/************************************************************
 * 
 * 
 * 
 * アプリケーション
 * 
 * 
 * @author Yomusu
 *
 */
public class GensiJin extends Activity implements SensorEventListener {
	
	private WindowManager	windowManager = null;
	private WakeLock		wakeLock = null;
	
	/** センサマネージャー */
	protected SensorManager	sensorManager;
	protected Sensor	accelometer;
	
	
	/** ボール */
	Ball	ball;
	
	/** デバッグ情報表示ウィンドウ */
	TextWindow	debugWindow;
	
	/** GLSurfaceView */
	GLFieldView	view = null;
	
	/** ゲーム進行スレッド */
	GameThread	thread = null;
	
	/** ゲームツールキット */
	GameAppToolkit	appToolkit = new GameAppToolkit();
	
	
	/** 加速度センサの値 */
	float	gravityX, gravityY;
	
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
		
		// Get an instance of the WindowManager
	//	windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
	//	Display	display = windowManager.getDefaultDisplay();
		
		// Get an instance of the PowerManager
		PowerManager	powerManager = (PowerManager) getSystemService(POWER_SERVICE);
		
		// Create a bright wake lock
		wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, getClass().getName() );
		
		
		//----------------------------------
		// センサマネージャーの取得
		sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		
		List<Sensor>	sensorList = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
		if( sensorList.size()>0 )
			accelometer = sensorList.get(0);
		
		
		//--------------------------------
		// GLフィールドの作成
	//	view = new GLFieldView( this, null );
		view = (GLFieldView)findViewById(R.id.GLView);
		view.entryTexture( R.drawable.ball, "ball" );
		view.entryTexture( R.drawable.iwa24, "iwa" );
		view.entryTexture( R.drawable.penguin01, "penguinL01" );
		view.entryTexture( R.drawable.penguin02, "penguinL02" );
		view.entryTexture( R.drawable.penguin11, "penguinR01" );
		view.entryTexture( R.drawable.penguin12, "penguinR02" );
		
		
		//-----------------------------------
		// DebugViewの初期化(しかしDebugViewはこれではダメっぽい)
		initDebugView( (LogView)findViewById(R.id.DebugView) );
		
		//-----------------------------------
		// Gameスレッドの起動
		thread = new GameThread();
		thread.start();
	}
	
	protected LogView initDebugView( LogView view ) {
		
		//--------------------------------
		// デバッグウィンドウ
		debugWindow = new TextWindow() {
			@Override
			public void process(YNode parent, AppToolkit h, YRendererList renderList ) {
				
				// デバッグ用情報表示
				StringBuilder	buf = new StringBuilder();
				buf.append("gravity: x=").append(h.getGravityX()).append(" y=").append(h.getGravityY()).append("\n");
				buf.append("ball-pos  = ").append( ball.pos ).append("\n");
				buf.append("ball-speed= ").append(ball.speed).append("\n");
				
				setText( buf.toString() );
			}
		};
		debugWindow.setSize( 300, 200 );
		debugWindow.setLocation( 0,0 );
		
		//--------------------------------
		// ログウィンドウ
		LogWindow	logwin = new LogWindow();
		
		logwin.setSize( 300,400 );
		logwin.setLocation( 800, 100 );
		
		YLog.setInstance( logwin );
		
		
	//	LogView	view = new LogView( this, null );
		view.root.addChild( debugWindow );
		view.root.addChild( logwin );
		
		return view;
	}
	
	
	/***********************************************************
	 * 
	 * 破棄
	 * Activityが終了する直前にのみ呼ばれる
	 * 
	 */
	@Override
	protected void onDestroy() {
		
		if( thread!=null )
			thread.stop();
		
		super.onDestroy();
	}


	/***********************************************************
	 * 
	 * Start()のあとに呼ばれる
	 * 
	 */
	@Override
	protected void onResume() {
		super.onResume();
		
		if( view!=null )
			view.onResume();
		if( wakeLock!=null )
			wakeLock.acquire();
		
		// センサリスナの登録
		sensorManager.registerListener( this, accelometer, SensorManager.SENSOR_DELAY_NORMAL );
		
		Log.v("App","onResume");
	}
	
	
	/***********************************************************
	 * 
	 * 他のアプリの裏に回った時に呼ばれる
	 * 
	 */
	@Override
	protected void onPause() {
		super.onPause();
		
		if( view!=null )
			view.onPause();
		if( wakeLock!=null )
			wakeLock.release();
		
		// センサリスナの解放
		sensorManager.unregisterListener( this );
		
		Log.v("App","onPause");
	}
	
	//====================================================================
	// センサーイベント：SensorEventListenerの実装
	//====================================================================

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}
	
	
	/****************************************************
	 * 
	 * センサーの状況が変化した
	 * 
	 */
	@Override
	public void onSensorChanged(SensorEvent event) {
		
		if( event.sensor==accelometer ) {
			gravityX = event.values[0] * -1.0f;
			gravityY = event.values[1];
		}
	}
	
	
	
	/*******************************************************
	 * 
	 * アプリツールキット
	 * 
	 * @author Yomusu
	 *
	 */
	class GameAppToolkit implements AppToolkit {

		@Override
		public float getGravityX() {
			return gravityX;
		}

		@Override
		public float getGravityY() {
			return gravityY;
		}
	}
	
	
	
	class GameThread implements Runnable {
		
		public void run() {
			
			YLog.info("App","GameThread is Started.");
			
			
			// イベントポンプを監視してサーフェイスが作成されるのを待つ
			// view.waitSurface();
			try {
				Thread.sleep(1000);
			} catch( InterruptedException e ) {
				
			}
			
			
			//-----------------------------------
			// タイトル
			
			
			
			//-----------------------------------
			// ステージ１
			YNode	root = new StageRoot();
			
			Kazan	kazan = new Kazan();
//			root.addChild( kazan );
			
			RakkaDan	rakka = new RakkaDan();
			root.addChild( rakka );
			
			Penguin		penguin = new Penguin();
			root.addChild( penguin );
			
			
			// スタート表示
			//root.addChild( new Start() );
			
			
			// 一定時間の間ループ
			int	nokoriTime = 60*1000;
			
			StopWatch	funkaWatch = new StopWatch();
			
			funkaWatch.reset();
			
			while( nokoriTime >= 0 && isFinished==false ) {
				
				YRendererList	rendererList = new YRendererList();
				
				// 噴火しまくる
				if( funkaWatch.watch() >= 3000 ) {
					kazan.funka();
					kazan.funka();
					kazan.funka();
					rakka.rakka();
					funkaWatch.reset();
				}
				
				// 1フレームの動き
				// 当たり判定
				root.process( null, appToolkit, rendererList );
				
				// 描画
				view.invokeDraw( rendererList );
				
				//------------------------
				// 次フレームまで待つ
				nextFrame();
				
				// 残り秒数を減らす
				nokoriTime -= intervalMillis;
			}
			
			
			YLog.info("App","GameThread is Finished.");
		}
		
		protected long	intervalMillis = 1000 / 30;
		protected boolean	isFinished = true;
		protected long	lastProcessTime = 0;
		
		synchronized public void nextFrame() {

			if( isFinished==false ) {

				// 処理時間と処理間隔から待ち時間を算出
				long	now = System.currentTimeMillis();
				long	processTime = now - lastProcessTime;
				long	waitTime = intervalMillis - processTime;
				if( waitTime>0 ) {
					try {
						wait(waitTime);
					} catch( InterruptedException e ){
					}
					//	Log.v("App", Long.toString(waitTime) + "  " + Long.toString(processTime) + " now="+Long.toString(System.currentTimeMillis()) );
				}

				// 処理前時間
				lastProcessTime = System.currentTimeMillis();
			}
		}

		/** スレッド処理を開始する */
		synchronized public void start() {
			if( isFinished ) {
				isFinished = false;
				Thread	th = new Thread(this);
				th.start();
				
				// 処理前時間
				lastProcessTime = System.currentTimeMillis();
			}
		}
		
		/** スレッド処理を停止する */
		synchronized public void stop() {
			isFinished = true;
			notifyAll();
		}
	}
}