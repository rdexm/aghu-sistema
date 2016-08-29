/**
 * Facade do sub-modulo centro de custo do financeiro 
 */
package br.gov.mec.aghu.financeiro.centrocusto.business;

import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.centrocusto.dao.FccCentroCustosDAO;
import br.gov.mec.aghu.centrocusto.dao.FcuAgrupaGrupoMaterialDAO;
import br.gov.mec.aghu.centrocusto.vo.CentroCustosVO;
import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.BypassInactiveModule;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioCaracteristicaCentroCusto;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoCentroProducaoCustos;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.FcuAgrupaGrupoMaterial;
import br.gov.mec.aghu.model.FcuGrupoCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigCentroProducao;
import br.gov.mec.aghu.paciente.vo.SituacaoPacienteVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;



@Modulo(ModuloEnum.CENTRO_CUSTO)
@Stateless
public class CentroCustoFacade extends BaseFacade implements ICentroCustoFacade {
	
	@EJB
	private CentroCustoON centroCustoON;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private FccCentroCustosDAO fccCentroCustosDAO;

	@Inject
	private FcuAgrupaGrupoMaterialDAO fcuAgrupaGrupoMaterialDAO;

	private static final long serialVersionUID = 4322045731464436869L;

	@Override
	@Secure("#{s:hasPermission('centroCusto','pesquisar')}")
	public List<FccCentroCustos> pesquisarCentroCustosSuperior(
			final String strPesquisa) {
		return getCentroCustoON().pesquisarCentroCustosSuperior(strPesquisa);
	}

	@Override
	@Secure("#{s:hasPermission('centroCusto','pesquisar')}")
	public Long pesquisarCentroCustosSuperiorCount(final String strPesquisa) {
		return getCentroCustoON().pesquisarCentroCustosSuperiorCount(
				strPesquisa);
	}

	@Override
	public FccCentroCustos pesquisarCentroCustosPorMatriculaVinculo(
			final Integer matricula, final Short vinCodigo) {
		return getCentroCustoON().pesquisarCentroCustosPorMatriculaVinculo(
				matricula, vinCodigo);
	}

	@Override
	public Long obterFccCentroCustoCount(final FccCentroCustos centroCusto,
			final FcuGrupoCentroCustos grupoCentroCusto,
			final FccCentroCustos centroCustoSuperior, RapServidores servidorChefia, DominioTipoCentroProducaoCustos[] tiposCentroProducao) {
		return getCentroCustoON().obterFccCentroCustoCount(centroCusto,
				grupoCentroCusto, centroCustoSuperior, servidorChefia, tiposCentroProducao);
	}

	@Override
	@Secure("#{s:hasPermission('centroCusto','pesquisar')}")
	public List<FccCentroCustos> pesquisarCentroCustos(
			final Integer firstResult, final Integer maxResults,
			final FccCentroCustos centroCusto,
			final FcuGrupoCentroCustos grupoCentroCusto,
			final FccCentroCustos centroCustoSuperior, RapServidores servidorChefia, DominioTipoCentroProducaoCustos[] tiposCentroProducao) {
		return getCentroCustoON().pesquisarCentroCustos(firstResult,
				maxResults, centroCusto, grupoCentroCusto, centroCustoSuperior, servidorChefia, tiposCentroProducao);
	}

	@Override
	@Secure("#{s:hasPermission('centroCusto','pesquisar')}")
	public FccCentroCustos obterFccCentroCustos(final Integer codigo) {
		return getCentroCustoON().obterFccCentroCustos(codigo);
	}

	@Override
	@Secure("#{s:hasPermission('centroCusto','pesquisar')}")
	public FccCentroCustos obterCentroCusto(Integer codigo) {
		return getCentroCustoON().obterCentroCusto(codigo);
	}

	@Override
	@Secure("#{s:hasPermission('centroCusto','pesquisar')}")
	public FccCentroCustos obterFccCentroCustosAtivos(final Integer codigo) {
		return getCentroCustoON().obterFccCentroCustosAtivos(codigo);
	}
	@Override
	@Secure("#{s:hasPermission('centroCusto','pesquisar')}")
	public List<FcuGrupoCentroCustos> pesquisarGruposCentroCustos(
			final String filtro) {
		return getCentroCustoON().pesquisarGruposCentroCustos(filtro);
	}

