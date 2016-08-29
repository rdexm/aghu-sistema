package br.gov.mec.aghu.blococirurgico.business;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.vo.NomeArquivoRelatorioCrgVO;
import br.gov.mec.aghu.blococirurgico.vo.PacientesCirurgiaUnidadeVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class PacientesCirurgiaUnidadeON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(PacientesCirurgiaUnidadeON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcCirurgiasDAO mbcCirurgiasDAO;


	private static final long serialVersionUID = 1134948931921935499L;

	private static final String SEPARADOR = ";";
	private static final String QUEBRA_LINHA = "\n";
	private static final String ENCODE = "ISO-8859-1";
	
	public enum PacientesCirurgiaUnidadeONExceptionCode implements	BusinessExceptionCode {
		MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA, DATA_INICIAL_MAIOR_QUE_DATA_FINAL
	}
	
	protected MbcCirurgiasDAO getMbcCirurgiasDAO() {
		return mbcCirurgiasDAO;
	}
	
	public NomeArquivoRelatorioCrgVO gerarRelatorioPacientesCirurgiaUnidadeCSV(Short unfSeq, Date crgDataInicio, Date crgDataFim, Integer serMatricula, Short serVinCodigo, String extensaoArquivo) throws IOException, ApplicationBusinessException {
		if(DateUtil.validaDataMaior(crgDataInicio, crgDataFim)){
			throw new ApplicationBusinessException(PacientesCirurgiaUnidadeONExceptionCode.DATA_INICIAL_MAIOR_QUE_DATA_FINAL);
		}
		
		List<PacientesCirurgiaUnidadeVO> lista = getMbcCirurgiasDAO()
				.obterPacientesCirurgiaUnidade(unfSeq, crgDataInicio,crgDataFim, serMatricula, serVinCodigo);
		
		if(lista == null || lista.isEmpty()){
			throw new ApplicationBusinessException(PacientesCirurgiaUnidadeONExceptionCode.MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA);
		}
		
		String nomeRelatorio = 
				"CIRURGIA_" + DateUtil.dataToString(crgDataInicio, "ddMMyyyy") 
				+ "_A_" + 
				DateUtil.dataToString(crgDataFim, "ddMMyyyy");
		File file = processarFileCirurgiaUnidade(extensaoArquivo, lista,
				nomeRelatorio);
		NomeArquivoRelatorioCrgVO vo = new NomeArquivoRelatorioCrgVO();
		vo.setFileName(file.getAbsolutePath());
		vo.setNomeRelatorio(nomeRelatorio);
		return vo;
	}

	private File processarFileCirurgiaUnidade(String extensaoArquivo,
			List<PacientesCirurgiaUnidadeVO> lista, String nomeRelatorio)
			throws IOException, UnsupportedEncodingException,
			FileNotFoundException {
		File file = File.createTempFile(nomeRelatorio, extensaoArquivo);
		Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);
		out.write("UNIDADE CIRÚRGICA" 	+ SEPARADOR //1
				+ "EQUIPE" 				+ SEPARADOR	//2
				+ "DATA INICIAL"		+ SEPARADOR //3
				+ "DATA FINAL" 			+ SEPARADOR	//4
				+ "SITUAÇÃO" 			+ SEPARADOR //5
				+ "PRONTUÁRIO" 			+ SEPARADOR //6
				+ "NOME"				+ SEPARADOR //7
				+ "CONVÊNIO" 			+ SEPARADOR //8
				+ "PROCEDIMENTO PRINCIPAL" + SEPARADOR //9
				+ "DATA AGENDA" 		+ SEPARADOR //10
				+ "MOTIVO" 				+ SEPARADOR	//11
				+ "ORIGEM" 				+ SEPARADOR //12
				+ "NATUREZA" 			+ SEPARADOR	//13
				+ "ESPECIALIDADE" 		+ SEPARADOR //14
				+ "DATA ATENDIMENTO" 	+ QUEBRA_LINHA);//15
		for (PacientesCirurgiaUnidadeVO vo : lista) {
						
			String dtInicio  = DateUtil.dataToString(vo.getDataini(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO);
			String dtFim 	 = DateUtil.dataToString(vo.getDatafim(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO);
			String dtatend 	 = DateUtil.dataToString(vo.getDtatend(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO);
			String dataagenda= DateUtil.dataToString(vo.getDataagenda(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO);
			
			out.write(vo.getUnid() 		+ SEPARADOR //1
					+ (vo.getEquipe()!=null?vo.getEquipe()	: "") + SEPARADOR //2
					+ dtInicio 	+ SEPARADOR //3
					+ dtFim 	+ SEPARADOR //4
					+ (vo.getSit()!=null	? vo.getSit() 	: "") + SEPARADOR //5
					+ (vo.getPrnt()!=null 	? vo.getPrnt()	: "") + SEPARADOR //6
					+ vo.getNome() 		+ SEPARADOR //7
					+ vo.getCnvdesc() 	+ SEPARADOR //8
					+ "\"" + vo.getProc().replace("\"","\"\"") + "\"" + SEPARADOR //9
					+ dataagenda + SEPARADOR //10
					+ (vo.getMotivo() != null ? vo.getMotivo() : "") 	+ SEPARADOR //11
					+ vo.getOrigem().getDescricaoAbreviada()	+ SEPARADOR //12
					+ vo.getNatureza() 	+ SEPARADOR //13
					+ vo.getEsp() 		+ SEPARADOR //14
					+ dtatend 			+ QUEBRA_LINHA);//15
		}
		out.flush();
		out.close();
		return file;
	}
	
}
