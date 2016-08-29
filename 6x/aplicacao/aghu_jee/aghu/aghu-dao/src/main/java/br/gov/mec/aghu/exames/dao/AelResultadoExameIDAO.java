package br.gov.mec.aghu.exames.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import br.gov.mec.aghu.model.AelItemSolicExameHist;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.IAelItemSolicitacaoExames;
import br.gov.mec.aghu.model.IAelResultadoExame;

public class AelResultadoExameIDAO implements Serializable {

	private static final long serialVersionUID = -1475370623599707345L;
	
	@Inject
	private AelResultadoExameDAO aelResultadoExameDAO;

	public List<IAelResultadoExame> listarResultadosExames(
			IAelItemSolicitacaoExames item, String pclVelEmaExaSigla,
			Integer pclVelEmaManSeq, Integer pclVelSeqp, Integer pclCalSeq,
			Integer seqp) {
		List<IAelResultadoExame> result = new ArrayList<IAelResultadoExame>();

		if (item instanceof AelItemSolicExameHist) {
			result.addAll(this.getAelResultadoExameDAO()
					.listarResultadosExamesHist(item.getId().getSoeSeq(),
							item.getId().getSeqp(), pclVelEmaExaSigla,
							pclVelEmaManSeq, pclVelSeqp, pclCalSeq, seqp));
		} else {
			result.addAll(this.getAelResultadoExameDAO()
					.listarResultadosExames(item.getId().getSoeSeq(),
							item.getId().getSeqp(), pclVelEmaExaSigla,
							pclVelEmaManSeq, pclVelSeqp, pclCalSeq, seqp));
		}

		return result;
	}

	public List<IAelResultadoExame> listarResultadosExame(
			IAelItemSolicitacaoExames item) {
		List<IAelResultadoExame> result = new ArrayList<IAelResultadoExame>();

		if (item instanceof AelItemSolicExameHist) {
			result.addAll(this.getAelResultadoExameDAO()
					.listarResultadosExameHist((AelItemSolicExameHist) item));
		} else {
			result.addAll(this.getAelResultadoExameDAO().listarResultadosExame(
					(AelItemSolicitacaoExames) item));
		}

		return result;
	}
	
	private AelResultadoExameDAO getAelResultadoExameDAO() {
		return this.aelResultadoExameDAO;
	}

}
