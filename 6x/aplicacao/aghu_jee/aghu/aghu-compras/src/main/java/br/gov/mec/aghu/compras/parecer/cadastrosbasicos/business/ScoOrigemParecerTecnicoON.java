package br.gov.mec.aghu.compras.parecer.cadastrosbasicos.business;

import br.gov.mec.aghu.compras.dao.ScoOrigemParecerTecnicoDAO;
import br.gov.mec.aghu.model.ScoOrigemParecerTecnico;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class ScoOrigemParecerTecnicoON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ScoOrigemParecerTecnicoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private ScoOrigemParecerTecnicoDAO scoOrigemParecerTecnicoDAO;


	private static final long serialVersionUID = 1901599164028655567L;

	public enum ScoOrigemParecerTecnicoONExceptionCode implements
			BusinessExceptionCode {
		MENSAGEM_PARAM_OBRIG, MENSAGEM_PASTA_PARECER_RN1_M07, 
		MENSAGEM_PASTA_PARECER_RN2_M06, MENSAGEM_PASTA_PARECER_RN3_M02, MENSAGEM_PASTA_PARECER_RN4_M08;
	}

	/**
	 * Insere pasta de parecer técnico
	 * @param scoOrigemParecer
	 * @author dilceia.alves
	 * @since 11/04/2013
	 * @throws ApplicationBusinessException 
	 */
	public void inserirOrigemParecer(ScoOrigemParecerTecnico scoOrigemParecer)
			throws ApplicationBusinessException {

		validarRNs(scoOrigemParecer);
		
		this.getScoOrigemParecerTecnicoDAO().persistir(scoOrigemParecer);
        this.getScoOrigemParecerTecnicoDAO().flush();
	}

	/**
	 * Altera pasta de parecer técnico pelo código
	 * @param scoOrigemParecer
	 * @author dilceia.alves
	 * @since 11/04/2013
	 * @throws ApplicationBusinessException 
	 */
	public void alterarOrigemParecer(ScoOrigemParecerTecnico scoOrigemParecer)
			throws ApplicationBusinessException {

		validarRNs(scoOrigemParecer);
	
		this.getScoOrigemParecerTecnicoDAO().merge(scoOrigemParecer);
	}

	/**
	 * Exclui pasta de parecer técnico pelo código
	 * @param scoOrigemParecer
	 * @author dilceia.alves
	 * @since 11/04/2013
	 * @throws ApplicationBusinessException 
	 */
	public void excluirOrigemParecer(Integer codigo)  throws ApplicationBusinessException {
		ScoOrigemParecerTecnico scoOrigemParecer = scoOrigemParecerTecnicoDAO.obterPorChavePrimaria(codigo);

		if (scoOrigemParecer == null) {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}
        try {
            this.getScoOrigemParecerTecnicoDAO().remover(scoOrigemParecer);
            this.getScoOrigemParecerTecnicoDAO().flush();
        }catch (Exception e) {

            //========================================================================
            // RN2 - Não pode existir nenhum Parecer associado a pasta a ser excluída
            //========================================================================
            if (e.getCause() instanceof ConstraintViolationException ) {
                throw new ApplicationBusinessException(
                        ScoOrigemParecerTecnicoONExceptionCode.MENSAGEM_PASTA_PARECER_RN2_M06);
            }
        }
	}
	
	public void validarRNs(ScoOrigemParecerTecnico scoOrigemParecer) 
			throws ApplicationBusinessException {

		if (scoOrigemParecer == null) {
			throw new ApplicationBusinessException(
					ScoOrigemParecerTecnicoONExceptionCode.MENSAGEM_PARAM_OBRIG);
		}
		
		//===========================================================================
		// RN1 - Não pode existir uma pasta com Agrupamento e Centro de Custo juntos
		//===========================================================================
		if (scoOrigemParecer.getScoAgrupamentoMateriais() != null && scoOrigemParecer.getFccCentroCusto() != null) {
			throw new ApplicationBusinessException(
					ScoOrigemParecerTecnicoONExceptionCode.MENSAGEM_PASTA_PARECER_RN1_M07);
		}
		else {
			//===========================================================================
			// RN4 - Agrupamento ou Centro de Custo deve ser informado
			//===========================================================================
			if (scoOrigemParecer.getScoAgrupamentoMateriais() == null && scoOrigemParecer.getFccCentroCusto() == null) {
				throw new ApplicationBusinessException(
						ScoOrigemParecerTecnicoONExceptionCode.MENSAGEM_PASTA_PARECER_RN4_M08);
			}
		}
				
		//=================================================================================
		// RN3 - Não pode existir mais de uma pasta para um Agrupamento ou Centro de Custo
		//=================================================================================
		ScoOrigemParecerTecnico paramResult = this.getScoOrigemParecerTecnicoDAO()
				.obterOrigemParecerPorAgrupamentoOuCCusto(scoOrigemParecer);
		
		//Inclusão
		if (scoOrigemParecer.getCodigo() == null) {  
			if (paramResult != null && paramResult.getCodigo() != null) {
				throw new ApplicationBusinessException(
						ScoOrigemParecerTecnicoONExceptionCode.MENSAGEM_PASTA_PARECER_RN3_M02);
			}
		} //Alteração
		else {  
			if (paramResult != null) {
				if (!paramResult.getCodigo().equals(scoOrigemParecer.getCodigo())) {
					throw new ApplicationBusinessException(
							ScoOrigemParecerTecnicoONExceptionCode.MENSAGEM_PASTA_PARECER_RN3_M02);
				}
			}
		}
	}
	
	private ScoOrigemParecerTecnicoDAO getScoOrigemParecerTecnicoDAO() {
		return scoOrigemParecerTecnicoDAO;
	}
	
}