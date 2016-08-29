package br.gov.mec.aghu.exames.coleta.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.BypassInactiveModule;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioSituacaoAmostra;
import br.gov.mec.aghu.dominio.DominioSituacaoHorario;
import br.gov.mec.aghu.exames.agendamento.vo.ExamesAgendadosNoHorarioVO;
import br.gov.mec.aghu.exames.agendamento.vo.ItemHorarioAgendadoVO;
import br.gov.mec.aghu.exames.agendamento.vo.VAelHrGradeDispVO;
import br.gov.mec.aghu.exames.coleta.vo.AelExamesAmostraVO;
import br.gov.mec.aghu.exames.coleta.vo.AgendaExamesHorariosVO;
import br.gov.mec.aghu.exames.coleta.vo.GrupoExameVO;
import br.gov.mec.aghu.exames.coleta.vo.OrigemUnidadeVO;
import br.gov.mec.aghu.exames.coleta.vo.PesquisaSolicitacaoDiversosFiltroVO;
import br.gov.mec.aghu.exames.coleta.vo.RelatorioExameColetaPorUnidadeVO;
import br.gov.mec.aghu.exames.dao.AelAmostraItemExamesDAO;
import br.gov.mec.aghu.exames.dao.AelAmostrasDAO;
import br.gov.mec.aghu.exames.dao.AelExtratoAmostrasDAO;
import br.gov.mec.aghu.exames.dao.AelHorarioExameDispDAO;
import br.gov.mec.aghu.exames.dao.AelInformacaoMdtoColetaDAO;
import br.gov.mec.aghu.exames.dao.AelItemHorarioAgendadoDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelSitItemSolicitacoesDAO;
import br.gov.mec.aghu.exames.dao.AelSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExamesPacientesResultsVO;
import br.gov.mec.aghu.exames.pesquisa.vo.VAelExamesAtdDiversosVO;
import br.gov.mec.aghu.exames.vo.AelAmostraExamesVO;
import br.gov.mec.aghu.exames.vo.VAelSolicAtendsVO;
import br.gov.mec.aghu.model.AelAmostraItemExames;
import br.gov.mec.aghu.model.AelAmostraItemExamesId;
import br.gov.mec.aghu.model.AelAmostras;
import br.gov.mec.aghu.model.AelExtratoAmostras;
import br.gov.mec.aghu.model.AelGrupoExames;
import br.gov.mec.aghu.model.AelInformacaoColeta;
import br.gov.mec.aghu.model.AelInformacaoColetaHist;
import br.gov.mec.aghu.model.AelInformacaoColetaHistId;
import br.gov.mec.aghu.model.AelInformacaoColetaId;
import br.gov.mec.aghu.model.AelInformacaoMdtoColeta;
import br.gov.mec.aghu.model.AelItemHorarioAgendado;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelSalasExecutorasExames;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;

/**
 * Porta de entrada do sub módulo de coleta do módulo exames.
 * 
 * @author fpalma
 * 
 */

@Modulo(ModuloEnum.EXAMES_LAUDOS)
@Stateless
public class ColetaExamesFacade extends BaseFacade implements IColetaExamesFacade {


@EJB
private RelatorioExamesColetaPorUnidadeON relatorioExamesColetaPorUnidadeON;

@EJB
private ColetaRealizadaON coletaRealizadaON;

@EJB
private InformacaoColetaON informacaoColetaON;

@EJB
private TransferenciaAgendamentoON transferenciaAgendamentoON;

@EJB
private AmostraExamesON amostraExamesON;

@EJB
private AgendaExameHorarioRN agendaExameHorarioRN;

@EJB
private SolicitacaoDiversosON solicitacaoDiversosON;

@EJB
private ColetaRealizadaRN coletaRealizadaRN;

@Inject
private AelAmostraItemExamesDAO aelAmostraItemExamesDAO;

@Inject
private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;

@Inject
private AelSitItemSolicitacoesDAO aelSitItemSolicitacoesDAO;

@Inject
private AelSolicitacaoExameDAO aelSolicitacaoExameDAO;

@Inject
private AelExtratoAmostrasDAO aelExtratoAmostrasDAO;

@Inject
private AelItemHorarioAgendadoDAO aelItemHorarioAgendadoDAO;

@Inject
private AelAmostrasDAO aelAmostrasDAO;

@Inject
private AelHorarioExameDispDAO aelHorarioExameDispDAO;

@Inject
private AelInformacaoMdtoColetaDAO aelInformacaoMdtoColetaDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 8004352621249027063L;
	
