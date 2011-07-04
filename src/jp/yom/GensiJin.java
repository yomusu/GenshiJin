package jp.yom;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import jp.yom.yglib.GameActivity;
import jp.yom.yglib.ScenarioInterruptException;
import jp.yom.yglib.StopWatch;
import jp.yom.yglib.YLog;
import jp.yom.yglib.gl.GLFieldView;
import jp.yom.yglib.gl.YRendererList;
import jp.yom.yglib.node.YNode;
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
	
	
	/** ボール */
	Ball	ball;
	
	DecimalFormat	cpuPowerFormat = new DecimalFormat("000.0%");
	
	/*************************************************************
	 * 
	 * GLSurfaceViewの初期化
	 */
	@Override
	protected void initGLSurfaceView( GLFieldView view ) {
		
		// テクスチャの登録
		view.entryTexture( R.drawable.ball, "ball" );
		view.entryTexture( R.drawable.iwa24, "iwa" );
		view.entryTexture( R.drawable.penguin01, "penguinL01" );
		view.entryTexture( R.drawable.penguin02, "penguinL02" );
		view.entryTexture( R.drawable.penguin11, "penguinR01" );
		view.entryTexture( R.drawable.penguin12, "penguinR02" );
	}

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
		String	s = cpuPowerFormat.format( (double)cpuPowerRatio );
		((TextView)findViewById(R.id.watchCPUValue)).setText( s );
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
		
		YLog.info("App","GameThread is Finished.");
	}
}