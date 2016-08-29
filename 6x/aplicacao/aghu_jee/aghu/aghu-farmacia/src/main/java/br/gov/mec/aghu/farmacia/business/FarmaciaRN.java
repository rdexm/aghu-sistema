package br.gov.mec.aghu.farmacia.business;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.farmacia.dao.AfaGrupoMedicamentoDAO;
import br.gov.mec.aghu.farmacia.dao.AfaGrupoMedicamentoMensagemDAO;
import br.gov.mec.aghu.farmacia.dao.AfaItemGrupoMedicamentoDAO;
import br.gov.mec.aghu.farmacia.dao.AfaMensagemMedicamentoDAO;
import br.gov.mec.aghu.farmacia.dao.AfaMensagemMedicamentoJnDAO;
import br.gov.mec.aghu.farmacia.dao.AfaTipoVelocAdministracoesDAO;
import br.gov.mec.aghu.farmacia.dao.AfaViaAdmUnfDAO;
import br.gov.mec.aghu.farmacia.dao.AfaViaAdministracaoDAO;
import br.gov.mec.aghu.internacao.vo.AghUnidadesFuncionaisVO;
import br.gov.mec.aghu.model.AfaGrupoMedicamento;
import br.gov.mec.aghu.model.AfaGrupoMedicamentoMensagem;
import br.gov.mec.aghu.model.AfaItemGrupoMedicamento;
import br.gov.mec.aghu.model.AfaMensagemMedicamento;
import br.gov.mec.aghu.model.AfaMensagemMedicamentoJn;
import br.gov.mec.aghu.model.AfaTipoVelocAdministracoes;
import br.gov.mec.aghu.model.AfaViaAdmUnf;
import br.gov.mec.aghu.model.AfaViaAdmUnfId;
import br.gov.mec.aghu.model.AfaViaAdministracao;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.vo.AfaViaAdmUnfVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

/**
 * Implementação da package AFAK_RN.
 */
// Metodos serão migrados por demanada.

@SuppressWarnings("PMD.AghuTooManyMethods")
@Stateless
public class FarmaciaRN extends BaseBusiness implements Serializable {

	@EJB
	private GrupoMedicamentoRN grupoMedicamentoRN;
	
	@EJB
	private TipoVelocidadeAdministracaoRN tipoVelocidadeAdministracaoRN;
	
	@EJB
	private ItemGrupoMedicamentoRN itemGrupoMedicamentoRN;
	
