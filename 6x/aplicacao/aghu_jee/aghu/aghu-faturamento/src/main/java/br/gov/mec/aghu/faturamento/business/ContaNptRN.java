package br.gov.mec.aghu.faturamento.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;

import br.gov.mec.aghu.business.exception.AghuCSVBaseException.AghuCSVBaseExceptionExceptionCode;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.faturamento.dao.FatContasHospitalaresDAO;
import br.gov.mec.aghu.faturamento.vo.ArquivoURINomeQtdVO;
import br.gov.mec.aghu.faturamento.vo.ContaNptVO;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * Realiza a geração do arquivo CSV do arquivo de conta npt
 * 
 * @author felipe.rocha
 */
@Stateless
public class ContaNptRN extends BaseBusiness {

	private static final long serialVersionUID = 3805116582799980576L;
	
	@Inject
	private  FatContasHospitalaresDAO contasHospitalaresDAO;
	
	private final String SEPARADOR=";";
	
	private final String QUEBRA_LINHA = "\n";

	private final String nomeArquivo="NPT_DIG_";
	
	private final String ENCODE="ISO-8859-1";

	protected enum ContaNPTRNExceptionCode implements BusinessExceptionCode {
		MS02_CONTAS_COM_NPT, MS03_CONTAS_COM_NPT;
	}
	
	private List<ContaNptVO> obterDadosContasComNPT() throws ApplicationBusinessException{
		List<ContaNptVO> listaContaHospitalares = new LinkedList<ContaNptVO>();
		
		listaContaHospitalares = contasHospitalaresDAO.obterContasComNPT();
		if (listaContaHospitalares.size() == 0) {
			throw new ApplicationBusinessException(ContaNPTRNExceptionCode.MS03_CONTAS_COM_NPT);
		}
		
		return listaContaHospitalares;
		
	}
	
	
	public ArquivoURINomeQtdVO gerarCSVDadosContasComNPT(FatCompetencia competencia) throws ApplicationBusinessException {
		if (competencia == null) {
			throw new ApplicationBusinessException(ContaNPTRNExceptionCode.MS02_CONTAS_COM_NPT);
		}
		final List<ContaNptVO> listaLinhas = obterDadosContasComNPT();
		Writer out = null;
		try {
			File file = File.createTempFile(obterNomeArquivo(competencia), DominioNomeRelatorio.EXTENSAO_CSV);
			out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);
			out.write(gerarCabecalhoDoRelatorio());
			if (!listaLinhas.isEmpty()) {
				for (ContaNptVO linha : listaLinhas) {
					out.write(gerarLinhasDoRelatorio(linha));
				}
			}
			ArquivoURINomeQtdVO vo = new ArquivoURINomeQtdVO(file.toURI(), obterNomeArquivo(competencia), listaLinhas.size(), 1);
			out.flush();
			
			return vo;
		} catch (IOException e) {
			throw new ApplicationBusinessException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage());
		} finally {
			IOUtils.closeQuietly(out);
		}
	}
	
	private String obterNomeArquivo(FatCompetencia competencia){
		return nomeArquivo +  competencia.getId().getMes() + competencia.getId().getAno() + DominioNomeRelatorio.EXTENSAO_CSV;
	}

	 private String gerarCabecalhoDoRelatorio() {
		StringBuilder builder = new StringBuilder();
		builder.append(getResourceBundleValue("LABEL_CONTAS_COM_NPT_CONTA"))
			.append(SEPARADOR)
			.append(getResourceBundleValue("LABEL_CONTAS_COM_NPT_DATA_ENCERRAMENTO"))
			.append(SEPARADOR)
			.append(getResourceBundleValue("LABEL_CONTAS_COM_NPT_NUMERO_AIH"))
			.append(SEPARADOR)
			.append(getResourceBundleValue("LABEL_CONTAS_COM_NPT_SITUACAO"))
			.append(SEPARADOR)
			.append(QUEBRA_LINHA);
		return builder.toString();
	}
	
	 
	 private String gerarLinhasDoRelatorio(ContaNptVO linha) {
			StringBuilder builder = new StringBuilder();
			builder.append(linha.getSeq())
				.append(SEPARADOR)
				.append(linha.getDtEncerramento() != null ? DateUtil.obterDataFormatada(linha.getDtEncerramento(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO_SEGUNDOS ): "")
				.append(SEPARADOR)
				.append(linha.getNroAih() != null ? linha.getNroAih() : "")
				.append(SEPARADOR)
				.append(linha.getSituacao().getDescricao())
				.append(SEPARADOR)
				.append(QUEBRA_LINHA);
			
			return builder.toString();
		}
	@Override
	protected Log getLogger() {
		// TODO Auto-generated method stub
		return null;
	}

}
