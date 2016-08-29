package br.gov.mec.aghu.estoque.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.estoque.dao.SceReqMateriaisDAO;
import br.gov.mec.aghu.model.ScePacoteMateriaisId;
import br.gov.mec.aghu.core.business.BaseBusiness;



@Stateless
public class ManterRequisicaoMaterialRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ManterRequisicaoMaterialRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private SceReqMateriaisDAO sceReqMateriaisDAO;

	
	private static final long serialVersionUID = 8707463305130042385L;

	protected SceReqMateriaisDAO getSceReqMateriaisDAO(){
		return sceReqMateriaisDAO;
	}

	public Long obterQuantidadeRequisicaoMaterialPorCentrosCustosNumeroPacoteMaterial(
			ScePacoteMateriaisId idPacoteMateriais) {
		return getSceReqMateriaisDAO().obterQuantidadeRequisicaoMateriaisPorCentrosCustosNumeroPacoteMaterial(idPacoteMateriais.getCodigoCentroCustoProprietario(), idPacoteMateriais.getCodigoCentroCustoAplicacao(), idPacoteMateriais.getNumero());
	}
}