	@Override
	public List<AgendaExamesHorariosVO> pesquisarAgendaExamesHorarios(Date dtAgenda, DominioSituacaoHorario situacaoHorario, Short unfSeq, Short seqP, RapServidoresId rapServidoresId) {
		return getAelHorarioExameDispDAO().pesquisarAgendaExamesHorarios(dtAgenda, situacaoHorario, unfSeq, seqP, rapServidoresId);
	}
	
	@Override
	public List<AelItemSolicitacaoExames> pesquisarItemSolicitacaoExamePorSolicitacaoExame(
			Integer soeSeq) {
		return this.getAelItemSolicitacaoExameDAO().pesquisarItemSolicitacaoExamePorSolicitacaoExame(soeSeq);
	}
	
	@Override
	public void refresh(AelItemSolicitacaoExames itemSolicitacaoExames) {
		this.getAelItemSolicitacaoExameDAO().refresh(itemSolicitacaoExames);
	}
	
	@Override
	@Secure("#{s:hasPermission('visualizarExamesAgendados','pesquisar')}")
	public List<ExamesAgendadosNoHorarioVO> pesquisarExamesAgendadosNoHorario(Short hedGaeUnfSeq, Integer hedGaeSeqp, Date hedDthrAgenda){
		return getAelItemHorarioAgendadoDAO().pesquisarExamesAgendadosNoHorario(hedGaeUnfSeq, hedGaeSeqp, hedDthrAgenda);
	}
	
	@Override
	public void validarAmostrasExamesAgendados(AelItemHorarioAgendado itemHorarioAgendado) throws ApplicationBusinessException {
		getAgendaExameHorarioRN().verificarAmostrasExamesAgendados(itemHorarioAgendado);
	}
	
	@Override
	public void receberItemSolicitacaoExameAgendado(Short hedGaeUnfSeq, Integer hedGaeSeqp, Date hedDthrAgenda, DominioSituacaoHorario dominioSituacaoHorario, String nomeMicrocomputador) throws BaseException {
		getAgendaExameHorarioRN().receberItemSolicitacaoExameAgendado(hedGaeUnfSeq, hedGaeSeqp, hedDthrAgenda, dominioSituacaoHorario, nomeMicrocomputador);
		
	}
	
	@Override
	public void voltarItemSolicitacaoExameAgendado(Short hedGaeUnfSeq, Integer hedGaeSeqp, Date hedDthrAgenda, DominioSituacaoHorario dominioSituacaoHorario, String nomeMicrocomputador) throws BaseException {
		getAgendaExameHorarioRN().voltarItemSolicitacaoExameAgendado(hedGaeUnfSeq, hedGaeSeqp, hedDthrAgenda, dominioSituacaoHorario, nomeMicrocomputador);
		getAelSolicitacaoExamesDAO().flush();
	}
	
	@Override
	public Boolean verificarMaterialAnaliseColetavel(AelItemHorarioAgendado	itemHorarioAgendado) {
		return getColetaRealizadaRN().verificarMaterialAnaliseColetavel(itemHorarioAgendado);
	}	
	
	@Override
	@Secure("#{s:hasPermission('amostraII','executar')}")
	public AelInformacaoColeta persistirInformacaoColeta(AelInformacaoColeta informacaoColetaNew, 
			List<AelInformacaoMdtoColeta> listaInformacaoMdtoColetaNew, List<AelSolicitacaoExames> listaSolicitacaoExames) 
			throws BaseException {
		return getInformacaoColetaON().persistirInformacaoColeta(informacaoColetaNew, listaInformacaoMdtoColetaNew, listaSolicitacaoExames);
	}
	
