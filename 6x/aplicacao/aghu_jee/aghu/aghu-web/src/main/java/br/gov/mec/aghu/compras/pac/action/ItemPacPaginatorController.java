package br.gov.mec.aghu.compras.pac.action;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.collections.comparators.ReverseComparator;

import br.gov.mec.aghu.business.exception.AghuCSVBaseException.AghuCSVBaseExceptionExceptionCode;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.dominio.DominioFrequenciaEntrega;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacao;
import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.suprimentos.vo.ScoItemPacVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class ItemPacPaginatorController extends ActionController  {

	private static final String SUPRIMENTOS_ANEXAR_DOCUMENTO_SOLICITACAO_COMPRA = "suprimentos-anexarDocumentoSolicitacaoCompra";

	private static final long serialVersionUID = 6311943654533755902L;
	
	private static final String PAGE_PROCESSO_ADM_COMPRA_CRUD = "processoAdmCompraCRUD";
	private static final String PAGE_ASSOCIA_SC_SS_PAC = "associaScSsPac";
	private static final String PAGE_CONDICOES_PAGAMENTO_PAC_LIST = "condicoesPagamentoPacList";
	private static final String PAGE_SOLICITACAO_COMPRA_CRUD = "compras-solicitacaoCompraCRUD";
	private static final String PAGE_SOLICITACAO_SERVICO_CRUD = "compras-solicitacaoServicoCRUD";
	
	@EJB
	private IPacFacade pacFacade;
	
	private Comparator<ScoItemPacVO> currentComparator;
	private String currentSortProperty;
	
	// filtros
	private Integer numeroPac;
	private String descricaoPac;
	private Boolean indExcluido;
	private Date dataGeracaoPac;
	private String modalidadePac;
	private String numeroDocumento;
	private String numeroEdital;
	private String anoEdital;
	private String tipoEdital;
	private String situacaoPac;
	private BigDecimal valorTotalItens;
	
	// URL para botão voltar
	private String voltarParaUrl;
	
	// controles da grid editavel
	private List<ScoItemPacVO> listAlteracoes;
	private List<ScoItemPacVO> listCompleta;
	private Boolean bloqueiaEdicao;
	
	// modal de exclusao
	private ScoItemLicitacao itemLicitacaoExclusao;
	private ScoPontoParadaSolicitacao pontoParadaAnterior;
	private String observacaoExclusao;
	private Boolean voltarPanel;
	private Boolean mostrarModalExclusao;
	private Boolean mostrarModalConfirmacaoExclusao;
	private String msgModalExclusao;
	
	// modal de encaminhar
	private Boolean mostrarModalReordenar;
	
	private Boolean gerouArquivo = Boolean.FALSE;
	private String fileName;
	private static final String EXTENSAO_CSV = ".csv";
	private static final String CONTENT_TYPE_CSV = "text/csv";
	
	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}
	
	public void inicio(){
	 
		if(numeroPac != null){
			pesquisar();
		}
		else {
			this.limparPesquisa();
		}
	
	}
	
		
	// botões	
	public void pesquisar() {
		this.listAlteracoes = new ArrayList<ScoItemPacVO>();
		this.limparModalExclusao();
		this.mostrarModalReordenar = Boolean.FALSE;
		if (this.obterDadosLicitacao()) {
			carregarDadosLista();
		} else {
			this.apresentarMsgNegocio(Severity.FATAL, "MENSAGEM_LICITACAO_NAO_ENCONTRADA", this.numeroPac);
			this.limparPesquisa();
		}
	}
	
	public void excluirItemPac() {
		if (this.getItemLicitacaoExclusao() != null) {
			try {	
	
				pacFacade.excluirItemPac(
						this.itemLicitacaoExclusao.getId().getLctNumero(), 
						this.itemLicitacaoExclusao.getId().getNumero(), 
						this.observacaoExclusao, this.itemLicitacaoExclusao.getExclusao());	
							
				this.apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_EXCLUSAO_ITEM_PAC_COM_SUCESSO",
						this.itemLicitacaoExclusao.getId().getNumero(),
						this.itemLicitacaoExclusao.getId().getLctNumero());
				
				if (!pacFacade.verificarLicitacaoProposta(
						this.getItemLicitacaoExclusao().getId().getLctNumero(), this.getItemLicitacaoExclusao().getId().getNumero(), false)) {
					pacFacade.removerItemLista(pacFacade.montarItemObjetoVO(this.getItemLicitacaoExclusao()), this.listAlteracoes);
					pacFacade.removerItemLista(pacFacade.montarItemObjetoVO(this.getItemLicitacaoExclusao()), this.listCompleta);
				}
				
				this.limparModalExclusao();	
				this.pesquisar();
				
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		}
	}
	
	public Boolean isHabilitadoExclusaoFisica(ScoItemPacVO item) {
		return !this.pacFacade.verificarLicitacaoProposta(
				item.getItemLicitacaoOriginal().getId().getLctNumero(), item.getItemLicitacaoOriginal().getId().getNumero(), false);
	}

	public String visualizarPac(){
		return PAGE_PROCESSO_ADM_COMPRA_CRUD;
	}
	
	public String redirecionarAnexarDocumentoSolicitacaoCompra(){
		return SUPRIMENTOS_ANEXAR_DOCUMENTO_SOLICITACAO_COMPRA;
	}
	
	public String voltar(){
		return voltarParaUrl;
	}
	
	public String incluirItens(){
		return PAGE_ASSOCIA_SC_SS_PAC;
	}
	
	public String condicoesPagamentoPac(){
		return PAGE_CONDICOES_PAGAMENTO_PAC_LIST;
	}
	
	public String visualizarSolicitacaoCompras(){
		return PAGE_SOLICITACAO_COMPRA_CRUD;
	}
	
	public String visualizarSolicitacaoServico(){
		return PAGE_SOLICITACAO_SERVICO_CRUD;
	}
		
	public void gravar() {
		try {
			 
			List<ScoItemPacVO> listRemover = new ArrayList<ScoItemPacVO>();
			for (ScoItemPacVO item : listAlteracoes) {
				if (item.getItemLicitacaoOriginal() != null){
					if(item.getItemLicitacaoOriginal().getId().getNumero().equals(item.getNumeroItem()) &&
					   item.getItemLicitacaoOriginal().getValorUnitario().equals(item.getValorUnitarioPrevisto()) &&
					   ((item.getItemLicitacaoOriginal().getFrequenciaEntrega() == null && item.getFrequenciaEntrega() != null) ||
					    (item.getItemLicitacaoOriginal().getFrequenciaEntrega() != null && item.getItemLicitacaoOriginal().getFrequenciaEntrega().equals(item.getFrequenciaEntrega()))) &&
					    ((item.getItemLicitacaoOriginal().getIndFrequenciaEntrega() == null && item.getIndFrequencia() != null) ||
						  (item.getItemLicitacaoOriginal().getIndFrequenciaEntrega() != null && item.getItemLicitacaoOriginal().getIndFrequenciaEntrega().equals(item.getIndFrequencia()))) ){
						listRemover.add(item);
					}
				}
			}
			
			listAlteracoes.removeAll(listRemover);
			
			pacFacade.gravarAlteracoesItensPac(this.numeroPac, this.listCompleta, this.listAlteracoes);
						
			this.apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_ITEMPAC_M01");
						
			this.listAlteracoes.clear();
					
			this.valorTotalItens = pacFacade.obterValorTotalPorNumeroLicitacao(this.numeroPac);
			this.pesquisar();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void reordenarItens() {
		try {
			pacFacade.reordenarItensPac(this.numeroPac, this.listCompleta);

			// o flush é feito lá na ON porque a operacao realizada é sobre as
			// chaves primárias da tabela...

			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ITEMPAC_M01");

			this.mostrarModalReordenar = Boolean.FALSE;
			this.pesquisar();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void prepararExclusao(ScoItemPacVO item) {
		this.setItemLicitacaoExclusao(item.getItemLicitacaoOriginal());
		
		pontoParadaAnterior = pacFacade.obterPontoParadaAnteriorItemLicitacao(item.getItemLicitacaoOriginal());
		
		if (!item.getIndExclusao()) {
			this.setObservacaoExclusao(null);
			this.setMostrarModalConfirmacaoExclusao(false);
			this.setMostrarModalExclusao(true);				
		} else {
			this.setMostrarModalExclusao(false);	
			this.setMostrarModalConfirmacaoExclusao(true);
		}
		
		StringBuilder msg = new StringBuilder();
		msg.append(getBundle().getString("MENSAGEM_RETORNO_EXCLUIR_SC"));
		
		/*if(item.getTipoSolicitacao().equals(DominioTipoSolicitacao.SC) ){
			msg.append(getBundle().getString("MENSAGEM_RETORNO_SC_PONTO_PARADA_1"));
		}
		else{
			msg.append(getBundle().getString("MENSAGEM_RETORNO_SS_PONTO_PARADA_1"));
		}
	
		msg.append(' ').append(pontoParadaAnterior.getCodigo()).append(" - ")
		.append(pontoParadaAnterior.getDescricao()).append(' ')
		.append(getBundle().getString("MENSAGEM_RETORNO_PONTO_PARADA_2"));*/
		
		msgModalExclusao = msg.toString();		
	}
	
	public void reativarItemPac(ScoItemLicitacao item) {
		if (item != null) {
			try {

				pacFacade.reativarItemPac(item.getId().getLctNumero(), item.getId().getNumero());

				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ITEMPAC_M01");

			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		}

		this.pesquisar();
	}
	
	// métodos de controle da grid editável
	public void atualizarListaEdicoes(ScoItemPacVO item) {
		
		Integer index = listAlteracoes.indexOf(item);

		// se já existe na lista só atualiza
		if (index >= 0) {
			((ScoItemPacVO) listAlteracoes.get(index))
				.setNumeroItem(item.getNumeroItem());
			((ScoItemPacVO) listAlteracoes.get(index))
					.setValorUnitarioPrevisto(item.getValorUnitarioPrevisto());
			((ScoItemPacVO) listAlteracoes.get(index)).setIndFrequencia(item.getIndFrequencia());
			((ScoItemPacVO) listAlteracoes.get(index)).setFrequenciaEntrega(item.getFrequenciaEntrega());
			((ScoItemPacVO) listAlteracoes.get(index)).setIndEmAf(item.getIndEmAf());
			((ScoItemPacVO) listAlteracoes.get(index)).setIndJulgada(item.getIndJulgada());
			((ScoItemPacVO) listAlteracoes.get(index)).setNumeroLote(item.getNumeroLote());
		} else {
			listAlteracoes.add(item);
		}
		
		Integer indexCompleta = listCompleta.indexOf(item);
		if (indexCompleta >= 0) {
			((ScoItemPacVO) listCompleta.get(indexCompleta))
				.setNumeroItem(item.getNumeroItem());
		} else {
			this.apresentarMsgNegocio(Severity.FATAL,
					"MENSAGEM_ITEM_PAC_INCONSISTENCIA_CONTROLE");
		}
	}

	
	// métodos de auxílio
	
	public void limparPesquisa() {
		this.numeroPac = null;
		this.descricaoPac = null; 
		this.indExcluido = null;
		this.dataGeracaoPac = null;
		this.modalidadePac = null;
		this.numeroDocumento = null;
		this.numeroEdital = null;
		this.anoEdital = null;
		this.tipoEdital = null;
		this.situacaoPac = null;
		this.valorTotalItens = BigDecimal.ZERO;
		this.listAlteracoes = new ArrayList<ScoItemPacVO>();
		this.listCompleta = new ArrayList<ScoItemPacVO>();
		this.mostrarModalReordenar = Boolean.FALSE;		
		this.limparModalExclusao();
	}

	public void limparModalExclusao() {
		this.setObservacaoExclusao(null);
		this.setVoltarPanel(false);
		this.setItemLicitacaoExclusao(null);
		this.setMostrarModalExclusao(false);
		this.setMostrarModalConfirmacaoExclusao(false);
	}		
	
	public Boolean obterDadosLicitacao() {
		ScoLicitacao licitacao = null;
		
		if (this.numeroPac != null) {
			licitacao = pacFacade.obterLicitacao(this.numeroPac);
			
			if (licitacao != null) {
				this.setDescricaoPac(licitacao.getDescricao());
				this.setIndExcluido(licitacao.getExclusao());
				this.setDataGeracaoPac(licitacao.getDtDigitacao());
				this.setModalidadePac(licitacao.getModalidadeLicitacao().getCodigo() + "-" + licitacao.getModalidadeLicitacao().getDescricao());
				if (licitacao.getNumDocLicit() != null) {
					this.setNumeroDocumento(licitacao.getNumDocLicit().toString());
				}
				if (licitacao.getNumEdital() != null) {
					this.setNumeroEdital(licitacao.getNumEdital().toString());
				}
				if (licitacao.getAnoComplemento() != null) {
					this.setAnoEdital(licitacao.getAnoComplemento().toString());
				}
				if (licitacao.getTipoPregao() != null) {
					this.setTipoEdital(licitacao.getTipoPregao().getDescricao());
				}
				this.setSituacaoPac(licitacao.getSituacao().getDescricao());			
				this.valorTotalItens = pacFacade.obterValorTotalPorNumeroLicitacao(this.numeroPac);
			}
			
			this.bloqueiaEdicao = this.verificarEdicaoItensPac(this.numeroPac, false, true);
		} 
		
		return (licitacao != null);
	}
	
	public Boolean verificarEdicaoItensPac(Integer numeroLicitacao, Boolean validarProposta, Boolean validarPublicacao) {
		return pacFacade.verificarEdicaoItensPac(numeroLicitacao, validarProposta, validarPublicacao);
	}
	
	public Boolean verificarEdicaoItensPacPropostaLote(Integer numeroLicitacao, Short numero) {
		return pacFacade.verificarEdicaoItensPacPropostaLote(numeroLicitacao, numero);
	}
	
	public String obterUnidadeMaterial(ScoItemLicitacao item) {
		return pacFacade.obterUnidadeMaterial(item);
	}
	
	public String obterComplementoAutorizacaoFornecimento(ScoItemLicitacao item) {
		return pacFacade.obterComplementoAutorizacaoFornecimento(item);
	}
	
	public DominioTipoSolicitacao obterTipoSolicitacao(ScoItemLicitacao item) {
		return pacFacade.obterTipoSolicitacao(item);
	}
	
	public String obterDescricaoSolicitacao(ScoItemLicitacao item) {
		return pacFacade.obterDescricaoSolicitacao(item);
	}
	
	public String obterNomeMaterialServico(ScoItemLicitacao item) {
		return pacFacade.obterNomeMaterialServico(item, false);
	}
	
	public String obterDescricaoMaterialServico(ScoItemLicitacao item) {
		return pacFacade.obterDescricaoMaterialServico(item);
	}
	
	public Integer obterQuantidadeMaterialServico(ScoItemLicitacao item) {
		return pacFacade.obterQuantidadeMaterialServico(item);
	}

	public String obterViewSolicitacao(ScoItemLicitacao item) {
		return this.obterTipoSolicitacao(item) == DominioTipoSolicitacao.SC ? "/compras/solicitacaoCompra/solicitacaoCompraCRUD.xhtml" : "/compras/solicitacaoServico/solicitacaoServicoCRUD.xhtml";
	}

	public Boolean verificarSolicitacaoCompras(ScoItemLicitacao item) {
		return (this.obterTipoSolicitacao(item) == DominioTipoSolicitacao.SC);
	}
	
	
	public Integer obterCodMatServ(ScoItemLicitacao item){
		return pacFacade.obterCodMatServ(item);
	}
	
	// paginator

	public void iniciar() {
		if (numeroPac != null) {
			this.pesquisar();
		} else {
			this.limparPesquisa();
		}
	}
	
	
	public void carregarDadosLista() {
	
		List<ScoItemLicitacao> listaItens = pacFacade.
				pesquisarItemLicitacaoPorNumeroPac(null, null, null, false, this.numeroPac); 
				
		if (listaItens == null) {
			listaItens = new ArrayList<ScoItemLicitacao>();
		}

		
		if (listCompleta!=null){
			listCompleta.clear();
		}else {
			listCompleta = new ArrayList<ScoItemPacVO>();
		}
		
		for (ScoItemLicitacao item : listaItens) {
			
			ScoItemPacVO itemPacVO = pacFacade.montarItemObjetoVO(item);
			
			if (this.getIndExcluido() == false && !this.bloqueiaEdicao) {
				if (item.getFrequenciaEntrega() == null || item.getIndFrequenciaEntrega() == null) {
					if (item.getIndFrequenciaEntrega() == null) {
						itemPacVO.setIndFrequencia(DominioFrequenciaEntrega.TOTAL);
					}
					
					if (item.getFrequenciaEntrega() == null) {
						itemPacVO.setFrequenciaEntrega(Integer.valueOf("1"));
					}
					
					this.listCompleta.add(itemPacVO);
					this.atualizarListaEdicoes(itemPacVO);
				} else {
					this.listCompleta.add(itemPacVO);
				}
			} else {
				this.listCompleta.add(itemPacVO);
			}
		}
		
		// sempre quando clica no pesquisar deve ordenar pelo numeroItem
		this.currentComparator = null;
		this.ordenar("numeroItem");

	}

	public void ordenar(String propriedade) {
		Comparator<ScoItemPacVO> comparator = null;

		// se mesma propriedade, reverte a ordem
		if (this.currentComparator != null
				&& this.currentSortProperty.equals(propriedade)) {
			comparator = new ReverseComparator(this.currentComparator);
		} else {
			// cria novo comparator para a propriedade escolhida
			comparator = new BeanComparator(propriedade, new NullComparator(
					PT_BR_COMPARATOR, false));
		}

		Collections.sort(this.listCompleta, comparator);

		// guarda ordenação corrente
		this.currentComparator = comparator;
		this.currentSortProperty = propriedade;
	}
	
	/**
	 * Comparator null safe e locale-sensitive String.
	 */
	private static final Comparator<Object> PT_BR_COMPARATOR = new Comparator<Object>() {
		@Override
		public int compare(Object o1, Object o2) {

			final Collator collator = Collator.getInstance(new Locale("pt",
					"BR"));
			collator.setStrength(Collator.PRIMARY);

			return ((Comparable<Object>) o1).compareTo(o2);
		}
	};
	
	public Boolean getGerouArquivo() {
		return gerouArquivo;
	}

	public void setGerouArquivo(Boolean gerouArquivo) {
		this.gerouArquivo = gerouArquivo;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public void getGerarArquivo() throws ApplicationBusinessException {
		try {
			fileName = pacFacade.geraArquivoItensPAC(listCompleta,numeroPac);
			gerouArquivo = Boolean.TRUE;
			this.dispararDownload();			
			
		} catch(IOException e) {
			gerouArquivo = Boolean.FALSE;
			apresentarExcecaoNegocio(new BaseException(
					AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage()));
		}
	}
	
	/**
	 * Dispara o download para o arquivo CSV do relatório.
	 */
	public void dispararDownload(){
		if(fileName != null){
			try {
				this.download(fileName,"ITENS_PAC_"+numeroPac.toString()+EXTENSAO_CSV, CONTENT_TYPE_CSV);	
				setGerouArquivo(Boolean.FALSE);
				fileName = null;				
			} catch (IOException e) {
				apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage()));
			}
		}
	}
	
	/*public void getExecutarDownload() {
		if (fileName != null) {
			try {
				this.download(fileName,"ITENS_PAC_"+numeroPac.toString()+EXTENSAO_CSV, CONTENT_TYPE_CSV);				
				fileName = null;
				gerouArquivo = Boolean.FALSE;
			} catch (IOException e) {
				apresentarExcecaoNegocio(new BaseException(
						AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage()));
			}
		}
	}*/
		
	// gets and set
	public Integer getNumeroPac() {
		return numeroPac;
	}

	public void setNumeroPac(Integer numeroPac) {
		this.numeroPac = numeroPac;
	}

	public String getDescricaoPac() {
		return descricaoPac;
	}

	public void setDescricaoPac(String descricaoPac) {
		this.descricaoPac = descricaoPac;
	}

	public Boolean getIndExcluido() {
		return indExcluido;
	}

	public void setIndExcluido(Boolean indExcluido) {
		this.indExcluido = indExcluido;
	}

	public Date getDataGeracaoPac() {
		return dataGeracaoPac;
	}

	public void setDataGeracaoPac(Date dataGeracaoPac) {
		this.dataGeracaoPac = dataGeracaoPac;
	}

	public String getModalidadePac() {
		return modalidadePac;
	}

	public void setModalidadePac(String modalidadePac) {
		this.modalidadePac = modalidadePac;
	}

	public String getNumeroDocumento() {
		return numeroDocumento;
	}

	public void setNumeroDocumento(String numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
	}

	public String getNumeroEdital() {
		return numeroEdital;
	}

	public void setNumeroEdital(String numeroEdital) {
		this.numeroEdital = numeroEdital;
	}

	public String getAnoEdital() {
		return anoEdital;
	}

	public void setAnoEdital(String anoEdital) {
		this.anoEdital = anoEdital;
	}

	public String getTipoEdital() {
		return tipoEdital;
	}
	
	public void setTipoEdital(String tipoEdital) {
		this.tipoEdital = tipoEdital;
	}

	public String getSituacaoPac() {
		return situacaoPac;
	}

	public void setSituacaoPac(String situacaoPac) {
		this.situacaoPac = situacaoPac;
	}

	public BigDecimal getValorTotalItens() {
		return valorTotalItens;
	}

	public void setValorTotalItens(BigDecimal valorTotalItens) {
		this.valorTotalItens = valorTotalItens;
	}	

	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}

	public List<ScoItemPacVO> getListAlteracoes() {
		return listAlteracoes;
	}

	public void setListAlteracoes(List<ScoItemPacVO> listAlteracoes) {
		this.listAlteracoes = listAlteracoes;
	}

	public String getObservacaoExclusao() {
		return observacaoExclusao;
	}

	public void setObservacaoExclusao(String observacaoExclusao) {
		this.observacaoExclusao = observacaoExclusao;
	}

	public Boolean getVoltarPanel() {
		return voltarPanel;
	}

	public void setVoltarPanel(Boolean voltarPanel) {
		this.voltarPanel = voltarPanel;
	}

	public ScoItemLicitacao getItemLicitacaoExclusao() {
		return itemLicitacaoExclusao;
	}

	public void setItemLicitacaoExclusao(ScoItemLicitacao itemLicitacaoExclusao) {
		this.itemLicitacaoExclusao = itemLicitacaoExclusao;
	}
	
	public ScoPontoParadaSolicitacao getPontoParadaAnterior() {
		return pontoParadaAnterior;
	}

	public void setPontoParadaAnterior(ScoPontoParadaSolicitacao pontoParadaAnterior) {
		this.pontoParadaAnterior = pontoParadaAnterior;
	}

	public Boolean getMostrarModalExclusao() {
		return mostrarModalExclusao;
	}

	public void setMostrarModalExclusao(Boolean mostrarModalExclusao) {
		this.mostrarModalExclusao = mostrarModalExclusao;
	}

	public List<ScoItemPacVO> getListCompleta() {
		return listCompleta;
	}

	public void setListCompleta(List<ScoItemPacVO> listCompleta) {
		this.listCompleta = listCompleta;
	}

	public Boolean getBloqueiaEdicao() {
		return bloqueiaEdicao;
	}

	public void setBloqueiaEdicao(Boolean bloqueiaEdicao) {
		this.bloqueiaEdicao = bloqueiaEdicao;
	}

	public Boolean getMostrarModalConfirmacaoExclusao() {
		return mostrarModalConfirmacaoExclusao;
	}

	public void setMostrarModalConfirmacaoExclusao(
			Boolean mostrarModalConfirmacaoExclusao) {
		this.mostrarModalConfirmacaoExclusao = mostrarModalConfirmacaoExclusao;
	}

	public Boolean getMostrarModalReordenar() {
		return mostrarModalReordenar;
	}

	public void setMostrarModalReordenar(Boolean mostrarModalReordenar) {
		this.mostrarModalReordenar = mostrarModalReordenar;
	}
	
	public String getMsgModalExclusao() {
		return msgModalExclusao;
	}

	public void setMsgModalExclusao(String msgModalExclusao) {
		this.msgModalExclusao = msgModalExclusao;
	}

	
}
