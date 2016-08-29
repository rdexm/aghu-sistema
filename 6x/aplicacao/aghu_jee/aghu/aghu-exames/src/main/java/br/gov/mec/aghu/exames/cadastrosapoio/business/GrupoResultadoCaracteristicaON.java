package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelGrupoResultadoCaracteristicaDAO;
import br.gov.mec.aghu.model.AelGrupoResultadoCaracteristica;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class GrupoResultadoCaracteristicaON extends BaseBusiness {


@EJB
private AelGrupoResultadoCaracteristicaRN aelGrupoResultadoCaracteristicaRN;

private static final Log LOG = LogFactory.getLog(GrupoResultadoCaracteristicaON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelGrupoResultadoCaracteristicaDAO aelGrupoResultadoCaracteristicaDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 7473180254585596425L;

	public List<AelGrupoResultadoCaracteristica> pesquisarGrupoResultadoCaracteristica(
			AelGrupoResultadoCaracteristica grupoResultadoCaracteristica, 
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		
		return this.getAelGrupoResultadoCaracteristicaDAO().pesquisarGrupoResultadoCaracteristica(
				grupoResultadoCaracteristica, firstResult, maxResult, orderProperty, asc);
	}
	
	public Long pesquisarGrupoResultadoCaracteristicaCount(
			AelGrupoResultadoCaracteristica grupoResultadoCaracteristica) {
		return this.getAelGrupoResultadoCaracteristicaDAO()
			.pesquisarGrupoResultadoCaracteristicaCount(grupoResultadoCaracteristica);
	}
	
	public AelGrupoResultadoCaracteristica removerGrupoResultadoCaracteristica(Integer seqExclusao) throws BaseException {
		return this.getAelGrupoResultadoCaracteristicaRN().remover(seqExclusao);		
	}

	public AelGrupoResultadoCaracteristica obterAelGrupoResultadoCaracteristica(Integer seq) {
		return this.getAelGrupoResultadoCaracteristicaDAO().obterPorChavePrimaria(seq);
	}

	public AelGrupoResultadoCaracteristica obterAelGrupoResultadoCaracteristica(Integer seq, Enum[] fetchArgsInnerJoin, Enum[] fetchArgsLeftJoin) {
		return this.getAelGrupoResultadoCaracteristicaDAO().obterPorChavePrimaria(seq, fetchArgsInnerJoin, fetchArgsLeftJoin);
	}
	
	/** GETS **/
	protected AelGrupoResultadoCaracteristicaDAO getAelGrupoResultadoCaracteristicaDAO() {
		return aelGrupoResultadoCaracteristicaDAO;
	}
	
	protected AelGrupoResultadoCaracteristicaRN getAelGrupoResultadoCaracteristicaRN() {
		return aelGrupoResultadoCaracteristicaRN;
	}
}
