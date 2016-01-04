/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/******************************************************************************
 *  Thanh Nguyen
 *  ID: 2451232
 *  31/3/2015
 *  SoundState.java creates several sound sources located on the sea. This active those
 *  source when the character is close and deactive those when the character is 
 *  far. 
 *******************************************************************************/
public class SoundState extends AbstractAppState {
    private AudioNode backGroundSound,backGroundSoundOfLost,backGroundSoundOfWin,seaAudio1,seaAudio2,
                      seaAudio3,seaAudio4,seaAudio5,seaAudio6,seaAudio7,seaAudio8;
    private Node rootNode;
    private SimpleApplication app;
    private AssetManager assetManager;
    private AppStateManager state;
    float time = 0;
    private Node playerNode;
    private boolean battleStart = false;
    private int gameState = 0;
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        // declaring the parameters from app
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;
        this.assetManager = this.app.getAssetManager();        
        this.rootNode = this.app.getRootNode();        
        state = stateManager;
        createSound(); // creating the sound
    }

    public void createSound() { //creating Sounds
        seaAudio1 = new AudioNode(assetManager,"Sounds/Environment/River.ogg");// Music sound suported by JME
        seaAudio1.setPositional(true);   // Use 3D audio
        seaAudio1.setRefDistance(50f);   // Distance of 50% volume
        seaAudio1.setMaxDistance(60f);   // Distance where it stops going quieter
        seaAudio1.setVolume(1);          // Default volume
        seaAudio1.setLooping(true);      // Activate continuous playing
        seaAudio1.setLocalTranslation(20, 0, 100);
        
        seaAudio2 = new AudioNode(assetManager,"Sounds/Environment/River.ogg");// Music sound suported by JME
        seaAudio2.setPositional(true);   // Use 3D audio
        seaAudio2.setRefDistance(50f);   // Distance of 50% volume
        seaAudio2.setMaxDistance(60f);   // Distance where it stops going quieter
        seaAudio2.setVolume(1);          // Default volume
        seaAudio2.setLooping(true);      // Activate continuous playing
        seaAudio2.setLocalTranslation(-50, 0, 150);
        
        seaAudio3 = new AudioNode(assetManager,"Sounds/Environment/River.ogg");// Music sound suported by JME
        seaAudio3.setPositional(true);   // Use 3D audio
        seaAudio3.setRefDistance(50f);   // Distance of 50% volume
        seaAudio3.setMaxDistance(60f);   // Distance where it stops going quieter
        seaAudio3.setVolume(1);          // Default volume
        seaAudio3.setLooping(true);      // Activate continuous playing
        seaAudio3.setLocalTranslation(-150, 0, 30);
        
        seaAudio4 = new AudioNode(assetManager,"Sounds/Environment/River.ogg");// Music sound suported by JME
        seaAudio4.setPositional(true);   // Use 3D audio
        seaAudio4.setRefDistance(60f);   // Distance of 50% volume
        seaAudio4.setMaxDistance(80f);   // Distance where it stops going quieter
        seaAudio4.setVolume(1);          // Default volume
        seaAudio4.setLooping(true);      // Activate continuous playing
        seaAudio4.setLocalTranslation(-150, 0, -70);
        
        seaAudio5 = new AudioNode(assetManager,"Sounds/Environment/River.ogg");// Music sound suported by JME
        seaAudio5.setPositional(true);   // Use 3D audio
        seaAudio5.setRefDistance(50f);   // Distance of 50% volume
        seaAudio5.setMaxDistance(60f);   // Distance where it stops going quieter
        seaAudio5.setVolume(1);          // Default volume
        seaAudio5.setLooping(true);      // Activate continuous playing
        seaAudio5.setLocalTranslation(100, 0, 70);
        
        seaAudio6 = new AudioNode(assetManager,"Sounds/Environment/River.ogg");// Music sound suported by JME
        seaAudio6.setPositional(true);   // Use 3D audio
        seaAudio6.setRefDistance(50f);   // Distance of 50% volume
        seaAudio6.setMaxDistance(60f);   // Distance where it stops going quieter
        seaAudio6.setVolume(1);          // Default volume
        seaAudio6.setLooping(true);      // Activate continuous playing
        seaAudio6.setLocalTranslation(150, 0, -30);
        
        seaAudio7 = new AudioNode(assetManager,"Sounds/Environment/River.ogg");// Music sound suported by JME
        seaAudio7.setPositional(true);   // Use 3D audio
        seaAudio7.setRefDistance(50f);   // Distance of 50% volume
        seaAudio7.setMaxDistance(60f);   // Distance where it stops going quieter
        seaAudio7.setVolume(1);          // Default volume
        seaAudio7.setLooping(true);      // Activate continuous playing
        seaAudio7.setLocalTranslation(150, 0, -130);
        
        seaAudio8 = new AudioNode(assetManager,"Sounds/Environment/River.ogg");// Music sound suported by JME
        seaAudio8.setPositional(true);   // Use 3D audio
        seaAudio8.setRefDistance(50f);   // Distance of 50% volume
        seaAudio8.setMaxDistance(60f);   // Distance where it stops going quieter
        seaAudio8.setVolume(1);          // Default volume
        seaAudio8.setLooping(true);      // Activate continuous playing
        seaAudio8.setLocalTranslation(50, 0, -130);
        
        // Back ground sound in baltle time
        backGroundSound = new AudioNode(assetManager,"Sounds/Environment/BackGroundMusic.wav");//music sound from Justus_Parrota_Music School of CUA
        backGroundSound.setVolume(5);          // Default volume
        backGroundSound.setLooping(true);      // Activate continuous playing
        
        // Back ground sound of ending game with lost
        backGroundSoundOfLost = new AudioNode(assetManager,"Sounds/Environment/Lost.wav");//music sound from Justus_Parrota_Music School of CUA
        backGroundSoundOfLost.setVolume(20);          // Default volume
        backGroundSoundOfLost.setLooping(true);      // Activate continuous playing    
        
        // Back ground sound of ending game with victory
        backGroundSoundOfWin = new AudioNode(assetManager,"Sounds/Environment/Victory.wav");//music sound from Justus_Parrota_Music School of CUA
        backGroundSoundOfWin.setVolume(20);          // Default volume
        backGroundSoundOfWin.setLooping(true);      // Activate continuous playing    
        
        Node shootingSoundNode = new Node("Riverbed");
        shootingSoundNode.setLocalTranslation(Vector3f.ZERO);
        // add sounds to the sound Node
        shootingSoundNode.attachChild(seaAudio1);
        shootingSoundNode.attachChild(seaAudio2);
        shootingSoundNode.attachChild(seaAudio3);
        shootingSoundNode.attachChild(seaAudio4);
        shootingSoundNode.attachChild(seaAudio5);
        shootingSoundNode.attachChild(seaAudio6);
        shootingSoundNode.attachChild(seaAudio7);        
        rootNode.attachChild(shootingSoundNode); // add sound Node to main rootNode

    }

    @Override
    public void update(float tpf) {   // update the current location of character
                                      // activate the sound when the character is close
        battleStart = state.getState(NPCMonkeyState.class).returnBattleStartCheck();
        gameState = state.getState(UserGuiState.class).returnGameState();
        if (gameState == 0 && battleStart){
            backGroundSound.play();
            backGroundSoundOfWin.stop();
            backGroundSoundOfLost.stop();
        }else if (gameState == 1){
            backGroundSound.stop();
            backGroundSoundOfLost.play();
            backGroundSoundOfWin.stop();
        }else if (gameState == 2){
            backGroundSound.stop();
            backGroundSoundOfWin.play();
            backGroundSoundOfLost.stop();
        }
        playerNode = state.getState(CharacterState.class).returnCharacter();
        if (playerNode.getLocalTranslation().distance(seaAudio1.getPosition())<60){
           seaAudio1.play(); 
        }else {seaAudio1.stop();}
        if (playerNode.getLocalTranslation().distance(seaAudio2.getPosition())<60){
           seaAudio2.play(); 
        }else {seaAudio2.stop();}
        if (playerNode.getLocalTranslation().distance(seaAudio3.getPosition())<60){
           seaAudio3.play(); 
        }else {seaAudio3.stop();}
        if (playerNode.getLocalTranslation().distance(seaAudio4.getPosition())<60){
           seaAudio4.play(); 
        }else {seaAudio4.stop();}
        if (playerNode.getLocalTranslation().distance(seaAudio5.getPosition())<60){
           seaAudio5.play(); 
        }else {seaAudio5.stop();}
        if (playerNode.getLocalTranslation().distance(seaAudio6.getPosition())<50){
           seaAudio6.play(); 
        }else {seaAudio6.stop();}
        if (playerNode.getLocalTranslation().distance(seaAudio7.getPosition())<60){
           seaAudio7.play(); 
        }else {seaAudio7.stop();}
        if (playerNode.getLocalTranslation().distance(seaAudio8.getPosition())<60){
           seaAudio8.play(); 
        }else {seaAudio8.stop();} // turn op the sound when the character is far from sound sources
    }

    @Override
    public void cleanup() {           // this is for make geo dispear from rootNode
        super.cleanup();
        rootNode.detachAllChildren();
    }
}
