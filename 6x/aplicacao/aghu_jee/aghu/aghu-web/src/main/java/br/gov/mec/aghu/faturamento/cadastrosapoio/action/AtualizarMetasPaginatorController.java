package br.gov.mec.aghu.faturamento.cadastrosapoio.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioTipoPlano;
import br.gov.mec.aghu.faturamento.cadastrosapoio.business.IFaturamentoApoioFacade;
import br.gov.mec.aghu.model.FatCaractFinanciamento;
import br.gov.mec.aghu.model.FatFormaOrganizacao;
import br.gov.mec.aghu.model.FatGrupo;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatMeta;
import br.gov.mec.aghu.model.FatSubGrupo;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

public class AtualizarMetasPaginatorController extends ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6759076204310814090L;

	private final String PAGE_MANTER_METAS = "manterMetas";
	
	@Inject @Paginator
	private DynamicDataModel<FatMeta> dataModel;
	
	@Inject
	private ManterMetasController manterMetasController;
	
	@EJB
	private IFaturamentoApoioFacade faturamentoApoioFacade;

	private List<FatMeta> listMetas = new ArrayList<FatMeta>();
	private FatMeta meta;
	private FatGrupo grupo;
	private FatSubGrupo subGrupo;
	private FatFormaOrganizacao formaOrganizacao;
	private FatCaractFinanciamento financiamento;
	private FatItensProcedHospitalar procedimento;
	//private boolean exibirNovo;
	private Long quantidade;
	private BigDecimal valor;
	private boolean refazerPesquisa = false;
	private Integer seq;
	private Boolean internacao;
	private Boolean ambulatorio;
	private DominioTipoPlano indicativo;

	@PostConstruct
	public void init() {
		begin(conversation, true);
	}
	
	@Override
	public Long recuperarCount() {
		return this.faturamentoApoioFacade.pesquisarFatMetaCount(getGrupo(),
				getSubGrupo(), getFormaOrganizacao(), getFinanciamento(),
				getProcedimento(), getInternacao(), getAmbulatorio());
	}

	@Override
	public List<FatMeta> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {

		this.listMetas = this.faturamentoApoioFacade.pesquisarFatMeta(
				firstResult, maxResult, orderProperty, asc, getGrupo(),
				getSubGrupo(), getFormaOrganizacao(), getFinanciamento(),
				getProcedimento(), getInternacao(), getAmbulatorio());
		return listMetas;
	}
	
	//#######################################################################################################
	/**
	 * Método iniciar
	 */
	public void iniciar() {
		if(this.isRefazerPesquisa()){
			this.pesquisar();
			this.setRefazerPesquisa(false);
		}
	}

	/**
	 * Método pesquisar
	 */
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}

	/**
	 * Método para limpar os campos e a lista da tela de pesquisa
	 */
	public void limpar() {		
		// limpa as variáveis de filtro
		this.setMeta(null);
		this.setGrupo(null);
		this.setSubGrupo(null);
		this.setFormaOrganizacao(null);
		this.setFinanciamento(null);
		this.setProcedimento(null);
		this.setQuantidade(null);
		this.setValor(null);
		this.setAmbulatorio(false);
		this.setInternacao(false);
		this.setIndicativo(null);
		this.dataModel.limparPesquisa();
	}
	
	/**
	 * edita um registro FatMeta.
	 * 
	 * @return
	 */
	public String editarMeta() {
		this.setSeq(this.meta.getSeq());
		manterMetasController.iniciar(this.meta);
		return PAGE_MANTER_METAS;
	}
	
	/**
	 * inclui um novo registro FatMeta.
	 * 
	 * @return
	 */
	public String incluirMeta() {
		this.setSeq(null);
		this.manterMetasController.iniciar(null);
		return PAGE_MANTER_METAS;
	}

	//#######################################################################################################
	
	public void limparDadosGrupo() {
		this.setGrupo(null);
		this.setSubGrupo(null);
		this.setFormaOrganizacao(null);
	}

	public void limparDadosSubGrupo() {
		this.setSubGrupo(null);
		this.setFormaOrganizacao(null);
	}
	
	/**
	 * Preenchimento da suggestion "Grupos".
	 * 
	 * @param pesquisa
	 *            Código ou Descrição do grupo.
	 * @return Lista de grupos.
	 */
	public List<FatGrupo> listarGrupos(String pesquisa) {

		List<FatGrupo> listaGrupos = faturamentoApoioFacade
				.listarGruposAtivosPorCodigoOuDescricao(pesquisa);

		return listaGrupos;
	}

	/**
	 * Preenchimento da suggestion "SubGrupo"
	 * 
	 * @param pesquisa
	 *            Código ou Descrição do subGrupo.
	 * @return Lista de SubGrupos.
	 */
	public List<FatSubGrupo> listarSubGrupos(String pesquisa) {

		List<FatSubGrupo> listaSubGrupos = faturamentoApoioFacade
				.listarSubGruposAtivosPorCodigoOuDescricao(pesquisa, getGrupo()
						.getSeq());

		return listaSubGrupos;
	}

	/**
	 * Preenchimento da suggestion "Forma de Organização".
	 * 
	 * @param pesquisa
	 *            Código ou Descrição da forma de organização.
	 * @return Lista de formas de organização.
	 */
	public List<FatFormaOrganizacao> listarFormasOrganizacao(String pesquisa) {

		List<FatFormaOrganizacao> listaSubGrupos = faturamentoApoioFacade
				.listarFormasOrganizacaoAtivosPorCodigoOuDescricao(pesquisa,
						getGrupo().getCodigo(), getSubGrupo().getId()
								.getSubGrupo());

		return listaSubGrupos;
	}

	/**
	 * Preenchimento da suggestion "Financiamento".
	 * 
	 * @param pesquisa
	 *            Código ou Descrição do Financiamento.
	 * @return Lista de Financiamentos (FatCaractFinanciamento).
	 */
	public List<FatCaractFinanciamento> listarFinanciamentos(String pesquisa) {

		List<FatCaractFinanciamento> listaFinanciamentos = faturamentoApoioFacade
				.listarFinanciamentosAtivosPorCodigoOuDescricao(pesquisa);

		return listaFinanciamentos;
	}

	/**
	 * Preenchimento da suggestion "Procedimentos".
	 * 
	 * @param pesquisa
	 *            Código ou Descrição do procedimento.
	 * @return Lista de Procedimentos (FatItensProcedHospitalar).
	 */
	public List<FatItensProcedHospitalar> listarProcedimentos(String pesquisa) {
		List<FatItensProcedHospitalar> listaProcedimentos = faturamentoApoioFacade
				.listarFatItensProcedHospitalarAtivosPorCodigoOuDescricao(
						pesquisa, getFormaOrganizacao(), getGrupo(),
						getSubGrupo());

		return listaProcedimentos;
	}
	
	public void verificarIndicativo() {
		if(DominioTipoPlano.A.equals(indicativo)) {
			this.ambulatorio = Boolean.TRUE;
			this.internacao = Boolean.FALSE;
		} else {
			this.ambulatorio = Boolean.FALSE;
			this.internacao = Boolean.TRUE;
		}
	}

	// getters & setters

	public FatMeta getMeta() {
		return meta;
	}

	public void setMeta(FatMeta meta) {
		this.meta = meta;
	}

	public FatGrupo getGrupo() {
		return grupo;
	}

	public void setGrupo(FatGrupo grupo) {
		this.grupo = grupo;
	}

	public FatSubGrupo getSubGrupo() {
		return subGrupo;
	}

	public void setSubGrupo(FatSubGrupo subGrupo) {
		this.subGrupo = subGrupo;
	}

	public FatFormaOrganizacao getFormaOrganizacao() {
		return formaOrganizacao;
	}

	public void setFormaOrganizacao(FatFormaOrganizacao formaOrganizacao) {
		this.formaOrganizacao = formaOrganizacao;
	}

	public FatCaractFinanciamento getFinanciamento() {
		return financiamento;
	}

	public void setFinanciamento(FatCaractFinanciamento financiamento) {
		this.financiamento = financiamento;
	}

	public FatItensProcedHospitalar getProcedimento() {
		return procedimento;
	}

	public void setProcedimento(FatItensProcedHospitalar procedimento) {
		this.procedimento = procedimento;
	}

	public List<FatMeta> getListMetas() {
		return listMetas;
	}

	public void setListMetas(List<FatMeta> listMetas) {
		this.listMetas = listMetas;
	}

	public Long getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Long quantidade) {
		this.quantidade = quantidade;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public boolean isRefazerPesquisa() {
		return refazerPesquisa;
	}

	public void setRefazerPesquisa(boolean refazerPesquisa) {
		this.refazerPesquisa = refazerPesquisa;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Boolean getInternacao() {
		return internacao;
	}

	public void setInternacao(Boolean internacao) {
		this.internacao = internacao;
	}

	public Boolean getAmbulatorio() {
		return ambulatorio;
	}

	public void setAmbulatorio(Boolean ambulatorio) {
		this.ambulatorio = ambulatorio;
	}

	public DominioTipoPlano getIndicativo() {
		return indicativo;
	}

	public void setIndicativo(DominioTipoPlano indicativo) {
		this.indicativo = indicativo;
	}

	public DynamicDataModel<FatMeta> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<FatMeta> dataModel) {
		this.dataModel = dataModel;
	}

}