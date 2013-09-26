package im.bci.lwjgl.nuit;

import java.util.Arrays;

import org.lwjgl.LWJGLException;

import im.bci.lwjgl.nuit.widgets.AudioConfigurator;
import im.bci.lwjgl.nuit.widgets.Button;
import im.bci.lwjgl.nuit.widgets.ControlsConfigurator;
import im.bci.lwjgl.nuit.widgets.Root;
import im.bci.lwjgl.nuit.widgets.Table;
import im.bci.lwjgl.nuit.widgets.VideoConfigurator;

public class NuitBasic {
    private NuitToolkit toolkit;
    private Root root;
    private Table mainMenu;
    private VideoConfigurator videoConfigurator;
    private AudioConfigurator audioConfigurator;
    private Table optionsMenu;
    private ControlsConfigurator controls;
    
    public NuitBasic() throws LWJGLException {
        toolkit = new NuitToolkit();
        root = new Root(toolkit);
        initVideo();
        initAudio();
        initControls();
        initOptions();
        initMain();
	}

    private void initVideo() throws LWJGLException {
		videoConfigurator = new VideoConfigurator(toolkit){
			@Override
			protected void closeVideoSettings() {
				root.show(optionsMenu);
			}
		};		
		root.add(videoConfigurator);
	}
	
    private void initAudio() {
        audioConfigurator = new AudioConfigurator(toolkit) {
            @Override
            protected void closeAudioSettings() {
                root.show(optionsMenu);
            }
        };
    }

	private void initMain() {
		mainMenu = new Table(toolkit);
		mainMenu.defaults().expand();
        mainMenu.cell(new Button(toolkit, "START"));
        mainMenu.row();
        mainMenu.cell(new Button(toolkit, "OPTIONS") {
            @Override
            public void onOK() {
                root.show(optionsMenu);
            }
        });
        mainMenu.row();
        mainMenu.cell(new Button(toolkit, "QUIT") { 
            @Override
            public void onOK() {
                System.exit(0);
            }
        });
        mainMenu.row();
        root.add(mainMenu);
	}

	private void initOptions() {
		optionsMenu = new Table(toolkit);
		optionsMenu.defaults().expand();
        optionsMenu.cell(new Button(toolkit, "VIDEO") {
	        @Override
	        public void onOK() {
	        	root.show(videoConfigurator);
	        }
        });
        optionsMenu.row();
        optionsMenu.cell(new Button(toolkit, "AUDIO") {
            @Override
            public void onOK() {
                root.show(audioConfigurator);
            }
        });
        optionsMenu.row();
        optionsMenu.cell(new Button(toolkit, "CONTROLS") {
            @Override
            public void onOK() {
                root.show(controls);
            }
        });
        optionsMenu.row();
        optionsMenu.cell(new Button(toolkit, "BACK") {
            @Override
            public void onOK() {
                root.show(mainMenu);
            }
        });
        optionsMenu.row();
        root.add(optionsMenu);
	}

	private void initControls() {
		controls = new ControlsConfigurator(toolkit, Arrays.asList(toolkit.getMenuUp(), toolkit.getMenuDown(),toolkit.getMenuLeft(), toolkit.getMenuRight(), toolkit.getMenuOK(), toolkit.getMenuCancel()), null) {
            @Override
            public void onBack() {
                root.show(optionsMenu);
            }
        };
        root.add(controls);
	}

	public void update() {
		root.update();
	}

	public void draw() {
		root.draw();
	}
}
