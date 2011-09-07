package jp.yom.blocker;

import jp.yom.yglib.gl.Material;
import jp.yom.yglib.gl.PolyModel;
import jp.yom.yglib.gl.YGraphics;
import jp.yom.yglib.gl.YRenderer;
import jp.yom.yglib.gl.PolyModel.PolyTriangleStrip;
import jp.yom.yglib.vector.AtariBall;
import jp.yom.yglib.vector.FMatrix;



/*********************************************************
 * 
 * 
 * ボール
 * 
 * @author Yomusu
 *
 */
public class BlockerBall extends AtariBall implements YRenderer {

	/** モデル */
	PolyModel	model;
	
	
	public BlockerBall( ) {
		
		// 幅
		r = 10;

		//------------------------------
		// モデルの作成
		model = new PolyModel();
		
		model.vertices = new float[][] {
				
				new float[]{ 0,r,0, 0,1,0 },
				new float[]{ 0,-r,0, 0,1,0 },
				
				new float[]{ 0,0,r, 0,0,1 },
				new float[]{ -r,0,0, -1,0,0 },
				new float[]{ 0,0,-r, 0,0,-1 },
				new float[]{ r,0,0, 1,0,0 },
		};
		
		model.polys = new PolyTriangleStrip[] {
			
				new PolyTriangleStrip( new int[]{ 0,2,3 } ),
				new PolyTriangleStrip( new int[]{ 0,3,4 } ),
				new PolyTriangleStrip( new int[]{ 0,4,5 } ),
				new PolyTriangleStrip( new int[]{ 0,4,2 } ),
				
				new PolyTriangleStrip( new int[]{ 1,3,2 } ),
				new PolyTriangleStrip( new int[]{ 1,4,3 } ),
				new PolyTriangleStrip( new int[]{ 1,5,4 } ),
				new PolyTriangleStrip( new int[]{ 1,2,5 } ),
		};
		
		// マテリアルの設定
		Material	mate = new Material();
		mate.setAmbientColor( 0.5f, 0.5f, 0.5f );
		mate.setDiffuseColor( 0.6f, 0.7f, 0.8f );
		mate.setSpecularColor( 0.9f, 0.9f, 0.9f );
		mate.setEmissionColor( 0.2f, 0.2f, 0.2f );
		mate.setShinness( 10f );

		model.material = mate;
		
		
		
		// 座標
		pos.set( -0, 5, -50 );
		p0.set( pos );
		
		// 速度
		speed.set( 0, 0, -6 );
	}
	
	
	
	@Override
	public void render(YGraphics g) {
		
		g.cullFace( true );
		
		
		FMatrix	mat = new FMatrix();
		mat.unit();
		mat.translate( pos.x, pos.y, pos.z );
		
		g.gl.glPushMatrix();
		
		// マトリックスのセット
		g.mulMatrix( mat );
		
		// モデルの描画
		model.render( g );
		
		g.gl.glPopMatrix();
		
		g.cullFace( false );
	}
	
	
}