	@Override
	@Secure("#{s:hasPermission('amostraII','executar') or s:hasPermission('informarColetaRealizada','executar') }")
	public void executarAtualizacaoColetaAmostraHorarioAgendado(Short hedGaeUnfSeq, Integer hedGaeSeqp, Date hedDthrAgenda, String nomeMicrocomputador)
			throws BaseException {
		getColetaRealizadaON().executarAtualizacaoColetaAmostraHorarioAgendado(hedGaeUnfSeq, hedGaeSeqp, hedDthrAgenda, nomeMicrocomputador);
	}
	
	@Override
	public void validarTransferenciaAgendamento(DominioSituacaoHorario situacao) throws BaseException {
		getTransferenciaAgendamentoON().validarTransferenciaAgendamento(situacao);
	}
	
	@Override
	public AelInformacaoColeta obterInformacaoColeta(Integer soeSeq) {
		return getInformacaoColetaON().obterInformacaoColeta(soeSeq);
	}
	
	@Override	
	public AelInformacaoColeta obterInformacaoColetaMascara(AelInformacaoColetaId informacaoColetaId) {
		return getInformacaoColetaON().obterInformacaoColetaMascara(informacaoColetaId);
	}	
	
	@Override	
	public AelInformacaoColetaHist obterInformacaoColetaMascaraHist(AelInformacaoColetaHistId informacaoColetaId) {
		return getInformacaoColetaON().obterInformacaoColetaMascara(informacaoColetaId);
	}		
	
	
	@Override
	public List<VAelSolicAtendsVO> obterSolicAtendsPorItemHorarioAgendado(Short hedGaeUnfSeq, Integer hedGaeSeqp, Date hedDthrAgenda) throws ApplicationBusinessException {
		return getAmostraExamesON().obterSolicAtendsPorItemHorarioAgendado(hedGaeUnfSeq,hedGaeSeqp,hedDthrAgenda);
	}
	
	@Override
	public List<AelAmostras> buscarAmostrasPorSolicitacaoExame(Integer soeSeq) {
		return getAelAmostrasDAO().buscarAmostrasPorSolicitacaoExameSeq(soeSeq);
	}
	
	@Override
	public List<AelAmostras> buscarAmostrasPorAmostraESolicitacaoExame(Short amostraSeq, Integer soeSeq) {
		return getAelAmostrasDAO().buscarAmostrasPorAmostraESolicitacaoExame(amostraSeq, soeSeq);
	}
	
	@Override
	public AelExtratoAmostras pesquisarExtratoAmostraAnterior(Integer soeSeq, Short seqp) {
		return getAelExtratoAmostrasDAO().pesquisarExtratoAmostraAnterior(soeSeq, seqp);
	}
	
	@Override
	public List<AelExamesAmostraVO> obterAmostraItemExamesPorAmostra(Integer soeSeq, Short seqp) {
		return getAmostraExamesON().obterAmostraItemExamesPorAmostra(soeSeq, seqp);
	}
	
	@Override
	public Boolean verificaSituacaoAmostraGeradaOuEmColeta(DominioSituacaoAmostra situacaoAmostra) {
		return getAmostraExamesON().verificaSituacaoAmostraGeradaOuEmColeta(situacaoAmostra);
	}
	
	@Override
	public Boolean verificaSituacaoAmostraCURMA(DominioSituacaoAmostra situacaoAmostra) {
		return getAmostraExamesON().verificaSituacaoAmostraCURMA(situacaoAmostra);
	}
	
