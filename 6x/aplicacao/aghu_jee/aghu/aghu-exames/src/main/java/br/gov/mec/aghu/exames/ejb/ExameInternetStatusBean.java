/**
 * 
 */
package br.gov.mec.aghu.exames.ejb;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioSituacaoExameInternet;
import br.gov.mec.aghu.dominio.DominioStatusExameInternet;
import br.gov.mec.aghu.exames.business.IExameInternetStatusBean;
import br.gov.mec.aghu.exames.dao.AelExameInternetGrupoDAO;
import br.gov.mec.aghu.exames.dao.AelExameInternetStatusDAO;
import br.gov.mec.aghu.model.AelExameInternetStatus;
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
@Stateless
public class ExameInternetStatusBean implements IExameInternetStatusBean {

	@Inject
	private AelExameInternetGrupoDAO aelExameInternetGrupoDAO;

	@Inject
	private AelExameInternetStatusDAO aelExameInternetStatusDAO;
	
	/**
	 * Realizar a atualização do status de envio dos exames para o Portal
	 * @param mensagemSolicitacaoExameGrupoVO
	 * @param situacao
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void atualizarStatusInternet(
			Integer soeSeq, Integer grupoSeq,
			DominioStatusExameInternet statusAtualiza, DominioSituacaoExameInternet situacao,
			DominioStatusExameInternet statusNovo, String mensagemErro ) {

		if(soeSeq == null || grupoSeq == null || situacao == null){
			throw new IllegalArgumentException("Parâmetros não informados.");
		}

		//Busca todos os itens de exames, conforme necessidade de atualização
		List<AelExameInternetStatus> listExameInternetStatus = this
				.getAelExameInternetStatusDAO().buscarExameInternetStatus(soeSeq, grupoSeq, statusAtualiza, DominioSituacaoExameInternet.N);

		for (AelExameInternetStatus exameInternetStatus : listExameInternetStatus) {
			exameInternetStatus.setSituacao(situacao);
			if(DominioSituacaoExameInternet.E.equals(situacao) && !StringUtils.isEmpty(mensagemErro)){
				exameInternetStatus
						.setMensagem(mensagemErro.length() > 4000 ? mensagemErro
								.substring(0, 4000) : mensagemErro);
			}
			exameInternetStatus.setDataHoraStatus(new Date());
			this.getAelExameInternetStatusDAO().atualizar(exameInternetStatus);
			
			if(statusNovo != null){
				inserirStatusInternet(
						exameInternetStatus.getSolicitacaoExames(),
						exameInternetStatus.getItemSolicitacaoExames(),
						exameInternetStatus.getDataHoraStatus(),
						DominioStatusExameInternet.EC.equals(statusNovo) ? DominioSituacaoExameInternet.R : DominioSituacaoExameInternet.N,
						statusNovo, null, null);
			}
		}

	}

	/**
	 * Realizar a atualização do status de envio dos exames para o Portal
	 * @param mensagemSolicitacaoExameGrupoVO
	 * @param situacao
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void inserirStatusInternet(AelSolicitacaoExames solicitacaoExame, AelItemSolicitacaoExames itemSolicitacaoExame,
			Date dataStatus, DominioSituacaoExameInternet situacao, DominioStatusExameInternet status, String mensagem, RapServidores servidor) {

		if(solicitacaoExame == null || situacao == null || status == null){
			throw new IllegalArgumentException("Parâmetros não informados.");
		}

		AelExameInternetStatus exameInternetStatus = new AelExameInternetStatus();

		exameInternetStatus.setSolicitacaoExames(solicitacaoExame);

		if (itemSolicitacaoExame != null) {
			exameInternetStatus.setItemSolicitacaoExames(itemSolicitacaoExame);
			exameInternetStatus.setExameInternetGrupo(this.getAelExameInternetGrupoDAO().buscarExameInternetGrupoArea(
					itemSolicitacaoExame.getId().getSoeSeq(),
					itemSolicitacaoExame.getId().getSeqp()));			
		}
		
		exameInternetStatus.setDataHoraStatus(new Date());
		exameInternetStatus.setSituacao(situacao);
		exameInternetStatus.setStatus(status);
		exameInternetStatus.setMensagem(mensagem);		
		exameInternetStatus.setServidor(servidor);
		
		this.getAelExameInternetStatusDAO().persistir(exameInternetStatus);

	}
	
	/**
	 * Realizar a atualização do status de envio dos exames para o Portal na mesma transação
	 * @param mensagemSolicitacaoExameGrupoVO
	 * @param situacao
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void inserirStatusInternetMesmaTransacao(AelSolicitacaoExames solicitacaoExame, AelItemSolicitacaoExames itemSolicitacaoExame,
			Date dataStatus, DominioSituacaoExameInternet situacao, DominioStatusExameInternet status, String mensagem, RapServidores servidor) {

		if(solicitacaoExame == null || situacao == null || status == null){
			throw new IllegalArgumentException("Parâmetros não informados.");
		}

		AelExameInternetStatus exameInternetStatus = new AelExameInternetStatus();

		exameInternetStatus.setSolicitacaoExames(solicitacaoExame);

		if (itemSolicitacaoExame != null) {
			exameInternetStatus.setItemSolicitacaoExames(itemSolicitacaoExame);
			exameInternetStatus.setExameInternetGrupo(this.getAelExameInternetGrupoDAO().buscarExameInternetGrupoArea(
					itemSolicitacaoExame.getId().getSoeSeq(),
					itemSolicitacaoExame.getId().getSeqp()));			
		}
		
		exameInternetStatus.setDataHoraStatus(new Date());
		exameInternetStatus.setSituacao(situacao);
		exameInternetStatus.setStatus(status);
		exameInternetStatus.setMensagem(mensagem);		
		exameInternetStatus.setServidor(servidor);
		
		this.getAelExameInternetStatusDAO().persistir(exameInternetStatus);

	}
	
	protected AelExameInternetGrupoDAO getAelExameInternetGrupoDAO() {
		return aelExameInternetGrupoDAO;
	}

	protected AelExameInternetStatusDAO getAelExameInternetStatusDAO() {
		return aelExameInternetStatusDAO;
	}
	
}