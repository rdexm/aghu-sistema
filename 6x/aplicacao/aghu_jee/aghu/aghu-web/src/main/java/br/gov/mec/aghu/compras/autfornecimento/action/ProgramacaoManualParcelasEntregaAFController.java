package br.gov.mec.aghu.compras.autfornecimento.action;

import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.vo.*;
import br.gov.mec.aghu.dominio.DominioDiaSemanaAbreviado;
import br.gov.mec.aghu.dominio.DominioFormaProgramacao;
import br.gov.mec.aghu.dominio.DominioParcela;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import java.util.List;

public class ProgramacaoManualParcelasEntregaAFController extends ActionController {

	private static final long serialVersionUID = -3672912971943538394L;

	private static final Log LOG = LogFactory.getLog(ProgramacaoManualParcelasEntregaAFController.class);

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	@EJB
	private IComprasFacade comprasFacade;

	@EJB
	private IAutFornecimentoFacade autFornecimentoFacade;

	private ProgramacaoManualParcelasEntregaFiltroVO filtro = new ProgramacaoManualParcelasEntregaFiltroVO();
	private List<ConsultaItensAFProgramacaoManualVO> listaItensAF;
	private boolean primeiraConsulta = true;
	private Boolean indExibeFiltros;
	private Integer lctNumero;
	private Integer numeroAF;
	private Short complemento;
	private Integer numeroFornecedor;
	private Integer numeroItem;
	private String voltarParaUrl;
	private Boolean sabadoChecked = false;
	private Boolean domingoChecked = false;
	private Boolean segundaChecked = false;
	private Boolean tercaChecked = false;
	private Boolean quartaChecked = false;
	private Boolean quintaChecked = false;
	private Boolean sextaChecked = false;
	private ModalAlertaGerarVO modalAlertaGerarVO;
	private boolean exibirModalAlertaGerar;

	public void iniciar() {

		// REALIZAR PESQUISA DEFAULT
		this.filtro.setFormaProgramacao(DominioFormaProgramacao.PS);
		if (this.primeiraConsulta) {
			limparTela();
			if (this.indExibeFiltros != null && this.indExibeFiltros.equals(Boolean.TRUE)) {
				if (this.lctNumero != null) {
					this.filtro.setAutorizacaoForn(new AutorizacaoFornVO(lctNumero));
				}
				if (this.complemento != null) {
					this.filtro.setComplemento(this.complemento);
				}
				if (this.numeroFornecedor != null) {
					ScoAutorizacaoForn autorizacaoForn = new ScoAutorizacaoForn();
					autorizacaoForn.setNumero(this.numeroAF);
					this.filtro.setFornecedor(this.comprasFacade.obterFornecedorComPropostaPorAF(autorizacaoForn));
				}
			}
			// Só pesquisa se possui parâmetros.
			if (this.lctNumero != null) {
				pesquisar();
			}
			this.primeiraConsulta = false;
		}

	}


	public String redirecionarProgEntregaItensAF() {
		return "pesquisarProgEntregaItensAF";
	}

	public void pesquisar() {
		try {
			if (this.indExibeFiltros.equals(Boolean.TRUE)) {
				this.autFornecimentoFacade.validarPesquisa(this.filtro);

				Integer lctNumero = this.filtro.getAutorizacaoForn() != null ? this.filtro.getAutorizacaoForn().getLctNumero() : null;
				Integer numeroFornecedor = this.filtro.getFornecedor() != null ? this.filtro.getFornecedor().getNumero() : null;
				Integer codigoMaterial = this.filtro.getMaterial() != null ? this.filtro.getMaterial().getCodigo() : null;
				Integer codigoGrupoMaterial = this.filtro.getGrupoMaterial() != null ? this.filtro.getGrupoMaterial().getCodigo() : null;

				this.listaItensAF = this.autFornecimentoFacade.consultarItensAFProgramacaoManual(null, lctNumero,
						this.filtro.getComplemento(), numeroFornecedor, codigoMaterial, codigoGrupoMaterial,
						this.filtro.getSituacaoProgramacao());

			} else {
				this.listaItensAF = this.autFornecimentoFacade.consultarItensAFProgramacaoManual(this.numeroItem, this.lctNumero, null,
						null, null, null, null);
			}
		} catch (BaseException e) {
			LOG.error(e, e.getCause());
			apresentarExcecaoNegocio(e);
		}
	}

