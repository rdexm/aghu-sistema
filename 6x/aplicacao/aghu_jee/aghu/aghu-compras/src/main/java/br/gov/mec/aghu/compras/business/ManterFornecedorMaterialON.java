package br.gov.mec.aghu.compras.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.SceFornecedorMaterial;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class ManterFornecedorMaterialON extends BaseBusiness {

	@EJB
	private ManterFornecedorMaterialRN manterFornecedorMaterialRN;
	
	private static final Log LOG = LogFactory.getLog(ManterFornecedorMaterialON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IEstoqueFacade estoqueFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5388353791268023996L;


	/**
	  * Metodo para persistir FornecedorMaterial.
	  * @param fornecMaterialNew
	  * @param flush
	  * @throws BaseException
	  */
	 public void persistirFornecedorMaterial(SceFornecedorMaterial fornecMaterialNew, Boolean flush) throws BaseException{
		if (fornecMaterialNew.getId() == null
				|| fornecMaterialNew.getId().getNumero() == null
				|| fornecMaterialNew.getId().getCodigo() == null
				|| fornecMaterialNew.getVersion() == null) {
			 this.inserirFornecedorMaterial(fornecMaterialNew, flush);
		 }else{
			 this.atualizarFornecedorMaterial(fornecMaterialNew, flush);
		 }
	 }
	 
	 /**
	  * Metodo para inserir FornecedorMaterial.
	  * @param fornecMaterialNew
	  * @param flush
	  * @throws BaseException
	  */
	 protected void inserirFornecedorMaterial(SceFornecedorMaterial fornecMaterialNew, Boolean flush) throws BaseException{
		 ManterFornecedorMaterialRN manterFornecedorMaterialRN = getManterFornecedorMaterialRN();
		 
		 manterFornecedorMaterialRN.executarAntesInserirFornecedorMaterial(fornecMaterialNew);
		 this.getEstoqueFacade().inserirSceFornecedorMaterial(fornecMaterialNew, flush);
	 }

	 
	 /**
	  * Metodo para atualizar FornecedorMaterial.
	  * @param fornecMaterialNew
	  * @param flush
	  * @throws BaseException
	  */
	 protected void atualizarFornecedorMaterial(SceFornecedorMaterial fornecMaterialNew, Boolean flush) throws BaseException{
		 ManterFornecedorMaterialRN manterFornecedorMaterialRN = getManterFornecedorMaterialRN();
		 
		 manterFornecedorMaterialRN.executarAntesAtualizarFornecedorMaterial(fornecMaterialNew);
		 this.getEstoqueFacade().atualizarSceFornecedorMaterial(fornecMaterialNew, flush);
	 }

	protected ManterFornecedorMaterialRN getManterFornecedorMaterialRN() {
		return manterFornecedorMaterialRN;
	}

	protected IEstoqueFacade getEstoqueFacade() {
		return this.estoqueFacade;
	}

}
