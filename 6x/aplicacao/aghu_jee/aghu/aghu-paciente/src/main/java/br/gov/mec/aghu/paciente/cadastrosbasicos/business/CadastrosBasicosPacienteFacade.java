package br.gov.mec.aghu.paciente.cadastrosbasicos.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTodosUltimo;
import br.gov.mec.aghu.model.AghOrigemEventos;
import br.gov.mec.aghu.model.AghSamis;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipBairros;
import br.gov.mec.aghu.model.AipCepLogradouros;
import br.gov.mec.aghu.model.AipCidades;
import br.gov.mec.aghu.model.AipCidadesDistritoSanitario;
import br.gov.mec.aghu.model.AipDistritoSanitarios;
import br.gov.mec.aghu.model.AipFinalidadesMovimentacao;
import br.gov.mec.aghu.model.AipLogradouros;
import br.gov.mec.aghu.model.AipNacionalidades;
import br.gov.mec.aghu.model.AipOcupacoes;
import br.gov.mec.aghu.model.AipPaises;
import br.gov.mec.aghu.model.AipSinonimosOcupacao;
import br.gov.mec.aghu.model.AipSolicitantesProntuario;
import br.gov.mec.aghu.model.AipTipoLogradouros;
import br.gov.mec.aghu.model.AipTituloLogradouros;
import br.gov.mec.aghu.model.AipUfs;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.dao.AipBairrosDAO;
import br.gov.mec.aghu.paciente.dao.AipCepLogradourosDAO;
import br.gov.mec.aghu.paciente.dao.AipCidadesDAO;
import br.gov.mec.aghu.paciente.dao.AipCidadesDistritoSanitarioDAO;
import br.gov.mec.aghu.paciente.dao.AipDistritoSanitariosDAO;
import br.gov.mec.aghu.paciente.dao.AipLogradourosDAO;
import br.gov.mec.aghu.paciente.dao.AipNacionalidadesDAO;
import br.gov.mec.aghu.paciente.dao.AipOcupacoesDAO;
import br.gov.mec.aghu.paciente.dao.AipPaisesDAO;
import br.gov.mec.aghu.paciente.dao.AipSinonimosOcupacaoDAO;
import br.gov.mec.aghu.paciente.dao.AipTipoLogradourosDAO;
import br.gov.mec.aghu.paciente.dao.AipTituloLogradourosDAO;
import br.gov.mec.aghu.paciente.dao.AipUfsDAO;
import br.gov.mec.aghu.paciente.vo.AipOcupacoesVO;
import br.gov.mec.aghu.paciente.vo.LogradouroVO;
import br.gov.mec.aghu.paciente.vo.ProfissaoVO;


@Modulo(ModuloEnum.PACIENTES)
@Stateless
public class CadastrosBasicosPacienteFacade extends BaseFacade implements ICadastrosBasicosPacienteFacade {

	
	@EJB
	private PaisCRUD paisCRUD;
	
	@EJB
	private DistritoSanitarioCRUD distritoSanitarioCRUD;
	
	@Inject
	private AipCidadesDistritoSanitarioDAO aipCidadesDistritoSanitarioDAO;
	
	@EJB
	private BairroCRUD bairroCRUD;
	
	@EJB
	private UfCRUD ufCRUD;
	
	@EJB
	private FinalidadeMovimentacaoCRUD finalidadeMovimentacaoCRUD;
	
	@EJB
	private OcupacaoCRUD ocupacaoCRUD;
	
	@EJB
	private NacionalidadeCRUD nacionalidadeCRUD;
	
	@EJB
	private SolicitanteProntuarioCRUD solicitanteProntuarioCRUD;
	
	@EJB
	private OrigemProntuarioCRUD origemProntuarioCRUD;
	
	@EJB
	private LogradouroCRUD logradouroCRUD;
	
	@EJB
	private CidadeCRUD cidadeCRUD;
	
	@Inject
	private AipDistritoSanitariosDAO aipDistritoSanitariosDAO;
	
	@Inject
	private AipUfsDAO aipUfsDAO;
	
	@Inject
	private AipCidadesDAO aipCidadesDAO;
	
	@Inject
	private AipTituloLogradourosDAO aipTituloLogradourosDAO;
	
	@Inject
	private AipTipoLogradourosDAO aipTipoLogradourosDAO;
	
	@Inject
	private AipPaisesDAO aipPaisesDAO;
	
	@Inject
	private AipNacionalidadesDAO aipNacionalidadesDAO;
	
	@Inject
	private AipCepLogradourosDAO aipCepLogradourosDAO;
	
	@Inject
	private AipOcupacoesDAO aipOcupacoesDAO;
	
	@Inject
	private AipSinonimosOcupacaoDAO aipSinonimosOcupacaoDAO;
	
	@Inject
	private AipLogradourosDAO aipLogradourosDAO;
	
	@Inject
	private AipBairrosDAO aipBairrosDAO;
	
	private static final long serialVersionUID = 8575351578055979329L;

