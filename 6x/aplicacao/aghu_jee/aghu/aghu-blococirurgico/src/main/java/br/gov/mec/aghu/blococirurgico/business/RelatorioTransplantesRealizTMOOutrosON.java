package br.gov.mec.aghu.blococirurgico.business;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcEspPorCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcTranspMedOsseaDAO;
import br.gov.mec.aghu.blococirurgico.vo.NomeArquivoRelatorioCrgVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioTransplantesRealizTMOOutrosVO;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class RelatorioTransplantesRealizTMOOutrosON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(RelatorioTransplantesRealizTMOOutrosON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcProcEspPorCirurgiasDAO mbcProcEspPorCirurgiasDAO;

	@Inject
	private MbcTranspMedOsseaDAO mbcTranspMedOsseaDAO;


	@EJB
	private RelatorioCirExpoRadIonON relatorioCirExpoRadIonON;

	@EJB
	private IParametroFacade iParametroFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 7727667882541979796L;
	
	private static final String NOME_ARQUIVO_COMPACTADO = "Transplantes_Realizados";
	private static final String DATE_PATTERN_DDMMYYYYHHMMSS = "ddMMyyyyHHmmss";
	private static final String DATE_PATTERN_DD_MM_YYYY = "dd/MM/yyyy";
	private static final String SEPARADOR = ";";
	private static final String QUEBRA_LINHA = "\n";
	private static final String ENCODING = "ISO-8859-1";
	private static final String LOCALE_LANG = "pt";
	private static final String LOCALE_COUNTRY = "BR";
	private static final int TAMANHO_BUFFER = 2048; // 2Kb

	private NomeArquivoRelatorioCrgVO gerarRelatorioTMOCSV(Date dtInicio,
			Date dtFim, String extensaoArquivoRelatorio) throws IOException {
		
		DateFormat dateFormatDiaMesAno = new SimpleDateFormat(DATE_PATTERN_DD_MM_YYYY, new Locale(LOCALE_LANG, LOCALE_COUNTRY));
		
		List<RelatorioTransplantesRealizTMOOutrosVO> listaRelatorioTransplantesRealizTMOOutrosVO = 
				getMbcTranspMedOsseaDAO().pesquisarTranspMedOsseaPorDtInicioEDtFim(dtInicio, dtFim);
		
		String nomeRelatorio = DominioNomeRelatorio.TMO.toString() + "_" + 
				new SimpleDateFormat(DATE_PATTERN_DDMMYYYYHHMMSS, new Locale(LOCALE_LANG, LOCALE_COUNTRY)).format(new Date());
		
		final File file = new File(nomeRelatorio + extensaoArquivoRelatorio);
		final Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODING);
		
		out.write("PRONTUÁRIO" + SEPARADOR + "NOME" + SEPARADOR + "DT_NASCIMENTO" + SEPARADOR 
				+ "DT_TRANSPLANTE" + SEPARADOR + "TIPO_TRANSPLANTE"	+ QUEBRA_LINHA);
		
		for (RelatorioTransplantesRealizTMOOutrosVO vo : listaRelatorioTransplantesRealizTMOOutrosVO) {
			out.write(vo.getProntuario() + SEPARADOR + vo.getNome() + SEPARADOR + dateFormatDiaMesAno.format(vo.getDtNascimento()) + SEPARADOR 
					+ dateFormatDiaMesAno.format(vo.getDtTransplante()) + SEPARADOR + vo.getIndTmo().getDescricao() + QUEBRA_LINHA);
		}
		
		out.flush();
		out.close();
		
		NomeArquivoRelatorioCrgVO nomeArquivoRelatorioCrgVO = new NomeArquivoRelatorioCrgVO();
		nomeArquivoRelatorioCrgVO.setFileName(file.getAbsolutePath());
		nomeArquivoRelatorioCrgVO.setNomeRelatorio(nomeRelatorio);
		
		return nomeArquivoRelatorioCrgVO;
	}
	
	private NomeArquivoRelatorioCrgVO gerarRelatorioOutrosTransplantesCSV(Date dtInicio,
			Date dtFim, String extensaoArquivoRelatorio) throws IOException, ApplicationBusinessException {
		
		AghParametros paramListaProcedTransplante = 
				getParametroFacade().obterAghParametro(AghuParametrosEnum.P_AGHU_LISTA_COD_PROCED_TRANSPLANTE);
		
		DateFormat dateFormatDiaMesAno = new SimpleDateFormat(DATE_PATTERN_DD_MM_YYYY, new Locale(LOCALE_LANG, LOCALE_COUNTRY));
		
		List<String> listaProcedTransplanteStr = Arrays.asList(paramListaProcedTransplante.getVlrTexto().split(","));
		List<Integer> listaProcedTransplante = new ArrayList<Integer>();
		
		for (String procedTransplanteStr : listaProcedTransplanteStr) {
			listaProcedTransplante.add(Integer.valueOf(procedTransplanteStr));
		}
		
		List<RelatorioTransplantesRealizTMOOutrosVO> listaRelatorioTransplantesRealizTMOOutrosVO = getMbcProcEspPorCirurgiasDAO()
				.pesquisarTransplantesPorDtInicioDtFimEListaProcedTransplante(
						dtInicio, dtFim, listaProcedTransplante);
		
		String nomeRelatorio = DominioNomeRelatorio.TRANSPLANTES.toString() + "_" + 
				new SimpleDateFormat(DATE_PATTERN_DDMMYYYYHHMMSS, new Locale(LOCALE_LANG, LOCALE_COUNTRY)).format(new Date());
		
		final File file = new File(nomeRelatorio + extensaoArquivoRelatorio);
		final Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODING);
		
		out.write("PRONTUÁRIO" + SEPARADOR + "NOME" + SEPARADOR + "DT_NASCIMENTO" + SEPARADOR 
				+ "DT_PROCEDIMENTO" + SEPARADOR + "PROCEDIMENTO" + SEPARADOR + "EQUIPE" + QUEBRA_LINHA);
		
		for (RelatorioTransplantesRealizTMOOutrosVO vo : listaRelatorioTransplantesRealizTMOOutrosVO) {
			out.write(vo.getProntuario() + SEPARADOR + vo.getNome() + SEPARADOR + dateFormatDiaMesAno.format(vo.getDtNascimento()) + SEPARADOR 
					+ dateFormatDiaMesAno.format(vo.getDtTransplante()) + SEPARADOR + vo.getProcedimento() + SEPARADOR + vo.getEquipe() + QUEBRA_LINHA);
		}
		
		out.flush();
		out.close();
		
		NomeArquivoRelatorioCrgVO nomeArquivoRelatorioCrgVO = new NomeArquivoRelatorioCrgVO();
		nomeArquivoRelatorioCrgVO.setFileName(file.getAbsolutePath());
		nomeArquivoRelatorioCrgVO.setNomeRelatorio(nomeRelatorio);
		
		return nomeArquivoRelatorioCrgVO;
	}
	
	public NomeArquivoRelatorioCrgVO gerarRelatoriosTMOEOutrosTransplantesZip(Date dtInicio,
			Date dtFim, RapPessoasFisicas pessoasFisica, String extensaoArquivoRelatorio, String extensaoArquivoDownload) throws IOException, ApplicationBusinessException {
		File file = null;
		File zipFile = null;
		ZipOutputStream zipOutputStream = null;

		// Gerar ZIP
		zipFile = new File(NOME_ARQUIVO_COMPACTADO);
		zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile));
		
		getRelatorioCirExpoRadIonON().validarProfissional(pessoasFisica);
		
		// Lista de VOs contém os caminhos dos arquivos gerados que serão compactados no arquivo Zip
		List<NomeArquivoRelatorioCrgVO> listaNomeArquivoRelatorioCrgVO = new ArrayList<NomeArquivoRelatorioCrgVO>();
		listaNomeArquivoRelatorioCrgVO.add(gerarRelatorioTMOCSV(dtInicio, dtFim, extensaoArquivoRelatorio));
		listaNomeArquivoRelatorioCrgVO.add(gerarRelatorioOutrosTransplantesCSV(dtInicio, dtFim, extensaoArquivoRelatorio));

		for (NomeArquivoRelatorioCrgVO nomeArquivoRelatorioCrgVO : listaNomeArquivoRelatorioCrgVO) {
			file = new File(nomeArquivoRelatorioCrgVO.getFileName());
			InputStream fileIS = new FileInputStream(file);
			zipOutputStream.putNextEntry(new ZipEntry(file.getName()));

			byte[] dados = new byte[TAMANHO_BUFFER];
			
            int len = 0;
            while((len = fileIS.read(dados, 0, TAMANHO_BUFFER)) > 0) {
            	zipOutputStream.write(dados, 0, len);
            }
                
			zipOutputStream.closeEntry();
			fileIS.close();
			file.delete(); // para deletar do tmp
		}
		
		zipOutputStream.close();
		
		NomeArquivoRelatorioCrgVO vo = new NomeArquivoRelatorioCrgVO();
		vo.setFileName(zipFile.getAbsolutePath());
		vo.setNomeRelatorio(zipFile.getName());
		
		return vo;
	}
	
	protected MbcTranspMedOsseaDAO getMbcTranspMedOsseaDAO() {
		return mbcTranspMedOsseaDAO;
	}
	
	protected MbcProcEspPorCirurgiasDAO getMbcProcEspPorCirurgiasDAO() {
		return mbcProcEspPorCirurgiasDAO;
	}
	
	protected IParametroFacade getParametroFacade() {
		return iParametroFacade;
	}
	
	protected RelatorioCirExpoRadIonON getRelatorioCirExpoRadIonON() {
		return relatorioCirExpoRadIonON;
	}
	
}
