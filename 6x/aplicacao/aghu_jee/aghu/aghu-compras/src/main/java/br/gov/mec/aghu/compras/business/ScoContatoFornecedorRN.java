package br.gov.mec.aghu.compras.business;


import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoContatoFornecedorDAO;
import br.gov.mec.aghu.model.ScoContatoFornecedor;
import br.gov.mec.aghu.model.ScoContatoFornecedorId;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class ScoContatoFornecedorRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ScoContatoFornecedorRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private ScoContatoFornecedorDAO scoContatoFornecedorDAO;
	
	private static final long serialVersionUID = 5537443901395824616L;

	protected ScoContatoFornecedorDAO getScoContatoFornecedorDAO() {
		return scoContatoFornecedorDAO;
	}
	
	
	/**
	*  Insere um contato para um fornecedor.
	* 
	* @param  ScoContatoFornecedor 
	* @return
	* @throws ApplicationBusinessException
	**/	          
	public void persistirContatoFornecedor(ScoContatoFornecedor scoContatoFornecedor) throws ApplicationBusinessException {
		      getScoContatoFornecedorDAO().persistir(scoContatoFornecedor);
	
	}
	/**RN2: Regra não implementada
	* Se um dos campos estiver sendo modificado (funcao ou e_mail_nf) e função igual a ‘CONTATO FISCAL’, fazer o update:
	*	-- Atualização cadastro Sapiens
	*	     update SCE_E095FOR
	*	          set  intnet   = :new.e_mail_nf,
	*	                nomven = :new.nome,
	*	                 datatu = data atual 
	*	          where CODFOR   = :new.frn_numero;
	*
	* Altera o contato do fornecedor.
	* @param  ScoContatoFornecedor 
	* @return
	* @throws ApplicationBusinessException
	* 
	*/
	public void alterarContatoFornecedor(ScoContatoFornecedor scoContatoFornecedor ,ScoContatoFornecedor cfAnterior ) throws ApplicationBusinessException {
		
		if (scoContatoFornecedor!=null && scoContatoFornecedor.getId()!=null){
				this.alterarDataAlteracao(scoContatoFornecedor, cfAnterior);
				getScoContatoFornecedorDAO().merge(scoContatoFornecedor);
			}
	}

	
	public void remover(ScoContatoFornecedorId id) throws ApplicationBusinessException {
		getScoContatoFornecedorDAO().removerPorId(id);
		
	}
	
	/**
	*  Regra antes de alterar um contato de fornecedor
	*  @ORADB SCOT_CTF_BRU
	**/
	public void alterarDataAlteracao(ScoContatoFornecedor scoContatoFornecedor, ScoContatoFornecedor cfAnterior){
		if ((cfAnterior.getEMail()!=null && !cfAnterior.getEMail().equalsIgnoreCase(scoContatoFornecedor.getEMail()))
		  || (cfAnterior.getIndRecEmailAf()!=null && !cfAnterior.getIndRecEmailAf().equals(scoContatoFornecedor.getIndRecEmailAf()))
		  || (cfAnterior.getIndRecEmailEdital()!=null && !cfAnterior.getIndRecEmailEdital().equals(scoContatoFornecedor.getIndRecEmailEdital()))
		  || (cfAnterior.getEMail()==null && scoContatoFornecedor.getEMail()!=null)
		  || (cfAnterior.getIndRecEmailAf()==null && scoContatoFornecedor.getIndRecEmailAf()==true)
		  || (cfAnterior.getIndRecEmailEdital()==null && scoContatoFornecedor.getIndRecEmailEdital()==true)){		
			
			scoContatoFornecedor.setDtAtuContato(new Date());
		}
		// parte da regra incluida por 47288
		if (scoContatoFornecedor.getEMail()!=null && scoContatoFornecedor.getIndRecEmailAf()!=null && scoContatoFornecedor.getIndRecEmailAf().equals(Boolean.TRUE) 
				&& scoContatoFornecedor.getIndAtuContato() !=null && scoContatoFornecedor.getIndAtuContato().equals(Boolean.TRUE)
				 &&(cfAnterior.getIndEnvioEmailSupTec()!=null &&  !cfAnterior.getIndEnvioEmailSupTec().equals(scoContatoFornecedor.getIndEnvioEmailSupTec())
				     || ((cfAnterior.getIndEnvioEmailSupTec()==null && scoContatoFornecedor.getIndEnvioEmailSupTec() != null))
				     || ((scoContatoFornecedor.getIndEnvioEmailSupTec()==null && cfAnterior.getIndEnvioEmailSupTec() != null)))
				  )
				  {		
					scoContatoFornecedor.setDtAtuContato(new Date());
				}
	}

}
