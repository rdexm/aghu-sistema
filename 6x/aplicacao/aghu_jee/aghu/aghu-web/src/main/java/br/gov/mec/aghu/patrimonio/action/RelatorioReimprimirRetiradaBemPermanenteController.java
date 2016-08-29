package br.gov.mec.aghu.patrimonio.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.DocumentException;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.patrimonio.business.IPatrimonioFacade;
import br.gov.mec.aghu.patrimonio.vo.AceiteTecnicoParaSerRealizadoVO;
import br.gov.mec.aghu.patrimonio.vo.DetalhamentoRetiradaBemPermanenteVO;
import br.gov.mec.aghu.patrimonio.vo.ReImprimirAceiteTecnicoParaSerRealizadoVO;
import br.gov.mec.aghu.patrimonio.vo.TecnicoItemRecebimentoVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import net.sf.jasperreports.engine.JRException;

public class RelatorioReimprimirRetiradaBemPermanenteController extends ActionReport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7506150627300243248L;
	
	private static final Log LOG = LogFactory.getLog(RelatorioReimprimirRetiradaBemPermanenteController.class);
	
	private final String ERRO_GERAR_RELATORIO = "ERRO_GERAR_RELATORIO";
	
	@EJB
	private IPatrimonioFacade patrimonioFacade;
	
	@EJB 
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private ReImprimirProtocoloRetiradaBemPermanenteController reImprimirProtocoloRetiradaBemPermanenteController;
	
	private ReImprimirAceiteTecnicoParaSerRealizadoVO aceiteVOPrinc;
	private List<AceiteTecnicoParaSerRealizadoVO> itemRetiradaList;
	private List<DetalhamentoRetiradaBemPermanenteVO> itensDetalhamentoListCompleta;
	private RapServidores servidor;
	private int posicao;
	private StreamedContent media;
	private String cameFrom;
	private String nomeTecnicoResp;
	private String tecnicoResponsavel;
	private String nomeArea;
	private String nomeCentroCusto;
	private String centroCusto;
	private TecnicoItemRecebimentoVO tecnicoItemRecebimento;
	
	private static final String PROTOCOLO_REIMPRIMIR_PROTOCOLO_RETIRADA_BEM_PERMANENTE_PDF = "patrimonio-reImprimirProtocoloRetiradaBemPermanentePDF";
	
	private static final String PROTOCOLO_REIMPRIMIR_PROTOCOLO_RETIRADA_BEM_PERMANENTE = "patrimonio-reImprimirProtocoloRetiradaBemPermanente";

	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		try {
			AghParametros parametro = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_CENTRO_CUSTO_PATRIMONIO);
			servidor = new RapServidores(); 
			servidor.setId(new RapServidoresId());
			Object[] objServidor = patrimonioFacade.obterNomeMatriculaServidorCentroCusto(parametro.getVlrNumerico().intValue());
			if(objServidor != null && objServidor.length > 0){
				servidor.getId().setMatricula((Integer) objServidor[0]);
				servidor.setPessoaFisica(new RapPessoasFisicas());
				servidor.getPessoaFisica().setNome((String)objServidor[1]);
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void imprimir() {
		posicao = posicao + 0;
		try {
			itemRetiradaList = patrimonioFacade.obterSubItensRelatorioProtocoloRetBensPermanentes(itemRetiradaList, itensDetalhamentoListCompleta, false);
			for (int i = 0; i < itemRetiradaList.size(); i++) {
				posicao = i;
				directPrint();
			}
		} catch(Exception e){
			apresentarMsgNegocio(Severity.ERROR, ERRO_GERAR_RELATORIO);
		}
	}
	
	public String reImprimirProtocolo() {
		try {
			itemRetiradaList = patrimonioFacade.obterSubItensRelatorioProtocoloRetBensPermanentes(itemRetiradaList, itensDetalhamentoListCompleta, true);
				DocumentoJasper documento = null;
				documento = gerarDocumento();
				this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());	
//			}
			apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_IMPRESSAO");
			return PROTOCOLO_REIMPRIMIR_PROTOCOLO_RETIRADA_BEM_PERMANENTE;
		} catch(Exception e){
			apresentarMsgNegocio(Severity.ERROR, ERRO_GERAR_RELATORIO);
		}
		return StringUtils.EMPTY;
	}
	
	public void directPrint() throws ApplicationBusinessException, SistemaImpressaoException {
			DocumentoJasper documento = null;
			documento = gerarDocumento();
			try {
				this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
			} catch (UnknownHostException e) {
				LOG.error(e.getMessage(),e);
				this.apresentarMsgNegocio(Severity.ERROR, ERRO_GERAR_RELATORIO);
			} catch (JRException e) {
				LOG.error(e.getMessage(),e);
				this.apresentarMsgNegocio(Severity.ERROR, ERRO_GERAR_RELATORIO);
			}	
			apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_IMPRESSAO");
	}
	
	@Override
	protected Map<String, Object> recuperarParametros() {
		
		Long numeroRecebimento = null;
		
		Map<String, Object> parametros = new HashMap<String, Object>();
	    
		numeroRecebimento = itemRetiradaList.get(0).getSeqItemPatrimonio();
		
		tecnicoItemRecebimento = patrimonioFacade.obterTecnicoResponsavelPorMatENome(numeroRecebimento);
		
		nomeTecnicoResp = tecnicoItemRecebimento.getNome();
		if(tecnicoItemRecebimento != null && tecnicoItemRecebimento.getMatricula()!= null){
			tecnicoResponsavel = tecnicoItemRecebimento.getMatricula().toString();
		}
		
		nomeArea = itemRetiradaList.get(0).getNomeArea();
		nomeCentroCusto = itemRetiradaList.get(0).getNomeCentroCusto();
		centroCusto = itemRetiradaList.get(0).getCentroCusto().toString();
		try {
			parametros.put("caminhoLogo", parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_LOGO_HOSPITAL_JEE7));
			parametros.put("serMat", servidor.getId().getMatricula());
			parametros.put("serNome", servidor.getPessoaFisica().getNome());
			parametros.put("SUBREPORT_DIR", "br/gov/mec/aghu/patrimonio/report/");
			parametros.put("data", obterData());
			parametros.put("nomeTecnicoResp", nomeTecnicoResp);
			parametros.put("tecnicoResponsavel", tecnicoResponsavel);
			parametros.put("nomeArea", nomeArea);
			parametros.put("nomeCentroCusto", nomeCentroCusto);
			parametros.put("centroCusto", centroCusto);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return parametros;
	}
	public String obterData(){
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		String data = sdf.format(new Date());
		return data;
	}
	@Override
	protected List<ReImprimirAceiteTecnicoParaSerRealizadoVO> recuperarColecao()throws ApplicationBusinessException {
		List<AceiteTecnicoParaSerRealizadoVO> retornoFinalListaAceite = new ArrayList<AceiteTecnicoParaSerRealizadoVO>();
		retornoFinalListaAceite.addAll(itemRetiradaList);
		if(cameFrom.equals("REIMPRIMIR_PROTOCOLO")){
			aceiteVOPrinc = new ReImprimirAceiteTecnicoParaSerRealizadoVO();
			for (AceiteTecnicoParaSerRealizadoVO aceiteTecnico : itemRetiradaList) {
				if(aceiteTecnico.getItens().size() == 0){
					retornoFinalListaAceite.remove(aceiteTecnico);
				}
			}
			
			
			aceiteVOPrinc.setItemRetiradaList(retornoFinalListaAceite);
			List<ReImprimirAceiteTecnicoParaSerRealizadoVO> retorno = new ArrayList<ReImprimirAceiteTecnicoParaSerRealizadoVO>();
			retorno.add(aceiteVOPrinc);
			return retorno;
		}else{
			return null;
		}
		
	}

	@Override
	protected String recuperarArquivoRelatorio() {
		if(cameFrom.equals("REIMPRIMIR_PROTOCOLO")){
			return "br/gov/mec/aghu/patrimonio/report/protocoloReImpressaoRetiradaBensPermanentesAvaliacaoTecnicaPrincipal.jasper";
		}else{
			return "br/gov/mec/aghu/patrimonio/report/protocoloRetiradaBensPermanentesAvaliacaoTecnica.jasper";
		}
	}
	
	public String print() throws ApplicationBusinessException, JRException, IOException, DocumentException{
		DocumentoJasper documento = null;
		cameFrom = "REIMPRIMIR_PROTOCOLO";
		itemRetiradaList = patrimonioFacade.obterSubItensRelatorioProtocoloRetBensPermanentes(itemRetiradaList, itensDetalhamentoListCompleta, false);
		documento = gerarDocumento();
		media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(Boolean.TRUE)));
		return PROTOCOLO_REIMPRIMIR_PROTOCOLO_RETIRADA_BEM_PERMANENTE_PDF;
	}
	
	public String voltar() throws ApplicationBusinessException, BaseListException, CloneNotSupportedException {
		return reImprimirProtocoloRetiradaBemPermanenteController.iniciar();
	}

	public List<AceiteTecnicoParaSerRealizadoVO> getItemRetiradaList() {
		return itemRetiradaList;
	}

	public void setItemRetiradaList(
			List<AceiteTecnicoParaSerRealizadoVO> itemRetiradaList) {
		this.itemRetiradaList = itemRetiradaList;
	}

	public IPatrimonioFacade getPatrimonioFacade() {
		return patrimonioFacade;
	}

	public void setPatrimonioFacade(IPatrimonioFacade patrimonioFacade) {
		this.patrimonioFacade = patrimonioFacade;
	}

	public IServidorLogadoFacade getServidorLogadoFacade() {
		return servidorLogadoFacade;
	}

	public void setServidorLogadoFacade(IServidorLogadoFacade servidorLogadoFacade) {
		this.servidorLogadoFacade = servidorLogadoFacade;
	}

	public SistemaImpressao getSistemaImpressao() {
		return sistemaImpressao;
	}

	public void setSistemaImpressao(SistemaImpressao sistemaImpressao) {
		this.sistemaImpressao = sistemaImpressao;
	}

	public List<DetalhamentoRetiradaBemPermanenteVO> getItensDetalhamentoListCompleta() {
		return itensDetalhamentoListCompleta;
	}

	public void setItensDetalhamentoListCompleta(
			List<DetalhamentoRetiradaBemPermanenteVO> itensDetalhamentoListCompleta) {
		this.itensDetalhamentoListCompleta = itensDetalhamentoListCompleta;
	}
	
	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}
	
	public String getCameFrom() {
		return cameFrom;
	}

	public void setCameFrom(String cameFrom) {
		this.cameFrom = cameFrom;
	}

	public TecnicoItemRecebimentoVO getTecnicoItemRecebimento() {
		return tecnicoItemRecebimento;
	}

	public void setTecnicoItemRecebimento(TecnicoItemRecebimentoVO tecnicoItemRecebimento) {
		this.tecnicoItemRecebimento = tecnicoItemRecebimento;
	}

}
