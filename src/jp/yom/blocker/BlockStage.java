package jp.yom.blocker;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import jp.yom.yglib.AtariModel;
import jp.yom.yglib.GameActivity;
import jp.yom.yglib.SlideWatcher;
import jp.yom.yglib.gl.Camera;
import jp.yom.yglib.gl.Light;
import jp.yom.yglib.gl.Material;
import jp.yom.yglib.gl.Model;
import jp.yom.yglib.gl.YGraphics;
import jp.yom.yglib.gl.YRenderer;
import jp.yom.yglib.gl.YRendererList;
import jp.yom.yglib.node.YNode;
import jp.yom.yglib.vector.AtariChecker;
import jp.yom.yglib.vector.FLine;
import jp.yom.yglib.vector.FMatrix;
import jp.yom.yglib.vector.FPoint;
import jp.yom.yglib.vector.FSurface;
import jp.yom.yglib.vector.FVector;
import jp.yom.yglib.vector.AtariChecker.AtariResult;
import android.util.Log;


/******************************************************
 * 
 * 
 * ステージ
 * ・カメラ
 * ・背景
 * ・ブロックのデータ保持＆配置
 * 
 * 
 * やること
 * ・透視変換＆カメラ位置
 * ・Blockクラス
 * ・Blockのレンダリング
 * ・ライティング…環境光
 * ・床
 * ・ボールの配置
 * ・ボールを動かす＆外壁に当たり判定
 * ・ブロックとボールの当たり判定
 * ・バー配置
 * ・バー移動
 * ・バーとボールの当たり判定
 * ・ブロックの破壊
 * ・ゲームクリア判定
 * 
 * 
 * @author matsumoto
 *
 */
public class BlockStage extends YNode implements YRenderer {
	
	
	/** ブロックのリスト */
	ArrayList<Block>	blockList = new ArrayList<Block>();
	/** カメラのレンダラ */
	protected final Camera	cameraRender = new Camera();
	/** 背景のレンダラ */
	protected final BackGroundRender	bgRender = new BackGroundRender();
	
	
	/** 平行光源 */
	Light.DirectionalLight	dirLight = new Light.DirectionalLight();
	
	/** フロアデータ */
	Floor		floor = new Floor();
	
	/** ボール */
	public BlockerBall	ball = null;
	
	/** バー */
	public RacketBar		bar = null;
	
	public SlideWatcher		slideWatcher = null;
	
	
	
	public BlockStage( SlideWatcher slide ) {
		
		this.slideWatcher = slide;
	}
	
	/**********************************************************
	 * 
	 * 配置データ初期化
	 * ブロック定義データをこのメソッドを介してセットします
	 * 
	 */
	public void initialize() {
		
		//------------------------------
		// ブロックデータの読み込み
		FMatrix	mat = new FMatrix();
		
		
		// 試しに一個配置
		
		float	x = 0;
		float	y = 0;
		
		
		mat.unit();
		mat.translate( x,y,0f );
		
		
		Block	block = new Block();
		block.atari.transform( mat );
		
		blockList.add( block );
		
		
		//------------------------------
		// 光源の設定
		dirLight.setGLLightNumber( GL10.GL_LIGHT0 );
		dirLight.setAmbientColor( 0.5f, 0.0f, 0.0f );
		dirLight.setDiffuseColor( 0.8f, 0.8f, 0.0f );
		dirLight.setSpecularColor( 0.1f, 0.1f, 0.1f );
		dirLight.setDirection( -1, -1, 0 );
		
		
		//------------------------------
		// カメラの初期設定
		cameraRender.campos.set( 0, 400, -200 );
		cameraRender.objpos.set( 0, 0, 0 );
		cameraRender.camdir.set( 0, 1, 0 );
		cameraRender.fovy = 60f;
		
		
		//------------------------------
		// ボール
		ball = new BlockerBall();
		
		//------------------------------
		// バー
		bar = new RacketBar();
	}
	
	AtariModel	beforeHitModel = null;
	
