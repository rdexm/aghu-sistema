package br.gov.mec.aghu.farmacia.business;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.farmacia.dao.AfaViaAdministracaoDAO;
import br.gov.mec.aghu.farmacia.dao.AfaViaAdministracaoMedicamentoJNDAO;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaViaAdministracaoMedicamentoJN;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class HistoricoViasAdministracaoON extends BaseBusiness implements Serializable{

private static final Log LOG = LogFactory.getLog(HistoricoViasAdministracaoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AfaViaAdministracaoMedicamentoJNDAO afaViaAdministracaoMedicamentoJNDAO;

@Inject
private AfaViaAdministracaoDAO afaViaAdministracaoDAO;

	private static final long serialVersionUID = 3088442842483416897L;

	public Long pesquisarJnCount(AfaMedicamento medicamento) {

		return getAfaViaAdministracaoMedicamentoJNDAO().pesquisarViasAdministracaoJnCount(medicamento);
	}

	public List<AfaViaAdministracaoMedicamentoJN> pesquisarJn(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			AfaMedicamento medicamento) {


		List<AfaViaAdministracaoMedicamentoJN> lista = 
			getAfaViaAdministracaoMedicamentoJNDAO().pesquisarViasAdministracaoJn(firstResult, maxResult, orderProperty, asc, medicamento);


		for(AfaViaAdministracaoMedicamentoJN viaAdmJn:lista){			
			// Seta Nome do Responsável 
			viaAdmJn.setNomeResponsavel(viaAdmJn.getServidor().getPessoaFisica().getNome());
			
			//Seta Descrição da via de administração a partir da silga
			viaAdmJn.setDescricaoViaAdministracao(getAfaViaAdministracaoDAO().obterPorChavePrimaria(viaAdmJn.getVadSigla()).getDescricao());
		}

		return lista;
	}


	private AfaViaAdministracaoMedicamentoJNDAO getAfaViaAdministracaoMedicamentoJNDAO(){
		return afaViaAdministracaoMedicamentoJNDAO;
	}
	
	private AfaViaAdministracaoDAO getAfaViaAdministracaoDAO(){
		return afaViaAdministracaoDAO;
	}

}
