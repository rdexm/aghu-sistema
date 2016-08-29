package br.gov.mec.aghu.perinatologia.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * Representa resultado de exames da perinatologia
 * 
 * @author luismoura
 * 
 */
public class ResultadoExameSignificativoPerinatologiaVO implements Serializable {
	private static final long serialVersionUID = -7851730788082499963L;

	private Integer gsoPacCodigo;
	private Short gsoSeqp;
	private Short seqp;
	private Date dataRealizacao;
	private String resultado;
	private Integer iseSoeSeq;
	private Short iseSeqp;
	private String emaExaSigla;
	private Integer emaManSeq;
	private String descricao;
	private String sigla;
	private String descricaoExameExterno;
	private Integer eexSeq;
	

	public ResultadoExameSignificativoPerinatologiaVO() {
		
	}

	public ResultadoExameSignificativoPerinatologiaVO(Integer gsoPacCodigo, Short gsoSeqp, Short seqp, Date dataRealizacao, String emaExaSigla, Integer emaManSeq, Integer eexSeq, String descricao) {
		this.gsoPacCodigo = gsoPacCodigo;
		this.gsoSeqp = gsoSeqp;
		this.seqp = seqp;
		this.dataRealizacao = dataRealizacao;
		
		this.emaExaSigla = emaExaSigla;
		this.emaManSeq = emaManSeq;
		this.eexSeq = eexSeq;
		this.descricao = descricao;		
	}

	public Integer getGsoPacCodigo() {
		return gsoPacCodigo;
	}

	public void setGsoPacCodigo(Integer gsoPacCodigo) {
		this.gsoPacCodigo = gsoPacCodigo;
	}

	public Short getGsoSeqp() {
		return gsoSeqp;
	}

	public void setGsoSeqp(Short gsoSeqp) {
		this.gsoSeqp = gsoSeqp;
	}

	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	public Date getDataRealizacao() {
		return dataRealizacao;
	}

	public void setDataRealizacao(Date dataRealizacao) {
		this.dataRealizacao = dataRealizacao;
	}

	public String getResultado() {
		return resultado;
	}

	public void setResultado(String resultado) {
		this.resultado = resultado;
	}

	public Integer getIseSoeSeq() {
		return iseSoeSeq;
	}

	public void setIseSoeSeq(Integer iseSoeSeq) {
		this.iseSoeSeq = iseSoeSeq;
	}

	public Short getIseSeqp() {
		return iseSeqp;
	}

	public void setIseSeqp(Short iseSeqp) {
		this.iseSeqp = iseSeqp;
	}

	public String getEmaExaSigla() {
		return emaExaSigla;
	}

	public void setEmaExaSigla(String emaExaSigla) {
		this.emaExaSigla = emaExaSigla;
	}

	public Integer getEmaManSeq() {
		return emaManSeq;
	}

	public void setEmaManSeq(Integer emaManSeq) {
		this.emaManSeq = emaManSeq;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public String getDescricaoExameExterno() {
		return descricaoExameExterno;
	}

	public void setDescricaoExameExterno(String descricaoExameExterno) {
		this.descricaoExameExterno = descricaoExameExterno;
	}

	public Integer getEexSeq() {
		return eexSeq;
	}

	public void setEexSeq(Integer eexSeq) {
		this.eexSeq = eexSeq;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((gsoPacCodigo == null) ? 0 : gsoPacCodigo.hashCode());
		result = prime * result + ((gsoSeqp == null) ? 0 : gsoSeqp.hashCode());
		result = prime * result + ((seqp == null) ? 0 : seqp.hashCode());
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		ResultadoExameSignificativoPerinatologiaVO other = (ResultadoExameSignificativoPerinatologiaVO) obj;
		if (gsoPacCodigo == null) {
			if (other.gsoPacCodigo != null) {
				return false;
			}
		} else if (!gsoPacCodigo.equals(other.gsoPacCodigo)) {
			return false;
		}
		if (gsoSeqp == null) {
			if (other.gsoSeqp != null) {
				return false;
			}
		} else if (!gsoSeqp.equals(other.gsoSeqp)) {
			return false;
		}
		if (seqp == null) {
			if (other.seqp != null) {
				return false;
			}
		} else if (!seqp.equals(other.seqp)) {
			return false;
		}
		return true;
	}
}
