package br.gov.mec.aghu.faturamento.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;

import br.gov.mec.aghu.business.exception.AghuCSVBaseException.AghuCSVBaseExceptionExceptionCode;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.faturamento.dao.FatCompetenciaDAO;
import br.gov.mec.aghu.faturamento.dao.FatContasHospitalaresDAO;
import br.gov.mec.aghu.faturamento.vo.ArquivoURINomeQtdVO;
import br.gov.mec.aghu.faturamento.vo.ContaRepresentadaVO;
import br.gov.mec.aghu.faturamento.vo.MesAnoVO;
import br.gov.mec.aghu.internacao.dao.AinLeitosDAO;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.AghuNumberFormat;
import br.gov.mec.aghu.core.utils.StringUtil;

/**
 * Realiza a geração do arquivo CSV do arquivo de conta npt
 * 
 * @author felipe.rocha
 */
@Stateless
public class ContaRepresentadaRN extends BaseBusiness {

	private static final long serialVersionUID = 3805116582799980576L;

	@Inject
	private FatContasHospitalaresDAO contasHospitalaresDAO;

	@Inject
	private FatCompetenciaDAO competenciaDAO;

	@Inject
	private AinLeitosDAO ainLeitoDAO;

	private final String SEPARADOR = ";";

	private final String QUEBRA_LINHA = "\n";

	private final String nomeArquivo = "ContasReapresentadas";

	private final String ENCODE = "ISO-8859-1";

	protected enum ContaNPTRNExceptionCode implements BusinessExceptionCode {
		MS01_CONTA_REPRESENTADA, MS03_CONTA_REPRESENTADA;
	}

	private List<ContaRepresentadaVO> obterDadosContasRepresentadas(
			FatCompetencia competencia, Date dataInicio)
			throws ApplicationBusinessException {
		List<ContaRepresentadaVO> listaContaHospitalares = new LinkedList<ContaRepresentadaVO>();
		listaContaHospitalares = contasHospitalaresDAO.obterContasRepresentadas(competencia.getId().getMes(),competencia.getId().getAno(), dataInicio);
		if (listaContaHospitalares.size() == 0) {
			throw new ApplicationBusinessException(ContaNPTRNExceptionCode.MS03_CONTA_REPRESENTADA);
		}
		return listaContaHospitalares;

	}

	/**
	 * @ORADB FATC_BUSCA_COMP_REAPRE
	 * @param cthSeq
	 * @return
	 */
	public String obterCompetenciaContaRepresentada(Integer cthSeq) {
		MesAnoVO comp = contasHospitalaresDAO
				.obterCompetenciaContaRepresentadaMeAno(cthSeq);
		if (comp != null && comp.getMes() != null) {
			if (comp.getMes().toString().length() == 1) {
				return comp.getMes().toString() + comp.getAno().toString();
			} else if (comp.getMes().toString().length() == 0) {
				return StringUtil.adicionaZerosAEsquerda(comp.getMes(), 1)
						+ comp.getAno().toString();
			}
		}
		return null;
	}

