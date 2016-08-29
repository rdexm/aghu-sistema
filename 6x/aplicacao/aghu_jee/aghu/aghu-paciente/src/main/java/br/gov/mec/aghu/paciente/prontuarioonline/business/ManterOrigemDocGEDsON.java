package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.AipOrigemDocGEDs;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.dao.AipOrigemDocGEDsDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class ManterOrigemDocGEDsON extends BaseBusiness {

	
	@EJB
	private ManterOrigemDocGEDsRN manterOrigemDocGEDsRN;
	
	private static final Log LOG = LogFactory.getLog(ManterOrigemDocGEDsON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@Inject
	private AipOrigemDocGEDsDAO aipOrigemDocGEDsDAO;

    private static final long serialVersionUID = -4547051071575833689L;

    public void persistir(AipOrigemDocGEDs origemDocGED, RapServidores servidor) throws ApplicationBusinessException {
		if (origemDocGED.getSeq() == null) {
		    inserir(origemDocGED, servidor);
		} else {
		    atualizar(origemDocGED, servidor);
		}
    }

    protected void inserir(AipOrigemDocGEDs origemDocGED, RapServidores servidor) throws ApplicationBusinessException {
		getManterOrigemDocGEDsRN().preInserir(origemDocGED);
		origemDocGED.setCriadoEm(new Date());
		origemDocGED.setServidor(servidor);
		getAipOrigemDocGEDsDAO().persistir(origemDocGED);
    }

    protected void atualizar(AipOrigemDocGEDs origemDocGED, RapServidores servidor) throws ApplicationBusinessException {
		getManterOrigemDocGEDsRN().preAtualizar(origemDocGED);
		origemDocGED.setServidor(servidor);
		getAipOrigemDocGEDsDAO().atualizar(origemDocGED);
		getManterOrigemDocGEDsRN().posAtualizar(origemDocGED, servidor);
    }

    protected AipOrigemDocGEDsDAO getAipOrigemDocGEDsDAO() {
    	return aipOrigemDocGEDsDAO;
    }

    public List<AipOrigemDocGEDs> pesquisar(AipOrigemDocGEDs origemFiltro, Integer firstResult, Integer maxResult, String orderProperty,
	    boolean asc) {
    	return getAipOrigemDocGEDsDAO().pesquisar(origemFiltro, firstResult, maxResult, orderProperty, asc);
    }

    public Long pesquisarCount(AipOrigemDocGEDs origemFiltro) {
    	return getAipOrigemDocGEDsDAO().pesquisarCount(origemFiltro);
    }

    public AipOrigemDocGEDs obter(Integer seq) {
    	return getAipOrigemDocGEDsDAO().obterPorChavePrimaria(seq);
    }

    private ManterOrigemDocGEDsRN getManterOrigemDocGEDsRN() {
    	return manterOrigemDocGEDsRN;
    }
    
}
