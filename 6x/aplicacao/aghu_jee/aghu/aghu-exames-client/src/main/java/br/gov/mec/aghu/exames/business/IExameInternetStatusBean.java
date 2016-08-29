/**
 * 
 */
package br.gov.mec.aghu.exames.business;

import java.util.Date;

import javax.ejb.Local;

import br.gov.mec.aghu.dominio.DominioSituacaoExameInternet;
import br.gov.mec.aghu.dominio.DominioStatusExameInternet;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.RapServidores;

/**
 * Classe responsável por atualizar o status dos exames na internet
 * Precisa ser um EJB para abrir uma transação nova se deu falha no processo
 * 
 * @author twickert
 * 
 */
@Local
public interface IExameInternetStatusBean {

	/**
	 * Realizar a atualização do status de envio dos exames para o Portal
	 * @param mensagemSolicitacaoExameGrupoVO
	 * @param situacao
	 */
	void atualizarStatusInternet(
			Integer soeSeq, Integer grupoSeq,
			DominioStatusExameInternet statusAtualiza, DominioSituacaoExameInternet situacao,
			DominioStatusExameInternet statusNovo, String mensagemErro );	
	
	
	void inserirStatusInternet(AelSolicitacaoExames solicitacaoExame, AelItemSolicitacaoExames itemSolicitacaoExame,
			Date dataStatus, DominioSituacaoExameInternet situacao, DominioStatusExameInternet status, String mensagem, RapServidores servidor);
	
	void inserirStatusInternetMesmaTransacao(AelSolicitacaoExames solicitacaoExame, AelItemSolicitacaoExames itemSolicitacaoExame,
			Date dataStatus, DominioSituacaoExameInternet situacao, DominioStatusExameInternet status, String mensagem, RapServidores servidor);
}