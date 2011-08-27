package jp.yom;

import java.text.DecimalFormat;
import java.util.Arrays;

import jp.yom.blocker.BlockStage;
import jp.yom.yglib.GameActivity;
import jp.yom.yglib.ScenarioInterruptException;
import jp.yom.yglib.SlideWatcher;
import jp.yom.yglib.StopWatch;
import jp.yom.yglib.TimeOutException;
import jp.yom.yglib.YLog;
import jp.yom.yglib.gl.TextureEntry;
import jp.yom.yglib.gl.YRendererList;
import jp.yom.yglib.node.YNode;
import jp.yom.yglib.vector.FVector;
import android.util.Log;
import android.widget.TextView;


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
public class GensiJin extends GameActivity {
	
	
	DecimalFormat	cpuPowerFormat = new DecimalFormat("000.0%");
	
	/** モーションイベントキャッシュ */
	SlideWatcher	slideWatcher = new SlideWatcher();
	
	/** ボール情報 */
	String	ballInfo = "";
	
	
	/*************************************************************
	 * 
	 * 
	 * 
	 */
	@Override
	protected void updateViews() {
		
		// デバッグ用情報表示
//		StringBuilder	buf = new StringBuilder();
//		buf.append("gravity: x=").append(getGravityX()).append(" y=").append(getGravityY()).append("\n");
//		buf.append("ball-pos  = ").append( ball.pos ).append("\n");
//		buf.append("ball-speed= ").append(ball.speed).append("\n");
		
		// ウオッチポイント情報を更新する
		
		// CPU
		String	s = cpuPowerFormat.format( (double)cpuPowerRatio );
		((TextView)findViewById(R.id.watchCPUValue)).setText( s );
		
		// Ball
		if( ballInfo!=null )
			((TextView)findViewById(R.id.watchBallValue)).setText( ballInfo );
	}
	
	
	/*************************************************************
	 * 
	 * シナリオ処理
	 * 
	 */
	@Override
	protected void scenario() throws ScenarioInterruptException {
		
		YLog.info("App","GameThread is Started.");

		// イベントポンプを監視してサーフェイスが作成されるのを待つ
		try {
			view.surfaceReadySignal.waitForSignalOf( Boolean.TRUE, 3000 );
		} catch( TimeOutException e ) {
			YLog.info("App","TimeOutException.");
			return;
		} catch( InterruptedException e ) {
			YLog.info("App","InterruptException.");
			return;
		}
		
		//-----------------------------------
		// テクスチャの読み込み
		TextureEntry[]	ts = new TextureEntry[] {
				new TextureEntry("ball", R.drawable.ball ),
				new TextureEntry("iwa", R.drawable.iwa24 ),
				new TextureEntry("penguinL01", R.drawable.penguin01 ),
				new TextureEntry("penguinL02", R.drawable.penguin02 ),
				new TextureEntry("penguinR01", R.drawable.penguin11 ),
				new TextureEntry("penguinR02", R.drawable.penguin12 ),
		};
		
		// 読み込み
		for( TextureEntry t : ts )
			t.loadBitmap( getResources() );
		
		// 登録
		view.entryTextures( Arrays.asList(ts) );
		
		// GLスレッドを回す
		invokeDraw( null );
		
		// ここでビットマップの解放はできない。invokeDrawがGLスレッドの終了を待たないため。
		// ここでというより、GLスレッドにて登録後、自動で解放した方がよいか
		
		
		//-----------------------------------
		// ブロック崩し編
		
		// ステージ
		BlockStage	stage = new BlockStage();
		// ブロックデータ初期化
		stage.initialize();
		
		
		// 一定時間の間ループ
		int	nokoriTime = 60*1000;
		
		// 入力開始
		view.setTouchListener( slideWatcher );
		
		try {
			while( nokoriTime >= 0 ) {

				YRendererList	rendererList = new YRendererList();
				
				
				// 1フレームの動き
				
				// スライドタッチイベント取得
				FVector	slide = slideWatcher.popLastSlide();
				stage.bar.move( slide );
				
				// ステージ
				stage.process( null, this, rendererList );
				
				// ボール情報
				StringBuilder	buf = new StringBuilder();
				buf.append("pos=").append( stage.ball.p0.toString() );
				buf.append(" speed=").append( stage.ball.speed );
				ballInfo = buf.toString();
				
				// 描画
				invokeDraw( rendererList );
				
				//------------------------
				// 次フレームまで待つ
				nextFrame();
				
				// update a watch point
				invokeViewUpdater();
				
				// 残り秒数を減らす
				nokoriTime -= intervalMillis;
			}
			
		} catch( ScenarioInterruptException e ) {

		}
		
		view.setTouchListener( null );
		
		
		
		//-----------------------------------
		// タイトル
		
		// テクスチャのセット


		
		//------------------------------------
		// テクスチャビットマップの解放
		for( TextureEntry t : ts )
			t.disposeBitmap();
		
		YLog.info("App","GameThread is Finished.");
	}
	
	
	private void funka() {
		
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
		int	nokoriTime = 10*1000;

		StopWatch	funkaWatch = new StopWatch();

		funkaWatch.reset();
		
		try {
			while( nokoriTime >= 0 ) {

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
				root.process( null, this, rendererList );

				// 描画
				invokeDraw( rendererList );

				//------------------------
				// 次フレームまで待つ
				nextFrame();
				
				// update a watch point
				invokeViewUpdater();
				
				// 残り秒数を減らす
				nokoriTime -= intervalMillis;
			}
			
		} catch( ScenarioInterruptException e ) {

		}
		
	}
}
