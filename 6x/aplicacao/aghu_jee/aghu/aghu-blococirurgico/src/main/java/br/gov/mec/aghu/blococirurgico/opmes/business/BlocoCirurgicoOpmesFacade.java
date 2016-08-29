package br.gov.mec.aghu.blococirurgico.opmes.business;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.blococirurgico.business.MbcAgendasON;
import br.gov.mec.aghu.blococirurgico.dao.AghWFEtapaDAO;
import br.gov.mec.aghu.blococirurgico.dao.AghWFExecutorDAO;
import br.gov.mec.aghu.blococirurgico.dao.AghWFHistoricoExecucaoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcRequisicaoOpmesDAO;
import br.gov.mec.aghu.blococirurgico.vo.ConsultaPreparaOPMEFiltroVO;
import br.gov.mec.aghu.blococirurgico.vo.ConsultarHistoricoProcessoOpmeVO;
import br.gov.mec.aghu.blococirurgico.vo.DemoFinanceiroOPMEVO;
import br.gov.mec.aghu.blococirurgico.vo.DescricaoCirurgicaMateriaisConsumidosVO;
import br.gov.mec.aghu.blococirurgico.vo.ExecutorEtapaAtualVO;
import br.gov.mec.aghu.blococirurgico.vo.GrupoExcludenteVO;
import br.gov.mec.aghu.blococirurgico.vo.InfoProcdCirgRequisicaoOPMEVO;
import br.gov.mec.aghu.blococirurgico.vo.ListaMateriaisRequisicaoOpmesVO;
import br.gov.mec.aghu.blococirurgico.vo.MateriaisProcedimentoOPMEVO;
import br.gov.mec.aghu.blococirurgico.vo.MaterialHospitalarVO;
import br.gov.mec.aghu.blococirurgico.vo.MbcItensRequisicaoOpmesVO;
import br.gov.mec.aghu.blococirurgico.vo.MbcOpmesListaGrupoExcludente;
import br.gov.mec.aghu.blococirurgico.vo.MbcOpmesVO;
import br.gov.mec.aghu.blococirurgico.vo.SolicitacaoCompraMaterialVO;
import br.gov.mec.aghu.blococirurgico.vo.VisualizarAutorizacaoOpmeVO;
import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.estoque.vo.MaterialOpmeVO;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AghWFEtapa;
import br.gov.mec.aghu.model.AghWFExecutor;
import br.gov.mec.aghu.model.AghWFFluxo;
import br.gov.mec.aghu.model.AghWFHistoricoExecucao;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcItensRequisicaoOpmes;
import br.gov.mec.aghu.model.MbcMateriaisItemOpmes;
import br.gov.mec.aghu.model.MbcRequisicaoOpmes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;


@Modulo(ModuloEnum.BLOCO_CIRURGICO)
@Stateless
public class BlocoCirurgicoOpmesFacade extends BaseFacade implements IBlocoCirurgicoOpmesFacade {

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AghWFEtapaDAO aghWFEtapaDAO;

	@Inject
	private AghWFHistoricoExecucaoDAO aghWFHistoricoExecucaoDAO;

	@Inject
	private AghWFExecutorDAO aghWFExecutorDAO;

	@Inject
	private MbcRequisicaoOpmesDAO mbcRequisicaoOpmesDAO;
	
	@EJB
	private ConsultaPreparaOPMEON consultaPreparaOPMEON;

	@EJB
	private OPMEPortalAgendamentoON oPMEPortalAgendamentoON;

	@EJB
	private RequisicaoAutorizacaoOPMEsRN requisicaoAutorizacaoOPMEsRN;

	@EJB
	private WorkflowAutorizacaoRequisicaoOPMEsRN workflowAutorizacaoRequisicaoOPMEsRN;

	@EJB
	private OPMEPortalAgendamentoRN oPMEPortalAgendamentoRN;

	@EJB
	private DescricaoCirurgicaMateriaisConsumidosRN descricaoCirurgicaMateriaisConsumidosRN;

	@EJB
	private VisualizarAutorizacaoOpmeON visualizarAutorizacaoOpmeON;
	
	@EJB
	private JournalOpmeRN journalOpmeRN;
	
	@EJB
	private MbcAgendasON mbcAgendasON;
	
