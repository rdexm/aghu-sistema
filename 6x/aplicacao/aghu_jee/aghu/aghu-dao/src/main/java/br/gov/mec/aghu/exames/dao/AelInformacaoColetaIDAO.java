package br.gov.mec.aghu.exames.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import br.gov.mec.aghu.model.AelSolicitacaoExamesHist;
import br.gov.mec.aghu.model.IAelInformacaoColeta;
import br.gov.mec.aghu.model.IAelSolicitacaoExames;

public class AelInformacaoColetaIDAO implements Serializable {

	private static final long serialVersionUID = 2294027604745554892L;
	
	@Inject
	private AelInformacaoColetaDAO aelInformacaoColetaDAO;
	
	@Inject
	private AelInformacaoColetaHistDAO aelInformacaoColetaHistDAO;

	public List<IAelInformacaoColeta> listarInformacoesPorSoeSeq(
			IAelSolicitacaoExames solicitacao) {
		List<IAelInformacaoColeta> result = new ArrayList<IAelInformacaoColeta>();

		if (solicitacao instanceof AelSolicitacaoExamesHist) {
			result.addAll(this.getAelInformacaoColetaHistDAO()
					.listarInformacoesPorSoeSeq(solicitacao.getSeq()));
		} else {
			result.addAll(this.getAelInformacaoColetaDAO()
					.listarInformacoesPorSoeSeq(solicitacao.getSeq()));
		}

		return result;
	}

	private AelInformacaoColetaDAO getAelInformacaoColetaDAO() {
		return this.aelInformacaoColetaDAO;
	}

	private AelInformacaoColetaHistDAO getAelInformacaoColetaHistDAO() {
		return this.aelInformacaoColetaHistDAO;
	}

}
