package com.weburg.services;

import com.weburg.domain.Engine;
import com.weburg.domain.Photo;

import java.io.IOException;
import java.util.List;

public interface HttpWebService {
	Engine getEngine(int id) throws IOException, ClassNotFoundException;

	List<Engine> getEngines() throws IOException, ClassNotFoundException;

	int createEngine(Engine engine) throws IOException;

	int createOrReplaceEngine(Engine engine) throws IOException;

	void updateEngine(Engine engine) throws IOException;

	void deleteEngine(int id) throws IOException;

	void restartEngine(int id) throws IOException, ClassNotFoundException;

	void stopEngine(int id) throws IOException, ClassNotFoundException;

	Photo getPhoto(String photoFile) throws IOException, ClassNotFoundException;

	List<Photo> getPhotos() throws IOException, ClassNotFoundException;

	String createPhoto(Photo photo) throws IOException;

	void playSound(String name);
}
