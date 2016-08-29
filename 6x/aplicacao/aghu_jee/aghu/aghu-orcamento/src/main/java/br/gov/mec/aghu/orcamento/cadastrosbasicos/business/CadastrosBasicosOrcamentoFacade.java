package br.gov.mec.aghu.orcamento.cadastrosbasicos.business;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.FsoFontesRecursoFinanc;
import br.gov.mec.aghu.model.FsoFontesXVerbaGestao;
import br.gov.mec.aghu.model.FsoGrupoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesaId;
import br.gov.mec.aghu.model.FsoParametrosOrcamento;
import br.gov.mec.aghu.model.FsoVerbaGestao;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.ScoSiasgServico;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.vo.FsoGrupoNaturezaDespesaCriteriaVO;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.vo.FsoParametrosOrcamentoCriteriaVO;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.vo.FsoParametrosOrcamentoCriteriaVO.Parametro;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.vo.FsoParametrosOrcamentoResultVO;
import br.gov.mec.aghu.orcamento.dao.FsoFontesRecursoFinancDAO;
import br.gov.mec.aghu.orcamento.dao.FsoGrupoNaturezaDespesaDAO;
import br.gov.mec.aghu.orcamento.dao.FsoNaturezaDespesaDAO;
import br.gov.mec.aghu.orcamento.dao.FsoVerbaGestaoDAO;
import br.gov.mec.aghu.orcamento.dao.ScoSiasgServicoDAO;


