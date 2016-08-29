package br.gov.mec.aghu.blococirurgico.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.RelatorioProcedAgendPorUnidCirurgicaON.RelatorioProcedAgendPorUnidCirurgicaONExceptionCode;
import br.gov.mec.aghu.blococirurgico.dao.MbcExtratoCirurgiaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcMotivoCancelamentoDAO;
import br.gov.mec.aghu.blococirurgico.vo.NomeArquivoRelatorioCrgVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioPacCirurgiasCanceladasVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.dominio.DominioTipoProcedimentoCirurgico;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MbcExtratoCirurgia;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;


@Stateless
public class RelatorioCirurgiasCanceladasON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(RelatorioCirurgiasCanceladasON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcMotivoCancelamentoDAO mbcMotivoCancelamentoDAO;

	@Inject
	private MbcExtratoCirurgiaDAO mbcExtratoCirurgiaDAO;


	@EJB
	private IRegistroColaboradorFacade iRegistroColaboradorFacade;

	@EJB
	private IParametroFacade iParametroFacade;

	@EJB
	private IAghuFacade iAghuFacade;


	private static final long serialVersionUID = 781370048509243581L;
	private static final String DATE_PATTERN_DD_MM_YYYY = "dd/MM/yyyy";
	private static final String DATE_PATTERN_DD_MM_YYYY_HH_MM = "dd/MM/yyyy HH:mm";
	private static final String DATE_PATTERN_DDMMYYYYHHMM = "ddMMyyyyHHmm";
	private static final String LOCALE_LANG = "pt";
	private static final String LOCALE_COUNTRY = "BR";
	private static final String ENCODING = "ISO-8859-1";
	private static final String SEPARADOR = ";";
	private static final String QUEBRA_LINHA = "\n";

	
	public enum RelatorioCirurgiasCanceladasONExceptionCode implements	BusinessExceptionCode {
		ERRO_RELATORIO_DATA_INICIO_MAIOR_DO_QUE_DATA_FIM, ERRO_RELATORIO_PERIODO_MAIOR_DO_QUE_X_DIAS;
	}
	
	public NomeArquivoRelatorioCrgVO gerarRelatorioCSV(Short unfSeq,Date dtInicio, Date dtFim, String extensaoArquivo) throws IOException {

		final List<RelatorioPacCirurgiasCanceladasVO> lista = 	getMbcMotivoCancelamentoDAO().obterPacientesCirurgiasCancMotivo(unfSeq, dtInicio, dtFim);

		String nomeRelatorio = DominioNomeRelatorio.CIR_CANCELADA.toString()+ "_" + new SimpleDateFormat(DATE_PATTERN_DDMMYYYYHHMM, new Locale(LOCALE_LANG, LOCALE_COUNTRY)).format(new Date());
		final File file = File.createTempFile(nomeRelatorio, extensaoArquivo);
		final Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODING);
		out.write("Unidade" + SEPARADOR + "Motivo" + SEPARADOR + "Data Cancelamento" + SEPARADOR+ "Data Cirurgia" + SEPARADOR + "Sala" + SEPARADOR + "Nome Paciente" + SEPARADOR 
				+ "Complemento" +SEPARADOR + "Cancelado por" +SEPARADOR + "Especialidade" + SEPARADOR+ "Equipe" +SEPARADOR + "Procedimento Principal" +SEPARADOR + "Tipo" +SEPARADOR 
				+ "Data Atendimento" +SEPARADOR + "Pagador" +QUEBRA_LINHA);
		montaArquivo(dtInicio, dtFim, lista, out);
		out.flush();
		out.close();
		NomeArquivoRelatorioCrgVO nomeArquivoRelatorioCrgVO = new NomeArquivoRelatorioCrgVO();
		nomeArquivoRelatorioCrgVO.setFileName(file.getAbsolutePath());
		nomeArquivoRelatorioCrgVO.setNomeRelatorio(nomeRelatorio + extensaoArquivo);

		return nomeArquivoRelatorioCrgVO;
	}

	private void montaArquivo(Date dtInicio, Date dtFim,final List<RelatorioPacCirurgiasCanceladasVO> lista,Writer out) throws IOException {
		List<RelatorioPacCirurgiasCanceladasVO> listVo = new ArrayList<RelatorioPacCirurgiasCanceladasVO>();
		ordenarListaVo(dtInicio, dtFim, lista, listVo);
		SimpleDateFormat formatDiaMesAno = new SimpleDateFormat(DATE_PATTERN_DD_MM_YYYY, new Locale(LOCALE_LANG, LOCALE_COUNTRY));
		SimpleDateFormat formatDiaMesAnoHrMin =new SimpleDateFormat(DATE_PATTERN_DD_MM_YYYY_HH_MM, new Locale(LOCALE_LANG, LOCALE_COUNTRY));
		for (RelatorioPacCirurgiasCanceladasVO vo : listVo) {
			String dataCancel = "";
			String canceladoPor= "";
			obterValores(formatDiaMesAnoHrMin,vo);
			if(vo.getDataCancel()!=null){
				dataCancel = formatDiaMesAno.format(vo.getDataCancel());
			}
			if(vo.getCanceladoPor()!=null){
				canceladoPor = vo.getCanceladoPor();
			}
			String nomeEquipe = vo.getNomeUsual()!=null?vo.getNomeUsual():vo.getNome();
			String tipo; 
			if(vo.getTipo()!= null && vo.getTipo().equals(DominioTipoProcedimentoCirurgico.CIRURGIA)){
				tipo = vo.getTipo().getDescricao();
			}else{
				tipo = "outros";
			}
			String convenio = vo.getConvenio()!=null?vo.getConvenio():"";
			out.write(vo.getUnidade()+SEPARADOR+vo.getMotivo()+SEPARADOR+dataCancel + SEPARADOR+obterDataCirurgia(vo,formatDiaMesAno)+SEPARADOR+
					vo.getSala()+SEPARADOR+	vo.getNomePac()+SEPARADOR+ vo.getComplemento()+SEPARADOR+canceladoPor+SEPARADOR+vo.getSiglaEspecialidade()+SEPARADOR+
					nomeEquipe+SEPARADOR+vo.getProcedimentoPrincipal()+SEPARADOR+tipo+SEPARADOR+vo.getDataAtend() + SEPARADOR+convenio+ QUEBRA_LINHA);
		}

	}

	@SuppressWarnings("unchecked")
	private void ordenarListaVo(Date dtInicio, Date dtFim,
			final List<RelatorioPacCirurgiasCanceladasVO> lista,
			List<RelatorioPacCirurgiasCanceladasVO> listVo) {
		for (RelatorioPacCirurgiasCanceladasVO vo : lista) {
			List<MbcExtratoCirurgia> extratoCirurgia = getMbcExtratoCirurgiaDAO().pesquisarMbcExtratoCirurgiaPorCirurgiaSituacaoPeriodo(vo.getCrgSeq(), DominioSituacaoCirurgia.CANC, dtInicio, dtFim);
			if(extratoCirurgia!=null && !extratoCirurgia.isEmpty()){
				vo.setDataCancel(extratoCirurgia.get(0).getCriadoEm());
				vo.setCanceladoPor(getRegistroColaboradorFacade().obterNomePessoaServidor(extratoCirurgia.get(0).getServidor().getId().getVinCodigo(), extratoCirurgia.get(0).getServidor().getId().getMatricula()));
			}
			listVo.add(vo);
		}
		
		List<BeanComparator> sortFields = new ArrayList<BeanComparator>();
		sortFields.add(new BeanComparator(RelatorioPacCirurgiasCanceladasVO.Fields.UNF_SEQ.toString(), new NullComparator(true)));
		sortFields.add(new BeanComparator(RelatorioPacCirurgiasCanceladasVO.Fields.TIPO.toString(), new NullComparator(true)));
		sortFields.add(new BeanComparator(RelatorioPacCirurgiasCanceladasVO.Fields.MOTIVO_CANC.toString(), new NullComparator(true)));
		sortFields.add(new BeanComparator(RelatorioPacCirurgiasCanceladasVO.Fields.DT_CANCEL.toString(), new NullComparator(true)));
		sortFields.add(new BeanComparator(RelatorioPacCirurgiasCanceladasVO.Fields.DT_CIRURGIA.toString(),new NullComparator(true)));
	
		ComparatorChain multiSort = new ComparatorChain(sortFields);
		Collections.sort(listVo,multiSort);
	}

	private void obterValores(SimpleDateFormat formatDiaMesAnoHrMin,RelatorioPacCirurgiasCanceladasVO vo) {
		vo.setSiglaEspecialidade(vo.getSiglaEspecialidade()!=null?vo.getSiglaEspecialidade():"");
		vo.setProcedimentoPrincipal(vo.getProcedimentoPrincipal()!=null?vo.getProcedimentoPrincipal():"");
		vo.setComplemento(vo.getComplemento()!=null?vo.getComplemento():"");
		obterDataAtendimento(formatDiaMesAnoHrMin,vo);
	}

	private String obterDataCirurgia(RelatorioPacCirurgiasCanceladasVO vo, SimpleDateFormat formatDiaMesAno) {
		if(vo.getDataCirurg()!=null){
			return formatDiaMesAno.format(vo.getDataCirurg());
		}
		return "";
	}

	private void obterDataAtendimento(DateFormat dateFormatDMAHrMin,RelatorioPacCirurgiasCanceladasVO vo ) {
		/**
		 * FUNCTION MBCC_BUSCA_DTINT - reuso de outro método para a função
		 */
		List<AghAtendimentos> atendimento = this.getAghuFacade().obterUnidadesAlta(vo.getPacCodigo(), vo.getData(),Boolean.TRUE);
		if(atendimento!=null && !atendimento.isEmpty()){
			vo.setDataAtend(dateFormatDMAHrMin.format(atendimento.get(0).getDthrInicio()));
		}else{
			vo.setDataAtend("");
		}

	}	
	
	public void validarIntervaloEntreDatasCirurgiasCanceladas(
			final Date dataInicio, final Date dataFim) throws ApplicationBusinessException {
		
		if(DateUtil.validaDataMaior(dataInicio, dataFim)){
			throw new ApplicationBusinessException(
					RelatorioProcedAgendPorUnidCirurgicaONExceptionCode.ERRO_RELATORIO_DATA_INICIO_MAIOR_DO_QUE_DATA_FIM);
		}
		
		AghParametros parametroIntervaloEntreDatas = getParametroFacade()  //P_DIAS_PESQ_CIRURG_CANC
		.obterAghParametro(AghuParametrosEnum.P_DIAS_PESQ_CIRURG_CANC);
		
		if(DateUtil.calcularDiasEntreDatas(dataInicio, dataFim) > parametroIntervaloEntreDatas.getVlrNumerico().intValue()){
			throw new ApplicationBusinessException(
					RelatorioCirurgiasCanceladasONExceptionCode.ERRO_RELATORIO_PERIODO_MAIOR_DO_QUE_X_DIAS,parametroIntervaloEntreDatas.getVlrNumerico().intValue());
			
		}
		
	}

	protected IParametroFacade getParametroFacade() {
		return iParametroFacade;
	}

	protected MbcMotivoCancelamentoDAO getMbcMotivoCancelamentoDAO() {
		return mbcMotivoCancelamentoDAO;
	}

	protected MbcExtratoCirurgiaDAO getMbcExtratoCirurgiaDAO() {
		return mbcExtratoCirurgiaDAO;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.iRegistroColaboradorFacade;
	}

	protected IAghuFacade getAghuFacade() {
		return this.iAghuFacade;
	}


}