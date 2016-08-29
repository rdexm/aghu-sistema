package br.gov.mec.aghu.parametrosistema.business;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioFiltroParametrosPreenchidos;
import br.gov.mec.aghu.dominio.DominioTipoDadoParametro;
import br.gov.mec.aghu.model.AghModuloAghu;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.parametrosistema.dao.AghModuloAghuDAO;
import br.gov.mec.aghu.parametrosistema.dao.AghParametrosDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

@Stateless
public class ParametroSistemaON extends BaseBusiness{
	
	@EJB
	private ParametroSistemaRN parametroSistemaRN;
	
	private static final Log LOG = LogFactory.getLog(ParametroSistemaON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@Inject
	private AghParametrosDAO aghParametrosDAO;
	
	@Inject
	private AghModuloAghuDAO aghModuloAghuDAO;
	

	private static final long serialVersionUID = 5304981411130937386L;

	public enum ParametroSistemaONExceptionCode implements BusinessExceptionCode{
		ERRO_SALVAR_PARAMETRO_TIPO_DADO_IMCOMPATIVEL,
		AGH_00016;
	}
	
	protected AghParametrosDAO getAghParametrosDAO(){
		return aghParametrosDAO;
	}
	
	protected AghModuloAghuDAO getAghModuloAghuDAO(){
		return aghModuloAghuDAO;
	}
	
	protected ParametroSistemaRN getParametroSistemaRN(){
		return parametroSistemaRN;
	}
	
	public AghParametros obterParametroPorId(Integer sequencial) {
		AghParametros param = getAghParametrosDAO().obterPorChavePrimaria(sequencial);
		param.getSistema().getNome();
		param.getSistema().getSigla();
		return param;
	}
	
	public AghParametros obterParametroPorId(Integer sequencial, Enum[] fetchArgsInnerJoin, Enum[] fetchArgsLeftJoin) {
		// Busca uma entidade para a ID informada, caso nao encontre instancia com este ID retorna erro
		// O erro retornado por este metodo serah tratado genericamente por: AghuExceptionHandlerWrapper
		getAghParametrosDAO().carregarPorChavePrimaria(sequencial);
		
		// Neste caso precisa fazer alguns joins para evitar Lazy Load na controller.
		return getAghParametrosDAO().obterPorChavePrimaria(sequencial, fetchArgsInnerJoin, fetchArgsLeftJoin);
	}
	
	/**
	 * Obtém a quantidade de paâmetros que não possuem valores associados,
	 * em qualquer dos campos vlrData, vlrNumerico, vlrTexto
	 * @author clayton.bras
	 * @return
	 */
	public Long obterNumeroParametrosSemQualquerValorAssociado() {
		return getAghParametrosDAO().obterNumeroParametrosSemQualquerValorAssociado();
	}
	
	/**
	 * Obtem os parâmetros que não possuem nenhum valor associado a nenhum dos campos de valor (Data, numérico nem texto).
	 * @return
	 * @author bruno.mourao
	 * @author clayton.bras
	 * @since 04/11/2011
	 */
	public List<AghParametros> obterParametrosSemQualquerValorAssociado(){
		return getAghParametrosDAO().obterParametrosSemQualquerValorAssociado();
	}
	
	/**
	 * Obtem os parâmetros que possuem pelo menos um campo de valor (data, nuḿerico ou texto) preenchido.
	 * @return
	 * @author bruno.mourao
	 * @since 04/11/2011
	 */
	public List<AghParametros> obterParametrosComValorAssociado(){
		return getAghParametrosDAO().obterParametrosComValorAssociado();
	}
	
	/**
	 * Obtem todos os parametros do sistema.
	 * @return List<AghParametros>
	 * @author bruno.mourao
	 * @since 04/11/2011
	 */
	public List<AghParametros> obterTodosParametros(){
		return getAghParametrosDAO().obterTodosParametros();
	}
	
	public AghParametros atualizarParametro(AghParametros parametro, String nomePessoa) throws ApplicationBusinessException{
		parametro.setAlteradoPor(nomePessoa);
		parametro.setAlteradoEm(new Date());
		
		validarNomeParametroDuplicado(parametro);
		
		Boolean compativel = verificarCompatibilidadeTipoDados(parametro);
		if(!compativel){
			throw new ApplicationBusinessException(ParametroSistemaONExceptionCode.ERRO_SALVAR_PARAMETRO_TIPO_DADO_IMCOMPATIVEL, Severity.ERROR,parametro.getTipoDado().getDescricao() );
		}
		
		AghParametros parametroAtualizado = getAghParametrosDAO().atualizar(parametro);
		
		//Gera journal
		getParametroSistemaRN().inserirJournal(parametro, DominioOperacoesJournal.UPD);
		
		return parametroAtualizado;
	}

	private void validarNomeParametroDuplicado(AghParametros parametro)
			throws ApplicationBusinessException {
		List<AghParametros> list =  getAghParametrosDAO().pesquisarAghParametroPorNome(parametro.getNome());
		if (!list.isEmpty()) {
			AghParametros p = list.get(0);
			if (!parametro.getSeq().equals(p.getSeq())) {
				throw new ApplicationBusinessException(ParametroSistemaONExceptionCode.AGH_00016);
			}
		}
	}

	private Boolean verificarCompatibilidadeTipoDados(AghParametros parametro) {
		//Verifica se o tipo de dados está compatível com o dado informado
		Boolean compativel = false;
		if(DominioTipoDadoParametro.T.equals(parametro.getTipoDado())){
			if(parametro.getVlrTexto()!=null && 
					parametro.getVlrNumerico() == null &&
					parametro.getVlrData() == null){
				compativel = true;
			}
		}
		else if(DominioTipoDadoParametro.N.equals(parametro.getTipoDado())){
			if(parametro.getVlrTexto()==null  && 
					parametro.getVlrNumerico() != null &&
					parametro.getVlrData() == null){
				compativel = true;
			}
		}
		else if(DominioTipoDadoParametro.D.equals(parametro.getTipoDado())){
			if(parametro.getVlrTexto()==null && 
					parametro.getVlrNumerico() == null &&
					parametro.getVlrData() != null){
				compativel = true;
			}
		}
		return compativel;
	}

	public List<AghModuloAghu> obterTodosModulosAGHU() {
		return getAghModuloAghuDAO().listarTodos();
	}

	
	/**
	 * Retorna a lista de parâmetros encontrados na pesquisa - RN1(RN2, RN3, RN4, RN5)
	 * @author clayton.bras
	 */
	public List<AghParametros> pesquisarParametrosPorNomeModuloValorFiltro(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc, String nome,
			List<AghModuloAghu> modulos, Object valor, DominioFiltroParametrosPreenchidos filtroPreenchidos) {
		return getAghParametrosDAO().pesquisarParametrosPorNomeModuloValorFiltro(firstResult,
				maxResult, orderProperty, asc, nome, modulos,
				valor, filtroPreenchidos);
	}

	/**
	 * Retorna a quantidade de parâmetros encontrados na pesquisa
	 * @author clayton.bras
	 */
	public Long pesquisarParametrosPorNomeModuloValorFiltroCount(String nome,
			List<AghModuloAghu> modulos, Object valor, DominioFiltroParametrosPreenchidos filtroPreenchidos) {
		return getAghParametrosDAO().pesquisarParametrosPorNomeModuloValorFiltroCount(nome, modulos,
				valor, filtroPreenchidos);
	}


	/**
	 * Copia e persiste o conteúdo presente em algum campo de valor padrão
	 * para o respectivo campo valor, de acordo com o tipo.
	 * @author clayton.bras
	 * @param seq
	 * @param nomePessoa 
	 *  
	 */
	public void copiarValorPadraoCampoValor(Integer seq, String nomePessoa) throws ApplicationBusinessException {
		
		if(seq!=null){
			AghParametros parametro = getAghParametrosDAO().obterPorChavePrimaria(seq);
			if(parametro.getValorPadrao()!=null){
				if(DominioTipoDadoParametro.D.equals(parametro.getTipoDado())){
					parametro.setVlrData((Date) parametro.getValorPadrao());
				}else if(DominioTipoDadoParametro.N.equals(parametro.getTipoDado())){
					parametro.setVlrNumerico((BigDecimal) parametro.getValorPadrao());
				}else{
					parametro.setVlrTexto((String) parametro.getValorPadrao());
				}
				atualizarParametro(parametro, nomePessoa);
			}
		}
		
	}

	/**
	 * Atualiza o campo valor, diretamente da respectiva coluna, no resultado
	 * da pesquisa
	 * @author clayton.bras
	 * @param nomePessoa 
	 * @param seq, valor
	 *  
	 */
	public void atualizarValorParametro(Integer seq, Object valor, String nomePessoa) throws ApplicationBusinessException {
		if(seq!=null){
			AghParametros parametro = getAghParametrosDAO().obterPorChavePrimaria(seq);
				parametro.setValor(valor);
				atualizarParametro(parametro, nomePessoa);
			}
	}
	
}
