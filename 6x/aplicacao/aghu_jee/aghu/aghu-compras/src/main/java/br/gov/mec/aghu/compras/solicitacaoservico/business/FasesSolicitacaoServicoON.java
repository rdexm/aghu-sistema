package br.gov.mec.aghu.compras.solicitacaoservico.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import java.util.Objects;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.dao.ScoFaseSolicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoSolicitacaoServicoDAO;
import br.gov.mec.aghu.compras.dao.ScoSsJnDAO;
import br.gov.mec.aghu.dominio.DominioTipoPontoParada;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.model.ScoSsJn;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class FasesSolicitacaoServicoON extends BaseBusiness {

	private static final Log LOG = LogFactory
			.getLog(FasesSolicitacaoServicoON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;

	@EJB
	private ScoSolicitacaoServicoRN scoSolicitacaoServicoRN;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private ScoFaseSolicitacaoDAO scoFaseSolicitacaoDAO;

	@Inject
	private ScoSsJnDAO scoSsJnDAO;

	@Inject
	private ScoSolicitacaoServicoDAO scoSolicitacaoServicoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -7130595457653656617L;

	public enum FasesSolicitacaoServicoONExceptionCode implements
			BusinessExceptionCode {
		MENSAGEM_RESGATAR_SS_SEM_FASES, MENSAGEM_RESGATAR_SS_SERVIDOR_DIFERENTE, MENSAGEM_RESGATAR_SS_EM_PAC, MENSAGEM_RESGATAR_SS_EM_AF, MENSAGEM_RESGATAR_SS_EXCLUIDA;
	}

	public List<ScoSsJn> listarPesquisaFasesSolicitacaoServico(Integer numero) {
		List<ScoSsJn> lista = new ArrayList<ScoSsJn>();
		lista = this.getScoSsJnDAO().listarPesquisaFasesSolicitacaoServico(
				numero);

		if (lista != null) {
			if (lista.size() > 0) {
				lista.get(0).setEtapa("Atual");
			}
			if (lista.size() > 1) {
				lista.get(1).setEtapa("Anterior");
			}
		}

		return lista;
	}

	public void resgatarSs(Integer slsNumero) throws BaseException {
		RapServidores servidor = this.servidorLogadoFacade.obterServidorLogado();
		
		// se não tem fases suficientes para voltar
		List<ScoSsJn> listaFases = this
				.listarPesquisaFasesSolicitacaoServico(slsNumero);
		if (listaFases == null || listaFases.size() < 2) {
			throw new ApplicationBusinessException(
					FasesSolicitacaoServicoONExceptionCode.MENSAGEM_RESGATAR_SS_SEM_FASES);
		}

		// se não é o servidor
		RapServidores servidorUltimoEnc = getRegistroColaboradorFacade()
				.obterServidorAtivoPorUsuario(
						listaFases.get(0).getNomeUsuario());

		if (!Objects.equals(servidorUltimoEnc, servidor)) {
			throw new ApplicationBusinessException(
					FasesSolicitacaoServicoONExceptionCode.MENSAGEM_RESGATAR_SS_SERVIDOR_DIFERENTE);
		}

		// testa SS em PAC
		List<ScoFaseSolicitacao> listaLicitacao = this
				.listarDadosLicitacaoSs(slsNumero);
		if (listaLicitacao != null && listaLicitacao.size() > 0) {
			throw new ApplicationBusinessException(
					FasesSolicitacaoServicoONExceptionCode.MENSAGEM_RESGATAR_SS_EM_PAC);
		}

		// testa SS em AF
		if (this.obterDadosAutorizacaoFornecimentoSs(slsNumero) != null) {
			throw new ApplicationBusinessException(
					FasesSolicitacaoServicoONExceptionCode.MENSAGEM_RESGATAR_SS_EM_AF);
		}

		ScoSolicitacaoServico solicitacao = this.getScoSolicitacaoServicoDAO()
				.obterPorChavePrimaria(slsNumero);
		ScoSolicitacaoServico solicitacaoOld = this
				.getScoSolicitacaoServicoDAO().obterOriginal(slsNumero);

		if (solicitacao != null) {
			if (solicitacao.getIndExclusao()) {
				throw new ApplicationBusinessException(
						FasesSolicitacaoServicoONExceptionCode.MENSAGEM_RESGATAR_SS_EXCLUIDA);
			}

			ScoPontoParadaSolicitacao ppsAnterior = this
					.getComprasCadastrosBasicosFacade().obterPontoParada(
							listaFases.get(1).getPpsCodigo());

			if (ppsAnterior != null) {
				solicitacao
						.setPontoParadaLocAtual(solicitacao.getPontoParada());
				solicitacao.setPontoParada(ppsAnterior);

				// se estiver autorizado e o ponto de parada destino é de
				// autorização da chefia, desfaz a autorização
				if (Objects.equals(solicitacao.getPontoParada()
						.getTipoPontoParada(), DominioTipoPontoParada.CH)
						&& solicitacao.getDtAutorizacao() != null) {
					solicitacao.setDtAutorizacao(null);
					solicitacao.setServidorAutorizador(null);
				}

				// se estiver no ponto de parada do coprador e tiver comprador
				// preenchido, limpa
				if (Objects.equals(solicitacao.getPontoParada()
						.getTipoPontoParada(), DominioTipoPontoParada.CP)
						&& solicitacao.getServidorComprador() != null) {
					solicitacao.setServidorComprador(null);
				}

				this.getScoSolicitacaoServicoRN().atualizarSolicitacaoServico(solicitacao, solicitacaoOld);
			}
		}
	}

	public Long countPesquisaFasesSolicitacaoServico(Integer numero) {
		return this.getScoSsJnDAO()
				.countPesquisaFasesSolicitacaoServico(numero);
	}

	public List<ScoFaseSolicitacao> listarDadosLicitacaoSs(Integer numero) {
		return this.getScoFaseSolicitacaoDAO().listarDadosLicitacaoSs(numero);
	}

	public ScoSsJn obterFaseSolicitacaoServico(Integer numero,
			Short codigoPontoParada, Integer seq) {
		return this.getScoSsJnDAO().obterFaseSolicitacaoServico(numero,
				codigoPontoParada, seq);
	}

	public ScoFaseSolicitacao obterDadosAutorizacaoFornecimentoSs(Integer numero) {
		return this.getScoFaseSolicitacaoDAO()
				.obterDadosAutorizacaoFornecimentoSs(numero);
	}

	protected ScoSsJnDAO getScoSsJnDAO() {
		return scoSsJnDAO;
	}

	protected ScoSolicitacaoServicoDAO getScoSolicitacaoServicoDAO() {
		return scoSolicitacaoServicoDAO;
	}

	private ScoFaseSolicitacaoDAO getScoFaseSolicitacaoDAO() {
		return scoFaseSolicitacaoDAO;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected IComprasCadastrosBasicosFacade getComprasCadastrosBasicosFacade() {
		return this.comprasCadastrosBasicosFacade;
	}
	
	protected ScoSolicitacaoServicoRN getScoSolicitacaoServicoRN() {
		return scoSolicitacaoServicoRN;
	}	

}
