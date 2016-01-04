package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/******************************************************************************* 
 * Name: ThanhNguyen   
 * ID  : 2451232
 * 31/3/2015
 * BoatState.java is a state of the model boat that includes:
 * -    loading boat model 
 * -    translate it onto the sea && attach it to rooNode
 * -    add physical effect with rigidbody
 *******************************************************************************/
public class BoatState extends AbstractAppState {
    private BulletAppState bulletAppState;
    private Node rootNode;
    private SimpleApplication app;
    private AssetManager assetManager;
    
    public BoatState(BulletAppState bulletAppStater) {
        this.bulletAppState = bulletAppStater;//passing bulletAppState from My3DGame.Class
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {// initialize the first appearance
        // declaring the parameters from app
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;
        this.assetManager = this.app.getAssetManager();        
        this.rootNode = this.app.getRootNode();        
        createBoat(); // creating the boat
    }

    public void createBoat() { // load model boat
        Spatial boatGeo = assetManager.loadModel("Models/Boat/boat.j3o");// loading model from J3M assetPack
        boatGeo.scale(2); // make boat bigger
        //boatGeo.setQueueBucket(RenderQueue.Bucket.Transparent); // leaves are transparent
        Vector3f Loc = new Vector3f(100,8.5f,85); 
        boatGeo.setLocalTranslation(Loc);     // translate boat to water
        rootNode.attachChild(boatGeo);        // attach to rooNode
        RigidBodyControl elephantPhy = new RigidBodyControl(0f); // creating rigidbody effect
        boatGeo.addControl(elephantPhy);                         // add rigidbody physic to the boat
        bulletAppState.getPhysicsSpace().add(boatGeo);           // add boat to physical controller of game
    }

    @Override
    public void update(float tpf) {   // not used yet     
    }

    @Override
    public void cleanup() {           // this is for make geo dispear from rootNode
        super.cleanup();
        rootNode.detachAllChildren();
    }    
}