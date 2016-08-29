package br.gov.mec.aghu.farmacia.cadastroapoio.business;

import java.util.Calendar;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.business.exception.FarmaciaExceptionCode;
import br.gov.mec.aghu.model.AfaGrupoUsoMedicamento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * Regras de negócio da tabela AFA_GRUPO_USO_MDTOS
 * 
 * @author lcmoura
 * 
 */
@Stateless
public class GrupoUsoMedicamentoRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(GrupoUsoMedicamentoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IFarmaciaFacade farmaciaFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1545353440964938436L;

	protected IFarmaciaFacade getFarmaciaFacade() {
		return this.farmaciaFacade;
	}

	/**
	 * @ORADB Trigger AFAT_GUP_BRI</br>
	 * 
	 * Passa a descrição para maiúsculo</br> Seta o usuário logado no
	 * sistema</br> Seta a data de criação
	 * 
	 * @param grupoUsoMedicamento
	 * @throws ApplicationBusinessException
	 *             AFA-00169 | Não existe Servidor com este Vínculo e Matrícula
	 * @throws ApplicationBusinessException 
	 */
	public void preInsertGrupoUsoMedicamento(
			AfaGrupoUsoMedicamento grupoUsoMedicamento)
			throws ApplicationBusinessException {

		// Seta o servidor
		this.setServidor(grupoUsoMedicamento);

		// Seta a data de criação
		grupoUsoMedicamento.setCriadoEm(Calendar.getInstance().getTime());
	}

	/**
	 * @ORADB Trigger AFAT_GUP_BRU</br>
	 * 
	 * Seta o usuário logado no sistema.</br> Verifica se a descrição ou
	 * responsavel foram alterados.
	 * 
	 * @param grupoUsoMedicamentoOld
	 *            estado anterior da entidade
	 * @param grupoUsoMedicamento
	 *            estado atual da entidade
	 * @throws ApplicationBusinessException
	 *             AFA-00169 | Não existe Servidor com este Vínculo e Matrícula
	 * @throws ApplicationBusinessException
	 *             AFA-00175 | Descrição e o Responsável pela avaliação do grupo
	 *             de uso do medicamento não podem ser alterados
	 * @throws ApplicationBusinessException 
	 */
	public void preUpdateGrupoUsoMedicamento(
			AfaGrupoUsoMedicamento grupoUsoMedicamentoOld,
			AfaGrupoUsoMedicamento grupoUsoMedicamento)
			throws ApplicationBusinessException {

		// Seta o servidor
		this.setServidor(grupoUsoMedicamento);

		// valida alteração
		if (!CoreUtil.igual(grupoUsoMedicamentoOld.getDescricao(),
				grupoUsoMedicamento.getDescricao())
				|| !CoreUtil.igual(grupoUsoMedicamentoOld
						.getIndResponsavelAvaliacao(), grupoUsoMedicamento
						.getIndResponsavelAvaliacao())) {

			throw new ApplicationBusinessException(FarmaciaExceptionCode.AFA_00175);
		}
	}

	/**
	 * @ORADB Trigger AFAT_GUP_BRD</br>
	 * 
	 * Verificar se o registro a ser deletado está dentro do período de exclusão
	 * conforme parametro de sistema
	 * 
	 * @param grupoUsoMedicamento
	 */
	public void preDeleteGrupoUsoMedicamento(
			AfaGrupoUsoMedicamento grupoUsoMedicamento)
			throws BaseException {

		getFarmaciaFacade().verificarDelecao(grupoUsoMedicamento.getCriadoEm());
	}

	/**
	 * Seta o usuário logado no sistema
	 * 
	 * @param grupoUsoMedicamento
	 * @throws ApplicationBusinessException
	 *             AFA-00169 | Não existe Servidor com este Vínculo e Matrícula.
	 * @throws ApplicationBusinessException 
	 */
	private void setServidor(AfaGrupoUsoMedicamento grupoUsoMedicamento)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (servidorLogado == null) {
			throw new ApplicationBusinessException(FarmaciaExceptionCode.AFA_00169);
		}
		grupoUsoMedicamento.setServidor(servidorLogado);
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
