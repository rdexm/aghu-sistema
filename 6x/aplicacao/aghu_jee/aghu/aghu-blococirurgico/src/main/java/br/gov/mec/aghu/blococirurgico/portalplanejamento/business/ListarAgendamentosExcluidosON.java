package br.gov.mec.aghu.blococirurgico.portalplanejamento.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcAgendasDAO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.AgendamentosExcluidosVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPesquisaCirurgiasParametrosVO;
import br.gov.mec.aghu.model.MbcAgendaJustificativa;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * Classe responsável por prover os métodos de negócio genéricos para o
 * lista de agendamentos excluidos
 * 
 * @author Romulo Panassolo
 * 
 */
@Stateless
public class ListarAgendamentosExcluidosON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ListarAgendamentosExcluidosON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcAgendasDAO mbcAgendasDAO;


	private static final long serialVersionUID = -5785778731173766724L;

	/**
	 * Método que retorna lista agendamentosExcluidosVO 
	 * 
	 */
	public List<AgendamentosExcluidosVO> listarAgendamentosExcluidos(Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc,PortalPesquisaCirurgiasParametrosVO portalPesquisaCirurgiasParametrosVO) {
		List<AgendamentosExcluidosVO> listaAgendamentosExcluidosVOCompleta = new ArrayList<AgendamentosExcluidosVO>();
		List<AgendamentosExcluidosVO> listaAgendamentosExcluidos = new ArrayList<AgendamentosExcluidosVO>();
		if (orderProperty == null || AgendamentosExcluidosVO.getFieldByDesc(orderProperty) == null) {
			orderProperty = AgendamentosExcluidosVO.Fields.DT_EXCLUSAO.toString();
		}
		listaAgendamentosExcluidos = getMbcAgendasDAO().pesquisarAgendamentosExcluidos(firstResult, maxResult, orderProperty, asc, portalPesquisaCirurgiasParametrosVO);
		for(AgendamentosExcluidosVO agendamentosExcluidos : listaAgendamentosExcluidos){
			MbcAgendas agenda = getMbcAgendasDAO().obterPorChavePrimaria(agendamentosExcluidos.getAgendaSeq());
			List<String> justificativas = new ArrayList<String>();
			for (MbcAgendaJustificativa justificativa : agenda.getAgendaJustificativas()) {
				justificativas.add(justificativa.getJustificativa());
			}
			agendamentosExcluidos.setAgendaJustificativas(justificativas);
			listaAgendamentosExcluidosVOCompleta.add(agendamentosExcluidos);
		}
		return listaAgendamentosExcluidosVOCompleta;
	}
	
	public Long recuperarCount(PortalPesquisaCirurgiasParametrosVO parametros) {
		return getMbcAgendasDAO().pesquisarAgendamentosExcluidosCount(parametros);
	}

	protected MbcAgendasDAO getMbcAgendasDAO() {
		return mbcAgendasDAO;
	}
}
