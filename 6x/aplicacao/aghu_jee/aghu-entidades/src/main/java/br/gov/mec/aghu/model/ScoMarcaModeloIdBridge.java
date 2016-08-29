package br.gov.mec.aghu.model;

import org.apache.lucene.document.Document;
import org.hibernate.search.bridge.LuceneOptions;
import org.hibernate.search.bridge.TwoWayFieldBridge;

/**
 * Implementacao de org.hibernate.search.bridge.TwoWayFieldBridge para ser usada
 * como um FieldBridge na classe ScoMarcaModeloId.
 * 
 * @author rafael.saraiva
 * 
 */
public class ScoMarcaModeloIdBridge implements TwoWayFieldBridge {

	public Object get(String name, Document document) {
		ScoMarcaModeloId id = new ScoMarcaModeloId();
		String field = document.get(name + ".mcmCodigo");
		id.setMcmCodigo(Integer.valueOf(field));
		field = document.get(name + ".seqp");
		if (field != null) {
			id.setSeqp(Integer.valueOf(field));
		}
		return id;
	}

	public void set(String name, Object value, Document document, LuceneOptions luceneOptions) {
		ScoMarcaModeloId id = (ScoMarcaModeloId) value;
		luceneOptions.addNumericFieldToDocument(name + ".mcmCodigo", id.getMcmCodigo(), document);
		luceneOptions.addNumericFieldToDocument(name + ".seqp", id.getSeqp(), document);
		luceneOptions.addFieldToDocument(name, objectToString(id), document);

	}

	public String objectToString(Object object) {
		ScoMarcaModeloId id = (ScoMarcaModeloId) object;
		StringBuilder sb = new StringBuilder();
		sb.append(id.getMcmCodigo()).append(' ').append(id.getSeqp());
		return sb.toString();
	}

}
