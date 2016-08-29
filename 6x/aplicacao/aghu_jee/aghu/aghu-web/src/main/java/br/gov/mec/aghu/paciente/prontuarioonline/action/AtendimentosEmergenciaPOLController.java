package br.gov.mec.aghu.paciente.prontuarioonline.action;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import net.sf.jasperreports.engine.JRException;

import org.jfree.util.Log;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.vo.RelatorioExamesPacienteVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExamesId;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MpmAltaPrincExame;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmMotivoAltaMedica;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.business.IProntuarioOnlineFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.AtendimentosEmergenciaPOLVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.NodoPOLVO;
import br.gov.mec.aghu.prescricaomedica.action.RelatorioSumarioAltaController;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.business.SelectionQualifier;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.AgrupadorRelatorioJasper;
import br.gov.mec.aghu.core.report.PdfUtil;

import com.itextpdf.text.DocumentException;



public class AtendimentosEmergenciaPOLController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -8764119058026431909L;
	private static final String PAGE_ATENDIMENTOS="atendimentosEmergenciaPOL";
	private static final String PAGE_CONSULTORIA="pol-consultaDetalheConsultoria";	
	private static final String PAGE_SUMARIO_ALTA="pol-relatorioSumarioAltaAtendEmergenciaPdf";

	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IProntuarioOnlineFacade prontuarioOnlineFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	@Inject
	private RelatorioPlanoContSumExamesController relatorioPlanoContSumExamesController;	

	@Inject
	private RelatorioInternacaoSumarioAltaObitoController relatorioInternacaoSumarioAltaObitoController;
	
    @Inject
	private RelatorioSumarioAltaAtendEmergenciaPOLController relatorioSumarioAltaAtendEmergenciaPOLController;
	
	@Inject
	private RelatorioSumarioAltaController relatorioSumarioAltaController;
	
	@Inject
	private SecurityController securityController;
	

	@Inject @Paginator
	private DynamicDataModel<AtendimentosEmergenciaPOLVO> dataModel;
	
	private Boolean habilitaBotaoEvolucaoAnamnese;	
	private Boolean habilitaBotaoConsultoria;	
	private Boolean habilitaBotaoSumarioAlta;	
	private List<AtendimentosEmergenciaPOLVO> atendimentos;
	private AtendimentosEmergenciaPOLVO registroSelecionado;	
	private AipPacientes paciente;	
	private Date dthrInicio; 
	private Date dthrFim;
	private Integer numeroConsulta;
	
	private Short seqEspecialidade; // valor recebido pelas estórias 17317 e 17318	
	private Integer seqAtendimento;
	
	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@Inject @SelectionQualifier
	private NodoPOLVO itemPOL;	
	
	private AgrupadorRelatorioJasper agrupador = new AgrupadorRelatorioJasper();
	private Boolean permiteImpressaoRelatorio;

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
		inicio();
	}	
	
	
	public void inicio(){
	 

		paciente = this.pacienteFacade.pesquisarPacientePorProntuario(itemPOL.getProntuario());		
		registroSelecionado = new AtendimentosEmergenciaPOLVO();
		inicializaBotoes();
		
		if (itemPOL.getParametros().get("dataInicial")!=null){
			dthrInicio=(Date) itemPOL.getParametros().get("dataInicial");
		}
		if (itemPOL.getParametros().get("dataFinal")!=null){
			dthrFim=(Date) itemPOL.getParametros().get("dataFinal");
		}
		if (itemPOL.getParametros().get("seqEspecialidade")!=null){
			seqEspecialidade=(Short) itemPOL.getParametros().get("seqEspecialidade");
		}
		if (itemPOL.getParametros().get("numeroConsulta")!=null){
			numeroConsulta=(Integer) itemPOL.getParametros().get("numeroConsulta");
		}		
		dataModel.reiniciarPaginator();
		if (dataModel.getRowCount()==1){
			//registroSelecionado=dataModel.getFirstQueryResult();
			selecionaRegistro();
		}
		
	
	}	
	

	public void selecionaRegistro(){		
		habilitaBotaoEvolucaoAnamnese = getProntuarioOnlineFacade().habilitarBotaoEvolucaoAnamnese(registroSelecionado);
		habilitaBotaoConsultoria = getProntuarioOnlineFacade().habilitarBotaoConsultoria(registroSelecionado);
		habilitaBotaoSumarioAlta = getProntuarioOnlineFacade().habilitarBotaoSumarioAlta(registroSelecionado);				

	}
	 
	
	public void exibirMsgFuncionalidadeNaoImplementada() {
		apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_FUNCIONALIDADE_NAO_IMPLEMENTADA");
	}
	
	@Override
	public Long recuperarCount() {
		return getProntuarioOnlineFacade().pesquisarAtendimentosEmergenciaPOLCount(paciente.getCodigo(), dthrInicio, dthrFim, numeroConsulta, seqEspecialidade);
	}


	@Override
	public List<AtendimentosEmergenciaPOLVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return getProntuarioOnlineFacade().pesquisarAtendimentosEmergenciaPOL(firstResult, maxResult, orderProperty, asc, 
						paciente.getCodigo(), dthrInicio, dthrFim, numeroConsulta, seqEspecialidade);
	}

	public String listarConsultorias(){		
		return PAGE_CONSULTORIA;
	}
	
	public String imprimirSumarioAlta() {
		return PAGE_SUMARIO_ALTA;
	}
	
	
	public String criarRelatorioAtdEmergencia(){
		return PAGE_ATENDIMENTOS;
	}
	
	
	public String detalharConsultoria(){
		return PAGE_CONSULTORIA;
	}
	
	
	public StreamedContent getRenderPdf() throws IOException, JRException, DocumentException, BaseException {
		
		Map<String, Object> parametros = new HashMap<String, Object>();

		parametros.put("BLOQUEAR_GERACAO_PENDENCIA", true);
		
		agrupador = new AgrupadorRelatorioJasper();
		seqAtendimento = registroSelecionado.getAtdSeq();
		
		Boolean planoCont = false;
		MpmAltaSumario sumario = this.prescricaoMedicaFacade.obterAltaSumariosAtivoConcluido(seqAtendimento);
		//AgrupadorRelatorioJasper agrupador = new AgrupadorRelatorioJasper();
		try {
			if(sumario != null) {
				Boolean indObito = false;
				if (sumario.getAltaMotivos()!=null){
					MpmMotivoAltaMedica mam = prescricaoMedicaFacade.obterMpmMotivoAltaMedica(sumario.getAltaMotivos().getMotivoAltaMedicas().getSeq());
					indObito = (sumario.getAltaMotivos() != null) ? mam.getIndObito() : Boolean.FALSE;
				}
				if(indObito) {
					//DESABILITA IMPRESSÃO
					//CHAMA SUMÁRIO OBITO
					relatorioInternacaoSumarioAltaObitoController.setAtdSeq(seqAtendimento);
					relatorioInternacaoSumarioAltaObitoController.verificaTipoRelatorio();
					agrupador.addReport(relatorioInternacaoSumarioAltaObitoController.getReportGenerator().gerarDocumento(parametros).getJasperPrint());
					permiteImpressaoRelatorio = securityController.usuarioTemPermissao("permiteImprimirSumarioAltaObitoPOL", "imprimir");
					
				} else {
					if(Boolean.TRUE.equals(sumario.getEmergencia())) {
						//DESABILITA IMPRESSÃO
						//CHAMA SUMÁRIO EMERGÊNCIA
						relatorioSumarioAltaAtendEmergenciaPOLController.setAtdSeq(seqAtendimento);
						agrupador.addReport(relatorioSumarioAltaAtendEmergenciaPOLController.getReportGenerator().gerarDocumento(parametros).getJasperPrint());
						permiteImpressaoRelatorio = securityController.usuarioTemPermissao("permiteImprimirSumEmergencia", "imprimir");
					}
					else {
						//CHAMA SUMÁRIO ALTA
						relatorioSumarioAltaController.setSeqAtendimento(seqAtendimento);
						relatorioSumarioAltaController.setPrevia(false);
						relatorioSumarioAltaController.setObito(false);
				        relatorioSumarioAltaController.populaRelatorioAltaObito();
				        agrupador.addReport(relatorioSumarioAltaController.getReportGenerator().gerarDocumento(parametros).getJasperPrint());
						permiteImpressaoRelatorio = securityController.usuarioTemPermissao("permiteImprimirRelatorioSumarioAlta", "imprimir");
					}
				}
			}
			List<MpmAltaSumario> sumarios = prescricaoMedicaFacade.pesquisarAltaSumariosConcluidoAltaEObitoPorAtdSeq(seqAtendimento);
			if(sumarios != null && !sumarios.isEmpty()) {
				List<MpmAltaPrincExame> altas = this.prescricaoMedicaFacade.listarMpmAltaPrincExamePorIdSemSeqpIndImprime(seqAtendimento, sumarios.get(0).getId().getApaSeq(), sumarios.get(0).getId().getSeqp());
				for(MpmAltaPrincExame alta : altas) {
					AelItemSolicitacaoExames solicitacao = this.examesFacade.obteritemSolicitacaoExamesPorChavePrimaria(new AelItemSolicitacaoExamesId(alta.getIseSoeSeq(), alta.getIseSeqp()));
					if(solicitacao != null) {
						planoCont = true;
						break;
					}
				}
				
				if(planoCont) {
					relatorioPlanoContSumExamesController.setOrigem("CONSULTA_INTERNACAO_POL");
					relatorioPlanoContSumExamesController.setAtdSeq(seqAtendimento);
					relatorioPlanoContSumExamesController.setDataHoraEvento(new Date());
					RelatorioExamesPacienteVO vo = examesFacade.montarRelatorioPlanoContingenciaSumarioExames(seqAtendimento, sumarios.get(0).getId().getApaSeq(), sumarios.get(0).getId().getSeqp());
					if (vo != null) {
						relatorioPlanoContSumExamesController.setVo(vo);
						agrupador.addReport(relatorioPlanoContSumExamesController.getReportGenerator().gerarDocumento(parametros).getJasperPrint());
					}
				}
			}			
			ByteArrayOutputStream outputStrem = agrupador.exportReportAsOutputStream();
			
			//if(true){//Se não permitir impressão, então protege o relatório
				outputStrem = PdfUtil.protectPdf(outputStrem);
			//}
			
			byte[] relatorioArrayBytes = outputStrem.toByteArray();
			return ActionReport.criarStreamedContentPdfPorByteArray(relatorioArrayBytes);
			
		} catch (NumberFormatException e) {
			Log.error("ERRO: " + e);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	public void directPrint() {
		try {
			//DocumentoJasper documento = gerarDocumento(Boolean.TRUE);
			this.sistemaImpressao.imprimir(agrupador.exportReportAsOutputStream(), super.getEnderecoIPv4HostRemoto(), "altaSum");

			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}

	}
	
	public String imprimirEvolucaoAnamnese(){
		
		seqAtendimento = registroSelecionado.getAtdSeq();
		return "imprimirEvolucaoAnamnese";
	}
	
	private void inicializaBotoes() {
		habilitaBotaoEvolucaoAnamnese = Boolean.FALSE;
		habilitaBotaoConsultoria = Boolean.FALSE;
		habilitaBotaoSumarioAlta = Boolean.FALSE;			
	}

	
	// Getters e Setters
	public IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	public void setParametroFacade(IParametroFacade parametroFacade) {
		this.parametroFacade = parametroFacade;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public Boolean getHabilitaBotaoEvolucaoAnamnese() {
		return habilitaBotaoEvolucaoAnamnese;
	}

	public void setHabilitaBotaoEvolucaoAnamnese(
			Boolean habilitaBotaoEvolucaoAnamnese) {
		this.habilitaBotaoEvolucaoAnamnese = habilitaBotaoEvolucaoAnamnese;
	}

	public Boolean getHabilitaBotaoConsultoria() {
		return habilitaBotaoConsultoria;
	}


	public void setHabilitaBotaoConsultoria(Boolean habilitaBotaoConsultoria) {
		this.habilitaBotaoConsultoria = habilitaBotaoConsultoria;
	}


	public Boolean getHabilitaBotaoSumarioAlta() {
		return habilitaBotaoSumarioAlta;
	}

	public void setHabilitaBotaoSumarioAlta(Boolean habilitaBotaoSumarioAlta) {
		this.habilitaBotaoSumarioAlta = habilitaBotaoSumarioAlta;
	}

	public IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}


	public void setPacienteFacade(IPacienteFacade pacienteFacade) {
		this.pacienteFacade = pacienteFacade;
	}

	public IProntuarioOnlineFacade getProntuarioOnlineFacade() {
		return prontuarioOnlineFacade;
	}


	public void setProntuarioOnlineFacade(
			IProntuarioOnlineFacade prontuarioOnlineFacade) {
		this.prontuarioOnlineFacade = prontuarioOnlineFacade;
	}

	public Integer getNumeroConsulta() {
		return numeroConsulta;
	}


	public void setNumeroConsulta(Integer numeroConsulta) {
		this.numeroConsulta = numeroConsulta;
	}

	public Short getSeqEspecialidade() {
		return seqEspecialidade;
	}

	public void setSeqEspecialidade(Short seqEspecialidade) {
		this.seqEspecialidade = seqEspecialidade;
	}


	public AtendimentosEmergenciaPOLVO getRegistroSelecionado() {
		return registroSelecionado;
	}


	public void setRegistroSelecionado(
			AtendimentosEmergenciaPOLVO registroSelecionado) {
		this.registroSelecionado = registroSelecionado;
	}


	public Integer getSeqAtendimento() {
		return seqAtendimento;
	}


	public void setSeqAtendimento(Integer seqAtendimento) {
		this.seqAtendimento = seqAtendimento;
	}


	public List<AtendimentosEmergenciaPOLVO> getAtendimentos() {
		return atendimentos;
	}


	public void setAtendimentos(List<AtendimentosEmergenciaPOLVO> atendimentos) {
		this.atendimentos = atendimentos;
	}


	public Boolean getPermiteImpressaoRelatorio() {
		return permiteImpressaoRelatorio;
	}


	public void setPermiteImpressaoRelatorio(Boolean permiteImpressaoRelatorio) {
		this.permiteImpressaoRelatorio = permiteImpressaoRelatorio;
	}

	public DynamicDataModel<AtendimentosEmergenciaPOLVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AtendimentosEmergenciaPOLVO> dataModel) {
		this.dataModel = dataModel;
	}


	public Date getDthrInicio() {
		return dthrInicio;
	}


	public void setDthrInicio(Date dthrInicio) {
		this.dthrInicio = dthrInicio;
	}


	public Date getDthrFim() {
		return dthrFim;
	}


	public void setDthrFim(Date dthrFim) {
		this.dthrFim = dthrFim;
	}
}
