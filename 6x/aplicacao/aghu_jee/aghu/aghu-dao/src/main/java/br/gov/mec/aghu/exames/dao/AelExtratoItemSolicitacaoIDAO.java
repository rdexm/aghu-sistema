package br.gov.mec.aghu.exames.dao;

import java.io.Serializable;
import java.util.Date;

import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacaoItemSolicitacaoExame;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.IAelExtratoItemSolicitacao;
import br.gov.mec.aghu.model.IAelItemSolicitacaoExames;

/**
 * 
 * @author cvagheti
 *
 */
public class AelExtratoItemSolicitacaoIDAO implements Serializable {

	private static final long serialVersionUID = -1790303388028564701L;
	
	@Inject
	private AelExtratoItemSolicitacaoDAO aelExtratoItemSolicitacaoDAO;
	
	@Inject
	private AelExtratoItemSolicHistDAO aelExtratoItemSolicHistDAO;

	public Date buscaMaiorDataRecebimento(IAelItemSolicitacaoExames item,
			DominioSituacaoItemSolicitacaoExame situacao) {
		if (item instanceof AelItemSolicitacaoExames) {
			return this.getAelExtratoItemSolicitacaoDAO()
					.buscaMaiorDataRecebimento(item.getId().getSoeSeq(),
							item.getId().getSeqp(), situacao.toString());
		}
		return this.getAelExtratoItemSolicHistDAO().buscaMaiorDataRecebimento(
				item.getId().getSoeSeq(), item.getId().getSeqp(), situacao.toString());
	}

	public IAelExtratoItemSolicitacao obterUltimoItemSolicitacaoSitCodigo(
			IAelItemSolicitacaoExames item, DominioSituacaoItemSolicitacaoExame situacao) {
		if (item instanceof AelItemSolicitacaoExames) {
			return this.getAelExtratoItemSolicitacaoDAO()
					.obterUltimoItemSolicitacaoSitCodigo(item.getId().getSoeSeq(),
							item.getId().getSeqp(), situacao.toString());
		}
		return this.getAelExtratoItemSolicHistDAO().obterUltimoItemSolicitacaoSitCodigo(
				item.getId().getSoeSeq(), item.getId().getSeqp(), situacao.toString());
	}

	private AelExtratoItemSolicitacaoDAO getAelExtratoItemSolicitacaoDAO() {
		return this.aelExtratoItemSolicitacaoDAO;
	}

	private AelExtratoItemSolicHistDAO getAelExtratoItemSolicHistDAO() {
		return this.aelExtratoItemSolicHistDAO;
	}

}
