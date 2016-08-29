package br.gov.mec.aghu.compras.business;

import javax.ejb.EJB;
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
public class ScoContatoFornecedorON extends BaseBusiness{

@EJB
private ScoContatoFornecedorRN scoContatoFornecedorRN;

private static final Log LOG = LogFactory.getLog(ScoContatoFornecedorON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private ScoContatoFornecedorDAO scoContatoFornecedorDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8067774112360772223L;
	
	
	public void persistirContatoFornecedor(Integer numeroFrn, ScoContatoFornecedor scoContatoFornecedor) throws ApplicationBusinessException{
		
		if (scoContatoFornecedor!=null && scoContatoFornecedor.getId()!=null){
			ScoContatoFornecedor cfAnterior = getScoContatoFornecedorDAO().obterOriginal(scoContatoFornecedor.getId());
			
			getScoContatoFornecedorRN().alterarContatoFornecedor(scoContatoFornecedor, cfAnterior);
		}
		else {
			ScoContatoFornecedorId id = new ScoContatoFornecedorId();
			id.setFrnNumero(numeroFrn);
			id.setNumero(getScoContatoFornecedorDAO().buscarNumeroContatoFornecedor(numeroFrn).shortValue());
			scoContatoFornecedor.setId(id);
			getScoContatoFornecedorRN().persistirContatoFornecedor(scoContatoFornecedor);
		}
		
	}
	
	
	public boolean verificarAlteracaoContatoFornecedor(ScoContatoFornecedor scoContatoFornecedor){
		
		ScoContatoFornecedor cfAnterior = getScoContatoFornecedorDAO().obterOriginal(scoContatoFornecedor.getId());
		
		if (cfAnterior!=null && cfAnterior.getId()!=null && scoContatoFornecedor!=null && scoContatoFornecedor.getId()!=null){
			if (!cfAnterior.getNome().equalsIgnoreCase(scoContatoFornecedor.getNome()) || 
																	(!cfAnterior.getFuncao().equalsIgnoreCase(scoContatoFornecedor.getFuncao()))){
					return true;
			}
			else if (this.validarEmailsContatoFornecedor(scoContatoFornecedor, cfAnterior) || 
							this.validarfoneContatoFornecedor(scoContatoFornecedor, cfAnterior) ||
							this.validarIndicativosContatoFornecedor(scoContatoFornecedor, cfAnterior)){
				return true;
			}
			
			else if ((cfAnterior.getObservacao()!=null && scoContatoFornecedor.getObservacao()==null) ||  
					(cfAnterior.getObservacao()==null && scoContatoFornecedor.getObservacao()!=null) || 
					(cfAnterior.getObservacao()!=null && !cfAnterior.getObservacao().equalsIgnoreCase(scoContatoFornecedor.getObservacao()))){
				return true;
			}
			
		}	
					
		return false;
	}
	
	
	private boolean validarEmailsContatoFornecedor(ScoContatoFornecedor scoContatoFornecedor, ScoContatoFornecedor cfAnterior){
		
		 if ((cfAnterior.getEMail()!=null && scoContatoFornecedor.getEMail()==null) ||  
					(cfAnterior.getEMail()==null && scoContatoFornecedor.getEMail()!=null) || 
					(cfAnterior.getEMail()!=null && !cfAnterior.getEMail().equalsIgnoreCase(scoContatoFornecedor.getEMail()))){
				return true;
			}
			else if ((cfAnterior.getEMailNf()!=null && scoContatoFornecedor.getEMailNf()==null) ||  
					(cfAnterior.getEMailNf()==null && scoContatoFornecedor.getEMailNf()!=null) || 
					(cfAnterior.getEMailNf()!=null && !cfAnterior.getEMailNf().equalsIgnoreCase(scoContatoFornecedor.getEMailNf()))){
				return true;
			}
		 return false;
	}
	
	private boolean validarfoneContatoFornecedor(ScoContatoFornecedor scoContatoFornecedor, ScoContatoFornecedor cfAnterior){
		if ((cfAnterior.getDdd()!=null && scoContatoFornecedor.getDdd()==null) ||  
				(cfAnterior.getDdd()==null && scoContatoFornecedor.getDdd()!=null) || 
				(cfAnterior.getDdd()!=null && scoContatoFornecedor.getDdd()!=null && cfAnterior.getDdd().intValue()!=scoContatoFornecedor.getDdd().intValue())){
			return true;
		}
		else if ((cfAnterior.getFone()!=null && scoContatoFornecedor.getFone()==null) ||  
				(cfAnterior.getFone()==null && scoContatoFornecedor.getFone()!=null) || 
				(cfAnterior.getFone()!=null && scoContatoFornecedor.getFone()!=null && cfAnterior.getFone().intValue()!=scoContatoFornecedor.getFone().intValue())){
			return true;
		}
		return false;
	}
	
	private boolean validarIndicativosContatoFornecedor(ScoContatoFornecedor scoContatoFornecedor, ScoContatoFornecedor cfAnterior){
		if ((cfAnterior.getIndRecEmailAf()!=null && scoContatoFornecedor.getIndRecEmailAf()==null) ||  
				(cfAnterior.getIndRecEmailAf()==null && scoContatoFornecedor.getIndRecEmailAf()!=null) || 
				(cfAnterior.getIndRecEmailAf().booleanValue()!=scoContatoFornecedor.getIndRecEmailAf().booleanValue())){
			return true;
		}
		else if ((cfAnterior.getIndRecEmailEdital()!=null && scoContatoFornecedor.getIndRecEmailEdital()==null) ||  
				(cfAnterior.getIndRecEmailEdital()==null && scoContatoFornecedor.getIndRecEmailEdital()!=null) || 
				(cfAnterior.getIndRecEmailEdital().booleanValue()!=scoContatoFornecedor.getIndRecEmailEdital().booleanValue())){
			return true;
		}
		else if ((cfAnterior.getIndAtuContato()!=null && scoContatoFornecedor.getIndAtuContato()==null) ||  
				(cfAnterior.getIndAtuContato()==null && scoContatoFornecedor.getIndAtuContato()!=null) || 
				(cfAnterior.getIndAtuContato().booleanValue()!=scoContatoFornecedor.getIndAtuContato().booleanValue())){
			return true;
		}	
		
		return false;

	}
	
	
	protected ScoContatoFornecedorRN getScoContatoFornecedorRN() {
		return scoContatoFornecedorRN;
	}
	
	protected ScoContatoFornecedorDAO getScoContatoFornecedorDAO() {
		return scoContatoFornecedorDAO;
	}

}
