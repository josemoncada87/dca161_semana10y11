import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PVector;

public class Grupo {
	private ArrayList<PVector> puntos;
	private int centroX, centroY;
	private final int RADIO = 40;	
	
	private float r,g,b;
	
	public Grupo() {
		puntos =  new ArrayList<PVector>();
		centroX = -1;
		centroY = -1;
		
		r = (float) (Math.random()*255);
		g = (float) (Math.random()*255);
		b = (float) (Math.random()*255);
		
		
	}	
	public void agregar(PVector np){
		puntos.add(np);
		calcularCentro();
	}	
	public boolean validar(PVector tp){		
		if(PApplet.dist(centroX, centroY, tp.x, tp.y)<RADIO){
			return true;
		}		
		return false;
	}
	private void calcularCentro() {
		int sumX = 0;
		int sumY = 0;		
		for (PVector p : puntos) {
			sumX+=p.x;
			sumY+=p.y;			
		}
		centroX = sumX/puntos.size();
		centroY = sumY/puntos.size();
	}
	
	public int getCentroX() {
		return centroX;
	}
	
	public int getCentroY() {
		return centroY;
	}
	
	public void pintar(PApplet app){
		
		
		/*for (PVector p : puntos) {
			app.noStroke();
			app.fill(r,g,b);
			app.ellipse(p.x, p.y, 1, 1);
		}*/
		
		app.fill(255,255,0);
		app.ellipse(centroX, centroY, 5, 5);
		
	}
}

