package br.gov.mec.aghu.compras.pac.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.compras.pac.vo.PreItemPacVO;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacao;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

public class AssociaScSsPacController extends ActionController {

	private static final long serialVersionUID = -854264626762588092L;

	private static final String PAGE_SOLICITACAO_SERVICO_CRUD = "compras-solicitacaoServicoCRUD";
	private static final String PAGE_SOLICITACAO_COMPRA_CRUD = "compras-solicitacaoCompraCRUD";
	private static final String PAGE_ASSOCIA_SCSS_PAC_LIST = "associaScSsPacList";
	private static final String PAGE_ITEM_PAC_LIST = "itemPacList";
	private static final String PAGE_PROCESSO_ADM_COMPRA_CRUD = "processoAdmCompraCRUD";
	
	// campos PAC(licitação)
	private Integer numeroPac;
	private BigDecimal vlrTotalItens = BigDecimal.ZERO;
	private BigDecimal vlrPreItem = BigDecimal.ZERO;
	private BigDecimal vlrTotalPreItens = BigDecimal.ZERO;
	private String descricaoPac;
	private String codModalidade;
	private Boolean indEngenharia = false;
	private Boolean indExcedeuVlrMax = false;
	private ScoLicitacao pac = new ScoLicitacao();
	private ScoModalidadeLicitacao modalidade = new ScoModalidadeLicitacao();

	// campos filtros solicitações
	private DominioTipoSolicitacao tipoSolicitacao;
	private Integer numeroInicial;
	private Integer numeroFinal;

	// Controles grid
	private String voltarPara;
	private Boolean adicionou = false;

	List<PreItemPacVO> listaItensPac = new ArrayList<PreItemPacVO>();

	private List<PreItemPacVO> listaItensAdicionados = new ArrayList<PreItemPacVO>();

	@EJB
	private IPacFacade pacFacade;

	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;

	private Map<PreItemPacVO, Boolean> objetoItensSelecionados = new HashMap<PreItemPacVO, Boolean>();

	private Boolean valorAlternarTodos = false;

	public enum AssociaScSsPacControllerExceptionCode implements BusinessExceptionCode {
		MENSAGEM_SOLICITACOES_EXCLUIDAS, MENSAGEM_SOLICITACOES_NAO_AUTORIZADAS, MENSAGEM_SOLICITACOES_ASSOCIADAS, MENSAGEM_ITEM_GRAVADO_COM_SUCESSO, MENSAGEM_CAMPOS_OBRIGATORIOS, MENSAGEM_ERRO_ASSOCIAR_SC_SC_PAC
		,MENSAGEM_TEM_GERACAO_TITULO_AVULSO;
	}

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void iniciar() {
		this.setTipoSolicitacao(DominioTipoSolicitacao.SC);
		if (listaItensAdicionados != null && listaItensAdicionados.size() > 0) {
			if (listaItensPac == null) {
				listaItensPac = new ArrayList<PreItemPacVO>();
			}
			for (PreItemPacVO item : listaItensAdicionados) {
				if (!listaItensPac.contains(item)) {
					listaItensPac.add(item);
					this.objetoItensSelecionados.put(item, true);
				}
			}
		}

		if (!this.obterDadosLicitacao()) {
			this.apresentarMsgNegocio(Severity.FATAL, "MENSAGEM_LICITACAO_NAO_ENCONTRADA", this.numeroPac);
		}
	
	}
	

	public Boolean obterDadosLicitacao() {

		if (this.numeroPac != null) {
			pac = this.pacFacade.obterLicitacao(this.numeroPac);

			if (pac != null) {
				this.setDescricaoPac(pac.getDescricao());
				this.vlrTotalItens = this.pacFacade.obterValorTotalPorNumeroLicitacao(this.numeroPac);
				this.indEngenharia = pac.getIndEngenharia();
				this.codModalidade = pac.getModalidadeLicitacao().getCodigo();

				atualizarVlrTotalPac();
			}
		}

		return (pac != null);
	}

