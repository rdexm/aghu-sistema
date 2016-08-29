package br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.BypassInactiveModule;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AfaViaAdministracao;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AnuTipoItemDieta;
import br.gov.mec.aghu.model.AnuTipoItemDietaUnfs;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MpmAlergiaUsual;
import br.gov.mec.aghu.model.MpmCuidadoUsual;
import br.gov.mec.aghu.model.MpmMotivoAltaMedica;
import br.gov.mec.aghu.model.MpmPlanoPosAlta;
import br.gov.mec.aghu.model.MpmProcedEspecialDiversos;
import br.gov.mec.aghu.model.MpmProcedEspecialRm;
import br.gov.mec.aghu.model.MpmServRecomendacaoAlta;
import br.gov.mec.aghu.model.MpmServRecomendacaoAltaId;
import br.gov.mec.aghu.model.MpmTipoModoUsoProcedimento;
import br.gov.mec.aghu.model.MpmUnidadeMedidaMedica;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.prescricaomedica.business.MpmAlergiaUsualRN;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAlergiaUsualDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmCuidadoUsualDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmTipoModoUsoProcedimentoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmUnidadeMedidaMedicaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.RecomendacoesAltaUsuarioDAO;
import br.gov.mec.aghu.prescricaomedica.vo.AnuTipoItemDietaVO;

/**
 * Classe de fachada que disponibiliza uma interface para as funcionalidades do
 * sub módulo 'Cadastros Básicos' do módulo 'Prescrição'.
 */


@Modulo(ModuloEnum.PRESCRICAO_MEDICA)
@Stateless
public class CadastrosBasicosPrescricaoMedicaFacade extends BaseFacade implements ICadastrosBasicosPrescricaoMedicaFacade {

	@EJB
	private ViasAdministracaoCRUD viasAdministracaoCRUD;
	
	@EJB
	private TiposDietaCRUD tiposDietaCRUD;
	
	@EJB
	private UnidadeMedidaMedicaCRUD unidadeMedidaMedicaCRUD;
	
	@EJB
	private ProcedEspecialDiversoON procedEspecialDiversoON;
	
	@EJB
	private RecomendacoesAltaUsuarioCRUD recomendacoesAltaUsuarioCRUD;
	
	@EJB
	private MotivoAltaMedicaCRUD motivoAltaMedicaCRUD;
	
	@EJB
	private PlanosPosAltaCRUD planosPosAltaCRUD;
	
	@Inject
	private MpmTipoModoUsoProcedimentoDAO mpmTipoModoUsoProcedimentoDAO;
	
	@Inject
	private MpmCuidadoUsualDAO mpmCuidadoUsualDAO;
	
	@Inject
	private MpmUnidadeMedidaMedicaDAO mpmUnidadeMedidaMedicaDAO;
	
	@Inject
	private RecomendacoesAltaUsuarioDAO recomendacoesAltaUsuarioDAO;
	
	@Inject
	private MpmAlergiaUsualDAO mpmAlergiaUsualDAO;
	
	@EJB
	private MpmAlergiaUsualRN mpmAlergiaUsualRN;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8586571994124542530L;

	private ViasAdministracaoCRUD getViasAdministracaoCRUD() {
		return viasAdministracaoCRUD;
	}

	private PlanosPosAltaCRUD getPlanosPosAltaCRUD() {
		return planosPosAltaCRUD;
	}

	private UnidadeMedidaMedicaCRUD getUnidadeMedidaMedicaCRUD() {
		return unidadeMedidaMedicaCRUD;
	}

	private RecomendacoesAltaUsuarioCRUD getRecomendacoesAltaUsuarioCRUD() {
		return recomendacoesAltaUsuarioCRUD;
	}

	private TiposDietaCRUD getTiposDietaCRUD() {
		return tiposDietaCRUD;
	}

	private MotivoAltaMedicaCRUD getMotivoAltaMedicaCRUD() {
		return motivoAltaMedicaCRUD;
	}	
	
	protected MpmUnidadeMedidaMedicaDAO getMpmUnidadeMedidaMedicaDAO() {
		return mpmUnidadeMedidaMedicaDAO;
	}
	
	@Override
	public MpmUnidadeMedidaMedica obterUnidadeMedicaPorId(Integer fdsUmmSeq) {
		return this.getMpmUnidadeMedidaMedicaDAO().obterUnidadesMedidaMedicaPeloId(fdsUmmSeq);
	}
	
