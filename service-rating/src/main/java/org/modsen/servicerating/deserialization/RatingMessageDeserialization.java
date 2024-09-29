package org.modsen.servicerating.deserialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.modsen.servicerating.dto.message.RatingMessage;

public class RatingMessageDeserialization implements Deserializer<RatingMessage> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public RatingMessage deserialize(String topic, byte[] data) {
        try {
            if (data == null || data.length == 0) {
                return null;
            }
            return objectMapper.readValue(data, RatingMessage.class);
        } catch (Exception e) {
            throw new SerializationException("Ошибка при десериализации RatingMessage", e);
        }
    }
}
