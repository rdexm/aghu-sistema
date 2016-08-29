package br.gov.mec.aghu.ambulatorio.business;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.dao.MamTmpPesosDAO;
import br.gov.mec.aghu.model.MamTmpPesos;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class MamTmpPesosRN extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4635502406562508931L;

	@Inject
	private MamTmpPesosDAO mamTmpPesosDAO;
	
	@EJB
	private MamRespostaEvolucoesRN mamRespostaEvolucoesRN; 
	
	private static final Log LOG = LogFactory.getLog(MamTmpPesosRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	public enum MamTmpPesosRNExceptionCode implements BusinessExceptionCode {
		MAM_00323, MAM_00324, MAM_00325, MAM_00326,	MAM_01850, MAM_01851, MAM_01852, MAM_01853,MAM_01854_01857, MAM_01855, MAM_01856,
		MAM_01858, MAM_01859, MAM_01837_01839_01843_01849_02360, MAM_01838_01840_01844_01866_01868, MAM_01860_01861_01865_01867, MAM_01862,
		MAM_02036_02040_02042_02048, MAM_02037_02041_02043, MAM_02051_02052_02056_02058_02078_02079_02083_02085, MAM_02053_02057_02059_02080_02084_02086,
		MAM_02358, MAM_02359_02361,	MAM_02362, MAM_02363, MAM_02205
	}
	
	public void preInsert(Integer pPacCodigo, Date criadoEm) throws ApplicationBusinessException{
		this.mamRespostaEvolucoesRN.rnTmppDtRegistro(pPacCodigo, criadoEm);
	}
	
	public MamTmpPesos persistirMamTmpPeso(String pTipo, Integer pQusQutSeq, Short pQusSeqP, Short pseq, BigDecimal pResposta, Integer pPacCodigo) {
		MamTmpPesos mamTmpPesos = new MamTmpPesos();
			mamTmpPesos.setQusQutSeq(pQusQutSeq);
			mamTmpPesos.setQusSeqp(pQusSeqP);
			mamTmpPesos.setSeqp(pseq);
			mamTmpPesos.setTipo(pTipo);
			mamTmpPesos.setPeso(pResposta);
			mamTmpPesos.setIndRecuperado("N");
			mamTmpPesos.setCriadoEm(new Date());
			mamTmpPesos.setPacCodigo(pPacCodigo);
			mamTmpPesos.setDataPeso(new Date());
		this.mamTmpPesosDAO.persistir(mamTmpPesos);
		return mamTmpPesos;
	}

	public MamTmpPesos atualizarPeso(Long pChave, BigDecimal pResposta, String tipo) {
		MamTmpPesos mamTmpPesos = buscarMamTmpPeso(pChave, tipo);
		mamTmpPesos.setPeso(pResposta);
		this.mamTmpPesosDAO.atualizar(mamTmpPesos);
		return mamTmpPesos;
	}
	
	public void deletarMamTpmPeso(Long pChave, BigDecimal pResposta, String tipo){
		MamTmpPesos mamTmpPesos = buscarMamTmpPeso(pChave, tipo);
		this.mamTmpPesosDAO.remover(mamTmpPesos);
	}

	private MamTmpPesos buscarMamTmpPeso(Long pChave, String tipo) {
		MamTmpPesos mamTmpPesos = this.mamTmpPesosDAO.obterMamTmpPesosPorChave(pChave, tipo);
		return mamTmpPesos;
	}
}