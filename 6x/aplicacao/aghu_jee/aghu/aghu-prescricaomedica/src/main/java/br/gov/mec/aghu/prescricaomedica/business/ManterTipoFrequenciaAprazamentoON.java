package br.gov.mec.aghu.prescricaomedica.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.prescricaomedica.dao.MpmTipoFrequenciaAprazamentoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class ManterTipoFrequenciaAprazamentoON extends BaseBusiness{

private static final Log LOG = LogFactory.getLog(ManterTipoFrequenciaAprazamentoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MpmTipoFrequenciaAprazamentoDAO mpmTipoFrequenciaAprazamentoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -6332292921126632132L;

	protected MpmTipoFrequenciaAprazamentoDAO getMpmTipoFrequenciaAprazamentoDAO(){
		
		return mpmTipoFrequenciaAprazamentoDAO;
	}
	
	/**
	 * Pesquisa tipos de aprazamento ativos e de frequência por código ou descrição
	 * @param objPesquisa Código/ Descrição
	 * @return Lista de tipos de aprazamento
	 */
	public List<MpmTipoFrequenciaAprazamento> pesquisarTipoAprazamentoAtivoFrequenciaPorCodigoDescricao(
			Object objPesquisa) {		
		return getMpmTipoFrequenciaAprazamentoDAO().pesquisarTipoAprazamentoAtivoFrequenciaPorCodigoDescricao(objPesquisa);
	}
}
