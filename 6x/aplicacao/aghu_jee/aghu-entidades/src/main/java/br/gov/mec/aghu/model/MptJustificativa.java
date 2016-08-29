package br.gov.mec.aghu.model;

import java.io.Serializable;
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

import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name="mptJtfSq1", sequenceName="AGH.MPT_JTF_SQ1", allocationSize = 1)
@Table(name = "MPT_JUSTIFICATIVA", schema = "AGH")
public class MptJustificativa extends BaseEntitySeq<Short> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2597905377126369501L;

	private Short seq;
	private String descricao;
	private DominioSituacao indSituacao;
	private Date criadoEm;
	private RapServidores servidor;
	private MptTipoJustificativa mptTipoJustificativa;
	private MptTipoSessao mptTipoSessao;
	

	public MptJustificativa() {
		
	}
	
	public MptJustificativa(Short seq, String descricao, String tipoJustificativa){
		this.seq = seq;
		this.descricao = descricao;
	
	}
	
	public enum Fields {
		
		SEQ("seq"),
		DESCRICAO("descricao"),
		TIPO("tipoJustificativa"),
		CRIADO_EM("criadoEm"),
		MPT_TIPO_JUSTIFICATIVA("mptTipoJustificativa"),
		SERVIDOR_MATRICULA("servidor.id.matricula"),
		SERVIDOR_VIN_CODIGO("servidor.id.vinCodigo"),
		IND_SITUACAO("indSituacao"),
		TIPO_SESSAO_SEQ("mptTipoSessao.seq"),
		TPS_SEQ("mptTipoSessao");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
	
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
		if (!(obj instanceof MptJustificativa)) {
			return false;
		}
		MptJustificativa other = (MptJustificativa) obj;
		if (getSeq() == null) {
			if (other.getSeq() != null) {
				return false;
			}
		} else if (!getSeq().equals(other.getSeq())) {
			return false;
		}
		return true;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mptJtfSq1")
	@Column(name = "SEQ", nullable = false, precision = 4, scale = 0)
	public Short getSeq() {
		return seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	@Column(name = "DESCRICAO", length = 60)
	@Length(max = 60)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
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
	@JoinColumn(name = "TPJ_SEQ", nullable = false)
	public MptTipoJustificativa getMptTipoJustificativa() {
		return mptTipoJustificativa;
	}

	public void setMptTipoJustificativa(MptTipoJustificativa mptTipoJustificativa) {
		this.mptTipoJustificativa = mptTipoJustificativa;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TPS_SEQ", nullable = false)
	public MptTipoSessao getMptTipoSessao() {
		return mptTipoSessao;
	}

	public void setMptTipoSessao(MptTipoSessao mptTipoSessao) {
		this.mptTipoSessao = mptTipoSessao;
	}

}