	public ArquivoURINomeQtdVO obterDadosContaRepresentada(
			FatCompetencia competencia) throws ApplicationBusinessException {
		Date dataInicio;
		if (competencia == null) {
			throw new ApplicationBusinessException(
					ContaNPTRNExceptionCode.MS01_CONTA_REPRESENTADA);
		} else {
			dataInicio = competenciaDAO.obterDataInicioPorCompetencia(
					competencia.getId().getMes(), competencia.getId().getAno());
			if (dataInicio == null) {
				throw new ApplicationBusinessException(
						ContaNPTRNExceptionCode.MS01_CONTA_REPRESENTADA);
			}
		}

		final List<ContaRepresentadaVO> listaLinhas = obterDadosContasRepresentadas(
				competencia, dataInicio);
		Writer out = null;
		try {
			File file = File.createTempFile(obterNomeArquivo(competencia),
					DominioNomeRelatorio.EXTENSAO_CSV);
			out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);
			out.write(gerarCabecalhoDoRelatorio());
			if (!listaLinhas.isEmpty()) {
				for (ContaRepresentadaVO linha : listaLinhas) {
					out.write(gerarLinhasDoRelatorio(linha));
				}
			}
			ArquivoURINomeQtdVO vo = new ArquivoURINomeQtdVO(file.toURI(), obterNomeArquivo(competencia), listaLinhas.size(), 1);
			out.flush();

			return vo;
		} catch (IOException e) {
			throw new ApplicationBusinessException(
					AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV,
					e, e.getLocalizedMessage());
		} finally {
			IOUtils.closeQuietly(out);
		}
	}

	private String obterNomeArquivo(FatCompetencia competencia) {
		return nomeArquivo + competencia.getId().getMes()
				+ competencia.getId().getAno()
				+ DominioNomeRelatorio.EXTENSAO_CSV;
	}

	private String converterNumeroToString(BigDecimal numero) {
		String numStr = "";
		if (numero != null) {
			numStr = AghuNumberFormat.formatarNumeroMoeda(numero);
		}
		return numStr;
	}

	private String calcularValoresSub(BigDecimal valor1, BigDecimal valor2) {
		if (valor1 != null && valor2 != null) {
			return converterNumeroToString(valor1.subtract(valor2));
		} else{
			return "";
		}
	}
	
	
	private BigDecimal calcularValores(ContaRepresentadaVO vo, Integer tipo) {
		BigDecimal resultado = BigDecimal.ZERO;
		if (tipo == 1) {
			resultado = calcularValores(vo, resultado);
		} else {
			resultado = calcularValores1(vo, resultado);
		}
		return resultado;
	}

	private BigDecimal calcularValores(ContaRepresentadaVO vo,
			BigDecimal resultado) {

		if (vo.getValorSh() == null || vo.getValorUti() == null
				|| vo.getValorUtie() == null || vo.getValorSp() == null
				|| vo.getValorAcomp() == null || vo.getValorRn() == null
				|| vo.getValorHemat() == null || vo.getValorTransp() == null
				|| vo.getValorOpm() == null
				|| vo.getValorProcedimento() == null
				|| vo.getValorAnestesista() == null
				|| vo.getValorSadt() == null) {
			return null;
		} else {
			resultado = resultado.add(vo.getValorSh());
			resultado = resultado.add(vo.getValorUti());
			resultado = resultado.add(vo.getValorUtie());
			resultado = resultado.add(vo.getValorSp());
			resultado = resultado.add(vo.getValorAcomp());
			resultado = resultado.add(vo.getValorRn());
			resultado = resultado.add(vo.getValorHemat());
			resultado = resultado.add(vo.getValorTransp());
			resultado = resultado.add(vo.getValorOpm());
			resultado = resultado.add(vo.getValorProcedimento());
			resultado = resultado.add(vo.getValorAnestesista());
			resultado = resultado.add(vo.getValorSadt());
		}
		return resultado;
	}

	private BigDecimal calcularValores1(ContaRepresentadaVO vo,
			BigDecimal resultado) {

		if (vo.getValorSh1() == null || vo.getValorUti1() == null
				|| vo.getValorUtie1() == null || vo.getValorSp1() == null
				|| vo.getValorAcomp1() == null || vo.getValorRn1() == null
				|| vo.getValorHemat1() == null || vo.getValorTransp1() == null
				|| vo.getValorOpm1() == null
				|| vo.getValorProcedimento1() == null
				|| vo.getValorAnestesista1() == null
				|| vo.getValorSadt1() == null) {
			return null;
		} else {
			resultado = resultado.add(vo.getValorSh1());
			resultado = resultado.add(vo.getValorUti1());
			resultado = resultado.add(vo.getValorUtie1());
			resultado = resultado.add(vo.getValorSp1());
			resultado = resultado.add(vo.getValorAcomp1());
			resultado = resultado.add(vo.getValorRn1());
			resultado = resultado.add(vo.getValorHemat1());
			resultado = resultado.add(vo.getValorTransp1());
			resultado = resultado.add(vo.getValorOpm1());
			resultado = resultado.add(vo.getValorProcedimento1());
			resultado = resultado.add(vo.getValorAnestesista1());
			resultado = resultado.add(vo.getValorSadt1());
		}
		return resultado;
	}

	private String gerarCabecalhoDoRelatorio() {
		StringBuilder builder = new StringBuilder();
		builder.append(getResourceBundleValue("LABEL_CONTA_REPRESENTADA_CONTA"))
				.append(SEPARADOR)
				.append(getResourceBundleValue("LABEL_CONTA_REPRESENTADA_PRONTUARIO"))
				.append(SEPARADOR)
				.append(getResourceBundleValue("LABEL_CONTA_REPRESENTADA_PACIENTE"))
				.append(SEPARADOR)
				.append(getResourceBundleValue("LABEL_CONTA_REPRESENTADA_SERVICO"))
				.append(SEPARADOR)
				.append(getResourceBundleValue("LABEL_CONTA_REPRESENTADA_UNIDADE"))
				.append(SEPARADOR)
				.append(getResourceBundleValue("LABEL_CONTA_REPRESENTADA_RZDO"))
				.append(SEPARADOR)
				.append(getResourceBundleValue("LABEL_CONTA_REPRESENTADA_DCIH"))
				.append(SEPARADOR)
				.append(getResourceBundleValue("LABEL_CONTA_REPRESENTADA_COMPLEXIDADE"))
				.append(SEPARADOR)
				.append(getResourceBundleValue("LABEL_CONTA_REPRESENTADA_AIH"))
				.append(SEPARADOR)
				.append(getResourceBundleValue("LABEL_CONTA_REPRESENTADA_VALOR_CONTA"))
				.append(SEPARADOR)
				.append(getResourceBundleValue("LABEL_CONTA_REPRESENTADA_COMPET"))
				.append(SEPARADOR)
				.append(getResourceBundleValue("LABEL_CONTA_REPRESENTADA_VALOR_CONTA_ANTERIOR"))
				.append(SEPARADOR)
				.append(getResourceBundleValue("LABEL_CONTA_REPRESENTADA_VALOR_DIFERENCA"))
				.append(SEPARADOR)
				.append(getResourceBundleValue("LABEL_CONTA_REPRESENTADA_DESCRICAO"))
				.append(SEPARADOR).append(QUEBRA_LINHA);
		return builder.toString();
	}

	private String gerarLinhasDoRelatorio(ContaRepresentadaVO linha) {
		Short unidadeFuncional = ainLeitoDAO.buscarUnidadeFuncional(linha.getSeq());
		BigDecimal valoresTotais = calcularValores(linha, 1);
		String competenciaContaRepresentada = obterCompetenciaContaRepresentada(linha.getCthRepresentadaSeq());
		BigDecimal valoresTotais1 = calcularValores(linha, 2);
		String valoresTotais2 = calcularValoresSub(valoresTotais, valoresTotais1);

		StringBuilder builder = obterLinhas(linha, unidadeFuncional,
				valoresTotais, competenciaContaRepresentada, valoresTotais1,
				valoresTotais2);

		return builder.toString();
	}
	
	private Object verificarValorNulo(Object valor){
		if (valor !=null) {
			return valor;
		}else{
			return "";
		}
	}

	private StringBuilder obterLinhas(ContaRepresentadaVO linha,
			Short unidadeFuncional, BigDecimal valoresTotais,
			String competenciaContaRepresentada, BigDecimal valoresTotais1,
			String valoresTotais2) {
		StringBuilder builder = new StringBuilder();
		builder.append(verificarValorNulo(linha.getSeq()))
				.append(SEPARADOR)
				.append(verificarValorNulo(linha.getPacProntuario()))
				.append(SEPARADOR)
				.append(verificarValorNulo(linha.getPacNome()))
				.append(SEPARADOR)
				.append(verificarValorNulo(linha.getCctCodigo()))
				.append(SEPARADOR)
				.append(verificarValorNulo(unidadeFuncional))
				.append(SEPARADOR)
				.append(verificarValorNulo(linha.getIphCodSusRealiz()))
				.append(SEPARADOR)
				.append(verificarValorNulo(linha.getDciCodDcih()))
				.append(SEPARADOR)
				.append(obterIndDciTransplante(linha))
				.append(SEPARADOR)
				.append(verificarValorNulo(linha.getNroAih()))
				.append(SEPARADOR)
				.append(converterNumeroToString(valoresTotais))
				.append(SEPARADOR)
				.append(verificarValorNulo(competenciaContaRepresentada))
				.append(SEPARADOR)
				.append(converterNumeroToString(valoresTotais1))
				.append(SEPARADOR)
				.append(valoresTotais2)
				.append(SEPARADOR)
				.append(verificarValorNulo(linha.getDescricao())).append(QUEBRA_LINHA);
		return builder;
	}

	private String obterIndDciTransplante(ContaRepresentadaVO linha) {
		return linha.getIndDcihTransplante() == Boolean.TRUE ? getResourceBundleValue("LABEL_CONTA_REPRESENTADA_ESTRATEGICO")
				: linha.getIndFaec() == Boolean.TRUE ? getResourceBundleValue("LABEL_CONTA_REPRESENTADA_ALTA_COMPLEXIDADE")			: getResourceBundleValue("LABEL_CONTA_REPRESENTADA_MEDIA_COMPLEXIDADE");
	}

	@Override
	protected Log getLogger() {
		// TODO Auto-generated method stub
		return null;
	}

}
