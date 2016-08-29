package br.gov.mec.aghu.faturamento.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;

import br.gov.mec.aghu.business.exception.AghuCSVBaseException.AghuCSVBaseExceptionExceptionCode;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.dominio.DominioTipoProcedimento;
import br.gov.mec.aghu.faturamento.dao.FatCompetenciaDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedHospInternosDAO;
import br.gov.mec.aghu.faturamento.vo.ArquivoURINomeQtdVO;
import br.gov.mec.aghu.faturamento.vo.ProdutividadeFisiatriaVO;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ProdutividadeFisiatriaRN extends BaseBusiness {

	private static final long serialVersionUID = -6231474799383137337L;
	
	@Inject
	FatCompetenciaDAO fatCompetenciaDAO;
	
	@Inject
	FatProcedHospInternosDAO fatProcedHospInternosDAO;

	private final String ENCODE = "ISO-8859-1";
	
	private final String SEPARADOR=";";
	
	private final String QUEBRA_LINHA = "\n";
	
	private final String LABEL_CABECALHO_CSV_ATEND = "Atend";
	private final String LABEL_CABECALHO_CSV_MES = "M\u00EAs";
	private final String LABEL_CABECALHO_CSV_FISIOTERAPEUTA = "Fisioterapeuta";
	private final String LABEL_CABECALHO_CSV_MEDICO_SOLICITANTE = "M\u00E9dico Solicitante";
	private final String LABEL_CABECALHO_CSV_PROCED = "Proced";
	private final String LABEL_CABECALHO_CSV_DESCRICAO = "Descri\u00E7\u00E3o";
	private final String LABEL_CABECALHO_CSV_ANDAR_ALA = "Andar/Ala";
	private final String LABEL_CABECALHO_CSV_TIPO_PROC = "Tipo Proc";
	private final String LABEL_CABECALHO_CSV_DATA_REALIZADO = "Data Realizado";
	
	enum ProdutividadeFisiatriaRNExceptionCode implements BusinessExceptionCode {
		NENHUM_REGISTRO;
	}
	
	public ArquivoURINomeQtdVO obterDadosProdutividadeFisiatria(FatCompetencia competencia) throws ApplicationBusinessException {		
		
		Calendar calendarInicio = Calendar.getInstance();
		calendarInicio.set(Calendar.DAY_OF_MONTH, 1);
		calendarInicio.set(Calendar.MONTH, competencia.getId().getMes()-1);
		calendarInicio.set(Calendar.YEAR, competencia.getId().getAno());
		calendarInicio.set(Calendar.HOUR_OF_DAY, 0);
		calendarInicio.set(Calendar.MINUTE, 0);
		calendarInicio.set(Calendar.SECOND, 0);
		
		Calendar calendarFim = Calendar.getInstance();
		calendarFim.set(Calendar.DAY_OF_MONTH, 1);
		calendarFim.set(Calendar.MONTH, competencia.getId().getMes()-1);
		calendarFim.set(Calendar.YEAR, competencia.getId().getAno());
		calendarFim.set(Calendar.HOUR_OF_DAY, 23);
		calendarFim.set(Calendar.MINUTE, 59);
		calendarFim.set(Calendar.SECOND, 59);
		calendarFim.set(Calendar.DATE, calendarFim.getActualMaximum(Calendar.DATE));
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyyHHmm");
		
		List<Object[]> listaProdutividadeFisiatria = fatProcedHospInternosDAO.pesquisarDadosGeracaoArquivoProdutividadeFisiatria(calendarInicio.getTime(), calendarFim.getTime());		
		
		if (listaProdutividadeFisiatria == null || listaProdutividadeFisiatria.isEmpty()) {
			throw new ApplicationBusinessException(ProdutividadeFisiatriaRNExceptionCode.NENHUM_REGISTRO);
		}
		
		List<ProdutividadeFisiatriaVO> listaLinhas = preencherListaVO(listaProdutividadeFisiatria);
		
		Writer out = null;
		try {
			File file = File.createTempFile("PROD_FIS_"+simpleDateFormat.format(new Date())+DominioNomeRelatorio.EXTENSAO_CSV, DominioNomeRelatorio.EXTENSAO_CSV);
			out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);
			out.write(gerarCabecalhoCSVProdutividadeFisiatria());
			if (!listaLinhas.isEmpty()) {
				for (ProdutividadeFisiatriaVO linha : listaLinhas) {
					out.write(gerarLinhasCSVProdutividadeFisiatria(linha));
				}
			}
			ArquivoURINomeQtdVO vo = new ArquivoURINomeQtdVO(file.toURI(), "PROD_FIS_"+simpleDateFormat.format(new Date())+DominioNomeRelatorio.EXTENSAO_CSV, listaLinhas.size(), 1);
			out.flush();
			
			return vo;
		} catch (IOException e) {
			throw new ApplicationBusinessException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage());
		} finally {
			IOUtils.closeQuietly(out);
		}
	}
	
	private List<ProdutividadeFisiatriaVO> preencherListaVO(List<Object[]> listaProdutividadeFisiatria) {
		List<ProdutividadeFisiatriaVO> listaRetorno = new ArrayList<ProdutividadeFisiatriaVO>();
		ProdutividadeFisiatriaVO produtividadeFisiatriaVO;
		
		for (Object[] objeto: listaProdutividadeFisiatria) {
			produtividadeFisiatriaVO = new ProdutividadeFisiatriaVO();
			
			produtividadeFisiatriaVO.setAtendimento(((Integer) objeto[0]));
			
			produtividadeFisiatriaVO.setCriadoEm(((Date) objeto[1]));
			
			produtividadeFisiatriaVO.setFisioterapeuta(((String) objeto[2]));

			produtividadeFisiatriaVO.setMedicoSolicitante(((String) objeto[3]));

			produtividadeFisiatriaVO.setSeqProcedimento(((Integer) objeto[4]));

			produtividadeFisiatriaVO.setDescricaoProcedimento(((String) objeto[5]));

			produtividadeFisiatriaVO.setAndarAla(((String) objeto[6]));
			
			if (objeto[7] != null) {
				if (DominioTipoProcedimento.A.toString().equals(objeto[7].toString())) {
					produtividadeFisiatriaVO.setTipoProcedimento(DominioTipoProcedimento.A);
				} else if (DominioTipoProcedimento.M.toString().equals(objeto[7].toString())) {
					produtividadeFisiatriaVO.setTipoProcedimento(DominioTipoProcedimento.M);
				} else if (DominioTipoProcedimento.R.toString().equals(objeto[7].toString())) {
					produtividadeFisiatriaVO.setTipoProcedimento(DominioTipoProcedimento.R);
				}
			}
			
			produtividadeFisiatriaVO.setDataRealizado(((Date) objeto[8]));			
			
			listaRetorno.add(produtividadeFisiatriaVO);
		}
		
		return listaRetorno;
		
	}
	
	private String gerarCabecalhoCSVProdutividadeFisiatria() {
		
		StringBuilder builder = new StringBuilder();
		builder.append(LABEL_CABECALHO_CSV_ATEND)
			.append(SEPARADOR)
			.append(LABEL_CABECALHO_CSV_MES)
			.append(SEPARADOR)
			.append(LABEL_CABECALHO_CSV_FISIOTERAPEUTA)
			.append(SEPARADOR)
			.append(LABEL_CABECALHO_CSV_MEDICO_SOLICITANTE)
			.append(SEPARADOR)
			.append(LABEL_CABECALHO_CSV_PROCED)
			.append(SEPARADOR)
			.append(LABEL_CABECALHO_CSV_DESCRICAO)
			.append(SEPARADOR)
			.append(LABEL_CABECALHO_CSV_ANDAR_ALA)
			.append(SEPARADOR)
			.append(LABEL_CABECALHO_CSV_TIPO_PROC)
			.append(SEPARADOR)
			.append(LABEL_CABECALHO_CSV_DATA_REALIZADO)		
			.append(QUEBRA_LINHA);

		return builder.toString();
	}
	
	private String gerarLinhasCSVProdutividadeFisiatria(ProdutividadeFisiatriaVO linha) {	
	 	
		StringBuilder builder = new StringBuilder();
		
		if (linha.getAtendimento() != null) {
			builder.append(linha.getAtendimento());
		} else {
			builder.append("");
		}
		
		builder.append(SEPARADOR).append(linha.getCriadoEmFormatado()).append(SEPARADOR);
		
		if (linha.getFisioterapeuta() != null) {
			builder.append(linha.getFisioterapeuta());
		} else {
			builder.append("");
		}

		builder.append(SEPARADOR);
		
		if (linha.getMedicoSolicitante() != null) {
			builder.append(linha.getMedicoSolicitante());
		} else {
			builder.append("");
		}

		builder.append(SEPARADOR);
		
		if (linha.getSeqProcedimento() != null) {
			builder.append(linha.getSeqProcedimento());
		} else {
			builder.append("");
		}
		
		builder.append(SEPARADOR);
		
		if (linha.getDescricaoProcedimento() != null) {
			builder.append(linha.getDescricaoProcedimento());
		} else {
			builder.append("");
		}
		
		builder.append(SEPARADOR);
		
		if (linha.getAndarAla() != null) {
			builder.append(linha.getAndarAla());
		} else {
			builder.append("");
		}
		
		builder.append(SEPARADOR);
		
		if (linha.getTipoProcedimento().getDescricao() != null) {
			builder.append(linha.getTipoProcedimento().getDescricao());
		} else {
			builder.append("");
		}
		
		builder.append(SEPARADOR).append(linha.getDataRealizadoFormatado()).append(QUEBRA_LINHA);
		
		return builder.toString();
	}

	@Override
	protected Log getLogger() {
		// TODO Auto-generated method stub
		return null;
	}

}
