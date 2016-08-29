package br.gov.mec.aghu.exames.action;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Vector;

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
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.PdfUtil;
import br.gov.mec.aghu.dominio.DominioSituacaoCxtPostalServidor;
import br.gov.mec.aghu.dominio.DominioSubTipoImpressaoLaudo;
import br.gov.mec.aghu.dominio.DominioTipoImpressaoLaudo;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.laudos.ExamesListaVO;
import br.gov.mec.aghu.exames.laudos.ResultadoLaudoVO;
import br.gov.mec.aghu.exames.solicitacao.vo.DetalharItemSolicitacaoPacienteVO;
import br.gov.mec.aghu.exames.vo.DesenhoMascaraExameVO;
import br.gov.mec.aghu.exames.vo.ListaDesenhosMascarasExamesVO;
import br.gov.mec.aghu.exameselaudos.business.IExamesLaudosFacade;
import br.gov.mec.aghu.impressao.OpcoesImpressao;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.AelItemSolicConsultado;
import br.gov.mec.aghu.model.AelItemSolicConsultadoHist;
import br.gov.mec.aghu.model.AelItemSolicConsultadoHistId;
import br.gov.mec.aghu.model.AelItemSolicConsultadoId;
import br.gov.mec.aghu.model.AelItemSolicExameHistId;
import br.gov.mec.aghu.model.AelItemSolicitacaoExamesId;
import br.gov.mec.aghu.model.AghCaixaPostalAplicacao;
import br.gov.mec.aghu.model.AghCaixaPostalServidor;
import br.gov.mec.aghu.model.AghCaixaPostalServidorId;
import br.gov.mec.aghu.model.AghParametroAplicacao;
import br.gov.mec.aghu.model.IAelItemSolicitacaoExames;
import br.gov.mec.aghu.model.IAelItemSolicitacaoExamesId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import net.sf.jasperreports.engine.JRException;


public class ConsultarResultadosNotaAdicionalController extends ActionController {

	private static final Log LOG = LogFactory.getLog(ConsultarResultadosNotaAdicionalController.class);
	private static final long serialVersionUID = 2941688789194461043L;
	
	private static final String PERFIL_USUARIO_TI = "ESP05"; //provisorio para ver botoes de impressao interno e externo
	
	private static final String PERMISSAO_IMPRESSAO_LAUDOS = "imprimirResultadoExame";
	
	private static final String REGEX_PONTO_VIRGULA = "\\;";
	
	private static final String REGEX_BARRA = "\\/";
	
	
	private ResultadoLaudoVO outputStreamLaudo;

	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private IExamesLaudosFacade examesLaudosFacade;

	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private ICascaFacade cascaFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@Inject
	private SecurityController securityController;
	
	private DesenhoMascaraExameVO desenhoMascaraExameVO;  
	
	private String voltarPara;

	private DetalharItemSolicitacaoPacienteVO pacienteVO;

	private Map<Integer, Vector<Short>> solicitacoes = new HashMap<Integer, Vector<Short>>();
	private List<ListaDesenhosMascarasExamesVO> listaDesenhosMascarasExamesVO;

	private Boolean permiteImprimirResultadoExamesPOL;

	private Long cxtSeq; //Seq de Caixa Postal Aplicacao
	
	private Boolean mostrarVoltar = Boolean.TRUE;
	
	private Boolean exameHistorico;
	private Boolean origemProntuarioOnline = Boolean.FALSE;//default e false
	
	// Caminho do arquivo de origem para geracao de pendÃªncias na certificacao digital
	private String caminhoArquivo;
	private DominioTipoImpressaoLaudo tipoLaudo = DominioTipoImpressaoLaudo.LAUDO_PAC_EXTERNO;
	private DominioSubTipoImpressaoLaudo dominioSubTipoImpressaoLaudo = DominioSubTipoImpressaoLaudo.LAUDO_GERAL;
	
	//Define se os dados acessados devem ser historico
	private Boolean isHist = Boolean.FALSE;//default e NAO HISTORICO
	
