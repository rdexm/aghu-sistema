package br.gov.mec.aghu.exames.solicitacao.business;

import java.util.List;

import br.gov.mec.aghu.dominio.DominioSolicitacaoExameLote;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameVO;
import br.gov.mec.aghu.model.AelLoteExameUsual;

public class SelecionarRadioDefaultUnidade extends SelecionarRadioDefault {

	private SolicitacaoExameVO solicitacaoExameVO;
	
	public SelecionarRadioDefaultUnidade(IExamesFacade examesFacade, SolicitacaoExameVO solicitacaoExameVO) {
		 this.solicitacaoExameVO = solicitacaoExameVO;
		 setExamesFacade(examesFacade);
	}
	
	@Override
	protected DominioSolicitacaoExameLote getDominioSolicitacaoExameLote() {
		return DominioSolicitacaoExameLote.U;
	}

	@Override
	protected List<AelLoteExameUsual> getLote() {
		return getExamesFacade().getLoteDefaultUnidade(this.solicitacaoExameVO);
	}


}
