package br.gov.mec.aghu.business.prescricaoenfermagem;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.model.AghAtendimentos;

@Stateless
public class VisualizarAnamneseEvolucoesRN extends BaseBusiness {

	private static final long serialVersionUID = 8330402742083604177L;

	private static final Log LOG = LogFactory.getLog(VisualizarAnamneseEvolucoesRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IAghuFacade aghuFacade;

	public enum VisualizarAnamneseEvolucoesRNExceptionCode implements
			BusinessExceptionCode {
		MENSAGEM_PACIENTE_NAO_ESTA_EM_ATENDIMENTO,
	}

	public AghAtendimentos obterUltimoAtendimentoEmAndamentoPorPaciente(
			Integer pacCodigo, String nome) throws ApplicationBusinessException {
		AghAtendimentos atendimento = getAghuFacade()
				.obterUltimoAtendimentoEmAndamentoPorPaciente(pacCodigo);
		if (atendimento == null) {
			throw new ApplicationBusinessException(
					VisualizarAnamneseEvolucoesRNExceptionCode.MENSAGEM_PACIENTE_NAO_ESTA_EM_ATENDIMENTO,
					nome);
		}
		return atendimento;
	}
	
	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}
		
}
