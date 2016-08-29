package br.gov.mec.aghu.exames.patologia.action;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.dominio.DominioSubTipoImpressaoLaudo;
import br.gov.mec.aghu.dominio.DominioTipoImpressaoLaudo;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.laudos.ExamesListaVO;
import br.gov.mec.aghu.exames.laudos.ResultadoLaudoVO;
import br.gov.mec.aghu.exameselaudos.business.IExamesLaudosFacade;
import br.gov.mec.aghu.impressao.OpcoesImpressao;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.AelItemSolicExameHistId;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExamesId;
import br.gov.mec.aghu.model.IAelItemSolicitacaoExamesId;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.PdfUtil;

import com.itextpdf.text.DocumentException;

public class RelatorioLaudoUnicoPdfController extends ActionController {

	private static final Log LOG = LogFactory.getLog(RelatorioLaudoUnicoPdfController.class);

	private static final long serialVersionUID = -2397479146598258175L;
	
	@EJB
	private IExamesLaudosFacade examesLaudosFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@Inject
	private SecurityController securityController;		

	@Inject
	private SistemaImpressao sistemaImpressao;
	
	private Boolean isDirectPrint;

	private StreamedContent media;
	
	private String cameFrom;

	private String nroAps;
	
	private Integer seqConfigExLaudoUnico;
	
	List<IAelItemSolicitacaoExamesId> itemIds;
	private String nomePaciente;	
	private String prontuarioPaciente;		
	private boolean exibirDadosPaciente;
	
	private Map<Integer, Vector<Short>> solicitacoes = new HashMap<Integer, Vector<Short>>();
	private DominioSubTipoImpressaoLaudo dominioSubTipoImpressaoLaudo = DominioSubTipoImpressaoLaudo.LAUDO_GERAL;
	private DominioTipoImpressaoLaudo tipoLaudo = DominioTipoImpressaoLaudo.LAUDO_SAMIS;
	private ExamesListaVO dadosLaudo;
	private Boolean isHist = Boolean.FALSE; 
	private ResultadoLaudoVO outputStreamLaudo;
	private Boolean permiteImprimirResultadoExamesPOL;
	private Boolean laudoGerado = false;
	
	
	@PostConstruct
	protected void inicializar() {
		permiteImprimirResultadoExamesPOL = securityController.usuarioTemPermissao("permiteImprimirResultadoExamesPOL", "imprimir");
		this.begin(conversation);
	}
	
	/**
	 * Chamado a partir de Laudo Unico, aba: Conclusão
	 * 
	 * @param nroAps
	 *            - Ap a ser enviado ao relatorio
	 * @return
	 */
	public String inicio(final String nroAps, String cameFrom, Integer seqConfigExLaudoUnico) {
		this.nroAps = nroAps;
		this.cameFrom = cameFrom;
		this.isDirectPrint = false;
		this.seqConfigExLaudoUnico = seqConfigExLaudoUnico;
		return inicio();
	}

	public String inicio() {
		examesLaudosFacade.verificaLaudoPatologia(solicitacoes, this.isHist);

		itemIds = converteParametros();
		
		try {
			// executa laudo no inicio deixando pronto para a pagina
			dadosLaudo = this.examesFacade.buscarDadosLaudo(itemIds);
			this.outputStreamLaudo = this.examesFacade.executaLaudo(this.dadosLaudo, null);
			
			//Erro ao mostrar laudo com fonte inexistente no servidor
			if (!outputStreamLaudo.getFontsAlteradas().isEmpty()){
				apresentarMsgNegocio(Severity.WARN, "AVISO_FONTS_ALTERADAS_LAUDO", outputStreamLaudo.getFontsAlteradas().toString());
			}
			
			if (outputStreamLaudo != null) {
				setLaudoGerado(true);
			}		
			
			this.setNomePaciente(dadosLaudo.getNomePaciente());
			this.setProntuarioPaciente(Objects.toString(dadosLaudo.getProntuario(), ""));
			
			exibirDadosPaciente = verificarSolicitacoesPacUnico();
		} catch (BaseException e) {
			setLaudoGerado(false);
			apresentarExcecaoNegocio(e);
			return cameFrom;
		}

		return null;	
	}

	private boolean verificarSolicitacoesPacUnico(){
		AelItemSolicitacaoExames item;
		
		List<Integer> listaPacCodigo = new ArrayList<Integer>();
		Integer pacCodigo;
		for (IAelItemSolicitacaoExamesId id : itemIds){
			item = examesFacade.buscaItemSolicitacaoExamePorId(id.getSoeSeq(), id.getSeqp());
			if (item.getSolicitacaoExame().getAtendimento() != null){
				pacCodigo = item.getSolicitacaoExame().getAtendimento().getPacCodigo();
			} else {
				pacCodigo = item.getSolicitacaoExame().getAtendimentoDiverso().getAipPaciente().getCodigo();
			}
			
			if (!listaPacCodigo.contains(pacCodigo)){
				listaPacCodigo.add(pacCodigo);
			}
 
			if (listaPacCodigo.size() > 1){
				return false;
			}
		}
		
		return true;
	}
	
	
	public void directPrint(){
		this.directPrint(null);
	}
	