	@Override
	@Secure("#{s:hasPermission('voltarColeta','executar')}")
	public void voltarColeta(Short hedGaeUnfSeq, Integer hedGaeSeqp, Date hedDthrAgenda, DominioSituacaoHorario situacao, String nomeMicrocomputador) throws BaseException {
		getAgendaExameHorarioRN().voltarColeta(hedGaeUnfSeq, hedGaeSeqp, hedDthrAgenda, situacao, nomeMicrocomputador);
		getAelSolicitacaoExamesDAO().flush();
	}
	

	
	@Override
	public void removerInformacaoMdtoColeta(AelInformacaoMdtoColeta informacaoMdtoColeta) throws ApplicationBusinessException {
		getInformacaoColetaON().removerInformacaoMdtoColeta(informacaoMdtoColeta);
	}

	@Override
	public List<ItemHorarioAgendadoVO> pesquisarExamesParaTransferencia(Short hedGaeUnfSeq, Integer hedGaeSeqp, Date hedDthrAgenda) {
		return this.getTransferenciaAgendamentoON().pesquisarExamesParaTransferencia(hedGaeUnfSeq, hedGaeSeqp, hedDthrAgenda);
	}
	
	@Override
	public OrigemUnidadeVO obterOrigemUnidadeSolicitante(Short gaeUnfSeq, Integer gaeSeqp, Date dthrAgenda){
		return this.getTransferenciaAgendamentoON().obterOrigemUnidadeSolicitante(gaeUnfSeq, gaeSeqp, dthrAgenda);
	}
	
	@Override
	public List<ItemHorarioAgendadoVO> obterExamesSelecionados(List<ItemHorarioAgendadoVO> listaExamesAgendados){
		return this.getTransferenciaAgendamentoON().obterExamesSelecionados(listaExamesAgendados);
	}
	
	@Override
	public List<VAelHrGradeDispVO> pesquisarHorariosParaExameSelecionado(Date dataHoraReativacao, Short tipo1, Short tipo2,ItemHorarioAgendadoVO itemHorarioAgendadoVO, Date data, Date hora, Integer grade, AelGrupoExames grupoExame, AelSalasExecutorasExames salaExecutoraExame, RapServidores servidor){
		return this.getTransferenciaAgendamentoON().pesquisarHorariosParaExameSelecionado(dataHoraReativacao, tipo1, tipo2,itemHorarioAgendadoVO, data, hora, grade, grupoExame, salaExecutoraExame, servidor);
	}
	
	@Override
	public List<VAelHrGradeDispVO> pesquisarHorariosParaGrupoExameSelecionado(Date dataHoraReativacao, Short tipo1, Short tipo2, List<GrupoExameVO> listaGrupoExame, Date data, Date hora, Integer grade, AelGrupoExames grupoExame, AelSalasExecutorasExames salaExecutoraExame, RapServidores servidor){
		return this.getTransferenciaAgendamentoON().pesquisarHorariosParaGrupoExameSelecionado(dataHoraReativacao, tipo1, tipo2, listaGrupoExame, data, hora, grade, grupoExame, salaExecutoraExame, servidor);
	}
	
	@Override
	public List<GrupoExameVO> pesquisarGrupoExameTransferenciaAgendamento(List<ItemHorarioAgendadoVO> listaItens){
		return this.getTransferenciaAgendamentoON().pesquisarGrupoExameTransferenciaAgendamento(listaItens);
	}
	
	@Override
	public Date obterDataReativacaoUnfExecutaExameAtiva(String emaExaSigla, Integer emaManSeq, Short unfSeq) {
		return this.getTransferenciaAgendamentoON().obterDataReativacaoUnfExecutaExameAtiva(emaExaSigla, emaManSeq, unfSeq);
	}
	
	@Override
	public Date obterMaiorDataReativacao(List<GrupoExameVO> listaItens){
		return this.getTransferenciaAgendamentoON().obterMaiorDataReativacao(listaItens);
	}
	
	@Override
	public void transferirHorarioAgendado(AelItemHorarioAgendado itemHorarioAgendado, Boolean permiteHoraExtra, AelUnfExecutaExames unfExecutoraExame, String nomeMicrocomputador) throws BaseException {
		this.getTransferenciaAgendamentoON().transferirHorarioAgendado(itemHorarioAgendado, permiteHoraExtra, unfExecutoraExame, nomeMicrocomputador);
	}
	
