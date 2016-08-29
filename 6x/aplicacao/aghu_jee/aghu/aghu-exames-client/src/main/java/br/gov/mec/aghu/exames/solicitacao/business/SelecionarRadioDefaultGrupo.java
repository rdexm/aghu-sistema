package br.gov.mec.aghu.exames.solicitacao.business;

import java.util.List;

import br.gov.mec.aghu.dominio.DominioSolicitacaoExameLote;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.model.AelLoteExameUsual;

public class SelecionarRadioDefaultGrupo extends SelecionarRadioDefault {

	public SelecionarRadioDefaultGrupo(IExamesFacade examesFacade) {
		setExamesFacade(examesFacade);
	}
	
	@Override
	protected DominioSolicitacaoExameLote getDominioSolicitacaoExameLote() {
		return DominioSolicitacaoExameLote.G;
	}

	@Override
	protected List<AelLoteExameUsual> getLote() {
		return getExamesFacade().getLoteDefaultGrupo();
	}


}
