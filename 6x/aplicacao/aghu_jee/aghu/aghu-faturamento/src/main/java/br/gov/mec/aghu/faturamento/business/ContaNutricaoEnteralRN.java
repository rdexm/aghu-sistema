package br.gov.mec.aghu.faturamento.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.exception.AghuCSVBaseException.AghuCSVBaseExceptionExceptionCode;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.faturamento.vo.ArquivoURINomeQtdVO;
import br.gov.mec.aghu.faturamento.vo.ContaNutricaoEnteralVO;
import br.gov.mec.aghu.paciente.dao.AipPacientesDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;



/**
 * Realiza a geração do arquivo CSV do arquivo de nutrição
 * 
 * @author felipe.rocha
 */
@Stateless
public class ContaNutricaoEnteralRN extends BaseBusiness {

	private static final long serialVersionUID = 3805116582799980576L;
	
	@Inject
	private AipPacientesDAO aipPacientesDAO;
	@EJB
	private IParametroFacade parametroFacade;
	
	private final String SEPARADOR=";";
	
	private final String QUEBRA_LINHA = "\n";
	
	private final String N = "N";
	
	private final String nomeArquivo="DIAS_NE_";
	
	private final String formatacao="ddMMYYYY-HHmmss";
	
	private final String ENCODE="ISO-8859-1";
	

	
	protected enum ContaNutricaoEnteralRNExceptionCode implements BusinessExceptionCode {
		MSG_CONTAS_NUTRICAO_ETERAL_M2;
	}
	
	private List<ContaNutricaoEnteralVO> obterDadosContaNutricaoEnteral() throws ApplicationBusinessException{
		// obter parametros
		
		List<Integer> listaParametros = new ArrayList<Integer>();
		List<ContaNutricaoEnteralVO> listaContaNutricaoEnteralVO = new ArrayList<ContaNutricaoEnteralVO>();
		
		Integer nutricaoEnteralAdulto = parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_PHI_SEQ_NUTRICAO_ENTERAL_ADULTO);
		Integer nutricaoEnteralNeo = parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_PHI_SEQ_NUTRICAO_ENTERAL_NEONATOLOGIA);
		Integer nutricaoEnteralPediatra = parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_PHI_SEQ_NUTRICAO_ENTERAL_PEDIATRIA);
		
		listaParametros.add(nutricaoEnteralAdulto);
		listaParametros.add(nutricaoEnteralNeo);
		listaParametros.add(nutricaoEnteralPediatra);
		
		listaContaNutricaoEnteralVO = aipPacientesDAO.obterContaNutricaoEnteral(listaParametros);
		
		if (listaContaNutricaoEnteralVO.size() == 0) {
			throw new ApplicationBusinessException(ContaNutricaoEnteralRNExceptionCode.MSG_CONTAS_NUTRICAO_ETERAL_M2);
		}
		
		return listaContaNutricaoEnteralVO;
		
	}

	public ArquivoURINomeQtdVO gerarCSVDadosContaNutricaoEnteral() throws ApplicationBusinessException {

		final List<ContaNutricaoEnteralVO> listaLinhas = obterDadosContaNutricaoEnteral();
		Writer out = null;
		try {
			File file = File.createTempFile(obterNomeArquivo(), DominioNomeRelatorio.EXTENSAO_CSV);
			out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);
			out.write(gerarCabecalhoDoRelatorio());
			if (!listaLinhas.isEmpty()) {
				for (ContaNutricaoEnteralVO linha : listaLinhas) {
					out.write(gerarLinhasDoRelatorio(linha));
				}
			}
			ArquivoURINomeQtdVO vo = new ArquivoURINomeQtdVO(file.toURI(), obterNomeArquivo(), listaLinhas.size(), 1);
			out.flush();
			
			return vo;
		} catch (IOException e) {
			throw new ApplicationBusinessException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage());
		} finally {
			IOUtils.closeQuietly(out);
		}
	}
	
	private String obterNomeArquivo(){
		return nomeArquivo +  obterDataNomeArquivo() + DominioNomeRelatorio.EXTENSAO_CSV;
	}
	
	private String obterDataNomeArquivo(){
		return 	DateUtil.dataToString(new Date(), formatacao);
	}

	 private String gerarCabecalhoDoRelatorio() {
		StringBuilder builder = new StringBuilder();
		builder.append(getResourceBundleValue("LABEL_CONTAS_NUTRICAO_ETERAL_CONTA"))
			.append(SEPARADOR)
			.append(getResourceBundleValue("LABEL_CONTAS_NUTRICAO_ETERAL_AUTORIZACAO"))
			.append(SEPARADOR)
			.append(getResourceBundleValue("LABEL_CONTAS_NUTRICAO_ETERAL_PRONTUARIO"))
			.append(SEPARADOR)
			.append(getResourceBundleValue("LABEL_CONTAS_NUTRICAO_ETERAL_NOME"))
			.append(SEPARADOR)
			.append(getResourceBundleValue("LABEL_CONTAS_NUTRICAO_ETERAL_DIAS"))
			.append(SEPARADOR)
			.append(getResourceBundleValue("LABEL_CONTAS_NUTRICAO_ETERAL_QTDE"))
			.append(SEPARADOR)
			.append(QUEBRA_LINHA);
		return builder.toString();
	}
	
	 
	 private String gerarLinhasDoRelatorio(ContaNutricaoEnteralVO linha) {
			StringBuilder builder = new StringBuilder();
			builder.append(linha.getSeq())
				.append(SEPARADOR)
				.append(StringUtils.isBlank(linha.getIndAutorizadoSms()) ? N : linha.getIndAutorizadoSms())
				.append(SEPARADOR)
				.append(linha.getProntuario())
				.append(SEPARADOR)
				.append(linha.getNome())
				.append(SEPARADOR)
				.append(calcularDiferencaData(linha.getDtIntAdministrativa(), linha.getDtAltaAdministrativa()))
				.append(SEPARADOR)
				.append(String.valueOf(linha.getQuantidadesRealizadas()))
				.append(SEPARADOR)
				.append(QUEBRA_LINHA);
			
			return builder.toString();
		}
	 
	 
	private Integer calcularDiferencaData(Date dtAltaAdministrativa, Date dtIntAdministrativa){
		return DateUtil.calcularDiasEntreDatasComPrecisao(dtAltaAdministrativa, dtIntAdministrativa).intValue();	
	}
	@Override
	protected Log getLogger() {
		// TODO Auto-generated method stub
		return null;
	}

}
