package br.gov.mec.aghu.transplante.business;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.configuracao.dao.AghCidDAO;
import br.gov.mec.aghu.configuracao.dao.AghEspecialidadesDAO;
import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.dominio.DominioOrdenacRelatorioSitPacOrgao;
import br.gov.mec.aghu.dominio.DominioOrdenacRelatorioSitPacTmo;
import br.gov.mec.aghu.dominio.DominioRepeticaoRetorno;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoTmo;
import br.gov.mec.aghu.dominio.DominioSituacaoTransplante;
import br.gov.mec.aghu.dominio.DominioTipoAlogenico;
import br.gov.mec.aghu.dominio.DominioTipoOrgao;
import br.gov.mec.aghu.dominio.DominioTipoRetorno;
import br.gov.mec.aghu.dominio.DominioTipoTransplante;
import br.gov.mec.aghu.exames.dao.AelResultadoExameDAO;
import br.gov.mec.aghu.exames.dao.AelSolicitacaoExameDAO;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.AipRegSanguineos;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MtxColetaMaterialTmo;
import br.gov.mec.aghu.model.MtxComorbidade;
import br.gov.mec.aghu.model.MtxComorbidadePaciente;
import br.gov.mec.aghu.model.MtxContatoPacientes;
import br.gov.mec.aghu.model.MtxCriterioPriorizacaoTmo;
import br.gov.mec.aghu.model.MtxDoencaBases;
import br.gov.mec.aghu.model.MtxExameUltResults;
import br.gov.mec.aghu.model.MtxItemPeriodoRetorno;
import br.gov.mec.aghu.model.MtxMotivoAlteraSituacao;
import br.gov.mec.aghu.model.MtxOrigens;
import br.gov.mec.aghu.model.MtxPeriodoRetorno;
import br.gov.mec.aghu.model.MtxProcedimentoTransplantes;
import br.gov.mec.aghu.model.MtxRegistrosTMO;
import br.gov.mec.aghu.model.MtxResultadoExames;
import br.gov.mec.aghu.model.MtxTipoRetorno;
import br.gov.mec.aghu.model.MtxTransplantes;
import br.gov.mec.aghu.paciente.dao.AipPacientesDAO;
import br.gov.mec.aghu.transplante.dao.MtxColetaMaterialTmoDAO;
import br.gov.mec.aghu.transplante.dao.MtxComorbidadeDAO;
import br.gov.mec.aghu.transplante.dao.MtxComorbidadePacienteDAO;
import br.gov.mec.aghu.transplante.dao.MtxContatoPacientesDAO;
import br.gov.mec.aghu.transplante.dao.MtxCriterioPriorizacaoTmoDAO;
import br.gov.mec.aghu.transplante.dao.MtxDoencaBasesDAO;
import br.gov.mec.aghu.transplante.dao.MtxExameUltResultsDAO;
import br.gov.mec.aghu.transplante.dao.MtxExtratoTransplantesDAO;
import br.gov.mec.aghu.transplante.dao.MtxItemPeriodoRetornoDAO;
import br.gov.mec.aghu.transplante.dao.MtxMotivoAlteraSituacaoDAO;
import br.gov.mec.aghu.transplante.dao.MtxOrigensDAO;
import br.gov.mec.aghu.transplante.dao.MtxPeriodoRetornoDAO;
import br.gov.mec.aghu.transplante.dao.MtxProcedimentoTransplantesDAO;
import br.gov.mec.aghu.transplante.dao.MtxRegistrosTMODAO;
import br.gov.mec.aghu.transplante.dao.MtxResultadoExamesDAO;
import br.gov.mec.aghu.transplante.dao.MtxTipoRetornoDAO;
import br.gov.mec.aghu.transplante.dao.MtxTransplantesDAO;
import br.gov.mec.aghu.transplante.vo.AgendaTransplanteRetornoVO;
import br.gov.mec.aghu.transplante.vo.CriteriosPriorizacaoAtendVO;
import br.gov.mec.aghu.transplante.vo.ExtratoAlteracoesListaOrgaosVO;
import br.gov.mec.aghu.transplante.vo.FiltroTempoPermanenciaListVO;
import br.gov.mec.aghu.transplante.vo.FiltroTempoSobrevidaTransplanteVO;
import br.gov.mec.aghu.transplante.vo.FiltroTransplanteOrgaoVO;
import br.gov.mec.aghu.transplante.vo.GerarExtratoListaTransplantesVO;
import br.gov.mec.aghu.transplante.vo.ListarTransplantesVO;
import br.gov.mec.aghu.transplante.vo.PacienteAguardandoTransplanteOrgaoVO;
import br.gov.mec.aghu.transplante.vo.PacienteTransplantadosOrgaoVO;
import br.gov.mec.aghu.transplante.vo.PacienteTransplanteMedulaOsseaVO;
import br.gov.mec.aghu.transplante.vo.RelatorioExtratoTransplantesPacienteVO;
import br.gov.mec.aghu.transplante.vo.RelatorioPermanenciaPacienteListaTransplanteVO;
import br.gov.mec.aghu.transplante.vo.RelatorioSobrevidaPacienteTransplanteVO;
import br.gov.mec.aghu.transplante.vo.RelatorioTransplanteOrgaosSituacaoVO;
import br.gov.mec.aghu.transplante.vo.RelatorioTransplanteTmoSituacaoVO;
import br.gov.mec.aghu.transplante.vo.ResultadoExameCulturalVO;
import br.gov.mec.aghu.transplante.vo.TiposExamesPacienteVO;
import br.gov.mec.aghu.transplante.vo.TotalizadorAgendaTransplanteVO;

@Modulo(ModuloEnum.TRANSPLANTE)
@SuppressWarnings({ "PMD.ExcessiveClassLength" })
@Stateless
public class TransplanteFacade extends BaseFacade implements ITransplanteFacade {
	
	private static final long serialVersionUID = 7621673807612165550L;
	
	@Inject
	private MtxContatoPacientesDAO mtxContatoPacientesDAO;
	
	@Inject
	private MtxExtratoTransplantesDAO mtxExtratoTransplantesDAO;
	
	@Inject
	private MtxDoencaBasesDAO mtxDoencaBasesDAO;
	
	@Inject 
	private MtxColetaMaterialTmoDAO mtxColetaMaterialTmoDAO;
	
	@Inject
	private AipPacientesDAO aipPacientesDAO;
	
	@Inject
	private MtxTransplantesDAO mtxTransplantesDAO;
	
	@Inject
	private AelSolicitacaoExameDAO aelSolicitacaoExameDAO;
	
	@Inject
	private AelResultadoExameDAO aelResultadoExameDAO;
	
