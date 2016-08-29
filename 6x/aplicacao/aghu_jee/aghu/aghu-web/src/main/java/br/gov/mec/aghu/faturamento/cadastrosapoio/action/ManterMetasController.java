package br.gov.mec.aghu.faturamento.cadastrosapoio.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.inject.Named;

import br.gov.mec.aghu.dominio.DominioTipoPlano;
import br.gov.mec.aghu.faturamento.cadastrosapoio.business.IFaturamentoApoioFacade;
import br.gov.mec.aghu.model.FatCaractFinanciamento;
import br.gov.mec.aghu.model.FatFormaOrganizacao;
import br.gov.mec.aghu.model.FatGrupo;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatMeta;
import br.gov.mec.aghu.model.FatSubGrupo;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;

@Named("manterMetasController")
public class ManterMetasController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -971876017208103209L;

	private final String PAGE_MANTER_METAS = "manterMetas";
	private final String PAGE_PESQUISAR_METAS = "atualizarMetas";	
	
	@EJB
	private IFaturamentoApoioFacade faturamentoApoioFacade;

	private List<FatMeta> listMetas = new ArrayList<FatMeta>();
	private FatMeta meta;
	private FatFormaOrganizacao formaOrganizacao;
	private FatCaractFinanciamento financiamento;
	private FatItensProcedHospitalar procedimento;
	private Long quantidade;
	private BigDecimal valor;
	private boolean valorAlterado;
	private boolean confirmarVolta;
	private boolean refazerPesquisa = false;
	private Integer seq;
	private boolean exibirNovo;
	private Date dthrInicioVig;
	private Date dthrFimVig;
	private FatGrupo grupo;
	private FatSubGrupo subGrupo;
	private Boolean internacao;
	private Boolean ambulatorio;
	private DominioTipoPlano indicativo;
	
	@Inject
	private AtualizarMetasPaginatorController atualizarMetasPaginatorController;

	protected boolean result() {
		return this.faturamentoApoioFacade.pesquisarFatMetaComDthrFimVig(getGrupo(),
				getSubGrupo(), getFormaOrganizacao(), getFinanciamento(),
				getProcedimento(), getAmbulatorio(), getInternacao());
	}

	/**
	 * Método para inicialização da tela.
	 */	
	public void iniciar(FatMeta meta) {
		
		// Se Edição
		if (meta != null) {
			//this.setSeq(this.atualizarMetasPaginatorController.getSeq());
			this.meta = meta;
			this.seq = meta.getSeq();
			this.quantidade = this.meta.getQuantidade();
			this.valor = this.meta.getValor();
			this.grupo =  this.meta.getFatGrupo();
			this.subGrupo = this.meta.getFatSubGrupo();
			this.formaOrganizacao = this.meta.getFatFormasOrganizacao();
			this.financiamento = this.meta.getFatCaractFinanciamento();
			this.procedimento = this.meta.getFatItensProcedHospitalar();
			this.dthrInicioVig = this.meta.getDthrInicioVig();
			this.dthrFimVig = this.meta.getDthrFimVig();
			this.setExibirNovo(false);
			this.internacao = this.meta.getIndInternacao();
			this.ambulatorio = this.meta.getIndAmbulatorio();
			
			if(this.internacao != null) {
				this.indicativo = this.internacao.booleanValue() ? DominioTipoPlano.I : DominioTipoPlano.A;
			}
		} else {// Se novo
			this.limpar();
			this.setExibirNovo(true);
		}
		this.setValorAlterado(false);
		this.setConfirmarVolta(false);
	}

	/**
	 * Método executado ao clicar no botão limpar
	 */
	public void limpar() {
		this.setMeta(null);
		this.setGrupo(null);
		this.setSubGrupo(null);
		this.setFormaOrganizacao(null);
		this.setFinanciamento(null);
		this.setProcedimento(null);
		this.setQuantidade(null);
		this.setValor(null);
		this.setDthrFimVig(null);
		this.setDthrInicioVig(null);
		this.setSeq(null);
		this.setValorAlterado(false);
		this.setConfirmarVolta(false);
		this.setAmbulatorio(null);
		this.setInternacao(null);
		this.setIndicativo(null);
	}

	public void marcarAlteracao() {
		this.setValorAlterado(true);
	}

	private void analisarValores() {
		if (!valorAlterado) {
			if (meta != null) {// edicao
				this.analisaValoresUpdate();
			} else {// novo
				this.analisaValoresInsert();
			}
		}
	}
	
	private void analisaValoresInsert() {
		if (this.getGrupo() != null) {
			this.setValorAlterado(true);
		}
		if (this.getSubGrupo() != null) {
			this.setValorAlterado(true);
		}
		if (this.getFormaOrganizacao() != null) {
			this.setValorAlterado(true);
		}
		if (this.getFinanciamento() != null) {
			this.setValorAlterado(true);
		}
		if (this.getProcedimento() != null) {
			this.setValorAlterado(true);
		}
		if (this.getDthrInicioVig() != null) {
			this.setValorAlterado(true);
		}
		if (this.getQuantidade() != null) {
			this.setValorAlterado(true);
		}
		if (this.getValor() != null) {
			this.setValorAlterado(true);
		}
		if (this.getInternacao() != null || this.getAmbulatorio() != null) {
			this.setValorAlterado(true);
		}
	}
	
	private void analisaValoresUpdate() {
		if (meta.getDthrInicioVig() != null
				&& !DateUtil.isDatasIguais(getDthrInicioVig(),
						meta.getDthrInicioVig())) {
			this.setValorAlterado(true);
		}
		if (meta.getDthrFimVig() != null
				&& !DateUtil.isDatasIguais(getDthrFimVig(),
						meta.getDthrFimVig())) {
			this.setValorAlterado(true);
		}
		if (meta.getQuantidade() != null
				&& !this.meta.getQuantidade().equals(
						this.getQuantidade())) {
			this.setValorAlterado(true);
		}
		if (meta.getValor() != null) {
			if (Double.compare(this.meta.getValor().doubleValue(), this
					.getValor().doubleValue()) != 0) {
				this.setValorAlterado(true);
			}
		}
		if(meta.getIndAmbulatorio() != null 
				&& !meta.getIndAmbulatorio().equals(this.getAmbulatorio())) {
			this.setValorAlterado(true);
		}
	}

	public String verificaAlteracoes() {
		this.analisarValores();
		setConfirmarVolta(valorAlterado);
		if (this.isConfirmarVolta()) {
			//this.setRefazerPesquisa(true);
			this.atualizarMetasPaginatorController.getDataModel().reiniciarPaginator();
			return PAGE_MANTER_METAS;
		} else {
			return voltar();
		}
	}

	public String voltar() {
		this.limpar();
		//this.setRefazerPesquisa(true);
		this.atualizarMetasPaginatorController.getDataModel().reiniciarPaginator();
		return PAGE_PESQUISAR_METAS;
	}

	public void limparDadosGrupo() {
		this.setSubGrupo(null);
		this.setFormaOrganizacao(null);
	}

	public void limparDadosSubGrupo() {
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

	private void carregarDados() {
		if (this.getMeta() == null) {
			this.meta = new FatMeta();
		}
		this.meta.setFatGrupo(getGrupo());
		this.meta.setFatSubGrupo(getSubGrupo());
		this.meta.setFatFormasOrganizacao(getFormaOrganizacao());
		this.meta.setFatCaractFinanciamento(getFinanciamento());
		this.meta.setFatItensProcedHospitalar(getProcedimento());
		this.meta.setQuantidade(getQuantidade());
		this.meta.setValor(getValor());
		this.meta.setDthrInicioVig(getDthrInicioVig());
		this.meta.setDthrFimVig(getDthrFimVig());
		this.meta.setIndAmbulatorio(getAmbulatorio());
		this.meta.setIndInternacao(getInternacao());
	}

	private boolean validarDadosOk() {
		Date dataAtual = new Date();
		dataAtual = DateUtil.obterDataComHoraInical(dataAtual);

		if (this.getSeq() == null) 
		{
			if (this.grupo == null && this.subGrupo == null
					&& this.formaOrganizacao == null
					&& this.financiamento == null && this.procedimento == null) {

				this.apresentarMsgNegocio(Severity.INFO,
						"MESSAGEM_NENHUM_DADO");
				return false;
			}

			if (this.quantidade == null && this.valor == null){
				this.apresentarMsgNegocio(Severity.INFO,
						"MESSAGEM_INFORMAR_QTDADE_VALOR");
				return false;
			}
			
			if (this.dthrInicioVig == null) {
				this.apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_DATA_INICIO_VIG_OBRIG");
				return false;
			}
			
			if (Boolean.FALSE.equals(this.internacao)
					&& Boolean.FALSE.equals(this.ambulatorio)) {
				this.apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_INFORMAR_AMBULATORIO_INTERNACAO");
				return false;
			}

			if (!this.result()) {// Já existe Meta!!
				this.apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_META_JA_EXISTENTE");
				return false;
			}
		} else if (this.getSeq() != null && DateUtil.validaDataMaior(this.dthrInicioVig, this.dthrFimVig)) {
				this.apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_FIM_MENOR_DATA_INICIO");
				return false;
		}
		return true;
	}

	public String inserir() {
		String destino = "";

		if (!validarDadosOk()) {
			return PAGE_MANTER_METAS;
		}

		carregarDados();
		
		try {	
			if (this.getSeq() == null) {
				this.faturamentoApoioFacade.inserirMeta(this.getMeta(), new Date());
				this.apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_INCLUSAO_META");
				destino = PAGE_MANTER_METAS;
			} else {
				this.faturamentoApoioFacade.atualizarMeta(this.getMeta(), new Date());
				this.apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_EDICAO_META");
				//this.setRefazerPesquisa(true);
				this.atualizarMetasPaginatorController.getDataModel().reiniciarPaginator();
				destino = PAGE_PESQUISAR_METAS;
			}
		} catch (ApplicationBusinessException  e) {
			this.apresentarExcecaoNegocio(e);
			destino = PAGE_MANTER_METAS;
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
			destino = PAGE_MANTER_METAS;
		}

		this.limpar();
		return destino;
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
	
	// getters &
	// setters//////////////////////////////////////////////////////////////////////
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

	public boolean isValorAlterado() {
		return valorAlterado;
	}

	public void setValorAlterado(boolean valorAlterado) {
		this.valorAlterado = valorAlterado;
	}

	public boolean isConfirmarVolta() {
		return confirmarVolta;
	}

	public void setConfirmarVolta(boolean confirmarVolta) {
		this.confirmarVolta = confirmarVolta;
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

	public boolean isExibirNovo() {
		return exibirNovo;
	}

	public void setExibirNovo(boolean exibirNovo) {
		this.exibirNovo = exibirNovo;
	}

	public Date getDthrInicioVig() {
		return dthrInicioVig;
	}

	public void setDthrInicioVig(Date dthrInicioVig) {
		this.dthrInicioVig = dthrInicioVig;
	}

	public Date getDthrFimVig() {
		return dthrFimVig;
	}

	public void setDthrFimVig(Date dthrFimVig) {
		this.dthrFimVig = dthrFimVig;
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
}