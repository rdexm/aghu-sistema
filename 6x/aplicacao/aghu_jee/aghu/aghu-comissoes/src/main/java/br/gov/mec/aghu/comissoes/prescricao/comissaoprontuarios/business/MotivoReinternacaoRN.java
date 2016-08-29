package br.gov.mec.aghu.comissoes.prescricao.comissaoprontuarios.business;

import java.util.Calendar;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.comissoes.business.exception.ComissoesExceptionCode;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MpmMotivoReinternacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * Regras de negócio da tabela MPM_MOTIVO_REINTERNACOES
 * 
 * @author lcmoura
 * 
 */
@Stateless
public class MotivoReinternacaoRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MotivoReinternacaoRN.class);
	
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
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 482882954674300985L;

	/**
	 * ORADB Trigger MPMT_MRN_BRI</br>
	 * 
	 * Seta o usuário logado no sistema</br> Seta a data de criação</br> Testa
	 * se o ind_digita_complemento está ligado, caso o ind_outros estiver ativo.
	 * 
	 * @param mpmMotivoReinternacao
	 * @throws ApplicationBusinessException
	 *             RAP_00175 | Não existe Servidor cadastrado com
	 *             matricula/vinculo
	 * @throws ApplicationBusinessException
	 *             MPM_02851 | Para ativar o indicador outros, o indicador de
	 *             exige complemento deve estar ativo
	 * @throws ApplicationBusinessException 
	 */
	public void preInsertMpmMotivoReinternacao(MpmMotivoReinternacao mpmMotivoReinternacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (servidorLogado == null) {
			throw new ApplicationBusinessException(ComissoesExceptionCode.RAP_00175);
		}
		mpmMotivoReinternacao.setServidor(servidorLogado);
		
		// MPM_02851
		if (mpmMotivoReinternacao.getIndOutros() && !mpmMotivoReinternacao.getIndExigeComplemento()) {
			throw new ApplicationBusinessException(ComissoesExceptionCode.MPM_02851);
		}

		// Seta a data de criação
		mpmMotivoReinternacao.setCriadoEm(Calendar.getInstance().getTime());
	}

	/**
	 * ORADB Trigger MPMT_MRN_BRU</br>
	 * 
	 * Seta o usuário logado no sistema.</br> Verifica se a descrição foi
	 * alterada.</br> Ao inativar um registro verificar se é usado em um
	 * medicamento ativo.
	 * 
	 * @param mpmMotivoReinternacaoOld
	 *            estado anterior da entidade
	 * @param mpmMotivoReinternacao
	 *            estado atual da entidade
	 * @throws ApplicationBusinessException
	 *             RAP_00175 | Não existe Servidor cadastrado com
	 *             matricula/vinculo
	 * @throws ApplicationBusinessException
	 *             MPM_02685 | O motivo de reinternação estando inativo não pode
	 *             ser ativado novamente
	 * @throws ApplicationBusinessException
	 *             MPM_02831 | Não é permitido alterar a descrição do motivo da
	 *             reinternação
	 * @throws ApplicationBusinessException
	 *             MPM_02851 | Para ativar o indicador outros, o indicador de
	 *             exige complemento deve estar ativo
	 * @throws ApplicationBusinessException 
	 */
	public void preUpdateMpmMotivoReinternacao(MpmMotivoReinternacao mpmMotivoReinternacaoOld,
			MpmMotivoReinternacao mpmMotivoReinternacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		// valida ativação
		if (mpmMotivoReinternacaoOld.getIndSituacao().equals(DominioSituacao.I)
				&& mpmMotivoReinternacao.getIndSituacao().equals(DominioSituacao.A)) {
			throw new ApplicationBusinessException(ComissoesExceptionCode.MPM_02685);
		}

		if (servidorLogado == null) {
			throw new ApplicationBusinessException(ComissoesExceptionCode.RAP_00175);
		}
		mpmMotivoReinternacao.setServidor(servidorLogado);

		// MPM_02831
		if (!CoreUtil.igual(mpmMotivoReinternacao.getDescricao(), mpmMotivoReinternacaoOld.getDescricao())) {
			throw new ApplicationBusinessException(ComissoesExceptionCode.MPM_02831);
		}

		// MPM_02851
		if (mpmMotivoReinternacao.getIndOutros() && !mpmMotivoReinternacao.getIndExigeComplemento()) {
			throw new ApplicationBusinessException(ComissoesExceptionCode.MPM_02851);
		}
	}

	/**
	 * ORADB Trigger MPMT_MRN_BRD</br>
	 * 
	 * Verificar se o registro a ser deletado está dentro do período de exclusão
	 * conforme parametro de sistema
	 * 
	 * @param mpmMotivoReinternacao
	 * @throws ApplicationBusinessException
	 *             MPM_00680 | Erro na recuperação do parâmetro P_DIAS_PERM_DEL
	 *             na MPMK_MPM_RN.RN_MPMP_VER_DEL
	 * @throws ApplicationBusinessException
	 *             MPM_00681 | Não é possível excluir o registro por estar fora
	 *             do período permitido para exclusão
	 */
	public void preDeleteMpmMotivoReinternacao(MpmMotivoReinternacao mpmMotivoReinternacao) throws ApplicationBusinessException {

		AghParametros aghParametro = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_DIAS_PERM_DEL_MPM);

		if (aghParametro != null && aghParametro.getVlrNumerico() != null) {
			float diff = CoreUtil.diferencaEntreDatasEmDias(Calendar.getInstance().getTime(), mpmMotivoReinternacao.getCriadoEm());
			if (diff > aghParametro.getVlrNumerico().floatValue()) {
				throw new ApplicationBusinessException(ComissoesExceptionCode.MPM_00681);
			}
		} else {
			throw new ApplicationBusinessException(ComissoesExceptionCode.MPM_00680);
		}
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
