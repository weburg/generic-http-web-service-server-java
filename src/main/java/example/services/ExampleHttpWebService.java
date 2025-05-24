package example.services;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Tag;
import com.weburg.ghowst.NotFoundException;
import example.SupportedMimeTypes;
import example.domain.*;
import javafx.application.Platform;
import javafx.scene.media.AudioClip;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.Detector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp4.MP4Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.TagTextField;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.logging.Logger;

import static org.apache.tika.metadata.TikaCoreProperties.RESOURCE_NAME_KEY;

public class ExampleHttpWebService implements ExampleService {
    int lastEngineId = 0;
    String dataFilePath;

    private static final Logger LOGGER = Logger.getLogger(ExampleHttpWebService.class.getName());

    public ExampleHttpWebService(String dataFilePath) {
        this.dataFilePath = dataFilePath;

        // Set the last engine ID based on existing files

        File[] engineFiles = getEngineFiles();

        int maxEngineId = 0;

        for (File engineFile : engineFiles) {
            int engineId = Integer.parseInt(engineFile.getName().substring(engineFile.getName().indexOf("_") + 1,
                    engineFile.getName().indexOf(".ser")));
            if (engineId > maxEngineId) {
                maxEngineId = engineId;
            }
        }

        this.lastEngineId = maxEngineId;

        // For playing sounds on the server, start the JavaFX toolkit
        Platform.startup(new Runnable() {
            @Override
            public void run() {
                // Run, Forrest, run
            }
        });
    }

    public Sound getSounds(String name) {
        return getMultimedias(name, Sound.class);
    }

    public List<Sound> getSounds() {
        return getMultimedias(Sound.class);
    }

    public String createSounds(Sound sound) {
        return createMultimedias(sound);
    }