	private CidadeCRUD getCidadeCRUD() {
		return cidadeCRUD;
	}
	
	protected BairroCRUD getBairroCRUD(){
		return bairroCRUD;
	}
	
	protected UfCRUD getUfCRUD(){
		return ufCRUD;
	}
	
	@Override
	public AipBairros obterBairro(Integer codigo){
		return getAipBairrosDAO().obterPorChavePrimaria(codigo);
	}

	protected AipBairrosDAO getAipBairrosDAO() {
		return aipBairrosDAO;
	}
	
	protected AipPaisesDAO getAipPaisesDAO() {
		return aipPaisesDAO;
	}
	
	protected AipCidadesDAO getAipCidadesDAO(){
		return aipCidadesDAO;
	}
	
	protected AipUfsDAO getAipUfsDAO(){
		return aipUfsDAO;
	}
	
	protected AipCepLogradourosDAO getAipCepLogradourosDAO() {
		return aipCepLogradourosDAO;
	}
	
	protected SolicitanteProntuarioCRUD getSolicitanteProntuarioCRUD() {
		return solicitanteProntuarioCRUD;
	}
	
	protected OrigemProntuarioCRUD getOrigemProntuarioCRUD() {
		return origemProntuarioCRUD;
	}
	
	@Override
	public List<AipBairros> pesquisarBairro(Integer firstResult,
			Integer maxResults, Integer codigo, String descricao) {

		return getAipBairrosDAO().pesquisarBairro(firstResult, maxResults,
				codigo, descricao);
	}
	
	@Override
	@Secure("#{s:hasPermission('bairro','alterar')}")
	public void persistirBairro(AipBairros bairro)
	throws ApplicationBusinessException{
		getBairroCRUD().persistirBairro(bairro);
	}
	
	@Override
	public List<AipBairros> pesquisarBairro(Object objPesquisa){
		return getAipBairrosDAO().pesquisarBairro(objPesquisa);
	}
	
	@Override
	public Long obterBairroCount(Integer codigo, String descricao) {
		return getAipBairrosDAO().obterBairroCount(codigo, descricao);
	}
	
	@Override
	@Secure("#{s:hasPermission('bairro','excluir')}")
	public void excluirBairro(Integer aipBairroCodigo) throws ApplicationBusinessException{
		getBairroCRUD().excluirBairro(aipBairroCodigo);
	}
	
	@Override
	public AipCidades obterCidade(Integer codigoCidade) {
		return getAipCidadesDAO().obterCidadePorCodigo(codigoCidade);
	}
	
	@Override
	public AipCidades obterCidadePorCodigo(Integer codigo, boolean obterDistritosSanitarios) {
		return getAipCidadesDAO().obterCidadePorCodigo(codigo, obterDistritosSanitarios);
	}
	
	@Override
	public AipCidades obterCidadePorCodigo(Integer codigo){
		return getAipCidadesDAO().obterCidadePorCodigo(codigo);
	}
	
	@Override
	public AipCidades obterCidadePorChavePrimaria(Integer codigo) {
		return getAipCidadesDAO().obterPorChavePrimaria(codigo);
	}
	
	@Override
	@Secure("#{s:hasPermission('cidade','excluir')")
	public void excluirCidade(Integer codigo) throws ApplicationBusinessException{
		getCidadeCRUD().excluirCidade(codigo);
	}
	
	@Override
	public Long obterCidadeCount(Integer codigo, Integer codIbge, String nome,  
            DominioSituacao situacao, Integer cep, Integer cepFinal, AipUfs uf){
		return getAipCidadesDAO().obterCidadeCount(codigo, codIbge, nome, situacao, cep, cepFinal, uf);
	}
	
	/**
	 * Método para pesquisar cidades que contemplam restrições informadas pelo usuário na tela de pesquisa de cidades.
	 *  
	 * @return Lista de cidades
	 */
	@Override
	public List<AipCidades> pesquisarCidades(Integer firstResult, Integer maxResult, Integer codigo, Integer codIbge, String nome,  
			                                 DominioSituacao situacao, Integer cep, Integer cepFinal, AipUfs uf) {
		return getAipCidadesDAO().pesquisarCidades(firstResult, maxResult, codigo, codIbge, nome, situacao, cep, cepFinal, uf);
	}
	
	@Override
	public List<AipCidades> pesquisarCidades(Integer codigo, Integer codIbge, String nome,  
            DominioSituacao situacao, Integer cep, Integer cepFinal, AipUfs uf){
		
		return getAipCidadesDAO().pesquisarCidades(codigo, codIbge, nome,
				situacao, cep, cepFinal, uf);
	}

	@Override
	public List<AipCidades> pesquisarCidades(Object objPesquisa){
		return getAipCidadesDAO().pesquisarCidades(objPesquisa);
	}
	
	@Override
	public List<AipCidades> pesquisarPorCodigoNome(Object paramPesquisa, boolean ativas){
		return getAipCidadesDAO().pesquisarPorCodigoNome(paramPesquisa, ativas);
	}
	
