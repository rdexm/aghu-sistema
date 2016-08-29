package br.gov.mec.aghu.estoque.cadastrosapoio.action;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.model.ScoHistoricoAdvertForn;
import br.gov.mec.aghu.model.ScoHistoricoMultaForn;
import br.gov.mec.aghu.model.ScoHistoricoOcorrForn;
import br.gov.mec.aghu.model.ScoHistoricoSuspensForn;
import br.gov.mec.aghu.model.VScoFornecedor;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.AghuNumberFormat;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;

import com.itextpdf.text.DocumentException;


public class RelatorioPendenciasFornecedorController extends ActionReport {

	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	@PostConstruct
	protected void inicializar(){
	 LOG.debug("Iniciando conversation");
	 this.begin(conversation);
	}
	
	@EJB
	private IParametroFacade parametroFacade;
	

	private static final Log LOG = LogFactory.getLog(RelatorioPendenciasFornecedorController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -6747165299593219462L;

	private static final String SEPARADOR = ";";
	private static final String ISO_8859_1 = "ISO-8859-1";
	private static final String EXTENSAO = ".csv";

	private List<ScoHistoricoAdvertForn> listaAdvertencias;
	private List<ScoHistoricoMultaForn> listaMultas;
	private List<ScoHistoricoSuspensForn> listaSuspensoes;
	private List<ScoHistoricoOcorrForn> listaOcorrencias;

	private Date inicioPeriodo;
	private Date fimPeriodo;
	private VScoFornecedor fornecedor;
	
	private String logo;
	
	@Override
	public Collection<?> recuperarColecao() throws ApplicationBusinessException {
		List<Integer> lista = new ArrayList<Integer>();
		lista.add(1);
		return lista;
	}
	
	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/compras/report/relatorioPendenciasFornecedor.jasper";
	}

	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		String subReportAdvertencias = "br/gov/mec/aghu/compras/report/relatorioPendenciasAdvertencias.jasper";
		String subReportMultas = "br/gov/mec/aghu/compras/report/relatorioPendenciasMultas.jasper";
		String subReportSuspensoes = "br/gov/mec/aghu/compras/report/relatorioPendenciasSuspensoes.jasper";
		String subReportOcorrencias = "br/gov/mec/aghu/compras/report/relatorioPendenciasOcorrencias.jasper";
		Date dataAtual = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		params.put("dataAtual", sdf.format(dataAtual));
		params.put("caminhoLogo", logo);
		params.put("fornecedor", CoreUtil.formatarCNPJ(fornecedor.getCgcCpf()) + " - " + fornecedor.getRazaoSocial());
		params.put("periodo", obterPeriodo());
		params.put("rodape", obterRodape());
		params.put("listaAdvertencias", listaAdvertencias);
		params.put("listaMultas", listaMultas);
		params.put("listaSuspensoes", listaSuspensoes);
		params.put("listaOcorrencias", listaOcorrencias);
		params.put("subRelatorioAdvertencias", Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(subReportAdvertencias));
		params.put("subRelatorioMultas", Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(subReportMultas));
		params.put("subRelatorioSuspensoes", Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(subReportSuspensoes));
		params.put("subRelatorioOcorrencias", Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(subReportOcorrencias));
		return params;
	}

	private String obterRodape() {
		StringBuffer rodape = new StringBuffer(250);
		try {
			rodape.append("Fone ");
			rodape.append(this.parametroFacade.buscarValorTexto(AghuParametrosEnum.P_HOSPITAL_END_FONE));
			rodape.append(" | Fax ");
			rodape.append(this.parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_HOSPITAL_FAX));
			rodape.append(" | ");
			rodape.append(this.parametroFacade.buscarValorTexto(AghuParametrosEnum.P_HOSPITAL_END_LOGRADOURO));
			rodape.append(" - ");
			rodape.append(this.parametroFacade.buscarValorTexto(AghuParametrosEnum.P_HOSPITAL_END_CIDADE));
			rodape.append('/');
			rodape.append(this.parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_HOSPITAL_UF_SIGLA));
			rodape.append(" - ");
			rodape.append(this.parametroFacade.buscarValorTexto(AghuParametrosEnum.P_HOSPITAL_END_CEP));
			rodape.append(" | ");
			rodape.append(this.parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_HOSPITAL_SITE));
		} catch(BaseException e) {
			return null;
		}
		return rodape.toString();
	}
	
	public void downloadCsv() throws IOException {
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpServletResponse response = (HttpServletResponse) fc.getExternalContext().getResponse();
		
		response.setContentType("text/csv");
		response.setHeader("Content-Disposition","attachment;filename=" + "HISTORICO_PENALIDADES_FORNECEDOR_"+(fornecedor != null?fornecedor.getNumeroFornecedor():"")+EXTENSAO);
		response.getCharacterEncoding();
		OutputStream out = response.getOutputStream();

		// GERA CABEÇALHOS DO CSV
		out.write(gerarCabecalhoCSV().getBytes(ISO_8859_1));

		// ESCREVE LINHAS DO CSV
		gerarLinhaCsvAdvertencia(out);
		gerarLinhaCsvMulta(out);
		gerarLinhaCsvOcorrencia(out);
		gerarLinhaCsvSuspensao(out);
		
		out.flush();
		out.close();
		fc.responseComplete();
	}
	
	private void gerarLinhaCsvAdvertencia(OutputStream out) throws IOException {
		if(listaAdvertencias != null && !listaAdvertencias.isEmpty()) {
			for(ScoHistoricoAdvertForn advertencia : listaAdvertencias) {
				out.write(LINE_SEPARATOR.getBytes(ISO_8859_1));
				out.write(("Advertência"+SEPARADOR+advertencia.getFornecedor().getRazaoSocial().replace("\n"," ").replace(";"," ")+
						SEPARADOR+advertencia.getId().getNumero()+
						SEPARADOR+DateUtil.obterDataFormatada(advertencia.getDtEmissao(), DateConstants.DATE_PATTERN_DDMMYYYY)+SEPARADOR+SEPARADOR+
						advertencia.getTexto().replace("\n"," ").replace(";"," ")+SEPARADOR+SEPARADOR+SEPARADOR).getBytes(ISO_8859_1));
			}
		}
	}

	private void gerarLinhaCsvMulta(OutputStream out) throws IOException {
		if(listaMultas != null && !listaMultas.isEmpty()) {
			for(ScoHistoricoMultaForn multa : listaMultas) {
				out.write(LINE_SEPARATOR.getBytes(ISO_8859_1));
				out.write(("Multa"+SEPARADOR+multa.getFornecedor().getRazaoSocial().replace("\n"," ").replace(";"," ")+SEPARADOR+multa.getId().getNumero()+
						SEPARADOR+DateUtil.obterDataFormatada(multa.getDtEmissao(), DateConstants.DATE_PATTERN_DDMMYYYY)+SEPARADOR+SEPARADOR+
						multa.getTexto().replace("\n"," ").replace(";"," ")+SEPARADOR+AghuNumberFormat.formatarNumeroMoeda(multa.getValor())+SEPARADOR+SEPARADOR).getBytes(ISO_8859_1));
			}
		}
	}

	private void gerarLinhaCsvOcorrencia(OutputStream out) throws IOException {
		if(listaOcorrencias != null && !listaOcorrencias.isEmpty()) {
			for(ScoHistoricoOcorrForn ocorrenica : listaOcorrencias) {
				out.write(LINE_SEPARATOR.getBytes(ISO_8859_1));
				out.write(("Ocorrência"+SEPARADOR+ocorrenica.getScoFornecedor().getRazaoSocial().replace("\n"," ").replace(";"," ")+SEPARADOR+ocorrenica.getId().getNumero()+
						SEPARADOR+DateUtil.obterDataFormatada(ocorrenica.getDtEmissao(), DateConstants.DATE_PATTERN_DDMMYYYY)+SEPARADOR+SEPARADOR+
						ocorrenica.getTexto().replace("\n"," ").replace(";"," ")+SEPARADOR+SEPARADOR+ocorrenica.getScoTipoOcorrForn().getDescricao().replace("\n"," ").replace(";"," ")+SEPARADOR).getBytes(ISO_8859_1));
			}
		}
	}
	
	private void gerarLinhaCsvSuspensao(OutputStream out) throws IOException {
		if(listaSuspensoes != null && !listaSuspensoes.isEmpty()) {
			for(ScoHistoricoSuspensForn suspensoes : listaSuspensoes) {
				out.write(LINE_SEPARATOR.getBytes(ISO_8859_1));
				out.write(("Suspensão"+SEPARADOR+suspensoes.getScoFornecedor().getRazaoSocial().replace("\n"," ").replace(";"," ")+
						SEPARADOR+suspensoes.getId().getNroProcesso()+
						SEPARADOR+DateUtil.obterDataFormatada(suspensoes.getDtInicio(), DateConstants.DATE_PATTERN_DDMMYYYY)+SEPARADOR+(suspensoes.getDtFim() != null ? DateUtil.obterDataFormatada(suspensoes.getDtFim(), DateConstants.DATE_PATTERN_DDMMYYYY) : "")+SEPARADOR+
						suspensoes.getJustificativa().replace("\n"," ").replace(";"," ")+SEPARADOR+SEPARADOR+SEPARADOR).getBytes(ISO_8859_1));
			}
		}
	}
	
	private String gerarCabecalhoCSV() {
		return "Tipo de Penalidade" + SEPARADOR + "Fornecedor" + SEPARADOR + "Número"
				+ SEPARADOR + "Data" + SEPARADOR + "Data Final"
				+ SEPARADOR + "Descrição" + SEPARADOR + "Valor" + SEPARADOR + "Tipo";
	}
	
	private String obterPeriodo() {
		if(fimPeriodo != null) {
			 return DateUtil.obterDataFormatada(inicioPeriodo, DateConstants.DATE_PATTERN_DDMMYYYY) + " Até " + DateUtil.obterDataFormatada(fimPeriodo, DateConstants.DATE_PATTERN_DDMMYYYY);
		}
		else if(inicioPeriodo != null){
			return  DateUtil.obterDataFormatada(inicioPeriodo, DateConstants.DATE_PATTERN_DDMMYYYY) + " Até -";
		}
		return null;
	}
	
	public void downloadPdf() throws IOException,
	BaseException, JRException, SystemException, DocumentException {
		DocumentoJasper documento = gerarDocumento();

		final FacesContext fc = FacesContext.getCurrentInstance();
		final HttpServletResponse response = (HttpServletResponse) fc.getExternalContext().getResponse();
        response.reset();
		response.setContentType("text/pdf");
		response.setHeader("Content-Disposition","attachment;filename=HISTORICO_PENALIDADES_FORNECEDOR_" + fornecedor.getNumeroFornecedor()  + ".pdf");
      	response.getCharacterEncoding();
        response.flushBuffer();
		
        ServletOutputStream out = response.getOutputStream();
		out.write(documento.getPdfByteArray(false));
		out.flush();
		out.close();
		
		fc.responseComplete();
	}
	
	public void carregarLogo() throws BaseException {
		logo = this.parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_RELATORIOS_CABECALHO);
	}
	
	public List<ScoHistoricoAdvertForn> getListaAdvertencias() {
		return listaAdvertencias;
	}

	public void setListaAdvertencias(List<ScoHistoricoAdvertForn> listaAdvertencias) {
		this.listaAdvertencias = listaAdvertencias;
	}

	public List<ScoHistoricoMultaForn> getListaMultas() {
		return listaMultas;
	}

	public void setListaMultas(List<ScoHistoricoMultaForn> listaMultas) {
		this.listaMultas = listaMultas;
	}

	public List<ScoHistoricoSuspensForn> getListaSuspensoes() {
		return listaSuspensoes;
	}

	public void setListaSuspensoes(List<ScoHistoricoSuspensForn> listaSuspensoes) {
		this.listaSuspensoes = listaSuspensoes;
	}

	public List<ScoHistoricoOcorrForn> getListaOcorrencias() {
		return listaOcorrencias;
	}

	public void setListaOcorrencias(List<ScoHistoricoOcorrForn> listaOcorrencias) {
		this.listaOcorrencias = listaOcorrencias;
	}

	public Date getInicioPeriodo() {
		return inicioPeriodo;
	}

	public void setInicioPeriodo(Date inicioPeriodo) {
		this.inicioPeriodo = inicioPeriodo;
	}

	public Date getFimPeriodo() {
		return fimPeriodo;
	}

	public void setFimPeriodo(Date fimPeriodo) {
		this.fimPeriodo = fimPeriodo;
	}

	public VScoFornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(VScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}
}