    public void playSounds(String name) {
        File soundFile = getSounds(name).getSoundFile();

        try {
            AudioClip clip = new AudioClip(soundFile.toURI().toURL().toString());
            clip.play();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public Image getImages(String name) {
        return getMultimedias(name, Image.class);
    }

    public List<Image> getImages() {
        return getMultimedias(Image.class);
    }

    public String createImages(Image image) {
        return createMultimedias(image);
    }

    public Video getVideos(String name) {
        return getMultimedias(name, Video.class);
    }

    public List<Video> getVideos() {
        return getMultimedias(Video.class);
    }

    public String createVideos(Video video) {
        return createMultimedias(video);
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
        } catch (FileNotFoundException e) {
            throw new NotFoundException("Engine with id \"" + id + "\" not found.");
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Server problem, could not get Engine.");
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

            engines.sort((o1, o2) -> {
                if (o1.getId() < o2.getId()) {
                    return -1;
                } else if (o1.getId() > o2.getId()) {
                    return 1;
                } else {
                    return 0;
                }
            });

            return engines;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Server problem, could not get Engines.");
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
        } catch (IOException e) {
            throw new RuntimeException("Server problem, Engine not created.");
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
        } catch (FileNotFoundException e) {
            throw new NotFoundException("Engine with id \"" + engine.getId() + "\" not found.");
        } catch (IOException e) {
            throw new RuntimeException("Server problem, Engine with id \"" + engine.getId() + "\" not processed.");
        }
    }

    public int updateEngines(Engine engine) {
        if (engine.getId() == 0) {
            throw new IllegalArgumentException("For update operations, the id must be specified.");
        }

        return createOrReplaceEngines(engine); // For simple persistence, reuse other method since replacement happens anyway
    }

    public void deleteEngines(int id) {
        File file = new File(this.dataFilePath + System.getProperty("file.separator") + "Engine_" + id + ".ser");
        boolean deleted = file.delete();

        if (!deleted) {
            throw new RuntimeException("Could not delete " + file.getName());
        }
    }

    public int restartEngines(int id) {
        Engine engine = getEngines(id);

        if (!engine.getName().endsWith("Restarted")) {
            engine.setName(engine.getName() + "Restarted");
            id = updateEngines(engine);
        }

        return id;
    }

    public int stopEngines(int id) {
        Engine engine = getEngines(id);

        if (engine.getName().endsWith("Restarted")) {
            engine.setName(engine.getName().substring(0, engine.getName().length() - "Restarted".length()));
            id = updateEngines(engine);
        }

        return id;
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

    public String raceTrucks(Truck truck1, Truck truck2) {
        // This method is just to demo and test parsing two objects in same call.

        Engine engine1 = getEngines(truck1.getEngineId());
        Engine engine2 = getEngines(truck2.getEngineId());

        LOGGER.info("Got trucks: " + truck1.getName() + " and " + truck2.getName());
        LOGGER.info("Engines: " + getEngines(truck1.getEngineId()).getName() + " and " + getEngines(truck2.getEngineId()).getName());

        StringBuilder sb = new StringBuilder();
        sb.append("The ").append(truck1.getName()).append(" and the ").append(truck2.getName()).append(" trucks race! ");

        if (engine1.getCylinders() > engine2.getCylinders() && engine1.getThrottleSetting() > engine2.getThrottleSetting()) {
            sb.append("The ").append(truck1.getName()).append(" wins!");
        } else if (engine2.getCylinders() > engine1.getCylinders() && engine2.getThrottleSetting() > engine1.getThrottleSetting()) {
            sb.append("The ").append(truck2.getName()).append(" wins!");
        } else {
            sb.append("The race is too close to call! It's a virtual tie.");
        }

        return sb.toString();
    }

    private <T extends Multimedia> File[] getMultimediaFiles(Class<T> clazz) {
        File directory = new File(this.dataFilePath);

        File mediaFiles[] = directory.listFiles(
                (dir, name) -> {
                    try {
                        if (SupportedMimeTypes.getExtensions(clazz.getDeclaredConstructor().newInstance().getMimeType()).contains(FilenameUtils.getExtension(name.toLowerCase()))) {
                            return true;
                        } else {
                            return false;
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        );

        return mediaFiles;
    }

    private <T extends Multimedia> T getMultimedias(String name, Class<T> clazz) {
        try {
            T multimedia = clazz.getDeclaredConstructor().newInstance();

            File mediaFile = new File(this.dataFilePath + System.getProperty("file.separator") + name);
            if (!mediaFile.exists() || !mediaFile.isFile()) {
                throw new NotFoundException(clazz.getSimpleName() + " \"" + name + "\" not found.");
            }

            multimedia.setMediaFile(mediaFile);

            return multimedia;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private <T extends Multimedia> List<T> getMultimedias(Class<T> clazz) {
        ArrayList<T> multimedias = new ArrayList<>();

        File[] multimediaFiles = getMultimediaFiles(clazz);

        for (File multimediaFile : multimediaFiles) {
            try {
                T multimedia = clazz.getDeclaredConstructor().newInstance();

                multimedia.setMediaFile(multimediaFile);

                multimedias.add(multimedia);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return multimedias;
    }

    private <T extends Multimedia> String createMultimedias(T multimedia) {
         try {
            String mimeType = Files.probeContentType(Path.of(multimedia.getMediaFile().getAbsolutePath()));

            if (!SupportedMimeTypes.isSupportedMimeType(multimedia.getMimeType(), mimeType)) {
                throw new IllegalArgumentException("Unsupported mime type for file \"" + multimedia.getMediaFile().getName() + "\": " + mimeType);
            } else if (!SupportedMimeTypes.isSupportedExtension(multimedia.getMimeType(), multimedia.getMediaFile().getName())) {
                throw new IllegalArgumentException("Mime type for file is supported but the extension is not for the file \"" + multimedia.getMediaFile().getName());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String caption = "";

        if (!multimedia.getCaption().isEmpty()) {
            caption = multimedia.getCaption();
        } else {
            caption = getCaptionFromMediaFile(multimedia.getMediaFile(), multimedia.getMimeType());
        }

        File captionFile = new File(this.dataFilePath + System.getProperty("file.separator") + multimedia.getMediaFile().getName() + ".txt");
        if (!caption.isEmpty()) {
            try {
                // Write caption
                FileOutputStream fos = new FileOutputStream(captionFile);
                fos.write(caption.getBytes());
                fos.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        try {
            File finalMediaFile = new File(this.dataFilePath + System.getProperty("file.separator") + multimedia.getMediaFile().getName());
            Files.copy(multimedia.getMediaFile().toPath(), finalMediaFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            multimedia.setMediaFile(finalMediaFile);
        } catch (IOException e) {
            try {
                Files.delete(captionFile.toPath());
            } catch (IOException e2) {
                LOGGER.severe("Could not clean up caption file \"" + captionFile.getName() + "\".");
            }
            throw new RuntimeException(e);
        }

        return multimedia.getMediaFile().getName();
    }

    private static String getCaptionFromMediaFile(File mediaFile, SupportedMimeTypes.MimeTypes mimeType) {
        String caption = "";

        if (mimeType.equals(SupportedMimeTypes.MimeTypes.AUDIO)) {
            try {
                AudioFile f = AudioFileIO.read(mediaFile);
                org.jaudiotagger.tag.Tag tag = f.getTag();
                TagTextField tf = (TagTextField) tag.getFirstField(FieldKey.TITLE);
                caption = tf.getContent().trim();
            } catch (Exception e) {
                // It was worth a shot, let it be blank
            }
        } else if (mimeType.equals(SupportedMimeTypes.MimeTypes.IMAGE) || mimeType.equals(SupportedMimeTypes.MimeTypes.VIDEO)) {
            try {
                com.drew.metadata.Metadata md = ImageMetadataReader.readMetadata(mediaFile);

                for (Directory directory : md.getDirectories()) {
                    for (Tag tag : directory.getTags()) {
                        LOGGER.info("Tag \"" + tag.getTagName() + "\" found: " + tag.getDescription().trim());
                        if (tag.getTagName().equals("Caption")
                                || tag.getTagName().equals("Caption/Abstract")
                                || tag.getTagName().equals("Description")
                                || tag.getTagName().equals("Image Description")
                                || tag.getTagName().equals("Video Description")
                                || tag.getTagName().equals("Object Name")
                                || tag.getTagName().equals("Title")
                                || tag.getTagName().equals("Windows XP Title")) {
                            LOGGER.info("Media description resolved by jaudiotagger with tag \"" + tag.getTagName() + "\": " + tag.getDescription().trim());
                            return tag.getDescription().trim();
                        }
                    }
                }

                TikaConfig config = TikaConfig.getDefaultConfig();
                Detector detector = config.getDetector();

                TikaInputStream stream = TikaInputStream.get(mediaFile);
                FileInputStream inputstream = new FileInputStream(mediaFile);
                try {
                    org.apache.tika.metadata.Metadata metadata = new org.apache.tika.metadata.Metadata();
                    metadata.add(RESOURCE_NAME_KEY, mediaFile.getName());
                    MediaType mediaType = detector.detect(stream, metadata);

                    if (mediaType.toString().equals("video/mp4") || mediaType.toString().equals("video/quicktime")) {
                        BodyContentHandler handler = new BodyContentHandler();

                        ParseContext pcontext = new ParseContext();
                        MP4Parser MP4Parser = new MP4Parser();
                        MP4Parser.parse(inputstream, handler, metadata, pcontext);

                        List<String> tags = Arrays.asList("dc:description", "dc:title");
                        for (String tag : tags) {
                            String title = metadata.get(tag);
                            if (title != null && !title.isEmpty()) {
                                LOGGER.info("Media description resolved by tika with tag \"" + tag + "\": " + title.trim());
                                return title.trim();
                            }
                        }
                    }
                } finally {
                    stream.close();
                    inputstream.close();
                }
            } catch (Exception e) {
                // It was worth a shot, let it be blank
            }
        }

        return caption;
    }

    static public void main(String[] args) {
        final String dataPath = System.getProperty("user.home") + System.getProperty("file.separator") + ".HttpWebService";

        ExampleService ws = new ExampleHttpWebService(dataPath);
        Map<String, String> results = new LinkedHashMap<>();

        List<Sound> sounds = ws.getSounds();
        for (Sound sound : sounds) {
            String caption = getCaptionFromMediaFile(sound.getSoundFile(), SupportedMimeTypes.MimeTypes.AUDIO);

            if (!caption.isEmpty()) {
                results.put(sound.getSoundFile().getName(), caption);
            }
        }

        List<Image> images = ws.getImages();
        for (Image image : images) {
            String caption = getCaptionFromMediaFile(image.getImageFile(), SupportedMimeTypes.MimeTypes.IMAGE);

            if (!caption.isEmpty()) {
                results.put(image.getImageFile().getName(), caption);
            }
        }

        List<Video> videos = ws.getVideos();
        for (Video video : videos) {
            String caption = getCaptionFromMediaFile(video.getVideoFile(), SupportedMimeTypes.MimeTypes.VIDEO);

            if (!caption.isEmpty()) {
                results.put(video.getVideoFile().getName(), caption);
            }
        }

        System.out.println("Results");

        for (String fileName : results.keySet()) {
            System.out.println(fileName + ": " + results.get(fileName));

            try {
                FileOutputStream fos = new FileOutputStream(dataPath + System.getProperty("file.separator")
                        + fileName + ".txt");
                fos.write(results.get(fileName).getBytes());
                fos.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}