	@Override
	public List<AipCidades> pesquisarPorCodigoNomeAlfabetica(Object paramPesquisa, boolean ativas) {
		return getAipCidadesDAO().pesquisarPorCodigoNomeAlfabetica(paramPesquisa, ativas);
	}

	@Override
	public Long pesquisarCidadePorCodigoNomeCount(String strPesquisa) {
		return getAipCidadesDAO().pesquisarCidadePorCodigoNomeCount(strPesquisa);
	}
	
	@Override
	public List<AipCidades> pesquisarCidadePorCodigoNome(String strPesquisa){
		return getAipCidadesDAO().pesquisarCidadePorCodigoNome(strPesquisa);
	}
	
	@Override
	public long pesquisarCountCidadePorCodigoNome(String strPesquisa){
		return getAipCidadesDAO().pesquisarCountCidadePorCodigoNome(strPesquisa);
	}
	
	@Override
	@Secure("#{s:hasPermission('distritoSanitario','pesquisar')}")
	public List<AipDistritoSanitarios> pesquisarDistritoSanitariosPorIds(AipCidades cidade){
		return getCidadeCRUD().pesquisarDistritoSanitariosPorIds(cidade);
	}
	
	@Override
	@Secure("#{s:hasPermission('cidade','alterar')}")
	public void persistirCidade(AipCidades cidade, List<AipDistritoSanitarios> distritos) throws ApplicationBusinessException{
		getCidadeCRUD().persistirCidade(cidade, distritos);
	}
	
	@Override
	public List<AipCidades> pesquisarCidadePorNome(Object strPesquisa){
		return getAipCidadesDAO().pesquisarCidadePorNome(strPesquisa);
	}
	

	
	@Override
	public AipUfs obterUF(String aipSiglaUF){
		return getAipUfsDAO().obterUF(aipSiglaUF);
	}
	
	@Override
	@Secure("#{s:hasPermission('uf','alterar')}")
	public void salvarUF(AipUfs uf) throws ApplicationBusinessException{
		getUfCRUD().salvarUF(uf);
	}
	
	@Override
	@Secure("#{s:hasPermission('uf','alterar')}")
	public void alterarUF(AipUfs uf) throws ApplicationBusinessException{
		getUfCRUD().alterarUF(uf);
	}
	
	@Override
	@Secure("#{s:hasPermission('uf','excluir')}")
	public void removerUF(String sigla) throws ApplicationBusinessException{
		getUfCRUD().removerUF(sigla);
	}
	
	@Override
	public List<AipPaises> pesquisarPaisesPorDescricao(String strPesquisa){
		return getAipPaisesDAO().pesquisarPaisesPorDescricao(strPesquisa);
	}
	
	@Override
	public List<AipUfs> pesquisaUFs(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, String sigla, String nome,
			String siglaPais, DominioSimNao cadastraCidades) {
		return getAipUfsDAO().pesquisaUFs(firstResult, maxResults,
				orderProperty, asc, sigla, nome, siglaPais, cadastraCidades);
	}
	
	@Override
	public List<AipUfs> listarTodosUFs() {
		return getAipUfsDAO().listarTodos();
	}
	
	@Override
	public Long pesquisaUFsCount(String sigla, String nome,
			String siglaPais, DominioSimNao cadastraCidades) {
		return getAipUfsDAO().pesquisaUFsCount(sigla, nome, siglaPais, cadastraCidades);
	}
	
	@Override
	public List<AipUfs> pesquisarPorSiglaNome(Object paramPesquisa){
		return getAipUfsDAO().pesquisarPorSiglaNome(paramPesquisa);
	}
	
	@Override
	public List<AipUfs> pesquisarPorSiglaNomePermiteCidades(Object paramPesquisa){
		return getAipUfsDAO().pesquisarPorSiglaNomePermiteCidades(paramPesquisa);
	}
	
	@Override
	public List<AipUfs> pesquisarPorSiglaNomeSemLike(Object paramPesquisa){
		return getAipUfsDAO().pesquisarPorSiglaNomeSemLike(paramPesquisa);
	}
	
	@Override
	public Long obterCountUfPorSiglaNome(Object paramPesquisa){
		return getAipUfsDAO().obterCountUfPorSiglaNome(paramPesquisa);
	}
	
	@Override
	public List<AipUfs> pesquisarPorSiglaEntaoNome(String valor){
		return getAipUfsDAO().pesquisarPorSiglaEntaoNome(valor);
	}
	
	@Override
	public Long pesquisarPaisesPorDescricaoCount(String strPesquisa){
		return getAipPaisesDAO().pesquisarPaisesPorDescricaoCount(strPesquisa);
	}
	
	@Override
	public AipUfs obterUfSemLike(String aipSiglaUF){
		return getAipUfsDAO().obterUfSemLike(aipSiglaUF);
	}
	
	protected AipLogradourosDAO getAipLogradourosDAO(){
		return aipLogradourosDAO;
	}
	
	protected AipTipoLogradourosDAO getAipTipoLogradourosDAO(){
		return aipTipoLogradourosDAO;
	}
	
