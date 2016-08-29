package br.gov.mec.aghu.compras.solicitacaocompra.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import java.util.Objects;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoAcoesPontoParadaDAO;
import br.gov.mec.aghu.compras.dao.ScoFaseSolicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoItemAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.dao.ScoItemLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoPropostaFornecedorDAO;
import br.gov.mec.aghu.compras.dao.ScoScJnDAO;
import br.gov.mec.aghu.compras.dao.ScoSolicitacoesDeComprasDAO;
import br.gov.mec.aghu.dominio.DominioTipoPontoParada;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoAcoesPontoParada;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.model.ScoScJn;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class FasesSolicitacaoCompraON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(FasesSolicitacaoCompraON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}

@EJB
private IServidorLogadoFacade servidorLogadoFacade;

@Inject
private ScoItemAutorizacaoFornDAO scoItemAutorizacaoFornDAO;

@Inject
private ScoFaseSolicitacaoDAO scoFaseSolicitacaoDAO;

@Inject
private ScoPropostaFornecedorDAO scoPropostaFornecedorDAO;

@Inject
private ScoAcoesPontoParadaDAO scoAcoesPontoParadaDAO;

@Inject
private ScoItemLicitacaoDAO scoItemLicitacaoDAO;

@Inject
private ScoScJnDAO scoScJnDAO;

@Inject 
private ScoSolicitacoesDeComprasDAO scoSolicitacoesDeComprasDAO;