	@Override
	@Secure("#{s:hasPermission('centroCusto','pesquisar')}")
	public Long pesquisarGruposCentroCustosCount(final String filtro) {
		return getCentroCustoON().pesquisarGruposCentroCustosCount(filtro);
	}

	@Override
	@BypassInactiveModule
	public List<FccCentroCustos> pesquisarCentroCustosPorCodigoDescricao(
			final String filtro) {
		return getCentroCustoON().pesquisarCentroCustosPorCodigoDescricao(
				filtro);
	}

	@Override
	@Secure("#{s:hasPermission('centroCusto','pesquisar')}")
	public Long pesquisarCentroCustosPorCodigoDescricaoCount(String filtro) {
		return getCentroCustoON().pesquisarCentroCustosPorCodigoDescricaoCount(
				filtro);
	}

	@Override
	@Secure("#{s:hasPermission('centroCusto','pesquisar')}")
	public List<FccCentroCustos> pesquisarCentroCustosAtivosComChefiaPorCodigoDescricao(
			final String descricaoCentroCustoBuscaLov) {
		return getCentroCustoON()
				.pesquisarCentroCustosAtivosComChefiaPorCodigoDescricao(
						descricaoCentroCustoBuscaLov);
	}

	@Override
	@Secure("#{s:hasPermission('centroCusto','pesquisar')}")
	public Long pesquisarCentroCustosAtivosComChefiaPorCodigoDescricaoCount(
			String filtro) {
		return getCentroCustoON()
				.pesquisarCentroCustosAtivosComChefiaPorCodigoDescricaoCount(
						filtro);
	}

	@Override
	@Secure("#{s:hasPermission('centroCusto','alterar')}")
	public void persistirCentroCusto(final FccCentroCustos centroCusto, final Boolean isEdicao) throws ApplicationBusinessException, BaseException {
		getCentroCustoON().persistirCentroCusto(centroCusto, isEdicao);
	}

	@Override
	@Secure("#{s:hasPermission('centroCusto','pesquisar')}")
	public List<FccCentroCustos> pesquisarCentroCustosAtivosPorCodigoDescricaoOrdemCodigo(
			final String centroCusto, final boolean somenteAtivo) {
		return getFccCentroCustosDAO()
				.pesquisarCentroCustosAtivosPorCodigoDescricaoOrdemCodigo(
						centroCusto, somenteAtivo);
	}

	@Override
	public List<FccCentroCustos> pesquisarCentroCustos(final Object objPesquisa) {
		return fccCentroCustosDAO.pesquisarCentroCustos(objPesquisa);
	}

	private CentroCustoON getCentroCustoON() {
		return centroCustoON;
	}

	private FccCentroCustosDAO getFccCentroCustosDAO() {
		return fccCentroCustosDAO;
	}
	
	public IServidorLogadoFacade getServidorLogadoFacade() {
		return servidorLogadoFacade;
	}

	@Override
	public FccCentroCustos pesquisaCentroCustoPorSituacaoPacienteVO(
			final SituacaoPacienteVO situacaoPacienteVO) {
		return this.getFccCentroCustosDAO()
				.pesquisaCentroCustoPorSituacaoPacienteVO(situacaoPacienteVO);
	}

	@Override
	public FccCentroCustos obterCentroCustoPorChavePrimaria(final Integer codigo) {
		return this.getFccCentroCustosDAO().obterPorChavePrimaria(codigo);
	}

	@Override
	public List<FccCentroCustos> pesquisarCentroCustosAtivosOrdemDescricao(
			final Object centroCusto) {
		return this.getFccCentroCustosDAO()
				.pesquisarCentroCustosAtivosOrdemDescricao(centroCusto);
	}

	@Override
	public Integer pesquisarCentroCustosAtivosOrdemDescricaoCount(
			final Object centroCusto) {
		return this.getFccCentroCustosDAO()
				.pesquisarCentroCustosAtivosOrdemDescricaoCount(centroCusto);
	}

	@Override
	public List<FccCentroCustos> pesquisarCentroCustosAtivos(
			final Object parametro) {
		return getCentroCustoON().pesquisarCentroCustosAtivos(parametro);
	}

