package br.gov.mec.aghu.blococirurgico.portalplanejamento.action;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.context.RequestContext;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.opmes.business.IBlocoCirurgicoOpmesFacade;
import br.gov.mec.aghu.blococirurgico.vo.CadastroPlanejamentoVO;
import br.gov.mec.aghu.blococirurgico.vo.GrupoExcludenteVO;
import br.gov.mec.aghu.blococirurgico.vo.MbcOpmesListaGrupoExcludente;
import br.gov.mec.aghu.blococirurgico.vo.MbcOpmesVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioRequeridoItemRequisicao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoAgendas;
import br.gov.mec.aghu.dominio.DominioSituacaoRequisicao;
import br.gov.mec.aghu.estoque.vo.MaterialOpmeVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcGrupoAlcadaAvalOpms;
import br.gov.mec.aghu.model.MbcItensRequisicaoOpmes;
import br.gov.mec.aghu.model.MbcRequisicaoOpmes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

@SuppressWarnings({"PMD.ExcessiveClassLength"})
public class CadastroPlanejamentoPacienteAgendaOPMESController extends ActionController {

	private static final String CAMPO_OBRIGATORIO = "CAMPO_OBRIGATORIO";

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	private static final Log LOG = LogFactory.getLog(CadastroPlanejamentoPacienteAgendaOPMESController.class);
	private static final long serialVersionUID = 4254219625997598246L;

	@EJB
	private IBlocoCirurgicoOpmesFacade blocoCirurgicoOpmesFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;

	@EJB
	private IServidorLogadoFacade servidorFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IComprasFacade comprasFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IAghuFacade aghuFacade;

	@Inject
	private CadastroPlanejamentoPacienteAgendaController principalController;

	private AghParametros parametro;

	private ScoMaterial scoMaterial;
	
	private MaterialOpmeVO materialSuggestion;
	
	private Integer qtdeSolicitada;

	private List<MbcOpmesVO> listaPesquisada;

	private List<MbcOpmesVO> listaPesquisadaClone;

	private String situacaoProcessoAutorizacao;
	private Double totalCompativel;
	private Double totalIncompativel;
	private String incompatibilidadesEncontrada;
	private String materiaisNovos;
	private FatItensProcedHospitalar procedimentoSus;
	private MbcRequisicaoOpmes requisicaoOpmes;
	private MbcRequisicaoOpmes requisicaoOpmesOld;
	private MbcOpmesVO opmeVO;
	private Boolean abreJustificativa = Boolean.FALSE;
	private Boolean materialNovoObrigatorio = Boolean.FALSE;
	private Boolean hideModalMateriaisNovos = Boolean.FALSE;
	private Boolean abreObservacao = Boolean.FALSE;
	private Boolean sliderAberta = Boolean.TRUE;
	private Boolean modalExclusao = Boolean.FALSE;
	private Boolean abreConfirmacaoCancelamento = Boolean.FALSE;
	private Boolean autorizada = Boolean.FALSE;
	private MbcOpmesVO voExcluir = null;
	private Boolean desabilitaProcedimentoSus = Boolean.FALSE;
	private Boolean renderizaProcedimentoSus = Boolean.FALSE;
	private List<MbcItensRequisicaoOpmes> listaExcluidos;
	private String justificativaAnterior = "";
	private String observacaoAnterior = "";
	private AghUnidadesFuncionais unidadeFuncional = null;
	private List<GrupoExcludenteVO> listaGrupoExcludente;
	private CadastroPlanejamentoVO cadastroPlanejamentoVO = null;
	private boolean gravar = false;
	private Boolean houveItemSelecionado = Boolean.FALSE;
	Boolean convenioValido = true;
	private Boolean procedimentoEmEscala = Boolean.FALSE;
	private Boolean visualizarJustificativa = Boolean.FALSE;
	private Boolean habilitarJustificativa = Boolean.FALSE;
	private Boolean comErro;
	private Boolean edicao;
	private Boolean alterouOPME;
	private Boolean habilitarOutrosMateriais;
	public void inicio(CadastroPlanejamentoPacienteAgendaController principal) {
		habilitarOutrosMateriais = true;
		edicao = false;
		alterouOPME = false;
		this.principalController = principal;
		if (listaPesquisada == null) {
			sliderAberta = Boolean.TRUE;
			listaPesquisada = new ArrayList<MbcOpmesVO>();
			listaPesquisadaClone = new ArrayList<MbcOpmesVO>();
		}
		if (requisicaoOpmes == null) {
			requisicaoOpmes = new MbcRequisicaoOpmes();
			requisicaoOpmes.setAgendas(principalController.getAgenda());
			requisicaoOpmes.setIndConsAprovacao(false);
			requisicaoOpmes.setItensRequisicao(new ArrayList<MbcItensRequisicaoOpmes>());
		}
		if (principalController.getAgenda().getSeq() != null) {
			try {
				carregaOpme(principalController.getAgenda());
			} catch (IllegalAccessException e) {
				LOG.error("Excecao Capturada", e);
			} catch (InvocationTargetException e) {
				LOG.error("Excecao Capturada", e);
			}
		}
	}