	public List<AutorizacaoFornVO> getPesquisarAF(String objPesquisa) {
		return this.returnSGWithCount(this.comprasFacade.listarAfComSaldoProgramar(objPesquisa),getPesquisarAFCount(objPesquisa));
	}

	public Long getPesquisarAFCount(String objPesquisa) {
		return this.comprasFacade.listarAfComSaldoProgramarCount(objPesquisa);
	}

	public List<ScoFornecedor> getPesquisarFornecedoresPorCgcCpfRazaoSocial(String parametro) {
		return this.returnSGWithCount(this.comprasFacade.listarFornecedoresAtivos(parametro, null, 100, null, true),getContarFornecedoresPorCgcCpfRazaoSocial(parametro));
	}

	public Long getContarFornecedoresPorCgcCpfRazaoSocial(String parametro) {
		return this.comprasFacade.listarFornecedoresAtivosCount(parametro);
	}

	public List<ScoMaterial> getListarMateriaisAtivos(String param) {
		return this.returnSGWithCount(this.comprasFacade.listarMateriaisAtivos(param, null),getListarMateriaisAtivosCount(param));
	}

	public Long getListarMateriaisAtivosCount(String param) {
		return this.comprasFacade.listarMateriaisAtivosCount(param, null);
	}

	public List<ScoGrupoMaterial> getObterGrupoMaterialPorSeqDescricao(String objPesquisa) {
		return this.returnSGWithCount(this.comprasFacade.obterGrupoMaterialPorSeqDescricao(objPesquisa),getObterGrupoMaterialPorSeqDescricaoCount(objPesquisa));
	}

	public Long getObterGrupoMaterialPorSeqDescricaoCount(String objPesquisa) {
		return this.comprasFacade.obterGrupoMaterialPorSeqDescricaoCount(objPesquisa);
	}

