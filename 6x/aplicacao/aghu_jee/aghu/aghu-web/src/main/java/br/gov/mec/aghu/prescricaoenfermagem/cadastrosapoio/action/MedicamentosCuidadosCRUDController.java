package br.gov.mec.aghu.prescricaoenfermagem.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.business.prescricaoenfermagem.IPrescricaoEnfermagemFacade;
import br.gov.mec.aghu.business.prescricaoenfermagem.cadastrosapoio.IPrescricaoEnfermagemApoioFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoMedicamento;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.EpeCuidadoMedicamento;
import br.gov.mec.aghu.model.EpeCuidadoMedicamentoId;
import br.gov.mec.aghu.model.EpeCuidados;
import br.gov.mec.aghu.prescricaoenfermagem.vo.CuidadoMedicamentoVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * #4961 - Manter medicamentos x cuidados
 * 
 * @author jback
 * 
 */

public class MedicamentosCuidadosCRUDController extends ActionController implements ActionPaginator {

	@Inject @Paginator
	private DynamicDataModel<CuidadoMedicamentoVO> dataModel;

	private static final long serialVersionUID = -5616267629647479374L;
	
	private static final String PAGE_MEDICAMENTOS_CUIDADOS_LIST = "medicamentosCuidadosList";

	@EJB
	private IPrescricaoEnfermagemFacade prescricaoEnfermagemFacade;

	@EJB
	private IPrescricaoEnfermagemApoioFacade prescricaoEnfermagemApoioFacade;

	private AfaMedicamento medicamento;
	private DominioSituacaoMedicamento indSituacaoMedicamento;
	private Integer matCodigoMedicamento;

	private Boolean medicamentoSituacao;
	private EpeCuidados cuidado;
	private Boolean situacao;
	private Boolean suggestionAdiconar;
	private Integer horasAntes;
	private Integer horasApos;
	private CuidadoMedicamentoVO cuidadoMedicamento;
	private CuidadoMedicamentoVO cuidadoMedicamentoSelecionado;
	private Boolean editarRegistro = false;
	private final int composHorasValorPadrao = 24;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void iniciar() {
	 

		setMedicamentoSituacao((indSituacaoMedicamento != null) ? DominioSituacao.A.getDescricao().equals(
				indSituacaoMedicamento.getDescricao()) : false);
		setSituacao(true);
		setSuggestionAdiconar(true);
		setHorasAntes(composHorasValorPadrao);
		setHorasApos(composHorasValorPadrao);

		setMedicamento(prescricaoEnfermagemApoioFacade.obterMedicamentoPorMatCodigo(matCodigoMedicamento));

		if (!dataModel.getPesquisaAtiva()) {
			dataModel.reiniciarPaginator();
		}
	
	}

	public List<EpeCuidados> pesquisarCuidados(String filtro) {
		return this.returnSGWithCount(prescricaoEnfermagemFacade.pesquisarCuidadosAtivosPorMatCodigoOuDescricaoNaoAtribuidosMedicamentos(
				(String) filtro, matCodigoMedicamento),pesquisarCuidadosCount(filtro));
	}

	public Long pesquisarCuidadosCount(String filtro) {
		return prescricaoEnfermagemFacade.pesquisarCuidadosAtivosPorMatCodigoOuDescricaoNaoAtribuidosMedicamentosCount(
				(String) filtro, matCodigoMedicamento);
	}

	public void ativarBotaoAdicionar() {
		setSuggestionAdiconar(false);
	}

	public void limpar() {
		setHorasAntes(composHorasValorPadrao);
		setHorasApos(composHorasValorPadrao);
		cuidado = null;
		dataModel.setPesquisaAtiva(Boolean.TRUE);
	}

	private EpeCuidadoMedicamento popularCuidadoMedicamento() {
		EpeCuidadoMedicamento cuidadoMedicamento = new EpeCuidadoMedicamento();
		EpeCuidadoMedicamentoId cuidadoMedicamentoId = new EpeCuidadoMedicamentoId();

		cuidadoMedicamentoId.setMedMatCodigo(matCodigoMedicamento);
		cuidadoMedicamentoId.setCuiSeq(cuidado.getSeq());
		cuidadoMedicamento.setId(cuidadoMedicamentoId);
		cuidadoMedicamento.setCuidado(cuidado);
		cuidadoMedicamento.setMedicamento(medicamento);
		cuidadoMedicamento.setHorasAntes(horasAntes);
		cuidadoMedicamento.setHorasApos(horasApos);
		cuidadoMedicamento.setSituacao((situacao) ? DominioSituacao.A : DominioSituacao.I);

		return cuidadoMedicamento;
	}

