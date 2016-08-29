package br.gov.mec.aghu.faturamento.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.faturamento.dao.FatContasHospitalaresDAO;
import br.gov.mec.aghu.faturamento.vo.ClinicaPorProcedimentoVO;
import br.gov.mec.aghu.faturamento.vo.NomeArquivoVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ClinicaPorProcedimentoCSVRN extends BaseBusiness {

	private static final long serialVersionUID = -4326347352606314297L;
	
	private static final Log LOG = LogFactory.getLog(ClinicaPorProcedimentoCSVRN.class);
	
	@EJB
	private ClinicaPorProcedimentoUtil util;
	
	@Inject
	private FatContasHospitalaresDAO contasHospitalaresDAO;
	
	private static final String QUEBRA_LINHA = "\r\n";
	private static final String SEPARADOR=";";
	private static final String ENCODE="ISO-8859-1";
	
	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	private enum ClinicaPorProcedimentoCSVRNExceptionCode implements BusinessExceptionCode {
		ERRO_REL_CLIN_POR_PROCED_GERAR_CSV;
	}	
	
	public NomeArquivoVO geraRelCSVClinicaPorProcedimento(
			final Date dciCpeDtHrInicio,
			final DominioModuloCompetencia dciCpeModulo, 
			final Byte dciCpeMes,
			final Short dciCpeAno, 
			final Byte codAtoOPM) throws BaseException {
		
		final List<ClinicaPorProcedimentoVO> listaLinhas = contasHospitalaresDAO.listarClinicaPorProcedimento(dciCpeDtHrInicio, dciCpeModulo, dciCpeMes, dciCpeAno, codAtoOPM);

		try {
			
			String nomeArquivo = DominioNomeRelatorio.FATR_INT_CLC_PROCED.toString();
			
			final File file = File.createTempFile(nomeArquivo, DominioNomeRelatorio.EXTENSAO_CSV);
	
			final Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);
			
			out.write(obterHeaderCSV());
			
			if (listaLinhas != null) {
				for(ClinicaPorProcedimentoVO linha : listaLinhas){
					out.write(obterlinhaCSV(linha));
				}
			}
				
			out.flush();
			out.close();
			
			NomeArquivoVO nomeRelatorio = new NomeArquivoVO();
			nomeRelatorio.setFileName(file.getAbsolutePath());
			nomeRelatorio.setNomeRelatorio(nomeArquivo);
			
			return nomeRelatorio;
		
		} catch (IOException e) {
			getLogger().error(getResourceBundleValue(new ApplicationBusinessException(ClinicaPorProcedimentoCSVRNExceptionCode.ERRO_REL_CLIN_POR_PROCED_GERAR_CSV)));
			throw new ApplicationBusinessException(ClinicaPorProcedimentoCSVRNExceptionCode.ERRO_REL_CLIN_POR_PROCED_GERAR_CSV);
		}	
	}
	
	private String obterHeaderCSV(){
		
		StringBuilder buffer = new StringBuilder(1000);
		
		buffer.append(getResourceBundleValue("HEADER_REL_CLIN_POR_PROCEDIMENTO"))
			.append(SEPARADOR)
			.append(getResourceBundleValue("HEADER_REL_CLIN_POR_PROCED_QTD"))
			.append(SEPARADOR)
			.append(getResourceBundleValue("HEADER_REL_CLIN_POR_PROCED_SERV_HOSP"))
			.append(SEPARADOR)
			.append(getResourceBundleValue("HEADER_REL_CLIN_POR_PROCED_SERV_PROF"))
			.append(SEPARADOR)
			.append(getResourceBundleValue("HEADER_REL_CLIN_POR_PROCED_SADT"))
			.append(SEPARADOR)
			.append(getResourceBundleValue("HEADER_REL_CLIN_POR_PROCED_TOTAL"))
			.append(SEPARADOR)
			.append(QUEBRA_LINHA);

		return buffer.toString();
	}
	
	private String obterlinhaCSV(ClinicaPorProcedimentoVO linha){
		
		StringBuilder buffer = new StringBuilder(3000);
		
		buffer.append(linha.getProcedimentoDescricao())
			.append(SEPARADOR)
			.append(linha.getQtdProcedimentosItem())
			.append(SEPARADOR)
			.append(util.formatarMoeda(linha.getValorShRealiz()))
			.append(SEPARADOR)
			.append(util.formatarMoeda(linha.getValorSpRealiz()))
			.append(SEPARADOR)
			.append(util.formatarMoeda(linha.getValorSadtRealiz())) 
			.append(SEPARADOR)
			.append(util.formatarMoeda(util.somar(linha.getValorShRealiz(), linha.getValorSpRealiz(), linha.getValorSadtRealiz())))
			.append(SEPARADOR)
			.append(QUEBRA_LINHA);
		
		return buffer.toString();
	}

}
