package br.gov.mec.aghu.estoque.business;

import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.estoque.dao.SceLoteFornecedorDAO;
import br.gov.mec.aghu.model.SceLoteFornecedor;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class SceLoteFornecedorRN extends BaseBusiness{

private static final Log LOG = LogFactory.getLog(SceLoteFornecedorRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private SceLoteFornecedorDAO sceLoteFornecedorDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3080524391594020410L;

	public void atualizar(SceLoteFornecedor fornecedor){
		getSceLoteFornecedorDAO().atualizar(fornecedor);
	}
	
	public void inserir(SceLoteFornecedor fornecedor){
		defineDataGeracao(fornecedor);
		getSceLoteFornecedorDAO().persistir(fornecedor);
		getSceLoteFornecedorDAO().flush();
	}
	
	/***
	 * Define data geracao
	 * @param loteForn
	 */
	private void defineDataGeracao(SceLoteFornecedor loteForn){
		loteForn.setDtGeracao(new Date());
	}

	protected SceLoteFornecedorDAO getSceLoteFornecedorDAO(){
		return sceLoteFornecedorDAO;
	}

	public void remover(SceLoteFornecedor loteFornecedorIgualDoc) {
		getSceLoteFornecedorDAO().remover(loteFornecedorIgualDoc);	
		getSceLoteFornecedorDAO().flush();
	}
}