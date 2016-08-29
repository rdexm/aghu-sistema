package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.LinkedList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelExameGrupoCaracteristicaDAO;
import br.gov.mec.aghu.exames.vo.AelExameGrupoCaracteristicaVO;
import br.gov.mec.aghu.model.AelExameGrupoCaracteristica;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class AgruparCaracteristicaExameON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(AgruparCaracteristicaExameON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelExameGrupoCaracteristicaDAO aelExameGrupoCaracteristicaDAO;
	
	private static final long serialVersionUID = 2970821925526537016L;

	
	public List<AelExameGrupoCaracteristicaVO> pesquisarExameGrupoCarateristica(AelExameGrupoCaracteristica exameGrupoCaracteristica,
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		
		List<AelExameGrupoCaracteristica> result = this.getAelExameGrupoCaracteristicaDAO().pesquisarExameGrupoCarateristica(exameGrupoCaracteristica, firstResult, 
				maxResult, orderProperty, asc);
		
		List<AelExameGrupoCaracteristicaVO> listVO = new LinkedList<AelExameGrupoCaracteristicaVO>();
		for (AelExameGrupoCaracteristica aelExameGrupoCaracteristica : result) {
			AelExameGrupoCaracteristicaVO vo = new AelExameGrupoCaracteristicaVO();
			vo.setAelExameGrupoCaracteristica(aelExameGrupoCaracteristica);
			listVO.add(vo);
		}
		
		return listVO;
	}
	
	
	public Long pesquisarExameGrupoCarateristicaCount(AelExameGrupoCaracteristica exameGrupoCaracteristica) {
		return this.getAelExameGrupoCaracteristicaDAO().pesquisarExameGrupoCarateristicaCount(exameGrupoCaracteristica);
	}
	
		
	
	/** GETS **/
	protected AelExameGrupoCaracteristicaDAO getAelExameGrupoCaracteristicaDAO() {
		return aelExameGrupoCaracteristicaDAO;
	}
}