	@Override
	public List<MpmUnidadeMedidaMedica> pesquisarUnidadesMedidaMedicaConcentracao(Object siglaOuDescricao){
		return getUnidadeMedidaMedicaCRUD().pesquisarUnidadesMedidaMedicaConcentracao(siglaOuDescricao);
	}
	
	@Override
	public void persistirTiposDieta(AnuTipoItemDieta tipoDieta, List<AnuTipoItemDietaUnfs> listaAnuTipoItemDietaUnfs) throws ApplicationBusinessException{
		this.getTiposDietaCRUD().persistirTiposDieta(tipoDieta, listaAnuTipoItemDietaUnfs);
	}
	
	@Override
	@BypassInactiveModule
	public void updateFatProcedHospInternosSituacao(ScoMaterial matCodigo,
			MbcProcedimentoCirurgicos pciSeq, MpmProcedEspecialDiversos pedSeq,
			String csaCodigo, String pheCodigo, DominioSituacao indSituacao, Short euuSeq, Integer cduSeq,
			Short cuiSeq, Integer tidSeq) throws ApplicationBusinessException {
		this.getTiposDietaCRUD().updateFatProcedHospInternosSituacao(matCodigo, pciSeq, pedSeq, csaCodigo, pheCodigo, indSituacao,
					euuSeq, cduSeq, cuiSeq, tidSeq);
	}
	
	@Override
	@BypassInactiveModule
	public void updateFatProcedHospInternosDescr(ScoMaterial matCodigo,
			MbcProcedimentoCirurgicos pciSeq, MpmProcedEspecialDiversos pedSeq,
			String csaCodigo, String pheCodigo, String descricao, Short euuSeq, Integer cduSeq,
			Short cuiSeq, Integer tidSeq) throws ApplicationBusinessException {
		this.getTiposDietaCRUD().updateFatProcedHospInternosDescr(matCodigo, pciSeq, pedSeq, csaCodigo, pheCodigo, descricao, euuSeq, 
				cduSeq, cuiSeq, tidSeq);
	}

	@Override
	public MpmMotivoAltaMedica obterMotivoAltaMedicaPeloId(Integer seq) {
		return getMotivoAltaMedicaCRUD().obterMotivoAltaMedicaPeloId(
				seq.shortValue());
	}

	@Override
	public void removerMotivoAltaMedica(Short seq,
			Integer periodo) throws BaseException {
		getMotivoAltaMedicaCRUD().removerMotivoAltaMedica(seq,
				periodo);
	}

	@Override
	public void persistMotivoAltaMedica(MpmMotivoAltaMedica motivoAltaMedica)
			throws ApplicationBusinessException {
		getMotivoAltaMedicaCRUD().persistMotivoAltaMedica(motivoAltaMedica);
	}

	@Override
	public Long pesquisarTipoItemDietaCount(Integer codigo,
			String descricao, DominioSituacao situacao) {
		return getTiposDietaCRUD().pesquisarTipoItemDietaCount(codigo,
				descricao, situacao); 
	}

	@Override
	public List<AnuTipoItemDietaVO> pesquisarTipoItemDieta(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			Integer codigo, String descricao, DominioSituacao situacao) {
		return getTiposDietaCRUD().pesquisarTipoItemDieta(firstResult,
					maxResult, orderProperty, asc, codigo, descricao, situacao); 
	}

	@Override
	public void removerTipoItemDieta(final Integer seqTipoDieta,
			Integer periodo) throws BaseException {
		getTiposDietaCRUD().excluirTiposDieta(seqTipoDieta);
	}

	@Override
	public Long pesquisarMotivoAltaMedicaCount(Integer codigo,
			String descricao, String sigla, DominioSituacao situacao) {
		return getMotivoAltaMedicaCRUD().pesquisarMotivoAltaMedicaCount(codigo,
				descricao, sigla, situacao);
	}

	@Override
	public List<MpmMotivoAltaMedica> pesquisarMotivoAltaMedica(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Integer codigo, String descricao, String sigla,
			DominioSituacao situacao) {
		return getMotivoAltaMedicaCRUD().pesquisarMotivoAltaMedica(firstResult,
				maxResult, orderProperty, asc, codigo, descricao, sigla,
				situacao);
	}

	@Override
	public Long pesquisarPlanosPosAltaCount(Integer codigoPlano,
			String descricaoPlano, DominioSituacao situacaoPlano) {
		return getPlanosPosAltaCRUD().pesquisarPlanosPosAltaCount(codigoPlano,
				descricaoPlano, situacaoPlano);
	}

