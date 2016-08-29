package br.gov.mec.aghu.faturamento.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.primefaces.event.CloseEvent;

import br.gov.mec.aghu.dominio.DominioItemPendencia;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.vo.PendenciasEncerramentoVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.StringUtil;

public class PesquisarPendenciasEncerramentoController extends ActionController  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6657828349500375975L;
	
	private List<DominioItemPendencia> itens;
	private DominioItemPendencia item;
	
	private Integer prontuario;
	private Integer conta;
	private Short itemConta;
	private String erro;
	private Date dtOperacao;
	private String programa;
	private Short tabItem;
	private Integer seqItem;
	private Long sus;
	private Integer hcpa;
	
	private Integer tabAtual; 
	
	
	private boolean pesquisaAtiva;
	
	private List<PendenciasEncerramentoVO> list = new ArrayList<PendenciasEncerramentoVO>();
	
	private PendenciasEncerramentoVO itemSelecionado;
	
	private Boolean permissao;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;

	@PostConstruct
	public void inicio() {
		begin(conversation, true);
	}

	public void init() {
		itens = new ArrayList<DominioItemPendencia>();
		itens.add(DominioItemPendencia.I1);
		itens.add(DominioItemPendencia.I2);
		itens.add(DominioItemPendencia.RE);
		itens.add(DominioItemPendencia.SO);
		limparPesquisa();
	}
	
	public void pesquisar() {
		if(validaForm()){
			try {
				list = faturamentoFacade.getPendenciasEncerramento(conta,itemConta,erro,prontuario,dtOperacao,programa,item,tabItem,seqItem,sus,hcpa);
				for (PendenciasEncerramentoVO item : list) {
					item.setErroTruncado(StringUtil.trunc(item.getErro(), true, (long)12));
					item.setPacienteTruncado(StringUtil.trunc(item.getPaciente(), true, (long)25));
				}
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
			this.setPesquisaAtiva(true);
		}
	}
	
	public void selecionar(PendenciasEncerramentoVO item,Integer tab){
		itemSelecionado = item;
		tabAtual = tab;
	}
	
	public void handleClose(CloseEvent event) {  
		itemSelecionado = null;
		tabAtual = 0;
    }  

	public void limparPesquisa() {
		// linha 1
		conta = null;
		itemConta = null;
		erro = null;
		prontuario = null;
		// linha 2
		dtOperacao = null;
		programa = null;
		// linha 3
		item = null;
		tabItem = null;
		seqItem = null;
		sus = null;
		hcpa = null;
		// listagem
		list = new ArrayList<PendenciasEncerramentoVO>();
		this.setPesquisaAtiva(false);
		// modal
		itemSelecionado = null;
		tabAtual = 0;
	}
	
	public boolean validaForm(){
		if(conta == null && itemConta == null && erro == null && prontuario == null && dtOperacao == null && programa == null){
			if(tabItem == null && seqItem == null && sus == null && hcpa == null){
				// dispara msg pelo menos 1 filtro
				this.apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_PELO_MENOS_UM_FILTRO");
				return false;
			}else if(item == null){
				// dispara msg pelo menos 1 filtro
				this.apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_PELO_MENOS_UM_FILTRO");
				return false;
			}
		}
		return true;
	}
	
	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Integer getConta() {
		return conta;
	}

	public void setConta(Integer conta) {
		this.conta = conta;
	}

	public boolean isPesquisaAtiva() {
		return pesquisaAtiva;
	}

	public void setPesquisaAtiva(boolean pesquisaAtiva) {
		this.pesquisaAtiva = pesquisaAtiva;
	}

	public List<PendenciasEncerramentoVO> getList() {
		return list;
	}

	public void setList(List<PendenciasEncerramentoVO> list) {
		this.list = list;
	}
	
	public Boolean getPermissao() {
		return permissao;
	}

	public void setPermissao(Boolean permissao) {
		this.permissao = permissao;
	}

	public List<DominioItemPendencia> getItens() {
		return itens;
	}

	public void setItens(List<DominioItemPendencia> itens) {
		this.itens = itens;
	}

	public DominioItemPendencia getItem() {
		return item;
	}

	public void setItem(DominioItemPendencia item) {
		this.item = item;
	}

	public Short getItemConta() {
		return itemConta;
	}

	public void setItemConta(Short itemConta) {
		this.itemConta = itemConta;
	}

	public String getErro() {
		return erro;
	}

	public void setErro(String erro) {
		this.erro = erro;
	}

	public Date getDtOperacao() {
		return dtOperacao;
	}

	public void setDtOperacao(Date dtOperacao) {
		this.dtOperacao = dtOperacao;
	}

	public String getPrograma() {
		return programa;
	}

	public void setPrograma(String programa) {
		this.programa = programa;
	}

	public Short getTabItem() {
		return tabItem;
	}

	public void setTabItem(Short tabItem) {
		this.tabItem = tabItem;
	}

	public Integer getSeqItem() {
		return seqItem;
	}

	public void setSeqItem(Integer seqItem) {
		this.seqItem = seqItem;
	}

	public Long getSus() {
		return sus;
	}

	public void setSus(Long sus) {
		this.sus = sus;
	}

	public Integer getHcpa() {
		return hcpa;
	}

	public void setHcpa(Integer hcpa) {
		this.hcpa = hcpa;
	}

	public PendenciasEncerramentoVO getItemSelecionado() {
		return itemSelecionado;
	}

	public void setItemSelecionado(PendenciasEncerramentoVO itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}

	public Integer getTabAtual() {
		return tabAtual;
	}

	public void setTabAtual(Integer tabAtual) {
		this.tabAtual = tabAtual;
	}
	
}