	public void directPrint(String tipo) {		
		// -- Pra homologar foi colocado dois botoes 
		if (DominioTipoImpressaoLaudo.LAUDO_PAC_EXTERNO.toString().equals(tipo)) {
			tipoLaudo = DominioTipoImpressaoLaudo.LAUDO_PAC_EXTERNO;
		}
		if (DominioTipoImpressaoLaudo.LAUDO_SAMIS.toString().equals(tipo)) {
			tipoLaudo = DominioTipoImpressaoLaudo.LAUDO_SAMIS;
		}

		try {
			examesLaudosFacade.verificaLaudoPatologia(solicitacoes, this.isHist);
			List<IAelItemSolicitacaoExamesId> itemIds = converteParametros();
			dadosLaudo = this.examesFacade.buscarDadosLaudo(itemIds);

			ResultadoLaudoVO baos = this.examesFacade.executaLaudo(
					this.dadosLaudo, this.tipoLaudo);
			OpcoesImpressao fitToPage = new OpcoesImpressao();
			fitToPage.setFitToPage(true);
			this.sistemaImpressao.imprimir(baos.getOutputStreamLaudo(),
					super.getEnderecoIPv4HostRemoto(), fitToPage);
			
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");

		} catch (SistemaImpressaoException e) {
			LOG.error(e.getMessage(), e);
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}
	
	private List<IAelItemSolicitacaoExamesId> converteParametros() {
		List<IAelItemSolicitacaoExamesId> itemIds = new ArrayList<IAelItemSolicitacaoExamesId>();
		for (Map.Entry<Integer, Vector<Short>> entry : solicitacoes
				.entrySet()) {
			Integer seq = entry.getKey();
			for (Short seqp : entry.getValue()) {
				IAelItemSolicitacaoExamesId id = null;
				if (isHist) {
					id = new AelItemSolicExameHistId(seq, seqp);
				} else {
					id = new AelItemSolicitacaoExamesId(seq, seqp);
				}
				itemIds.add(id);
			}
		}
		return itemIds;
	}	
	
	public String voltar() {
		String retorno = this.cameFrom;
		this.cameFrom = null;
		this.nroAps = null;
		this.isDirectPrint = null;
		
		return retorno;
	}

	public StreamedContent getRenderPdf() throws IOException, DocumentException{
		ByteArrayOutputStream baos = this.outputStreamLaudo.getOutputStreamLaudo();
		if (getPermiteImprimirResultadoExamesPOL()) {
			return ActionReport.criarStreamedContentPdfPorByteArray(baos.toByteArray());
		} else {
			return ActionReport.criarStreamedContentPdfPorByteArray(PdfUtil.protectPdf(baos.toByteArray()).toByteArray()); 	
		}
	}
	
	public StreamedContent getMedia() {	
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}
	
	public String getNroAps() {
		return nroAps;
	}

	public void setNroAps(String nroAps) {
		this.nroAps = nroAps;
	}

	public String getCameFrom() {
		return cameFrom;
	}

	public void setCameFrom(String cameFrom) {
		this.cameFrom = cameFrom;
	}
	
	public Integer getSeqConfigExLaudoUnico() {
		return seqConfigExLaudoUnico;
	}

	public void setSeqConfigExLaudoUnico(Integer seqConfigExLaudoUnico) {
		this.seqConfigExLaudoUnico = seqConfigExLaudoUnico;
	}

	public Map<Integer, Vector<Short>> getSolicitacoes() {
		return solicitacoes;
	}

	public void setSolicitacoes(Map<Integer, Vector<Short>> solicitacoes) {
		this.solicitacoes = solicitacoes;
	}

	public DominioSubTipoImpressaoLaudo getDominioSubTipoImpressaoLaudo() {
		return dominioSubTipoImpressaoLaudo;
	}

	public void setDominioSubTipoImpressaoLaudo(
			DominioSubTipoImpressaoLaudo dominioSubTipoImpressaoLaudo) {
		this.dominioSubTipoImpressaoLaudo = dominioSubTipoImpressaoLaudo;
	}

	public DominioTipoImpressaoLaudo getTipoLaudo() {
		return tipoLaudo;
	}

	public void setTipoLaudo(DominioTipoImpressaoLaudo tipoLaudo) {
		this.tipoLaudo = tipoLaudo;
	}

	public Boolean getPermiteImprimirResultadoExamesPOL() {
		return permiteImprimirResultadoExamesPOL;
	}

	public void setPermiteImprimirResultadoExamesPOL(
			Boolean permiteImprimirResultadoExamesPOL) {
		this.permiteImprimirResultadoExamesPOL = permiteImprimirResultadoExamesPOL;
	}

	public Boolean getLaudoGerado() {
		return laudoGerado;
	}

	public void setLaudoGerado(Boolean laudoGerado) {
		this.laudoGerado = laudoGerado;
	}

	public Boolean getIsDirectPrint() {
		return isDirectPrint;
	}

	public void setIsDirectPrint(Boolean isDirectPrint) {
		this.isDirectPrint = isDirectPrint;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public String getProntuarioPaciente() {
		return prontuarioPaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public void setProntuarioPaciente(String prontuarioPaciente) {
		this.prontuarioPaciente = prontuarioPaciente;
	}

	public boolean isExibirDadosPaciente() {
		return exibirDadosPaciente;
	}

	public void setExibirDadosPaciente(boolean exibirDadosPaciente) {
		this.exibirDadosPaciente = exibirDadosPaciente;
	}
}