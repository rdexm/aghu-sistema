package br.gov.mec.aghu.compras.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.contaspagar.vo.ConsultaGeralTituloVO;
import br.gov.mec.aghu.compras.contaspagar.vo.FiltroConsultaGeralTituloVO;
import br.gov.mec.aghu.dominio.DominioSituacaoTitulo;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacaoTitulo;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.FcpClassificacaoTitulo;
import br.gov.mec.aghu.model.FcpTipoTitulo;
import br.gov.mec.aghu.model.FsoGrupoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoVerbaGestao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoGrupoServico;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.business.ICadastrosBasicosOrcamentoFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;


/**
 * Classe responsável por realizar a Pesquisa Geral de Titulos
 * 
 * @author lucas.lima
 * 
 */
public class PesquisaGeralTitulosController extends ActionController  {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2369797345521041836L;


	private static final String COMPRAS_RELACIONA_TITULOS = "compras-relacionaTituloSolicitacao";
	
	private static final String TRACO_ESPACO = " - ";

	
	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	protected IComprasFacade comprasFacade;

	@EJB
	protected ICentroCustoFacade centroCustoFacade;
	
	
	@EJB
	private ICadastrosBasicosOrcamentoFacade cadastrosBasicosOrcamentoFacade;
	
	
	@EJB
	private IPermissionService permissionService;
	
//	@Inject
//	private RelacionaTituloXSolicitacaoController relacionaTituloXSolicitacaoController;
	
