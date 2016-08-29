package br.gov.mec.aghu.model;

import org.apache.lucene.document.Document;
import org.hibernate.search.bridge.LuceneOptions;
import org.hibernate.search.bridge.TwoWayFieldBridge;

/**
 * Implementacao de org.hibernate.search.bridge.TwoWayFieldBridge para ser usada
 * como um FieldBridge na classe AipSinonimosOcupacaoId.
 * 
 * @author rafael.saraiva
 * 
 */
public class AipSinonimosOcupacaoIdBridge implements TwoWayFieldBridge {

	public Object get(String name, Document document) {
		AipSinonimosOcupacaoId id = new AipSinonimosOcupacaoId();
		String field = document.get(name + ".ocpCodigo");
		id.setOcpCodigo(Integer.valueOf(field));
		field = document.get(name + ".codigo");
		if (field != null) {
			id.setCodigo(Short.valueOf(field));
		}
		return id;
	}

	public void set(String name, Object value, Document document, LuceneOptions luceneOptions) {
		AipSinonimosOcupacaoId id = (AipSinonimosOcupacaoId) value;
		luceneOptions.addNumericFieldToDocument(name + ".ocpCodigo", id.getOcpCodigo(), document);
		luceneOptions.addNumericFieldToDocument(name + ".codigo", id.getCodigo(), document);
		luceneOptions.addFieldToDocument(name, objectToString(id), document);

	}

	public String objectToString(Object object) {
		AipSinonimosOcupacaoId id = (AipSinonimosOcupacaoId) object;
		StringBuilder sb = new StringBuilder();
		sb.append(id.getOcpCodigo()).append(' ').append(id.getCodigo());
		return sb.toString();
	}

}
