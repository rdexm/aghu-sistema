package br.gov.mec.aghu.internacao.cadastrosbasicos.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AghOrigemEventos;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;


@Stateless
public class OrigemInternacaoCRUD extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(OrigemInternacaoCRUD.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IAghuFacade iAghuFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5294391215007956892L;

	private enum OrigemInternacaoCRUDExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ERRO_REMOVER_ORIGEM_EVENTOS; 
		
	}
		
	
	public void persistirOrigemEventos(AghOrigemEventos origemEventos) throws ApplicationBusinessException {
		if (origemEventos.getSeq() == null) {
			// inclusão
			this.incluirOrigemEventos(origemEventos);
		} else {
			// edição
			this.atualizarOrigemEventos(origemEventos);
		}

	}

	
	private void incluirOrigemEventos(AghOrigemEventos origemEventos) throws ApplicationBusinessException {
		//this.validarDadosOrigemEventos(origemEventos);
		
		getAghuFacade().persistirAghOrigemEventos(origemEventos);
	}
	
	
	private void atualizarOrigemEventos(AghOrigemEventos origemEventos) throws ApplicationBusinessException {
		//this.validarDadosOrigemEventos(origemEventos);
		
		getAghuFacade().atualizarAghOrigemEventos(origemEventos);
	}

	
//	private void validarDadosOrigemEventos(AghOrigemEventos origemEventos) throws ApplicationBusinessException {
//		if (StringUtils.isBlank(origemEventos.getDescricao())) {
//			throw new ApplicationBusinessException(
//					OrigemInternacaoCRUDExceptionCode.DESCRICAO_TIPOSMVTOINTERNACAO_OBRIGATORIO);
//		}		
//	}
	
	
	public void removerOrigemEventos(AghOrigemEventos origemEventos) throws ApplicationBusinessException {
		
		getAghuFacade().excluirAghOrigemEventos(origemEventos);
		
//		try {
//			getAghOrigemEventosDAO().excluir(origemEventos);
//			
//		} catch (Exception e) {
//			logError("Exceção capturada: ", e);
//			if(e.getCause() != null && e.getCause().getClass().equals(ConstraintViolationException.class)){
//				throw new ApplicationBusinessException(
//						OrigemInternacaoCRUDExceptionCode.MENSAGEM_ERRO_REMOVER_ORIGEM_EVENTOS);
//			}			
//			logError("Erro ao remover a origem de internação.", e);
//			throw new ApplicationBusinessException(
//					OrigemInternacaoCRUDExceptionCode.MENSAGEM_ERRO_REMOVER_ORIGEM_EVENTOS);
//		}
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.iAghuFacade;
	}
	
}