@Modulo(ModuloEnum.SUPRIMENTOS)
@Stateless
public class CadastrosBasicosOrcamentoFacade  
	extends BaseFacade 
	implements ICadastrosBasicosOrcamentoFacade{


@EJB
private FsoGrupoNaturezaDespesaSsParamON fsoGrupoNaturezaDespesaSsParamON;

@EJB
private FsoGrupoNaturezaDespesaON fsoGrupoNaturezaDespesaON;

@EJB
private FsoNaturezaDespesaON fsoNaturezaDespesaON;

@EJB
private FsoVerbaGestaoScParamON fsoVerbaGestaoScParamON;

@EJB
private FsoParametrosOrcamentoON fsoParametrosOrcamentoON;

@EJB
private FsoFontesRecursoFinancON fsoFontesRecursoFinancON;

@EJB
private VerbaGestaoON verbaGestaoON;

@EJB
private FsoVerbaGestaoSsParamON fsoVerbaGestaoSsParamON;

@EJB
private FsoGrupoNaturezaDespesaScParamON fsoGrupoNaturezaDespesaScParamON;

@EJB
private FccCentroCustoScParamON fccCentroCustoScParamON;

@EJB
private FsoNaturezaDespesaSsParamON fsoNaturezaDespesaSsParamON;

@EJB
private FsoNaturezaDespesaScParamON fsoNaturezaDespesaScParamON;

@Inject
private FsoFontesRecursoFinancDAO fsoFontesRecursoFinancDAO;

@Inject
private FsoGrupoNaturezaDespesaDAO fsoGrupoNaturezaDespesaDAO;

@Inject
private FsoNaturezaDespesaDAO fsoNaturezaDespesaDAO;

@Inject
private FsoVerbaGestaoDAO fsoVerbaGestaoDAO;

@Inject
private ScoSiasgServicoDAO scoSiasgServicoDAO;

	private static final long serialVersionUID = -7681242350064849295L;

	@Override
	@Secure("#{s:hasPermission('consultarApoioFinanceiro', 'visualizar')}")
	public Long countPesquisaListaNaturezaDespesa(FsoGrupoNaturezaDespesa grupoNatureza, String descricaoNatureza, DominioSituacao indSituacao) {
		return this.getFsoNaturezaDespesaON().countPesquisaListaNaturezaDespesa(grupoNatureza, descricaoNatureza, indSituacao);
	}

	@Override
	@Secure("#{s:hasPermission('consultarApoioFinanceiro', 'visualizar')}")
	public List<FsoNaturezaDespesa> pesquisarListaNaturezaDespesa(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, FsoGrupoNaturezaDespesa grupoNatureza, String descricaoNatureza, DominioSituacao indSituacao) {
		return this.getFsoNaturezaDespesaON().pesquisarListaNaturezaDespesa(firstResult, maxResults, 
				orderProperty, asc, grupoNatureza, descricaoNatureza, indSituacao);
	}

	@Override
	@Secure("#{s:hasPermission('consultarApoioFinanceiro', 'visualizar')}")
	public FsoNaturezaDespesa obterNaturezaDespesa(FsoNaturezaDespesaId codigoNatureza) {
		return this.getFsoNaturezaDespesaDAO().obterNaturezaDespesa(codigoNatureza);
	}

	@Override
	@Secure("#{s:hasPermission('consultarApoioFinanceiro', 'visualizar')}")
	public List<FsoGrupoNaturezaDespesa> pesquisarGrupoNaturezaDespesaPorCodigoEDescricao(final Object strPesquisa) {
		return this.getFsoGrupoNaturezaDespesaON().pesquisarGrupoNaturezaDespesaPorCodigoEDescricao(strPesquisa);
	}

	@Override
	@Secure("#{s:hasPermission('cadastrarApoioFinanceiro', 'gravar')}")
	public void inserirNaturezaDespesa(FsoNaturezaDespesa naturezaDespesa) throws ApplicationBusinessException  {
		this.getFsoNaturezaDespesaON().inserirNaturezaDespesa(naturezaDespesa);
	}

	@Override
	@Secure("#{s:hasPermission('cadastrarApoioFinanceiro', 'gravar')}")
	public void alterarNaturezaDespesa(FsoNaturezaDespesa naturezaDespesa) throws ApplicationBusinessException {
		this.getFsoNaturezaDespesaON().alterarNaturezaDespesa(naturezaDespesa);
	}

	@Override
	@Secure("#{s:hasPermission('cadastrarApoioFinanceiro', 'gravar')}")
	public void excluirNaturezaDespesa(FsoNaturezaDespesaId id) throws ApplicationBusinessException {
		this.getFsoNaturezaDespesaON().excluirNaturezaDespesa(id);
	}

	@Override
	public List<FsoVerbaGestao> pesquisarVerbaGestao(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			FsoVerbaGestao verbaGestaoFiltro, DominioSituacao situacao, Boolean convenio, String descricaoVerba,
			String nroInterno, BigInteger nroConvSiafi, String planoInterno){
		return getFsoVerbaGestaoDAO().pesquisarVerbaGestao(firstResult, maxResult, orderProperty, asc, verbaGestaoFiltro, situacao, convenio,
				descricaoVerba, nroInterno, nroConvSiafi, planoInterno);
	}

	@Override
	public Long pesquisarVerbaGestaoCount(FsoVerbaGestao verbaGestaoFiltro, DominioSituacao situacao, Boolean convenio, String descricaoVerba,
			String nroInterno, BigInteger nroConvSiafi, String planoInterno){
		return getFsoVerbaGestaoDAO().pesquisarVerbaGestaoCount(verbaGestaoFiltro, situacao, convenio, descricaoVerba,
				nroInterno, nroConvSiafi, planoInterno);
	}

	@Override
	@Secure("#{s:hasPermission('cadastrarApoioFinanceiro', 'gravar')}")
	public void excluirVerbaGestao(Integer seq) throws ApplicationBusinessException {
		this.getFsoVerbaGestaoON().excluirVerbaGestao(seq);
	}
	
	@Override
	public List<FsoFontesXVerbaGestao> pesquisarFontesXVerba(FsoVerbaGestao verbaGestao){
		return getFsoVerbaGestaoON().pesquisarFontesXVerba(verbaGestao);
	}

	@Override
	@Secure("#{s:hasPermission('cadastrarApoioFinanceiro', 'gravar')}")
	public void gravaFontesRecursoXVerbaGestao(FsoVerbaGestao verbaGestao,
			List<FsoFontesXVerbaGestao> inserirFontesXVerba,
			List<FsoFontesXVerbaGestao> removerFontesXVerba) throws ApplicationBusinessException{
		getFsoVerbaGestaoON().gravaFontesRecursoXVerbaGestao(verbaGestao, inserirFontesXVerba, removerFontesXVerba);		
	}
	
	@Override
	public List<FsoVerbaGestao> pesquisarVerbaGestaoPorSeqOuDescricao(
			Object paramPesquisa) {
		return getFsoVerbaGestaoDAO().pesquisarVerbaGestaoPorSeqOuDescricao(paramPesquisa);
	}

	@Override
	@Secure("#{s:hasPermission('consultarApoioFinanceiro', 'visualizar')}")
	public FsoGrupoNaturezaDespesa obterGrupoNaturezaDespesa(Integer codigo) {
		return getFsoGrupoNaturezaDespesaON().obterGrupoNaturezaDespesa(codigo);
	}

	@Override
	@Secure("#{s:hasPermission('consultarApoioFinanceiro', 'visualizar')}")
	public List<FsoGrupoNaturezaDespesa> pesquisarGruposNaturezaDespesa(
			FsoGrupoNaturezaDespesaCriteriaVO criteria, int first, int max,
			String orderField, Boolean orderAsc) {
		return getFsoGrupoNaturezaDespesaON().pesquisarGruposNaturezaDespesa(criteria, first, max, orderField, orderAsc);
	}

	@Override
	@Secure("#{s:hasPermission('consultarApoioFinanceiro', 'visualizar')}")
	public Long contarGruposNaturezaDespesa(
			FsoGrupoNaturezaDespesaCriteriaVO criteria) {
		return getFsoGrupoNaturezaDespesaON().contarGruposNaturezaDespesa(criteria);
	}

	@Override
	@Secure("#{s:hasPermission('cadastrarApoioFinanceiro', 'gravar')}")
	public void incluir(FsoGrupoNaturezaDespesa grupo)
			throws ApplicationBusinessException {
		getFsoGrupoNaturezaDespesaON().incluir(grupo);
	}

	@Override
	@Secure("#{s:hasPermission('cadastrarApoioFinanceiro', 'gravar')}")
	public void alterar(FsoGrupoNaturezaDespesa grupo) throws ApplicationBusinessException {
		getFsoGrupoNaturezaDespesaON().alterar(grupo);
	}

	@Override
	@Secure("#{s:hasPermission('cadastrarApoioFinanceiro', 'gravar')}")
	public void excluir(Integer codigo) throws ApplicationBusinessException {
		getFsoGrupoNaturezaDespesaON().excluir(codigo);
	}

	@Override
	public FsoVerbaGestao obterVerbaGestaoPorChavePrimaria(Integer seq) {
		return getFsoVerbaGestaoDAO().obterPorChavePrimaria(seq);
	}

	@Override
	@Secure("#{s:hasPermission('consultarApoioFinanceiro', 'visualizar')}")
	public Long countPesquisaFontesRecursoFinanc(FsoFontesRecursoFinanc fontesRecursoFinanc) {

		return this.getFsoFontesRecursoFinancON().countPesquisaFontesRecursoFinanc(fontesRecursoFinanc);
	}

	@Override
	public List<FsoFontesRecursoFinanc> pesquisarFonteRecursoPorCodigoOuDescricao(Object paramPesquisa){
		return getFsoFontesRecursoFinacDAO().pesquisarFonteRecursoPorCodigoOuDescricao(paramPesquisa);
	}

	@Override
	@Secure("#{s:hasPermission('consultarApoioFinanceiro', 'visualizar')}")
	public List<FsoFontesRecursoFinanc> listaPesquisaFontesRecursoFinanc(
			Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, 
			FsoFontesRecursoFinanc fontesRecursoFinanc) {

		return this.getFsoFontesRecursoFinancON().listaPesquisaFontesRecursoFinanc(firstResult, maxResults, 
						orderProperty, asc, fontesRecursoFinanc);
	}

	@Override
	@Secure("#{s:hasPermission('consultarApoioFinanceiro', 'visualizar')}")
	public FsoFontesRecursoFinanc obterFontesRecursoFinanc(Long codigoFonte) {
		return getFsoFontesRecursoFinancON().obterFontesRecursoFinanc(codigoFonte);
	}

	@Override
	@Secure("#{s:hasPermission('cadastrarApoioFinanceiro', 'gravar')}")
	public void incluirFontesRecursoFinanc(FsoFontesRecursoFinanc fontesRecursoFinanc) throws ApplicationBusinessException {
		this.getFsoFontesRecursoFinancON().incluirFontesRecursoFinanc(fontesRecursoFinanc);
	}

	@Override
	@Secure("#{s:hasPermission('cadastrarApoioFinanceiro', 'gravar')}")
	public void alterarFontesRecursoFinanc(FsoFontesRecursoFinanc fontesRecursoFinanc) throws ApplicationBusinessException {
		this.getFsoFontesRecursoFinancON().alterarFontesRecursoFinanc(fontesRecursoFinanc);
	}

	@Override
	@Secure("#{s:hasPermission('cadastrarApoioFinanceiro', 'gravar')}")
	public void excluirFontesRecursoFinanc(final Long codigo) throws ApplicationBusinessException {
		this.getFsoFontesRecursoFinancON().excluirFontesRecursoFinanc(codigo);
	}

	@Override
	public Boolean verificarFonteRecursoFinancUsadaEmVerbaGestao(FsoFontesRecursoFinanc fontesRecursoFinanc) {
		return this.getFsoFontesRecursoFinancON().verificarFonteRecursoFinancUsadaEmVerbaGestao(fontesRecursoFinanc);
	}

	@Override
	@Secure("#{s:hasPermission('consultarApoioFinanceiro', 'visualizar')}")
	public List<FsoParametrosOrcamentoResultVO> pesquisarParametrosOrcamento(
			FsoParametrosOrcamentoCriteriaVO criteria, int first, int max,
			String orderField, Boolean orderAsc) {
		return getFsoParametrosOrcamentoON().pesquisarParametrosOrcamento(
				criteria, first, max, orderField, orderAsc);
	}

	@Override
	@Secure("#{s:hasPermission('consultarApoioFinanceiro', 'visualizar')}")
	public Long contarParametrosOrcamento(
			FsoParametrosOrcamentoCriteriaVO criteria) {
		return getFsoParametrosOrcamentoON()
				.contarParametrosOrcamento(criteria);
	}
	
	@Override
	public FsoVerbaGestao obterVerbaGestaoProjetoFipe(FccCentroCustos ccAplic) {
		return getFsoParametrosOrcamentoON().obterVerbaGestaoProjetoFipe(ccAplic);
	}

	@Override
	@Secure("#{s:hasPermission('consultarApoioFinanceiro', 'visualizar')}")
	public List<FsoNaturezaDespesa> pesquisarNaturezasDespesa(
			FsoGrupoNaturezaDespesa grupo, Object filter) {
		return getFsoNaturezaDespesaON().pesquisarNaturezasDespesa(grupo,
				filter);
	}

	@Override
	@Secure("#{s:hasPermission('consultarApoioFinanceiro', 'visualizar')}")
	public FsoParametrosOrcamento obterParametrosOrcamento(Integer id) {
		return getFsoParametrosOrcamentoON().obterParametrosOrcamento(id);
	}

	@Override
	@Secure("#{s:hasPermission('consultarApoioFinanceiro', 'visualizar')}")
	public FsoParametrosOrcamento obterParametrosOrcamento(Integer id, Enum[] fetchArgsInnerJoin, Enum[] fetchArgsLeftJoin){
		return getFsoParametrosOrcamentoON().obterParametrosOrcamento(id, fetchArgsInnerJoin, fetchArgsLeftJoin);
	}
	

	@Override
	@Secure("#{s:hasPermission('cadastrarApoioFinanceiro', 'gravar')}")
	public void incluir(FsoParametrosOrcamento entidade) throws ApplicationBusinessException {
		getFsoParametrosOrcamentoON().incluir(entidade);
	}

	@Override
	@Secure("#{s:hasPermission('cadastrarApoioFinanceiro', 'gravar')}")
	public void alterar(FsoParametrosOrcamento entidade) throws ApplicationBusinessException {
		getFsoParametrosOrcamentoON().alterar(entidade);
	}

	@Override
	@Secure("#{s:hasPermission('cadastrarApoioFinanceiro', 'gravar')}")
	public void excluirFsoParametrosOrcamento(Integer seq) throws ApplicationBusinessException {
		getFsoParametrosOrcamentoON().excluir(seq);
		
	}
	
	@Override
	public FsoParametrosOrcamento clonarParametroOrcamento(FsoParametrosOrcamento parametroOriginal) throws ApplicationBusinessException{
		return getFsoParametrosOrcamentoON().clonarParametroOrcamento(parametroOriginal);
	}
	
	@Override
	@Secure("#{s:hasPermission('consultarApoioFinanceiro', 'visualizar')}")
	public FccCentroCustos getCentroCustoScParam(
			ScoMaterial material, FccCentroCustos centroCusto, BigDecimal valor) {
		return getFccCentroCustoScParamON().getCentroCustoScParam(material, centroCusto, valor);
	}

	@Override
	@Secure("#{s:hasPermission('consultarApoioFinanceiro', 'visualizar')}")
	public Boolean hasUniqueRequiredCentroCustoScParam(ScoMaterial material,
			FccCentroCustos centroCusto, BigDecimal valor) {
		return getFccCentroCustoScParamON()
				.hasUniqueRequiredCentroCustoScParam(material, centroCusto,
						valor);
	}

	@Override
	@Secure("#{s:hasPermission('consultarApoioFinanceiro', 'visualizar')}")
	public List<FccCentroCustos> listarCentroCustosScParams(
			ScoMaterial material, FccCentroCustos centroCusto,
			BigDecimal valorTotal, Object filter) {
		return getFccCentroCustoScParamON().listarCentroCustosScParams(
				material, centroCusto, valorTotal, filter);
	}

	@Override
	public Boolean isCentroCustoValidScParam(ScoMaterial material,
			FccCentroCustos ccSolicitante, BigDecimal valor,
			FccCentroCustos ccAplicacao) {
		return getFccCentroCustoScParamON().isCentroCustoValidScParam(
				material, ccSolicitante, valor, ccAplicacao);
	}

	@Override
	public Boolean hasUniqueRequiredVerbaGestaoScParam(ScoMaterial material,
			FccCentroCustos centroCusto, BigDecimal valor) {
		return getFsoVerbaGestaoScParamON()
				.hasUniqueRequiredVerbaGestaoScParam(material, centroCusto,
						valor);
	}

	@Override
	public FsoVerbaGestao getVerbaGestaoScParam(ScoMaterial material,
			FccCentroCustos centroCusto, BigDecimal valor) {
		return getFsoVerbaGestaoScParamON().getVerbaGestaoScParam(material,
				centroCusto, valor);
	}

	@Override
	@Secure("#{s:hasPermission('consultarApoioFinanceiro', 'visualizar')}")
	public List<FsoVerbaGestao> listarVerbasGestaoScParams(
			ScoMaterial material, FccCentroCustos centroCusto,
			BigDecimal valorTotal, Object filter) {
		return getFsoVerbaGestaoScParamON().listarVerbasGestaoScParams(
				material, centroCusto, valorTotal, filter);
	}

	@Override
	public Boolean isVerbaGestaoValidScParam(ScoMaterial material,
			FccCentroCustos ccSolicitante, BigDecimal valor,
			FsoVerbaGestao verbaGestao) {
		return getFsoVerbaGestaoScParamON().isVerbaGestaoValidScParam(
				material, ccSolicitante, valor, verbaGestao);
	}

	@Override
	public List<FsoVerbaGestao> pesquisarVerbaGestao(Object filter, Date data,
			Integer max) {
		return getFsoVerbaGestaoDAO().pesquisarVerbaGestao(filter, data, max);
	}

	@Override
	public Boolean existeVerbaGestaoComFonteVigente(Integer id, Date data) {
		return getFsoVerbaGestaoDAO().existeVerbaGestaoComFonteVigente(id, data);
	}

	@Override
	public boolean hasUniqueRequiredGrupoNaturezaScParam(ScoMaterial material,
			FccCentroCustos centroCusto, BigDecimal valor) {
		return getFsoGrupoNaturezaDespesaScParamON()
				.hasUniqueRequiredGrupoNaturezaScParam(material, centroCusto,
						valor);
	}

	@Override
	public FsoGrupoNaturezaDespesa getGrupoNaturezaScParam(
			ScoMaterial material, FccCentroCustos centroCusto,
			BigDecimal valor) {
		return getFsoGrupoNaturezaDespesaScParamON().getGrupoNaturezaScParam(material,
				centroCusto, valor);
	}

	@Override
	@Secure("#{s:hasPermission('consultarApoioFinanceiro', 'visualizar')}")
	public List<FsoGrupoNaturezaDespesa> listarGruposNaturezaScParams(
			ScoMaterial material, FccCentroCustos centroCusto,
			BigDecimal valorTotal, Object filter) {
		return getFsoGrupoNaturezaDespesaScParamON().listarGruposNaturezaScParams(
				material, centroCusto, valorTotal, filter);
	}

	@Override
	public boolean hasUniqueRequiredNaturezaScParam(ScoMaterial material,
			FccCentroCustos centroCusto, FsoGrupoNaturezaDespesa grupoNatureza,
			BigDecimal valor) {
		return getFsoNaturezaDespesaScParamON().hasUniqueRequiredNaturezaScParam(
				material, centroCusto, grupoNatureza, valor);
	}

	@Override
	public FsoNaturezaDespesa getNaturezaScParam(ScoMaterial material,
			FccCentroCustos centroCusto,
			FsoGrupoNaturezaDespesa grupoNatureza, BigDecimal valor) {
		return getFsoNaturezaDespesaScParamON().getNaturezaScParam(
				material, centroCusto, grupoNatureza, valor);
	}

	@Override
	@Secure("#{s:hasPermission('consultarApoioFinanceiro', 'visualizar')}")
	public List<FsoNaturezaDespesa> listarNaturezaScParams(
			ScoMaterial material, FccCentroCustos centroCusto,
			FsoGrupoNaturezaDespesa grupoNaturezaDespesa,
			BigDecimal valorTotal, Object filter) {
		return getFsoNaturezaDespesaScParamON().listarNaturezaScParams(material,
				centroCusto, grupoNaturezaDespesa, valorTotal, filter);
	}

	@Override
	public boolean isNaturezaValidScParam(ScoMaterial material,
			FccCentroCustos centroCusto, BigDecimal valor,
			FsoNaturezaDespesa naturezaDespesa) {
		return getFsoNaturezaDespesaScParamON().isNaturezaValidScParam(
				material, centroCusto, valor, naturezaDespesa);
	}
	
	public List<FsoNaturezaDespesa> pesquisarNaturezasDespesaAtivas(
			FsoGrupoNaturezaDespesa grupo, Object filter) {
		return getFsoNaturezaDespesaDAO().pesquisarNaturezasDespesaAtivas(
				grupo, filter);
	}
	
	@Override
	public Boolean existeNaturezaDespesaAtiva(FsoNaturezaDespesaId id) {
		return getFsoNaturezaDespesaDAO().existeNaturezaDespesaAtiva(id);
	}

	@Override
	public FsoParametrosOrcamento getAcaoScParam(ScoMaterial material,
			FccCentroCustos centroCusto, BigDecimal valor, Parametro param) {
		return getFsoParametrosOrcamentoON().getAcaoScParam(material,
				centroCusto, valor, param);
	}
	
	@Override
	public List<FsoNaturezaDespesa> listarTodasNaturezaDespesa(
			Object objPesquisa){
		return getFsoNaturezaDespesaDAO().listarTodasNaturezaDespesa(objPesquisa);
	}
	
	@Override
	public Long listarTodasNaturezaDespesaCount(Object objPesquisa) {
		return getFsoNaturezaDespesaDAO().listarTodasNaturezaDespesaCount(objPesquisa);
	}

	@Override
	@Secure("#{s:hasPermission('consultarApoioFinanceiro', 'visualizar')}")
	public boolean hasUniqueRequiredNaturezaSsParam(ScoServico servico,
			FsoGrupoNaturezaDespesa grupoNaturezaDespesa) {
		return getFsoNaturezaDespesaSsParamON()
				.hasUniqueRequiredNaturezaSsParam(servico, grupoNaturezaDespesa);
	}

	@Override
	@Secure("#{s:hasPermission('consultarApoioFinanceiro', 'visualizar')}")
	public FsoNaturezaDespesa getNaturezaSsParam(ScoServico servico,
			FsoGrupoNaturezaDespesa grupoNaturezaDespesa) {
		return getFsoNaturezaDespesaSsParamON().getNaturezaSsParam(
				servico, grupoNaturezaDespesa);
	}

	@Override
	@Secure("#{s:hasPermission('consultarApoioFinanceiro', 'visualizar')}")
	public List<FsoGrupoNaturezaDespesa> listarGruposNaturezaSsParams(
			ScoServico servico, Object filter) {
		return getFsoGrupoNaturezaDespesaSsParamON()
				.listarGruposNaturezaSsParams(servico, filter);
	}

	@Override
	@Secure("#{s:hasPermission('consultarApoioFinanceiro', 'visualizar')}")
	public List<FsoNaturezaDespesa> listarNaturezaSsParams(ScoServico servico,
			FsoGrupoNaturezaDespesa grupoNaturezaDespesa, Object filter) {
		return getFsoNaturezaDespesaSsParamON()
				.listarNaturezaSsParams(servico, grupoNaturezaDespesa, filter);
	}

	@Override
	@Secure("#{s:hasPermission('consultarApoioFinanceiro', 'visualizar')}")
	public List<FsoVerbaGestao> listarVerbasGestaoSsParams(ScoServico servico,
			Object filter) {
		return getFsoVerbaGestaoSsParamON()
				.listarVerbasGestaoScParams(servico, filter);
	}

	@Override
	@Secure("#{s:hasPermission('consultarApoioFinanceiro', 'visualizar')}")
	public Boolean hasUniqueRequiredGrupoNaturezaSsParam(ScoServico servico) {
		return getFsoGrupoNaturezaDespesaSsParamON()
				.hasUniqueRequiredGrupoNaturezaSsParam(servico);
	}

	@Override
	@Secure("#{s:hasPermission('consultarApoioFinanceiro', 'visualizar')}")
	public Boolean hasUniqueRequiredVerbaGestaoSsParam(ScoServico servico) {
		return getFsoVerbaGestaoSsParamON()
				.hasUniqueRequiredVerbaGestaoSsParam(servico);
	}

	@Override
	@Secure("#{s:hasPermission('consultarApoioFinanceiro', 'visualizar')}")
	public FsoGrupoNaturezaDespesa getGrupoNaturezaSsParam(ScoServico servico) {
		return getFsoGrupoNaturezaDespesaSsParamON().getGrupoNaturezaSsParam(servico);
	}

	@Override
	@Secure("#{s:hasPermission('consultarApoioFinanceiro', 'visualizar')}")
	public FsoVerbaGestao getVerbaGestaoSsParam(ScoServico servico) {
		return getFsoVerbaGestaoSsParamON().getVerbaGestaoSsParam(servico);
	}

	@Override
	@Secure("#{s:hasPermission('consultarApoioFinanceiro', 'visualizar')}")
	public boolean isNaturezaValidSsParam(ScoServico servico,
			FsoNaturezaDespesa naturezaDespesa) {
		return getFsoNaturezaDespesaSsParamON()
				.isNaturezaValidSsParam(servico, naturezaDespesa);
	}

	@Override
	@Secure("#{s:hasPermission('consultarApoioFinanceiro', 'visualizar')}")
	public boolean isVerbaGestaoValidSsParam(ScoServico servico,
			FsoVerbaGestao verbaGestao) {
		return getFsoVerbaGestaoSsParamON()
				.isVerbaGestaoValidSsParam(servico, verbaGestao);
	}
	
	public List<FsoNaturezaDespesa> pesquisarNaturezaDespesaPorGrupo(
			FsoGrupoNaturezaDespesa grupo, Object filter){
		return this.getFsoNaturezaDespesaDAO().pesquisarNaturezaDespesaPorGrupo(grupo, filter);
	}
	
	public Long pesquisarNaturezaDespesaPorGrupoCount(
			FsoGrupoNaturezaDespesa grupo, Object filter){
		return this.getFsoNaturezaDespesaDAO().pesquisarNaturezaDespesaPorGrupoCount(grupo, filter);
	}
	
	public List<FsoGrupoNaturezaDespesa> pesquisarGrupoNaturezaDespesaPorCodigoEDescricaoAtivos(final Object strPesquisa) {
		return this.getFsoGrupoNaturezaDespesaDAO().pesquisarGrupoNaturezaDespesaPorCodigoEDescricaoAtivos(strPesquisa);
	}

	
	@Override
	public List<ScoSiasgServico> pesquisarCatSer(Object objCatSer){
		return getScoSiasgServicoDAO().pesquisarCatSer(objCatSer);
	}
	
	private FsoGrupoNaturezaDespesaON getFsoGrupoNaturezaDespesaON() {
		return fsoGrupoNaturezaDespesaON;
	}

	private FsoNaturezaDespesaON getFsoNaturezaDespesaON() {
		return fsoNaturezaDespesaON;
	}

	private FsoVerbaGestaoDAO getFsoVerbaGestaoDAO() {
		return fsoVerbaGestaoDAO;
	}

	private FsoFontesRecursoFinancDAO getFsoFontesRecursoFinacDAO() {
		return fsoFontesRecursoFinancDAO;
	}

	private VerbaGestaoON getFsoVerbaGestaoON() {
		return verbaGestaoON;
	}

	private FsoFontesRecursoFinancON getFsoFontesRecursoFinancON() {
		return fsoFontesRecursoFinancON;
	}

	private FsoParametrosOrcamentoON getFsoParametrosOrcamentoON() {
		return fsoParametrosOrcamentoON;
	}
	
	private FsoNaturezaDespesaDAO getFsoNaturezaDespesaDAO() {
		return fsoNaturezaDespesaDAO;
	}
	
	private FsoGrupoNaturezaDespesaDAO getFsoGrupoNaturezaDespesaDAO() {
		return fsoGrupoNaturezaDespesaDAO;
	}
	
	private FsoGrupoNaturezaDespesaScParamON getFsoGrupoNaturezaDespesaScParamON() {
		return fsoGrupoNaturezaDespesaScParamON;
	}
	
	private FsoGrupoNaturezaDespesaSsParamON getFsoGrupoNaturezaDespesaSsParamON() {
		return fsoGrupoNaturezaDespesaSsParamON;
	}
	
	private FsoNaturezaDespesaScParamON getFsoNaturezaDespesaScParamON() {
		return fsoNaturezaDespesaScParamON;
	}
	
	private FsoNaturezaDespesaSsParamON getFsoNaturezaDespesaSsParamON() {
		return fsoNaturezaDespesaSsParamON;
	}
	
	private FccCentroCustoScParamON getFccCentroCustoScParamON() {
		return fccCentroCustoScParamON;
	}
	
	private FsoVerbaGestaoScParamON getFsoVerbaGestaoScParamON() {
		return fsoVerbaGestaoScParamON;
	}
	
	private FsoVerbaGestaoSsParamON getFsoVerbaGestaoSsParamON() {
		return fsoVerbaGestaoSsParamON;
	}
	
	private ScoSiasgServicoDAO getScoSiasgServicoDAO() {
		return scoSiasgServicoDAO;
	}

	@Override
	public List<FsoNaturezaDespesa> pesquisarFsoNaturezaDespesaAtivosPorGrupo(Integer seqGrupo, String param, Integer maxResults) {
		return getFsoNaturezaDespesaDAO().pesquisarFsoNaturezaDespesaAtivosPorGrupo(seqGrupo, param, maxResults);
	}
	
	@Override
	public Long pesquisarFsoNaturezaDespesaAtivosPorGrupoCount(Integer seqGrupo, String param) {
		return getFsoNaturezaDespesaDAO().pesquisarFsoNaturezaDespesaAtivosPorGrupoCount(seqGrupo, param);
	}
	
	@Override
	public FsoNaturezaDespesa obterFsoNaturezaDespesa(Integer seqGrupo, Byte codigo) {
		return getFsoNaturezaDespesaDAO().obterPorChavePrimaria(new FsoNaturezaDespesaId(seqGrupo, codigo));
	}
	
	@Override	
	@Secure("#{s:hasPermission('consultarApoioFinanceiro', 'visualizar')}")
	public FsoParametrosOrcamento pesquisarRegraOrcamentaria(FsoParametrosOrcamentoCriteriaVO criteria) {
		return getFsoParametrosOrcamentoON().pesquisarRegraOrcamentaria(criteria);
	}
	
	@Override
	public FsoNaturezaDespesa getNaturezaScGrupoMaterial(ScoMaterial material, BigDecimal paramVlrNumerico) {
		return getFsoParametrosOrcamentoON().pesquisarNaturezaScGrupoMaterial(material, paramVlrNumerico);
	}

	@Override
	public List<FsoGrupoNaturezaDespesa> obterListaGrupoNaturezaDespesaAtivosPorCodigoOuDescricao(String filter) {
		return this.getFsoGrupoNaturezaDespesaDAO().obterListaGrupoNaturezaDespesaAtivosPorCodigoOuDescricao(filter);
	}

	@Override
	public Long obterCountGrupoNaturezaDespesaAtivosPorCodigoOuDescricao(String filter) {
		return this.getFsoGrupoNaturezaDespesaDAO().obterCountGrupoNaturezaDespesaAtivosPorCodigoOuDescricao(filter);
	}

	@Override
	public List<FsoNaturezaDespesa> obterListaNaturezaDespesaAtivosPorGrupoCodigoOuDescricao(FsoGrupoNaturezaDespesa grupoNaturezaDespesa, String filter) {
		return this.getFsoNaturezaDespesaDAO().obterListaNaturezaDespesaAtivosPorGrupoCodigoOuDescricao(grupoNaturezaDespesa, filter);
	}

	@Override
	public Long obterCountNaturezaDespesaAtivosPorGrupoCodigoOuDescricao(FsoGrupoNaturezaDespesa grupoNaturezaDespesa, String filter) {
		return this.getFsoNaturezaDespesaDAO().obterCountNaturezaDespesaAtivosPorGrupoCodigoOuDescricao(grupoNaturezaDespesa, filter);
	}

	@Override
	public List<FsoVerbaGestao> obterListaVerbaGestaoAtivosPorSeqOuDescricao(String filter) {
		return getFsoVerbaGestaoDAO().obterListaVerbaGestaoAtivosPorSeqOuDescricao(filter);
	}

	@Override
	public Long obterCountVerbaGestaoAtivosPorSeqOuDescricao(String filter) {
		return getFsoVerbaGestaoDAO().obterCountVerbaGestaoAtivosPorSeqOuDescricao(filter);
	}
}