package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcTipoAnestesiaCombinadaDAO;
import br.gov.mec.aghu.model.MbcTipoAnestesiaCombinada;
import br.gov.mec.aghu.model.MbcTipoAnestesiaCombinadaId;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável por prover os métodos de negócio genéricos para cadastro de 
 * tipos de anestesias combinadas.
 * 
 * @autor fwinck
 * 
 */
@Stateless
public class TipoAnestesiasCombinadasRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(TipoAnestesiasCombinadasRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcTipoAnestesiaCombinadaDAO mbcTipoAnestesiaCombinadaDAO;


	private static final long serialVersionUID = -9001235819703398140L;

	private enum TipoAnestesiasCombinadasRNExceptionCode implements BusinessExceptionCode {
		MBC_01671, MENSAGEM_ERRO_INCLUSAO_TIPO_ANESTESIA_COMBINADO_JA_EXISTE;
	}

	public void persistirTipoAnestesiaCombinada(MbcTipoAnestesiaCombinada tipoAnestesiaCombinada) throws ApplicationBusinessException {
		if (tipoAnestesiaCombinada.getId() == null) {
			this.executarAntesInserir(tipoAnestesiaCombinada);
			defineId(tipoAnestesiaCombinada);
			this.getMbcTipoAnestesiaCombinadaDAO().persistir(tipoAnestesiaCombinada);
			this.getMbcTipoAnestesiaCombinadaDAO().flush();

		} else {
			this.getMbcTipoAnestesiaCombinadaDAO().atualizar(tipoAnestesiaCombinada);
			this.getMbcTipoAnestesiaCombinadaDAO().flush();
		}
	}

	/**
	 * ORADB MBCT_ACB_BRI
	 * @param tipoAnestesiaCombinada
	 * @throws ApplicationBusinessException 
	 */
	protected void executarAntesInserir(MbcTipoAnestesiaCombinada tipoAnestesiaCombinada) throws ApplicationBusinessException{
		tipoAnestesiaCombinada.setCriadoEm(new Date());
		verCombina(tipoAnestesiaCombinada);
	}
	
	private void defineId(MbcTipoAnestesiaCombinada tipoAnestesiaCombinada) throws ApplicationBusinessException{
		MbcTipoAnestesiaCombinadaId tipoAnestesiaCombinadaId = new MbcTipoAnestesiaCombinadaId();
		tipoAnestesiaCombinadaId.setTanSeq(tipoAnestesiaCombinada.getMbcTipoAnestesiasByTanSeq().getSeq());
		tipoAnestesiaCombinadaId.setTanSeqCombina(tipoAnestesiaCombinada.getMbcTipoAnestesiasByTanSeqCombina().getSeq());
		
		MbcTipoAnestesiaCombinada objExistente = getMbcTipoAnestesiaCombinadaDAO().obterPorChavePrimaria(tipoAnestesiaCombinadaId);
		if(objExistente != null){
			throw new ApplicationBusinessException(TipoAnestesiasCombinadasRNExceptionCode.MENSAGEM_ERRO_INCLUSAO_TIPO_ANESTESIA_COMBINADO_JA_EXISTE, tipoAnestesiaCombinada.getMbcTipoAnestesiasByTanSeqCombina().getDescricao());
		}
		
		tipoAnestesiaCombinada.setId(tipoAnestesiaCombinadaId);
	}
	
	/**
	 * ORADB mbck_acb_rn.rn_acbp_ver_combina
	 * @param tipoAnestesiaCombinada
	 * @throws ApplicationBusinessException 
	 */
	protected void verCombina(MbcTipoAnestesiaCombinada tipoAnestesiaCombinada) throws ApplicationBusinessException{
		if(!tipoAnestesiaCombinada.getMbcTipoAnestesiasByTanSeq().getIndCombinada()){
			throw new ApplicationBusinessException(TipoAnestesiasCombinadasRNExceptionCode.MBC_01671);
		}
	}
	
	protected MbcTipoAnestesiaCombinadaDAO getMbcTipoAnestesiaCombinadaDAO(){
		return mbcTipoAnestesiaCombinadaDAO;
	}
	
}