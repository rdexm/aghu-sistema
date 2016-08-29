package br.gov.mec.aghu.casca.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.casca.dao.PalavraChaveMenuDAO;
import br.gov.mec.aghu.casca.model.PalavraChaveMenu;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class PalavraChaveON extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2284563186323672284L;

	private static final Log LOG = LogFactory.getLog(PalavraChaveON.class);
	
    @Override
    @Deprecated
    protected Log getLogger() {
	return LOG;
    }

    @Inject
    private PalavraChaveMenuDAO palavraChaveMenuDAO;

    public void persistir(PalavraChaveMenu palavra) {
    	if(palavra.getId() == null) {
    		palavraChaveMenuDAO.persistir(palavra);
    	}
    	else {
    		palavraChaveMenuDAO.atualizar(palavra);
    	}
    }
    
    public void delete(PalavraChaveMenu palavra) {
    	palavra = palavraChaveMenuDAO.obterPorChavePrimaria(palavra.getId());
    	palavraChaveMenuDAO.remover(palavra);
    }
}
