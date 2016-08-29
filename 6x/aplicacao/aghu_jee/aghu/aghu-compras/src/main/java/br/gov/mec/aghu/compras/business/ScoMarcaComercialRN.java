package br.gov.mec.aghu.compras.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.model.ScoMarcaModelo;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ScoMarcaComercialRN extends BaseBusiness{

private static final Log LOG = LogFactory.getLog(ScoMarcaComercialRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IComprasFacade comprasFacade;
	
	private static final long serialVersionUID = -1877138513528210172L;
	public enum ScoMarcaComercialRNExceptionCode implements BusinessExceptionCode{
		SCO_00844, //Marca comercial relacionada a modelo está inativa.
		MARCA_COMERCIAL_EXISTENTE //Registro duplicado! 
	}
	
	@SuppressWarnings("deprecation")
	public void atualizaScoMarcaComercial(ScoMarcaComercial scoMarcaComercial) throws ApplicationBusinessException{
		preAtualizarScoMarcaComercial(scoMarcaComercial);
		getComprasFacade().atualizarScoMarcaComercialDepreciado(scoMarcaComercial);
	}

	@SuppressWarnings("deprecation")
	public void insereScoMarcaComercial(ScoMarcaComercial scoMarcaComercial) throws ApplicationBusinessException{
		
		preInsertScoMarcaComercial(scoMarcaComercial);
		getComprasFacade().inserirScoMarcaComercial(scoMarcaComercial);
	}
	
	/**
	 * ORADB SCO_MOM_BRI
	 * @param scoMarcaComercial
	 * @throws ApplicationBusinessException 
	 * 
	 */
	
	public void preInsertScoMarcaComercial(ScoMarcaComercial scoMarcaComercial) throws ApplicationBusinessException{
		verificaChave(scoMarcaComercial);
		verificaSituacao(scoMarcaComercial);
		
	}
	
	/**
	 * ORADB SCO_MOM_BRU
	 * @param scoMarcaComercial
	 * @throws ApplicationBusinessException 
	 */
	
	@SuppressWarnings("deprecation")
	public void preAtualizarScoMarcaComercial(ScoMarcaComercial scoMarcaComercial) throws ApplicationBusinessException{
		ScoMarcaComercial original = getComprasFacade().obterScoMarcaComercialOriginal(scoMarcaComercial);
		if(!scoMarcaComercial.getDescricao().equals(original.getDescricao())){ //se não estiver atualizando a mesma marca sem alterar a descrição.
			verificaChave(scoMarcaComercial);
		}
		
		List <ScoMarcaModelo> modelos = getComprasFacade().pesquisarMarcaModeloPorMarcaComercial(scoMarcaComercial.getCodigo());
		if (!(original.getCodigo()).equals(scoMarcaComercial.getCodigo())){
			if(scoMarcaComercial.getIndSituacao() == DominioSituacao.I){
				for(ScoMarcaModelo modelo : modelos){
					modelo.setIndSituacao(DominioSituacao.I);
					getComprasFacade().atualizarScoMarcaModeloDepreciado(modelo);
				}				
			}
		}
	}
	
	
	/**
	 * ORADB RN_MOMP_VER_MCM
	 * @param scoMarcaComercial
	 * @throws ApplicationBusinessException 
	 */
	public void verificaSituacao(ScoMarcaComercial scoMarcaComercial) throws ApplicationBusinessException{
		if(!scoMarcaComercial.getIndSituacao().isAtivo()){
			throw new ApplicationBusinessException(ScoMarcaComercialRNExceptionCode.SCO_00844);
		}
	}
	//sco_mcm_uk1
	public void verificaChave(ScoMarcaComercial marcaComercial) throws ApplicationBusinessException{

		if(getComprasFacade().verificarMarcaExistente(marcaComercial.getCodigo(), marcaComercial.getDescricao())){
			
			throw new ApplicationBusinessException(ScoMarcaComercialRNExceptionCode.MARCA_COMERCIAL_EXISTENTE);
		}
	
	}
	
	protected IComprasFacade getComprasFacade() {
		return this.comprasFacade;
	}
	
}