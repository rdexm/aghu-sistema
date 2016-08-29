package br.gov.mec.aghu.paciente.prontuarioonline.action;

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
import br.gov.mec.aghu.paciente.vo.RelatorioParadaInternacaoControleVO;
import br.gov.mec.aghu.paciente.vo.RelatorioParadaInternacaoVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;


public class RelatorioParadaInternacaoFolhaPaisagemController extends ActionReport {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(RelatorioParadaInternacaoFolhaPaisagemController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 122962820336405034L;

	private static final String TITULO_RELATORIO = "Sumário da Internação Atual";
	
	private Integer atdSeq;
	
	private Date dthrCriacao;	
	
	private String titlePdfView;	
	
	private List<RelatorioParadaInternacaoControleVO> dadosRelatorio;
	
	private RelatorioParadaInternacaoVO relatorioParadaInternacaoVO; 
	
	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/paciente/prontuarioonline/report/paradaInternacao/SumarioParadaInternacaoAtualControlesPaisagem.jasper";
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
		
		// informa todos os parametros necessarios
		params.put("nomePaciente", getRelatorioParadaInternacaoVO().getNomePaciente()); 
		params.put("prontuario", getRelatorioParadaInternacaoVO().getProntuario());
		params.put("agenda", getRelatorioParadaInternacaoVO().getAgenda());
		try {
			params.put("caminhoLogo", recuperarCaminhoLogo());
		} catch (BaseException e) {
			LOG.error("Erro ao tentar recuparar logotipo para o relatório",e);
		}			
		params.put("SUBREPORT_DIR", "br/gov/mec/aghu/paciente/prontuarioonline/report/paradaInternacao/");
		params.put("imgBackground", recuperarCaminhoImgBackground());
		params.put("dataAtual", new Date());
		return params;
	}		
	
	private String recuperarCaminhoImgBackground() {
		ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		String path = servletContext.getRealPath("/resources/img/back-relatorios-temporarios.gif");
		return path;
	}
	
	public void prepararRelatorio(List<RelatorioParadaInternacaoVO> listParadaInternacao) {
		setRelatorioParadaInternacaoVO(listParadaInternacao.get(0));
		this.setDadosRelatorio(getRelatorioParadaInternacaoVO().getControles());
		
		//Mapa para organização dos itens
		Map<Date, StringBuilder> mapVos = new HashMap<Date, StringBuilder>();		
		
		//Agrupa as anotações
		for(RelatorioParadaInternacaoControleVO ctrl : getDadosRelatorio()){
			agruparAnotacoes(mapVos, ctrl);
		}		
	
		//Junta as anotações em todos os registros do mesmo dia, para aparecer no sumário final.
		for(RelatorioParadaInternacaoControleVO voAux : getDadosRelatorio()){
			voAux.setAnotacoes(mapVos.get(voAux.getDataHora()).toString());
		}
		
		setTitlePdfView(TITULO_RELATORIO);		
	}
	
	private void agruparAnotacoes(Map<Date, StringBuilder> mapVos,
			RelatorioParadaInternacaoControleVO vo) {
		if(!mapVos.containsKey(vo.getDataHora())){
			mapVos.put(vo.getDataHora(), new StringBuilder());		
		}
		if(vo.getAnotacoes() != null){
			mapVos.get(vo.getDataHora()).append(' ').append(vo.getAnotacoes());
		}
	}	

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public Date getDthrCriacao() {
		return dthrCriacao;
	}

	public void setDthrCriacao(Date dthrCriacao) {
		this.dthrCriacao = dthrCriacao;
	}

	private List<RelatorioParadaInternacaoControleVO> getDadosRelatorio() {
		return dadosRelatorio;
	}

	private void setDadosRelatorio(List<RelatorioParadaInternacaoControleVO> dadosRelatorio) {
		this.dadosRelatorio = dadosRelatorio;
	}

	public String getTitlePdfView() {
		return titlePdfView;
	}

	public void setTitlePdfView(String titlePdfView) {
		this.titlePdfView = titlePdfView;
	}

	public void setRelatorioParadaInternacaoVO(
			RelatorioParadaInternacaoVO relatorioParadaInternacaoVO) {
		this.relatorioParadaInternacaoVO = relatorioParadaInternacaoVO;
	}

	public RelatorioParadaInternacaoVO getRelatorioParadaInternacaoVO() {
		return relatorioParadaInternacaoVO;
	}
}
