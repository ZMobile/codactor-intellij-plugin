package com.translator.model.codactor.data.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeTypeAdapterFactory implements TypeAdapterFactory {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_TIME;

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        if (typeToken.getRawType() != LocalDateTime.class) {
            return null;
        }

        @SuppressWarnings("unchecked")
        TypeAdapter<T> adapter = (TypeAdapter<T>) new LocalDateTimeAdapter();
        return adapter;
    }

    private class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {

        @Override
        public void write(JsonWriter out, LocalDateTime value) throws IOException {
            if (value == null) {
                out.nullValue();
                return;
            }

            out.beginObject();
            out.name("date");
            out.beginObject();
            out.name("year").value(value.getYear());
            out.name("month").value(value.getMonthValue());
            out.name("day").value(value.getDayOfMonth());
            out.endObject();
            out.name("time");
            out.beginObject();
            out.name("hour").value(value.getHour());
            out.name("minute").value(value.getMinute());
            out.name("second").value(value.getSecond());
            out.name("nano").value(value.getNano());
            out.endObject();
            out.endObject();
        }

        @Override
        public LocalDateTime read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }

            in.beginObject();
            in.nextName(); // "date"
            in.beginObject();
            int year = in.nextName().equals("year") ? in.nextInt() : 0;
            int month = in.nextName().equals("month") ? in.nextInt() : 0;
            int day = in.nextName().equals("day") ? in.nextInt() : 0;
            in.endObject();
            in.nextName(); // "time"
            in.beginObject();
            int hour = in.nextName().equals("hour") ? in.nextInt() : 0;
            int minute = in.nextName().equals("minute") ? in.nextInt() : 0;
            int second = in.nextName().equals("second") ? in.nextInt() : 0;
            int nano = in.nextName().equals("nano") ? in.nextInt() : 0;
            in.endObject();
            in.endObject();

            LocalDate date = LocalDate.of(year, month, day);
            LocalTime time = LocalTime.of(hour, minute, second, nano);
            return LocalDateTime.of(date, time);
        }
    }
}