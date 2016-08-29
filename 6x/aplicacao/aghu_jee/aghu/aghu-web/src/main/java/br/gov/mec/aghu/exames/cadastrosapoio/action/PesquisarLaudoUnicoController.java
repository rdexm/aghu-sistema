package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioFuncaoPatologista;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSimNaoNaoAplicavel;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoImpressaoLaudo;
import br.gov.mec.aghu.exames.action.ConsultarResultadosNotaAdicionalController;
import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.exames.patologia.vo.AelItemSolicitacaoExameLaudoUnicoVO;
import br.gov.mec.aghu.model.AelAnatomoPatologico;
import br.gov.mec.aghu.model.AelConfigExLaudoUnico;
import br.gov.mec.aghu.model.AelExameAp;
import br.gov.mec.aghu.model.AelExtratoExameAp;
import br.gov.mec.aghu.model.AelNomenclaturaEspecs;
import br.gov.mec.aghu.model.AelNomenclaturaGenerics;
import br.gov.mec.aghu.model.AelOcorrenciaExameAp;
import br.gov.mec.aghu.model.AelPatologista;
import br.gov.mec.aghu.model.AelTopografiaAparelhos;
import br.gov.mec.aghu.model.AelTopografiaSistemas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAelApXPatologiaAghu;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;