	/**********************************************************
	 * 
	 * 
	 * 
	 */
	@Override
	public void process(YNode parent, GameActivity app, YRendererList renderList) {
		
		// チャイルドの処理
		super.process(parent, app, renderList);
		
		
		// 試しに回転
		float	radZ = (float)((3.14/180.0) * 1);
		
		FMatrix	mat = new FMatrix();
		mat.unit();
		mat.rotateY( radZ );
		
		blockList.get(0).atari.transform( mat );
		
		renderList.add( 100, this );
		
		
		//------------------------
		// バーの移動

		// スライドタッチイベント取得→移動量に変換
		FVector	slide = slideWatcher.popLastSlide();
		slide.set( slide.x*-1, 0, 0 );

		// バー移動
		bar.move( slide );


		//------------------------
		// ボール進行
		ball.forward();

		//------------------------
		// 当たり判定
		AtariChecker	atari = new AtariChecker();
		atari.setCancelModel( beforeHitModel );

		atari.addAll( floor.atari );
		atari.add( blockList.get(0).atari );
		atari.add( bar.atari );

		AtariResult	it = atari.doAtari( ball.p0, ball.p1, 10 );
		while( it!=null ) {

			// ログ
			{
				Log.v("atari", "-- atari ----" );
				StringBuilder	buf = new StringBuilder("before:");
				buf.append(" ball=").append(ball.p0).append("-").append(ball.p1);
				buf.append(" speed=").append(ball.speed);
				Log.v("atari", buf.toString() );
			}

			// 速度ベクトルを反射
			ball.speed.set( it.calcAction( ball.speed ) );

			// 続いて残りの移動距離を反射させる
			float	nokoriLength = new FVector( it.cp, ball.p1 ).getScalar();
			FVector	vnokori = new FVector( ball.speed ).normalize().scale( nokoriLength );

			// ボールの位置を交点に
			ball.p1.set(it.cp).add( vnokori );
			ball.p0.set(it.cp);

			// ログ
			Log.v("atari", "Model="+it.model );
			Log.v("atari", "Surface="+it.surface );
			Log.v("atari", "Line="+it.atariLine );
			Log.v("atari", "atariResult="+it.atariLineResult );
			Log.v("atari", "cp="+it.cp );
			{
				StringBuilder	buf = new StringBuilder("after:");
				buf.append(" ball=").append(ball.p0).append("-").append(ball.p1);
				buf.append(" speed=").append(ball.speed);
				buf.append(" ref=").append(it.normal);
				Log.v("atari", buf.toString() );
			}

			beforeHitModel = it.model;
			atari.setCancelModel( beforeHitModel );

			// イテレーションしなおし
			if( nokoriLength > 1f ) {
				it = atari.doAtari( ball.p0, ball.p1, 10 );
			} else
				it = null;
		}
		
		
		//-------------------------------
		// レンダリングリストに登録
		ball.process( parent, app, renderList );
		bar.process( parent, app, renderList );
	}
	
	
	/**********************************************************
	 * 
	 * レンダリング
	 * 
	 */
	@Override
	public void render(YGraphics g) {
		
		//---------------------
		// 初期化
		g.initializeFor3D( cameraRender );
		
		
		// 背景のレンダラをセット
		//renderList.add( 10000, bgRender );
		
		//---------------------
		// 光源をセットする
		dirLight.render( g );
		
		//---------------------
		// ブロック
		
		g.cullFace( true );
		
		floor.model.render( g );
		
		
		// モデル
		for( Block block : blockList )
			block.model.render(g);
		
		g.cullFace( false );
	}
}



/****************************************************
 * 
 * 背景のレンダラ
 * 
 * @author Yomusu
 *
 */
class BackGroundRender implements YRenderer {
	
	float[]	v1 = new float[]{
			-320f, 300f,	320f, 300f,
			-320f, 0f,		320f, 0f,
	};
	
	float[]	v2 = new float[]{
			-320f, 0f,		320f, 0f,
			-320f, -100f,	320f, -100f,
	};
	
	float[]	c1 = new float[]{
			0f,0f,0f, 1f,
			0f,0f,0f, 1f,
			0f,0f,0f, 1f,
			0f,0f,0f, 1f,
	};
	
	float[]	c2 = new float[]{
			0.6f,0.4f,0.4f, 1f,
			0.6f,0.4f,0.4f, 1f,
			0.6f,0.4f,0.4f, 1f,
			0.6f,0.4f,0.4f, 1f,
	};
	
	@Override
	public void render(YGraphics g) {
		
		g.drawRect( v1, c1 );
		g.drawRect( v2, c2 );
	}
}


/*****************************************
 * 
 * 
 * 床と壁
 * 
 * @author Yomusu
 *
 */
class Floor {
	
	/** 当たり面(World座標) */
	AtariModel[]	atari = new AtariModel[] {
			new AtariModel(),
			new AtariModel(),
			new AtariModel(),
			new AtariModel(),
	};
	
