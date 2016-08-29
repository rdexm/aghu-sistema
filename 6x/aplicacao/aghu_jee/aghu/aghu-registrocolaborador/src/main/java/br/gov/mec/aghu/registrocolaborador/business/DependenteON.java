package br.gov.mec.aghu.registrocolaborador.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.RapDependentes;
import br.gov.mec.aghu.model.RapDependentesId;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.dao.RapDependentesDAO;
import br.gov.mec.aghu.registrocolaborador.dao.RapPessoasFisicasDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class DependenteON extends BaseBusiness {


@EJB
private DependenteRN dependenteRN;

private static final Log LOG = LogFactory.getLog(DependenteON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private RapDependentesDAO rapDependentesDAO;

@Inject
private RapPessoasFisicasDAO rapPessoasFisicasDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -6635904746927641165L;

	public enum DependenteONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_PARAMETRO_NAO_INFORMADO,
		ERRO_PESSOA_FISICA_INESXISTENTE,
		AIP_DATA_NASCIMENTO_NAO_INFORMADA,
		AIP_DATA_NASCIMENTO_INVALIDA
	}
		
	public RapPessoasFisicas obterPessoaFisica(Integer codigo)
	throws ApplicationBusinessException {

     if (codigo == null) {
	     throw new ApplicationBusinessException(
	    		 DependenteONExceptionCode.MENSAGEM_PARAMETRO_NAO_INFORMADO);
      }

      return getPessoasFisicasDAO().obterPorChavePrimaria(codigo);
    }

	
	private void verificarDataNascimento(Date dthrNascimento)
	throws ApplicationBusinessException {
	
	//entityManager.flush();
    if (dthrNascimento == null) {
    	    	    	        	
	    throw new ApplicationBusinessException(	    		
	    	DependenteONExceptionCode.AIP_DATA_NASCIMENTO_NAO_INFORMADA);	    	
       }
    if (dthrNascimento != null && dthrNascimento.after(new Date())) {
    	    	    
	    throw new ApplicationBusinessException(
			DependenteONExceptionCode.AIP_DATA_NASCIMENTO_INVALIDA);
       }
    }

	
	public void removerDependente(RapDependentesId id) throws ApplicationBusinessException {
		getDependenteRN().delete(id);
	}

	
	public void salvarDependente(RapDependentes dependentes)
			throws ApplicationBusinessException {
		//this.validarDadosDependente(dependentes, true);
				
		verificarDataNascimento(dependentes.getDtNascimento());		
		
		// verifica se PesCodigo é nulo
		if ( dependentes.getId().getPesCodigo() == null ) {
			throw new ApplicationBusinessException(
					DependenteONExceptionCode.ERRO_PESSOA_FISICA_INESXISTENTE);
		}
		
		// busca próximo codigo da pessoa
		getDependentesDAO().atribuiCodigoDependente(dependentes);
		
		//obtem pessoa fisica
		RapPessoasFisicas pessoafisica = obterPessoaFisica(dependentes.getId().getPesCodigo());

		dependentes.setPessoaFisica(pessoafisica);
							
		getDependenteRN().insert(dependentes);
	}

	
	public void alterar(RapDependentes dependentes, RapServidores servidorLogado)
			throws ApplicationBusinessException {
		// this.validarDadosDependente(dependentes, true);
		
		verificarDataNascimento(dependentes.getDtNascimento());		
				
		getDependenteRN().update(dependentes);
	}	

	protected DependenteRN getDependenteRN() {
		return dependenteRN;
	}
	
	protected RapDependentesDAO getDependentesDAO() {
		return rapDependentesDAO;
	}
	
	protected RapPessoasFisicasDAO getPessoasFisicasDAO() {
		return rapPessoasFisicasDAO;
	}
	
}
