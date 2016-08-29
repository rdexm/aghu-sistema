package br.gov.mec.aghu.model;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Table(name = "RHFP0283", schema = "AGH")
public class Rhfp0283 extends BaseEntityId<Rhfp0283Id> implements java.io.Serializable {

	/**
	 * #42229 Incluido Starh 
	 * 
	 */
	private static final long serialVersionUID = -2402084174193876728L;
	private Rhfp0283Id id;
	private Date dataFim;
	private Date dataVencimento;
	private Byte observacoes;
	private String nroCarteira;


	public Rhfp0283() {

	}
	public Rhfp0283(Rhfp0283Id id) {
		this.id = id;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "codContrato", column = @Column(name = "COD_CONTRATO", nullable = false)),
			@AttributeOverride(name = "codPlanoSaude", column = @Column(name = "COD_PLANO_SAUDE", nullable = false)),
			@AttributeOverride(name = "dataInicio", column = @Column(name = "DATA_INICIO", nullable = false)),})
	public Rhfp0283Id getId() {
		return this.id;
	}
	public void setId(Rhfp0283Id id) {
		this.id = id;
	}

	public enum Fields {

		COD_CONTRATO("id.codContrato"), 
		COD_PLANO_SAUDE("id.codPlanoSaude"), 
		DATA_INICIO("id.dataInicio"), 
		DATA_FIM("dataFim"),
		NRO_CARTEIRA("nroCarteira");

		public String getFields() {
			return fields;
		}

		public void setFields(String fields) {
			this.fields = fields;
		}

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_FIM", length = 7)
	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	@Column(name = "NUM_CARTEIRA")
	public String getNroCarteira() {
		return nroCarteira;
	}

	public void setNroCarteira(String nroCarteira) {
		this.nroCarteira = nroCarteira;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_VENCIMENTO", length = 7)
	public Date getDataVencimento() {
		return dataVencimento;
	}

	public void setDataVencimento(Date dataVencimento) {
		this.dataVencimento = dataVencimento;
	}
	@Column(name = "OBSERVACOES")
	public Byte getObservacoes() {
		return observacoes;
	}

	public void setObservacoes(Byte observacoes) {
		this.observacoes = observacoes;
	}
}