	private static final Log LOG = LogFactory.getLog(FarmaciaRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AfaTipoVelocAdministracoesDAO afaTipoVelocAdministracoesDAO;
	
	@Inject
	private AfaItemGrupoMedicamentoDAO afaItemGrupoMedicamentoDAO;
	
	@Inject
	private AfaGrupoMedicamentoMensagemDAO afaGrupoMedicamentoMensagemDAO;
	
	@Inject
	private AfaMensagemMedicamentoJnDAO afaMensagemMedicamentoJnDAO;
	
	@Inject
	private AfaMensagemMedicamentoDAO afaMensagemMedicamentoDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private AfaGrupoMedicamentoDAO afaGrupoMedicamentoDAO;

	
	@Inject
	private AfaViaAdministracaoDAO afaViaAdministracaoDAO;
	
	@Inject
	private AfaViaAdmUnfDAO afaViaAdmUnfDAO;
	
	private static final long serialVersionUID = -7173127368443801380L;

	protected enum FarmaciaExceptionCode implements BusinessExceptionCode {
		RAP_00175, AFA_00169, AFA_00172, AFA_00173, AFA_00220, AFA_01707, AFA_01708,ERRO_UNIDADE_FUNCIONAL_OBRIGATORIA;
	}

	/**
	 * @ORADB Procedure AFAK_RN.RN_AFAC_VER_TP_VELOC
	 * 
	 * Verifica tipo de velocidade de administração ativa.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public boolean isTipoVelocidadeAtiva(Short seq) throws ApplicationBusinessException {
		DominioSituacao situacao = getAfaTipoVelocAdministracoesDAO()
				.obtemSituacaoTipoVelocidade(seq);

		if (situacao == null) {
			throw new ApplicationBusinessException(FarmaciaExceptionCode.AFA_00220);
		}

		return DominioSituacao.A.equals(situacao);
	}

	/**
	 * @ORADB AFAK_RN.RN_AFAP_VER_DEL
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificarDelecao(Date data) throws ApplicationBusinessException {
		AghParametros aghParametro = getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_DIAS_PERM_DEL_AFA);

		if (aghParametro != null && aghParametro.getVlrNumerico() != null) {
			float diff = CoreUtil.diferencaEntreDatasEmDias(Calendar
					.getInstance().getTime(), data);
			if (diff > aghParametro.getVlrNumerico().floatValue()) {
				throw new ApplicationBusinessException(FarmaciaExceptionCode.AFA_00172);
			}
		} else {
			throw new ApplicationBusinessException(FarmaciaExceptionCode.AFA_00173);
		}
	}

	protected AfaTipoVelocAdministracoesDAO getAfaTipoVelocAdministracoesDAO() {
		return afaTipoVelocAdministracoesDAO;
	}

	/**
	 * @ORADB Trigger AFAT_TVA_BRD
	 * 
	 * Verifica se o registro a ser deletedo está dentor do período de exclusão
	 * conforme parametro de sistema.
	 * 
	 * @param tipoVelocidadeAdministracao
	 * @throws ApplicationBusinessException
	 */
	private void preDeleteAfaTipoVelocAdministracoes(AfaTipoVelocAdministracoes tipoVelocidadeAdministracao) throws ApplicationBusinessException {
		this.getTipoVelocidadeAdministracaoRN().verificaDelecao(tipoVelocidadeAdministracao.getCriadoEm());
	}

	public void removerAfaTipoVelocAdministracoes(Short seq) throws ApplicationBusinessException {
		final AfaTipoVelocAdministracoes tipoVelocidadeAdministracao = getAfaTipoVelocAdministracoesDAO().obterPorChavePrimaria(seq);
		this.preDeleteAfaTipoVelocAdministracoes(tipoVelocidadeAdministracao);
		getAfaTipoVelocAdministracoesDAO().remover(tipoVelocidadeAdministracao);
	}

	public void removerAfaMensagemMedicamento(AfaMensagemMedicamento mensagemMedicamento) throws ApplicationBusinessException {
		
		List<AfaGrupoMedicamentoMensagem> gruposMedicamentosMensagem = mensagemMedicamento.getGruposMedicamentosMensagem();
		if (gruposMedicamentosMensagem != null && !gruposMedicamentosMensagem.isEmpty()) {
			AfaGrupoMedicamentoMensagemDAO afaGrupoMedicamentoMensagemDAO = this.getAfaGrupoMedicamentoMensagemDAO();
			
			for (AfaGrupoMedicamentoMensagem grupoMedicamentoMensagem : gruposMedicamentosMensagem) {
				afaGrupoMedicamentoMensagemDAO.remover(grupoMedicamentoMensagem);
			}
		}

		this.getAfaMensagemMedicamentoDAO().remover(mensagemMedicamento);
		this.posDeleteAfaMensagemMedicamento(mensagemMedicamento);
	}

	/**
	 * @ORADB Trigger AFAT_MEM_ARD
	 * 
	 * @param mensagemMedicamento
	 * @throws ApplicationBusinessException
	 */
	private void posDeleteAfaMensagemMedicamento(
			AfaMensagemMedicamento mensagemMedicamento)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AfaMensagemMedicamentoJn jn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.DEL, AfaMensagemMedicamentoJn.class, servidorLogado.getUsuario());
		
