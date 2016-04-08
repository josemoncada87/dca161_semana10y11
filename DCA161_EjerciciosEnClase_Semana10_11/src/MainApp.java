import java.util.ArrayList;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class MainApp extends PApplet {

	private PImage imgUno, imgDos, imgTres;
	private ArrayList<Grupo> grupos;

	private int estado;

	@Override
	public void setup() {
		size(640, 480);
		estado = 0;
		grupos = new ArrayList<Grupo>();
		imgUno = loadImage("../data/Ejercicio1.png");
		imgDos = loadImage("../data/Ejercicio2.png");
		imgTres = loadImage("../data/Ejercicio3.png");
	}

	@Override
	public void draw() {
		background(255);
		switch (estado) {
		case 0:
			String mensaje =  "use los numeros 0 - 3 para cambiar de pantallas";
			text( mensaje , (width/2)-(textWidth(mensaje))/2 , height/2);
			break;
		case 1:
			kmeans(imgUno, false);			
			break;
		case 2:
			puntosSobreUnaLinea(imgDos, false);			
			break;
		case 3:			
			contornoBasico(imgTres, false);
			contornoSquareTracingAlgorithm(imgTres, false);
			//contornoMooreNeighborTracing(imgTres, false); // incompleto
			break;

		default:
			break;
		}

		fill(0);
		stroke(0);
		text("FPS: " + frameRate, 10, 10);
	}
	
	@Override
	public void keyPressed() {		
		switch (key) {
		case '0':
			estado = 0;
		break;
		case '1':
			estado = 1;
			break;
		case '2':
			estado = 2;
			break;
		case '3':
			estado = 3;
			break;

		default:
			break;
		}
	}

	private ArrayList<PVector> puntosSobreUnaLinea(PImage base, boolean isRGB) {
		image(base, 0, 0);
		ArrayList<ArrayList<PVector>> franjas = new ArrayList<>();
		ArrayList<PVector> puntosMedios = new ArrayList<>();
		base.loadPixels();
		for (int x = 0; x < base.width; x += 5) {
			boolean first = true;
			int state = 0;
			for (int y = 0; y < base.height; y += 1) {
				int i = x + y * base.width;
				if (alpha(base.pixels[i]) != 0) {
					if (first) {
						franjas.add(new ArrayList<PVector>());
						first = false;
						state = 1;
					}
					franjas.get(franjas.size() - 1).add(new PVector(x, y));
					base.pixels[i] = color(255, 0, 255);
				} else {
					if (!first) {
						break;
					}
				}
			}
		}
		for (int i = 0; i < franjas.size(); i++) {
			int sumX = 0;
			int sumY = 0;
			int cant = 0;
			for (int j = 0; j < franjas.get(i).size(); j++) {
				sumX += franjas.get(i).get(j).x;
				sumY += franjas.get(i).get(j).y;
				cant++;
			}
			if (cant > 0) {
				puntosMedios.add(new PVector(sumX / (cant), sumY / (cant)));
			}
		}
		noStroke();
		fill(255, 255, 0);
		for (PVector p : puntosMedios) {
			ellipse(p.x, p.y, 2, 2);
		}

		PVector prev = puntosMedios.get(0);
		stroke(0, 255, 0);
		for (PVector p : puntosMedios) {
			line(prev.x, prev.y, p.x, p.y);
			prev = p;
		}
		noStroke();

		imgDos.updatePixels();
		return puntosMedios;
	}

	private void kmeans(PImage base, boolean isRGB) {
		grupos.clear();
		image(base, 0, 0);
		base.loadPixels();
		boolean fin = false;
		for (int y = 0; y < base.height; y++) {
			for (int x = 0; x < base.width; x++) {
				int i = x + y * base.width;
				if (alpha(base.pixels[i]) != 0) {
					PVector actual = new PVector(x, y);
					grupos.add(new Grupo());
					grupos.get(0).agregar(actual);
					fin = true;
					break;
				}
			}
			if (fin) {
				break;
			}
		}
		for (int y = 0; y < base.height; y++) {
			for (int x = 0; x < base.width; x++) {
				int i = x + y * base.width;
				if (alpha(base.pixels[i]) != 0) {
					PVector actual = new PVector(x, y);
					boolean tieneGrupo = false;
					for (int j = 0; j < grupos.size(); j++) {
						if (grupos.get(j).validar(actual)) {
							grupos.get(j).agregar(actual);
							tieneGrupo = true;
						}
					}
					if (!tieneGrupo) {
						grupos.add(new Grupo());
						grupos.get(grupos.size() - 1).agregar(actual);
					}
				}
			}
		}
		base.updatePixels();
		for (Grupo g : grupos) {
			g.pintar(this);
		}
	}

	
	private ArrayList<PVector> contornoSquareTracingAlgorithm(PImage base, boolean isRGB) {
		ArrayList<PVector> bordes = new ArrayList<>();
		image(base, 0, 0);
		base.loadPixels();
		int indiceInicial = -2;
		int indiceActual = -1;
		int px = -1;
		int py = -1;
		int dir = 0; // 0:arriba 1:derecha 2:abajo 3:izquierda
		// Seleccion del pixel inicial
		for (int i = base.pixels.length - 1; i >= 0; i--) {
			if (alpha(base.pixels[i]) != 0) {
				indiceInicial = i;
				indiceActual = i;
				base.pixels[indiceInicial] = color(255, 0, 0);
				px = indiceActual % base.width;
				py = indiceActual / base.width;
				bordes.add(new PVector(px, py));
				break;
			}
		}
		//////// Mover elemento por primera vez
		switch (dir) {
		case 0:
			px--;
			break;
		case 1:
			py--;
			break;
		case 2:
			px++;
			break;
		case 3:
			py++;
			break;
		}
		// determinar nuevo indice
		indiceActual = (px) + (py * base.width);
		// modificar la direccion
		dir--;
		if (dir < 0) {
			dir = 3;
		}

		while (indiceActual != indiceInicial) {
			// si en esa posicion (actual) hay negro
			boolean shape = false;
			if (isRGB) {
				float r = red(base.pixels[indiceActual]);
				float g = green(base.pixels[indiceActual]);
				float b = blue(base.pixels[indiceActual]);
				shape = ((r + g + b) / 3) < 20;
			} else {
				shape = alpha(base.pixels[indiceActual]) != 0;
			}
			if (indiceActual < base.pixels.length && shape) {
				// ** pintar de rojo y agregar a la lista de puntos
				// ** base.pixels[indiceInicial] = color(255, 0, 0);
				bordes.add(new PVector(px, py));
				// modificar la posicion de acuerdo a la direccion
				switch (dir) {
				case 0:
					px--;
					break;
				case 1:
					py--;
					break;
				case 2:
					px++;
					break;
				case 3:
					py++;
					break;
				}
				// determinar nuevo indice
				indiceActual = (px) + (py * base.width);
				// modificar la direccion
				dir--;
				if (dir < 0) {
					dir = 3;
				}
			} else {
				switch (dir) {
				case 0:
					px++;
					break;
				case 1:
					py++;
					break;
				case 2:
					px--;
					break;
				case 3:
					py--;
					break;
				}
				indiceActual = (px) + (py * base.width);
				dir++;
				if (dir > 3) {
					dir = 0;
				}
			}
		}
		base.updatePixels();
		PVector prev = bordes.get(0);
		stroke(0, 255, 0);
		for (PVector p : bordes) {
			line(prev.x, prev.y, p.x, p.y);
			prev = p;
		}
		noStroke();
		return bordes;
	}

	private ArrayList<PVector> contornoBasico(PImage base, boolean isRGB) {
		image(base, 0, 0);
		base.loadPixels();
		ArrayList<PVector> bordes = new ArrayList<>();
		float prevProm = 0;
		for (int y = 0; y < base.height; y++) {
			for (int x = 0; x < base.width; x++) {
				int i = x + y * base.width;
				float r = red(base.pixels[i]);
				float g = green(base.pixels[i]);
				float b = blue(base.pixels[i]);
				float alfa = alpha(base.pixels[i]);
				float prom = 0;
				if (!isRGB) {
					prom = alfa;
				} else {
					prom = (r + g + b) / 3;
				}
				if (prom < 65 && prevProm > 65 || prom > 65 && prevProm < 65) {
					noStroke();
					fill(255, 0, 0);
					ellipse(x, y, 2, 2);
					bordes.add(new PVector(x, y));
				}
				prevProm = prom;
			}
		}
		prevProm = 0;
		for (int x = 0; x < base.width; x++) {
			for (int y = 0; y < base.height; y++) {
				int i = x + y * base.width;
				float r = red(base.pixels[i]);
				float g = green(base.pixels[i]);
				float b = blue(base.pixels[i]);
				float alfa = alpha(base.pixels[i]);
				float prom = 0;
				if (!isRGB) {
					prom = alfa;
				} else {
					prom = (r + g + b) / 3;
				}
				if (prom < 65 && prevProm > 65 || prom > 65 && prevProm < 65) {
					noStroke();
					fill(255, 0, 0);
					ellipse(x, y, 1, 1);
					bordes.add(new PVector(x, y));
				}
				prevProm = prom;
			}
		}
		return bordes;
	}

	private ArrayList<PVector> contornoMooreNeighborTracing(PImage base, boolean isRGB) {
		ArrayList<PVector> bordes = new ArrayList<>();
		image(base, 0, 0);
		base.loadPixels();

		int indice = -1;
		int indiceInicial = -2;
		int px = -1;
		int py = -1;
		boolean init = false;
		int pos = -1;

		for (int y = base.height - 1; y >= 0; y--) {
			for (int x = 0; x < base.width; x++) {
				int i = x + y * base.width;
				if (alpha(base.pixels[i]) != 0) {
					indiceInicial = i;
					// indice = i;
					init = true;
					base.pixels[i] = color(255, 0, 0);
					px = x;
					py = y;
					bordes.add(new PVector(px, py));
					pos = 0;
					break;
				}
				if (init) {
					break;
				}
			}
		}

		int ss = 0;
		// while(indiceInicial!=indice){
		while (ss < 1000) {
			for (int i = 1; i < 9; i++) {
				if (i == 1) {
					px--;
				}
				if (i == 2) {
					py--;
				}
				if (i == 3) {
					px++;
				}
				if (i == 4) {
					px++;
				}
				if (i == 5) {
					py++;
				}
				if (i == 6) {
					py++;
				}
				if (i == 7) {
					px--;
				}
				if (i == 8) {
					px--;
				}
				System.out.println(px + "," + py);
				indice = (px + (py * base.width));
				base.pixels[i] = color(0, 255, 0);

				if (alpha(base.pixels[indice]) != 0) {
					base.pixels[i] = color(255, 0, 0);
					bordes.add(new PVector(px, py));
					pos = i - 1;
					px = pos % base.width;
					py = pos / base.width;
					break;
				}
			}

			ss++;
		}

		System.out.println(indice);
		base.updatePixels();
		return bordes;
	}
}