	protected AipDistritoSanitariosDAO getAipDistritoSanitariosDAO(){
		return aipDistritoSanitariosDAO;
	}
	
	protected AipTituloLogradourosDAO getAipTituloLogradourosDAO(){
		return aipTituloLogradourosDAO;
	}
	
	protected LogradouroCRUD getLogradouroCRUD(){
		return logradouroCRUD;
	}
	
	protected DistritoSanitarioCRUD getDistritoSanitarioCRUD(){
		return distritoSanitarioCRUD;
	}
	
	protected PaisCRUD getPaisCRUD(){
		return paisCRUD;
	}
	
	@Override
	@Secure("#{s:hasPermission('logradouro','pesquisar')}")
	public AipLogradouros obterLogradouroPorCodigo(Integer codigo){
		return getLogradouroCRUD().obterLogradouroPorCodigoComCeps(codigo);
	}
	
	@Override
	@Secure("#{s:hasPermission('logradouro','alterar')}")
	public void persistirLogradouro(AipLogradouros aipLogradouro) throws ApplicationBusinessException{
		getLogradouroCRUD().persistirLogradouro(aipLogradouro);
	}
	
	@Override
	public void cancelaEdicaoOuInclusaoLogradouro(AipLogradouros logradouro){
		getAipLogradourosDAO().cancelaEdicaoOuInclusaoLogradouro(logradouro);
	}
	
	@Override
	@Secure("#{s:hasPermission('tipoLogradouro','pesquisar')}")
	public List<AipTipoLogradouros> pesquisarTipoLogradouro(Object descricao){
		return getAipTipoLogradourosDAO().pesquisarTipoLogradouro(descricao);
	}
	
	@Override
	public List<AipTituloLogradouros> pesquisarTituloLogradouro(Object descricao){
		return getAipTituloLogradourosDAO().pesquisarTituloLogradouro(descricao);
	}
	
	@Override
	public List<AipCidades> pesquisarCidadesParaLogradouro(Object objPesquisa){
		return getAipCidadesDAO().pesquisarCidadesParaLogradouro(objPesquisa);
	}
	
	@Override
	public Long obterLogradouroVOCount(String nome, AipCidades aipCidades){
		return getAipLogradourosDAO().obterLogradouroVOCount(nome, aipCidades);
	}
	
	@Override
	public List<LogradouroVO> pesquisarLogradouroVO(Integer firstResult, Integer maxResults, String nome, AipCidades aipCidades){
		return getAipLogradourosDAO().pesquisarLogradouroVO(firstResult, maxResults, nome, aipCidades);
	}
	
	@Override
	@Secure("#{s:hasPermission('logradouro','excluir')}")
	public void excluirLogradouro(Integer codigoLogradouro) throws ApplicationBusinessException{
		getLogradouroCRUD().excluirLogradouro(codigoLogradouro);
	}
	
	@Override
	@Secure("#{s:hasPermission('logradouro','excluir')}")
	public void excluirBairroCepLogradouro(Integer codigoLogradouro, Integer cep, Integer codigoBairro) throws ApplicationBusinessException{
		getLogradouroCRUD().excluirBairroCepLogradouro(codigoLogradouro, cep, codigoBairro);
	}
	
	@Override
	@Secure("#{s:hasPermission('tipoLogradouro','alterar')}")
	public void persistirTipoLogradouro(AipTipoLogradouros tipoLogradouro)throws ApplicationBusinessException{
		getLogradouroCRUD().persistirTipoLogradouro(tipoLogradouro);
	}
	
	@Override
	@Secure("#{s:hasPermission('tipoLogradouro','pesquisar')}")
	public AipTipoLogradouros obterTipoLogradouroPorCodigo(Integer codigo){
		return getAipTipoLogradourosDAO().obterPorChavePrimaria(codigo);
	}
	
	@Override
	public Long obterTipoLogradouroCount(String abreviatura, String descricao){
		return getAipTipoLogradourosDAO().obterTipoLogradouroCount(abreviatura, descricao);
	}
	
	@Override
	public List<AipTipoLogradouros> pesquisarTipoLogradouro(
			Integer firstResult, Integer maxResult, String abreviatura,
			String descricao){
		return getAipTipoLogradourosDAO().pesquisarTipoLogradouro(firstResult,
				maxResult, abreviatura, descricao);
	}
	
	@Override
	@Secure("#{s:hasPermission('tipoLogradouro','excluir')}")
	public void removerTipoLogradouro(AipTipoLogradouros tipoLogradouro)
	throws ApplicationBusinessException{
		getLogradouroCRUD().removerTipoLogradouro(tipoLogradouro);
	}
	
	@Override
	public AipTituloLogradouros obterTituloLogradouroPorCodigo(Integer codigo){
		return getAipTituloLogradourosDAO().obterPorChavePrimaria(codigo);
	}
	
	@Override
	@Secure("#{s:hasPermission('tituloLogradouro','alterar')}")
	public void persistirTituloLogradouro(AipTituloLogradouros tituloLogradouro)
	throws ApplicationBusinessException{
		getLogradouroCRUD().persistirTituloLogradouro(tituloLogradouro);
	}
	
