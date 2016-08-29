package br.gov.mec.aghu.perinatologia.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.vo.RegiaoAnatomicaVO;
import br.gov.mec.aghu.model.McoAchado;
import br.gov.mec.aghu.perinatologia.vo.AchadoVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
@Stateless
public class McoAchadoON extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1233405382062213037L;
	
	@EJB
	private McoAchadoRN mcoAchadoRN;

	@Override
	@Deprecated
	protected Log getLogger() {	
		return null;
	}

	//ON01 ON03
	public void ativarInativarAchado(McoAchado entity) throws ApplicationBusinessException {
		if (entity.getIndSituacao().isAtivo()) {
			entity.setIndSituacao(DominioSituacao.I);
		} else {
			entity.setIndSituacao(DominioSituacao.A);
		}
		mcoAchadoRN.atualizarAchado(entity);
	}
	
	public List<AchadoVO> pesquisarAchadosPorDescSituacaoRegiaoAnatomicas(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc, AchadoVO filtro) {
		return mcoAchadoRN.pesquisarAchadosPorDescSituacaoRegiaoAnatomicas(firstResult,
				maxResults, orderProperty, asc, filtro);
	}
	
	public void inserirAchado(McoAchado entity, String descricaoRegiao) throws ApplicationBusinessException {
		mcoAchadoRN.inserirAchado(entity, descricaoRegiao);
	}

	public void atualizarAchado(McoAchado entity) throws ApplicationBusinessException {
		mcoAchadoRN.atualizarAchado(entity);
	}
	
	public List<RegiaoAnatomicaVO> pesquisarRegioesAnatomicasAtivasPorDesc(String descricao) {
		return mcoAchadoRN.pesquisarRegioesAnatomicasAtivasPorDesc(descricao);
	}

	public Long pesquisarRegioesAnatomicasAtivasPorDescCount(String param) {
		return mcoAchadoRN.pesquisarRegioesAnatomicasAtivasPorDescCount(param);
	}
	
	public Long pesquisarAchadosCount(AchadoVO filtro) { 
		return mcoAchadoRN.pesquisarAchadosCount(filtro);
	}
}
