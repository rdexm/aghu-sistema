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
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

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
@Table(name = "MPA_CAD_CID_PROTOCOLOS", schema = "AGH")
public class MpaCadCidProtocolo extends BaseEntityId<MpaCadCidProtocoloId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7485276758890929415L;
	private MpaCadCidProtocoloId id;
	private Integer version;
	private AghCid aghCid;
	private RapServidores rapServidores;
	private MpaVersaoProtAssistencial mpaVersaoProtAssistencial;
	private Date criadoEm;
	private String indSituacao;

	public MpaCadCidProtocolo() {
	}

	public MpaCadCidProtocolo(MpaCadCidProtocoloId id, AghCid aghCid, RapServidores rapServidores,
			MpaVersaoProtAssistencial mpaVersaoProtAssistencial, Date criadoEm, String indSituacao) {
		this.id = id;
		this.aghCid = aghCid;
		this.rapServidores = rapServidores;
		this.mpaVersaoProtAssistencial = mpaVersaoProtAssistencial;
		this.criadoEm = criadoEm;
		this.indSituacao = indSituacao;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "vpaPtaSeq", column = @Column(name = "VPA_PTA_SEQ", nullable = false)),
			@AttributeOverride(name = "vpaSeqp", column = @Column(name = "VPA_SEQP", nullable = false)),
			@AttributeOverride(name = "cidSeq", column = @Column(name = "CID_SEQ", nullable = false)) })
	public MpaCadCidProtocoloId getId() {
		return this.id;
	}

	public void setId(MpaCadCidProtocoloId id) {
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
	@JoinColumn(name = "CID_SEQ", nullable = false, insertable = false, updatable = false)
	public AghCid getAghCid() {
		return this.aghCid;
	}

	public void setAghCid(AghCid aghCid) {
		this.aghCid = aghCid;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getRapServidores() {
		return this.rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "VPA_PTA_SEQ", referencedColumnName = "PTA_SEQ", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "VPA_SEQP", referencedColumnName = "SEQP", nullable = false, insertable = false, updatable = false) })
	public MpaVersaoProtAssistencial getMpaVersaoProtAssistencial() {
		return this.mpaVersaoProtAssistencial;
	}

	public void setMpaVersaoProtAssistencial(MpaVersaoProtAssistencial mpaVersaoProtAssistencial) {
		this.mpaVersaoProtAssistencial = mpaVersaoProtAssistencial;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Length(max = 1)
	public String getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(String indSituacao) {
		this.indSituacao = indSituacao;
	}

	public enum Fields {

		ID("id"),
		VERSION("version"),
		AGH_CID("aghCid"),
		RAP_SERVIDORES("rapServidores"),
		MPA_VERSAO_PROT_ASSISTENCIAIS("mpaVersaoProtAssistencial"),
		CRIADO_EM("criadoEm"),
		IND_SITUACAO("indSituacao");

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
		if (!(obj instanceof MpaCadCidProtocolo)) {
			return false;
		}
		MpaCadCidProtocolo other = (MpaCadCidProtocolo) obj;
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
