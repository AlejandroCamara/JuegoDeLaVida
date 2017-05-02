package mx.itesm.juegodelavida;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Principal extends Game {

	protected FPSLogger logger = new FPSLogger();

	@Override
	public void create () {

		setScreen(new JuegoVidaOptimizado(this));
	}

}
