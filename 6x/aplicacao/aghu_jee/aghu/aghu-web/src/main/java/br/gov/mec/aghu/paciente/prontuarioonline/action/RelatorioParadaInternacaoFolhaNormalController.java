package br.gov.mec.aghu.paciente.prontuarioonline.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.paciente.vo.RelatorioParadaInternacaoDetalhesExamesVO;
import br.gov.mec.aghu.paciente.vo.RelatorioParadaInternacaoExamesVO;
import br.gov.mec.aghu.paciente.vo.RelatorioParadaInternacaoVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;


public class RelatorioParadaInternacaoFolhaNormalController extends ActionReport {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(RelatorioParadaInternacaoFolhaNormalController.class);

	private static final long serialVersionUID = 2045890373730725902L;
	
	
	private String titlePdfView;
	
	private List<RelatorioParadaInternacaoVO> dadosRelatorio;
	
	
	
	public void prepararRelatorio(List<RelatorioParadaInternacaoVO> relatorio, String title) {
		this.setDadosRelatorio(relatorio);
		this.setTitlePdfView(title);
	}
	
	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/paciente/prontuarioonline/report/paradaInternacao/SumarioParadaInternacaoAtual.jasper";
	}
	
	@Override
	public Collection<?> recuperarColecao() throws ApplicationBusinessException {
		return getDadosRelatorio();
	}
	
	/**
	 * Retorna os parametros utilizados no relatorio
	 */
	@Override
	public Map<String, Object> recuperarParametros() {
		
		Map<String, Object> params = new HashMap<String, Object>();
		
		String nomePaciente = "";
		String prontuario = "";
		String agenda = "";
		
		List<RelatorioParadaInternacaoExamesVO> exames = new ArrayList<RelatorioParadaInternacaoExamesVO>();
		List<RelatorioParadaInternacaoDetalhesExamesVO> detalhesExames = new ArrayList<RelatorioParadaInternacaoDetalhesExamesVO>();		
		
		if (getDadosRelatorio() != null && !getDadosRelatorio().isEmpty()) {
			RelatorioParadaInternacaoVO vo = getDadosRelatorio().get(0);
			nomePaciente = vo.getNomePaciente();
			prontuario = vo.getProntuario();
			agenda = vo.getAgenda();
			exames = vo.getExames();
			detalhesExames = vo.getDetalhesExames();
		}
		
		// informa todos os parametros necessarios
		params.put("tituloRelatorio", getTitlePdfView());
		params.put("textoCabecalho", "Este documento deve ser impresso somente em caso de parada cardiorespiratória. Após a sua utilização deve ser entregue à secretária da unidade. Impressão com qualquer outra finalidade caracterizará cópia de prontuário do paciente que é expressamente proibida pelo Conselho Federal de Medicina. Todas as impressões deste documento serão auditadas.");
		params.put("nomePaciente", nomePaciente); 
		params.put("prontuario", prontuario);
		params.put("agenda", agenda);
		try {
			params.put("caminhoLogo", recuperarCaminhoLogo());
		} catch (BaseException e) {
			LOG.error("Erro ao tentar recuparar logotipo para o relatório",e);
		}
		params.put("voMaster", getDadosRelatorio());
		params.put("voExames", exames);
		params.put("voDetalhesExames", detalhesExames);
		params.put("SUBREPORT_DIR", "br/gov/mec/aghu/paciente/prontuarioonline/report/paradaInternacao/");
		params.put("imgBackground", recuperarCaminhoImgBackground());
		params.put("dataAtual", new Date());
		return params;
	}	
	
	private String recuperarCaminhoImgBackground() {
		ServletContext servletContext = (ServletContext)  FacesContext.getCurrentInstance().getExternalContext().getContext();
		String path = servletContext.getRealPath("resources/img/back-relatorios-temporarios.gif");
		return path;
	}
	
	private void setTitlePdfView(String t) {
		this.titlePdfView = t;
	}
	
	private String getTitlePdfView() {
		return titlePdfView;
	}
	
	private List<RelatorioParadaInternacaoVO> getDadosRelatorio() {
		return dadosRelatorio;
	}

	private void setDadosRelatorio(List<RelatorioParadaInternacaoVO> dadosRelatorio) {
		this.dadosRelatorio = dadosRelatorio;
	}
}
