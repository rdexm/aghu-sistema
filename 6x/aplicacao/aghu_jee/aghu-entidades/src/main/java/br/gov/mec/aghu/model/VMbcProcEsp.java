package br.gov.mec.aghu.model;

// Generated 28/03/2013 11:08:31 by Hibernate Tools 3.4.0.CR1

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;


import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Parameter;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioIndContaminacao;
import br.gov.mec.aghu.dominio.DominioRegimeProcedimentoCirurgicoSus;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoProcedimentoCirurgico;
import br.gov.mec.aghu.core.persistence.BaseEntityId;


/**
 * @author rpanassolo
 * 
 */



@Entity
@Table(name = "V_MBC_PROC_ESP", schema = "AGH")
@Immutable
public class VMbcProcEsp extends BaseEntityId<VMbcProcEspId> implements java.io.Serializable {

	private static final long serialVersionUID = -1543846229953753766L;
	
	private VMbcProcEspId id;
	private String sigla;
	private String descricao;
	private String descProc;
	private DominioIndContaminacao indContaminacao;
	private Boolean indProcMultiplo;
	private Short tempoMinimo;
	private DominioSituacao situacaoSinonimo;
	private DominioSituacao situacaoProc;
	private DominioSituacao situacaoEspProc;
	private DominioSituacao situacaoEsp;
	private DominioTipoProcedimentoCirurgico tipo;
	private DominioRegimeProcedimentoCirurgicoSus regimeProcedSus;
	
	
	public VMbcProcEsp() {
	
	}

	public VMbcProcEsp(VMbcProcEspId id) {
		this.id = id;
	}
	
	public VMbcProcEsp(short espSeq, String sigla, int pciSeq, short seqp,
			String descricao, String descProc, DominioIndContaminacao indContaminacao,
			Boolean indProcMultiplo, DominioSituacao situacaoSinonimo,
			DominioSituacao situacaoProc, DominioSituacao situacaoEspProc, DominioSituacao situacaoEsp,
			DominioTipoProcedimentoCirurgico tipo) {
		this.sigla = sigla;
		this.descricao = descricao;
		this.descProc = descProc;
		this.indContaminacao = indContaminacao;
		this.indProcMultiplo = indProcMultiplo;
		this.situacaoSinonimo = situacaoSinonimo;
		this.situacaoProc = situacaoProc;
		this.situacaoEspProc = situacaoEspProc;
		this.situacaoEsp = situacaoEsp;
		this.tipo = tipo;
	}