	@EJB
	private ListagemMateriaisOPMERN listagemMateriaisOPMERN;

	private static final long serialVersionUID = -8703743820880576178L;
	
	private WorkflowAutorizacaoRequisicaoOPMEsRN getWorkflowAutorizacaoRequisicaoOPMEsRN(){
		return workflowAutorizacaoRequisicaoOPMEsRN;
	}
	@Override
	public MbcItensRequisicaoOpmes criaRequisicaoAdicional(MbcRequisicaoOpmes requisicao, MaterialOpmeVO materialVO, String solicitacaoMaterial, Integer qtdeSolicitada) {
		return oPMEPortalAgendamentoON.criaPojosAdc(requisicao, materialVO, solicitacaoMaterial, qtdeSolicitada);
	}
	@Override
	public MbcOpmesVO adicionarOpms(MbcItensRequisicaoOpmes item) {
		return oPMEPortalAgendamentoON.adicionar(item);
	}
	
	@Override
	public List<FatItensProcedHospitalar> consultarProcedimentoSUSVinculadoProcedimentoInterno(
			Integer pciSeq, Object param) {
		return oPMEPortalAgendamentoRN.consultarProcedimentoSUSVinculadoProcedimentoInterno(pciSeq, param);
	}
	
	@Override
	public MbcOpmesListaGrupoExcludente consultaItensProcedimento(Integer pciSeq, FatItensProcedHospitalar procedimentoSus, MbcRequisicaoOpmes requisicaoOpmes, Date dtAgenda) {
		return listagemMateriaisOPMERN.consultaItensProcedimento(pciSeq, procedimentoSus, requisicaoOpmes,dtAgenda);
	}

	@Override
	public MbcRequisicaoOpmes obterRequisicaoOriginal(Short seq) {
		return getMbcRequisicaoOpmesDAO().obterRequisicaoOriginal(seq);
	}
	
	@Override
	public boolean verificaAlteracoes(MbcRequisicaoOpmes requisicaoOpmes, MbcRequisicaoOpmes old) throws ApplicationBusinessException {
		return oPMEPortalAgendamentoRN.verificaAlteracoes(requisicaoOpmes, old);
	}
	
	@Override
	public MbcOpmesListaGrupoExcludente carregaGrid(MbcRequisicaoOpmes requisicaoOpmes) {
		return listagemMateriaisOPMERN.carregaGrid(requisicaoOpmes);
	}
	
	@Override
	public void concluirRequisicaoOpmes(MbcAgendas agenda, AipPacientes paciente, AghUnidadesFuncionais unidadeFuncional, MbcRequisicaoOpmes requisicaoOpmes, boolean persist) throws ApplicationBusinessException {
		oPMEPortalAgendamentoRN.concluirRequisicaoOpmes(agenda, paciente, unidadeFuncional, requisicaoOpmes, persist);
	}
	
	@Override
	public Integer verificarPrazoAgenda(MbcAgendas agenda,
			MbcRequisicaoOpmes requisicaoOpmes) throws ApplicationBusinessException {
		return oPMEPortalAgendamentoRN.verificarPrazoAgenda(agenda, requisicaoOpmes);
	}
	
	@Override
	public void gravaRequisicao(MbcRequisicaoOpmes requisicaoOpmes) {
		oPMEPortalAgendamentoRN.persistRequisicao(requisicaoOpmes, true);
	}

	@Override
	public void gravaRequisicaoFull(MbcRequisicaoOpmes requisicaoOpmes, List<MbcItensRequisicaoOpmes> itensExcluidos, List<MbcOpmesVO> listaClone, Boolean zeraFluxo) throws ApplicationBusinessException {
		oPMEPortalAgendamentoRN.persistirRequisicaoOpme(requisicaoOpmes, itensExcluidos, listaClone,zeraFluxo);
	}
	
	@Override
	public List<Integer> buscaDadosProcessoAutorizacaoOpme() throws ApplicationBusinessException {
		return oPMEPortalAgendamentoON.buscaDadosProcessoAutorizacaoOpme();
	}
	
