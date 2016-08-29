package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * ================================================================================
 *   ####   #####    ####   ######  #####   ##  ##   ####    ####    ####    #### 
 *  ##  ##  ##  ##  ##      ##      ##  ##  ##  ##  ##  ##  ##  ##  ##  ##  ##  ##
 *  ##  ##  #####    ####   ####    #####   ##  ##  ######  ##      ######  ##  ##
 *  ##  ##  ##  ##      ##  ##      ##  ##   ####   ##  ##  ##  ##  ##  ##  ##  ##
 *   ####   #####    ####   ######  ##  ##    ##    ##  ##   ####   ##  ##   #### 
 * ================================================================================
 *
 * A partir de uma análise originada pela tarefa #19993
 * esta model foi escolhida para ser apenas de leitura
 * no AGHU e por isso foi anotada como Immutable.
 *
 * Entretanto, caso esta entidade seja necessária na construção
 * de uma estória que necessite escrever dados no banco, este
 * comentário e esta anotação pode ser retirada desta model.
 */
@Immutable

@Entity
@Table(name = "MBC_ESTATISTICA_PROC_CIRGS", schema = "AGH")
public class MbcEstatisticaProcCirg extends BaseEntityId<MbcEstatisticaProcCirgId> implements java.io.Serializable {

	private static final long serialVersionUID = 8361820379991195800L;
	private MbcEstatisticaProcCirgId id;
	private Integer version;
	private MbcProcedimentoCirurgicos mbcProcedimentoCirurgicos;
	private Short qtdeProcedimento;
	private Integer tempoTotal;
	private Short tempoMaximo;
	private Short tempoMinimo;
	private Date criadoEm;
	private Boolean indPrincipal;

	public MbcEstatisticaProcCirg() {
	}

	public MbcEstatisticaProcCirg(MbcEstatisticaProcCirgId id, MbcProcedimentoCirurgicos mbcProcedimentoCirurgicos,
			Short qtdeProcedimento, Integer tempoTotal, Short tempoMaximo, Short tempoMinimo, Date criadoEm, Boolean indPrincipal) {
		this.id = id;
		this.mbcProcedimentoCirurgicos = mbcProcedimentoCirurgicos;
		this.qtdeProcedimento = qtdeProcedimento;
		this.tempoTotal = tempoTotal;
		this.tempoMaximo = tempoMaximo;
		this.tempoMinimo = tempoMinimo;
		this.criadoEm = criadoEm;
		this.indPrincipal = indPrincipal;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "pciSeq", column = @Column(name = "PCI_SEQ", nullable = false)),
			@AttributeOverride(name = "dtCompetencia", column = @Column(name = "DT_COMPETENCIA", nullable = false, length = 29)) })
	public MbcEstatisticaProcCirgId getId() {
		return this.id;
	}

	public void setId(MbcEstatisticaProcCirgId id) {
		this.id = id;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PCI_SEQ", nullable = false, insertable = false, updatable = false)
	public MbcProcedimentoCirurgicos getMbcProcedimentoCirurgicos() {
		return this.mbcProcedimentoCirurgicos;
	}

	public void setMbcProcedimentoCirurgicos(MbcProcedimentoCirurgicos mbcProcedimentoCirurgicos) {
		this.mbcProcedimentoCirurgicos = mbcProcedimentoCirurgicos;
	}

	@Column(name = "QTDE_PROCEDIMENTO", nullable = false)
	public Short getQtdeProcedimento() {
		return this.qtdeProcedimento;
	}

	public void setQtdeProcedimento(Short qtdeProcedimento) {
		this.qtdeProcedimento = qtdeProcedimento;
	}

	@Column(name = "TEMPO_TOTAL", nullable = false)
	public Integer getTempoTotal() {
		return this.tempoTotal;
	}

	public void setTempoTotal(Integer tempoTotal) {
		this.tempoTotal = tempoTotal;
	}

	@Column(name = "TEMPO_MAXIMO", nullable = false)
	public Short getTempoMaximo() {
		return this.tempoMaximo;
	}

	public void setTempoMaximo(Short tempoMaximo) {
		this.tempoMaximo = tempoMaximo;
	}

	@Column(name = "TEMPO_MINIMO", nullable = false)
	public Short getTempoMinimo() {
		return this.tempoMinimo;
	}

	public void setTempoMinimo(Short tempoMinimo) {
		this.tempoMinimo = tempoMinimo;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public enum Fields {

		ID("id"),
		VERSION("version"),
		MBC_PROCEDIMENTO_CIRURGICOS("mbcProcedimentoCirurgicos"),
		QTDE_PROCEDIMENTO("qtdeProcedimento"),
		TEMPO_TOTAL("tempoTotal"),
		TEMPO_MAXIMO("tempoMaximo"),
		TEMPO_MINIMO("tempoMinimo"),
		CRIADO_EM("criadoEm"),
		IND_PRINCIPAL("indPrincipal");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
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
		if (!(obj instanceof MbcEstatisticaProcCirg)) {
			return false;
		}
		MbcEstatisticaProcCirg other = (MbcEstatisticaProcCirg) obj;
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

	private enum MbcEstatisticaProcCirgsExceptionCode implements BusinessExceptionCode {
		MBC_EET_CK1,
		MBC_EET_CK2
	}
	
	@SuppressWarnings({"unused", "deprecation"})
	@PrePersist
	@PreUpdate
	private void validarMbcEstatisticaProcCirgs() {
		
		if ((this.qtdeProcedimento == null || this.tempoTotal == null || this.tempoMaximo == null || this.tempoMinimo == null) ||
			(this.qtdeProcedimento < 0 || this.tempoTotal < 0 || this.tempoMaximo < 0 || this.tempoMinimo < 0) ||
			((this.qtdeProcedimento == 0 || this.tempoTotal == 0 || this.tempoMaximo == 0 || this.tempoMinimo == 0) && (this.qtdeProcedimento > 0 || this.tempoTotal > 0 || this.tempoMaximo > 0 || this.tempoMinimo > 0)) ||
			(this.tempoMinimo > this.tempoMaximo)) {
			throw new BaseRuntimeException(MbcEstatisticaProcCirgsExceptionCode.MBC_EET_CK1);
		}
		if ((new Date(this.id.getDtCompetencia().getYear(),this.id.getDtCompetencia().getMonth(),1)).after((new Date(this.criadoEm.getYear(),this.criadoEm.getMonth(),1))) ||
			(new Date(this.id.getDtCompetencia().getYear(),this.id.getDtCompetencia().getMonth(),1)).equals((new Date(this.criadoEm.getYear(),this.criadoEm.getMonth(),1)))) {
			throw new BaseRuntimeException(MbcEstatisticaProcCirgsExceptionCode.MBC_EET_CK2);
		}
	}

	@Column(name = "IND_PRINCIPAL", nullable = false, length = 1)
	@Length(max = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndPrincipal() {
		return indPrincipal;
	}

	public void setIndPrincipal(Boolean indPrincipal) {
		this.indPrincipal = indPrincipal;
	}
	
}
