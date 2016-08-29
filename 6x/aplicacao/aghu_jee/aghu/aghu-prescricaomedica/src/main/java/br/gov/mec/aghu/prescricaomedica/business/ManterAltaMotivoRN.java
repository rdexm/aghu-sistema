package br.gov.mec.aghu.prescricaomedica.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MpmAltaMotivo;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.model.MpmMotivoAltaMedica;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaMotivoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmMotivoAltaMedicaDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * 
 * @author bsoliveira
 * 
 */
@Stateless
public class ManterAltaMotivoRN extends BaseBusiness {


@EJB
private ManterAltaSumarioRN manterAltaSumarioRN;

private static final Log LOG = LogFactory.getLog(ManterAltaMotivoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MpmMotivoAltaMedicaDAO mpmMotivoAltaMedicaDAO;

@Inject
private MpmAltaMotivoDAO mpmAltaMotivoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -4819114683445095807L;

	public enum ManterAltaMotivoRNExceptionCode implements BusinessExceptionCode {

		MPM_02696, MPM_02686, MPM_02681, MPM_02682, MPM_02683, MPM_03037;;

		public void throwException(Object... params)
				throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}

	}

	/**
	 * Insere objeto MpmAltaMotivo.
	 * 
	 * @param {MpmAltaMotivo} altaMotivo
	 * @throws ApplicationBusinessException
	 */
	public void inserirAltaMotivo(MpmAltaMotivo altaMotivo)
			throws ApplicationBusinessException {

		this.preInserirAltaMotivo(altaMotivo);
		montaDescricaoMotivoComPrimeiraMaiuscula(altaMotivo);//Muda a deswcrição para minuscula
		this.getAltaMotivoDAO().persistir(altaMotivo);
		this.getAltaMotivoDAO().flush();

	}

	/**
	 * Insere objeto MpmAltaMotivo.
	 * 
	 * @param {MpmAltaMotivo} altaMotivo
	 * @throws ApplicationBusinessException
	 */
	public void atualizarAltaMotivo(MpmAltaMotivo altaMotivo)
			throws ApplicationBusinessException {

		this.preAtualizarAltaMotivo(altaMotivo);
		montaDescricaoMotivoComPrimeiraMaiuscula(altaMotivo);//Muda a deswcrição para minuscula
		
		this.getAltaMotivoDAO().merge(altaMotivo);
		this.getAltaMotivoDAO().flush();

	}
	
	private void montaDescricaoMotivoComPrimeiraMaiuscula(MpmAltaMotivo altaMotivo){
		if(altaMotivo != null && altaMotivo.getDescMotivo()!= null && altaMotivo.getDescMotivo().trim().length()>1){
			altaMotivo.setDescMotivo(altaMotivo.getDescMotivo().trim().substring(0,1).toUpperCase()+altaMotivo.getDescMotivo().trim().substring(1).toLowerCase());
		}
		
		if(altaMotivo != null && altaMotivo.getComplMotivo()!=null && altaMotivo.getComplMotivo().trim().length()>1){
			altaMotivo.setComplMotivo(altaMotivo.getComplMotivo().trim().substring(0,1).toUpperCase()+altaMotivo.getComplMotivo().trim().substring(1).toLowerCase());
		}
	}
	
	/**
	 * Remove objeto MpmAltaMotivo.
	 * 
	 * @param {MpmAltaMotivo} altaMotivo
	 * @throws ApplicationBusinessException
	 */
	public void removerAltaMotivo(MpmAltaMotivo altaMotivo)
			throws ApplicationBusinessException {

		this.preRemoverAltaMotivo(altaMotivo);
		this.getAltaMotivoDAO().remover(altaMotivo);
		this.getAltaMotivoDAO().flush();

	}

	/**
	 * @ORADB Trigger MPMT_AMT_BRI
	 * 
	 * @param {MpmAltaMotivo} altaMotivo
	 * @throws ApplicationBusinessException
	 */
	protected void preInserirAltaMotivo(MpmAltaMotivo altaMotivo)
			throws ApplicationBusinessException {

		// Verifica se ALTA_SUMARIOS está ativo
		getAltaSumarioRN().verificarAltaSumarioAtivo(altaMotivo.getAltaSumario());

		List<MpmAltaMotivo> altaMotivoList = getAltaMotivoDAO().buscaAltaMotivoPorAltaSumario(altaMotivo.getAltaSumario());
		
		if(!altaMotivoList.isEmpty()) {
			ManterAltaMotivoRNExceptionCode.MPM_03037.throwException();
		}
		
		// Se na tabela mpm_motivo_alta_medica o ind_exige_complemento = 'S'
		// o compl_motivo deve ser informado.
		this.verificarCompl(altaMotivo.getMotivoAltaMedicas().getSeq(),
				null,
				altaMotivo.getComplMotivo(),
				null);

	}

	/**
	 * @ORADB Trigger MPMT_AMT_BRU
	 * 
	 * @param {MpmAltaMotivo} altaMotivo
	 * @throws ApplicationBusinessException
	 */
	protected void preAtualizarAltaMotivo(MpmAltaMotivo altaMotivo)
			throws ApplicationBusinessException {

		this.getAltaSumarioRN().verificarAltaSumarioAtivo(
				altaMotivo.getAltaSumario());

		MpmAltaMotivo altaMotivoOriginal = getAltaMotivoDAO()
				.obterAltaMotivoOriginal(altaMotivo);

		this.verificarAtualizacao(altaMotivo.getDescMotivo(),
				altaMotivoOriginal.getDescMotivo(), altaMotivo
						.getMotivoAltaMedicas().getSeq(), altaMotivoOriginal
						.getMotivoAltaMedicas().getSeq());

		this.verificarCompl(altaMotivo.getMotivoAltaMedicas().getSeq(),
				altaMotivoOriginal.getMotivoAltaMedicas().getSeq(),
				altaMotivo.getComplMotivo(),
				altaMotivoOriginal.getComplMotivo());

	}
	
