package jp.yom.yglib.vector;


public class AtariModel extends AtariObject {
	
	
	public void forward() {
		
		// 座標を進める
		p0.set( pos );
		pos.add( speed );
		
		FMatrix	mat = new FMatrix();
		mat.unit();
		mat.translate( speed.x, speed.y, speed.z );
		
		transform( mat );
		
	}
}