	@Override
	public Long obterTituloLogradouroCount(Integer codigo, String descricao){
		return getAipTituloLogradourosDAO().obterTituloLogradouroCount(codigo, descricao);
	}
	
	@Override
	public List<AipTituloLogradouros> pesquisarTituloLogradouro(
			Integer firstResult, Integer maxResult, Integer codigo,
			String descricao){
		return getAipTituloLogradourosDAO().pesquisarTituloLogradouro(
				firstResult, maxResult, codigo, descricao);
	}
	
	@Override
	@Secure("#{s:hasPermission('tituloLogradouro','excluir')}")
	public void removerTituloLogradouro(AipTituloLogradouros tituloLogradouro) throws ApplicationBusinessException{
		getLogradouroCRUD().removerTituloLogradouro(tituloLogradouro);
	}
	
	@Override
	@Secure("#{s:hasPermission('distritoSanitario','pesquisar')}")
	public AipDistritoSanitarios obterDistritoSanitarioPorCodigo(Short codigo){
		return getDistritoSanitarioCRUD().obterDistritoSanitarioPorCodigo(codigo);
	}
	
	@Override
	@Secure("#{s:hasPermission('distritoSanitario','pesquisar')}")
	public AipDistritoSanitarios obterDistritoSanitarioPorCodigo(Short codigo, Enum ...enums) {
		return getDistritoSanitarioCRUD().obterDistritoSanitarioPorCodigo(codigo, enums);
	}
	
	@Override
	public Long obterDistritoSanitarioCount(Short codigo, String descricao){
		return getAipDistritoSanitariosDAO().obterDistritoSanitarioCount(codigo, descricao);
	}
	
	@Override
	public List<AipDistritoSanitarios> pesquisarDistritoSanitario(
			Integer firstResult, Integer maxResult, Short codigo, String descricao){
		return getAipDistritoSanitariosDAO().pesquisarDistritoSanitario(
				firstResult, maxResult, codigo, descricao);
	}
	
	@Override
	@Secure("#{s:hasPermission('distritoSanitario','alterar')}")
	public void persistDistritoSanitario (AipDistritoSanitarios distritoSanitario,
				List<AipCidadesDistritoSanitario> cidadesInseridas, List<AipCidadesDistritoSanitario> cidadesExcluidas)
								throws ApplicationBusinessException{
		getDistritoSanitarioCRUD().persistDistritoSanitario(distritoSanitario, cidadesInseridas, cidadesExcluidas);
	}
	
	@Override
	@Secure("#{s:hasPermission('distritoSanitario','excluir')}")
	public void removerDistritoSanitario(AipDistritoSanitarios distritoSanitario) 
	throws ApplicationBusinessException{
		getDistritoSanitarioCRUD().removerDistritoSanitario(distritoSanitario);
	}
	
	@Override
	public List<AipDistritoSanitarios> pesquisarDistritoSanitariosPorCodigoDescricao(Object objPesquisa){
		return getAipDistritoSanitariosDAO().pesquisarPorCodigoDescricao(objPesquisa);
	}
	
	@Override
	public Long obterPaisCount(String sigla, String nome){
		return getAipPaisesDAO().obterPaisCount(sigla, nome);
	}
	
	@Override
	public List<AipPaises> pesquisarPais(Integer firstResult, Integer maxResult, String sigla, String nome){
		return getAipPaisesDAO().pesquisarPais(firstResult, maxResult, sigla, nome);
	}
	
	@Override
	@Secure("#{s:hasPermission('pais','pesquisar')}")
	public AipPaises obterPaisPorSigla(String sigla){
		return getAipPaisesDAO().obterPorChavePrimaria(sigla);
	}
	
	@Override
	@Secure("#{s:hasPermission('pais','excluir')}")
	public void removerPais(String sigla) throws ApplicationBusinessException{
		getPaisCRUD().removerPais(sigla);
	}
	
	@Override
	@Secure("#{s:hasPermission('pais','alterar')}")
	public void persistirPais(AipPaises pais, boolean edicao) throws ApplicationBusinessException{
		getPaisCRUD().persistirPais(pais, edicao);
	}
	
	@Override
	@Secure("#{s:hasPermission('pais','pesquisar')}")
	public AipPaises obterPaisPorNome(String nome){
		return getAipPaisesDAO().obterPaisPorNome(nome);
	}
	
	@Override
	public List<AipCepLogradouros> listarCepLogradourosPorCEP(Integer cep) {
		return getAipCepLogradourosDAO().listarCepLogradourosPorCEP(cep);
	}
	
	protected AipNacionalidadesDAO getAipNacionalidadesDAO(){
		return aipNacionalidadesDAO;
	}
	
	protected NacionalidadeCRUD getNacionalidadeCRUD(){
		return nacionalidadeCRUD;
	}
	
