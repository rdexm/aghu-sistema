package br.gov.mec.aghu.compras.pac.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.dao.ScoCondicaoPgtoLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoFaseSolicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoItemAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.dao.ScoItemLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoItemPropostaFornecedorDAO;
import br.gov.mec.aghu.compras.dao.ScoLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoParcelasPagamentoDAO;
import br.gov.mec.aghu.compras.dao.ScoPontoParadaSolicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoScJnDAO;
import br.gov.mec.aghu.compras.dao.ScoSolicitacaoServicoDAO;
import br.gov.mec.aghu.compras.dao.ScoSolicitacoesDeComprasDAO;
import br.gov.mec.aghu.compras.dao.ScoSsJnDAO;
import br.gov.mec.aghu.compras.solicitacaocompra.business.ISolicitacaoComprasFacade;
import br.gov.mec.aghu.compras.solicitacaoservico.business.ISolicitacaoServicoFacade;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.ScoCondicaoPgtoLicitacao;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoItemLicitacaoId;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedor;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoLoteLicitacao;
import br.gov.mec.aghu.model.ScoLoteLicitacaoId;
import br.gov.mec.aghu.model.ScoParcelasPagamento;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.suprimentos.vo.ScoItemPacVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;


@Stateless
public class ScoItemLicitacaoON extends BaseBusiness {

	@EJB
	private DadosItemLicitacaoON dadosItemLicitacaoON;
	@EJB
	private ScoItemLicitacaoRN scoItemLicitacaoRN;
	@EJB
	private ScoCondicaoPgtoLicitacaoRN scoCondicaoPgtoLicitacaoRN;
	@EJB
	private ScoItemPropostaFornecedorRN scoItemPropostaFornecedorRN;

