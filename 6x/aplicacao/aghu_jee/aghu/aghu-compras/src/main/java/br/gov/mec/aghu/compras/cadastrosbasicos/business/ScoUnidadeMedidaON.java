package br.gov.mec.aghu.compras.cadastrosbasicos.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoUnidadeMedidaDAO;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;


@Stateless
public class ScoUnidadeMedidaON extends BaseBusiness{

	private static final Log LOG = LogFactory.getLog(ScoUnidadeMedidaON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}	
	
	@Inject
	private ScoUnidadeMedidaDAO scoUnidadeMedidaDAO;

	private static final long serialVersionUID = -3006681015829195232L;


	private enum ScoUnidadeMedidaONExceptionCode implements BusinessExceptionCode {
		VIOLACAO_FK_UNIDADE_MEDIDA, MENSAGEM_PARAM_OBRIG , MENSAGEM_ERRO_HIBERNATE_VALIDATION
		,MENSAGEM_ERRO_PERSISTIR_DADOS, MENSAGEM_CODIGO_REPETIDO_UN_MEDIDA, MENSAGEM_DESCRICAO_REPETIDO_UN_MEDIDA
	};
	
	protected ScoUnidadeMedidaDAO getScoUnidadeMedidaDAO() {
		return scoUnidadeMedidaDAO;
	}
	
	public ScoUnidadeMedida obterUnidadeMedidaPorId(String idUnidade) {
		if (idUnidade == null) {
			return null;
		}
		return scoUnidadeMedidaDAO.obterPorChavePrimaria(idUnidade);
	}
	
	/**
	 * Altera ou insere um registro de {@code ScoUnidadeMedida}.
	 * 
	 * @param unidadeMedida
	 *            
	 * @throws ApplicationBusinessException
	 */
	public void cadastrar(ScoUnidadeMedida unidadeMedida) throws ApplicationBusinessException {
		
		if (unidadeMedida == null) {
			throw new ApplicationBusinessException(ScoUnidadeMedidaONExceptionCode.MENSAGEM_PARAM_OBRIG);
		}
		
		// Verififcar se a descrição ja existe em alguma unidade de Medida Cadastrada
		if (this.getScoUnidadeMedidaDAO().verificarDescricao(unidadeMedida)){
			// Nova unidade de medida
			if (unidadeMedida.getVersion()==null){
				if (!this.getScoUnidadeMedidaDAO().verificarCodigo(unidadeMedida)){
					 throw new ApplicationBusinessException(ScoUnidadeMedidaONExceptionCode.MENSAGEM_CODIGO_REPETIDO_UN_MEDIDA);
				}
				this.getScoUnidadeMedidaDAO().persistir(unidadeMedida);
				
			} else {
				getScoUnidadeMedidaDAO().merge(unidadeMedida);
			}
					
		}else {
			throw new ApplicationBusinessException(ScoUnidadeMedidaONExceptionCode.MENSAGEM_DESCRICAO_REPETIDO_UN_MEDIDA);
		}
			
	}
	
}
