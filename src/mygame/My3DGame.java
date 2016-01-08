package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.cursors.plugins.JmeCursor;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture2D;
import com.jme3.water.WaterFilter;
import java.util.ArrayList;


/*******************************************************************************
 * Thanh Nguyen
 * ID 2451232
 * 31/3/2015
 * My3DGame.java is the main class of the game which includes:
 * -   operate appState of all game elements: character, boat, elephant, robot, tree, terrain 
 * -   change cursor's shape, 2D display of interface, set up camera
 * -   adding ambient light, spot light, directional light (optional)
 * -   make water effect, bloom effect
 * -   change the opening window's picture of game, add pause setting.
 * -   initialize the physical effect and shadow effect
 ******************************************************************************/

public class My3DGame extends SimpleApplication { // extends SimpleAllication class
    
    private ArrayList<JmeCursor> cursors = new ArrayList<JmeCursor>();// creating cursors array list
    private BulletAppState bulletAppState;    // physical control declaring
    private Node playerNode;
    private DirectionalLight sun;
    
    @Override
    public void simpleInitApp() {  // set up the inital apprearance of the game
        setDisplayStatView(false); // remove the default status view
        setDisplayFps(false);      // remove the default Fps (Frames per second)
        cursors.add((JmeCursor) assetManager.loadAsset("Textures/Cursors/meme.cur"));// add cursor file (from J3M AssetPacks)to array,
        inputManager.setMouseCursor(cursors.get(0));// set the new shape of cursor at index position 0 in array
        // set up speed of camera && enable Drag mouse to rotate camera
        flyCam.setMoveSpeed(100f);     // set cam's speed of moving is 100
        flyCam.setDragToRotate(true);  // enable drag mouse to rotage

        // set initial location && rotate cam at 45 degrees
        rootNode.setShadowMode(ShadowMode.Off);        
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        
        MapState floor = new MapState(bulletAppState);                //declare map's State
        CharacterState character = new CharacterState(bulletAppState);//declare character's State
        TankState tank = new TankState(bulletAppState);               //declare tank's State
        TreeState tree = new TreeState(bulletAppState);               //declare tree's State
        BoatState boat = new BoatState(bulletAppState);               //declare boat's State
        ElephantState elephant = new ElephantState(bulletAppState);   //declare elephant's State  
        RobotState robot = new RobotState(bulletAppState);            //declare robot's State 
        SoundState sound = new SoundState();                          ////declare sound's State
        NPCMonkeyState NPC = new NPCMonkeyState(bulletAppState);
        UserGuiState userGui = new UserGuiState(settings);
        stateManager.attach(floor);         //adding the map state
        stateManager.attach(character);     //adding the character state (controls and appState are in CharacterControl.java & CharacterState.java ) 
        stateManager.attach(tank);          //adding the tank state
        stateManager.attach(tree);          //adding the tree state
        stateManager.attach(boat);          //adding the boat state
        stateManager.attach(elephant);      //adding the elephant state        
        stateManager.attach(sound);         // adding the enviroment's sound state
        stateManager.attach(robot);         // adding the robot state
        stateManager.attach(NPC);         // adding the robot state
        stateManager.attach(userGui);
        iniLight();                         // using Spot && Ambient Light
        iniEffect();                        // add bloom && water effect        
        skyBox();                           // add sky background
    }

    @Override
    public void simpleUpdate(float tpf) { // keep updating position of character and set camera following it
       // Vector3f loc = stateManager.getState(CharacterState.class).returnCharacter().getLocalTranslation(); // get current location of character       
       // spot.setPosition(new Vector3f(loc.x, loc.y+50, loc.z-50));// translate spot light onto the character's location
        playerNode = stateManager.getState(CharacterState.class).returnCharacter(); // call character's state
        Vector3f camLoc = new Vector3f(playerNode.getLocalTranslation().x,          // adjust the position of camera to have good view
                                       playerNode.getLocalTranslation().y+100,
                                       playerNode.getLocalTranslation().z-120);

        cam.setLocation(camLoc);  // set initial location of cam
        Quaternion camRotate = new Quaternion();
        camRotate.fromAngleAxis( 30*FastMath.DEG_TO_RAD , Vector3f.UNIT_X); // set rotating angle of 30 degrees arround X axis
        cam.setRotation(camRotate); // rotate cam
    }

