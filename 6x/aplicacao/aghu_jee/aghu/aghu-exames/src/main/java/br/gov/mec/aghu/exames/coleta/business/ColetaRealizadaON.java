package br.gov.mec.aghu.exames.coleta.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.PersistenceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacaoHorario;
import br.gov.mec.aghu.exames.agendamento.business.IAgendamentoExamesFacade;
import br.gov.mec.aghu.model.AelItemHorarioAgendado;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Regras de negócio para "Informar coleta realizada" 
 * considerando data/hora agendada para o exame.
 * 
 * @author diego.pacheco
 *
 */
@Stateless
public class ColetaRealizadaON extends BaseBusiness {


@EJB
private ColetaRealizadaRN coletaRealizadaRN;

private static final Log LOG = LogFactory.getLog(ColetaRealizadaON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IAgendamentoExamesFacade agendamentoExamesFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 511685034725953852L;
	
	public enum ColetaRealizadaONExceptionCode implements BusinessExceptionCode {
		AEL_00916, AEL_00905, AEL_00873;
	}
	
	/**
	 * Atualiza situacao de coleta de amostra considerando a
	 * situacao do horario de itemHorarioAgendado.
	 * 
	 * @param itemHorarioAgendado
	 * @throws BaseException 
	 */
	public void executarAtualizacaoColetaAmostraHorarioAgendado(Short hedGaeUnfSeq, Integer hedGaeSeqp, Date hedDthrAgenda, String nomeMicrocomputador)
			throws BaseException {
		AelItemHorarioAgendado itemHorarioAgendado = getAgendamentoExamesFacade()
				.pesquisarItemHorarioAgendadoPorGaeUnfSeqGaeSeqpDthrAgenda(hedGaeUnfSeq, hedGaeSeqp, hedDthrAgenda).get(0);
		if (DominioSituacaoHorario.M.equals(itemHorarioAgendado.getHorarioExameDisp().getSituacaoHorario())) {
			ColetaRealizadaRN coletaRealizadaRN = getColetaRealizadaRN();
			if (!coletaRealizadaRN.verificarMaterialAnaliseColetavel(itemHorarioAgendado)) {
				throw new ApplicationBusinessException(ColetaRealizadaONExceptionCode.AEL_00916);
			}
			try {
				coletaRealizadaRN.atualizarSituacaoColetaAmostra(itemHorarioAgendado, nomeMicrocomputador);	
			} catch(PersistenceException e) {
				throw new ApplicationBusinessException(ColetaRealizadaONExceptionCode.AEL_00905);
			}
		} else {
			throw new ApplicationBusinessException(ColetaRealizadaONExceptionCode.AEL_00873);
		}
	}
	
	protected ColetaRealizadaRN getColetaRealizadaRN() {
		return coletaRealizadaRN;
	}
	
	protected IAgendamentoExamesFacade getAgendamentoExamesFacade() {
		return agendamentoExamesFacade;
	}

}
