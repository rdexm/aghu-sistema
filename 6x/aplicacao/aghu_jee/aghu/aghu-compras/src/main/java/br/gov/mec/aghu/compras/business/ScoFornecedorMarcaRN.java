package br.gov.mec.aghu.compras.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoFornMarcaMateriaisDAO;
import br.gov.mec.aghu.compras.dao.ScoFornecedorMarcaDAO;
import br.gov.mec.aghu.model.ScoFornMarcaMateriais;
import br.gov.mec.aghu.model.ScoFornecedorMarca;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ScoFornecedorMarcaRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ScoFornecedorMarcaRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private ScoFornMarcaMateriaisDAO scoFornMarcaMateriaisDAO;

@Inject
private ScoFornecedorMarcaDAO scoFornecedorMarcaDAO;
	
	public enum SceEstoqueAlmoxarifadoRNExceptionCode implements BusinessExceptionCode {
		MARCA_FORNECEDOR_DUPLICADA, EXISTE_FORN_MARCA_MATERIAIS;
	}
	
	private static final long serialVersionUID = 5537443901395824615L;

	private ScoFornecedorMarcaDAO getScoFornecedorMarcaDAO() {
		return scoFornecedorMarcaDAO;
	}
	
	private ScoFornMarcaMateriaisDAO getScoFornMarcaMateriaisDAO() {
		return scoFornMarcaMateriaisDAO;
	}

	public void persistirFornecedorMarca(ScoFornecedorMarca scoFornecedorMarca) throws ApplicationBusinessException {
		if(getScoFornecedorMarcaDAO().verificaFornecedorMarca(scoFornecedorMarca)){
			throw new ApplicationBusinessException(SceEstoqueAlmoxarifadoRNExceptionCode.MARCA_FORNECEDOR_DUPLICADA);				
		}else{
			getScoFornecedorMarcaDAO().persistir(scoFornecedorMarca);
			getScoFornecedorMarcaDAO().flush();
		}
	}

	
	public void remover(ScoFornecedorMarca fornecedorMarcaExcluir) throws ApplicationBusinessException {
		fornecedorMarcaExcluir = getScoFornecedorMarcaDAO().obterPorChavePrimaria(fornecedorMarcaExcluir.getId());
		preRemover(fornecedorMarcaExcluir);
		getScoFornecedorMarcaDAO().remover(fornecedorMarcaExcluir);
		getScoFornecedorMarcaDAO().flush();
	}

	private void preRemover(ScoFornecedorMarca fornecedorMarcaExcluir) throws ApplicationBusinessException {
		List<ScoFornMarcaMateriais> list = getScoFornMarcaMateriaisDAO().pesquisarFornecedorMarca(fornecedorMarcaExcluir.getId().getFrnNumero(), fornecedorMarcaExcluir.getId().getMcmCodigo());
		if(list != null && !list.isEmpty()){
			throw new ApplicationBusinessException(SceEstoqueAlmoxarifadoRNExceptionCode.EXISTE_FORN_MARCA_MATERIAIS);		
		}
		
	}

}
