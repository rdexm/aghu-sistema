package br.gov.mec.aghu.blococirurgico.action;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.DocumentException;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioNotasDeConsumoDaSalaVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
//import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.TipoDocumentoImpressao;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import net.sf.jasperreports.engine.JRException;


public class RelatorioNotasDeConsumoDaSalaController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {		
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}
	
	private static final long serialVersionUID = -4292035220406165224L;

	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	private static final Log LOG = LogFactory.getLog(RelatorioNotasDeConsumoDaSalaController.class);

	
	private AghUnidadesFuncionais unidadeCirurgica;
	private Date dataCirurgia;
	private Short numeroAgenda;
	private boolean nsDigitada;
	/*
	 * Parâmetros de conversação
	 */
	private String voltarPara; // Controla a navegação do botão voltar
	private boolean isDirectPrint;
	
	private String nomeMicrocomputador;
	
	private static final String RELATORIO_NOTA_CONSUMO_SALA_PDF = "relatorioNotasDeConsumoDaSalaPdf";
	private static final String RELATORIO_NOTA_CONSUMO_SALA = "relatorioNotasDeConsumoDaSala";
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String inicio() throws ApplicationBusinessException, JRException, SystemException, IOException {
		if (this.isDirectPrint) {
			try {
				this.nsDigitada = false;
				this.directPrint();
				return voltarPara;
				
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);

			} catch (Exception e) {
				LOG.error("Exceção capturada: ", e);
				apresentarMsgNegocio(Severity.ERROR, e.getLocalizedMessage());
			}
		}
		
		if (unidadeCirurgica == null) {
			try {
				try {
					nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
				} catch (UnknownHostException e) {
					LOG.error("Exceção capturada:", e);
				}
				
				unidadeCirurgica = blocoCirurgicoFacade.obterUnidadeFuncionalCirurgia(nomeMicrocomputador);
				
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
				LOG.error("Excecao capturada: ", e);
			}
		}
		return null;
	
	}
	
	/**
	 * Dados que serão impressos em PDF.
	 */
	private List<RelatorioNotasDeConsumoDaSalaVO> colecao = new ArrayList<RelatorioNotasDeConsumoDaSalaVO>();
	
	public List<AghUnidadesFuncionais> listarUnidadesFuncionaisPorUnidadeExecutora(final String strPesquisa) {
		return this.returnSGWithCount(this.aghuFacade.listarUnidadesFuncionaisPorUnidadeExecutora((String) strPesquisa, ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_CIRURGIAS, false),listarUnidadesFuncionaisPorUnidadeExecutoraCount(strPesquisa));
	}

	public Long listarUnidadesFuncionaisPorUnidadeExecutoraCount(final String strPesquisa) {
		return this.aghuFacade.listarUnidadesFuncionaisPorUnidadeExecutoraCount((String) strPesquisa, ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_CIRURGIAS, false);
	}
	
	@Override
	public String recuperarArquivoRelatorio() {
		if (this.nsDigitada) {
			return "br/gov/mec/aghu/blococirurgico/report/relatorioNotasDeConsumoDaSalaPreenchida.jasper";
		}
		return "br/gov/mec/aghu/blococirurgico/report/relatorioNotasDeConsumoDaSala.jasper";
		
	}
	
	/**
	 * Retorna os parametros utilizados no relatorio
	 */
	@Override
	public Map<String, Object> recuperarParametros() {	
		Map<String, Object> params = new HashMap<String, Object>();
		try {
			params.put("caminhoLogo", recuperarCaminhoLogo());
		} catch (ApplicationBusinessException e) {
			LOG.error("Erro ao tentar recuperar logotipo para o relatório",e);
		}
		params.put("unfDescricao", this.unidadeCirurgica.getDescricao());
		params.put("dataCirurgia", DateUtil.obterDataFormatada(this.dataCirurgia, "dd/MM/yyyy"));
		params.put("SUBREPORT_DIR","/br/gov/mec/aghu/blococirurgico/report/");
		params.put("dataAtual", DateUtil.obterDataFormatada(new Date(), "dd/MM/yyyy HH:mm"));
		
		return params;
	}
	
	/**
	 * Recupera a coleção utilizada no relatório
	 */
	@Override
	public Collection<RelatorioNotasDeConsumoDaSalaVO> recuperarColecao() throws ApplicationBusinessException {
		return this.colecao; 
	}
	
	public String visualizarImpressao(){
		try {
			this.colecao = blocoCirurgicoFacade.
				listarCirurgiasPorSeqUnidadeFuncionalDataNumeroAgenda(this.unidadeCirurgica.getSeq(), this.dataCirurgia, this.numeroAgenda, this.nsDigitada);
			if(colecao.isEmpty()) {
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_RELATORIO_NOTAS_CONSUMO_SALA_VAZIA");	
			} else {
				return RELATORIO_NOTA_CONSUMO_SALA_PDF;
			}
		} catch (Exception e) {
			e.getMessage();
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_RELATORIO_NOTAS_CONSUMO_SALA");	
		}
		return null;
	}
	
    public String voltarRelatorio(){    	
    	return RELATORIO_NOTA_CONSUMO_SALA;
    	
    }

	@Override
	public void directPrint() throws ApplicationBusinessException {
		try {
			this.colecao = blocoCirurgicoFacade.
				listarCirurgiasPorSeqUnidadeFuncionalDataNumeroAgenda(this.unidadeCirurgica.getSeq(), this.dataCirurgia, this.numeroAgenda, this.nsDigitada);

		} catch (Exception e) {
			e.getMessage();
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_RELATORIO_NOTAS_CONSUMO_SALA");
			return;
		}
		
		if(colecao.isEmpty()) {
			apresentarMsgNegocio(Severity.INFO,"MENSAGEM_RELATORIO_NOTAS_CONSUMO_SALA_VAZIA");
			return;
		}

		try {
			DocumentoJasper documento = gerarDocumento();

			this.sistemaImpressao.imprimir(documento.getJasperPrint(), unidadeCirurgica, TipoDocumentoImpressao.NOTA_CONSUMO);
	
			apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_IMPRESSAO_RELATORIO", "Nota de Consumo da Sala");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			apresentarMsgNegocio(Severity.ERROR,
					"ERRO_GERAR_RELATORIO");
		}
	}
	
	public StreamedContent getRenderPdf() throws IOException, JRException, DocumentException, ApplicationBusinessException{
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(Boolean.FALSE));			
	}
	
	public void limparPesquisa(){
		this.unidadeCirurgica = null;
		this.dataCirurgia = null;
		this.numeroAgenda = null;
		this.nsDigitada = false;
	}

	/**
	 * Método chamado para o botão voltar
	 */
	public String voltar() {
		return this.voltarPara;
	}
	
	//Getters and Setters
	public void setUnidadeCirurgica(AghUnidadesFuncionais unidadeCirurgica) {
		this.unidadeCirurgica = unidadeCirurgica;
	}

	public AghUnidadesFuncionais getUnidadeCirurgica() {
		return unidadeCirurgica;
	}

	public void setBlocoCirurgicoFacade(IBlocoCirurgicoFacade blocoCirurgicoFacade) {
		this.blocoCirurgicoFacade = blocoCirurgicoFacade;
	}

	public IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}

	public void setDataCirurgia(Date dataCirurgia) {
		this.dataCirurgia = dataCirurgia;
	}

	public Date getDataCirurgia() {
		return dataCirurgia;
	}

	public Short getNumeroAgenda() {
		return numeroAgenda;
	}

	public void setNumeroAgenda(Short numeroAgenda) {
		this.numeroAgenda = numeroAgenda;
	}

	public Boolean getNsDigitada() {
		return nsDigitada;
	}

	public void setNsDigitada(Boolean nsDigitada) {
		this.nsDigitada = nsDigitada;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}
	
	public Boolean getIsDirectPrint() {
		return isDirectPrint;
	}

	public void setIsDirectPrint(Boolean isDirectPrint) {
		this.isDirectPrint = isDirectPrint;
	}

	public SistemaImpressao getSistemaImpressao() {
		return sistemaImpressao;
	}

	public void setSistemaImpressao(SistemaImpressao sistemaImpressao) {
		this.sistemaImpressao = sistemaImpressao;
	}

	public List<RelatorioNotasDeConsumoDaSalaVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<RelatorioNotasDeConsumoDaSalaVO> colecao) {
		this.colecao = colecao;
	}
}
