package br.gov.mec.aghu.paciente.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.AipMovimentacaoProntuarioJn;
import br.gov.mec.aghu.model.AipMovimentacaoProntuarios;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.dao.AipMovimentacaoProntuarioJnDAO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class MovimentacaoProntuarioJournalON extends BaseBusiness {
	
	private static final Log LOG = LogFactory.getLog(MovimentacaoProntuarioJournalON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AipMovimentacaoProntuarioJnDAO aipMovimentacaoProntuarioJnDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 8747466743527817108L;

	/**
	 * Método que conduz a geração de uma entrada na tabela de journal na
	 * remoção e atualização de uma movimentação de prontuário.
	 * @param usuarioLogado 
	 * 
	 * @param AipMovimentacaoProntuarios
	 */	
	public void observarPersistenciaMovimentacaoProntuario(
			AipMovimentacaoProntuarios movimentacaoProntuarioAnterior,
			DominioOperacoesJournal operacaoJournal, String usuarioLogado) {
		
		// Gera journal para operações DELETE e UPDATE
		if (DominioOperacoesJournal.DEL.equals(operacaoJournal)
				|| DominioOperacoesJournal.UPD.equals(operacaoJournal)) {
			
			// Gera journal do movimentação de prontuario (tanto para UPD como
			// para DEL). Na trigger não existem validações para sua geração.
			this.gerarJournalPersistenciaMovimentacaoProntuario(
					movimentacaoProntuarioAnterior, operacaoJournal, usuarioLogado);
		}
	}

	/**
	 * Método responsável pela agregação e persistencia do objeto
	 * AipMovimentacaoProntuarioJn, que representa o journal da movimentação de
	 * prontuário. Esse método gera o registro de journal na atualiação e
	 * remoção de um objeto de AipMovimentacaoProntuarios.
	 * @param usuarioLogado 
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	private void gerarJournalPersistenciaMovimentacaoProntuario(
			AipMovimentacaoProntuarios movimentacaoProntuario,
			DominioOperacoesJournal operacao, String usuarioLogado) {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AipMovimentacaoProntuarioJn jn = BaseJournalFactory.getBaseJournal(operacao, AipMovimentacaoProntuarioJn.class, servidorLogado.getUsuario());

		jn.setSeq(movimentacaoProntuario.getSeq());
		jn.setObservacoes(movimentacaoProntuario
				.getObservacoes());
		jn.setVolumes(movimentacaoProntuario
				.getVolumes());
		jn.setTipoEnvio(movimentacaoProntuario
				.getTipoEnvio());
		jn.setSituacao(movimentacaoProntuario
				.getSituacao());
		jn.setDataMovimento(movimentacaoProntuario
				.getDataMovimento());
		jn.setDataRetirada(movimentacaoProntuario
				.getDataRetirada());
		jn.setDataDevolucao(movimentacaoProntuario
				.getDataDevolucao());
		jn.setPacCodigo(movimentacaoProntuario
				.getPaciente() == null ? null : movimentacaoProntuario
				.getPaciente().getCodigo());
		jn.setSerMatricula(movimentacaoProntuario
				.getServidor() == null ? null : movimentacaoProntuario
				.getServidor().getId().getMatricula());
		jn.setSerVinCodigo(movimentacaoProntuario
				.getServidor() == null ? null : movimentacaoProntuario
				.getServidor().getId().getVinCodigo());
		jn
				.setSerMatriculaRetirado(movimentacaoProntuario
						.getServidorRetirado() == null ? null
						: movimentacaoProntuario.getServidorRetirado().getId()
								.getMatricula());
		jn
				.setSerVinCodigoRetirado(movimentacaoProntuario
						.getServidorRetirado() == null ? null
						: movimentacaoProntuario.getServidorRetirado().getId()
								.getVinCodigo());
		jn.setSopSeq(movimentacaoProntuario
				.getSolicitante() == null ? null : movimentacaoProntuario
				.getSolicitante().getSeq());
		jn.setSlpCodigo(movimentacaoProntuario
				.getSolicitacao() == null ? null : movimentacaoProntuario
				.getSolicitacao().getCodigo());
		jn.setLocal(movimentacaoProntuario
				.getLocal());

		this.getAipMovimentacaoProntuarioJnDAO().persistir(jn);
		this.getAipMovimentacaoProntuarioJnDAO().flush();
	}
	
	protected AipMovimentacaoProntuarioJnDAO getAipMovimentacaoProntuarioJnDAO() {
		return aipMovimentacaoProntuarioJnDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