public class PesquisarLaudoUnicoController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -7102047377830610820L;

	private static final String RELATORIO_LAUDO_UNICO_PDF = "exames-consultarResultadoNotaAdicional";

	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;
	
	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@Inject
	private ConsultarResultadosNotaAdicionalController consultarResultadosNotaAdicionalController;
	
	private AelNomenclaturaGenerics nomenclaturaGenerica;
	private AelNomenclaturaEspecs nomenclaturaEspecifica;
	
	private AelTopografiaSistemas topografiaSistema;
	private AelTopografiaAparelhos topografiaAparelho;

	private AelPatologista residenteResp;
	private AelPatologista patologistaResp;
	
	private Date dtDe;
	private Date dtAte;
	private DominioSimNao neoplasiaMaligna;
	private DominioSimNaoNaoAplicavel margemComprometida;
	private DominioSimNao biopsia;
	

	private String nroApSelecionado;
	private Boolean apresentarOcorrencias = Boolean.FALSE; 

	private Integer vAelApXPatologiaAghuCount;
	private List<AelExtratoExameAp> extratosExameAP;
	private List<AelOcorrenciaExameAp> ocorrencias;
	
	private AelConfigExLaudoUnico exame;
	
	@Inject @Paginator
	private DynamicDataModel<VAelApXPatologiaAghu> dataModel;
	
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	/**
	 * Inicializa valores defaults dos principais campos da tela
	 */
	public void iniciar(){
	 

		dataModel.setDefaultMaxRow(30);
		
		extratosExameAP = null;

		if(Boolean.TRUE.equals(dataModel.getPesquisaAtiva())){
			return;
		}
		
		if(dtDe == null && dtAte == null){
			inicializarDatas();
		}

		if(residenteResp == null && patologistaResp == null){
			RapServidores servidorLogado = null;
			try {
				servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado());
			} catch (ApplicationBusinessException e) {
				servidorLogado = null;
			}
			
			if(residenteResp == null){
				residenteResp = examesPatologiaFacade.obterAelPatologistaAtivoPorServidorEFuncao(servidorLogado, DominioFuncaoPatologista.R);
			}
			
			if(patologistaResp == null){
				patologistaResp = examesPatologiaFacade.obterAelPatologistaAtivoPorServidorEFuncao(servidorLogado, DominioFuncaoPatologista.C, DominioFuncaoPatologista.P);
			}
		}
		
		pesquisar();
	
	}

	private void inicializarDatas() {
		try {
			final Integer periodoDias = (this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_PERIODO_REFERENCIA_LISTA_EXAMES_AP)
												.getVlrNumerico().intValue() * -1); // Negativo para retroceder a data.
			
			dtAte = new Date();
			dtDe = DateUtil.adicionaDias(new Date(), periodoDias);
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void pesquisar() {
		extratosExameAP = null;
		
		if(DateUtil.validaDataMenor(dtAte, dtDe)){
			this.apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_LISTA_REALIZACAO_EXAMES_PATOLOGIA_PERIODO_INVALIDO");
			return;
		}
		this.setApresentarOcorrencias(false);
		dataModel.reiniciarPaginator();
	}

	public void limpar() {
		dataModel.limparPesquisa();
		extratosExameAP = null;
		
		nomenclaturaGenerica = null;
		nomenclaturaEspecifica = null;
		
		topografiaSistema = null;
		topografiaAparelho = null;

		residenteResp = null;
		patologistaResp = null;
		
		dtDe = null;
		dtAte = null;
		neoplasiaMaligna = null;
		margemComprometida = null;
		biopsia = null;
		
		residenteResp = null;
		patologistaResp = null;
		
		inicializarDatas();
		this.setApresentarOcorrencias(false);
	}
	
	public List<AelPatologista> pesquisarPatologistaResponsavel(String filtro){
		return this.returnSGWithCount(examesPatologiaFacade.listarPatologistasPorCodigoNomeFuncao((String) filtro, DominioFuncaoPatologista.P, DominioFuncaoPatologista.C),pesquisarPatologistaResponsavelCount(filtro));
	}
	
	public Long pesquisarPatologistaResponsavelCount(String filtro){
		return examesPatologiaFacade.listarPatologistasPorCodigoNomeFuncaoCount((String) filtro, DominioFuncaoPatologista.P, DominioFuncaoPatologista.C);
	}
	
	public List<AelPatologista> pesquisarResidenteResponsavel(String filtro){
		return this.returnSGWithCount(examesPatologiaFacade.listarPatologistasPorCodigoNomeFuncao((String) filtro, DominioFuncaoPatologista.R),pesquisarResidenteResponsavelCount(filtro));
	}

	public Long pesquisarResidenteResponsavelCount(String filtro){
		return examesPatologiaFacade.listarPatologistasPorCodigoNomeFuncaoCount((String) filtro, DominioFuncaoPatologista.R);
	}

	public List<AelNomenclaturaGenerics> pesquisarAelNomenclaturaGenerics(final String filtro){
		return this.returnSGWithCount(examesPatologiaFacade.pesquisarAelNomenclaturaGenerics((String) filtro, DominioSituacao.A),pesquisarAelNomenclaturaGenericsCount(filtro)); 
	}

	public Long pesquisarAelNomenclaturaGenericsCount(final String filtro){
		return examesPatologiaFacade.pesquisarAelNomenclaturaGenericsCount((String) filtro, DominioSituacao.A);
	}
	
	public void limparNomenclaturaEspecifica(){
		nomenclaturaEspecifica = null;
	}
	
	public List<AelNomenclaturaEspecs> pesquisarAelNomenclaturaEspecs(final String filtro){
		return this.returnSGWithCount(examesPatologiaFacade.pesquisarAelNomenclaturaEspecs((String) filtro, DominioSituacao.A, nomenclaturaGenerica),pesquisarAelNomenclaturaEspecsCount(filtro));
	}

	public Long pesquisarAelNomenclaturaEspecsCount(final String filtro){
		return examesPatologiaFacade.pesquisarAelNomenclaturaEspecsCount((String) filtro, DominioSituacao.A, nomenclaturaGenerica);
	}
	
	public List<AelTopografiaSistemas> pesquisarAelTopografiaSistemas(final String filtro){
		return this.returnSGWithCount(examesPatologiaFacade.pesquisarAelTopografiaSistemas((String)filtro, DominioSituacao.A),pesquisarAelTopografiaSistemasCount(filtro));
	}

	public Long pesquisarAelTopografiaSistemasCount(final String filtro){
		return examesPatologiaFacade.pesquisarAelTopografiaSistemasCount((String) filtro, DominioSituacao.A);
	}
	
	public void limparTopografiaAparelho(){
		topografiaAparelho = null;
	}
	
	public List<AelTopografiaAparelhos> pesquisarAelTopografiaAparelhos(final String filtro){
		return this.returnSGWithCount(examesPatologiaFacade.pesquisarAelTopografiaAparelhos((String) filtro, DominioSituacao.A, topografiaSistema),pesquisarAelTopografiaAparelhosCount(filtro));
	}

	public Long pesquisarAelTopografiaAparelhosCount(final String filtro){
		return examesPatologiaFacade.pesquisarAelTopografiaAparelhosCount((String) filtro, DominioSituacao.A, topografiaSistema);
	}
	
	@Override
	public List<VAelApXPatologiaAghu> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {		
		List<VAelApXPatologiaAghu> result = null;
		try {
			result = examesPatologiaFacade.pesquisarVAelApXPatologiaAghu( firstResult, maxResult, orderProperty, asc, 
																							         nomenclaturaGenerica, nomenclaturaEspecifica,
																							         topografiaSistema, topografiaAparelho,
																							         residenteResp, patologistaResp,
																							         neoplasiaMaligna, margemComprometida, biopsia,
																							         dtDe, dtAte, exame );
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return result;		
	}
	
	@Override
	public Long recuperarCount() {
		vAelApXPatologiaAghuCount = examesPatologiaFacade.pesquisarVAelApXPatologiaAghuCount( nomenclaturaGenerica, nomenclaturaEspecifica, 
				  topografiaSistema, topografiaAparelho,
				  residenteResp, patologistaResp, 
				  neoplasiaMaligna, margemComprometida, biopsia,
				  dtDe, dtAte, exame);

		return (vAelApXPatologiaAghuCount != null ? vAelApXPatologiaAghuCount.longValue() : 0L);
	}
	
	public void listarAelExtratoExameApPorLuxSeq(Integer lu2Seq, Long nroAp){
		assert nroAp != null : "Número do exame não definido";
		extratosExameAP = null;
		// TODO REVISAR! PARECE ESTAR PASSANDO PARAMETROS ERRADOS, NA ORDEM ERRADA.
		final AelAnatomoPatologico aelAP = examesPatologiaFacade.obterAelAnatomoPatologicoByNumeroAp(nroAp, lu2Seq);
        final AelExameAp aelExameAp = examesPatologiaFacade.obterAelExameApPorAelAnatomoPatologicos(aelAP);
        extratosExameAP = examesPatologiaFacade.listarAelExtratoExameApPorLuxSeq(aelExameAp.getSeq());
	}
	
	public String visualizarRelatorio(Long luxSeq) {
		geraLaudo(luxSeq, false, "exames-pesquisarLaudoUnico");
		
		return RELATORIO_LAUDO_UNICO_PDF;
	}
	
	public void imprimirRelatorio(Long luxSeq){
		geraLaudo(luxSeq, true, null);
	}
	
	private void geraLaudo(Long luxSeq, Boolean directPrint, String voltarPara) {
		Map<Integer, Vector<Short>> solicitacoes = new HashMap<Integer, Vector<Short>>();
		AelExameAp exameAP = examesPatologiaFacade.obterAelExameApPorChavePrimaria(luxSeq);
		final AelItemSolicitacaoExameLaudoUnicoVO itemVo = examesPatologiaFacade.obterAelItemSolicitacaoExameLaudoUnicoVO(exameAP, false);
		
		Integer soeSeq = itemVo.getSoeSeq();
		Short seqp = itemVo.getSeqp();
		solicitacoes.put(soeSeq, new Vector<Short>());
		solicitacoes.get(soeSeq).add(seqp);
		consultarResultadosNotaAdicionalController.setListaDesenhosMascarasExamesVO(null);
		consultarResultadosNotaAdicionalController.setIsHist(false);
		consultarResultadosNotaAdicionalController.setSolicitacoes(solicitacoes);
		consultarResultadosNotaAdicionalController.setExibeBotoes(false);
		consultarResultadosNotaAdicionalController.setVoltarPara(voltarPara);
		consultarResultadosNotaAdicionalController.setTipoLaudo(DominioTipoImpressaoLaudo.LAUDO_SAMIS);
		consultarResultadosNotaAdicionalController.setIsDirectPrint(directPrint);
		consultarResultadosNotaAdicionalController.inicio();
		if (directPrint) {
			consultarResultadosNotaAdicionalController.directPrint();
		}
	}

	
	public void ocorrenciasExames(Integer lu2Seq, Long nroAp) {
		assert nroAp != null : "Número do exame não definido";
		this.setApresentarOcorrencias(false);
		// TODO REVISAR! PARECE ESTAR PASSANDO PARAMETROS ERRADOS, NA ORDEM ERRADA.
		final AelAnatomoPatologico aelAP = examesPatologiaFacade.obterAelAnatomoPatologicoByNumeroAp(nroAp, lu2Seq);
		final AelExameAp aelExameAp = examesPatologiaFacade.obterAelExameApPorAelAnatomoPatologicos(aelAP);
		this.setOcorrencias(this.examesPatologiaFacade.buscarAelOcorrenciaExameApPorSeqExameAp(aelExameAp.getSeq()));
		if(this.getOcorrencias() == null || this.getOcorrencias().isEmpty()) {
			this.apresentarMsgNegocio(Severity.ERROR, "MSG_NENHUMA_OCORRENCIA_ENCONTRADA");
		} else {
			this.setApresentarOcorrencias(true);
		}
	}
	
	public List<AelConfigExLaudoUnico> getPesquisarAelConfigExLaudoUnico(String value){
		return examesPatologiaFacade.pesquisarAelConfigExLaudoUnico(AelConfigExLaudoUnico.Fields.NOME.toString(), value);
	}
	
	public Long getPesquisarAelConfigExLaudoUnicoCount(String value){
		return examesPatologiaFacade.pesquisarAelConfigExLaudoUnicoCount(AelConfigExLaudoUnico.Fields.NOME.toString(), value);
	}
	
	public void fechaModais() {
		apresentarOcorrencias = Boolean.FALSE;
		extratosExameAP = null;
	}
	
	public AelPatologista getPatologistaResp() {
		return patologistaResp;
	}

	public void setPatologistaResp(AelPatologista patologistaResp) {
		this.patologistaResp = patologistaResp;
	}

	public AelPatologista getResidenteResp() {
		return residenteResp;
	}

	public void setResidenteResp(AelPatologista residenteResp) {
		this.residenteResp = residenteResp;
	}

	public Date getDtAte() {
		return dtAte;
	}

	public void setDtAte(Date dtAte) {
		this.dtAte = dtAte;
	}

	public Date getDtDe() {
		return dtDe;
	}

	public void setDtDe(Date dtDe) {
		this.dtDe = dtDe;
	}

	public Integer getvAelApXPatologiaAghuCount() {
		return vAelApXPatologiaAghuCount;
	}

	public void setvAelApXPatologiaAghuCount(Integer vAelApXPatologiaAghuCount) {
		this.vAelApXPatologiaAghuCount = vAelApXPatologiaAghuCount;
	}

	public DominioSimNao getNeoplasiaMaligna() {
		return neoplasiaMaligna;
	}

	public void setNeoplasiaMaligna(DominioSimNao neoplasiaMaligna) {
		this.neoplasiaMaligna = neoplasiaMaligna;
	}

	public DominioSimNaoNaoAplicavel getMargemComprometida() {
		return margemComprometida;
	}

	public void setMargemComprometida(DominioSimNaoNaoAplicavel margemComprometida) {
		this.margemComprometida = margemComprometida;
	}

	public AelNomenclaturaGenerics getNomenclaturaGenerica() {
		return nomenclaturaGenerica;
	}

	public void setNomenclaturaGenerica(AelNomenclaturaGenerics nomenclaturaGenerica) {
		this.nomenclaturaGenerica = nomenclaturaGenerica;
	}

	public AelNomenclaturaEspecs getNomenclaturaEspecifica() {
		return nomenclaturaEspecifica;
	}

	public void setNomenclaturaEspecifica(
			AelNomenclaturaEspecs nomenclaturaEspecifica) {
		this.nomenclaturaEspecifica = nomenclaturaEspecifica;
	}

	public AelTopografiaSistemas getTopografiaSistema() {
		return topografiaSistema;
	}

	public void setTopografiaSistema(AelTopografiaSistemas topografiaSistema) {
		this.topografiaSistema = topografiaSistema;
	}

	public AelTopografiaAparelhos getTopografiaAparelho() {
		return topografiaAparelho;
	}

	public void setTopografiaAparelho(AelTopografiaAparelhos topografiaAparelho) {
		this.topografiaAparelho = topografiaAparelho;
	}

	public String getNroApSelecionado() {
		return nroApSelecionado;
	}

	public void setNroApSelecionado(String nroApSelecionado) {
		this.nroApSelecionado = nroApSelecionado;
	}

	public List<AelExtratoExameAp> getExtratosExameAP() {
		return extratosExameAP;
	}

	public void setExtratosExameAP(List<AelExtratoExameAp> extratosExameAP) {
		this.extratosExameAP = extratosExameAP;
	}
	
	public void setApresentarOcorrencias(Boolean apresentarOcorrencias) {
		this.apresentarOcorrencias = apresentarOcorrencias;
	}

	public Boolean getApresentarOcorrencias() {
		return apresentarOcorrencias;
	}

	public void setOcorrencias(List<AelOcorrenciaExameAp> ocorrencias) {
		this.ocorrencias = ocorrencias;
	}

	public List<AelOcorrenciaExameAp> getOcorrencias() {
		return ocorrencias;
	} 
	public DynamicDataModel<VAelApXPatologiaAghu> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<VAelApXPatologiaAghu> dataModel) {
	 this.dataModel = dataModel;
	}

	public DominioSimNao getBiopsia() {
		return biopsia;
	}

	public void setBiopsia(DominioSimNao biopsia) {
		this.biopsia = biopsia;
	}

	public AelConfigExLaudoUnico getExame() {
		return exame;
	}

	public void setExame(AelConfigExLaudoUnico exame) {
		this.exame = exame;
	}
	
	
}