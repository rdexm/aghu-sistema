package br.gov.mec.aghu.faturamento.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioGrupoProcedimento;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioModuloMensagem;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.TipoArquivoRelatorio;
import br.gov.mec.aghu.faturamento.dao.FatContasHospitalaresDAO;
import br.gov.mec.aghu.faturamento.dao.FatItensProcedHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedAmbRealizadoDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedHospInternosDAO;
import br.gov.mec.aghu.faturamento.dao.FatVlrItemProcedHospCompsDAO;
import br.gov.mec.aghu.faturamento.vo.AIHFaturadaPorPacienteVO;
import br.gov.mec.aghu.faturamento.vo.AihPorProcedimentoVO;
import br.gov.mec.aghu.faturamento.vo.AihsFaturadasPorClinicaVO;
import br.gov.mec.aghu.faturamento.vo.ConsultaRateioProfissionalVO;
import br.gov.mec.aghu.faturamento.vo.ContasNPTVO;
import br.gov.mec.aghu.faturamento.vo.ContasPreenchimentoLaudosVO;
import br.gov.mec.aghu.faturamento.vo.FaturaAmbulatorioVO;
import br.gov.mec.aghu.faturamento.vo.FaturamentoPorProcedimentoVO;
import br.gov.mec.aghu.faturamento.vo.GerarArquivoProcedimentoVO;
import br.gov.mec.aghu.faturamento.vo.LogInconsistenciasInternacaoVO;
import br.gov.mec.aghu.faturamento.vo.NutricaoEnteralDigitadaVO;
import br.gov.mec.aghu.faturamento.vo.PreviaDiariaFaturamentoVO;
import br.gov.mec.aghu.faturamento.vo.RelacaoDeOPMNaoFaturadaVO;
import br.gov.mec.aghu.faturamento.vo.RelacaoDeOrtesesProtesesVO;
import br.gov.mec.aghu.faturamento.vo.TotaisPorDCIHVO;
import br.gov.mec.aghu.faturamento.vo.ValoresAIHPorDCIHVO;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.FatRelatorioProducaoPHIVO;
import br.gov.mec.aghu.internacao.vo.RelatorioIntermediarioLancamentosContaVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.model.FatCompetenciaId;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.AghuNumberFormat;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;

@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.ExcessiveClassLength"})
@Stateless
public class RelatorioCSVFaturamentoON extends BaseBusiness {

	private static final String FIM_NL = ";\n";

	private static final String MASCARA_VALORES = "#######,###.####";

	private static final String AIH = "AIH";

	private static final String _HIFEN_ = " - ";

	private static final String PACIENTE = "Paciente";

	private static final String NLNL = "\n\n";

	private static final String DD_MM_YYYY = DateConstants.DATE_PATTERN_DDMMYYYY;

	private static final String PHI = "PHI";

	@EJB
	private FaturamentoAmbulatorioON faturamentoAmbulatorioON;
	
	@EJB
	private ProcedimentosAmbRealizadosON procedimentosAmbRealizadosON;

	private static final Log LOG = LogFactory.getLog(RelatorioCSVFaturamentoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@Inject
	private FatContasHospitalaresDAO fatContasHospitalaresDAO;
	
	@Inject
	private FatProcedHospInternosDAO fatProcedHospInternosDAO;
	
	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@Inject
	private FatProcedAmbRealizadoDAO fatProcedAmbRealizadoDAO;
	
	@Inject
	private FatItensProcedHospitalarDAO fatItensProcedHospitalarDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private FatVlrItemProcedHospCompsDAO fatVlrItemProcedHospCompsDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4353859723963839660L;
//	private static final int BUFFER = 1024;
	private static final String SEPARADOR=";";
	private static final String ENCODE="ISO-8859-1";
	private static final String EXTENSAO=".csv";
	private static String lineSeparator;
	
	public enum RelatorioCSVFaturamentoONExceptionCode implements BusinessExceptionCode {
		NENHUM_REGISTRO_ENCONTRADO_PRODUCAO_PHI, PROBLEMA_CRIACAO_ARQUIVO, INICIAIS_INVALIDAS_ORTESES_PROTESES, NENHUM_REGISTRO_ORTESES_PROTESES
		;
	}

	
	public String geraCSVRelatorioLogInconsistenciasInternacao(final Date dtCriacaoIni, final Date dtCriacaoFim, final Date dtPrevia, final Integer pacProntuario, final Integer cthSeq, final String inconsistencia, final String iniciaisPaciente, final Boolean reapresentada, final DominioGrupoProcedimento grupoProcedimento) throws IOException, ApplicationBusinessException{
		final List<LogInconsistenciasInternacaoVO> colecao = getFaturamentoFacade().getLogsInconsistenciasInternacaoVO(dtCriacaoIni, dtCriacaoFim, dtPrevia, pacProntuario, cthSeq, inconsistencia, iniciaisPaciente, reapresentada, grupoProcedimento);
		Integer tempCthSeq = null;
		for (final LogInconsistenciasInternacaoVO log : colecao) {
			if(!log.getCthseq().equals(tempCthSeq)){
				tempCthSeq = log.getCthseq();
				log.setLeito(getInternacaoFacade().obterLeitoContaHospitalar(log.getCthseq()));
			} 
		}
		
		final File file = File.createTempFile(DominioNomeRelatorio.FATR_INT_LOG_ERRO.toString(), EXTENSAO);
		final Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);

		Integer conta=null;
		for (final LogInconsistenciasInternacaoVO log : colecao) {
			if(!log.getCthseq().equals(conta)){
				conta=log.getCthseq();
				out.write(NLNL);
				out.write("Conta  "+log.getCthseq()+SEPARADOR+SEPARADOR+
						  "Prontuário  "+ (log.getProntuario() != null ? log.getProntuario(): "")+SEPARADOR +SEPARADOR+ 
						  "Nome"+SEPARADOR+ (log.getPacnome() != null ? log.getPacnome() : "")+SEPARADOR+
						  "Leito  "+ (log.getLeito() != null ? log.getLeito() : "")+"\n");
				
				out.write("Data Internação:"+SEPARADOR+DateUtil.obterDataFormatadaHoraMinutoSegundo(log.getDatainternacaoadministrativa())+SEPARADOR+
						  "Data Alta:"+SEPARADOR+DateUtil.obterDataFormatadaHoraMinutoSegundo(log.getDtaltaadministrativa())+SEPARADOR+
						  "AIH:"+SEPARADOR+(log.getNroaih() != null?log.getNroaih():"")+SEPARADOR+
						  "Motivo Saída: "+ (log.getCodigosusmsp() != null ? log.getCodigosusmsp() : "")+"\n");
				
				out.write("Solicitado:"+SEPARADOR+(log.getIphseq() != null ? log.getIphseq() : "")+_HIFEN_+(log.getDescitemsol() != null ? log.getDescitemsol() : "") +SEPARADOR+
						  "Realizado:"+SEPARADOR+(log.getIphseqrealizado() != null ? log.getIphseqrealizado() : "")+_HIFEN_+(log.getDescitemreal()!=null?log.getDescitemreal(): "")+"\n");
			}

			if(log.getPhiseqitem1()!= null){
				out.write("Item PHI1:"+SEPARADOR+(log.getIphseqitem1() != null ? log.getIphseqitem1() : "")+_HIFEN_+(log.getDescricaophi1() != null ? log.getDescricaophi1() : "")+SEPARADOR+
						   (log.getUnfseq() != null ? log.getUnfseq() : "")+SEPARADOR+DateUtil.obterDataFormatada(log.getDthrrealizado(),DateConstants.DATE_PATTERN_DDMMYYYY)+SEPARADOR+
						   "Item Sus1:"+(log.getIphseqitem1() !=  null ? log.getIphseqitem1() : "")+" "+(log.getDescsus1() != null ? log.getDescsus1() : "")+"\n");
				
				out.write("Item PHI2:"+SEPARADOR+(log.getIphseqitem2() != null ? log.getIphseqitem2() : "")+_HIFEN_+(log.getDescricaophi2() !=null ? log.getDescricaophi2() : "")+SEPARADOR+SEPARADOR+SEPARADOR+SEPARADOR+SEPARADOR+
						  "Item Sus2:"+(log.getIphseqitem2() != null ? log.getIphseqitem2() : "")+_HIFEN_+(log.getDescsus2() != null ? log.getDescsus2() :"")+"\n");
			}
			
			out.write("INCONSISTÊNCIA"+SEPARADOR+(log.getErro() != null ? log.getErro(): "")+"\n");
		}

		out.flush();
		out.close();
		
		return file.getAbsolutePath();
	}

	@SuppressWarnings("PMD.NPathComplexity")
	public String geraCSVRelatorioAIHFaturadaPorPaciente(final Date dtHrInicio, final Integer ano, final Integer mes, final String iniciaisPaciente, final Boolean reapresentada, final Integer clinica) throws IOException, ApplicationBusinessException{
		final List<AIHFaturadaPorPacienteVO> colecao = getFaturamentoFacade().obterAIHsFaturadasPorPaciente(dtHrInicio, ano, mes, iniciaisPaciente, reapresentada, clinica);

		final File file = File.createTempFile(DominioNomeRelatorio.FATR_INT_OPM_NAO_FAT.toString(), EXTENSAO);
		
		
		final Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);
		
		out.write("CLÍNICA"+SEPARADOR+"PRONTUÁRIO"+SEPARADOR+"NOME DO PACIENTE"+SEPARADOR+"CONTA"+SEPARADOR+"INTERNAÇÃO"+SEPARADOR+"ALTA"+SEPARADOR+"AIH "+SEPARADOR+"SSM\n");

