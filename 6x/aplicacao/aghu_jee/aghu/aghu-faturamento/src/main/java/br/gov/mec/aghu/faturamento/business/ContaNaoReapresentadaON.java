package br.gov.mec.aghu.faturamento.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.exception.AghuCSVBaseException.AghuCSVBaseExceptionExceptionCode;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.faturamento.vo.ArquivoURINomeQtdVO;
import br.gov.mec.aghu.faturamento.vo.ContaNaoReapresentadaCPFVO;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.model.FatCompetenciaId;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ContaNaoReapresentadaON extends BaseBusiness {
	
	private static final long serialVersionUID = 1496925216648125258L;

	private static final Log LOG = LogFactory.getLog(ContaNaoReapresentadaON.class);
	
	@EJB
	private ContaNaoReapresentadaRN contaNaoReapresentadaRN;
	
	private static final String QUEBRA_LINHA = "\n";
	private static final String SEPARADOR=";";
	private static final String ENCODE="ISO-8859-1";

	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	private enum ContaNaoReapresentadaONException implements BusinessExceptionCode {
		COMPETENCIA_NAO_SELECIONADA_CNR;

	}
	
	//	Ao clicar no botão deverá ser validado se uma competência foi selecionada. Caso não tenha sido selecionada nenhuma competência deverá interromper a regra e mostrar a mensagem MS01.
	//	Caso contrário, executar a RN01.
	//ON 1 
	private List<ContaNaoReapresentadaCPFVO> buscarDadosContasNaoReapresentadasCPF(FatCompetencia competencia) throws ApplicationBusinessException {
		try {
			Validate.notNull(competencia);
			return contaNaoReapresentadaRN.buscarDadosContasNaoReapresentadasCPF(competencia.getId().getAno(), competencia.getId().getMes(), competencia.getId().getDtHrInicio());
		} catch(IllegalArgumentException  e) {
			throw new ApplicationBusinessException(ContaNaoReapresentadaONException.COMPETENCIA_NAO_SELECIONADA_CNR);
		}		
	}
	
	public ArquivoURINomeQtdVO gerarCSVContasNaoReapresentadasCPF(FatCompetencia competencia) throws ApplicationBusinessException {
		ArquivoURINomeQtdVO resultado = null;
		Writer out = null;
		try {
			List<ContaNaoReapresentadaCPFVO>  lista = buscarDadosContasNaoReapresentadasCPF(competencia);
			
			String filename = criarNomeArquivo(competencia.getId());
			final File file = File.createTempFile(filename, DominioNomeRelatorio.EXTENSAO_CSV);
			
			out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);
			out.write(gerarCabecalhoDoRelatorio());

			if (lista != null && !lista.isEmpty()) {
				for(ContaNaoReapresentadaCPFVO linha : lista){
					out.write(gerarLinhasDoRelatorio(linha));
				}
				resultado = new ArquivoURINomeQtdVO(file.toURI(), filename, lista.size(), 1);
			}
			out.flush();
		} catch (IOException e) {
			 throw new ApplicationBusinessException( AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage());
		} finally {
			IOUtils.closeQuietly(out);
		}
		return resultado;
	}

	private String criarNomeArquivo(FatCompetenciaId id){
		String mes = String.valueOf(id.getMes());
		String ano = String.valueOf(id.getAno());
		ano = ano.substring(2);
		return "CONTAS_NAO_REAPR_CPF_" + mes +  ano + DominioNomeRelatorio.EXTENSAO_CSV;
	}
	
	
	private String gerarCabecalhoDoRelatorio() {
		StringBuilder builder = new StringBuilder();
		builder.append(getResourceBundleValue("LABEL_CONTA_CNR"))
			.append(SEPARADOR)
			.append(getResourceBundleValue("LABEL_CPF_CNR"))
			.append(SEPARADOR)
			.append(getResourceBundleValue("LABEL_REAPR_CNR"))
			.append(SEPARADOR)
			.append(getResourceBundleValue("LABEL_SUS_CNR"))
			.append(SEPARADOR)
			.append(QUEBRA_LINHA);
		return builder.toString();
	}
	
	private String gerarLinhasDoRelatorio(ContaNaoReapresentadaCPFVO linha) {
		StringBuilder builder = new StringBuilder();
		builder.append(linha.getEaiCthSeq() == null ? "" : String.valueOf(linha.getEaiCthSeq()))
			.append(SEPARADOR)
			.append(linha.getCpfCns() == null ? "" : String.valueOf(linha.getCpfCns()))
			.append(SEPARADOR)
			.append(linha.getContaReapresentada())
			.append(SEPARADOR)
			.append(linha.getIphCodSus() == null ? "" : String.valueOf(linha.getIphCodSus()))
			.append(SEPARADOR)
			.append(QUEBRA_LINHA);
		return builder.toString();
	}
}
