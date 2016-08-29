package br.gov.mec.aghu.blococirurgico.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcAgendaHistoricoDAO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.HistoricoAgendaVO;
import br.gov.mec.aghu.dominio.DominioOperacaoAgenda;
import br.gov.mec.aghu.dominio.DominioOrigem;
import br.gov.mec.aghu.model.MbcAgendaHistorico;
import br.gov.mec.aghu.core.business.BaseBusiness;


@Stateless
public class MbcAgendaHistoricoON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MbcAgendaHistoricoON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcAgendaHistoricoDAO mbcAgendaHistoricoDAO;

	

	/**
	 * 
	 */
	private static final long serialVersionUID = -2677483108760175358L;

	// Codigo movido da controller para ON - estoria #22394
	public List<HistoricoAgendaVO> buscarHistoricoAgenda(Integer agdSeq) {
		List<MbcAgendaHistorico> agendaHistorico = new ArrayList<MbcAgendaHistorico>();
		List<HistoricoAgendaVO> agendas = new ArrayList<HistoricoAgendaVO>();

		if (agdSeq != null) {
			agendaHistorico = getMbcAgendaHistoricoDAO().buscarAgendaHistorico(agdSeq);
			if (agendaHistorico != null) {
				for (MbcAgendaHistorico mbcAgendaHistorico : agendaHistorico) {
					SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
					String criadoEm = "";
					if (mbcAgendaHistorico.getCriadoEm() != null) {
						criadoEm = df2.format(mbcAgendaHistorico.getCriadoEm());
					}
					String origem = mbcAgendaHistorico.getOrigem().getDescricao();
					String operacao = mbcAgendaHistorico.getOperacao()
							.getDescricao();
					String informadoPor = " ";
					if (mbcAgendaHistorico.getRapServidores() != null
							&& mbcAgendaHistorico.getRapServidores()
									.getPessoaFisica() != null) {
						informadoPor = mbcAgendaHistorico.getRapServidores()
								.getPessoaFisica().getNome();
					}
					String descricao = mbcAgendaHistorico.getDescricao();
	
	
					HistoricoAgendaVO historicoAgendaVO = new HistoricoAgendaVO(
							criadoEm, origem, operacao, informadoPor, descricao);
					agendas.add(historicoAgendaVO);
				}
			}
		}
		
		if(agendaHistorico.isEmpty()){
			HistoricoAgendaVO vo = new HistoricoAgendaVO(null, DominioOrigem.A.getDescricao(), DominioOperacaoAgenda.I.getDescricao(), null, null);
			agendas.add(vo);
		}
		
		for(int i = 0; i < agendas.size(); i++) {
			agendas.get(i).setSeqp(i);
		}
		return agendas;
	}

	protected MbcAgendaHistoricoDAO getMbcAgendaHistoricoDAO() {
		return mbcAgendaHistoricoDAO;
	}
}