	@Override
	public void validarSolicitacaoPorAmostra(Integer soeSeq, Short amostraSeq) throws ApplicationBusinessException{
		this.getAmostraExamesON().validarSolicitacaoPorAmostra(soeSeq, amostraSeq);
	}
	
	@Override
	public List<VAelSolicAtendsVO> pesquisarSolicitacaoPorPaciente(Integer soeq, Integer pacCodigo){
		return this.getAmostraExamesON().pesquisarSolicitacaoPorPaciente(soeq, pacCodigo);
	}
	
	@Override
	public List<VAelSolicAtendsVO> pesquisarSolicitacaoPorPacienteEAmostra(Integer soeq, Integer pacCodigo, Short amostraSeq){
		return this.getAmostraExamesON().pesquisarSolicitacaoPorPacienteEAmostra(soeq, pacCodigo, amostraSeq);
	}
	
	@Override
	public void validarSolicitacaoPaciente(Integer soeSeq, Integer pacCodigo) throws ApplicationBusinessException{
		this.getAmostraExamesON().validarSolicitacaoPaciente(soeSeq, pacCodigo);
	}
	
	@Override
	public AelAmostras obterAmostra(Integer soeSeq, Short seqp){
		return this.getAelAmostrasDAO().obterAmostraPorId(soeSeq, seqp);
	}
	
	@Override
	@Secure("#{s:hasPermission('amostraII','executar')}")
	public void coletarExame(AelAmostraItemExamesId amostraItemExamesId, String nomeMicrocomputador) throws BaseException{
		AelAmostraItemExames amostraItemExame = this.getAelAmostraItemExamesDAO().obterPorChavePrimaria(amostraItemExamesId);
		this.getAmostraExamesON().coletarExame(amostraItemExame, nomeMicrocomputador);
	}
	
	@Override
	@Secure("#{s:hasPermission('amostraII','executar')}")
	public void voltarExame(AelAmostraItemExamesId amostraItemExamesId, String nomeMicrocomputador) throws BaseException{
		AelAmostraItemExames amostraItemExame = this.getAelAmostraItemExamesDAO().obterPorChavePrimaria(amostraItemExamesId);
		this.getAmostraExamesON().voltarExame(amostraItemExame, nomeMicrocomputador);
	}
	
	@Override
	public void validarColetaExames(Integer amoSeqp, List<AelExamesAmostraVO> listaExamesAmostra) throws ApplicationBusinessException{
		this.getAmostraExamesON().validarColetaExames(amoSeqp, listaExamesAmostra);
	}
	
	@Override
	@BypassInactiveModule
	public void validarVoltaExames(Integer amoSeqp, List<AelExamesAmostraVO> listaExamesAmostra) throws ApplicationBusinessException{
		this.getAmostraExamesON().validarVoltaExames(amoSeqp, listaExamesAmostra);
	}
	
	@Override
	public void validarColetaExame(List<AelAmostraExamesVO> listaItensAmostra) throws ApplicationBusinessException{
		this.getAmostraExamesON().validarColetaExame(listaItensAmostra);
	}
	
	@Override
	public void validarVoltaExame(List<AelAmostraExamesVO> listaItensAmostra) throws ApplicationBusinessException{
		this.getAmostraExamesON().validarVoltaExame(listaItensAmostra);
	}

	@Override
	public void atualizarSituacaoExamesAmostra(AelAmostras amostra, String nomeMicrocomputador) throws BaseException {
		getAmostraExamesON().atualizarSituacaoExamesAmostra(amostra, nomeMicrocomputador);
	}
	
	@Override
	public void atualizarSituacaoExamesAmostraColetada(AelAmostras amostra, String nomeMicrocomputador) throws BaseException {
		getAmostraExamesON().atualizarSituacaoExamesAmostraColetada(amostra, nomeMicrocomputador);
	}
	