	@Override
	public void calculaQuantidade(MbcOpmesVO opmeVO) {
		oPMEPortalAgendamentoON.calculaQuantidadeAutorizada(opmeVO);
	}
	@Override
	public void solicitaMaterial(MbcOpmesVO vo, int quantidade, List<MbcOpmesVO> listaPesquisada, FatItensProcedHospitalar procedimentoSus, List<GrupoExcludenteVO> listaGrupoExcludenteVO) {
		oPMEPortalAgendamentoON.solicitaMaterial(vo, quantidade, listaPesquisada, procedimentoSus, listaGrupoExcludenteVO);
	}
	@Override
	public void calculaQuantidades(List<MbcOpmesVO> listaPesquisada) {
		oPMEPortalAgendamentoON.calculaQuantidades(listaPesquisada);
	}
	@Override
	public Boolean verificarConvenio(MbcAgendas agenda) {
		return oPMEPortalAgendamentoRN.verificarConvenio(agenda);
	}
	@Override
	public MbcAgendas atualizarConvenio(MbcAgendas agenda) throws ApplicationBusinessException {
		return mbcAgendasON.atualizarConvenio(agenda);
	}
	
	@Override
	public Double atualizaIncompativel(MbcRequisicaoOpmes requisicaoOpmes, StringBuffer incompatibilidadesEncontradas, List<MbcOpmesVO> listaPesquisada) {
		return oPMEPortalAgendamentoON.atualizarIncompatibilidades(requisicaoOpmes, incompatibilidadesEncontradas, listaPesquisada);
	}
	
	@Override
	public void setCompatibilidadeGrupoExcludencia(List<MbcOpmesVO> listaPesquisada) {
		new OPMEPortalAgendamentoON().setCompatibilidadeGrupoExcludencia(listaPesquisada);
	}
	
	@Override
	public Double atualizaTotalCompativel(List<MbcOpmesVO> listaPesquisada) {
		return new OPMEPortalAgendamentoON().atualizaTotalCompativel(listaPesquisada);
	}
	
	@Override
	public Boolean permiteAlteracaoRequisicao(MbcRequisicaoOpmes requisicaoOpmes) {
		return oPMEPortalAgendamentoON.isPermiteAlteracaoRequisicao(requisicaoOpmes);
	}
	
	@Override
	public void atualizaSituacao(MbcRequisicaoOpmes requisicaoOpmes) {
		oPMEPortalAgendamentoRN.atualizarRequisicaoCompatibilidade(requisicaoOpmes);
	}
	
	@Override
	public MbcRequisicaoOpmes carregaRequisicao(MbcAgendas agenda) {
		return oPMEPortalAgendamentoRN.carregaRequisicao(agenda);
	}
	
	@Override
	public boolean validaExclusao(MbcOpmesVO opmesVO) throws ApplicationBusinessException {
		return oPMEPortalAgendamentoRN.validaExclusao(opmesVO);
	}
	
	@Override
	public List<MbcOpmesVO> excluir(MbcRequisicaoOpmes requisicaoOpmes, MbcOpmesVO opmesVO,	List<MbcOpmesVO> listaPesquisada) {
		return oPMEPortalAgendamentoRN.excluir(requisicaoOpmes, opmesVO, listaPesquisada);
	}
	
	@Override
	public MbcRequisicaoOpmes copiaRequisicao(MbcRequisicaoOpmes requisicaoOpmes) throws IllegalAccessException, InvocationTargetException {
		return oPMEPortalAgendamentoON.getCopiaRequisicao(requisicaoOpmes);
	}
	@Override
	public AghWFFluxo iniciarFluxoAutorizacaoOPMEs(RapServidores servidor, MbcRequisicaoOpmes requisicao) throws BaseException {
		return getWorkflowAutorizacaoRequisicaoOPMEsRN().iniciarFluxoAutorizacaoOPMEs(servidor, requisicao);
		
	}
	@Override
	public void rejeitarEtapaFluxoAutorizacaoOPMEs(AghWFEtapa etapa,
			String justificativa)
			throws BaseException {
		getWorkflowAutorizacaoRequisicaoOPMEsRN().rejeitarEtapaFluxoAutorizacaoOPMEs(etapa, justificativa);
		
	}
	
	@Override
	public void cancelarFluxoAutorizacaoOPMEs(RapServidores servidor, AghWFFluxo fluxo,	String justificativa, MbcRequisicaoOpmes requisicaoOpmes) throws BaseException {
		getWorkflowAutorizacaoRequisicaoOPMEsRN().cancelarFluxoAutorizacaoOPMEs(servidor, fluxo, justificativa, requisicaoOpmes);
	}
	
