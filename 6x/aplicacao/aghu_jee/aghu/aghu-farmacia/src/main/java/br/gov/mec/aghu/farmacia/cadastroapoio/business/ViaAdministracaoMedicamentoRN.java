package br.gov.mec.aghu.farmacia.cadastroapoio.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.AbstractAGHUCrudRn;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.farmacia.business.exception.FarmaciaExceptionCode;
import br.gov.mec.aghu.farmacia.dao.AfaViaAdministracaoMedicamentoJNDAO;
import br.gov.mec.aghu.model.AfaViaAdministracaoMedicamento;
import br.gov.mec.aghu.model.AfaViaAdministracaoMedicamentoJN;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

/**
 * 
 * <p>
 * Triggers de:<br/>
 * @ORADB: <code>AFA_VIA_ADM_MDTOS</code>
 * </p>
 * @author gandriotti
 *
 */
@SuppressWarnings({"PMD.HierarquiaONRNIncorreta"})
@Stateless
public class ViaAdministracaoMedicamentoRN
		extends AbstractAGHUCrudRn<AfaViaAdministracaoMedicamento> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 112530678424738246L;

	private static final Log LOG = LogFactory.getLog(ViaAdministracaoMedicamentoRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}		

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private AfaViaAdministracaoMedicamentoJNDAO afaViaAdministracaoMedicamentoJNDAO;
	
	/**
	 * BEFORE ROW INSERT Atribui usuario e data de criacao da Via; testa se a
	 * Via esta ativa; verifica se Via permite bomba de infusao.
	 * 
	 * @param viaAdministracaoMedicamento
	 *            Via
	 * @return true se a condicao de pre insercao foi satisfeita; caso contrario
	 *         retorna false.
	 */
	@Override
	public boolean briPreInsercaoRow(final AfaViaAdministracaoMedicamento viaAdministracaoMedicamento, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {

		this.briEbru(viaAdministracaoMedicamento);

		viaAdministracaoMedicamento.setCriadoEm(new Date());
		if (!viaAdministracaoMedicamento.getSituacao().isAtivo()) {
			throw new ApplicationBusinessException(FarmaciaExceptionCode.AFA_00185);
		}

		return true;
	}

	/**
	 * BEFORE ROW UPDATE Atribui usuario; verifica se via permite bomba de
	 * infusao.
	 * 
	 * @param viaAdministracaoMedicamentoOld
	 *            Via com valores iniciais
	 * @param viaAdministracaoMedicamento
	 *            Via com valores modificados
	 * @return true se a condicao de pre atualizacao foi satisfeita; caso
	 *         contrario retorna false.
	 */
	@Override
	public boolean bruPreAtualizacaoRow(
			AfaViaAdministracaoMedicamento viaAdministracaoMedicamentoOld,
			AfaViaAdministracaoMedicamento viaAdministracaoMedicamento,
			String nomeMicrocomputador,
			Date dataFimVinculoServidor) throws BaseException {

		this.briEbru(viaAdministracaoMedicamento);

		return true;
	}

	private void briEbru(final AfaViaAdministracaoMedicamento viaAdministracaoMedicamento) throws ApplicationBusinessException {

		this.setServidor(viaAdministracaoMedicamento);
		this.verificarPermiteBI(viaAdministracaoMedicamento);
	}

	/**
	 * AFTER ROW UPDATE Testa se realmente foram feitas modificacoes nos valores
	 * da Via; se sim, insere na journal.
	 * 
	 * @param viaAdministracaoMedicamentoOld
	 *            Via com valores iniciais
	 * @param viaAdministracaoMedicamento
	 *            Via com valores modificados
	 * @return true se a condicao de pos atualizacao foi satisfeita; caso
	 *         contrario retorna false.
	 */
	@Override
	public boolean aruPosAtualizacaoRow(AfaViaAdministracaoMedicamento viaAdministracaoMedicamentoOld, 
			AfaViaAdministracaoMedicamento viaAdministracaoMedicamento,
			String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException {
	
		if (!CoreUtil.igual(viaAdministracaoMedicamentoOld.getServidor(),
				viaAdministracaoMedicamento.getServidor())
				|| !CoreUtil.igual(viaAdministracaoMedicamentoOld.getCriadoEm(),
						viaAdministracaoMedicamento.getCriadoEm())
				|| !CoreUtil.igual(
						viaAdministracaoMedicamentoOld.getCriadoEm(),
						viaAdministracaoMedicamento.getCriadoEm())
				|| !CoreUtil.igual(
						viaAdministracaoMedicamentoOld.getSituacao(),
						viaAdministracaoMedicamento.getSituacao())
				|| !CoreUtil.igual(
						viaAdministracaoMedicamentoOld.getDefaultBi(),
						viaAdministracaoMedicamento.getDefaultBi())
				|| !CoreUtil.igual(
						viaAdministracaoMedicamentoOld.getPermiteBi(),
						viaAdministracaoMedicamento.getPermiteBi())) {

			AfaViaAdministracaoMedicamentoJN viaAdministracaoMedicamentoJN = this.criarJournal(
					viaAdministracaoMedicamentoOld,
					DominioOperacoesJournal.UPD);
			this.insertViaAdministracaoMedicamentoJN(viaAdministracaoMedicamentoJN);
		}

		return true;
	}

	public IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected void setServidor(final AfaViaAdministracaoMedicamento viaAdministracaoMedicamento) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (servidorLogado == null) {
			throw new ApplicationBusinessException(FarmaciaExceptionCode.AFA_00169);
		}
		viaAdministracaoMedicamento.setServidor(servidorLogado);
	}

	protected AfaViaAdministracaoMedicamentoJN criarJournal(final AfaViaAdministracaoMedicamento viaAdministracaoMedicamentoOld,
			final DominioOperacoesJournal operacao) {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		AfaViaAdministracaoMedicamentoJN viaAdministracaoMedicamentoJN = BaseJournalFactory.getBaseJournal(
				operacao,
				AfaViaAdministracaoMedicamentoJN.class, servidorLogado.getUsuario());

		viaAdministracaoMedicamentoJN.setMedMatCodigo(viaAdministracaoMedicamentoOld.getId().getMedMatCodigo());
		viaAdministracaoMedicamentoJN.setVadSigla(viaAdministracaoMedicamentoOld.getId().getVadSigla());
		viaAdministracaoMedicamentoJN.setServidor(viaAdministracaoMedicamentoOld.getServidor());
		viaAdministracaoMedicamentoJN.setCriadoEm(viaAdministracaoMedicamentoOld.getCriadoEm());
		viaAdministracaoMedicamentoJN.setIndSituacao(viaAdministracaoMedicamentoOld.getSituacao());
		viaAdministracaoMedicamentoJN.setPermiteBI(viaAdministracaoMedicamentoOld.getPermiteBi());
		viaAdministracaoMedicamentoJN.setDefaultBI(viaAdministracaoMedicamentoOld.getDefaultBi());

		return viaAdministracaoMedicamentoJN;
	}

	protected AfaViaAdministracaoMedicamentoJN insertViaAdministracaoMedicamentoJN(final AfaViaAdministracaoMedicamentoJN viaAdministracaoMedicamentoJN) throws ApplicationBusinessException {

		this.getAfaViaAdministracaoMedicamentoJNDAO().persistir(
				viaAdministracaoMedicamentoJN);
		this.getAfaViaAdministracaoMedicamentoJNDAO().flush();
		return viaAdministracaoMedicamentoJN;
	}

	protected void verificarPermiteBI(final AfaViaAdministracaoMedicamento viaAdministracaoMedicamento) throws ApplicationBusinessException {

		if (Boolean.TRUE.equals(viaAdministracaoMedicamento.getPermiteBi())
				&& Boolean.FALSE.equals(viaAdministracaoMedicamento.getViaAdministracao().getIndPermiteBi())) {
			throw new ApplicationBusinessException(FarmaciaExceptionCode.AFA_01446);
		}

		if (Boolean.FALSE.equals(viaAdministracaoMedicamento.getPermiteBi())
				&& Boolean.TRUE.equals(viaAdministracaoMedicamento.getDefaultBi())) {
			throw new ApplicationBusinessException(FarmaciaExceptionCode.AFA_01444);
		}
	}

	protected AfaViaAdministracaoMedicamentoJNDAO getAfaViaAdministracaoMedicamentoJNDAO() {

		return afaViaAdministracaoMedicamentoJNDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
