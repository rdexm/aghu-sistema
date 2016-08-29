package br.gov.mec.aghu.compras.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.contaspagar.vo.ConsultaGeralTituloVO;
import br.gov.mec.aghu.compras.contaspagar.vo.SolicitacaoTituloVO;
import br.gov.mec.aghu.dominio.DominioSituacaoTitulo;
import br.gov.mec.aghu.dominio.DominioSituacaoTituloSemPnd;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


/**
 * Classe Edita relacionaTituloXsolicitação
 * 
 * @author lucas.lima
 * 
 */
public class RelacionaTituloXSolicitacaoController extends ActionController  {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2369797345521041836L;


	private static final String COMPRAS_PESQUISA_TITULOS = "compras-pesquisaGeralTitulos";
	
		
	private static final String COMPRAS_EDITAR_SOLICITACAO_COMPRAS = "compras-solicitacaoCompraCRUD";
	
	private static final String COMPRAS_EDITAR_SOLICITACAO_SERVICO = "compras-solicitacaoServicoCRUD";
	
	
	private String tituloVisualizar = "Visualizar Título Sem Licitação";
	
	private String tituloEditar = "Alterar Título Sem Licitação";
	
	private DominioSituacaoTituloSemPnd situacaoTitulo;
	
	@EJB
	protected IComprasFacade comprasFacade;

		
	private List<SolicitacaoTituloVO> listaTitulosSolicitacoes;
	
	private SolicitacaoTituloVO linhaSelecionada;
	
	private ConsultaGeralTituloVO itemSelecionado;
	
	@Inject
	private PesquisaGeralTitulosController pesquisaGeralTitulosController;
	

	private boolean modoEdicao;
	private boolean pm01;
	private boolean pm02;
	
	private Short parametroFcp;
	
	 private boolean pesquisar=true;
	 
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void inicio(){
		if(pesquisar){
			pesquisar();
			carregarTipoTitulo();
			pesquisar=false;
		}
	}	
			
	public void excluirTitulo(ConsultaGeralTituloVO item){
		comprasFacade.excluirTitulosTelaConsultaGeral(item);
		apresentarMsgNegocio(Severity.INFO, "MESSAGEM_TITULO_ALTERADO_SUCESSO");
		pesquisaGeralTitulosController.pesquisar();
	}
	
	public boolean validarDataVencimento(){
		if(itemSelecionado.getDataVencimento() != null && !itemSelecionado.getDataVencimento().before(new Date())){
			return true;
		}
		apresentarMsgNegocio(Severity.ERROR, "MESSAGEM_DATA_INFERIOR_DATA_ATUAL");
		return false;
	}
	
	public void pesquisar(){
					listaTitulosSolicitacoes = comprasFacade.obterTitulosSolicitacaoCompraServico(itemSelecionado.getTtlSeq());
	}
	
	public boolean habilitaVisulizar(ConsultaGeralTituloVO item){
		if(item.getSituacaoTitulo().equals(DominioSituacaoTitulo.PG)||!pm02){
			return true;
		}
		return false;
	}
	
	public boolean habilitaEditarExcluir(ConsultaGeralTituloVO item){
		if(item.getSituacaoTitulo() == null || !item.getSituacaoTitulo().equals(DominioSituacaoTitulo.PG)||pm02){
			return true;
		}
		return false;
	}
	
	public static String getCpfCnpjFormatado(ScoFornecedor item) {
		if (item.getCpf() == null) {
			if (item.getCgc() == null) {
				return StringUtils.EMPTY;
			}
			return CoreUtil.formatarCNPJ(item.getCgc());
		}
		return CoreUtil.formataCPF(item.getCpf());
	}
	
	public static String obterCpfCnpjFormatado(ConsultaGeralTituloVO item) {
		String prefixo = "CNPJ/CPF: ";
		if (item.getFrnCpf() == null) {
			if (item.getFrnCnpj()  == null) {
				return prefixo+StringUtils.EMPTY;
			}
			return prefixo + CoreUtil.formatarCNPJ(item.getFrnCnpj());
		}
		return prefixo+CoreUtil.formataCPF(item.getFrnCpf());
	}
	
	public String redirecionaSolicitacaoServico(){
	
		return  COMPRAS_EDITAR_SOLICITACAO_SERVICO;
		
	}
	
	public String redirecionaSolicitacaoCompras(){
		return COMPRAS_EDITAR_SOLICITACAO_COMPRAS;
		
	}
	
	
	public String obterStringTruncada(String str, Integer tamanhoMaximo) {
		if (str.length() > tamanhoMaximo) {
			str = str.substring(0, tamanhoMaximo) + "...";
		}
		return str;
	}
	
	public String obterDocFormatado(ScoFornecedor fornecedor) {
		return this.comprasFacade.obterCnpjCpfFornecedorFormatado(fornecedor).concat(" - ").concat(fornecedor.getRazaoSocial());
	}
	
	public String obterServMaterial(ScoMaterial material, ScoServico servico, boolean truncado) {
		String retorno = "";
		if (material != null) {
			retorno = material.getCodigoENome();
		} else if (servico != null) {
			retorno = servico.getCodigoENome();
		}
		if (truncado && retorno.length() > 30) {
			retorno = retorno.substring(0, 30) + "...";
		}
		return retorno;
	}
	
	public String redirecionarPesquisaGeral(){
		pesquisaGeralTitulosController.pesquisar();
		pesquisar=true;
		return COMPRAS_PESQUISA_TITULOS;
	}
	