	public void adicionar() {
		try {
			EpeCuidadoMedicamento cuidadoMedicamento = popularCuidadoMedicamento();

			if (editarRegistro) {
				prescricaoEnfermagemApoioFacade.atualizarCuidadoMedicamento(cuidadoMedicamento);
			} else {
				prescricaoEnfermagemApoioFacade.persistirCuidadoMedicamento(cuidadoMedicamento);
			}

			apresentarMsgNegocio(Severity.INFO, (editarRegistro) ? "MENSAGEM_SUCESSO_ALTERACAO_MANTER_MEDICAMENTOS_CUIDADOS"
					: "MENSAGEM_SUCESSO_ADICAO_MANTER_MEDICAMENTOS_CUIDADOS", cuidado.getDescricao());

			limpar();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

		dataModel.reiniciarPaginator();
		editarRegistro = Boolean.FALSE;
	}

	public void editar() throws ApplicationBusinessException {
		EpeCuidados cuidado = new EpeCuidados();
		cuidado.setSeq(cuidadoMedicamentoSelecionado.getCuiSeq());
		cuidado.setDescricao(cuidadoMedicamentoSelecionado.getDescricao());
		setCuidado(cuidado);

		AfaMedicamento medicamento = new AfaMedicamento();
		medicamento.setMatCodigo(this.medicamento.getMatCodigo());

		setHorasAntes(cuidadoMedicamentoSelecionado.getHorasAntes());
		setHorasApos(cuidadoMedicamentoSelecionado.getHorasApos());
		setSituacao(DominioSituacaoMedicamento.A.getDescricao().equals(cuidadoMedicamentoSelecionado.getSituacao()));
		setEditarRegistro(true);
	}

	public void cancelarEdicao() {
		setEditarRegistro(false);
		limpar();
	}

	public void excluir() {
		try {
			prescricaoEnfermagemApoioFacade.excluirCuidadoMedicamento(cuidadoMedicamentoSelecionado.getCuiSeq(), medicamento.getMatCodigo());

			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_MANTER_MEDICAMENTOS_CUIDADOS",
					cuidadoMedicamentoSelecionado.getDescricao());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

		dataModel.reiniciarPaginator();
	}

	@Override
	public List<CuidadoMedicamentoVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {
		List<CuidadoMedicamentoVO> listaPaginada = prescricaoEnfermagemFacade.pesquisarCuidadosMedicamentos(matCodigoMedicamento,
				firstResult, maxResult, orderProperty, asc);
		return listaPaginada;
	}

	@Override
	public Long recuperarCount() {
		return prescricaoEnfermagemFacade.pesquisarCuidadosMedicamentosCount(matCodigoMedicamento);
	}

	public String cancelar() {
		cancelarEdicao();
		return PAGE_MEDICAMENTOS_CUIDADOS_LIST;
	}

	public Boolean getMedicamentoSituacao() {
		return medicamentoSituacao;
	}

	public void setMedicamentoSituacao(Boolean medicamentoSituacao) {
		this.medicamentoSituacao = medicamentoSituacao;
	}

	public EpeCuidados getCuidado() {
		return cuidado;
	}

	public void setCuidado(EpeCuidados cuidado) {
		this.cuidado = cuidado;
	}

	public Boolean getSituacao() {
		return situacao;
	}

	public void setSituacao(Boolean situacao) {
		this.situacao = situacao;
	}

	public Boolean getSuggestionAdiconar() {
		return suggestionAdiconar;
	}

	public void setSuggestionAdiconar(Boolean suggestionAdiconar) {
		this.suggestionAdiconar = suggestionAdiconar;
	}

	public Integer getHorasAntes() {
		return horasAntes;
	}

	public void setHorasAntes(Integer horasAntes) {
		this.horasAntes = horasAntes;
	}

	public Integer getHorasApos() {
		return horasApos;
	}

	public void setHorasApos(Integer horasApos) {
		this.horasApos = horasApos;
	}

	public CuidadoMedicamentoVO getCuidadoMedicamento() {
		return cuidadoMedicamento;
	}

	public void setCuidadoMedicamento(CuidadoMedicamentoVO cuidadoMedicamento) {
		this.cuidadoMedicamento = cuidadoMedicamento;
	}

	public Boolean getEditarRegistro() {
		return editarRegistro;
	}

	public void setEditarRegistro(Boolean editarRegistro) {
		this.editarRegistro = editarRegistro;
	}

	public AfaMedicamento getMedicamento() {
		return medicamento;
	}

	public void setMedicamento(AfaMedicamento medicamento) {
		this.medicamento = medicamento;
	}

	public DominioSituacaoMedicamento getIndSituacaoMedicamento() {
		return indSituacaoMedicamento;
	}

	public void setIndSituacaoMedicamento(DominioSituacaoMedicamento indSituacaoMedicamento) {
		this.indSituacaoMedicamento = indSituacaoMedicamento;
	}

	public Integer getMatCodigoMedicamento() {
		return matCodigoMedicamento;
	}

	public void setMatCodigoMedicamento(Integer matCodigoMedicamento) {
		this.matCodigoMedicamento = matCodigoMedicamento;
	}

	public DynamicDataModel<CuidadoMedicamentoVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<CuidadoMedicamentoVO> dataModel) {
		this.dataModel = dataModel;
	}

	public CuidadoMedicamentoVO getCuidadoMedicamentoSelecionado() {
		return cuidadoMedicamentoSelecionado;
	}

	public void setCuidadoMedicamentoSelecionado(CuidadoMedicamentoVO cuidadoMedicamentoSelecionado) {
		this.cuidadoMedicamentoSelecionado = cuidadoMedicamentoSelecionado;
	}
}
