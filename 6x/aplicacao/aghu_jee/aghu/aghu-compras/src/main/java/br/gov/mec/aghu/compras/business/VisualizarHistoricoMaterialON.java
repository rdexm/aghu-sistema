package br.gov.mec.aghu.compras.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoMaterialJNDAO;
import br.gov.mec.aghu.model.ScoMaterialJN;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;


@Stateless
public class VisualizarHistoricoMaterialON extends BaseBusiness {

	@Inject
	private ScoMaterialJNDAO scoMaterialJNDAO;

	private static final Log LOG = LogFactory.getLog(VisualizarHistoricoMaterialON.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -726825091778807819L;

	public Boolean validarVisualizacaoHistoricoMaterial(Integer codigoMaterial) {
		List<ScoMaterialJN> lista = scoMaterialJNDAO.pesquisarScoMaterialJNPorCodigoMaterial(codigoMaterial, DominioOperacoesJournal.UPD);
		if (lista == null || lista.isEmpty()) {
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}

}