		jn.setSeq(mensagemMedicamento.getSeq());
		jn.setDescricao(mensagemMedicamento.getDescricao());
		jn.setCriadoEm(mensagemMedicamento.getCriadoEm());
		jn.setCoexistente(mensagemMedicamento.getCoexistente());
		jn.setSerMatricula(mensagemMedicamento.getServidor().getId()
				.getMatricula());
		jn.setSerVinCodigo(mensagemMedicamento.getServidor().getId()
				.getVinCodigo());
		jn.setSituacao(mensagemMedicamento.getSituacao());
		
		this.getAfaMensagemMedicamentoJnDAO().persistir(jn);
		this.getAfaMensagemMedicamentoJnDAO().flush();
	}

	public void inserirAfaMensagemMedicamento(
			AfaMensagemMedicamento mensagemMedicamento)
			throws ApplicationBusinessException {
		this.preInserirAfaMensagemMedicamento(mensagemMedicamento);
		this.getAfaMensagemMedicamentoDAO().persistir(mensagemMedicamento);
		this.getAfaMensagemMedicamentoDAO().flush();
	}

	public void atualizarAfaMensagemMedicamento(
			AfaMensagemMedicamento mensagemMedicamento)
			throws ApplicationBusinessException {
		this.getAfaMensagemMedicamentoDAO().desatachar(mensagemMedicamento);

		AfaMensagemMedicamento oldMensagemMedicamento = this
				.getAfaMensagemMedicamentoDAO().obterPorChavePrimaria(
						mensagemMedicamento.getSeq());
		this.getAfaMensagemMedicamentoDAO().desatachar(oldMensagemMedicamento);
		
		this.preAtualizarAfaMensagemMedicamento(mensagemMedicamento);
		this.getAfaMensagemMedicamentoDAO().atualizar(mensagemMedicamento);
		this.getAfaMensagemMedicamentoDAO().flush();
		this.posAtualizarAfaMensagemMedicamento(mensagemMedicamento,
				oldMensagemMedicamento);
	}

	/**
	 * @ORADB Trigger AFAT_MEM_BRI
	 * 
	 * @param mensagemMedicamento
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	private void preInserirAfaMensagemMedicamento(
			AfaMensagemMedicamento mensagemMedicamento)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		mensagemMedicamento.setCriadoEm(new Date());

		if (servidorLogado != null) {
			mensagemMedicamento.setServidor(servidorLogado);
		} else {
			throw new ApplicationBusinessException(FarmaciaExceptionCode.AFA_00169);
		}
	}

	/**
	 * @ORADB Trigger AFAT_MEM_BRU
	 * 
	 * @param mensagemMedicamento
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	private void preAtualizarAfaMensagemMedicamento(
			AfaMensagemMedicamento mensagemMedicamento)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (servidorLogado != null) {
			mensagemMedicamento.setServidor(servidorLogado);
		} else {
			throw new ApplicationBusinessException(FarmaciaExceptionCode.AFA_00169);
		}
	}

	/**
	 * @ORADB Trigger AFAT_MEM_ARU
	 * 
	 * @param newMensagemMedicamento
	 * @param oldMensagemMedicamento
	 * @throws ApplicationBusinessException
	 */
	private void posAtualizarAfaMensagemMedicamento(
			AfaMensagemMedicamento newMensagemMedicamento,
			AfaMensagemMedicamento oldMensagemMedicamento)
			throws ApplicationBusinessException {
		
		if (CoreUtil.modificados(newMensagemMedicamento.getSeq(),
				oldMensagemMedicamento.getSeq())
				|| CoreUtil.modificados(newMensagemMedicamento.getDescricao(),
						oldMensagemMedicamento.getDescricao())
				|| CoreUtil.modificados(newMensagemMedicamento.getCriadoEm(),
						oldMensagemMedicamento.getCriadoEm())
				|| CoreUtil.modificados(newMensagemMedicamento.getServidor(),
						oldMensagemMedicamento.getServidor())
				|| CoreUtil.modificados(
						newMensagemMedicamento.getCoexistente(),
						oldMensagemMedicamento.getCoexistente())
				|| CoreUtil.modificados(newMensagemMedicamento.getSituacao(),
						oldMensagemMedicamento.getSituacao())) {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			AfaMensagemMedicamentoJn jn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, AfaMensagemMedicamentoJn.class, servidorLogado != null ? servidorLogado.getUsuario() : null);
			
			jn.setSeq(oldMensagemMedicamento.getSeq());
			jn.setDescricao(oldMensagemMedicamento.getDescricao());
			jn.setCriadoEm(oldMensagemMedicamento.getCriadoEm());
			jn.setCoexistente(oldMensagemMedicamento.getCoexistente());
			jn.setSerMatricula(oldMensagemMedicamento.getServidor().getId()
					.getMatricula());
			jn.setSerVinCodigo(oldMensagemMedicamento.getServidor().getId()
					.getVinCodigo());
			jn.setSituacao(oldMensagemMedicamento.getSituacao());
			
			this.getAfaMensagemMedicamentoJnDAO().persistir(jn);
			this.getAfaMensagemMedicamentoJnDAO().flush();
			
		}
	}

	public void inserirAfaGrupoMedicamentoMensagem(
			AfaGrupoMedicamentoMensagem grupoMedicamentoMensagem)
			throws ApplicationBusinessException {
		this.preInserirAfaGrupoMedicamentoMensagem(grupoMedicamentoMensagem);
		this.getAfaGrupoMedicamentoMensagemDAO().persistir(
				grupoMedicamentoMensagem);
		this.getAfaGrupoMedicamentoMensagemDAO().flush();
	}

	public void atualizarAfaGrupoMedicamentoMensagem(
			AfaGrupoMedicamentoMensagem grupoMedicamentoMensagem)
			throws ApplicationBusinessException {
		this.preAtualizarAfaGrupoMedicamentoMensagem(grupoMedicamentoMensagem);
		this.getAfaGrupoMedicamentoMensagemDAO().merge(
				grupoMedicamentoMensagem);
		this.getAfaGrupoMedicamentoMensagemDAO().flush();
	}

	/**
	 * @ORADB Trigger AFAT_GME_BRI
	 * 
	 * @param grupoMedicamentoMensagem
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	private void preInserirAfaGrupoMedicamentoMensagem(
			AfaGrupoMedicamentoMensagem grupoMedicamentoMensagem)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		grupoMedicamentoMensagem.setCriadoEm(new Date());

		if (servidorLogado != null) {
			grupoMedicamentoMensagem.setServidor(servidorLogado);
		} else {
			throw new ApplicationBusinessException(FarmaciaExceptionCode.AFA_00169);
		}
	}

	/**
	 * @ORADB Trigger AFAT_GME_BRU
	 * 
	 * @param grupoMedicamentoMensagem
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	private void preAtualizarAfaGrupoMedicamentoMensagem(
			AfaGrupoMedicamentoMensagem grupoMedicamentoMensagem)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (servidorLogado != null) {
			grupoMedicamentoMensagem.setServidor(servidorLogado);
		} else {
			throw new ApplicationBusinessException(FarmaciaExceptionCode.AFA_00169);
		}
	}

	public void removerAfaGrupoMedicamento(AfaGrupoMedicamento grupoMedicamento)
			throws ApplicationBusinessException {
		List<AfaItemGrupoMedicamento> itensGrupoMedicamento = grupoMedicamento
				.getItensGruposMedicamento();
		if (itensGrupoMedicamento != null && !itensGrupoMedicamento.isEmpty()) {
			AfaItemGrupoMedicamentoDAO afaItemGrupoMedicamentoDAO = this
					.getAfaItemGrupoMedicamentoDAO();
			for (AfaItemGrupoMedicamento itemGrupoMedicamento : itensGrupoMedicamento) {
				this.preDeleteAfaItemGrupoMedicamento(itemGrupoMedicamento);
				afaItemGrupoMedicamentoDAO.remover(itemGrupoMedicamento);
				afaItemGrupoMedicamentoDAO.flush();
			}
		}

		this.preDeleteAfaGrupoMedicamento(grupoMedicamento);
		this.getAfaGrupoMedicamentoDAO().remover(grupoMedicamento);
		this.getAfaGrupoMedicamentoDAO().flush();
	}

	/**
	 * @ORADB Trigger AFAT_GMD_BRD
	 * 
	 * Verifica o período em que um registro pode ser deletado.
	 * 
	 * @param grupoMedicamento
	 * @throws ApplicationBusinessException
	 */
	private void preDeleteAfaGrupoMedicamento(
			AfaGrupoMedicamento grupoMedicamento) throws ApplicationBusinessException {
		this.verificarDelecao(grupoMedicamento.getCriadoEm());
	}

	/**
	 * @ORADB Trigger AFAT_IGM_BRD
	 * 
	 * Verifica o período em que um registro pode ser deletado.
	 * 
	 * @param itemGrupoMedicamento
	 * @throws ApplicationBusinessException
	 */
	private void preDeleteAfaItemGrupoMedicamento(
			AfaItemGrupoMedicamento itemGrupoMedicamento)
			throws ApplicationBusinessException {
		this.verificarDelecao(itemGrupoMedicamento.getCriadoEm());
	}

	public void inserirAfaGrupoMedicamento(AfaGrupoMedicamento grupoMedicamento)
			throws ApplicationBusinessException {
		this.preInserirAfaGrupoMedicamento(grupoMedicamento);
		this.getAfaGrupoMedicamentoDAO().persistir(grupoMedicamento);
		this.getAfaGrupoMedicamentoDAO().flush();
	}

	public void atualizarAfaGrupoMedicamento(
			AfaGrupoMedicamento grupoMedicamento) throws BaseException {

		this.preAtualizarAfaGrupoMedicamento(grupoMedicamento);
		
		this.getAfaGrupoMedicamentoDAO().atualizar(grupoMedicamento);
	}

	public void inserirAfaItemGrupoMedicamento(
			AfaItemGrupoMedicamento itemGrupoMedicamento)
			throws BaseException  {
		this.preInserirAfaItemGrupoMedicamento(itemGrupoMedicamento);
		this.getAfaItemGrupoMedicamentoDAO().persistir(itemGrupoMedicamento);
		this.getAfaItemGrupoMedicamentoDAO().flush();
	}

	public void atualizarAfaItemGrupoMedicamento(
			AfaItemGrupoMedicamento itemGrupoMedicamento)
			throws BaseException  {
		this.getAfaItemGrupoMedicamentoDAO().desatachar(itemGrupoMedicamento);

		AfaItemGrupoMedicamento oldItemGrupoMedicamento = this
				.getAfaItemGrupoMedicamentoDAO()
				.obterPorAfaItemGrupoMedicamento(
						itemGrupoMedicamento.getMedicamento().getMatCodigo(),
						itemGrupoMedicamento.getGrupoMedicamento().getSeq());
		this.getAfaItemGrupoMedicamentoDAO().desatachar(oldItemGrupoMedicamento);
		
		this.preAtualizarAfaItemGrupoMedicamento(itemGrupoMedicamento,
				oldItemGrupoMedicamento);
		this.getAfaItemGrupoMedicamentoDAO().merge(itemGrupoMedicamento);
		this.getAfaItemGrupoMedicamentoDAO().flush();
	}

	/**
	 * @ORADB Trigger AFAT_GMD_BRI
	 * 
	 * @param grupoMedicamento
	 * @throws ApplicationBusinessException
	 */
	private void preInserirAfaGrupoMedicamento(
			AfaGrupoMedicamento grupoMedicamento) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		grupoMedicamento.setCriadoEm(new Date());

		if (servidorLogado != null) {
			grupoMedicamento.setServidor(servidorLogado);
		} else {
			throw new ApplicationBusinessException(FarmaciaExceptionCode.RAP_00175);
		}
	}

	/**
	 * @ORADB Trigger AFAT_GMD_BRU
	 * 
	 * @param grupoMedicamento
	 * @throws ApplicationBusinessException
	 */
	private void preAtualizarAfaGrupoMedicamento(
			AfaGrupoMedicamento newGrupoMedicamento)
			throws BaseException  {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		if (servidorLogado != null) {
			newGrupoMedicamento.setServidor(servidorLogado);
		} else {
			throw new ApplicationBusinessException(FarmaciaExceptionCode.RAP_00175);
		}
	}

	/**
	 * @ORADB Trigger AFAT_IGM_BRI
	 * 
	 * @param itemGrupoMedicamento
	 * @throws ApplicationBusinessException
	 */
	private void preInserirAfaItemGrupoMedicamento(
			AfaItemGrupoMedicamento itemGrupoMedicamento)
			throws BaseException  {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		itemGrupoMedicamento.setCriadoEm(new Date());

		// Verifica se o grupo de medicamentos e item grupo medicamento estão
		// ativos
		this.getItemGrupoMedicamentoRN().verificaMedicamento(
				itemGrupoMedicamento.getGrupoMedicamento(),
				itemGrupoMedicamento.getMedicamento());

		if (servidorLogado != null) {
			itemGrupoMedicamento.setServidor(servidorLogado);
		} else {
			throw new ApplicationBusinessException(FarmaciaExceptionCode.RAP_00175);
		}
	}

	/**
	 * @ORADB Trigger AFAT_IGM_BRU
	 * 
	 * @param itemGrupoMedicamento
	 * @throws ApplicationBusinessException
	 */
	private void preAtualizarAfaItemGrupoMedicamento(
			AfaItemGrupoMedicamento newItemGrupoMedicamento,
			AfaItemGrupoMedicamento oldItemGrupoMedicamento)
			throws BaseException  {
		// Verifica se o grupo de medicamentos e item grupo medicamento estão
		// ativos
		if (oldItemGrupoMedicamento!=null &&
				CoreUtil.modificados(newItemGrupoMedicamento.getGrupoMedicamento()
				.getSeq(), oldItemGrupoMedicamento.getGrupoMedicamento()
				.getSeq())
				|| CoreUtil
						.modificados(newItemGrupoMedicamento.getMedicamento()
								.getMatCodigo(), oldItemGrupoMedicamento
								.getMedicamento().getMatCodigo())) {
			this.getItemGrupoMedicamentoRN().verificaMedicamento(
					newItemGrupoMedicamento.getGrupoMedicamento(),
					newItemGrupoMedicamento.getMedicamento());
		}

		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (servidorLogado != null) {
			newItemGrupoMedicamento.setServidor(servidorLogado);
		} else {
			throw new ApplicationBusinessException(FarmaciaExceptionCode.RAP_00175);
		}
	}
	
	public List<AfaViaAdmUnfVO> listarViaAdmUnfVO(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			AghUnidadesFuncionais unidFuncionais){
		List<AfaViaAdmUnfVO> afaViaAdmUnfVOTmp = new ArrayList<AfaViaAdmUnfVO>();
		
		List<AghUnidadesFuncionais> listUnidadeUnf = this.getAghuFacade().pesquisarUnidadesFuncionais();
		
		AghUnidadesFuncionaisVO voUnid = new AghUnidadesFuncionaisVO();
		voUnid.setSeq(unidFuncionais.getSeq());
		
		List<AfaViaAdmUnf> afaViaAdmUnfTmp = getAfaViaAdmUnfDAO().listarAfaViaAdmUnf(firstResult, maxResult, orderProperty, asc, voUnid);
		for (AfaViaAdmUnf afaViaAdmUnf : afaViaAdmUnfTmp) {
			AfaViaAdmUnfVO afaViaAdmUnfVO = new AfaViaAdmUnfVO();
			afaViaAdmUnfVO.setAfaViaAdmUnf(afaViaAdmUnf);
			for (AghUnidadesFuncionais unidadeFuncionalTmp : listUnidadeUnf) {
				if(unidadeFuncionalTmp.getSeq().equals(afaViaAdmUnf.getId().getUnfSeq())){
					afaViaAdmUnfVO.setUnidadeFuncional(unidadeFuncionalTmp);
				}
			}
			afaViaAdmUnfVOTmp.add(afaViaAdmUnfVO);
		}
		return afaViaAdmUnfVOTmp;
		
	}

	public void excluirRN(AfaViaAdmUnfVO viaVinculadoUnidade,
			RapServidores servidor) throws ApplicationBusinessException  {
		AfaViaAdmUnf afaViaAdmUnf = getAfaViaAdmUnfDAO().obterPorChavePrimaria(viaVinculadoUnidade.getAfaViaAdmUnf().getId());
		getAfaViaAdmUnfDAO().remover(afaViaAdmUnf);
		getAfaViaAdmUnfDAO().flush();
	}	

	public void incluirTodasViasUnf(AghUnidadesFuncionais unidFuncionais,
			RapServidores servidor) throws ApplicationBusinessException {
		List<AfaViaAdmUnf> listAfaViaAdmUnfFinalTmp = getAfaViaAdmUnfDAO().listarTodos();
		List<AfaViaAdmUnf> listAfaViaAdmUnTmp = new ArrayList<AfaViaAdmUnf>();
		List<AfaViaAdministracao> listaAfaViaAdministracao = this.getAfaViaAdministracaoDAO().obterViaAdminstracaoSiglaouDescricao(null);
		List<AghUnidadesFuncionais> listaUnidadeUnf = this.getAghuFacade().pesquisarUnidadesFuncionais();
		for ( AghUnidadesFuncionais aghUnidadesFuncionaiTmp : listaUnidadeUnf) {
			for (AfaViaAdministracao afaViaAdministracaoTmp : listaAfaViaAdministracao) {
				AfaViaAdmUnf afaViaAdmUnfTmp = new AfaViaAdmUnf();
				AfaViaAdmUnfId AfaViaAdmUnfIdTmp = new AfaViaAdmUnfId(); 
				
				afaViaAdmUnfTmp.setCriadoEm(new Date());
				AfaViaAdmUnfIdTmp.setUnfSeq(aghUnidadesFuncionaiTmp.getSeq());
				AfaViaAdmUnfIdTmp.setVadSigla(afaViaAdministracaoTmp.getSigla());
				afaViaAdmUnfTmp.setId(AfaViaAdmUnfIdTmp);
				afaViaAdmUnfTmp.setIndSituacao(DominioSituacao.A);
				afaViaAdmUnfTmp.setServidor(servidor);
				afaViaAdmUnfTmp.setViaAdministracao(afaViaAdministracaoTmp);
				
				if(excluirDado( afaViaAdmUnfTmp,listAfaViaAdmUnfFinalTmp)){
					listAfaViaAdmUnTmp.add(afaViaAdmUnfTmp);
				}
			}
		}
		
		if(listAfaViaAdmUnTmp.size()!=0){
			for (AfaViaAdmUnf afaViaAdmUnfTmp : listAfaViaAdmUnTmp) {
				getAfaViaAdmUnfDAO().persistir(afaViaAdmUnfTmp);
				getAfaViaAdmUnfDAO().flush();
			}
		} else {
			throw new ApplicationBusinessException(
					FarmaciaExceptionCode.AFA_01708);
		}
	}

	public void incluirViasUnfs(AghUnidadesFuncionais unidFuncionais,
			RapServidores servidor) throws ApplicationBusinessException {
		if (unidFuncionais == null) {
			throw new ApplicationBusinessException(
					FarmaciaExceptionCode.ERRO_UNIDADE_FUNCIONAL_OBRIGATORIA);
		}
		
		List<AfaViaAdmUnf> listAfaViaAdmUnfFinalTmp = getAfaViaAdmUnfDAO().listarAfaViaAdmUnfPorUnidadeFuncional(unidFuncionais);
		List<AfaViaAdmUnf> listAfaViaAdmUnTmp = new ArrayList<AfaViaAdmUnf>();
		List<AfaViaAdministracao> listaAfaViaAdministracao = this.getAfaViaAdministracaoDAO().obterViaAdminstracaoSiglaouDescricao(null);
		for (AfaViaAdministracao afaViaAdministracaoTmp : listaAfaViaAdministracao) {
			AfaViaAdmUnf afaViaAdmUnfTmp = new AfaViaAdmUnf();
			AfaViaAdmUnfId AfaViaAdmUnfIdTmp = new AfaViaAdmUnfId(); 
			
			afaViaAdmUnfTmp.setCriadoEm(new Date());
			AfaViaAdmUnfIdTmp.setUnfSeq(unidFuncionais.getSeq());
			AfaViaAdmUnfIdTmp.setVadSigla(afaViaAdministracaoTmp.getSigla());
			afaViaAdmUnfTmp.setId(AfaViaAdmUnfIdTmp);
			afaViaAdmUnfTmp.setIndSituacao(DominioSituacao.A);
			afaViaAdmUnfTmp.setServidor(servidor);
			afaViaAdmUnfTmp.setViaAdministracao(afaViaAdministracaoTmp);
			
			if(excluirDado( afaViaAdmUnfTmp,listAfaViaAdmUnfFinalTmp)){
				listAfaViaAdmUnTmp.add(afaViaAdmUnfTmp);
			}
		}
		
		if(listAfaViaAdmUnTmp.size()!=0){
			for (AfaViaAdmUnf afaViaAdmUnfTmp : listAfaViaAdmUnTmp) {
				getAfaViaAdmUnfDAO().persistir(afaViaAdmUnfTmp);
				getAfaViaAdmUnfDAO().flush();
			}
		} else {
			throw new ApplicationBusinessException(
					FarmaciaExceptionCode.AFA_01707);
		}
	}

	private boolean excluirDado(AfaViaAdmUnf afaViaAdmUnfTmp,
			List<AfaViaAdmUnf> listAfaViaAdmUnfFinalTmp) {
		
		for (AfaViaAdmUnf afaViaAdmUnf : listAfaViaAdmUnfFinalTmp) {
			if(afaViaAdmUnf.equals(afaViaAdmUnfTmp)){
				return false;
			}
		}
		
		return true;
	}
	
	protected TipoVelocidadeAdministracaoRN getTipoVelocidadeAdministracaoRN() {
		return tipoVelocidadeAdministracaoRN;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected AfaMensagemMedicamentoDAO getAfaMensagemMedicamentoDAO() {
		return afaMensagemMedicamentoDAO;
	}

	protected AfaGrupoMedicamentoMensagemDAO getAfaGrupoMedicamentoMensagemDAO() {
		return afaGrupoMedicamentoMensagemDAO;
	}

	protected AfaMensagemMedicamentoJnDAO getAfaMensagemMedicamentoJnDAO() {
		return afaMensagemMedicamentoJnDAO;
	}

	protected AfaItemGrupoMedicamentoDAO getAfaItemGrupoMedicamentoDAO() {
		return afaItemGrupoMedicamentoDAO;
	}

	protected AfaGrupoMedicamentoDAO getAfaGrupoMedicamentoDAO() {
		return afaGrupoMedicamentoDAO;
	}

	protected ItemGrupoMedicamentoRN getItemGrupoMedicamentoRN() {
		return itemGrupoMedicamentoRN;
	}

	protected GrupoMedicamentoRN getGrupoMedicamentoRN() {
		return grupoMedicamentoRN;
	}
	
	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
	protected AfaViaAdministracaoDAO getAfaViaAdministracaoDAO(){
		return afaViaAdministracaoDAO;
	}
	
	protected AfaViaAdmUnfDAO getAfaViaAdmUnfDAO(){
		return afaViaAdmUnfDAO;
	}
}