	@Override
	public List<MpmPlanoPosAlta> pesquisarPlanosPosAlta(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			Integer codigoPlano, String descricaoPlano,
			DominioSituacao situacaoPlano) {
		return getPlanosPosAltaCRUD().pesquisarPlanosPosAlta(firstResult,
				maxResult, orderProperty, asc, codigoPlano, descricaoPlano,
				situacaoPlano);
	}

	@Override
	public Long pesquisarUnidadesMedidaMedicaCount(Integer codigoUnidade,
			String descricaoUnidade, DominioSituacao situacaoUnidade) {
		return getUnidadeMedidaMedicaCRUD().pesquisarUnidadesMedidasCount(
				codigoUnidade, descricaoUnidade, situacaoUnidade);
	}

	@Override
	public List<MpmUnidadeMedidaMedica> pesquisarUnidadesMedidaMedica(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Integer codigoMedida, String descricaoMedida,
			DominioSituacao situacaoUnidadeMedidaMedica) {

		return getUnidadeMedidaMedicaCRUD().pesquisarUnidadesMedidaMedica(
				firstResult, maxResult, orderProperty, asc, codigoMedida,
				descricaoMedida, situacaoUnidadeMedidaMedica);
	}

	@Override
	public List<MpmUnidadeMedidaMedica> pesquisarUnidadesMedidaMedica(
			Object idOuDescricao) {
		return getUnidadeMedidaMedicaCRUD().pesquisarUnidadesMedidaMedica(
				idOuDescricao);
	}

	@Override
	public MpmPlanoPosAlta obterPlanoPosAltaPeloId(Integer seq) {
		return getPlanosPosAltaCRUD().obterPlanoPosAltaPeloId(seq);
	}

	@Override
	public void removerPlano(final Short codigoPlanoPosAltaExclusao, Integer periodo)
			throws BaseException {
		getPlanosPosAltaCRUD().removerPlano(codigoPlanoPosAltaExclusao, periodo);
	}

	@Override
	public void removerUnidadeMedidaMedica(Integer seq,
			Integer periodo) throws BaseException {
		getUnidadeMedidaMedicaCRUD().removerUnidadeMedidaMedica(seq,
				periodo);
	}

	@Override
	public void persistPlanoPosAlta(MpmPlanoPosAlta planoPosAlta)
			throws ApplicationBusinessException {
		getPlanosPosAltaCRUD().persistPlanoPosAlta(planoPosAlta);
	}

	@Override
	public void persistUnidadeMedidaMedica(MpmUnidadeMedidaMedica unidade) throws ApplicationBusinessException {
		getUnidadeMedidaMedicaCRUD().persistUnidadeMedidaMedica(unidade);
	}

	@Override
	public Long pesquisarViasAdministracaoCount(String sigla,
			String descricao, DominioSituacao situacao) {

		return getViasAdministracaoCRUD().pesquisarViasAdministracaoCount(
				sigla, descricao, situacao);
	}

	@Override
	public List<AfaViaAdministracao> pesquisarViasAdministracao(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, String sigla, String descricao,
			DominioSituacao situacao) {

		return getViasAdministracaoCRUD().pesquisarViasAdministracao(
				firstResult, maxResult, orderProperty, asc, sigla, descricao,
				situacao);
	}

	@Override
	public Long pesquisarRecomendacoesUsuarioCount(RapServidores usuario) {
		return getRecomendacoesAltaUsuarioCRUD()
				.pesquisarRecomendacoesUsuarioCount(usuario);
	}

	@Override
	public List<MpmServRecomendacaoAlta> pesquisarRecomendacoesUsuario(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, RapServidores usuario) {

		return getRecomendacoesAltaUsuarioCRUD().pesquisarRecomendacoesUsuario(
				firstResult, maxResult, orderProperty, asc, usuario);
	}

	@Override
	public MpmServRecomendacaoAlta obterRecomendacaoAltaPorId(
			MpmServRecomendacaoAltaId id) {
		return getRecomendacoesAltaUsuarioDAO().obterRecomendacaoAltaPorId(id);
	}
	
	private RecomendacoesAltaUsuarioDAO getRecomendacoesAltaUsuarioDAO() {
		return recomendacoesAltaUsuarioDAO;
	}

	@Override
	public void persistRecomendacaoAlta(MpmServRecomendacaoAlta recomendacao)
			throws ApplicationBusinessException {
		getRecomendacoesAltaUsuarioCRUD().persistRecomendacaoAlta(recomendacao);
	}

