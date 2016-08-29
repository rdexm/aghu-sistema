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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.DocumentException;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.vo.ProtocoloEntregaNotasDeConsumoVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
//import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioOrdenacaoProtocoloEntregaNotasConsumo;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import net.sf.jasperreports.engine.JRException;



public class ProtocoloEntregaNotasDeConsumoController extends  ActionReport {

	private static final String MENSAGEM_RELATORIO_PROTOCOLO_VAZIO = "MENSAGEM_RELATORIO_PROTOCOLO_VAZIO";

	private StreamedContent media;

	public StreamedContent getMedia() {				
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}
	
	/**
	 * 
	 */		
	private static final long serialVersionUID = -7322106307548311780L;
	
	private static final Log LOG = LogFactory.getLog(ProtocoloEntregaNotasDeConsumoController.class);
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private SistemaImpressao sistemaImpressao;
	
	private DominioSimNao[] itensPacienteSus = new DominioSimNao[] {DominioSimNao.S, DominioSimNao.N};
	private DominioOrdenacaoProtocoloEntregaNotasConsumo[] itensOrdenacao = new DominioOrdenacaoProtocoloEntregaNotasConsumo[] {
			DominioOrdenacaoProtocoloEntregaNotasConsumo.AGENDA, DominioOrdenacaoProtocoloEntregaNotasConsumo.NOME};
	
	/*
	 * CAMPOS DE FILTRO
	 */
	private AghUnidadesFuncionais unidadeCirurgica;
	private Date dataCirurgia;
	private DominioSimNao pacienteSus;
	private DominioOrdenacaoProtocoloEntregaNotasConsumo ordenacao;
	
    private String nomeMicrocomputador;

	private static final String RELATORIO_PROTOCOLO_ENTRE_NOTAS_CONSUMO_PDF = "protocoloEntregaNotasDeConsumoPdf";
	private static final String RELATORIO_PROTOCOLO_ENTRE_NOTAS_CONSUMO = "protocoloEntregaNotasDeConsumo";
	
    @PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	/**
	 * Método executado ao abrir a tela
	 */
	public void iniciar() {
		if (unidadeCirurgica == null) {
			try {
				
				try {
					nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
				} catch (UnknownHostException e) {
					this.LOG.error("Exceção capturada:", e);
				}
				
				unidadeCirurgica = blocoCirurgicoFacade.obterUnidadeFuncionalCirurgia(nomeMicrocomputador);
				
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
				this.LOG.error(e);
			}
		}	
	}		
	
	/*
	 * MÉTODOS DA SUGGESTION DE UNIDADES CIRÚRGICAS
	 */
	public List<AghUnidadesFuncionais> listarUnidadesFuncionaisPorUnidadeExecutora(final String strPesquisa) {
		return this.returnSGWithCount(this.aghuFacade.listarUnidadesFuncionaisPorUnidadeExecutora((String) strPesquisa, ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_CIRURGIAS,  false),listarUnidadesFuncionaisPorUnidadeExecutoraCount(strPesquisa));
	}

	public Long listarUnidadesFuncionaisPorUnidadeExecutoraCount(final String strPesquisa) {
		return this.aghuFacade.listarUnidadesFuncionaisPorUnidadeExecutoraCount((String) strPesquisa, ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_CIRURGIAS, false);
	}
	
