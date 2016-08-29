package br.gov.mec.aghu.faturamento.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.faturamento.dao.FatEspelhoAihDAO;
import br.gov.mec.aghu.faturamento.dao.FatValorContaHospitalarDAO;
import br.gov.mec.aghu.faturamento.vo.NomeArquivoVO;
import br.gov.mec.aghu.faturamento.vo.TotaisAIHGeralVO;
import br.gov.mec.aghu.faturamento.vo.TotaisProcedEspHemotGeralVO;
import br.gov.mec.aghu.faturamento.vo.TotalGeralClinicaPorProcedimentoVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class TotalGeralClinicaPorProcedimentoCSVRN extends BaseBusiness {

	private static final long serialVersionUID = -4326347352606314297L;
	
	private static final Log LOG = LogFactory.getLog(TotalGeralClinicaPorProcedimentoCSVRN.class);
	
	@EJB
	private ClinicaPorProcedimentoUtil util;
	
	@Inject
	private FatValorContaHospitalarDAO fatValorContaHospitalarDAO;
	
	@Inject
	private FatEspelhoAihDAO espelhoAihDAO;
	
	private static final String QUEBRA_LINHA = "\r\n";
	private static final String SEPARADOR=";";
	private static final String ENCODE="ISO-8859-1";
	
	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	private enum TotalGeralClinicaPorProcedimentoCSVRNExceptionCode implements BusinessExceptionCode {
		ERRO_REL_CLIN_POR_PROCED_GERAR_CSV_TOTAL;
	}	
	
	public NomeArquivoVO geraRelCSVTotalGeralClinicaPorProcedimento(
			final DominioModuloCompetencia modulo, 
			final Integer mes,
			final Integer ano, 
			final Date dtHrInicio, final Byte codAtoOPM) throws BaseException {
		 
		final TotalGeralClinicaPorProcedimentoVO totalGeralClinicaPorProcedimentoVO = obterTotalGeralClinicaPorProcedimento(modulo, mes, ano, dtHrInicio);
		final TotaisAIHGeralVO totaisAIHGeralVO = obterTotaisAIHGeralVO(modulo, mes.byteValue(), ano.shortValue(), dtHrInicio);
		final TotaisProcedEspHemotGeralVO  espHemotGeralVOTotal = obterTotaisProcedEspHemotGeralVO(modulo, mes.byteValue(), ano.shortValue(), dtHrInicio, codAtoOPM);

		try {
			
			String nomeArquivo = DominioNomeRelatorio.FATR_INT_CLC_PROCED.toString() + "_TOTAL_GERARL";
			
			final File file = File.createTempFile(nomeArquivo, DominioNomeRelatorio.EXTENSAO_CSV);
	
			final Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);
			
			out.write(obterHeaderCSV());
			
			out.write(obterlinhaCSV(totaisAIHGeralVO));
			out.write(obterlinhaCSV(espHemotGeralVOTotal));
			out.write(obterlinhaCSV(totalGeralClinicaPorProcedimentoVO));
			out.write(obterlinhaCSV(totalGeralClinicaPorProcedimentoVO, totaisAIHGeralVO, espHemotGeralVOTotal));
			
			out.flush();
			out.close();
			
			NomeArquivoVO nomeRelatorio = new NomeArquivoVO();
			nomeRelatorio.setFileName(file.getAbsolutePath());
			nomeRelatorio.setNomeRelatorio(nomeArquivo);
			
			return nomeRelatorio;	
		
		} catch (IOException e) {
			getLogger().error(getResourceBundleValue(new ApplicationBusinessException(TotalGeralClinicaPorProcedimentoCSVRNExceptionCode.ERRO_REL_CLIN_POR_PROCED_GERAR_CSV_TOTAL)));
			throw new ApplicationBusinessException(TotalGeralClinicaPorProcedimentoCSVRNExceptionCode.ERRO_REL_CLIN_POR_PROCED_GERAR_CSV_TOTAL);
		}
	}

	private TotaisAIHGeralVO obterTotaisAIHGeralVO(DominioModuloCompetencia modulo, Byte mes, Short ano, Date dtHrInicio) {
		TotaisAIHGeralVO vo = espelhoAihDAO.obterTotaisAIHGeralVO(modulo, mes, ano, dtHrInicio);
		formatarTotaisAIHGeralVO(vo);
		return vo;
	}

	private void formatarTotaisAIHGeralVO(TotaisAIHGeralVO vo) {
		vo.setValorSadtRealiz(util.nvlBigDecimalDefaultZero(vo.getValorSadtRealiz()));
		vo.setValorShRealiz(util.nvlBigDecimalDefaultZero(vo.getValorShRealiz()));
		vo.setValorSpRealiz(util.nvlBigDecimalDefaultZero(vo.getValorSpRealiz()));
		vo.setTotal(util.nvlBigDecimalDefaultZero(vo.getTotal()));
	}

	private TotalGeralClinicaPorProcedimentoVO obterTotalGeralClinicaPorProcedimento(
			DominioModuloCompetencia modulo, Integer mes, Integer ano,
			Date dtHrInicio) {
		
		TotalGeralClinicaPorProcedimentoVO vo = fatValorContaHospitalarDAO.obterTotalGeralClinicaPorProcedimento(modulo, mes, ano,dtHrInicio);		
		util.processarTotalGeralClinicaPorProcedimentoVO(vo);
		return vo;
	}

	private TotaisProcedEspHemotGeralVO obterTotaisProcedEspHemotGeralVO(
			final DominioModuloCompetencia modulo, final Byte mes,
			final Short ano, final Date dtHrInicio, final Byte codAtoOPM) {
		
		final TotaisProcedEspHemotGeralVO espHemotGeralVOConsultaUm = espelhoAihDAO.obterTotaisProcedEspHemotGeralConsultaUm(modulo, mes, ano, dtHrInicio);
		final TotaisProcedEspHemotGeralVO espHemotGeralVOConsultaDois = espelhoAihDAO.obterTotaisProcedEspHemotGeralConsultaDois(modulo, mes, ano, dtHrInicio, codAtoOPM);
		
		formtarTotaisProcedEspHemotGeralVO(espHemotGeralVOConsultaUm);
		formtarTotaisProcedEspHemotGeralVO(espHemotGeralVOConsultaDois);
		
		TotaisProcedEspHemotGeralVO vo = new TotaisProcedEspHemotGeralVO();
		
		vo.setQuant(espHemotGeralVOConsultaUm.getQuant() + espHemotGeralVOConsultaDois.getQuant());
		vo.setValorSadt(espHemotGeralVOConsultaUm.getValorSadt().add(espHemotGeralVOConsultaDois.getValorSadt()));
		vo.setValorServHosp(espHemotGeralVOConsultaUm.getValorServHosp().add(espHemotGeralVOConsultaDois.getValorServHosp()));
		vo.setValorServProf(espHemotGeralVOConsultaUm.getValorServProf().add(espHemotGeralVOConsultaDois.getValorServProf()));
		vo.setTotal(espHemotGeralVOConsultaUm.getTotal().add(espHemotGeralVOConsultaDois.getTotal()));
		
		return vo;
	}
	
	private void formtarTotaisProcedEspHemotGeralVO(TotaisProcedEspHemotGeralVO vo){
		vo.setValorSadt(util.nvlBigDecimalDefaultZero(vo.getValorSadt()));
		vo.setValorServHosp(util.nvlBigDecimalDefaultZero(vo.getValorServHosp()));
		vo.setValorServProf(util.nvlBigDecimalDefaultZero(vo.getValorServProf()));
		vo.setTotal(util.nvlBigDecimalDefaultZero(vo.getTotal()));
	}
	
	private String obterHeaderCSV(){

		StringBuilder buffer = new StringBuilder(1000);
		
		buffer.append("")
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
	
	private String obterlinhaCSV(TotaisAIHGeralVO linha){
		
		StringBuilder buffer = new StringBuilder(3000);
		
		buffer.append(getResourceBundleValue("LABEL_REL_CLIN_POR_PROCED_AIH_GERAL"))
			.append(SEPARADOR)
			.append(linha.getIphCodSusRealiz())
			.append(SEPARADOR)
			.append(util.formatarMoeda(linha.getValorShRealiz()))
			.append(SEPARADOR)
			.append(util.formatarMoeda(linha.getValorSpRealiz()))
			.append(SEPARADOR)
			.append(util.formatarMoeda(linha.getValorSadtRealiz()))
			.append(SEPARADOR)
			.append(util.formatarMoeda(linha.getTotal()))
			.append(SEPARADOR)
			
			.append(QUEBRA_LINHA);
		
		return buffer.toString();
	}
	
	private String obterlinhaCSV(TotaisProcedEspHemotGeralVO linha){
		
		StringBuilder buffer = new StringBuilder(3000);
		
		buffer.append(getResourceBundleValue("LABEL_REL_CLIN_POR_PROCED_PROD_DIA_ESP_HEM_GERAL"))
			.append(SEPARADOR)
			.append(linha.getQuant())
			.append(SEPARADOR)
			.append(util.formatarMoeda(linha.getValorServHosp()))
			.append(SEPARADOR)
			.append(util.formatarMoeda(linha.getValorServProf()))
			.append(SEPARADOR)
			.append(util.formatarMoeda(linha.getValorSadt()))
			.append(SEPARADOR)
			.append(util.formatarMoeda(linha.getTotal()))
			.append(SEPARADOR)
			
			.append(QUEBRA_LINHA);
		
		return buffer.toString();
	}
	
	private String obterlinhaCSV(TotalGeralClinicaPorProcedimentoVO linha){
		
		StringBuilder buffer = new StringBuilder(3000);
		
		buffer.append(getResourceBundleValue("LABEL_REL_CLIN_POR_PROCED_DIARIAS_ACOMP"))
			.append(SEPARADOR)
			.append(linha.getDiariasAcompanhante())
			.append(SEPARADOR)
			.append(util.formatarMoeda(linha.getValorShAcomp()))
			.append(SEPARADOR)
			.append(util.formatarMoeda(linha.getValorSpAcomp()))
			.append(SEPARADOR)
			.append(util.formatarMoeda(linha.getValorSadtAcomp()))
			.append(SEPARADOR)
			.append(util.formatarMoeda(linha.getValorAcomp()))
			.append(SEPARADOR)
			.append(QUEBRA_LINHA)
			
			.append(getResourceBundleValue("LABEL_REL_CLIN_POR_PROCED_DIARIAS_UTI"))
			.append(SEPARADOR)
			.append(linha.getDiasUti())
			.append(SEPARADOR)
			.append(util.formatarMoeda(linha.getValorShUti()))
			.append(SEPARADOR)
			.append(util.formatarMoeda(linha.getValorSpUti()))
			.append(SEPARADOR)
			.append(util.formatarMoeda(linha.getValorSadtUti()))
			.append(SEPARADOR)
			.append(util.formatarMoeda(linha.getValorUti()))
			.append(SEPARADOR)
			
			.append(QUEBRA_LINHA);
		
		return buffer.toString();
	}
	
	private String obterlinhaCSV(
			TotalGeralClinicaPorProcedimentoVO totalGeralClinicaPorProcedimentoVO,
			TotaisAIHGeralVO totaisAIHGeralVO,
			TotaisProcedEspHemotGeralVO espHemotGeralVOTotal) {
		
		StringBuilder buffer = new StringBuilder(3000);
		
		buffer.append(getResourceBundleValue("LABEL_REL_CLIN_POR_PROCED_TOTAL_GERAL"))
				.append(SEPARADOR)
				
				.append(totaisAIHGeralVO.getIphCodSusRealiz()
						+ espHemotGeralVOTotal.getQuant()
						+ totalGeralClinicaPorProcedimentoVO.getDiariasAcompanhante()
						+ totalGeralClinicaPorProcedimentoVO.getDiasUti())
				.append(SEPARADOR)

				.append(util.formatarMoeda(util.somar(totaisAIHGeralVO.getValorShRealiz(),
						espHemotGeralVOTotal.getValorServHosp(),
						totalGeralClinicaPorProcedimentoVO.getValorShAcomp(),
						totalGeralClinicaPorProcedimentoVO.getValorShUti())))
				.append(SEPARADOR)
				
				.append(util.formatarMoeda(util.somar(totaisAIHGeralVO.getValorSpRealiz(),
						espHemotGeralVOTotal.getValorServProf(),
						totalGeralClinicaPorProcedimentoVO.getValorSpAcomp(),
						totalGeralClinicaPorProcedimentoVO.getValorSpUti())))
				.append(SEPARADOR)
				
				.append(util.formatarMoeda(util.somar(totaisAIHGeralVO.getValorSadtRealiz(),
						espHemotGeralVOTotal.getValorServProf(),
						totalGeralClinicaPorProcedimentoVO.getValorSpAcomp(),
						totalGeralClinicaPorProcedimentoVO.getValorSpUti())))
				.append(SEPARADOR)
				
				.append(util.formatarMoeda(util.somar(totaisAIHGeralVO.getTotal(),
						espHemotGeralVOTotal.getTotal(),
						totalGeralClinicaPorProcedimentoVO.getValorAcomp(),
						totalGeralClinicaPorProcedimentoVO.getValorUti())))
				.append(SEPARADOR)
				
				.append(QUEBRA_LINHA);
		
		return buffer.toString();
	}
}
