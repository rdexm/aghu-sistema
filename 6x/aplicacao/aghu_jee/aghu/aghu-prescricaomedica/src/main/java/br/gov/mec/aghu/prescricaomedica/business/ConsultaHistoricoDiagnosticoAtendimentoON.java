package br.gov.mec.aghu.prescricaomedica.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.MpmCidAtendimento;
import br.gov.mec.aghu.prescricaomedica.dao.MpmCidAtendimentoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ConsultaHistoricoDiagnosticoAtendimentoON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ConsultaHistoricoDiagnosticoAtendimentoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MpmCidAtendimentoDAO mpmCidAtendimentoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -7846431203881659331L;
	public enum ConsultaHistoricoDiagnosticoAtendimentoONExceptionCode implements
			BusinessExceptionCode {
		
		/**
		 * Não consultar sem atendimento
		 */
		MENSAGEM_ATENDIMENTO_ERRO_PARAMETRO_OBRIGATORIO
		;
	}
	

	/**
	 * Obtém o histórico de MpmCidAtendimento. 
	 *  
	 * @param atendimento
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<MpmCidAtendimento> buscarHistoricoMpmCidsPorAtendimento (
			AghAtendimentos atendimento) throws ApplicationBusinessException {
		if (atendimento == null) {
			throw new ApplicationBusinessException(
					ConsultaHistoricoDiagnosticoAtendimentoONExceptionCode.MENSAGEM_ATENDIMENTO_ERRO_PARAMETRO_OBRIGATORIO);
		}
		return getMpmCidAtendimentoDAO().listarHistorico(atendimento);
	}
	/**
	 * Retorna instância para MpmCidAtendimentoDAO.
	 * 
	 * @return Instância de MpmCidAtendimentoDAO
	 */
	protected MpmCidAtendimentoDAO getMpmCidAtendimentoDAO() {
		return mpmCidAtendimentoDAO;
	}
	

}
