package br.gov.mec.aghu.farmacia.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.farmacia.dao.AfaTipoVelocAdministracoesDAO;
import br.gov.mec.aghu.model.AfaTipoVelocAdministracoes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.TipoOperacaoEnum;

/**
 * @ORADB AFAK_TVA_RN
 */
@Stateless
public class TipoVelocidadeAdministracaoRN extends BaseBusiness {

	@EJB
	private FarmaciaRN farmaciaRN;
	
	private static final Log LOG = LogFactory.getLog(TipoVelocidadeAdministracaoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AfaTipoVelocAdministracoesDAO afaTipoVelocAdministracoesDAO;
	
	@EJB
	private IAghuFacade aghuFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 7821212495768886979L;

	public enum TipoVelocidadeAdministracaoRNExceptionCode implements
			BusinessExceptionCode {
		AFA_00169, AFA_00216, AFA_00217, AFA_00218, AFA_00219
	}

	/**
	 * @ORADB Function AFAK_TVA_RN.RN_TVAP_VER_IND_USUL
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificaUsoSoroterapiaNutricaoParental(
			Short seqTipoVelocidadeAdmnistracao,
			Boolean indTipoUsualSoroterapia, Boolean indTipoUsualNpt)
			throws ApplicationBusinessException {
		List<AfaTipoVelocAdministracoes> tiposVelocidadeAdministacao = getAfaTipoVelocAdministracoesDAO()
				.cursorVerificaUsoSoroterapiaNutricaoParental(
						seqTipoVelocidadeAdmnistracao);

		if (tiposVelocidadeAdministacao != null
				&& !tiposVelocidadeAdministacao.isEmpty()) {
			for (AfaTipoVelocAdministracoes tipoVelocidadeAdministracao : tiposVelocidadeAdministacao) {
				if (tipoVelocidadeAdministracao.getIndTipoUsualSoroterapia()
						&& Boolean.TRUE.equals(indTipoUsualSoroterapia)) {
					// Já existe tipo velocidade administração com
					// ind_tipo_usual_soroterapia = 'S'
					throw new ApplicationBusinessException(
							TipoVelocidadeAdministracaoRNExceptionCode.AFA_00219);
				} else if (tipoVelocidadeAdministracao.getIndTipoUsualNpt()
						&& Boolean.TRUE.equals(indTipoUsualNpt)) {
					// Já existe tipo velocidade administração com
					// ind_tipo_usual_npt = 'S'
					throw new ApplicationBusinessException(
							TipoVelocidadeAdministracaoRNExceptionCode.AFA_00218);
				}
			}
		}
	}

	/**
	 * @ORADB Function AFAK_TVA_RN.RN_TVAP_VER_INATIVO
	 * 
	 * Ocorrência inativa não pode sofrer alterações.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificaInativo() throws ApplicationBusinessException {
		throw new ApplicationBusinessException(
				TipoVelocidadeAdministracaoRNExceptionCode.AFA_00217);
	}

	/**
	 * @ORADB Function AFAK_TVA_RN.RN_TVAP_VER_DESCR
	 * 
	 * A descricao não pode alterada.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificaDescricao() throws ApplicationBusinessException {
		throw new ApplicationBusinessException(TipoVelocidadeAdministracaoRNExceptionCode.AFA_00216);
	}

	/**
	 * @ORADB Function AFAK_TVA_RN.RN_TVAP_VER_DELECAO
	 * 
	 * Verifica se o registro pode ser deletado levando em conta o período
	 * limite de deleção conforme parametro de sistema.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificaDelecao(Date data) throws ApplicationBusinessException {
		getFarmaciaRN().verificarDelecao(data);
	}

	/**
	 * @ORADB Trigger AFA_TIPO_VELOC_ADMINISTRACOES
	 * 
	 * @param newTipoVelocidadeAdministracao
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	private void preInserirAfaTipoVelocAdministracoes( AfaTipoVelocAdministracoes newTipoVelocidadeAdministracao)throws ApplicationBusinessException {
		
		newTipoVelocidadeAdministracao.setCriadoEm(new Date());

		if (newTipoVelocidadeAdministracao.getIndTipoUsualNpt()
				|| newTipoVelocidadeAdministracao.getIndTipoUsualSoroterapia()) {
			this.verificaUsoSoroterapiaNutricaoParental( newTipoVelocidadeAdministracao.getSeq(), 
														 newTipoVelocidadeAdministracao.getIndTipoUsualSoroterapia(),
														 newTipoVelocidadeAdministracao.getIndTipoUsualNpt());
		}

		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		// Dados do servidor que digita
		if (servidorLogado != null) {
			newTipoVelocidadeAdministracao.setServidor(servidorLogado);
		} else {
			throw new ApplicationBusinessException(TipoVelocidadeAdministracaoRNExceptionCode.AFA_00169);
		}
	}

	/**
	 * @ORADB Trigger AFAT_TVA_BRU
	 * 
	 * @param newTipoVelocidadeAdministracao
	 * @param oldTipoVelocidadeAdministracao
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	private void preAtualizarAfaTipoVelocAdministracoes( AfaTipoVelocAdministracoes newTipoVelocidadeAdministracao, AfaTipoVelocAdministracoes oldTipoVelocidadeAdministracao) throws ApplicationBusinessException {
		if (DominioSituacao.I.equals(newTipoVelocidadeAdministracao.getIndSituacao()) && 
				(CoreUtil.modificados(newTipoVelocidadeAdministracao.getDescricao(), oldTipoVelocidadeAdministracao.getDescricao()) || 
					CoreUtil.modificados(newTipoVelocidadeAdministracao.getSeq(), oldTipoVelocidadeAdministracao.getSeq()) || 
					CoreUtil.modificados(newTipoVelocidadeAdministracao.getServidor(), oldTipoVelocidadeAdministracao.getServidor()) || 
					CoreUtil.modificados(newTipoVelocidadeAdministracao.getCriadoEm(), oldTipoVelocidadeAdministracao.getCriadoEm()) || 
					CoreUtil.modificados(newTipoVelocidadeAdministracao.getIndTipoUsualNpt(), oldTipoVelocidadeAdministracao.getIndTipoUsualNpt()) || 
					CoreUtil.modificados(newTipoVelocidadeAdministracao.getIndTipoUsualSoroterapia(),oldTipoVelocidadeAdministracao.getIndTipoUsualSoroterapia()))) {
			this.verificaInativo();
		}

		if (!newTipoVelocidadeAdministracao.getDescricao().equalsIgnoreCase(oldTipoVelocidadeAdministracao.getDescricao())) {
			this.verificaDescricao();
		}

		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		// Dados do servidor que digita
		if (servidorLogado != null) {
			newTipoVelocidadeAdministracao.setServidor(servidorLogado);
		} else {
			throw new ApplicationBusinessException(TipoVelocidadeAdministracaoRNExceptionCode.AFA_00169);
		}
	}

	/**
	 * @ORADB Trigger AFAT_TVA_ARU
	 * 
	 * @param newTipoVelocidadeAdministracao
	 * @param oldTipoVelocidadeAdministracao
	 * @throws ApplicationBusinessException
	 */
	private void posAtualizarAfaTipoVelocAdministracoes( AfaTipoVelocAdministracoes newTipoVelocidadeAdministracao, AfaTipoVelocAdministracoes oldTipoVelocidadeAdministracao) throws ApplicationBusinessException {
		if (CoreUtil.modificados(newTipoVelocidadeAdministracao.getSeq(),oldTipoVelocidadeAdministracao.getSeq()) || 
				CoreUtil.modificados(newTipoVelocidadeAdministracao.getIndTipoUsualNpt(), oldTipoVelocidadeAdministracao.getIndTipoUsualNpt()) || 
				CoreUtil.modificados(newTipoVelocidadeAdministracao.getIndTipoUsualSoroterapia(),oldTipoVelocidadeAdministracao.getIndTipoUsualSoroterapia()) || 
				CoreUtil.modificados(newTipoVelocidadeAdministracao.getIndSituacao(), oldTipoVelocidadeAdministracao.getIndSituacao())) {
			this.enforceAfaTipoVelocAdministracoes(newTipoVelocidadeAdministracao, oldTipoVelocidadeAdministracao, TipoOperacaoEnum.UPDATE);
		}
	}

	/**
	 * @ORADB AFAP_ENFORCE_TVA_RULES
	 * 
	 * @param newTipoVelocidadeAdministracao
	 * @param oldTipoVelocidadeAdministracao
	 * @param operacao
	 * @throws ApplicationBusinessException
	 */
	private void enforceAfaTipoVelocAdministracoes(
			AfaTipoVelocAdministracoes newTipoVelocidadeAdministracao,
			AfaTipoVelocAdministracoes oldTipoVelocidadeAdministracao,
			TipoOperacaoEnum operacao) throws ApplicationBusinessException {
		if (TipoOperacaoEnum.UPDATE.equals(operacao)) {
			if ((CoreUtil.modificados(newTipoVelocidadeAdministracao.getIndTipoUsualNpt(), oldTipoVelocidadeAdministracao.getIndTipoUsualNpt()) && 
					newTipoVelocidadeAdministracao.getIndTipoUsualNpt()) || 
					(CoreUtil.modificados(newTipoVelocidadeAdministracao.getIndTipoUsualSoroterapia(), oldTipoVelocidadeAdministracao.getIndTipoUsualSoroterapia()) && 
							newTipoVelocidadeAdministracao.getIndTipoUsualSoroterapia())) {
				this.verificaUsoSoroterapiaNutricaoParental(newTipoVelocidadeAdministracao.getSeq(),
						newTipoVelocidadeAdministracao.getIndTipoUsualSoroterapia(),
						newTipoVelocidadeAdministracao.getIndTipoUsualNpt());
			}

			if (CoreUtil.modificados(newTipoVelocidadeAdministracao.getIndSituacao(), oldTipoVelocidadeAdministracao.getIndSituacao()) && 
					DominioSituacao.A.equals(newTipoVelocidadeAdministracao.getIndSituacao()) && 
					(newTipoVelocidadeAdministracao.getIndTipoUsualSoroterapia() || newTipoVelocidadeAdministracao.getIndTipoUsualNpt())) {
				this.verificaUsoSoroterapiaNutricaoParental(newTipoVelocidadeAdministracao.getSeq(), newTipoVelocidadeAdministracao.getIndTipoUsualSoroterapia(),
						newTipoVelocidadeAdministracao.getIndTipoUsualNpt());
			}
		}
	}

	public void inserirAfaTipoVelocAdministracoes(
			AfaTipoVelocAdministracoes tipoVelocidadeAdministracao)
			throws ApplicationBusinessException {
		this.preInserirAfaTipoVelocAdministracoes(tipoVelocidadeAdministracao);
		this.getAfaTipoVelocAdministracoesDAO().persistir(
				tipoVelocidadeAdministracao);
		this.getAfaTipoVelocAdministracoesDAO().flush();
		this.enforceAfaTipoVelocAdministracoes(tipoVelocidadeAdministracao,
				null, TipoOperacaoEnum.INSERT);
	}

	public void atualizarAfaTipoVelocAdministracoes(AfaTipoVelocAdministracoes tipoVelocidadeAdministracao) throws ApplicationBusinessException {

		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		tipoVelocidadeAdministracao.setServidor(servidorLogado);
		
		AfaTipoVelocAdministracoes oldTipoVelocidadeAdministracao = this.getAfaTipoVelocAdministracoesDAO().obterOriginal(tipoVelocidadeAdministracao);

		this.preAtualizarAfaTipoVelocAdministracoes(tipoVelocidadeAdministracao, oldTipoVelocidadeAdministracao);
		
		this.getAfaTipoVelocAdministracoesDAO().atualizar(tipoVelocidadeAdministracao);
		
		this.posAtualizarAfaTipoVelocAdministracoes(tipoVelocidadeAdministracao, oldTipoVelocidadeAdministracao);
	}

	protected FarmaciaRN getFarmaciaRN() {
		return farmaciaRN;
	}

	protected AfaTipoVelocAdministracoesDAO getAfaTipoVelocAdministracoesDAO() {
		return afaTipoVelocAdministracoesDAO;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
