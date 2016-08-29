package br.gov.mec.aghu.blococirurgico.portalplanejamento.business;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.blococirurgico.dao.MbcAgendaAnestesiaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendaAnotacaoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendaDiagnosticoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendaHemoterapiaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendaOrtProteseDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendaProcedimentoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendaSolicEspecialDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcNecessidadeCirurgicaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcPorEquipeDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfAtuaUnidCirgsDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcRequisicaoOpmesDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcSalaCirurgicaDAO;
import br.gov.mec.aghu.blococirurgico.dao.VMbcProcEspDAO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.AgendamentosExcluidosVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.CirurgiasCanceladasAgendaMedicoVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.CirurgiasCanceladasVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.EscalaPortalPlanejamentoCirurgiasVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.EscalaSalasVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.ListaEsperaVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPesquisaCirurgiasAgendaEscalaDiaVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPesquisaCirurgiasC2VO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPesquisaCirurgiasParametrosVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPlanejamentoCirurgiasDiaVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.RelatorioEscalaDeSalasVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.RelatorioPortalPlanejamentoCirurgiasVO;
import br.gov.mec.aghu.blococirurgico.vo.AgendaCirurgiaSolicitacaoEspecialVO;
import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioSituacaoAgendas;
import br.gov.mec.aghu.dominio.DominioTipoAgendaJustificativa;
import br.gov.mec.aghu.model.AghCaractUnidFuncionais;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MbcAgendaAnestesia;
import br.gov.mec.aghu.model.MbcAgendaAnotacao;
import br.gov.mec.aghu.model.MbcAgendaAnotacaoId;
import br.gov.mec.aghu.model.MbcAgendaDiagnostico;
import br.gov.mec.aghu.model.MbcAgendaHemoterapia;
import br.gov.mec.aghu.model.MbcAgendaOrtProtese;
import br.gov.mec.aghu.model.MbcAgendaProcedimento;
import br.gov.mec.aghu.model.MbcAgendaSolicEspecial;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcNecessidadeCirurgica;
import br.gov.mec.aghu.model.MbcProcPorEquipe;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgsId;
import br.gov.mec.aghu.model.MbcRequisicaoOpmes;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.model.VMbcProcEsp;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;


@SuppressWarnings({ "PMD.CouplingBetweenObjects", "PMD.ExcessiveClassLength" })
@Modulo(ModuloEnum.BLOCO_CIRURGICO)
@Stateless
public class BlocoCirurgicoPortalPlanejamentoFacade extends BaseFacade implements IBlocoCirurgicoPortalPlanejamentoFacade {

	@Inject
	private MbcAgendaHemoterapiaDAO mbcAgendaHemoterapiaDAO;
	
	@Inject
	private VMbcProcEspDAO vMbcProcEspDAO;

	@Inject
	private MbcAgendasDAO mbcAgendasDAO;

	@Inject
	private MbcAgendaProcedimentoDAO mbcAgendaProcedimentoDAO;

	@Inject
	private MbcAgendaSolicEspecialDAO mbcAgendaSolicEspecialDAO;

	@Inject
	private MbcAgendaAnestesiaDAO mbcAgendaAnestesiaDAO;

	@Inject
	private MbcSalaCirurgicaDAO mbcSalaCirurgicaDAO;

	@Inject
	private MbcAgendaAnotacaoDAO mbcAgendaAnotacaoDAO;

	@Inject
	private MbcProfAtuaUnidCirgsDAO mbcProfAtuaUnidCirgsDAO;

	@Inject
	private MbcAgendaDiagnosticoDAO mbcAgendaDiagnosticoDAO;

	@Inject
	private MbcAgendaOrtProteseDAO mbcAgendaOrtProteseDAO;

	@Inject
	private MbcProcPorEquipeDAO mbcProcPorEquipeDAO;

	@Inject
	private MbcNecessidadeCirurgicaDAO mbcNecessidadeCirurgicaDAO;
	
	@Inject
	private MbcRequisicaoOpmesDAO mbcRequisicaoOpmesDAO;

	@EJB
	private PortalPlanejamentoCirurgia2ON portalPlanejamentoCirurgia2ON;

	@EJB
	private ListaCirurgiasCanceladasRN listaCirurgiasCanceladasRN;

	@EJB
	private CirurgiasCanceladasAgendaMedicoON cirurgiasCanceladasAgendaMedicoON;

	@EJB
	private ConsultaEscalaSalasRN consultaEscalaSalasRN;

	@EJB
	private RelatorioPortalPlanejamentoCirurgiasON relatorioPortalPlanejamentoCirurgiasON;

	@EJB
	private IncluirAnotacaoEquipeON incluirAnotacaoEquipeON;

	@EJB
	private MbcAgendaAnestesiaON mbcAgendaAnestesiaON;

	@EJB
	private RelatorioEscalaDeSalasON relatorioEscalaDeSalasON;

	@EJB
	private VisualizarListaEsperaON visualizarListaEsperaON;

	@EJB
	private ListarAgendamentosExcluidosON listarAgendamentosExcluidosON;

