package br.gov.mec.aghu.prescricaoenfermagem.cadastrosapoio.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.business.prescricaoenfermagem.IPrescricaoEnfermagemFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.EpeCuidadoUnf;
import br.gov.mec.aghu.model.EpeCuidados;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.prescricaoenfermagem.action.PesquisaCuidadosController;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.action.ActionController;
//import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class CadastroCuidadosController extends ActionController {

	private static final long serialVersionUID = 6116421759278516589L;
	
	private static final String PAGE_PESQUISA_CUIDADOS = "pesquisaCuidados";
	
	private final static ConstanteAghCaractUnidFuncionais[] CARACTERISTICAS_UNIDADE_FUNCIONAL = {
		ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA, ConstanteAghCaractUnidFuncionais.UNID_INTERNACAO };

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IPrescricaoEnfermagemFacade prescricaoEnfermagemFacade;

	@Inject
	private PesquisaCuidadosController pesquisaCuidadosController;
	

	private Short seqEpeCuidado;
	private EpeCuidados epeCuidado;

	private Boolean situacaoCuidado;

	private Short frequencia;
	private MpmTipoFrequenciaAprazamento tipoAprazamento;

	private AghUnidadesFuncionais unidadeFuncional;

	private Boolean cuidadoRotina;
	private List<EpeCuidadoUnf> listaCuidadoUnidades;
	private EpeCuidadoUnf epeCuidadoUnf;
	private Boolean ativoEpeCuidadoUnf;
	private Boolean ativaBotaoGravarEpeCuidadoUnfs;
	private Boolean ativaCrudCuidadoUnfs;
	private Boolean ativaSugestioneGravar;	

	private EpeCuidadoUnf registroSelecionado;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void iniciar() {
	 

		epeCuidado = new EpeCuidados();
		epeCuidadoUnf = new EpeCuidadoUnf();
		unidadeFuncional = null;
		if (situacaoCuidado == null) {
			situacaoCuidado = Boolean.TRUE;
		}
		if (getSeqEpeCuidado() != null) {
			epeCuidado = prescricaoEnfermagemFacade.obterEpeCuidadosPorSeq(getSeqEpeCuidado());
			setSituacaoCuidado(epeCuidado.getIndSituacao().isAtivo());//
			carregarListaUnidadesPorCuidado();
			ativaCrudCuidadoUnfs = Boolean.TRUE;
			ativoEpeCuidadoUnf = Boolean.TRUE;
			ativaBotaoGravarEpeCuidadoUnfs = Boolean.TRUE;
			this.setTipoAprazamento(epeCuidado.getTipoFrequenciaAprazamento());
			this.prepararAprazamento();
			ativaSugestioneGravar = epeCuidado.getIndRotina();
		} else {
			ativaCrudCuidadoUnfs = Boolean.FALSE;
			this.setFrequencia(null);
			this.setTipoAprazamento(null);
		}
	
	}

	public void prepararAprazamento() {
		if (this.epeCuidado.getFrequencia() != null) {
			this.frequencia = this.epeCuidado.getFrequencia();
		}
	}

	public void carregarListaUnidadesPorCuidado() {

		listaCuidadoUnidades = prescricaoEnfermagemFacade.obterEpeCuidadoUnfPorEpeCuidadoSeq(epeCuidado.getSeq());
		prescricaoEnfermagemFacade.restaurarEpeCuidadoUnf(listaCuidadoUnidades);
	}

	public List<MpmTipoFrequenciaAprazamento> pesquisarTipoAprazamento(String parametro) {
		return prescricaoMedicaFacade.obterListaTipoFrequenciaAprazamentoDigitaFrequencia(false, parametro);
	}

	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncional(String parametro) {
		return aghuFacade.pesquisarUnidadesFuncionaisPorCodigoDescricaoCaracteristica(parametro,
				CARACTERISTICAS_UNIDADE_FUNCIONAL, null, Boolean.TRUE);
	}

	public String gravarEpeCuidadoUnfs() {
		try {
			if (ativoEpeCuidadoUnf) {
				epeCuidadoUnf.setSituacao(DominioSituacao.A);
			} else {
				epeCuidadoUnf.setSituacao(DominioSituacao.I);
			}
			String mensagemSucesso = prescricaoEnfermagemFacade.gravarEpeCuidadoUnfs(epeCuidadoUnf, epeCuidado, unidadeFuncional);
			apresentarMsgNegocio(Severity.INFO, mensagemSucesso);
			epeCuidadoUnf = new EpeCuidadoUnf();
			carregarListaUnidadesPorCuidado();
			ativoEpeCuidadoUnf = Boolean.TRUE;
			ativaBotaoGravarEpeCuidadoUnfs = Boolean.TRUE;
			unidadeFuncional = null;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	public String gravar() throws ApplicationBusinessException, ApplicationBusinessException {
		try {
			if (getSituacaoCuidado()) {
				epeCuidado.setIndSituacao(DominioSituacao.A);
			} else {
				epeCuidado.setIndSituacao(DominioSituacao.I);
			}
			epeCuidado.setFrequencia((this.frequencia != null) ? this.frequencia.shortValue() : null);
			validarAprazamento(epeCuidado);
			String mensagemSucesso = prescricaoEnfermagemFacade.gravar(epeCuidado, getSeqEpeCuidado());
			apresentarMsgNegocio(Severity.INFO, mensagemSucesso);
			ativaCrudCuidadoUnfs = Boolean.TRUE;
			ativoEpeCuidadoUnf = Boolean.TRUE;
			ativaBotaoGravarEpeCuidadoUnfs = Boolean.TRUE;
			ativaSugestioneGravar = epeCuidado.getIndRotina();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		pesquisaCuidadosController.getDataModel().reiniciarPaginator();
		return null;
	}
	
	public void processarSelecaoRegistro(EpeCuidadoUnf registroSelecionado){
		this.registroSelecionado = registroSelecionado;
	}
	
	public void excluir() {
		try {
			prescricaoEnfermagemFacade.removerEpeCuidadoUnf(registroSelecionado.getId());
			carregarListaUnidadesPorCuidado();
			this.apresentarMsgNegocio(Severity.INFO, "MSG_SUCESSO_EXCLUSAO_CUIDADO");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public String alterarSituacao(EpeCuidadoUnf epeCuidadoUnf) {
		String mensagemSucesso = prescricaoEnfermagemFacade.alterarSituacao(epeCuidadoUnf);
		apresentarMsgNegocio(Severity.INFO, mensagemSucesso);
		return null;
	}

	public void validarAprazamento(EpeCuidados cuidado) {

		if (this.frequencia != null) {
			cuidado.setFrequencia(this.frequencia.shortValue());
		}

		if (this.getTipoAprazamento() != null) {

			cuidado.setTipoFrequenciaAprazamento(this.getTipoAprazamento());

		} else {
			cuidado.setTipoFrequenciaAprazamento(null);
		}
	}

	public String buscarDescricaoTipoFrequenciaAprazamento(MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento) {
		if (tipoFrequenciaAprazamento != null) {
			return tipoFrequenciaAprazamento.getDescricaoSintaxeFormatada(this.frequencia);
		}

		return null;
	}

	public String getDescricaoTipoFrequenciaAprazamento() {
		return buscarDescricaoTipoFrequenciaAprazamento(tipoAprazamento);
	}

	public void validarCuidadoRotina() {
		try {
			prescricaoEnfermagemFacade.validarCuidadoRotina(epeCuidado.getIndRotina(), listaCuidadoUnidades);
		} catch (ApplicationBusinessException e) {
			epeCuidado.setIndRotina(Boolean.TRUE);
			apresentarExcecaoNegocio(e);
			return;
		}
	}

	public String cancelar() {

		limpar();

		return PAGE_PESQUISA_CUIDADOS;
	}

	private void limpar() {
		seqEpeCuidado = null;
		frequencia = null;
		tipoAprazamento = null;
		epeCuidado = new EpeCuidados();
		epeCuidadoUnf = new EpeCuidadoUnf();
		listaCuidadoUnidades = new ArrayList<EpeCuidadoUnf>();
	}

	public boolean verificarRequiredFrequencia() {
		return this.tipoAprazamento != null && this.tipoAprazamento.getIndDigitaFrequencia();
	}

	public void verificarFrequencia() {
		if (!this.verificarRequiredFrequencia()) {
			this.frequencia = null;
		}
	}

	// Getters e setters
	public Short getSeqEpeCuidado() {
		return seqEpeCuidado;
	}

	public void setSeqEpeCuidado(Short seqEpeCuidado) {
		this.seqEpeCuidado = seqEpeCuidado;
	}

	public Short getFrequencia() {
		return frequencia;
	}

	public void setFrequencia(Short frequencia) {
		this.frequencia = frequencia;
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public MpmTipoFrequenciaAprazamento getTipoAprazamento() {
		return tipoAprazamento;
	}

	public void setTipoAprazamento(MpmTipoFrequenciaAprazamento tipoAprazamento) {
		this.tipoAprazamento = tipoAprazamento;
	}

	public EpeCuidados getEpeCuidado() {
		return epeCuidado;
	}

	public void setEpeCuidado(EpeCuidados epeCuidado) {
		this.epeCuidado = epeCuidado;
	}

	public void setListaCuidadoUnidades(List<EpeCuidadoUnf> listaCuidadoUnidades) {
		this.listaCuidadoUnidades = listaCuidadoUnidades;
	}

	public Boolean getSituacaoCuidado() {
		return situacaoCuidado;
	}

	public void setSituacaoCuidado(Boolean situacaoCuidado) {
		this.situacaoCuidado = situacaoCuidado;
	}

	public Boolean getAtivaCrudCuidadoUnfs() {
		return ativaCrudCuidadoUnfs;
	}

	public void setAtivaCrudCuidadoUnfs(Boolean ativaCrudCuidadoUnfs) {
		this.ativaCrudCuidadoUnfs = ativaCrudCuidadoUnfs;
	}

	public void setEpeCuidadoUnf(EpeCuidadoUnf epeCuidadoUnf) {
		this.epeCuidadoUnf = epeCuidadoUnf;
	}

	public EpeCuidadoUnf getEpeCuidadoUnf() {
		return epeCuidadoUnf;
	}

	public List<EpeCuidadoUnf> getListaCuidadoUnidades() {
		return listaCuidadoUnidades;
	}

	public Boolean getAtivoEpeCuidadoUnf() {
		return ativoEpeCuidadoUnf;
	}

	public void setAtivoEpeCuidadoUnf(Boolean ativoEpeCuidadoUnf) {
		this.ativoEpeCuidadoUnf = ativoEpeCuidadoUnf;
	}

	public Boolean getAtivaBotaoGravarEpeCuidadoUnfs() {
		return ativaBotaoGravarEpeCuidadoUnfs;
	}

	public void setAtivaBotaoGravarEpeCuidadoUnfs(Boolean ativaBotaoGravarEpeCuidadoUnfs) {
		this.ativaBotaoGravarEpeCuidadoUnfs = ativaBotaoGravarEpeCuidadoUnfs;
	}

	public EpeCuidadoUnf getRegistroSelecionado() {
		return registroSelecionado;
	}

	public void setRegistroSelecionado(EpeCuidadoUnf registroSelecionado) {
		this.registroSelecionado = registroSelecionado;
	}

	public Boolean getCuidadoRotina() {
		return cuidadoRotina;
	}

	public void setCuidadoRotina(Boolean cuidadoRotina) {
		this.cuidadoRotina = cuidadoRotina;
	}

	public Boolean getAtivaSugestioneGravar() {
		return ativaSugestioneGravar;
	}

	public void setAtivaSugestioneGravar(Boolean ativaSugestioneGravar) {
		this.ativaSugestioneGravar = ativaSugestioneGravar;
	}
}
