package br.gov.mec.aghu.estoque.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.estoque.dao.SceValidadeDAO;
import br.gov.mec.aghu.estoque.vo.RelatorioMateriaisValidadeVencidaVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class GerarRelatorioMateriaisValidadeVencidaON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(GerarRelatorioMateriaisValidadeVencidaON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@Inject
	private SceValidadeDAO sceValidadeDAO;

	private static final String SEPARADOR = ";";
	private static final String ENCODE = "ISO-8859-1";
	private static final String EXTENSAO = ".csv";

	private enum GerarRelatorioMateriaisValidadeVencidaONExceptionCode
			implements BusinessExceptionCode {
		MENSAGEM_DATA_INICIAL_NAO_INFORMADA, MENSAGEM_DATA_FINAL_NAO_INFORMADA, MENSAGEM_DATA_FINAL_MAIOR_DATA_INICIAL_VALIDADE_VENCIA, MENSAGEM_PERIODO_MENOR_6_MESES;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 8952261290995684306L;

	private SceValidadeDAO getSceValidadeDAO() {
		return sceValidadeDAO;
	}

	
	/**
	 * Retorna uma lista com os dados para relatorio de material de validade
	 * vencida
	 * 
	 * @param seqAlmoxarifado
	 * @param codigoGrupo
	 * @param dataInicial
	 * @param paramDataFinal
	 * @param numeroFornecedor
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<RelatorioMateriaisValidadeVencidaVO> pesquisarDadosRelatorioMaterialValidadeVencida(Short seqAlmoxarifado, 
																								    Integer codigoGrupo, 
																								    Date dataInicial,
																								    Date paramDataFinal, 
																								    Integer numeroFornecedor) throws ApplicationBusinessException {

		List<RelatorioMateriaisValidadeVencidaVO> itensRelatorio = null;
		validarPesquisaRelatorioMaterialValidadeVencida(dataInicial, paramDataFinal);
		itensRelatorio = getSceValidadeDAO().pesquisarDadosRelatorioMaterialValidadeVencida(seqAlmoxarifado, codigoGrupo, dataInicial, paramDataFinal,numeroFornecedor);
		
		for (RelatorioMateriaisValidadeVencidaVO item : itensRelatorio) {		
				item.setValido(DateUtil.validaDataMaiorIgual(item.getValidade(), new Date()));
		}
		return itensRelatorio;
	}	
	
	/**
	 * 
	 * @param dataInicial
	 * @param dataFinal
	 * @throws ApplicationBusinessException
	 */
	private void validarPesquisaRelatorioMaterialValidadeVencida(Date dataInicial, Date dataFinal) throws ApplicationBusinessException {

		// RN04
		if (dataInicial != null && dataFinal == null) {
			throw new ApplicationBusinessException(
					GerarRelatorioMateriaisValidadeVencidaONExceptionCode.MENSAGEM_DATA_FINAL_NAO_INFORMADA);
		}
		// RN05
		if (dataInicial == null && dataFinal != null) {
			throw new ApplicationBusinessException(
					GerarRelatorioMateriaisValidadeVencidaONExceptionCode.MENSAGEM_DATA_INICIAL_NAO_INFORMADA);
		}

		if (dataInicial != null && dataFinal != null) {
			// RN02
			if (dataInicial.after(dataFinal)) {
				throw new ApplicationBusinessException(
						GerarRelatorioMateriaisValidadeVencidaONExceptionCode.MENSAGEM_DATA_FINAL_MAIOR_DATA_INICIAL_VALIDADE_VENCIA);
			}
			// RN03
			int diferencaMeses = DateUtil.difMeses(dataFinal, dataInicial);
			if(diferencaMeses > 6) {
				throw new ApplicationBusinessException(GerarRelatorioMateriaisValidadeVencidaONExceptionCode.MENSAGEM_PERIODO_MENOR_6_MESES);
			}			
			if(diferencaMeses == 6) {
				Calendar calendarInicio = Calendar.getInstance();
				calendarInicio.setTime(dataInicial);
				Calendar calendarFim = Calendar.getInstance();
				calendarFim.setTime(dataFinal);
				int diaDataInicio = calendarInicio.get(Calendar.DAY_OF_MONTH);
				int diaDataFinal = calendarFim.get(Calendar.DAY_OF_MONTH);
				if(diaDataFinal > diaDataInicio) {
					throw new ApplicationBusinessException(GerarRelatorioMateriaisValidadeVencidaONExceptionCode.MENSAGEM_PERIODO_MENOR_6_MESES);
				}
			}
		}
	}

	/**
	 * Gera o relatório de materiais com validade vencida como arquivo CSV
	 * 
	 * @param seqAlmoxarifado
	 * @param codigoGrupo
	 * @param dataInicial
	 * @param dataFinal
	 * @param numeroFornecedor
	 * @return
	 * @throws IOException
	 * @throws ApplicationBusinessException
	 */
	public String gerarCSVRelatorioRelatorioMaterialValidadeVencida(
			Short seqAlmoxarifado, Integer codigoGrupo, Date dataInicial,
			Date dataFinal, Integer numeroFornecedor) throws IOException,
			ApplicationBusinessException {
		File file = null;
		List<RelatorioMateriaisValidadeVencidaVO> listaRelatorio = this
				.pesquisarDadosRelatorioMaterialValidadeVencida(
						seqAlmoxarifado, codigoGrupo, dataInicial, dataFinal,
						numeroFornecedor);
		Writer out = null;
		Integer totalMaterial = 0, ultimoCodigoMaterial = null;
		String situacao = null;
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String fileName = null;
		boolean mudouMaterial = false;
		if (listaRelatorio != null && !listaRelatorio.isEmpty()) {
			file = File.createTempFile(
					DominioNomeRelatorio.RELATORIO_MATERIAIS_VALIDADE_VENCIDA
							.toString(), EXTENSAO);
			out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);
			if (codigoGrupo != null) {
				out.write("GRUPO MATERIAL" + SEPARADOR
						+ listaRelatorio.get(0).getGmtDescricao() + SEPARADOR
						+ SEPARADOR + SEPARADOR + SEPARADOR + SEPARADOR
						+ SEPARADOR + SEPARADOR + "\n");
			}

			out.write("MATERIAL" + SEPARADOR + "CÓDIGO" + SEPARADOR + "ALMOX"
					+ SEPARADOR + "FORN/ PROPR" + SEPARADOR + "UNID"
					+ SEPARADOR + "END" + SEPARADOR + "QTD DISP" + SEPARADOR
					+ "SITUAÇÃO" + SEPARADOR + "VALIDADE" + SEPARADOR
					+ "QTDE VALD\n");

			// Escreve a linha referente ao cabeçalho
			for (RelatorioMateriaisValidadeVencidaVO registro : listaRelatorio) {
				if (ultimoCodigoMaterial == null
						|| !ultimoCodigoMaterial.equals(registro
								.getCodigoMaterial())) {
					if (ultimoCodigoMaterial != null) {
						tratarTotalizador(out, totalMaterial);
						out.write("MATERIAL" + SEPARADOR + "CÓDIGO" + SEPARADOR
								+ "ALMOX" + SEPARADOR + "FORN/ PROPR"
								+ SEPARADOR + "UNID" + SEPARADOR + "END"
								+ SEPARADOR + "QTD DISP" + SEPARADOR
								+ "SITUAÇÃO" + SEPARADOR + "VALIDADE"
								+ SEPARADOR + "QTDE VALD\n");
					}
					ultimoCodigoMaterial = registro.getCodigoMaterial();
					mudouMaterial = true;
					totalMaterial = 0;
				} else {
					mudouMaterial = false;
				}
				if (registro.getValido()) {
					situacao = "A VENCER";
				} else {
					situacao = "VENCIDO";
				}
				if (mudouMaterial) {
					out.write(registro.getNomeMaterial() + SEPARADOR
							+ registro.getCodigoMaterial() + SEPARADOR
							+ registro.getAlmoxarifado() + " "
							+ registro.getAlmoxDescricao() + SEPARADOR
							+ registro.getNumeroFornecedor() + SEPARADOR
							+ registro.getEstoqueAlmoxUmdCodigo() + SEPARADOR
							+ registro.getEstoqueAlmoxEndereco() + SEPARADOR
							+ registro.getEstoqueAlmoxQuantidadeDisponivel()
							+ SEPARADOR + situacao + SEPARADOR
							+ sdf.format(registro.getValidade()) + SEPARADOR
							+ registro.getQuantidadeDisponivelValidade() + "\n");
				} else {
					out.write(SEPARADOR + SEPARADOR + SEPARADOR + SEPARADOR
							+ SEPARADOR + SEPARADOR + SEPARADOR + situacao
							+ SEPARADOR + sdf.format(registro.getValidade())
							+ SEPARADOR
							+ registro.getQuantidadeDisponivelValidade() + "\n");
				}

				totalMaterial += registro.getQuantidadeDisponivelValidade();
			}
			if (ultimoCodigoMaterial != null) {
				tratarTotalizador(out, totalMaterial);
			}
			fileName = file.getAbsolutePath();
			out.flush();
			out.close();
		}

		return fileName;
	}

	private void tratarTotalizador(Writer out, Integer totalMaterial)
			throws IOException {
		out.write(SEPARADOR + SEPARADOR + SEPARADOR + SEPARADOR + SEPARADOR
				+ SEPARADOR + SEPARADOR + SEPARADOR + SEPARADOR + totalMaterial
				+ "\n");
	}

	public String nameHeaderEfetuarDownloadCSVMaterialValidadeVencida()	throws IOException {
		return "Materiais_validade_vencida_" + new SimpleDateFormat("MM_yyyy").format(new Date()) + EXTENSAO;
	}
}