	@Override
	public Long pesquisaNacionalidadesCount(Integer codigo, String sigla,
			String descricao, DominioSituacao situacao){
		return getAipNacionalidadesDAO().pesquisaNacionalidadesCount(codigo,
				sigla, descricao, situacao);
	}
	
	@Override
	public List<AipNacionalidades> pesquisaNacionalidades(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc,
			Integer codigo, String sigla, String descricao,
			DominioSituacao situacao){
		
		return getAipNacionalidadesDAO().pesquisaNacionalidades(firstResult,
				maxResults, orderProperty, true, codigo, sigla, descricao,
				situacao);
		
	}
	
	@Override
	public AipNacionalidades obterNacionalidade(Integer codigo){
		return getAipNacionalidadesDAO().obterPorChavePrimaria(codigo);
	}
	
	@Override
	@Secure("#{s:hasPermission('nacionalidade','excluir')}")
	public void excluirNacionalidade(AipNacionalidades nacionalidade)
			throws ApplicationBusinessException {
		getNacionalidadeCRUD().excluirNacionalidade(nacionalidade);
	}
	
	@Override
	@Secure("#{s:hasPermission('nacionalidade','alterar')}")
	public void incluirNacionalidade(AipNacionalidades nacionalidade)
	throws ApplicationBusinessException{
		getNacionalidadeCRUD().incluirNacionalidade(nacionalidade);
	}
	
	@Override
	@Secure("#{s:hasPermission('nacionalidade','alterar')}")
	public void atualizarNacionalidade(AipNacionalidades nacionalidade)
	throws ApplicationBusinessException{
		getNacionalidadeCRUD().atualizarNacionalidade(nacionalidade);
	}
	
	@Override
	public List<AipNacionalidades> pesquisarNacionalidadesInclusiveInativasPorCodigoSiglaNome(
			Object paramPesquisa){
		return getAipNacionalidadesDAO()
				.pesquisarNacionalidadesInclusiveInativasPorCodigoSiglaNome(
						paramPesquisa);
	}
	
	
	@Override
	public Long pesquisarCountPorCodigoSiglaNome(Object paramPesquisa){
		return getAipNacionalidadesDAO().pesquisarCountPorCodigoSiglaNome(paramPesquisa);
	}
	
	@Override
	public List<AipNacionalidades> pesquisarNacionalidadesPessoaFisica(
			String valor){
		return getAipNacionalidadesDAO().pesquisarNacionalidadesPessoaFisica(valor);
	}

	@Override
	@Secure("#{s:hasPermission('logradouro','pesquisar')}")
	public List<Integer> listarLogradouros(Integer cep, Integer codigoTipoLogradouro) {
		return getAipLogradourosDAO().listarLogradouros(cep, codigoTipoLogradouro);
	}

	@Override
	@Secure("#{s:hasPermission('ocupacao','pesquisar')}")
	public AipOcupacoes obterOcupacao(Integer codigo){
		return getAipOcupacoesDAO().obterOcupacaoComSinonimos(codigo);
	}
	
	@Override
	@Secure("#{s:hasPermission('ocupacao','pesquisar')}")
	public AipOcupacoes obterOcupacao(Integer codigo, boolean left, Enum ...fields){
		return getAipOcupacoesDAO().obterPorChavePrimaria(codigo, left, fields);
	}
	
	@Override
	public List<AipSinonimosOcupacao> pesquisarSinonimosPorOcupacao(AipOcupacoes ocupacao){
		return getAipSinonimosOcupacaoDAO().pesquisarSinonimosPorOcupacao(ocupacao);
	}
	
	protected AipOcupacoesDAO getAipOcupacoesDAO(){
		return aipOcupacoesDAO;
	}
	
	@Override
	@Secure("#{s:hasPermission('ocupacao','excluir')}")
	public void removerOcupacao(Integer codigo) throws ApplicationBusinessException{
		getOcupacaoCRUD().removerOcupacao(codigo);
	}
	
	protected OcupacaoCRUD getOcupacaoCRUD(){
		return ocupacaoCRUD;
	}
	
	@Override
	public Long pesquisaOcupacoesCount(Integer codigo, String descricao){
		return getAipOcupacoesDAO().pesquisaCount(codigo, descricao);
	}
	
	@Override
	@Secure("#{s:hasPermission('ocupacao','pesquisar')}")
	public List<AipOcupacoesVO> pesquisarOcupacoes(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, Integer codigo, String descricao){
		
		return getOcupacaoCRUD().pesquisa(firstResult, maxResults,
				orderProperty, asc, codigo, descricao);
	}
	
	@Override
	public void validarSinonimo(AipSinonimosOcupacao sinonimo,
			List<AipSinonimosOcupacao> sinonimos, AipOcupacoes ocupacao)
	throws ApplicationBusinessException{
		
		getOcupacaoCRUD().validarSinonimo(sinonimo, sinonimos, ocupacao);
	}
	