	public void pressionarBotaoSimModal() {
		this.exibirModalAlertaGerar = false;

		try {
			this.autFornecimentoFacade.gerarProgramacao(this.filtro, this.listaItensAF);

//			this.limparTela();
			this.setPrimeiraConsulta(Boolean.TRUE);
			this.iniciar();

			if (this.filtro.getUrgencia() != null) {
				this.apresentarMsgNegocio(Severity.INFO, "PARCELA_ENTREGA_GERADA_SUCESSO");
			} else {
				this.apresentarMsgNegocio(Severity.INFO, "PROGRAMACAO_REALIZADA_SUCESSO");
			}

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void gerarProgramacao() {
		this.exibirModalAlertaGerar = false;

		try {
			this.modalAlertaGerarVO = this.autFornecimentoFacade.preGerarProgramacao(this.filtro, this.listaItensAF,
					this.modalAlertaGerarVO);

//			this.exibirModalAlertaGerar = true;
//			openDialog("modalAlertaBotaoGerarWG");

			// gera a programação manual
			pressionarBotaoSimModal();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void adicionarRemoverLinha() {
		getAdicionarRemoverLinhaParte1();
		getAdicionarRemoverLinhaParte2();
	}

	private void getAdicionarRemoverLinhaParte1() {
		// Sábado
		if (this.sabadoChecked.equals(Boolean.TRUE) && !this.filtro.getListaDiasSemana().contains(DominioDiaSemanaAbreviado.SAB)) {
			this.filtro.getListaHorarioSabado().add(new HorarioSemanaVO());
			this.filtro.getListaDiasSemana().add(DominioDiaSemanaAbreviado.SAB);

		} else if (this.sabadoChecked.equals(Boolean.FALSE) && this.filtro.getListaDiasSemana().contains(DominioDiaSemanaAbreviado.SAB)) {
			this.filtro.getListaHorarioSabado().clear();
			this.filtro.getListaDiasSemana().remove(DominioDiaSemanaAbreviado.SAB);
		}
		// Domingo
		if (this.domingoChecked.equals(Boolean.TRUE) && !this.filtro.getListaDiasSemana().contains(DominioDiaSemanaAbreviado.DOM)) {
			this.filtro.getListaHorarioDomingo().add(new HorarioSemanaVO());
			this.filtro.getListaDiasSemana().add(DominioDiaSemanaAbreviado.DOM);

		} else if (this.domingoChecked.equals(Boolean.FALSE) && this.filtro.getListaDiasSemana().contains(DominioDiaSemanaAbreviado.DOM)) {
			this.filtro.getListaHorarioDomingo().clear();
			this.filtro.getListaDiasSemana().remove(DominioDiaSemanaAbreviado.DOM);
		}
		// Segunda
		if (this.segundaChecked.equals(Boolean.TRUE) && !this.filtro.getListaDiasSemana().contains(DominioDiaSemanaAbreviado.SEG)) {
			this.filtro.getListaHorarioSegunda().add(new HorarioSemanaVO());
			this.filtro.getListaDiasSemana().add(DominioDiaSemanaAbreviado.SEG);

		} else if (this.segundaChecked.equals(Boolean.FALSE) && this.filtro.getListaDiasSemana().contains(DominioDiaSemanaAbreviado.SEG)) {
			this.filtro.getListaHorarioSegunda().clear();
			this.filtro.getListaDiasSemana().remove(DominioDiaSemanaAbreviado.SEG);
		}
		// Terça
		if (this.tercaChecked.equals(Boolean.TRUE) && !this.filtro.getListaDiasSemana().contains(DominioDiaSemanaAbreviado.TER)) {
			this.filtro.getListaHorarioTerca().add(new HorarioSemanaVO());
			this.filtro.getListaDiasSemana().add(DominioDiaSemanaAbreviado.TER);

		} else if (this.tercaChecked.equals(Boolean.FALSE) && this.filtro.getListaDiasSemana().contains(DominioDiaSemanaAbreviado.TER)) {
			this.filtro.getListaHorarioTerca().clear();
			this.filtro.getListaDiasSemana().remove(DominioDiaSemanaAbreviado.TER);
		}
	}

	private void getAdicionarRemoverLinhaParte2() {
		// Quarta
		if (this.quartaChecked.equals(Boolean.TRUE) && !this.filtro.getListaDiasSemana().contains(DominioDiaSemanaAbreviado.QUA)) {
			this.filtro.getListaHorarioQuarta().add(new HorarioSemanaVO());
			this.filtro.getListaDiasSemana().add(DominioDiaSemanaAbreviado.QUA);

		} else if (this.quartaChecked.equals(Boolean.FALSE) && this.filtro.getListaDiasSemana().contains(DominioDiaSemanaAbreviado.QUA)) {
			this.filtro.getListaHorarioQuarta().clear();
			this.filtro.getListaDiasSemana().remove(DominioDiaSemanaAbreviado.QUA);
		}
		// Quinta
		if (this.quintaChecked.equals(Boolean.TRUE) && !this.filtro.getListaDiasSemana().contains(DominioDiaSemanaAbreviado.QUI)) {
			this.filtro.getListaHorarioQuinta().add(new HorarioSemanaVO());
			this.filtro.getListaDiasSemana().add(DominioDiaSemanaAbreviado.QUI);

		} else if (this.quintaChecked.equals(Boolean.FALSE) && this.filtro.getListaDiasSemana().contains(DominioDiaSemanaAbreviado.QUI)) {
			this.filtro.getListaHorarioQuinta().clear();
			this.filtro.getListaDiasSemana().remove(DominioDiaSemanaAbreviado.QUI);
		}
		// Sexta
		if (this.sextaChecked.equals(Boolean.TRUE) && !this.filtro.getListaDiasSemana().contains(DominioDiaSemanaAbreviado.SEX)) {
			this.filtro.getListaHorarioSexta().add(new HorarioSemanaVO());
			this.filtro.getListaDiasSemana().add(DominioDiaSemanaAbreviado.SEX);

		} else if (this.sextaChecked.equals(Boolean.FALSE) && this.filtro.getListaDiasSemana().contains(DominioDiaSemanaAbreviado.SEX)) {
			this.filtro.getListaHorarioSexta().clear();
			this.filtro.getListaDiasSemana().remove(DominioDiaSemanaAbreviado.SEX);
		}
	}

	public void adicionarLinhaSabado() {
		boolean deveAdicionar = true;
		for (HorarioSemanaVO item : this.filtro.getListaHorarioSabado()) {
			if (item.getHorario() == null || item.getQuantidadeItem() == null) {
				deveAdicionar = false;
				break;
			}
		}
		if (deveAdicionar) {
			this.filtro.getListaHorarioSabado().add(new HorarioSemanaVO());
		}
	}

	public void adicionarLinhaDomingo() {
		boolean deveAdicionar = true;
		for (HorarioSemanaVO item : this.filtro.getListaHorarioDomingo()) {
			if (item.getHorario() == null || item.getQuantidadeItem() == null) {
				deveAdicionar = false;
				break;
			}
		}
		if (deveAdicionar) {
			this.filtro.getListaHorarioDomingo().add(new HorarioSemanaVO());
		}
	}

	public void adicionarLinhaSegunda() {
		boolean deveAdicionar = true;
		for (HorarioSemanaVO item : this.filtro.getListaHorarioSegunda()) {
			if (item.getHorario() == null || item.getQuantidadeItem() == null) {
				deveAdicionar = false;
				break;
			}
		}
		if (deveAdicionar) {
			this.filtro.getListaHorarioSegunda().add(new HorarioSemanaVO());
		}
	}

	public void adicionarLinhaTerca() {
		boolean deveAdicionar = true;
		for (HorarioSemanaVO item : this.filtro.getListaHorarioTerca()) {
			if (item.getHorario() == null || item.getQuantidadeItem() == null) {
				deveAdicionar = false;
				break;
			}
		}
		if (deveAdicionar) {
			this.filtro.getListaHorarioTerca().add(new HorarioSemanaVO());
		}
	}

	public void adicionarLinhaQuarta() {
		boolean deveAdicionar = true;
		for (HorarioSemanaVO item : this.filtro.getListaHorarioQuarta()) {
			if (item.getHorario() == null || item.getQuantidadeItem() == null) {
				deveAdicionar = false;
				break;
			}
		}
		if (deveAdicionar) {
			this.filtro.getListaHorarioQuarta().add(new HorarioSemanaVO());
		}
	}

	public void adicionarLinhaQuinta() {
		boolean deveAdicionar = true;
		for (HorarioSemanaVO item : this.filtro.getListaHorarioQuinta()) {
			if (item.getHorario() == null || item.getQuantidadeItem() == null) {
				deveAdicionar = false;
				break;
			}
		}
		if (deveAdicionar) {
			this.filtro.getListaHorarioQuinta().add(new HorarioSemanaVO());
		}
	}

	public void adicionarLinhaSexta() {
		boolean deveAdicionar = true;
		for (HorarioSemanaVO item : this.filtro.getListaHorarioSexta()) {
			if (item.getHorario() == null || item.getQuantidadeItem() == null) {
				deveAdicionar = false;
				break;
			}
		}
		if (deveAdicionar) {
			this.filtro.getListaHorarioSexta().add(new HorarioSemanaVO());
		}
	}

	public void limparTiposParcelas() {
		this.filtro.setTipoParcela(null);
		limparHorarios();
	}

	public void limparListas() {
		if (this.filtro.getTipoParcela().equals(DominioParcela.IP)) {
			limparHorarios();
		}
	}

	public void limparHorarios() {
		this.filtro.getListaDiasSemana().clear();
		this.filtro.getListaHorarioSabado().clear();
		this.filtro.getListaHorarioDomingo().clear();
		this.filtro.getListaHorarioSegunda().clear();
		this.filtro.getListaHorarioTerca().clear();
		this.filtro.getListaHorarioQuarta().clear();
		this.filtro.getListaHorarioQuinta().clear();
		this.filtro.getListaHorarioSexta().clear();
		this.sabadoChecked = false;
		this.domingoChecked = false;
		this.segundaChecked = false;
		this.tercaChecked = false;
		this.quartaChecked = false;
		this.quintaChecked = false;
		this.sextaChecked = false;
	}

	public void limparTela() {
		this.filtro = new ProgramacaoManualParcelasEntregaFiltroVO();
		this.filtro.setFormaProgramacao(DominioFormaProgramacao.PS);
		limparHorarios();
		this.listaItensAF = null;
	}

	public String voltar() {

		this.lctNumero = null;
		this.numeroAF = null;
		this.complemento = null;
		this.numeroFornecedor = null;

		return "compras-pesquisarAFProgramarManual";
	}

	public String obterStringTruncada(String str, Integer tamanhoMaximo) {
		if (str.length() > tamanhoMaximo) {
			str = str.substring(0, tamanhoMaximo) + "...";
		}
		return str;
	}

	public void limparComplemento() {
		this.filtro.setComplemento(null);
	}

	// Getters e Setters

	public ProgramacaoManualParcelasEntregaFiltroVO getFiltro() {
		return filtro;
	}

	public void setFiltro(ProgramacaoManualParcelasEntregaFiltroVO filtro) {
		this.filtro = filtro;
	}

	public List<ConsultaItensAFProgramacaoManualVO> getListaItensAF() {
		return listaItensAF;
	}

	public void setListaItensAF(List<ConsultaItensAFProgramacaoManualVO> listaItensAF) {
		this.listaItensAF = listaItensAF;
	}

	public boolean isPrimeiraConsulta() {
		return primeiraConsulta;
	}

	public void setPrimeiraConsulta(boolean primeiraConsulta) {
		this.primeiraConsulta = primeiraConsulta;
	}

	public Boolean getIndExibeFiltros() {
		return indExibeFiltros;
	}

	public void setIndExibeFiltros(Boolean indExibeFiltros) {
		this.indExibeFiltros = indExibeFiltros;
	}

	public Integer getLctNumero() {
		return lctNumero;
	}

	public void setLctNumero(Integer lctNumero) {
		this.lctNumero = lctNumero;
	}

	public Integer getNumeroAF() {
		return numeroAF;
	}

	public void setNumeroAF(Integer numeroAF) {
		this.numeroAF = numeroAF;
	}

	public Short getComplemento() {
		return complemento;
	}

	public void setComplemento(Short complemento) {
		this.complemento = complemento;
	}

	public Integer getNumeroFornecedor() {
		return numeroFornecedor;
	}

	public void setNumeroFornecedor(Integer numeroFornecedor) {
		this.numeroFornecedor = numeroFornecedor;
	}

	public Integer getNumeroItem() {
		return numeroItem;
	}

	public void setNumeroItem(Integer numeroItem) {
		this.numeroItem = numeroItem;
	}

	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}

	public Boolean getSabadoChecked() {
		return sabadoChecked;
	}

	public void setSabadoChecked(Boolean sabadoChecked) {
		this.sabadoChecked = sabadoChecked;
	}

	public Boolean getDomingoChecked() {
		return domingoChecked;
	}

	public void setDomingoChecked(Boolean domingoChecked) {
		this.domingoChecked = domingoChecked;
	}

	public Boolean getSegundaChecked() {
		return segundaChecked;
	}

	public void setSegundaChecked(Boolean segundaChecked) {
		this.segundaChecked = segundaChecked;
	}

	public Boolean getTercaChecked() {
		return tercaChecked;
	}

	public void setTercaChecked(Boolean tercaChecked) {
		this.tercaChecked = tercaChecked;
	}

	public Boolean getQuartaChecked() {
		return quartaChecked;
	}

	public void setQuartaChecked(Boolean quartaChecked) {
		this.quartaChecked = quartaChecked;
	}

	public Boolean getQuintaChecked() {
		return quintaChecked;
	}

	public void setQuintaChecked(Boolean quintaChecked) {
		this.quintaChecked = quintaChecked;
	}

	public Boolean getSextaChecked() {
		return sextaChecked;
	}

	public void setSextaChecked(Boolean sextaChecked) {
		this.sextaChecked = sextaChecked;
	}

	public ModalAlertaGerarVO getModalAlertaGerarVO() {
		return modalAlertaGerarVO;
	}

	public void setModalAlertaGerarVO(ModalAlertaGerarVO modalAlertaGerarVO) {
		this.modalAlertaGerarVO = modalAlertaGerarVO;
	}

	public boolean isExibirModalAlertaGerar() {
		return exibirModalAlertaGerar;
	}

	public void setExibirModalAlertaGerar(boolean exibirModalAlertaGerar) {
		this.exibirModalAlertaGerar = exibirModalAlertaGerar;
	}
}
