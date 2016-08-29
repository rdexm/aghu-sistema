package br.gov.mec.aghu.compras.cadastrosbasicos.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoFnRamoComerClasDAO;
import br.gov.mec.aghu.compras.dao.ScoFornRamoComercialDAO;
import br.gov.mec.aghu.compras.dao.ScoFornecedorDAO;
import br.gov.mec.aghu.model.ScoFnRamoComerClas;
import br.gov.mec.aghu.model.ScoFornRamoComercial;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ScoFornRamoComercialRN extends BaseBusiness {

	private static final long serialVersionUID = -797757311751682604L;

	public enum ScoFornRamoComercialRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_M6_FORN_RC,MENSAGEM_M7_FORN_RC;
	}
	
	private static final Log LOG = LogFactory.getLog(ScoFornRamoComercialRN.class);

	@Inject
	private ScoFornRamoComercialDAO scoFornRamoComercialDAO;
	
	@Inject
	private ScoFnRamoComerClasDAO scoFnRamoComerClasDAO;
	
	@Inject
	private ScoFornecedorDAO scoFornecedorDAO;	
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	public void inserir(ScoFornRamoComercial fornRamoComercial) throws BaseException {
		fornRamoComercial.setVersion(0);
		this.preInserir(fornRamoComercial);
		this.getScoFornRamoComercialDAO().persistir(fornRamoComercial);
		//this.getScoFornRamoComercialDAO().flush();
	}

	private void preInserir(ScoFornRamoComercial fornRamoComercial) throws ApplicationBusinessException {
		ScoFornRamoComercial fornRamoComerc = this.getScoFornRamoComercialDAO().pesquisarScoFornRamoComerciailPorForneCodigo(fornRamoComercial);
	
		if(fornRamoComerc != null){
			throw new ApplicationBusinessException(ScoFornRamoComercialRNExceptionCode.MENSAGEM_M6_FORN_RC);
		}
		
		ScoFornecedor scoFornecedor = this.getScoFornecedorDAO().obterFornecedorPorNumero(fornRamoComercial.getId().getFrnNumero());
		fornRamoComercial.setScoFornecedor(scoFornecedor);
	}

	public void excluir(ScoFornRamoComercial fornRamoComercial) throws ApplicationBusinessException {
		preExcluir(fornRamoComercial);
		this.getScoFornRamoComercialDAO().remover(fornRamoComercial);
	}

	private void preExcluir(ScoFornRamoComercial fornRamoComercial)	throws ApplicationBusinessException {
		ScoFnRamoComerClas fnRamoComercClas = this.getScoFnRamoComerClasDAO().pesquisarScoFornRamoComerciailClasPorForneCodigo(fornRamoComercial.getId().getFrnNumero(), fornRamoComercial.getId().getRcmCodigo());		
		if(fnRamoComercClas!=null){
			throw new ApplicationBusinessException(ScoFornRamoComercialRNExceptionCode.MENSAGEM_M7_FORN_RC);
		}
	}

	private ScoFornRamoComercialDAO getScoFornRamoComercialDAO() {
		return scoFornRamoComercialDAO;
	}

	private ScoFnRamoComerClasDAO getScoFnRamoComerClasDAO() {
		return scoFnRamoComerClasDAO;
	}
	
	private ScoFornecedorDAO getScoFornecedorDAO() {
		return scoFornecedorDAO;
	}
}