	@EJB
	private InformarDadoMaterialRecebidoTmoON informarDadoMaterialRecebidoTmoON;
	
	@EJB
	private MtxTransplantesRN mtxTransplantesRN;
	
	@EJB
	private MtxPeriodoRetornoRN mtxPeriodoRetornoRN;
	
	@EJB
	private MtxResultadoExamesTransplanteRN mtxResultadoExamesTransplanteRN;
	
	@EJB
	private MtxCriterioPriorizacaoTmoRN mtxCriterioPriorizacaoTmoRN;
	
	@EJB
	private MtxCriterioPriorizacaoTmoON mtxCriterioPriorizacaoTmoON;
	
	@EJB
	private MtxDoencasBaseON mtxDoencasBaseON;
	
	@EJB
	private MtxOrigemPacienteRN origemPacienteRN;
	
	@EJB
	private MtxOrigemPacienteON origemPacienteON;
	
	@EJB
	private MtxComorbidadeRN mtxComorbidadeRN;
	
	@EJB
	private MtxComorbidadeON mtxComorbidadeON;
	
	@EJB
	private MtxTransplantesON mtxTransplantesON;
	
	@EJB
	private MtxTipoRetornoON mtxTipoRetornoON;
	
	@EJB
	private MtxTipoRetornoRN mtxTipoRetornoRN;
	
	@Inject
	private AghCidDAO aghCidDao;
	
	@Inject 
	private MtxCriterioPriorizacaoTmoDAO mtxCriterioPriorizacaoTmoDAO;
	
	@Inject
	private MtxOrigensDAO mtxOrigensDAO;
	
	@Inject
	private MtxResultadoExamesDAO mtxResultadoExamesDAO;
	
	@EJB
	private MtxExameUltResultsRN mtxExameUltResultsRN;
	
	@Inject
	private MtxExameUltResultsDAO mtxExameUltResultsDAO;
	
	@Inject
	private MtxMotivoAlteraSituacaoDAO mtxMotivoAlteraSituacaoDAO;
	
	@Inject	
	private MtxComorbidadeDAO mtxComorbidadeDAO;
	
	@Inject
	private MtxComorbidadePacienteDAO mtxComorbidadePacienteDAO;
	
	@EJB
	private RelatoriosTransplanteON relatoriosTransplanteON;
	
	@Inject
	private MtxProcedimentoTransplantesDAO mtxProcedimentoTransplantesDAO;

	@EJB
	private RelatorioTempoPacienteListaTransplanteRN relatorioTempoPacienteListaTransplanteRN;
	
	@EJB
	private MtxProcedimentoTransplantesON mtxProcedimentoTransplantesON;
	
	@EJB
	private MtxRegistrosTMORN mtxRegistrosTMORN;
	
	@Inject
	private MtxRegistrosTMODAO mtxRegistrosTMODAO;

	@Inject	
	private MtxTipoRetornoDAO mtxTipoRetornoDAO;
	
	@Inject
	private MtxItemPeriodoRetornoDAO mtxItemPeriodoRetornoDAO;
	
	@Inject
	private AghEspecialidadesDAO aghEspecialidadesDAO;
	
	
	@Inject
	private MtxPeriodoRetornoDAO mtxPeriodoRetornoDAO;
	
	@EJB
	private RelatorioTransplanteOrgaoSituacaoON relatorioTransplanteOrgaoSituacaoON;
	
	@EJB
	private RelatorioTransplanteTmoSituacaoON relatorioTransplanteTmoSituacaoON;
	
	@Override
	public List<MtxContatoPacientes> obterListaContatoPacientesPorCodigoPaciente(Integer pacCodigo) {
		
		return this.mtxContatoPacientesDAO.obterListaContatoPacientesPorCodigoPaciente(pacCodigo);
	}

	@Override
	public List<MtxDoencaBases> obterListaDoencaBasePorTipoOrgao(DominioTipoOrgao tipoOrgao) {
		
		return this.mtxDoencaBasesDAO.obterListaDoencaBasePorTipoOrgao(tipoOrgao);
	}

	@Override
	public AipPacientes buscarPacientePorCodTransplante(Integer codTransplante) {
		return aipPacientesDAO.buscarPacientePorTransplanteSeq(codTransplante);
	}

	@Override
	public void gravarColetaMaterialTMO(MtxColetaMaterialTmo coletaMaterialTmo) {
		mtxColetaMaterialTmoDAO.persistir(coletaMaterialTmo);
	}

	@Override
	public MtxColetaMaterialTmo buscarColetaMaterialTMOPorTransplanteSeq(Integer codTransplante) {
		return mtxColetaMaterialTmoDAO.buscarColetaMaterialTmoPorCodTransplante(codTransplante);
	}

	@Override
	public void atualizarColetaMaterialTMO(MtxColetaMaterialTmo coletaMaterialTmo) {
		mtxColetaMaterialTmoDAO.merge(coletaMaterialTmo);
	}

	@Override
	public MtxTransplantes obterTransplantePorSeq(Integer codTransplante) {
		return mtxTransplantesDAO.obterTransplantePorSeq(codTransplante);
	}

	@Override
	public BigDecimal buscarValorLeucocitosTotais(Integer codigo) {
		return aelSolicitacaoExameDAO.obterValorLeucocitosTotais(codigo);
	}

	@Override
	public List<ResultadoExameCulturalVO> obterListaExameCultural(Integer codigo, Integer codMaterial) {
		return aelResultadoExameDAO.obterListaResultadosExameCultural(codigo, codMaterial);
	}

	@Override
	public String buscarValorCampoCD34(Integer codigo, Integer codMaterial) {
		return aelSolicitacaoExameDAO.obterValorCD34(codigo, codMaterial);
	}

	@Override
	public void validarCamposObrigatoriosColetaMaterialTmo(MtxColetaMaterialTmo coletaMaterialTmo) throws ApplicationBusinessException, BaseListException {
		informarDadoMaterialRecebidoTmoON.validarCamposObrigatorios(coletaMaterialTmo);
	}

	@Override
	public void salvarTransplanteComManutencaoContatosRegSanguineo(
			MtxTransplantes transplante, AipRegSanguineos regSanguineo, AipPacientes paciente,
			List<MtxContatoPacientes> listaContatosIncluidos, List<MtxContatoPacientes> listaContatosExcluidos) 
			throws ApplicationBusinessException, BaseListException {
		
		this.mtxTransplantesRN.salvarTransplanteListaContatoPaciente(transplante, regSanguineo, paciente, listaContatosIncluidos, listaContatosExcluidos);
	}
	