	@Override
	public List<FccCentroCustos> pesquisarCentrosCustosAtuacaoEFilhosAtivosOrdenadosPeloCodigo(
			final Object parametro) {
		return getCentroCustoON()
				.pesquisarCentrosCustosAtuacaoEFilhosAtivosOrdenadosPeloCodigo(
						parametro);
	}

	@Override
	public FccCentroCustos obterCentroCustosPorCodigoCentroCustoAtuacaoOuLotacao() {
		return getCentroCustoON()
				.obterCentroCustosPorCodigoCentroCustoAtuacaoOuLotacao(servidorLogadoFacade.obterServidorLogado());
	}

	@Override
	public List<FccCentroCustos> pesquisarCentroCustosServidor(
			Object centroCusto) {
		return getFccCentroCustosDAO().pesquisarCentroCustosServidor(
				centroCusto, this.getServidorLogadoFacade().obterServidorLogado());
	}

	@Override
	public Long pesquisarCentroCustosServidorCount(Object centroCusto) {
		return getFccCentroCustosDAO().pesquisarCentroCustosServidorCount(
				centroCusto, this.getServidorLogadoFacade().obterServidorLogado());
	}

	@Override
	public List<FccCentroCustos> pesquisarCentroCustosServidorOrdemDescricao(
			Object centroCusto) {
		return getFccCentroCustosDAO()
				.pesquisarCentroCustosServidorOrdemDescricao(centroCusto, servidorLogadoFacade.obterServidorLogado());
	}

	@Override
	public List<FccCentroCustos> pesquisarCentroCustosAplicacaoOrdemDescricao(
			Object centroCusto) {
		return getFccCentroCustosDAO()
				.pesquisarCentroCustosAplicacaoOrdemDescricao(centroCusto);
	}

	@Override
	public List<FccCentroCustos> pesquisarCentroCustoUsuarioAutorizaSC() {
		return getFccCentroCustosDAO().pesquisarCentroCustoUsuarioAutorizaSC(servidorLogadoFacade.obterServidorLogado());
	}

	@Override
	public List<Integer> getCodigosCentrosCusto(List<FccCentroCustos> ccs) {
		return getCentroCustoON().getCodigosCentrosCusto(ccs);
	}
	
	@Override
	public List<FccCentroCustos> pesquisarCentroCustoUsuarioAutorizacaoSC() {
		return getFccCentroCustosDAO().pesquisarCentroCustoUsuarioAutorizacaoSC(servidorLogadoFacade.obterServidorLogado());
	}
	
	@Override
	public List<FccCentroCustos> pesquisarCentroCustoUsuarioAutorizaSs() {
		return getFccCentroCustosDAO().pesquisarCentroCustoUsuarioAutorizaSS(servidorLogadoFacade.obterServidorLogado());
	}
	
	@Override
	public List<FccCentroCustos> pesquisarCentroCustoUsuarioAutorizacaoSs() {
		return getFccCentroCustosDAO().pesquisarCentroCustoUsuarioAutorizacaoSS(servidorLogadoFacade.obterServidorLogado());
	}
	
	@Override
	public List<FccCentroCustos> pesquisarCentroCustosAtivosPorCodigoOuDescricao(
			Object objPesquisa) {
		return getFccCentroCustosDAO().pesquisarCentroCustosAtivos(objPesquisa);
	}

	@Override
	public Long pesquisarCentroCustosCount(Object objPesquisa) {
		return getFccCentroCustosDAO().pesquisarCentroCustosCount(objPesquisa);
	}

	@Override
	public List<FccCentroCustos> pesquisarCentrosCustosUsuarioHierarquiaAtuacaoLotacao(Object parametro, final DominioCaracteristicaCentroCusto caracteristica) throws ApplicationBusinessException {
		return getFccCentroCustosDAO().pesquisarCentroCustoUsuarioGerarSCSuggestion(parametro,servidorLogadoFacade.obterServidorLogado(), caracteristica);
	}

	@Override
	public List<FccCentroCustos> obterListaServicosEmEspecialidades(
			String objPesquisa) {
		return getFccCentroCustosDAO().obterServicosEmEspecialidades(
				objPesquisa);
	}