	@Override
	public void removerRecomendacao(MpmServRecomendacaoAltaId id,
			Integer periodo) throws ApplicationBusinessException {
		getRecomendacoesAltaUsuarioCRUD().removerRecomendacao(id,
				periodo);
	}

	@Override
	public void persistirViasAdministracao(
			AfaViaAdministracao viaAdministracao,
			AfaViaAdministracao viaAdministracaoAux) throws ApplicationBusinessException {
		getViasAdministracaoCRUD().persistirViasAdministracao(viaAdministracao, viaAdministracaoAux);
	}

	@Override
	public void removerViaAdministracao(final String siglaViaAdministracao,
			Integer periodo) throws BaseException {
		getViasAdministracaoCRUD().removerViasAdministracao(siglaViaAdministracao, periodo);
		
	}
	
	private ProcedEspecialDiversoON getProcedEspecialDiversoON() {
		return procedEspecialDiversoON;
	}

	@Override
	public MpmProcedEspecialDiversos obterProcedimentoEspecialPeloId(Short codigo) {
		return getProcedEspecialDiversoON().obterProcedimentoEspecialPeloId(codigo);
	}

	@Override
	public MpmProcedEspecialDiversos persistirProcedimentoEspecial(MpmProcedEspecialDiversos elemento, 
						List<MpmTipoModoUsoProcedimento> modosUsos, List<MpmTipoModoUsoProcedimento> modosUsosExcluidos,
						List<MpmProcedEspecialRm> materiaisMpmProcedEspecialRm,
						List<MpmProcedEspecialRm> materiaisMpmProcedEspecialRmExcluidos) throws BaseException {
		return getProcedEspecialDiversoON().persistirProcedimentoEspecial(elemento, modosUsos, modosUsosExcluidos, materiaisMpmProcedEspecialRm, materiaisMpmProcedEspecialRmExcluidos);
	}
	
	@Override
	public void removerProcedimentoEspecial(Short codigoProcedimento) throws BaseException {
		getProcedEspecialDiversoON().removerProcedimentoEspecial(codigoProcedimento);
	}
	
	@Override
	public List<ScoMaterial> listarMateriaisRMAutomatica(Integer gmtCodigo, String nome, int maxResults) {
		return getProcedEspecialDiversoON().getListaMateriaisRMAutomatica(gmtCodigo, nome, maxResults);
	}
	
	@Override
	public List<ScoMaterial> listarMateriaisRMAutomatica(Integer gmtCodigo, String nome) {
		return getProcedEspecialDiversoON().getListaMateriaisRMAutomatica(gmtCodigo, nome);
	}
	
	@Override
	public List<MpmProcedEspecialDiversos> pesquisarProcedimentoEspecial(
			Integer firstResult, Integer maxResult, String string, boolean b,
			MpmProcedEspecialDiversos elemento) {
		return getProcedEspecialDiversoON().pesquisar(firstResult, maxResult, string, b, elemento);
	}

	@Override
	public Long pesquisarCount(MpmProcedEspecialDiversos elemento) {
		return getProcedEspecialDiversoON().pesquisarCount(elemento);
	}
	
	@Override
	public List<MpmUnidadeMedidaMedica> listarUnidadesMedidaMedicaAtivas(Object parametroConsulta) {
		return this.getMpmUnidadeMedidaMedicaDAO()
				.getListaUnidadeMedidaMedicaAtivas(parametroConsulta);
	}
	
	@Override
	public List<MpmUnidadeMedidaMedica> listarUnidadesMedidaMedicaAtivas() {
		return this.getMpmUnidadeMedidaMedicaDAO()
				.getListaUnidadeMedidaMedicaAtivas();
	}

	@Override
	@BypassInactiveModule
	public List<MpmCuidadoUsual> pesquisarCuidadosMedicos(String parametro) {
		return this.getMpmCuidadoUsualDAO().obterListaCuidadoUsual(parametro);
	}

	protected MpmCuidadoUsualDAO getMpmCuidadoUsualDAO() {
		return mpmCuidadoUsualDAO;
	}

	@Override
	@BypassInactiveModule
	public MpmUnidadeMedidaMedica obterUnidadeMedidaMedicaPorId(Integer seq) {
		return this.getMpmUnidadeMedidaMedicaDAO().obterPorChavePrimaria(seq);
	}
	