	@EJB
	private RelatorioPortalPlanejamentoCirurgiasRN relatorioPortalPlanejamentoCirurgiasRN;

	@EJB
	private RemarcarPacienteAgendaON remarcarPacienteAgendaON;

	@EJB
	private VisualizarAgendaEscalasPortalPesquisaON visualizarAgendaEscalasPortalPesquisaON;

	@EJB
	private PortalPlanejamentoCirurgiaDetalheON portalPlanejamentoCirurgiaDetalheON;

	@EJB
	private ListarCirurgiasCanceladasON listarCirurgiasCanceladasON;

	@EJB
	private PortalPlanejamentoCirurgiaON portalPlanejamentoCirurgiaON;

	@EJB
	private EscalaPortalPlanejamentoCirurgiaON escalaPortalPlanejamentoCirurgiaON;

	@EJB
	private PortalPesquisaCirurgiasON portalPesquisaCirurgiasON;

	private static final long serialVersionUID = 8645537000631798327L;

	private MbcAgendaAnotacaoDAO getMbcAgendaAnotacaoDAO() {
		return mbcAgendaAnotacaoDAO;
	}
	
	private MbcAgendasDAO getMbcAgendasDAO() {
		return mbcAgendasDAO;
	}

	private RelatorioEscalaDeSalasON getRelatorioEscalaDeSalasON() {
		return relatorioEscalaDeSalasON;
	}
	
	private MbcSalaCirurgicaDAO getMbcSalaCirurgicaDAO() {
		return mbcSalaCirurgicaDAO;
	}

	@Override
	public MbcAgendaAnotacao obterMbcAgendaAnotacaoPorChavePrimaria(MbcAgendaAnotacaoId mbcAgendaAnotacaoId) {
		return getMbcAgendaAnotacaoDAO().obterPorChavePrimaria(mbcAgendaAnotacaoId);
	}
	
	@Override
	public void persistirMbcAgendaAnotacao(MbcAgendaAnotacao mbcAgendaAnotacao) throws ApplicationBusinessException {
		getIncluirAnotacaoEquipeON().persistirMbcAgendaAnotacao(mbcAgendaAnotacao);
	}

	@Override
	public List<RelatorioEscalaDeSalasVO> listarEquipeSalas(Short seqUnidade) {
		return getRelatorioEscalaDeSalasON().listarEquipeSalas(seqUnidade);
	}

	private IncluirAnotacaoEquipeON getIncluirAnotacaoEquipeON() {
		return incluirAnotacaoEquipeON;
	}

	@Override
	public Collection<RelatorioPortalPlanejamentoCirurgiasVO> listarEquipePlanejamentoCirurgias(
			Date pDtIni, Date pDtFim, Integer pPucSerMatricula,
			Short pPucSerVinCodigo, Short pPucUnfSeq, DominioFuncaoProfissional pPucIndFuncaoProf,
			Short pEspSeq, Short pUnfSeq, String pEquipe) throws ApplicationBusinessException {
		return getPortalPlanejamentoCirurgiasON().listarEquipePlanejamentoCirurgias (pDtIni, pDtFim, pPucSerMatricula, pPucSerVinCodigo, pPucUnfSeq, pPucIndFuncaoProf, pEspSeq, pUnfSeq, pEquipe);
	}
	
	@Override
	public Boolean listarEquipePlanejamentoCirurgiasPossuiRegistro(
			Date pDtIni, Date pDtFim, Integer pPucSerMatricula,
			Short pPucSerVinCodigo, Short pPucUnfSeq, DominioFuncaoProfissional pPucIndFuncaoProf,
			Short pEspSeq, Short pUnfSeq, String pEquipe) throws ApplicationBusinessException {
		return getPortalPlanejamentoCirurgiasON().listarEquipePlanejamentoCirurgiasPossuiRegistro (pDtIni, pDtFim, pPucSerMatricula, pPucSerVinCodigo, pPucUnfSeq, pPucIndFuncaoProf, pEspSeq, pUnfSeq, pEquipe);
	}
	
	private RelatorioPortalPlanejamentoCirurgiasON getPortalPlanejamentoCirurgiasON() {
		return relatorioPortalPlanejamentoCirurgiasON;
	}
	
	private PortalPesquisaCirurgiasON getPortalPesquisaCirurgiasON() {
		return portalPesquisaCirurgiasON;
	}

	@Override
	public List<AghCaractUnidFuncionais> listarAghCaractUnidFuncionais(String objPesquisa) {
		return getPortalPesquisaCirurgiasON().listarAghCaractUnidFuncionais(objPesquisa);
	}

	@Override
	public List<PortalPesquisaCirurgiasC2VO> listarMbcProfAtuaUnidCirgsPorUnfSeq(
			Short unfSeq, String strPesquisa) {
		return getPortalPesquisaCirurgiasON().listarMbcProfAtuaUnidCirgsPorUnfSeq(unfSeq, strPesquisa);
	}