	@Override
	@Secure("#{s:hasPermission('ocupacao','alterar')}")
	public void persistirOcupacao(AipOcupacoes ocupacao, final List<AipSinonimosOcupacao> sinonimosOcupacaoInseridos, final List<AipSinonimosOcupacao> sinonimosOcupacaoRemovidos) throws ApplicationBusinessException{
		getOcupacaoCRUD().persistirOcupacao(ocupacao, sinonimosOcupacaoInseridos, sinonimosOcupacaoRemovidos);
	}
	
	@Override
	public List<AipOcupacoes> pesquisarPorCodigoDescricao(Object paramPesquisa){
		return getAipOcupacoesDAO().pesquisarPorCodigoDescricao(paramPesquisa);
	}
	
	@Override
	public List<ProfissaoVO> pesquisarProfissioesPorCodigoDescricao(String strPesquisa){
		return getAipOcupacoesDAO().pesquisarProfissioesPorCodigoDescricao(strPesquisa);
	}
	
	@Override
	public Long pesquisarProfissioesPorCodigoDescricaoCount(
			String strPesquisa){
		
		return getAipOcupacoesDAO().pesquisarProfissioesPorCodigoDescricaoCount(strPesquisa);
	}
	
	@Override
	public AipOcupacoes obterAipOcupacoesPorChavePrimaria(Integer codigo) {
		return getAipOcupacoesDAO().obterPorChavePrimaria(codigo);
	}

	/* ### Início Finalidade Movimentação ### */
	protected FinalidadeMovimentacaoCRUD getFinalidadeMovimentacaoCRUD() {
		return finalidadeMovimentacaoCRUD;
	}

	@Override
	public Long pesquisaCount(Integer codigo, String descricao, DominioSituacao situacao) {
		return getFinalidadeMovimentacaoCRUD().pesquisaCount(codigo, descricao, situacao);
	}

	@Override
	@Secure("#{s:hasPermission('finalidadeMovimentacao','pesquisar')}")
	public List<AipFinalidadesMovimentacao> pesquisa(Integer firstResult, Integer maxResults, String orderProperty, boolean asc,
			Integer codigo, String descricao, DominioSituacao situacao) {
		return getFinalidadeMovimentacaoCRUD().pesquisa(firstResult, maxResults, orderProperty, asc, codigo, descricao, situacao);
	}

	@Override
	@Secure("#{s:hasPermission('finalidadeMovimentacao','pesquisar')}")
	public AipFinalidadesMovimentacao obterFinalidadeMovimentacao(Integer aipFinalidadeMovimentacaoCodigo) {
		AipFinalidadesMovimentacao retorno = getFinalidadeMovimentacaoCRUD().obterFinalidadeMovimentacao(
				aipFinalidadeMovimentacaoCodigo);
		return retorno;
	}

	@Override
	@Secure("#{s:hasPermission('finalidadeMovimentacao','excluir')}")
	public void excluirFinalidadeMovimentacao(AipFinalidadesMovimentacao finalidadesMovimentacao) throws ApplicationBusinessException {
		getFinalidadeMovimentacaoCRUD().excluirFinalidadeMovimentacao(finalidadesMovimentacao);
	}

	@Override
	@Secure("#{s:hasPermission('finalidadeMovimentacao','alterar')}")
	public void persistirFinalidadeMovimentacao(AipFinalidadesMovimentacao finalidadeMovimentacao) throws ApplicationBusinessException {
		getFinalidadeMovimentacaoCRUD().persistirFinalidadeMovimentacao(finalidadeMovimentacao);
	}

