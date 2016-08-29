package br.gov.mec.aghu.exames.solicitacao.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.dao.VAelArcoSolicitacaoDAO;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.view.VAelArcoSolicitacaoAghu;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class EtiquetasRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(EtiquetasRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IExamesFacade examesFacade;

@Inject
private VAelArcoSolicitacaoDAO vAelArcoSolicitacaoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -8377109564958218763L;

	public VAelArcoSolicitacaoAghu obterVAelArcoSolicitacaoAghuPeloId(final Integer seq) {
		final VAelArcoSolicitacaoAghu resultado = this.getVAelArcoSolicitacaoDAO().obterPorChavePrimaria(seq);
		if (resultado == null) {
			return null;
		} else {
			final IExamesFacade examesFacade = getExamesFacade();
			final AelSolicitacaoExames solicitacaoExames = examesFacade.obterAelSolicitacaoExamesPeloId(seq);

			resultado.setNomePaciente(examesFacade.buscarLaudoNomePaciente(solicitacaoExames));
			resultado.setProntuario(examesFacade.buscarLaudoProntuarioPaciente(solicitacaoExames));
			resultado.setLeito(this.buscarLeitoPaciente(solicitacaoExames));

			return resultado;
		}
	}

	/**
	 * ORADB AELC_LAUDO_LTO_PAC
	 * 
	 * @param seq
	 * @return
	 */
	public String buscarLeitoPaciente(final AelSolicitacaoExames solicitacaoExames) {
		if (solicitacaoExames.getAtendimento() == null) {
			return null;
		}
		return solicitacaoExames.getAtendimento().getLeito() == null ? null : solicitacaoExames.getAtendimento().getLeito().getLeitoID();
	}

	public VAelArcoSolicitacaoDAO getVAelArcoSolicitacaoDAO() {
		return vAelArcoSolicitacaoDAO;
	}

	protected IExamesFacade getExamesFacade() {
		return this.examesFacade;
	}
	
}
