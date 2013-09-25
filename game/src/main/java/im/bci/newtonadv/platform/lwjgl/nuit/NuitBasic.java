package im.bci.newtonadv.platform.lwjgl.nuit;

import java.util.Arrays;

import org.lwjgl.LWJGLException;

import im.bci.newtonadv.platform.lwjgl.nuit.widgets.Button;
import im.bci.newtonadv.platform.lwjgl.nuit.widgets.ControlsConfigurator;
import im.bci.newtonadv.platform.lwjgl.nuit.widgets.Root;
import im.bci.newtonadv.platform.lwjgl.nuit.widgets.Table;
import im.bci.newtonadv.platform.lwjgl.nuit.widgets.VideoConfigurator;

public class NuitBasic {
    private NuitToolkit toolkit;
    private Root root;
    private Table mainMenu;
    private VideoConfigurator videoMenu;
    private Table optionsMenu;
    private ControlsConfigurator controls;
    
    public NuitBasic() throws LWJGLException {
        toolkit = new NuitToolkit();
        root = new Root(toolkit);
        initVideo();
        initControls();
        initOptions();
        initMain();
	}

	private void initVideo() throws LWJGLException {
		videoMenu = new VideoConfigurator(toolkit){
			@Override
			protected void closeVideoSettings() {
				root.show(optionsMenu);
			}
		};		
		root.add(videoMenu);
	}
	
 

	private void initMain() {
		mainMenu = new Table(toolkit);
		mainMenu.defaults().expand().fill();
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
		optionsMenu.defaults().expand().fill();
        optionsMenu.cell(new Button(toolkit, "VIDEO") {
	        @Override
	        public void onOK() {
	        	root.show(videoMenu);
	        }
        });
        optionsMenu.row();
        optionsMenu.cell(new Button(toolkit, "AUDIO"));
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