@EJB
private SolicitacaoCompraRN solicitacaoCompraRN;

	/**
	 * 
	 */
	private static final long serialVersionUID = -7130595457653656617L;
	
	public enum FasesSolicitacaoCompraONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_RESGATAR_SC_SEM_FASES, MENSAGEM_RESGATAR_SC_SERVIDOR_DIFERENTE,
		MENSAGEM_RESGATAR_SC_EM_PAC, MENSAGEM_RESGATAR_SC_EM_AF, MENSAGEM_RESGATAR_SC_EXCLUIDA;
	}
	

	
	public List<ScoScJn> listaPesquisaFasesSolicitacaoCompra(Integer numero) {
		
		List<ScoScJn> lista = this.getScoScJnDAO().listarPesquisaFasesSolicitacaoCompra(numero);
		
		for (int index = 0; index < lista.size(); index++){		
			if (index == 0){
				lista.get(index).setEtapa("Atual");
			} else {
				lista.get(index).setEtapa("Anterior");
			}
		}
		
		return lista;
	}
	
	public Long countPesquisaFasesSolicitacaoCompra(Integer numero) {

		return this.getScoScJnDAO().countPesquisaFasesSolicitacaoCompra(numero);
	}
	
	public List<ScoFaseSolicitacao> listaDadosLicitacao(Integer numero){

		return this.getScoFaseSolicitacaoDAO().listarDadosLicitacao(numero);
		
	}

	public ScoScJn obterFaseSolicitacaoCompra(Integer numero, Short codigoPontoParada, Integer seq){

		return this.getScoScJnDAO().obterFaseSolicitacaoCompra(numero, codigoPontoParada, seq);
		
	}

	public ScoFaseSolicitacao obterDadosAutorizacaoFornecimento(Integer numero){

		return this.getScoFaseSolicitacaoDAO().obterDadosAutorizacaoFornecimento(numero);
		
	}
	
	public List<ScoPropostaFornecedor> listaDataDigitacaoPropostaForn(Integer numero){

		return this.getScoPropostaFornecedorDAO().listarDataDigitacaoPropostaForn(numero);
		
	}
	
	public ScoItemLicitacao obterDataDigitacaoPublicacaoLicitacao(Integer lctnumero, Short numero){

		return this.getScoItemLicitacaoDAO().obterItemLicitacaoPorNumeroLicitacaoENumeroItem(lctnumero,numero);		
	}
	
	public void resgatarSc(Integer slcNumero) throws ApplicationBusinessException {
		RapServidores servidor = this.servidorLogadoFacade.obterServidorLogado();
		// se não tem fases suficientes para voltar
		List<ScoScJn> listaFases = this.listaPesquisaFasesSolicitacaoCompra(slcNumero);
		if (listaFases == null || listaFases.size() < 2) {
			throw new ApplicationBusinessException(
					FasesSolicitacaoCompraONExceptionCode.MENSAGEM_RESGATAR_SC_SEM_FASES);
		}
		
		// se não é o servidor
		if (!listaFases.get(0).getServidor().equals(servidor)) {
			throw new ApplicationBusinessException(
					FasesSolicitacaoCompraONExceptionCode.MENSAGEM_RESGATAR_SC_SERVIDOR_DIFERENTE);	
		}
		
		// testa SC em PAC
		List<ScoFaseSolicitacao> listaLicitacao = this.listaDadosLicitacao(slcNumero);
		if (listaLicitacao != null && listaLicitacao.size() > 0) {
			throw new ApplicationBusinessException(
					FasesSolicitacaoCompraONExceptionCode.MENSAGEM_RESGATAR_SC_EM_PAC);
		}
		
		// testa SC em AF
		if (this.obterDadosAutorizacaoFornecimento(slcNumero) != null) {
			throw new ApplicationBusinessException(
					FasesSolicitacaoCompraONExceptionCode.MENSAGEM_RESGATAR_SC_EM_AF);
		}
		
		ScoSolicitacaoDeCompra solicitacao = this.getScoSolicitacoesDeComprasDAO().obterPorChavePrimaria(slcNumero);
		ScoSolicitacaoDeCompra solicitacaoOld = this.getScoSolicitacoesDeComprasDAO().obterOriginal(slcNumero);
		
		if (solicitacao != null) {
			if (solicitacao.getExclusao()) {
				throw new ApplicationBusinessException(
						FasesSolicitacaoCompraONExceptionCode.MENSAGEM_RESGATAR_SC_EXCLUIDA);
			}
			
			solicitacao.setPontoParada(solicitacao.getPontoParadaProxima());
			solicitacao.setPontoParadaProxima(listaFases.get(1).getPontoParadaSolicitacao());			
			
			// se estiver autorizado e o ponto de parada destino é de autorização da chefia, desfaz a autorização
			if (Objects.equals(solicitacao.getPontoParadaProxima().getTipoPontoParada(), DominioTipoPontoParada.CH) && solicitacao.getDtAutorizacao() != null) {
				solicitacao.setDtAutorizacao(null);
				solicitacao.setServidorAutorizacao(null);
			}

			// se estiver no ponto de parada do coprador e tiver comprador preenchido, limpa
			if (Objects.equals(solicitacao.getPontoParada().getTipoPontoParada(), DominioTipoPontoParada.CP) && solicitacao.getServidorCompra() != null) {
				solicitacao.setServidorCompra(null);
			}
			
			this.getSolicitacaoCompraRN().atualizarSolicitacaoCompra(solicitacao, solicitacaoOld);
		}
	}
	

	
	
	public ScoItemAutorizacaoForn obterDadosItensAutorizacaoFornecimento(
			Integer afNumero, Integer numero) {
		return this.getScoItemAutorizacaoFornDAO()
				.obterDadosItensAutorizacaoFornecimento(afNumero, numero);
	}
	
	public List<ScoAcoesPontoParada> listaAcoesPontoParada(Integer numero, Short codigoPontoParada, DominioTipoSolicitacao tipoSolicitacao){
		return this.getScoAcoesPontoParadaDAO().listarAcoesPontoParada(numero, codigoPontoParada, tipoSolicitacao);		
	}
		
	private ScoScJnDAO getScoScJnDAO() {
			return scoScJnDAO;
	}
	
	private ScoItemAutorizacaoFornDAO getScoItemAutorizacaoFornDAO() {
		return scoItemAutorizacaoFornDAO;
    }
	
	private ScoPropostaFornecedorDAO getScoPropostaFornecedorDAO() {
		return scoPropostaFornecedorDAO;
    }

	
	private ScoFaseSolicitacaoDAO getScoFaseSolicitacaoDAO() {
			return scoFaseSolicitacaoDAO;
	}
	
	private ScoItemLicitacaoDAO getScoItemLicitacaoDAO() {
		return scoItemLicitacaoDAO;
    }
	
	private ScoAcoesPontoParadaDAO getScoAcoesPontoParadaDAO() {
		return scoAcoesPontoParadaDAO;
	}

	private ScoSolicitacoesDeComprasDAO getScoSolicitacoesDeComprasDAO() {
		return scoSolicitacoesDeComprasDAO;
	}
	
	private SolicitacaoCompraRN getSolicitacaoCompraRN() {
		return solicitacaoCompraRN;
	}	

	
}
