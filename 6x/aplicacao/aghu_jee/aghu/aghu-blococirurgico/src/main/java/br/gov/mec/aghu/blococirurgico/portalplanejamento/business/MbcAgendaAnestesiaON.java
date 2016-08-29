package br.gov.mec.aghu.blococirurgico.portalplanejamento.business;

import java.util.List;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MbcAgendaAnestesia;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável pelas regras de FORMS para #22336
 * @author fpalma
 *
 */
@Stateless
public class MbcAgendaAnestesiaON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MbcAgendaAnestesiaON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	


	private static final long serialVersionUID = -3441346635017174907L;

	public enum MbcAgendaAnestesiaONExceptionCode implements BusinessExceptionCode {
		MBC_00824_1
	}

	/**
	 * ON1
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void validarAnestesiaAdicionadaExistente(List<MbcAgendaAnestesia> listaAgendaAnestesias, MbcAgendaAnestesia agendaAnestesia) throws ApplicationBusinessException {
		for(MbcAgendaAnestesia item : listaAgendaAnestesias) {
			if(agendaAnestesia.getMbcTipoAnestesias().equals(item.getMbcTipoAnestesias())
					&& agendaAnestesia.getMbcAgendas().equals(item.getMbcAgendas())) {
				throw new ApplicationBusinessException(MbcAgendaAnestesiaONExceptionCode.MBC_00824_1);
			}
		}
	}
	
}
