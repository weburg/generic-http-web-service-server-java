package com.weburg.services;

import com.weburg.domain.Engine;
import com.weburg.domain.Photo;
import com.weburg.domain.Sound;
import org.apache.commons.io.FileUtils;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DefaultHttpWebService implements HttpWebService {
    int lastEngineId = 0;
    String dataFilePath;

    public DefaultHttpWebService(String dataFilePath) {
        this.dataFilePath = dataFilePath;

        File directory = new File(this.dataFilePath);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Set the last engine ID based on existing files

        File[] engineFiles = getEngineFiles();

        int maxEngineId = 0;

        for (File engineFile : engineFiles) {
            System.out.println(engineFile.getName());
            int engineId = Integer.parseInt(engineFile.getName().substring(engineFile.getName().indexOf("_") + 1,
                    engineFile.getName().indexOf(".ser")));
            if (engineId > maxEngineId) {
                maxEngineId = engineId;
            }
        }

        this.lastEngineId = maxEngineId;
    }

    public String getDataFilePath() {
        return dataFilePath;
    }

    public Engine getEngine(int id) throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(this.dataFilePath + System.getProperty("file.separator")
                + "Engine_" + id + ".ser");
        ObjectInputStream ois = new ObjectInputStream(fis);

        Engine engine = (Engine) ois.readObject();

        ois.close();
        fis.close();

        return engine;
    }

    public ArrayList<Engine> getEngines() throws IOException, ClassNotFoundException {
        ArrayList<Engine> engines = new ArrayList<>();

        File[] engineFiles = getEngineFiles();

        for (File engineFile : engineFiles) {
            FileInputStream fis = new FileInputStream(engineFile);
            ObjectInputStream ois = new ObjectInputStream(fis);

            Engine engine = (Engine) ois.readObject();

            engines.add(engine);

            ois.close();
            fis.close();
        }

        return engines;
    }

    public int createEngine(Engine engine) throws IOException {
        int engineId = ++this.lastEngineId;

        engine.setId(engineId);

        FileOutputStream fos = new FileOutputStream(this.dataFilePath + System.getProperty("file.separator")
                + "Engine_" + engineId + ".ser");
        ObjectOutputStream oos = new ObjectOutputStream(fos);

        oos.writeObject(engine);

        oos.close();
        fos.close();

        return engineId;
    }

    public int createOrReplaceEngine(Engine engine) throws IOException {
        if (engine.getId() == 0) {
            throw new IllegalArgumentException("For createOrReplace operations, the id must be specified.");
        }

        File file = new File(this.dataFilePath + System.getProperty("file.separator")
                + "Engine_" + engine.getId() + ".ser");

        FileOutputStream fos = new FileOutputStream(file);
        ObjectOutputStream oos = new ObjectOutputStream(fos);

        oos.writeObject(engine);

        oos.close();
        fos.close();

        if (this.lastEngineId < engine.getId()) {
            this.lastEngineId = engine.getId();
        }

        return engine.getId();
    }

    public void updateEngine(Engine engine) throws IOException {
        if (engine.getId() == 0) {
            throw new IllegalArgumentException("For update operations, the id must be specified.");
        }

        createOrReplaceEngine(engine); // For simple persistence, reuse other method since replacement happens anyway
    }

    public void deleteEngine(int id) throws IOException {
        File file = new File(this.dataFilePath + System.getProperty("file.separator") + "Engine_" + id + ".ser");
        boolean deleted = file.delete();

        if (!deleted) {
            throw new IOException("Could not delete " + file.getName());
        }
    }

    public void restartEngine(int id) throws IOException, ClassNotFoundException {
        Engine engine = getEngine(id);

        if (!engine.getName().endsWith("Restarted")) {
            engine.setName(engine.getName() + "Restarted");
            updateEngine(engine);
        }
    }

    public void stopEngine(int id) throws IOException, ClassNotFoundException {
        Engine engine = getEngine(id);

        if (engine.getName().endsWith("Restarted")) {
            engine.setName(engine.getName().substring(0, engine.getName().length() - "Restarted".length()));
            updateEngine(engine);
        }
    }

    private File[] getEngineFiles() {
        File directory = new File(this.dataFilePath);

        File engineFiles[] = directory.listFiles(
                (dir, name) -> {
                    if (name.startsWith("Engine_") && name.endsWith(".ser")) {
                        return true;
                    } else {
                        return false;
                    }
                }
        );

        return engineFiles;
    }

    private File[] getPhotoFiles() {
        File directory = new File(this.dataFilePath);

        File photoFiles[] = directory.listFiles(
                (dir, name) -> {
                    if (name.toLowerCase().endsWith(".gif") || name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".jpeg") || name.toLowerCase().endsWith(".png")) {
                        return true;
                    } else {
                        return false;
                    }
                }
        );

        return photoFiles;
    }

    private File[] getSoundFiles() {
        File directory = new File(this.dataFilePath);

        File soundFiles[] = directory.listFiles(
                (dir, name) -> {
                    if (name.toLowerCase().endsWith(".wav")) {
                        return true;
                    } else {
                        return false;
                    }
                }
        );

        return soundFiles;
    }

    public Photo getPhoto(String photoFile) throws IOException, ClassNotFoundException {
        Photo photo = new Photo();
        photo.setCaption(FileUtils.readFileToString(new File(this.dataFilePath + System.getProperty("file.separator") + photoFile + ".txt")));
        photo.setPhotoFile(new File(this.dataFilePath + System.getProperty("file.separator") + photoFile));

        return photo;
    }

    public List<Photo> getPhotos() throws IOException, ClassNotFoundException {
        ArrayList<Photo> photos = new ArrayList<>();

        File[] photoFiles = getPhotoFiles();

        for (File photoFile : photoFiles) {
            Photo photo = new Photo();
            photo.setCaption(FileUtils.readFileToString(new File(this.dataFilePath + System.getProperty("file.separator") + photoFile.getName() + ".txt")));
            photo.setPhotoFile(photoFile);

            photos.add(photo);
        }

        return photos;
    }

    public String createPhoto(Photo photo) throws IOException {
        // The file will need to have been written to disk already, we only have a reference to it now
        // TODO given that this is now a File, we should be able to write it out here instead, it's better to do in here

        // Write caption
        FileOutputStream fos = new FileOutputStream(this.dataFilePath + System.getProperty("file.separator")
                + photo.getPhotoFile().getName() + ".txt");
        fos.write(photo.getCaption().getBytes());
        fos.close();

        return photo.getPhotoFile().getName();
    }

    public Sound getSound(String soundFile) throws IOException, ClassNotFoundException {
        Sound sound = new Sound();
        sound.setSoundFile(new File(this.dataFilePath + System.getProperty("file.separator") + soundFile));

        return sound;
    }

    public List<Sound> getSounds() throws IOException, ClassNotFoundException {
        ArrayList<Sound> sounds = new ArrayList<>();

        File[] soundFiles = getSoundFiles();

        for (File soundFile : soundFiles) {
            Sound sound = new Sound();
            sound.setSoundFile(soundFile);

            sounds.add(sound);
        }

        return sounds;
    }

    public String createSound(Sound sound) throws IOException {
        // The file will need to have been written to disk already, we only have a reference to it now
        // TODO given that this is now a File, we should be able to write it out here instead, it's better to do in here

        return sound.getSoundFile().getName();
    }

    public void playSound(String name) {
        try {
            Clip sound = AudioSystem.getClip();

            sound.open(AudioSystem.getAudioInputStream(new File(this.dataFilePath + System.getProperty("file.separator") + name)));

            sound.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
