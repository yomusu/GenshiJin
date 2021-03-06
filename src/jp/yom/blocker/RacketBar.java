package jp.yom.blocker;

import jp.yom.yglib.GameActivity;
import jp.yom.yglib.gl.Material;
import jp.yom.yglib.gl.PolyModel;
import jp.yom.yglib.gl.PolyModel.Polygon;
import jp.yom.yglib.gl.YGraphics;
import jp.yom.yglib.gl.YRenderer;
import jp.yom.yglib.gl.YRendererList;
import jp.yom.yglib.node.YNode;
import jp.yom.yglib.vector.AtariModel;
import jp.yom.yglib.vector.FLine;
import jp.yom.yglib.vector.FMatrix;
import jp.yom.yglib.vector.FPoint;
import jp.yom.yglib.vector.FSurface;


/**************************************************************
 * 
 * 
 * ラケットバー
 * 
 * 
 * @author Yomusu
 *
 */
public class RacketBar  extends YNode implements YRenderer {
	
	/** 当たり判定Model */
	AtariModel	atari = new AtariModel();
	
	/** モデル */
	PolyModel	model;
	
	
	public RacketBar( ) {

		// 幅
		int	w = 60;
		int	d = 20;
		int	h = 20;
		
		float	hw = w / 2f;
		float	hd = d / 2f;
		
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
		
		atari.surfaces = new FSurface[] {
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
		model = new PolyModel();
		
		model.positions = new float[] {
				
				-hw,h,hd,	hw,h,hd,
				-hw,h,-hd,	hw,h,-hd,
				
				-hw,0,hd,	hw,0,hd,
				-hw,0,-hd,	hw,0,-hd,
		};
		
		model.normals = new float[] {
				// 天井
				0,1,0,
				// 前>右>後>左
				0,0,-1,	-1,0,0,	0,0,1,	1,0,0
		};
		
		model.polys = new Polygon[] {
				// 蓋
				PolyModel.createTriStrip( new int[]{ 0,1,2,3 }, new int[]{ 0,0,0,0 }, 0 ),
				// 前>右>後>左
				PolyModel.createTriStrip( new int[]{ 6,2,7,3, 5,1, 4,0, 6,2 }, new int[]{1,1,1,1, 2,2, 3,3, 4,4 }, 0 ),
		};
		
		
		// マテリアルの設定
		Material	mate = new Material();
		mate.setAmbientColor( 0.2f, 0.2f, 0.2f );
		mate.setDiffuseColor( 0.6f, 0.7f, 0.8f );
		mate.setSpecularColor( 0.9f, 0.9f, 0.9f );
		mate.setEmissionColor( 0.2f, 0.2f, 0.2f );
		mate.setShinness( 10f );

		model.materials = new Material[]{ mate };
		
		
		
		// 座標
		FMatrix	mat = new FMatrix();
		mat.unit();
		mat.translate( 0, 0, -150 );
		
		atari.transform( mat );
	}
	
	/******************************************
	 * 
	 * 毎フレームの処理
	 * 
	 */
	@Override
	public void process(YNode parent, GameActivity app, YRendererList renderList) {
		
		super.process(parent, app, renderList);
		
		renderList.add( 10, this );
	}
	
	
	@Override
	public void render(YGraphics g) {
		
	//	g.cullFace( true );
		
		// モデルの描画
		model.render( g );
		
	//	g.cullFace( false );
	}
	
	

}
