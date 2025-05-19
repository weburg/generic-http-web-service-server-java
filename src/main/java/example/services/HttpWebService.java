package example.services;

import com.weburg.ghowst.DescriptionInOut;
import example.domain.Engine;
import example.domain.Photo;
import example.domain.Sound;
import example.domain.Truck;
import jdk.jfr.Description;
import jdk.jfr.Name;

import java.util.List;

@Name("Generic HTTP Web Service")
@Description("An example service showing the power of GHoWSt")
public interface HttpWebService {
    @Description("Gets a list of Engines based on the id")
    Engine getEngines(int id);

    List<Engine> getEngines();

    @Description("Create a new engine")
    @DescriptionInOut("The id of the created engine") int createEngines(@DescriptionInOut("The engine to create") Engine engine);

    int createOrReplaceEngines(Engine engine);

    int updateEngines(Engine engine);

    void deleteEngines(int id);

    int restartEngines(int id);

    int stopEngines(int id);

    Photo getPhotos(String name);

    List<Photo> getPhotos();

    String createPhotos(Photo photo);

    void displayPhotos(String name);

    Sound getSounds(String name);

    List<Sound> getSounds();

    String createSounds(Sound sound);

    void playSounds(String name);

    String raceTrucks(Truck truck1, Truck truck2);

    void lightKeyboards(String color);

    void restoreKeyboards();
}