		int tot=0;
		for (final AIHFaturadaPorPacienteVO aih : colecao) {
			tot++;
			out.write( (aih.getCodigo() != null ? aih.getCodigo() : "") + " " +(aih.getDescricao() != null ? aih.getDescricao() : "") +SEPARADOR +
					   (aih.getProntuario() != null ? aih.getProntuario() : "") + SEPARADOR +
					   (aih.getPacnome()!=null ? aih.getPacnome() : "") + SEPARADOR+
					   (aih.getCthseq()!= null ? aih.getCthseq() : "") + SEPARADOR+
					   (aih.getDatainternacaoadministrativa() != null ? DateUtil.obterDataFormatada(aih.getDatainternacaoadministrativa(), DateConstants.DATE_PATTERN_DDMMYYYY) : "") +SEPARADOR +
					   (aih.getDtaltaadministrativa() != null ? DateUtil.obterDataFormatada(aih.getDtaltaadministrativa(), DateConstants.DATE_PATTERN_DDMMYYYY) : "") +SEPARADOR +
					   (aih.getNroaih() != null ? aih.getNroaih() : "")+SEPARADOR +
					   (aih.getIphcodsusrealiz() != null ? aih.getIphcodsusrealiz() : "") + "\n"
					   
			         );
		}

		out.write(SEPARADOR+SEPARADOR+tot);
		
		out.flush();
		out.close();
		