	@Override
	public Long listarMbcProfAtuaUnidCirgsPorUnfSeqCount(Short unfSeq,
			String strPesquisa) {
		return getPortalPesquisaCirurgiasON().listarMbcProfAtuaUnidCirgsPorUnfSeqCount(unfSeq, strPesquisa);
	}

	@Override
	public List<LinhaReportVO> listarCaracteristicaSalaCirgPorUnidade(String pesquisa, Short unfSeq) {
		return getPortalPesquisaCirurgiasON().listarCaracteristicaSalaCirgPorUnidade(pesquisa, unfSeq);
	}

	@Override
	public List<MbcProcedimentoCirurgicos> listarMbcProcedimentoCirurgicoPorTipo(String strPesquisa) {
		return getPortalPesquisaCirurgiasON().listarMbcProcedimentoCirurgicoPorTipo(strPesquisa);
	}

	@Override
	public Long listarMbcProcedimentoCirurgicoPorTipoCount(String strPesquisa) {
		return getPortalPesquisaCirurgiasON().listarMbcProcedimentoCirurgicoPorTipoCount(strPesquisa);
	}

	private VisualizarListaEsperaON getVisualizarListaEsperaON() {
		return visualizarListaEsperaON;
	}

	@Override
	public List<ListaEsperaVO> listaEsperaRecuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, PortalPesquisaCirurgiasParametrosVO parametros) throws ApplicationBusinessException {
		return getVisualizarListaEsperaON().recuperarListaPaginada(firstResult, maxResult, orderProperty, asc, parametros);
	}
	
	@Override
	public Long listaEsperaRecuperarCount(PortalPesquisaCirurgiasParametrosVO parametros) {
		return getVisualizarListaEsperaON().recuperarCount(parametros);
	}

	@Override
	public List<CirurgiasCanceladasVO> pesquisarCirurgiasCanceladas(
			String orderProperty, boolean asc,
			PortalPesquisaCirurgiasParametrosVO portalPesquisaCirurgiasParametrosVO) throws ApplicationBusinessException {
		return getListarCirurgiasCanceladasON().listarCirurgiasCanceladas
			(orderProperty, asc, portalPesquisaCirurgiasParametrosVO);
	}
	
	@Override
	@Secure("#{s:hasPermission('consultarAgendamentosExluidos', 'consultar')}")
	public	List<AgendamentosExcluidosVO> pesquisarAgendamentosExcluidos(Integer firstResult, Integer maxResult,String orderProperty, boolean asc,
			PortalPesquisaCirurgiasParametrosVO portalPesquisaCirurgiasParametrosVO){
		return getListarAgendamentosExcluidosON().listarAgendamentosExcluidos(firstResult, maxResult, orderProperty, asc, portalPesquisaCirurgiasParametrosVO);
	}

	@Override
	public	Long pesquisarAgendamentosExcluidosCount(PortalPesquisaCirurgiasParametrosVO portalPesquisaCirurgiasParametrosVO){
		return getListarAgendamentosExcluidosON().recuperarCount(portalPesquisaCirurgiasParametrosVO);
	}

	@Override
	public List<MbcAgendaDiagnostico> pesquisarAgendaDiagnosticoEscalaCirurgicaPorAgenda(Integer seq) {
		return mbcAgendaDiagnosticoDAO.pesquisarAgendaDiagnosticoEscalaCirurgicaPorAgenda(seq);
	}
	
	private ListarCirurgiasCanceladasON getListarCirurgiasCanceladasON() {
		return listarCirurgiasCanceladasON;
	}
	
	private ListarAgendamentosExcluidosON getListarAgendamentosExcluidosON() {
		return listarAgendamentosExcluidosON;
	}
	
	@Override
	public Integer pesquisarEprPciSeqporCirurgia(Integer agdSeq) {
		return getListaCirurgiasCanceladasRN().pesquisarEprPciSeqporCirurgia(agdSeq);
	}

	protected ListaCirurgiasCanceladasRN getListaCirurgiasCanceladasRN() {
		return listaCirurgiasCanceladasRN;
	}

	@Override
	public String pesquisarProcEspCirurgico(Integer agdSeq) {
		return getListaCirurgiasCanceladasRN().pesquisarProcEspCirurgico(agdSeq);
	}

	@Override
	public void validarPesquisaPortalCirurgias(
			PortalPesquisaCirurgiasParametrosVO parametrosVO)
			throws ApplicationBusinessException {
		getPortalPesquisaCirurgiasON().validarPesquisaPortalCirurgias(parametrosVO);
	}
	
	@Override
	public List<EscalaSalasVO> pesquisarEscalaSalasPorUnidadeCirurgica(final Short unfSeq) {
		return this.getConsultaEscalaSalasRN().pesquisarEscalaSalasPorUnidadeCirurgica(unfSeq);
	}
	
	protected ConsultaEscalaSalasRN getConsultaEscalaSalasRN() {
		return consultaEscalaSalasRN;
	}

	@Override
	public List<MbcAgendas> pesquisarAgendasPorPacienteEquipe(
			DominioSituacaoAgendas[] dominioSituacaoAgendas, Integer pacCodigo,
			Integer matriculaEquipe, Short vinCodigoEquipe, Short unfSeqEquipe,
			DominioFuncaoProfissional indFuncaoProfEquipe, Short seqEspecialidade,
			Short seqUnidFuncionalCirugica) {
		return getMbcAgendasDAO().pesquisarAgendasPorPacienteEquipe(dominioSituacaoAgendas,pacCodigo,matriculaEquipe,vinCodigoEquipe,unfSeqEquipe,indFuncaoProfEquipe,seqEspecialidade,seqUnidFuncionalCirugica);
	}

	@Override
	public Boolean verificarExisteSolicEspApCongelacao(Integer agdSeq,
			BigDecimal paramApCongelacao) {
		return getMbcAgendaSolicEspecialDAO().verificarExisteSolicEspApCongelacao(agdSeq,paramApCongelacao);
	}
	
	public MbcAgendaSolicEspecialDAO getMbcAgendaSolicEspecialDAO(){
		return mbcAgendaSolicEspecialDAO;
	}

	@Override
	public List<MbcProcPorEquipe> pesquisarProcedimentosEquipe(
			Integer matriculaEquipe, Short vinCodigoEquipe, Short unfSeqEquipe) {
		return getMbcProcPorEquipeDAO().pesquisarProcedimentosEquipe(matriculaEquipe,vinCodigoEquipe,unfSeqEquipe);
	}

	@Override
	public List<MbcAgendaAnestesia> listarAgendaAnestesiaPorAgdSeq(Integer agdSeq) {
		return getMbcAgendaAnestesiaDAO().listarAgendaAnestesiaPorAgdSeq(agdSeq);
	}

	@Override
	public Set<MbcAgendaHemoterapia> listarAgendasHemoterapiaPorAgendaSeq(final Integer agendaSeq){
		return mbcAgendaHemoterapiaDAO.listarAgendasHemoterapiaPorAgendaSeq(agendaSeq);
	}
	
	private MbcAgendaAnestesiaDAO getMbcAgendaAnestesiaDAO() {
		return mbcAgendaAnestesiaDAO;
	}
	
	@Override
	public List<MbcAgendaProcedimento> listarAgendaProcedimentoPorAgdSeq(Integer agdSeq) {
		return getMbcAgendaProcedimentoDAO().pesquisarPorAgdSeq(agdSeq);
	}
	
	@Override
	public MbcAgendaDiagnostico obterAgendaDiagnosticoEscalaCirurgicaPorAgenda(Integer agdSeq) {
		return getMbcAgendaDiagnosticoDAO().obterAgendaDiagnosticoEscalaCirurgicaPorAgenda(agdSeq);
	}
	
	private MbcAgendaDiagnosticoDAO getMbcAgendaDiagnosticoDAO() {
		return mbcAgendaDiagnosticoDAO;
	}
	
	@Override
	public void validarAnestesiaAdicionadaExistente(List<MbcAgendaAnestesia> listaAgendaAnestesias, MbcAgendaAnestesia agendaAnestesia) throws ApplicationBusinessException {
		getMbcAgendaAnestesiaON().validarAnestesiaAdicionadaExistente(listaAgendaAnestesias, agendaAnestesia);
	}
	
	private MbcAgendaAnestesiaON getMbcAgendaAnestesiaON() {
		return mbcAgendaAnestesiaON;
	}
	
	private MbcProcPorEquipeDAO getMbcProcPorEquipeDAO(){
		return mbcProcPorEquipeDAO;
	}
	
	@Override
	public List<VMbcProcEsp> pesquisarVMbcProcEspPorEsp(Object objParam, Short espSeq, Integer maxResults) {
		return getVMbcProcEspDAO().pesquisarProcEspPorEspecialidade(objParam, espSeq, maxResults );
	}
	
	@Override
	public Long pesquisarVMbcProcEspPorEspCount(Object objParam, Short espSeq) {
		return getVMbcProcEspDAO().pesquisarProcEspPorEspecialidadeCount(objParam, espSeq);
	}
	
	
	private VMbcProcEspDAO getVMbcProcEspDAO(){
		return vMbcProcEspDAO;
	}

	private MbcAgendaProcedimentoDAO getMbcAgendaProcedimentoDAO(){
		return mbcAgendaProcedimentoDAO;
	}
	
	@Override
	public List<MbcAgendaProcedimento> pesquisarAgendaProcedimento(Integer agdSeq) {
		return getMbcAgendaProcedimentoDAO().pesquisarAgendaProcedimentoPorId(agdSeq);
	}
	
	@Override
	public List<MbcAgendaSolicEspecial> buscarMbcAgendaSolicEspecialPorAgdSeq(Integer agdSeq) {
		return getMbcAgendaSolicEspecialDAO().buscarMbcAgendaSolicEspecialPorAgdSeq(agdSeq);
	}
	
	@Override
	public List<AgendaCirurgiaSolicitacaoEspecialVO> buscarMbcAgendaCirurgiaSolicEspecialPorAgdSeq(Integer agdSeq) {
		return getMbcAgendaSolicEspecialDAO().buscarMbcAgendaCirurgiaSolicEspecialPorAgdSeq(agdSeq);
	}
	
	@Override
	public List<MbcAgendaOrtProtese> buscarOrteseprotesePorAgenda(Integer agdSeq){
		return getMbcAgendaOrtProteseDAO().buscarOrteseprotesePorAgenda(agdSeq);
	}
	
	private MbcAgendaOrtProteseDAO getMbcAgendaOrtProteseDAO(){
		return mbcAgendaOrtProteseDAO;
	}

	@Override
	public MbcAgendas obterAgendaPorAgdSeq(Integer agdSeq) {
		return getMbcAgendasDAO().obterPorChavePrimaria(agdSeq, true, MbcAgendas.Fields.PACIENTE,  MbcAgendas.Fields.UNF);
	}
	
	@Override
	public MbcAgendas retornarAgendaPorAgdSeq(Integer agdSeq) {
		return getMbcAgendasDAO().retornarAgendaPorAgdSeq(agdSeq);
	}
	
	@Override
	public MbcAgendas obterAgendaPorAgdSeq(Integer agdSeq, Enum[] inner, Enum[] left) {
		return getMbcAgendasDAO().obterPorChavePrimaria(agdSeq, inner, left);
	}
	
	@Override
	public MbcAgendas obterAgendaPorSeq(Integer agdSeq) {
		return getMbcAgendasDAO().obterAgendaPorSeq(agdSeq);
	}
	
	@Override
	public MbcAgendas obterAgendaRemarcarPorSeq(Integer agdSeq) {
		return getMbcAgendasDAO().obterAgendaPorSeqRemarcar(agdSeq);
	}
	
	@Override
	public List<MbcRequisicaoOpmes> consultarListaRequisicoesPorAgenda(Integer agdSeq) {
		return mbcRequisicaoOpmesDAO.consultarListaRequisicoesPorAgenda(agdSeq);
	}
	
	@Override
	public MbcAgendas buscarAgenda(Integer agdSeq) {
		return getMbcAgendasDAO().obterOriginal(agdSeq);
	}

	@Override
	public List<MbcSalaCirurgica> buscarSalasDisponiveisParaTrocaNaAgenda(Date data, Short unfSeq, Short espSeq,
			Integer matriculaEquipe, Short vinCodigoEquipe, Short unfSeqEquipe, DominioFuncaoProfissional indFuncaoProf) {
		return getMbcSalaCirurgicaDAO().buscarSalasDisponiveisParaTrocaNaAgenda(data, unfSeq, espSeq, matriculaEquipe, vinCodigoEquipe, 
				unfSeqEquipe, indFuncaoProf);
	}
	
	@Override
	public List<MbcProfAtuaUnidCirgs> buscarEquipeMedicaParaMudancaNaAgenda(String nome, Short unfSeq, Short espSeq) {
		return getMbcProfAtuaUnidCirgsDAO().buscarEquipeMedicaParaMudancaNaAgenda(nome, unfSeq, espSeq);
	}
	
	@Override
	public Long buscarEquipeMedicaParaMudancaNaAgendaCount(String nome, Short unfSeq, Short espSeq) {
		return getMbcProfAtuaUnidCirgsDAO().buscarEquipeMedicaParaMudancaNaAgendaCount(nome, unfSeq, espSeq);
	}
	
	@Override
	public List<MbcProfAtuaUnidCirgs> buscarEquipeMedicaParaAgendamento(String nome, Short unfSeq, Short espSeq) {
		return getMbcProfAtuaUnidCirgsDAO().buscarEquipeMedicaParaAgendamento(nome, unfSeq, espSeq);
	}
	
	@Override
	public Long buscarEquipeMedicaParaAgendamentoCount(String nome, Short unfSeq, Short espSeq) {
		return getMbcProfAtuaUnidCirgsDAO().buscarEquipeMedicaParaAgendamentoCount(nome, unfSeq, espSeq);
	}
	
	@Override
	public MbcProfAtuaUnidCirgs obterEquipePorChavePrimaria(MbcProfAtuaUnidCirgsId idEquipe) {
		return getMbcProfAtuaUnidCirgsDAO().obterPorChavePrimaria(idEquipe);
	}
	
	private MbcProfAtuaUnidCirgsDAO getMbcProfAtuaUnidCirgsDAO(){
		return mbcProfAtuaUnidCirgsDAO;
	}
	
	@Override
	public List<MbcSalaCirurgica> buscarSalasCirurgicasPorUnfSeq(Short unfSeq) {
		return getMbcSalaCirurgicaDAO().buscarSalasCirurgicasPorUnfSeq(unfSeq);
	}

	@Override 
	@Secure("#{s:hasPermission('portalPlanejamentoCirurgias','pesquisar')}")
	public PortalPlanejamentoCirurgiasDiaVO pesquisarPortalPlanejamentoCirurgia(
			Short unfSeq, Date dataBase, AghEspecialidades especialidade,
			MbcProfAtuaUnidCirgs atuaUnidCirgs, MbcSalaCirurgica salaCirurgica, Boolean reverse, Integer countDias) throws ApplicationBusinessException, ApplicationBusinessException {
		return getPortalPlanejamentoCirurgia2ON().pesquisarPortalPlanejamentoCirurgia(unfSeq, dataBase, especialidade, atuaUnidCirgs, salaCirurgica, reverse, countDias);
	}

	@Override 
	@Secure("#{s:hasPermission('portalPlanejamentoCirurgias','pesquisar')}")
	public PortalPlanejamentoCirurgiasDiaVO pesquisarPortalPlanejamentoCirurgia(
			Short unfSeq, Date dataBase, AghEspecialidades especialidade,
			MbcProfAtuaUnidCirgs atuaUnidCirgs, MbcSalaCirurgica salaCirurgica, Boolean reverse, Integer countDias, Boolean otimizado) 
					throws ApplicationBusinessException, ApplicationBusinessException {
		return getPortalPlanejamentoCirurgia2ON().pesquisarPortalPlanejamentoCirurgia(unfSeq, dataBase, especialidade, atuaUnidCirgs, salaCirurgica, reverse, countDias, otimizado);
	}

	public PortalPlanejamentoCirurgiaON getPortalPlanejamentoCirurgiaON(){
		return portalPlanejamentoCirurgiaON;
	}
	
	public PortalPlanejamentoCirurgia2ON getPortalPlanejamentoCirurgia2ON(){
		return portalPlanejamentoCirurgia2ON;
	}

	@Override
	public List<CirurgiasCanceladasAgendaMedicoVO> pesquisarCirgsCanceladasByMedicoEquipe(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, 
			Integer serMatricula, Short serVinCodigo, Short pucUnfSeq, DominioFuncaoProfissional indFuncaoProf, Short espSeq, Short unfSeq, Integer pacCodigo) throws ApplicationBusinessException {
		return getCirurgiasCanceladasAgendaMedicoON().pesquisarCirgsCanceladasByMedicoEquipe(firstResult, maxResult, orderProperty, asc, serMatricula, serVinCodigo,
				pucUnfSeq, indFuncaoProf, espSeq, unfSeq, pacCodigo);
	}
	
	private CirurgiasCanceladasAgendaMedicoON getCirurgiasCanceladasAgendaMedicoON() {
		return cirurgiasCanceladasAgendaMedicoON;
	}
	

	@Override	          
	public MbcProfAtuaUnidCirgs buscarEquipesPorUsuarioLogado() throws ApplicationBusinessException {
		return getPortalPlanejamentoCirurgiaON().popularFiltrosPorUsuarioLogado();
	}

	@Override
	public Long pesquisarCirgsCanceladasByMedicoEquipeCount(Integer serMatricula, Short serVinCodigo, Short pucUnfSeq,
			DominioFuncaoProfissional indFuncaoProf, Short espSeq, Short unfSeq, Integer pacCodigo) {
		return getCirurgiasCanceladasAgendaMedicoON().pesquisarCirgsCanceladasByMedicoEquipeCount(serMatricula, serVinCodigo,
				pucUnfSeq, indFuncaoProf, espSeq, unfSeq, pacCodigo);
	}

	@Override
	public Long listarAgendaPorUnidadeEspecialidadeEquipePacienteCount(
			Integer pucSerMatricula, Short pucSerVinCodigo, Short pucUnfSeq,
			DominioFuncaoProfissional pucIndFuncaoProf, Short espSeq,
			Short unfSeq, Integer pacCodigo) {
		return getMbcAgendasDAO().listarAgendaPorUnidadeEspecialidadeEquipePacienteCount(pucSerMatricula, pucSerVinCodigo, pucUnfSeq, pucIndFuncaoProf, espSeq, unfSeq, pacCodigo);
	}

	@Override
	public List<MbcAgendas> listarAgendaPorUnidadeEspecialidadeEquipePaciente(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Integer pucSerMatricula, Short pucSerVinCodigo,
			Short pucUnfSeq, DominioFuncaoProfissional pucIndFuncaoProf,
			Short espSeq, Short unfSeq, Integer pacCodigo) {
		return getMbcAgendasDAO().listarAgendaPorUnidadeEspecialidadeEquipePaciente(firstResult, maxResult, orderProperty, asc, pucSerMatricula, pucSerVinCodigo, pucUnfSeq, pucIndFuncaoProf, espSeq, unfSeq, pacCodigo);
	}

	@Override
	public void desatacharMbcAgendas(MbcAgendas mbcAgendas) {
		getMbcAgendasDAO().desatachar(mbcAgendas);
	}

	public PortalPlanejamentoCirurgiaDetalheON getPortalPlanejamentoCirurgiaDetalheON(){
		return portalPlanejamentoCirurgiaDetalheON;
	}
	
	@Override
	public DominioTipoAgendaJustificativa retornarParametroCirurgiasCanceladas(
			Integer agdSeq) throws ApplicationBusinessException {
		return getPortalPlanejamentoCirurgiaDetalheON().retornarParametroCirurgiasCanceladas(agdSeq);
	}
	
	@Override
	public String obterResumoAgendamento(Date dtAgenda, MbcAgendas agenda) {
		return getRemarcarPacienteAgendaON().obterResumoAgendamento(dtAgenda, agenda);
	}
	
	@Override
	@Secure("#{s:hasPermission('remarcarAgendamentoPaciente','remarcar') or s:hasPermission('manterAgendaCirgPac','alterar')}")
	public void remarcarPacienteAgenda(Date dtReagendamento, MbcAgendas agenda, String justificativa, DominioTipoAgendaJustificativa dominio, MbcSalaCirurgica salaCirurgica) throws BaseException {
		getRemarcarPacienteAgendaON().remarcarPacienteAgenda(dtReagendamento, agenda, justificativa, dominio, salaCirurgica);
	}
	
	@Override
	public void validarInclusaoPacienteAgenda(Date dtAgendamento, MbcProfAtuaUnidCirgs prof, Short espSeq, Short unfSeq, Short salaSeqp, String descricaoTurno) throws ApplicationBusinessException {
		getRemarcarPacienteAgendaON().validarInclusaoPacienteAgenda(dtAgendamento, prof, espSeq, unfSeq, salaSeqp, descricaoTurno);
	}
	
	@Override
	public Boolean validarDataReagendamento(Date dtReagendamento, MbcProfAtuaUnidCirgs prof, Short espSeq, Short unfSeq) {
		return getRemarcarPacienteAgendaON().validarDataReagendamento(dtReagendamento, prof, espSeq, unfSeq);
	}
	
	@Override
	public List<MbcSalaCirurgica> pesquisarSalasCirurgicasParaReagendamentoPaciente(
			Date dataAgenda, MbcProfAtuaUnidCirgs atuaUnidCirgs, Short espSeq, Short unfSeq) {
		return getMbcSalaCirurgicaDAO().pesquisarSalasCirurgicasParaReagendamentoPaciente(dataAgenda, atuaUnidCirgs, espSeq, unfSeq);
	}
	
	@Override
	public Boolean verificarEscalaDefinitivaFoiExecutada(Date dtReagendamento, Short unfSeq) {
		return getRemarcarPacienteAgendaON().verificarEscalaDefinitivaFoiExecutada(dtReagendamento, unfSeq);
	}
	
	private RemarcarPacienteAgendaON getRemarcarPacienteAgendaON() {
		return remarcarPacienteAgendaON;
	}
	
	public String obterNomeIntermediarioPacienteAbreviado(String nome) {
		return getRelatorioPortalPlanejamentoCirurgiasRN().obterNomeIntermediarioPacienteAbreviado(nome);
	}
	
	private RelatorioPortalPlanejamentoCirurgiasRN getRelatorioPortalPlanejamentoCirurgiasRN() {
		return relatorioPortalPlanejamentoCirurgiasRN;
	}
	
	@Override
	public List<EscalaPortalPlanejamentoCirurgiasVO> pesquisarAgendasPlanejadas(Date dtAgenda, Integer pucSerMatricula, Short pucSerVinCodigo,
			Short pucUnfSeq, DominioFuncaoProfissional funProf, Short espSeq, Short unfSeq) {
		return getEscalaPortalPlanejamentoCirurgiaON().pesquisarAgendasPlanejadas(dtAgenda, pucSerMatricula, pucSerVinCodigo, pucUnfSeq, funProf, espSeq, unfSeq);
	}
	
	private EscalaPortalPlanejamentoCirurgiaON getEscalaPortalPlanejamentoCirurgiaON() {
		return escalaPortalPlanejamentoCirurgiaON;
	}
	
	@Override
	public List<MbcSalaCirurgica> buscarSalasDisponiveisParaEscala(Date data, Short unfSeq, Short espSeq, 
			Integer matriculaEquipe, Short vinCodigoEquipe, Short unfSeqEquipe, DominioFuncaoProfissional indFuncaoProf) {
		return getMbcSalaCirurgicaDAO().buscarSalasDisponiveisParaEscala(data, unfSeq, espSeq, matriculaEquipe, vinCodigoEquipe, 
				unfSeqEquipe, indFuncaoProf);
	}

	@Override
	public Date atualizaHoraInicioEscala(Date horaInicioEscala,
			Integer pucSerMatriculaParam, Short pucSerVinCodigoParam,
			Short pucUnfSeqParam, DominioFuncaoProfissional pucFuncProfParam,
			Short espSeq, Short unfSeq, Short sciSeqp, Date dataAgenda) throws ApplicationBusinessException {
		return getEscalaPortalPlanejamentoCirurgiaON().atualizaHoraInicioEscala(horaInicioEscala, pucSerMatriculaParam, 
				pucSerVinCodigoParam, pucUnfSeqParam, pucFuncProfParam,	espSeq, unfSeq, sciSeqp, dataAgenda);
	}

	@Override
	public List<EscalaPortalPlanejamentoCirurgiasVO> pesquisarAgendasEmEscala(Date dtAgendaParam, Short sciUnfSeqCombo, Short sciSeqpCombo,
			Short unfSeqParam, Integer pucSerMatriculaParam, Short pucSerVinCodigoParam, Short pucUnfSeqParam, DominioFuncaoProfissional pucFuncProfParam, Short espSeqParam) {
		return getEscalaPortalPlanejamentoCirurgiaON().pesquisarAgendasEmEscala(dtAgendaParam, sciUnfSeqCombo,
				sciSeqpCombo, unfSeqParam, pucSerMatriculaParam, pucSerVinCodigoParam, pucUnfSeqParam,
				pucFuncProfParam, espSeqParam); 
	}
	
	@Override
	public void chamarTelaEscala(Date dtAgenda, Integer pucSerMatricula, Short pucSerVinCodigo, Short pucUnfSeq,
			DominioFuncaoProfissional pucFuncProf, Short unfSeq, Short espSeq) throws ApplicationBusinessException {
		getEscalaPortalPlanejamentoCirurgiaON().chamarTelaEscala(dtAgenda, pucSerMatricula, pucSerVinCodigo, pucUnfSeq, pucFuncProf, unfSeq, espSeq);
	}

	@Override
	public String verificarRegimeMinimoSus(Integer seq) {
		return getEscalaPortalPlanejamentoCirurgiaON().verificarRegimeMinimoSus(seq);
	}

	@Override
	public void verificarHoraTurnoValido(Date dtAgendaParam,
			Short sciUnfSeqCombo, Short sciSeqpCombo, Short unfSeqParam,
			Integer pucSerMatriculaParam, Short pucSerVinCodigoParam,
			Short pucUnfSeqParam, DominioFuncaoProfissional pucFuncProfParam,
			Short espSeqParam, Date horaEscala)
			throws ApplicationBusinessException {
		 getEscalaPortalPlanejamentoCirurgiaON().verificarHoraTurnoValido(pucSerMatriculaParam, pucSerVinCodigoParam, pucUnfSeqParam, pucFuncProfParam, espSeqParam, pucUnfSeqParam, dtAgendaParam, sciSeqpCombo, horaEscala);
	}

	@Override
	@Secure("#{s:hasPermission('visualizarAgendaEscalasPortalPesquisa','consultar')}")
	public PortalPesquisaCirurgiasAgendaEscalaDiaVO pesquisarAgendasEscalasDia(PortalPesquisaCirurgiasParametrosVO portalPesquisaCirurgiasParametrosVO, Date data, Boolean reverse) throws ApplicationBusinessException {
		return getVisualizarAgendaEscalasPortalPesquisaON().pesquisarAgendasEscalasDia(portalPesquisaCirurgiasParametrosVO, data, reverse);
	}
	
	private VisualizarAgendaEscalasPortalPesquisaON getVisualizarAgendaEscalasPortalPesquisaON(){
		return visualizarAgendaEscalasPortalPesquisaON;
	}

	@Override
	public List<Date> buscarTodasDatasPaciente(PortalPesquisaCirurgiasParametrosVO parametros) throws ApplicationBusinessException {
		return getVisualizarAgendaEscalasPortalPesquisaON().buscarTodasDatasPaciente(parametros);
	}

	@Override
	public Long listarAghCaractUnidFuncionaisCount(String objPesquisa) {
		return getPortalPesquisaCirurgiasON().listarAghCaractUnidFuncionaisCount(objPesquisa);
	}

	@Override
	public Long listarCaracteristicaSalaCirgPorUnidadeCount(String pesquisa, Short unfSeq) {
		return getPortalPesquisaCirurgiasON().listarCaracteristicaSalaCirgPorUnidadeCount(pesquisa, unfSeq);
	}
	
	@Override
	public List<MbcSalaCirurgica> buscarSalasCirurgicasAtivasPorUnfSeqSeqp(Short unfSeq, Short seqp){
		return getMbcSalaCirurgicaDAO().buscarSalasCirurgicasAtivasPorUnfSeqSeqp(unfSeq, seqp);
	}
	
	@Override
	public Long buscarSalasCirurgicasAtivasPorUnfSeqSeqpCount(Short unfSeq, Short seqp){
		return getMbcSalaCirurgicaDAO().buscarSalasCirurgicasAtivasPorUnfSeqSeqpCount(unfSeq, seqp);
	}
	
	@Override
	public void validaSituacaoAgenda(MbcAgendas agenda) throws ApplicationBusinessException{
		getRelatorioEscalaDeSalasON().validaSituacaoAgenda(agenda);
	};
	
	public MbcAgendaSolicEspecial obterMbcAgendaSolicEspecialPorNciSeqUnfseq(Integer agdSeq, Short nciSeq, Short seqp) {
		return getMbcAgendaSolicEspecialDAO().obterMbcAgendaSolicEspecialPorNciSeqUnfseq(agdSeq, nciSeq, seqp);
	}
	
	public MbcNecessidadeCirurgica obterMbcNecessidadeCirurgicaPorId(Short seq, Enum[] innerJoins, Enum[] leftJoins) {
		return mbcNecessidadeCirurgicaDAO.obterPorChavePrimaria(seq, innerJoins, leftJoins);
	}
}