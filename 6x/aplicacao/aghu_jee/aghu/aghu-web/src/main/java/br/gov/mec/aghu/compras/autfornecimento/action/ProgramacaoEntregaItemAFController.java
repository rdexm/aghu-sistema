package br.gov.mec.aghu.compras.autfornecimento.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.vo.ProgramacaoEntregaItemAFVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class ProgramacaoEntregaItemAFController extends ActionController {

	private static final String CONSULTA_SCSS_LIST = "compras-consultaSCSSList";

	private static final String PESQUISAR_PLANJ_PROG_ENTREGA_ITENS_AF = "pesquisarPlanjProgEntregaItensAF";

	private static final String ESTOQUE_ESTATISTICA_CONSUMO = "estoque-estatisticaConsumo";
	
	private static final String COMPRAS_ULTIMAS_COMPRAS = "compras-ultimasCompras";

	private static final long serialVersionUID = -1735260782789657323L;

	private Integer numeroAF;
	
	private List<ProgramacaoEntregaItemAFVO> listaProgramacaoEntregaItemAF;
	
	private List<ProgramacaoEntregaItemAFVO> listaProgramacaoEntregaItemAFAlterados;
	
	private String observacao;

	private ProgramacaoEntregaItemAFVO programacaoEntregaItemAFVOSelecionado;
	
	private Integer codigoMaterialSelecionado;
	
	private ProgramacaoEntregaItemAFVO itemSelecionado = new ProgramacaoEntregaItemAFVO();
	
	@EJB
	private IAutFornecimentoFacade autFornecimentoFacade;
	
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void iniciar() {
	 

	 
		
		listaProgramacaoEntregaItemAFAlterados = new ArrayList<ProgramacaoEntregaItemAFVO>();
		listaProgramacaoEntregaItemAF = autFornecimentoFacade.listarProgramacaoEntregaItensAF(numeroAF);
		codigoMaterialSelecionado = null;
	
	}
	
	
	public void buscarObservacao(ProgramacaoEntregaItemAFVO programacaoEntregaItemAFSelecionado) {
		observacao = autFornecimentoFacade.buscarQuantidadeAFsProgramadas(programacaoEntregaItemAFSelecionado.getCodigoMaterial(), 
																			programacaoEntregaItemAFSelecionado.getNumero());
		
		if(programacaoEntregaItemAFVOSelecionado != null) {
			programacaoEntregaItemAFVOSelecionado.setCorFundoLinha("");
		}
		programacaoEntregaItemAFSelecionado.setCorFundoLinha("#EEE8AA");
		programacaoEntregaItemAFVOSelecionado = programacaoEntregaItemAFSelecionado;
		programacaoEntregaItemAFVOSelecionado.setCorFundoLinha("#EEE8AA");
		codigoMaterialSelecionado = programacaoEntregaItemAFVOSelecionado.getCodigoMaterial();
		
		setObservacao("Saldo programado em outras AFs: " + observacao);
	}
	
	public void verificaAtualizacaoRegistro(ProgramacaoEntregaItemAFVO programacaoEntregaItemAFVO) {
		autFornecimentoFacade.verificaAtualizacaoRegistro(programacaoEntregaItemAFVO, listaProgramacaoEntregaItemAFAlterados);		
	}
	
	public void gravarProgramacaoEntregaItemAF() {
		try {
			autFornecimentoFacade.gravarProgramacaoEntregaItemAF(listaProgramacaoEntregaItemAFAlterados);
			iniciar();
			apresentarMsgNegocio(Severity.INFO, "M1_PROGENTITEMAF");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String redirecionarUltimasCompras(){
		return COMPRAS_ULTIMAS_COMPRAS;
	}

	public String voltar() {
		observacao = null;
		return PESQUISAR_PLANJ_PROG_ENTREGA_ITENS_AF;
	}
	
	public String scsMaterial() {
		return CONSULTA_SCSS_LIST;
	}
	
	public String redirecionarEstoqueEstatisticaConsumo(){
		return ESTOQUE_ESTATISTICA_CONSUMO;
	}
	
	public List<ProgramacaoEntregaItemAFVO> getListaProgramacaoEntregaItemAF() {
		return listaProgramacaoEntregaItemAF;
	}

	public void setListaProgramacaoEntregaItemAF(
			List<ProgramacaoEntregaItemAFVO> listaProgramacaoEntregaItemAF) {
		this.listaProgramacaoEntregaItemAF = listaProgramacaoEntregaItemAF;
	}

	public Integer getNumeroAF() {
		return numeroAF;
	}

	public void setNumeroAF(Integer numeroAF) {
		this.numeroAF = numeroAF;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public ProgramacaoEntregaItemAFVO getProgramacaoEntregaItemAFVOSelecionado() {
		return programacaoEntregaItemAFVOSelecionado;
	}

	public void setProgramacaoEntregaItemAFVOSelecionado(
			ProgramacaoEntregaItemAFVO programacaoEntregaItemAFVOSelecionado) {
		this.programacaoEntregaItemAFVOSelecionado = programacaoEntregaItemAFVOSelecionado;
	}

	public Integer getCodigoMaterialSelecionado() {
		return codigoMaterialSelecionado;
	}

	public void setCodigoMaterialSelecionado(Integer codigoMaterialSelecionado) {
		this.codigoMaterialSelecionado = codigoMaterialSelecionado;
	}

	public ProgramacaoEntregaItemAFVO getItemSelecionado() {
		return itemSelecionado;
	}

	public void setItemSelecionado(ProgramacaoEntregaItemAFVO itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}

}
