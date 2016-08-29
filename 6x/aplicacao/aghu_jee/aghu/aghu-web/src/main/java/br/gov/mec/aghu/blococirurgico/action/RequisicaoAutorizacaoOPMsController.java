package br.gov.mec.aghu.blococirurgico.action;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.opmes.business.IBlocoCirurgicoOpmesFacade;
import br.gov.mec.aghu.blococirurgico.vo.ConsultarHistoricoProcessoOpmeVO;
import br.gov.mec.aghu.blococirurgico.vo.ExecutorEtapaAtualVO;
import br.gov.mec.aghu.blococirurgico.vo.ListaMateriaisRequisicaoOpmesVO;
import br.gov.mec.aghu.blococirurgico.vo.MbcItensRequisicaoOpmesVO;
import br.gov.mec.aghu.blococirurgico.vo.MbcOpmesListaGrupoExcludente;
import br.gov.mec.aghu.blococirurgico.vo.MbcOpmesVO;
import br.gov.mec.aghu.blococirurgico.vo.RequerenteVO;
import br.gov.mec.aghu.blococirurgico.vo.RequisicoesOPMEsProcedimentosVinculadosVO;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoRequisicao;
import br.gov.mec.aghu.estoque.vo.MaterialOpmeVO;
import br.gov.mec.aghu.model.AghWFEtapa;
import br.gov.mec.aghu.model.AghWFFluxo;
import br.gov.mec.aghu.model.AghWFHistoricoExecucao;
import br.gov.mec.aghu.model.AghWFTemplateEtapa;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.MbcItensRequisicaoOpmes;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MbcRequisicaoOpmes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class RequisicaoAutorizacaoOPMsController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 5959269543314994355L;
	
	private static final String VOLTAR_TELA_ACOMPANHAMENTO = "acompanharProcessoAutorizacaoOPMs";
	private static final String VISUALIZA_AUTORIZACAO = "blococirurgico-visualizarAutorizacaoOpmePdf";
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@EJB
	private IBlocoCirurgicoOpmesFacade blocoCirurgicoOpmesFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IComprasFacade comprasFacadeFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	private RequisicoesOPMEsProcedimentosVinculadosVO itemRequisicao;
	private ExecutorEtapaAtualVO executorEtapaAtual;
	private List<MbcItensRequisicaoOpmes> listaMateriais;
	private List<ListaMateriaisRequisicaoOpmesVO> listaMateriaisVO;
	private ListaMateriaisRequisicaoOpmesVO itemListaSelecionado;
	private Date dataRequisicao;
	private RequerenteVO requerente;
	private String etapaAtualRequisicao;
	private String procedimentoPrincipal;
	private String procedimentoSus;
	private String observacaoAutorizacao;
	private String justificativaNegacao;	
	private String justificativa;
	private String observacao;
	private DominioSimNao compativel;
	private DominioSimNao licitado;
	private Double totalIncompativel;
	private Double totalCompativel;
	private String incompatibilidade;
	private Boolean habilitaAutorizacao = Boolean.FALSE;
	private Boolean abreObservacaoAutorizacao;
	private Boolean abreJustificativaNegacao;
	private Boolean habilitaBotaoVisualizarAutorizacao = Boolean.FALSE;
	private Short requisicaoSeqAutorizada;
	private String voltarParaUrl;
	private List<MbcItensRequisicaoOpmesVO> listaBind;
	private static final String ACOMPANHAR_PROCESSO_AUTORIZACAO_OPMS = "blococirurgico-acompanharProcessoAutorizacaoOPMs";
	private Integer fluxoSeq;
	private List<ConsultarHistoricoProcessoOpmeVO> listaHistoricoVO;
	private ConsultarHistoricoProcessoOpmeVO requisicoesOPMEsProcedimentosVinculadosSelecionado;
	private Date dataProcedimento;
	private String nomePaciente;
	private String strProntuario;
	private BigDecimal valorUnit;
	private List<MbcOpmesVO> listaPesquisada;
	private List<MbcOpmesVO> listaPesquisadaClone;
	private MbcRequisicaoOpmes requisicaoDetalhe;

	private static final Log LOG = LogFactory.getLog(RequisicaoAutorizacaoOPMsController.class);
	
	public void inicio(){		
		
		try {			
			//Reinicia o filtro ao iniciar
			compativel = null;
			licitado = null;
			justificativaNegacao = null;
			observacaoAutorizacao = null;
			habilitaAutorizacao = Boolean.FALSE;
			fluxoSeq = null;

			if(itemRequisicao != null){
				this.setDataRequisicao(this.itemRequisicao.getDataRequisicao());
				List<RequerenteVO> requerentes = this.blocoCirurgicoFacade.consultarRequerentes(this.itemRequisicao.getRequerenteMatricula());
				if(requerentes != null && !requerentes.isEmpty()) {
					this.setRequerente(requerentes.get(0));
				}
				this.setEtapaAtualRequisicao(this.itemRequisicao.getEtapaDescricao());
				
				requisicaoDetalhe = this.obterDetalhesRequisicao(this.itemRequisicao.getRequisicaoSeq());
				
				if(requisicaoDetalhe != null){
					List<MaterialOpmeVO> vo = comprasFacadeFacade.obterValorMateriaisProcedimento(requisicaoDetalhe.getSeq());
					if(vo != null){
						if(vo.size() > 0){
							valorUnit = vo.get(0).getIphValorUnit();
						}
					}
					if(requisicaoDetalhe.getFluxo() != null){
						fluxoSeq = requisicaoDetalhe.getFluxo().getSeq();
					}
				}
				dataProcedimento = requisicaoDetalhe.getAgendas().getDtAgenda();
				nomePaciente = requisicaoDetalhe.getAgendas().getPaciente().getNome();
				strProntuario = CoreUtil.formataProntuario(requisicaoDetalhe.getAgendas().getPaciente().getProntuario());
				
				this.setJustificativa(requisicaoDetalhe.getJustificativaRequisicaoOpme());
				this.setObservacao(requisicaoDetalhe.getObservacaoOpme());
					
				MbcProcedimentoCirurgicos proced = requisicaoDetalhe.getAgendas().getEspProcCirgs().getMbcProcedimentoCirurgicos();
				this.setProcedimentoPrincipal(proced.getDescricao());
				
				FatItensProcedHospitalar itens = requisicaoDetalhe.getAgendas().getItensProcedHospitalar();
				this.setProcedimentoSus(itens.getCodTabela().toString() + " - " + itens.getDescricao());
			
				RapServidores servidorLogado = this.getServidorLogado();
				this.executorEtapaAtual = this.blocoCirurgicoOpmesFacade.pesquisarExecutorEtapaAtualProcesso(this.itemRequisicao.getRequisicaoSeq(), servidorLogado);
				
				if((DominioSituacaoRequisicao.EM_AUTORIZACAO_01.equals(requisicaoDetalhe.getSituacao()) ||
					  DominioSituacaoRequisicao.EM_AUTORIZACAO_02.equals(requisicaoDetalhe.getSituacao()) || 
					  DominioSituacaoRequisicao.EM_AUTORIZACAO_03.equals(requisicaoDetalhe.getSituacao()) ||
					      DominioSituacaoRequisicao.EM_AUTORIZACAO_04.equals(requisicaoDetalhe.getSituacao())) && this.executorEtapaAtual != null && this.executorEtapaAtual.getAutorizadoExecutar()){
					
					this.setHabilitaAutorizacao(Boolean.TRUE);
					
					List<RapServidores> servidoresAutorizados = new ArrayList<RapServidores>();
					
					AghWFEtapa etapa = new AghWFEtapa();
					AghWFTemplateEtapa templateEtapa = new AghWFTemplateEtapa();
					templateEtapa.setCodigo(requisicaoDetalhe.getSituacao().toString());
					
					AghWFFluxo fluxo = new AghWFFluxo();
					fluxo.setSeq(requisicaoDetalhe.getFluxo().getSeq());
					
					etapa.setTemplateEtapa(templateEtapa);
					etapa.setFluxo(fluxo);
					
					List<AghWFHistoricoExecucao> autorizados = blocoCirurgicoOpmesFacade.buscarHistExecutoresAutorizacao(etapa);
					RapServidores servidor = servidorLogadoFacade.obterServidorLogado();
					
					if(autorizados != null){
						if(autorizados.size() > 0){
							for (AghWFHistoricoExecucao item : autorizados) {
								servidoresAutorizados.add(item.getExecutor().getRapServidor());
							}
							
							if(servidoresAutorizados.contains(servidor)){
								this.setHabilitaAutorizacao(Boolean.FALSE);
							}
							
						}
					}
							
					
				}
				if (DominioSituacaoRequisicao.AUTORIZADA.equals(requisicaoDetalhe.getSituacao()) || 
						DominioSituacaoRequisicao.CONCLUIDA.equals(requisicaoDetalhe.getSituacao())) {
					this.setHabilitaBotaoVisualizarAutorizacao(Boolean.TRUE);
				}
			}
			
			limpaValorCampos();
			
			//Chama a pesquisa ao abrir a tela, para vir já todos os materiais.
			pesquisar();
					
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		}
	
	}
	
	public void consultarHistorico(){
		this.listaHistoricoVO =	this.blocoCirurgicoOpmesFacade.consultarHistorico(fluxoSeq);
		
		if(this.listaHistoricoVO.isEmpty()){
			this.apresentarMsgNegocio(Severity.ERROR, "M01_SEM_REGISTROS");
		}else{
			openDialog("modalConsultarHistoricoProcessoWG");
		}
	
	}
		
	private MbcRequisicaoOpmes obterDetalhesRequisicao(Short requisicaoSeq){
		return this.blocoCirurgicoOpmesFacade.obterDetalhesRequisicao(requisicaoSeq);
	}
	
	public void pesquisar(){

		limpaValorCampos();
		
		this.listaBind = new ArrayList<MbcItensRequisicaoOpmesVO>();
		this.listaMateriaisVO = new ArrayList<ListaMateriaisRequisicaoOpmesVO>();
		
		listaBind = this.blocoCirurgicoOpmesFacade.pesquisarMateriaisRequisicao(itemRequisicao.getRequisicaoSeq(), this.compativel, this.licitado);
		
		if(listaBind.isEmpty()){
			this.apresentarMsgNegocio(Severity.ERROR, "MSG_ERRO_CONSULTA_SEM_REGISTROS_01");
		}
		
		//this.listaMateriaisVO = this.blocoCirurgicoOpmesFacade.pesquisarListaMateriaisRequisicaoOpmes(listaBind);
		this.listaMateriaisVO = this.blocoCirurgicoOpmesFacade.pesquisarListaMateriaisRequisicaoOpmes(listaBind);
		this.setTotalCompativel(this.blocoCirurgicoOpmesFacade.calculaCompatibilidade(this.listaMateriaisVO, this.totalCompativel));
		this.setTotalIncompativel(this.blocoCirurgicoOpmesFacade.calculaIncompatibilidade(this.listaMateriaisVO, this.totalIncompativel));
		StringBuffer sb = new StringBuffer();
		this.blocoCirurgicoOpmesFacade.montarDescricaoIncompatibilidade(sb,this.listaBind);
		this.setIncompatibilidade(sb.toString());
		
	}
	
	public void download(ListaMateriaisRequisicaoOpmesVO item){
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletResponse resp = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
	    resp.setContentType("application/zip, application/octet-stream");
	    resp.setHeader("Content-Disposition", "attachment;filename=" + "arquivo_orcamento_opme_.zip");
	    try {
			resp.getOutputStream().write(item.getAnexoOrcamento());
			context.responseComplete();
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	public String voltar(){
		return VOLTAR_TELA_ACOMPANHAMENTO;
	}
	
	public void abreObservacaoAutorizacao() {
		abreObservacaoAutorizacao = Boolean.TRUE;
		observacaoAutorizacao = "";
		openDialog("modalObservacaoAprovacaoWG");
	}
	
	public void abreJustificativaNegacao() {
		abreJustificativaNegacao = Boolean.TRUE;
		justificativaNegacao = "";
		openDialog("modalJustificativaNegacaoWG");
	}
	
	public void cancelarObservacaoAutorizacao() {
		abreObservacaoAutorizacao = Boolean.FALSE;
		this.setObservacaoAutorizacao(null);
		closeDialog("modalObservacaoAprovacaoWG");
	}
	
	public String autorizar() {
		try {
			
			RapServidores servidor = this.getServidorLogado();
			AghWFEtapa etapa = this.getEtapaPorSeq(this.itemRequisicao.getEtapaSeq());
			this.getBlocoCirurgicoOpmesFacade().executarEtapaFluxoAutorizacaoOPMEs(servidor, etapa, observacaoAutorizacao);
			this.setRequisicaoSeqAutorizada(this.itemRequisicao.getRequisicaoSeq());
			this.apresentarMsgNegocio(Severity.INFO, "MSG_REQUISICAO_AUTORIZADA");		
			abreObservacaoAutorizacao = Boolean.FALSE;
			this.setHabilitaAutorizacao(Boolean.FALSE);
			return ACOMPANHAR_PROCESSO_AUTORIZACAO_OPMS;
			
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
		return null;
	}
		
	public String naoAutorizar(){
		
		if (justificativaNegacao ==  null || justificativaNegacao.isEmpty()) {
			this.apresentarMsgNegocio(Severity.ERROR, "CAMPO_OBRIGATORIO", " Justificativa");
			return null;
		}
		
		try {
			AghWFEtapa etapa = this.getEtapaPorSeq(this.itemRequisicao.getEtapaSeq());
			this.blocoCirurgicoOpmesFacade.rejeitarEtapaFluxoAutorizacaoOPMEs(etapa, justificativaNegacao);
			abreJustificativaNegacao = Boolean.FALSE;
			closeDialog("modalJustificativaNegacaoWG");
			this.apresentarMsgNegocio(Severity.ERROR, "MSG_REQUISICAO_NAO_AUTORIZADA");
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
		return ACOMPANHAR_PROCESSO_AUTORIZACAO_OPMS;
	}
	
	private void limpaValorCampos(){
		listaBind = null;
		listaMateriaisVO = null;
		totalIncompativel = 0.00;
		totalCompativel = 0.00;
	}
	
	public String visualizarAutorizacao() {
		return VISUALIZA_AUTORIZACAO;
	}
	
	public void cancelarObservacaoNaoAutorizacao(){
		abreJustificativaNegacao = Boolean.FALSE;
		this.setJustificativaNegacao(null);
		closeDialog("modalJustificativaNegacaoWG");
	}
	
	public void getBindListaPesquisada() {
		if (listaPesquisada != null && listaPesquisadaClone != null) {
			for (MbcOpmesVO vo : listaPesquisada) {
				MbcOpmesVO clone = new MbcOpmesVO();
				if (vo.getMaterial() != null) {
					clone.setCodigoMaterial(vo.getMaterial().getCodigo().intValue());
				}
				if (vo.getCodTabela() != null) {
					clone.setCodTabela(vo.getCodTabela().longValue());
				}
				clone.setItemSeq(vo.getItensRequisicaoOpmes().getSeq().shortValue());
				clone.setQtdeSol(vo.getQtdeSol().intValue());
				listaPesquisadaClone.add(clone);
			}
		}
	}
	
	public void carregaGridOpme(){
		listaPesquisada = null;
		MbcRequisicaoOpmes requisicaoOpmes = blocoCirurgicoOpmesFacade.carregaRequisicao(requisicaoDetalhe.getAgendas());
		MbcOpmesListaGrupoExcludente voTransporte = blocoCirurgicoOpmesFacade.carregaGrid(requisicaoOpmes);
		listaPesquisada = voTransporte.getListaPesquisada();
		//listaGrupoExcludente = voTransporte.getListaGrupoExcludente();
		//listaExcluidos = new ArrayList<MbcItensRequisicaoOpmes>();

		getBindListaPesquisada();

		// setCalculaListaSemAtualizar();
		blocoCirurgicoOpmesFacade.calculaQuantidades(listaPesquisada);
		// Conteúdo do método setAtualizaCompIncomp() incluído aqui, devido a
		// violação de PMD por execesso de métodos.
		//totalCompativel = blocoCirurgicoOpmesFacade.atualizaTotalCompativel(listaPesquisada);
		//StringBuffer incompatibilidadesEncontradas = new StringBuffer();
		//totalIncompativel = blocoCirurgicoOpmesFacade.atualizaIncompativel(requisicaoOpmes,incompatibilidadesEncontradas, listaPesquisada);
		//incompatibilidadesEncontrada = incompatibilidadesEncontradas.toString();
		blocoCirurgicoOpmesFacade.setCompatibilidadeGrupoExcludencia(listaPesquisada);
		
		
		// setCalculaListaSemAtualizar();
		blocoCirurgicoOpmesFacade.calculaQuantidades(listaPesquisada);
		// Conteúdo do método setAtualizaCompIncomp() incluído aqui, devido a
		// violação de PMD por execesso de métodos.
		blocoCirurgicoOpmesFacade.atualizaTotalCompativel(listaPesquisada);
		StringBuffer incompatibilidadesEncontradas = new StringBuffer();
		blocoCirurgicoOpmesFacade.atualizaIncompativel(requisicaoOpmes,incompatibilidadesEncontradas, listaPesquisada);
		
		blocoCirurgicoOpmesFacade.setCompatibilidadeGrupoExcludencia(listaPesquisada);

		// refatorar
		List<MbcOpmesVO> listaSemRedundancia = new ArrayList<MbcOpmesVO>();
		for (MbcOpmesVO vo : listaPesquisada) {
			if (vo.getVoQuebra() == null) {
				if (vo.getFilhos().size() > 1) {
					for (MbcOpmesVO filhoVO : vo.getFilhos()) {
						if (filhoVO.getUnidadeMaterial() == null) {
							vo.getFilhos().remove(filhoVO);
							break;
						}
					}
					List<MbcOpmesVO> filhos = new ArrayList<MbcOpmesVO>();
					for (MbcOpmesVO filhoVO : vo.getFilhos()) {
						if(!filhos.contains(filhoVO)){
							filhos.add(filhoVO);
						}
					}
					vo.setFilhos(filhos);
				}
				if(vo.getFilhos() != null){
					if(!vo.getFilhos().isEmpty()){
						listaSemRedundancia.add(vo);
					}
				}
				
			}
		}
		listaPesquisada = listaSemRedundancia;
		super.openDialog("modalConsultarGridWG");
	}
	
	private RapServidores getServidorLogado() throws ApplicationBusinessException{
		return this.registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(), new Date());
	}
	
	private AghWFEtapa getEtapaPorSeq(Integer etapaSeq){
		return this.blocoCirurgicoOpmesFacade.obterEtapaPorSeq(etapaSeq);
	}
	
	public IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}

	public void setBlocoCirurgicoFacade(IBlocoCirurgicoFacade blocoCirurgicoFacade) {
		this.blocoCirurgicoFacade = blocoCirurgicoFacade;
	}

	public IBlocoCirurgicoOpmesFacade getBlocoCirurgicoOpmesFacade() {
		return blocoCirurgicoOpmesFacade;
	}

	public void setBlocoCirurgicoOpmesFacade(IBlocoCirurgicoOpmesFacade blocoCirurgicoOpmesFacade) {
		this.blocoCirurgicoOpmesFacade = blocoCirurgicoOpmesFacade;
	}

	public RequisicoesOPMEsProcedimentosVinculadosVO getItemRequisicao() {
		return itemRequisicao;
	}

	public void setItemRequisicao(RequisicoesOPMEsProcedimentosVinculadosVO itemRequisicao) {
		this.itemRequisicao = itemRequisicao;
	}

	public ExecutorEtapaAtualVO getExecutorEtapaAtual() {
		return executorEtapaAtual;
	}

	public void setExecutorEtapaAtual(ExecutorEtapaAtualVO executorEtapaAtual) {
		this.executorEtapaAtual = executorEtapaAtual;
	}

	public Date getDataRequisicao() {
		return dataRequisicao;
	}

	public void setDataRequisicao(Date dataRequisicao) {
		this.dataRequisicao = dataRequisicao;
	}

	public RequerenteVO getRequerente() {
		return requerente;
	}

	public void setRequerente(RequerenteVO requerente) {
		this.requerente = requerente;
	}

	public String getEtapaAtualRequisicao() {
		return etapaAtualRequisicao;
	}

	public void setEtapaAtualRequisicao(String etapaAtualRequisicao) {
		this.etapaAtualRequisicao = etapaAtualRequisicao;
	}

	public String getProcedimentoPrincipal() {
		return procedimentoPrincipal;
	}

	public void setProcedimentoPrincipal(String procedimentoPrincipal) {
		this.procedimentoPrincipal = procedimentoPrincipal;
	}

	public String getProcedimentoSus() {
		return procedimentoSus;
	}

	public void setProcedimentoSus(String procedimentoSus) {
		this.procedimentoSus = procedimentoSus;
	}

	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public DominioSimNao getCompativel() {
		return compativel;
	}

	public void setCompativel(DominioSimNao compativel) {
		this.compativel = compativel;
	}

	public DominioSimNao getLicitado() {
		return licitado;
	}

	public void setLicitado(DominioSimNao licitado) {
		this.licitado = licitado;
	}

	public List<MbcItensRequisicaoOpmes> getListaMateriais() {
		return listaMateriais;
	}

	public void setListaMateriais(List<MbcItensRequisicaoOpmes> listaMateriais) {
		this.listaMateriais = listaMateriais;
	}

	public List<ListaMateriaisRequisicaoOpmesVO> getListaMateriaisVO() {
		return listaMateriaisVO;
	}

	public void setListaMateriaisVO(List<ListaMateriaisRequisicaoOpmesVO> listaMateriaisVO) {
		this.listaMateriaisVO = listaMateriaisVO;
	}

	public Double getTotalIncompativel() {
		return totalIncompativel;
	}

	public void setTotalIncompativel(Double totalIncompativel) {
		this.totalIncompativel = totalIncompativel;
	}

	public Double getTotalCompativel() {
		return totalCompativel;
	}

	public void setTotalCompativel(Double totalCompativel) {
		this.totalCompativel = totalCompativel;
	}

	public String getIncompatibilidade() {
		return incompatibilidade;
	}

	public void setIncompatibilidade(String incompatibilidade) {
		this.incompatibilidade = incompatibilidade;
	}

	public Boolean getHabilitaAutorizacao() {
		return habilitaAutorizacao;
	}

	public void setHabilitaAutorizacao(Boolean habilitaAutorizacao) {
		this.habilitaAutorizacao = habilitaAutorizacao;
	}

	public Short getRequisicaoSeqAutorizada() {
		return requisicaoSeqAutorizada;
	}

	public void setRequisicaoSeqAutorizada(Short requisicaoSeqAutorizada) {
		this.requisicaoSeqAutorizada = requisicaoSeqAutorizada;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}

	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public Boolean getHabilitaBotaoVisualizarAutorizacao() {
		return habilitaBotaoVisualizarAutorizacao;
	}

	public void setHabilitaBotaoVisualizarAutorizacao(
			Boolean habilitaBotaoVisualizarAutorizacao) {
		this.habilitaBotaoVisualizarAutorizacao = habilitaBotaoVisualizarAutorizacao;
	}
	
	public String getObservacaoAutorizacao() {
		return observacaoAutorizacao;
	}

	public void setObservacaoAutorizacao(String observacaoAutorizacao) {
		this.observacaoAutorizacao = observacaoAutorizacao;
	}
	
	public String getJustificativaNegacao() {
		return justificativaNegacao;
	}

	public void setJustificativaNegacao(String justificativaNegacao) {
		this.justificativaNegacao = justificativaNegacao;
	}
	
	public Boolean getAbreObservacaoAutorizacao() {
		return abreObservacaoAutorizacao;
	}

	public void setAbreObservacaoAutorizacao(Boolean abreObservacaoAutorizacao) {
		this.abreObservacaoAutorizacao = abreObservacaoAutorizacao;
	}
	
	public Boolean getAbreJustificativaNegacao() {
		return abreJustificativaNegacao;
	}

	public void setAbreJustificativaNegacao(Boolean abreJustificativaNegacao) {
		this.abreJustificativaNegacao = abreJustificativaNegacao;
	}
	
	public ListaMateriaisRequisicaoOpmesVO getItemListaSelecionado() {
		return itemListaSelecionado;
	}

	public void setItemListaSelecionado(ListaMateriaisRequisicaoOpmesVO itemListaSelecionado) {
		this.itemListaSelecionado = itemListaSelecionado;
	}

	public Integer getFluxoSeq() {
		return fluxoSeq;
	}

	public void setFluxoSeq(Integer fluxoSeq) {
		this.fluxoSeq = fluxoSeq;
	}

	public List<ConsultarHistoricoProcessoOpmeVO> getListaHistoricoVO() {
		return listaHistoricoVO;
	}

	public void setListaHistoricoVO(List<ConsultarHistoricoProcessoOpmeVO> listaHistoricoVO) {
		this.listaHistoricoVO = listaHistoricoVO;
	}

	public ConsultarHistoricoProcessoOpmeVO getRequisicoesOPMEsProcedimentosVinculadosSelecionado() {
		return requisicoesOPMEsProcedimentosVinculadosSelecionado;
	}

	public void setRequisicoesOPMEsProcedimentosVinculadosSelecionado(
			ConsultarHistoricoProcessoOpmeVO requisicoesOPMEsProcedimentosVinculadosSelecionado) {
		this.requisicoesOPMEsProcedimentosVinculadosSelecionado = requisicoesOPMEsProcedimentosVinculadosSelecionado;
	}

	public Date getDataProcedimento() {
		return dataProcedimento;
	}

	public void setDataProcedimento(Date dataProcedimento) {
		this.dataProcedimento = dataProcedimento;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public String getStrProntuario() {
		return strProntuario;
	}

	public void setStrProntuario(String strProntuario) {
		this.strProntuario = strProntuario;
	}

	public BigDecimal getValorUnit() {
		return valorUnit;
	}

	public void setValorUnit(BigDecimal valorUnit) {
		this.valorUnit = valorUnit;
	}

	public List<MbcOpmesVO> getListaPesquisada() {
		return listaPesquisada;
	}

	public void setListaPesquisada(List<MbcOpmesVO> listaPesquisada) {
		this.listaPesquisada = listaPesquisada;
	}

	public List<MbcOpmesVO> getListaPesquisadaClone() {
		return listaPesquisadaClone;
	}

	public void setListaPesquisadaClone(List<MbcOpmesVO> listaPesquisadaClone) {
		this.listaPesquisadaClone = listaPesquisadaClone;
	}

	public MbcRequisicaoOpmes getRequisicaoDetalhe() {
		return requisicaoDetalhe;
	}

	public void setRequisicaoDetalhe(MbcRequisicaoOpmes requisicaoDetalhe) {
		this.requisicaoDetalhe = requisicaoDetalhe;
	}
	
}
