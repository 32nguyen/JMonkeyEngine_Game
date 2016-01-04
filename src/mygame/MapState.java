package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;


/******************************************************************************* 
 * Name: ThanhNguyen   
 * ID  : 2451232
 * 31/3/2015
 * MapState.java loads my own terrain & and make shadow, fire effect
 ******************************************************************************/
public class MapState extends AbstractAppState {
    private BulletAppState bulletAppState;
    private Node rootNode;
    private SimpleApplication app;
    private AssetManager assetManager;
    private Spatial mapModel;    
    public MapState(BulletAppState bulletAppStater) {
        this.bulletAppState = bulletAppStater;// passing bulletAppState from My3DGame.Class
    }
    @Override
    public void initialize(AppStateManager stateManager, Application app) {// initialize the state
        
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;
        this.assetManager = this.app.getAssetManager();
        this.rootNode = this.app.getRootNode();
        iniMap(); // call method to create a terrain
        fire();             // creating a fire
    }
    public void iniMap(){
        mapModel =  assetManager.loadModel("Scenes/Island.j3o");// create and Island Terrain using J3M editor
        mapModel.setName("Map");      // setName for the terrain
        RigidBodyControl scenePhy = new RigidBodyControl(0f); // add rigidbody physical effect
        mapModel.addControl(scenePhy);
        bulletAppState.getPhysicsSpace().add(mapModel); // add map to main physical controller of game
        rootNode.attachChild(mapModel);                 // add to rootNode
        mapModel.setShadowMode(ShadowMode.CastAndReceive); // make the map can cast and receive shadow
    } 
    
    public void fire(){ // create a fire on middle of terrain. code adapted from JME book, chapter 7   
        ParticleEmitter fire = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 30);
        Material mat_red = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        mat_red.setTexture("Texture", assetManager.loadTexture("Textures/Effects/flame.png"));
        fire.setMaterial(mat_red);
        fire.setImagesX(2); 
        fire.setImagesY(2); // 2x2 texture animation
        fire.setEndColor(  new ColorRGBA(1f, 0f, 0f, 1f));   // red
        fire.setStartColor(new ColorRGBA(1f, 1f, 0f, 0.5f)); // yellow
        fire.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 2, 0));
        fire.setStartSize(4f);
        fire.setEndSize(0.5f);
        fire.setGravity(0, 0, 0); // no gravity
        fire.setLowLife(2f);
        fire.setHighLife(6f);
        fire.getParticleInfluencer().setVelocityVariation(0.3f);
        fire.setLocalTranslation(10,15,-20); // translate the fire to the middle of map
        rootNode.attachChild(fire);      // add fire to the main rootNode
    }

    @Override
    public void update(float tpf) { // not used yet
    }

    @Override
    public void cleanup() {
        super.cleanup(); // use this method to disappear this state
        rootNode.detachAllChildren();
    }
    public Spatial returnMap(){ // return mapModel for later need
        return mapModel;
    }
}