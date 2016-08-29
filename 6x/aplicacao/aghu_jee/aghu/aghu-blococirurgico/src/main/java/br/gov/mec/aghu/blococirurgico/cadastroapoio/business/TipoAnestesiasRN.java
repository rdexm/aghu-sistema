package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcTipoAnestesiasDAO;
import br.gov.mec.aghu.model.MbcTipoAnestesias;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável por prover os métodos de negócio genéricos para cadastro de 
 * tipos de anestesia.
 * 
 * @autor fwinck
 * 
 */
@Stateless
public class TipoAnestesiasRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(TipoAnestesiasRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcTipoAnestesiasDAO mbcTipoAnestesiasDAO;


	private static final long serialVersionUID = -9001235819703398140L;

	private enum TipoAnestesiasRNExceptionCode implements BusinessExceptionCode {
		MBC_00212, MBC_00206, DESCRICAO_TIPO_ANESTESIA_JA_EXISTE;
	}

	public void persistirTipoAnestesia(MbcTipoAnestesias tipoAnestesia) throws ApplicationBusinessException {
		if (tipoAnestesia.getSeq() == null) {
			this.executarAntesInserir(tipoAnestesia);
			this.getMbcTipoAnestesiasDAO().persistir(tipoAnestesia);
			this.getMbcTipoAnestesiasDAO().flush();

		} else {
			MbcTipoAnestesias tipoAnestesiaOld = this.getMbcTipoAnestesiasDAO().obterOriginal(tipoAnestesia);
					
			executarAntesAlterar(tipoAnestesia, tipoAnestesiaOld);

			this.getMbcTipoAnestesiasDAO().atualizar(tipoAnestesia);
			this.getMbcTipoAnestesiasDAO().flush();
		}
	}

	/**
	 * ORADB MBCT_TAN_BRI
	 * @param tipoAnestesia
	 */
	protected void executarAntesInserir(MbcTipoAnestesias tipoAnestesia) throws ApplicationBusinessException {
		tipoAnestesia.setCriadoEm(new Date());
		//Valida UK - MBC_TAN_UK1
		if(getMbcTipoAnestesiasDAO().obterTipoAnestesiaPorDescricaoCount(tipoAnestesia.getDescricao()) > 0) {
			throw new ApplicationBusinessException(TipoAnestesiasRNExceptionCode.DESCRICAO_TIPO_ANESTESIA_JA_EXISTE);
		}
	}
	
	/**
	 * ORADB MBCT_TAN_BRU
	 * MBCK_TAN_RN.RN_TANP_VER_DESC
	 * mbck_mbc_rn.rn_mbcp_ver_update 
	 * @param tipoAnestesia
	 * @param tipoAnestesiaOld
	 * @throws ApplicationBusinessException
	 */
	protected void executarAntesAlterar(MbcTipoAnestesias tipoAnestesia, MbcTipoAnestesias tipoAnestesiaOld) throws ApplicationBusinessException{
		if(!tipoAnestesia.getCriadoEm().equals(tipoAnestesiaOld.getCriadoEm()) || !tipoAnestesia.getRapServidores().equals(tipoAnestesiaOld.getRapServidores())){
			throw new ApplicationBusinessException(TipoAnestesiasRNExceptionCode.MBC_00212);
		}
		
		if(!tipoAnestesia.getDescricao().equals(tipoAnestesiaOld.getDescricao())){
			throw new ApplicationBusinessException(TipoAnestesiasRNExceptionCode.MBC_00206);
		}
	}
		
	protected MbcTipoAnestesiasDAO getMbcTipoAnestesiasDAO(){
		return mbcTipoAnestesiasDAO;
	}
	
}