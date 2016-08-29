package br.gov.mec.aghu.nutricao.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AnuTipoItemDieta;
import br.gov.mec.aghu.model.AnuTipoItemDietaUnfs;
import br.gov.mec.aghu.nutricao.dao.AnuTipoItemDietaUnfsDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * 
 * @author bsoliveira - 29/10/2010
 *
 */
@Stateless
public class TipoItemDietaUnidadeFuncionalRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(TipoItemDietaUnidadeFuncionalRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AnuTipoItemDietaUnfsDAO anuTipoItemDietaUnfsDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6196691847324072025L;

	/**
	 * Lista AnuTipoItemDietaUnfs de acordo com a unidade funcional e o tipo de
	 * dieta.
	 * 
	 * @param tipoItemDieta
	 * @param unidadeFuncional
	 * @return
	 */
	public List<AnuTipoItemDietaUnfs> listarAnuTipoItemDietaUnfsPorTipoItemEUnidadeFuncional(
			AnuTipoItemDieta tipoItemDieta,
			AghUnidadesFuncionais unidadeFuncional) {

		return this.getAnuTipoItemDietaUnfsDAO()
				.listarAnuTipoItemDietaUnfsPorTipoItemEUnidadeFuncional(
						tipoItemDieta, unidadeFuncional);

	}



	private AnuTipoItemDietaUnfsDAO getAnuTipoItemDietaUnfsDAO() {
		return anuTipoItemDietaUnfsDAO;
	}

}
