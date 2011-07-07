package jp.yom.yglib;

import java.util.List;

import jp.yom.R;
import jp.yom.yglib.gl.GLFieldView;
import jp.yom.yglib.gl.YRendererList;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;



/************************************************************
 * 
 * 
 * ゲームアクティビティの基本クラス
 * 
 * 
 * @author matsumoto
 *
 */
public class GameActivity extends Activity implements SensorEventListener {
	
	private WindowManager	windowManager = null;
	private WakeLock		wakeLock = null;
	
	/** センサマネージャー */
	protected SensorManager	sensorManager;
	protected Sensor	accelometer;
	
	
	/** GLSurfaceView */
	protected GLFieldView	view = null;
	
	
	//==============================================================================
	// メンバ変数の宣言
	//==============================================================================
	
	/** １フレームの時間(ms) */
	protected long	intervalMillis = 1000 / 30;
	
	/** 終了フラグ */
	protected boolean	isScenarioRunning = false;
	
	/** 最後のフレームの終了時間 */
	protected long	lastProcessTime = 0;
	
	/** 最後のフレームのCPUパワー */
	public float	cpuPowerRatio;
	
	/** 加速度センサの値 */
	protected float	gravityX, gravityY;
	
	
	/** ハンドラ */
	protected Handler	handler = new Handler();
	
	
	
	//==============================================================================
	// 各種ハンドラ
	//==============================================================================
	
	/** ゲーム進行スレッド */
	final Runnable	scenario = new Runnable() {
		@Override
		public void run() {
			
			lastProcessTime = System.currentTimeMillis();
			
			isScenarioRunning = true;
			
			// シナリオ処理
			try {
				scenario();
			} catch( Exception e ) {
				e.printStackTrace();
			}
			
			// 停止処理
			stopScenario();
		}
	};
	
	/** ウオッチポイント更新用ハンドラ */
	final Runnable	watchUpdater = new Runnable() {
		@Override
		public void run() {
			updateViews();
		}
	};
	
	
	
	//==============================================================================
	// Activity関連メソッドの実装
	//==============================================================================
	
	/**************************************************************
	 * 
	 * OSから最初に呼ばれる例のアレ
	 * 
	 */
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
		{
			View	v = findViewById(R.id.GLView);
			if( v instanceof GLFieldView ) {
				view = (GLFieldView)v;
				initGLSurfaceView( view );
			}
		}
		
		//-----------------------------------
		// ログシステムにTextViewをセット
		YLog.setTextView( (TextView)findViewById(R.id.textView1) );
		
		//-----------------------------------
		// シナリオスレッドの起動
		Thread	th = new Thread( scenario );
		th.start();
	}
	
	/***********************************************************
	 * 
	 * 破棄
	 * Activityが終了する直前にのみ呼ばれる
	 * 
	 */
	@Override
	protected void onDestroy() {
		
		stopScenario();
		
		super.onDestroy();
	}


	/***********************************************************
	 * 
	 * Start()のあとに呼ばれる
	 * もしくは、裏から表に回ったときに呼ばれる
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
	
	
	//==============================================================================
	// 公開メソッド
	//==============================================================================
	
	public float getGravityX() {
		return gravityX;
	}
	
	public float getGravityY() {
		return gravityY;
	}
	
	/**************************************************
	 * 
	 * スレッド処理を停止する
	 */
	public void stopScenario() {
		synchronized (scenario) {
			isScenarioRunning = false;
			scenario.notifyAll();
		}
	}
	
	//==============================================================================
	// サブクラスがオーバーライドするべきメソッド
	//==============================================================================
	
	
	/****************************************************
	 * 
	 * GLFieldViewの初期化を行います
	 * メインスレッドのonCreateにて呼ばれます。
	 * しかし、これは暫定メソッド
	 * 
	 */
	protected void initGLSurfaceView( GLFieldView view ) {
	}
	
	/****************************************************
	 * 
	 * シナリオメソッド
	 * 
	 * サブクラスはこのメソッドをオーバーライドして、ゲームシナリオを実装します。
	 * シナリオ用スレッドにて呼ばれます。
	 * このメソッドの処理が終了したらアクティビティは終了します。
	 * 
	 */
	protected void scenario() throws ScenarioInterruptException {
		
		
	}
	
	/*****************************************************
	 * 
	 * Viewの内容を更新する
	 * 
	 * サブクラスはViewに対する操作をこのメソッドに記述します。
	 * Handler.postによりメインスレッドで呼ぶので、Viewに対する操作が許されます。
	 * 
	 */
	protected void updateViews() {
		
	}
	
	//==============================================================================
	// シナリオメソッドからの呼び出し専用メソッド
	//==============================================================================
	
	/**********************************************
	 * 
	 * 次のフレームまで時間を待ちます
	 * 
	 */
	protected void nextFrame() throws ScenarioInterruptException {
		
		synchronized (scenario) {
			if( isScenarioRunning ) {

				// 処理時間と処理間隔から待ち時間を算出
				long	now = System.currentTimeMillis();
				long	processTime = now - lastProcessTime;
				long	waitTime = intervalMillis - processTime;
				if( waitTime>0 ) {
					try {
						scenario.wait(waitTime);
					} catch( InterruptedException e ){
					}
					//	Log.v("App", Long.toString(waitTime) + "  " + Long.toString(processTime) + " now="+Long.toString(System.currentTimeMillis()) );
				}

				// CPUパワー
				cpuPowerRatio = (float)waitTime / (float)intervalMillis;

				// 処理前時間
				lastProcessTime = System.currentTimeMillis();
				
			} else {
				
				// 既に中断済みなら例外
				throw new ScenarioInterruptException();
				
			}
		}
	}
	
	
	/**********************************************
	 * 
	 * ウオッチポイントの内容を更新します
	 * 
	 */
	protected void invokeViewUpdater() throws ScenarioInterruptException {
		
		if( isScenarioRunning )
			handler.post( watchUpdater );
		else {
			// 既に中断済みなら例外
			throw new ScenarioInterruptException();
		}
	}
	
	
	/***********************************************
	 * 
	 * レンダラリストの描画を行う
	 * 
	 * @param renderList
	 */
	protected void invokeDraw( YRendererList renderList ) throws ScenarioInterruptException {
		
		if( isScenarioRunning ) {
			if( view!=null )
				view.invokeDraw( renderList );
		} else {
			// 既に中断済みなら例外
			throw new ScenarioInterruptException();
		}
	}
}
