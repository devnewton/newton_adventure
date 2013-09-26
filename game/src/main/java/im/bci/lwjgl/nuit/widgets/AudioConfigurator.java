package im.bci.lwjgl.nuit.widgets;

import java.util.ArrayList;
import java.util.List;

import im.bci.lwjgl.nuit.NuitToolkit;

public class AudioConfigurator extends Table {

    public static class Volume {

        int level;

        Volume(int level) {
            this.level = level;
        }

        @Override
        public String toString() {
            return level + "%";
        }
    }

    public AudioConfigurator(NuitToolkit toolkit) {
        super(toolkit);
        List<Volume> possibleVolumes = new ArrayList<>();
        for (int l = 0; l <= 100; l += 10) {
            possibleVolumes.add(new Volume(l));
        }

        defaults().expand();
        cell(new Label(toolkit, "Music volume"));
        cell(new Select<Volume>(toolkit, possibleVolumes) {
            @Override
            public void onOK() {
                super.onOK();
                changeMusicVolume(getSelected().level / 100.0f);
            }
        });
        row();
        cell(new Label(toolkit, "Effects volume"));
        cell(new Select<Volume>(toolkit, possibleVolumes) {
            @Override
            public void onOK() {
                super.onOK();
                changeEffectVolume(getSelected().level / 100.0f);
            }
        });
        row();
        cell(new Button(toolkit, "Back") {
            public void onOK() {
                closeAudioSettings();
            }
        }).colspan(2);
    }

    protected void changeEffectVolume(float f) {
    }

    protected void changeMusicVolume(float f) {
    }

    protected void closeAudioSettings() {
    }

}