	@Override
	public List<RelatorioExameColetaPorUnidadeVO> obterExamesColetaPorUnidade(AghUnidadesFuncionais unidadeExecutora) throws BaseException {
		return getRelatorioExamesColetaPorUnidadeON().obterAgendasPorUnidade(unidadeExecutora);
	}

	@Override
	public List<AelInformacaoMdtoColeta> buscarAelInformacaoMdtoColetaByAelInformacaoColeta(final Short seqp, final Integer soeSeq){
		return this.getAelInformacaoMdtoColetaDAO().buscarAelInformacaoMdtoColetaByAelInformacaoColeta(seqp, soeSeq);
	}
	
	@Override
	@Secure("#{s:hasPermission('pesquisarSolicitacaoDiversos','pesquisar')}")
	public List<VAelExamesAtdDiversosVO> pesquisarSolicitacaoDiversos(PesquisaSolicitacaoDiversosFiltroVO filtro) throws ApplicationBusinessException {
		return getSolicitacaoDiversosON().pesquisarSolicitacaoDiversos(filtro);
	}
	
	@Override
	public List<PesquisaExamesPacientesResultsVO> popularListaImpressaoLaudo(List<VAelExamesAtdDiversosVO> listaSolicitacaoDiversos) {
		return getSolicitacaoDiversosON().popularListaImpressaoLaudo(listaSolicitacaoDiversos);
	}
	
	@Override
	public List<AelSitItemSolicitacoes> pesquisarSitItemSolicitacoesPorCodigoOuDescricao(String parametro) {
		return getAelSitItemSolicitacoesDAO().pesquisarSitItemSolicitacoesPorCodigoOuDescricao(parametro);
	}
	
	protected TransferenciaAgendamentoON getTransferenciaAgendamentoON() {
		return transferenciaAgendamentoON;
	}
	
	protected AelHorarioExameDispDAO getAelHorarioExameDispDAO() {
		return aelHorarioExameDispDAO;
	}
	
	protected AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO() {
		return aelItemSolicitacaoExameDAO;
	}
	
	protected AelInformacaoMdtoColetaDAO getAelInformacaoMdtoColetaDAO() {
		return aelInformacaoMdtoColetaDAO;
	}
	
	protected AgendaExameHorarioRN getAgendaExameHorarioRN() {
		return agendaExameHorarioRN;
	}
	
	protected AmostraExamesON getAmostraExamesON() {
		return amostraExamesON;
	}
	
	protected AelAmostrasDAO getAelAmostrasDAO() {
		return aelAmostrasDAO;
	}
	
	protected AelExtratoAmostrasDAO getAelExtratoAmostrasDAO() {
		return aelExtratoAmostrasDAO;
	}
	
	protected AelAmostraItemExamesDAO getAelAmostraItemExamesDAO() {
		return aelAmostraItemExamesDAO;
	}
	
	protected AelSolicitacaoExameDAO getAelSolicitacaoExamesDAO() {
		return aelSolicitacaoExameDAO;
	}
	
	protected AelItemHorarioAgendadoDAO getAelItemHorarioAgendadoDAO() {
		return aelItemHorarioAgendadoDAO;
	}
	
	protected ColetaRealizadaRN getColetaRealizadaRN() {
		return coletaRealizadaRN;
	}
	
	protected ColetaRealizadaON getColetaRealizadaON() {
		return coletaRealizadaON;
	}
	
	protected InformacaoColetaON getInformacaoColetaON() {
		return informacaoColetaON;
	}
	
	protected AelSitItemSolicitacoesDAO getAelSitItemSolicitacoesDAO() {
		return aelSitItemSolicitacoesDAO;
	}
	
	protected RelatorioExamesColetaPorUnidadeON getRelatorioExamesColetaPorUnidadeON() {
		return relatorioExamesColetaPorUnidadeON;
	}
	
	protected SolicitacaoDiversosON getSolicitacaoDiversosON() {
		return solicitacaoDiversosON;
	}
	
}
