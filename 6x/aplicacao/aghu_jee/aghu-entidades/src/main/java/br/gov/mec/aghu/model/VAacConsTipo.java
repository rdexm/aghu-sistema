package br.gov.mec.aghu.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * 
 * Está VIEW só possui código ANSI SQL.
 * 
 * @author THIAGO.CORTES
 * 
 */
@Entity
@Table(name = "V_AAC_CONS_TIPO", schema = "AGH")
@Immutable
public class VAacConsTipo extends BaseEntityId<VAacConsTipoId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3343221357303964116L;

	private VAacConsTipoId id;
	
	public VAacConsTipo(){
		
	}
	
	public VAacConsTipo(VAacConsTipoId id){
		this.id = id;
	}
	
	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "espSeq", column = @Column(name = "V_ESP_SEQ", nullable = false, length = 1)),
			@AttributeOverride(name = "espNome", column = @Column(name = "ESP_NOME", nullable = false, length = 45)),
			@AttributeOverride(name = "vCaaSeq", column = @Column(name = "V_CAA_SEQ", nullable = false)),
			@AttributeOverride(name = "caaDescricao", column = @Column(name = "CAA_DESCRICAO", nullable = false, length = 45)),
			@AttributeOverride(name = "vPgdSeq", column = @Column(name = "V_PGD_SEQ", nullable = false)),
			@AttributeOverride(name = "pgdDescricao", column = @Column(name = "PGD_DESCRICAO", nullable = false, length = 45)),
			@AttributeOverride(name = "vTagSeq", column = @Column(name = "V_TAG_SEQ", nullable = false)),
			@AttributeOverride(name = "tagDescricao", column = @Column(name = "TAG_DESCRICAO", nullable = false, length = 45)),
			@AttributeOverride(name = "visitIndSitCons", column = @Column(name = "VSIT_IND_SIT_CONS", nullable = false, length = 1)),
			@AttributeOverride(name = "espSigla", column = @Column(name = "ESP_SIGLA", nullable = false, length = 3))})
	public VAacConsTipoId getId() {
		return id;
	}

	@Override
	public void setId(VAacConsTipoId id) {
		this.id = id;
	}
	
	public enum Fields{
		ESP_SEQ("id.espSeq"),
		ESP_NOME("id.espNome"),
		ESP_SIGLA("id.espSigla"),
		V_CAA_SEQ("id.vCaaSeq"),
		CAA_DESCRICAO("id.caaDescricao"),
		V_PGD_SEQ("id.vPgdSeq"),
		PGD_DESCRICAO("id.pgdDescricao"),
		V_TAG_SEQ("id.vTagSeq"),
		TAG_DESCRICAO("id.tagDescricao"),
		VISIT_IND_SIT_CONS("id.visitIndSitCons");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

}