	@Override
	public FccCentroCustos pesquisarCentroCustoAtivoPorCodigo(Integer cCodigo) {
		return this.getFccCentroCustosDAO().pesquisarCentroCustoAtivoPorCodigo(
				cCodigo);
	}

	@Override
	public List<FccCentroCustos> pesquisarCentroCustosPorCentroProdExcluindoGcc(Object paramPesquisa, Integer seqCentroProducao, DominioSituacao situacao) {
		return this.getCentroCustoON().pesquisarCentroCustosPorCentroProdExcluindoGcc(paramPesquisa, seqCentroProducao, situacao);
	}
	
	@Override
	public List<FccCentroCustos> pesquisarCentroCustosPorCentroProdAtivo(SigCentroProducao centroProducao) {
		return getFccCentroCustosDAO().pesquisarCentroCustosPorCentroProdAtivo(centroProducao);
	}
	
	//CENTRO PRODUCAO---------------------------------------------------------------
	@Override
	public boolean existeCentroCustoAssociado(SigCentroProducao centroProducao){
		return getFccCentroCustosDAO().existeCentroCustoAssociado(centroProducao);
	}

	
	@Override
	public List<FccCentroCustos> pesquisarCentroCustoUsuarioGerarSC(){
		return getFccCentroCustosDAO().pesquisarCentroCustoUsuarioGerarSC(servidorLogadoFacade.obterServidorLogado());
	}

	public List<FccCentroCustos> pesquisarCentroCustoUsuarioCaracteristica(final DominioCaracteristicaCentroCusto caracteristica) {
		return getFccCentroCustosDAO().pesquisarCentroCustoUsuarioCaracteristica(servidorLogadoFacade.obterServidorLogado(), caracteristica);
	}
	
	@Override
	public List<FccCentroCustos> pesquisarCentroCustoUsuarioGerarSs(){
		return getFccCentroCustosDAO().pesquisarCentroCustoUsuarioGerarSs(servidorLogadoFacade.obterServidorLogado());
	}
	
	@Override
	public List<FccCentroCustos> pesquisarCentroCustoUsuarioGerarSCSuggestion(Object paramPesquisa){
		return this.getCentroCustoON().pesquisarCentroCustoUsuarioGerarSCSuggestion(paramPesquisa);
	}
	
	@Override
	public Long pesquisarCentroCustoUsuarioGerarSCSuggestionCount(Object paramPesquisa){
		return this.getCentroCustoON().pesquisarCentroCustoUsuarioGerarSCSuggestionCount(paramPesquisa);
	}

	@Override
	public boolean centroCustoAceitaProjeto(FccCentroCustos centroCusto){
		return getFccCentroCustosDAO().centroCustoAceitaProjeto(servidorLogadoFacade.obterServidorLogado(), centroCusto);
		
	}
	
	@Override
	public FccCentroCustos pesquisarCentroCustoAtuacaoLotacaoServidor() {
		return getFccCentroCustosDAO().pesquisarCentroCustoAtuacaoLotacaoServidor(servidorLogadoFacade.obterServidorLogado());
	}
	
	@Override
	public List<FccCentroCustos> pesquisarCentroCustosComLimitResult(
			Object objPesquisa, Integer limit) {
		return getFccCentroCustosDAO().pesquisarCentroCustosComLimitResult(objPesquisa, limit);
	}
	
	public List<FccCentroCustos> pesquisarCentroCustoUsuarioGerarSSSuggestion(Object paramPesquisa){
		return centroCustoON.pesquisarCentroCustoUsuarioGerarSSSuggestion(paramPesquisa, servidorLogadoFacade.obterServidorLogado());			
    }
	
	@Override
	public List<FccCentroCustos> pesquisarCentroCusto(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			SigCentroProducao centroProducao, DominioTipoCentroProducaoCustos tipo, String descricao, DominioSituacao situacao) {
		return getFccCentroCustosDAO().pesquisarCentroCusto(firstResult, maxResult, orderProperty, asc, centroProducao, tipo, descricao, situacao);
	}

	@Override
	public Long pesquisarCentroCustoCount(SigCentroProducao centroProducao, DominioTipoCentroProducaoCustos tipo, String descricao, DominioSituacao situacao) {
		return getFccCentroCustosDAO().pesquisarCentroCustoCount(centroProducao, tipo, descricao, situacao);
	}

