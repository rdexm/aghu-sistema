package br.gov.mec.aghu.prescricaomedica.cadastroapoio.action;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.internacao.vo.AghUnidadesFuncionaisVO;
import br.gov.mec.aghu.model.MpmHorarioInicAprazamento;
import br.gov.mec.aghu.model.MpmHorarioInicAprazamentoId;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterHorarioInicioAprazamentoPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 5912648867720596933L;
	
	private static final String PAGE_MANTER_HORARIOS_INI_APRAZAMENTO = "manterHorariosInicioAprazamento";

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@Inject @Paginator
	private DynamicDataModel<MpmHorarioInicAprazamento> dataModel;

	private AghUnidadesFuncionaisVO unidadeFuncionalPesquisa;
	private DominioSituacao situacaoUnidadeFuncional;
	
	private MpmHorarioInicAprazamento horarioAprazamento;
	private Integer frequencia;	
	
	private Integer frequenciaExclusao;
	private Integer unidadeFuncionalExclusao;
	private Integer tipoFrequenciaAprazamentoExclusao;
	
	private Boolean modoEdicao = Boolean.FALSE;
	private Boolean modoManter = Boolean.FALSE;
	
	@PostConstruct
	public void init() {
		begin(conversation, true);
	}
	
	/**
	 * Obtém unidades funcionais relativas à aprazamento por código ou descricao e situação
	 * @param paramPesquisa Código ou descrição
	 * 
	 * @return Lista de Unidades Funcionais
	 */
	public List<AghUnidadesFuncionaisVO> pesquisarUnidadesFuncionaisAprazamentoPorCodigoDescricao(
			String paramPesquisa) {
		return prescricaoMedicaFacade.pesquisarUnidadesFuncionaisAprazamentoPorCodigoDescricao(paramPesquisa);

	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<MpmHorarioInicAprazamento> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		List<MpmHorarioInicAprazamento> horariosAprazamentos = new ArrayList<MpmHorarioInicAprazamento>();
		Short codUnidadeFuncional = null;
		
		if(getUnidadeFuncionalPesquisa() != null){
			codUnidadeFuncional = getUnidadeFuncionalPesquisa().getSeq();
		}
		horariosAprazamentos = prescricaoMedicaFacade.pesquisarHorariosInicioAprazamentos(firstResult,
					maxResult, orderProperty, asc, codUnidadeFuncional, getSituacaoUnidadeFuncional());
		
		return horariosAprazamentos;
	}

	@Override
	public Long recuperarCount() {
		Short codUnidadeFuncional = null;
		if(getUnidadeFuncionalPesquisa() != null){
			codUnidadeFuncional = getUnidadeFuncionalPesquisa().getSeq();
		}
		return this.prescricaoMedicaFacade.pesquisarHorariosInicioAprazamentosCount(codUnidadeFuncional, 
				getSituacaoUnidadeFuncional());
	}
	
	
	public void limparCamposPesquisa() {
		setUnidadeFuncionalPesquisa(null);
		setSituacaoUnidadeFuncional(null);
		setHorarioAprazamento(new MpmHorarioInicAprazamento());
		getHorarioAprazamento().setIndSituacao(null);
		setFrequencia(null);			
		setModoEdicao(Boolean.FALSE);
		setModoManter(Boolean.FALSE);
		this.getDataModel().limparPesquisa();
	}
	
	public void pesquisar() {
		setHorarioAprazamento(new MpmHorarioInicAprazamento());
		getHorarioAprazamento().setIndSituacao(DominioSituacao.A);
		this.getDataModel().reiniciarPaginator();
		setModoManter(Boolean.TRUE);
	}
	
	public String gravar() throws ParseException, BaseException{
		String bundleMsgSucesso = "";
		String retornoGravacao = null;
		try {
			if (getModoEdicao()) {
				bundleMsgSucesso = "SUCESSO_ALTERACAO_HORARIO_INICIO_APRAZAMENTO";
			} else {
				bundleMsgSucesso = "SUCESSO_GRAVACAO_HORARIO_INICIO_APRAZAMENTO";
				
				getHorarioAprazamento().setId(new MpmHorarioInicAprazamentoId());
				getHorarioAprazamento().getId().setFrequencia(frequencia.shortValue());
				getHorarioAprazamento().getId().setTfqSeq(horarioAprazamento.getTipoFreqAprazamento().getSeq());				
				getHorarioAprazamento().getId().setUnfSeq(getUnidadeFuncionalPesquisa().getSeq());
			}
			prescricaoMedicaFacade.persistirHorarioInicioAprazamento(getHorarioAprazamento());
			this.apresentarMsgNegocio(Severity.INFO, bundleMsgSucesso);
			//Efetua a pesquisa de novo para o registro aparecer atualizado
			pesquisar();
			cancelarEdicao();
		} catch (BaseListException e) {
			getHorarioAprazamento().setCriadoEm(null);
			this.apresentarExcecaoNegocio(e);
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		}
		return retornoGravacao;
	}
	
	
	public String redirecionarEdicao(MpmHorarioInicAprazamento horarioAprazamento) {
		setHorarioAprazamento(horarioAprazamento);
		setFrequencia(horarioAprazamento.getId().getFrequencia().intValue());
		setModoEdicao(Boolean.TRUE);
		return PAGE_MANTER_HORARIOS_INI_APRAZAMENTO;
	}
	
	public void cancelarEdicao() {
		setFrequencia(null);
		setHorarioAprazamento(new MpmHorarioInicAprazamento());
		getHorarioAprazamento().setHorarioInicio(null);
		getHorarioAprazamento().setIndSituacao(DominioSituacao.A);
		setModoEdicao(Boolean.FALSE);
		this.getDataModel().reiniciarPaginator();
	}
	
	public void excluir(MpmHorarioInicAprazamentoId id) {
		if(getHorarioAprazamento()!=null) {
			prescricaoMedicaFacade.removerHorarioAprazamento(id);
			this.apresentarMsgNegocio(Severity.INFO, "SUCESSO_EXCLUIR_HORARIO_APRAZAMENTO");
		}
		cancelarEdicao();
	}
	
	public String getDescricaoTipoFrequenciaAprazamento() {
		return buscaDescricaoTipoFrequenciaAprazamento(horarioAprazamento.getTipoFreqAprazamento());
	}

	public String buscaDescricaoTipoFrequenciaAprazamento(
			MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento) {
		return tipoFrequenciaAprazamento != null ? tipoFrequenciaAprazamento
				.getDescricaoSintaxeFormatada(this.frequencia != null ? this.frequencia
						.shortValue()
						: null)
				: "";
	}
	
	/**
	 * Pesquisa tipos de aprazamento ativos e de frequência por código ou descrição
	 * @param objPesquisa Códuigo/ Descrição
	 * @return Lista de tipos de aprazamento
	 */
	public List<MpmTipoFrequenciaAprazamento> pesquisarTipoAprazamentoAtivoFrequenciaPorCodigoDescricao(
			String objPesquisa) {

		return prescricaoMedicaFacade.pesquisarTipoAprazamentoAtivoFrequenciaPorCodigoDescricao(
				objPesquisa);

	}

	/*
	 * Getters and Setters
	 */
	public DynamicDataModel<MpmHorarioInicAprazamento> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<MpmHorarioInicAprazamento> dataModel) {
		this.dataModel = dataModel;
	}
	
	/**
	 * @param horarioAprazamento the horarioAprazamento to set
	 */
	public void setHorarioAprazamento(MpmHorarioInicAprazamento horarioAprazamento) {
		this.horarioAprazamento = horarioAprazamento;
	}

	/**
	 * @return the horarioAprazamento
	 */
	public MpmHorarioInicAprazamento getHorarioAprazamento() {
		return horarioAprazamento;
	}


	/**
	 * @param prescricaoFacade the prescricaoFacade to set
	 */
	public void setPrescricaoMedicaFacade(IPrescricaoMedicaFacade prescricaoMedicaFacade) {
		this.prescricaoMedicaFacade = prescricaoMedicaFacade;
	}

	public AghUnidadesFuncionaisVO getUnidadeFuncionalPesquisa() {
		return unidadeFuncionalPesquisa;
	}

	public void setUnidadeFuncionalPesquisa(
			AghUnidadesFuncionaisVO unidadeFuncionalPesquisa) {
		this.unidadeFuncionalPesquisa = unidadeFuncionalPesquisa;
	}

	public DominioSituacao getSituacaoUnidadeFuncional() {
		return situacaoUnidadeFuncional;
	}

	public void setSituacaoUnidadeFuncional(DominioSituacao situacaoUnidadeFuncional) {
		this.situacaoUnidadeFuncional = situacaoUnidadeFuncional;
	}

	public void setFrequencia(Integer frequencia) {
		this.frequencia = frequencia;
	}

	public Integer getFrequencia() {
		return frequencia;
	}



	public Integer getFrequenciaExclusao() {
		return frequenciaExclusao;
	}

	public void setFrequenciaExclusao(Integer frequenciaExclusao) {
		this.frequenciaExclusao = frequenciaExclusao;
	}

	public Integer getUnidadeFuncionalExclusao() {
		return unidadeFuncionalExclusao;
	}

	public void setUnidadeFuncionalExclusao(Integer unidadeFuncionalExclusao) {
		this.unidadeFuncionalExclusao = unidadeFuncionalExclusao;
	}

	public Integer getTipoFrequenciaAprazamentoExclusao() {
		return tipoFrequenciaAprazamentoExclusao;
	}

	public void setTipoFrequenciaAprazamentoExclusao(
			Integer tipoFrequenciaAprazamentoExclusao) {
		this.tipoFrequenciaAprazamentoExclusao = tipoFrequenciaAprazamentoExclusao;
	}

	public Boolean getModoEdicao() {
		return modoEdicao;
	}

	public void setModoEdicao(Boolean modoEdicao) {
		this.modoEdicao = modoEdicao;
	}

	public Boolean getModoManter() {
		return modoManter;
	}

	public void setModoManter(Boolean modoManter) {
		this.modoManter = modoManter;
	}
	
	/**
	 * Método colocado no controller para indicar se a linha do horário clicado no grid deve ser colorida
	 * Método definido aqui para facilitar a leitura, estava muito extenso no .xhtml.
	 * @param horarioAprazamentoClicado
	 * @return Boolean
	 */
	public Boolean colorirLinha(MpmHorarioInicAprazamento horarioAprazamentoClicado){
		return getModoEdicao() && getHorarioAprazamento() != null && horarioAprazamentoClicado != null && getHorarioAprazamento().getId().equals(horarioAprazamentoClicado.getId());
	}
}