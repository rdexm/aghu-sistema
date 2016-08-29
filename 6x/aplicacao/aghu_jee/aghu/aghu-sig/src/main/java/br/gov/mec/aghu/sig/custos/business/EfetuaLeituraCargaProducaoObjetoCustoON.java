package br.gov.mec.aghu.sig.custos.business;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import br.gov.mec.aghu.dominio.DominioColunaExcel;
import br.gov.mec.aghu.sig.custos.vo.EntradaProducaoObjetoCustoVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class EfetuaLeituraCargaProducaoObjetoCustoON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(EfetuaLeituraCargaProducaoObjetoCustoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


	private static final long serialVersionUID = -2381840198294999707L;

	private enum EfetuaLeituraCargaProducaoObjetoCustoONExceptionCode implements BusinessExceptionCode {
		ERRO_LEITURA_ARQUIVO_ARQUIVO
	}

	public List<EntradaProducaoObjetoCustoVO> efetuaLeituraExcel(InputStream arquivo, DominioColunaExcel colCentroCusto, DominioColunaExcel colValor, int linInicial)
			throws ApplicationBusinessException, IOException {

		List<EntradaProducaoObjetoCustoVO> listaEntrada = null;
		try {
			listaEntrada = new ArrayList<EntradaProducaoObjetoCustoVO>();
			linInicial--;
			HSSFWorkbook workbook = new HSSFWorkbook(arquivo);
			HSSFSheet sheet = workbook.getSheetAt(0);
			Iterator<Row> rows = sheet.rowIterator();
			while (rows.hasNext()) {
				HSSFRow row = (HSSFRow) rows.next();
				Iterator<Cell> cells = row.cellIterator();

				EntradaProducaoObjetoCustoVO entrada = this.getValorCelula(cells, colCentroCusto, colValor, linInicial);

				if (!(entrada.getCentroCusto() == null && entrada.isLeituraCorreta() == true && entrada.getValor() == null)) {
					listaEntrada.add(entrada);
				}
			}
		} catch (Exception ex) {
			if (listaEntrada.isEmpty()) {
				throw new ApplicationBusinessException(EfetuaLeituraCargaProducaoObjetoCustoONExceptionCode.ERRO_LEITURA_ARQUIVO_ARQUIVO);
			}
		}
		return listaEntrada;
	}

	private void formataMensagemErro(EntradaProducaoObjetoCustoVO entrada, String mensagem, HSSFCell cell, Integer nroColuna) {
		entrada.setLeituraCorreta(false);
		Integer nroLinha;
		if (cell != null) {
			nroLinha = cell.getRow().getRowNum();
		} else {
			nroLinha = nroColuna;
		}
		nroLinha++;
		entrada.setErroLeitura(this.buscarMensagem(mensagem, nroLinha));
	}

	private EntradaProducaoObjetoCustoVO getValorCelula(Iterator<Cell> cells, DominioColunaExcel colCentroCusto, DominioColunaExcel colValor, int linInicial) {
		final String m02 = "MESAGEM_LEITURA_DOCUMENTO_M02";
		final String m03 = "MESAGEM_LEITURA_DOCUMENTO_M03";
		final String m04 = "MESAGEM_LEITURA_DOCUMENTO_M04";
		final String m05 = "MESAGEM_LEITURA_DOCUMENTO_M05";
		final String m06 = "MESAGEM_LEITURA_DOCUMENTO_M06";

		EntradaProducaoObjetoCustoVO entrada = new EntradaProducaoObjetoCustoVO();
		Integer nroCelula = null;
		boolean isCentroCusto = false;
		boolean isValor = false;

		while (cells.hasNext()) {
			HSSFCell cell = (HSSFCell) cells.next();

			if (cell.getRow().getRowNum() >= linInicial) {
				nroCelula = cell.getRow().getRowNum();
				if (colCentroCusto != null && cell.getColumnIndex() == colCentroCusto.getNumeroColuna()) {
					isCentroCusto = true;
					Double retorno = this.getValorCelula(cell, entrada, m03);
					if (retorno != null) {
						entrada.setCentroCusto(retorno.intValue());
					} else if (entrada.getErroLeitura().isEmpty()) {
						formataMensagemErro(entrada, m02, cell, null);
					}
				}
				if (colValor.getNumeroColuna() == cell.getColumnIndex()) {
					isValor = true;
					Double retorno = this.getValorCelula(cell, entrada, m05);
					if (retorno == null && entrada.getErroLeitura().isEmpty()) {
						formataMensagemErro(entrada, m04, cell, null);
					} else if (retorno != null && retorno < 0D) {
						formataMensagemErro(entrada, m06, cell, null);
					}
					entrada.setValor(retorno);
				}
				entrada.setNroLinha(nroCelula);
			}
		}
		if (!isCentroCusto && nroCelula != null && entrada.getErroLeitura().isEmpty()) {
			formataMensagemErro(entrada, m02, null, nroCelula);
		}
		if (!isValor && nroCelula != null && entrada.getErroLeitura().isEmpty()) {
			formataMensagemErro(entrada, m04, null, nroCelula);
		}
		return entrada;
	}

	private Double getValorCelula(HSSFCell cell, EntradaProducaoObjetoCustoVO entrada, String mensagem) {
		if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
			return cell.getNumericCellValue();
		} else if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
			formataMensagemErro(entrada, mensagem, cell, null);
		} else if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
			formataMensagemErro(entrada, mensagem, cell, null);
		}
		return null;
	}

	protected String buscarMensagem(String chave, Object... parametros) {
		String mensagem = getResourceBundleValue(chave);
		mensagem = java.text.MessageFormat.format(mensagem, parametros);
		return mensagem;
	}

}