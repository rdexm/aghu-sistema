package br.gov.mec.aghu.model;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity

@Table(name = "ABS_DOACOES", schema = "AGH")
public class AbsDoacoes extends BaseEntityId<AbsDoacoesId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3839905522379668544L;
	private AbsDoacoesId id;
	private AbsSolicitacoesDoacoes absSolicitacoesDoacoes;
	private RapServidores servidor;
	private AbsTipoDoacao tipoDoacao; 
	private AbsCandidatosDoadores cadSeq;
	private Date criadoEm;
	private Byte unidColetadas;
	private Boolean indTriagem;
	private Boolean indColeta;
	private String observacao;
	private Boolean indCandidatoAferese;

	public AbsDoacoes() {
	}

	
	
	public AbsDoacoes(AbsDoacoesId id,
			AbsSolicitacoesDoacoes absSolicitacoesDoacoes,
			RapServidores servidor, AbsTipoDoacao tipoDoacao,
			AbsCandidatosDoadores cadSeq, Date criadoEm, Byte unidColetadas,
			Boolean indTriagem, Boolean indColeta, String observacao,
			Boolean indCandidatoAferese) {
		super();
		this.id = id;
		this.absSolicitacoesDoacoes = absSolicitacoesDoacoes;
		this.servidor = servidor;
		this.tipoDoacao = tipoDoacao;
		this.cadSeq = cadSeq;
		this.criadoEm = criadoEm;
		this.unidColetadas = unidColetadas;
		this.indTriagem = indTriagem;
		this.indColeta = indColeta;
		this.observacao = observacao;
		this.indCandidatoAferese = indCandidatoAferese;
	}


	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "bolNumero", column = @Column(name = "BOL_NUMERO", nullable = false, precision = 7, scale = 0)),
			@AttributeOverride(name = "bolBsaCodigo", column = @Column(name = "BOL_BSA_CODIGO", nullable = false, precision = 3, scale = 0)),
			@AttributeOverride(name = "bolData", column = @Column(name = "BOL_DATA", nullable = false, length = 7)) })
	public AbsDoacoesId getId() {
		return this.id;
	}

	public void setId(AbsDoacoesId id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SDO_PAC_CODIGO", referencedColumnName = "PAC_CODIGO"),
			@JoinColumn(name = "SDO_SEQUENCIA", referencedColumnName = "SEQUENCIA") })
	public AbsSolicitacoesDoacoes getAbsSolicitacoesDoacoes() {
		return this.absSolicitacoesDoacoes;
	}

	public void setAbsSolicitacoesDoacoes(
			AbsSolicitacoesDoacoes absSolicitacoesDoacoes) {
		this.absSolicitacoesDoacoes = absSolicitacoesDoacoes;
	}

	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return this.servidor;
	}
	
	
	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@ManyToOne
	@JoinColumn(name = "TDO_CODIGO", referencedColumnName = "CODIGO", nullable = false)
	public AbsTipoDoacao getTipoDoacao() {
		return tipoDoacao;
	}

	public void setTipoDoacao(AbsTipoDoacao tipoDoacao) {
		this.tipoDoacao = tipoDoacao;
	}
	
	@ManyToOne
	@JoinColumn(name = "CAD_SEQ", referencedColumnName = "SEQ", nullable = false)
	public AbsCandidatosDoadores getCadSeq() {
		return this.cadSeq;
	}

	public void setCadSeq(AbsCandidatosDoadores cadSeq) {
		this.cadSeq = cadSeq;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "UNID_COLETADAS", precision = 2, scale = 0)
	public Byte getUnidColetadas() {
		return this.unidColetadas;
	}

	public void setUnidColetadas(Byte unidColetadas) {
		this.unidColetadas = unidColetadas;
	}

	@Column(name = "IND_TRIAGEM", length = 1)
	@org.hibernate.annotations.Type( type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndTriagem() {
		return this.indTriagem;
	}

	public void setIndTriagem(Boolean indTriagem) {
		this.indTriagem = indTriagem;
	}

	@Column(name = "IND_COLETA", length = 1)
	@org.hibernate.annotations.Type( type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndColeta() {
		return this.indColeta;
	}

	public void setIndColeta(Boolean indColeta) {
		this.indColeta = indColeta;
	}

	@Column(name = "OBSERVACAO", length = 200)
	@Length(max = 200)
	public String getObservacao() {
		return this.observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	@Column(name = "IND_CANDIDATO_AFERESE", length = 1)
	@org.hibernate.annotations.Type( type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndCandidatoAferese() {
		return this.indCandidatoAferese;
	}

	public void setIndCandidatoAferese(Boolean indCandidatoAferese) {
		this.indCandidatoAferese = indCandidatoAferese;
	}

	public enum Fields {
		DOACAO_PAC_CODIGO("absSolicitacoesDoacoes.id.pacCodigo"), 
		DOACAO_SEQUENCIA("absSolicitacoesDoacoes.id.sequencia"),
		BOL_DATA("id.bolData"),
		BOL_NUMERO("id.bolNumero"),
		TDO_CODIGO("tipoDoacao.codigo"),
		SER_MATRICULA("servidor.id.matricula"),
		SER_VIN_CODIGO("servidor.id.vinCodigo"),
		BOL_BSA_CODIGO("id.bolBsaCodigo");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof AbsDoacoes)) {
			return false;
		}
		AbsDoacoes other = (AbsDoacoes) obj;
		if (getId() == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!getId().equals(other.getId())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}
