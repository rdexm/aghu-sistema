package br.gov.mec.aghu.ambulatorio.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.configuracao.dao.AghAtendimentoDAO;
import br.gov.mec.aghu.exames.dao.AelSolicitacaoExameDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedAmbRealizadoDAO;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.FatProcedAmbRealizado;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class TransferirExamesRN extends BaseBusiness {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -535072750315007604L;

	private static final Log LOG = LogFactory.getLog(TransferirExamesRN.class);
	
	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	@Inject
	private AelSolicitacaoExameDAO aelSolicitacaoExameDAO;
	
	@Inject
	private FatProcedAmbRealizadoDAO fatProcedAmbRealizadoDAO;
	
	@Inject
	private AghAtendimentoDAO aghAtendimentoDAO;
	
	public Integer trocarSolicitacoes(Integer oldAtdSeq, Integer newAtdSeq){
		
		Integer vQtde = 0;
		
		vQtde = this.aelSolicitacaoExameDAO.obterCountAtendimentosSolicitacaoExame(oldAtdSeq);
		
		//Atualiza os registros de FatProcedAmbRealizado
		atualizarFatProcedAmbRealizado(oldAtdSeq, newAtdSeq);
		
		//Atualiza os registros de AelSolicitacaoExames
		atualizarAelSolicitacaoExames(oldAtdSeq, newAtdSeq);
		
		return vQtde;
	}

	private void atualizarAelSolicitacaoExames(Integer oldAtdSeq,
			Integer newAtdSeq) {
		List<AelSolicitacaoExames>  listAelSolicitacaoExames = new ArrayList<AelSolicitacaoExames>();
		listAelSolicitacaoExames = this.aelSolicitacaoExameDAO.obterProcedimentosRelizado(oldAtdSeq, newAtdSeq);
		
		if (listAelSolicitacaoExames != null && !listAelSolicitacaoExames.isEmpty()) {
			for (AelSolicitacaoExames aelSolicitacaoExame : listAelSolicitacaoExames) {
				if (aelSolicitacaoExame != null && aelSolicitacaoExame.getSeq() != null) {
					AghAtendimentos aghAtendimentos = aghAtendimentoDAO.obterPorChavePrimaria(newAtdSeq);
					if (aghAtendimentos != null && aghAtendimentos.getSeq() != null) {
						aelSolicitacaoExame.setAtendimento(aghAtendimentos);
						this.aelSolicitacaoExameDAO.atualizar(aelSolicitacaoExame);
					}
				}
			}
		}
	}

	private void atualizarFatProcedAmbRealizado(Integer oldAtdSeq,
			Integer newAtdSeq) {
		List<FatProcedAmbRealizado>  listFatProcedAmbRealizado = new ArrayList<FatProcedAmbRealizado>();
		listFatProcedAmbRealizado = this.fatProcedAmbRealizadoDAO.obterProcedimentosRelizado(oldAtdSeq, newAtdSeq);
		
		if (listFatProcedAmbRealizado != null && !listFatProcedAmbRealizado.isEmpty()) {
			for (FatProcedAmbRealizado fatProcedAmbRealizado : listFatProcedAmbRealizado) {
				if (fatProcedAmbRealizado != null && fatProcedAmbRealizado.getSeq() != null) {
					AghAtendimentos aghAtendimentos = aghAtendimentoDAO.obterPorChavePrimaria(newAtdSeq);
					if (aghAtendimentos != null && aghAtendimentos.getSeq() != null) {
						fatProcedAmbRealizado.setAtendimento(aghAtendimentos);
						this.fatProcedAmbRealizadoDAO.atualizar(fatProcedAmbRealizado);
					}
				}
			}
		}
	}

	
}