	/** モデル */
	Model	model;
	
	
	public Floor( ) {
		
		// 幅
		int	w = 500;	// X
		// 奥行き
		int	d = 500;	// Z
		
		// 壁の高さ(Y)
		int	h = 30;	// Y
		
		int	hw = w / 2;	// X
		int	hd = d / 2;	// Y
		
		// 底面の４点
		FPoint	ptb[] = new FPoint[] {
				new FPoint(-hw,0,hd), new FPoint(hw,0,hd),
				new FPoint(-hw,0,-hd), new FPoint(hw,0,-hd)
		};
		// 底面を壁の高さまであげた4点
		FPoint	ptc[] = new FPoint[] {
				new FPoint(-hw,h,hd), new FPoint(hw,h,hd),
				new FPoint(-hw,h,-hd), new FPoint(hw,h,-hd)
		};

		FSurface[]	surfaces = new FSurface[] {
				// 床
				//	new FSurface(ptb[0],ptb[1],ptb[2],ptb[3]),
				// 壁：手前
				new FSurface(ptc[3],ptc[2],ptb[3],ptb[2]),
				// 壁：奥
				new FSurface(ptc[0],ptc[1],ptb[0],ptb[1]),
				// 壁：向かって右側面
				new FSurface(ptc[1],ptc[3],ptb[1],ptb[3]),
				// 壁：向かって左側面
				new FSurface(ptc[2],ptc[0],ptb[2],ptb[0]),
		};

		atari[0].surfaces = new FSurface[] { surfaces[0] };
		// 壁：奥
		atari[1].surfaces = new FSurface[] { surfaces[1] };
		// 壁：向かって右側面
		atari[2].surfaces = new FSurface[] { surfaces[2] };
		// 壁：向かって左側面
		atari[3].surfaces = new FSurface[] { surfaces[3] };
		
		
		//------------------------------
		// モデルの作成
		model = new Model(surfaces);
		
		// マテリアルの設定
		Material	mate = new Material();
		mate.setAmbientColor( 0.5f, 0.5f, 0.5f );
		mate.setDiffuseColor( 0.5f, 0.5f, 0.5f );
		mate.setSpecularColor( 0f, 0f, 0f );
		mate.setEmissionColor( 0f, 0f, 0f );
		mate.setShinness( 0f );
		
		model.material = mate;
	}
}

/***************************************
 * 
 * ブロックひとつ
 * 
 * @author matsumoto
 *
 */
class Block {
	
	AtariModel	atari = new AtariModel();
	
	/** モデル */
	Model	model;
	
	
	public Block( ) {
		
		// 幅
		int	w = 20;	// X
		// 高さ(Y)
		int	h = 30;	// Y
		// 奥行き
		int	d = 20;	// Z
		
		int	hw = w / 2;	// X
		int	hd = d / 2;	// Y
		
		// 底面の４点
		FPoint	ptb[] = new FPoint[] {
				new FPoint(-hw,0,hd), new FPoint(hw,0,hd),
				new FPoint(-hw,0,-hd), new FPoint(hw,0,-hd)
		};
		
		// 天井の４点
		FPoint	ptc[] = new FPoint[] {
				new FPoint(-hw,h,hd), new FPoint(hw,h,hd),
				new FPoint(-hw,h,-hd), new FPoint(hw,h,-hd)
		};
		
		atari.surfaces = new FSurface[] {
				// 底面
				new FSurface(ptb[1],ptb[0],ptb[3],ptb[2]),
				// 天井
				new FSurface(ptc[0],ptc[1],ptc[2],ptc[3]),
				// 前面
				new FSurface(ptc[2],ptc[3],ptb[2],ptb[3]),
				// 背面
				new FSurface(ptc[1],ptc[0],ptb[1],ptb[0]),
				// 向かって右側面
				new FSurface(ptc[3],ptc[1],ptb[3],ptb[1]),
				// 向かって左側面
				new FSurface(ptc[0],ptc[2],ptb[0],ptb[2]),
		};
		
		atari.lines = new FLine[] {
				new FLine( ptc[0], ptb[0] ),
				new FLine( ptc[1], ptb[1] ),
				new FLine( ptc[2], ptb[2] ),
				new FLine( ptc[3], ptb[3] ),
		};
		
		//------------------------------
		// モデルの作成
		model = new Model(atari.surfaces);
		
		// マテリアルの設定
		Material	mate = new Material();
		
		mate.setAmbientColor( 0.2f, 1.0f, -1.0f );
		mate.setDiffuseColor( 0.3f, 1.0f, -1.0f );
		mate.setSpecularColor( 0f, 0f, 0f );
		mate.setEmissionColor( 0f, 0f, 0f );
		mate.setShinness( 0.1f );
		
		model.material = mate;
	}
	
}