	private Boolean isDirectPrint = Boolean.FALSE;
	
	private Boolean laudoGerado = false;
	
	private Map<Integer, Vector<Short>> itensSelecionados;
	
	private String paramSoeSeqSeqp; //no formato (encoded): solic/item;solic/item 
	
	private String paramTipoLaudo; 
	
	private String paramSubTipoLaudo;
	
	private boolean exibeBotoes = true;
	
	protected ExamesListaVO dadosLaudo;
	
	private boolean usuarioHomologador;
	
	private boolean exibirDadosPaciente;
	
	List<IAelItemSolicitacaoExamesId> itemIds;

	private boolean previaPDF;
	
	private StreamedContent streamedLaudoPdf;
	
	private boolean executouInicio;

	@PostConstruct
	public void init() {
		begin(conversation);
		permiteImprimirResultadoExamesPOL = securityController.usuarioTemPermissao("permiteImprimirResultadoExamesPOL", "imprimir");
		
		Set<String> listaUsuarioLaudoAGHU = cascaFacade.obterNomePerfisPorUsuario(obterLoginUsuarioLogado().toUpperCase());
		usuarioHomologador = listaUsuarioLaudoAGHU.contains(PERFIL_USUARIO_TI);
	}	
	
	public String inicio() {
		if (!this.executouInicio) {
		try {
			outputStreamLaudo = null;
			streamedLaudoPdf = null;
			
			if (!previaPDF) {
				dadosLaudo = null;
			}
			paramSoeSeqSeqp = this.getRequestParameter("paramSoeSeqSeqp");
			boolean isToken = paramSoeSeqSeqp != null;
			
			if (isToken) {
				this.recebeParametrosRequisicao();
			} else {
				mostrarVoltar = Boolean.TRUE;
				String paramCxtSeq = this.getRequestParameter("cxtSeq");
				if (paramCxtSeq != null) {
					cxtSeq = Long.valueOf(paramCxtSeq);
					mostrarVoltar = Boolean.FALSE;
					this.recebeParametrosCaixaPostal();

					cxtSeq = null;
				}
			}

			examesLaudosFacade.verificaLaudoPatologia(solicitacoes, this.isHist);

			itemIds = converteParametros();
			
			if (!previaPDF) {
				 itemIds = converteParametros();
			
				// executa laudo no inicio deixando pronto para a pagina
				dadosLaudo = this.examesFacade.buscarDadosLaudo(itemIds);
			}
			
			examesFacade.setCaminhoLogo(parametroFacade.recuperarCaminhoLogo());
			
			if (cascaFacade.usuarioTemPermissao(obterLoginUsuarioLogado(), PERMISSAO_IMPRESSAO_LAUDOS)) {
				this.outputStreamLaudo = this.examesFacade.executaLaudo(
						this.dadosLaudo, DominioTipoImpressaoLaudo.LAUDO_SAMIS);
			}
			else {
				this.outputStreamLaudo = this.examesFacade.executaLaudo(
						this.dadosLaudo, DominioTipoImpressaoLaudo.LAUDO_PAC_EXTERNO);
			}

			//Erro ao mostrar laudo com fonte inexistente no servidor
			if (!outputStreamLaudo.getFontsAlteradas().isEmpty()){
				apresentarMsgNegocio(Severity.WARN, "AVISO_FONTS_ALTERADAS_LAUDO", outputStreamLaudo.getFontsAlteradas().toString());
			}

			if (outputStreamLaudo != null){
				ByteArrayOutputStream baos = this.outputStreamLaudo.getOutputStreamLaudo();
				if (getPermiteImprimirResultadoExamesPOL() || (cascaFacade.usuarioTemPermissao(obterLoginUsuarioLogado(), PERMISSAO_IMPRESSAO_LAUDOS))) {
					streamedLaudoPdf =  ActionReport.criarStreamedContentPdfPorByteArray(baos.toByteArray());
				} else {
					streamedLaudoPdf =  ActionReport.criarStreamedContentPdfPorByteArray(PdfUtil.protectPdf(baos.toByteArray()).toByteArray());
				}
				setLaudoGerado(true);
			} 	
			
			pacienteVO = new DetalharItemSolicitacaoPacienteVO();
			pacienteVO.setNomePaciente(dadosLaudo.getNomePaciente());
			pacienteVO.setProntuarioPaciente(Objects.toString(dadosLaudo.getProntuario(), ""));
			
			// registra a visualizacao
			if (!previaPDF) {
				this.insereVisualizaoItens(itemIds);
			}
			
			exibirDadosPaciente = verificarSolicitacoesPacUnico();
			
		} catch (BaseException e) {
			setLaudoGerado(false);
			apresentarExcecaoNegocio(e);
			return voltarPara;
		} catch (IOException e) {
			setLaudoGerado(false);
			apresentarExcecaoNegocio(new BaseException(ApplicationBusinessExceptionCode.OCORREU_O_SEGUINTE_ERRO, e, e.getLocalizedMessage()));
			return voltarPara;
		} catch (DocumentException e) {
			setLaudoGerado(false);
			apresentarExcecaoNegocio(new BaseException(ApplicationBusinessExceptionCode.OCORREU_O_SEGUINTE_ERRO, e, e.getLocalizedMessage()));
			return voltarPara;
		}
	}
		return null;
	}
	
