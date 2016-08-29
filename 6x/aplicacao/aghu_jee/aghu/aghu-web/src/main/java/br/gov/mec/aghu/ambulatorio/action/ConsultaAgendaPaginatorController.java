package br.gov.mec.aghu.ambulatorio.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.ambulatorio.pesquisa.vo.ConsultasAgendaVO;
import br.gov.mec.aghu.ambulatorio.vo.ArquivosEsusVO;
import br.gov.mec.aghu.ambulatorio.vo.VAacSiglaUnfSalaVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioDiaSemana;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.faturamento.vo.FatProcedHospInternosVO;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.AacCondicaoAtendimento;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacPagador;
import br.gov.mec.aghu.model.AacRetornos;
import br.gov.mec.aghu.model.AacSituacaoConsultas;
import br.gov.mec.aghu.model.AacTipoAgendamento;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AipCidades;
import br.gov.mec.aghu.model.AipEnderecosPacientes;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;

/**
 * Controller da tela de Pesquisar Consulta/Agenda
 *  
 *  @author georgenes.zapalaglio
 * 
 */
public class ConsultaAgendaPaginatorController extends ActionController implements ActionPaginator {

	private static final String AAC_00145 = "AAC_00145";

	private static final long serialVersionUID = -3744812806893136737L;
	
	private static final Log LOG = LogFactory.getLog(ConsultaAgendaPaginatorController.class);
	
	private static final Integer MAX_LINHAS_RETORNADAS = 500000;
	
	@Inject @Paginator
	private DynamicDataModel<ConsultasAgendaVO> dataModel;

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	@EJB
	private IAghuFacade aghuFacade;
	@EJB
	private IParametroFacade parametroFacade;
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	@EJB
	protected ICentroCustoFacade centroCustoFacade;
	
	private List<AacConsultas> consultas;
	
	// FILTRO
	private FccCentroCustos servico;
	private Integer grdSeq;
	private VAacSiglaUnfSalaVO siglaUnfSalaVO;
	private AghEspecialidades especialidade;
	private AghEquipes equipe;
	private AacPagador pagador;
	private AacTipoAgendamento autorizacao;
	private AacCondicaoAtendimento condicao;
	private AacSituacaoConsultas situacao;
	private DominioDiaSemana diaSemana;
	private Date horaConsulta;
	private Date dtInicio;
	private Date dtFim;	
	private RapServidores profissional;
	private AacRetornos retorno;
	private boolean exibirPopupConfirmacao = false;
	private FatProcedHospInternosVO procedimento;
	
	private List<ConsultasAgendaVO> consultaList;
	
	// Lista de VOs para suggestion da zona
	private List<VAacSiglaUnfSalaVO> listaZonaVO = new ArrayList<VAacSiglaUnfSalaVO>();
	
	// Labels parametrizados
	private String labelZona;
	private String labelZonaSala;
	private String titleZona;
	
	private String fileName;
	
	private StreamedContent streamedContent;
	
	private static final String MODAL_CONFIRMACAO_MAX_REGISTROS="modalConfirmacaoMaxRegistrosWG";
	
	private List<AacConsultas> consultasRaas;
	
	private List<AacConsultas> consultasEsus;
	
