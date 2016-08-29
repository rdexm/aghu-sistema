package br.gov.mec.aghu.administracao.business;

import java.io.Serializable;
import java.util.List;

import br.gov.mec.aghu.configuracao.vo.NotificacaoDestinoVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioCaracteristicaMicrocomputador;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.AacUnidFuncionalSalas;
import br.gov.mec.aghu.model.AghCaractMicrocomputador;
import br.gov.mec.aghu.model.AghCaractMicrocomputadorId;
import br.gov.mec.aghu.model.AghMicrocomputador;
import br.gov.mec.aghu.model.AghNotificacoes;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;


public interface IAdministracaoFacade extends Serializable {

	List<AghMicrocomputador> pesquisarMicrocomputador(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc, String nome,
			Byte sala, Short ramal, DominioSimNao prioridade,
			DominioSimNao indAtivo, String ponto, String ip, String usuario,
			String impPadrao, String impEtiquetas, String impMatricial,
			String observacao, AghUnidadesFuncionais unidadeFuncional);

	Long pesquisarMicrocomputadorCount(String nome, Byte sala, Short ramal,
			DominioSimNao prioridade, DominioSimNao indAtivo, String ponto,
			String ip, String usuario, String impPadrao, String impEtiquetas,
			String impMatricial, String observacao,
			AghUnidadesFuncionais unidadeFuncional);

	List<AacUnidFuncionalSalas> listarSalasPorNumeroSala(Object param);

	String obterNomeMicrocomputador(String texto);

	String obterDescricaoUnidadeFuncionalAbreviada(
			AghMicrocomputador microcomputador) throws BaseException;

	String obterNomeUsuarioAbreviado(String nomeUsuario);

	void excluirMicrocomputador(String nomeMicrocomputador)
			throws ApplicationBusinessException;

	AghMicrocomputador obterMicrocomputadorPorNome(String nomeMicrocomputador);

	void persistirMicrocomputador(AghMicrocomputador microcomputador)
			throws BaseException;

	void atualizarMicrocomputador(AghMicrocomputador microcomputador)
			throws BaseException;

	void persistirCaracteristicaMicrocomputador(
			AghMicrocomputador microcomputador,
			DominioCaracteristicaMicrocomputador dominioCaracteristica)
			throws BaseException;

	void excluirCaracteristicaMicrocomputador(
			AghCaractMicrocomputadorId caracteristicaId)
			throws ApplicationBusinessException;

	
	public List<AghMicrocomputador> pesquisarAghMicrocomputadorPorSala(Byte sala);

	public List<AghMicrocomputador> pesquisarAghMicrocomputadorPorZonaESala(
			Short unfSeq, Byte sala);
	
	public AghMicrocomputador buscarMicrocomputador(String micro) throws ApplicationBusinessException;
	
	public Long pesquisarTodosMicroAtivosCount(Object estacao); 
	
	public List<AghMicrocomputador> pesquisarTodosMicroAtivos(Object estacao);

	public AghMicrocomputador obterAghMicroComputadorPorNomeOuIP(String computadorRede, 
			DominioCaracteristicaMicrocomputador caracteristica);	
	
	public AghMicrocomputador obterAghMicroComputadorPorNomeOuIPException(String nomeMicrocomputador)
			throws ApplicationBusinessException;
	
	public void desatacharAghMicroComputador(AghMicrocomputador computador);

	Long buscarQtdeMicrocomputadorPorCentroCusto(FccCentroCustos centroCusto);

	List<AghCaractMicrocomputador> pesquisaCaractMicrocomputadorPorNome(
			String nome, DominioCaracteristicaMicrocomputador caracteristica);
	
	public List<AghNotificacoes> pesquisarNotificacoes(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, String descricao, String nomeProcesso, RapServidores servidor, Long celular);
	
	public Long pesquisarNotificacoesCount(String descricao, String nomeProcesso, RapServidores servidor, Long celular);
	
	public void excluirNotificacao(AghNotificacoes notificacao) throws BaseException;

	List<NotificacaoDestinoVO> obtemDestinosDaNotificacao(Integer ntsSeq);

	AghNotificacoes pesquisarNotificacaoPorDescricao(String descricao);

	void validarDestino(RapServidores servidorDestino, Long celular) throws ApplicationBusinessException;

	void gravarNotificacao(AghNotificacoes notificacao,
			List<NotificacaoDestinoVO> notificacaoDestinos,
			List<Integer> notificacaoDestinosExcluidos,
			String nomeMicrocomputador) throws BaseException;
	
	void alterarNotificacao(AghNotificacoes notificacao, String nomeMicrocomputador) throws BaseException;
	
	Short obtemDddCelular(Long celular);
	
	String obtemNumeroCelular(Long celular);

}