	public FiltroConsultaGeralTituloVO getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroConsultaGeralTituloVO filtro) {
		this.filtro = filtro;
	}

	private FiltroConsultaGeralTituloVO filtro= new FiltroConsultaGeralTituloVO();

	private List<ConsultaGeralTituloVO> listaTitulos;
	
	private boolean exibeServico=true;
	private boolean exibeMaterial;
	private boolean exibeGrid;
	private boolean pm01;
	private boolean pm02;
	
	
	@PostConstruct
	protected void inicializar(){
		filtro.setTipoSolicitacao(DominioTipoSolicitacaoTitulo.SS);
		this.begin(conversation);
	}
	
	
	private void setarPamametros(){
			setPm01(permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "consultarTituloSemLicitacao", "executar"));
			setPm02(permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "gerarTituloSemLicitacao", "executar"));
	}
	
	private boolean validarDatas(){
		if(filtro.getDataVencimentoInicial() !=null && filtro.getDataVencimentoFinal() != null){
			if(filtro.getDataVencimentoInicial().before(filtro.getDataVencimentoFinal())){
				return true;
			}
			else{
				apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_DATA_INICIAL_MAIOR_FINAL");
				return false;
			}
		}
		return true;
	}
	
	private void setarDataInicioDiaFimDia(){
		if(filtro.getDataVencimentoInicial() != null){
			filtro.setDataVencimentoInicial(DateUtil.obterDataComHoraInical(filtro.getDataVencimentoInicial()));
		}
		if(filtro.getDataVencimentoFinal() !=null ){
			filtro.setDataVencimentoFinal((DateUtil.obterDataComHoraFinal(filtro.getDataVencimentoFinal())));
		}
	}
	
	public void excluirTitulo(ConsultaGeralTituloVO item){
		comprasFacade.excluirTitulosTelaConsultaGeral(item);
		apresentarMsgNegocio(Severity.INFO, "MENSAGEM_EXCLUSAO_TITULO");
		pesquisar();
	}
	
	public void inicio() {
		setarPamametros();
	}
	
	public void pesquisar(){
		
		setarDataInicioDiaFimDia();
		if(validarDatas()){
			listaTitulos = comprasFacade.consultaGeralTitulos(filtro);
		}
		exibeGrid = true;
	}
	
	public void limparPesquisa(){
		listaTitulos = null;
		filtro = new FiltroConsultaGeralTituloVO();
		filtro.setTipoSolicitacao(DominioTipoSolicitacaoTitulo.SS);
		exibeServico = true;
		exibeMaterial = false;
		exibeGrid = false;
	}
	
	public void limparNaturezaDespesa(){
		filtro.setNaturezaDespesa(null);
		
	}
	
	public void mudarTipoSolicitacao(DominioTipoSolicitacaoTitulo solicitacao){
		if(solicitacao==null){
			exibeServico = false;
			exibeMaterial = false;
			filtro.setGrupoMaterial(null);
			filtro.setGrupoServico(null);
			filtro.setServico(null);
			filtro.setMaterial(null);
		}
		else if(solicitacao.equals(DominioTipoSolicitacaoTitulo.SS)){
			exibeServico = true;
			exibeMaterial = false;
			filtro.setGrupoMaterial(null);
			filtro.setMaterial(null);
		}
		else if(solicitacao.equals(DominioTipoSolicitacaoTitulo.SC)){
			exibeMaterial = true;
			exibeServico = false;
			filtro.setServico(null);
			filtro.setGrupoServico(null);
		}
		filtro.setNumeroSolicitacao(null);
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
	
	public List<ScoModalidadeLicitacao> getPesquisarModalidades(String filter) {
		return this.comprasCadastrosBasicosFacade.listarModalidadeLicitacao(filter);
	}
	
	public List<RapServidores> getListarServidores(String objPesquisa) {
		return this.returnSGWithCount(this.registroColaboradorFacade.pesquisarServidorPorMatriculaNome(objPesquisa),getListarServidoresCount(objPesquisa));
	}

	public Long getListarServidoresCount(String objPesquisa) {
		return this.registroColaboradorFacade.pesquisarServidorPorMatriculaNomeCount(objPesquisa);
	}
	
	public List<ScoFornecedor> pesquisarFornecedores(final String pesquisa) {
		return this.returnSGWithCount(comprasFacade.listarFornecedoresAtivos(pesquisa, 0, 100, null, true),pesquisarFornecedoresCount(pesquisa));
	}
	
	
	public List<FcpClassificacaoTitulo> pesquisarClassificacaoTitulo(final String pesquisa) {
		return this.returnSGWithCount(comprasFacade.obterListaClassificacaoTituloAtivosPorCodigoOuDescricao(pesquisa),comprasFacade.obterCountClassificacaoTituloAtivosPorCodigoOuDescricao((pesquisa)));
	}
	
	public List<FcpTipoTitulo> pesquisarTipoTitulo(final String pesquisa) {
		return this.returnSGWithCount(comprasFacade.obterListaTipoTitulo(pesquisa),comprasFacade.obterCountTipoTitulo((pesquisa)));
	}

	public Long pesquisarFornecedoresCount(final String strPesquisa) {
		return comprasFacade.listarFornecedoresAtivosCount(strPesquisa);
	}
	
	public List<ScoGrupoMaterial> listarGrupoMateriais(String filter){
		return this.returnSGWithCount(this.comprasFacade.pesquisarGrupoMaterialPorFiltro(filter),getListarGrupoMateriaisCount(filter));
	}
	
	public Long getListarGrupoMateriaisCount(String filter){
		return this.comprasFacade.pesquisarGrupoMaterialPorFiltroCount(filter);
	}
	
	public List<ScoMaterial> listarMateriais(String filter){
		return this.returnSGWithCount(this.comprasFacade.listarScoMateriais(filter, null, true),getListarMateriaisCount(filter));
	}

	public Long getListarMateriaisCount(String filter){
		return this.comprasFacade.listarScoMateriaisCount(filter, null, true);
	}
	
	public List<ScoGrupoServico> listarGrupoServico(String filter){
		return this.returnSGWithCount(comprasFacade.pesquisarGrupoServico(filter),comprasFacade.pesquisarGrupoServicoCount(filter));
	}
	
	public List<ScoServico> listarServicos(String filter){
		return this.returnSGWithCount(this.comprasFacade.listarServicos(filter),listarServicosCount(filter));
	}
	public Long listarServicosCount(String filter){
		return this.comprasFacade.listarServicosCount(filter);
	}
	
	public List<FccCentroCustos> listarCentroCustos(String filter) {
		String srtPesquisa = (String) filter;
		return this.returnSGWithCount(centroCustoFacade.pesquisarCentroCustosPorCodigoDescricao(srtPesquisa),listarCentroCustosCount(filter));
	}
	
	public Long listarCentroCustosCount(String filter) {
		String srtPesquisa = (String) filter;
		return centroCustoFacade.pesquisarCentroCustosPorCodigoDescricaoCount(srtPesquisa);
	}
	
	public List<FsoVerbaGestao> pesquisarVerbaGestaoPorSeqOuDescricao(String objParam) throws ApplicationBusinessException {
		return this.cadastrosBasicosOrcamentoFacade.pesquisarVerbaGestaoPorSeqOuDescricao(objParam);
	}
	
	public List<FsoGrupoNaturezaDespesa> pesquisarGrupoNaturezaDespesaPorCodigoEDescricao(String filter) {
		return cadastrosBasicosOrcamentoFacade
				.pesquisarGrupoNaturezaDespesaPorCodigoEDescricao(filter);
	}
	
	public List<FsoNaturezaDespesa> pesquisarNaturezaDespesaPorGrupo(String objParam) throws ApplicationBusinessException {
		return this.cadastrosBasicosOrcamentoFacade.pesquisarNaturezaDespesaPorGrupo(this.filtro.getGrupoNaturezaDespesa(), objParam);
	}

	public Long pesquisarNaturezaDespesaPorGrupoCount(Object objParam) throws ApplicationBusinessException {
		return this.cadastrosBasicosOrcamentoFacade.pesquisarNaturezaDespesaPorGrupoCount(this.filtro.getGrupoNaturezaDespesa(), objParam);
	}
	
	public void limparSuggestionNatrezaDespesa(){
		filtro.setNaturezaDespesa(null);
	}
	
	public String concatenaCodigoDescricaoItem(ConsultaGeralTituloVO item){
		
		if(item.getNtdCodigo() !=null && item.getNtdDescricao() != null){
			return item.getNtdCodigo().toString()+TRACO_ESPACO +item.getNtdDescricao(); 
		}else{
			if(item.getNtdCodigo()!=null){
				return item.getNtdCodigo().toString()+TRACO_ESPACO; 
			}
			else{
				return TRACO_ESPACO+item.getNtdDescricao();
			}
		}
		
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
	
	public String redirecionarRelacionaTituloXSolicitacaoEdicao(ConsultaGeralTituloVO itemSelecionado){
//		relacionaTituloXSolicitacaoController.setItemSelecionado(itemSelecionado);
//		relacionaTituloXSolicitacaoController.setModoEdicao(true);
		return COMPRAS_RELACIONA_TITULOS;
	}
	
	
	public String redirecionarRelacionaTituloXSolicitacaoVisualizar(ConsultaGeralTituloVO itemSelecionado){
//		relacionaTituloXSolicitacaoController.setItemSelecionado(itemSelecionado);
//		relacionaTituloXSolicitacaoController.setModoEdicao(false);
		return COMPRAS_RELACIONA_TITULOS;
	}
	

	public Boolean validarPoll() {
		return true;
	}

	public List<ConsultaGeralTituloVO> getListaTitulos() {
		return listaTitulos;
	}

	public void setListaTitulos(List<ConsultaGeralTituloVO> listaTitulos) {
		this.listaTitulos = listaTitulos;
	}

	public boolean isExibeServico() {
		return exibeServico;
	}

	public void setExibeServico(boolean exibeServico) {
		this.exibeServico = exibeServico;
	}

	public boolean isExibeMaterial() {
		return exibeMaterial;
	}

	public void setExibeMaterial(boolean exibeMaterial) {
		this.exibeMaterial = exibeMaterial;
	}

	public boolean isExibeGrid() {
		return exibeGrid;
	}

	public void setExibeGrid(boolean exibeGrid) {
		this.exibeGrid = exibeGrid;
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
//
//	public RelacionaTituloXSolicitacaoController getRelacionaTituloXSolicitacaoController() {
//		return relacionaTituloXSolicitacaoController;
//	}
//
//	public void setRelacionaTituloXSolicitacaoController(
//			RelacionaTituloXSolicitacaoController relacionaTituloXSolicitacaoController) {
//		this.relacionaTituloXSolicitacaoController = relacionaTituloXSolicitacaoController;
//	}
}