	public void limpar(){
		outputStreamLaudo = null;
	}	
	
	private boolean verificarSolicitacoesPacUnico(){
		IAelItemSolicitacaoExames item;
		
		List<Integer> listaPacCodigo = new ArrayList<Integer>();
		Integer pacCodigo = null;
		for (IAelItemSolicitacaoExamesId id : itemIds){
			item = buscarItemSolicitacaoExame(id);
			
			if (item.getSolicitacaoExame().getAtendimento() != null){
				pacCodigo = item.getSolicitacaoExame().getAtendimento().getPacCodigo();
			} else {
				if(item.getSolicitacaoExame().getAtendimentoDiverso().getAipPaciente() != null){
					pacCodigo = item.getSolicitacaoExame().getAtendimentoDiverso().getAipPaciente().getCodigo();	
				}
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
	
	private IAelItemSolicitacaoExames buscarItemSolicitacaoExame(IAelItemSolicitacaoExamesId id) {
		IAelItemSolicitacaoExames item = examesFacade.buscaItemSolicitacaoExamePorId(id.getSoeSeq(), id.getSeqp());
		if(item == null) {
			item = examesFacade.buscaItemSolicitacaoExamePorIdHist(id.getSoeSeq(), id.getSeqp());
		}
		return item;
	}

	/**
	 * Recebe parametros pela caixa postal e coloca em atributos desta controller para processar laudo.
	 */
	private void recebeParametrosCaixaPostal() throws ApplicationBusinessException {
		solicitacoes = new HashMap<Integer, Vector<Short>>();
		List<AghCaixaPostalAplicacao> listCaixaPostalAp = aghuFacade.pesquisarCaixaPostalPorCxtSeq(cxtSeq);

		for(AghCaixaPostalAplicacao caixaPostalAplicacao: listCaixaPostalAp){

			if(caixaPostalAplicacao.getOrdemChamada().equals(Short.valueOf("1"))){ // 1 == AELP_GRAVA_RESULTADO

				List<AghParametroAplicacao>  parametrosAplicacao = aghuFacade.pesquisarParametroAplicacaoPorCaixaPostalAplicacao(caixaPostalAplicacao.getId());

				if(parametrosAplicacao!=null){

					this.solicitacoes.put(Integer.valueOf(parametrosAplicacao.get(0).getParametros()), new Vector<Short>());
					this.solicitacoes.get(Integer.valueOf(parametrosAplicacao.get(0).getParametros())).add(Short.valueOf(parametrosAplicacao.get(1).getParametros()));
				}
			}
		}
		
		AghCaixaPostalServidorId id = new AghCaixaPostalServidorId();
		id.setCxtSeq(cxtSeq);
		id.setServidor(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()));
		AghCaixaPostalServidor caixaPostalServidor = this.aghuFacade.obterAghCaixaPostalServidor(id);
		caixaPostalServidor.setSituacao(DominioSituacaoCxtPostalServidor.E);
		caixaPostalServidor.setDthrExcluida(new Date());
		caixaPostalServidor.setMotivoExcluida("Exclusao da mensagem pelo usuario");
		this.aghuFacade.atualizarAghCaixaPostalServidor(caixaPostalServidor);
	}

	/**
	 * Recebe os parametros da requiscao e coloca em atributos desta controller para processar laudo.
	 */
	private void recebeParametrosRequisicao() {
		mostrarVoltar = Boolean.FALSE;
			
		solicitacoes = new HashMap<Integer, Vector<Short>>();
		paramTipoLaudo = this.getRequestParameter("paramTipoLaudo");
		paramSubTipoLaudo = this.getRequestParameter("paramSubTipoLaudo");
		isDirectPrint = Boolean.valueOf(this.getRequestParameter("isDirectPrint"));
		isHist = Boolean.valueOf(this.getRequestParameter("isHist"));
		
		if (DominioTipoImpressaoLaudo.LAUDO_PAC_EXTERNO.toString().equals(paramTipoLaudo)) {
			tipoLaudo = DominioTipoImpressaoLaudo.LAUDO_PAC_EXTERNO;
		}
		else if (DominioTipoImpressaoLaudo.LAUDO_SAMIS.toString().equals(paramTipoLaudo)) {
			tipoLaudo = DominioTipoImpressaoLaudo.LAUDO_SAMIS;
		}
		
		if (DominioSubTipoImpressaoLaudo.LAUDO_SISMAMA.toString().equals(paramSubTipoLaudo)) {
			dominioSubTipoImpressaoLaudo = DominioSubTipoImpressaoLaudo.LAUDO_SISMAMA;
		}
		else {
			dominioSubTipoImpressaoLaudo = DominioSubTipoImpressaoLaudo.LAUDO_GERAL;
		}
		
		String solicitacaoComItemArray[] = paramSoeSeqSeqp.split(REGEX_PONTO_VIRGULA);
		
		for (String solicComItem : solicitacaoComItemArray) {
			
			String solicItem[] = solicComItem.split(REGEX_BARRA);
			
			if (solicitacoes.get(Integer.parseInt(solicItem[0])) != null) {
				solicitacoes.get(Integer.parseInt(solicItem[0])).add(Short.parseShort(solicItem[1]));
			}
			else {
				solicitacoes.put(Integer.parseInt(solicItem[0]), new Vector<Short>());
				solicitacoes.get(Integer.parseInt(solicItem[0])).add(Short.parseShort(solicItem[1]));
			}
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
	
	/**
	 * Insere registro de visualizaÃ§Ã£o de todos os itens fornecidos.
	 */
	private void insereVisualizaoItens(List<IAelItemSolicitacaoExamesId> itemIds) throws ApplicationBusinessException {
		for (IAelItemSolicitacaoExamesId id : itemIds) {
			this.insereVisualizaoItem(id);
		}
	}
	
	/**
	 * Insere registro de visualizaÃ§Ã£o do item com o id fornecido.
	 */
	private void insereVisualizaoItem(IAelItemSolicitacaoExamesId id) throws ApplicationBusinessException {
		if (isHist) {
			AelItemSolicConsultadoHist itemSolicConsultado = new AelItemSolicConsultadoHist();
			AelItemSolicConsultadoHistId idConcreto = new AelItemSolicConsultadoHistId();
			idConcreto.setIseSeqp(id.getSeqp());
			idConcreto.setIseSoeSeq(id.getSoeSeq());
			itemSolicConsultado.setId(idConcreto);
			examesFacade.insereVisualizacaoItemSolicitacaoHist(itemSolicConsultado, Boolean.TRUE);
		} else {
			AelItemSolicConsultado itemSolicConsultado = new AelItemSolicConsultado();
			AelItemSolicConsultadoId idConcreto = new AelItemSolicConsultadoId();
			idConcreto.setIseSeqp(id.getSeqp());
			idConcreto.setIseSoeSeq(id.getSoeSeq());
			itemSolicConsultado.setId(idConcreto);
			examesFacade.insereVisualizacaoItemSolicitacao(itemSolicConsultado,Boolean.TRUE);
		}

	}
	
	/**
	 * Metodo invocado pelo a:mediaOutput para geracao de PDF dentro de XHTML.
	 */
	public StreamedContent getRenderPdf() throws IOException, BaseException, JRException, SystemException, DocumentException, com.lowagie.text.DocumentException {
		if (outputStreamLaudo != null){
			ByteArrayOutputStream baos = this.outputStreamLaudo.getOutputStreamLaudo();
			if (getPermiteImprimirResultadoExamesPOL() || (cascaFacade.usuarioTemPermissao(obterLoginUsuarioLogado(), PERMISSAO_IMPRESSAO_LAUDOS))) {
				return ActionReport.criarStreamedContentPdfPorByteArray(baos.toByteArray());
			} else {
				return ActionReport.criarStreamedContentPdfPorByteArray(PdfUtil.protectPdf(baos.toByteArray()).toByteArray());			
			}
		} else {
			return null;
		}
	
	}
	
	public StreamedContent getRenderLaudoPdf() throws IOException, DocumentException {
		return streamedLaudoPdf;
	}	

	public void directPrint() {
		directPrint(DominioTipoImpressaoLaudo.LAUDO_PAC_EXTERNO.toString());
	}
	
	
	/**
	 * Impressao direta usando o CUPS.
	 */
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

	public ResultadoLaudoVO executaLaudo(boolean visualizacao)
			throws ApplicationBusinessException, BaseException {
		return this.examesFacade.executaLaudo(this.converteParametros(),
				this.tipoLaudo);
	}
	
	public String voltar() {
		solicitacoes = new HashMap<Integer, Vector<Short>>();
		listaDesenhosMascarasExamesVO=null;
		return this.voltarPara;
	}

	/** GET/SET **/
	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public DetalharItemSolicitacaoPacienteVO getPacienteVO() {
		return pacienteVO;
	}

	public void setPacienteVO(DetalharItemSolicitacaoPacienteVO pacienteVO) {
		this.pacienteVO = pacienteVO;
	}

	public Map<Integer, Vector<Short>> getSolicitacoes() {
		return solicitacoes;
	}

	public void setSolicitacoes(Map<Integer, Vector<Short>> solicitacoes) {
		this.solicitacoes = solicitacoes;
	}

	public List<ListaDesenhosMascarasExamesVO> getListaDesenhosMascarasExamesVO() {
		return listaDesenhosMascarasExamesVO;
	}

	public void setListaDesenhosMascarasExamesVO(List<ListaDesenhosMascarasExamesVO> listaDesenhosMascarasExamesVO) {
		this.listaDesenhosMascarasExamesVO = listaDesenhosMascarasExamesVO;
	}

	public Boolean getPermiteImprimirResultadoExamesPOL() {
		return permiteImprimirResultadoExamesPOL;
	}

	public void setPermiteImprimirResultadoExamesPOL(
			Boolean permiteImprimirResultadoExamesPOL) {
		this.permiteImprimirResultadoExamesPOL = permiteImprimirResultadoExamesPOL;
	}

	public Long getCxtSeq() {
		return cxtSeq;
	}

	public void setCxtSeq(Long cxtSeq) {
		this.cxtSeq = cxtSeq;
	}

	public Boolean getMostrarVoltar() {
		return mostrarVoltar;
	}

	public void setMostrarVoltar(Boolean mostrarVoltar) {
		this.mostrarVoltar = mostrarVoltar;
	}

	public DominioTipoImpressaoLaudo getTipoLaudo() {
		return tipoLaudo;
	}

	public void setTipoLaudo(DominioTipoImpressaoLaudo tipoLaudo) {
		this.tipoLaudo = tipoLaudo;
	}
	
	public String getCaminhoArquivo() {
		return caminhoArquivo;
	}
	
	public void setCaminhoArquivo(String caminhoArquivo) {
		this.caminhoArquivo = caminhoArquivo;
	}

	public DominioSubTipoImpressaoLaudo getDominioSubTipoImpressaoLaudo() {
		return dominioSubTipoImpressaoLaudo;
	}

	public void setDominioSubTipoImpressaoLaudo(
			DominioSubTipoImpressaoLaudo dominioSubTipoImpressaoLaudo) {
		this.dominioSubTipoImpressaoLaudo = dominioSubTipoImpressaoLaudo;
	}

	public Boolean getExameHistorico() {
		return exameHistorico;
	}

	public void setExameHistorico(Boolean exameHistorico) {
		this.exameHistorico = exameHistorico;
	}

	public Boolean getOrigemProntuarioOnline() {
		return origemProntuarioOnline;
	}

	public void setOrigemProntuarioOnline(Boolean origemProntuarioOnline) {
		this.origemProntuarioOnline = origemProntuarioOnline;
	}

	public Boolean getIsHist() {
		return isHist;
	}

	public void setIsHist(Boolean isHist) {
		this.isHist = isHist;
	}

	public Map<Integer, Vector<Short>> getItensSelecionados() {
		return itensSelecionados;
	}

	public void setItensSelecionados(Map<Integer, Vector<Short>> itensSelecionados) {
		this.itensSelecionados = itensSelecionados;
	}

	public DesenhoMascaraExameVO getDesenhoMascaraExameVO() {
		return desenhoMascaraExameVO;
	}

	public void setDesenhoMascaraExameVO(DesenhoMascaraExameVO desenhoMascaraExameVO) {
		this.desenhoMascaraExameVO = desenhoMascaraExameVO;
	}

	public boolean isExibeBotoes() {
		return exibeBotoes;
	}

	public void setExibeBotoes(boolean exibeBotoes) {
		this.exibeBotoes = exibeBotoes;
	}
	
	public String getParamSoeSeqSeqp() {
		return paramSoeSeqSeqp;
	}

	public void setParamSoeSeqSeqp(String paramSoeSeqSeqp) {
		this.paramSoeSeqSeqp = paramSoeSeqSeqp;
	}

	public Boolean getIsDirectPrint() {
		return isDirectPrint;
	}

	public void setIsDirectPrint(Boolean isDirectPrint) {
		this.isDirectPrint = isDirectPrint;
	}

	public Boolean getLaudoGerado() {
		return laudoGerado;
	}

	public void setLaudoGerado(Boolean laudoGerado) {
		this.laudoGerado = laudoGerado;
	}

	public boolean isUsuarioHomologador() {
		return usuarioHomologador;
	}

	public void setUsuarioHomologador(boolean usuarioHomologador) {
		this.usuarioHomologador = usuarioHomologador;
	}

	public boolean isPreviaPDF() {
		return previaPDF;
	}

	public void setPreviaPDF(boolean previaPDF) {
		this.previaPDF = previaPDF;
	}

	public ExamesListaVO getDadosLaudo() {
		return dadosLaudo;
	}

	public void setDadosLaudo(ExamesListaVO dadosLaudo) {
		this.dadosLaudo = dadosLaudo;
	}

	public boolean isExibirDadosPaciente() {
		return exibirDadosPaciente;
	}

	public void setExibirDadosPaciente(boolean exibirDadosPaciente) {
		this.exibirDadosPaciente = exibirDadosPaciente;
	}

	public boolean isExecutouInicio() {
		return executouInicio;
	}

	public void setExecutouInicio(boolean executouInicio) {
		this.executouInicio = executouInicio;
	}
}