	private ArquivosEsusVO arquivosEsusVO;
	
	
	
	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}
	
	/**
	 * Método executado ao iniciar a controller
	 */
	public void iniciar() {

		obterPagadoresComAgendamento();
		obterAutorizacoesAtivas();
		obterCondicaoAtentimentoAtivas();
		obterSituacao();
		try {
			labelZona = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_LABEL_ZONA).getVlrTexto();
			labelZonaSala = labelZona + "/" +  parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_LABEL_SALA).getVlrTexto();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error("Erro ao buscar parâmetro", e);
			this.labelZona = "zona";
			this.labelZonaSala = "zona/sala";
		}
		
		String message = "";//getMessages().get("TITLE_PESQUISAR_CONSULTAS_AGENDA_ZONASALA");
		this.titleZona = MessageFormat.format(message, this.labelZona);
	}
	
	

	/**
	 * Método executado ao clicar no botão limpar
	 */	
	public void limparPesquisa() {
		servico = null;
		grdSeq = null;
		siglaUnfSalaVO = null;
		especialidade = null;
		equipe = null;
		pagador = null;
		autorizacao = null;
		condicao = null;
		situacao = null;
		diaSemana = null;
		horaConsulta = null;
		dtInicio = null;
		dtFim = null;
		profissional  = null;
		retorno = null;
		procedimento = null;
		consultaList=new ArrayList<ConsultasAgendaVO>();
		dataModel.setPesquisaAtiva(false);
		//this.ativo = false;		
	}
	

	public void pesquisar() {
		
		if (dtFim!=null && dtFim.before(dtInicio)){
			apresentarMsgNegocio(Severity.ERROR, AAC_00145);
			return;
		}		
		dataModel.reiniciarPaginator();		
	}
	
	
	@Override
	public Long recuperarCount() {
		Short unfSeq = null;		
		if (siglaUnfSalaVO != null) {
			unfSeq = siglaUnfSalaVO.getUnfSeq(); 
		}
		
		Long count = ambulatorioFacade.listarConsultasAgendaCount(servico, grdSeq, unfSeq, especialidade,
					equipe, pagador, autorizacao, condicao, situacao, diaSemana, horaConsulta, dtInicio,
					dtFim, profissional, retorno, procedimento);
		
		return count!=null?count : 0L;
	}	


	@Override
	public List<ConsultasAgendaVO> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		
		Short unfSeq = null;		
		if (siglaUnfSalaVO != null) {
			unfSeq = siglaUnfSalaVO.getUnfSeq(); 
		}
		
		consultaList = ambulatorioFacade.listarConsultasAgendaScrooler(
					servico, grdSeq, unfSeq, especialidade,
					equipe, pagador, autorizacao, condicao, situacao, diaSemana, horaConsulta, dtInicio,
					dtFim, profissional, retorno, firstResult, maxResult, procedimento);
		return consultaList;
	}
	
	
	/**
	 * Obtém o município e a sigla do UF do endereço de cada paciente
	 * @param enderecoPadrao
	 * @return
	 */
	public String obterMunicipioPaciente(AipPacientes paciente){
		String strEndereco = "";
		if(paciente != null) {
			AipEnderecosPacientes enderecoPadrao = paciente.getEnderecoPadrao();
			if (enderecoPadrao != null){
				if (enderecoPadrao.getAipBairrosCepLogradouro() != null){
					AipCidades municipio = enderecoPadrao.getAipBairrosCepLogradouro().getCepLogradouro().getLogradouro().getAipCidade();
					strEndereco = municipio.getNome() + "/" + municipio.getAipUf().getSigla();
				}
				else{
					if (enderecoPadrao.getAipCidade() != null){
						AipCidades municipio = enderecoPadrao.getAipCidade();
						strEndereco = municipio.getNome() + "/" + municipio.getAipUf().getSigla();
					} 
				}			
			}
		}
		return strEndereco;
	}
	
	
	/**
	 * Método executado ao clicar no botão Gerar Arquivo
	 * @throws ApplicationBusinessException 
	 */
	//@Restrict("#{s:hasPermission('gerarArquivoConsultasAgenda','gerar')}")
	public void gerarArquivo() throws NumberFormatException, IOException, ApplicationBusinessException{
		if (dtFim!=null && dtFim.before(dtInicio)){
			apresentarMsgNegocio(Severity.ERROR, AAC_00145);
			return;
		}	
		
		//#38403 - Descomentar o código abaixo para habilitar a exibição da popup caso retorne mais de 500000 linhas
//		if(!exibirPopupConfirmacao && recuperarCount() > MAX_LINHAS_RETORNADAS){
//			exibirPopupConfirmacao = true;
//			this.openDialog(MODAL_CONFIRMACAO_MAX_REGISTROS);
//			return;
//		}else {
//			exibirPopupConfirmacao = false;
//			this.closeDialog(MODAL_CONFIRMACAO_MAX_REGISTROS);
//		}
		
		Short unfSeq = null;
		if (siglaUnfSalaVO != null) {
			unfSeq = siglaUnfSalaVO.getUnfSeq(); 
		}
		
		File file;
		try {
			file = ambulatorioFacade.geraArquivoConsulta(servico, grdSeq, unfSeq, especialidade,
					equipe, pagador, autorizacao, condicao, situacao, diaSemana, horaConsulta, dtInicio,
					dtFim, profissional, retorno, procedimento);
			this.apresentarMsgNegocio(Severity.INFO, "MSG_INFO_ARQ_GERADO_SUCESSO");
			this.streamedContent = new DefaultStreamedContent(new FileInputStream(file),"text/csv", file.getName());
		}catch(ApplicationBusinessException e) {
			LOG.error(e.getMessage(),e);
			apresentarExcecaoNegocio(e);
			return;
		}
	}
	public void carregarArquivoRAAS() throws IOException{
		Short unfSeq = null;
		if (siglaUnfSalaVO != null) {
			unfSeq = siglaUnfSalaVO.getUnfSeq(); 
		}
		try {
			ambulatorioFacade.verificarUnidadeCapsRetornoAtendido(unfSeq, retorno);
			consultasRaas = ambulatorioFacade.carregarArquivoRass(servico, grdSeq, unfSeq, especialidade,
						equipe, pagador, autorizacao, condicao, situacao, diaSemana, horaConsulta, dtInicio,
						dtFim, profissional, retorno);
			this.apresentarMsgNegocio(Severity.INFO, "MSG_RAAS_CARREGADO");
		}catch(ApplicationBusinessException e) {
			LOG.error(e.getMessage(),e);
			apresentarExcecaoNegocio(e);
			return;
		}
	}
	
	/**
	 * Método que gera o arquivo do RASS
	 * @throws IOException 
	 */
	public void gerarArquivoRASS() throws IOException{
		if (consultasRaas == null || consultasRaas.isEmpty()){
			this.apresentarMsgNegocio(Severity.ERROR, "MSG_CARREGAR_RAAS");
		}
		else{
			if (dtFim!=null && dtFim.before(dtInicio)){
				apresentarMsgNegocio(Severity.ERROR, AAC_00145);
				return;
			}	
			
			//#38403 - Descomentar o codigo abaixo para habilitar a exibicao da popup caso retorne mais de 500000 linhas
			if(!exibirPopupConfirmacao && recuperarCount() > MAX_LINHAS_RETORNADAS){
				exibirPopupConfirmacao = true;
				this.openDialog(MODAL_CONFIRMACAO_MAX_REGISTROS);
				return;
			}else {
				exibirPopupConfirmacao = false;
				this.closeDialog(MODAL_CONFIRMACAO_MAX_REGISTROS);
			}
			
			Short unfSeq = null;
			if (siglaUnfSalaVO != null) {
				unfSeq = siglaUnfSalaVO.getUnfSeq(); 
			}
			
			File file;
			try {
				file = ambulatorioFacade.geraArquivoRass(consultasRaas, dtInicio, dtFim, unfSeq, retorno);
				this.apresentarMsgNegocio(Severity.INFO, "MSG_INFO_ARQ_GERADO_SUCESSO");
				this.streamedContent = new DefaultStreamedContent(new FileInputStream(file),"text/txt", file.getName());
			}catch(ApplicationBusinessException e) {
				LOG.error(e.getMessage(),e);
				apresentarExcecaoNegocio(e);
				return;
			}
		}
	}
	
	public void carregarArquivoEsus() throws IOException{
		Short unfSeq = null;
		if (siglaUnfSalaVO != null) {
			unfSeq = siglaUnfSalaVO.getUnfSeq(); 
		}
		try {
			ambulatorioFacade.verificarAgendasEsus(especialidade, retorno);
			consultasEsus = ambulatorioFacade.carregarArquivoEsus(servico, grdSeq, unfSeq, especialidade,
						equipe, pagador, autorizacao, condicao, situacao, diaSemana, horaConsulta, dtInicio,
						dtFim, profissional, retorno);
			this.apresentarMsgNegocio(Severity.INFO, "Arquivo Esus carregado");
		}catch(ApplicationBusinessException e) {
			LOG.error(e.getMessage(),e);
			apresentarExcecaoNegocio(e);
			return;
		}
	}
	
	/**
	 * Método que gera o arquivo do RASS
	 * @throws IOException 
	 */
	public void gerarArquivoEsus() throws IOException{
		if (consultasEsus == null){
			this.apresentarMsgNegocio(Severity.ERROR, "MSG_CARREGAR_ARQUIVO_ESUS");
		}
		if(consultasEsus.isEmpty()){
			this.apresentarMsgNegocio(Severity.ERROR, "MSG_NAO_HA_CONSULTAS_ESUS");
		}
		else{
			if (dtFim!=null && dtFim.before(dtInicio)){
				apresentarMsgNegocio(Severity.ERROR, AAC_00145);
				return;
			}	
			
			//#38403 - Descomentar o codigo abaixo para habilitar a exibicao da popup caso retorne mais de 500000 linhas
			if(!exibirPopupConfirmacao && recuperarCount() > MAX_LINHAS_RETORNADAS){
				exibirPopupConfirmacao = true;
				this.openDialog(MODAL_CONFIRMACAO_MAX_REGISTROS);
				return;
			}else {
				exibirPopupConfirmacao = false;
				this.closeDialog(MODAL_CONFIRMACAO_MAX_REGISTROS);
			}
			
			try {
				arquivosEsusVO =  ambulatorioFacade.gerarArquivoEsus(consultasEsus, dtInicio, dtFim, especialidade);
				this.apresentarMsgNegocio(Severity.INFO, "MSG_ARQUIVO_ESUS_SUCESSO");
				this.streamedContent = new DefaultStreamedContent(new FileInputStream(arquivosEsusVO.getArquivoEsus()),"application/zip", arquivosEsusVO.getArquivoEsus().getName());
			}catch(ApplicationBusinessException e) {
				LOG.error(e.getMessage(),e);
				apresentarExcecaoNegocio(e);
				return;
			}catch(BaseException e) {
				LOG.error(e.getMessage(),e);
				apresentarExcecaoNegocio(e);
				return;
			}
		}
	}
	
	public void gerarArquivoInconsistenciasEsus() throws IOException{
		if(consultasEsus == null){
			this.apresentarMsgNegocio(Severity.ERROR, "MSG_CARREGAR_ARQUIVO_ESUS");
		}
		if(consultasEsus.isEmpty()){
			this.apresentarMsgNegocio(Severity.ERROR, "MSG_NAO_HA_CONSULTAS_ESUS");
		}else{
			if(arquivosEsusVO == null){
				this.apresentarMsgNegocio(Severity.ERROR, "MSG_CARREGAR_ARQUIVO_ESUS");
			}else{
				this.streamedContent = new DefaultStreamedContent(new FileInputStream(this.getArquivosEsusVO().getArquivoInconsistenciasEsus()),"text/csv", this.getArquivosEsusVO().getArquivoInconsistenciasEsus().getName());
			}
		}
		
	}
	
	
	/**
	 * Método para Suggestion Serviço que estejam em Especialidade
	 */	
	public List<FccCentroCustos> obterServico(String parametro) {
		return centroCustoFacade.obterListaServicosEmEspecialidades((String) parametro);	
	}
	
	/**
	 * Método para Suggestion de Equipes
	 */	
	public List<AghEquipes> obterEquipe(String parametro) {
		return aghuFacade.getListaEquipesAtivas((String) parametro);
	}
	
	/**
	 * Método para Suggestion de Retorno de Pacientes
	 */	
	public List<AacRetornos> obterListaRetornosAtivos(String parametro) {
		return ambulatorioFacade.getListaRetornosAtivos((String) parametro);
	}

	/**
	 * Método que lista os pagadores que tem agendamento.
	 */	
	public List<AacPagador> obterPagadoresComAgendamento() {
		return ambulatorioFacade.obterListaPagadoresComAgendamento();
	}
	
	/**
	 * Método que lista autorizações ativas.
	 * @return 
	 */	
	public List<AacTipoAgendamento> obterAutorizacoesAtivas() {
		return ambulatorioFacade.obterListaAutorizacoesAtivas();
	}
	
	/**
	 * Método que lista as condicões de atendimento ativas.
	 * @return 
	 */	
	public List<AacCondicaoAtendimento> obterCondicaoAtentimentoAtivas() {
		return ambulatorioFacade.listarCondicaoAtendimento();
	}

	/**
	 * Método que lista as situações ativas.
	 * @return 
	 */	
	public List<AacSituacaoConsultas> obterSituacao() {
		return ambulatorioFacade.obterSituacoesAtivas();
	}
	
	/**
	 * Método para Suggestion Situação
	 */
	public List<AacSituacaoConsultas> obterSituacao(String objPesquisa)
			throws ApplicationBusinessException {
		return ambulatorioFacade.pesquisarSituacao(objPesquisa);
	}
	
	/**
	 * Método que retorna o dia da semana por extenso.
	 */	
	public String obterDiaSemana(Date data) {
		DominioDiaSemana diaSemana = CoreUtil.retornaDiaSemana(data);
		return diaSemana.getDescricao();
	}

	public void ocultarModalConfirmacaoMaxRegistros(){
		exibirPopupConfirmacao = false;
	}
	
	/**
	 * Método que retorna a lista de especialidades.
	 */	
	public List<AghEspecialidades> obterEspecialidade(String parametro) {
		return aghuFacade.getListaEspecialidadesServico((String) parametro, servico);
	}
	
	/**
	 * #43342
	 * Método para a suggestion Procedimentos.
	 */	
	public List<FatProcedHospInternosVO> obterProcedimentosSuggest(String parametro) {
		return returnSGWithCount(ambulatorioFacade.listarFatProcedHospInternosPorEspOuEspGrad(grdSeq, especialidade, (String)parametro),
				ambulatorioFacade.listarFatProcedHospInternosPorEspOuEspGradCount(grdSeq, especialidade, (String)parametro));
	}
	
	public List<RapServidores> obterProfissionais(String parametro) {
		return registroColaboradorFacade.listarServidoresComPessoaFisicaPorNome((String) parametro);
	}

	public void setSituacao(AacSituacaoConsultas situacao) {
		this.situacao = situacao;
	}

	public AacSituacaoConsultas getSituacao() {
		return situacao;
	}

	public void setDiaSemana(DominioDiaSemana diaSemana) {
		this.diaSemana = diaSemana;
	}

	public DominioDiaSemana getDiaSemana() {
		return diaSemana;
	}

	public void setHoraConsulta(Date horaConsulta) {
		this.horaConsulta = horaConsulta;
	}

	public Date getHoraConsulta() {
		return horaConsulta;
	}

	public void setPagador(AacPagador pagador) {
		this.pagador = pagador;
	}

	public AacPagador getPagador() {
		return pagador;
	}

	public void setAutorizacao(AacTipoAgendamento autorizacao) {
		this.autorizacao = autorizacao;
	}

	public AacTipoAgendamento getAutorizacao() {
		return autorizacao;
	}
	
	public void setCondicao(AacCondicaoAtendimento condicao) {
		this.condicao = condicao;
	}

	public AacCondicaoAtendimento getCondicao() {
		return condicao;
	}

	public void setConsultas(List<AacConsultas> consultas) {
		this.consultas = consultas;
	}

	public List<AacConsultas> getConsultas() {
		return consultas;
	}

	public void setDtInicio(Date dtInicio) {
		this.dtInicio = dtInicio;
	}

	public Date getDtInicio() {
		return dtInicio;
	}

	public void setDtFim(Date dtFim) {
		this.dtFim = dtFim;
	}

	public Date getDtFim() {
		return dtFim;
	}

	public void setServico(FccCentroCustos servico) {
		this.servico = servico;
	}

	public FccCentroCustos getServico() {
		return servico;
	}
	
	public VAacSiglaUnfSalaVO getSiglaUnfSalaVO() {
		return siglaUnfSalaVO;
	}

	public void setSiglaUnfSalaVO(VAacSiglaUnfSalaVO siglaUnfSalaVO) {
		this.siglaUnfSalaVO = siglaUnfSalaVO;
	}

	public String getLabelZonaSala() {
		return labelZonaSala;
	}

	public void setLabelZonaSala(String labelZonaSala) {
		this.labelZonaSala = labelZonaSala;
	}
	
	/**
	 * Método para Suggestion Box de Zona
	 */	
	public List<VAacSiglaUnfSalaVO> obterZona(String objPesquisa) throws BaseException  {		
		listaZonaVO = ambulatorioFacade.pesquisarZonas(objPesquisa);
		return listaZonaVO;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	public void setEquipe(AghEquipes equipe) {
		this.equipe = equipe;
	}

	public AghEquipes getEquipe() {
		return equipe;
	}

	public String getLabelZona() {
		return labelZona;
	}

	public void setLabelZona(String labelZona) {
		this.labelZona = labelZona;
	}

	public List<ConsultasAgendaVO> getConsultaList() {
		return consultaList;
	}

	public void setConsultaList(List<ConsultasAgendaVO> consultaList) {
		this.consultaList = consultaList;
	}


	public RapServidores getProfissional() {
		return profissional;
	}


	public void setProfissional(RapServidores profissional) {
		this.profissional = profissional;
	}


	public String getTitleZona() {
		return titleZona;
	}


	public void setTitleZona(String titleZonaSala) {
		this.titleZona = titleZonaSala;
	}


	public Integer getGrdSeq() {
		return grdSeq;
	}


	public void setGrdSeq(Integer grdSeq) {
		this.grdSeq = grdSeq;
	}


	public AacRetornos getRetorno() {
		return retorno;
	}


	public void setRetorno(AacRetornos retorno) {
		this.retorno = retorno;
	}
	public DynamicDataModel<ConsultasAgendaVO> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<ConsultasAgendaVO> dataModel) {
	 this.dataModel = dataModel;
	}

	public boolean isExibirPopupConfirmacao() {
		return exibirPopupConfirmacao;
	}

	public void setExibirPopupConfirmacao(boolean exibirPopupConfirmacao) {
		this.exibirPopupConfirmacao = exibirPopupConfirmacao;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public StreamedContent getStreamedContent() {
		return streamedContent;
	}

	public void setStreamedContent(StreamedContent streamedContent) {
		this.streamedContent = streamedContent;
	}

	public List<AacConsultas> getConsultasRaas() {
		return consultasRaas;
}

	public void setConsultasRaas(List<AacConsultas> consultasRaas) {
		this.consultasRaas = consultasRaas;
	}
	
	public FatProcedHospInternosVO getProcedimento() {
		return procedimento;
	}

	public void setProcedimento(FatProcedHospInternosVO procedimento) {
		this.procedimento = procedimento;
	}

	public ArquivosEsusVO getArquivosEsusVO() {
		return arquivosEsusVO;
	}

	public void setArquivosEsusVO(ArquivosEsusVO arquivosEsusVO) {
		this.arquivosEsusVO = arquivosEsusVO;
	}	
	
	
}
