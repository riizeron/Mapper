package mapper.models;

import mapper.utils.ObjectConverter;
import ru.hse.homework4.interfaces.Mapper;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Mapa implements Mapper {

    @Override
    public <T> T readFromString(Class<T> clazz, String input) {
        return ObjectConverter.stringToExportedClass(clazz, input);
    }

    @Override
    public <T> T read(Class<T> clazz, InputStream inputStream) throws IOException {
        return readFromString(clazz, new String(inputStream.readAllBytes(), StandardCharsets.UTF_8));
    }

    @Override
    public <T> T read(Class<T> clazz, File file) throws IOException {
        return read(clazz, new FileInputStream(file));
    }

    @Override
    public String writeToString(Object object) {
        return ObjectConverter.exportedClassToString(object);
    }

    @Override
    public void write(Object object, OutputStream outputStream) throws IOException {
        outputStream.write(writeToString(object).getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void write(Object object, File file) throws IOException {
        write(object, new FileOutputStream(file));
    }
}
