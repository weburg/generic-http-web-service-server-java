package example.services;

import example.domain.Engine;
import example.domain.Photo;
import example.domain.Sound;
import example.domain.Truck;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class DefaultHttpWebService implements HttpWebService {
    int lastEngineId = 0;
    String dataFilePath;

    private static final Logger LOGGER = Logger.getLogger(DefaultHttpWebService.class.getName());

    public DefaultHttpWebService(String dataFilePath) {
        this.dataFilePath = dataFilePath;

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

    public Engine getEngines(int id) {
        try {
            FileInputStream fis = new FileInputStream(this.dataFilePath + System.getProperty("file.separator")
                    + "Engine_" + id + ".ser");
            ObjectInputStream ois = new ObjectInputStream(fis);

            Engine engine = (Engine) ois.readObject();

            ois.close();
            fis.close();

            return engine;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Engine> getEngines() {
        ArrayList<Engine> engines = new ArrayList<>();

        File[] engineFiles = getEngineFiles();

        try {
            for (File engineFile : engineFiles) {
                FileInputStream fis = new FileInputStream(engineFile);
                ObjectInputStream ois = new ObjectInputStream(fis);

                Engine engine = (Engine) ois.readObject();

                engines.add(engine);

                ois.close();
                fis.close();
            }

            return engines;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int createEngines(Engine engine) {
        int engineId = ++this.lastEngineId;

        engine.setId(engineId);

        try {
            FileOutputStream fos = new FileOutputStream(this.dataFilePath + System.getProperty("file.separator")
                    + "Engine_" + engineId + ".ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(engine);

            oos.close();
            fos.close();

            return engineId;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int createOrReplaceEngines(Engine engine) {
        if (engine.getId() == 0) {
            throw new IllegalArgumentException("For createOrReplace operations, the id must be specified.");
        }

        File file = new File(this.dataFilePath + System.getProperty("file.separator")
                + "Engine_" + engine.getId() + ".ser");

        try {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(engine);

            oos.close();
            fos.close();

            if (this.lastEngineId < engine.getId()) {
                this.lastEngineId = engine.getId();
            }

            return engine.getId();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updateEngines(Engine engine) {
        if (engine.getId() == 0) {
            throw new IllegalArgumentException("For update operations, the id must be specified.");
        }

        createOrReplaceEngines(engine); // For simple persistence, reuse other method since replacement happens anyway
    }

    public void deleteEngines(int id) {
        File file = new File(this.dataFilePath + System.getProperty("file.separator") + "Engine_" + id + ".ser");
        boolean deleted = file.delete();

        if (!deleted) {
            throw new RuntimeException("Could not delete " + file.getName());
        }
    }

    public void restartEngines(int id) {
        Engine engine = getEngines(id);

        if (!engine.getName().endsWith("Restarted")) {
            engine.setName(engine.getName() + "Restarted");
            updateEngines(engine);
        }
    }

    public void stopEngines(int id) {
        Engine engine = getEngines(id);

        if (engine.getName().endsWith("Restarted")) {
            engine.setName(engine.getName().substring(0, engine.getName().length() - "Restarted".length()));
            updateEngines(engine);
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

    public Photo getPhotos(String name) {
        Photo photo = new Photo();

        File localPhotoFile = new File(this.dataFilePath + System.getProperty("file.separator") + name);
        if (!localPhotoFile.exists() || !localPhotoFile.isFile()) {
            throw new RuntimeException("File not found");
        }

        photo.setPhotoFile(localPhotoFile);

        return photo;
    }

    public List<Photo> getPhotos() {
        ArrayList<Photo> photos = new ArrayList<>();

        File[] photoFiles = getPhotoFiles();

        for (File photoFile : photoFiles) {
            Photo photo = new Photo();
            photo.setPhotoFile(photoFile);

            photos.add(photo);
        }

        return photos;
    }

    public String createPhotos(Photo photo) {
        /* The Web server implementation determines whether the photo's file is
        written by now or not, or after this call (assuming no exceptions are
        thrown from here). There is currently no way to read the file without
        having already written it to disk in the server's normal http file
        upload location. Ideally, the file would be written in a holding
        location, checked in here, and then moved by the service method. For
        now, this setup works and is simple, but lacks checking and
        transactional integrity.
         */

        try {
            // Write caption
            FileOutputStream fos = new FileOutputStream(this.dataFilePath + System.getProperty("file.separator")
                    + photo.getPhotoFile().getName() + ".txt");
            fos.write(photo.getCaption().getBytes());
            fos.close();

            return photo.getPhotoFile().getName();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

    public Sound getSounds(String name) {
        Sound sound = new Sound();

        File localSoundFile = new File(this.dataFilePath + System.getProperty("file.separator") + name);

        if (!localSoundFile.isFile()) {
            throw new RuntimeException("File not found");
        }

        sound.setSoundFile(new File(localSoundFile.getName()));

        return sound;
    }

    public List<Sound> getSounds() {
        ArrayList<Sound> sounds = new ArrayList<>();

        File[] soundFiles = getSoundFiles();

        for (File soundFile : soundFiles) {
            Sound sound = new Sound();
            sound.setSoundFile(new File(soundFile.getName()));

            sounds.add(sound);
        }

        return sounds;
    }

    public String createSounds(Sound sound) {
        return sound.getSoundFile().getName();
    }

    public void playSounds(String name) {
        try {
            Clip sound = AudioSystem.getClip();

            sound.open(AudioSystem.getAudioInputStream(new File(this.dataFilePath + System.getProperty("file.separator") + name)));

            sound.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int raceTrucks(Truck truck1, Truck truck2) {
        /* This method is just to demo and test parsing two objects in same
        call. It just returns an int, as we want to test whether we can
        differentiate names.
         */

        LOGGER.info("Got trucks: " + truck1.getName() + " and " + truck2.getName());
        LOGGER.info("Engines: " + getEngines(truck1.getEngineId()).getName() + " and " + getEngines(truck2.getEngineId()).getName());

        return truck1.getName().compareTo(truck2.getName());
    }
}
