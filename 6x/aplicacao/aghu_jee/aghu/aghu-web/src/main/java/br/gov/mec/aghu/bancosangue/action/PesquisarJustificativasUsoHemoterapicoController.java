package br.gov.mec.aghu.bancosangue.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.business.bancosangue.IBancoDeSangueFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.AbsComponenteSanguineo;
import br.gov.mec.aghu.model.AbsGrupoJustificativaComponenteSanguineo;
import br.gov.mec.aghu.model.AbsJustificativaComponenteSanguineo;
import br.gov.mec.aghu.model.AbsProcedHemoterapico;

public class PesquisarJustificativasUsoHemoterapicoController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -5541553978097376301L;

	@EJB
	private IBancoDeSangueFacade bancoDeSangueFacade;
	
	// parametro recebido da estoria #6384
	private String codigoComponenteSanguineo;
	
	// parametro recebido da estoria #6403
	private String codigoProcedimentoHemoterapico;
	
	// suggestion box componente sanguineo (desabilitado)
	private AbsComponenteSanguineo componenteSanguineo;
	
	// suggestion box procedimento hemoterapico (desabilitado)
	private AbsProcedHemoterapico procedimentoHemoterapico;
	
	// suggestion box grupo justificativa
	private AbsGrupoJustificativaComponenteSanguineo grupoJustificativaComponenteSanguineo;
	
	// pojo com os parametros da tela (form)
	private AbsJustificativaComponenteSanguineo justificativaComponenteSanguineo; 
	
	// combo descricao livre
	private DominioSimNao descricaoLivre;
	
	// identifica qual tela originou a requisicao
	private String origemRequisicao;
	private String origemCadastro;
	
	private String voltar;

	@Inject @Paginator
	private DynamicDataModel<AbsJustificativaComponenteSanguineo> dataModel;
	
	private AbsJustificativaComponenteSanguineo selecionado;
	
	private enum EnumTargetJustificativasUsoHemoterapicoPaginatorController {
		
		// tela de cadastro
		VOLTAR_CADASTRO("bancodesangue-manterJustificativasUsoHemoterapico"), 
		
		// estoria #6403
		VOLTAR_PROCEDIMENTO_HEMOTERAPICO("bancodesangue-manterProcedimentosHemoterapicos"),
		
		// estoria #6384
		VOLTAR_COMPONENTE_SANGUINEO("prescricaomedica-manterComponentesSanguineos"),
		
		// manter
		MANTER("bancodesangue-manterJustificativasUsoHemoterapico");
		
		public final String descricao;

		private EnumTargetJustificativasUsoHemoterapicoPaginatorController(String descricao) {
			this.descricao = descricao;
		} 
	}
	
	private enum OrigemRequisicaoEnum {
		// tela de cadastro
		ORIGEM_CADASTRO,
		// estoria #6403
		ORIGEM_PROCEDIMENTO_HEMOTERAPICO,
		// estoria #6384
		ORIGEM_COMPONENTE_SANGUINEO;
	}
	
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

		
	/**
	 * Executado quando a tela é acessada
	 */
	public void iniciar() {
	 

	 

		
		if(StringUtils.isNotBlank(origemCadastro)) {
			pesquisar();
		}
		
		// identifica a tela de origem
		if(StringUtils.isNotBlank(origemRequisicao)) {			
			if(OrigemRequisicaoEnum.ORIGEM_PROCEDIMENTO_HEMOTERAPICO.toString().equalsIgnoreCase(origemRequisicao)) {
				preencherComboProcedimentoHemoterapico();
				setVoltar(EnumTargetJustificativasUsoHemoterapicoPaginatorController.VOLTAR_PROCEDIMENTO_HEMOTERAPICO.descricao);
			}
			if(OrigemRequisicaoEnum.ORIGEM_COMPONENTE_SANGUINEO.toString().equalsIgnoreCase(origemRequisicao)) {
				preencherComboComponenteSanguineo();
				setVoltar(EnumTargetJustificativasUsoHemoterapicoPaginatorController.VOLTAR_COMPONENTE_SANGUINEO.descricao);
			}
			if(OrigemRequisicaoEnum.ORIGEM_CADASTRO.toString().equalsIgnoreCase(origemRequisicao)) {
				preencherComboProcedimentoHemoterapico();
				preencherComboComponenteSanguineo();
				setVoltar(EnumTargetJustificativasUsoHemoterapicoPaginatorController.VOLTAR_CADASTRO.descricao);
			}
			pesquisar();
			
		} else {
			
			if(StringUtils.isNotBlank(codigoComponenteSanguineo)) {
				componenteSanguineo = bancoDeSangueFacade.obterComponeteSanguineoPorCodigo(codigoComponenteSanguineo);
				setOrigemRequisicao(OrigemRequisicaoEnum.ORIGEM_COMPONENTE_SANGUINEO.toString().toString());
				setVoltar(EnumTargetJustificativasUsoHemoterapicoPaginatorController.VOLTAR_COMPONENTE_SANGUINEO.descricao);
			}
			if(StringUtils.isNotBlank(codigoProcedimentoHemoterapico)) {
				procedimentoHemoterapico = bancoDeSangueFacade.obterProcedimentoHemoterapicoPorCodigo(codigoProcedimentoHemoterapico);
				setOrigemRequisicao(OrigemRequisicaoEnum.ORIGEM_PROCEDIMENTO_HEMOTERAPICO.toString());
				setVoltar(EnumTargetJustificativasUsoHemoterapicoPaginatorController.VOLTAR_PROCEDIMENTO_HEMOTERAPICO.descricao);
			}
		}
	
	}
	
		
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}
	
	private void preencherComboComponenteSanguineo() {
		if(StringUtils.isNotBlank(codigoComponenteSanguineo)) {
			componenteSanguineo = bancoDeSangueFacade.obterComponeteSanguineoPorCodigo(codigoComponenteSanguineo);
		}
	}

	private void preencherComboProcedimentoHemoterapico() {
		if(StringUtils.isNotBlank(codigoProcedimentoHemoterapico)) {
			procedimentoHemoterapico = bancoDeSangueFacade.obterProcedimentoHemoterapicoPorCodigo(codigoProcedimentoHemoterapico);
		}
	}
	
	
	/**
	 * Redireciona para estoria que originou a requisicao
	 */
	public String voltar() {
		if(StringUtils.isBlank(voltar)) {
			return EnumTargetJustificativasUsoHemoterapicoPaginatorController.VOLTAR_CADASTRO.descricao;
		} else {			
			return voltar;
		}
	}
	
	public String inserir() {
		return EnumTargetJustificativasUsoHemoterapicoPaginatorController.MANTER.descricao;
	}
	
	public String editar() {
		return EnumTargetJustificativasUsoHemoterapicoPaginatorController.MANTER.descricao;
	}
	
	
	/**
	 * Limpa os campos da tela
	 */
	public void limpar() {
		dataModel.limparPesquisa();
		setJustificativaComponenteSanguineo(new AbsJustificativaComponenteSanguineo());
		setDescricaoLivre(null);
		setGrupoJustificativaComponenteSanguineo(null);
	}

	@Override
	public List<AbsJustificativaComponenteSanguineo> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		Short seqGrupoJustificativa = null;
		String codigoComponenteSanguineo = null;
		String codigoProcedimentoHemoterapico = null;
		if(grupoJustificativaComponenteSanguineo != null) {
			seqGrupoJustificativa = grupoJustificativaComponenteSanguineo.getSeq();
		}
		if(getProcedimentoHemoterapico() !=  null) {
			codigoProcedimentoHemoterapico = getProcedimentoHemoterapico().getCodigo();
		}
		if(getComponenteSanguineo() !=  null) {
			codigoComponenteSanguineo = getComponenteSanguineo().getCodigo();
		}
		return bancoDeSangueFacade.pesquisarJustificativaUsoHemoterapico(getJustificativaComponenteSanguineo().getSeq(),
																	     codigoComponenteSanguineo, 
																	     codigoProcedimentoHemoterapico,
																	     seqGrupoJustificativa,
																	     getJustificativaComponenteSanguineo().getTipoPaciente(), 
																	     getDescricaoLivre(),
																	     getJustificativaComponenteSanguineo().getSituacao(), 
																	     getJustificativaComponenteSanguineo().getDescricao(),
																	     firstResult,
																	     maxResult);
	}

	@Override
	public Long recuperarCount() {
		Short seqGrupoJustificativa = null;
		String codigoComponenteSanguineo = null;
		String codigoProcedimentoHemoterapico = null;
		if(grupoJustificativaComponenteSanguineo != null) {
			seqGrupoJustificativa = grupoJustificativaComponenteSanguineo.getSeq();
		}
		if(getProcedimentoHemoterapico() !=  null) {
			codigoProcedimentoHemoterapico = getProcedimentoHemoterapico().getCodigo();
		}
		if(getComponenteSanguineo() !=  null) {
			codigoComponenteSanguineo = getComponenteSanguineo().getCodigo();
		}
		return bancoDeSangueFacade.pesquisarJustificativaUsoHemoterapicoCount(getJustificativaComponenteSanguineo().getSeq(),
																			  codigoComponenteSanguineo, 
																			  codigoProcedimentoHemoterapico,
			     															  seqGrupoJustificativa,
																		      getJustificativaComponenteSanguineo().getTipoPaciente(), 
																		      getDescricaoLivre(),
																		      getJustificativaComponenteSanguineo().getSituacao(), 
																		      getJustificativaComponenteSanguineo().getDescricao());
	}
	
	public String abreviar(String str){
		String abreviado = str;
		if(isAbreviar(str)) {
			abreviado = StringUtils.abbreviate(str, 50);
		}
		return abreviado;
	}
	
	public Boolean isAbreviar(String str){
		Boolean abreviar = Boolean.FALSE;
		if (str != null) {
			abreviar = str.length() > 50;
		}
		return abreviar;
	}
	
	
	/**
	 * Suggestion box de componente sanguineo
	 */
	public List<AbsGrupoJustificativaComponenteSanguineo> pesquisarGrupoJustificativaComponenteSanguineo(String param){
		return bancoDeSangueFacade.pesquisarGrupoJustificativaComponenteSanguineo(param);
	}
	
	public AbsJustificativaComponenteSanguineo getJustificativaComponenteSanguineo() {
		if(justificativaComponenteSanguineo == null) {
			justificativaComponenteSanguineo = new AbsJustificativaComponenteSanguineo();
		}
		return justificativaComponenteSanguineo;
	}

	public void setJustificativaComponenteSanguineo(AbsJustificativaComponenteSanguineo justificativaComponenteSanguineo) {
		this.justificativaComponenteSanguineo = justificativaComponenteSanguineo;
	}

	public String getCodigoComponenteSanguineo() {
		return codigoComponenteSanguineo;
	}

	public void setCodigoComponenteSanguineo(String codigoComponenteSanguineo) {
		this.codigoComponenteSanguineo = codigoComponenteSanguineo;
	}

	public String getCodigoProcedimentoHemoterapico() {
		return codigoProcedimentoHemoterapico;
	}

	public void setCodigoProcedimentoHemoterapico(String codigoProcedimentoHemoterapico) {
		this.codigoProcedimentoHemoterapico = codigoProcedimentoHemoterapico;
	}

	public DominioSimNao getDescricaoLivre() {
		return descricaoLivre;
	}

	public void setDescricaoLivre(DominioSimNao descricaoLivre) {
		this.descricaoLivre = descricaoLivre;
	}

	public AbsGrupoJustificativaComponenteSanguineo getGrupoJustificativaComponenteSanguineo() {
		return grupoJustificativaComponenteSanguineo;
	}

	public void setGrupoJustificativaComponenteSanguineo(AbsGrupoJustificativaComponenteSanguineo grupoJustificativaComponenteSanguineo) {
		this.grupoJustificativaComponenteSanguineo = grupoJustificativaComponenteSanguineo;
	}

	public String getOrigemRequisicao() {
		return origemRequisicao;
	}

	public void setOrigemRequisicao(String origemRequisicao) {
		this.origemRequisicao = origemRequisicao;
	}

	public AbsComponenteSanguineo getComponenteSanguineo() {
		return componenteSanguineo;
	}

	public void setComponenteSanguineo(AbsComponenteSanguineo componenteSanguineo) {
		this.componenteSanguineo = componenteSanguineo;
	}

	public AbsProcedHemoterapico getProcedimentoHemoterapico() {
		return procedimentoHemoterapico;
	}

	public void setProcedimentoHemoterapico(AbsProcedHemoterapico procedimentoHemoterapico) {
		this.procedimentoHemoterapico = procedimentoHemoterapico;
	}

	public String getVoltar() {
		return voltar;
	}

	public void setVoltar(String voltar) {
		this.voltar = voltar;
	}

	public String getOrigemCadastro() {
		return origemCadastro;
	}

	public void setOrigemCadastro(String origemCadastro) {
		this.origemCadastro = origemCadastro;
	}

	public DynamicDataModel<AbsJustificativaComponenteSanguineo> getDataModel() {
		return dataModel;
	}

	public void setDataModel(
			DynamicDataModel<AbsJustificativaComponenteSanguineo> dataModel) {
		this.dataModel = dataModel;
	}

	public AbsJustificativaComponenteSanguineo getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(AbsJustificativaComponenteSanguineo selecionado) {
		this.selecionado = selecionado;
	} 
}
