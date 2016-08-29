package br.gov.mec.aghu.compras.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoMarcaModeloDAO;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.model.ScoMarcaModelo;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ManterMarcaComercialON extends BaseBusiness {

	@EJB
	private ScoMarcaComercialRN scoMarcaComercialRN;

	private static final Log LOG = LogFactory.getLog(ManterMarcaComercialON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IComprasFacade comprasFacade;

	@Inject
	private ScoMarcaModeloDAO scoMarcaModeloDAO;

	private static final long serialVersionUID = -1235476219794953173L;
	
	public enum ManterMarcaComercialONExceptionCode implements BusinessExceptionCode{
		MARCA_MODELO_EXISTENTE //Registro duplicado! 
	}

	public void persistirMarcaComercial(ScoMarcaComercial scoMarcaComercial) throws ApplicationBusinessException{
		
		if(scoMarcaComercial.getCodigo()!=null) {
			updateMarcaComercial(scoMarcaComercial);
		} else {
			insertMarcaComercial(scoMarcaComercial);
		}
	}	
	
	public void persistirMarcaModelo(ScoMarcaModelo scoMarcaModelo, ScoMarcaComercial scoMarcaComercial) throws ApplicationBusinessException{
		
		
		if(scoMarcaModelo.getId().getSeqp() == null){
			scoMarcaModelo.getId().setSeqp(setarSeqpModeloMarcaComercial(scoMarcaComercial));
			scoMarcaModelo.setVersion(0);
			validarMarcaModelo(scoMarcaModelo, scoMarcaComercial);
			insertMarcaModelo(scoMarcaModelo);
		} else {
			validarMarcaModelo(scoMarcaModelo, scoMarcaComercial);
			updateMarcaModelo(scoMarcaModelo);
		}
	}
	
	public void validarMarcaModelo(ScoMarcaModelo scoMarcaModelo, ScoMarcaComercial scoMarcaComercial) throws ApplicationBusinessException{
		if (scoMarcaModeloDAO.verificarExisteModeloDescricao(scoMarcaModelo, scoMarcaComercial)){
			throw new ApplicationBusinessException(ManterMarcaComercialONExceptionCode.MARCA_MODELO_EXISTENTE);
		}
	} 

	/**
	 * ORADB DERIVE_MOM_SEQP
	 * @param scoMarcaComercial
	 * procedure 	
	 */
	public Integer setarSeqpModeloMarcaComercial(ScoMarcaComercial scoMarcaComercial){
		List <ScoMarcaModelo> modelos  = getComprasFacade().pesquisarMarcaModeloPorMarcaComercial(scoMarcaComercial.getCodigo());
		Integer seq;
		if(modelos.size() > 0){
			seq = modelos.size() + 1;
		} else {
			seq = 1;
		}
		return seq;
	}
	
	private void insertMarcaModelo(ScoMarcaModelo scoMarcaModelo){
		getComprasFacade().inserirScoMarcaModelo(scoMarcaModelo);
	}
	
	private void updateMarcaModelo(ScoMarcaModelo scoMarcaModelo){
		getComprasFacade().atualizarScoMarcaModeloDepreciado(scoMarcaModelo);
	}
	
	private void insertMarcaComercial(ScoMarcaComercial scoMarcaComercial) throws ApplicationBusinessException {
		getScoMarcaComercialRN().insereScoMarcaComercial(scoMarcaComercial);
	}
	
	private void updateMarcaComercial(ScoMarcaComercial scoMarcaComercial) throws ApplicationBusinessException{
		getScoMarcaComercialRN().atualizaScoMarcaComercial(scoMarcaComercial);
	}
	
	protected ScoMarcaComercialRN getScoMarcaComercialRN(){
		return scoMarcaComercialRN;
	}
	
	protected IComprasFacade getComprasFacade() {
		return this.comprasFacade;
	}
	
}