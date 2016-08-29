package br.gov.mec.aghu.paciente.prontuarioonline.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.exames.vo.RelatorioExamesPacienteDetalhesVO;
import br.gov.mec.aghu.exames.vo.RelatorioExamesPacienteExamesVO;
import br.gov.mec.aghu.exames.vo.RelatorioExamesPacienteVO;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;


public class RelatorioPlanoContSumExamesController extends ActionReport {

	private static final Log LOG = LogFactory.getLog(RelatorioPlanoContSumExamesController.class);

	private static final long serialVersionUID = -1201664920662254871L;

	private static final String TITULO_RELATORIO = "Sumário de Exames";

	@Inject
	private SecurityController securityController;
	
	private DominioNomeRelatorio relatorio;
	private String fileName;
	private Integer pacCodigo;
	private String titlePdfView;
	private Map<String, Object> parametrosEspecificos;
	private String nomeArquivoRelatorio;
	private List<RelatorioExamesPacienteVO> dadosRelatorio = new ArrayList<RelatorioExamesPacienteVO>();
	private Integer numeroProntuario;
	private Integer atdSeq;
	private Date dataHoraEvento;

	private RelatorioExamesPacienteVO vo;
	private Boolean permiteImprimirSumarioExames;
	
	//origem da chamada da pagina
	private String origem;
	
	//TODO a permissão não está implementada
	
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
		permiteImprimirSumarioExames = securityController.usuarioTemPermissao("permiteImprimirSumarioExames", "imprimir");
	}	
	
	public void inicio() throws ApplicationBusinessException {

		if (dadosRelatorio == null) {
			gerarDados();
		}

	}
	
	/**
	 * Recupera arquivo compilado do Jasper
	 */
	@Override
	public String recuperarArquivoRelatorio() {
		return "/br/gov/mec/aghu/exames/report/RelatorioExamesPaciente.jasper";
				
	}

	/**
	 * Recupera a coleção utilizada no relatório
	 */
	@Override
	public Collection<?> recuperarColecao() throws ApplicationBusinessException {

		return dadosRelatorio;
	}
	
	
	@Override
	public Map<String, Object> recuperarParametros() {
		
		gerarDados();
		
		Map<String, Object> params = new HashMap<String, Object>();
		String nomePaciente = "";
		List<RelatorioExamesPacienteExamesVO> exames = new ArrayList<RelatorioExamesPacienteExamesVO>();
		List<RelatorioExamesPacienteDetalhesVO> examesDetalhes = new ArrayList<RelatorioExamesPacienteDetalhesVO>();
		String leito = "";
		String identificacao = "";
		String prontuario = "";
		
		if (dadosRelatorio != null && !dadosRelatorio.isEmpty()) {
			RelatorioExamesPacienteVO vo = dadosRelatorio.get(0);
			exames = vo.getExames();
			examesDetalhes = vo.getExamesDetalhes();
			leito = vo.getLtoLtoId();
			identificacao = vo.getIdentificacao();
			prontuario = vo.getProntuario();
			nomePaciente = vo.getNome();
			if(prontuario == null) {
				prontuario = " " ;
			}
			else{
				prontuario = prontuario.concat(" - ");
			}
			
		}

		// informa todos os parametros necessarios
		params.put("tituloRelatorio", TITULO_RELATORIO);
		params.put("nomePaciente", nomePaciente);
		params.put("textoCabecalho","");
		try {
			params.put("caminhoLogo", recuperarCaminhoLogo());
		} catch (BaseException e) {
			LOG.error("Erro ao tentar recuparar logotipo para o relatório",e);
		}			
		params.put("voMaster", dadosRelatorio);
		params.put("voExames", exames);
		params.put("voExamesDesc", examesDetalhes);
		
		params.put("leito", leito);
		params.put("identificacao", identificacao);
		

		params.put("SUBREPORT_DIR","/br/gov/mec/aghu/exames/report/");
		params.put("PLANO_CONTG", true);

		if (getParametrosEspecificos() != null) {
			params.putAll(getParametrosEspecificos());
		}

		return params;
	}

	public DominioNomeRelatorio getRelatorio() {
		return relatorio;
	}

	public void setRelatorio(DominioNomeRelatorio relatorio) {
		this.relatorio = relatorio;
	}

	public void gerarDados() {
		
		dadosRelatorio = new ArrayList<RelatorioExamesPacienteVO>();
		
		if(vo != null){
			dadosRelatorio.add(vo);
			if(StringUtils.isNotBlank(vo.getProntuario())) {
				String prontuario = StringUtils.remove(vo.getProntuario(), "/");
				if(prontuario != null) {
					setNumeroProntuario(Integer.valueOf(prontuario));
				}
			}
		}
		
		setTitlePdfView(TITULO_RELATORIO);
	}
	
	public StreamedContent getRenderPdf() throws IOException,JRException, DocumentException, ApplicationBusinessException {
		DocumentoJasper documento = gerarDocumento();
		if (permiteImprimirSumarioExames) {
			return this.criarStreamedContentPdf(documento.getPdfByteArray(Boolean.FALSE));			
		} else {
			return this.criarStreamedContentPdf(documento.getPdfByteArray(Boolean.TRUE));
		}
	}

	
	public String voltar() {
		return getOrigem();
	}

	// GETTERS e SETTERS

	public Map<String, Object> getParametrosEspecificos() {
		return parametrosEspecificos;
	}
	
	public String getTitlePdfView() {
		return titlePdfView;
	}

	public void setTitlePdfView(String titlePdfView) {
		this.titlePdfView = titlePdfView;
	}
	
	public String getOrigem() {
		return origem;
	}


	public void setOrigem(String origem) {
		this.origem = origem;
	}
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getNomeArquivoRelatorio() {
		return nomeArquivoRelatorio;
	}

	public void setNomeArquivoRelatorio(String nomeArquivoRelatorio) {
		this.nomeArquivoRelatorio = nomeArquivoRelatorio;
	}
	
	public Integer getNumeroProntuario() {
		return numeroProntuario;
	}

	public void setNumeroProntuario(Integer numeroProntuario) {
		this.numeroProntuario = numeroProntuario;
	}
	
	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public Date getDataHoraEvento() {
		return dataHoraEvento;
	}

	public void setDataHoraEvento(Date dataHoraEvento) {
		this.dataHoraEvento = dataHoraEvento;
	}

	public RelatorioExamesPacienteVO getVo() {
		return vo;
	}

	public void setVo(RelatorioExamesPacienteVO vo) {
		this.vo = vo;
	}
}