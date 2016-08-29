package br.gov.mec.aghu.internacao.vo;

import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.core.commons.BaseBean;

public class PesquisaCidVO implements BaseBean {

	private static final long serialVersionUID = 377578583146032016L;

	private AghCid cid;
	private Integer codigoPaciente;
	private boolean retornouTelaAssociada;

	public AghCid getCid() {
		return cid;
	}

	public void setCid(AghCid cid) {
		this.cid = cid;
	}

	public Integer getCodigoPaciente() {
		return codigoPaciente;
	}

	public void setCodigoPaciente(Integer codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
	}

	public boolean isRetornouTelaAssociada() {
		return retornouTelaAssociada;
	}

	public void setRetornouTelaAssociada(boolean retornouTelaAssociada) {
		this.retornouTelaAssociada = retornouTelaAssociada;
	}

}
