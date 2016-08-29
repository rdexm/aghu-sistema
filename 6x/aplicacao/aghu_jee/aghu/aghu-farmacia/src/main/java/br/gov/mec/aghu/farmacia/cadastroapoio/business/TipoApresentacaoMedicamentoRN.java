package br.gov.mec.aghu.farmacia.cadastroapoio.business;

import java.util.Calendar;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.business.exception.FarmaciaExceptionCode;
import br.gov.mec.aghu.farmacia.dao.AfaMedicamentoDAO;
import br.gov.mec.aghu.farmacia.dao.AfaTipoApresentacaoMedicamentoDAO;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaTipoApresentacaoMedicamento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * Regras de negócio da tabela AFA_TIPO_APRES_MDTOS
 * 
 * @author lcmoura
 * 
 */
@Stateless
public class TipoApresentacaoMedicamentoRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(TipoApresentacaoMedicamentoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AfaTipoApresentacaoMedicamentoDAO afaTipoApresentacaoMedicamentoDAO;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private AfaMedicamentoDAO afaMedicamentoDAO;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IFarmaciaFacade farmaciaFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8251344566742497367L;

	protected IFarmaciaFacade getFarmaciaFacade() {
		return this.farmaciaFacade;
	}

	/**
	 * @ORADB Trigger AFAT_TPR_BRI</br>
	 * 
	 * Passa a descrição para maiúsculo</br> Seta o usuário logado no
	 * sistema</br> Seta a data de criação
	 * 
	 * @param tipoApresentacaoMedicamento
	 * @throws ApplicationBusinessException
	 *             AFA-00169 | Não existe Servidor com este Vínculo e Matrícula
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	public void preInsertTipoApresentacaoMedicamento(
			AfaTipoApresentacaoMedicamento tipoApresentacaoMedicamento)
			throws ApplicationBusinessException {

		// AFA_00027
		AfaTipoApresentacaoMedicamento tipoApresentacaoMedicamentoLoaded = getAfaTipoApresentacaoMedicamentoDAO()
				.obterPorChavePrimaria(tipoApresentacaoMedicamento.getSigla());
		if (tipoApresentacaoMedicamentoLoaded != null) {
			throw new ApplicationBusinessException(
					FarmaciaExceptionCode.AFA_00027);
		}

		// Seta o servidor
		this.setServidor(tipoApresentacaoMedicamento);

		// Seta a data de criação
		tipoApresentacaoMedicamento.setCriadoEm(Calendar.getInstance()
				.getTime());
	}

	/**
	 * @ORADB Trigger AFAT_TPR_BRU</br>
	 * 
	 * Seta o usuário logado no sistema.</br> Verifica se a descrição foi
	 * alterada.</br> Ao inativar um registro verificar se é usado em um
	 * medicamento ativo.
	 * 
	 * @param tipoApresentacaoMedicamentoOld
	 *            estado anterior da entidade
	 * @param tipoApresentacaoMedicamento
	 *            estado atual da entidade
	 * @throws ApplicationBusinessException
	 *             AFA-00169 | Não existe Servidor com este Vínculo e Matrícula
	 * @throws ApplicationBusinessException
	 *             AFA-00187 | O Tipo Apresentação do Medicamento não pode ser
	 *             inativada se algum medicamento ativo o usa
	 * @throws ApplicationBusinessException
	 *             AFA-00188 | A descrição do Tipo Apresentação do Medicamento
	 *             não pode ser alterado
	 * @throws ApplicationBusinessException 
	 */
	public void preUpdateTipoApresentacaoMedicamento(
			AfaTipoApresentacaoMedicamento tipoApresentacaoMedicamentoOld,
			AfaTipoApresentacaoMedicamento tipoApresentacaoMedicamento)
			throws ApplicationBusinessException {

		// Seta o servidor
		this.setServidor(tipoApresentacaoMedicamento);

		// valida alteração
		if (!CoreUtil.igual(tipoApresentacaoMedicamentoOld.getDescricao(),
				tipoApresentacaoMedicamento.getDescricao())) {
			throw new ApplicationBusinessException(
					FarmaciaExceptionCode.AFA_00188);
		}

		// valida inativação
		if (CoreUtil.igual(tipoApresentacaoMedicamento.getSituacao(),
				DominioSituacao.I)) {

			Long numMedicamentosAtivos = getAfaMedicamentoDAO()
					.obterMedicamentosAtivosPorTipoApresentacaoCount(
							tipoApresentacaoMedicamento.getSigla());

			if (numMedicamentosAtivos > 0) {
				throw new ApplicationBusinessException(
						FarmaciaExceptionCode.AFA_00187);
			}
		}
	}

	/**
	 * @ORADB Trigger AFAT_TPR_BRD</br>
	 * 
	 * Verificar se o registro a ser deletado está dentro do período de exclusão
	 * conforme parametro de sistema
	 * 
	 * @param tipoApresentacaoMedicamento
	 * @throws BaseException
	 *             AFA_00173 | Erro na recuperação do parâmetro P_DIAS_PERM_DEL
	 *             na AFAK_TUM_RN.RN_TUMP_VER_DELECAO
	 * @throws BaseException
	 *             AFA_00172 | Não é possível excluir o registro por estar fora
	 *             do período permitido para exclusão
	 */
	public void preDeleteTipoApresentacaoMedicamento(
			AfaTipoApresentacaoMedicamento tipoApresentacaoMedicamento)
			throws BaseException {

		getFarmaciaFacade().verificarDelecao(
				tipoApresentacaoMedicamento.getCriadoEm());

	}
	
	/**
	 * 
	 * @param tipoApresentacaoMedicamento
	 * @throws BaseException
	 */
	public void verificarDependenciasDelecao(
			AfaTipoApresentacaoMedicamento tipoApresentacaoMedicamento)
			throws BaseException {
		
		List<AfaMedicamento> listaMedicamentos = getFarmaciaFacade()
				.pesquisarMedicamentosPorTipoApresentacao(
						tipoApresentacaoMedicamento.getSigla());
		
		if (!listaMedicamentos.isEmpty()){
			throw new ApplicationBusinessException(
					FarmaciaExceptionCode.MEDICAMENTO_ASSOCIADO_TIPO_APRESENTACAO);
		}
		
	}

	/**
	 * Seta o usuário logado no sistema
	 * 
	 * @param tipoApresentacaoMedicamento
	 * @throws ApplicationBusinessException
	 *             AFA-00169 | Não existe Servidor com este Vínculo e Matrícula.
	 * @throws ApplicationBusinessException 
	 */
	private void setServidor(
			AfaTipoApresentacaoMedicamento tipoApresentacaoMedicamento)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (servidorLogado == null) {
			throw new ApplicationBusinessException(
					FarmaciaExceptionCode.AFA_00169);
		}
		tipoApresentacaoMedicamento.setServidor(servidorLogado);
	}

	protected AfaTipoApresentacaoMedicamentoDAO getAfaTipoApresentacaoMedicamentoDAO() {
		return afaTipoApresentacaoMedicamentoDAO;
	}

	protected AfaMedicamentoDAO getAfaMedicamentoDAO() {
		return afaMedicamentoDAO;
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
