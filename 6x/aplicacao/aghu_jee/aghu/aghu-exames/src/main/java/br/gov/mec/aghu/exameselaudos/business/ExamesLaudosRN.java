package br.gov.mec.aghu.exameselaudos.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.exames.dao.AelExtratoItemSolicitacaoDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelSolicitacaoExameDAO;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class ExamesLaudosRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ExamesLaudosRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelExtratoItemSolicitacaoDAO aelExtratoItemSolicitacaoDAO;

@Inject
private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;

@Inject
private AelSolicitacaoExameDAO aelSolicitacaoExameDAO;

@EJB
private IExamesLaudosFacade examesLaudosFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1685373974153135734L;

	/**
	 * ORADB AELC_LAUDO_ORIG_PAC
	 * 
	 * @param soeSeq
	 * @return
	 */
	public DominioOrigemAtendimento buscaLaudoOrigemPaciente(Integer soeSeq) {
		DominioOrigemAtendimento retorno = null;

		AelSolicitacaoExames soe = this.getExamesLaudosFacade()
				.obterAelSolicitacaoExamesPorChavePrimaria(soeSeq);
		if (soe != null) {
			AghAtendimentos atd = soe.getAtendimento();
			if (atd != null) {
				if (DominioOrigemAtendimento.N.equals(atd.getOrigem())) {
					retorno = DominioOrigemAtendimento.I;
				} else {
					retorno = atd.getOrigem();
				}
			} else if (soe.getAtendimentoDiverso() != null) {
				retorno = DominioOrigemAtendimento.D;
			}
		}

		return retorno;
	}
	
	/**
	 * ORADB AELC_GET_JUST_LAUDO
	 * 
	 * @param seqAtendimento
	 * @param phiSeq
	 * @return
	 */
	public String buscaJustificativaLaudo(Integer seqAtendimento, Integer phiSeq) {
		List<String> informacoesClinicas = getAelSolicitacaoExameDAO()
				.buscaInformacoesClinicas(seqAtendimento, phiSeq);

		String justificativa = null;
		if (informacoesClinicas != null && !informacoesClinicas.isEmpty()) {
			justificativa = informacoesClinicas.get(0);
		}

		return justificativa;
	}

	/**
	 * ORADB AELC_GET_DTHR_RECEBE
	 * 
	 * Este método tem a finalidade de trazer a maior data de recebimento (a
	 * última) de material de um item da solicitação de exames a partir do
	 * extrato de movimentação do item.
	 * 
	 * @param itemSolicitacaoExameSoeSeq
	 * @param itemSolicitacaoExameSeqp
	 * @param situacaoItemSolicitacaoCodigo
	 * @return
	 */
	public Date buscaMaiorDataRecebimento(Integer itemSolicitacaoExameSoeSeq,
			Short itemSolicitacaoExameSeqp, String situacaoItemSolicitacaoCodigo) {
		return getAelExtratoItemSolicitacaoDAO().buscaMaiorDataRecebimento(
				itemSolicitacaoExameSoeSeq, itemSolicitacaoExameSeqp, situacaoItemSolicitacaoCodigo);
	}
	
	/**
	 * ORADB AELC_GET_MAIOR_DT_LI
	 * 
	 * Este método tem a finalidade de trazer a maior data de liberação (a
	 * última) do resultado de um item da solicitação de exames a partir da solicitacao
	 * 
	 * @param itemSolicitacaoExameSoeSeq
	 * @param unidadeFuncionalSeqp
	 * @return
	 */
	public Date buscaMaiorDataLiberacao(Integer soeSeq, Short unidadeFuncionalSeqp) {
		return getAelItemSolicitacaoExameDAO().buscaMaiorDataLiberacao(soeSeq, unidadeFuncionalSeqp);
	}
	
	protected AelSolicitacaoExameDAO getAelSolicitacaoExameDAO() {
		return aelSolicitacaoExameDAO;
	}
	
	protected AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO() {
		return aelItemSolicitacaoExameDAO;
	}
	
	protected AelExtratoItemSolicitacaoDAO getAelExtratoItemSolicitacaoDAO() {
		return aelExtratoItemSolicitacaoDAO;
	}

	protected IExamesLaudosFacade getExamesLaudosFacade() {
		return this.examesLaudosFacade;
	}
	
}
