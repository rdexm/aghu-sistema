package br.gov.mec.aghu.exames.coleta.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacaoAmostra;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.dao.AelAmostraItemExamesDAO;
import br.gov.mec.aghu.exames.dao.AelItemHorarioAgendadoDAO;
import br.gov.mec.aghu.model.AelAmostraItemExames;
import br.gov.mec.aghu.model.AelItemHorarioAgendado;
import br.gov.mec.aghu.model.AelItemHorarioAgendadoId;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * Regras de negócio para "Informar coleta realizada" 
 * considerando data/hora agendada para o exame.
 * 
 * @author diego.pacheco
 *
 */
@Stateless
public class ColetaRealizadaRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ColetaRealizadaRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelAmostraItemExamesDAO aelAmostraItemExamesDAO;

@Inject
private AelItemHorarioAgendadoDAO aelItemHorarioAgendadoDAO;

@EJB
private IExamesFacade examesFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 2707912333294807887L;
	
	/**
	 * Function
	 * 
	 * ORADB: AELC_VERIF_MAT_ANALISE_COL
	 * 
	 * @param unfSeq
	 * @param gaeSeqp
	 * @param dthrAgenda
	 * @return
	 */
	public Boolean verificarMaterialAnaliseColetavel(AelItemHorarioAgendado	itemHorarioAgendado) {
		AelItemHorarioAgendadoId itemHorarioAgendadoId = itemHorarioAgendado.getId(); 
		List<AelItemHorarioAgendado> listaItemHorarioAgendado = 
				getAelItemHorarioAgendadoDAO().pesquisarItemHorarioAgendadoItemSolicitacaoExameMaterialColetavel(
						itemHorarioAgendadoId.getHedGaeUnfSeq(), itemHorarioAgendadoId.getHedGaeSeqp(), 
						itemHorarioAgendadoId.getHedDthrAgenda());
		
		if (listaItemHorarioAgendado.isEmpty()) {
			return Boolean.FALSE;
		} else {
			return Boolean.TRUE;
		}
	}
	
	/**
	 * Procedure
	 * 
	 * ORADB: AELK_AIE_RN.RN_AIEP_ATU_COL_AMOS
	 * 
	 * @param unfSeq
	 * @param gaeSeqp
	 * @param dthrAgenda
	 * @throws BaseException 
	 */
	public void atualizarSituacaoColetaAmostra(AelItemHorarioAgendado itemHorarioAgendado, String nomeMicrocomputador) throws BaseException {
		AelItemHorarioAgendadoId itemHorarioAgendadoId = itemHorarioAgendado.getId(); 
		List<AelAmostraItemExames> listaAmostraItemExame = getAelAmostraItemExamesDAO()
				.pesquisarAmostraItemExameSituacaoGeradaPorItemHorarioAgendado(itemHorarioAgendadoId.getHedGaeUnfSeq(), 
						itemHorarioAgendadoId.getHedGaeSeqp(), itemHorarioAgendadoId.getHedDthrAgenda());
		
		IExamesFacade examesFacade = getExamesFacade();
				
		for (AelAmostraItemExames amostraItemExame : listaAmostraItemExame) {
			amostraItemExame.setSituacao(DominioSituacaoAmostra.C);
			examesFacade.atualizarAelAmostraItemExames(amostraItemExame, Boolean.FALSE, Boolean.TRUE, nomeMicrocomputador);
		}
	}
	
	protected AelItemHorarioAgendadoDAO getAelItemHorarioAgendadoDAO() {
		return aelItemHorarioAgendadoDAO;
	}
	
	protected AelAmostraItemExamesDAO getAelAmostraItemExamesDAO() {
		return aelAmostraItemExamesDAO;
	}
	
	protected IExamesFacade getExamesFacade() {
		return this.examesFacade;
	}
	
}