	@Override
	public void cancelarOpmeSemFluxo(MbcRequisicaoOpmes requisicaoOpmes) throws BaseException {
		getWorkflowAutorizacaoRequisicaoOPMEsRN().cancelarOpmeSemFluxo(requisicaoOpmes);
	}
	
	@Override
	public void executarEtapaFluxoAutorizacaoOPMEs(RapServidores servidor,
			AghWFEtapa etapa, String observacao) throws BaseException {
		getWorkflowAutorizacaoRequisicaoOPMEsRN().executarEtapaFluxoAutorizacaoOPMEs(servidor, etapa, observacao);
		
	}
	
	protected MbcRequisicaoOpmesDAO getMbcRequisicaoOpmesDAO(){
		return mbcRequisicaoOpmesDAO;
	}
	
	@Override
	public MbcRequisicaoOpmes obterDetalhesRequisicao(Short requisicaoSeq){
		return this.getMbcRequisicaoOpmesDAO().obterDetalhesRequisicao(requisicaoSeq);
	}
	
	@Override
	public List<MbcItensRequisicaoOpmesVO> pesquisarMateriaisRequisicao(Short requisicaoSeq, DominioSimNao compativel, DominioSimNao licitado){
		return this.getMbcRequisicaoOpmesDAO().pesquisarMateriaisRequisicao(requisicaoSeq, compativel, licitado);
	}
	
	private RequisicaoAutorizacaoOPMEsRN getRequisicaoAutorizacaoOPMEsRN(){
		return requisicaoAutorizacaoOPMEsRN;
	}
	
	@Override
	public List<ListaMateriaisRequisicaoOpmesVO> pesquisarListaMateriaisRequisicaoOpmes(List<MbcItensRequisicaoOpmesVO> listaMateriais){
		return this.getRequisicaoAutorizacaoOPMEsRN().pesquisarListaMateriaisRequisicaoOpmes(listaMateriais);
	}
	
	@Override
	public Double calculaCompatibilidade(List<ListaMateriaisRequisicaoOpmesVO> listaMateriais, Double totalCompativel){
		return this.getRequisicaoAutorizacaoOPMEsRN().calcularCompatibilidade(listaMateriais, totalCompativel);
	}

	@Override
	public Double calculaIncompatibilidade(List<ListaMateriaisRequisicaoOpmesVO> listaMateriais, Double totalIncompativel){
		return this.getRequisicaoAutorizacaoOPMEsRN().calcularIncompatibilidade(listaMateriais, totalIncompativel);
	}
	
	@Override
	public ExecutorEtapaAtualVO pesquisarExecutorEtapaAtualProcesso(Short requisicaoSeq, RapServidores servidorLogado){
		return this.getMbcRequisicaoOpmesDAO().pesquisarExecutorEtapaAtualProcesso(requisicaoSeq, servidorLogado);
	}
	
	protected AghWFHistoricoExecucaoDAO getAghWFHistoricoExecucaoDAO(){
		return aghWFHistoricoExecucaoDAO;
	}
	
	@Override
	public List<ConsultarHistoricoProcessoOpmeVO> consultarHistorico(Integer seqFluxo){
		return this.getAghWFHistoricoExecucaoDAO().consultarHistorico(seqFluxo);
	}
	
	@Override
	public List<MateriaisProcedimentoOPMEVO> consultarMaterialProcedimentoOPME(ConsultaPreparaOPMEFiltroVO filtroVO) throws BaseException {
		return getConsultaPreparaOPMEON().consultarMaterialProcedimentoOPME(filtroVO);
	}
	
	protected ConsultaPreparaOPMEON getConsultaPreparaOPMEON(){
		return consultaPreparaOPMEON;
	}
	
	protected AghWFEtapaDAO getAghWFEtapaDAO(){
		return aghWFEtapaDAO;
	}
	
	@Override
	public AghWFEtapa obterEtapaPorSeq(Integer etapaSeq){
		return this.getAghWFEtapaDAO().obterPorChavePrimaria(etapaSeq);
	}
	
