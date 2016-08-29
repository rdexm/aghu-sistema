package br.gov.mec.aghu.paciente.cadastrosbasicos.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.bancosangue.IBancoDeSangueFacade;
import br.gov.mec.aghu.model.AipOcupacoes;
import br.gov.mec.aghu.model.AipSinonimosOcupacao;
import br.gov.mec.aghu.model.AipSinonimosOcupacaoId;
import br.gov.mec.aghu.paciente.dao.AipOcupacoesDAO;
import br.gov.mec.aghu.paciente.dao.AipPacientesDAO;
import br.gov.mec.aghu.paciente.dao.AipSinonimosOcupacaoDAO;
import br.gov.mec.aghu.paciente.vo.AipOcupacoesVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseOptimisticLockException;
import br.gov.mec.aghu.core.exception.BaseOptimisticLockException.BaseOptimisticLockExceptionCode;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável por prover os métodos de negócio genéricos para a entidade
 * de ocupações.
 */
@Stateless
public class OcupacaoCRUD extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(OcupacaoCRUD.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IBancoDeSangueFacade iBancoDeSangueFacade;

@Inject
private AipPacientesDAO aipPacientesDAO;

@Inject
private AipOcupacoesDAO aipOcupacoesDAO;

@Inject
private AipSinonimosOcupacaoDAO aipSinonimosOcupacaoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -5127321516785293688L;

	/**
	 * Enumeracao com os codigos de mensagens de excecoes negociais do cadastro
	 * ocupação.
	 * 
	 * Cada entrada nesta enumeracao deve corresponder a um chave no message
	 * bundle de ocupação.
	 * 
	 * Porém se não houver uma este valor será enviado como mensagem de execeção
	 * sem localização alguma.
	 */
	private enum OcupacaoCRUDExceptionCode implements BusinessExceptionCode {
		DESCRICAO_SINONIMO_OBRIGATORIO, DESCRICAO_OCUPACAO_OBRIGATORIO, ERRO_REMOVER_OCUPACAO, 
		SINONIMO_EXISTENTE, ERRO_PERSISTIR_OCUPACAO, DESCRICAO_OCUPACAO_JA_EXISTENTE, 
		ERRO_REMOVER_OCUPACAO_COM_DEPENDENCIA_EM_PACIENTES, 
		ERRO_REMOVER_OCUPACAO_COM_DEPENDENCIA_EM_SINONIMOS_OCUPACAO, 
		ERRO_REMOVER_OCUPACAO_COM_DEPENDENCIA_EM_CANDIDATOS_DOADORES,
		SINONIMOS_EXISTENTES;
	}

	/**
	 * Consulta a base de dados de entidades AipOcupacoes retornando uma lista 
	 * de instancias desta entidade que confere com codigo (Chave Primária) e contiver
	 * a descricao (LIKE).
	 * 
	 * @dbtables AipOcupacoes select
	 * 
	 * @param firstResult parâmetro que indica onde inicia a lista de retorno
	 * @param maxResults parâmetro que indica o núemro máximo de registros da lista de retorno
	 * @param orderProperty parâmetro que indica as colunas de ordenação para a consulta
	 * @param asc parâmetro que indica se é ascendente ou decrescente a ordenação
	 * @param codigo parâmetro que indica o código (Chave Primaria) de AipOcupacoes 
	 * @param descricao parâmetro que indica uma parte da descrição (LIKE) de AipOcupacoes
	 * 
	 * @return uma lista de AipOcupacoes que está de acordo com os parametros
	 */
	public List<AipOcupacoesVO> pesquisa(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, Integer codigo, String descricao) {

		return getAipOcupacoesDAO().pesquisarOcupacoes(firstResult, maxResults, orderProperty, asc, codigo, descricao);
	}


	/**
	 * Método responsável pela persistência de uma ocupação.
	 * 
	 * @dbtables AipOcupacoes insert,update
	 * 
	 * @param ocupacao
	 * @throws ApplicationBusinessException
	 */
	public void persistirOcupacao(AipOcupacoes ocupacao, final List<AipSinonimosOcupacao> sinonimosOcupacaoInseridos, final List<AipSinonimosOcupacao> sinonimosOcupacaoRemovidos) throws ApplicationBusinessException{
		if (ocupacao.getCodigo() == null) {
			// inclusão
			this.incluirOcupacao(ocupacao, sinonimosOcupacaoInseridos, sinonimosOcupacaoRemovidos);
		} else {
			// edição
			this.atualizarOcupacao(ocupacao, sinonimosOcupacaoInseridos, sinonimosOcupacaoRemovidos);
		}
	}

	/**
	 * Método responsável por incluir uma nova ocupação.
	 * 
	 * @param ocupacao
	 * @throws ApplicationBusinessException
	 */
	private void incluirOcupacao(AipOcupacoes ocupacao, final List<AipSinonimosOcupacao> sinonimosOcupacaoInseridos, final List<AipSinonimosOcupacao> sinonimosOcupacaoRemovidos) throws ApplicationBusinessException{
		
		this.validarDadosOcupacao(ocupacao);
		aipOcupacoesDAO.persistir(ocupacao);
		aipOcupacoesDAO.flush();
		
		removerSinonimosOcupacao(sinonimosOcupacaoRemovidos);
		salvarSinonimos(ocupacao, sinonimosOcupacaoInseridos);
	}

	/**
	 * Método responsável pela atualização de uma ocupação.
	 * 
	 * @param ocupacao
	 * @throws ApplicationBusinessException
	 */
	private void atualizarOcupacao(AipOcupacoes ocupacao, final List<AipSinonimosOcupacao> sinonimosOcupacaoInseridos, final List<AipSinonimosOcupacao> sinonimosOcupacaoRemovidos) throws ApplicationBusinessException{
		
		this.validarDadosOcupacao(ocupacao);
		
		this.getAipOcupacoesDAO().atualizar(ocupacao);

		removerSinonimosOcupacao(sinonimosOcupacaoRemovidos);
		salvarSinonimos(ocupacao, sinonimosOcupacaoInseridos);
	}
	
	private void salvarSinonimos(AipOcupacoes ocupacao, List<AipSinonimosOcupacao> sinonimos){
		short codSinonimo = aipSinonimosOcupacaoDAO.obterMaxCodigoAipSinonimosOcupacao(ocupacao);
		
		for (AipSinonimosOcupacao sinonimo : sinonimos) {
			sinonimo.setId(new AipSinonimosOcupacaoId(ocupacao.getCodigo(), ++codSinonimo));
			sinonimo.setAipOcupacoes(ocupacao);
			this.getAipSinonimosOcupacaoDAO().persistir(sinonimo);
		}
		getAipSinonimosOcupacaoDAO().flush();
	}
	
	private void removerSinonimosOcupacao(List<AipSinonimosOcupacao> listaSinonimos){
		for (AipSinonimosOcupacao sinonimo: listaSinonimos){
			aipSinonimosOcupacaoDAO.remover(aipSinonimosOcupacaoDAO.obterPorChavePrimaria(sinonimo.getId()));
		}
		getAipSinonimosOcupacaoDAO().flush();
	}

	/**
	 * Método responsável pelas validações dos dados de ocupação. Método
	 * utilizado para inclusão e atualização de ocupação.
	 * 
	 * @param ocupacao
	 * @throws ApplicationBusinessException
	 */
	private void validarDadosOcupacao(AipOcupacoes ocupacao) throws ApplicationBusinessException {
		
		if (StringUtils.isBlank(ocupacao.getDescricao())) {
			throw new ApplicationBusinessException(OcupacaoCRUDExceptionCode.DESCRICAO_OCUPACAO_OBRIGATORIO);
		}

		// validar descrição duplicada
		List<AipOcupacoes> ocupacoes = aipOcupacoesDAO.getOcupacoesComMesmaDescricao(ocupacao);
		if (ocupacoes != null && !ocupacoes.isEmpty()) {
			throw new ApplicationBusinessException(OcupacaoCRUDExceptionCode.DESCRICAO_OCUPACAO_JA_EXISTENTE);
		}
	}


	/**
	 * Valida as informações obrigatóris em um sinônimo.
	 * 
	 * @dbtables AipSinonimosOcupacao consulta
	 * 
	 * @param sinonimo
	 * @throws ApplicationBusinessException
	 */
	public void validarSinonimo(AipSinonimosOcupacao sinonimo,
			List<AipSinonimosOcupacao> sinonimosOcupacao, AipOcupacoes ocupacao)
			throws ApplicationBusinessException {
		if (StringUtils.isBlank(sinonimo.getDescricao())) {
			throw new ApplicationBusinessException(
					OcupacaoCRUDExceptionCode.DESCRICAO_SINONIMO_OBRIGATORIO);
		}

		List<AipSinonimosOcupacao> sinonimos = getAipSinonimosOcupacaoDAO().pesquisarSinonimosExcetoPelaOcupacao(ocupacao);
		sinonimos.addAll(sinonimosOcupacao);
		
		if (sinonimos != null) {
			for (AipSinonimosOcupacao sinonimoExistente: sinonimos){
				if (sinonimoExistente.getDescricao().equalsIgnoreCase(sinonimo.getDescricao())){
					throw new ApplicationBusinessException(OcupacaoCRUDExceptionCode.SINONIMO_EXISTENTE);					
				}
			}
		}
	}


	/**
	 * Apaga uma ocupação do banco de dados.
	 * 
	 * @dbtables AipOcupacoes delete
	 * 
	 * @param ocupacao
	 *            Ocupação a ser removida.
	 * @throws ApplicationBusinessException
	 */
	public void removerOcupacao(Integer codigo) throws ApplicationBusinessException {

		final AipOcupacoes ocupacao = aipOcupacoesDAO.obterPorChavePrimaria(codigo);
		
		if(ocupacao != null){
			Long candidatosCount = getBancoDeSangueFacade().pesquisarCandidatosDoadoresPorOcupacaoCount(ocupacao);
			if (candidatosCount > 0){
				throw new ApplicationBusinessException(OcupacaoCRUDExceptionCode.ERRO_REMOVER_OCUPACAO_COM_DEPENDENCIA_EM_CANDIDATOS_DOADORES);
			}
			
			Long pacientesCount = getAipPacientesDAO().pesquisarPacientesPorOcupacaoCount(ocupacao);
			if (pacientesCount> 0){
				throw new ApplicationBusinessException(OcupacaoCRUDExceptionCode.ERRO_REMOVER_OCUPACAO_COM_DEPENDENCIA_EM_PACIENTES);
			}
			
			this.getAipOcupacoesDAO().remover(ocupacao);
			
		} else {
			throw new BaseOptimisticLockException(BaseOptimisticLockExceptionCode.OPTIMISTIC_LOCK, codigo);
		}
	}


	
	protected AipOcupacoesDAO getAipOcupacoesDAO(){
		return aipOcupacoesDAO;
	}
	
	protected AipSinonimosOcupacaoDAO getAipSinonimosOcupacaoDAO(){
		return aipSinonimosOcupacaoDAO;
	}
	
	protected AipPacientesDAO getAipPacientesDAO(){
		return aipPacientesDAO;
	}

	protected IBancoDeSangueFacade getBancoDeSangueFacade() {
		return (IBancoDeSangueFacade) iBancoDeSangueFacade;
	}
	
}