	public void limpar(){
		comErro = true;
		desabilitaProcedimentoSus = false;
		procedimentoEmEscala = false;
		visualizarJustificativa = false;
		situacaoProcessoAutorizacao = "";
		requisicaoOpmes = null;
		requisicaoOpmesOld = null;
		listaExcluidos = null;
		listaGrupoExcludente = null;
		cadastroPlanejamentoVO = null;
		incompatibilidadesEncontrada = "";
		voExcluir = null;
		totalCompativel = null;
		totalIncompativel = null;
		opmeVO = null;
		procedimentoSus = null;
	}
	
	public List<MaterialOpmeVO> pesquisarMateriaisOrteseseProteses(String objParam) {
		BigDecimal paramVlNumerico = null;
	
		try {
			parametro = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.GRPO_MAT_ORT_PROT);
			if (parametro != null) {
				paramVlNumerico = parametro.getVlrNumerico();
			} 
			// obter materiais
			List<MaterialOpmeVO> result = comprasFacade.obterMateriaisOrteseseProtesesAgendaComValoreMarca(paramVlNumerico,(String) objParam,ScoMaterial.Fields.CODIGO.toString(),principalController.getAgenda().getDtAgenda());
			 return this.returnSGWithCount(result,result.size());
			//return result;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	
		return new ArrayList<MaterialOpmeVO>();
	}
	public Long pesquisarMateriaisOrteseseProtesesCount(String objParam) {
		BigDecimal paramVlNumerico = null;
		if (parametro != null) {
			paramVlNumerico = parametro.getVlrNumerico();
		}
		return comprasFacade.obterMateriaisOrteseseProtesesAgendaCount(paramVlNumerico, (String) objParam);
	}
	public void adicionar() {
		if (materialSuggestion == null) {
			this.apresentarMsgNegocio(Severity.ERROR, CAMPO_OBRIGATORIO,"Material");
			this.comErro = Boolean.TRUE;
			return;
		}
		if (qtdeSolicitada == null) {
			this.apresentarMsgNegocio(Severity.ERROR, CAMPO_OBRIGATORIO,"Quantidade");
			this.comErro = Boolean.TRUE;
			return;
		}
		if (scoMaterial != null && qtdeSolicitada != null) {
			this.comErro = Boolean.FALSE;
		}
		MbcItensRequisicaoOpmes item = blocoCirurgicoOpmesFacade.criaRequisicaoAdicional(requisicaoOpmes, materialSuggestion, null,qtdeSolicitada);
		MbcOpmesVO vo = blocoCirurgicoOpmesFacade.adicionarOpms(item);
		vo.setValorUnitario(materialSuggestion.getIafValorUnit());
		vo.setFilhos(new ArrayList<MbcOpmesVO>());
		vo.getFilhos().add(vo);
		listaPesquisada.add(0, vo);
		scoMaterial = null;
		materialSuggestion = null;
		qtdeSolicitada = null;
		alterouOPME = true;
		setCalculaLista();
		this.houveItemSelecionado = Boolean.TRUE;
		super.closeDialog("modalMateriaisWG");
	}
	public Boolean confirmarExclusao(MbcOpmesVO opmesVO) {
		try {
			modalExclusao = blocoCirurgicoOpmesFacade.validaExclusao(opmesVO);
			voExcluir = opmesVO;
			this.houveItemSelecionado = Boolean.TRUE;
			return modalExclusao;
		} catch (ApplicationBusinessException e) {
			modalExclusao = Boolean.FALSE;
			apresentarExcecaoNegocio(e);
			return modalExclusao;
		}
	}
	// CASO NA HORA DE CANCELAR SEJA NECESSAŔIO MANTER OS DADOS, CRIAR UM BOOLEANO PARA PASSAR NO INICIO
	public void cancelarOpmes() throws IllegalAccessException,
			InvocationTargetException {
		if (this.getRequisicaoOpmes().getFluxo() == null) {
			try {
				blocoCirurgicoOpmesFacade.cancelarOpmeSemFluxo(requisicaoOpmes);
				apresentarMsgNegocio(Severity.INFO, "MSG_CANCELAMENTO_REQ_SUCESSO");
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
			inicio(principalController);
		} else {
			abreConfirmacaoCancelamento = Boolean.TRUE;
			super.openDialog("modalCancelarRequisicaoWG");
		}
	}

	public void setCancelarExclusao() {
		modalExclusao = Boolean.FALSE;
	}

	public void setCancelarCancelarRequisicao() {
		abreConfirmacaoCancelamento = Boolean.FALSE;
	}

	public void setOutrosMateriais() {
		scoMaterial = null;
		qtdeSolicitada = null;
		habilitarOutrosMateriais = false;
	}

	public void setCancelarRequisicao() {
		try {
			if(requisicaoOpmes.getSeq() != null){
				blocoCirurgicoOpmesFacade.validaRequisicaoEscala(requisicaoOpmes);
			}
			if (requisicaoOpmes.getFluxo() != null) {
				blocoCirurgicoOpmesFacade.cancelarFluxoAutorizacaoOPMEs(servidorFacade.obterServidorLogado(),requisicaoOpmes.getFluxo(), "", requisicaoOpmes);
			}
			inicio(principalController);
			setReiniciaRequisicao();
			// Conteúdo do método setExcluiItens() incluído aqui, devido a
			// violação de PMD por execesso de métodos.
			if (requisicaoOpmes.getSeq() != null) {
				if (listaExcluidos == null) {
					listaExcluidos = new ArrayList<MbcItensRequisicaoOpmes>();
				}
				listaExcluidos.addAll(requisicaoOpmes.getItensRequisicao());
			}
			abreConfirmacaoCancelamento = Boolean.FALSE;
			super.closeDialog("modalCancelarRequisicaoWG");

			listaPesquisada = new ArrayList<MbcOpmesVO>();

			// Cria uma nova requisao
			requisicaoOpmes = new MbcRequisicaoOpmes();
			requisicaoOpmes.setAgendas(principalController.getAgenda());
			requisicaoOpmes.setIndConsAprovacao(false);
			requisicaoOpmes.setItensRequisicao(new ArrayList<MbcItensRequisicaoOpmes>());

			requisicaoOpmesOld = new MbcRequisicaoOpmes();

			criaLista();
			apresentarMsgNegocio(Severity.INFO, "MSG_CANCELAMENTO_REQ_SUCESSO");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

	}

	public void excluir(MbcOpmesVO opmesVO) {
		try {
			modalExclusao = blocoCirurgicoOpmesFacade.validaExclusao(opmesVO);
			voExcluir = opmesVO;
			if (modalExclusao) {
				listaPesquisada = blocoCirurgicoOpmesFacade.excluir(requisicaoOpmes, voExcluir, listaPesquisada);
				if (listaExcluidos != null) {
					listaExcluidos.add(voExcluir.getItensRequisicaoOpmes());
				}
			}
			this.houveItemSelecionado = Boolean.TRUE;
			modalExclusao = Boolean.FALSE;
			this.apresentarMsgNegocio(Severity.INFO, "MSG_SUCESSO_EXLC_MAT");
			alterouOPME = false;
			setCalculaLista();
		} catch (ApplicationBusinessException e) {
			modalExclusao = Boolean.FALSE;
			apresentarExcecaoNegocio(e);
		}
	}

	public void editarJustificativas() {
		abreJustificativa = Boolean.TRUE;
		justificativaAnterior = requisicaoOpmes.getJustificativaRequisicaoOpme();
		super.openDialog("modalJustificativaWG");
	}

	public void materiaisNovos() {
		materiaisNovos = "";
		materialNovoObrigatorio = Boolean.TRUE;
		hideModalMateriaisNovos = Boolean.FALSE;
	}

	public void setValidaPrazo() throws ApplicationBusinessException {
		convenioValido = true;
	}

	public MbcRequisicaoOpmes concluirOpmes() throws ParseException,ApplicationBusinessException, IllegalAccessException,InvocationTargetException {
		abreJustificativa = Boolean.FALSE;
		materialNovoObrigatorio = Boolean.FALSE;
		hideModalMateriaisNovos = Boolean.FALSE;
		abreObservacao = Boolean.FALSE;
		sliderAberta = Boolean.TRUE;
		modalExclusao = Boolean.FALSE;
		voExcluir = null;
		if (unidadeFuncional == null) {
			unidadeFuncional = this.aghuFacade.obterUnidadeFuncional(principalController.getSeqUnidFuncionalCirugica());
		}
		return requisicaoOpmes;
	}

	public boolean validaJustificativa() {
		List<FatItensProcedHospitalar> listaProcedSus = pesquisarProcedimentoSus(null);
		if(listaProcedSus != null){
			if(listaProcedSus.size()> 0){
				if(requisicaoOpmes.getIndCompativel() != null){
					if (!requisicaoOpmes.getIndCompativel() && (requisicaoOpmes.getJustificativaRequisicaoOpme() == null || requisicaoOpmes.getJustificativaRequisicaoOpme().isEmpty())) {
						abreJustificativa = Boolean.TRUE;
						justificativaAnterior = requisicaoOpmes.getJustificativaRequisicaoOpme();
						super.openDialog("modalJustificativaWG");
						return false;
					}
				}
			}
		}
		return true;
	}

	public void gravarMaterialNovo() {
		if (materiaisNovos == null || materiaisNovos.isEmpty()) {
			this.apresentarMsgNegocio(Severity.ERROR, CAMPO_OBRIGATORIO,"Materiais Novos");
			return;
		}
		MbcItensRequisicaoOpmes item = blocoCirurgicoOpmesFacade.criaRequisicaoAdicional(requisicaoOpmes, null, materiaisNovos,1);
		MbcOpmesVO vo = blocoCirurgicoOpmesFacade.adicionarOpms(item);
		vo.setFilhos(new ArrayList<MbcOpmesVO>());
		vo.getFilhos().add(vo);
		listaPesquisada.add(0, vo);
		setCalculaLista();
		materialNovoObrigatorio = Boolean.FALSE;
		hideModalMateriaisNovos = Boolean.TRUE;
		this.houveItemSelecionado = Boolean.TRUE;
		
		alterouOPME = true;
		
		super.closeDialog("modalMateriaisNovosWG");
	}

	public boolean isProcedimentoSusPreenchido() {
		setCadastroPlanejamentoVO();
		 List<MbcGrupoAlcadaAvalOpms> alcadas = this.blocoCirurgicoFacade.listarGrupoAlcadaFiltro(null,principalController.getAgenda().getEspecialidade(), null, null, null,DominioSituacao.A);
	    // Não Obriga se não tiver grupoAlcada
	    if(alcadas == null || alcadas.size() == 0){
	    	return true;
	    }
		List<FatItensProcedHospitalar> listaProcedSus = pesquisarProcedimentoSus(null);
		
		//Não Obriga se não tiver o que selecionar
		if(listaProcedSus != null){
			if(listaProcedSus.size()> 0){
				//if (blocoCirurgicoOpmesFacade.verificarConvenio(principalController.getAgenda()) && procedimentoSus == null) {
				if (procedimentoSus == null) {	
					this.apresentarMsgNegocio(Severity.ERROR, CAMPO_OBRIGATORIO, " Procedimento SUS");
					return false;
				}
			}
		}
		return true;
	}

	public String gravarJustificativa() throws ParseException, IllegalAccessException, InvocationTargetException {
		String retorno = null;
		if (requisicaoOpmes.getJustificativaRequisicaoOpme() == null|| requisicaoOpmes.getJustificativaRequisicaoOpme().isEmpty()) {
			this.apresentarMsgNegocio(Severity.ERROR, CAMPO_OBRIGATORIO," Justificativa");
			return retorno;
		}
		try {
			justificativaAnterior = requisicaoOpmes.getJustificativaRequisicaoOpme();
			if (gravar) {
				principalController.setLimpaOpme(false);
				concluirOpmes();
				retorno = principalController.gravar();
				if(retorno != null){
					limpar();
				}
			}
			abreJustificativa = Boolean.FALSE;
			
			super.closeDialog("modalJustificativaWG");

			return retorno;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	public Boolean isRequisicaoFinalizada() {
		if (blocoCirurgicoOpmesFacade.isRequisicaoFinalizada(requisicaoOpmes)) {
			apresentarMsgNegocio(Severity.ERROR, "MSG_ERRO_REQ_FINALIZADA");
			return true;
		}
		return false;
	}

	public Boolean isRequisicaoAndamento() {
		if (blocoCirurgicoOpmesFacade.isRequisicaoAndamento(requisicaoOpmes)) {
			apresentarMsgNegocio(Severity.ERROR, "MSG_ERRO_REQ_ANDAMENTO",situacaoProcessoAutorizacao);
			return true;
		}
		return false;
	}

	public void cancelarJustificativa() {
		requisicaoOpmes.setJustificativaRequisicaoOpme(justificativaAnterior);
		try {
			blocoCirurgicoOpmesFacade.validaJustificativa(requisicaoOpmes);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		abreJustificativa = Boolean.FALSE;
	}
	public void cancelarMaterialNovo() {
		materialNovoObrigatorio = Boolean.FALSE;
	}
	public void setConfirmaObservacao() {
		abreObservacao = Boolean.FALSE;
	}
	public void setEditarObservacao() {
		observacaoAnterior = requisicaoOpmes.getObservacaoOpme();
		abreObservacao = Boolean.TRUE;
	}
	public void setCancelarObservacao() {
		requisicaoOpmes.setObservacaoOpme(observacaoAnterior);
		abreObservacao = Boolean.FALSE;
	}
	public void alteraQuantidadeDigitada(MbcOpmesVO vo) {
		Integer codigoRequerido = vo.getItensRequisicaoOpmes().getRequerido().getCodigo();
		if ((codigoRequerido == DominioRequeridoItemRequisicao.NOV.getCodigo() || codigoRequerido == DominioRequeridoItemRequisicao.ADC.getCodigo()) && (vo.getQtdeSolicitadaMaterial() == null || vo.getQtdeSolicitadaMaterial() == 0)) {
			apresentarMsgNegocio(Severity.INFO, "MSG_QTD_SOL_MIN");
			this.comErro = Boolean.TRUE;
			if(vo.getQtdeSolicitadaMaterial() == null || vo.getQtdeSolicitadaMaterial() == 0){
				vo.setQtdeSolicitadaMaterial(1);
				vo.setQtdeSol(1);
			}
		} else {
			if(vo.getQtdeSolicitadaMaterial() == null){
				vo.setQtdeSolicitadaMaterial(0);
			}
			blocoCirurgicoOpmesFacade.solicitaMaterial(vo, 0, listaPesquisada,procedimentoSus, listaGrupoExcludente);
			setCalculaLista();
			this.houveItemSelecionado = Boolean.TRUE;
			alterouOPME = true;
		}
		RequestContext.getCurrentInstance().execute("bindInput()");
	}
	public void somaQuantidade(MbcOpmesVO vo) {
		blocoCirurgicoOpmesFacade.solicitaMaterial(vo, 1, listaPesquisada,procedimentoSus, listaGrupoExcludente);
		setCalculaLista();
		this.houveItemSelecionado = Boolean.TRUE;
		alterouOPME = true;
		RequestContext.getCurrentInstance().execute("bindInput()");
	}
	public void subtraiQuantidade(MbcOpmesVO vo) {
		Integer codigoRequerido = vo.getItensRequisicaoOpmes().getRequerido().getCodigo();
		if ((codigoRequerido == DominioRequeridoItemRequisicao.NOV.getCodigo() || codigoRequerido == DominioRequeridoItemRequisicao.ADC.getCodigo()) && vo.getQtdeSol() == 1) {
			apresentarMsgNegocio(Severity.INFO, "MSG_QTD_SOL_MIN");
			this.comErro = Boolean.TRUE;
		} else {
			blocoCirurgicoOpmesFacade.solicitaMaterial(vo, -1, listaPesquisada,procedimentoSus, listaGrupoExcludente);
			setCalculaLista();
			this.houveItemSelecionado = Boolean.TRUE;
			alterouOPME = true;
		}
		RequestContext.getCurrentInstance().execute("bindInput()");
	}

	public void calculaQuantidade(ValueChangeEvent event) {
		blocoCirurgicoOpmesFacade.calculaQuantidade(opmeVO);
	}
	public List<FatItensProcedHospitalar> pesquisarProcedimentoSus(String objParam) {
		return blocoCirurgicoOpmesFacade.consultarProcedimentoSUSVinculadoProcedimentoInterno(principalController.getProcedimento().getId().getPciSeq(), objParam);
	}
	public Integer pesquisarProcedimentoSusCount(String objParam) {
		return 0;
	}
	public void limpaLista() {
		sliderAberta = Boolean.TRUE;
		listaPesquisada = new ArrayList<MbcOpmesVO>();
		setCalculaLista();
	}
	public void criaLista() {
		setExcluiItens();
		sliderAberta = Boolean.FALSE;
		MbcOpmesListaGrupoExcludente voTransporte = blocoCirurgicoOpmesFacade.consultaItensProcedimento(principalController.getProcedimento().getId().getPciSeq(),procedimentoSus, requisicaoOpmes,principalController.getAgenda().getDtAgenda());
		listaPesquisada = voTransporte.getListaPesquisada();
		listaGrupoExcludente = voTransporte.getListaGrupoExcludente();
		if(listaPesquisada.isEmpty()){
			requisicaoOpmes.setItensRequisicao(new ArrayList<MbcItensRequisicaoOpmes>());
		}
		setCalculaLista();
		setReiniciaRequisicao();
	}
	public void setReiniciaRequisicao() {
		requisicaoOpmes.setSituacao(DominioSituacaoRequisicao.COMPATIVEL);
		requisicaoOpmes.setJustificativaRequisicaoOpme("");
		requisicaoOpmes.setObservacaoOpme("");
	}
	private void setExcluiItens() {
		if (requisicaoOpmes != null && requisicaoOpmes.getSeq() != null) {
			if (listaExcluidos == null) {
				listaExcluidos = new ArrayList<MbcItensRequisicaoOpmes>();
			}
			listaExcluidos.addAll(requisicaoOpmes.getItensRequisicao());
		}
	}
	private void setCalculaLista() {
		blocoCirurgicoOpmesFacade.calculaQuantidades(listaPesquisada);
		totalCompativel = blocoCirurgicoOpmesFacade.atualizaTotalCompativel(listaPesquisada);
		StringBuffer incompatibilidadesEncontradas = new StringBuffer();
		totalIncompativel = blocoCirurgicoOpmesFacade.atualizaIncompativel(requisicaoOpmes,incompatibilidadesEncontradas, listaPesquisada);
		incompatibilidadesEncontrada = incompatibilidadesEncontradas.toString();
		blocoCirurgicoOpmesFacade.setCompatibilidadeGrupoExcludencia(listaPesquisada);
		blocoCirurgicoOpmesFacade.atualizaSituacao(requisicaoOpmes);
	}
	private void setCadastroPlanejamentoVO() {
		if (cadastroPlanejamentoVO == null) {
			cadastroPlanejamentoVO = new CadastroPlanejamentoVO();
		}
		cadastroPlanejamentoVO.setPaciente(principalController.getPaciente());
		cadastroPlanejamentoVO.setFatConvenioSaude(principalController.getAgenda().getFatConvenioSaude());
		cadastroPlanejamentoVO.setConvenioSaudePlano(principalController.getAgenda().getConvenioSaudePlano());
	}
	public void carregaOpme(MbcAgendas agenda) throws IllegalAccessException, InvocationTargetException {
		setProcedimentoSus(agenda.getItensProcedHospitalar());
		requisicaoOpmes = blocoCirurgicoOpmesFacade.carregaRequisicao(agenda);

		if(requisicaoOpmes != null){
			edicao = true;
			requisicaoOpmesOld = blocoCirurgicoOpmesFacade.copiaRequisicao(requisicaoOpmes);
	
			// invertido significado do teste?
			desabilitaProcedimentoSus = blocoCirurgicoOpmesFacade.permiteAlteracaoRequisicao(requisicaoOpmes);
	
			procedimentoEmEscala = blocoCirurgicoOpmesFacade.isRequisicaoEscalada(requisicaoOpmes);
	
			visualizarJustificativa = blocoCirurgicoOpmesFacade.isRequisicaoOpmeIncompativel(requisicaoOpmes);
			habilitarJustificativa = blocoCirurgicoOpmesFacade.isRequisicaoOpmeIncompativelSemEscala(requisicaoOpmes);
	
			setRenderizaProcedimentoSus();
	
			situacaoProcessoAutorizacao = blocoCirurgicoOpmesFacade.getEtapaAutorizacao(requisicaoOpmes);
			// setCadastroPlanejamentoVO();
			if (cadastroPlanejamentoVO == null) {
				cadastroPlanejamentoVO = new CadastroPlanejamentoVO();
			}
			cadastroPlanejamentoVO.setPaciente(principalController.getPaciente());
			cadastroPlanejamentoVO.setFatConvenioSaude(principalController.getAgenda().getFatConvenioSaude());
			cadastroPlanejamentoVO.setConvenioSaudePlano(principalController.getAgenda().getConvenioSaudePlano());
	
			MbcOpmesListaGrupoExcludente voTransporte = blocoCirurgicoOpmesFacade.carregaGrid(requisicaoOpmes);
			listaPesquisada = voTransporte.getListaPesquisada();
			listaGrupoExcludente = voTransporte.getListaGrupoExcludente();
			listaExcluidos = new ArrayList<MbcItensRequisicaoOpmes>();
	
			getBindListaPesquisada();
	
			// setCalculaListaSemAtualizar();
			blocoCirurgicoOpmesFacade.calculaQuantidades(listaPesquisada);
			// Conteúdo do método setAtualizaCompIncomp() incluído aqui, devido a
			// violação de PMD por execesso de métodos.
			totalCompativel = blocoCirurgicoOpmesFacade.atualizaTotalCompativel(listaPesquisada);
			StringBuffer incompatibilidadesEncontradas = new StringBuffer();
			totalIncompativel = blocoCirurgicoOpmesFacade.atualizaIncompativel(requisicaoOpmes,incompatibilidadesEncontradas, listaPesquisada);
			incompatibilidadesEncontrada = incompatibilidadesEncontradas.toString();
			blocoCirurgicoOpmesFacade.setCompatibilidadeGrupoExcludencia(listaPesquisada);
	
			// refatorar
			List<MbcOpmesVO> listaSemRedundancia = new ArrayList<MbcOpmesVO>();
			for (MbcOpmesVO vo : listaPesquisada) {
				if (vo.getVoQuebra() == null) {
					if (vo.getFilhos().size() > 1) {
						for (MbcOpmesVO filhoVO : vo.getFilhos()) {
							if (filhoVO.getUnidadeMaterial() == null) {
								vo.getFilhos().remove(filhoVO);
								break;
							}
						}
						List<MbcOpmesVO> filhos = new ArrayList<MbcOpmesVO>();
						for (MbcOpmesVO filhoVO : vo.getFilhos()) {
							if(!filhos.contains(filhoVO)){
								filhos.add(filhoVO);
							}
						}
						vo.setFilhos(filhos);
					}
					if(vo.getFilhos() != null){
						if(!vo.getFilhos().isEmpty()){
							listaSemRedundancia.add(vo);
						}
					}
					
				}
			}
			listaPesquisada = listaSemRedundancia;
		}else{
			// MESMO NÃO TENDO REQUISICAO, SE FOR ESCALA DESABILITA PROCED SUS
			if(principalController.getAgenda().getIndSituacao() != null){
				if(DominioSituacaoAgendas.ES.equals(principalController.getAgenda().getIndSituacao())){
					procedimentoEmEscala = true;
				}
			}
			requisicaoOpmes = new MbcRequisicaoOpmes();
			requisicaoOpmes.setAgendas(principalController.getAgenda());
			requisicaoOpmes.setIndConsAprovacao(false);
			requisicaoOpmes.setItensRequisicao(new ArrayList<MbcItensRequisicaoOpmes>());
			setRenderizaProcedimentoSus();
		}
	}
	public void getBindListaPesquisada() {
		if (listaPesquisada != null && listaPesquisadaClone != null) {
			for (MbcOpmesVO vo : listaPesquisada) {
				MbcOpmesVO clone = new MbcOpmesVO();
				if (vo.getMaterial() != null) {
					clone.setCodigoMaterial(vo.getMaterial().getCodigo().intValue());
				}
				if (vo.getCodTabela() != null) {
					clone.setCodTabela(vo.getCodTabela().longValue());
				}
				clone.setItemSeq(vo.getItensRequisicaoOpmes().getSeq().shortValue());
				clone.setQtdeSol(vo.getQtdeSol().intValue());
				listaPesquisadaClone.add(clone);
			}
		}
	}
	public void setValidaRequisicaoEscala() {
		alterouOPME = true;
		if (principalController.getAgenda().getSeq() != null) {
			try {
				if(requisicaoOpmes.getSeq() != null){
					blocoCirurgicoOpmesFacade.validaRequisicaoEscala(requisicaoOpmes);
				}
			} catch (ApplicationBusinessException e) {
				this.setProcedimentoSus(principalController.getAgenda().getItensProcedHospitalar());
				apresentarExcecaoNegocio(e);
				return;
			}
		}
		criaLista();
	}
	public void setValidaRequisicaoEscalaAlteracao() {
		alterouOPME = true;
		if (principalController.getAgenda().getSeq() != null) {
			try {
				if(requisicaoOpmes.getSeq() != null){
					blocoCirurgicoOpmesFacade.validaRequisicaoEscala(requisicaoOpmes);
				}
			} catch (ApplicationBusinessException e) {
				this.setProcedimentoSus(principalController.getAgenda().getItensProcedHospitalar());
				apresentarExcecaoNegocio(e);
				return;
			}
		}
	}
	public void setRenderizaProcedimentoSus() {
		// setCadastroPlanejamentoVO();
		if (cadastroPlanejamentoVO == null) {
			cadastroPlanejamentoVO = new CadastroPlanejamentoVO();
		}
		cadastroPlanejamentoVO.setPaciente(principalController.getPaciente());
		cadastroPlanejamentoVO.setFatConvenioSaude(principalController.getAgenda().getFatConvenioSaude());
		cadastroPlanejamentoVO.setConvenioSaudePlano(principalController.getAgenda().getConvenioSaudePlano());
		
		try {
			principalController.setAgenda(blocoCirurgicoOpmesFacade.atualizarConvenio(principalController.getAgenda()));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		//renderizaProcedimentoSus = blocoCirurgicoOpmesFacade.verificarConvenio(principalController.getAgenda());
		renderizaProcedimentoSus = true;
	}
	public void iniciaProcessosOPME() {
		try {
			for (Integer seq : buscaDadosProcessoAutorizacaoOpme()) {
				try {
					blocoCirurgicoOpmesFacade.iniciarFluxoAutorizacaoOPMEs(this.getServidorLogado(),obterRequisicaoOpme(seq.shortValue()));
				} catch (BaseException e) {
					this.apresentarExcecaoNegocio(e);
				}
			}
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}
	private RapServidores getServidorLogado() throws ApplicationBusinessException{
		return this.registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(), new Date());
	}
	
	private MbcRequisicaoOpmes obterRequisicaoOpme(Short seq) {
		return this.blocoCirurgicoOpmesFacade.obterRequisicaoOpme(seq);
	}
	private List<Integer> buscaDadosProcessoAutorizacaoOpme() throws ApplicationBusinessException {
		return this.blocoCirurgicoOpmesFacade.buscaDadosProcessoAutorizacaoOpme();
	}
	public void abreFechaSlider() {
		sliderAberta = !sliderAberta;
	}
	public Integer getAgdSeq() {
		return principalController.getAgenda().getSeq();
	}
	public ScoMaterial getScoMaterial() {
		return scoMaterial;
	}
	public void setScoMaterial(ScoMaterial scoMaterial) {
		this.scoMaterial = scoMaterial;
	}
	public AghParametros getParametro() {
		return parametro;
	}
	public void setParametro(AghParametros parametro) {
		this.parametro = parametro;
	}
	public Integer getQtdeSolicitada() {
		return qtdeSolicitada;
	}
	public void setQtdeSolicitada(Integer qtdeSolicitada) {
		this.qtdeSolicitada = qtdeSolicitada;
	}
	public List<MbcOpmesVO> getListaPesquisada() {
		return listaPesquisada;
	}
	public void setListaPesquisada(List<MbcOpmesVO> listaPesquisada) {
		this.listaPesquisada = listaPesquisada;
	}
	public String getSituacaoProcessoAutorizacao() {
		return situacaoProcessoAutorizacao;
	}
	public void setSituacaoProcessoAutorizacao(String situacaoProcessoAutorizacao) {
		this.situacaoProcessoAutorizacao = situacaoProcessoAutorizacao;
	}
	public Double getTotalCompativel() {
		return totalCompativel;
	}
	public void setTotalCompativel(Double totalCompativel) {
		this.totalCompativel = totalCompativel;
	}
	public Double getTotalIncompativel() {
		return totalIncompativel;
	}
	public void setTotalIncompativel(Double totalIncompativel) {
		this.totalIncompativel = totalIncompativel;
	}
	public String getIncompatibilidadesEncontrada() {
		return incompatibilidadesEncontrada;
	}
	public void setIncompatibilidadesEncontrada(String incompatibilidadesEncontrada) {
		this.incompatibilidadesEncontrada = incompatibilidadesEncontrada;
	}
	public String getMateriaisNovos() {
		return materiaisNovos;
	}
	public void setMateriaisNovos(String materiaisNovos) {
		this.materiaisNovos = materiaisNovos;
	}
	public FatItensProcedHospitalar getProcedimentoSus() {
		return procedimentoSus;
	}
	public void setProcedimentoSus(FatItensProcedHospitalar procedimentoSus) {
		this.procedimentoSus = procedimentoSus;
	}
	public MbcOpmesVO getOpmeVO() {
		return opmeVO;
	}
	public void setOpmeVO(MbcOpmesVO opmeVO) {
		this.opmeVO = opmeVO;
	}
	public MbcRequisicaoOpmes getRequisicaoOpmes() {
		return requisicaoOpmes;
	}
	public void setRequisicaoOpmes(MbcRequisicaoOpmes requisicaoOpmes) {
		this.requisicaoOpmes = requisicaoOpmes;
	}
	public Boolean getAbreJustificativa() {
		return abreJustificativa;
	}
	public void setAbreJustificativa(Boolean abreJustificativa) {
		this.abreJustificativa = abreJustificativa;
	}
	public Boolean getMaterialNovoObrigatorio() {
		return materialNovoObrigatorio;
	}
	public void setMaterialNovoObrigatorio(Boolean materialNovoObrigatorio) {
		this.materialNovoObrigatorio = materialNovoObrigatorio;
	}
	public Boolean getHideModalMateriaisNovos() {
		return hideModalMateriaisNovos;
	}
	public void setHideModalMateriaisNovos(Boolean hideModalMateriaisNovos) {
		this.hideModalMateriaisNovos = hideModalMateriaisNovos;
	}
	public Boolean getSliderAberta() {
		return sliderAberta;
	}
	public void setSliderAberta(Boolean sliderAberta) {
		this.sliderAberta = sliderAberta;
	}
	public Boolean getModalExclusao() {
		return modalExclusao;
	}
	public void setModalExclusao(Boolean modalExclusao) {
		this.modalExclusao = modalExclusao;
	}
	public List<MbcItensRequisicaoOpmes> getListaExcluidos() {
		return listaExcluidos;
	}
	public void setListaExcluidos(List<MbcItensRequisicaoOpmes> listaExcluidos) {
		this.listaExcluidos = listaExcluidos;
	}
	public Boolean getAbreObservacao() {
		return abreObservacao;
	}
	public void setAbreObservacao(Boolean abreObservacao) {
		this.abreObservacao = abreObservacao;
	}
	public MbcOpmesVO getVoExcluir() {
		return voExcluir;
	}
	public void setVoExcluir(MbcOpmesVO voExcluir) {
		this.voExcluir = voExcluir;
	}
	public Boolean getAbreConfirmacaoCancelamento() {
		return abreConfirmacaoCancelamento;
	}
	public void setAbreConfirmacaoCancelamento(Boolean abreConfirmacaoCancelamento) {
		this.abreConfirmacaoCancelamento = abreConfirmacaoCancelamento;
	}
	public Boolean getAutorizada() {
		return autorizada;
	}
	public void setAutorizada(Boolean autorizada) {
		this.autorizada = autorizada;
	}
	public boolean isGravar() {
		return gravar;
	}
	public void setGravar(boolean gravar) {
		this.gravar = gravar;
	}
	public Boolean getDesabilitaProcedimentoSus() {
		return desabilitaProcedimentoSus;
	}
	public void setDesabilitaProcedimentoSus(Boolean desabilitaProcedimentoSus) {
		this.desabilitaProcedimentoSus = desabilitaProcedimentoSus;
	}
	public List<MbcOpmesVO> getListaPesquisadaClone() {
		return listaPesquisadaClone;
	}
	public void setListaPesquisadaClone(List<MbcOpmesVO> listaPesquisadaClone) {
		this.listaPesquisadaClone = listaPesquisadaClone;
	}
	public Boolean getRenderizaProcedimentoSus() {
		return renderizaProcedimentoSus;
	}
	public void setRenderizaProcedimentoSus(Boolean renderizaProcedimentoSus) {
		this.renderizaProcedimentoSus = renderizaProcedimentoSus;
	}
	public Boolean getHouveItemSelecionado() {
		return houveItemSelecionado;
	}
	public void setHouveItemSelecionado(Boolean houveItemSelecionado) {
		this.houveItemSelecionado = houveItemSelecionado;
	}
	public Boolean getProcedimentoEmEscala() {
		return procedimentoEmEscala;
	}
	public void setProcedimentoEmEscala(Boolean procedimentoEmEscala) {
		this.procedimentoEmEscala = procedimentoEmEscala;
	}
	public Boolean getComErro() {
		return comErro;
	}
	public void setComErro(Boolean comErro) {
		this.comErro = comErro;
	}
	public Boolean getVisualizarJustificativa() {
		return visualizarJustificativa;
	}
	public void setVisualizarJustificativa(Boolean visualizarJustificativa) {
		this.visualizarJustificativa = visualizarJustificativa;
	}
	public Boolean getHabilitarJustificativa() {
		return habilitarJustificativa;
	}
	public void setHabilitarJustificativa(Boolean habilitarJustificativa) {
		this.habilitarJustificativa = habilitarJustificativa;
	}
	public MbcRequisicaoOpmes getRequisicaoOpmesOld() {
		return requisicaoOpmesOld;
	}
	public void setRequisicaoOpmesOld(MbcRequisicaoOpmes requisicaoOpmesOld) {
		this.requisicaoOpmesOld = requisicaoOpmesOld;
	}
	public Boolean getEdicao() {
		return edicao;
	}
	public void setEdicao(Boolean edicao) {
		this.edicao = edicao;
	}
	public Boolean getAlterouOPME() {
		return alterouOPME;
	}
	public void setAlterouOPME(Boolean alterouOPME) {
		this.alterouOPME = alterouOPME;
	}
	public MaterialOpmeVO getMaterialSuggestion() {
		return materialSuggestion;
	}
	public void setMaterialSuggestion(MaterialOpmeVO materialSuggestion) {
		this.materialSuggestion = materialSuggestion;
	}
	public Boolean getHabilitarOutrosMateriais() {	
		return habilitarOutrosMateriais;
	}
	public void setHabilitarOutrosMateriais(Boolean habilitarOutrosMateriais) {	
		this.habilitarOutrosMateriais = habilitarOutrosMateriais;
	}	
}
