package mygame;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/******************************************************************************* 
 * Name: ThanhNguyen   
 * ID  : 2451232
 * 31/3/2015
 * ElephantState.java loads elephent model with
 * -    rotate, translate and do animation
 * -    cast and receive shadow
 * -    add physic effect to the elephant
 *******************************************************************************/

public class ElephantState extends AbstractAppState implements AnimEventListener {// extends AbstractAppState package and implements AnimEventListener for control animation
    private BulletAppState bulletAppState;
    private Node rootNode;
    private SimpleApplication app;
    private AssetManager assetManager;
    private AnimControl control;
    private AnimChannel channel;
    private static final String LEGUP = "legUp";
    private Spatial elephant;

    public ElephantState(BulletAppState bulletAppStater) {
        this.bulletAppState = bulletAppStater;// passing bulletAppState from My3DGame.Class
    }
    @Override
    public void initialize(AppStateManager stateManager, Application app) {// initialize the state
        // declaring the parameter from app
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;
        this.assetManager = this.app.getAssetManager();
        this.rootNode = this.app.getRootNode();
        
        createElephant();   // call createElephant() method to create a elephant    
        control = elephant.getControl(AnimControl.class); // get control for elephant
        control.addListener(this);                        // add listener to control
        //for (String anim : control.getAnimationNames()) {
        //    System.out.print(anim);
        //}
        channel= control.createChannel(); // create channel for animation
        channel.setAnim(LEGUP); // set animation
    }

    public void createElephant() {// loading model elephant, rotate, add shadow and add physics
        elephant = assetManager.loadModel("Models/Elephant/Elephant.mesh.j3o");// model is from J3M assetPack
        elephant.setName("elephant"); 
        elephant.scale(0.2f);                       // make tree bigger
        Vector3f treeLoc = new Vector3f(-25,10,20);
        elephant.setLocalTranslation(treeLoc);      // translate
        Quaternion elephantRotate = new Quaternion();
        elephantRotate.fromAngleAxis( 90*FastMath.DEG_TO_RAD , Vector3f.UNIT_Y);    // rotate elephant
        elephant.rotate(elephantRotate);// rotate
        RigidBodyControl elephantPhy = new RigidBodyControl(0f);  // create rigidbody physical effect
        elephant.addControl(elephantPhy);                         // add physic to elephant
        bulletAppState.getPhysicsSpace().add(elephant);           // add elephant to main physical control of game
        rootNode.attachChild(elephant); // add spatial to rootNode
        elephant.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);// make elephant to cast and receive shadow
    }

    @Override
    public void update(float tpf) { // update method, not used yet
 
    }
    
    @Override
    public void cleanup() { // function for dispear from rootNode
        super.cleanup();
        rootNode.detachAllChildren();
    }

    public Spatial returnCharacter(){// return the elephant geometry 
        return elephant;
    }    

    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) { // not used yet       
    }
    
    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {   // not used yet      
    }
}