	/**
	 * @ORADB Trigger MPMT_AMT_BRD
	 * 
	 * @param {MpmAltaMotivo} altaMotivo
	 * @throws ApplicationBusinessException
	 */
	protected void preRemoverAltaMotivo(MpmAltaMotivo altaMotivo)
			throws ApplicationBusinessException {

		// Verifica se ALTA_SUMARIOS está ativo
		getAltaSumarioRN().verificarAltaSumarioAtivo(
				altaMotivo.getAltaSumario());

	}

	/**
	 * @ORADB Package MPMK_AMT_RN
	 * @ORADB Procedure RN_AMTP_VER_UPDATE
	 * 
	 * mpmk_amt_rn.rn_amtp_ver_update
	 * 
	 * Operação: UPD
  	 * Ao alterar  DESC_MOTIVO a FK da MOTIVO ALTA MEDICA também deve ser alterada.
  	 * Ao alterar MAN_SEQ também deve ser aterado o DESC_MOTIVO.
  	 * 
	 * @param novoDescMotivo
	 * @param atualDescMotivo
	 * @param novoSeq
	 * @param atualSeq
	 */
	protected void verificarAtualizacao(String novoDescMotivo,
			String atualDescMotivo, Short novoManSeq, Short atualManSeq) throws ApplicationBusinessException {

		if (CoreUtil.modificados(novoDescMotivo, atualDescMotivo)
				&& ((novoDescMotivo == null && atualDescMotivo == null) || 
						!CoreUtil.modificados(novoManSeq, atualManSeq))) {
			
			ManterAltaMotivoRNExceptionCode.MPM_02681.throwException();

		}
		
		if (CoreUtil.modificados(novoManSeq, atualManSeq)
				&& ((novoDescMotivo == null && atualDescMotivo == null) || 
						!CoreUtil.modificados(novoDescMotivo, atualDescMotivo))) {
			
			ManterAltaMotivoRNExceptionCode.MPM_02682.throwException();

		}

	}

	/**
	 * @ORADB Procedure MPMK_AMT_RN.RN_AMTP_VER_COMPL
	 * 
	 * Operação: INS, UPD
	 * 
	 * Descrição: Se a tabela MOTIVO ALTA MEDICA o ind_exige_complemento igual =
	 * 'S' então o compl_motivo deverá ser informado.
	 * 
	 * bsoliveira - 01/11/2010
	 * 
	 * @param {Short} novoManSeq
	 * @param {Short} oldManSeq
	 * @param {String} novoComplMotivo
	 * @param {String} oldComplMotivo
	 * @throws ApplicationBusinessException
	 */
	public void verificarCompl(Short novoManSeq, Short oldManSeq,
			String novoComplMotivo, String oldComplMotivo)
			throws ApplicationBusinessException {

		MpmMotivoAltaMedica motivoAltaMedica = null;

		if (novoManSeq != null) {

			motivoAltaMedica = getMotivoAltaMedicaDAO()
					.obterMotivoAltaMedicaPeloId(novoManSeq);

		} else {

			motivoAltaMedica = getMotivoAltaMedicaDAO()
					.obterMotivoAltaMedicaPeloId(oldManSeq);

		}

		if (motivoAltaMedica != null) {

			if (motivoAltaMedica.getIndExigeComplemento() != null
					&& motivoAltaMedica.getIndExigeComplemento().booleanValue()
					&& (novoComplMotivo == null || StringUtils.isBlank(novoComplMotivo))) {

				ManterAltaMotivoRNExceptionCode.MPM_02696.throwException();

			}

			if (motivoAltaMedica.getIndExigeComplemento() != null
					&& !motivoAltaMedica.getIndExigeComplemento()
							.booleanValue() && novoComplMotivo != null) {

				ManterAltaMotivoRNExceptionCode.MPM_02686.throwException();

			}

		} else {

			ManterAltaMotivoRNExceptionCode.MPM_02683.throwException();

		}

	}
	
	/**
     * Método responsável pela validação da alta do paciente.<br>
     * Validação do motivo da alta do paciente deve pelo menos ter um registro associado ao sumário do paciente.<br>
     * 
     * @author gfmenezes
     * 
     * @param altaSumarioId
     * @return
     *  
     */
	public Boolean validarMotivoAltaPaciente(MpmAltaSumarioId altaSumarioId) {
		List<Long> result = this.getAltaMotivoDAO().listMotivoAltaPaciente(altaSumarioId);
		
		Long rowCount = 0L;
        if (!result.isEmpty()) {
            rowCount = (Long) result.get(0);
        }
        
		return rowCount > 0;
	}

	protected ManterAltaSumarioRN getAltaSumarioRN() {
		return manterAltaSumarioRN;
	}

	protected MpmAltaMotivoDAO getAltaMotivoDAO() {
		return mpmAltaMotivoDAO;
	}

	protected MpmMotivoAltaMedicaDAO getMotivoAltaMedicaDAO() {
		return mpmMotivoAltaMedicaDAO;
	}

}
