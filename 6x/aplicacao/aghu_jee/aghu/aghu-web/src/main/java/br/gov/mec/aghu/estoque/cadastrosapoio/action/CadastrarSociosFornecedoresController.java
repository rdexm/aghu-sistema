package br.gov.mec.aghu.estoque.cadastrosapoio.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.AipCidades;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoSocios;
import br.gov.mec.aghu.model.ScoSociosFornecedores;
import br.gov.mec.aghu.model.ScoSociosFornecedoresId;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class CadastrarSociosFornecedoresController extends ActionController {

	private static final String COMPRAS_MANTER_CADASTRO_FORNECEDOR = "compras-manterCadastroFornecedor";

	private static final String PESQUISAR_SOCIOS_FORNECEDORES = "estoque-pesquisarSociosFornecedores";

	private static final long serialVersionUID = 13838382382832332L;
	
	private static final String MARCA_FORNECEDOR_SELECIONADO = "background-color: yellow";
	
	public enum CadastrarSociosFornecedoresControllerExceptionCode {
		MSG_SOCIOS_FORNECEDOR_M5,
		MSG_SOCIOS_FORNECEDOR_M6
	}
	
	@EJB
	protected IComprasFacade comprasFacade;
	
	@EJB
	protected IEstoqueFacade estoqueFacade;
	
	private ScoFornecedor filtroFornecedor;
	private ScoSocios filtroSocio;
	private Integer seqSocio;
	private Integer numeroFornecedor;
	private Integer fornecedorSelecionado;
	private ScoFornecedor exclusaoFornecedor;
	private List<ScoSociosFornecedores> listaSociosFornecedores;
	private List<ScoFornecedor> listaScoFornecedores;
	private String voltarPara;
	private boolean desabilitaConsultaFornecedor;
	
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void inicio (){
		
		//se receber o codigo do socio ja carrega as informações do mesmo
		//se receber o numero do fornecedor já adiciona o mesmo a lista de fornecedores
		limparPesquisa();
		this.filtroSocio = new ScoSocios();
		if(seqSocio != null){
			this.filtroSocio = comprasFacade.buscarSocioPorSeq(seqSocio);
			pesquisar();
		}
		//this.//setIgnoreInitPageConfig(true);
		
	}
	
	public void pesquisar(){
		this.listaSociosFornecedores = this.comprasFacade.listarFornecedoresPorSeqSocio(filtroSocio.getSeq());
		this.listaScoFornecedores = new ArrayList<ScoFornecedor>();
		if(listaSociosFornecedores != null && !listaSociosFornecedores.isEmpty()){
			for (ScoSociosFornecedores socioFornecedor : listaSociosFornecedores) {
				listaScoFornecedores.add(socioFornecedor.getFornecedor());
			}
			ordenarLista();
		}
	}

	public void ordenarLista() {
		Collections.sort(this.listaScoFornecedores, new Comparator<ScoFornecedor>() {
			@Override
			public int compare(ScoFornecedor o1, ScoFornecedor o2) {
				ScoFornecedor c1 = (ScoFornecedor) o1;  
				ScoFornecedor c2 = (ScoFornecedor) o2;  
				return c1.getRazaoSocial().compareTo(c2.getRazaoSocial());  
			}
		});
	}
	
	public void limparPesquisa(){
		this.filtroFornecedor = null;
		if(this.filtroSocio != null){
			this.filtroSocio.setCpf(null);
			this.filtroSocio.setNome(null);
			this.filtroSocio.setRg(null);
		}
		this.fornecedorSelecionado = null;
		this.listaScoFornecedores = null;
	}
	
	public void excluiFornecedorSuggestion(){
		this.fornecedorSelecionado = null;
	}
	
	public void adicionar(){
		if(filtroFornecedor != null){
			if(this.verificaFornecedorJaCadastrado(filtroFornecedor)){
				this.fornecedorSelecionado = filtroFornecedor.getNumero();
				this.apresentarMsgNegocio(Severity.INFO, CadastrarSociosFornecedoresControllerExceptionCode.MSG_SOCIOS_FORNECEDOR_M5.toString());
			} else{
				if(this.listaScoFornecedores == null){
					this.listaScoFornecedores = new ArrayList<ScoFornecedor>();
				}
				this.listaScoFornecedores.add(comprasFacade.obterScoFornecedorComCidadePorChavePrimaria(filtroFornecedor.getNumero()));
				ordenarLista();
			}
		}
	}
	
	public String fonecedorSelecionado(Integer numero){
		if(numero != null && numero.equals(fornecedorSelecionado)){
			return MARCA_FORNECEDOR_SELECIONADO;
		}
		return null;
	}
	
	private boolean verificaFornecedorJaCadastrado(ScoFornecedor fornecedor) {
		if(this.listaScoFornecedores == null || this.listaScoFornecedores .isEmpty()){
			return false;
		} else {
			return listaScoFornecedores.contains(fornecedor);	
		}
	}

	public void gravar(){
		try {
			estoqueFacade.gravarSocioFornecedores(filtroSocio, listaScoFornecedores);
			this.apresentarMsgNegocio(Severity.INFO, CadastrarSociosFornecedoresControllerExceptionCode.MSG_SOCIOS_FORNECEDOR_M6.toString());
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void excluir(){
		if(exclusaoFornecedor != null && filtroSocio != null && filtroSocio.getSeq() != null){
			ScoSociosFornecedoresId id = new ScoSociosFornecedoresId(filtroSocio.getSeq(), exclusaoFornecedor.getNumero());
			ScoSociosFornecedores scoSociosFornecedores = comprasFacade.buscarScoSociosFornecedores(id);
			if(scoSociosFornecedores == null) {	
				listaScoFornecedores.remove(exclusaoFornecedor);
			} else {
				estoqueFacade.removerScoSociosFornecedores(scoSociosFornecedores);
				listaScoFornecedores.remove(exclusaoFornecedor);
			}
		}
	}
	
	public String cancelar(){
		
		if(StringUtils.isNotBlank(voltarPara)){
			if(voltarPara.equalsIgnoreCase("VOLTAR_CADASTRO_FORNECEDOR")){
				return COMPRAS_MANTER_CADASTRO_FORNECEDOR;
			}
			else if (voltarPara.equalsIgnoreCase("VOLTAR_PESQUISA_SOCIO_FORNECEDOR")){
				return PESQUISAR_SOCIOS_FORNECEDORES;
			}
		}
		return voltarPara;
	}
	
	public Boolean convertDominioSituacaoToBoolean(DominioSituacao situacao){
		return situacao.isAtivo();
	}
	
	public String verificaNomeCidade(AipCidades cidade){
		return cidade != null ? cidade.getNome() : null;
	}
	
	public String formataTelefone(ScoFornecedor fornecedor){
		if(fornecedor.getDdd() != null && fornecedor.getFone() != null) {
			return fornecedor.getDdd().toString() + " - " + fornecedor.getFone().toString();
		} else if  (fornecedor.getDdd() == null && fornecedor.getFone() != null){
			return fornecedor.getFone().toString();
		}
		else {
			return "";
		}
	}
	
	public List<ScoFornecedor> pesquisarFornecedoresPorCgcCpfRazaoSocial(String parametro) {
		return this.returnSGWithCount(this.comprasFacade.listarFornecedoresAtivos(parametro, 0, 100, null, true),pesquisarFornecedoresPorCgcCpfRazaoSocialCount(parametro)); 
	}
	
	public Long pesquisarFornecedoresPorCgcCpfRazaoSocialCount(String parametro) {
		return this.comprasFacade.listarFornecedoresAtivosCount(parametro);		
	}

	public ScoFornecedor getFiltroFornecedor() {
		return filtroFornecedor;
	}

	public void setFiltroFornecedor(ScoFornecedor filtroFornecedor) {
		this.filtroFornecedor = filtroFornecedor;
	}

	public Integer getSeqSocio() {
		return seqSocio;
	}

	public void setSeqSocio(Integer seqSocio) {
		this.seqSocio = seqSocio;
	}

	public void setFiltroSocio(ScoSocios filtroSocio) {
		this.filtroSocio = filtroSocio;
	}


	public ScoSocios getFiltroSocio() {
		return filtroSocio;
	}


	public void setListaScoFornecedores(List<ScoFornecedor> listaScoFornecedor) {
		this.listaScoFornecedores = listaScoFornecedor;
	}


	public List<ScoFornecedor> getListaScoFornecedores() {
		return listaScoFornecedores;
	}

	public void setNumeroFornecedor(Integer numeroFornecedor) {
		this.numeroFornecedor = numeroFornecedor;
	}

	public Integer getNumeroFornecedor() {
		return numeroFornecedor;
	}
	
	public Integer getFornecedorSelecionado() {
		return fornecedorSelecionado;
	}

	public void setFornecedorSelecionado(Integer fornecedorSelecionado) {
		this.fornecedorSelecionado = fornecedorSelecionado;
	}

	public void setExclusaoFornecedor(ScoFornecedor exclusaoFornecedor) {
		this.exclusaoFornecedor = exclusaoFornecedor;
	}

	public ScoFornecedor getExclusaoFornecedor() {
		return exclusaoFornecedor;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setDesabilitaConsultaFornecedor(boolean desabilitaConsultaFornecedor) {
		this.desabilitaConsultaFornecedor = desabilitaConsultaFornecedor;
	}

	public boolean isDesabilitaConsultaFornecedor() {
		return desabilitaConsultaFornecedor;
	}
}
