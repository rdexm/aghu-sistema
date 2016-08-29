package br.gov.mec.aghu.registrocolaborador.business;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dao.ObjetosOracleDAO;
import br.gov.mec.aghu.dominio.DominioSituacaoQualificacao;
import br.gov.mec.aghu.dominio.DominioTipoQualificacao;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapQualificacao;
import br.gov.mec.aghu.model.RapQualificacaoJn;
import br.gov.mec.aghu.model.RapQualificacoesId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.dao.RapDependentesDAO;
import br.gov.mec.aghu.registrocolaborador.dao.RapPessoasFisicasDAO;
import br.gov.mec.aghu.registrocolaborador.dao.RapQualificacaoDAO;
import br.gov.mec.aghu.registrocolaborador.dao.RapQualificacaoJnDAO;
import br.gov.mec.aghu.registrocolaborador.dao.RapServidoresDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

/**
 * 
 * @author cvagheti
 * 
 */
@SuppressWarnings("PMD.AghuTooManyMethods")
@Stateless
public class QualificacaoRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(QualificacaoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private RapDependentesDAO rapDependentesDAO;
	
	@Inject
	private RapQualificacaoDAO rapQualificacaoDAO;
	
	@Inject
	private RapQualificacaoJnDAO rapQualificacaoJnDAO;
	
	@Inject
	private RapServidoresDAO rapServidoresDAO;
	
	@Inject
	private RapPessoasFisicasDAO rapPessoasFisicasDAO;
	
	@Inject
	private ObjetosOracleDAO objetosOracleDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6018559482035515479L;

	public enum QualificacaoRNExceptionCode implements BusinessExceptionCode {
		/**
		 * RAP-00285 - A qualificação sendo informada possui data futura de
		 * término. Ainda não pode ser considerada concluída.
		 */
		MESSAGE_CONCLUIDA_COM_DATA_FUTURA_TERMINO,
		/**
		 * RAP-00193 - Somente permite Nro de Registro de Conselho para
		 * qualificação já concluída.
		 */
		MESSAGE_REGISTRO_CONSELHO_SOMENTE_CONCLUIDA,
		/**
		 * RAP-00198 - Inválido informar o semestre para qualificação já
		 * concluída.
		 */
		MENSAGEM_INVALIDO_SEMESTRE_PARA_CONCLUIDA,
		/** RAP-00116 - Data de fim não pode ser anterior à data de início. */
		MENSAGEM_DATA_FIM_ANTERIOR_INICIO,
		/** RAP-00231 - Publicações (PUB) só permite tipo de atuação compatível. */
		MENSAGEM_PUBLICACOES_EXIGE_TIPO_ATUACAO_COMPATIVEL,
		/**
		 * RAP-00233 - Cursos com e sem conselho regulamentador (CCC, CSC)
		 * exigem tipo de atuação compatível.
		 */
		MENSAGEM_CURSO_COM_E_SEM_CONSELHO_EXIGE_TIPO_ATUACAO_COMPATIVEL,
		/**
		 * RAP-00195 - Este tipo de qualificação não permite número de registro
		 * de conselho.
		 */
		MENSAGEM_TIPO_QUALIFICACAO_NAO_PERMITE_REGISTRO_CONSELHO,
		/** RAP-00531 - Data final informada, após data atual. */
		MENSAGEM_DATA_FINAL_APOS_DATA_ATUAL, //
		/** Não pode ser excluido pelo aghu pois há contrato ativo na starh. */
		MENSAGEM_EXISTE_CONTRATO_ATIVO_STARH;
	}

	/**
	 * Exceção lançada quando servidor tem contrato ativo no STARH.
	 * 
	 * @author cvagheti
	 * 
	 */
	private class ContratoAtivoStarhException extends Exception {

	private static final long serialVersionUID = 1058629824841935183L;}

	public void insert(RapQualificacao qualificacao)
			throws ApplicationBusinessException {
		RapQualificacaoDAO qualificacaoDAO = getQualificacaoDAO();
		
		qualificacaoDAO.atribuiSequencia(qualificacao);
		this.beforeRowInsert(qualificacao);		
		qualificacaoDAO.persistir(qualificacao);
		qualificacaoDAO.flush();
	}

	public void update(RapQualificacao qualificacao)
			throws ApplicationBusinessException {
		this.beforeRowUpdate(qualificacao);
		RapQualificacaoDAO qualificacaoDAO = getQualificacaoDAO();
		qualificacaoDAO.merge(qualificacao);
		qualificacaoDAO.flush();
		this.afterRowUpdate(qualificacao);
	}

	public void delete(RapQualificacao qualificacao)
			throws ApplicationBusinessException {
		this.beforeRowDelete(qualificacao);
		RapQualificacaoDAO qualificacaoDAO = getQualificacaoDAO();
		qualificacaoDAO.remover(qualificacao);
		qualificacaoDAO.flush();
		this.afterRowDelete(qualificacao);
	}

	/**
	 * Retorna a qualificação pelo id fornecido.
	 * 
	 * @param id
	 * @return null se não encontrado
	 */
	public RapQualificacao obterQualificacao(RapQualificacoesId id) {
		if (id == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}

		id.setPessoaFisica(obterPessoaFisica(id.getPessoaFisica().getCodigo()));

		return getQualificacaoDAO().obterPorChavePrimaria(id);
	}
	
	public RapQualificacao obterQualificacao(RapQualificacoesId id, Enum[] fetchArgsInnerJoin, Enum[] fetchArgsLeftJoin) {
		if (id == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}

		id.setPessoaFisica(obterPessoaFisica(id.getPessoaFisica().getCodigo()));

		return getQualificacaoDAO().obterPorChavePrimaria(id, fetchArgsInnerJoin, fetchArgsLeftJoin);
	}

	/**
	 * Retorna a pessoa fisica pelo id fornecido.
	 * 
	 */
	public RapPessoasFisicas obterPessoaFisica(Integer id) {
		if (id == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}

		RapPessoasFisicas pessoaFisica = getPessoasFisicasDAO().obterPorChavePrimaria(id);

		return pessoaFisica;
	}

	/**
	 * ORADB: Trigger RAPT_QLF_BRI
	 */
	private void beforeRowInsert(RapQualificacao qualificacao)
			throws ApplicationBusinessException {
		try {
			integracaoStarh(qualificacao);
		} catch (ContratoAtivoStarhException e) {
			throw new ApplicationBusinessException(
					QualificacaoRNExceptionCode.MENSAGEM_EXISTE_CONTRATO_ATIVO_STARH,
					"incluído");
		}
		validar(qualificacao);
		getQualificacaoDAO().verificaRegistroConselho(qualificacao);
		atualizarDataServidor(qualificacao);
	}

	/**
	 * ORADB: Trigger RAPT_QLF_BRU
	 */
	private void beforeRowUpdate(RapQualificacao qualificacao)
			throws ApplicationBusinessException {
		try {
			integracaoStarh(qualificacao);
		} catch (ContratoAtivoStarhException e) {
			throw new ApplicationBusinessException(
					QualificacaoRNExceptionCode.MENSAGEM_EXISTE_CONTRATO_ATIVO_STARH,
					"alterado");
		}
		validar(qualificacao);
		getQualificacaoDAO().verificaRegistroConselho(qualificacao);
		atualizarDataServidor(qualificacao);
	}

	/**
	 * @ORADB: Trigger RAPT_QLF_ARD
	 */
	private void afterRowDelete(RapQualificacao rapQuali) {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		RapQualificacaoJn jn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.DEL, RapQualificacaoJn.class, servidorLogado != null ? servidorLogado.getUsuario() : null);
		rapQuali.asJournal(jn);
		RapQualificacaoJnDAO qualificacaoJnDAO = getRapQualificacaoJnDAO();
		qualificacaoJnDAO.persistir(jn);
		qualificacaoJnDAO.flush();
	}

	/**
	 * @ORADB: Trigger RAPT_QLF_ARU
	 */
	private void afterRowUpdate(RapQualificacao qualificacao) {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		// TODO: se houve alteracao
		// salva os dados :old
		getQualificacaoDAO().evict(qualificacao);

		RapQualificacao old = obterQualificacao(qualificacao.getId());

		RapQualificacaoJn jn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, RapQualificacaoJn.class, servidorLogado.getUsuario());
		old.asJournal(jn);

		RapQualificacaoJnDAO qualificacaoJnDAO = getRapQualificacaoJnDAO();
		qualificacaoJnDAO.persistir(jn);
		qualificacaoJnDAO.flush();
	}

	/**
	 * ORADB: Trigger RAPT_QLF_BRD
	 * 
	 * @throws ApplicationBusinessException
	 */
	private void beforeRowDelete(RapQualificacao qualificacao)
			throws ApplicationBusinessException {

		try {
			integracaoStarh(qualificacao);
		} catch (ContratoAtivoStarhException e) {
			throw new ApplicationBusinessException(
					QualificacaoRNExceptionCode.MENSAGEM_EXISTE_CONTRATO_ATIVO_STARH,
					"excluído");

		}

	}

	/**
	 * Método para integração com sistema Starh.<br>
	 * Executa processos especificos para o ambiente do HCPA onde o sistema AGH
	 * tem integração com o sistema STARH.
	 * 
	 * @param qualificacao
	 * @throws ApplicationBusinessException
	 *             se servidor ativo no STARH.
	 * @throws ContratoAtivoStarhException
	 */
	private void integracaoStarh(RapQualificacao qualificacao)
			throws ContratoAtivoStarhException, ApplicationBusinessException {

		if (!getRapDependentesDAO().isOracle()) {
			return;
		}		

		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (getObjetosOracleDAO().servidorAtivoStarh(
				qualificacao.getId().getPessoaFisica(), null,
				servidorLogado)) {
			throw new ContratoAtivoStarhException();
		}
	}

	/**
	 * Realiza validações.
	 * 
	 * @param qualificacao
	 * @throws ApplicationBusinessException
	 */
	private void validar(RapQualificacao qualificacao)
			throws ApplicationBusinessException {
		Date when = (Date) ObjectUtils.defaultIfNull(qualificacao.getDtFim(),
				Calendar.getInstance().getTime());

		// data inicio não pode ser maior que o fim ou hoje
		if (qualificacao.getDtInicio() != null
				&& qualificacao.getDtInicio().after(when)) {
			throw new ApplicationBusinessException(
					QualificacaoRNExceptionCode.MENSAGEM_DATA_FIM_ANTERIOR_INICIO);
		}

		verificaQualificacao(qualificacao);

		//
		if (StringUtils.isNotBlank(qualificacao.getSemestre())
				&& qualificacao.getSituacao() == DominioSituacaoQualificacao.C) {
			throw new ApplicationBusinessException(
					QualificacaoRNExceptionCode.MENSAGEM_INVALIDO_SEMESTRE_PARA_CONCLUIDA);
		}

		if (qualificacao.getNroRegConselho() != null) {
			if (qualificacao.getSituacao() != DominioSituacaoQualificacao.C) {
				throw new ApplicationBusinessException(
						QualificacaoRNExceptionCode.MESSAGE_REGISTRO_CONSELHO_SOMENTE_CONCLUIDA);
			} else if (qualificacao.getDtFim().after(
					Calendar.getInstance().getTime())) {
				throw new ApplicationBusinessException(
						QualificacaoRNExceptionCode.MESSAGE_CONCLUIDA_COM_DATA_FUTURA_TERMINO);
			}
		}

		verificaTermino(qualificacao);

	}

	/**
	 * Atualiza as propriedades criadoPor/alteradoPor e data de atualizacao.
	 * 
	 * @param qualificacao
	 *  
	 */
	private void atualizarDataServidor(RapQualificacao qualificacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		qualificacao.setServidor(servidorLogado);
		qualificacao.setDtAtualizacao(Calendar.getInstance().getTime());
	}

	/**
	 * Valida a data de fim para qualificacoes do tipo diferente de evento.
	 * 
	 * ORADB: Procedure RAPK_QLF_RN.RN_QLFP_VER_TERMINO
	 * 
	 * @param qualificacao
	 * @throws ApplicationBusinessException
	 *             se diferente de evento e data de fim maior que data atual
	 */
	private void verificaTermino(RapQualificacao qualificacao)
			throws ApplicationBusinessException {
		// java.sql.Date trunca horario
		Date when = new java.sql.Date(Calendar.getInstance().getTimeInMillis());
		if (qualificacao.getTipoQualificacao().getTipoQualificacao() != DominioTipoQualificacao.EVE
				&& qualificacao.getDtFim() != null
				&& qualificacao.getDtFim().after(when)) {
			throw new ApplicationBusinessException(
					QualificacaoRNExceptionCode.MENSAGEM_DATA_FINAL_APOS_DATA_ATUAL);
		}
	}

	/**
	 * Conforme o tipo, consiste tipo de atuação e nro registro no conselho. <br>
	 * 
	 * ORADB: Procedure RAPK_QLF_RN.RN_QLFP_VER_QUALIF
	 * 
	 * @throws ApplicationBusinessException
	 */
	private void verificaQualificacao(RapQualificacao qualificacao)
			throws ApplicationBusinessException {
		// para curso não concluido o registro no conselho não deve ser
		// informado
		// if (qualificacao.getTipoQualificacao().getTipoQualificacao() ==
		// DominioTipoQualificacao.CCC
		// && qualificacao.getNroRegConselho() != null
		// && qualificacao.getSituacao() != DominioSituacaoQualificacao.C) {
		// throw new ApplicationBusinessException(
		// QualificacaoRNExceptionCode.MENSAGEM_NAO_INFORMAR_REGISTRO_SE_NAO_CONCLUIDO);
		// }

		// qualificacao não permite registro no conselho
		if (qualificacao.getNroRegConselho() != null
				&& qualificacao.getTipoQualificacao().getTipoQualificacao() != DominioTipoQualificacao.CCC) {
			throw new ApplicationBusinessException(
					QualificacaoRNExceptionCode.MENSAGEM_TIPO_QUALIFICACAO_NAO_PERMITE_REGISTRO_CONSELHO);
		}

		// para cursos com e sem conselho o tipo de atuacao deve ser discente ou
		// docente
		if (qualificacao.getTipoQualificacao().getTipoQualificacao() == DominioTipoQualificacao.CCC
				|| qualificacao.getTipoQualificacao().getTipoQualificacao() == DominioTipoQualificacao.CSC) {
			if (!qualificacao.getTipoAtuacao().getIndDiscente()
					&& !qualificacao.getTipoAtuacao().getIndDocente()) {
				throw new ApplicationBusinessException(
						QualificacaoRNExceptionCode.MENSAGEM_CURSO_COM_E_SEM_CONSELHO_EXIGE_TIPO_ATUACAO_COMPATIVEL);
			}
		}

		// para publicacoes o tipo de atuacao deve ser "autor"
		if (qualificacao.getTipoQualificacao().getTipoQualificacao() == DominioTipoQualificacao.PUB
				&& !qualificacao.getTipoAtuacao().getIndAutor()) {
			throw new ApplicationBusinessException(
					QualificacaoRNExceptionCode.MENSAGEM_PUBLICACOES_EXIGE_TIPO_ATUACAO_COMPATIVEL);
		}
	}
	
	/**
	 * Retorna as graduações com os parâmetros fornecidos.
	 * 
	 * @param object
	 * @param matricula
	 * @param vinculo
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<RapQualificacao> pesquisarGraduacao(Integer pesCodigo, Integer matricula, Short vinculo, Integer firstResult,
													Integer maxResult, String orderProperty, boolean asc) {
		RapServidores servidor = null;
		if(matricula != null && vinculo != null){
			servidor = getRapServidoresDAO().obterServidor(new RapServidoresId(matricula, vinculo));
		}
		return getQualificacaoDAO().pesquisarGraduacao(pesCodigo, servidor, firstResult, maxResult, orderProperty, asc);
	}
	
	/**
	 * Retorna a quantidade de registros de graduação encontrados com os
	 * parâmetros fornecidos.
	 * 
	 * @param pesCodigo
	 * @param serMatricula
	 * @param serVinCodigo
	 */
	public Long pesquisarGraduacaoCount(Integer pesCodigo, Integer serMatricula, Short serVinCodigo) {

		RapServidores servidor = null;
		if(serMatricula != null && serVinCodigo != null){
			servidor = getRapServidoresDAO().obterServidor(new RapServidoresId(serMatricula, serVinCodigo));
		}
		return getQualificacaoDAO().pesquisarGraduacaoCount(pesCodigo, servidor);
	}
	
	protected RapQualificacaoDAO getQualificacaoDAO() {
		return rapQualificacaoDAO;
	}
	
	protected RapQualificacaoJnDAO getRapQualificacaoJnDAO(){
		return rapQualificacaoJnDAO;
	}

	protected ObjetosOracleDAO getObjetosOracleDAO() {
		return objetosOracleDAO;
	}
	
	protected RapPessoasFisicasDAO getPessoasFisicasDAO() {
		return rapPessoasFisicasDAO;
	}
	
	protected RapServidoresDAO getRapServidoresDAO() {
		return rapServidoresDAO;
	}

	protected RapDependentesDAO getRapDependentesDAO() {
		return rapDependentesDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
