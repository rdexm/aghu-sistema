package br.gov.mec.aghu.exames.vo;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.commons.BaseBean;

public class ResultadoCodificadoExameVO implements BaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2066912515589670334L;

	private Integer rcdSeq;
	private String rcdDescricao;
	private DominioSituacao rcdSituacao;
	private Boolean rcdVirusFungo;
	private Boolean rcdPositivoCci;
	private Integer gtcSeq;
	private String gtcDescricao;
	private DominioSituacao gtcSituacao;
	private DominioSituacao situacaoExame;
	private Short seqp;
	private Integer seqPai;


	public String getCodigoCompleto() {
		if (getRcdSeq() != null && getGtcSeq() != null) {
			return getRcdSeq() + " - " + getGtcSeq();
		}

		if (getRcdSeq() != null) {
			return getRcdSeq().toString();
		}

		if (getGtcSeq() != null) {
			return getGtcSeq().toString();
		}
		return "";
	}



	public String getDescricaoCompleta() {
		if (StringUtils.isNotBlank(getRcdDescricao())
				&& StringUtils.isNotBlank(getGtcDescricao())) {
			return getRcdDescricao() + " - " + getGtcDescricao();
		}
		if (StringUtils.isNotBlank(getGtcDescricao())) {
			return getGtcDescricao();
		}

		if (StringUtils.isNotBlank(getRcdDescricao())) {
			return getRcdDescricao();
		}
		return "";
	}

	public String getGtcDescricao() {
		return gtcDescricao;
	}
	public Integer getGtcSeq() {
		return gtcSeq;
	}
	public DominioSituacao getGtcSituacao() {
		return gtcSituacao;
	}
	public String getRcdDescricao() {
		return rcdDescricao;
	}
	public Boolean getRcdPositivoCci() {
		return rcdPositivoCci;
	}
	public Integer getRcdSeq() {
		return rcdSeq;
	}
	public DominioSituacao getRcdSituacao() {
		return rcdSituacao;
	}
	public Boolean getRcdVirusFungo() {
		return rcdVirusFungo;
	}

	public Short getSeqp() {
		return seqp;
	}
	public Integer getSeqPai() {
		return seqPai;
	}
	public DominioSituacao getSituacaoExame() {
		return situacaoExame;
	}
	public void setGtcDescricao(String gtcDescricao) {
		this.gtcDescricao = gtcDescricao;
	}
	public void setGtcSeq(Integer gtcSeq) {
		this.gtcSeq = gtcSeq;
	}
	public void setGtcSituacao(DominioSituacao gtcSituacao) {
		this.gtcSituacao = gtcSituacao;
	}
	public void setRcdDescricao(String rcdDescricao) {
		this.rcdDescricao = rcdDescricao;
	}

	public void setRcdPositivoCci(Boolean rcdPositivoCci) {
		this.rcdPositivoCci = rcdPositivoCci;
	}



	public void setRcdSeq(Integer rcdSeq) {
		this.rcdSeq = rcdSeq;
	}



	public void setRcdSituacao(DominioSituacao rcdSituacao) {
		this.rcdSituacao = rcdSituacao;
	}


	public void setRcdVirusFungo(Boolean rcdVirusFungo) {
		this.rcdVirusFungo = rcdVirusFungo;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	public void setSeqPai(Integer seqPai) {
		this.seqPai = seqPai;
	}

	public void setSituacaoExame(DominioSituacao situacaoExame) {
		this.situacaoExame = situacaoExame;
	}

}
