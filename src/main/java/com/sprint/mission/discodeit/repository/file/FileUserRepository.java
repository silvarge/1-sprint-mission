package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;

public class FileUserRepository implements UserRepository {
    private final Path directory;

    public FileUserRepository(Path directory) {
        this.directory = directory;
        init(directory);
    }

    @Override
    public void init(Path directory) {
        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                throw new RuntimeException("Failed to create directory: " + directory, e);
            }
        }
    }

    @Override
    public void saveUser(Long id, User user) {
        Path filePath = directory.resolve(id + ".ser");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
            oos.writeObject(user);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save user data: " + id, e);
        }

    }

    @Override
    public User loadUser(Long id) {
        Path filePath = directory.resolve(id + ".ser");
        if (!Files.exists(filePath)) {
            throw new RuntimeException("User data not found for ID: " + id);
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath.toFile()))) {
            return (User) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to load user data: " + id, e);
        }
    }

    @Override
    public Map<Long, User> loadAllUsers() {
        try {
            return Files.list(directory)
                    .filter(Files::isRegularFile)
                    .map(path -> {
                        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                            User user = (User) ois.readObject();
                            Long id = Long.valueOf(path.getFileName().toString().replace(".ser", ""));
                            return Map.entry(id, user);
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException("Failed to load user data from file: " + path, e);
                        }
                    })
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load users from directory", e);
        }
    }

    @Override
    public void deleteUser(Long id) {
        try {
            Files.deleteIfExists(directory.resolve(id + ".ser"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long getNextId() {
        try {
            return Files.list(directory).count() + 1;
        } catch (IOException e) {
            throw new RuntimeException("Failed to calculate next ID", e);
        }
    }

}
