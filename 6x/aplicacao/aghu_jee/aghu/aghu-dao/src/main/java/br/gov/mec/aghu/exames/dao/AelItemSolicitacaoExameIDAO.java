package br.gov.mec.aghu.exames.dao;

import java.io.Serializable;

import javax.inject.Inject;

import br.gov.mec.aghu.model.AelItemSolicExameHistId;
import br.gov.mec.aghu.model.IAelItemSolicitacaoExames;
import br.gov.mec.aghu.model.IAelItemSolicitacaoExamesId;

public class AelItemSolicitacaoExameIDAO implements Serializable {

	private static final long serialVersionUID = -1475370623599707345L;
	
	@Inject
	private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;

	@Inject
	private AelItemSolicExameHistDAO aelItemSolicExameHistDAO;

	
	public IAelItemSolicitacaoExames obterPorChavePrimaria(
			IAelItemSolicitacaoExamesId chavePrimaria) {

		// se historico
		if (chavePrimaria instanceof AelItemSolicExameHistId) {
			return this.getAelItemSolicExameHistDAO().obterPorChavePrimaria(
					chavePrimaria);
		}
		return this.getAelItemSolicitacaoExameDAO().obterPorChavePrimaria(
				chavePrimaria);
	}

	private AelItemSolicExameHistDAO getAelItemSolicExameHistDAO() {
		return this.aelItemSolicExameHistDAO;
	}

	private AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO() {
		return this.aelItemSolicitacaoExameDAO;
	}

}
