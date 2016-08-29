package br.gov.mec.aghu.administracao.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.administracao.dao.AghCaractMicrocomputadorDAO;
import br.gov.mec.aghu.administracao.dao.AghMicrocomputadorDAO;
import br.gov.mec.aghu.administracao.dao.AghNotificacaoDestinosDAO;
import br.gov.mec.aghu.administracao.dao.AghNotificacoesDAO;
import br.gov.mec.aghu.configuracao.vo.NotificacaoDestinoVO;
import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
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

/**
 * 
 * @author rodrigo
 *
 */
@Modulo(ModuloEnum.CONFIGURACAO)
@Stateless
public class AdministracaoFacade extends BaseFacade implements IAdministracaoFacade {

	@EJB
	private MicrocomputadorON microcomputadorON;
	
	@EJB
	private MicrocomputadorRN microcomputadorRN;
	
	@EJB
	private NotificacoesON notificacoesON;
	
	@Inject
	private AghMicrocomputadorDAO aghMicrocomputadorDAO;
	
	@Inject
	private AghCaractMicrocomputadorDAO aghCaractMicrocomputadorDAO;
	
	@Inject
	private AghNotificacoesDAO aghNotificacoesDAO;
	
	@Inject
	private AghNotificacaoDestinosDAO aghNotificacaoDestinosDAO;
	
	private static final long serialVersionUID = 2974033666502049478L;

	@Override
	public List<AghMicrocomputador> pesquisarMicrocomputador(Integer firstResult, Integer maxResult, 
			String orderProperty, boolean asc, String nome, Byte sala, Short ramal, 
			DominioSimNao prioridade, DominioSimNao indAtivo, String ponto, String ip, String usuario,
			String impPadrao, String impEtiquetas, String impMatricial, String observacao, AghUnidadesFuncionais unidadeFuncional){
		
		return getAghMicrocomputadorDAO().pesquisarMicrocomputador(firstResult, maxResult, orderProperty,
				asc, nome, sala, ramal, prioridade, indAtivo, ponto, ip, usuario, impPadrao, impEtiquetas,
				impMatricial, observacao, unidadeFuncional);
	}
	
	@Override
	public Long pesquisarMicrocomputadorCount(String nome, Byte sala, Short ramal, DominioSimNao prioridade, 
			DominioSimNao indAtivo, String ponto, String ip, String usuario, String impPadrao, String impEtiquetas, 
			String impMatricial, String observacao, AghUnidadesFuncionais unidadeFuncional){
		
		return getAghMicrocomputadorDAO().pesquisarMicromputadorCount(nome, sala, ramal, prioridade, indAtivo,
				ponto, ip, usuario, impPadrao, impEtiquetas, impMatricial, observacao, unidadeFuncional);
	}
	
	@Override
	public List<AacUnidFuncionalSalas> listarSalasPorNumeroSala(Object param) {
		return getMicrocomputadorON().listarSalasPorNumeroSala(param);
	}
	
	@Override
	public String obterNomeMicrocomputador(String texto) {
		return getMicrocomputadorON().obterAbreviado(texto);
	}
	
	@Override
	public String obterDescricaoUnidadeFuncionalAbreviada(AghMicrocomputador microcomputador) throws BaseException {
		return getMicrocomputadorON().obterDescricaoUnidadeFuncionalAbreviada(microcomputador);
	}
	
	@Override
	public String obterNomeUsuarioAbreviado(String nomeUsuario) {
		return getMicrocomputadorON().obterAbreviado(nomeUsuario);
	}
	
	@Override
	public void excluirMicrocomputador(String nomeMicrocomputador) throws ApplicationBusinessException {
		getMicrocomputadorRN().excluirMicrocomputador(nomeMicrocomputador);
	}
	
	@Override
	public AghMicrocomputador obterMicrocomputadorPorNome(String nomeMicrocomputador) {
		return getAghMicrocomputadorDAO().obterComputadorPorNomeComputador(nomeMicrocomputador);
	}
	
	@Override
	public void persistirMicrocomputador(AghMicrocomputador microcomputador) throws ApplicationBusinessException {
		getMicrocomputadorRN().persistirMicrocomputador(microcomputador);
	}
	
	@Override
	public void atualizarMicrocomputador(AghMicrocomputador microcomputador) throws BaseException {
		getMicrocomputadorRN().atualizarMicrocomputador(microcomputador);
	}

	@Override
	public void persistirCaracteristicaMicrocomputador(AghMicrocomputador microcomputador, DominioCaracteristicaMicrocomputador dominioCaracteristica) throws BaseException {
		getMicrocomputadorRN().persistirCaracteristicaMicrocomputador(microcomputador, dominioCaracteristica);
	}
	
	@Override
	public void excluirCaracteristicaMicrocomputador(AghCaractMicrocomputadorId caracteristicaId) throws ApplicationBusinessException {
		getMicrocomputadorRN().excluirCaracteristica(caracteristicaId);
		
	}
	
	private AghMicrocomputadorDAO getAghMicrocomputadorDAO(){
		return aghMicrocomputadorDAO;
	}
	
	private MicrocomputadorON getMicrocomputadorON() {
		return microcomputadorON;
	}
	
