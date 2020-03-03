package app.datamodel;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.bson.BsonReader;
import org.bson.BsonReaderMark;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.ObjectId;

public class ConfigCodec implements Codec<Config>
{

	@Override
	public void encode(BsonWriter writer, Config value, EncoderContext encoderContext)
	{
		writer.writeStartDocument();
		writer.writeString("market", value.getMarket());
		writer.writeBoolean("inverseCross", value.isInverseCross());
		writer.writeInt32("granularity", value.getGranularity());
		writer.writeDateTime("startTime", value.getStartTime().toEpochMilli());
		writer.writeDateTime("endTime", value.getEndTime().toEpochMilli());
		for (Map.Entry<String, Object> entry: value.getParameters().entrySet()) {
			Object parValue = entry.getValue();
			String name = entry.getKey();
			if (parValue == null) {
				writer.writeNull(name);
			} else if (parValue instanceof String) {
				writer.writeString(name, (String)parValue);
			} else if (parValue instanceof Integer) {
				writer.writeInt32(name, (int)parValue);
			} else if (parValue instanceof Long) {
				writer.writeInt64(name, (long)parValue);
			} else if (parValue instanceof Double) {
				writer.writeDouble(name, (double)parValue);
			} else if (parValue instanceof Boolean) {
				writer.writeBoolean(name, (boolean)parValue);
			} else if (parValue instanceof Instant) {
				writer.writeDateTime(name, ((Instant)parValue).toEpochMilli());
			} else if (parValue instanceof ObjectId) {
				writer.writeObjectId(name, (ObjectId)parValue);
			} else {
				throw new UnsupportedOperationException();
			}
		}
		writer.writeEndDocument();
	}

	@Override
	public Class<Config> getEncoderClass()
	{
		return Config.class;
	}

	@Override
	public Config decode(BsonReader reader, DecoderContext decoderContext)
	{
		BsonReaderMark initialState = reader.getMark();
		Config config = new Config(reader.readString("market"), reader.readBoolean("inverseCross"), reader.readInt32("granularity"), Instant.ofEpochMilli(reader.readDateTime("startTime")), Instant.ofEpochMilli(reader.readDateTime("endTime")));
		
		initialState.reset();
		reader.readStartDocument();

		while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
			String fieldName = reader.readName();
			Object fieldValue = null;
			switch (reader.getCurrentBsonType()) {
			case INT32:
				fieldValue = reader.readInt32();
				break;
			case INT64:
				fieldValue = reader.readInt64();
				break;
			case ARRAY:
				break;
			case BINARY:
				break;
			case BOOLEAN:
				fieldValue = reader.readBoolean();
				break;
			case DATE_TIME:
				fieldValue = Instant.ofEpochMilli(reader.readDateTime());
				break;
			case DB_POINTER:
				break;
			case DECIMAL128:
				break;
			case DOCUMENT:
				break;
			case DOUBLE:
				fieldValue = reader.readDouble();
				break;
			case END_OF_DOCUMENT:
				break;
			case JAVASCRIPT:
				break;
			case JAVASCRIPT_WITH_SCOPE:
				break;
			case MAX_KEY:
				break;
			case MIN_KEY:
				break;
			case NULL:
				fieldValue = null;
				break;
			case OBJECT_ID:
				fieldValue = reader.readObjectId();
				break;
			case REGULAR_EXPRESSION:
				break;
			case STRING:
				fieldValue = reader.readString();
				break;
			case SYMBOL:
				break;
			case TIMESTAMP:
				break;
			case UNDEFINED:
				break;
			default:
			}
			if (fieldName.equals("market") || fieldName.equals("inverseCross") || fieldName.equals("granularity") || fieldName.equals("startTime") || fieldName.equals("endTime"))
				continue;
			config.setParameter(fieldName, fieldValue);
		}

		reader.readEndDocument();
		
		return config;
	}

}
