package br.gov.mec.aghu.prescricaomedica.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MpmAltaReinternacao;
import br.gov.mec.aghu.model.MpmMotivoReinternacao;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaReinternacaoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmMotivoReinternacaoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class ManterAltaReinternacaoRN extends BaseBusiness {


@EJB
private ManterAltaSumarioRN manterAltaSumarioRN;

private static final Log LOG = LogFactory.getLog(ManterAltaReinternacaoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MpmMotivoReinternacaoDAO mpmMotivoReinternacaoDAO;

@Inject
private MpmAltaReinternacaoDAO mpmAltaReinternacaoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -7991269718994184612L;

	public enum ManterAltaReinternacaoRNExceptionCode implements
			BusinessExceptionCode {

		ERRO_INSERIR_ALTA_REINTERNACAO, MPM_02853, MPM_02854, MPM_02891;

		public void throwException(Object... params)
				throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}

		public void throwException(Throwable cause, Object... params)
				throws ApplicationBusinessException {
			// Tratamento adicional para não esconder a excecao de negocio
			// original
			if (cause instanceof ApplicationBusinessException) {
				throw (ApplicationBusinessException) cause;
			}
			throw new ApplicationBusinessException(this, cause, params);
		}

	}

	/**
	 * Insere objeto MpmAltaReinternacao.
	 * 
	 * @param {MpmAltaReinternacao} altaReinternacao
	 */
	public void inserirAltaReinternacao(MpmAltaReinternacao altaReinternacao)
			throws ApplicationBusinessException {

		try {

			this.preInserirAltaReinternacao(altaReinternacao);
			this.getAltaReinternacaoDAO().persistir(altaReinternacao);
			this.getAltaReinternacaoDAO().flush();

		} catch (Exception e) {

			ManterAltaReinternacaoRNExceptionCode.ERRO_INSERIR_ALTA_REINTERNACAO
					.throwException(e);

		}

	}
	
	/**
	 * ORADB Trigger MPMT_ALR_BRU
	 * @param altaReinternacao
	 */
	public void atualizarAltaReinternacao(MpmAltaReinternacao altaReinternacao) throws ApplicationBusinessException {
		
		this.preAtualizarAltaReinternacao(altaReinternacao);
		this.getAltaReinternacaoDAO().atualizar(altaReinternacao);
		this.getAltaReinternacaoDAO().flush();
		
	}
	
	/**
	 * ORADB Trigger MPMT_ALR_BRD
	 * @param altaReinternacao
	 */
	public void removerReinternacao(MpmAltaReinternacao altaReinternacao) throws ApplicationBusinessException {
		
		MpmAltaReinternacao alta = this.getAltaReinternacaoDAO().obterPorChavePrimaria(altaReinternacao.getId());
		
		if (alta != null) {
			this.preRemoverAltaReinternacao(alta);
			this.getAltaReinternacaoDAO().remover(alta);
			this.getAltaReinternacaoDAO().flush();
		}
		
	}

	/**
	 * ORADB Trigger MPMT_ALR_BRI
	 * 
	 * @param {MpmAltaReinternacao} altaReinternacao
	 */
	protected void preInserirAltaReinternacao(
			MpmAltaReinternacao altaReinternacao) throws ApplicationBusinessException {

		// Verifica se ALTA_SUMARIOS está ativo
		getAltaSumarioRN().verificarAltaSumarioAtivo(altaReinternacao.getAltaSumario());

		// Se o ind_exige_complemento do motivo de reinternacao = 'S' então a
		// observacao
		// deve ser obrigatoriamente preenchida senão deve ser nula.
		this.verificarObservacao(altaReinternacao.getObservacao(),
				altaReinternacao.getMotivoReinternacao().getSeq());

		// verifica se a dats é maior que sysdate
		this.verificarDataReinternacao(altaReinternacao.getData());

	}
	
	/**
	 * ORADB Trigger MPMT_ALR_BRU
	 * 
	 * @param {MpmAltaReinternacao} altaReinternacao
	 */
	protected void preAtualizarAltaReinternacao(
			MpmAltaReinternacao altaReinternacao) throws ApplicationBusinessException {

		// Verifica se ALTA_SUMARIOS está ativo
		getAltaSumarioRN().verificarAltaSumarioAtivo(altaReinternacao.getAltaSumario());

		// Se o ind_exige_complemento do motivo de reinternacao = 'S' então a
		// observacao
		// deve ser obrigatoriamente preenchida senão deve ser nula.
		this.verificarObservacao(altaReinternacao.getObservacao(),
				altaReinternacao.getMotivoReinternacao().getSeq());

		// verifica se a dats é maior que sysdate
		this.verificarDataReinternacao(altaReinternacao.getData());

	}
	
	/**
	 * ORADB Trigger MPMT_ALR_BRD
	 * 
	 * @param {MpmAltaReinternacao} altaReinternacao
	 */
	protected void preRemoverAltaReinternacao(
			MpmAltaReinternacao altaReinternacao) throws ApplicationBusinessException {

		// Verifica se ALTA_SUMARIOS está ativo
		getAltaSumarioRN().verificarAltaSumarioAtivo(altaReinternacao.getAltaSumario());

	}

	/**
	 * ORADB Procedure mpmk_alr_rn.rn_alrp_ver_data_rei
	 * 
	 * Descrição: A data da reinternação  deve ser maior que sysdate
	 *  
	 * @param data
	 *  
	 */
	public void verificarDataReinternacao(Date data) throws ApplicationBusinessException {

		if (DateUtil.validaDataTruncadaMaior(new Date(), data)) {

			ManterAltaReinternacaoRNExceptionCode.MPM_02891.throwException();

		}
		
	}

	/**
	 * ORADB Procedure mpmk_alr_rn.rn_alrp_ver_observac
	 * 
	 * Operação: INS, UPD
	 * 
	 * Descrição: Se o ind_exige_complemento do motivo de reinternacao = 'S'
	 * então a observacao deve ser obrigatoriamente preenchida senão deve ser
	 * nula.
	 * 
	 * 
	 * @param {String} novoObservacao
	 * @param {Integer} novoMrnSeq
	 */
	public void verificarObservacao(String novoObservacao, Integer novoMrnSeq) throws ApplicationBusinessException {

		MpmMotivoReinternacao motivoReinternacao = getMotivoReinternacaoDAO().obterMotivoReinternacaoPeloId(novoMrnSeq);

		if (motivoReinternacao != null) {

			Boolean exigeComplemento = motivoReinternacao.getIndExigeComplemento();

			if ((novoObservacao == null || StringUtils.isBlank(novoObservacao)) && exigeComplemento) {

				ManterAltaReinternacaoRNExceptionCode.MPM_02853.throwException();

			}

			if ((novoObservacao != null && StringUtils.isNotBlank(novoObservacao)) && !exigeComplemento) {

				ManterAltaReinternacaoRNExceptionCode.MPM_02854.throwException();

			}

		}

	}

	protected ManterAltaSumarioRN getAltaSumarioRN() {
		return manterAltaSumarioRN;
	}

	protected MpmAltaReinternacaoDAO getAltaReinternacaoDAO() {
		return mpmAltaReinternacaoDAO;
	}

	protected MpmMotivoReinternacaoDAO getMotivoReinternacaoDAO() {
		return mpmMotivoReinternacaoDAO;
	}

}