	/**
	 * Dados que serão impressos em PDF.
	 */
	private List<ProtocoloEntregaNotasDeConsumoVO> colecao = new ArrayList<ProtocoloEntregaNotasDeConsumoVO>();
		
	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/blococirurgico/report/protocoloEntregaNotasDeConsumo.jasper";
	}
	
	/**
	 * Retorna os parametros utilizados no relatorio
	 */
	@Override
	public Map<String, Object> recuperarParametros() {
		Map<String, Object> params = new HashMap<String, Object>();
		
		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		params.put("hospitalLocal", hospital);
		params.put("dataAtual", DateUtil.obterDataFormatada(new Date(), "dd/MM/yyyy HH:mm"));
		params.put("dataInformada", DateUtil.obterDataFormatada(this.dataCirurgia, "dd/MM/yyyy"));
		params.put("unidadeFuncional", this.unidadeCirurgica.getDescricao());
		
		String convenio = pacienteSus != null ? pacienteSus.toString() : null;
		if (StringUtils.equals(convenio, DominioSimNao.S.toString())) {
			convenio = " - SUS";
		} else if (StringUtils.equals(convenio, DominioSimNao.N.toString())) {
			convenio = " – Convênio/Particular";
		}
		params.put("convenio", convenio);
		return params;
	}
	
	public String visualizarImpressao(){
		try {
			this.colecao = blocoCirurgicoFacade.listarRelatorioProtocoloEntregaNotasDeConsumo(unidadeCirurgica.getSeq(), dataCirurgia, pacienteSus, ordenacao);

		} catch (Exception e) {
			e.getMessage();
			apresentarMsgNegocio(Severity.INFO,MENSAGEM_RELATORIO_PROTOCOLO_VAZIO);
			return null;
		}
		if(colecao.isEmpty()) {
			apresentarMsgNegocio(Severity.INFO,MENSAGEM_RELATORIO_PROTOCOLO_VAZIO);
			return null;
		}
		return RELATORIO_PROTOCOLO_ENTRE_NOTAS_CONSUMO_PDF;
	}
	
	@Override
	public void directPrint() {
		try {
			this.colecao = blocoCirurgicoFacade.listarRelatorioProtocoloEntregaNotasDeConsumo(unidadeCirurgica.getSeq(), dataCirurgia, pacienteSus, ordenacao);
				
		} catch (Exception e) {
			e.getMessage();
			apresentarMsgNegocio(Severity.INFO,MENSAGEM_RELATORIO_PROTOCOLO_VAZIO);
			return;
		}
		
		if(colecao.isEmpty()) {
			apresentarMsgNegocio(Severity.INFO,MENSAGEM_RELATORIO_PROTOCOLO_VAZIO);
			return;
		}

		try {
			DocumentoJasper documento = gerarDocumento();

			this.sistemaImpressao.imprimir(documento.getJasperPrint(),
					super.getEnderecoIPv4HostRemoto());

			apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_SUCESSO_IMPRESSAO_RELATORIO", "Protocolo de Entrega das Notas de Consumo");
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
	
	/**
	 * Recupera a coleção utilizada no relatório
	 */
	@Override
	public Collection<ProtocoloEntregaNotasDeConsumoVO> recuperarColecao() throws ApplicationBusinessException {
		return this.colecao; 
	}
	
	public void limparPesquisa(){
		this.unidadeCirurgica = null;
		this.dataCirurgia = null;
		this.pacienteSus = null;
		this.ordenacao = null;
	}
	
	public String voltar() {
		return RELATORIO_PROTOCOLO_ENTRE_NOTAS_CONSUMO;
	}
	
	/*
	 * GETTERS AND SETTERS
	 */
	public AghUnidadesFuncionais getUnidadeCirurgica() {
		return unidadeCirurgica;
	}

	public void setUnidadeCirurgica(AghUnidadesFuncionais unidadeCirurgica) {
		this.unidadeCirurgica = unidadeCirurgica;
	}

	public Date getDataCirurgia() {
		return dataCirurgia;
	}

	public void setDataCirurgia(Date dataCirurgia) {
		this.dataCirurgia = dataCirurgia;
	}

	public DominioSimNao[] getItensPacienteSus() {
		return itensPacienteSus;
	}

	public void setItensPacienteSus(DominioSimNao[] itensPacienteSus) {
		this.itensPacienteSus = itensPacienteSus;
	}

	public DominioOrdenacaoProtocoloEntregaNotasConsumo[] getItensOrdenacao() {
		return itensOrdenacao;
	}

	public void setItensOrdenacao(
			DominioOrdenacaoProtocoloEntregaNotasConsumo[] itensOrdenacao) {
		this.itensOrdenacao = itensOrdenacao;
	}

	public DominioSimNao getPacienteSus() {
		return pacienteSus;
	}

	public void setPacienteSus(DominioSimNao pacienteSus) {
		this.pacienteSus = pacienteSus;
	}

	public DominioOrdenacaoProtocoloEntregaNotasConsumo getOrdenacao() {
		return ordenacao;
	}

	public void setOrdenacao(DominioOrdenacaoProtocoloEntregaNotasConsumo ordenacao) {
		this.ordenacao = ordenacao;
	}

	public List<ProtocoloEntregaNotasDeConsumoVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<ProtocoloEntregaNotasDeConsumoVO> colecao) {
		this.colecao = colecao;
	}

}
