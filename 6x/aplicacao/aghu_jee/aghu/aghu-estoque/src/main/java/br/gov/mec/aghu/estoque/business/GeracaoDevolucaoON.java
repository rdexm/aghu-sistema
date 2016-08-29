package br.gov.mec.aghu.estoque.business;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioBoletimOcorrencias;
import br.gov.mec.aghu.estoque.dao.SceBoletimOcorrenciasDAO;
import br.gov.mec.aghu.estoque.dao.SceHistoricoProblemaMaterialDAO;
import br.gov.mec.aghu.estoque.dao.SceItemBocDAO;
import br.gov.mec.aghu.estoque.dao.SceItemNotaRecebimentoDAO;
import br.gov.mec.aghu.estoque.vo.PendenciasDevolucaoVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceBoletimOcorrencias;
import br.gov.mec.aghu.model.SceDocumentoFiscalEntrada;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceHistoricoProblemaMaterial;
import br.gov.mec.aghu.model.SceItemBoc;
import br.gov.mec.aghu.model.SceItemBocId;
import br.gov.mec.aghu.model.SceNotaRecebimento;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class GeracaoDevolucaoON extends BaseBusiness {

	@EJB
	private ConfirmacaoDevolucaoON confirmacaoDevolucaoON;
	
	@EJB
	private SceItemBocRN sceItemBocRN;
	
	@EJB
	private SceEstoqueAlmoxarifadoRN sceEstoqueAlmoxarifadoRN;
	
	@EJB
	private SceBoletimOcorrenciasRN sceBoletimOcorrenciasRN;
	
	@EJB
	private SceHistoricoProblemaMaterialRN sceHistoricoProblemaMaterialRN;
	
	private static final Log LOG = LogFactory.getLog(GeracaoDevolucaoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private SceBoletimOcorrenciasDAO sceBoletimOcorrenciasDAO;
	
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@Inject
	private SceItemBocDAO sceItemBocDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private SceItemNotaRecebimentoDAO sceItemNotaRecebimentoDAO;
	
	@Inject
	private SceHistoricoProblemaMaterialDAO sceHistoricoProblemaMaterialDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1799228967597026995L;

	public enum GeracaoDevolucaoONExceptionCode implements BusinessExceptionCode { 
		MENSAGEM_GERACAO_DEVOLUCAO_MSG001, MENSAGEM_GERACAO_DEVOLUCAO_MSG002, MENSAGEM_GERACAO_DEVOLUCAO_MSG003, MENSAGEM_GERACAO_DEVOLUCAO_MSG004,
		MENSAGEM_GERACAO_DEVOLUCAO_MSG005,MENSAGEM_GERACAO_DEVOLUCAO_MSG006,MENSAGEM_GERACAO_DEVOLUCAO_MSG007, MENSAGEM_GERACAO_DEVOLUCAO_MSG008,
		MENSAGEM_GERACAO_DEVOLUCAO_MSG009;
	}

	public Boolean verificaExisteBoletimOcorrencia(Integer seqNotaRecebimento){
		List<SceBoletimOcorrencias> listaBoletins = obterSceBoletimOcorrencias(seqNotaRecebimento);
		
		return (listaBoletins != null && !listaBoletins.isEmpty());
	}
	
	public List<SceBoletimOcorrencias> obterSceBoletimOcorrencias(Integer seqNotaRecebimento){		
		return this.getSceBoletimOcorrenciasDAO().pesquisarBoletimOcorrenciaNotaRecebimentoSituacao(seqNotaRecebimento, DominioBoletimOcorrencias.G, false);
	}
	
	public List<PendenciasDevolucaoVO> pesquisarGeracaoPendenciasDevolucao(Integer numeroNr){
		
		List<PendenciasDevolucaoVO> listaPendenciasDevolucao = this.getSceItemNotaRecebimentoDAO().pesquisarGeracaoPendenciasDevolucao(numeroNr);
		
		for(PendenciasDevolucaoVO pendenciaDevolucao: listaPendenciasDevolucao){
			
			Long qtde = this.getSceBoletimOcorrenciasDAO().obterQtdeBoletimOcorrenciaPorNrMaterial(numeroNr, pendenciaDevolucao.getMatCodigo());			
			pendenciaDevolucao.setQtdeBo(qtde);	
			pendenciaDevolucao.setValorTotalItemNr(new Double(0));
			
		}
		
		return listaPendenciasDevolucao;
	}
	
	public void gerarDevolucao(List<PendenciasDevolucaoVO> listaPendencias, SceNotaRecebimento notaRecebimento, SceDocumentoFiscalEntrada docFiscalEntrada, String nomeMicrocomputador) throws BaseException {
		Boolean existePendenciaSelecionada = false;
		Boolean isMaterialImobilizado = false;
		Boolean isExisteBoletimOcorrencia = false; 
		
		this.mensagemExcecao(notaRecebimento.getEstorno(), new ApplicationBusinessException(GeracaoDevolucaoONExceptionCode.MENSAGEM_GERACAO_DEVOLUCAO_MSG001));
		this.mensagemExcecao(docFiscalEntrada == null, new ApplicationBusinessException(GeracaoDevolucaoONExceptionCode.MENSAGEM_GERACAO_DEVOLUCAO_MSG002));
		
		AghParametros parametroFornecedorPadrao = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_FORNECEDOR_PADRAO);
		Short almoxCentral = this.getParametroFacade().obterAghParametro(AghuParametrosEnum.P_ALMOX_CENTRAL).getVlrNumerico().shortValue();
		
		List<SceBoletimOcorrencias> listaBoletins = this.obterSceBoletimOcorrencias(notaRecebimento.getSeq());
		
		SceEstoqueAlmoxarifado estoqueAlmoxarifado = null;
		
		isExisteBoletimOcorrencia = (listaBoletins != null && listaBoletins.size() > 0);
		SceBoletimOcorrencias boletimOcorrencias = null;
		Short nroItem = null;
		for (PendenciasDevolucaoVO pendenciaDevolucao:listaPendencias){
			
			if (pendenciaDevolucao.getIsMarked()){
				existePendenciaSelecionada = true;				
				
				this.mensagemExcecao(pendenciaDevolucao.getQtdeSaida() == null, 
						             new ApplicationBusinessException(GeracaoDevolucaoONExceptionCode.MENSAGEM_GERACAO_DEVOLUCAO_MSG004, 
						                                      pendenciaDevolucao.getNroItem(), pendenciaDevolucao.getMatCodigo()));				
				
				this.mensagemExcecao(pendenciaDevolucao.getQtdeSaida().intValue() == 0,
						             new ApplicationBusinessException(GeracaoDevolucaoONExceptionCode.MENSAGEM_GERACAO_DEVOLUCAO_MSG005, 
						            		                  pendenciaDevolucao.getNroItem(), pendenciaDevolucao.getMatCodigo()));				
				
				this.mensagemExcecao(pendenciaDevolucao.getQtdeSaida().compareTo(pendenciaDevolucao.getQtdSaldoCalculado().longValue()) > 0,
						             new ApplicationBusinessException(GeracaoDevolucaoONExceptionCode.MENSAGEM_GERACAO_DEVOLUCAO_MSG006,
						            		                  pendenciaDevolucao.getQtdSaldoCalculado(), pendenciaDevolucao.getNroItem() ));
				
				this.mensagemExcecao(StringUtils.isBlank(pendenciaDevolucao.getDescricao()),
						             new ApplicationBusinessException(GeracaoDevolucaoONExceptionCode.MENSAGEM_GERACAO_DEVOLUCAO_MSG007,
						            		                  pendenciaDevolucao.getNroItem(), pendenciaDevolucao.getMatCodigo()));
				
				this.mensagemExcecao(pendenciaDevolucao.getQtdSaldoCalculado().longValue() == 0,
						new ApplicationBusinessException(GeracaoDevolucaoONExceptionCode.MENSAGEM_GERACAO_DEVOLUCAO_MSG008,
      		                  pendenciaDevolucao.getQtdeBo(), pendenciaDevolucao.getQtdeNr()));
				
				isMaterialImobilizado = this.getConfirmacaoDevolucaoON().verificarMaterialImobilizado(pendenciaDevolucao.getMatCodigo(), new BigDecimal(pendenciaDevolucao.getValorUnitarioItemCalculado())); 
				
				Short almoxSeq = almoxCentral;

				

				if (this.getParametroFacade().obterAghParametro(AghuParametrosEnum.P_DF_ORIG_LOC_ESTOQUE).getVlrTexto().equals("S")) {
					ScoMaterial material = this.getComprasFacade().obterScoMaterialPorChavePrimaria(pendenciaDevolucao.getMatCodigo());
					almoxSeq = material.getAlmoxarifado().getSeq();
				}

				estoqueAlmoxarifado = this.getEstoqueFacade().obterEstoqueAlmoxarifadoEstocavelPorMaterialAlmoxarifadoFornecedor(almoxSeq, pendenciaDevolucao.getMatCodigo(), parametroFornecedorPadrao.getVlrNumerico().intValue());
				if (!isMaterialImobilizado){
					this.mensagemExcecao((estoqueAlmoxarifado != null && estoqueAlmoxarifado.getQtdeBloqueada() < pendenciaDevolucao.getQtdeSaida().intValue()),
							             new ApplicationBusinessException(GeracaoDevolucaoONExceptionCode.MENSAGEM_GERACAO_DEVOLUCAO_MSG009,
		      		                                              pendenciaDevolucao.getNroItem(), pendenciaDevolucao.getMatCodigo(),
		      		                                              estoqueAlmoxarifado.getQtdeBloqueada(), pendenciaDevolucao.getQtdeSaida()));
				}				
				
				if (!isExisteBoletimOcorrencia) {
					// devemos fazer o new() aqui para avancar a sequence antes da primeira insercao pois podemos inserir dois itens diferentes dentro
					// de um mesmo BO. Tudo porque na segunda insercao, como nao ocorreu o flush ainda, a seq do BO continua nula. dai la embaixo
					// quando esta nulo pego o currval da sequence e nao da null pointer.
					// amenegotto em 11/11/2013
					boletimOcorrencias = new SceBoletimOcorrencias();
					nroItem = 1;
				} else {
					if (listaBoletins != null && !listaBoletins.isEmpty()) {						
						boletimOcorrencias = listaBoletins.get(0);
						nroItem = this.getSceItemBocDAO().obterProximoNroItem(listaBoletins.get(0).getSeq());
					} else {
						nroItem++;
					}
				}

				criarAtualizarBoletimOcorrencia(isExisteBoletimOcorrencia, pendenciaDevolucao,
						                        notaRecebimento, docFiscalEntrada, boletimOcorrencias, nroItem, estoqueAlmoxarifado);
				
				// tem que colocar esta variavel como true senao se rodar mais de uma vez vai sempre
				// criar bo - amenegotto em 08/11/2013
				if (!isExisteBoletimOcorrencia) {
					isExisteBoletimOcorrencia = true;
				}

				if (!isMaterialImobilizado){
					if (estoqueAlmoxarifado != null){
						Integer qtdeBloqueada = estoqueAlmoxarifado.getQtdeBloqueada(); 
						estoqueAlmoxarifado.setQtdeBloqueada(qtdeBloqueada - pendenciaDevolucao.getQtdeSaida().intValue());
						this.getSceEstoqueAlmoxarifadoRN().atualizar(estoqueAlmoxarifado, nomeMicrocomputador, false);
						this.criarAtualizarHistoricoProblemaMaterial(notaRecebimento, estoqueAlmoxarifado, pendenciaDevolucao);
					}
				}
			}			
		}
		
		if (!existePendenciaSelecionada){
			throw new ApplicationBusinessException(GeracaoDevolucaoONExceptionCode.MENSAGEM_GERACAO_DEVOLUCAO_MSG003);		
		}
		
	}
	
	private void criarAtualizarHistoricoProblemaMaterial(SceNotaRecebimento notaRecebimento, SceEstoqueAlmoxarifado estoqueAlmoxarifado,
			PendenciasDevolucaoVO pendenciaDevolucao ) throws BaseException {

		ScoFornecedor fornecedor = this.getComprasFacade().obterFornecedorComPropostaPorAF(notaRecebimento.getAutorizacaoFornecimento());
		
		if (fornecedor != null) {
			// se jah existe um sceHistoricoProblemaMaterial para o mesmo fornecedor, material e motivo ainda nao efetivado,
			// agrega o valor senao insere
			List<SceHistoricoProblemaMaterial> listaHistProblema = this.getSceHistoricoProblemaMaterialDAO().
					pesquisarHistoricoProblemaMaterialPorFornecedorQtdProblema(fornecedor.getNumero(), estoqueAlmoxarifado.getSeq(), null, pendenciaDevolucao.getMotivoProblema());
			
			SceHistoricoProblemaMaterial sceHistProbAlmox = null;
			if (listaHistProblema == null || listaHistProblema.isEmpty()) {
				RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
				
				sceHistProbAlmox = new SceHistoricoProblemaMaterial();
				sceHistProbAlmox.setSceEstqAlmox(estoqueAlmoxarifado);
				sceHistProbAlmox.setDtGeracao(new Date());
				sceHistProbAlmox.setMotivoProblema(pendenciaDevolucao.getMotivoProblema());
				sceHistProbAlmox.setServidor(servidorLogado);
				sceHistProbAlmox.setQtdeProblema(pendenciaDevolucao.getQtdeSaida().intValue());
				sceHistProbAlmox.setIndEfetivado(false);
				sceHistProbAlmox.setNrsSeq(notaRecebimento.getSeq());						
				sceHistProbAlmox.setFornecedor(fornecedor);
				sceHistProbAlmox.setQtdeDf(0);
				sceHistProbAlmox.setQtdeDesbloqueada(0);
				this.getSceHistoricoProblemaMaterialRN().inserir(sceHistProbAlmox,false);
			} else {
				sceHistProbAlmox = listaHistProblema.get(0);
				sceHistProbAlmox.setQtdeProblema(sceHistProbAlmox.getQtdeProblema() + pendenciaDevolucao.getQtdeSaida().intValue());
				// apesar de ambos utilizarem o persist, existem operacoes na insercao que nao devem
				// serem feitas no update...
				this.getSceHistoricoProblemaMaterialRN().atualizar(sceHistProbAlmox, false);
			}
		}
	}
	
	public void criarAtualizarBoletimOcorrencia(Boolean isExisteBoletimOcorrencia, PendenciasDevolucaoVO pendenciaDevolucao, SceNotaRecebimento notaRecebimento, 
			                                    SceDocumentoFiscalEntrada docFiscalEntrada, SceBoletimOcorrencias boletimOcorrencias, Short nroItem,
			                                    SceEstoqueAlmoxarifado estoqueAlmoxarifado) throws ApplicationBusinessException{
		
		if (!isExisteBoletimOcorrencia){
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			boletimOcorrencias.setDtGeracao(new Date());
			boletimOcorrencias.setServidor(servidorLogado);
			boletimOcorrencias.setSituacao(DominioBoletimOcorrencias.G);
			boletimOcorrencias.setDocumentoFiscalEntrada(docFiscalEntrada);
			boletimOcorrencias.setNotaRecebimento(notaRecebimento);			
			this.getSceBoletimOcorrenciasRN().inserir(boletimOcorrencias);
			this.inserirItemBoletimOcorrencias(boletimOcorrencias, nroItem, pendenciaDevolucao);					
		}
		else {
			ScoFornecedor fornecedor = this.getComprasFacade().obterFornecedorComPropostaPorAF(notaRecebimento.getAutorizacaoFornecimento());
			if (fornecedor != null) {
				// primeiro verifica se existe um SceHistoricoProblemaMaterial pelo mesmo motivo que estamos fazendo esta devolucao.
				// se existir, somente da update. senao, insere. tudo isto porque falta uma fk da SceHistoricoProblemaMaterial dizendo
				// qual item do BO ela se refere - amenegotto em 07/10/2013.
				List<SceHistoricoProblemaMaterial> listaHistProblema = this.getSceHistoricoProblemaMaterialDAO().
						pesquisarHistoricoProblemaMaterialPorFornecedorQtdProblema(fornecedor.getNumero(), estoqueAlmoxarifado.getSeq(), null, pendenciaDevolucao.getMotivoProblema());
	
				List<SceItemBoc> listaItemBoc = this.getSceItemBocDAO().pesquisarItensBOCPorNotaRecebimentoMaterial(notaRecebimento.getSeq(), pendenciaDevolucao.getMatCodigo());
				
				if (listaItemBoc != null && !listaItemBoc.isEmpty() && listaHistProblema != null && listaHistProblema.size() > 0){
					SceItemBoc itemBoc = listaItemBoc.get(0);
					itemBoc.setQtde( itemBoc.getQtde() + pendenciaDevolucao.getQtdeSaida());
					itemBoc.setValor(itemBoc.getValor().add(new BigDecimal(pendenciaDevolucao.getValorTotalItemNr())));
					this.getSceItemBocRN().atualizar(itemBoc);
				}
				else {
					this.inserirItemBoletimOcorrencias(boletimOcorrencias, nroItem, pendenciaDevolucao);					
				}
			}
		}
	}
	
	public void inserirItemBoletimOcorrencias(SceBoletimOcorrencias boletimOcorrencias, Short nroItem, PendenciasDevolucaoVO pendenciaDevolucao) throws ApplicationBusinessException{
		SceItemBocId itemBOCid = null;
		if (boletimOcorrencias.getSeq() != null) {
			itemBOCid = new SceItemBocId(boletimOcorrencias.getSeq(), nroItem);
		} else {	
			itemBOCid = new SceItemBocId(this.getSceBoletimOcorrenciasDAO().obterUltimoSeq() , nroItem);
		}
		
		SceItemBoc itemBoc = new SceItemBoc();
		itemBoc.setBoletimOcorrencia(boletimOcorrencias);
		itemBoc.setMatCodigo(pendenciaDevolucao.getMatCodigo());
		itemBoc.setId(itemBOCid);
		itemBoc.setQtde(pendenciaDevolucao.getQtdeSaida());
		itemBoc.setValor(new BigDecimal(pendenciaDevolucao.getValorTotalItemNr()));
		itemBoc.setDescricao(pendenciaDevolucao.getDescricao());
		this.getSceItemBocRN().inserir(itemBoc);
	}
	
	public void mensagemExcecao(boolean validacaoTeste, ApplicationBusinessException aghuNegocioExcecao) throws ApplicationBusinessException{	
		if(validacaoTeste){
			throw aghuNegocioExcecao;
		}
	}
	
	public ConfirmacaoDevolucaoON getConfirmacaoDevolucaoON(){
		return confirmacaoDevolucaoON;
	}
	
	public SceItemBocRN getSceItemBocRN(){
		return sceItemBocRN;
	}
	
	public SceBoletimOcorrenciasRN getSceBoletimOcorrenciasRN(){
		return sceBoletimOcorrenciasRN;
	}
	
	public SceEstoqueAlmoxarifadoRN getSceEstoqueAlmoxarifadoRN(){
		return sceEstoqueAlmoxarifadoRN;
	}
	
	public SceHistoricoProblemaMaterialRN getSceHistoricoProblemaMaterialRN(){
		return sceHistoricoProblemaMaterialRN;
	}
	
	private SceHistoricoProblemaMaterialDAO getSceHistoricoProblemaMaterialDAO() {
		return sceHistoricoProblemaMaterialDAO;
	}
	
	private SceBoletimOcorrenciasDAO getSceBoletimOcorrenciasDAO() {
		return sceBoletimOcorrenciasDAO;
	}
	
	private SceItemBocDAO getSceItemBocDAO() {
		return sceItemBocDAO;
	}
	
	private SceItemNotaRecebimentoDAO getSceItemNotaRecebimentoDAO() {
		return sceItemNotaRecebimentoDAO;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected IEstoqueFacade getEstoqueFacade() {
		return estoqueFacade;
	}
	
	protected IComprasFacade getComprasFacade() {
		return comprasFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
