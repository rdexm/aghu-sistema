package br.gov.mec.aghu.faturamento.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.exception.AghuCSVBaseException.AghuCSVBaseExceptionExceptionCode;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.faturamento.vo.ContaApresentadaPacienteProcedimentoVO;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.AghuNumberFormat;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * 
 * @author paulo.silveira
 *
 */
@Stateless
public class RelatorioContaApresentadaEspMesON extends BaseBusiness {

	private static final long serialVersionUID = -8286559166434589404L;
	private static final Log LOG = LogFactory.getLog(RelatorioContaApresentadaEspMesON.class);
	
	private static final String QUEBRA_LINHA = "\n";
	private static final String SEPARADOR=";";

	// Utilizado para nao replicar a especialidade.	
	private static final String ENCODE="ISO-8859-1";
	
	@EJB
	private RelatorioContasApresentadasPorEspecialidadeRN apresentadasPorEspecialidadeRN;
	
	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	public String gerarCSVContaApresentadaEspMes(Short seqEspecialidade, FatCompetencia competencia) throws ApplicationBusinessException {
		String resultado = null;
		Writer out = null;
		try {
			List<ContaApresentadaPacienteProcedimentoVO> vo = apresentadasPorEspecialidadeRN.obterContaApresentadaEspecialidade(seqEspecialidade, competencia);
			final File file = File.createTempFile(criarNomeArquivo(), DominioNomeRelatorio.EXTENSAO_CSV);
			out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);
			out.write(gerarCabecalhoDoRelatorio());
			if (vo != null) {
				out.write(gerarLinhasDoRelatorio(vo));				
				resultado = file.getAbsolutePath();
			}
			out.flush();
		} catch (IOException e) {
			 throw new ApplicationBusinessException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage());
		} finally {
			IOUtils.closeQuietly(out);
		}
		return resultado;
	}
	
	public String mesAnoAtual(){
		Calendar cal = Calendar.getInstance();
		return 	nomeDoMes(cal.get(Calendar.MONTH)) + "-"+ cal.get(Calendar.YEAR);
	}	
	
	private static String nomeDoMes(int i) { 
		String mes[] = {"JANEIRO", "FEVEREIRO", "MARCO", "ABRIL", "MAIO", "JUNHO", "JULHO", "AGOSTO", "SETEMBRO", "OUTUBRO", "NOVEMBRO", "DEZEMBRO"};
			return(mes[i-1]);
	}
	
	/**
	 * Como não foi definido nome do arquivo
	 * usado o padrao: CONTAS_APRESENTAS_ESP_<MES>_<ANO>		
	 * @return
	 */
	private String criarNomeArquivo(){
		 return DominioNomeRelatorio.FATR_INT_ESPEC_MES.toString() + mesAnoAtual(); 
	}

	private String gerarCabecalhoDoRelatorio() {
		StringBuilder builder = new StringBuilder();
		builder
			.append(getResourceBundleValue("LABEL_ESPECIALIDADE_RCAE"))
			.append(SEPARADOR)
			.append(getResourceBundleValue("LABEL_SSM_RCAE"))
			.append(SEPARADOR)
			 .append(getResourceBundleValue("LABEL_AIH_RCAE"))
			.append(SEPARADOR)
			.append(getResourceBundleValue("LABEL_PRON_RCAE"))
			.append(SEPARADOR)
			.append(getResourceBundleValue("LABEL_PAC_RCAE"))
			.append(SEPARADOR)
			.append(getResourceBundleValue("LABEL_DT_RCAE"))
			.append(SEPARADOR)
			.append(getResourceBundleValue("LABEL_PROC_RCAE"))
			.append(SEPARADOR)
			.append(getResourceBundleValue("LABEL_QTD_RCAE"))
			.append(SEPARADOR)
			.append(getResourceBundleValue("LABEL_VL_RCAE"))
			.append(SEPARADOR)
			.append(QUEBRA_LINHA);
		return builder.toString();
	}	

	private String gerarLinhasDoRelatorio(List<ContaApresentadaPacienteProcedimentoVO> linhas) {
		StringBuilder procedimento = new StringBuilder();
		String descEspecialidade = null;
		int totEspAih = 0, totAihGeral = 0;
		BigDecimal totEsp = BigDecimal.ZERO, totEspGeral = BigDecimal.ZERO;
		
		for (ContaApresentadaPacienteProcedimentoVO especialidade : linhas) {

			totEsp = totEsp.add(especialidade.getTotal());
			
			if(descEspecialidade != null && !descEspecialidade.equals(especialidade.getEspecialidade())) {
				criarRodapeEspecialidade(procedimento, totEsp, totEspAih);
				totAihGeral += totEspAih;
				totEspGeral = totEspGeral.add(totEsp);
				totEsp = BigDecimal.ZERO;
				totEspAih = 0;
			}

			criarDadosCSV(procedimento, especialidade);
			procedimento.append(QUEBRA_LINHA);
			
			//Adicionar Procedimentos Secundários
			for(ContaApresentadaPacienteProcedimentoVO secundario : especialidade.getProcedimentos()) {
				criarDadosCSV(procedimento, secundario);
				procedimento.append(QUEBRA_LINHA);
			}

			if(especialidade.getNroAIh() != null) {
				totEspAih++;
			}
			descEspecialidade = especialidade.getEspecialidade();
		}
		
		//rodape geral
		totAihGeral += totEspAih;
		totEspGeral = totEspGeral.add(totEsp);
		
		criarRodapeEspecialidade(procedimento, totEsp, totEspAih);
		
		procedimento.append(QUEBRA_LINHA);
		
		criarRodapeGeral(procedimento, totEspGeral, totAihGeral);
		return procedimento.toString();
	}
			
	protected void criarRodapeGeral(StringBuilder procedimento, BigDecimal totEsp, int totEspAih) {
		procedimento
		.append(getResourceBundleValue("LABEL_TOTAL_AIH_RCAE")).append(SEPARADOR)
		.append(totEspAih).append(SEPARADOR).append(QUEBRA_LINHA)
		.append(getResourceBundleValue("LABEL_TOTAL_GERAL_RCAE")).append(SEPARADOR)
		.append(AghuNumberFormat.formatarNumeroMoeda(totEsp)).append(SEPARADOR).append(QUEBRA_LINHA);
	}
		
	protected void criarRodapeEspecialidade(StringBuilder procedimento, BigDecimal totEsp, int totEspAih) {
		procedimento
		.append(QUEBRA_LINHA)
		.append(getResourceBundleValue("LABEL_TOTAL_AIH_ESP_RCAE")).append(SEPARADOR)
		.append(totEspAih).append(SEPARADOR).append(QUEBRA_LINHA)
		.append(getResourceBundleValue("LABEL_TOTAL_ESP_RCAE")).append(SEPARADOR)
		.append(AghuNumberFormat.formatarNumeroMoeda(totEsp)).append(SEPARADOR).append(QUEBRA_LINHA);
	}
	
	protected void criarDadosCSV(StringBuilder procedimento, ContaApresentadaPacienteProcedimentoVO item) {
		verificarEspecialidade(procedimento, item);
		verificarCodigoSus(procedimento, item);
		verificarNroAih(procedimento, item);
		verificarProntuario(procedimento, item);
		verificarNome(procedimento, item);
		verificarDtInternacao(procedimento, item);
		verificarProcedimento(procedimento, item);
		verificarQuantidade(procedimento, item);
		verificarValor(procedimento, item);
	}
		
	private void verificarValor(StringBuilder procedimento,
			ContaApresentadaPacienteProcedimentoVO item) {
		procedimento.append(item.getTotal() == null ? "": AghuNumberFormat.formatarNumeroMoeda(item.getTotal()));
		procedimento.append(SEPARADOR);
	}
		
	private void verificarQuantidade(StringBuilder procedimento,
			ContaApresentadaPacienteProcedimentoVO item) {
		procedimento.append(item.getQuantidade() == null ? "" : item.getQuantidade());
					procedimento.append(SEPARADOR);
	}

	private void verificarProcedimento(StringBuilder procedimento,
			ContaApresentadaPacienteProcedimentoVO item) {
		procedimento.append(item.getProcedimento() == null? "" : item.getProcedimento());
					procedimento.append(SEPARADOR);
	}

	private void verificarDtInternacao(StringBuilder procedimento,
			ContaApresentadaPacienteProcedimentoVO item) {
		procedimento.append(formatarDataInternacao(item.getDataInternacao()));
					procedimento.append(SEPARADOR);
	}

	private void verificarNome(StringBuilder procedimento,
			ContaApresentadaPacienteProcedimentoVO item) {
		procedimento.append(item.getNome() == null? "": item.getNome());
					procedimento.append(SEPARADOR);
	}

	private void verificarProntuario(StringBuilder procedimento,
			ContaApresentadaPacienteProcedimentoVO item) {
		procedimento.append(item.getProntuario() == null? "" : "=\""+item.getProntuario()+"\"");
					procedimento.append(SEPARADOR);
	}

	private void verificarNroAih(StringBuilder procedimento,
			ContaApresentadaPacienteProcedimentoVO item) {
		procedimento.append(item.getNroAIh() == null? "" : "=\""+item.getNroAIh()+"\"");
					procedimento.append(SEPARADOR);
	}

	private void verificarCodigoSus(StringBuilder procedimento,
			ContaApresentadaPacienteProcedimentoVO item) {
		procedimento.append(item.getCodSus() == null? "": "=\""+item.getCodSus()+"\"");
					procedimento.append(SEPARADOR);
	}
	
	private void verificarEspecialidade(StringBuilder procedimento,
			ContaApresentadaPacienteProcedimentoVO item) {
		procedimento.append(item.getEspecialidade() == null? "": item.getEspecialidade());
					procedimento.append(SEPARADOR);
				}

	
	private String formatarDataInternacao(Date dataInternacao) {
		if (dataInternacao != null) {
			return DateUtil.dataToString(dataInternacao,  "dd/MM/yyyy");
			}
		return " ";
	}
}

