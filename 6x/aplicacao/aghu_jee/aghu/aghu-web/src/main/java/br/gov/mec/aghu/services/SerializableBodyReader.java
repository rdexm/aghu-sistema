package br.gov.mec.aghu.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.enterprise.context.Dependent;
import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Provider
@Consumes(MediaType.APPLICATION_XML)
@Dependent
public class SerializableBodyReader implements MessageBodyReader<Serializable> {

	private static final Log LOG = LogFactory
			.getLog(SerializableBodyReader.class);

	@Override
	public boolean isReadable(Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return Serializable.class.isAssignableFrom(type);
	}

	@Override
	public Serializable readFrom(Class<Serializable> type, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException, WebApplicationException {
		ObjectInputStream objStream = new ObjectInputStream(entityStream);
		Serializable retorno = null;
		try {
			retorno = (Serializable) objStream.readObject();
		} catch (ClassNotFoundException e) {
			LOG.error(e.getMessage(), e);
		}
		return retorno;
	}

}
