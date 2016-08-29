package br.gov.mec.aghu.internacao.business;

import java.sql.BatchUpdateException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.internacao.dao.AinAcompanhantesInternacaoDAO;
import br.gov.mec.aghu.internacao.dao.AinCrachaAcompanhantesDAO;
import br.gov.mec.aghu.model.AinAcompanhantesInternacao;
import br.gov.mec.aghu.model.AinAcompanhantesInternacaoId;
import br.gov.mec.aghu.model.AinCrachaAcompanhantes;
import br.gov.mec.aghu.model.AinCrachaAcompanhantesId;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * ON para a estoria de usuario 'Atualizar Acompanhantes da Internacao'.
 */
@Stateless
public class AtualizaAcompanhantesInternacaoON extends BaseBusiness {

	@EJB
	private AtualizaAcompanhantesInternacaoRN atualizaAcompanhantesInternacaoRN;
	
	private static final Log LOG = LogFactory.getLog(AtualizaAcompanhantesInternacaoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AinCrachaAcompanhantesDAO ainCrachaAcompanhantesDAO;
	
	
	@Inject
	private AinAcompanhantesInternacaoDAO ainAcompanhantesInternacaoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -4926932188537343118L;

	private enum AtualizaAcompanhantesInternacaoONExceptionCode implements
			BusinessExceptionCode {
		AIN_00149_DT_ACOMP_FINAL_MAIOR_IGUAL_DT_INICIAL, ERRO_ATUALIZAR_ACOMPANHANTE_INTERNACAO, NOME_ACOMPANHANTE_OBRIGATORIO, DT_INICIO_ACOMPANHANTE_OBRIGATORIO, ERRO_REMOVER_ACOMPANHANTE_INTERNACAO, RG_ACOMPANHANTE_OBRIGATORIO, NRO_CRACHA_NAO_ENCONTRADO, AIN_00911, AIN_00912, AIN_00913, AIN_00914, AIN_00871;
	}

//	private static final String ENTITY_MANAGER = "entityManager";

	protected BatchUpdateException extractBatchUpdateException(Throwable ex) {
		BatchUpdateException bue = null;
		if (ex instanceof BatchUpdateException) {
			bue = (BatchUpdateException) ex;
		} else if (ex.getCause() != null) {
			bue = extractBatchUpdateException(ex.getCause());
		}
		return bue;
	}

	protected String getErrorMessage(Throwable ex) {
		String msg = null;
		BatchUpdateException bue = extractBatchUpdateException(ex);
		if (bue != null) {
			SQLException sqle = bue.getNextException();
			msg = sqle.getMessage();
		}
		return msg;
	}

	/**
	 * Valida datas.
	 * 
	 * @param prontuario
	 * @param codigo
	 * @throws ApplicationBusinessException
	 */
	public void validaCampos(Date dtInicio, Date dtFim, String nomeAcomp)
			throws ApplicationBusinessException {

		if (dtInicio == null) {
			throw new ApplicationBusinessException(
					AtualizaAcompanhantesInternacaoONExceptionCode.DT_INICIO_ACOMPANHANTE_OBRIGATORIO);
		}

		if (dtFim != null) {
			if (!CoreUtil.isMenorOuIgualDatas(dtInicio, dtFim)) {
				throw new ApplicationBusinessException(
						AtualizaAcompanhantesInternacaoONExceptionCode.AIN_00149_DT_ACOMP_FINAL_MAIOR_IGUAL_DT_INICIAL);
			}
		}

		if (StringUtils.isBlank(nomeAcomp)) {
			throw new ApplicationBusinessException(
					AtualizaAcompanhantesInternacaoONExceptionCode.NOME_ACOMPANHANTE_OBRIGATORIO);
		}
	}

	/**
	 * MÃ©todo usado para persistir os acompanhantes.
	 * 
	 * @param acompanhantesInternacaos
	 * @throws ApplicationBusinessException
	 */
	public void persistirAcompanhantes(
			List<AinAcompanhantesInternacao> acompanhantesInternacaos,
			AinInternacao internacao) throws ApplicationBusinessException {
		AinAcompanhantesInternacaoDAO ainAcompanhantesInternacaoDAO = this
				.getAinAcompanhantesInternacaoDAO();

		List<AinAcompanhantesInternacao> acompanhantesGravados = ainAcompanhantesInternacaoDAO
				.pesquisarAinAcompanhantesInternacao(internacao);

		HashMap<AinAcompanhantesInternacaoId, AinAcompanhantesInternacao> acompanhantesMap = new HashMap<AinAcompanhantesInternacaoId, AinAcompanhantesInternacao>(
				acompanhantesGravados.size());

		for (AinAcompanhantesInternacao acompanhantesInternacao : acompanhantesGravados) {
			acompanhantesMap.put(acompanhantesInternacao.getId(),
					acompanhantesInternacao);
		}

		Byte seq = null;

		for (AinAcompanhantesInternacao acompanhanteInternacao : acompanhantesInternacaos) {

			boolean isNew = false;

			if (acompanhanteInternacao.getId().getSeq() == null) {

				if (seq == null) {
					seq = ainAcompanhantesInternacaoDAO
							.obterValorSeqId(acompanhanteInternacao);
				} else {
					seq++;
				}
				acompanhanteInternacao.getId().setSeq(seq);

				isNew = true;
			}

			// Remove do Map, assim quem sobrar no map, sÃ£o aqueles que
			// foram excluÃ­dos.
			acompanhantesMap.remove(acompanhanteInternacao.getId());

			if (isNew) {

				// ORADB trigger AINT_ACI_BRI
				this.getAtualizaAcompanhantesInternacaoRN()
						.executarAintAciBriBru(acompanhanteInternacao,
								acompanhantesInternacaos);

				ainAcompanhantesInternacaoDAO.persistir(acompanhanteInternacao);
			} else {

				// NÃ£o hÃ¡ essa verificaÃ§Ã£o na trigger de BRU, mas apÃ³s conversa
				// com
				// Milena em 01/07/2010 constatou-se que deveria ter.
				// ORADB trigger AINT_ACI_BRU
				this.getAtualizaAcompanhantesInternacaoRN()
						.executarAintAciBriBru(acompanhanteInternacao,
								acompanhantesInternacaos);
			
				acompanhanteInternacao = ainAcompanhantesInternacaoDAO.merge(acompanhanteInternacao);
				ainAcompanhantesInternacaoDAO.atualizar(acompanhanteInternacao);
			}
		}

		AinCrachaAcompanhantesDAO ainCrachaAcompanhantesDAO = this
				.getAinCrachaAcompanhantesDAO();

		// Remove apenas os <code>AinAcompanhantesInternacao</code> que nÃ£o
		// estÃ£o
		// mais associados ao pai.
		for (AinAcompanhantesInternacao acompanhantesInternacao : acompanhantesMap
				.values()) {

			ainAcompanhantesInternacaoDAO.refresh(acompanhantesInternacao);

			// Removendo os filhos.
			for (AinCrachaAcompanhantes cracha : acompanhantesInternacao
					.getCrachaAcompanhantes()) {
				ainCrachaAcompanhantesDAO.remover(cracha);
			}

			ainAcompanhantesInternacaoDAO.remover(acompanhantesInternacao);
		}

		try {
			ainAcompanhantesInternacaoDAO.flush();
		} catch (Exception e) {
			logError("Erro ao atualizar Acompanhantes.", e);
			throw new ApplicationBusinessException(
					AtualizaAcompanhantesInternacaoONExceptionCode.ERRO_ATUALIZAR_ACOMPANHANTE_INTERNACAO);
		}
	}

	/**
	 * MÃ©todo usado para persistir os cracha dos acompanhantes.
	 * 
	 * @param acompanhantesInternacaos
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public void persistirCrachaAcompanhantes(
			List<AinCrachaAcompanhantes> crachasAcompanhantes,
			AinAcompanhantesInternacao acompanhanteInternacao,
			List<AinAcompanhantesInternacao> acompanhantesInternacaos,
			AinInternacao internacao)
			throws ApplicationBusinessException {
		AinAcompanhantesInternacaoDAO ainAcompanhantesInternacaoDAO = this
				.getAinAcompanhantesInternacaoDAO();
		AinCrachaAcompanhantesDAO ainCrachaAcompanhantesDAO = this
				.getAinCrachaAcompanhantesDAO();

		// Salvar acompanhante.0
		Byte seq = null;

		boolean isNew = false;

		if (acompanhanteInternacao.getId().getSeq() == null) {
			seq = ainAcompanhantesInternacaoDAO
					.obterValorSeqId(acompanhanteInternacao);
			acompanhanteInternacao.getId().setSeq(seq);
			isNew = true;
		}

		if (isNew) {
			// ORADB trigger AINT_ACI_BRI
			this.getAtualizaAcompanhantesInternacaoRN().executarAintAciBriBru(
					acompanhanteInternacao, acompanhantesInternacaos);
			ainAcompanhantesInternacaoDAO.persistir(acompanhanteInternacao);
		} else {

			// NÃ£o hÃ¡ essa verificaÃ§Ã£o na trigger de BRU, mas apÃ³s conversa
			// com
			// Milena em 01/07/2010 constatou-se que deveria ter.
			// ORADB trigger AINT_ACI_BRU
			this.getAtualizaAcompanhantesInternacaoRN().executarAintAciBriBru(
					acompanhanteInternacao, acompanhantesInternacaos);

			ainAcompanhantesInternacaoDAO.atualizar(acompanhanteInternacao);
		}

		try {
			ainAcompanhantesInternacaoDAO.flush();
		} catch (Exception e) {
			logError("Erro ao atualizar Acompanhantes.", e);
			throw new ApplicationBusinessException(
					AtualizaAcompanhantesInternacaoONExceptionCode.ERRO_ATUALIZAR_ACOMPANHANTE_INTERNACAO);
		}

		// Refresh Acompanhante
		// acompanhanteInternacao = entityManager.find(
		// AinAcompanhantesInternacao.class, acompanhanteInternacao
		// .getId());

		// Salvando os crachÃ¡s.
		List<AinCrachaAcompanhantes> crachasGravados = this
				.getAinCrachaAcompanhantesDAO()
				.pesquisarAinCrachaAcompanhantes(acompanhanteInternacao);

		HashMap<AinCrachaAcompanhantesId, AinCrachaAcompanhantes> crachasAcompanhantesMap = new HashMap<AinCrachaAcompanhantesId, AinCrachaAcompanhantes>(
				crachasGravados.size());

		for (AinCrachaAcompanhantes cracha : crachasGravados) {
			crachasAcompanhantesMap.put(cracha.getId(), cracha);
		}

		persistirListaCrachas(crachasAcompanhantes, acompanhanteInternacao, crachasAcompanhantesMap);
		
		

		// Remove apenas os <code>AinCrachaAcompanhantes</code> que nÃ£o
		// estÃ£o mais associados ao pai.
		for (AinCrachaAcompanhantes crachaAcompanhante : crachasAcompanhantesMap
				.values()) {
			ainCrachaAcompanhantesDAO.refresh(crachaAcompanhante);
			ainCrachaAcompanhantesDAO.remover(crachaAcompanhante);
		}

		// Refresh na entidade.
		if (acompanhanteInternacao.getId() != null) {
			ainAcompanhantesInternacaoDAO.atualizar(acompanhanteInternacao);
		}

		try {
			ainAcompanhantesInternacaoDAO.flush();
		} catch (Exception e) {		
			exibirMensagensRaise(e);
		}
	}

	private void persistirListaCrachas(List<AinCrachaAcompanhantes> crachasAcompanhantes, 
			AinAcompanhantesInternacao acompanhanteInternacao, 
			Map<AinCrachaAcompanhantesId, AinCrachaAcompanhantes> crachasAcompanhantesMap) throws ApplicationBusinessException {

		AinCrachaAcompanhantesDAO ainCrachaAcompanhantesDAO = this.getAinCrachaAcompanhantesDAO();
		AinAcompanhantesInternacaoDAO ainAcompanhantesInternacaoDAO = this.getAinAcompanhantesInternacaoDAO();
		
		Byte seq = null;

		for (AinCrachaAcompanhantes crachaAcompanhante : crachasAcompanhantes) {

			boolean isNew = false;

			if (crachaAcompanhante.getId().getSeqp() == null) {

				if (seq == null) {
					seq = this.getAinCrachaAcompanhantesDAO().obterValorSeqId(
							crachaAcompanhante,
							acompanhanteInternacao.getId().getSeq(),
							acompanhanteInternacao.getId().getIntSeq());
				} else {
					seq++;
				}
				crachaAcompanhante.getId().setSeqp(seq);
				crachaAcompanhante.getId().setAciSeq(
						acompanhanteInternacao.getId().getSeq());
				crachaAcompanhante.getId().setAciIntSeq(
						acompanhanteInternacao.getId().getIntSeq());

				isNew = true;
			}

			// Remove do Map, assim quem sobrar no map, sÃ£o aqueles que
			// foram excluÃ­dos.
			crachasAcompanhantesMap.remove(crachaAcompanhante.getId());

			if (isNew) {
				// TODO: Adicionar rotinas BRI...
				ainCrachaAcompanhantesDAO.persistir(crachaAcompanhante);
			} else {
				// TODO: Adicionar rotinas BRU...
				crachaAcompanhante = ainCrachaAcompanhantesDAO.merge(crachaAcompanhante);
				ainCrachaAcompanhantesDAO.atualizar(crachaAcompanhante);
			}
		
		try {
			ainAcompanhantesInternacaoDAO.flush();
		} catch (Exception e) {
				crachaAcompanhante.setId(new AinCrachaAcompanhantesId());
			exibirMensagensRaise(e);
		}
	}

	}

	private void exibirMensagensRaise(Exception e)throws ApplicationBusinessException{
		if (getErrorMessage(e) != null) {
			/*
			 * 'AIN-00911' - 'Numero do Cracha informado nao pertence a  faixa do Refeitorio ou Acompanhante.' 
			 * 'AIN-00912' - 'Numero do Cracha informado estÃ¡ em aberto. Utilize outro cracha.'
			 * 'AIN-00913' - 'Numero do RG deve ser informado.' 
			 * 'AIN-00914' - 'Numero do RG jÃ¡ estÃ¡ em uso por outro Acompanhante.'
			 */
			final String msg = getErrorMessage(e).toString();
			logError(e.getMessage().toString(), e);
			if (msg.indexOf("AIN-00911") >= 0) {
				throw new ApplicationBusinessException(
						AtualizaAcompanhantesInternacaoONExceptionCode.AIN_00911);
			} else if (msg.indexOf("AIN-00912") >= 0) {
				throw new ApplicationBusinessException(
						AtualizaAcompanhantesInternacaoONExceptionCode.AIN_00912);
			} else if (msg.indexOf("AIN-00913") >= 0) {
				throw new ApplicationBusinessException(
						AtualizaAcompanhantesInternacaoONExceptionCode.AIN_00913);
			} else if (msg.indexOf("AIN-00914") >= 0) {
				throw new ApplicationBusinessException(
						AtualizaAcompanhantesInternacaoONExceptionCode.AIN_00914);
			} else if (msg.indexOf("AIN-00871") >= 0) {
				throw new ApplicationBusinessException(
						AtualizaAcompanhantesInternacaoONExceptionCode.AIN_00871);
			} else {
				throw new ApplicationBusinessException(
						AtualizaAcompanhantesInternacaoONExceptionCode.ERRO_ATUALIZAR_ACOMPANHANTE_INTERNACAO);
			}
		} else {
			logError("Erro ao atualizar Acompanhantes. "
					+ e.getMessage().toString(), e);
			throw new ApplicationBusinessException(
					AtualizaAcompanhantesInternacaoONExceptionCode.ERRO_ATUALIZAR_ACOMPANHANTE_INTERNACAO);
		}
	}
	
	
	/**
	 * MÃ©todo usado para remover os acompanhantes.
	 * 
	 * @param acompanhantesInternacaos
	 * @throws ApplicationBusinessException
	 */
	public void removerAcompanhantes(
			List<AinAcompanhantesInternacao> acompanhantesInternacao)
			throws ApplicationBusinessException {
		AinAcompanhantesInternacaoDAO ainAcompanhantesInternacaoDAO = this
				.getAinAcompanhantesInternacaoDAO();

		try {
			for (AinAcompanhantesInternacao acomp : acompanhantesInternacao) {
				AinAcompanhantesInternacao item = ainAcompanhantesInternacaoDAO
						.obterPorChavePrimaria(acomp.getId());
				removerCrachaAcompanhantes(item.getCrachaAcompanhantes());
				ainAcompanhantesInternacaoDAO.remover(item);
				ainAcompanhantesInternacaoDAO.flush();
			}

		} catch (Exception e) {
			logError("ExceÃ§Ã£o capturada: ", e);
			Throwable cause = ExceptionUtils.getCause(e);
			if (cause instanceof ConstraintViolationException) {
				String constraint = ((ConstraintViolationException) cause)
						.getConstraintName();
				throw new ApplicationBusinessException(
						AtualizaAcompanhantesInternacaoONExceptionCode.ERRO_REMOVER_ACOMPANHANTE_INTERNACAO,
						constraint == null ? "" : constraint);
			} else {
				throw new ApplicationBusinessException(
						AtualizaAcompanhantesInternacaoONExceptionCode.ERRO_REMOVER_ACOMPANHANTE_INTERNACAO,
						"");
			}
		}
	}

	public void removerCrachaAcompanhantes(
			Set<AinCrachaAcompanhantes> crachaAcompanhantes)
			throws ApplicationBusinessException {
		try {
			AinCrachaAcompanhantesDAO ainCrachaAcompanhantesDAO = this
					.getAinCrachaAcompanhantesDAO();

			for (AinCrachaAcompanhantes cracha : crachaAcompanhantes) {
				AinCrachaAcompanhantes item = this
						.getAinCrachaAcompanhantesDAO().obterPorChavePrimaria(
								cracha.getId());
				ainCrachaAcompanhantesDAO.remover(item);
			}

		} catch (Exception e) {
			logError("ExceÃ§Ã£o capturada: ", e);
			Throwable cause = ExceptionUtils.getCause(e);
			if (cause instanceof ConstraintViolationException) {
				String constraint = ((ConstraintViolationException) cause)
						.getConstraintName();
				throw new ApplicationBusinessException(
						AtualizaAcompanhantesInternacaoONExceptionCode.ERRO_REMOVER_ACOMPANHANTE_INTERNACAO,
						constraint == null ? "" : constraint);
			} else {
				throw new ApplicationBusinessException(
						AtualizaAcompanhantesInternacaoONExceptionCode.ERRO_REMOVER_ACOMPANHANTE_INTERNACAO,
						"");
			}
		}
	}

	/**
	 * Obtem lista de crachÃ¡s por acompanhante.
	 * 
	 * @param acompanhanteInternacao
	 * @return Lista de crachÃ¡s.
	 */
	public List<AinCrachaAcompanhantes> obterCrachasAcompanhantes(
			AinAcompanhantesInternacao acompanhanteInternacao) {

		return getAinCrachaAcompanhantesDAO().pesquisarAinCrachaAcompanhantes(
				acompanhanteInternacao);
	}

	/**
	 * Sugere nÃºmero do crachÃ¡ para acompanhantes, que nÃ£o tenham crÃ©dito
	 * refeitÃ³rio.
	 * 
	 * @param acompanhanteInternacao
	 * @return NÃºmero do crachÃ¡.
	 * @throws ApplicationBusinessException
	 */
	public Long sugereNumeroDoCracha(final String usuario)
			throws ApplicationBusinessException {
		// SoluÃ§Ã£o temporÃ¡ria para as regras do HCPA. NÃ£o executa esse cÃ³digo em
		// outros HU's.
		if (!this.isHCPA() || !ainAcompanhantesInternacaoDAO.isOracle()) {
			return null;
		}	
		
		return ainCrachaAcompanhantesDAO.sugereNumeroDoCracha(usuario);
	}

	
	protected AtualizaAcompanhantesInternacaoRN getAtualizaAcompanhantesInternacaoRN() {
		return atualizaAcompanhantesInternacaoRN;
	}

	protected AinAcompanhantesInternacaoDAO getAinAcompanhantesInternacaoDAO() {
		return ainAcompanhantesInternacaoDAO;
	}

	protected AinCrachaAcompanhantesDAO getAinCrachaAcompanhantesDAO() {
		return ainCrachaAcompanhantesDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