	public String alterarSolicitacoesTitulo(){
		if(validarDataVencimento() && verificaValoresGrid()){
			setarValorTipoTitulo();
			try {
				comprasFacade.alterarSolicitacoes(listaTitulosSolicitacoes, itemSelecionado);
				apresentarMsgNegocio(Severity.INFO, "MESSAGEM_TITULO_ALTERADO_SUCESSO");
				pesquisaGeralTitulosController.pesquisar();
				pesquisar=true;
				return COMPRAS_PESQUISA_TITULOS;
			} 
			catch (EJBException e) {
				Throwable causa = e.getCause();
				while(causa != null && causa.getCause()!=null){
					if(causa.getCause().getMessage().contains("20000: FCP-00043")){
						apresentarMsgNegocio(Severity.ERROR, "MESSAGEM_PAGAMENTO_NAO_REGISTRATO");
					}
					causa = causa.getCause();
				}
			}
			catch (ApplicationBusinessException e) {
				apresentarMsgNegocio(Severity.ERROR, "Ocorreu um erro na aplicação");
			}
		}
		return null;
	}
	
	private boolean verificaValoresGrid(){
		for (SolicitacaoTituloVO titulosSolicitacoes : listaTitulosSolicitacoes) {
			if(titulosSolicitacoes.getValorTitulSolicitacao()==null||
					titulosSolicitacoes.getValorTitulSolicitacao()<Double.valueOf("0.01")){
				apresentarMsgNegocio(Severity.ERROR, "MESSAGEM_INFORMAR_VALOR_SUPERIOR_ZERO");
				return false;
			}
		}
		return true;
	}
	
	private void setarValorTipoTitulo(){
		if(situacaoTitulo.equals(DominioSituacaoTituloSemPnd.APG)){
			itemSelecionado.setSituacaoTitulo(DominioSituacaoTitulo.APG);
		}
		else if(situacaoTitulo.equals(DominioSituacaoTituloSemPnd.BLQ)){
			itemSelecionado.setSituacaoTitulo(DominioSituacaoTitulo.BLQ);
		}
		else if(situacaoTitulo.equals(DominioSituacaoTituloSemPnd.PG)){
			itemSelecionado.setSituacaoTitulo(DominioSituacaoTitulo.PG);
		}
	}
	
	private void carregarTipoTitulo(){
		if(itemSelecionado.getSituacaoTitulo().equals(DominioSituacaoTitulo.APG)){
			setSituacaoTitulo(DominioSituacaoTituloSemPnd.APG);
		}
		else if(itemSelecionado.getSituacaoTitulo().equals(DominioSituacaoTitulo.BLQ)){
			setSituacaoTitulo(DominioSituacaoTituloSemPnd.BLQ);
		}
		else if(itemSelecionado.getSituacaoTitulo().equals(DominioSituacaoTitulo.PG)){
			setSituacaoTitulo(DominioSituacaoTituloSemPnd.PG);
		}
	}
 
	public Boolean validarPoll() {
		return true;
	}

	public Short getParametroFcp() {
		return parametroFcp;
	}

	public void setParametroFcp(Short parametroFcp) {
		this.parametroFcp = parametroFcp;
	}

	public boolean isPm01() {
		return pm01;
	}

	public void setPm01(boolean pm01) {
		this.pm01 = pm01;
	}

	public boolean isPm02() {
		return pm02;
	}

	public void setPm02(boolean pm02) {
		this.pm02 = pm02;
	}

	public ConsultaGeralTituloVO getItemSelecionado() {
		return itemSelecionado;
	}

	public void setItemSelecionado(ConsultaGeralTituloVO itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}

	public List<SolicitacaoTituloVO> getListaTitulosSolicitacoes() {
		return listaTitulosSolicitacoes;
	}

	public void setListaTitulosSolicitacoes(
			List<SolicitacaoTituloVO> listaTitulosSolicitacoes) {
		this.listaTitulosSolicitacoes = listaTitulosSolicitacoes;
	}

	public boolean isModoEdicao() {
		return modoEdicao;
	}

	public void setModoEdicao(boolean modoEdicao) {
		this.modoEdicao = modoEdicao;
	}
	
	public String getTituloEditar() {
		return tituloEditar;
	}

	public void setTituloEditar(String tituloEditar) {
		this.tituloEditar = tituloEditar;
	}

	public String getTituloVisualizar() {
		return tituloVisualizar;
	}

	public void setTituloVisualizar(String tituloVisualizar) {
		this.tituloVisualizar = tituloVisualizar;
	}

	public PesquisaGeralTitulosController getPesquisaGeralTitulosController() {
		return pesquisaGeralTitulosController;
	}

	public void setPesquisaGeralTitulosController(
			PesquisaGeralTitulosController pesquisaGeralTitulosController) {
		this.pesquisaGeralTitulosController = pesquisaGeralTitulosController;
	}

	public DominioSituacaoTituloSemPnd getSituacaoTitulo() {
		return situacaoTitulo;
	}

	public void setSituacaoTitulo(DominioSituacaoTituloSemPnd situacaoTitulo) {
		this.situacaoTitulo = situacaoTitulo;
	}

	public SolicitacaoTituloVO getLinhaSelecionada() {
		return linhaSelecionada;
	}

	public void setLinhaSelecionada(SolicitacaoTituloVO linhaSelecionada) {
		this.linhaSelecionada = linhaSelecionada;
	}

	public boolean isPesquisar() {
		return pesquisar;
	}

	public void setPesquisar(boolean pesquisar) {
		this.pesquisar = pesquisar;
	}
	
	
}