	/**
	 * @dbtables AipFinalidadesMovimentacao select
	 * 
	 * @param objPesquisa
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('finalidadeMovimentacao','pesquisar')}")
	public List<AipFinalidadesMovimentacao> pesquisarFinalidadeMovimentacaoPorCodigoEDescricao(Object objPesquisa) {
		return getFinalidadeMovimentacaoCRUD().pesquisarFinalidadeMovimentacaoPorCodigoEDescricao(objPesquisa);
	}

	@Override
	public Long pesquisaFinalidadeMovimentacaoCount(Integer codigo, String descricao) {
		return getFinalidadeMovimentacaoCRUD().pesquisaFinalidadeMovimentacaoCount(codigo, descricao);
	}

	@Override
	@Secure("#{s:hasPermission('finalidadeMovimentacao','pesquisar')}")
	public List<AipFinalidadesMovimentacao> pesquisaFinalidadesMovimentacao(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, Integer codigo, String descricao, DominioSituacao situacao) {
		return getFinalidadeMovimentacaoCRUD().pesquisaFinalidadesMovimentacao(firstResult, maxResults, orderProperty, asc, codigo,
				descricao, situacao);
	}
	
	@Override
	public List<AipDistritoSanitarios> pesquisarDistritoSanitarioPorCidadeCodigo(Integer cddCodigo) {
		return aipDistritoSanitariosDAO.pesquisarDistritoSanitarioPorCidadeCodigo(cddCodigo);
	}

	@Override
	@Secure("#{s:hasPermission('solicitanteProntuario','pesquisar')}")
	public AipSolicitantesProntuario obterSolicitanteProntuario(Short aipSolicitanteProntuarioCodigo) {
		return this.getSolicitanteProntuarioCRUD().obterSolicitanteProntuario(aipSolicitanteProntuarioCodigo);
	}

	@Override
	public Long obterSolicitanteProntuarioCount(Integer seq, DominioSituacao indSituacao, AghUnidadesFuncionais unidadeFuncional,
			AghOrigemEventos origemEvento, AipFinalidadesMovimentacao finalidadeMovimentacao, String descricao,
			Boolean exigeResponsavel, DominioSimNao mensagemSamis, DominioSimNao separacaoPrevia, DominioSimNao retorno,
			DominioTodosUltimo volumesManuseados) {
		return this.getSolicitanteProntuarioCRUD().obterSolicitanteProntuarioCount(seq, indSituacao, unidadeFuncional, origemEvento,
				finalidadeMovimentacao, descricao, exigeResponsavel, mensagemSamis, separacaoPrevia, retorno, volumesManuseados);
	}

	@Override
	@Secure("#{s:hasPermission('solicitanteProntuario','pesquisar')}")
	public List<AipSolicitantesProntuario> pesquisarSolicitanteProntuario(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, Integer seq, DominioSituacao indSituacao, AghUnidadesFuncionais unidadeFuncional,
			AghOrigemEventos origemEvento, AipFinalidadesMovimentacao finalidadeMovimentacao, String descricao,
			Boolean exigeResponsavel, DominioSimNao mensagemSamis, DominioSimNao separacaoPrevia, DominioSimNao retorno,
			DominioTodosUltimo volumesManuseados) {
		return this.getSolicitanteProntuarioCRUD().pesquisarSolicitanteProntuario(firstResult, maxResults, orderProperty, asc, seq,
				indSituacao, unidadeFuncional, origemEvento, finalidadeMovimentacao, descricao, exigeResponsavel, mensagemSamis,
				separacaoPrevia, retorno, volumesManuseados);
	}

	@Override
	@Secure("#{s:hasPermission('solicitanteProntuario','alterar')}")
	public void persistirSolicitanteProntuario(AipSolicitantesProntuario solicitanteProntuario) throws ApplicationBusinessException {
		this.getSolicitanteProntuarioCRUD().persistirSolicitanteProntuario(solicitanteProntuario);
	}

	public AipSinonimosOcupacaoDAO getAipSinonimosOcupacaoDAO() {
		return aipSinonimosOcupacaoDAO;
	}

	public AipCidadesDistritoSanitarioDAO getAipCidadesDistritoSanitarioDAO() {
		return aipCidadesDistritoSanitarioDAO;
	}

	@Override
	public List<AipCidadesDistritoSanitario> obterAipCidadesDistritoSanitarioPorAipDistritoSanitario(Short dstCodigo){
		return aipCidadesDistritoSanitarioDAO.obterAipCidadesDistritoSanitarioPorAipDistritoSanitario(dstCodigo);
	}

	@Override
	public List<AipNacionalidades> pesquisarPorCodigoSiglaNome(
			String paramPesquisa){
		return getAipNacionalidadesDAO().pesquisarPorCodigoSiglaNome(paramPesquisa);
	}

	@Override
	public void persistirOrigemProntuario(AghSamis samisOrigemProntuario,
			RapServidores servidorLogado) throws ApplicationBusinessException {
		getOrigemProntuarioCRUD().persistirOrigemProntuario(samisOrigemProntuario, servidorLogado);
	}

	@Override
	public Long pesquisaOrigemProntuarioCount(
			Integer codigoPesquisaOrigemProntuario, String descricaoPesquisa,
			DominioSituacao situacaoOrigemProntuario) {
		return getOrigemProntuarioCRUD().pesquisaCount(codigoPesquisaOrigemProntuario, descricaoPesquisa, situacaoOrigemProntuario);
	}

	public List<AghSamis> pesquisaOrigemProntuario(Integer firstResult, Integer maxResults, String orderProperty, boolean asc,
			Integer codigo, String descricao, DominioSituacao situacao) {
		return getOrigemProntuarioCRUD().pesquisaOrigemProntuario(firstResult, maxResults, orderProperty, asc, codigo, descricao, situacao);
	}

	@Override
	public AghSamis obterOrigemProntuario(Short codigoPesquisaOrigemProntuario) {
		AghSamis retorno = getOrigemProntuarioCRUD().obterOrigemProntuario(
				codigoPesquisaOrigemProntuario);
		return retorno;
	}

	@Override
	public void excluirOrigemProntuario(AghSamis samisOrigemProntuario, RapServidores servidorLogado, Short codigoSamisExclusao)
			throws ApplicationBusinessException {
		getOrigemProntuarioCRUD().excluirOrigemProntuario(samisOrigemProntuario, servidorLogado, codigoSamisExclusao);
	}
	
	@Override
	public List<AghSamis> pesquisaOrigemProntuarioPorCodigoOuDescricao(String param) {
		return getOrigemProntuarioCRUD().pesquisaOrigemProntuarioPorCodigoOuDescricao(param);
	}	
	
}