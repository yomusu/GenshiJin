package jp.yom.blocker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import javax.microedition.khronos.opengles.GL10;

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
import jp.yom.yglib.vector.AtariModel;
import jp.yom.yglib.vector.AtariObject;
import jp.yom.yglib.vector.FLine;
import jp.yom.yglib.vector.FMatrix;
import jp.yom.yglib.vector.FPoint;
import jp.yom.yglib.vector.FSurface;
import jp.yom.yglib.vector.FVector;
import jp.yom.yglib.vector.AtariChecker.AtariCallback;
import jp.yom.yglib.vector.AtariChecker.AtariResult;


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
public class BlockStage extends YNode implements YRenderer,AtariCallback {
	
	
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
	
	/** タッチのスライドを監視 */
	public SlideWatcher		slideWatcher = null;
	
	/** 当たり判定 */
	AtariChecker	atari = new AtariChecker();
	
	
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
		buildBlock();
		
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
		
		//------------------------------
		// 当たり判定
		atari.setCallback( this );
	}
	
	private void buildBlock() {
		
		String[]	datas = new String[]{
				"0000 0000",
				"00 000 00",
				"000 0 000",
		};
		
		FMatrix	mat = new FMatrix();
		
		int	blockWidth = 35;
		int	blockDepth = 25;
		
		float	z = 50;
		
		for( String row : datas ) {
			
			// 左座標
			float	x = (row.length()/2) * blockWidth;
			
			for( char c : row.toCharArray() ) {
				
				if( c == '0' ) {
					
					mat.unit();
					mat.translate( x, 0, z );
					
					Block	block = new Block();
					block.atari.transform( mat );
					
					blockList.add( block );
				}
				
				x -= blockWidth;
			}
			
			z += blockDepth;
		}
		
	}
	
	
	/**********************************************************
	 * 
	 * 
	 * 
	 */
	@Override
	public void process(YNode parent, GameActivity app, YRendererList renderList) {
		
		// チャイルドの処理
		super.process(parent, app, renderList);
		
		
		//------------------------
		// バーの移動

		// スライドタッチイベント取得→移動量に変換
		FVector	slide = slideWatcher.popLastSlide();
		slide.set( slide.x*-1, 0, 0 );
		
		//--------------------------------------
		// 当たり判定に載せるリストを作成
		ArrayList<AtariObject>	list = new ArrayList<AtariObject>();
		list.addAll( Arrays.asList(floor.atari) );
		
		for( Block b : blockList )
			if( b.hp > 0 )
				list.add( b.atari );
		
		list.add( bar.atari );
		list.add( ball );
		
		
		//------------------------
		// ボール進行 & 当たり判定
		atari.forwardAndCheck( ball, list.iterator() );

		//-------------------------------
		// バー移動
		bar.atari.speed.set( slide );
		atari.forwardAndCheck( bar.atari, list.iterator() );
		
		//-------------------------------
		// レンダリングリストに自信を登録
		renderList.add( 100, this );
	}
	
	/***************************************
	 * 
	 * 当たった時に呼ばれる
	 * 
	 */
	public void atariCallback( AtariResult result, AtariObject src ) {
		
		Iterator<Block>	it = blockList.iterator();
		
		while( it.hasNext() ) {
			Block	block = it.next();
			if( block.atari == result.atariobj ) {
				
				// ブロック消滅
				
				
				block.hp -= 1;
				
				return;
			}
		}
		
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
		for( Block block : blockList ) {
			if( block.hp > 0 )
				block.model.render(g);
		}
		
		g.cullFace( false );
		
		//---------------------
		// ボール
		ball.render( g );
		
		//---------------------
		// バー
		bar.render( g );
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
	
	/** 耐久度(0で消滅) */
	int		hp = 1;
	
	
	public Block( ) {
		
		// 幅
		int	w = 40;	// X
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
