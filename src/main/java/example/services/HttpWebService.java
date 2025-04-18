package example.services;

import example.domain.Engine;
import example.domain.Photo;
import example.domain.Sound;
import example.domain.Truck;

import java.util.List;

public interface HttpWebService {
    Engine getEngines(int id);

    List<Engine> getEngines();

    int createEngines(Engine engine);

    int createOrReplaceEngines(Engine engine);

    void updateEngines(Engine engine);

    void deleteEngines(int id);

    void restartEngines(int id);

    void stopEngines(int id);

    Photo getPhotos(String name);

    List<Photo> getPhotos();

    String createPhotos(Photo photo);

    Sound getSounds(String name);

    List<Sound> getSounds();

    String createSounds(Sound sound);

    void playSounds(String name);

    int raceTrucks(Truck truck1, Truck truck2);
}