	private MicrocomputadorRN getMicrocomputadorRN() {
		return microcomputadorRN;
	}
	
	private AghCaractMicrocomputadorDAO getAghCaractMicrocomputadorDAO() {
		return aghCaractMicrocomputadorDAO;
	}
	
	@Override
	public List<AghMicrocomputador> pesquisarAghMicrocomputadorPorSala(Byte sala) {
		return getAghMicrocomputadorDAO().pesquisarAghMicrocomputadorPorSala(
				sala);
	}

	@Override
	public List<AghMicrocomputador> pesquisarAghMicrocomputadorPorZonaESala(
			Short unfSeq, Byte sala) {
		return getAghMicrocomputadorDAO()
				.pesquisarAghMicrocomputadorPorZonaESala(unfSeq, sala);
	}
	
	@Override
	public AghMicrocomputador buscarMicrocomputador(String micro) throws ApplicationBusinessException {
		return getAghMicrocomputadorDAO().obterAghMicroComputadorPorNomeOuIP(
				micro, null);
	}
	
	@Override
	public Long pesquisarTodosMicroAtivosCount(Object estacao) {
		return getAghMicrocomputadorDAO().pesquisarTodosMicroAtivosCount(
				estacao);
	}
	
	@Override
	public List<AghMicrocomputador> pesquisarTodosMicroAtivos(Object estacao) {
		return getAghMicrocomputadorDAO().pesquisarTodosMicroAtivos(estacao);
	}
	
	@Override
	public AghMicrocomputador obterAghMicroComputadorPorNomeOuIPException(String computadorRede) throws ApplicationBusinessException {
		return getAghMicrocomputadorDAO().obterAghMicroComputadorPorNomeOuIPException(computadorRede);
	}

	
	@Override
	public AghMicrocomputador obterAghMicroComputadorPorNomeOuIP(String computadorRede, DominioCaracteristicaMicrocomputador caracteristica) {
		return getAghMicrocomputadorDAO().obterAghMicroComputadorPorNomeOuIP(computadorRede, caracteristica);	
	}	
	
	@Override
	public void desatacharAghMicroComputador(AghMicrocomputador computador) {
		getAghMicrocomputadorDAO().desatachar(computador);
	}
	
	@Override
	public Long buscarQtdeMicrocomputadorPorCentroCusto(FccCentroCustos centroCusto) {
		return getAghMicrocomputadorDAO().buscarQtdeMicrocomputadorPorCentroCusto(centroCusto);
	}

	@Override
	public List<AghCaractMicrocomputador> pesquisaCaractMicrocomputadorPorNome(
			String nome, DominioCaracteristicaMicrocomputador caracteristica) {
		return getAghCaractMicrocomputadorDAO()
				.pesquisaCaractMicrocomputadorPorNome(nome, caracteristica);
	}

	@Override
	public List<AghNotificacoes> pesquisarNotificacoes(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc,
			String descricao, String nomeProcesso, RapServidores servidor,
			Long celular) {
		return aghNotificacoesDAO.pesquisarNotificacoes(firstResult, maxResults, orderProperty, asc, descricao, nomeProcesso, servidor, celular);
	}

	@Override
	public Long pesquisarNotificacoesCount(String descricao,
			String nomeProcesso, RapServidores servidor, Long celular) {
		return aghNotificacoesDAO.pesquisarNotificacoesCount(descricao, nomeProcesso, servidor, celular);
	}

	@Override
	public void excluirNotificacao(AghNotificacoes notificacao) throws BaseException {
		notificacoesON.excluirNotificacao(notificacao);
	}
	
	@Override
	public List<NotificacaoDestinoVO> obtemDestinosDaNotificacao(Integer ntsSeq) {
		return aghNotificacaoDestinosDAO.obtemDestinosDaNotificacao(ntsSeq);
	}
	
	@Override
	public AghNotificacoes pesquisarNotificacaoPorDescricao(String descricao) {
		return aghNotificacoesDAO.pesquisarNotificacaoPorDescricao(descricao);
	}
	
	@Override
	public void validarDestino(RapServidores servidorDestino, Long celular) throws ApplicationBusinessException {
		this.notificacoesON.validarDestino(servidorDestino, celular);
	}

	@Override
	public void gravarNotificacao(AghNotificacoes notificacao,
			List<NotificacaoDestinoVO> notificacaoDestinos,
			List<Integer> notificacaoDestinosExcluidos,
			String nomeMicrocomputador) throws BaseException {
		this.notificacoesON.gravarNotificacao(notificacao, notificacaoDestinos, notificacaoDestinosExcluidos, nomeMicrocomputador);
	}

	@Override
	public Short obtemDddCelular(Long celular) {
		return notificacoesON.obtemDddCelular(celular);
	}

	@Override
	public String obtemNumeroCelular(Long celular) {
		return notificacoesON.obtemNumeroCelular(celular);
	}

	@Override
	public void alterarNotificacao(AghNotificacoes notificacao, String nomeMicrocomputador) throws BaseException {
		notificacoesON.alterarNotificacao(notificacao, nomeMicrocomputador);
	}
	
}