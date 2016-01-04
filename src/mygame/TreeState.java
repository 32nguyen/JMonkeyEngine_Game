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
import com.jme3.effect.shapes.EmitterSphereShape;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/******************************************************************************* 
 * Name: ThanhNguyen   
 * ID  : 2451232
 * 31/3/2015
 * TreeState.java loads a tree model on to the map, add physic, shadow and add emitter effect
 *******************************************************************************/
public class TreeState extends AbstractAppState {
    private BulletAppState bulletAppState; 
    private Node rootNode;
    private SimpleApplication app;
    private AssetManager assetManager;
   
    public TreeState(BulletAppState bulletAppStater) {
        this.bulletAppState = bulletAppStater;// passing bulletAppState from My3DGame.Class
    }
    @Override
    public void initialize(AppStateManager stateManager, Application app) {// initalize the state
        // declaring the parameter from app
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;
        this.assetManager = this.app.getAssetManager();        
        this.rootNode = this.app.getRootNode();        
        createTree(); // creating the tree
        emitter();    // create the emmiter effects
    }

    public void createTree() { // create a tree on map
        Spatial treeGeo = assetManager.loadModel("Models/Tree/Tree.mesh.j3o"); // loading tree model downloaded from J3M package
        treeGeo.setName("Tree");
        treeGeo.scale(5); // make tree bigger
        treeGeo.setQueueBucket(RenderQueue.Bucket.Transparent); // set transparent
        Vector3f treeLoc = new Vector3f(40,10,0); // vector location of tree
        treeGeo.setLocalTranslation(treeLoc);  // translate the tree
        RigidBodyControl elephantPhy = new RigidBodyControl(0f); // make the tree be a rigidbody geo
        treeGeo.addControl(elephantPhy);
        bulletAppState.getPhysicsSpace().add(treeGeo); // add the tree to the main physical controller of game
        rootNode.attachChild(treeGeo);  // add to rootNode
        treeGeo.setShadowMode(RenderQueue.ShadowMode.CastAndReceive); // make the tree cast and receive the shadow
    }
    public void emitter(){    // create the emitter effects, code is adapted from JME book's example
        ParticleEmitter embersEmitter = new ParticleEmitter("embers", ParticleMesh.Type.Triangle, 100);
        Material embersMat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        embersMat.setTexture("Texture", assetManager.loadTexture("Textures/Effects/embers.png"));
        embersEmitter.setMaterial(embersMat);
        embersEmitter.setImagesX(1);
        embersEmitter.setImagesY(1); //1x1 texture
        embersEmitter.setStartColor(ColorRGBA.White); // set starting color of emitter
        embersEmitter.setEndColor(ColorRGBA.Yellow);  // set ending color of emitter
        embersEmitter.setStartSize(4f);
        embersEmitter.setEndSize(10f);
        embersEmitter.setGravity(0, -.5f, 0); // set gravity
        embersEmitter.setLowLife(2f);
        embersEmitter.setHighLife(2.2f);
        embersEmitter.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 3, 0));
        embersEmitter.getParticleInfluencer().setVelocityVariation(.5f);
        embersEmitter.setShape(new EmitterSphereShape(Vector3f.ZERO, 2f));
        embersEmitter.scale(5);             // scale emitter
        embersEmitter.setLocalTranslation(40,25,0); // translate the emitter ontop of the tree
        rootNode.attachChild(embersEmitter); // add emitter to rootNode
    }

    @Override
    public void update(float tpf) {   // not used yet     
    }

    @Override
    public void cleanup() { // use this method to disappear this state
        super.cleanup();
        rootNode.detachAllChildren();
    }    
}