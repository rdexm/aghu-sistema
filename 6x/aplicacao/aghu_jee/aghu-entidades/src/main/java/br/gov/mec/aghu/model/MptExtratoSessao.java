package br.gov.mec.aghu.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacaoSessao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name="mptSesSq1", sequenceName="AGH.MPT_EXS_SQ1", allocationSize = 1)
@Table(name = "MPT_EXTRATO_SESSAO", schema = "AGH")

public class MptExtratoSessao extends BaseEntitySeq<Integer> implements java.io.Serializable {
	
	private static final long serialVersionUID = 3934601518964037741L;
	
	private Integer seq;
	private Integer version;
	private MptSessao mptSessao;
	private DominioSituacaoSessao indSituacao;
	private String justificativa;
	private Date criadoEm;	
	private RapServidores servidor;	
	private MptJustificativa motivo;

	
	public MptExtratoSessao() {
		
	}

	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mptSesSq1")
	@Column(name = "SEQ", nullable = false, precision = 4, scale = 0)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return servidor;
	}


	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SES_SEQ", nullable = false, referencedColumnName = "seq")
	public MptSessao getMptSessao() {
		return mptSessao;
	}


	public void setMptSessao(MptSessao mptSessao) {
		this.mptSessao = mptSessao;
	}

	@Column(name = "JUSTIFICATIVA", length = 3000)
	@Length(max = 3000)
	public String getJustificativa() {
		return justificativa;
	}


	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}
	
	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacaoSessao getIndSituacao() {
		return indSituacao;
	}


	public void setIndSituacao(DominioSituacaoSessao indSituacao) {
		this.indSituacao = indSituacao;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "JUS_SEQ", referencedColumnName = "SEQ", nullable = false)
	public MptJustificativa getMotivo() {
		return motivo;
	}
	
	public void setMotivo(MptJustificativa motivo) {
		this.motivo = motivo;
	}

	public enum Fields {
		
		SEQ("seq"),
		MPTSESSAO_SEQ("mptSessao.seq"),		
		MPTSESSAO("mptSessao"),		
		JUSTIFICATIVA("justificativa"),		
		CRIADO_EM("criadoEm"),
		SERVIDOR("servidor"),
		IND_SITUACAO("indSituacao"),
		SERVIDOR_MATRICULA("servidor.id.matricula"),
		SERVIDOR_VIN_CODIGO("servidor.id.vinCodigo"),
		MOTIVO ("motivo");
		
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
	
	
//	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getSeq() == null) ? 0 : getSeq().hashCode());
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
		if (!(obj instanceof MptExtratoSessao)) {
			return false;
		}
		MptExtratoSessao other = (MptExtratoSessao) obj;
		if (getSeq() == null) {
			if (other.getSeq() != null) {
				return false;
			}
		} else if (!getSeq().equals(other.getSeq())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####
}
