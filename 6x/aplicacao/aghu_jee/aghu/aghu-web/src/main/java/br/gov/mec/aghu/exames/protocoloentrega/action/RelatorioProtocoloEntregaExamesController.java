package br.gov.mec.aghu.exames.protocoloentrega.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateFormatUtil;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExamesPacientesVO;
import br.gov.mec.aghu.exames.vo.ProtocoloEntregaExamesVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AelProtocoloEntregaExames;

import com.itextpdf.text.DocumentException;

public class RelatorioProtocoloEntregaExamesController extends ActionReport {

	private static final long serialVersionUID = 1617393569614047754L;
	private static final Log LOG = LogFactory.getLog(RelatorioProtocoloEntregaExamesController.class);
	public static final String IMPRIMIR_ENTREGA_EXAMES = "exames-protocoloEntregaExames";
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;
		
	@PostConstruct
	public void inicio() {
		begin(conversation);
	}
	
	private AelProtocoloEntregaExames protocolo;
	private List<PesquisaExamesPacientesVO> listaPacientes;
	private Map<Integer, Vector<Short>> listaItens;
	
	private String voltarPara;
	private String voltarParaPesquisa;
	private Boolean isPesquisa;
	
	private List<ProtocoloEntregaExamesVO> colecao = new ArrayList<ProtocoloEntregaExamesVO>();
	
	public String imprimirRelatorio(){
		try {
			this.directPrint();
			apresentarMsgNegocio("MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} catch (SistemaImpressaoException e) {
			LOG.error(e.getMessage(),e);
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
		
		return null;
	}
	
	public StreamedContent getRenderPdf() throws IOException, ApplicationBusinessException, JRException, DocumentException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}

	@Override
	public Collection<ProtocoloEntregaExamesVO> recuperarColecao()	throws ApplicationBusinessException {
		colecao.clear();
		if (this.listaItens != null && !this.listaItens.isEmpty()) {
			ProtocoloEntregaExamesVO protocolo = cadastrosApoioExamesFacade.recuperarRelatorioEntregaExames(this.listaItens, this.protocolo, this.listaPacientes);
			colecao.add(protocolo);
			
		} else {
			ProtocoloEntregaExamesVO protocolo = cadastrosApoioExamesFacade.recuperarNovoRelatorioEntregaExames(this.protocolo, this.listaPacientes);
			colecao.add(protocolo);
		}
	
		return colecao;
	}

	@Override
	protected String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/exames/report/relatorioProtocoloEntregaExames.jasper";
	}
	
	@Override
	public Map<String, Object> recuperarParametros() {
		
		Map<String, Object> params = new HashMap<String, Object>();
		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		
		try {
			params.put("dataAtual", DateFormatUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
			params.put("footerCaminhoLogo", recuperarCaminhoLogo2());
			params.put("hospitalLocal", hospital);
			params.put("nomeRelatorio", "PROTOCOLO");
			params.put("tituloRelatorio", "Comprovante de Entrega de Exames");
			params.put("totalRegistros", colecao.size()-1);
		} catch (ApplicationBusinessException e) {
			LOG.error("Erro ao tentar recuparar logotipo para o relatório", e);
		}
		
		return params;
	}
	
	public String voltar() {
		String voltar;
		if (isPesquisa) {
			voltar = this.voltarParaPesquisa;
		} else {
			voltar = this.voltarPara;
		}
		return voltar; 
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}
	
	public String getVoltarParaPesquisa() {
		return voltarParaPesquisa;
	}

	public void setVoltarParaPesquisa(String voltarParaPesquisa) {
		this.voltarParaPesquisa = voltarParaPesquisa;
	}

	public AelProtocoloEntregaExames getProtocolo() {
		return protocolo;
	}

	public void setProtocolo(AelProtocoloEntregaExames protocolo) {
		this.protocolo = protocolo;
	}

	public List<PesquisaExamesPacientesVO> getListaPacientes() {
		return listaPacientes;
	}

	public void setListaPacientes(List<PesquisaExamesPacientesVO> listaPacientes) {
		this.listaPacientes = listaPacientes;
	}

	public Map<Integer, Vector<Short>> getListaItens() {
		return listaItens;
	}

	public void setListaItens(Map<Integer, Vector<Short>> listaItens) {
		this.listaItens = listaItens;
	}

	public Boolean getIsPesquisa() {
		return isPesquisa;
	}

	public void setIsPesquisa(Boolean isPesquisa) {
		this.isPesquisa = isPesquisa;
	}

}
