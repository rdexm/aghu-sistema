package br.gov.mec.aghu.exames.solicitacao.business;

import java.util.List;

import br.gov.mec.aghu.dominio.DominioSolicitacaoExameLote;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.model.AelLoteExameUsual;
import br.gov.mec.aghu.model.AghEspecialidades;

public class SelecionarRadioDefaultEspecialidade extends SelecionarRadioDefault {

	private AghEspecialidades especialidade;
	
	public SelecionarRadioDefaultEspecialidade(IExamesFacade examesFacade, AghEspecialidades especialidade) {
		setExamesFacade(examesFacade);
		this.especialidade = especialidade;
	}
	
	@Override
	protected DominioSolicitacaoExameLote getDominioSolicitacaoExameLote() {
		return DominioSolicitacaoExameLote.E;
	}

	@Override
	protected List<AelLoteExameUsual> getLote() {
		return getExamesFacade().getLoteDefaultEspecialidade(especialidade);
	}

}
