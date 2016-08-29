package br.gov.mec.aghu.exames.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoAmostra;
import br.gov.mec.aghu.dominio.DominioUnidadeMedidaTempo;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.exames.dao.AelAmostraItemExamesDAO;
import br.gov.mec.aghu.exames.dao.AelAmostraItemExamesHistDAO;
import br.gov.mec.aghu.exames.dao.AelAmostrasDAO;
import br.gov.mec.aghu.exames.dao.AelAmostrasHistDAO;
import br.gov.mec.aghu.exames.dao.AelAmostrasJnDAO;
import br.gov.mec.aghu.exames.dao.AelAnticoagulanteDAO;
import br.gov.mec.aghu.exames.dao.AelCadGuicheDAO;
import br.gov.mec.aghu.exames.dao.AelConfigExLaudoUnicoDAO;
import br.gov.mec.aghu.exames.dao.AelMaterialAnaliseDAO;
import br.gov.mec.aghu.exames.dao.AelRecipienteColetaDAO;
import br.gov.mec.aghu.exames.dao.AelSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelSolicitacaoExamesHistDAO;
import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.exames.vo.AelAmostraExamesVO;
import br.gov.mec.aghu.exames.vo.AelAmostrasVO;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AelAmostraItemExames;
import br.gov.mec.aghu.model.AelAmostraItemExamesHist;
import br.gov.mec.aghu.model.AelAmostras;
import br.gov.mec.aghu.model.AelAmostrasHist;
import br.gov.mec.aghu.model.AelAmostrasJn;
import br.gov.mec.aghu.model.AelAnticoagulante;
import br.gov.mec.aghu.model.AelCadGuiche;
import br.gov.mec.aghu.model.AelConfigExLaudoUnico;
import br.gov.mec.aghu.model.AelExtratoAmostras;
import br.gov.mec.aghu.model.AelExtratoAmostrasId;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelRecipienteColeta;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelSolicitacaoExamesHist;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

/**
 * 
 * @author lsamberg
 * 
 */
@SuppressWarnings("PMD.AghuTooManyMethods")
@Stateless
public class AelAmostrasRN extends BaseBusiness {

	@Inject
	private AelAnticoagulanteDAO aelAnticoagulanteDAO;
	
	@Inject
	private AelRecipienteColetaDAO aelRecipienteColetaDAO;
	
	@Inject
	private AelAmostrasHistDAO aelAmostrasHistDAO;
	
	@Inject
	private AelSolicitacaoExamesHistDAO aelSolicitacaoExamesHistDAO;
	
	@EJB
	private AelExtratoAmostrasON aelExtratoAmostrasON;
	