	// botões
	public String redirecionaSolicitacaoServico() {
		return PAGE_SOLICITACAO_SERVICO_CRUD;
	}

	public String redirecionaSolicitacaoCompra() {
		return PAGE_SOLICITACAO_COMPRA_CRUD;
	}

	public void limpar() {		
		this.limparPesquisa();
	}

	public void limparPesquisa() {
		this.setNumeroInicial(null);
		this.setNumeroFinal(null);		
	}

	public String voltar() {
		this.listaItensPac = new ArrayList<PreItemPacVO>();
		return voltarPara;
	}

	public String abrirPesquisa() {
		this.listaItensAdicionados = null;

		return PAGE_ASSOCIA_SCSS_PAC_LIST;
	}

	public String pesquisar() {

		this.adicionou = true;
		List<PreItemPacVO> listaPesquisa = null;

		try {
			

			listaPesquisa = pacFacade.pesquisarPreItensPac(tipoSolicitacao, numeroInicial, numeroFinal);

			listaPesquisa = this.verificaMensagensErro(listaPesquisa);

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

		if (listaPesquisa != null && listaPesquisa.size() > 0 && listaPesquisa.get(0).getNumero() != null) {

			if (listaItensPac == null) {
				listaItensPac = new ArrayList<PreItemPacVO>();
			}

			if (listaPesquisa.size() == 1) {
				this.objetoItensSelecionados.put(listaPesquisa.get(0), true);
			}

			for (PreItemPacVO item : listaPesquisa) {
				if (!listaItensPac.contains(item)) {
					listaItensPac.add(item);
				}
			}
		} else if (listaPesquisa != null && listaPesquisa.size() > 0 && listaPesquisa.get(0).getNumero() == null) {
			listaPesquisa = null;
		}

		if (listaItensPac != null && listaItensPac.size() > 0) {
			atualizarVlrTotalPac();
		}

		this.setNumeroInicial(null);
		this.setNumeroFinal(null);
		return null;
	}

	private List<PreItemPacVO> verificaMensagensErro(List<PreItemPacVO> listaPesquisa) throws ApplicationBusinessException {

		String msgPesquisa = null;

		if (listaPesquisa.size() > 0 && listaPesquisa.get(0).getMsgAssociadas() != null) {
			msgPesquisa = listaPesquisa.get(0).getMsgAssociadas();
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_SOLICITACOES_ASSOCIADAS", msgPesquisa);
//			throw new ApplicationBusinessException(AssociaScSsPacControllerExceptionCode.MENSAGEM_SOLICITACOES_ASSOCIADAS,
//					msgPesquisa);
		}

		if (listaPesquisa.size() > 0 && listaPesquisa.get(0).getMsgNAutorizadas() != null) {
			msgPesquisa = listaPesquisa.get(0).getMsgNAutorizadas();

			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_SOLICITACOES_NAO_AUTORIZADAS", msgPesquisa);
//			throw new ApplicationBusinessException(AssociaScSsPacControllerExceptionCode.MENSAGEM_SOLICITACOES_NAO_AUTORIZADAS,
//					msgPesquisa);
		}

		if (listaPesquisa.size() > 0 && listaPesquisa.get(0).getMsgExcluidas() != null) {
			msgPesquisa = listaPesquisa.get(0).getMsgExcluidas();
			
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_SOLICITACOES_EXCLUIDAS", msgPesquisa);
//			throw new ApplicationBusinessException(AssociaScSsPacControllerExceptionCode.MENSAGEM_SOLICITACOES_EXCLUIDAS,
//					msgPesquisa);
		}
		
		if (listaPesquisa.size() > 0 && listaPesquisa.get(0).getMsgTituloAvulso() != null) {
			msgPesquisa = listaPesquisa.get(0).getMsgTituloAvulso();
			
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_TEM_GERACAO_TITULO_AVULSO", msgPesquisa);
//			throw new ApplicationBusinessException(AssociaScSsPacControllerExceptionCode.MENSAGEM_TEM_GERACAO_TITULO_AVULSO,
//					msgPesquisa);
		}
		

		return listaPesquisa;

	}

	public void atualizarVlrTotalPac() {

		this.vlrPreItem = BigDecimal.ZERO;
		this.vlrTotalPreItens = BigDecimal.ZERO;
		if (listaItensPac != null) {
			for (PreItemPacVO item : listaItensPac) {

				// Calcula o valor total do item depois da alteração do valor
				// unitário
				if (item.getValorUnitPrevisto() != null) {
					if (item.getQtdSC() == null) {
						this.vlrPreItem = item.getValorUnitPrevisto().multiply(new BigDecimal(item.getQtdSS()));
					} else {
						this.vlrPreItem = item.getValorUnitPrevisto().multiply(new BigDecimal(item.getQtdSC()));
					}

					this.setVlrTotalPreItens(this.getVlrTotalPreItens().add(this.vlrPreItem));
				}
			}
		}

		// Atualiza campo da tela com o valor total do PAC
		this.vlrTotalItens = this.pacFacade.obterValorTotalPorNumeroLicitacao(this.numeroPac);
		this.setVlrTotalItens(this.getVlrTotalItens().add(this.getVlrTotalPreItens()));

		// Valida se o valor máximo do PAC foi atingido
		validarVlrMaximoPac();
	}

	public Boolean validarVlrMaximoPac() {

		this.indExcedeuVlrMax = false;
		modalidade = this.comprasCadastrosBasicosFacade.obterScoModalidadeLicitacaoPorChavePrimaria(this.codModalidade);

		if (modalidade != null) {
			if (this.indEngenharia) {
				if (modalidade.getValorPermitidoEng() != null
						&& this.getVlrTotalItens().compareTo(modalidade.getValorPermitidoEng()) > 0) {
					this.indExcedeuVlrMax = true;
				}
			} else {
				if (modalidade.getValorPermitido() != null
						&& this.getVlrTotalItens().compareTo(modalidade.getValorPermitido()) > 0) {
					this.indExcedeuVlrMax = true;
				}
			}
		}

		return (this.indExcedeuVlrMax);
	}

	public String gravar() {

		List<PreItemPacVO> listaItensGravar = new ArrayList<PreItemPacVO>();

		for (PreItemPacVO item : listaItensPac) {
			if (objetoItensSelecionados.get(item)){
				listaItensGravar.add(item);
			}
		}
		
		/*for (Entry<PreItemPacVO, Boolean> entry : objetoItensSelecionados.entrySet()) {
			if (entry.getValue()) {
				listaItensGravar.add(entry.getKey());
			}
		}*/

		this.objetoItensSelecionados = new HashMap<PreItemPacVO, Boolean>();
		
		try {
			this.pacFacade.validaValoresRegrasOrcamentarias(listaItensGravar);
		} catch (ApplicationBusinessException e1) {
			this.apresentarExcecaoNegocio(e1);
			this.listaItensAdicionados = null;
			return null;
		}
		

		try {
			this.pacFacade.persistirItemPac(this.pac, listaItensGravar);
		} catch (BaseException e) {
			this.apresentarMsgNegocio(Severity.ERROR,
					AssociaScSsPacControllerExceptionCode.MENSAGEM_ERRO_ASSOCIAR_SC_SC_PAC.toString());
			this.listaItensAdicionados = null;
			return null;
		}

		this.listaItensPac = new ArrayList<PreItemPacVO>();
		this.listaItensAdicionados = null;
		this.apresentarMsgNegocio(Severity.INFO,
				AssociaScSsPacControllerExceptionCode.MENSAGEM_ITEM_GRAVADO_COM_SUCESSO.toString());
		
		if (this.voltarPara.equalsIgnoreCase(PAGE_PROCESSO_ADM_COMPRA_CRUD)) {
			return PAGE_PROCESSO_ADM_COMPRA_CRUD;
		}
		return PAGE_ITEM_PAC_LIST;
	}

	public void alternarSelecaoTodos() {
		for (PreItemPacVO vo : this.listaItensPac) {
			vo.setMarked(this.getValorAlternarTodos());
			this.objetoItensSelecionados.put(vo, this.getValorAlternarTodos());
		}
	}

	// gets and sets
	public ScoLicitacao getPac() {
		return pac;
	}

	public void setPac(ScoLicitacao pac) {
		this.pac = pac;
	}

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

	public String getCodModalidade() {
		return codModalidade;
	}

	public void setCodModalidade(String codModalidade) {
		this.codModalidade = codModalidade;
	}

	public ScoModalidadeLicitacao getModalidade() {
		return modalidade;
	}

	public void setModalidade(ScoModalidadeLicitacao modalidade) {
		this.modalidade = modalidade;
	}

	public Boolean getIndEngenharia() {
		return indEngenharia;
	}

	public void setIndEngenharia(Boolean indEngenharia) {
		this.indEngenharia = indEngenharia;
	}

	public Boolean getIndExcedeuVlrMax() {
		return indExcedeuVlrMax;
	}

	public void setIndExcedeuVlrMax(Boolean indExcedeuVlrMax) {
		this.indExcedeuVlrMax = indExcedeuVlrMax;
	}

	public BigDecimal getVlrTotalItens() {
		return vlrTotalItens;
	}

	public void setVlrTotalItens(BigDecimal vlrTotalItens) {
		this.vlrTotalItens = vlrTotalItens;
	}

	public BigDecimal getVlrPreItem() {
		return vlrPreItem;
	}

	public void setVlrPreItem(BigDecimal vlrPreItem) {
		this.vlrPreItem = vlrPreItem;
	}

	public BigDecimal getVlrTotalPreItens() {
		return vlrTotalPreItens;
	}

	public void setVlrTotalPreItens(BigDecimal vlrTotalPreItens) {
		this.vlrTotalPreItens = vlrTotalPreItens;
	}

	public Integer getNumeroInicial() {
		return numeroInicial;
	}

	public void setNumeroInicial(Integer numeroInicial) {
		this.numeroInicial = numeroInicial;
	}

	public Integer getNumeroFinal() {
		return numeroFinal;
	}

	public void setNumeroFinal(Integer numeroFinal) {
		this.numeroFinal = numeroFinal;
	}

	public DominioTipoSolicitacao getTipoSolicitacao() {
		return tipoSolicitacao;
	}

	public void setTipoSolicitacao(DominioTipoSolicitacao tipoSolicitacao) {
		this.tipoSolicitacao = tipoSolicitacao;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public Boolean getAdicionou() {
		return adicionou;
	}

	public void setAdicinou(Boolean adicionou) {
		this.adicionou = adicionou;
	}

	public List<PreItemPacVO> getListaItensPac() {
		return listaItensPac;
	}

	public void setListaItensPac(List<PreItemPacVO> listaItensPac) {
		this.listaItensPac = listaItensPac;
	}

	public List<PreItemPacVO> getListaItensAdicionados() {
		return listaItensAdicionados;
	}

	public void setListaItensAdicionados(List<PreItemPacVO> listaItensAdicionados) {
		this.listaItensAdicionados = listaItensAdicionados;
	}

	public Map<PreItemPacVO, Boolean> getObjetoItensSelecionados() {
		return objetoItensSelecionados;
	}

	public void setObjetoItensSelecionados(Map<PreItemPacVO, Boolean> objetoItensSelecionados) {
		this.objetoItensSelecionados = objetoItensSelecionados;
	}

	public Boolean getValorAlternarTodos() {
		return valorAlternarTodos;
	}

	public void setValorAlternarTodos(Boolean valorAlternarTodos) {
		this.valorAlternarTodos = !this.valorAlternarTodos;
	}
}