	@Override
	public List<FccCentroCustos> pesquisarCentroCustoComStatusDaEspecialidade(Object paramPesquisa, DominioSituacao ativaOuInativa) {
		return this.getFccCentroCustosDAO().pesquisarCentroCustoComStatusDaEspecialidade(paramPesquisa, ativaOuInativa);
	}
	
	@Override
	public Set<Integer> pesquisarCentroCustoComHierarquia(Integer cctCodigo) {
		return getFccCentroCustosDAO().pesquisarCentroCustoComHierarquia(cctCodigo);
	}
	
	@Override
	public List<FccCentroCustos> pesquisarCentroCustoPorCodigoEDescricao(Object paramPesquisa) {
		return this.getFccCentroCustosDAO().pesquisarCentroCustoPorCodigoEDescricao(paramPesquisa);
	}

	@Override
	public Long pesquisarCentroCustoPorCodigoEDescricaoCount(Object paramPesquisa) {
		return this.getFccCentroCustosDAO().pesquisarCentroCustoPorCodigoEDescricaoCount(paramPesquisa);
	}
	
	@Override
	public CentroCustosVO obterCentroCustoParaSolicitacaoCompraOuServico(Integer numeroSolicitacaoCompraServico, boolean isSolicitacaoCompra) {
		return this.getFccCentroCustosDAO().obterCentroCustoParaSolicitacaoCompraOuServico(numeroSolicitacaoCompraServico, isSolicitacaoCompra);
	}
	protected FcuAgrupaGrupoMaterialDAO getFcuAgrupaGrupoMaterialDAO() {
		return fcuAgrupaGrupoMaterialDAO;
	}

	@Override
	public List<FcuAgrupaGrupoMaterial> pesquisarFcuAgrupaGrupoMaterialAtivos(String param, Integer maxResults) {
		return getFcuAgrupaGrupoMaterialDAO().pesquisarFcuAgrupaGrupoMaterialAtivos(param, maxResults);
	}

	@Override
	public Long pesquisarFcuAgrupaGrupoMaterialAtivosCount(String param) {
		return getFcuAgrupaGrupoMaterialDAO().pesquisarFcuAgrupaGrupoMaterialAtivosCount(param);
	}

	@Override
	public List<FccCentroCustos> pesquisarCCLotacaoEAtuacaoFuncionario(
			Object filtro) {
		return getFccCentroCustosDAO().pesquisarCCLotacaoEAtuacao(filtro);
	}

	@Override
	public Long pesquisarCCLotacaoEAtuacaoFuncionarioCount(Object filtro) {
		return getFccCentroCustosDAO().pesquisarCCLotacaoEAtuacaoCount(filtro);
	}

	@Override
	public FccCentroCustos obterCentroCustoPorUnidadeFuncional( Short unfSeq ){
		return getFccCentroCustosDAO().obterCentroCustoPorUnidadeFuncional(unfSeq);
	}	@Override
	public Long pesquisarCentroCustosAtivosCount(String objPesquisa) {
		return this.fccCentroCustosDAO.pesquisarCentroCustosAtivosCount(objPesquisa);
	}

	@Override
	public Long pesquisarCentroCustosAtivosOrdemDescricaoCountL(String objPesquisa) {
		return this.fccCentroCustosDAO.pesquisarCentroCustosAtivosOrdemDescricaoCountL(objPesquisa);
	}

	@Override
	public Long obterCentroCustoAtivosCount() {
		return this.fccCentroCustosDAO.obterCentroCustoAtivosCount();
	}

	@Override
	public List<FccCentroCustos> pesquisarCentroCustosAtivosOrdemOuDescricao(String objPesquisa) {
		return this.fccCentroCustosDAO.pesquisarCentroCustosAtivosOrdemOuDescricao(objPesquisa);
	}
	
	@Override
	public Long pesquisarCentroCustosAtivosOrdemOuDescricaoCount(String objPesquisa) {
		return this.fccCentroCustosDAO.pesquisarCentroCustosAtivosOrdemDescricaoCountL(objPesquisa);
	}
	
	@Override
	public void enviarEmailNotificacaoChefe(FccCentroCustos centroCusto) throws ApplicationBusinessException{
		centroCustoON.enviarEmailNotificacaoChefe(centroCusto);
	}

}