		return file.getAbsolutePath();
	}

	@SuppressWarnings("PMD.NPathComplexity")
	public String geraCSVRelatorioContasPreenchimentoLaudos(final Date dtPrevia, final Short unfSeq, final String iniciaisPaciente) throws IOException, ApplicationBusinessException{
		final List<ContasPreenchimentoLaudosVO> colecao = getFaturamentoFacade().obterContasPreenchimentoLaudos(dtPrevia, unfSeq, iniciaisPaciente);

		final File file = File.createTempFile(DominioNomeRelatorio.FATR_LAUD_CONTAS.toString(), EXTENSAO);
		
		final Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);
		
		out.write("Prontuário"+SEPARADOR+"Código"+SEPARADOR+PACIENTE+SEPARADOR+"Procedimento Realizado"+SEPARADOR+"Procedimento Solicitado"+SEPARADOR+"Usuário"+SEPARADOR+"Alta Médica"+SEPARADOR+"Conta"+SEPARADOR+"UTI"+SEPARADOR+"AC"+SEPARADOR+"MP"+SEPARADOR+"PM"+SEPARADOR+"EC"+SEPARADOR+"DES\n");

		int contConta=0;
		for (final ContasPreenchimentoLaudosVO aih : colecao) {
			contConta++;
			
			out.write( (aih.getProntuario() != null ? aih.getProntuario() : "") + SEPARADOR +
					   (aih.getCodPaciente() != null ? aih.getCodPaciente() : "") +SEPARADOR +
					   (aih.getPaciente() != null ? aih.getPaciente() : "") +SEPARADOR +
					   (aih.getSsmRealizado() !=null ? aih.getSsmRealizado() : "") + SEPARADOR+
					   (aih.getSsmSolicitado() != null ? aih.getSsmSolicitado() : "") + SEPARADOR+
					   (aih.getUsuario() != null ? aih.getUsuario() : "") + SEPARADOR+
					   (aih.getDtAlta() != null ? DateUtil.obterDataFormatada(aih.getDtAlta(), DateConstants.DATE_PATTERN_DDMMYYYY) : "") +SEPARADOR +
					   (aih.getConta() != null ? aih.getConta() : "") +SEPARADOR +
					   (aih.getUti() != null ? aih.getUti() : "")+SEPARADOR +
					   (aih.getAcomp() != null ? aih.getAcomp() : "")+SEPARADOR +
					   (aih.getmProced() != null ? aih.getmProced() : "")+SEPARADOR +
					   (aih.getpMaior() != null ? aih.getpMaior() : "")+SEPARADOR +
					   (aih.getExcluCrit() != null ? aih.getExcluCrit() : "")+SEPARADOR +
					   (aih.getDes() != null ? aih.getDes() : "")+ "\n"
			         );
		}

		out.write(SEPARADOR+SEPARADOR+SEPARADOR+SEPARADOR+SEPARADOR+SEPARADOR+SEPARADOR+contConta);
		
		out.flush();
		out.close();
		
		return file.getAbsolutePath();
	}

	@SuppressWarnings("PMD.NPathComplexity")
	public String geraCSVRelatorioAIHPorProcedimento(final Long procedimentoInicial, final Long procedimentoFinal, final Date dtHrInicio,
			final Integer mes, final Integer ano, final String iniciaisPaciente, final boolean reapresentada) throws IOException,
			ApplicationBusinessException {
		final List<AihPorProcedimentoVO> colecao = getFaturamentoFacade().obterAihsPorProcedimentoVO(procedimentoInicial, procedimentoFinal,
				dtHrInicio, mes, ano, iniciaisPaciente, reapresentada);

		final File file = File.createTempFile(DominioNomeRelatorio.FATR_INT_AIH_PROCED.toString(), EXTENSAO);

		final Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);

		out.write("Prontuário" + SEPARADOR + "Código" + SEPARADOR + PACIENTE + SEPARADOR + "Cidade" + SEPARADOR + "Número AIH" + SEPARADOR
				+ "Procedimento Realizado" + SEPARADOR + "Internação" + SEPARADOR + "Alta\n");

		int contConta = 0;
		for (final AihPorProcedimentoVO aih : colecao) {
			contConta++;

			out.write((aih.getProntuario() != null ? aih.getProntuario() : "") + SEPARADOR + (aih.getCodigo() != null ? aih.getCodigo() : "")
					+ SEPARADOR + (aih.getNome() != null ? aih.getNome() : "") + SEPARADOR + (aih.getCidade() != null ? aih.getCidade() : "")
					+ SEPARADOR + (aih.getAih() != null ? aih.getAih() : "") + SEPARADOR
					+ (aih.getProcedimento() != null ? aih.getProcedimento() : "") + SEPARADOR
					+ (aih.getInternacao() != null ? DateUtil.obterDataFormatada(aih.getInternacao(), DateConstants.DATE_PATTERN_DDMMYYYY) : "") + SEPARADOR
					+ (aih.getAlta() != null ? DateUtil.obterDataFormatada(aih.getAlta(), DateConstants.DATE_PATTERN_DDMMYYYY) : "") + "\n");
		}

		out.write(SEPARADOR + SEPARADOR + SEPARADOR + SEPARADOR + SEPARADOR + SEPARADOR + "Total" + SEPARADOR + contConta);

		out.flush();
		out.close();

		return file.getAbsolutePath();
	}

	@SuppressWarnings("PMD.NPathComplexity")
	public String geraCSVRelatorioPreviaDiariaFaturamento(final FatCompetencia competencia) throws IOException, ApplicationBusinessException{
		
		final List<PreviaDiariaFaturamentoVO> colecao = getProcedimentosAmbRealizadosON().obterPreviaDiariaFaturamento(competencia, false);

		final File file = File.createTempFile(DominioNomeRelatorio.FATR_PRV_GRP_FIN.toString(), EXTENSAO);
		
		final Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);
		
		out.write("Cód. Centro Custo"+SEPARADOR+"Descrição Centro Custo"+SEPARADOR+"Cód. Unid. Func."+SEPARADOR+"Descrição Unidade Funcional"+SEPARADOR+"Espec. Genérica"+SEPARADOR+"Espec. Específica"+SEPARADOR+
				  "Grupo"+SEPARADOR+"Subgrupo"+SEPARADOR+"Forma Org."+SEPARADOR+"SSM"+SEPARADOR+"Descrição SSM"+SEPARADOR+PHI+ SEPARADOR+
				  "Descrição PHI"+SEPARADOR+"Cód. Financ."+SEPARADOR+"Descrição Financiamento"+SEPARADOR+"Cód. Complex."+SEPARADOR+"Descrição Complexidade"+SEPARADOR+"Total Anestesia"+ SEPARADOR+
				  "Total Serv. Prof."+SEPARADOR+"Quantidade"+SEPARADOR+"Total"+SEPARADOR+"Teto Qtd."+SEPARADOR+"Dif. Qtd."+SEPARADOR+
				  "Teto Vlr."+SEPARADOR+"Dif. Vlr.\n");

		for (final PreviaDiariaFaturamentoVO pdf : colecao) {
			
			String quantidadeTetoStr = "";
			if (pdf.getQuantidadeTeto() != null) {
				quantidadeTetoStr = pdf.getQuantidadeTeto().toString();
			}
			
			String diferencaQuantidadeTetoStr = "";
			if (pdf.getDiferencaQuantidadeTeto() != null) {
				diferencaQuantidadeTetoStr = pdf.getDiferencaQuantidadeTeto().toString();
			}

			String valorTetoStr = "";
			if (pdf.getValorTeto() != null) {
				valorTetoStr = AghuNumberFormat.formatarNumeroMoeda(pdf.getValorTeto());
			}
			
			String diferencaValorTetoStr = "";
			if (pdf.getDiferencaValorTeto() != null) {
				diferencaValorTetoStr = AghuNumberFormat.formatarNumeroMoeda(pdf.getDiferencaValorTeto());
			}
			
			out.write( (pdf.getCodCentroCusto()   	!= null ? pdf.getCodCentroCusto() : "") 						 + SEPARADOR +
					   (pdf.getDescCentroCusto()  	!= null ? pdf.getDescCentroCusto().replaceAll(";", ",") : "")  + SEPARADOR +
					   (pdf.getCodUnidade()       	!= null ? pdf.getCodUnidade() : "") 							 + SEPARADOR +
					   (pdf.getDescUnidade()      	!= null ? pdf.getDescUnidade().replaceAll(";", ",") : "") 	 + SEPARADOR + 
					   (pdf.getGenerica() 		  	!= null ? pdf.getGenerica().replaceAll(";", ",") : "") 		 + SEPARADOR +
					   (pdf.getAgenda() 		  	!= null ? pdf.getAgenda().replaceAll(";", ",") : "") 			 + SEPARADOR +
					   (pdf.getGrupo() 			  	!= null ? pdf.getGrupo() : "") 								 + SEPARADOR +
					   (pdf.getSubGrupo() 		  	!= null ? pdf.getSubGrupo() : "") 							 + SEPARADOR +
					   (pdf.getFogCod() 		  	!= null ? pdf.getFogCod() : "") 							 + SEPARADOR +
					   (pdf.getSsm() 			  	!= null ? pdf.getSsm() : "") 									 + SEPARADOR +
					   (pdf.getDescrSSM() 		  	!= null ? pdf.getDescrSSM().replaceAll(";", ",") : "") 		 + SEPARADOR +
					   (pdf.getPhi() 			  	!= null ? pdf.getPhi() : "") 									 + SEPARADOR +
					   (pdf.getDescrPhi() 		  	!= null ? pdf.getDescrPhi().replaceAll(";", ",") : "") 		 + SEPARADOR +
					   (pdf.getFcfSeq() 		  	!= null ? pdf.getFcfSeq() : "") 								 + SEPARADOR +
					   (pdf.getFinanciamento()    	!= null ? pdf.getFinanciamento().replaceAll(";", ",") : "") 	 + SEPARADOR +
					   (pdf.getFccSeq() 		  	!= null ? pdf.getFccSeq() : "") 								 + SEPARADOR +
					   (pdf.getComplexibilidade() 	!= null ? pdf.getComplexibilidade().replaceAll(";", ",") : "") + SEPARADOR +
					   (pdf.getTotalAnestesia()   	!= null ? AghuNumberFormat.formatarNumeroMoeda(pdf.getTotalAnestesia()) : 0) + SEPARADOR +
					   (pdf.getTotalServProf() 	  	!= null ? AghuNumberFormat.formatarNumeroMoeda(pdf.getTotalServProf())  : 0) + SEPARADOR +
					   (pdf.getQuantidade() 	  	!= null ? pdf.getQuantidade() : 0) + SEPARADOR + 
					   (pdf.getTotal() 			  	!= null ? AghuNumberFormat.formatarNumeroMoeda(pdf.getTotal()) 		  : 0) + SEPARADOR + 
					   quantidadeTetoStr			+ SEPARADOR + 
					   diferencaQuantidadeTetoStr	+ SEPARADOR +
					   valorTetoStr				+ SEPARADOR +
					   diferencaValorTetoStr		+ "\n"
					);
		}

		out.flush();
		out.close();
		
		return file.getAbsolutePath();
	}

	@SuppressWarnings("PMD.NPathComplexity")
	public String geraCSVRelatorioFaturaAmbulatorio(final FatCompetencia competencia) throws IOException, ApplicationBusinessException{
		
		final List<FaturaAmbulatorioVO> colecao = getFaturamentoAmbulatorioON().listarFaturamentoAmbulatorioPorCompetencia(competencia.getId().getMes(), competencia.getId().getAno(), competencia.getId().getDtHrInicio());

		final File file = File.createTempFile(DominioNomeRelatorio.FATR_FATURA_AMB.toString(), EXTENSAO);
		
		final Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);

		out.write("Período"+SEPARADOR+"Cód. Financiamento"+SEPARADOR+"Descrição Financiamento"+SEPARADOR+"Cód. Grupo"+SEPARADOR+"Descrição Grupo"+SEPARADOR+
				"Cód. Sub-Grupo"+SEPARADOR+"Descrição Sub-Grupo"+SEPARADOR+"Cód. Forma de Organização"+SEPARADOR+"Descrição Forma de Organização"+SEPARADOR+
				  "Procedimento"+SEPARADOR+"Quantidade"+SEPARADOR+"Valor"+SEPARADOR+"Teto Qtd."+SEPARADOR+"Dif. Qtd."+SEPARADOR+"Teto Vlr."+ SEPARADOR+
				  "Dif. Vlr."+"\n");

		for (final FaturaAmbulatorioVO pdf : colecao) {
			
			String diferencaQuantidadeTetoStr = "";
			if (pdf.getDiferencaQuantidadeTeto() != null) {
				diferencaQuantidadeTetoStr = pdf.getDiferencaQuantidadeTeto().toString();
			}
			
			String diferencaValorTetoStr = "";
			if (pdf.getDiferencaValorTeto() != null) {
				diferencaValorTetoStr = AghuNumberFormat.formatarNumeroMoeda(pdf.getDiferencaValorTeto());
			}			
			
			out.write( DateUtil.obterDataFormatada(competencia.getId().getDtHrInicio(), DateConstants.DATE_PATTERN_DDMMYYYY) + (competencia.getDtHrFim() != null ? " À " + DateUtil.obterDataFormatada(competencia.getDtHrFim(), DateConstants.DATE_PATTERN_DDMMYYYY) : "") + SEPARADOR +
					   (pdf.getCaracteristicaFinanciamentoSeq()  != null ? pdf.getCaracteristicaFinanciamentoSeq() : "")  + SEPARADOR +
					   (pdf.getCaracteristicaFinanciamentoSeq()  != null ? pdf.getCaracteristicaFinanciamento().replaceAll(";", ",") : "")  + SEPARADOR +
					   (pdf.getGrupoSeq()  != null ? pdf.getGrupoSeq() : "")  + SEPARADOR +
					   (pdf.getGrupo()  != null ? pdf.getGrupo().replaceAll(";", ",") : "")  + SEPARADOR +
					   (pdf.getSubGrupoSeq()  != null ? pdf.getSubGrupoSeq() : "")  + SEPARADOR +
					   (pdf.getSubGrupo()  != null ? pdf.getSubGrupo().replaceAll(";", ",") : "")  + SEPARADOR +
					   (pdf.getFormaOrganizacaoCodigo() != null ? pdf.getFormaOrganizacaoCodigo() : "")  + SEPARADOR +
					   (pdf.getFormaOrganizacao()  != null ? pdf.getFormaOrganizacao().replaceAll(";", ",") : "")  + SEPARADOR +
					   (pdf.getProcedimentoHospitalar() 		  != null ? pdf.getProcedimentoHospitalar() : "")  + SEPARADOR +
					   (pdf.getQuantidade() 	!= null ? pdf.getQuantidade() : "") + SEPARADOR +
					   (pdf.getValorProcedimento() 		  != null ? AghuNumberFormat.formatarNumeroMoeda(pdf.getValorProcedimento()) : "") + SEPARADOR +
					   (pdf.getQuantidadeTeto()			  != null ? pdf.getQuantidadeTeto() : "")  + SEPARADOR +
					   diferencaQuantidadeTetoStr 		+ SEPARADOR +
					   (pdf.getValorTeto()		  != null ? AghuNumberFormat.formatarNumeroMoeda(pdf.getValorTeto()) : "") + SEPARADOR +
					   diferencaValorTetoStr 	 + "\n"
			         );
		}

		out.flush();
		out.close();
		
		return file.getAbsolutePath();
	}

	public String geraCSVRelatorioLogInconsistenciaBPA(final DominioModuloMensagem modulo, final DominioSituacao situacao) throws IOException, ApplicationBusinessException{
		
		AghParametros param = null;
//		Short pTipoGrupoContaSUS = null;
		Short pCpgCphCspSeq = null;
		Short pCpgCphCspCnvCodigo = null;
		Short pIphPhoSeq = null;
		
		final IParametroFacade parametroFacade = getParametroFacade(); 
//		if(pTipoGrupoContaSUS == null){
//			param = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS);
//			if(param != null){
//				pTipoGrupoContaSUS = param.getVlrNumerico().shortValue(); 
//			}
//		}
		
		if(pCpgCphCspSeq == null){
			param = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SUS_PLANO_AMBULATORIO);
			if(param != null){
				pCpgCphCspSeq = param.getVlrNumerico().shortValue(); 
			}
		}
		
		if(pCpgCphCspCnvCodigo == null){
			param = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_CONVENIO_PADRAO);
			if(param != null && param.getVlrNumerico() != null){
				pCpgCphCspCnvCodigo = param.getVlrNumerico().shortValue(); 
			} else {
				pCpgCphCspCnvCodigo = Short.valueOf("1");
			}
		}
		
		if(pIphPhoSeq == null){
			param = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_TABELA_FATUR_PADRAO);
			if(param != null){
				pIphPhoSeq = param.getVlrNumerico().shortValue(); 
			}
		}
		
		final DominioModuloCompetencia[] modulos = {DominioModuloCompetencia.AMB, DominioModuloCompetencia.MAMA, DominioModuloCompetencia.SIS};
		final String[] erros = {"NAO ENCONTROU CBO DO RESPONSAVEL","NAO ENCONTROU CNS DO RESPONSAVEL", "CBO PROFISSIONAL INCOMPATIVEL COM CBO PROCEDIMENTO"};
		final String erro = "NAO ENCONTROU CNS";
		
		final List<String> colecao = getFaturamentoFacade().obterLogInconsistenciaBPACSV( modulos, erros, erro , 
																						  situacao, pCpgCphCspSeq, 
																						  pCpgCphCspCnvCodigo, pIphPhoSeq);

		final File file = File.createTempFile(DominioNomeRelatorio.FATR_AMB_LOG_MSG.toString(), EXTENSAO);
		
		final Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);

		out.write("Erro"+SEPARADOR+"Vinculo Prof"+SEPARADOR+"Matrícula Prof"+SEPARADOR+"Nome Prof"+SEPARADOR+
				  "CBO Princ Prof"+SEPARADOR+"Grade"+SEPARADOR+
				  "Seq Ambulatório"+SEPARADOR+"Solicitação"+SEPARADOR+"Item"+SEPARADOR+PHI+SEPARADOR+
				  "IPH"+SEPARADOR+"Cod Tabela"+SEPARADOR+"Cod Procedimento Unif"+SEPARADOR+"Descrição IPH"+SEPARADOR+
				  "Unidade"+SEPARADOR+"Cod paciente"+SEPARADOR+"Prontuario"+SEPARADOR+PACIENTE+SEPARADOR+"Data do Realizado\n");

		if(colecao != null && !colecao.isEmpty()){
			for (final String log : colecao) {
				out.write(log+ "\n" );
			}
		}

		out.flush();
		out.close();
		
		return file.getAbsolutePath();
	}
	
	public String geraCSVRelatorioRelacaoDeOPMNaoFaturada(final Long procedimento, final Integer ano, final Integer mes, final Date dtHrInicio, final Long SSM, final String iniciaisPaciente, final Boolean reapresentada) throws IOException, ApplicationBusinessException{
		final List<RelacaoDeOPMNaoFaturadaVO> colecao = getFaturamentoFacade().obterRelacaoDeOPMNaoFaturada(procedimento, ano, mes, dtHrInicio, SSM, iniciaisPaciente, reapresentada);

		final File file = File.createTempFile(DominioNomeRelatorio.FATR_INT_OPM_NAO_FAT.toString(), EXTENSAO);
		
		
		final Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);
		
		out.write(PACIENTE+SEPARADOR+"Leito"+SEPARADOR+AIH+SEPARADOR+"SSM"+SEPARADOR+"Material"+SEPARADOR+"Unid"+SEPARADOR+"Data Utl"+SEPARADOR+"Qtde"+SEPARADOR+"Valor\n");
		
		
		String especialidade = null;
		String nomePaciente = null;
		
		double valorApreGroup = 0.0;
		long countQtdeGroup = 0;
		long countAIHSGroup = 0;
		

		double valorApreRel = 0.0;
		long countQtdeRel = 0;
		long countAIHSRel = 0;
		
		
		boolean firstLoop = true;
		
		List<Long> aihsEspecialidade = null;
		for (final RelacaoDeOPMNaoFaturadaVO pojo : colecao) {
			countQtdeRel += pojo.getQuantidade();  valorApreRel += pojo.getValorapres();
			
			if(!pojo.getEspecialidade().equals(especialidade)){
				aihsEspecialidade = new ArrayList<Long>();

				if(!firstLoop){
					out.write(SEPARADOR+SEPARADOR+SEPARADOR+"Totais por Especialidade - AIH: "+SEPARADOR+ countAIHSGroup+SEPARADOR+"Quantidade:"+SEPARADOR+countQtdeGroup+SEPARADOR+"Valor:"+SEPARADOR+AghuNumberFormat.formatarNumeroMoeda(valorApreGroup)+NLNL);
					countQtdeGroup=0; countAIHSGroup=0; valorApreGroup =0;
				}
				
				
				firstLoop = false;
				out.write("Especialidade:"+SEPARADOR+pojo.getEspecialidade()+"\n");
				especialidade = pojo.getEspecialidade();
			}
				
			if(pojo.getEspecialidade().equals(especialidade) && pojo.getNroaih() != null && !aihsEspecialidade.contains(pojo.getNroaih())){
				aihsEspecialidade.add(pojo.getNroaih());
				countAIHSRel++;
				countAIHSGroup++;
			}
			
			countQtdeGroup += pojo.getQuantidade();  valorApreGroup += pojo.getValorapres();
			
			if(!pojo.getPacnome().equals(nomePaciente)){
				out.write( pojo.getProntuario() + pojo.getPacnome() +SEPARADOR+
						   pojo.getLeito() +SEPARADOR+
						   (pojo.getNroaih() != null ? pojo.getNroaih() : "") +SEPARADOR+
						   pojo.getSsm() +SEPARADOR+
						   pojo.getCodtabela() + " " + pojo.getDescricao() + SEPARADOR+
						   (pojo.getUnfseq() != null ? pojo.getUnfseq() : "") +SEPARADOR+
						   DateUtil.obterDataFormatada(pojo.getDatautl(), DD_MM_YYYY) +SEPARADOR+
						   pojo.getQuantidade() +SEPARADOR+
						   pojo.getValorapres() + "\n"
						  );
				
				nomePaciente = pojo.getPacnome();
			} else {
				out.write( SEPARADOR+
						   pojo.getLeito() +SEPARADOR+
						   (pojo.getNroaih() != null ? pojo.getNroaih() : "") +SEPARADOR+
						   pojo.getSsm() +SEPARADOR+
						   pojo.getCodtabela() + " " + pojo.getDescricao() + SEPARADOR+
						   (pojo.getUnfseq() != null ? pojo.getUnfseq() : "") +SEPARADOR+
						   DateUtil.obterDataFormatada(pojo.getDatautl(), DD_MM_YYYY) +SEPARADOR+
						   pojo.getQuantidade() +SEPARADOR+
						   pojo.getValorapres() + "\n"
						  );
			}
		}
		
		out.write(SEPARADOR+SEPARADOR+SEPARADOR+"Totais por Especialidade - AIH: "+SEPARADOR+ countAIHSGroup+SEPARADOR+"Quantidade:"+SEPARADOR+countQtdeGroup+SEPARADOR+"Valor:"+SEPARADOR+AghuNumberFormat.formatarNumeroMoeda(valorApreGroup)+NLNL);
		out.write(SEPARADOR+SEPARADOR+SEPARADOR+SEPARADOR+SEPARADOR+SEPARADOR+"T O T A I S -"+SEPARADOR+"Quantidade:"+SEPARADOR+countQtdeRel+"\n");
		out.write(SEPARADOR+SEPARADOR+SEPARADOR+SEPARADOR+SEPARADOR+SEPARADOR+SEPARADOR+"AIH:"+SEPARADOR+countAIHSRel+"\n");
		out.write(SEPARADOR+SEPARADOR+SEPARADOR+SEPARADOR+SEPARADOR+SEPARADOR+SEPARADOR+"VALOR:"+SEPARADOR+AghuNumberFormat.formatarNumeroMoeda(valorApreRel)+"\n");
		
		out.flush();
		out.close();
		
		return file.getAbsolutePath();
	}

	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public String geraCSVRelatorioValoresAIHPorDCIH(final Integer ano, final Integer mes) throws IOException{
		final List<ValoresAIHPorDCIHVO> list = getFaturamentoFacade().obterValoresAIHPorDCIH(ano, mes);

		final File file = File.createTempFile(DominioNomeRelatorio.FATR_INT_DCIH_AIH.toString(), EXTENSAO);
		
		
		final Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);
		
		
		out.write( "REAP."+SEPARADOR+AIH+SEPARADOR+"PRONT."+SEPARADOR+"PROCED."+SEPARADOR+"ALTA"+SEPARADOR+"PER"+SEPARADOR+"UTI"+SEPARADOR+"AC"+SEPARADOR+"H"+SEPARADOR+"SVCO.HOSP."+SEPARADOR+"SVCO.PROF."+SEPARADOR+"S A D T"+SEPARADOR+"PRÓTESE"+SEPARADOR+"QUADRO MÉDICO AUDITOR\n" );
		
		String dcih = null;
		int totAIH = 0;
		double sHosp = 0;
		double sProf = 0;
		double sSadt = 0;
		double sProtese = 0;
		
		int totAIHRel = 0;
		double sHospRel = 0;
		double sProfRel = 0;
		double sSadtRel = 0;
		double sProteseRel = 0;
		double totalRel = 0;
		
		boolean firstLoop = true;
		for (final ValoresAIHPorDCIHVO registro : list) {
			if(dcih == null || !registro.getDcih().equalsIgnoreCase(dcih)){
				if(!firstLoop){
					out.write( "QTD AIH" + SEPARADOR + totAIH + SEPARADOR+ SEPARADOR+ SEPARADOR+ SEPARADOR+ SEPARADOR+ SEPARADOR+ SEPARADOR + SEPARADOR +  
							AghuNumberFormat.formatarNumeroMoeda(sHosp) + SEPARADOR +
								AghuNumberFormat.formatarNumeroMoeda(sProf) + SEPARADOR +
								AghuNumberFormat.formatarNumeroMoeda(sSadt) + SEPARADOR +
								AghuNumberFormat.formatarNumeroMoeda(sProtese) + SEPARADOR +
								AghuNumberFormat.formatarNumeroMoeda(sHosp+sProf+sSadt+sProtese) + SEPARADOR + NLNL);
					
					totAIHRel += totAIH;
					sHospRel += sHosp; 
					sProfRel += sProf; 
					sSadtRel += sSadt; 
					sProteseRel += sProtese;
					totalRel += sHosp+sProf+sSadt+sProtese;
				}
				firstLoop = false;
				sHosp = 0; sProf = 0; sSadt = 0; sProtese = 0; totAIH = 0;
				
				dcih = registro.getDcih();
				out.write(SEPARADOR+"DCIH"+SEPARADOR+ dcih + SEPARADOR + registro.getTipo() + SEPARADOR + registro.getDescricao() + "\n" );
			}
			
			if(registro.getNroaih() != null){
				totAIH++;
			}
			
			sHosp += ( registro.getServhosp() != null ? registro.getServhosp() : 0); 
			sProf += ( registro.getServprof() != null ? registro.getServprof() : 0); 
			sSadt += ( registro.getSadt() != null ? registro.getSadt() : 0); 
			sProtese += (registro.getProtese() != null ? registro.getProtese() : 0); 
			
			out.write( (registro.getReapresentada() != null ? "R" : "") +
					     SEPARADOR +
					     (registro.getNroaih() != null ? registro.getNroaih() : "" )+
					     SEPARADOR+
					     (registro.getProntuario() != null ? registro.getProntuario() : "")+
					     SEPARADOR+
					     (registro.getProcedimento() != null ? registro.getProcedimento() : "" )+
					     SEPARADOR+
					     DateUtil.obterDataFormatada(registro.getAlta(), "mm/yyyy") +
					     SEPARADOR+
					     (registro.getPer() != null ? registro.getPer() : "")+
					     SEPARADOR+
					     (registro.getUti() != null ? registro.getUti() : "")+
					     SEPARADOR+
					     (registro.getAcomp() != null ? registro.getAcomp() : "")+
					     SEPARADOR+
					     (registro.getHem() > 0  ? "H" : " ")+
					     SEPARADOR+
					     AghuNumberFormat.formatarNumeroMoeda(registro.getServhosp())+
					     SEPARADOR+
					     AghuNumberFormat.formatarNumeroMoeda(registro.getServprof())+
					     SEPARADOR+
					     AghuNumberFormat.formatarNumeroMoeda(registro.getSadt())+
					     SEPARADOR+
					     AghuNumberFormat.formatarNumeroMoeda(registro.getProtese())+
					     SEPARADOR+
					     (registro.getIphcodsus() != null ? registro.getIphcodsus() : "")+
					     FIM_NL
				     ); 
			
		}

		out.write( "QTD AIH" + SEPARADOR + totAIH + SEPARADOR+ SEPARADOR+ SEPARADOR+ SEPARADOR+ SEPARADOR+ SEPARADOR+ SEPARADOR +  SEPARADOR +
				AghuNumberFormat.formatarNumeroMoeda(sHosp) + SEPARADOR +
				AghuNumberFormat.formatarNumeroMoeda(sProf) + SEPARADOR +
				AghuNumberFormat.formatarNumeroMoeda(sSadt) + SEPARADOR +
				AghuNumberFormat.formatarNumeroMoeda(sProtese) + SEPARADOR +
				AghuNumberFormat.formatarNumeroMoeda(sHosp+sProf+sSadt+sProtese) + SEPARADOR + "\n");
	
		totAIHRel += totAIH;
		sHospRel += sHosp; 
		sProfRel += sProf; 
		sSadtRel += sSadt; 
		sProteseRel += sProtese;
		totalRel += sHosp+sProf+sSadt+sProtese;
		
		out.write("QTD TOT" + SEPARADOR + totAIHRel + SEPARADOR+ SEPARADOR+ SEPARADOR+ SEPARADOR+ SEPARADOR+ SEPARADOR+ SEPARADOR +  SEPARADOR +
				AghuNumberFormat.formatarNumeroMoeda(sHospRel) + SEPARADOR +
					AghuNumberFormat.formatarNumeroMoeda(sProfRel) + SEPARADOR +
					AghuNumberFormat.formatarNumeroMoeda(sSadtRel) + SEPARADOR +
					AghuNumberFormat.formatarNumeroMoeda(sProteseRel) + SEPARADOR +
					AghuNumberFormat.formatarNumeroMoeda(totalRel) + SEPARADOR);
		
		out.flush();
		out.close();
		
		return file.getAbsolutePath();
	}
	
	
	public String geraCSVRelatorioAihsFaturadasPorClinica(
			final Integer ano, 
			final Integer mes,
			final Date dtHrInicio, 
			final String iniciaisPaciente) throws IOException, ApplicationBusinessException{
		
		//Integer totalQtde = 0;
		//Double totalApres = (double)0;

		if (!CoreUtil.validaIniciaisPaciente(iniciaisPaciente)) {
			throw new ApplicationBusinessException(RelatorioCSVFaturamentoONExceptionCode.INICIAIS_INVALIDAS_ORTESES_PROTESES);
		} 
		
		final List<AihsFaturadasPorClinicaVO> colecao = getFaturamentoFacade().obterAihsFaturadasPorClinica(ano, mes, dtHrInicio, iniciaisPaciente);
		
		if(colecao == null || colecao.isEmpty()){
			throw new ApplicationBusinessException(RelatorioCSVFaturamentoONExceptionCode.NENHUM_REGISTRO_ORTESES_PROTESES);
		}
		
		final File file = File.createTempFile(DominioNomeRelatorio.FATR_INT_AIH_CLINICA.toString(), EXTENSAO);
		
		final Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);
		out.write(geraCabecalhoRelatorioAihsFaturadasPorClinica());
		
		String dcih = null;
		String vazio = " ";
		
		for (AihsFaturadasPorClinicaVO item : colecao) {
			if(item.getDcih().equals(dcih)){
				item.setDcih(vazio);
			}else{
				dcih = item.getDcih();
			}
			
			out.write(geraLinhaRelatorioAihsFaturadasPorClinica(item));
		}
		//DecimalFormat df = new DecimalFormat("#,###.00");  
		//String totalApresString = df.format(totalApres);   
		
		//out.write(geraLinhaTotaisRelacaoOrtesesProteses(totalQtde,totalApresString));
		
		out.flush();
		out.close();
		
		return file.getAbsolutePath();
	}
	
	private String geraCabecalhoRelatorioAihsFaturadasPorClinica() {
		StringBuffer cabecalho = new StringBuffer(300);
		
		cabecalho.append("DCIH")
			     .append(SEPARADOR)
			     .append("CLÍNICA")
			     .append(SEPARADOR)
			     .append("PRONTUARIO")
			     .append(SEPARADOR)
			     .append("NOME DO PACIENTE")
			     .append(SEPARADOR)
			     .append("INTERNAÇÃO")
			     .append(SEPARADOR)
			     .append("ALTA")
			     .append(SEPARADOR)
			     .append(AIH)
			     .append(SEPARADOR)
			     .append("SSM")
			     .append(SEPARADOR)
			     .append('\n');
		
		return cabecalho.toString();
	}
	
	private String geraLinhaRelatorioAihsFaturadasPorClinica(final AihsFaturadasPorClinicaVO registro){
		final StringBuffer linha = new StringBuffer(300);
		String vazio = " ";
			linha.append(verificaNull(registro.getDcih()))
			.append(SEPARADOR);
			if(registro.getCodcli() != null && registro.getDesccli() != null){
				String  cliente = registro.getCodcli().toString()+_HIFEN_+registro.getDesccli();
				linha.append(cliente);
			}else{
				linha.append(vazio);
			}
		    linha.append(SEPARADOR)
		    .append(registro.getProntuario() != null ? CoreUtil.formataProntuario(registro.getProntuario()) :  vazio)
		    .append(SEPARADOR)
		    .append(verificaNull(registro.getPacnome()))
		    .append(SEPARADOR)
		    .append(registro.getDtint() != null ? DateUtil.obterDataFormatada(registro.getDtint() , "dd/MM/yy") : vazio)
		    .append(SEPARADOR)
		    .append(registro.getDtalta() != null ? DateUtil.obterDataFormatada(registro.getDtalta() , "dd/MM/yy") : vazio)
		    .append(SEPARADOR)
		    .append(verificaNull(registro.getAih()))
		    .append(SEPARADOR)
		    .append(verificaNull(registro.getSsmrealiz()))
		    .append(SEPARADOR)

		.append(FIM_NL); 
		
		return linha.toString();
	}
	
	public String geraCSVRelatorioRelacaoOrtesesProteses(final Long procedimento, 
			final Integer ano, 
			final Integer mes,
			final Date dtHrInicio, 
			final String iniciaisPaciente,
			final Date dtIni,
			final Date dtFim) throws IOException, ApplicationBusinessException{
		
		Integer totalQtde = 0;
		BigDecimal totalApres = BigDecimal.ZERO;

		if (!CoreUtil.validaIniciaisPaciente(iniciaisPaciente)) {
			throw new ApplicationBusinessException(RelatorioCSVFaturamentoONExceptionCode.INICIAIS_INVALIDAS_ORTESES_PROTESES);
		} 

		final List<RelacaoDeOrtesesProtesesVO> colecao = getFaturamentoFacade().obterRelacaoDeOrtesesProteses(procedimento, ano, mes, dtHrInicio, iniciaisPaciente, dtIni, dtFim);
		
		if(colecao == null || colecao.isEmpty()){
			throw new ApplicationBusinessException(RelatorioCSVFaturamentoONExceptionCode.NENHUM_REGISTRO_ORTESES_PROTESES);
		}
		
		final File file = File.createTempFile(DominioNomeRelatorio.FATR_INT_OPM.toString(), EXTENSAO);
		
		final Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);
		out.write(geraCabecalhoRelatorioRelacaoOrtesesProteses());
		
		for (RelacaoDeOrtesesProtesesVO relacaoDeOrtesesProtesesVO : colecao) {
			out.write(geraLinhaRelacaoOrtesesProteses(relacaoDeOrtesesProtesesVO));
			if(relacaoDeOrtesesProtesesVO.getQtde() != null){
				totalQtde++;
			}
			if(relacaoDeOrtesesProtesesVO.getValorapres() != null){
				totalApres = totalApres.add(relacaoDeOrtesesProtesesVO.getValorapres());
			}
		}
		DecimalFormat df = new DecimalFormat("#,###.00");  
		String totalApresString = df.format(totalApres);   
		
		out.write(geraLinhaTotaisRelacaoOrtesesProteses(totalQtde,totalApresString));
		
		out.flush();
		out.close();
		
		return file.getAbsolutePath();
	}
	
	public File geraCSVRelatorioNutricaoEnteralDigitada(String data1, String data2,FatCompetenciaId id) throws IOException, ApplicationBusinessException{

		final List<NutricaoEnteralDigitadaVO> colecao = fatProcedHospInternosDAO.getNutricaoEnteralDigitada();
		
		if(colecao == null || colecao.isEmpty()){
			throw new ApplicationBusinessException(RelatorioCSVFaturamentoONExceptionCode.NENHUM_REGISTRO_ORTESES_PROTESES);
		}
		
		final File file = File.createTempFile("ENTERAL_DIG_"+id.getMes()+"_"+id.getAno(), EXTENSAO);
		
		final Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);
		out.write(geraCabecalhoNutricaoEnteralDigitada());
		
		for (NutricaoEnteralDigitadaVO item : colecao) {
			out.write(geraLinhaNutricaoEnteralDigitada(item));
		}
		
		out.flush();
		out.close();
		
		return file;
	}
	
	public File geraCSVContas(String data1, String data2,FatCompetenciaId id) throws IOException, ApplicationBusinessException{

		final List<ContasNPTVO> colecao = fatProcedHospInternosDAO.getContasNPT(data1,data2);
		
		if(colecao == null || colecao.isEmpty()){
			throw new ApplicationBusinessException(RelatorioCSVFaturamentoONExceptionCode.NENHUM_REGISTRO_ORTESES_PROTESES);
		}
		
		final File file = File.createTempFile("NPT_"+id.getMes()+"_"+id.getAno(), EXTENSAO);
		
		final Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);
		out.write(geraCabecalhoContas());
		
		for (ContasNPTVO item : colecao) {
			out.write(geraLinhaContas(item));
		}
		
		out.flush();
		out.close();
		
		return file;
	}
	
	private String geraCabecalhoNutricaoEnteralDigitada() {
		StringBuffer cabecalho = new StringBuffer(300);
		
		cabecalho.append("Conta")
			     .append(SEPARADOR)
			     .append("Data Encerramento")
			     .append(SEPARADOR)
			     .append("Número AIH")
			     .append(SEPARADOR)
			     .append("Situação")
			     .append(SEPARADOR)
			     .append('\n');
		
		return cabecalho.toString();
	}
	
	private String geraLinhaNutricaoEnteralDigitada(NutricaoEnteralDigitadaVO item) {
		StringBuffer cabecalho = new StringBuffer(300);
		
		
		
		cabecalho.append(item.getSeq() != null ? item.getSeq() : " ")
			     .append(SEPARADOR)
			     .append(item.getDtencerramento() != null ? DateUtil.obterDataFormatada(item.getDtencerramento(), "dd/MM/yyy hh:mm") : " ")
			     .append(SEPARADOR)
			     .append(item.getNroaih() != null ? item.getNroaih() : " ")
			     .append(SEPARADOR)
			     .append(item.getIndsituacao() != null ? item.getIndsituacao() : " ")
			     .append(SEPARADOR)
			     .append('\n');
		
		return cabecalho.toString();
	}
	
	private String geraCabecalhoContas() {
		StringBuffer cabecalho = new StringBuffer(300);
		
		cabecalho.append("Conta")
			     .append(SEPARADOR)
			     .append("Autorizada SMS")
			     .append(SEPARADOR)
			     .append("Prontuário")
			     .append(SEPARADOR)
			     .append("Paciente")
			     .append(SEPARADOR)
			     .append("Dias Int")
			     .append(SEPARADOR)
			     .append("Qtde")
			     .append(SEPARADOR)
			     .append('\n');
		
		return cabecalho.toString();
	}
	
	private String geraLinhaContas(ContasNPTVO item) {
		StringBuffer cabecalho = new StringBuffer(300);
		
		Integer integerData = null;
		if(item.getDtalta() != null && item.getDtint() != null){
			integerData = DateUtil.obterQtdDiasEntreDuasDatas(item.getDtint(),item.getDtalta()); // DIFERENÇA ENTRE DATAS 
		}
		String vazio = " ";
		
		if(item.getConta() != null){
	    	 cabecalho.append(item.getConta()); 
	     }else{
	    	 cabecalho.append(vazio);
	     }
		cabecalho.append(SEPARADOR);
	     if(item.getAutorizadasms() != null){
	    	 cabecalho.append(item.getAutorizadasms()); 
	     }else{
	    	 cabecalho.append(vazio);
	     }
	    cabecalho.append(SEPARADOR)
			     .append(item.getProntuario() != null ? item.getProntuario() : " ")
			     .append(SEPARADOR);
	     if(item.getNome() != null){
	    	 cabecalho.append(item.getNome()); 
	     }else{
	    	 cabecalho.append(vazio);
	     }
	    cabecalho.append(SEPARADOR)
			     .append(integerData) 
			     .append(SEPARADOR)
			     .append(item.getQtde() != null ? item.getQtde() : " ")
			     .append(SEPARADOR)
			     .append('\n');
		
		return cabecalho.toString();
	}

	private String geraCabecalhoRelatorioRelacaoOrtesesProteses() {
		StringBuffer cabecalho = new StringBuffer(300);
		
		cabecalho.append("NOME PACIENTE")
			     .append(SEPARADOR)
			     .append("NRO CUM")
			     .append(SEPARADOR)
			     .append("DATA UTL")
			     .append(SEPARADOR)
			     .append("CGC FORNECEDOR")
			     .append(SEPARADOR)
			     .append("PRONTUÁRIO")
			     .append(SEPARADOR)
			     .append("COD.ROPM")
			     .append(SEPARADOR)
			     .append("COD.PPROC")
			     .append(SEPARADOR)
			     .append("LEITO")
			     .append(SEPARADOR)
			     .append(AIH)
			     .append(SEPARADOR)
			     .append("QTDE")
			     .append(SEPARADOR)
			     .append("VALOR APRES")
			     .append(SEPARADOR)
			     .append('\n');
		
		return cabecalho.toString();
	}

	private String geraLinhaRelacaoOrtesesProteses(final RelacaoDeOrtesesProtesesVO registro){
		final StringBuffer linha = new StringBuffer(300);

		linha.append(verificaNull(registro.getPacnome()))
		     .append(SEPARADOR)
		     .append(verificaNull(registro.getSrmseq()))
		     .append(SEPARADOR)
		     .append(verificaNull(registro.getDatautl()))
		     .append(SEPARADOR)
		     .append(registro.getCgcfornecedor() != null ? CoreUtil.formatarCNPJ(registro.getCgcfornecedor()) :  " ")
		     .append(SEPARADOR)
		     .append(registro.getProntuario() != null ? CoreUtil.formataProntuario(registro.getProntuario()) :  " ")
		     .append(SEPARADOR)
		     .append(verificaNull(registro.getCodropm()))
		     .append(SEPARADOR)
		     .append(verificaNull(registro.getCodproc()))
		     .append(SEPARADOR)
		     .append(verificaNull(registro.getLeito()))
		     .append(SEPARADOR)
		     .append(verificaNull(registro.getAih()))
		     .append(SEPARADOR)
		     .append(verificaNull(registro.getQtde()))
		     .append(SEPARADOR)
		     .append(registro.getValorapres() != null ? AghuNumberFormat.formatarNumeroMoeda(registro.getValorapres()) : " ")
		     .append(SEPARADOR)
	
		     .append(FIM_NL); 
		
		return linha.toString();
	}
	
	private String geraLinhaTotaisRelacaoOrtesesProteses(Integer qtde, String valorApres){
		final StringBuffer linha = new StringBuffer(300);
		String vazio = " ";
		linha.append(vazio)
        	.append(SEPARADOR)
        	.append(vazio)
        	.append(SEPARADOR)
        	.append(vazio)
        	.append(SEPARADOR)
        	.append(vazio)
        	.append(SEPARADOR)
        	.append(vazio)
        	.append(SEPARADOR)
        	.append(vazio)
        	.append(SEPARADOR)
        	.append(vazio)
        	.append(SEPARADOR)
        	.append(vazio)
        	.append(SEPARADOR)
        	.append(vazio)
        	.append(SEPARADOR)
        	.append(vazio)
        	.append(SEPARADOR)
        	.append(vazio)
        	.append(SEPARADOR)
        	.append(FIM_NL)
        	.append(vazio)
        	.append(SEPARADOR)
        	.append(vazio)
        	.append(SEPARADOR)
        	.append(vazio)
        	.append(SEPARADOR)
        	.append(vazio)
        	.append(SEPARADOR)
        	.append(vazio)
        	.append(SEPARADOR)
        	.append(vazio)
        	.append(SEPARADOR)
        	.append(vazio)
        	.append(SEPARADOR)
        	.append(vazio)
        	.append(SEPARADOR)
        	.append("TOTAL")
        	.append(SEPARADOR)
        	.append(qtde.toString())
        	.append(SEPARADOR)
        	.append(valorApres)
        	.append(SEPARADOR)
        	.append(FIM_NL)			 
			 .append(vazio)
		     .append(SEPARADOR)
		     .append(vazio)
		     .append(SEPARADOR)
		     .append(vazio)
		     .append(SEPARADOR)
		     .append(vazio)
			 .append(SEPARADOR)
			 .append(vazio)
		     .append(SEPARADOR)
		     .append(vazio)
		     .append(SEPARADOR)
		     .append(vazio)
		     .append(SEPARADOR)
		     .append("TOTAL")
		     .append(SEPARADOR)
		     .append(qtde.toString())
		     .append(SEPARADOR)
		     .append(vazio)
		     .append(SEPARADOR)
		     .append(valorApres)
			 .append(SEPARADOR)
			 .append(";\n"); 
		
		return linha.toString();
	}
	
	private String verificaNull(Object obj){
		if(obj == null){
			return " ";
		}
		return obj.toString();
	}
	
	public String geraCSVRelatorioTotaisDCIH(final FatCompetencia competencia) throws IOException{
		final List<TotaisPorDCIHVO> lst = getFatContasHospitalaresDAO().obterTotaisPorDCIH(competencia.getId().getDtHrInicio(), competencia.getId().getAno(), competencia.getId().getMes());
		
		final File file = File.createTempFile(DominioNomeRelatorio.FATR_INT_FAT_DCIH.toString(), EXTENSAO);
		
		final Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);
		out.write(geraCabecalhoRelatorioTotaisDCI());
		for (final TotaisPorDCIHVO registro : lst) {
			out.write(geraLinhaRelatorioTotaisDCI(registro));
		}
		out.flush();
		out.close();
		
		return file.getAbsolutePath();
	}
	
	
	private String geraCabecalhoRelatorioTotaisDCI() {
		StringBuffer cabecalho = new StringBuffer(100);
		
		cabecalho.append("DCIH")
			     .append(SEPARADOR)
			     .append("DESCRIÇÃO")
			     .append(SEPARADOR)
			     .append("QTD")
			     .append(SEPARADOR)
			     .append("SVCO.HOSP.")
			     .append(SEPARADOR)
			     .append("SVCO.PROF.")
			     .append(SEPARADOR)
			     .append("S A D T")
			     .append(SEPARADOR)
			     .append("HEMOTERAPIA")
			     .append(SEPARADOR)
			     .append("PRÓTESE")
			     .append(SEPARADOR)
			     .append("TOTAL")
			     .append(SEPARADOR)
			     .append('\n');
		
		return cabecalho.toString();
	}
	
	private String geraLinhaRelatorioTotaisDCI(final TotaisPorDCIHVO registro ){
		final StringBuffer linha = new StringBuffer();
		
		linha.append(registro.getDcih())
		     .append(SEPARADOR)
		     .append(registro.getDescricao())
		     .append(SEPARADOR)
		     .append(registro.getQtd())
		     .append(SEPARADOR)
		     .append(AghuNumberFormat.formatarNumeroMoeda(registro.getHosp()))
		     .append(SEPARADOR)
		     .append(AghuNumberFormat.formatarNumeroMoeda(registro.getProf()))
		     .append(SEPARADOR)
		     .append(AghuNumberFormat.formatarNumeroMoeda(registro.getSadt()))
		     .append(SEPARADOR)
		     .append(AghuNumberFormat.formatarNumeroMoeda(registro.getHemat()))
		     .append(SEPARADOR) 
		     .append(AghuNumberFormat.formatarNumeroMoeda(registro.getProte()))
		     .append(SEPARADOR)
		     .append(AghuNumberFormat.formatarNumeroMoeda(registro.getTotal())) 
		     .append(FIM_NL); 
		
		return linha.toString();
	}

	@SuppressWarnings("PMD.NPathComplexity")
	public String geraCSVRelatorioIntermediarioLancamentosConta(final Integer cthSeq) throws IOException, ApplicationBusinessException{
		Map<AghuParametrosEnum, AghParametros> parametros = new HashMap<>();
		parametros.put(AghuParametrosEnum.P_SUS_PLANO_INTERNACAO,  getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SUS_PLANO_INTERNACAO));
		parametros.put(AghuParametrosEnum.P_CONVENIO_SUS,  getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_CONVENIO_SUS));
		parametros.put(AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS,  getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS));
		
		final List<RelatorioIntermediarioLancamentosContaVO> colecao = getFaturamentoFacade().obterItensContaParaRelatorioIntermediarioLancamentos(cthSeq, parametros);
		final File file = File.createTempFile(cthSeq + DominioNomeRelatorio.RELATORIO_INTERMEDIARIO_LANCAMENTOS_CONTA.toString(), EXTENSAO);		
		final Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);
		
		out.write("Código SUS"+SEPARADOR+"Código PHI"+SEPARADOR+"Descrição"+SEPARADOR+"Quantidade"+SEPARADOR+"Data/hora Realiz."+SEPARADOR+"Unidade Realizadora"+SEPARADOR+"Vínculo Resp."+SEPARADOR+"Matrícula Resp."+SEPARADOR+"Vínculo Anest."+SEPARADOR+"Matrícula Anest."+"\n");

		for (final RelatorioIntermediarioLancamentosContaVO item : colecao) {
			out.write( (item.getCodsus() != null ? item.getCodsus() : "") + SEPARADOR + 
					   (item.getCodphi() != null ? item.getCodphi() : "") + SEPARADOR + 
					   (item.getDescricao() != null ? item.getDescricao() : "") + SEPARADOR + 
					   (item.getQuantidade() != null ? item.getQuantidade() : "") + SEPARADOR + 
					   (item.getDataHoraRealizado() != null ? item.getDataHoraRealizado() : "") + SEPARADOR + 
					   (item.getUnidadeRealizadora() != null ? item.getUnidadeRealizadora() : "") + SEPARADOR +
					   (item.getVinCodResponsavel() != null ? item.getVinCodResponsavel() : "") + SEPARADOR +
					   (item.getMatriculaResponsavel() != null ? item.getMatriculaResponsavel() : "") + SEPARADOR +
					   (item.getVinCodAnestesista() != null ? item.getVinCodAnestesista() : "") + SEPARADOR +
					   (item.getMatriculaAnestesista() != null ? item.getMatriculaAnestesista() : "") + SEPARADOR + "\n"
			         );
		}
		
		out.flush();
		out.close();
		
		return file.getAbsolutePath();
	}

	@SuppressWarnings("PMD.NPathComplexity")
	public String geraCSVRelatorioFaturamentoPorProcedimento(final FatCompetencia competencia) throws IOException, ApplicationBusinessException{
		final List<FaturamentoPorProcedimentoVO> colecao = getFaturamentoFacade().obterFaturamentoPorProcedimento(competencia.getId().getDtHrInicio(),competencia.getId().getAno(),competencia.getId().getMes());

		final File file = File.createTempFile(DominioNomeRelatorio.FATR_INT_FAT_PROD.toString(), EXTENSAO);
		
		final Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);
		
		out.write("Código SUS"+SEPARADOR+"Procedimento"+SEPARADOR+"Qtde"+SEPARADOR+"Serv Hosp"+SEPARADOR+"Serv Prof"+SEPARADOR+"Total\n");

		long sumQtAIH=0, sumQtdProc=0;
		double sumHospAIH=0, sumProfAIH=0, servHospProc=0, servProfProc=0;
		for (final FaturamentoPorProcedimentoVO item : colecao) {
			sumQtAIH   += item.getQtdAih();
			sumQtdProc += item.getQtdProc();
			
			sumHospAIH  += item.getHospAih();
			sumProfAIH += item.getProfAih();

			servHospProc += item.getServHospProc();
			servProfProc += item.getServProfProc();
			
			out.write( (item.getCodSus() != null ? item.getCodSus() : "") 	     + SEPARADOR +
					   (item.getDescricao() != null ? item.getDescricao() : "")  + SEPARADOR + 
					   (item.getQtd() != null ? item.getQtd() : "")  + SEPARADOR + 
					   AghuNumberFormat.formatarNumeroMoeda(item.getHosp()) + SEPARADOR +
					   AghuNumberFormat.formatarNumeroMoeda(item.getProf()) + SEPARADOR +
					   AghuNumberFormat.formatarNumeroMoeda(item.getHosp()+item.getProf()) + SEPARADOR + "\n"
			         );
		}
		
		final FaturamentoPorProcedimentoVO valores = getFaturamentoFacade().obterFaturamentoPorProcedimentoUTIEspelho(competencia.getId().getDtHrInicio(),competencia.getId().getAno(),competencia.getId().getMes());
		
		out.write( NLNL 	+ SEPARADOR + "AIH :" + SEPARADOR  +
				   sumQtAIH + SEPARADOR +
				   AghuNumberFormat.formatarNumeroMoeda(sumHospAIH) + SEPARADOR  +
				   AghuNumberFormat.formatarNumeroMoeda(sumProfAIH) + SEPARADOR  +
				   AghuNumberFormat.formatarNumeroMoeda((sumHospAIH+sumProfAIH)) +"\n");

		out.write( SEPARADOR  + "PROCED/DIÁRIAS ESPECIAS/HEMOT :" + SEPARADOR  +
				   sumQtdProc + SEPARADOR  +
				   AghuNumberFormat.formatarNumeroMoeda(servHospProc) + SEPARADOR  +
				   AghuNumberFormat.formatarNumeroMoeda(servProfProc) + SEPARADOR  +
				   AghuNumberFormat.formatarNumeroMoeda((servHospProc+servProfProc)) +"\n");
		
		out.write( SEPARADOR + "DIÁRIAS ACOMPANHANTE :" + SEPARADOR  +
				   (valores.getDiasAcomp() != null ? valores.getDiasAcomp() : 0)  + SEPARADOR  +
				   AghuNumberFormat.formatarNumeroMoeda(valores.getDiariaAcompServHosp()) + SEPARADOR  +
				   AghuNumberFormat.formatarNumeroMoeda(valores.getDiariaAcompServProf()) + SEPARADOR  +
				   AghuNumberFormat.formatarNumeroMoeda(valores.getValorAcomp()) +"\n");
		
		out.write( SEPARADOR + "DIÁRIAS UTI I :" + SEPARADOR  +
				   valores.getDiasUTI()  + SEPARADOR  +
				   AghuNumberFormat.formatarNumeroMoeda(valores.getDiariaUtiHosp()) + SEPARADOR  +
				   AghuNumberFormat.formatarNumeroMoeda(valores.getDiariaUtiProf()) + SEPARADOR  +
				   AghuNumberFormat.formatarNumeroMoeda(valores.getValorUTI()) +"\n");
		
		out.write( SEPARADOR + "TOTAL :" + SEPARADOR  +
				   (sumQtAIH + sumQtdProc + (valores.getDiasAcomp() != null ? valores.getDiasAcomp() : 0) + (valores.getDiasUTI() != null ? valores.getDiasUTI() : 0))  + SEPARADOR  +
				   AghuNumberFormat.formatarNumeroMoeda((sumHospAIH + servHospProc + (valores.getDiariaAcompServHosp() != null ? valores.getDiariaAcompServHosp() :0) + (valores.getDiariaUtiHosp() != null ? valores.getDiariaUtiHosp() : 0))) + SEPARADOR  +
				   AghuNumberFormat.formatarNumeroMoeda((sumProfAIH + servProfProc + (valores.getDiariaAcompServProf() != null ? valores.getDiariaAcompServProf() :0) + (valores.getDiariaUtiProf() != null ? valores.getDiariaUtiProf() : 0))) + SEPARADOR  +
				   AghuNumberFormat.formatarNumeroMoeda((sumHospAIH+sumProfAIH) + (servHospProc+servProfProc) + valores.getValorAcomp() + valores.getValorUTI()) +"\n");

		out.flush();
		out.close();
		
		return file.getAbsolutePath();
	}
	
	@SuppressWarnings("PMD.NPathComplexity")
	public String gerarCSVRelatorioArquivoProcedimento() throws IOException, ApplicationBusinessException{
		
		BigDecimal bCpgCphCspCnvCodigo = parametroFacade.buscarValorNumerico(AghuParametrosEnum.P_AGHU_CONVENIO_PADRAO);
		byte pSusPlanoAmbulatorio = parametroFacade.buscarValorByte(AghuParametrosEnum.P_SUS_PLANO_AMBULATORIO);
		short pTipoGrupoContaSus = parametroFacade.buscarValorShort(AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS);
		
		final List<GerarArquivoProcedimentoVO> colecao = fatItensProcedHospitalarDAO.listarTabelaProcedimentoValorEFinancimentoCompetencia(bCpgCphCspCnvCodigo, pSusPlanoAmbulatorio, pTipoGrupoContaSus);

		final String nomeArquivo = DominioNomeRelatorio.TABVAL.toString()  + DateUtil.obterDataFormatada(new Date(), "MMyyyy");
		
		final File file = new File(nomeArquivo + EXTENSAO);
		
		final Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);
		
		
		out.write("Financiamento"+SEPARADOR+"Complexidade"+SEPARADOR+"SSM_CDIG"+SEPARADOR+"SSM_SDIG"
				+SEPARADOR+"Descrição SSM"+SEPARADOR+PHI+SEPARADOR+"Valor proced"+SEPARADOR+"Valor SP"
				+SEPARADOR+"Valor SP Ambu"+SEPARADOR+"Valor SADT"+SEPARADOR+"Valor Anest"+SEPARADOR+"Descrição PHI"+SEPARADOR+"Exame \n");

		for (final GerarArquivoProcedimentoVO pdf : colecao) {
			
			out.write( (pdf.getDescricaoFCF()   			!= null ? pdf.getDescricaoFCF().trim() : "") 	+ SEPARADOR +
					   (pdf.getDescricaoFCC()  				!= null ? pdf.getDescricaoFCC().trim() : "") 	+ SEPARADOR +
					   (pdf.getCodTabela()       			!= null ? pdf.getCodTabela() : "") 		+ SEPARADOR +
					   (pdf.getCodTabela()      			!= null ? pdf.getCodTabela().toString().substring(0, pdf.getCodTabela().toString().length() - 1) : "")	+ SEPARADOR + 
					   (pdf.getDescricaoIPH() 		  		!= null ? obterTextoSubString(pdf.getDescricaoIPH().trim(), 60) : "") + SEPARADOR +
					   (pdf.getPhiSeq() 		  			!= null ? pdf.getPhiSeq() : "") 			+ SEPARADOR +
					   (pdf.getVlrProcedimento() 		  	!= null ? AghuNumberFormat.formatarValor(pdf.getVlrProcedimento(), MASCARA_VALORES) : 0) 		+ SEPARADOR +
					   (pdf.getVlrServProfissional() 		!= null ? AghuNumberFormat.formatarValor(pdf.getVlrServProfissional().floatValue(), MASCARA_VALORES) : 0) 		+ SEPARADOR +
					   (pdf.getVlrServProfisAmbulatorio() 	!= null ? AghuNumberFormat.formatarValor(pdf.getVlrServProfisAmbulatorio().floatValue(), MASCARA_VALORES) : 0) 			+ SEPARADOR +
					   (pdf.getVlrSadt() 			  		!= null ? AghuNumberFormat.formatarValor(pdf.getVlrSadt().floatValue(), MASCARA_VALORES) : 0) 				+ SEPARADOR +
					   (pdf.getVlrAnestesista() 		  	!= null ? AghuNumberFormat.formatarValor(pdf.getVlrAnestesista().floatValue(), MASCARA_VALORES) : 0) 		+ SEPARADOR +
					   (pdf.getPhiDescricao() 			  	!= null ? obterTextoSubString(pdf.getPhiDescricao().trim(), 60) : "") + SEPARADOR +  
					   (pdf.getSiglaExame() 				!= null ? pdf.getSiglaExame().trim() + "-" + pdf.getDescricaoExame().trim() : "") + "\n"
			         );
		}

		out.flush();
		out.close();
		
		return file.getPath();
	}
		
	
	private String obterTextoSubString(final String str, final Integer sizeEnd) {
		try {
			if(str.length() >= sizeEnd) {
				return str.substring(0, sizeEnd);
			} else {
				return str.substring(0, str.length());
			}
		} catch(final ArrayIndexOutOfBoundsException e) {
			return str;
		}
	}

	@SuppressWarnings("PMD.NPathComplexity")
	public String gerarCSVRelatorioConsultaRateioServicosProfissionais(final FatCompetencia competencia) throws IOException, ApplicationBusinessException{
		final List<ConsultaRateioProfissionalVO> colecao = 
			this.getFaturamentoFacade().listarConsultaRateioServicosProfissionais(competencia);
		
		final String nomeArquivo =  DominioNomeRelatorio.FATR_CON_RAT_ATIV.toString();
		final File file = new File(nomeArquivo + EXTENSAO);
		final Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);
		
		out.write("Código" + SEPARADOR + "Descrição" + SEPARADOR + "Quant" + SEPARADOR + "Valor" + SEPARADOR + "Serv. Prof \n");
		
		for (final ConsultaRateioProfissionalVO pdf : colecao) {
			out.write( (pdf.getProcedimentoHosp()  			!= null ? pdf.getProcedimentoHosp() : "") 	+ SEPARADOR +
					   (pdf.getDescricao()  				!= null ? pdf.getDescricao().trim() : "") 	+ SEPARADOR +
					   (pdf.getQuantidade()       			!= null ? pdf.getQuantidade() : 0) 		+ SEPARADOR +
					   (pdf.getVlrProcedimento()			!= null ? AghuNumberFormat.formatarValor(pdf.getVlrProcedimento(), MASCARA_VALORES) : 0)	+ SEPARADOR +
					   AghuNumberFormat.formatarValor(pdf.getVlrServProfReport(), MASCARA_VALORES) + "\n"
			         );
		}

		out.flush();
		out.close();
		
		return file.getPath();
	}
	
	
	public List<ConsultaRateioProfissionalVO> listarConsultaRateioServicosProfissionais(final FatCompetencia competencia) throws IOException, ApplicationBusinessException{
		final List<ConsultaRateioProfissionalVO> colecao = 
			this.getAghuFacade().listarConsultaRateioServicosProfissionais(competencia);
		
		final BigDecimal divisor = new BigDecimal("2");
		
		for (final ConsultaRateioProfissionalVO vo : colecao) {
			if(vo.getVlrServProfReport() != null){
				vo.setVlrServProfReport(vo.getVlrServProfReport().divide(divisor));
			} else {
				vo.setVlrServProfReport(BigDecimal.ZERO);
			}
		}
		
		return colecao;
	}
	
	IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected FatContasHospitalaresDAO getFatContasHospitalaresDAO(){
		return fatContasHospitalaresDAO;
	}
	
	protected FatItensProcedHospitalarDAO getFatItensProcedHospitalarDAO(){
		return fatItensProcedHospitalarDAO;
	}

	protected FatVlrItemProcedHospCompsDAO getFatVlrItemProcedHospCompsDAO() {
		return fatVlrItemProcedHospCompsDAO;
	}
	
	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}

	
	public String nameHeaderGerarArquivoProducaoPHI(){
		return DominioNomeRelatorio.NOME_ARQUIVO_PRODUCAO_PHI + DateUtil.dataToString(new Date(), "ddMMyyyy");		
	}
	
	@SuppressWarnings("PMD.NPathComplexity")
	private String gerarArquivoProducaoPHI(final List<FatRelatorioProducaoPHIVO> colecao) throws BaseException {
		File file =null;
		try {
			file = File.createTempFile(nameHeaderGerarArquivoProducaoPHI(), "." + TipoArquivoRelatorio.CSV.getExtensao());
			
			final Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);
			//PRONTUÁRIO'||';'||'PACIENTE'||';'|| 'PHI'||';'||'DESCRIÇÃO'||';'||'DATA'||';'||'CARTÃO SUS')
			out.write(("PRONTUÁRIO" + SEPARADOR + "PACIENTE" + SEPARADOR + PHI + SEPARADOR + "DESCRIÇÃO" + SEPARADOR + "DATA" + SEPARADOR
					+ "CARTÃO SUS" + this.getLineSeparator()));
			for (final FatRelatorioProducaoPHIVO fatRelatorioProducaoPHIVO : colecao) {
				out.write(((fatRelatorioProducaoPHIVO.getProntuario() == null ? "" : fatRelatorioProducaoPHIVO.getProntuario().toString())
						+ SEPARADOR
						+ CoreUtil.nvl(fatRelatorioProducaoPHIVO.getNome(), "")
						+ SEPARADOR
						+ (fatRelatorioProducaoPHIVO.getPhiSeq() == null ? "" : fatRelatorioProducaoPHIVO.getPhiSeq())
						+ SEPARADOR
						+ CoreUtil.nvl(fatRelatorioProducaoPHIVO.getDescricao(), "")
						+ SEPARADOR
						+ (fatRelatorioProducaoPHIVO.getDthrRealizado() == null ? "" : DateUtil.dataToString(
								fatRelatorioProducaoPHIVO.getDthrRealizado(), "dd/MM/yyyy HH:mm:ss")) + SEPARADOR
						+ (fatRelatorioProducaoPHIVO.getNroCartaoSaude() == null ? "" : fatRelatorioProducaoPHIVO.getNroCartaoSaude()) + this
						.getLineSeparator()));
				
			}
			out.flush();
			out.close();
		} catch (final Exception e) {
			logError(e);
			throw new BaseException(RelatorioCSVFaturamentoONExceptionCode.PROBLEMA_CRIACAO_ARQUIVO, e);
		}
		if(file == null){
			return null;
		}
		return file.getAbsolutePath();
	}

	private FatProcedAmbRealizadoDAO getFatProcedAmbRealizadoDao() {
		return fatProcedAmbRealizadoDAO;
	}

	private ProcedimentosAmbRealizadosON getProcedimentosAmbRealizadosON(){
		return procedimentosAmbRealizadosON;
	}

	private FaturamentoAmbulatorioON getFaturamentoAmbulatorioON() {
		return faturamentoAmbulatorioON;
	}
	
	private String getLineSeparator() {
		if (this.lineSeparator == null) {
			this.lineSeparator = System.getProperty("line.separator");
			if (this.lineSeparator == null) {
				this.lineSeparator = "\n";
			}
		}
		return this.lineSeparator;
	}

	public String gerarArquivoProducaoPHI(final List<Integer> phiSeqs, final Date dtInicio, final Date dtFinal) throws BaseException  {
		final Date dtFim;
		if(dtFinal == null){
			dtFim = new Date();
		} else {
			dtFim = dtFinal;
		}
		final List<FatRelatorioProducaoPHIVO> colecao = this.getFatProcedAmbRealizadoDao().getRelatorioProducaoPHI(phiSeqs,
				DateUtil.obterDataComHoraInical(dtInicio), DateUtil.obterDataComHoraFinal(dtFim));
		if(colecao == null || colecao.isEmpty()){
			throw new ApplicationBusinessException(RelatorioCSVFaturamentoONExceptionCode.NENHUM_REGISTRO_ENCONTRADO_PRODUCAO_PHI); //REGISTRO NÃO LOCALIZADO.
		} else {
			return this.gerarArquivoProducaoPHI(colecao); 
		}
	}

	public void gerarArquivoRelatorioRealizadosIndividuaisFolhaRosto(final FatCompetenciaId id, final Integer phiSeqInicial, final Integer phiSeqFinal, final TipoArquivoRelatorio tipoArquivo) {
		// TODO rubens.souza: parado o desenvolvimento da #13560. 

		
	}

	protected IInternacaoFacade getInternacaoFacade() {
		return this.internacaoFacade;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
}