	@Override
	public void montarDescricaoIncompatibilidade(StringBuffer sb, List<MbcItensRequisicaoOpmesVO> listaMateriais){
//		return this.getRequisicaoAutorizacaoOPMEsRN().montarDescricaoIncompatibilidade(listaMateriais);
		this.getRequisicaoAutorizacaoOPMEsRN().atualizaIncompativel(sb, listaMateriais);
	}
	
	protected AghWFExecutorDAO getAghWFExecutorDAO(){
		return aghWFExecutorDAO;
	}
	
	@Override
	public List<AghWFExecutor> obterExecutorPorCodigoFluxo(Integer fluxoSeq){
		return this.getAghWFExecutorDAO().obterExecutorPorCodigoFluxo(fluxoSeq);
	}
	
//	@Override
//	public void rejeitarEtapaFluxoAutorizacaoExecutoresOPMEs(AghWFEtapa etapa, String justificativa) throws ApplicationBusinessException {
//		getWorkflowAutorizacaoRequisicaoOPMEsRN().rejeitarEtapaFluxoAutorizacaoExecutoresOPMEs(etapa, servidor, justificativa);
//	}
	@Override 
	public InfoProcdCirgRequisicaoOPMEVO consultarInformacoesProcedimentoCirurgicoAtravesRequisicaoOPME(Short requisicaoSeqSelecionada) throws BaseException {
		return getConsultaPreparaOPMEON().consultarInformacoesProcedimentoCirurgicoAtravesRequisicaoOPME(requisicaoSeqSelecionada);
	}
	
	@Override 
	public InfoProcdCirgRequisicaoOPMEVO consultarInformacoesProcedimentoCirurgicoAtravesRequisicaoOPMESemSituacao(Short requisicaoSeqSelecionada) throws BaseException {
		return getConsultaPreparaOPMEON().consultarInformacoesProcedimentoCirurgicoAtravesRequisicaoOPMESemSituacao(requisicaoSeqSelecionada);
	}
	
	
	@Override
	public ScoSolicitacaoDeCompra persistirSolicitacaoCompraMaterial(SolicitacaoCompraMaterialVO solicitacaoCompraMaterialVO) throws BaseException {
		return getConsultaPreparaOPMEON().persistirSolicitacaoCompraMaterial(solicitacaoCompraMaterialVO);
	}
	
	private DescricaoCirurgicaMateriaisConsumidosRN getDescricaoCirurgicaMateriaisConsumidosRN(){
		return descricaoCirurgicaMateriaisConsumidosRN;
	}
	
	@Override
	public void concluirJustificativa(Short seqRequisicao, String justificativa){
		this.getDescricaoCirurgicaMateriaisConsumidosRN().concluirJustificativa(seqRequisicao, justificativa);
	}
	
	@Override
	public MbcItensRequisicaoOpmes validarItensRequisicao(MbcItensRequisicaoOpmes itemCorrente, MbcItensRequisicaoOpmes itemRequisicao){
		return oPMEPortalAgendamentoON.validarItensRequisicao(itemCorrente, itemRequisicao);
	}
	
	@Override
	public void validarConcluirMateriaisConsumidos(List<DescricaoCirurgicaMateriaisConsumidosVO> listaMateriaisConsumidos, String justificativa){
		this.getDescricaoCirurgicaMateriaisConsumidosRN().validarConcluirMateriaisConsumidos(listaMateriaisConsumidos, justificativa);
	}
	
	@Override
	public void concluirMateriaisConsumidos(List<DescricaoCirurgicaMateriaisConsumidosVO> itemLista){
		this.getDescricaoCirurgicaMateriaisConsumidosRN().concluirMateriaisConsumidos(itemLista, servidorLogadoFacade.obterServidorLogado().getUsuario());
	}
	
	@Override
	public void validaQtdeUtilizada(DescricaoCirurgicaMateriaisConsumidosVO itemMaterialConsumido){
		this.getDescricaoCirurgicaMateriaisConsumidosRN().validaQtdeUtilizada(itemMaterialConsumido);
	}
	
	@Override
	public String montarJustificativaMateriaisConsumidos(Short seqRequisicaoOpme){
		return this.getDescricaoCirurgicaMateriaisConsumidosRN().montarJustificativaMateriaisConsumidos(seqRequisicaoOpme);
	}
	
	protected VisualizarAutorizacaoOpmeON getVisualizarAutorizacaoOpmeON(){
		return visualizarAutorizacaoOpmeON;
	}
	