	private static final Log LOG = LogFactory.getLog(AelAmostrasRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelAmostraItemExamesDAO aelAmostraItemExamesDAO;
	
	@Inject
	private AelCadGuicheDAO aelCadGuicheDAO;
	
	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;
	
	@Inject
	private AelSolicitacaoExameDAO aelSolicitacaoExameDAO;
	
	@Inject
	private AelMaterialAnaliseDAO aelMaterialAnaliseDAO;	

	@Inject
	private AelAmostraItemExamesHistDAO aelAmostraItemExamesHistDAO;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private AelAmostrasDAO aelAmostrasDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;
	
	@Inject
	private AelAmostrasJnDAO aelAmostrasJnDAO;
	
	@Inject
	private AelConfigExLaudoUnicoDAO aelConfigExLaudoUnicoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5960016652904441045L;

	public enum AelAmostrasRNExceptionCode implements BusinessExceptionCode {

		AEL_00556, AEL_00558, AEL_00560, AEL_00562, AEL_01922, AEL_01477;

	}
	
	// @ORADB AELC_CALC_NUMERO_AP
	public Long calcularNumeroAp(AelItemSolicitacaoExames item)
			throws ApplicationBusinessException {
		AelConfigExLaudoUnico configExame = getAelConfigExLaudoUnicoDAO().obterConfigExLaudoUnico(item);
		Long numeroAp = configExame.getUltimoNumero();
		Integer anoAtual = Calendar.getInstance().get(Calendar.YEAR);
		anoAtual = anoAtual - ((anoAtual / 100) * 100);
		Integer numero = (int) (numeroAp / 100);
		final Integer anoAp = (int) (numeroAp - numero * 100);

		if (anoAtual == anoAp) {
			numero++;
			if (numero == 999999) {
				throw new ApplicationBusinessException(
						AelAmostrasRNExceptionCode.AEL_01477);
			}
		} else {
			numero = 1;
		}

		numeroAp = (numero * 100L) + anoAtual;
		configExame.setUltimoNumero(numeroAp);
		
		try {
			getExamesPatologiaFacade().persistirConfigLaudoUnico(configExame);
		} catch (BaseException e) {
			throw new ApplicationBusinessException(e);
		}

		return numeroAp.longValue();
	}

	/**
	 * Insere objeto AelAmostras.
	 * 
	 * @param {AelAmostras} amostra
	 * @return {AelAmostras}
	 * @throws BaseException
	 */
	public AelAmostras inserir(AelAmostras amostra) throws BaseException {
		this.beforeInsertAelAmostras(amostra);

		this.getAelAmostrasDAO().persistir(amostra);
		this.getAelAmostrasDAO().flush();

		// ENFORCE
		this.extrAmo(amostra, amostra.getSituacao());

		return amostra;
	}

	/**
	 * Regras de update em AEL_AMOSTRAS
	 * 
	 * @param aelAmostras
	 * @throws ApplicationBusinessException
	 */
	public void atualizarAelAmostra(AelAmostras aelAmostras, Boolean flush)throws BaseException {
		AelAmostras aelAmostrasOld = this.getAelAmostrasDAO().obterOriginal(aelAmostras.getId());
		beforeUpdateAelAmostras(aelAmostras);
		getAelAmostrasDAO().merge(aelAmostras);
		if (flush){
			getAelAmostrasDAO().flush();
		}
		afterUpdateAelAmostras(aelAmostras, aelAmostrasOld);
	}

	/**
	 * Remover objeto AelAmostras SEM FLUSH
	 * 
	 * @param {AelAmostras} amostra
	 * @return {AelAmostras}
	 * @throws BaseException
	 */
	public void removerSemFlush(final AelAmostras amostra) throws BaseException {

		final AelAmostras aelAmostrasOld = this.getAelAmostrasDAO().obterOriginal(amostra.getId());

		this.getAelAmostrasDAO().remover(amostra);

		this.afterDeleteAelAmostras(aelAmostrasOld);

	}

	/**
	 * ORADB TRIGGER AELT_AMO_BRI
	 * 
	 * @param aelAmostras
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	public void beforeInsertAelAmostras(final AelAmostras aelAmostras) throws ApplicationBusinessException {

		this.verUnidFun(aelAmostras);

		this.verMatAnls(aelAmostras);

		this.verAnticoag(aelAmostras);

		this.verRecCole(aelAmostras);

		this.atualizarDataHoraEntrada(aelAmostras);

	}

	/**
	 * Se a situação for igual a 'R' deve atualizar a data de entrada para data
	 * corrente da aplicação.
	 * 
	 * @param {AelAmostras} aelAmostras
	 */
	public void atualizarDataHoraEntrada(final AelAmostras amostra) {

		if (DominioSituacaoAmostra.R.equals(amostra.getSituacao())) {

			amostra.setDthrEntrada(new Date());

		}

	}

	/**
	 * 
	 * @param solicitacaoExame
	 * @param amostraSeqp
	 * @return
	 * @throws BaseException
	 */
	public List<AelAmostrasVO> listarAmostrasExamesVO(final Integer soqSeq, Boolean isHist) throws BaseException {
		if(isHist){
			return listarAmostrasExamesVOHistorico(soqSeq);
		}else{
			return listarAmostrasExamesVOProducao(soqSeq);
		}
	}

	private List<AelAmostrasVO> listarAmostrasExamesVOHistorico(Integer soqSeq) {
		final AelSolicitacaoExamesHist solicitacaoExame = getAelSolicitacaoExamesHistDAO().obterPorChavePrimaria(soqSeq);

		final List<AelAmostrasHist> listaAmostras = this.getAelAmostrasHistDAO().buscarAmostrasPorSolicitacaoExameEItemSolicitacao(solicitacaoExame,
				null);

		List<AelAmostrasVO> listaVO = null;

		if (listaAmostras != null && !listaAmostras.isEmpty()) {
			listaVO = new LinkedList<AelAmostrasVO>();
			Short seqVO = 0;

			// Popula lista de VOs
			for (final AelAmostrasHist amostra : listaAmostras) {
				final AelAmostrasVO vo = new AelAmostrasVO();
				vo.setSeqVO(seqVO++);
				vo.setSoeSeq(amostra.getSolicitacaoExame().getSeq());
				vo.setSeqp(amostra.getId().getSeqp());
				vo.setNroUnico(amostra.getNroUnico());
				vo.setDtNumeroUnico(amostra.getDtNumeroUnico());
				vo.setNroFrascoFabricante(amostra.getNroFrascoFabricante());
				vo.setTempoIntervaloColeta(amostra.getTempoIntervaloColeta());
				if(amostra.getUnidTempoIntervaloColeta() != null){
					vo.setUnidTempoIntervaloColeta(DominioUnidadeMedidaTempo.valueOf(amostra.getUnidTempoIntervaloColeta()));
				}
				if(amostra.getSituacao() != null){
					vo.setSituacao(DominioSituacaoAmostra.valueOf(amostra.getSituacao()));
				}
				vo.setEmEdicao(false);

				/*
				 * Ghichê
				 */
				if (amostra.getCguSeq() != null) {
					AelCadGuiche guiche = getAelCadGuicheDAO().obterPorChavePrimaria(amostra.getCguSeq());
					vo.setGuiche(guiche != null ? guiche.getDescricao() : null);
				}
	
				if (amostra.getRcoSeq() != null) {
					AelRecipienteColeta recipienteColeta = getAelRecipienteColetaDAO().obterPorChavePrimaria(amostra.getRcoSeq());
					vo.setRecipienteColeta(recipienteColeta != null ? recipienteColeta.getDescricao(): null);
				}
				if (amostra.getAtcSeq() != null) {
					AelAnticoagulante anticoagulante = getAelAnticoagulanteDAO().obterPorChavePrimaria(amostra.getAtcSeq());
					vo.setAnticoagulante(anticoagulante != null ? anticoagulante.getDescricao(): null);
				}

				vo.setDthrPrevistaColeta(amostra.getDthrPrevistaColeta());

				vo.setPaciente(processarNomePaciente(amostra.getSolicitacaoExame()));

				// Acrescenta item populado de vo na listagem
				listaVO.add(vo);
			}
		}

		return listaVO;
	}

	private List<AelAmostrasVO> listarAmostrasExamesVOProducao(
			final Integer soqSeq) {
		final AelSolicitacaoExames solicitacaoExame = getAelSolicitacaoExameDAO().obterPeloId(soqSeq);

		final List<AelAmostras> listaAmostras = this.getAelAmostrasDAO().buscarAmostrasPorSolicitacaoExameEItemSolicitacao(solicitacaoExame,
				null);

		List<AelAmostrasVO> listaVO = null;

		if (listaAmostras != null && !listaAmostras.isEmpty()) {
			listaVO = new LinkedList<AelAmostrasVO>();
			Short seqVO = 0;

			// Popula lista de VOs
			for (final AelAmostras amostra : listaAmostras) {
				final AelAmostrasVO vo = new AelAmostrasVO();
				vo.setSeqVO(seqVO++);
				vo.setSoeSeq(amostra.getId().getSoeSeq());
				vo.setSeqp(amostra.getId().getSeqp());
				vo.setNroUnico(amostra.getNroUnico());
				vo.setDtNumeroUnico(amostra.getDtNumeroUnico());
				vo.setNroFrascoFabricante(amostra.getNroFrascoFabricante());
				vo.setTempoIntervaloColeta(amostra.getTempoIntervaloColeta());
				vo.setUnidTempoIntervaloColeta(amostra.getUnidTempoIntervaloColeta());
				vo.setSituacao(amostra.getSituacao());
				vo.setEmEdicao(false);

				/*
				 * Ghichê
				 */
				if (amostra.getGuiche() != null) {
					vo.setGuiche(amostra.getGuiche().getDescricao());
				}

				if (amostra.getRecipienteColeta() != null) {
					vo.setRecipienteColeta(amostra.getRecipienteColeta().getDescricao());
				}
				if (amostra.getAnticoagulante() != null) {
					vo.setAnticoagulante(amostra.getAnticoagulante().getDescricao());
				}

				vo.setDthrPrevistaColeta(amostra.getDthrPrevistaColeta());

				vo.setPaciente(processarNomePaciente(amostra.getSolicitacaoExame()));

				// Acrescenta item populado de vo na listagem
				listaVO.add(vo);
			}
		}

		return listaVO;
	}
	
	public List<AelAmostrasVO> listarAmostrasExamesVOPorAtendimento(final Short hedGaeUnfSeq,
		final Integer hedGaeSeqp, final Date hedDthrAgenda, Boolean isHist) throws BaseException {
		if(isHist){//Todas as alterações feitas em "produção" precisam ser feitas também em histórico
			return listarAmostrasExamesVOPorAtendimentoHistorico(hedGaeUnfSeq,
					hedGaeSeqp, hedDthrAgenda);
		}else{
			return listarAmostrasExamesVOPorAtendimentoProducao(hedGaeUnfSeq,
					hedGaeSeqp, hedDthrAgenda);
		}
		
	}

	private List<AelAmostrasVO> listarAmostrasExamesVOPorAtendimentoHistorico(
			Short hedGaeUnfSeq, Integer hedGaeSeqp, Date hedDthrAgenda) {
		final List<AelAmostrasHist> listaAmostras = this.getAelAmostrasHistDAO().listarAmostrasPorAgendamento(hedGaeUnfSeq, hedGaeSeqp, hedDthrAgenda);

		List<AelAmostrasVO> listaVO = null;

		if (listaAmostras != null && !listaAmostras.isEmpty()) {
			listaVO = new LinkedList<AelAmostrasVO>();
			Short seqVO = 0;

			// Popula lista de VOs
			for (final AelAmostrasHist amostra : listaAmostras) {
				final AelAmostrasVO vo = new AelAmostrasVO();
				vo.setSeqVO(seqVO++);
				vo.setSoeSeq(amostra.getSolicitacaoExame().getSeq());
				vo.setSeqp(amostra.getId().getSeqp());
				vo.setNroUnico(amostra.getNroUnico());
				vo.setDtNumeroUnico(amostra.getDtNumeroUnico());
				vo.setNroFrascoFabricante(amostra.getNroFrascoFabricante());
				vo.setTempoIntervaloColeta(amostra.getTempoIntervaloColeta());
				if(amostra.getUnidTempoIntervaloColeta() != null){
					vo.setUnidTempoIntervaloColeta(DominioUnidadeMedidaTempo.valueOf(amostra.getUnidTempoIntervaloColeta()));
				}
				if(amostra.getSituacao() != null){
					vo.setSituacao(DominioSituacaoAmostra.valueOf(amostra.getSituacao()));
				}
				vo.setEmEdicao(false);

				/*
				 * Ghichê
				 */
				if (amostra.getCguSeq() != null) {
						AelCadGuiche guiche = getAelCadGuicheDAO().obterPorChavePrimaria(amostra.getCguSeq());
						vo.setGuiche(guiche != null ? guiche.getDescricao() : null);
				}

				if (amostra.getRcoSeq() != null) {
					AelRecipienteColeta recipienteColeta = getAelRecipienteColetaDAO().obterPorChavePrimaria(amostra.getRcoSeq());
					vo.setRecipienteColeta(recipienteColeta != null ? recipienteColeta.getDescricao(): null);
				}
				if (amostra.getAtcSeq() != null) {
					AelAnticoagulante anticoagulante = getAelAnticoagulanteDAO().obterPorChavePrimaria(amostra.getAtcSeq());
					vo.setAnticoagulante(anticoagulante != null ? anticoagulante.getDescricao(): null);
				}

				vo.setDthrPrevistaColeta(amostra.getDthrPrevistaColeta());

				vo.setPaciente(processarNomePaciente(amostra.getSolicitacaoExame()));//adicionado devido a pmd

				// Acrescenta item populado de vo na listagem
				listaVO.add(vo);
			}
		}

		return listaVO;
	}

	private String processarNomePaciente(AelSolicitacaoExamesHist solicitacaoExame) {
		if (solicitacaoExame != null && solicitacaoExame.getAtendimento() != null && solicitacaoExame.getAtendimento().getPaciente() != null) {
			return solicitacaoExame.getAtendimento().getPaciente().getNome();
		} else if (solicitacaoExame != null && solicitacaoExame.getAtendimentoDiverso() != null) {
			final AghAtendimentos atendimento = getAghuFacade().obterAghAtendimentoPorChavePrimaria(
					solicitacaoExame.getAtendimentoDiverso().getSeq());
			return atendimento.getPaciente().getNome();
		}
		return null;
	}
	
	private String processarNomePaciente(AelSolicitacaoExames solicitacaoExame) {
		if (solicitacaoExame != null && solicitacaoExame.getAtendimento() != null && solicitacaoExame.getAtendimento().getPaciente() != null) {
			return solicitacaoExame.getAtendimento().getPaciente().getNome();
		} else if (solicitacaoExame != null && solicitacaoExame.getAtendimentoDiverso() != null) {
			final AghAtendimentos atendimento = getAghuFacade().obterAghAtendimentoPorChavePrimaria(
					solicitacaoExame.getAtendimentoDiverso().getSeq());
			return atendimento.getPaciente().getNome();
		}
		return null;
	}
	
	/**
	 * As alterações feitas neste método precisam ser feitas também em "sua cópia" de histórico
	 * @param hedGaeUnfSeq
	 * @param hedGaeSeqp
	 * @param hedDthrAgenda
	 * @return
	 */
	private List<AelAmostrasVO> listarAmostrasExamesVOPorAtendimentoProducao(
			final Short hedGaeUnfSeq, final Integer hedGaeSeqp,
			final Date hedDthrAgenda) {
		final List<AelAmostras> listaAmostras = this.getAelAmostrasDAO().listarAmostrasPorAgendamento(hedGaeUnfSeq, hedGaeSeqp, hedDthrAgenda);

		List<AelAmostrasVO> listaVO = null;

		if (listaAmostras != null && !listaAmostras.isEmpty()) {
			listaVO = new LinkedList<AelAmostrasVO>();
			Short seqVO = 0;

			// Popula lista de VOs
			for (final AelAmostras amostra : listaAmostras) {
				final AelAmostrasVO vo = new AelAmostrasVO();
				vo.setSeqVO(seqVO++);
				vo.setSoeSeq(amostra.getId().getSoeSeq());
				vo.setSeqp(amostra.getId().getSeqp());
				vo.setNroUnico(amostra.getNroUnico());
				vo.setDtNumeroUnico(amostra.getDtNumeroUnico());
				vo.setNroFrascoFabricante(amostra.getNroFrascoFabricante());
				vo.setTempoIntervaloColeta(amostra.getTempoIntervaloColeta());
				vo.setUnidTempoIntervaloColeta(amostra.getUnidTempoIntervaloColeta());
				vo.setSituacao(amostra.getSituacao());
				vo.setEmEdicao(false);

				/*
				 * Ghichê
				 */
				if (amostra.getGuiche() != null) {
						vo.setGuiche(amostra.getGuiche().getDescricao());
				}

				if (amostra.getRecipienteColeta() != null) {
					vo.setRecipienteColeta(amostra.getRecipienteColeta().getDescricao());
				}
				if (amostra.getAnticoagulante() != null) {
					vo.setAnticoagulante(amostra.getAnticoagulante().getDescricao());
				}

				vo.setDthrPrevistaColeta(amostra.getDthrPrevistaColeta());

				vo.setPaciente(processarNomePaciente(amostra.getSolicitacaoExame()));

				// Acrescenta item populado de vo na listagem
				listaVO.add(vo);
			}
		}

		return listaVO;
	}

	

	/**
	 * ORADB TRIGGER AELT_AMO_BRU
	 * 
	 * @param aelAmostras
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	public void beforeUpdateAelAmostras(final AelAmostras aelAmostras) throws ApplicationBusinessException {
		this.getAelAmostrasDAO().desatachar(aelAmostras);
		final AelAmostras aelAmostrasOld = this.getAelAmostrasDAO().obterPorChavePrimaria(aelAmostras.getId());

		if (aelAmostras.getUnidadesFuncionais() != null
				&& aelAmostras.getUnidadesFuncionais().getSeq() != aelAmostrasOld.getUnidadesFuncionais().getSeq()) {
			verUnidFun(aelAmostras);
		}

		if (aelAmostras.getMateriaisAnalises() != null
				&& aelAmostras.getMateriaisAnalises().getSeq() != aelAmostrasOld.getMateriaisAnalises().getSeq()) {
			verMatAnls(aelAmostras.getMateriaisAnalises().getSeq());
		}

		if (aelAmostras.getAnticoagulante() != null && aelAmostras.getAnticoagulante().getSeq() != aelAmostrasOld.getAnticoagulante().getSeq()) {
			verAnticoag(aelAmostras);
		}

		if (aelAmostras.getRecipienteColeta() != null && aelAmostras.getRecipienteColeta().getSeq() != aelAmostrasOld.getRecipienteColeta().getSeq()) {
			verRecCole(aelAmostras);
		}
	}

	/**
	 * ORADB TRIGGER AELT_AMO_ASU - AELP_ENFORCE_AMO_RULES - AELT_AMO_ARU
	 * 
	 * @param aelAmostras
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	public void afterUpdateAelAmostras(final AelAmostras aelAmostras, final AelAmostras aelAmostrasOld) throws BaseException{

		final DominioSituacaoAmostra situacaoAmostraOld = aelAmostrasOld.getSituacao();
		final Integer nroUnicoOld = aelAmostrasOld.getNroUnico();

		if (!aelAmostras.getSituacao().equals(situacaoAmostraOld)) {
			extrAmo(aelAmostras, situacaoAmostraOld);
		}

		if (aelAmostras.getNroUnico() != null && situacaoAmostraOld != null 
				&& situacaoAmostraOld.equals(DominioSituacaoAmostra.R) && !CoreUtil.igual(nroUnicoOld, aelAmostras.getNroUnico())) {

				//aelAmostras.setSituacao(situacaoAmostraOld);
				//aelAmostras.setNroUnico(nroUnicoOld);
				verNrUnico(aelAmostras);

		}
		
		this.inserirAelAmostrasJn(aelAmostrasOld, DominioOperacoesJournal.UPD);

	}

	/**
	 * ORADB TRIGGER AELT_AMO_ARD
	 * 
	 * @param aelAmostras
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	public void afterDeleteAelAmostras(final AelAmostras aelAmostrasOld) throws ApplicationBusinessException {

		this.inserirAelAmostrasJn(aelAmostrasOld, DominioOperacoesJournal.DEL);
	}

	private void inserirAelAmostrasJn(final AelAmostras aelAmostrasOld, final DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		

		final AelAmostrasJn jn = BaseJournalFactory.getBaseJournal(operacao, AelAmostrasJn.class, servidorLogado.getUsuario());

		if (aelAmostrasOld.getAnticoagulante() != null) {

			jn.setAtcSeq(aelAmostrasOld.getAnticoagulante().getSeq().shortValue());

		}
		
		if(aelAmostrasOld != null && aelAmostrasOld.getConfigMapa() != null) {
			jn.setCgmSeq(aelAmostrasOld.getConfigMapa().getSeq());
		}
		jn.setCguSeq(aelAmostrasOld.getGuiche() != null ? aelAmostrasOld.getGuiche().getSeq() : null);
		jn.setDthrEntrada(aelAmostrasOld.getDthrEntrada());
		jn.setDthrPrevistaColeta(aelAmostrasOld.getDthrPrevistaColeta());
		jn.setDtNumeroUnico(aelAmostrasOld.getDtNumeroUnico());

		if (aelAmostrasOld.getMateriaisAnalises() != null) {

			jn.setManSeq(aelAmostrasOld.getMateriaisAnalises().getSeq());

		}

		jn.setNroFrascoFabricante(aelAmostrasOld.getNroFrascoFabricante());
		jn.setNroInterno(aelAmostrasOld.getNroInterno());
		jn.setNroUnico(aelAmostrasOld.getNroUnico());

		if (aelAmostrasOld.getRecipienteColeta() != null) {

			jn.setRcoSeq(aelAmostrasOld.getRecipienteColeta().getSeq().shortValue());

		}

		if (aelAmostrasOld.getSalasExecutorasExames() != null) {

			jn.setSeeSeqp(aelAmostrasOld.getSalasExecutorasExames().getId().getSeqp());
			jn.setSeeUnfSeq(aelAmostrasOld.getSalasExecutorasExames().getId().getUnfSeq());

		}

		jn.setSeqp(aelAmostrasOld.getId().getSeqp());
		jn.setTempoIntervaloColeta(aelAmostrasOld.getTempoIntervaloColeta());

		if (aelAmostrasOld.getUnidadesFuncionais() != null) {

			jn.setUnfSeq(aelAmostrasOld.getUnidadesFuncionais().getSeq());

		}

		jn.setUnidTempoIntervaloColeta(aelAmostrasOld.getUnidTempoIntervaloColeta());

		if (aelAmostrasOld.getServidor() != null) {

			jn.setSerMatricula(aelAmostrasOld.getServidor().getId().getMatricula());
			jn.setSerVinCodigo(aelAmostrasOld.getServidor().getId().getVinCodigo());

		}

		jn.setSituacao(aelAmostrasOld.getSituacao());
		jn.setSoeSeq(aelAmostrasOld.getId().getSoeSeq());

		getAelAmostrasJnDAO().persistir(jn);

	}

	/**
	 * ORADB aelk_amo_rn.rn_amop_ver_nr_unico
	 * 
	 * @param aelAmostras
	 */
	public void verNrUnico(final AelAmostras aelAmostras) throws ApplicationBusinessException {
		final List<AelAmostras> listaAmostrasDiferentes = getAelAmostrasDAO().buscarAmostrasOutrasSolicitacoes(aelAmostras);

		if (listaAmostrasDiferentes != null && !listaAmostrasDiferentes.isEmpty()) {
			final AelAmostras amostraDiff = listaAmostrasDiferentes.get(0);
			final AelSolicitacaoExames solicitacaoExamesDiff = this.getAelSolicitacaoExameDAO()
				.obterPeloId(amostraDiff.getId().getSoeSeq());
			final AelSolicitacaoExames solicitacaoExames = this.getAelSolicitacaoExameDAO()
			.obterPeloId(aelAmostras.getId().getSoeSeq());
			
			final Integer vPacCodigo = this.getExamesFacade().buscarLaudoCodigoPaciente(solicitacaoExamesDiff);
			final Integer vPacCodigoNew = this.getExamesFacade().buscarLaudoCodigoPaciente(solicitacaoExames);
			
			if(vPacCodigo != null && vPacCodigo != vPacCodigoNew) {
				throw new ApplicationBusinessException(AelAmostrasRNExceptionCode.AEL_01922);
			}

		}

	}

	/**
	 * ORADB aelk_amo_rn.rn_amop_atu_extr_amo
	 * 
	 * @param aelAmostras
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	public void extrAmo(final AelAmostras aelAmostras, final DominioSituacaoAmostra situacaoAmostra) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		final List<AelExtratoAmostras> extratos = getAelExtratoAmostrasON().buscarAelExtratosAmostrasPorAmostra(aelAmostras);
		Short maxSeqp = 0;

		for (final AelExtratoAmostras ex : extratos) {

			if (ex.getId().getSeqp() > maxSeqp) {

				maxSeqp = ex.getId().getSeqp();

			}

		}

		final AelExtratoAmostras extrato = new AelExtratoAmostras();
		extrato.setId(new AelExtratoAmostrasId());
		extrato.getId().setAmoSeqp(aelAmostras.getId().getSeqp());
		extrato.getId().setAmoSoeSeq(aelAmostras.getId().getSoeSeq());
		extrato.getId().setSeqp((short) (maxSeqp + 1));
		extrato.setSituacao(aelAmostras.getSituacao());
		extrato.setServidor(servidorLogado);

		getAelExtratoAmostrasON().inserirAelExtratoAmostra(extrato);

	}

	/**
	 * ORADB aelk_amo_rn.rn_amop_ver_unid_fun
	 * 
	 * @param aelAmostras
	 * @throws ApplicationBusinessException
	 */
	public void verUnidFun(final AelAmostras aelAmostras) throws ApplicationBusinessException {

		getCadastrosApoioExamesFacade().verUnfAtiv(aelAmostras.getUnidadesFuncionais());

		if (!getInternacaoFacade().verificarCaracteristicaUnidadeFuncional(aelAmostras.getUnidadesFuncionais().getSeq(),
				ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_EXAMES)) {

			throw new ApplicationBusinessException(AelAmostrasRNExceptionCode.AEL_00556);

		}

	}

	/**
	 * ORADB aelk_amo_rn.rn_amop_ver_mat_anls
	 * 
	 * @param aelAmostras
	 * @throws ApplicationBusinessException
	 */
	public void verMatAnls(final AelAmostras aelAmostras) throws ApplicationBusinessException {

		final AelMateriaisAnalises material = aelAmostras.getMateriaisAnalises();

		if (!material.getIndSituacao().equals(DominioSituacao.A)) {

			throw new ApplicationBusinessException(AelAmostrasRNExceptionCode.AEL_00558);

		}

	}

	/**
	 * ORADB aelk_amo_rn.rn_amop_ver_mat_anls
	 * 
	 * @param aelAmostras
	 * @throws ApplicationBusinessException
	 */
	public void verMatAnls(final Integer codigoMaterial ) throws ApplicationBusinessException {
		if(codigoMaterial != null){
			final AelMateriaisAnalises material =  aelMaterialAnaliseDAO.obterPeloId(codigoMaterial);
			if (!material.getIndSituacao().equals(DominioSituacao.A)) {
				throw new ApplicationBusinessException(AelAmostrasRNExceptionCode.AEL_00558);
			}
		}
	}

	
	/**
	 * ORADB aelk_amo_rn.rn_amop_ver_anticoag
	 * 
	 * @param aelAmostras
	 * @throws ApplicationBusinessException
	 */
	public void verAnticoag(final AelAmostras aelAmostras) throws ApplicationBusinessException {

		if (aelAmostras.getAnticoagulante() != null) {

			final AelAnticoagulante anti = aelAmostras.getAnticoagulante();

			if (!DominioSituacao.A.equals(anti.getIndSituacao())) {

				throw new ApplicationBusinessException(AelAmostrasRNExceptionCode.AEL_00560);

			}

		}

	}

	/**
	 * ORADB aelk_amo_rn.rn_amop_ver_rec_cole
	 * 
	 * @param aelAmostras
	 * @throws ApplicationBusinessException
	 */
	public void verRecCole(final AelAmostras aelAmostras) throws ApplicationBusinessException {

		final AelRecipienteColeta rec = aelAmostras.getRecipienteColeta();

		if (!rec.getIndSituacao().equals(DominioSituacao.A)) {

			throw new ApplicationBusinessException(AelAmostrasRNExceptionCode.AEL_00562);

		}

	}

	/**
	 * ORADB aelc_avalia_sit_amos
	 * 
	 * @param aelAmostras
	 * @return
	 */
	public DominioSituacaoAmostra avaliarSituacaoAmostras(final AelAmostras aelAmostras) {

		final List<AelAmostraItemExames> aelAmostraItemExamesList = this.getAelAmostraItemExamesDAO().buscarAelAmostraItemExamesPorAmostra(aelAmostras
				.getId().getSoeSeq(), aelAmostras.getId().getSeqp() + 0);

		final StringBuffer situacaoItens = new StringBuffer("|");

		for (final AelAmostraItemExames item : aelAmostraItemExamesList) {
			situacaoItens.append(item.getSituacao().toString()).append('|');
		}

		final String vSituacao = situacaoItens.toString();
		if (vSituacao.contains("|G")) {

			return DominioSituacaoAmostra.G;

		} else if (vSituacao.contains("|M")) {

			return DominioSituacaoAmostra.M;

		} else if (vSituacao.contains("|C")) {

			return DominioSituacaoAmostra.C;

		} else if (vSituacao.contains("|U")) {

			return DominioSituacaoAmostra.U;

		} else if (vSituacao.contains("|R")) {

			return DominioSituacaoAmostra.R;

		} else if (vSituacao.contains("|E")) {

			return DominioSituacaoAmostra.E;

		} else {

			return DominioSituacaoAmostra.A;
		}

	}

	public ICadastrosApoioExamesFacade getCadastrosApoioExamesFacade() {
		return cadastrosApoioExamesFacade;
	}

	public AelRecipienteColetaDAO getAelRecipienteColetaDAO() {
		return aelRecipienteColetaDAO;
	}
	
	public AelAmostrasDAO getAelAmostrasDAO() {
		return aelAmostrasDAO;
	}
	
	public AelAnticoagulanteDAO getAelAnticoagulanteDAO() {
		return aelAnticoagulanteDAO;
	}
	
	public AelAmostrasHistDAO getAelAmostrasHistDAO() {
		return aelAmostrasHistDAO;
	}

	protected AelSolicitacaoExameDAO getAelSolicitacaoExameDAO() {
		return aelSolicitacaoExameDAO;
	}
	
	protected AelSolicitacaoExamesHistDAO getAelSolicitacaoExamesHistDAO() {
		return aelSolicitacaoExamesHistDAO;
	}

	public AelAmostrasJnDAO getAelAmostrasJnDAO() {
		return aelAmostrasJnDAO;
	}

	public AelExtratoAmostrasON getAelExtratoAmostrasON() {
		return aelExtratoAmostrasON;
	}

	protected IInternacaoFacade getInternacaoFacade() {
		return internacaoFacade;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	public AelCadGuicheDAO getAelCadGuicheDAO() {
		return aelCadGuicheDAO;
	}

	protected IExamesFacade getExamesFacade() {
		return examesFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}
	
	protected AelAmostraItemExamesHistDAO getAelAmostraItemExamesHistDAO(){
		return aelAmostraItemExamesHistDAO;
	}

	public List<AelAmostraExamesVO> buscarItensAmostraExame(Integer soeSeq,
			Short iseSeqp, Integer amoSeqp, Boolean isHist) {
		Short seqVO = 0;
		List<AelAmostraExamesVO> listaItensAmostra = new ArrayList<AelAmostraExamesVO>();
		if(isHist){
			List<AelAmostraItemExamesHist> itensAmostra = getAelAmostraItemExamesHistDAO().buscarItensAmostraExame(soeSeq, iseSeqp, amoSeqp);
			for(AelAmostraItemExamesHist item : itensAmostra) {
				AelAmostraExamesVO itemAmostra = new AelAmostraExamesVO();
				itemAmostra.setSeqVO(seqVO++);
				itemAmostra.setIseSoeSeq(item.getId().getIseSoeSeq());
				itemAmostra.setIseSeqp(item.getId().getIseSeqp());
				itemAmostra.setAmoSoeSeq(item.getId().getAmoSoeSeq());
				itemAmostra.setAmoSeqp(item.getId().getAmoSeqp());
				itemAmostra.setSelecionado(true);
				itemAmostra.setNumeroMapa(item.getNroMapa());
				itemAmostra.setDescricaoUsual(item.getAelItemSolicitacaoExames().getExame().getDescricaoUsual());
				itemAmostra.setDescricaoMaterial(item.getAelItemSolicitacaoExames().getMaterialAnalise().getDescricao());
				itemAmostra.setSituacao(item.getSituacao());
				listaItensAmostra.add(itemAmostra);
				
			}
		}else{
			List<AelAmostraItemExames> itensAmostra = getAelAmostraItemExamesDAO().buscarItensAmostraExame(soeSeq, iseSeqp, amoSeqp);
			for(AelAmostraItemExames item : itensAmostra) {
				AelAmostraExamesVO itemAmostra = new AelAmostraExamesVO();
				itemAmostra.setSeqVO(seqVO++);
				itemAmostra.setIseSoeSeq(item.getId().getIseSoeSeq());
				itemAmostra.setIseSeqp(item.getId().getIseSeqp());
				itemAmostra.setAmoSoeSeq(item.getId().getAmoSoeSeq());
				itemAmostra.setAmoSeqp(item.getId().getAmoSeqp());
				itemAmostra.setSelecionado(true);
				itemAmostra.setNumeroMapa(item.getNroMapa());
				itemAmostra.setDescricaoUsual(item.getAelItemSolicitacaoExames().getExame().getDescricaoUsual());
				itemAmostra.setDescricaoMaterial(item.getAelItemSolicitacaoExames().getMaterialAnalise().getDescricao());
				itemAmostra.setSituacao(item.getSituacao());
				listaItensAmostra.add(itemAmostra);
				
			}
		}
		return listaItensAmostra;
	}

	protected AelAmostraItemExamesDAO getAelAmostraItemExamesDAO() {
		return aelAmostraItemExamesDAO;
	}
	
	protected AelConfigExLaudoUnicoDAO getAelConfigExLaudoUnicoDAO() {
		return aelConfigExLaudoUnicoDAO;
	}
	
	protected IExamesPatologiaFacade getExamesPatologiaFacade() {
		return examesPatologiaFacade;
	}
	
	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}