    @Override
    public void simpleRender(RenderManager rm) { // not used yet
        //TODO: add render code
    }    

    public static void main(String[] args) {
        My3DGame app = new My3DGame();
        // change setting of opening window of game: name and picture
        AppSettings settings = new AppSettings(true);// creating setting to the user interface
        settings.setTitle("ShoBiNo");               // Set name for the game
        settings.setSettingsDialogImage("Interface/Sinbad.jpg"); //donwload image from google.com
        settings.setResolution(1024,768);
        //app.setShowSettings(false); // skip built-in dialog
        app.setSettings(settings);                               // apply setting   
        app.setPauseOnLostFocus(false);// set true to pause the game with Alt-tab
        app.start();                   // start the game
    }
    
    public void iniEffect(){ // add bloom effect and water effect to game
        
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager); // declare FPP, book's example
        BloomFilter bf = new BloomFilter(BloomFilter.GlowMode.SceneAndObjects); // Bloom filter       
        fpp.addFilter(bf);                         // add filter     
        // create the light that casting the shadow
        DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, 1024, 2);
        dlsr.setLight(sun);
        viewPort.addProcessor(dlsr);

        WaterFilter water = new WaterFilter();         // water filter
        Vector3f lightDir = new Vector3f(0,-0.001f,0); // light direction
        water = new WaterFilter(rootNode, lightDir);
        water.setCenter(Vector3f.ZERO); // set water layer is in the center point 0,0,0 
        
        // set up parameters for water's properties
        // some were modified following https://www.youtube.com/watch?v=wOFZzRZYkkg
        water.setRadius(600);              // set this parameter lagre to cover total the terrain (creating terrain in MapState.java)
        water.setWaveScale(0.03f);         // set the detail performance of wave water
        water.setMaxAmplitude(2f);         // set Amplitute
        water.setWaterTransparency(0.2f);  // set the transparence for water
        water.setFoamExistence(new Vector3f(1f, 3, 0.5f)); 
        water.setFoamTexture((Texture2D) assetManager.loadTexture("Common/MatDefs/Water/Textures/foam2.jpg"));// load build-in FoamTexture
        water.setRefractionStrength(0.2f); // set refraction coefficient
        water.setShininess(0.3f);          // set Shinness
        water.setShoreHardness(1.0f);      // set ShoreHardness
        water.setWaterHeight(7f);          // WaterHeight
        fpp.addFilter(water);              // add water filter to FilterPostProcessor
        viewPort.addProcessor(fpp);        // add FilterPostProcessor to viewPort
    }
    
    public void skyBox(){ // creating a sky background for game
        Material boxMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        boxMat.setTexture("DiffuseMap",assetManager.loadTexture("Textures/Sky/Lagoon/Mars.jpg"));// picture was downloaded from google.com
        Box boxMesh = new Box(250, 250, 250);           // make the box lagre enough to cover 1 side of the terrain
        Geometry sky = new Geometry("Box", boxMesh);
        sky.setMaterial(boxMat);
        sky.setLocalTranslation(0,20,500);              // set location in  foward direction of player's view
        rootNode.attachChild(sky);                      // attact sky on main rootNode

        Quaternion skyRotate = new Quaternion();
        skyRotate.fromAngleAxis( 90*FastMath.DEG_TO_RAD , Vector3f.UNIT_X); // set rotating angle of 30 degrees arround X axis
        sky.rotate(skyRotate); // rotate sky box to have better view
    }
    
    public void iniLight(){// code from J3M Palette      
        //A Directional light source        
        sun = new DirectionalLight();
        sun.setDirection((new Vector3f(1, -1, 1)));
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun); 

        //A white ambient light source
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White);
        rootNode.addLight(ambient); 
    }    
}
