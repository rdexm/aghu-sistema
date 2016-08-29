package br.gov.mec.aghu.compras.autfornecimento.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.autfornecimento.vo.PesquisaItensPendentesPacVO;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.model.FsoGrupoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesaId;
import br.gov.mec.aghu.model.FsoVerbaGestao;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.Severity;


public class AutorizacaoFornecimentoItensPacController extends ActionController {

	private static final long serialVersionUID = -1552724787813172347L;
	
	private Integer numeroPac;
	private ScoLicitacao pac = new ScoLicitacao();
	private FsoVerbaGestao verbaGestao = new FsoVerbaGestao();
	private FsoNaturezaDespesa naturezaDesp = new FsoNaturezaDespesa();
	private FsoNaturezaDespesaId naturezaDespID = new FsoNaturezaDespesaId();
	private FsoGrupoNaturezaDespesa grupoNaturezaDesp = new FsoGrupoNaturezaDespesa();
	
	private String voltarPara;
	private String descricao;
	private String modalidade;
	
	private List<PesquisaItensPendentesPacVO> itensPendentesPacVOs;
	
	@EJB
	private IPacFacade pacFacade;
	
	@EJB
	protected IAutFornecimentoFacade autFornecimentoFacade;


	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void iniciar() {
	 

	 

		
		if (this.numeroPac != null) {
			pac = this.pacFacade.obterLicitacao(this.numeroPac);
		} 
		
		if (pac == null) {
			this.apresentarMsgNegocio(Severity.FATAL, "MENSAGEM_LICITACAO_NAO_ENCONTRADA", this.numeroPac);
		}
		else {
			modalidade = pac.getModalidadeLicitacao().getCodigo() + "-" + pac.getModalidadeLicitacao().getDescricao();
			this.itensPendentesPacVOs = this.autFornecimentoFacade.pesquisarItemLicitacaoPorNumeroLicitacao(this.numeroPac);
		}
	
	}
	
	

	//botões
	public String voltar() {
		return this.voltarPara;
	}
	
	//gets and sets
	public ScoLicitacao getPac() {
		return pac;
	}

	public void setPac(ScoLicitacao pac) {
		this.pac = pac;
	}
	
	public FsoVerbaGestao getVerbaGestao() {
		return verbaGestao;
	}

	public void setVerbaGestao(FsoVerbaGestao verbaGestao) {
		this.verbaGestao = verbaGestao;
	}
	
	public FsoNaturezaDespesa getNaturezaDesp() {
		return naturezaDesp;
	}

	public void setNaturezaDesp(FsoNaturezaDespesa naturezaDesp) {
		this.naturezaDesp = naturezaDesp;
	}

	public FsoNaturezaDespesaId getNaturezaDespID() {
		return naturezaDespID;
	}

	public void setNaturezaDespID(FsoNaturezaDespesaId naturezaDespID) {
		this.naturezaDespID = naturezaDespID;
	}
	
	public FsoGrupoNaturezaDespesa getGrupoNaturezaDesp() {
		return grupoNaturezaDesp;
	}

	public void setGrupoNaturezaDesp(FsoGrupoNaturezaDespesa grupoNaturezaDesp) {
		this.grupoNaturezaDesp = grupoNaturezaDesp;
	}
	
	public Integer getNumeroPac() {
		return numeroPac;
	}

	public void setNumeroPac(Integer numeroPac) {
		this.numeroPac = numeroPac;
	}
	
	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public String getModalidade() {
		return modalidade;
	}

	public void setModalidade(String modalidade) {
		this.modalidade = modalidade;
	}

	public List<PesquisaItensPendentesPacVO> getItensPendentesPacVOs() {
		return itensPendentesPacVOs;
	}

	public void setItensPendentesPacVOs(
			List<PesquisaItensPendentesPacVO> itensPendentesPacVOs) {
		this.itensPendentesPacVOs = itensPendentesPacVOs;
	}
}