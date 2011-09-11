package jp.yom.blocker;

import java.util.Arrays;

import jp.yom.yglib.gl.Material;
import jp.yom.yglib.gl.PolyModel;
import jp.yom.yglib.gl.PolyModel.Polygon;
import jp.yom.yglib.gl.YGraphics;
import jp.yom.yglib.gl.YRenderer;
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
		
		// 半径1の円に（法線）
		float	H = 0.7071f;
		model.normals = new float[] {
				
				0,1,0,
				
				0,0,1,
				-H,0,H,
				-1,0,0,
				-H,0,-H,
				 0,0,-1,
				 H,0,-H,
				 1,0,0,
				 H,0,H,
				
				 0,-1,0,
		};
		
		// 直径rをかけて円を作成
		model.positions = Arrays.copyOf( model.normals, model.normals.length );
		for( int i=0; i<model.positions.length; i++ )
			model.positions[i] *= r;
		
		int[]	upper = new int[]{ 0, 1,2,3,4,5,6,7,8,1 };
		int[]	bottom= new int[]{ 9, 8,7,6,5,4,3,2,1,8 };
		
		model.polys = new Polygon[] {
				PolyModel.createTriFan( upper, upper, 0 ),
				PolyModel.createTriFan( bottom, bottom, 0 ),
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
