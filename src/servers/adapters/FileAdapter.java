package servers.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.IOException;

public class FileAdapter extends TypeAdapter<File> {

    @Override
    public void write(final JsonWriter jsonWriter, final File file) throws IOException {
        if (file == null) {
            jsonWriter.nullValue();
        } else {
            jsonWriter.value(file.getPath());
        }
    }

    @Override
    public File read(final JsonReader jsonReader) throws IOException {
        if (jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull();
            return null;
        } else {
            return new File(jsonReader.nextString());
        }
    }
}