	@Override
	public List<VisualizarAutorizacaoOpmeVO> carregarVizualizacaoAutorizacaoOpme(Short seqRequisicao) throws ApplicationBusinessException{
		return this.getVisualizarAutorizacaoOpmeON().carregarVizualizacaoAutorizacaoOpme(seqRequisicao);
	}

	@Override
	public void validaJustificativa(MbcRequisicaoOpmes requisicaoOpmes) throws ApplicationBusinessException {
		oPMEPortalAgendamentoON.validaJustificativa(requisicaoOpmes);
	}
	
	@Override
	public String getEtapaAutorizacao(MbcRequisicaoOpmes requisicaoOpmes) {
		return oPMEPortalAgendamentoON.getEtapaProcessoAutorizacao(requisicaoOpmes);
	}
	
	@Override
	public MbcRequisicaoOpmes obterRequisicaoOpme(Short seq) {
		return getMbcRequisicaoOpmesDAO().obterPorChavePrimaria(seq);
	}
	
	@Override
	public void insereMbcItensRequisicaoOpmesJn(MbcItensRequisicaoOpmes opme, DominioOperacoesJournal operacao) {
		this.journalOpmeRN.inserirMbcItensRequisicaoOpmesJn(opme, operacao);
		
	}
	
	@Override
	public void insereMbcRequisicaoOpmes(MbcRequisicaoOpmes opme, DominioOperacoesJournal operacao) {
		this.journalOpmeRN.inserirMbcRequisicaoOpmes(opme, operacao);
		
	}
	
	@Override
	public void insereMbcMateriaisItemOpmesJn(MbcMateriaisItemOpmes opme, DominioOperacoesJournal operacao) {
		this.journalOpmeRN.inserirMbcMateriaisItemOpmesJn(opme, operacao);		
	}
	
	@Override
	public void validaRequisicaoEscala(MbcRequisicaoOpmes requisicaoOpmes) throws ApplicationBusinessException {
		oPMEPortalAgendamentoON.validaRequisicaoEscala(requisicaoOpmes);
	}
	
	@Override
	public Boolean isRequisicaoEscalada(MbcRequisicaoOpmes requisicaoOpmes) {
		return oPMEPortalAgendamentoON.isRequisicaoEscalada(requisicaoOpmes);
	}
	@Override
	public Boolean isRequisicaoOpmeIncompativel(MbcRequisicaoOpmes requisicaoOpmes) {
		return oPMEPortalAgendamentoON.isRequisicaoOpmeIncompativel(requisicaoOpmes);
	}
	@Override
	public Boolean isRequisicaoOpmeIncompativelSemEscala(MbcRequisicaoOpmes requisicaoOpmes) {
		return oPMEPortalAgendamentoON.isRequisicaoOpmeIncompativelSemEscala(requisicaoOpmes);
	}
	@Override
	public Boolean isRequisicaoFinalizada(MbcRequisicaoOpmes requisicaoOpmes) {
		return oPMEPortalAgendamentoON.isRequisicaoFinalizada(requisicaoOpmes);
	}
	@Override
	public Boolean isRequisicaoAndamento(MbcRequisicaoOpmes requisicaoOpmes) {
		return oPMEPortalAgendamentoON.isRequisicaoAndamento(requisicaoOpmes);
	}
	@Override
	public List<MaterialHospitalarVO> pesquisarMaterialHospitalar(String matNome) throws BaseException {
		return listagemMateriaisOPMERN.pesquisarMaterialHospitalar(matNome);
	}
	@Override
	public List<DemoFinanceiroOPMEVO> pesquisaDemonstrativoFinanceiroOpmes  (
			Date competenciaInicial, Date competenciaFinal, Short especialidadeSeq, Integer prontuario,
			Integer matCodigo) throws BaseException  {
		return listagemMateriaisOPMERN.pesquisaDemonstrativoFinanceiroOpmes(
				 competenciaInicial, competenciaFinal,  especialidadeSeq,  prontuario,
				 matCodigo); 
	}
	@Override
	public List<AghWFHistoricoExecucao> buscarHistExecutoresAutorizacao(AghWFEtapa etapa){
		return aghWFHistoricoExecucaoDAO.buscarHistExecutoresAutorizacao(etapa);
	}

	

}