	@Override
	public void inserirFatProcedHospInternos(ScoMaterial matCodigo,	MbcProcedimentoCirurgicos pciSeq, MpmProcedEspecialDiversos pedSeq,
			String csaCodigo, String pheCodigo, String descricao, DominioSituacao indSituacao, Short euuSeq, Integer cduSeq,
			Short cuiSeq, Integer tidSeq) throws ApplicationBusinessException {
		getTiposDietaCRUD().inserirFatProcedHospInternos(matCodigo, pciSeq,	pedSeq, csaCodigo, pheCodigo, descricao, indSituacao, euuSeq,
				cduSeq, cuiSeq, tidSeq);
	}

	@Override
	@BypassInactiveModule
	public MpmCuidadoUsual obterCuidadoUsualPorChavePrimaria(Integer icsCduSeq) {		 
		return getMpmCuidadoUsualDAO().obterPorChavePrimaria(icsCduSeq);
	}
	
	@Override
	public void criarFatProcedHospInternos(Short euuSeq, String descricao, DominioSituacao situacao) throws ApplicationBusinessException {
		this.getProcedEspecialDiversoON().criarFatProcedHospInternos(euuSeq, descricao, situacao);
	}

	@Override
	public List<MpmTipoModoUsoProcedimento> buscarModosUsoPorProcedimentoEspecial(Short procedimentoEspecialId) {
		return getMpmTipoModoUsoProcedimentoDAO().buscarModosUsoPorProcedimentoEspecial(procedimentoEspecialId);
	}

	public MpmTipoModoUsoProcedimentoDAO getMpmTipoModoUsoProcedimentoDAO() {
		return mpmTipoModoUsoProcedimentoDAO;
	}
	
	@Override
	public List<MpmAlergiaUsual> pesquisarAlergiaUsual(Integer codigo, String descricao, DominioSituacao situacao) {
		return this.mpmAlergiaUsualDAO.pesquisarAlergiaUsual(codigo, descricao, situacao);
	}
	
	@Override
	public void salvarAlergiaUsual(MpmAlergiaUsual obj, boolean situacao) throws ApplicationBusinessException {
		this.mpmAlergiaUsualRN.salvarAlergiaUsual(obj, situacao);
	}
	
	@Override
	public void alterarAlergiaUsual(MpmAlergiaUsual obj, boolean situacao) throws ApplicationBusinessException  {
		this.mpmAlergiaUsualRN.alterarAlergiaUsual(obj, situacao);
	}
	
	@Override
	public void removerAlergiaUsual(MpmAlergiaUsual obj) throws ApplicationBusinessException {
		this.mpmAlergiaUsualRN.removerAlergiaUsual(obj);
	}
	
	//--------------------

	@Override
	public Integer pesquisarTipoDietaCount(
			AghUnidadesFuncionais unidadeFuncional) {
		return this.getTiposDietaCRUD().pesquisarTipoDietaCount(unidadeFuncional);
	}

	@Override
	public List<AnuTipoItemDietaUnfs> pesquisarTipoDieta(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			AghUnidadesFuncionais unidadeFuncional) {
		return this.getTiposDietaCRUD().pesquisarTipoDieta(firstResult, maxResult,
				orderProperty, asc, unidadeFuncional);
	}

	@Override
	public void excluir(AnuTipoItemDietaUnfs anuTipoItemDietaUnfs) {
		this.getTiposDietaCRUD().excluir(anuTipoItemDietaUnfs);
	}

	@Override
	public void inserirTodosTiposDietasUnfs(RapServidores servidor)
			throws ApplicationBusinessException {
		this.getTiposDietaCRUD().inserirTodosTiposDietasUnfs(servidor);
	}

	@Override
	public void incluirTiposDietasUnfs(AghUnidadesFuncionais unidadeFuncional,
			RapServidores servidor) throws ApplicationBusinessException {
		this.getTiposDietaCRUD().incluirTiposDietasUnfs(unidadeFuncional, servidor);
	}

	@Override
	public void persistirTiposDieta(AnuTipoItemDieta tipoDieta,
			List<AnuTipoItemDietaUnfs> listaUnidadeFuncAdicionadas,
			List<AnuTipoItemDietaUnfs> listaExcluiUnidadeFunc) throws ApplicationBusinessException {
		this.getTiposDietaCRUD().persistirTiposDieta(tipoDieta, listaUnidadeFuncAdicionadas,listaExcluiUnidadeFunc);
		
	}
	
}
