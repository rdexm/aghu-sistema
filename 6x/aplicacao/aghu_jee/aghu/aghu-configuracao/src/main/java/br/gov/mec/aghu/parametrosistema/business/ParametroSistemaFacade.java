package br.gov.mec.aghu.parametrosistema.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioFiltroParametrosPreenchidos;
import br.gov.mec.aghu.model.AghJobDetail;
import br.gov.mec.aghu.model.AghModuloAghu;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghSistemas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.parametrosistema.dao.AghModuloAghuDAO;
import br.gov.mec.aghu.parametrosistema.vo.AghParametroVO;
import br.gov.mec.aghu.parametrosistema.vo.AghSistemaVO;

/**
 * Porta de entrada do módulo Parametro Sistema.
 * 
 * @author lcmoura
 * 
 */

@Modulo(ModuloEnum.CONFIGURACAO)
@Stateless
public class ParametroSistemaFacade extends BaseFacade implements IParametroSistemaFacade {

	@EJB
	private ModuloAghuON moduloAghuON;
	
	@EJB
	private ParametroSistemaON parametroSistemaON;
	
	@EJB
	private ParametroSistemaCRUD parametroSistemaCRUD;
	
	@Inject
	private AghModuloAghuDAO aghModuloAghuDAO;
	private static final long serialVersionUID = -1146253241395102030L;
	
	@Override
	public List<AghSistemas> pesquisarSistemaPorNome(String nomeSistema) {
		return getParametroSistemaCRUD().pesquisarSistemaPorNome(nomeSistema);
	}

	@Override
	public AghSistemas buscaAghSistemaPorId(String id) {
		return getParametroSistemaCRUD().buscaAghSistemaPorId(id);
	}

	@Override
	public void persistirAghSistema(AghSistemas umAghSistema, boolean edicao) throws ApplicationBusinessException {
		getParametroSistemaCRUD().persistirAghSistema(umAghSistema, edicao);
	}
	
	@Override
	public boolean executarParametroSistema(final AghJobDetail job, final String descricao, final Boolean executar, final RapServidores servidor) throws ApplicationBusinessException {
		return getParametroSistemaCRUD().executarParametroSistema(job, descricao, executar, servidor);
	}
	
	@Override
	public Long pesquisaParametroSistemaListCount(String sigla, String nome) {
		return getParametroSistemaCRUD().pesquisaParametroSistemaListCount(sigla, nome);
	}

	@Override
	public List<AghSistemaVO> pesquisaParametroSistemaList(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			String sigla, String nome) {
		return getParametroSistemaCRUD().pesquisaParametroSistemaList(firstResult, maxResult, orderProperty, asc, sigla, nome);
	}

	@Override
	public void excluirAghSistema(AghSistemas sistema) throws ApplicationBusinessException {
		getParametroSistemaCRUD().excluirAghSistema(sistema);
	}
	
	@Override
	public void excluirAghSistema(String sigla) throws ApplicationBusinessException {
		getParametroSistemaCRUD().excluirAghSistema(sigla);
	}
	
	@Override
	public void excluirAghParametro(Integer seq) throws ApplicationBusinessException {
		getParametroSistemaCRUD().excluirAghParametro(seq);
	}

	protected ParametroSistemaCRUD getParametroSistemaCRUD() {
		return parametroSistemaCRUD;
	}

	protected ModuloAghuON getModuloAghuON() {
		return moduloAghuON;
	}
	
	private ParametroSistemaON getParametroSistemaON(){
		return parametroSistemaON;
	}
	
	protected AghModuloAghuDAO getAghModuloAghuDAO(){
		return aghModuloAghuDAO;
	}
	
	@Override
	public AghParametros obterParametroPorId(Integer sequencial){
		return getParametroSistemaON().obterParametroPorId(sequencial);
	}

	@Override
	public AghParametros obterParametroPorId(Integer sequencial, Enum[] fetchArgsInnerJoin, Enum[] fetchArgsLeftJoin){
		return getParametroSistemaON().obterParametroPorId(sequencial, fetchArgsInnerJoin, fetchArgsLeftJoin);
	}
	
	@Override
	public List<AghModuloAghu> obterTodosModulosAGHU() {
		return getParametroSistemaON().obterTodosModulosAGHU();
	}
	
	@Override
	public List<AghModuloAghu> pesquisarModulosParametroSistemas() {
		return getModuloAghuON().pesquisarModulosParametroSistemas();
	}
	
	@Override
	public List<AghParametros> obterParametrosSemQualquerValorAssociado(){
		return getParametroSistemaON().obterParametrosSemQualquerValorAssociado();
	}
	
	@Override
	public List<AghParametros> obterParametrosComValorAssociado(){
		return getParametroSistemaON().obterParametrosComValorAssociado();
	}
	
	@Override
	public void persistirParametro(AghParametros parametro) throws BaseException {
		getParametroSistemaCRUD().persistirAghParametro(parametro);
	}
	
	@Override
	public Long obterNumeroParametrosSemQualquerValorAssociado() {
		return getParametroSistemaON().obterNumeroParametrosSemQualquerValorAssociado();
	}
	
	@Override
	public List<AghParametros> pesquisarParametrosPorNomeModuloValorFiltro(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc, String nome,
			List<AghModuloAghu> modulos, Object valor, DominioFiltroParametrosPreenchidos filtroPreenchidos) {
		return getParametroSistemaON().pesquisarParametrosPorNomeModuloValorFiltro(firstResult,
			maxResult, orderProperty, asc, nome, modulos,
			valor, filtroPreenchidos);
	}

	@Override
	public Long pesquisarParametrosPorNomeModuloValorFiltroCount(String nome,
	List<AghModuloAghu> modulos, Object valor, DominioFiltroParametrosPreenchidos filtroPreenchidos) {
		return getParametroSistemaON().pesquisarParametrosPorNomeModuloValorFiltroCount(nome, modulos,
				valor, filtroPreenchidos);
	}

	@Override
	public void copiarValorPadraoCampoValor(Integer seq, String nomePessoa) throws ApplicationBusinessException {
		getParametroSistemaON().copiarValorPadraoCampoValor(seq, nomePessoa);
	}

	@Override
	public void atualizarValorParametro(Integer seq, Object valor, String nomePessoa) throws ApplicationBusinessException {
		getParametroSistemaON().atualizarValorParametro(seq, valor, nomePessoa);
		
	}
	
	@Override
	public AghParametros atualizarParametroSistema(AghParametros parametro, String nomePessoa) throws ApplicationBusinessException{
		return getParametroSistemaON().atualizarParametro(parametro, nomePessoa);
	}

	@Override
	public List<AghParametros> obterTodosParametros() {
		return getParametroSistemaON().obterTodosParametros();
	}
	
	@Override
	public List<AghModuloAghu> pesquisarModulosPorParametro(Integer seqParametro){
		return getAghModuloAghuDAO().pesquisarModulosPorParametro(seqParametro);
	}
	
	@Override
	public Long pesquisaParametroListCount(AghParametros parametro) {
		return getParametroSistemaCRUD().pesquisaParametroListCount(parametro);
	}

	@Override
	public List<AghParametroVO> pesquisaParametroList(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			AghParametros parametro) {
		return getParametroSistemaCRUD().pesquisaParametroList(firstResult, maxResult, orderProperty, asc, parametro);
	}
}
