package gameEngine;

import java.util.ArrayList;

import org.joml.Vector3f;

import gameEngine.camera.Camera;
import gameEngine.items.ItemGroup;
import gameEngine.light.SceneLight;
import gameEngine.skybox.Skybox;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.*;


public class World implements Runnable{
	Camera c ;
	ArrayList<Camera> camerasList;
	Renderer renderer;
	SceneVertex sc ;
	SceneLight sl;
	Callback callback;
	ArrayList<ItemGroup> groupsList;
	ArrayList<Skybox> skyboxToBeRemoved;

	private Skybox sk = null;
	private boolean loop;
	
	public Camera getCamera(){
		return c;
	}
	
	public void setCamera(Camera c){
		this.c=c;
	}
	
	public ArrayList<Camera> getCamerasList(){
		return camerasList;
	}
	
	public void setSkybox(float width, float length, float height, float r,float g,float b){
		Skybox sky=new Skybox(width,length,height,new Vector3f(r,g,b));
		setSkybox(sky);
	}
	
	public void setSkybox(Skybox sk){
		if(this.sk != null){
			synchronized (skyboxToBeRemoved) {
				skyboxToBeRemoved.add(sk);	
			}
		}
		this.sk = sk;
	}
	
	public void removeSkybox(){
		synchronized (skyboxToBeRemoved) {
			skyboxToBeRemoved.add(sk);	
		}
		this.sk = null;
	}
	
	public Skybox getSkybox(){
		return sk;
	}
	
	public SceneVertex getSceneVertex(){
		return sc;
	}
	public ArrayList<ItemGroup> getGroupsList(){
		return groupsList;
	}
	public SceneLight getSceneLight(){
		return sl;
	}
	public Callback getCallback(){
		return callback;
	}
	
	public void init() throws Exception{
		this.camerasList=new ArrayList<Camera>();
		this.c= new Camera();
		this.sc= new SceneVertex();
		this.sl= new SceneLight();
		this.callback=new Callback();
		this.groupsList=new ArrayList<ItemGroup>();
		this.skyboxToBeRemoved=new ArrayList<Skybox>();
		loop=true;
	}
	
	public void emptyListCamera() {
		camerasList=new ArrayList<Camera>();
	}
	
	public void setCamera(int index, Camera camera) {
		if (index == 0)
			c = camera;
		camerasList.add(index, camera);
	}
	
	public void run(){
		Window.init();
		try {
			this.renderer= new Renderer();
		} catch (Exception e1) {
			e1.printStackTrace();
			return;
		}
		
		try{
			//Initialization of the window we currently use
			glViewport(0, 0, 500,500);
			while ( glfwWindowShouldClose(Window.window) == GLFW_FALSE && loop) {
				Thread.sleep(20);
				c.update();
				callback.update();
				//Clean the window
				boolean isResized = Window.clear();
				
				synchronized (skyboxToBeRemoved) {
					for(Skybox s : skyboxToBeRemoved){
						s.cleanUp();
					}
					System.out.println("ici�");
					skyboxToBeRemoved.clear();
				}
				
				synchronized(sc){
				//Update the world
				renderer.render(sc, sl, isResized,c, sk);
				}
				//Thread.sleep(1000);
				//Update the window's picture
				Window.update();
			}
		}catch(Exception e){System.out.println(e.getMessage());}
		finally{
			renderer.cleanUp();
			sc.cleanUp();
			if(sk!= null)
				sk.cleanUp();
			// Deallocation of the window's resources
			Window.cleanUp();
			this.notifyFinished();
		}
	}
	
	private synchronized void notifyFinished() {
		this.notify();
	}
	
	public synchronized void close() throws InterruptedException {
		loop=false;
		this.wait();
	}
}