	public VMbcProcEsp(short espSeq, String sigla, int pciSeq, short seqp,
			String descricao, String descProc, DominioIndContaminacao indContaminacao,
			Boolean indProcMultiplo, Short tempoMinimo, DominioSituacao situacaoSinonimo,
			DominioSituacao situacaoProc, DominioSituacao situacaoEspProc, DominioSituacao situacaoEsp,
			DominioTipoProcedimentoCirurgico tipo, DominioRegimeProcedimentoCirurgicoSus regimeProcedSus, String indLadoCirurgia) {
		this.sigla = sigla;
		this.descricao = descricao;
		this.descProc = descProc;
		this.indContaminacao = indContaminacao;
		this.indProcMultiplo = indProcMultiplo;
		this.tempoMinimo = tempoMinimo;
		this.situacaoSinonimo = situacaoSinonimo;
		this.situacaoProc = situacaoProc;
		this.situacaoEspProc = situacaoEspProc;
		this.situacaoEsp = situacaoEsp;
		this.tipo = tipo;
		this.regimeProcedSus = regimeProcedSus;
		
	}

	

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "espSeq", column = @Column(name = "ESP_SEQ", nullable = false, precision = 4, scale = 0)),
			@AttributeOverride(name = "pciSeq", column = @Column(name = "PCI_SEQ", nullable = false, precision = 5, scale = 0))})
			
	
	public VMbcProcEspId getId() {
		return this.id;
	}

	public void setId(VMbcProcEspId id) {
		this.id = id;
	}
	
	
	@Column(name = "SIGLA", nullable = false, insertable = false, updatable = false, length = 3)
	@Length(max = 3)
	public String getSigla() {
		return this.sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}
	
	@Column(name = "DESCRICAO", nullable = false, insertable = false, updatable = false, length = 120)
	@Length(max = 120)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "DESC_PROC", nullable = false, insertable = false, updatable = false, length = 120)
	@Length(max = 120)
	public String getDescProc() {
		return this.descProc;
	}

	public void setDescProc(String descProc) {
		this.descProc = descProc;
	}

	@Column(name = "IND_CONTAMINACAO", nullable = false, insertable = false, updatable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioIndContaminacao getIndContaminacao() {
		return this.indContaminacao;
	}

	public void setIndContaminacao(DominioIndContaminacao indContaminacao) {
		this.indContaminacao = indContaminacao;
	}

	@Column(name = "IND_PROC_MULTIPLO", nullable = false, insertable = false, updatable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndProcMultiplo() {
		return this.indProcMultiplo;
	}

	public void setIndProcMultiplo(Boolean indProcMultiplo) {
		this.indProcMultiplo = indProcMultiplo;
	}

	@Column(name = "TEMPO_MINIMO", precision = 4, scale = 0)
	public Short getTempoMinimo() {
		return this.tempoMinimo;
	}

	public void setTempoMinimo(Short tempoMinimo) {
		this.tempoMinimo = tempoMinimo;
	}

	@Column(name = "SITUACAO_SINONIMO", nullable = false, insertable = false, updatable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacaoSinonimo() {
		return this.situacaoSinonimo;
	}

	public void setSituacaoSinonimo(DominioSituacao situacaoSinonimo) {
		this.situacaoSinonimo = situacaoSinonimo;
	}

	@Column(name = "SITUACAO_PROC", nullable = false, insertable = false, updatable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacaoProc() {
		return this.situacaoProc;
	}

	public void setSituacaoProc(DominioSituacao situacaoProc) {
		this.situacaoProc = situacaoProc;
	}

	@Column(name = "SITUACAO_ESP_PROC", nullable = false, insertable = false, updatable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacaoEspProc() {
		return this.situacaoEspProc;
	}

	public void setSituacaoEspProc(DominioSituacao situacaoEspProc) {
		this.situacaoEspProc = situacaoEspProc;
	}

	@Column(name = "SITUACAO_ESP", nullable = false, insertable = false, updatable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacaoEsp() {
		return this.situacaoEsp;
	}

	public void setSituacaoEsp(DominioSituacao situacaoEsp) {
		this.situacaoEsp = situacaoEsp;
	}

	@Column(name = "TIPO", nullable = false, insertable = false, updatable = false, precision = 1, scale = 0)
	@org.hibernate.annotations.Type(parameters = { @Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioTipoProcedimentoCirurgico") }, type = "br.gov.mec.aghu.core.model.jpa.DominioUserType")
	public DominioTipoProcedimentoCirurgico getTipo() {
		return this.tipo;
	}

	public void setTipo(DominioTipoProcedimentoCirurgico tipo) {
		this.tipo = tipo;
	}

	@Column(name = "REGIME_PROCED_SUS", length = 1)
	@Length(max = 1)
	@org.hibernate.annotations.Type(parameters = { @Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioRegimeProcedimentoCirurgicoSus") }, type = "br.gov.mec.aghu.core.model.jpa.DominioStringUserType")
	public DominioRegimeProcedimentoCirurgicoSus getRegimeProcedSus() {
		return this.regimeProcedSus;
	}

	public void setRegimeProcedSus(DominioRegimeProcedimentoCirurgicoSus regimeProcedSus) {
		this.regimeProcedSus = regimeProcedSus;
	}

	
	
	public enum Fields {
		ID_ESP_SEQ("id.espSeq"),
		ID_PCI_SEQ("id.pciSeq"),
		SEQP("id.seqp"),
		SIGLA("sigla"),
		DESCRICAO("descricao"),
		DESC_PROC("descProc"),
		IND_CONTAMINACAO("indContaminacao"),
		IND_PROC_MULTIPLO("indProcMultiplo"),
		TEMPO_MINIMO("tempoMinimo"),
		SITUACAO_SINONIMO("situacaoSinonimo"),
		SITUACAO_PROC("situacaoProc"),
		SITUACAO_ESP_PROC("situacaoEspProc"),
		SITUACAO_ESP("situacaoEsp"),
		TIPO("tipo"),
		REGIME_PROCED_SUS("regimeProcedSus");
		
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	
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
		if (!(obj instanceof VMbcProcEsp)) {
			return false;
		}
		VMbcProcEsp other = (VMbcProcEsp) obj;
		if (getId() == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!getId().equals(other.getId())) {
			return false;
		}
		return true;
	}
}
