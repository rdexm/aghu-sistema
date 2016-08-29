package br.gov.mec.aghu.exames.vo;

import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;


public class AelItemSolicitacaoExameRelLaudoUnicoVO {

	private Long luxSeq;

	private AghUnidadesFuncionais unidadeFuncional;

	private AelSolicitacaoExames solicitacaoExame;

	public AelItemSolicitacaoExameRelLaudoUnicoVO() {
		super();
	}

	public AelItemSolicitacaoExameRelLaudoUnicoVO(Long luxSeq, AghUnidadesFuncionais unidadeFuncional,
			AelSolicitacaoExames solicitacaoExame) {
		super();
		this.luxSeq = luxSeq;
		this.unidadeFuncional = unidadeFuncional;
		this.solicitacaoExame = solicitacaoExame;
	}

	public Long getLuxSeq() {
		return luxSeq;
	}

	public void setLuxSeq(Long luxSeq) {
		this.luxSeq = luxSeq;
	}

	public AelSolicitacaoExames getSolicitacaoExame() {
		return solicitacaoExame;
	}

	public void setSolicitacaoExame(AelSolicitacaoExames solicitacaoExame) {
		this.solicitacaoExame = solicitacaoExame;
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

}