	private static final Log LOG = LogFactory.getLog(ScoItemLicitacaoON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private ScoSolicitacoesDeComprasDAO scoSolicitacoesDeComprasDAO;

	@Inject
	private ScoItemAutorizacaoFornDAO scoItemAutorizacaoFornDAO;

	@Inject
	private ScoLicitacaoDAO scoLicitacaoDAO;

	@Inject
	private ScoPontoParadaSolicitacaoDAO scoPontoParadaSolicitacaoDAO;

	@Inject
	private ScoSolicitacaoServicoDAO scoSolicitacaoServicoDAO;

	@EJB
	private IComprasFacade comprasFacade;

	@Inject
	private ScoFaseSolicitacaoDAO scoFaseSolicitacaoDAO;

	@Inject
	private ScoItemPropostaFornecedorDAO scoItemPropostaFornecedorDAO;

	@EJB
	private ISolicitacaoServicoFacade solicitacaoServicoFacade;

	@Inject
	private ScoCondicaoPgtoLicitacaoDAO scoCondicaoPgtoLicitacaoDAO;

	@Inject
	private ScoItemLicitacaoDAO scoItemLicitacaoDAO;

	@Inject
	private ScoSsJnDAO scoSsJnDAO;

	@Inject
	private ScoParcelasPagamentoDAO scoParcelasPagamentoDAO;

	@Inject
	private ScoScJnDAO scoScJnDAO;

	@EJB
	private ISolicitacaoComprasFacade solicitacaoComprasFacade;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	private static final long serialVersionUID = -893064524258218868L;

	public enum ScoItemLicitacaoONExceptionCode implements BusinessExceptionCode { 
		MENSAGEM_ITEMPAC_M11, MENSAGEM_ITEMPAC_M02, MENSAGEM_ITEMPAC_M08, MENSAGEM_ERRO_CLONE, MENSAGEM_ITENS_PAC_ORDENADOS,
		MENSAGEM_NUMERO_ITEM_OBRIGATORIO, MS03_SEM_LICITACAO_SELECIONADA, MENSAGEM_ERRO_LOTE_ITENS_PAC;
	}
			
	/**
	 * Verifica se o item de licitacao possui proposta
	 * @param numLicitacao
	 * @param numero
	 * @param propostaEscolhida
	 * @return Boolean
	 */
	public Boolean verificarLicitacaoProposta(Integer numLicitacao, Short numero, Boolean propostaEscolhida) {
		Boolean existeLicitacaoProposta = Boolean.FALSE;
		
		if (propostaEscolhida) {
			existeLicitacaoProposta = (this.getScoItemPropostaFornecedorDAO().
					obterQuantidadePropostasEscolhidasPeloNumLicitacaoENumeroItem (numLicitacao, numero) > 0);
		} else {
			existeLicitacaoProposta = ((this.getScoItemPropostaFornecedorDAO().
					obterItemPropostaFornecedorPeloNumLicitacaoENumeroItem(numLicitacao, numero)).size() > 0);
		}
		return existeLicitacaoProposta;
	}
	
	/**
	 * Verifica se um item de licitação pode ser editado
	 * @param numeroLicitacao
	 * @param validarProposta
	 * @param validarPublicacao
	 * @return Boolean
	 */
	public Boolean verificarEdicaoItensPac(Integer numeroLicitacao, Boolean validarProposta, Boolean validarPublicacao) {
		Boolean verificarProposta = Boolean.FALSE;
		Boolean verificarPublicacao = Boolean.FALSE;
		
		if (numeroLicitacao == null){
			return false;
		}
		
		if (validarProposta) {
			verificarProposta = (this.getScoItemPropostaFornecedorDAO().obterQuantidadePropostasEscolhidasPeloItlLctNumero(numeroLicitacao) > 0);
		}
		
		if (validarPublicacao) {
			ScoLicitacao licitacao = this.getScoLicitacaoDAO().obterPorChavePrimaria(numeroLicitacao);
			if (licitacao != null && licitacao.getDtPublicacao() != null && licitacao.getDtPublicacao().before(new Date())) {
				verificarPublicacao = Boolean.TRUE;
			}
		}
		if (validarProposta && !validarPublicacao) {
			return verificarProposta;
		} else if (!validarProposta && validarPublicacao) {
			return verificarPublicacao;
		} else {
			return (verificarProposta || verificarPublicacao);
		}
	}
	
	
	/**
	 * Verifica se um item de licitação pode ser editado
	 * @param numeroLicitacao
	 * @param validarProposta
	 * @param validarPublicacao
	 * @return Boolean
	 */
	public Boolean verificarEdicaoItensPacPropostaLote(Integer numeroLicitacao, Short numero) {
		Boolean verificarProposta = Boolean.FALSE;
		Boolean verificarLote = Boolean.FALSE;
		
		if (numeroLicitacao == null){
			return false;
		}
		
		verificarProposta = verificarLicitacaoProposta(numeroLicitacao,numero, true);
		
		ScoItemLicitacao itemLicitacao = this.scoItemLicitacaoDAO.obterItemLicitacaoPorNumeroLicitacaoENumeroItem(numeroLicitacao, numero);
		
		verificarLote = (itemLicitacao != null && itemLicitacao.getLoteLicitacao() != null && itemLicitacao.getLoteLicitacao().getId() != null);
		
		return (verificarProposta || verificarLote);
				
		
	}
	
	public ScoPontoParadaSolicitacao obterPontoParadaAnteriorItemLicitacao(ScoItemLicitacao itemLicitacao){
		ScoPontoParadaSolicitacao pontoParadaAtual =  null;
		
		List<ScoFaseSolicitacao> listaFasesSolicitacao = this.getScoFaseSolicitacaoDAO().
				obterFasesSolicitacaoPorNumeroLicitacaoENumeroItemLicitacao(itemLicitacao.getId().getLctNumero(), itemLicitacao.getId().getNumero());

		for (ScoFaseSolicitacao item : listaFasesSolicitacao) {
			
			if (item.getSolicitacaoDeCompra() != null) {
				ScoSolicitacaoDeCompra solCompras =  this.getScoSolicitacoesDeComprasDAO().
						obterSolicitacaoCompraPorNumero(item.getSolicitacaoDeCompra().getNumero());
				pontoParadaAtual = solCompras.getPontoParada();
			} 

			if (item.getSolicitacaoServico() != null){
				ScoSolicitacaoServico solServico = this.getScoSolicitacaoServicoDAO().
						obterScoSolicitacaoServicoPeloId(item.getSolicitacaoServico().getNumero());
				
				pontoParadaAtual = solServico.getPontoParadaLocAtual();				
			}
		}	
				
		return pontoParadaAtual;
	}
	
	/**
	 * Exclui logicamente (inativa) um item de licitacao
	 * @param numeroLicitacao
	 * @param numeroItem
	 * @param motivoExclusao
	 * @throws BaseException 
	 */
	public void realizarExclusaoLogica(Integer numeroLicitacao, Short numeroItem, String motivoExclusao) throws BaseException {
		// exclusao logica, tem que ter o motivo
		if (StringUtils.isBlank(motivoExclusao)) {
			throw new ApplicationBusinessException(
					ScoItemLicitacaoONExceptionCode.MENSAGEM_ITEMPAC_M11);								
		}			
		// atualiza o item da licitacao
		ScoItemLicitacao itemLicitacao = this.getScoItemLicitacaoDAO().
				obterItemLicitacaoPorNumeroLicitacaoENumeroItem(numeroLicitacao, numeroItem);
		
		itemLicitacao.setExclusao(Boolean.TRUE);
		itemLicitacao.setDtExclusao(new Date());
		itemLicitacao.setMotivoExclusao(motivoExclusao);
		
		alternarPontosParadaSolicitacao(numeroLicitacao, numeroItem);
				
		// pega as fases ligadas ao item de licitacao
		List<ScoFaseSolicitacao> listaFasesSolicitacao = this.getScoFaseSolicitacaoDAO().
				obterFasesSolicitacaoPorNumeroLicitacaoENumeroItemLicitacao(numeroLicitacao, numeroItem);
		
		for (ScoFaseSolicitacao item : listaFasesSolicitacao) {
						
			ScoFaseSolicitacao faseSolicitacaoOld = this.getScoFaseSolicitacaoDAO().obterOriginal(item);

			// inativa a fase
			item.setExclusao(Boolean.TRUE);
			item.setDtExclusao(new Date());
			try {
				this.getComprasFacade().atualizarScoFaseSolicitacao(item, faseSolicitacaoOld);
			} catch (BaseException e) {
				throw new ApplicationBusinessException(e);
			}			
		}	
		
		Long numNaoExcluidos = this.getScoItemLicitacaoDAO().pesquisarItemLicitacaoPorNumLicitacaoEIndExclusaoCount(numeroLicitacao,false);
				
		if (numNaoExcluidos == null || numNaoExcluidos == 0){
			ScoLicitacao licitacao = this.scoLicitacaoDAO.obterPorChavePrimaria(numeroLicitacao);
			licitacao.setDtExclusao(new Date());
			licitacao.setServidorExcluido(servidorLogadoFacade.obterServidorLogado());
			licitacao.setExclusao(true);
			licitacao.setMotivoExclusao(motivoExclusao);
			this.scoLicitacaoDAO.atualizar(licitacao);
		}
	}

	/**
	 * Realiza exclusao fisica de um item de licitação
	 * @param numeroLicitacao
	 * @param numeroItem
	 * @throws ApplicationBusinessException 
	 */
	public void realizarExclusaoFisica(Integer numeroLicitacao, Short numeroItem) throws BaseException {

		//alternarPontosParadaSolicitacao(numeroLicitacao, numeroItem);
		
		// exclusao fisica, nao precisa do motivo
		List<ScoFaseSolicitacao> listaFasesSolicitacao = this.getScoFaseSolicitacaoDAO().
				obterFasesSolicitacaoPorNumeroLicitacaoENumeroItemLicitacao(numeroLicitacao, numeroItem);
		
		for (ScoFaseSolicitacao item : listaFasesSolicitacao) {
			this.getComprasFacade().excluirFaseSolicitacao(item);			
		}
		
		ScoItemLicitacao itemLicitacao = this.getScoItemLicitacaoDAO().
				obterItemLicitacaoPorNumeroLicitacaoENumeroItem(numeroLicitacao, numeroItem);
		this.getScoItemLicitacaoRN().excluirItemLicitacao(itemLicitacao);						
	}
	
	public void alternarPontosParadaSolicitacao(Integer numeroLicitacao, Short numeroItem) throws BaseException{
		// pega as fases ligadas ao item de licitacao
		List<ScoFaseSolicitacao> listaFasesSolicitacao = this.getScoFaseSolicitacaoDAO().
				obterFasesSolicitacaoPorNumeroLicitacaoENumeroItemLicitacao(numeroLicitacao, numeroItem);
		
		ScoPontoParadaSolicitacao pontoParadaAnterior;
		
		for (ScoFaseSolicitacao item : listaFasesSolicitacao) {
			
			if (item.getSolicitacaoDeCompra() != null) {
				ScoSolicitacaoDeCompra solCompras =  this.getScoSolicitacoesDeComprasDAO().
						obterPorChavePrimaria(item.getSolicitacaoDeCompra().getNumero());
				
				ScoSolicitacaoDeCompra solComprasOld =  this.getScoSolicitacoesDeComprasDAO().
						obterOriginal(item.getSolicitacaoDeCompra().getNumero());
				
			    pontoParadaAnterior = solCompras.getPontoParada();
			    
				solCompras.setPontoParada(solCompras.getPontoParadaProxima());
				solCompras.setPontoParadaProxima(pontoParadaAnterior);			
				this.getSolicitacaoComprasFacade().atualizarScoSolicitacaoDeCompra(solCompras, solComprasOld);
			} else {
				ScoSolicitacaoServico solServico = this.getScoSolicitacaoServicoDAO().
						obterPorChavePrimaria(item.getSolicitacaoServico().getNumero());
				ScoSolicitacaoServico solServicoOld = this.getScoSolicitacaoServicoDAO().
						obterOriginal(item.getSolicitacaoServico().getNumero());
					
				pontoParadaAnterior = solServico.getPontoParadaLocAtual();
				solServico.setPontoParadaLocAtual(solServico.getPontoParada());
				solServico.setPontoParada(pontoParadaAnterior);

				this.getSolicitacaoServicoFacade().atualizarSolicitacaoServico(solServico, solServicoOld);
			}	
			break;
		}			
	}
	
	
	/**
	 * Exclui um item do PAC, avaliando se a exclusao deve ser lógica ou física
	 * @param numeroLicitacao
	 * @param numeroItem
	 * @param motivoExclusao
	 * @throws BaseException 
	 */
	public void excluirItemPac(Integer numeroLicitacao, Short numeroItem, String motivoExclusao, Boolean indExcluido) throws BaseException {
		
		// valida se tem proposta escolhida
		if (verificarLicitacaoProposta(numeroLicitacao, numeroItem, true)) {
			throw new ApplicationBusinessException(
					ScoItemLicitacaoONExceptionCode.MENSAGEM_ITEMPAC_M02,
					numeroItem);
		}
		
		// verifica se tem proposta para escolher se vai fazer exclusao física ou lógica
		if (verificarLicitacaoProposta(numeroLicitacao, numeroItem, false) || !indExcluido) {
			this.realizarExclusaoLogica(numeroLicitacao, numeroItem, motivoExclusao);
		} else {
			this.realizarExclusaoFisica(numeroLicitacao, numeroItem);
		}
	}
	
	/**
	 * Reativa um item de licitação que foi previamente excluido logicamente
	 * @param numeroLicitacao
	 * @param numeroItem
	 * @throws BaseException 
	 */
	public void reativarItemPac(Integer numeroLicitacao, Short numeroItem) throws BaseException {
		// realiza a reativacao
		ScoItemLicitacao itemLicitacao = this.getScoItemLicitacaoDAO().
				obterItemLicitacaoPorNumeroLicitacaoENumeroItem(numeroLicitacao, numeroItem);
		
		this.verificarPropostasItemLicitacaoEmAf(itemLicitacao.getId().getLctNumero(),	
				itemLicitacao.getId().getNumero());
		
		itemLicitacao.setDtExclusao(null);
		itemLicitacao.setExclusao(Boolean.FALSE);
		itemLicitacao.setMotivoExclusao(null);
		if (itemLicitacao.getMotivoCancel() != null) {
			itemLicitacao.setMotivoCancel(null);
		}

		
		this.getScoItemLicitacaoRN().atualizarItemLicitacao(itemLicitacao);
		
		alternarPontosParadaSolicitacao(numeroLicitacao, numeroItem);
		
		// pega as fases ligadas ao item de licitacao
		List<ScoFaseSolicitacao> listaFasesSolicitacao = this.getScoFaseSolicitacaoDAO().
				obterFasesSolicitacaoPorNumeroLicitacaoENumeroItemLicitacao(numeroLicitacao, numeroItem);
		
		for (ScoFaseSolicitacao item : listaFasesSolicitacao) {			
			ScoFaseSolicitacao faseSolicitacaoOld = this.getScoFaseSolicitacaoDAO().obterOriginal(item);

			// reativa a fase
			item.setExclusao(Boolean.FALSE);
			item.setDtExclusao(null);
			try {
				this.getComprasFacade().atualizarScoFaseSolicitacao(item, faseSolicitacaoOld);
			} catch (BaseException e) {
				throw new ApplicationBusinessException(e);
			}	
		}	
		
        /**Long numNaoExcluidos = this.getScoItemLicitacaoDAO().pesquisarItemLicitacaoPorNumLicitacaoEIndExclusaoCount(numeroLicitacao,false);
		
		
		if (numNaoExcluidos != null && numNaoExcluidos > 0){
			ScoLicitacao licitacao = this.scoLicitacaoDAO.obterPorChavePrimaria(numeroLicitacao);			
			licitacao.setDtExclusao(null);
			licitacao.setServidorExcluido(null);
			licitacao.setExclusao(false);
			licitacao.setMotivoExclusao(null);
			this.scoLicitacaoDAO.atualizar(licitacao);
		}	*/	
	}
	
	/**
	 * Valida a existência de duplicidade na sequencia numérica dos itens de uma AF
	 * @param listaItens
	 * @throws ApplicationBusinessException
	 */
	public void validarSequenciaEDuplicidadeItens(List <ScoItemPacVO> listaItens) throws ApplicationBusinessException {
		Set<Short> listaNumeros = new HashSet<Short>();
		
		for (ScoItemPacVO item : listaItens) {
			
			if (!listaNumeros.add(item.getNumeroItem())) {
				throw new ApplicationBusinessException(
						ScoItemLicitacaoONExceptionCode.MENSAGEM_ITEMPAC_M08);
			}
		}
	}
	
	/**
	 * Troca de posicao entre itens do mesmo pac
	 * @param numeroLicitacao
	 * @param listCompleta
	 * @param listAlteracoes
	 * @throws ApplicationBusinessException
	 * @throws BaseException 
	 */
	private void trocarPosicaoItemPac(Integer numeroLicitacao, List<ScoItemPacVO> listCompleta, List<ScoItemPacVO> listAlteracoes) throws BaseException {
		List<ScoItemLicitacao> listItensInsercao = new ArrayList<ScoItemLicitacao>();
		List<ScoItemLicitacaoId> listItensRemocao = new ArrayList<ScoItemLicitacaoId>();
		List<ScoItemLicitacaoId> listIdVerificacao = new ArrayList<ScoItemLicitacaoId>();
		
		for (ScoItemPacVO item : listCompleta) {
			if (!item.getNumeroItem().equals(item.getItemLicitacaoOriginal().getId().getNumero())) {
				// guarda os dados antigos
				ScoItemLicitacao novoItemLicitacao = this.clonarItemLicitacao(item.getItemLicitacaoOriginal());
				
				// desatacha para evitar um update indesejado no flush
				this.getScoItemLicitacaoDAO().desatachar(novoItemLicitacao);
				
				Set<ScoFaseSolicitacao> listaFasesSrc = new HashSet<ScoFaseSolicitacao>();
				for(ScoFaseSolicitacao fase: item.getItemLicitacaoOriginal().getFasesSolicitacao()){
					ScoFaseSolicitacao faseNova = new ScoFaseSolicitacao();
					faseNova.setNumero(fase.getNumero());
					faseNova.setTipo(fase.getTipo());
					faseNova.setSolicitacaoDeCompra(fase.getSolicitacaoDeCompra());
					faseNova.setSolicitacaoServico(fase.getSolicitacaoServico());
					faseNova.setItemLicitacao(fase.getItemLicitacao());
					faseNova.setItemAutorizacaoForn(fase.getItemAutorizacaoForn());
					faseNova.setExclusao(fase.getExclusao());
					faseNova.setDtExclusao(fase.getDtExclusao());
					
					/*this.getScoFaseSolicitacaoDAO().desatachar(fase);
					CoreUtil.cloneBean(fase.getSolicitacaoDeCompra());
					CoreUtil.cloneBean(fase.getSolicitacaoServico());
					CoreUtil.cloneBean(fase.getItemLicitacao());
					CoreUtil.cloneBean(fase.getItemAutorizacaoForn());*/
					listaFasesSrc.add(faseNova);
				}
				
				List<ScoCondicaoPgtoLicitacao> listaCondPag = this.getScoCondicaoPgtoLicitacaoDAO().
						pesquisarCondicaoPagamentoLicitacao(numeroLicitacao, item.getItemLicitacaoOriginal().getId().getNumero(), false);
				Set<ScoCondicaoPgtoLicitacao> listaInsercaoCondPag = new HashSet<ScoCondicaoPgtoLicitacao>();
				if (listaCondPag != null && !listaCondPag.isEmpty()) {
					
					for(ScoCondicaoPgtoLicitacao cpg: listaCondPag){
						this.getScoCondicaoPgtoLicitacaoDAO().desatachar(cpg);
						CoreUtil.cloneBean(cpg.getLicitacao());
						CoreUtil.cloneBean(cpg.getFormaPagamento());
						CoreUtil.cloneBean(cpg.getItemLicitacao());
						
						Set<ScoParcelasPagamento> listaParcelasInsercao = new HashSet<ScoParcelasPagamento>();
						List<ScoParcelasPagamento> listaParcelas = this.getScoParcelasPagamentoDAO().obterParcelasPagamento(cpg.getSeq());
						
						for(ScoParcelasPagamento parc : listaParcelas) {
							this.getScoParcelasPagamentoDAO().desatachar(parc);
							CoreUtil.cloneBean(parc.getCondicaoPagamentoPropos());
							CoreUtil.cloneBean(parc.getCondicaoPgtoLicitacao());
							listaParcelasInsercao.add(CoreUtil.cloneBean(parc));
						}
						
						cpg.setParcelas(listaParcelasInsercao);
						cpg.setItemLicitacao(novoItemLicitacao);
						CoreUtil.cloneBean(cpg.getParcelas());
						listaInsercaoCondPag.add(CoreUtil.cloneBean(cpg));
					}
				}
				
				// aplica as alteracoes feitas aos itensOriginais...
				novoItemLicitacao.setJulgParcial(item.getIndJulgada());
				novoItemLicitacao.setValorUnitario(item.getValorUnitarioPrevisto());
				novoItemLicitacao.setFrequenciaEntrega(item.getFrequenciaEntrega());
				novoItemLicitacao.setIndFrequenciaEntrega(item.getIndFrequencia());
				novoItemLicitacao.setEmAf(item.getIndEmAf());			
				novoItemLicitacao.setFasesSolicitacao(listaFasesSrc);
				novoItemLicitacao.setCondicoesPagamento(listaInsercaoCondPag);
				// armazena o id da ultima alteracao para posterior delecao
				listIdVerificacao.add(new ScoItemLicitacaoId(novoItemLicitacao.getId().getLctNumero(),novoItemLicitacao.getId().getNumero()));
				// troca o numero
				novoItemLicitacao.getId().setNumero(item.getNumeroItem());
				listItensInsercao.add(novoItemLicitacao);
				listItensRemocao.add(item.getItemLicitacaoOriginal().getId());
			}	
		}
		
		if (listItensRemocao.size() > 0 && listItensRemocao.size() == listItensInsercao.size()) {
			for (ScoItemLicitacaoId id : listItensRemocao) {
				ScoItemLicitacao itemLicitacaoRemover = this.getScoItemLicitacaoDAO().obterPorChavePrimaria(id); 			
				if (itemLicitacaoRemover != null) {
					for(ScoFaseSolicitacao fase: itemLicitacaoRemover.getFasesSolicitacao()){
						this.getComprasFacade().excluirFaseSolicitacao(fase);						
					}
					for (ScoCondicaoPgtoLicitacao cpg : itemLicitacaoRemover.getCondicoesPagamento()) {
						this.getScoCondicaoPgtoLicitacaoRN().remover(cpg.getSeq());
					}
					this.getScoItemLicitacaoRN().excluirItemLicitacao(itemLicitacaoRemover);
				}
			}
			// verifica se sobrou algo para ser removido
			for (ScoItemLicitacaoId id : listIdVerificacao) {
				Integer index = listItensRemocao.indexOf(id);

				if (index < 0) {
					ScoItemLicitacao itemLicitacaoRemover = this.getScoItemLicitacaoDAO().obterPorChavePrimaria(id);
					if (itemLicitacaoRemover != null) {
						for(ScoFaseSolicitacao fase: itemLicitacaoRemover.getFasesSolicitacao()){
							this.getComprasFacade().excluirFaseSolicitacao(fase);
						}
						for (ScoCondicaoPgtoLicitacao cpg : itemLicitacaoRemover.getCondicoesPagamento()) {
							this.getScoCondicaoPgtoLicitacaoRN().remover(cpg.getSeq());
						}
						this.getScoItemLicitacaoRN().excluirItemLicitacao(itemLicitacaoRemover);
					}	
				}
			}
			// faz o flush dos deletes
			this.getScoItemLicitacaoDAO().flush();
			// insere os objetos com os novos codigos
			this.inserirListaOrdenada(numeroLicitacao, listItensInsercao, Boolean.FALSE);		
			this.getScoItemLicitacaoDAO().flush();
		}
	}
	
	/**
	 * Persiste no banco as alterações realizadas na tela de itens do PAC
	 * @param numeroLicitacao
	 * @param listCompleta
	 * @param listAlteracoes
	 * @throws ApplicationBusinessException
	 * @throws BaseException 
	 */
	public void gravarAlteracoesItensPac(Integer numeroLicitacao, List<ScoItemPacVO> listCompleta, List<ScoItemPacVO> listAlteracoes) throws BaseException {
		//this.getScoItemLicitacaoRN().validarLicitacaoPublicada(numeroLicitacao);
		
		// RN07
		this.validarSequenciaEDuplicidadeItens(listCompleta);
		
		for (ScoItemPacVO item : listAlteracoes) {
			
			if(item.getNumeroLote() != null){
				throw new ApplicationBusinessException(
						ScoItemLicitacaoONExceptionCode.MENSAGEM_ERRO_LOTE_ITENS_PAC);
			}
			
			if (item.getNumeroItem() == null) {
				throw new ApplicationBusinessException(
						ScoItemLicitacaoONExceptionCode.MENSAGEM_NUMERO_ITEM_OBRIGATORIO);
			}
			ScoItemLicitacao itemLicitacao = this.getScoItemLicitacaoDAO().
					obterItemLicitacaoPorNumeroLicitacaoENumeroItem(numeroLicitacao, item.getItemLicitacaoOriginal().getId().getNumero());
			
			if (itemLicitacao != null) {
				this.verificarPropostasItemLicitacaoEmAf(itemLicitacao.getId().getLctNumero(),	
						itemLicitacao.getId().getNumero());
				
				// RN 10				
				itemLicitacao.setJulgParcial(item.getIndJulgada());
				itemLicitacao.setValorUnitario(item.getValorUnitarioPrevisto());
				itemLicitacao.setFrequenciaEntrega(item.getFrequenciaEntrega());
				itemLicitacao.setIndFrequenciaEntrega(item.getIndFrequencia());
				itemLicitacao.setEmAf(item.getIndEmAf());				
				this.getScoItemLicitacaoRN().atualizarItemLicitacao(itemLicitacao);
			}
		}						
		this.getScoItemLicitacaoDAO().flush();
		
		this.trocarPosicaoItemPac(numeroLicitacao, listCompleta, listAlteracoes);
	}

	private Boolean compararOrdemListas(List<ScoItemLicitacao> listaOrdenada, List<ScoItemLicitacao> listaOriginal) {
		Boolean listaDiferente = Boolean.FALSE;
		
		if (listaOrdenada.size() != listaOriginal.size()) {
			listaDiferente = Boolean.TRUE;
		}
		
		if (!listaDiferente) {
			TreeSet<Short> listaNumeros = new TreeSet<Short>(); 
			for (ScoItemLicitacao item : listaOrdenada) {
				listaNumeros.add(item.getId().getNumero());
			}
			Short seq = 1;
			for (int i = 0; i < listaOrdenada.size() - 1; i++) {
				if (!listaOrdenada.get(i).getId().getNumero().equals(seq)) {
					listaDiferente = Boolean.TRUE;
					break;
				}
				seq++;
			}
		}
		
		if (!listaDiferente) {
			for (int i = 0; i < listaOrdenada.size() - 1; i++) {
				if (!listaOrdenada.get(i).getId().getNumero().equals(listaOriginal.get(i).getId().getNumero())) {
					listaDiferente = Boolean.TRUE;
					break;	
				}
			}
		}
		
		return listaDiferente;
	}
	
	private void inserirListaOrdenada(Integer numeroLicitacao, List<ScoItemLicitacao> listaInsercao, Boolean trocarNumeracao) throws BaseException {
		Short pos = 1;
		for (ScoItemLicitacao it : listaInsercao) {
			
			ScoItemLicitacaoId id = new ScoItemLicitacaoId();
			id.setLctNumero(numeroLicitacao);
			
			if (trocarNumeracao) {
				id.setNumero(pos);				
			} else {
				id.setNumero(it.getId().getNumero());		
			}
			
			ScoItemLicitacao itemLicitacaoInsercao = new ScoItemLicitacao(id);
			itemLicitacaoInsercao.setClassifItem(id.getNumero());
			itemLicitacaoInsercao.setValorUnitario(it.getValorUnitario());
			itemLicitacaoInsercao.setExclusao(it.getExclusao());
			itemLicitacaoInsercao.setMotivoExclusao(it.getMotivoExclusao());
			itemLicitacaoInsercao.setDtExclusao(it.getDtExclusao());
			itemLicitacaoInsercao.setMotivoCancel(it.getMotivoCancel());
			itemLicitacaoInsercao.setPropostaEscolhida(it.getPropostaEscolhida());
			itemLicitacaoInsercao.setEmAf(it.getEmAf());
			itemLicitacaoInsercao.setValorOriginalItem(it.getValorOriginalItem());
			itemLicitacaoInsercao.setJulgParcial(it.getJulgParcial());
			itemLicitacaoInsercao.setDtJulgParcial(it.getDtJulgParcial());
			if (it.getLoteLicitacao() != null) {
				itemLicitacaoInsercao.setLoteLicitacao(it.getLoteLicitacao());
			}
			itemLicitacaoInsercao.setServidorJulgParcial(it.getServidorJulgParcial());
			itemLicitacaoInsercao.setLicitacao(it.getLicitacao());
			itemLicitacaoInsercao.setIndFrequenciaEntrega(it.getIndFrequenciaEntrega());
			itemLicitacaoInsercao.setFrequenciaEntrega(it.getFrequenciaEntrega());
			this.getScoItemLicitacaoRN().inserirItemLicitacao(itemLicitacaoInsercao);
			
			
			for (ScoFaseSolicitacao fase : it.getFasesSolicitacao()) {
				
				ScoFaseSolicitacao faseInsercao = new ScoFaseSolicitacao();
				faseInsercao.setNumero(fase.getNumero());
				faseInsercao.setTipo(fase.getTipo());
				if(fase.getSolicitacaoDeCompra() != null){
					faseInsercao.setSolicitacaoDeCompra(this.getScoSolicitacoesDeComprasDAO().obterPorChavePrimaria(fase.getSolicitacaoDeCompra().getNumero()));
				}
				if(fase.getSolicitacaoServico() != null){
					faseInsercao.setSolicitacaoServico(this.getScoSolicitacaoServicoDAO().obterPorChavePrimaria(fase.getSolicitacaoServico().getNumero()));
				}
				faseInsercao.setItemLicitacao(itemLicitacaoInsercao);
				if(fase.getItemAutorizacaoForn() != null){
					faseInsercao.setItemAutorizacaoForn(this.getScoItemAutorizacaoFornDAO().obterItemAutorizacaoFornPorId(fase.getItemAutorizacaoForn().getId()));
				}
				faseInsercao.setExclusao(fase.getExclusao());
				faseInsercao.setDtExclusao(fase.getDtExclusao());
				this.getComprasFacade().inserirScoFaseSolicitacao(faseInsercao);				
			}
			
			for (ScoCondicaoPgtoLicitacao cpg : it.getCondicoesPagamento()) {
				ScoCondicaoPgtoLicitacao cpgInsercao = new ScoCondicaoPgtoLicitacao();
				cpgInsercao.setFormaPagamento(cpg.getFormaPagamento());
				cpgInsercao.setItemLicitacao(itemLicitacaoInsercao);
				if (cpg.getLicitacao() != null) {
					cpgInsercao.setLicitacao(itemLicitacaoInsercao.getLicitacao());
				}
				cpgInsercao.setNumero(cpg.getNumero());
				if (cpg.getPercAcrescimo() != null) {
					cpgInsercao.setPercAcrescimo(cpg.getPercAcrescimo());
				}
				if (cpg.getPercDesconto() != null) {
					cpgInsercao.setPercDesconto(cpg.getPercDesconto());
				}
				
				if (cpg.getParcelas() != null) {
					Set<ScoParcelasPagamento> listaParcelasInsercao = new HashSet<ScoParcelasPagamento>();
					
					for (ScoParcelasPagamento parc : cpg.getParcelas()) {
						ScoParcelasPagamento parcelaInsercao = new ScoParcelasPagamento();
						parcelaInsercao.setCondicaoPagamentoPropos(parc.getCondicaoPagamentoPropos());
						parcelaInsercao.setCondicaoPgtoLicitacao(cpgInsercao);
						parcelaInsercao.setParcela(parc.getParcela());
						parcelaInsercao.setPrazo(parc.getPrazo());
						listaParcelasInsercao.add(parcelaInsercao);
						
						this.getComprasFacade().persistirParcelaPagamento(parcelaInsercao);
					}
					cpgInsercao.setParcelas(listaParcelasInsercao);
				}
				
				this.getScoCondicaoPgtoLicitacaoRN().persistir(cpgInsercao);
			}
			pos++;
		}	
	}
	
	private List<ScoItemLicitacao> converterGradeEmListaItemLicitacao(Integer numeroLicitacao, List<ScoItemPacVO> listaGradeAtual) {
		List<ScoItemLicitacao> listaRetorno = new ArrayList<ScoItemLicitacao>();
		
		for (ScoItemPacVO itemVO : listaGradeAtual) {
		
			ScoItemLicitacao itemLicitacao = this.getScoItemLicitacaoDAO().
					obterPorChavePrimaria(new ScoItemLicitacaoId(numeroLicitacao, itemVO.getNumeroItem()));
			
			if (itemLicitacao != null) {
				listaRetorno.add(itemLicitacao);
			}
		}
		
		return listaRetorno;
	}
	
	/**
	 * Reordena os codigos dos itens de licitação de um PAC com a mesma ordem definida pelo usuario na tela
	 * @param numeroLicitacao
	 * @param listaGradeAtual
	 * @throws ApplicationBusinessException
	 * @throws BaseException 
	 */
	public void reordenarItensPac(Integer numeroLicitacao, List<ScoItemPacVO> listaGradeAtual) throws BaseException {
		
		List<ScoItemLicitacao> listaOriginal = this.getScoItemLicitacaoDAO().
				pesquisarItemLicitacaoPorNumeroLicitacao(null, null, null,false, numeroLicitacao);
		
		List<ScoItemLicitacao> listaOrdenada = this.converterGradeEmListaItemLicitacao(numeroLicitacao, listaGradeAtual);

		// valida RN04 antes de qualquer operacao
		for (ScoItemLicitacao it : listaOrdenada) {
			this.getScoItemLicitacaoRN().validarSolicitacaoUtilizadaOutraLicitacao(numeroLicitacao, it.getId().getNumero());
			
			if(it.getLoteLicitacao() != null){
				throw new ApplicationBusinessException(
						ScoItemLicitacaoONExceptionCode.MENSAGEM_ERRO_LOTE_ITENS_PAC);
			}
		}			
		
		if (compararOrdemListas(listaOrdenada, listaOriginal)) {
			// vou colocar numa lista auxiliar para inserir depois...
			List<ScoItemLicitacao> listaInsercao = new ArrayList<ScoItemLicitacao>();
			
			for (ScoItemLicitacao it : listaOrdenada) {
				ScoItemLicitacao itemInsercao = this.clonarItemLicitacao(it);			
				Set<ScoFaseSolicitacao> listaInsercaoFases = new HashSet<ScoFaseSolicitacao>();
				for(ScoFaseSolicitacao fase: it.getFasesSolicitacao()){
					ScoFaseSolicitacao faseNova = new ScoFaseSolicitacao();
					faseNova.setNumero(fase.getNumero());
					faseNova.setTipo(fase.getTipo());
					faseNova.setSolicitacaoDeCompra(fase.getSolicitacaoDeCompra());
					faseNova.setSolicitacaoServico(fase.getSolicitacaoServico());
					faseNova.setItemLicitacao(fase.getItemLicitacao());
					faseNova.setItemAutorizacaoForn(fase.getItemAutorizacaoForn());
					faseNova.setExclusao(fase.getExclusao());
					faseNova.setDtExclusao(fase.getDtExclusao());
					
					listaInsercaoFases.add(faseNova);
				}
				
				Set<ScoCondicaoPgtoLicitacao> listaInsercaoCondPag = new HashSet<ScoCondicaoPgtoLicitacao>();
				for(ScoCondicaoPgtoLicitacao cpg: it.getCondicoesPagamento()){
					CoreUtil.cloneBean(cpg.getLicitacao());
					CoreUtil.cloneBean(cpg.getFormaPagamento());
					CoreUtil.cloneBean(cpg.getItemLicitacao());
					
					Set<ScoParcelasPagamento> listaParcelasInsercao = new HashSet<ScoParcelasPagamento>();
					List<ScoParcelasPagamento> listaParcelas = this.getScoParcelasPagamentoDAO().obterParcelasPagamento(cpg.getSeq());
					
					for(ScoParcelasPagamento parc : listaParcelas) {
						CoreUtil.cloneBean(parc.getCondicaoPagamentoPropos());
						CoreUtil.cloneBean(parc.getCondicaoPgtoLicitacao());
						listaParcelasInsercao.add(CoreUtil.cloneBean(parc));
					}
					
					cpg.setParcelas(listaParcelasInsercao);
					CoreUtil.cloneBean(cpg.getParcelas());
					listaInsercaoCondPag.add(CoreUtil.cloneBean(cpg));
				}
				
				itemInsercao.setFasesSolicitacao(listaInsercaoFases);
				itemInsercao.setCondicoesPagamento(listaInsercaoCondPag);
				listaInsercao.add(itemInsercao);
			}
			// vou remover os objetos para posterior inserção...
			for (ScoItemLicitacao it : listaInsercao) {		
				ScoItemLicitacao itemLicitacaoRemover = this.getScoItemLicitacaoDAO().obterPorChavePrimaria(it.getId()); 			
				
				for(ScoFaseSolicitacao fase: itemLicitacaoRemover.getFasesSolicitacao()){
					this.getComprasFacade().excluirFaseSolicitacao(fase);					
				}
				for (ScoCondicaoPgtoLicitacao cpg : itemLicitacaoRemover.getCondicoesPagamento()) {
					this.getScoCondicaoPgtoLicitacaoRN().remover(cpg.getSeq());
				}
				this.getScoItemLicitacaoRN().excluirItemLicitacao(itemLicitacaoRemover);
			}
	
			this.getScoItemLicitacaoDAO().flush();		
			// insere a lista ordenadamente...
			inserirListaOrdenada(numeroLicitacao, listaInsercao, Boolean.TRUE);
			this.getScoItemLicitacaoDAO().flush();
		
		} else {
			throw new ApplicationBusinessException(
					ScoItemLicitacaoONExceptionCode.MENSAGEM_ITENS_PAC_ORDENADOS);
		}
	}
	
	public ScoItemLicitacao clonarItemLicitacao(ScoItemLicitacao itemLicitacao) throws ApplicationBusinessException {
		
		ScoItemLicitacao itemLicitacaoClone = null;
		try{
			itemLicitacaoClone = (ScoItemLicitacao) BeanUtils.cloneBean(itemLicitacao);
		} catch(Exception e){
			throw new ApplicationBusinessException(ScoItemLicitacaoONExceptionCode.MENSAGEM_ERRO_CLONE);
		}
		if(itemLicitacao.getLoteLicitacao() != null) {
			ScoLoteLicitacao lote = new ScoLoteLicitacao();
			lote.setId(new ScoLoteLicitacaoId(itemLicitacao.getLoteLicitacao().getId().getLctNumero(), itemLicitacao.getLoteLicitacao().getId().getNumero()));
			itemLicitacaoClone.setLoteLicitacao(lote);
		}
		if(itemLicitacao.getServidorJulgParcial() != null) {
			RapServidores servidor = new RapServidores();
			servidor.setId(new RapServidoresId(itemLicitacao.getServidorJulgParcial().getId().getMatricula(), itemLicitacao.getServidorJulgParcial().getId().getVinCodigo()));
			itemLicitacaoClone.setServidorJulgParcial(servidor);
		}
		if(itemLicitacao.getLicitacao() != null) {
			ScoLicitacao licitacao = new ScoLicitacao();
			licitacao.setNumero(itemLicitacao.getLicitacao().getNumero());
			itemLicitacaoClone.setLicitacao(licitacao);
		}
		return itemLicitacaoClone;
	}

	public void verificarPropostasItemLicitacaoEmAf(Integer numeroPac, Short numeroItem) throws ApplicationBusinessException {
		List<ScoItemPropostaFornecedor> listaPropostas = this.getScoItemPropostaFornecedorDAO().pesquisarItemPropostaPorNumeroLicitacaoENumeroItem(numeroPac, numeroItem);
		
		for (ScoItemPropostaFornecedor itemProposta : listaPropostas) {
			this.getScoItemPropostaFornecedorRN().validaItemPropostaEmAf(itemProposta);
		}		
	}
	/**
	 * #5481 Verifica se alguma licitação foi selecionada na tela
	 * @param nrosPac lista de itens selecionados na tela
	 * @throws ApplicationBusinessException
	 */
	public void validarLicitacaoSelecionada(List<Integer> nrosPac) throws ApplicationBusinessException{
		if (nrosPac == null || nrosPac.isEmpty()){
			throw new ApplicationBusinessException(ScoItemLicitacaoONExceptionCode.MS03_SEM_LICITACAO_SELECIONADA);
		}
	}

	public boolean validarJulgadoCancelado(ScoItemLicitacao item) {
		return item.getMotivoCancel() != null || Boolean.TRUE.equals(item.getPropostaEscolhida());
	}
	
	protected ScoItemPropostaFornecedorRN getScoItemPropostaFornecedorRN() {
		return scoItemPropostaFornecedorRN;
	}
	
	protected ScoItemPropostaFornecedorDAO getScoItemPropostaFornecedorDAO() {
		return scoItemPropostaFornecedorDAO;
	}
	
	protected ScoItemLicitacaoDAO getScoItemLicitacaoDAO() {
		return scoItemLicitacaoDAO;
	}
	
	protected ScoLicitacaoDAO getScoLicitacaoDAO() {
		return scoLicitacaoDAO;
	}
	
	protected ScoFaseSolicitacaoDAO getScoFaseSolicitacaoDAO() {
		return scoFaseSolicitacaoDAO;
	}
	
	protected ScoSolicitacoesDeComprasDAO getScoSolicitacoesDeComprasDAO() {
		return scoSolicitacoesDeComprasDAO;
	}

	protected ScoSolicitacaoServicoDAO getScoSolicitacaoServicoDAO() {
		return scoSolicitacaoServicoDAO;
	}
	
	protected ScoPontoParadaSolicitacaoDAO getScoPontoParadaSolicitacaoDAO() {
		return scoPontoParadaSolicitacaoDAO;
	}
	
	protected ScoItemAutorizacaoFornDAO getScoItemAutorizacaoFornDAO() {
		return scoItemAutorizacaoFornDAO;
	}
	
	protected ScoItemLicitacaoRN getScoItemLicitacaoRN() {
		return scoItemLicitacaoRN;
	}
	
	protected ScoScJnDAO getScoScJnDAO() {
		return scoScJnDAO;
	}
	
	protected ScoSsJnDAO getScoSsJnDAO() {
		return scoSsJnDAO;
	}
	
	private ScoParcelasPagamentoDAO getScoParcelasPagamentoDAO() {
		return scoParcelasPagamentoDAO;
	}
	
	private ScoCondicaoPgtoLicitacaoDAO getScoCondicaoPgtoLicitacaoDAO() {
		return scoCondicaoPgtoLicitacaoDAO;
	}
	
	protected DadosItemLicitacaoON getDadosItemLicitacaoON(){
		return dadosItemLicitacaoON;
	}
	
	private ScoCondicaoPgtoLicitacaoRN getScoCondicaoPgtoLicitacaoRN() {
		return scoCondicaoPgtoLicitacaoRN;
	}

	protected IComprasFacade getComprasFacade() {
		return comprasFacade;
	}	
	
	protected ISolicitacaoComprasFacade getSolicitacaoComprasFacade() {
		return solicitacaoComprasFacade;
	}
	
	protected ISolicitacaoServicoFacade getSolicitacaoServicoFacade() {
		return solicitacaoServicoFacade;
	}
}