	@Override
	public List<CriteriosPriorizacaoAtendVO> pesquisarCriteriosPriorizacaoAtendimento(CriteriosPriorizacaoAtendVO filtro,	
			Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {		
		return mtxCriterioPriorizacaoTmoDAO.pesquisarCriteriosPriorizacaoAtendimento(filtro, firstResult, maxResults, orderProperty, asc);
	}

	@Override
	public Long pesquisarCriteriosPriorizacaoAtendimentoCount(CriteriosPriorizacaoAtendVO filtro) {
		return mtxCriterioPriorizacaoTmoDAO.pesquisarCriteriosPriorizacaoAtendimentoCount(filtro);
	}

	@Override
	public List<AghCid> pesquisarCidPorSeqCodDescricao(String pesquisa) {
		return aghCidDao.pesquisarCidPorSeqCodDescricao(pesquisa);
	}

	@Override
	public Long pesquisarCidPorSeqCodDescricaoCount(String pesquisa) {
		return aghCidDao.pesquisarCidPorSeqCodDescricaoCount(pesquisa);
	}

	@Override
	public boolean verificarExistenciaRegistro(Integer cidSeq) {
		return mtxCriterioPriorizacaoTmoRN.verificarExistenciaRegistro(cidSeq);
	}

	@Override
	public void gravarMtxCriterioPriorizacaoTmo(MtxCriterioPriorizacaoTmo obj) throws ApplicationBusinessException {
		mtxCriterioPriorizacaoTmoRN.gravarUsuarioDataCriacao(obj);
	}

	@Override
	public void atualizarMtxCriterioPriorizacaoTmo(MtxCriterioPriorizacaoTmo obj) throws ApplicationBusinessException{
		mtxCriterioPriorizacaoTmoRN.atualizarMtxCriterioPriorizacaoTmo(obj);
		
	}
	@Override
	public AghCid obterCidSeq(Integer seq){
		return aghCidDao.obterCidSeq(seq);
	}

	@Override
	public void validarCamposObrigatorios(CriteriosPriorizacaoAtendVO filtro) throws ApplicationBusinessException, BaseListException {
		mtxCriterioPriorizacaoTmoON.validarCamposObrigatorios(filtro);
	}
	
	//41770
	@Override
    public List<MtxOrigens> pesquisarMtxOrigensPorSeqCodDescricao(String pesquisa) {
    	return this.mtxOrigensDAO.pesquisarMtxOrigensPorSeqCodDescricao(pesquisa);
    }
    
    @Override
    public Long pesquisarMtxOrigensPorSeqCodDescricaoCount(String pesquisa) {
    	return this.mtxOrigensDAO.pesquisarMtxOrigensPorSeqCodDescricaoCount(pesquisa);
    }
    
    @Override
    public String calcularIdadeIngresso(Date dtIngresso, Date dtNascimento){
    	return this.mtxTransplantesRN.calcularIdadeIngresso(dtIngresso, dtNascimento);
    }
    @Override
    public Double obterEscore(Integer seqCid, Date dtIngresso, Date dtNascimento){
    	return this.mtxTransplantesRN.obterEscore(seqCid, dtIngresso, dtNascimento);
    }
    @Override
    public void persitirPacienteListaTransplanteTMO(MtxTransplantes transplante, AipRegSanguineos regSanguineos, AipPacientes paciente, MtxCriterioPriorizacaoTmo statusDoenca, boolean grupoSanguineoFatorAlterado) throws ApplicationBusinessException, BaseListException{
    	this.mtxTransplantesRN.persitirPacienteListaTransplanteTMO(transplante, regSanguineos, paciente, statusDoenca, grupoSanguineoFatorAlterado);
    }
	//41770 FIM

	public MtxCriterioPriorizacaoTmoDAO getMtxCriterioPriorizacaoTmoDAO() {
		return mtxCriterioPriorizacaoTmoDAO;
	}
	
	//41773
	@Override
	public List<ListarTransplantesVO> obterPacientesAguardandoTransplantePorFiltro(ListarTransplantesVO filtro, Integer firstResult, Integer maxResults, 
			String orderProperty, boolean asc) throws ApplicationBusinessException{
		List<ListarTransplantesVO> lista = new ArrayList<ListarTransplantesVO>();
		lista = mtxTransplantesRN.obterPacientesAguardandoTransplantePorFiltro(filtro, firstResult, maxResults, orderProperty, asc);
		return lista;
	}
	@Override
	public Long obterPacientesAguardandoTransplantePorFiltroCount(ListarTransplantesVO filtro){
		return mtxTransplantesDAO.obterPacientesAguardandoTransplantePorFiltroCount(filtro);
	}
	@Override
	public void validarTipoTransplante(ListarTransplantesVO filtro) throws ApplicationBusinessException{
		mtxTransplantesON.validarTipoTransplante(filtro);
	}
	//41773 FIM
	
	@Override
	public List<MtxCriterioPriorizacaoTmo> obterStatusDoencaPaciente(
			DominioSituacaoTmo tipo) {
		return mtxCriterioPriorizacaoTmoDAO.obterStatusDoencaPaciente(tipo);
	}

	@Override
	public MtxCriterioPriorizacaoTmo obterCoeficiente(Integer statusDoenca) {
		return mtxCriterioPriorizacaoTmoDAO.obterCoeficiente(statusDoenca);
	}
	
	@Override
	public List<MtxOrigens> pesquisarOrigemPaciente(MtxOrigens mtxOrigens, Integer firstResult, Integer maxResults, String orderProperty, boolean asc){
		return mtxOrigensDAO.pesquisarMtxOrigensPorSituacaoDesc(mtxOrigens, firstResult, maxResults, orderProperty, asc);
	}
	
	@Override
	public Long pesquisarOrigemPacienteCount(MtxOrigens mtxOrigens, boolean isEqual){
		return mtxOrigensDAO.pesquisarMtxOrigensPorSituacaoDescCount(mtxOrigens, isEqual);
	}
	
	@Override
	public void gravarAtualizarOrigemPaciente(MtxOrigens mtxOrigens) throws ApplicationBusinessException, BaseException{
		mtxOrigensDAO.gravarAtualizarOrigemPaciente(mtxOrigens);
	}
	
	@Override
	public void validarInclusaoOrigemPaciente(MtxOrigens mtxOrigens) throws ApplicationBusinessException, BaseException{
		origemPacienteRN.validarInclusaoOrigemPaciente(mtxOrigens);
	}
	
	@Override
	public void validarInputOrigemPaciente(MtxOrigens mtxOrigens) throws ApplicationBusinessException, BaseException {
		origemPacienteON.validarInputOrigemPaciente(mtxOrigens);
	}
	
	@Override
	public void excluirOrigemPaciente(MtxOrigens mtxOrigens) throws ApplicationBusinessException, BaseException{
		
		mtxOrigensDAO.excluirOrigemPaciente(mtxOrigens);
	}
	@Override
	public void validarExclusaoOrigemPaciente(MtxOrigens mtxOrigens) throws ApplicationBusinessException, BaseException{
		origemPacienteRN.validarExclusaoOrigemPaciente(mtxOrigens);
	}
	
	@Override
	public List<MtxDoencaBases> obterListaDoencasBase(Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, MtxDoencaBases mtxDoencaBases) {
		return mtxDoencaBasesDAO.obterListaDoencasBase(firstResult, maxResults, orderProperty, asc, mtxDoencaBases);
	}
	
	@Override
	public Long obterListaDoencasBaseCount(MtxDoencaBases mtxDoencaBases) {
		return mtxDoencaBasesDAO.obterListaDoencasBaseCount(mtxDoencaBases);
	}
	
	@Override
	public boolean excluirDoencaBase(MtxDoencaBases mtxDoencaBase){
		return this.mtxDoencasBaseON.excluirDoencaBase(mtxDoencaBase);
	}
	
	@Override
	public boolean inserirDoencaBase(MtxDoencaBases mtxDoencaBase){
		return this.mtxDoencasBaseON.inserirDoencaBase(mtxDoencaBase);	
	}
	
	public boolean atualizarDoencaBase(MtxDoencaBases mtxDoencaBase){
		return this.mtxDoencasBaseON.atualizarDoencaBase(mtxDoencaBase);
	}
	
	@Override
	public void inserirMtxExameUltResults(MtxExameUltResults mtxExameUltResults) throws ApplicationBusinessException{
		mtxExameUltResultsRN.inserirMtxExameUltResults(mtxExameUltResults);
	}
	
	@Override
	public void atualizarMtxExameUltResults(MtxExameUltResults mtxExameUltResults) throws ApplicationBusinessException{
		mtxExameUltResultsRN.atualizarMtxExameUltResults(mtxExameUltResults);
	}
	
	@Override
	public void excluirMtxExameUltResults(MtxExameUltResults mtxExameUltResults) throws ApplicationBusinessException{
		mtxExameUltResultsRN.excluirMtxExameUltResults(mtxExameUltResults);
	}
	
	@Override
	public List<MtxExameUltResults> pesquisarExamesLaudosCampo(String exameSigla, Integer laudoSeq, DominioSituacao situacao, 
															   Integer firstResult, Integer maxResult, String orderProperty, boolean asc){
		return mtxExameUltResultsDAO.pesquisarExamesLaudosCampo(exameSigla, laudoSeq, situacao, firstResult, maxResult, orderProperty, asc);
	}
	
	@Override
	public Long pesquisarExamesLaudosCampoCount(String exameSigla, Integer laudoSeq, DominioSituacao situacao){
		return mtxExameUltResultsDAO.pesquisarExamesLaudosCampoCount(exameSigla, laudoSeq, situacao);
	}
	
	@Override
	public List<MtxMotivoAlteraSituacao> obterListaMotivoAlteraSituacao(Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, MtxMotivoAlteraSituacao mtxMotivoAlteraSituacao) {
		return mtxMotivoAlteraSituacaoDAO.obterListaMotivoAlteraSituacao(firstResult, maxResults, orderProperty, asc, mtxMotivoAlteraSituacao);
	}
	
	@Override
	public Long obterListaMotivoAlteraSituacaoCount(MtxMotivoAlteraSituacao mtxMotivoAlteraSituacao) {
		return mtxMotivoAlteraSituacaoDAO.obterListaMotivoAlteraSituacaoCount(mtxMotivoAlteraSituacao);
	}
	
	@Override
	public void inserirMotivoAlteraSituacao(MtxMotivoAlteraSituacao mtxMotivoAlteraSituacao){
		this.mtxMotivoAlteraSituacaoDAO.persistir(mtxMotivoAlteraSituacao);
		
	}
	
	@Override
	public void atualizarMotivoAlteraSituacao(MtxMotivoAlteraSituacao mtxMotivoAlteraSituacao){
		this.mtxMotivoAlteraSituacaoDAO.atualizar(mtxMotivoAlteraSituacao);
	}
	
	@Override
	public boolean excluirMotivoAlteraSituacao(MtxMotivoAlteraSituacao mtxMotivoAlteraSituacao){
		if(mtxMotivoAlteraSituacaoDAO.consultaExtratoTransplante(mtxMotivoAlteraSituacao)){
			return false;
		}else{
			MtxMotivoAlteraSituacao entidade = this.mtxMotivoAlteraSituacaoDAO.obterPorChavePrimaria(mtxMotivoAlteraSituacao.getSeq());
			this.mtxMotivoAlteraSituacaoDAO.remover(entidade);
			return true;
		}
				
	}
	
	//#46203
		@Override
		public List<MtxComorbidade> pesquisarComorbidade(MtxComorbidade mtxComorbidade, Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {
			return mtxComorbidadeON.pesquisarComorbidade(mtxComorbidade, firstResult, maxResults, orderProperty, asc);
		}

		@Override
		public Long pesquisarComorbidadeCount(MtxComorbidade mtxComorbidade) {
			return mtxComorbidadeDAO.pesquisarComorbidadePorDescricaoTipoAtivoCount(mtxComorbidade);
		}
		
		@Override
		public List<MtxComorbidade> pesquisarDoenca(MtxComorbidade mtxComorbidade) {
			return mtxComorbidadeON.pesquisarDoenca(mtxComorbidade);
		}

		@Override
		public Long pesquisarDoencaCount(MtxComorbidade mtxComorbidade) {
			return mtxComorbidadeDAO.pesquisarDoencaCount(mtxComorbidade);
		}

		@Override
		public void gravarAtualizarComorbidade(MtxComorbidade mtxComorbidade) throws ApplicationBusinessException, BaseException {
			mtxComorbidadeDAO.gravarAtualizarComorbidade(mtxComorbidade);
		}

		@Override
		public void validarInclusaoComorbidade(MtxComorbidade mtxComorbidade) throws ApplicationBusinessException, BaseException {
			mtxComorbidadeRN.validarInclusaoComorbidade(mtxComorbidade);
		}
		
		/**
		 * #41790
		 * */
		public List<RelatorioPermanenciaPacienteListaTransplanteVO> gerarRelatorioPermanenciaPacienteListaTransplante(FiltroTempoPermanenciaListVO filtro){
			return this.relatorioTempoPacienteListaTransplanteRN.montarRelatorioTempoPermanenciaPacienteLista(filtro);
		}

		@Override
		public void validarRegrasTelaRelatorioFilaTransplante(FiltroTempoPermanenciaListVO filtro) throws BaseListException {
			this.relatoriosTransplanteON.validarRegrasTelaRelatorioFilaTransplante(filtro); 
		} 

		@Override
		public boolean verificaTipoTMOFilaTransplante(
				FiltroTempoPermanenciaListVO filtro) {
			return this.relatoriosTransplanteON.verificaTipoTMOFilaTransplante(filtro);
		}

		@Override
		public boolean verificaTipoTransplanteRelatorioFila(
				FiltroTempoPermanenciaListVO filtro) {
			return this.relatoriosTransplanteON.verificaTipoTransplanteRelatorioFila(filtro);
		}
		
		@Override
		public String gerarRelatorioTempoPermanenciaCSV(List<RelatorioPermanenciaPacienteListaTransplanteVO> colecao) throws BaseException, IOException{
			return this.relatoriosTransplanteON.gerarRelatorioTempoPermanenciaCSV(colecao);
		}

		@Override
		public List<RelatorioSobrevidaPacienteTransplanteVO> gerarRelatorioSobrevidaTransplante(
				FiltroTempoSobrevidaTransplanteVO filtro) {
			return relatorioTempoPacienteListaTransplanteRN.montarRelatorioSobrevidaPacienteTransplante(filtro);
		}

		@Override
		public String gerarRelatorioSobrevidaTransplanteCSV(
				List<RelatorioSobrevidaPacienteTransplanteVO> colecao)
				throws BaseException, IOException {
			return null;
		}
		
		/**
		 * #41792
		 */
		public void validarRegrasTelaRelatorioSobrevidaTransplante(FiltroTempoSobrevidaTransplanteVO filtro) throws BaseListException{
			this.relatoriosTransplanteON.validarRegrasTelaRelatorioSobrevidaTransplante(filtro); 
		}

		@Override
		public String gerarRelatorioSobrevidaCSV(
				List<RelatorioSobrevidaPacienteTransplanteVO> colecao)
				throws BaseException, IOException {
			return this.relatoriosTransplanteON.gerarRelatorioSobrevidaCSV(colecao);
		}
		
		@Override
		public List<RelatorioExtratoTransplantesPacienteVO> pesquisarExtratoTransplantePorFiltros(Integer pacCodigo,FiltroTempoPermanenciaListVO filtro){
			return mtxExtratoTransplantesDAO.pesquisarExtratoTransplantePorFiltros(pacCodigo, filtro);
		}
		
		@Override
		public String gerarRelatorioExtratoTransplantePacienteCSV(List<RelatorioExtratoTransplantesPacienteVO> colecao)
				throws BaseException, IOException{
			return relatoriosTransplanteON.gerarRelatorioExtratoTransplantePacienteCSV(colecao);
		}
		 
		@Override
		public List<RelatorioExtratoTransplantesPacienteVO> formatarExtratoPacienteTransplante(List<RelatorioExtratoTransplantesPacienteVO> colecao){
			return relatoriosTransplanteON.formatarExtratoPacienteTransplante(colecao);
		}
		
		@Override
		public String validarDatas(FiltroTempoPermanenciaListVO filtro,int numeroMaximo){
			return relatoriosTransplanteON.validarDatas(filtro, numeroMaximo);
		}
		
		@Override
		public List<MtxProcedimentoTransplantes> obterProcedimentosAssociados(Integer firstResult, Integer maxResults, String orderProperty,
				boolean asc, MtxProcedimentoTransplantes mtxProcedimentoTransplantes, String procedimento){
			return this.mtxProcedimentoTransplantesDAO.obterProcedimentosAssociados(firstResult, maxResults, orderProperty, asc, mtxProcedimentoTransplantes, procedimento);
		}
		
		@Override
		public Long obterListaProcedimentoTransplantesCount(String procedimento) {
			return this.mtxProcedimentoTransplantesDAO.obterListaProcedimentoTransplantesCount(procedimento);
		}
		
		@Override
		public List<MbcProcedimentoCirurgicos> obterListaProcedimentoTransplantes(String procedimento) {
			return this.mtxProcedimentoTransplantesDAO.obterListaProcedimentoTransplantes(procedimento);
		}
		
		@Override
		public Long pesquisarListaProcedimentoTransplantesCount(MtxProcedimentoTransplantes mtxProcedimentoTransplantes, String procedimento){
			return this.mtxProcedimentoTransplantesDAO.pesquisarListaProcedimentoTransplantesCount(mtxProcedimentoTransplantes ,procedimento);
		}
		
		@Override
		public List<MtxProcedimentoTransplantes> verificarMtxProcedimentoTransplantes(MtxProcedimentoTransplantes mtxProcedimentoTransplantes){
			return this.mtxProcedimentoTransplantesDAO.verificarMtxProcedimentoTransplantes(mtxProcedimentoTransplantes);
		}
		
		@Override
		public void editarProcedimentoTransplantes(MtxProcedimentoTransplantes mtxProcedimentoTransplantes) throws ApplicationBusinessException{
			this.mtxProcedimentoTransplantesON.editarProcedimentoTransplantes(mtxProcedimentoTransplantes);
		}
		

		@Override
		public void adicionarProcedimentoTransplantes(MtxProcedimentoTransplantes mtxProcedimentoTransplantes) throws ApplicationBusinessException{
			this.mtxProcedimentoTransplantesON.adicionarProcedimentoTransplantes(mtxProcedimentoTransplantes);
		}
		
		@Override
		public void excluirProcedimentoTransplantes(MtxProcedimentoTransplantes mtxProcedimentoTransplantes) throws ApplicationBusinessException{
			this.mtxProcedimentoTransplantesON.excluirProcedimentoTransplantes(mtxProcedimentoTransplantes);
		}
		
		
		
		
		
		@Override
		public List<RelatorioExtratoTransplantesPacienteVO> pesquisarPacienteComObtitoListaTranplante(FiltroTempoPermanenciaListVO  filtro,Integer masSeq){
			return mtxTransplantesDAO.pesquisarPacienteComObtitoListaTranplante(filtro, masSeq);
		}
		
		@Override
		public String gerarRelatorioPacientesComObitoListaEsperaTranplenteCSV(List<RelatorioExtratoTransplantesPacienteVO> colecao) throws BaseException, IOException{
			return relatoriosTransplanteON.gerarRelatorioPacientesComObitoListaEsperaTranplenteCSV(colecao);
		}
		
		@Override
		public List<RelatorioExtratoTransplantesPacienteVO> removerRepetidosLista(List<RelatorioExtratoTransplantesPacienteVO> listaRelatorio){
			return relatoriosTransplanteON.removerRepetidosLista(listaRelatorio);
		}
		
	@Override
	public List<ListarTransplantesVO> obterPacientesTransplantadosPorFiltro(ListarTransplantesVO filtro, Integer firstResult, Integer maxResults, 
			String orderProperty, boolean asc) throws ApplicationBusinessException{
		List<ListarTransplantesVO> lista = new ArrayList<ListarTransplantesVO>();
		lista = mtxTransplantesRN.obterPacientesTransplantadosPorFiltro(filtro, firstResult, maxResults, orderProperty, asc);
		return lista;
	}
	@Override
	public Long obterPacientesTransplantadosPorFiltroCount(ListarTransplantesVO filtro){
		return mtxTransplantesDAO.obterPacientesAguardandoTransplantePorFiltroCount(filtro);
	}
	@Override
	public String obterIdadeFormatada(Date dataNascimento){
		return mtxTransplantesRN.obterIdadeFormatada(dataNascimento);
	}
	@Override
	public MtxTransplantes obterTransplanteEdicao(Integer transplanteSeq){
		return mtxTransplantesDAO.obterTransplanteEdicao(transplanteSeq);
	}
	
	@Override
	public List<PacienteAguardandoTransplanteOrgaoVO> obterListaPacientesAguardandoTransplanteOrgao(FiltroTransplanteOrgaoVO filtro, Integer firstResult, Integer maxResults, 
			String orderProperty, boolean asc, boolean paginacao) {
		return mtxTransplantesDAO.obterListaPacientesAguardandoTransplanteOrgao(filtro, firstResult, maxResults, orderProperty, asc, paginacao);
	}

	@Override
	public void verificarDoencasPacientesON(List<PacienteAguardandoTransplanteOrgaoVO> listaRetorno, boolean isCount) throws CloneNotSupportedException, ApplicationBusinessException {
		mtxTransplantesRN.verificarDoencasPacientesON(listaRetorno, isCount);
	}

	@Override
	public Boolean verificarExisteResultadoExame(Integer pacCodigo) {
		return aelResultadoExameDAO.verificarExisteResultadoExame(pacCodigo);
	}
	
	@Override
	public List<PacienteAguardandoTransplanteOrgaoVO> obterListaPacientesRetiradosOrgao(FiltroTransplanteOrgaoVO filtro, Integer firstResult, Integer maxResults, 
			String orderProperty, boolean asc, boolean paginacao) throws ApplicationBusinessException, CloneNotSupportedException {
		return mtxTransplantesRN.obterListaPacientesRetiradosOrgao(filtro, firstResult, maxResults, orderProperty, asc, paginacao);
	}
	
	@Override
	public List<PacienteAguardandoTransplanteOrgaoVO> obterListaPacientesInativosAguardandoTransplanteOrgao(FiltroTransplanteOrgaoVO filtro, Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc,
			boolean paginacao) {
		return mtxTransplantesDAO.obterListaPacientesInativoAguardandoTransplanteOrgao(filtro, firstResult, maxResults, orderProperty, asc, paginacao);
	}

	@Override
	public void verificarDoencasPacientesInativoON(List<PacienteAguardandoTransplanteOrgaoVO> listaRetorno)
			throws CloneNotSupportedException, ApplicationBusinessException {
		mtxTransplantesRN.verificarDoencasPacientesInativoON(listaRetorno);
	}

	
	@Override
	public List<GerarExtratoListaTransplantesVO> consultarListagemExtratoTransplante(
			Integer trpSeq) {
		return mtxExtratoTransplantesDAO.consultarListagemExtratoTransplante(trpSeq);
	}
	
	@Override
	public List<ExtratoAlteracoesListaOrgaosVO> obterExtratoAlteracoesListaOrgaos(Integer trpSeq) {
		return mtxExtratoTransplantesDAO.obterExtratoAlteracoesListaOrgaos(trpSeq);
	}
	@Override
	public List<PacienteTransplantadosOrgaoVO> obterListaPacientesTransplantadosOrgao(FiltroTransplanteOrgaoVO filtro, Integer firstResult, Integer maxResults, 
			String orderProperty, boolean asc) {
		return mtxTransplantesRN.obterListaPacientesTransplantadosOrgao(filtro, firstResult, maxResults, orderProperty, asc);
	}
	
	public Long obterListaPacientesTransplantadosOrgaoCount(FiltroTransplanteOrgaoVO filtro) {
		return mtxTransplantesDAO.obterListaPacientesTransplantadosOrgaoCount(filtro);
	}
	
	/**
	 * #47146 - INICIO
	 */
	@Override
	public AipPacientes obterDadosPaciente(Integer seqTransplante) {
		return aipPacientesDAO.obterDadosPaciente(seqTransplante);
	}
	
	@Override
	public TiposExamesPacienteVO obterTiposExamesPaciente(AipPacientes paciente, MtxExameUltResults mtxExameUltResults) throws ApplicationBusinessException{
		return mtxResultadoExamesTransplanteRN.obterTiposExamesPaciente(paciente, mtxExameUltResults);
	}
	
	@Override
	public void atualizarResultadoExames(MtxResultadoExames resultadoExames) {
		 mtxTransplantesON.atualizarResultadoExames(resultadoExames);
	}
	
	@Override
	public void excluirResultadoExames(MtxResultadoExames resultadoExames) throws ApplicationBusinessException{
		mtxTransplantesON.excluirResultadoExames(resultadoExames);
	}
	
	@Override
	public void salvarResultadoExames(MtxResultadoExames resultadoExames, Integer seqTransplante) throws ApplicationBusinessException{
		mtxTransplantesON.salvarResultadoExames(resultadoExames, seqTransplante);
	}
	
	@Override
	public Boolean verificarHcvReagente(Integer seqTransplante, AipPacientes paciente, MtxExameUltResults mtxExameUltResults) throws ApplicationBusinessException{
		return mtxResultadoExamesTransplanteRN.verificarHcvReagente(seqTransplante, paciente, mtxExameUltResults);
	}
	
	@Override
	public List<MtxResultadoExames> obterResultadoExames(Integer seqTransplante, AipPacientes paciente){
		return mtxResultadoExamesDAO.obterResultadosExames(seqTransplante);
	}

	@Override
	public List<TiposExamesPacienteVO> buscaUltimosResultados(AipPacientes paciente) {
		return mtxResultadoExamesTransplanteRN.buscaUltimosResultados(paciente);
	}
	/** * #47146 - FIM  */
	
	//#41787
	@Override
	public boolean mudarStatusPacienteTMO(Integer trpSeq, DominioSituacaoTransplante situacao, Integer pacCodigo, Integer masSeq) throws ApplicationBusinessException{
		return this.mtxTransplantesRN.mudarStatusPacienteTMO(trpSeq, situacao, pacCodigo, masSeq);
	}
	@Override
	public ListarTransplantesVO obterPacientePorCodTransplante(Integer codTransplante){
		return mtxTransplantesDAO.obterPacientePorCodTransplante(codTransplante);
	}
	@Override
	public List<MtxMotivoAlteraSituacao> listarMotivosAlteracaoSituacao(){
		return this.mtxMotivoAlteraSituacaoDAO.listarMotivosAlteracaoSituacao();
	}
	
	//#41799
	@Override
	public PacienteAguardandoTransplanteOrgaoVO obterPacientePorCodTransplanteRins(Integer codTransplante){
		return mtxTransplantesDAO.obterPacientePorCodTransplanteRins(codTransplante);
	}
	
	@Override
	public boolean mudarStatusPacienteRins(Integer trpSeq, DominioSituacaoTransplante situacao, Integer masSeq) throws ApplicationBusinessException{
		return this.mtxTransplantesRN.mudarStatusPacienteRins(trpSeq, situacao, masSeq);
	}
	
	//#50188
	@Override
	public void gravarAtualizarTipoRetorno(MtxTipoRetorno mtxTipoRetorno) throws ApplicationBusinessException, BaseListException{
		mtxTipoRetornoDAO.gravarAtualizarTipoRetorno(mtxTipoRetorno);
	}
	@Override
	public List<MtxTipoRetorno> pesquisarTipoRetorno(MtxTipoRetorno mtxTipoRetorno){
		return mtxTipoRetornoDAO.pesquisarTipoRetorno(mtxTipoRetorno);
	}
	@Override
	public void validarTipoRetorno(MtxTipoRetorno mtxTipoRetorno) throws ApplicationBusinessException, BaseListException {
		mtxTipoRetornoON.validarDescricaoTipoRetorno(mtxTipoRetorno);
		mtxTipoRetornoON.validarSituacaoTipoRetorno(mtxTipoRetorno);
		mtxTipoRetornoRN.checarSeJaExiste(mtxTipoRetorno);
	}
	
	// #49925
	@Override
	public List<MtxItemPeriodoRetorno> pesquisarItemPeriodoRetorno(DominioTipoRetorno tipoRetorno, Integer seqPeriodoRetorno, String pesquisaSuggestion){
		return mtxItemPeriodoRetornoDAO.pesquisarItemPeriodoRetorno(tipoRetorno, seqPeriodoRetorno, pesquisaSuggestion);
	}

	@Override
	public List<AghEspecialidades> obterEspecialidadesAtivas() {
		return aghEspecialidadesDAO.obterEspecialidadesAtivas();
	}

	@Override
	public List<AgendaTransplanteRetornoVO> obterAgendaTransplanteComPrevisaoRetorno(Integer codPaciente, List<AghEspecialidades> listaEspecialidade, DominioTipoRetorno tipoRetorno, MtxItemPeriodoRetorno descricaoTipoRetorno) {
		List<AgendaTransplanteRetornoVO> listaAgenda = mtxTransplantesDAO.obterAgendaPacienteTransplante(codPaciente, listaEspecialidade, tipoRetorno);
		
		if (listaAgenda != null && listaAgenda.size() > 0) {
			listaAgenda =  mtxTipoRetornoRN.calcularPrevisaoRetorno(listaAgenda, tipoRetorno, descricaoTipoRetorno);
		}
		
		return listaAgenda;
	}

	@Override
	public List<TotalizadorAgendaTransplanteVO> obterTotalConsultasPorDia(List<AgendaTransplanteRetornoVO> listaAgenda, MtxItemPeriodoRetorno descricaoTipoRetorno) {
		if (listaAgenda != null && listaAgenda.size() > 0) {
			return mtxTipoRetornoRN.obterTotalConsultasPorDia(listaAgenda, descricaoTipoRetorno);
		}
		
		return null;
	}

	@Override
	public void atualizarObservacaoTransplante(MtxTransplantes observacaoTransplante) {
		mtxTipoRetornoRN.atualizarObservacaoTransplante(observacaoTransplante);
	}
	
	//#49923
	@Override
	public List<MtxPeriodoRetorno> consultarPeriodoRetorno (MtxPeriodoRetorno mtxPeriodoRetorno, DominioRepeticaoRetorno repeticao, MtxPeriodoRetorno selecionado){
		return mtxPeriodoRetornoDAO.pesquisarPeriodoRetorno(mtxPeriodoRetorno,repeticao, selecionado);
	}
	
	@Override
	public List<MtxTipoRetorno> listarPeriodoRetorno (DominioTipoRetorno indTipo, String descricao){
		return mtxPeriodoRetornoDAO.pesquisarRegistroPeriodoRetorno(indTipo, descricao);
		
	}
	
	@Override
	public void gravarRegistroPeriodoRetorno(MtxPeriodoRetorno mtxPeriodoRetorno, MtxTipoRetorno selecionaDescricao, DominioSimNao dominioSimNao,List<MtxItemPeriodoRetorno> listaItem){
		mtxPeriodoRetornoRN.persistirPeriodoRetorno(mtxPeriodoRetorno,selecionaDescricao,dominioSimNao,listaItem);	
		
	}
	
	@Override
	public void editarRegistroPeriodoRetorno(MtxPeriodoRetorno mtxPeriodoRetorno, MtxTipoRetorno selecionaDescricao, DominioSimNao dominioSimNao,List<MtxItemPeriodoRetorno> listaItem, List<MtxItemPeriodoRetorno> listaItemExcluir){
		mtxPeriodoRetornoRN.editarPeriodoRetorno(mtxPeriodoRetorno,selecionaDescricao,dominioSimNao,listaItem,listaItemExcluir);	
		
	}
	
	@Override
	public List<MtxItemPeriodoRetorno> consultarItensPeriodoRetorno(MtxPeriodoRetorno mtxPeriodoRetorno){
		return mtxPeriodoRetornoDAO.pesquisarItensPeriodoRetorno(mtxPeriodoRetorno);
	}
	
	public Long listarPeriodoRetornoCount(DominioTipoRetorno indTipo, String param){
		return mtxPeriodoRetornoDAO.pesquisarRegistroPeriodoRetornoCount(indTipo, param);
	}
	
	
	//48373
	@Override
	public List<RelatorioTransplanteOrgaosSituacaoVO> pesquisarTransplante(DominioTipoOrgao dominioTipoOrgao, Integer prontuario, Date dataInicial,
			Date dataFinal,	List<DominioSituacaoTransplante> listaDominioSituacaoTransplanteSelecionados, DominioOrdenacRelatorioSitPacOrgao ordenacao, DominioSexo sexo){
		List<RelatorioTransplanteOrgaosSituacaoVO> lista = mtxTransplantesDAO.pesquisarTransplante(dominioTipoOrgao, prontuario, dataInicial, dataFinal, listaDominioSituacaoTransplanteSelecionados, sexo);
		relatorioTransplanteOrgaoSituacaoON.ordenarRelatorioTransplanteOrgao(ordenacao, lista);
		return lista;
	}

	@Override
	public String gerarCSVRelatorioTransplanteOrgaosSituacao(String nomeHospital, 
			List<RelatorioTransplanteOrgaosSituacaoVO> listaRelatorioTransplanteOrgaosSituacaoVO) throws IOException{
		return relatorioTransplanteOrgaoSituacaoON.gerarCSV(nomeHospital, listaRelatorioTransplanteOrgaosSituacaoVO);
	}
	@Override
	public void validarData(Date dataInicial, Date dataFinal) throws ApplicationBusinessException{
		relatorioTransplanteOrgaoSituacaoON.validarData(dataInicial, dataFinal);
	}
	@Override
	public void validarListaRelatorioTransplanteOrgaosSituacaoVO (List<RelatorioTransplanteOrgaosSituacaoVO> listaRelatorioTransplanteOrgaosSituacaoVO) 
			throws ApplicationBusinessException{
		relatorioTransplanteOrgaoSituacaoON.validarListaRelatorioTransplanteOrgaosSituacaoVO(listaRelatorioTransplanteOrgaosSituacaoVO);
	}
	
	//48795
	@Override
	public List<RelatorioTransplanteTmoSituacaoVO> pesquisarTransplante(DominioSituacaoTmo situacaoTmo, DominioTipoAlogenico tipoAlogenico, Integer prontuario, Date dataInicial,
			Date dataFinal,	List<DominioSituacaoTransplante> listaDominioSituacaoTransplanteSelecionados, DominioOrdenacRelatorioSitPacTmo ordenacao){
		List<RelatorioTransplanteTmoSituacaoVO> lista = mtxTransplantesDAO.pesquisarTransplante(situacaoTmo,  tipoAlogenico, prontuario, dataInicial,
				dataFinal,	listaDominioSituacaoTransplanteSelecionados);
		relatorioTransplanteTmoSituacaoON.calcularEscore(lista);
		relatorioTransplanteTmoSituacaoON.ordenarRelatorioTransplanteTmo(ordenacao, lista, situacaoTmo);
		return lista;
	}
	
	@Override
	public void validarListaRelatorioTransplanteTmoSituacaoVO (List<RelatorioTransplanteTmoSituacaoVO> listaRelatorioTransplanteTmoSituacaoVO) throws ApplicationBusinessException{
		relatorioTransplanteTmoSituacaoON.validarListaRelatorioTransplanteTmoSituacaoVO(listaRelatorioTransplanteTmoSituacaoVO);
	}
	
	@Override
	public String gerarCSVRelatorioTransplanteTmoSituacao(String nomeHospital, 
			List<RelatorioTransplanteTmoSituacaoVO> listaRelatorioTransplanteTmoSituacaoVO) throws IOException{
		return relatorioTransplanteTmoSituacaoON.gerarCSV(nomeHospital, listaRelatorioTransplanteTmoSituacaoVO);
	}
	
	@Override
	public void registarTransplantes(List<MbcProcedimentoCirurgicos> listaProcedimentos, Integer codPaciente) throws ApplicationBusinessException{
		mtxTransplantesRN.registarTransplantes(listaProcedimentos, codPaciente);
	}
	
	//#49346
	@Override
	public PacienteTransplanteMedulaOsseaVO carregarDadosPaciente(Integer prontuario, Object tipo){
		return mtxComorbidadeON.carregarDadosPaciente(prontuario, tipo);
	}
	
	@Override
	public List<MtxComorbidade> pesquisarComorbidadePorTipoDescricaoCid(MtxComorbidade mtxComorbidade) {
		return mtxComorbidadeON.pesquisarComorbidadePorTipoDescricaoCid(mtxComorbidade);
	}

	@Override
	public Long pesquisarComorbidadePorTipoDescricaoCidCount(MtxComorbidade mtxComorbidade) {
		return mtxComorbidadeDAO.pesquisarComorbidadePorTipoDescricaoCidCount(mtxComorbidade);
	}
	
	@Override
	public List<MtxComorbidadePaciente> carregarComorbidadesPaciente(DominioTipoTransplante tipo, AipPacientes aipPacientes){
		return mtxComorbidadeON.carregarComorbidadesPaciente(tipo, aipPacientes);
	}
	
	@Override
	public void excluirComorbidadePaciente(MtxComorbidadePaciente mtxComorbidadePaciente) throws ApplicationBusinessException, BaseException{
		mtxComorbidadePacienteDAO.excluirComorbidadePaciente(mtxComorbidadePaciente);
	}
	
	@Override
	public void gravarComorbidadePaciente(MtxComorbidadePaciente mtxComorbidadePaciente) throws ApplicationBusinessException, BaseException{
		mtxComorbidadePacienteDAO.gravarComorbidadePaciente(mtxComorbidadePaciente);
	}
	
	@Override
	public boolean validarGravarComorbidadePaciente(List<MtxComorbidade> listaComorbidades, List<MtxComorbidade> listaComorbidadesExcluidas){
		return mtxComorbidadeRN.validarGravarComorbidadePaciente(listaComorbidades, listaComorbidadesExcluidas);
	}
	
	@Override
	public List<MtxComorbidade> concatenarCID(List<MtxComorbidade> listaComorbidade){
		return mtxComorbidadeON.concatenarCID(listaComorbidade);
	}
	
	@Override
	public List<MtxComorbidade> removerComorbidadePacienteJaInseridas(List<MtxComorbidade> listaComorbidades, AipPacientes aipPacientes){
		return mtxComorbidadeRN.removerComorbidadePacienteJaInseridas(listaComorbidades, aipPacientes);
	}
	
	//49346 FIM
	
	@Override
	public void persistirMtxRegistrosTMO(MtxRegistrosTMO mtxRegistrosTMO) throws ApplicationBusinessException{
		mtxRegistrosTMORN.persistir(mtxRegistrosTMO);
	}
	
	@Override
	public MtxRegistrosTMO obterRegistroTransplantePorTransplante(Integer transplanteSeq){
		return mtxRegistrosTMODAO.obterRegistroTransplantePorTransplante(transplanteSeq);
	}
	
	@Override
	public void atualizarMtxRegistrosTMO(MtxRegistrosTMO mtxRegistrosTMO) throws ApplicationBusinessException{
		mtxRegistrosTMORN.persistir(mtxRegistrosTMO);
	}

	@Override
	public List<MtxTransplantes> obtemTransplantados(DominioSituacaoTmo tipoTmo, Integer codPacienteReceptor) {
		return mtxTransplantesRN.obtemTransplantados(tipoTmo, codPacienteReceptor);
	}
}