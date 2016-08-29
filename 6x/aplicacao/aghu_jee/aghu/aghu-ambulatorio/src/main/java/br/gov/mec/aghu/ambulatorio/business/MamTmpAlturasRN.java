package br.gov.mec.aghu.ambulatorio.business;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.dao.MamTmpAlturasDAO;
import br.gov.mec.aghu.model.MamTmpAlturas;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class MamTmpAlturasRN extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = 433514862023395056L;
	
	@Inject
	private MamTmpAlturasDAO mamTmpAlturasDAO;
	
	@EJB
	private MamRespostaEvolucoesRN mamRespostaEvolucoesRN; 

	private static final Log LOG = LogFactory.getLog(MamTmpAlturasRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	public enum MamTmpAlturasRNExceptionCode implements BusinessExceptionCode {
		MAM_00323, MAM_00324, MAM_00325, MAM_00326,	MAM_01850, MAM_01851, MAM_01852, MAM_01853,MAM_01854_01857, MAM_01855, MAM_01856,
		MAM_01858, MAM_01859, MAM_01837_01839_01843_01849_02360, MAM_01838_01840_01844_01866_01868, MAM_01860_01861_01865_01867, MAM_01862,
		MAM_02036_02040_02042_02048, MAM_02037_02041_02043, MAM_02051_02052_02056_02058_02078_02079_02083_02085, MAM_02053_02057_02059_02080_02084_02086,
		MAM_02358, MAM_02359_02361,	MAM_02362, MAM_02363, MAM_02205
	}

	public void deletarMamTpmAlturas(Long chave, Integer pQusQutSeq, Short pQusSeqP, String tipo, BigDecimal pResposta){
		MamTmpAlturas mamTmpAlturas = buscarMamTpmAlturas(chave, pQusQutSeq, pQusSeqP, tipo, pResposta);
		this.mamTmpAlturasDAO.remover(mamTmpAlturas);
	}
	
	private void preInsert(Integer pPacCodigo, Date criadoEm) throws ApplicationBusinessException{
		this.mamRespostaEvolucoesRN.rnTmppDtRegistro(pPacCodigo, criadoEm);
	}
	
	public MamTmpAlturas inserirMamTpmAlturas(String pTipo, Integer pQusQutSeq, Short pQusSeqP, Short pseq, BigDecimal pResposta, Integer pPacCodigo, Date pCriadoEm) throws ApplicationBusinessException {
		preInsert(pPacCodigo, pCriadoEm);
		MamTmpAlturas mamTmpAlturas = new MamTmpAlturas();
			mamTmpAlturas.setQusQutSeq(pQusQutSeq);
			mamTmpAlturas.setQusSeqp(pQusSeqP);
			mamTmpAlturas.setSeqp(pseq);
			mamTmpAlturas.setTipo(pTipo);
			mamTmpAlturas.setAltura(pResposta);
			mamTmpAlturas.setIndRecuperado("N");
			mamTmpAlturas.setCriadoEm(new Date());
			mamTmpAlturas.setPacCodigo(pPacCodigo);
		this.mamTmpAlturasDAO.persistir(mamTmpAlturas);
		return mamTmpAlturas;
	}

	public MamTmpAlturas atualizarMamTpmAlturas(Long chave, Integer pQusQutSeq, Short pQusSeqP, String tipo, BigDecimal pResposta) {
		MamTmpAlturas mamTmpAlturas = buscarMamTpmAlturas(chave, pQusQutSeq, pQusSeqP.shortValue(), tipo, pResposta);
		mamTmpAlturas.setAltura(pResposta);
		this.mamTmpAlturasDAO.atualizar(mamTmpAlturas);
		return mamTmpAlturas;
	}

	private MamTmpAlturas buscarMamTpmAlturas(Long pChave, Integer pQusQutSeq, Short pQusSeqP, String tipo, BigDecimal pResposta) {
		MamTmpAlturas mamTmpAlturas = this.mamTmpAlturasDAO.obterMamTmpAlturaPorChaveSeqTipo(pChave, pQusQutSeq, pQusSeqP, tipo);
		return mamTmpAlturas;
	}
}