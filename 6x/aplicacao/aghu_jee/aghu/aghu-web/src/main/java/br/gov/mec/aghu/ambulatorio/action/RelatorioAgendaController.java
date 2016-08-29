package br.gov.mec.aghu.ambulatorio.action;

import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.ambulatorio.vo.RelatorioConsultasAgendaVO;
import br.gov.mec.aghu.ambulatorio.vo.VAacSiglaUnfSalaVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.dominio.DominioDiaSemana;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.faturamento.vo.FatProcedHospInternosVO;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AacCondicaoAtendimento;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacPagador;
import br.gov.mec.aghu.model.AacRetornos;
import br.gov.mec.aghu.model.AacSituacaoConsultas;
import br.gov.mec.aghu.model.AacTipoAgendamento;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;

import com.itextpdf.text.DocumentException;

/**
 * Controller da tela de Relatórios das Agendas
 * 
 * 
 * 
 */

public class RelatorioAgendaController extends ActionReport {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(RelatorioAgendaController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1921850930638683668L;

	@EJB
	protected ICentroCustoFacade centroCustoFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IParametroFacade parametroFacade;

	private List<AacConsultas> consultas = new ArrayList<AacConsultas>();

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	@EJB
	private IAghuFacade aghuFacade;

	@Inject
	private SistemaImpressao sistemaImpressao;

	// Lista de VOs para suggestion da zona
	private List<VAacSiglaUnfSalaVO> listaZonaVO = new ArrayList<VAacSiglaUnfSalaVO>();

	// FILTRO
	private FccCentroCustos servico;
	private Integer grdSeq;
	private VAacSiglaUnfSalaVO siglaUnfSalaVO;
	private AghEspecialidades especialidade;
	private AghEquipes equipe;
	private AacPagador pagador;
	private List<AacPagador> pagadorList;
	private AacTipoAgendamento autorizacao;
	private List<AacTipoAgendamento> autorizacaoList;
	private AacCondicaoAtendimento condicao;
	private List<AacCondicaoAtendimento> condicaoList;
	private AacSituacaoConsultas situacao;
	private List<AacSituacaoConsultas> situacaoList;
	private DominioDiaSemana diaSemana;
	private Date horaConsulta;
	private Date dtInicio;
	private Date dtFim;
	private RapServidores profissional;
	private AacRetornos retorno;
	private FatProcedHospInternosVO filtroProcedimento;
	private boolean exibirPopupConfirmacao = false;

	// Labels parametrizados
	private String labelZona;
	private String labelZonaSala;
	private String titleZona;
	
	private static final String REDIRECT_RELATORO_CONSULTAS = "relatorioConsultasAgendaPdf";
	private static final String REDIRECT_CONSULTAS = "relatorioConsultasAgenda";

	public void iniciar() {

		obterPagadoresComAgendamento();
		obterAutorizacoesAtivas();
		obterCondicaoAtendimentoAtivas();
		obterSituacao();

		try {
			labelZona = parametroFacade.buscarAghParametro(
					AghuParametrosEnum.P_AGHU_LABEL_ZONA).getVlrTexto();
			labelZonaSala = labelZona
					+ "/"
					+ parametroFacade.buscarAghParametro(
							AghuParametrosEnum.P_AGHU_LABEL_SALA).getVlrTexto();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error("Erro ao buscar parâmetro", e);
			this.labelZona = "zona";
			this.labelZonaSala = "zona/sala";
		}

		String message = getBundle().getString(
				"TITLE_PESQUISAR_CONSULTAS_AGENDA_ZONASALA");
		this.titleZona = MessageFormat.format(message, this.labelZona);

	}
	
	public String voltar() {
		return REDIRECT_CONSULTAS;
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
		diaSemana = null;
		horaConsulta = null;
		dtInicio = null;
		dtFim = null;
		profissional = null;
		retorno = null;
		filtroProcedimento = null;
	}

	public Collection<RelatorioConsultasAgendaVO> recuperarColecao() throws ApplicationBusinessException {
		List<RelatorioConsultasAgendaVO> listaVO;

		listaVO = ambulatorioFacade.recuperarInformacoesConsultaParaRelPDF(consultas);

		return listaVO;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/ambulatorio/report/relatorioAgendaConsultas.jasper";
	}

	public void renderPdf(OutputStream out, Object data)
			throws BaseException, IOException, JRException,
			DocumentException {
		DocumentoJasper documento = gerarDocumento();
		out.write(documento.getPdfByteArray(false));
	}

	public void recuperarListaAgendaConsulta() throws ApplicationBusinessException {

		Short unfSeq = null;

		if (siglaUnfSalaVO != null) {
			unfSeq = siglaUnfSalaVO.getUnfSeq();
		}

		consultas = ambulatorioFacade.listarConsultasAgenda(servico, grdSeq,
				unfSeq, especialidade, equipe, pagador, autorizacao, condicao,
				situacao, diaSemana, horaConsulta, dtInicio, dtFim,
				profissional, retorno, filtroProcedimento);
	}

	public String print() throws ApplicationBusinessException {

		if (validarPesquisa()) {
			return REDIRECT_RELATORO_CONSULTAS;
		}

		return null;

	}
	
	private boolean validarPesquisa() throws ApplicationBusinessException {

		if (DateUtil.validaDataMaior(dtInicio, dtFim)) {
			this.apresentarMsgNegocio(Severity.ERROR, "AAC_00145");
			return Boolean.FALSE;
		}

		if (DateUtil.calcularDiasEntreDatas(dtInicio, dtFim) > 10) {
			this.apresentarMsgNegocio(Severity.ERROR, "AAC_00147");
			return Boolean.FALSE;
		}

		recuperarListaAgendaConsulta();

		if (consultas.isEmpty()) {
			this.apresentarMsgNegocio(Severity.WARN, "AAC_00149");
			return Boolean.FALSE;
		}

		return Boolean.TRUE;

	}

	public void directPrint() throws ApplicationBusinessException {

		try {
			if (validarPesquisa()) {
				DocumentoJasper documento = gerarDocumento();

				this.sistemaImpressao.imprimir(documento.getJasperPrint(),
						super.getEnderecoIPv4HostRemoto());
				this.apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_IMPRESSAO");
			}

		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}

	@Override
	public Map<String, Object> recuperarParametros() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("nomeHospital", cadastrosBasicosInternacaoFacade
				.recuperarNomeInstituicaoLocal());
		return params;
	}

	/**
	 * Método para Suggestion Serviço que estejam em Especialidade
	 */
	public List<FccCentroCustos> obterServico(String parametro) {
		return centroCustoFacade
				.obterListaServicosEmEspecialidades(parametro);
	}

	/**
	 * Método para Suggestion Box de Zona
	 */
	public List<VAacSiglaUnfSalaVO> obterZona(String objPesquisa)
			throws BaseException {
		listaZonaVO = ambulatorioFacade.pesquisarZonas(objPesquisa);
		return listaZonaVO;
	}

	/**
	 * Método que retorna a lista de especialidades.
	 */
	public List<AghEspecialidades> obterEspecialidade(String parametro) {
		return aghuFacade.getListaEspecialidadesServico(parametro,
				servico);
	}

	/**
	 * Método que lista os pagadores que tem agendamento.
	 */
	public void obterPagadoresComAgendamento() {
		pagadorList = ambulatorioFacade.obterListaPagadoresComAgendamento();
	}

	/**
	 * Método que lista autorizações ativas.
	 */
	public void obterAutorizacoesAtivas() {
		autorizacaoList = ambulatorioFacade.obterListaAutorizacoesAtivas();
	}

	/**
	 * Método que lista as condicões de atendimento ativas.
	 */
	public void obterCondicaoAtendimentoAtivas() {
		condicaoList = ambulatorioFacade.listarCondicaoAtendimento();
	}

	/**
	 * Método que lista as situações ativas.
	 */
	public void obterSituacao() {

		situacaoList = ambulatorioFacade.obterSituacoesAtivas();
		int i = 0;
		for (AacSituacaoConsultas situacao : situacaoList) {
			if (situacao.getSituacao().equals("M")) {
				situacao = situacaoList.get(i);
				this.situacao = situacao;
				return;
			}
			i++;
		}
	}

	/**
	 * Método para Suggestion Situação
	 */
	public List<AacSituacaoConsultas> obterSituacao(String objPesquisa)
			throws BaseException {
		return ambulatorioFacade.pesquisarSituacao(objPesquisa);
	}

	public List<RapServidores> obterProfissionais(String parametro) {
		return registroColaboradorFacade
				.listarServidoresComPessoaFisicaPorNome(parametro);
	}

	public List<AacConsultas> getConsultas() {
		return consultas;
	}

	public FccCentroCustos getServico() {
		return servico;
	}

	public Integer getGrdSeq() {
		return grdSeq;
	}

	public VAacSiglaUnfSalaVO getSiglaUnfSalaVO() {
		return siglaUnfSalaVO;
	}

	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	public AghEquipes getEquipe() {
		return equipe;
	}

	public AacPagador getPagador() {
		return pagador;
	}

	public List<AacPagador> getPagadorList() {
		return pagadorList;
	}

	public AacTipoAgendamento getAutorizacao() {
		return autorizacao;
	}

	public List<AacTipoAgendamento> getAutorizacaoList() {
		return autorizacaoList;
	}

	public AacCondicaoAtendimento getCondicao() {
		return condicao;
	}

	public List<AacCondicaoAtendimento> getCondicaoList() {
		return condicaoList;
	}

	public AacSituacaoConsultas getSituacao() {
		return situacao;
	}

	public List<AacSituacaoConsultas> getSituacaoList() {
		return situacaoList;
	}

	public DominioDiaSemana getDiaSemana() {
		return diaSemana;
	}

	public Date getHoraConsulta() {
		return horaConsulta;
	}

	public Date getDtInicio() {
		return dtInicio;
	}

	public Date getDtFim() {
		return dtFim;
	}

	public RapServidores getProfissional() {
		return profissional;
	}

	public AacRetornos getRetorno() {
		return retorno;
	}

	public boolean isExibirPopupConfirmacao() {
		return exibirPopupConfirmacao;
	}

	public void setConsultas(List<AacConsultas> consultas) {
		this.consultas = consultas;
	}

	public void setServico(FccCentroCustos servico) {
		this.servico = servico;
	}

	public void setGrdSeq(Integer grdSeq) {
		this.grdSeq = grdSeq;
	}

	public void setSiglaUnfSalaVO(VAacSiglaUnfSalaVO siglaUnfSalaVO) {
		this.siglaUnfSalaVO = siglaUnfSalaVO;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

	public void setEquipe(AghEquipes equipe) {
		this.equipe = equipe;
	}

	public void setPagador(AacPagador pagador) {
		this.pagador = pagador;
	}

	public void setPagadorList(List<AacPagador> pagadorList) {
		this.pagadorList = pagadorList;
	}

	public void setAutorizacao(AacTipoAgendamento autorizacao) {
		this.autorizacao = autorizacao;
	}

	public void setAutorizacaoList(List<AacTipoAgendamento> autorizacaoList) {
		this.autorizacaoList = autorizacaoList;
	}

	public void setCondicao(AacCondicaoAtendimento condicao) {
		this.condicao = condicao;
	}

	public void setCondicaoList(List<AacCondicaoAtendimento> condicaoList) {
		this.condicaoList = condicaoList;
	}

	public void setSituacao(AacSituacaoConsultas situacao) {
		this.situacao = situacao;
	}

	public void setSituacaoList(List<AacSituacaoConsultas> situacaoList) {
		this.situacaoList = situacaoList;
	}

	public void setDiaSemana(DominioDiaSemana diaSemana) {
		this.diaSemana = diaSemana;
	}

	public void setHoraConsulta(Date horaConsulta) {
		this.horaConsulta = horaConsulta;
	}

	public void setDtInicio(Date dtInicio) {
		this.dtInicio = dtInicio;
	}

	public void setDtFim(Date dtFim) {
		this.dtFim = dtFim;
	}

	public void setProfissional(RapServidores profissional) {
		this.profissional = profissional;
	}

	public void setRetorno(AacRetornos retorno) {
		this.retorno = retorno;
	}

	public void setExibirPopupConfirmacao(boolean exibirPopupConfirmacao) {
		this.exibirPopupConfirmacao = exibirPopupConfirmacao;
	}

	public String getLabelZona() {
		return labelZona;
	}

	public String getLabelZonaSala() {
		return labelZonaSala;
	}

	public String getTitleZona() {
		return titleZona;
	}

	public void setLabelZona(String labelZona) {
		this.labelZona = labelZona;
	}

	public void setLabelZonaSala(String labelZonaSala) {
		this.labelZonaSala = labelZonaSala;
	}

	public void setTitleZona(String titleZona) {
		this.titleZona = titleZona;
	}

	public FatProcedHospInternosVO getFiltroProcedimento() {
		return filtroProcedimento;
	}

	public void setFiltroProcedimento(FatProcedHospInternosVO filtroProcedimento) {
		this.filtroProcedimento = filtroProcedimento;
	}

}
