package br.gov.mec.aghu.controleinfeccao.vo;

import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.commons.BaseBean;


public class HistoricoSalaJnVO implements BaseBean {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6000853875451385582L;
	
	private Integer seqJn;
	private Date dataAlt; 				// SALAS_JN.JN_DATE_TIME
	private String usuario;				// SALAS_JN.JN_USER
	private String descSalaJn;			// SALAS_JN.DESCRICAO
	private String nomeEsp;				// ESP.NOME_ESPECIALIDADE
	private String descTpSessao;		// TP_SESSAO.DESCRICAO
	private DominioSituacao situacao;	// SALAS_JN.IND_SITUACAO
	
	private Short espSeq;
	private Short tpSeq;
	
	public HistoricoSalaJnVO() {
		super();
	}
	
	public enum Fields {

		SEQ_JN("seqJn"),
		DATA_ALT("dataAlt"),
		USUARIO("usuario"),
		DESC_SALA_JN("descSalaJn"),
		NOME_ESP("nomeEsp"),
		DESC_TIPO_SESSAO("descTpSessao"),
		SITUACAO("situacao"),
		ESP_SEQ("espSeq"),
		TP_SEQ("tpSeq");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

	public Integer getSeqJn() {
		return seqJn;
	}

	public void setSeqJn(Integer seqJn) {
		this.seqJn = seqJn;
	}

	public Date getDataAlt() {
		return dataAlt;
	}

	public void setDataAlt(Date dataAlt) {
		this.dataAlt = dataAlt;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getDescSalaJn() {
		return descSalaJn;
	}

	public void setDescSalaJn(String descSalaJn) {
		this.descSalaJn = descSalaJn;
	}

	public String getNomeEsp() {
		return nomeEsp;
	}

	public void setNomeEsp(String nomeEsp) {
		this.nomeEsp = nomeEsp;
	}

	public String getDescTpSessao() {
		return descTpSessao;
	}

	public void setDescTpSessao(String descTpSessao) {
		this.descTpSessao = descTpSessao;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}
	
	public Short getEspSeq() {
		return espSeq;
	}

	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
	}

	public Short getTpSeq() {
		return tpSeq;
	}

	public void setTpSeq(Short tpSeq) {
		this.tpSeq = tpSeq;
	}
	
	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getSeqJn());
		umHashCodeBuilder.append(this.getDataAlt());
		umHashCodeBuilder.append(this.getUsuario());
		umHashCodeBuilder.append(this.getDescSalaJn());
		umHashCodeBuilder.append(this.getNomeEsp());
		umHashCodeBuilder.append(this.getDescTpSessao());
		umHashCodeBuilder.append(this.getSituacao());
		umHashCodeBuilder.append(this.getEspSeq());
		umHashCodeBuilder.append(this.getTpSeq());
		return umHashCodeBuilder.toHashCode();
	}	

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof HistoricoSalaJnVO)) {
			return false;
		}
		HistoricoSalaJnVO other = (HistoricoSalaJnVO) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getSeqJn(), other.getSeqJn());
		umEqualsBuilder.append(this.getDataAlt(), other.getDataAlt());
		umEqualsBuilder.append(this.getUsuario(), other.getUsuario());
		umEqualsBuilder.append(this.getDescSalaJn(), other.getDescSalaJn());
		umEqualsBuilder.append(this.getNomeEsp(), other.getNomeEsp());
		umEqualsBuilder.append(this.getDescTpSessao(), other.getDescTpSessao());
		umEqualsBuilder.append(this.getSituacao(), other.getSituacao());
		umEqualsBuilder.append(this.getEspSeq(), other.getEspSeq());
		umEqualsBuilder.append(this.getTpSeq(), other.getTpSeq());
		return umEqualsBuilder.isEquals();
		
	}
	
}