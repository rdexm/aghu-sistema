package br.gov.mec.aghu.compras.solicitacaocompra.business;


import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoAutTempSolicitaDAO;
import br.gov.mec.aghu.model.ScoAutTempSolicita;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ScoAutTempSolicitaON extends BaseBusiness {

@EJB
private ScoAutTempSolicitaRN scoAutTempSolicitaRN;

private static final Log LOG = LogFactory.getLog(ScoAutTempSolicitaON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private ScoAutTempSolicitaDAO scoAutTempSolicitaDAO;

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -569801916229931466L;
	
	
	private enum ScoAutTempSplicitaONExceptionCode implements BusinessExceptionCode {
		VIOLACAO_FK_UNIDADE_MEDIDA, MENSAGEM_PARAM_OBRIG , MENSAGEM_ERRO_HIBERNATE_VALIDATION
		,MENSAGEM_ERRO_PERSISTIR_DADOS, MENSAGEM_CODIGO_REPETIDO_AUT_TEMP_SOL,
		MENSAGEM_DATA_FIM_MAIOR
	}
	
	
	
	
	/**
	 * Altera ou insere um registro de {@code ScoAutTempSolicita}.
	 */
	public void cadastrar(ScoAutTempSolicita autTempSol) throws ApplicationBusinessException {
		
		if (autTempSol == null) {
			throw new ApplicationBusinessException(ScoAutTempSplicitaONExceptionCode.MENSAGEM_PARAM_OBRIG);
		}
		
		// Verificar datas
		if (autTempSol.getDtFim()!=null){
			if (autTempSol.getId().getDtInicio().after(autTempSol.getDtFim())){
				throw new ApplicationBusinessException(ScoAutTempSplicitaONExceptionCode.MENSAGEM_DATA_FIM_MAIOR);
			}
		}

		if (autTempSol.getVersion()==null){
			if (this.getScoAutTempSolicitaDAO().obterPorChavePrimaria(autTempSol.getId())!=null){
				 throw new ApplicationBusinessException(ScoAutTempSplicitaONExceptionCode.MENSAGEM_CODIGO_REPETIDO_AUT_TEMP_SOL);
			}
			this.getScoAutTempSolicitaRN().persistir(autTempSol);
				
		} else {
			getScoAutTempSolicitaRN().atualizar(autTempSol);
		}
	}
	
	
	protected ScoAutTempSolicitaRN getScoAutTempSolicitaRN() {
		return scoAutTempSolicitaRN;
	}
	
	protected ScoAutTempSolicitaDAO getScoAutTempSolicitaDAO() {
		return scoAutTempSolicitaDAO;
	}
}
