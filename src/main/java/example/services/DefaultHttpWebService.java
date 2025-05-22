package example.services;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.weburg.ghowst.NotFoundException;
import example.ScratchLogitechSimple;
import example.SupportedMimeTypes;
import example.domain.*;
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

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static example.ScratchImageDisplay.scratchImageDisplay;
import static org.apache.tika.metadata.TikaCoreProperties.RESOURCE_NAME_KEY;

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
            int engineId = Integer.parseInt(engineFile.getName().substring(engineFile.getName().indexOf("_") + 1,
                    engineFile.getName().indexOf(".ser")));
            if (engineId > maxEngineId) {
                maxEngineId = engineId;
            }
        }

        this.lastEngineId = maxEngineId;
    }

    private File[] getSoundFiles() {
        File directory = new File(this.dataFilePath);

        File soundFiles[] = directory.listFiles(
                (dir, name) -> {
                    if (SupportedMimeTypes.getExtensions(SupportedMimeTypes.MimeTypes.AUDIO).contains(FilenameUtils.getExtension(name.toLowerCase()))) {
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

        File soundFile = new File(this.dataFilePath + System.getProperty("file.separator") + name);

        if (!soundFile.exists() || !soundFile.isFile()) {
            throw new NotFoundException("Sound \"" + name + "\" not found.");
        }

        sound.setSoundFile(soundFile);

        return sound;
    }

    public List<Sound> getSounds() {
        ArrayList<Sound> sounds = new ArrayList<>();

        File[] soundFiles = getSoundFiles();

        for (File soundFile : soundFiles) {
            Sound sound = new Sound();
            sound.setSoundFile(soundFile);

            sounds.add(sound);
        }

        return sounds;
    }

    public String createSounds(Sound sound) {
        sound.setSoundFile(new File(this.dataFilePath + System.getProperty("file.separator") + sound.getSoundFile().getName()));

        try {
            String mimeType = Files.probeContentType(Path.of(sound.getSoundFile().getAbsolutePath()));

            if (!SupportedMimeTypes.isSupportedMimeType(SupportedMimeTypes.MimeTypes.AUDIO, mimeType)) {
                throw new IllegalArgumentException("Unsupported mime type for file \"" + sound.getSoundFile().getName() + "\": " + mimeType);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String caption = "";

        if (!sound.getCaption().isEmpty()) {
            caption = sound.getCaption();
        } else {
            caption = getCaptionFromMediaFile(sound.getSoundFile(), SupportedMimeTypes.MimeTypes.AUDIO);
        }

        if (!caption.isEmpty()) {
            try {
                // Write caption
                FileOutputStream fos = new FileOutputStream(this.dataFilePath + System.getProperty("file.separator")
                        + sound.getSoundFile().getName() + ".txt");
                fos.write(caption.getBytes());
                fos.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return sound.getSoundFile().getName();
    }

    public void playSounds(String name) {
        File soundFile = getSounds(name).getSoundFile();

        try {
            Clip sound = AudioSystem.getClip();

            sound.open(AudioSystem.getAudioInputStream(soundFile));

            sound.start();
        } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private File[] getImageFiles() {
        File directory = new File(this.dataFilePath);

        File imageFiles[] = directory.listFiles(
                (dir, name) -> {
                    if (SupportedMimeTypes.getExtensions(SupportedMimeTypes.MimeTypes.IMAGE).contains(FilenameUtils.getExtension(name.toLowerCase()))) {
                        return true;
                    } else {
                        return false;
                    }
                }
        );

        return imageFiles;
    }

    public Image getImages(String name) {
        Image image = new Image();

        File imageFile = new File(this.dataFilePath + System.getProperty("file.separator") + name);
        if (!imageFile.exists() || !imageFile.isFile()) {
            throw new NotFoundException("Image \"" + name + "\" not found.");
        }

        image.setImageFile(imageFile);

        return image;
    }

    public List<Image> getImages() {
        ArrayList<Image> images = new ArrayList<>();

        File[] imageFiles = getImageFiles();

        for (File imageFile : imageFiles) {
            Image image = new Image();
            image.setImageFile(imageFile);

            images.add(image);
        }

        return images;
    }

    public String createImages(Image image) {
        image.setImageFile(new File(this.dataFilePath + System.getProperty("file.separator") + image.getImageFile().getName()));

        /* The Web server implementation determines whether the image's file is
        written by now or not, or after this call (assuming no exceptions are
        thrown from here). There is currently no way to read the file without
        having already written it to disk in the server's normal http file
        upload location. Ideally, the file would be written in a holding
        location, checked in here, and then moved by the service method. For
        now, this setup works and is simple, but lacks transactional integrity.
         */

        try {
            String mimeType = Files.probeContentType(Path.of(image.getImageFile().getAbsolutePath()));

            if (!SupportedMimeTypes.isSupportedMimeType(SupportedMimeTypes.MimeTypes.IMAGE, mimeType)) {
                throw new IllegalArgumentException("Unsupported mime type for file \"" + image.getImageFile().getName() + "\": " + mimeType);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String caption = "";

        if (!image.getCaption().isEmpty()) {
            caption = image.getCaption();
        } else {
            caption = getCaptionFromMediaFile(image.getImageFile(), SupportedMimeTypes.MimeTypes.IMAGE);
        }

        if (!caption.isEmpty()) {
            try {
                // Write caption
                FileOutputStream fos = new FileOutputStream(this.dataFilePath + System.getProperty("file.separator")
                        + image.getImageFile().getName() + ".txt");
                fos.write(caption.getBytes());
                fos.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return image.getImageFile().getName();
    }

    public void displayImages(String name) {
        Image image = new Image();
        image.setImageFile(new File(this.dataFilePath + System.getProperty("file.separator") + name));

        try {
            scratchImageDisplay(image);
        } catch (IOException e) {
            throw new NotFoundException("Image \"" + name + "\" not found.");
        }
    }

    private File[] getVideoFiles() {
        File directory = new File(this.dataFilePath);

        File videoFiles[] = directory.listFiles(
                (dir, name) -> {
                    if (SupportedMimeTypes.getExtensions(SupportedMimeTypes.MimeTypes.VIDEO).contains(FilenameUtils.getExtension(name.toLowerCase()))) {
                        return true;
                    } else {
                        return false;
                    }
                }
        );

        return videoFiles;
    }

    public Video getVideos(String name) {
        Video video = new Video();

        File videoFile = new File(this.dataFilePath + System.getProperty("file.separator") + name);
        if (!videoFile.exists() || !videoFile.isFile()) {
            throw new NotFoundException("Video \"" + name + "\" not found.");
        }

        video.setVideoFile(videoFile);

        return video;
    }

    public List<Video> getVideos() {
        ArrayList<Video> videos = new ArrayList<>();

        File[] videoFiles = getVideoFiles();

        for (File videoFile : videoFiles) {
            Video video = new Video();
            video.setVideoFile(videoFile);

            videos.add(video);
        }

        return videos;
    }

    public String createVideos(Video video) {
        video.setVideoFile(new File(this.dataFilePath + System.getProperty("file.separator") + video.getVideoFile().getName()));

         try {
            String mimeType = Files.probeContentType(Path.of(video.getVideoFile().getAbsolutePath()));

            if (!SupportedMimeTypes.isSupportedMimeType(SupportedMimeTypes.MimeTypes.VIDEO, mimeType)) {
                throw new IllegalArgumentException("Unsupported mime type for file \"" + video.getVideoFile().getName() + "\": " + mimeType);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String caption = "";

        if (!video.getCaption().isEmpty()) {
            caption = video.getCaption();
        } else {
            caption = getCaptionFromMediaFile(video.getVideoFile(), SupportedMimeTypes.MimeTypes.VIDEO);
        }

        if (!caption.isEmpty()) {
            try {
                // Write caption
                FileOutputStream fos = new FileOutputStream(this.dataFilePath + System.getProperty("file.separator")
                        + video.getVideoFile().getName() + ".txt");
                fos.write(caption.getBytes());
                fos.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return video.getVideoFile().getName();
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

    public void lightKeyboards(String color) {

        color = color.replace("#", "");

        int red = Integer.valueOf(color.substring(0, 2), 16);
        int green = Integer.valueOf(color.substring(2, 4), 16);
        int blue = Integer.valueOf(color.substring(4, 6), 16);

        try {
            ScratchLogitechSimple.setColor(red, green, blue);

            //ScratchLogitechSimple.main(new String[]{});
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void restoreKeyboards() {
        try {
            ScratchLogitechSimple.resetColor();

            //ScratchLogitechSimple.main(new String[]{});
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
                Metadata md = ImageMetadataReader.readMetadata(mediaFile);

                for (Directory directory : md.getDirectories()) {
                    for (Tag tag : directory.getTags()) {
                        if (tag.getTagName().equals("Title") || tag.getTagName().equals("Caption") || tag.getTagName().equals("Caption/Abstract") ||
                                tag.getTagName().equals("Description") || tag.getTagName().equals("Image Description")
                                || tag.getTagName().equals("Video Description")) {
                            return tag.getDescription().trim();
                        }
                    }
                }

                TikaConfig config = TikaConfig.getDefaultConfig();
                Detector detector = config.getDetector();

                TikaInputStream stream = TikaInputStream.get(mediaFile);

                org.apache.tika.metadata.Metadata metadata = new org.apache.tika.metadata.Metadata();
                metadata.add(RESOURCE_NAME_KEY, mediaFile.getName());
                MediaType mediaType = detector.detect(stream, metadata);

                if (mediaType.toString().equals("video/mp4") || mediaType.toString().equals("video/quicktime")) {
                    BodyContentHandler handler = new BodyContentHandler();
                    FileInputStream inputstream = new FileInputStream(mediaFile);
                    ParseContext pcontext = new ParseContext();

                    MP4Parser MP4Parser = new MP4Parser();
                    MP4Parser.parse(inputstream, handler, metadata, pcontext);
                    inputstream.close();

                    String title = metadata.get("dc:title");
                    if (title != null && !title.isEmpty()) {
                        return title.trim();
                    }
                }

                stream.close();
            } catch (Exception e) {
                // It was worth a shot, let it be blank
            }
        }

        return caption;
    }

    static public void main(String[] args) {
        final String dataPath = System.getProperty("user.home") + System.getProperty("file.separator") + ".HttpWebService";

        HttpWebService ws = new DefaultHttpWebService(dataPath);
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