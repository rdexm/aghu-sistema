package br.gov.mec.aghu.ambulatorio.action;

import static br.gov.mec.aghu.core.commons.WebUtil.initLocalizedMessage;

import java.text.MessageFormat;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoUnidadeFuncionalSala;
import br.gov.mec.aghu.model.AacUnidFuncionalSalas;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.WebUtil;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;



public class ManterZonasSalasPaginatorController extends ActionController implements ActionPaginator {
	
	private static final long serialVersionUID = -3877520736187930704L;
	private static final String MANTER_ZONAS_SALAS = "manterZonasSalas";
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;	
	@EJB
	private IAghuFacade aghuFacade;	
	@EJB
	private IParametroFacade parametroFacade;
	
	private AacUnidFuncionalSalas parametroSelecionado;

	@Inject @Paginator
	private DynamicDataModel<AacUnidFuncionalSalas> dataModel;
	
	//Filtros
	private AghUnidadesFuncionais unidadeFuncional;
	private Byte sala;
	private DominioTipoUnidadeFuncionalSala tipo;
	private DominioSituacao situacao;

	// Labels parametrizados
	private String labelZona;
	private String labelSala;
	private String titleZona;
	private String titleSala;
	
	//Atributos para excluir
	private Short unfSeqExcluir;
	private Byte salaExcluir;
	
	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}
	
	public enum ManterZonasSalasPaginatorControllerExceptionCode implements
	BusinessExceptionCode {
		MSG_SALA_POR_UNIDADE_FUNCIONAL_EXCLUIDO_SUCESSO;
	}	
	
	/**
	 * Método executado ao iniciar a controller
	 */
	public void iniciar() {
	 

	 

		this.carregarParametros();
	
	}
		
	
	private void carregarParametros() {
		try {
			labelZona = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_LABEL_ZONA).getVlrTexto();
			labelSala = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_LABEL_SALA).getVlrTexto();
			
			String message = WebUtil.initLocalizedMessage("TITLE_ZONA_GRADE_AGENDAMENTO", null);
			this.titleZona = MessageFormat.format(message, this.labelZona);
			message = initLocalizedMessage("TITLE_PESQUISAR_PACIENTES_AGENDADOS_SALA", null);
			this.titleSala = MessageFormat.format(message, this.labelSala);
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void pesquisar() {
		
		this.getDataModel().reiniciarPaginator();
		this.carregarParametros();
	}
	
	@Override
	public List<AacUnidFuncionalSalas> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {
		return this.ambulatorioFacade.listarSalasPorUnidadeFuncionalSalaTipoSituacao(firstResult, maxResult, orderProperty, asc, unidadeFuncional == null ? null : unidadeFuncional.getSeq(),
				this.sala, this.tipo, this.situacao);
	}

	@Override
	public Long recuperarCount() {
		return this.ambulatorioFacade.listarSalasPorUnidadeFuncionalSalaTipoSituacaoCount(unidadeFuncional == null ? null : unidadeFuncional.getSeq(), this.sala, this.tipo, this.situacao);
	}
	
	/**
	 * Método executado ao clicar no botão limpar
	 */
	public void limparPesquisa() {
		this.unidadeFuncional = null;
		this.sala = null;
		this.tipo = null;
		this.situacao = null;
		this.dataModel.setPesquisaAtiva(Boolean.FALSE);
		this.carregarParametros();
	}

	public String excluir() {
		try {
			this.ambulatorioFacade.removerZonaSala(this.parametroSelecionado.getId().getUnfSeq(), this.parametroSelecionado.getId().getSala());
			this.getDataModel().reiniciarPaginator();
			apresentarMsgNegocio(Severity.INFO, 
					ManterZonasSalasPaginatorControllerExceptionCode.
					MSG_SALA_POR_UNIDADE_FUNCIONAL_EXCLUIDO_SUCESSO.toString(), this.labelSala);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		return null;
	}
	
	public String inserirEditar(){
		return MANTER_ZONAS_SALAS;
	}

	/** Suggestions de unidade funcional (zona/sala)
	 * 
	 * @param param
	 * @return
	 */
	public List<AghUnidadesFuncionais> listaUnidadeFuncionalComSiglaNaoNulla(String param) {
		return  this.aghuFacade.listaUnidadeFuncionalComSiglaNaoNulla(param, DominioSituacao.A, 100, AghUnidadesFuncionais.Fields.SIGLA.toString());
	}

	public Long pesquisarPagadoresCount(Object param) {
		return this.aghuFacade.listaUnidadeFuncionalComSiglaNaoNullaCount(param, DominioSituacao.A);
	}
	
	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public Byte getSala() {
		return sala;
	}

	public void setSala(Byte sala) {
		this.sala = sala;
	}

	public DominioTipoUnidadeFuncionalSala getTipo() {
		return tipo;
	}

	public void setTipo(DominioTipoUnidadeFuncionalSala tipo) {
		this.tipo = tipo;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public String getLabelZona() {
		return labelZona;
	}

	public void setLabelZona(String labelZona) {
		this.labelZona = labelZona;
	}

	public String getLabelSala() {
		return labelSala;
	}

	public void setLabelSala(String labelSala) {
		this.labelSala = labelSala;
	}

	public IAmbulatorioFacade getAmbulatorioFacade() {
		return ambulatorioFacade;
	}

	public void setAmbulatorioFacade(IAmbulatorioFacade ambulatorioFacade) {
		this.ambulatorioFacade = ambulatorioFacade;
	}
	
	public Short getUnfSeqExcluir() {
		return unfSeqExcluir;
	}

	public void setUnfSeqExcluir(Short unfSeqExcluir) {
		this.unfSeqExcluir = unfSeqExcluir;
	}

	public Byte getSalaExcluir() {
		return salaExcluir;
	}

	public void setSalaExcluir(Byte salaExcluir) {
		this.salaExcluir = salaExcluir;
	}

	public String getTitleZona() {
		return titleZona;
	}

	public void setTitleZona(String titleZona) {
		this.titleZona = titleZona;
	}

	public String getTitleSala() {
		return titleSala;
	}

	public void setTitleSala(String titleSala) {
		this.titleSala = titleSala;
	}

	public DynamicDataModel<AacUnidFuncionalSalas> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<AacUnidFuncionalSalas> dataModel) {
	 this.dataModel = dataModel;
	}	

	public AacUnidFuncionalSalas getParametroSelecionado() {
		return parametroSelecionado;
	}

	public void setParametroSelecionado(AacUnidFuncionalSalas parametroSelecionado) {
		this.parametroSelecionado = parametroSelecionado;
	}
}
