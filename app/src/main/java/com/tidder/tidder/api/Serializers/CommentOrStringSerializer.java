package com.tidder.tidder.api.Serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.tidder.tidder.api.output.RedditCommentObject;

import java.io.IOException;

/**
 * Created by Jonah on 4.12.2014.
 */
public class CommentOrStringSerializer extends JsonSerializer<Object> {

    @Override
    public void serialize(Object tmpObj, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
        throws IOException, JsonProcessingException
        {
            if(tmpObj.toString().length() == 0)
            {
                jsonGenerator.writeObject(null);
            }
            else
            {
                jsonGenerator.writeObject(RedditCommentObject.class);
            }
        }
}
