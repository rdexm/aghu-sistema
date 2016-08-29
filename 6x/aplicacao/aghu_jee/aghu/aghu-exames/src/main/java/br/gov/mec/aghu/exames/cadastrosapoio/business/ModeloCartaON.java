package br.gov.mec.aghu.exames.cadastrosapoio.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelExamesMaterialAnaliseDAO;
import br.gov.mec.aghu.exames.dao.AelModeloCartasDAO;
import br.gov.mec.aghu.model.AelModeloCartas;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável pelas regras de negócio 
 * relacionadas à modelo de carta de recoleta.
 * 
 * @author dpacheco
 *
 */
@Stateless
public class ModeloCartaON extends BaseBusiness {

	
	@EJB
	private ModeloCartaRN modeloCartaRN;
	
	private static final Log LOG = LogFactory.getLog(ModeloCartaON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@Inject
	private AelModeloCartasDAO aelModeloCartasDAO;
	
	@Inject
	private AelExamesMaterialAnaliseDAO aelExamesMaterialAnaliseDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 8513518423709224213L;
	
	public enum ModeloCartaONExceptionCode implements BusinessExceptionCode {
		ERRO_MODELO_CARTA_RECOLETA_POSSUI_EXAME_MATERIAL_ANALISE_ASSOCIADO;
	}
	
	public void excluirModeloCarta(Short seq) throws ApplicationBusinessException {

		AelModeloCartas modeloCarta = aelModeloCartasDAO.obterPorChavePrimaria(seq);

		if (modeloCarta == null) {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}
		
		Long countExameMatModeloCarta = getAelExamesMaterialAnaliseDAO().obterExamesMaterialAnaliseModeloCartaCount(modeloCarta.getSeq());
		if (countExameMatModeloCarta == 0) {
			getModeloCartaRN().excluirModeloCarta(modeloCarta);
		} else {
			throw new ApplicationBusinessException(ModeloCartaONExceptionCode.ERRO_MODELO_CARTA_RECOLETA_POSSUI_EXAME_MATERIAL_ANALISE_ASSOCIADO);	
		}
	}
	
	protected AelExamesMaterialAnaliseDAO getAelExamesMaterialAnaliseDAO() {
		return aelExamesMaterialAnaliseDAO;
	}
	
	protected ModeloCartaRN getModeloCartaRN() {
		return modeloCartaRN;
	}

}
