package com.jacat.First2DGame;

import com.jacat.First2DGame.graphics.Screen;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class Game extends Canvas implements Runnable{
	private static final long serialVersionUID = 1L;


	public static int width = 300;
	public static int height = 168;
	public static int scale = 3;


	private Thread thread;
	private JFrame frame;
	private boolean running = false;
	private String title = "First 2D Game";

	private Screen screen;

	private BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
	private int[] pixel = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();

	public Game() {
		Dimension size = new Dimension(width * scale, height * scale);
		setPreferredSize(size);

		screen = new Screen(width,height);

		frame = new JFrame();
	}

	public synchronized void start()  {
		running = true;
		thread = new Thread(this, "Display");
		thread.start();
	}

	public synchronized void stop ()  {
		try  {
			thread.join();
		} catch (InterruptedException e)  {
			e.printStackTrace();
		}
	}

	public void run() {
		long lastTime = System.nanoTime();
		long timer = System.currentTimeMillis();
		final double ns = 1000000000.0 / 60.0;
		double delta = 0;
		int frames = 0;
		int updates = 0;
		while (running) {
			long now = System.nanoTime();
			delta += (now-lastTime) / ns;
			lastTime = now;
			while(delta >= 1){
				update();
				updates++;
				delta--;
			}
			render();
			frames++;
			if(System.currentTimeMillis()- timer > 1000){
				timer += 1000;
				System.out.println(updates + " ups, " + frames + " fps");
				frame.setTitle(title + "  |  " + updates + " ups, " + frames + " fps");
				updates = 0;
				frames = 0;
			}
		}
		stop();
	}

	public int x = 0, y = 0;

	public void update() {
		x++;
		y++;
	}

	public void render() {
		BufferStrategy bs = getBufferStrategy();
		if (bs == null){
			createBufferStrategy(3);
			return;
		}

		screen.clear();

		screen.render(x,y);

		for(int i = 0; i < pixel.length; i ++) {
			pixel[i] = screen.pixels[i];
		}

		Graphics g = bs.getDrawGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0,0,getWidth(),getHeight());
		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
		g.dispose();
		bs.show();

	}

	public static void main(String[] args) {
		Game game = new Game();
		game.frame.setResizable(false);
		game.frame.setTitle(game.title);
		game.frame.add(game);
		game.frame.pack();
		game.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		game.frame.setLocationRelativeTo(null);
		game.frame.setVisible(true);

		game.start();
	}

}
