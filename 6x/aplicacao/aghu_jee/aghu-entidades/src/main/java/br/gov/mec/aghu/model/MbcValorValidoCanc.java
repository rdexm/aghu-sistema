package br.gov.mec.aghu.model;

// Generated 17/06/2010 15:43:38 by Hibernate Tools 3.2.5.Beta

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Table(name = "MBC_VALOR_VALIDO_CANCS", schema = "AGH")

public class MbcValorValidoCanc extends BaseEntityId<MbcValorValidoCancId> implements java.io.Serializable {
	
	// TODO Implementar triggers (não foi implementado, pois o mapeamento
	// foi feito na implementação do módulo de internação)

	private static final long serialVersionUID = -3866225540290747786L;
	private MbcValorValidoCancId id;
	private MbcQuestao questao;
	private RapServidores servidor;
	private String valor;
	private DominioSituacao situacao;
	private Date criadoEm;
	private Set<MbcCirurgias> cirurgias = new HashSet<MbcCirurgias>(0);

	// TODO Implementar version quando necessário
	// private Integer version;
	
	public MbcValorValidoCanc() {
	}

	public MbcValorValidoCanc(MbcValorValidoCancId id,
			MbcQuestao questao, RapServidores servidor, String valor,
			DominioSituacao situacao, Date criadoEm) {
		this.id = id;
		this.questao = questao;
		this.servidor = servidor;
		this.valor = valor;
		this.situacao = situacao;
		this.criadoEm = criadoEm;
	}

	public MbcValorValidoCanc(MbcValorValidoCancId id,
			MbcQuestao questao, RapServidores servidor, String valor,
			DominioSituacao situacao, Date criadoEm, Set<MbcCirurgias> cirurgias) {
		this.id = id;
		this.questao = questao;
		this.servidor = servidor;
		this.valor = valor;
		this.situacao = situacao;
		this.criadoEm = criadoEm;
		this.cirurgias = cirurgias;
	}

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "qesMtcSeq", column = @Column(name = "QES_MTC_SEQ", nullable = false, precision = 3, scale = 0)),
			@AttributeOverride(name = "qesSeqp", column = @Column(name = "QES_SEQP", nullable = false, precision = 6, scale = 0)),
			@AttributeOverride(name = "seqp", column = @Column(name = "SEQP", nullable = false, precision = 4, scale = 0)) })
	public MbcValorValidoCancId getId() {
		return this.id;
	}

	public void setId(MbcValorValidoCancId id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "QES_MTC_SEQ", referencedColumnName = "MTC_SEQ", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "QES_SEQP", referencedColumnName = "SEQP", nullable = false, insertable = false, updatable = false) })
	public MbcQuestao getQuestao() {
		return this.questao;
	}

	public void setQuestao(MbcQuestao questao) {
		this.questao = questao;
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

	@Column(name = "VALOR", nullable = false, length = 45)
	@Length(max = 45)
	public String getValor() {
		return this.valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacao() {
		return this.situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "valorValidoCanc")
	public Set<MbcCirurgias> getCirurgias() {
		return this.cirurgias;
	}

	public void setCirurgias(Set<MbcCirurgias> cirurgias) {
		this.cirurgias = cirurgias;
	}
	
	/*
	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	*/

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		MbcValorValidoCanc other = (MbcValorValidoCanc) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	public enum Fields {

		ID("id"),
		SEQP("id.seqp"),
		QES_MTC_SEQ("id.qesMtcSeq"),
		QES_SEQP("id.qesSeqp"),
		QUESTAO("questao"),
		SERVIDOR("servidor"),
		VALOR("valor"),
		SITUACAO("situacao"),
		CRIADO_EM("criadoEm"),
		CIRURGIAS("cirurgias"),
		QUESTAO_MTC_SEQ("questao.id.mtcSeq"),
		QUESTAO_SEQP("questao.id.seqp"),
		;

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

}
