package br.gov.mec.aghu.exames.agendamento.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.agendamento.vo.EtiquetaEnvelopePacienteVO;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.dao.AelSolicitacaoExameDAO;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.utils.DateUtil;


@Stateless
public class RelatorioEtiquetaEnvelopePacienteON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(RelatorioEtiquetaEnvelopePacienteON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelSolicitacaoExameDAO aelSolicitacaoExameDAO;

@EJB
private IExamesFacade examesFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5239442468545541986L;
	
	public List<EtiquetaEnvelopePacienteVO> pesquisarEtiquetaEnvelopePaciente(Integer codSolicitacao, Short unfSeq) {
		List<EtiquetaEnvelopePacienteVO> listaRetorno = getAelSolicitacaoExameDAO().pesquisarEtiquetaEnvelopePaciente(codSolicitacao, unfSeq, false);
		if(listaRetorno == null || listaRetorno.isEmpty()) {
			listaRetorno = this.pesquisarEtiquetaEnvelopePacienteAtendimentoDiverso(codSolicitacao, unfSeq);
		}
		for(EtiquetaEnvelopePacienteVO item : listaRetorno) {
			item.setDataAgenda(DateUtil.dataToString(item.getDataHoraEvento(), "dd/MM/yyyy"));
		}
		
		return listaRetorno;
	}
	
	private List<EtiquetaEnvelopePacienteVO> pesquisarEtiquetaEnvelopePacienteAtendimentoDiverso(final Integer soeSeq, final Short unfSeq) {
		final List<EtiquetaEnvelopePacienteVO> result = getAelSolicitacaoExameDAO().pesquisarEtiquetaEnvelopePacienteAtendimentoDiverso(soeSeq, unfSeq, false);
		final IExamesFacade examesFacade = this.getExamesFacade();
		final AelSolicitacaoExames solicitacaoExames = examesFacade.obterAelSolicitacaoExamesPeloId(soeSeq);
		Integer prontuario = null;
		String laudo = examesFacade.buscarLaudoProntuarioPaciente(solicitacaoExames);
		if(laudo!=null && !laudo.isEmpty()){
			prontuario = Integer.parseInt(laudo.replace("/", ""));
		}
		for (final EtiquetaEnvelopePacienteVO etiquetaEnvelopePacienteVO : result) {
			etiquetaEnvelopePacienteVO.setProntuario(prontuario);
		}
		return result;
	}

	protected AelSolicitacaoExameDAO getAelSolicitacaoExameDAO() {
		return aelSolicitacaoExameDAO;
	}
	
	protected IExamesFacade getExamesFacade() {
		return examesFacade;
	}
}
