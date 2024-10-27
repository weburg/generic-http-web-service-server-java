package com.weburg.services;

import com.weburg.domain.Engine;
import com.weburg.domain.Photo;
import com.weburg.domain.Sound;

import java.io.IOException;
import java.util.List;

public interface HttpWebService {
    Engine getEngines(int id) throws IOException, ClassNotFoundException;

    List<Engine> getEngines() throws IOException, ClassNotFoundException;

    int createEngines(Engine engine) throws IOException;

    int createOrReplaceEngines(Engine engine) throws IOException;

    void updateEngines(Engine engine) throws IOException;

    void deleteEngines(int id) throws IOException;

    void restartEngines(int id) throws IOException, ClassNotFoundException;

    void stopEngines(int id) throws IOException, ClassNotFoundException;

    Photo getPhotos(String name) throws IOException, ClassNotFoundException;

    List<Photo> getPhotos() throws IOException, ClassNotFoundException;

    String createPhotos(Photo photo) throws IOException;

    Sound getSounds(String name) throws IOException, ClassNotFoundException;

    List<Sound> getSounds() throws IOException, ClassNotFoundException;

    String createSounds(Sound sound) throws IOException;

    void playSounds(String name);

    void lightKeyboards();
}
