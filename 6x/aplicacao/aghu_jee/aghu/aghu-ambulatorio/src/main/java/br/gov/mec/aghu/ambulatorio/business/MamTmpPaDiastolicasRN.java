package br.gov.mec.aghu.ambulatorio.business;

import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.dao.MamTmpPaDiastolicasDAO;
import br.gov.mec.aghu.model.MamTmpPaDiastolicas;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class MamTmpPaDiastolicasRN extends BaseBusiness{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5486851267060241160L;
	
	@Inject
	private MamTmpPaDiastolicasDAO mamTmpPaDiastolicasDAO;

	private static final Log LOG = LogFactory.getLog(MamTmpPaDiastolicasRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	public enum MamTmpPaDiastolicasRNExceptionCode implements BusinessExceptionCode {
		MAM_00323, MAM_00324, MAM_00325, MAM_00326,	MAM_01850, MAM_01851, MAM_01852, MAM_01853,MAM_01854_01857, MAM_01855, MAM_01856,
		MAM_01858, MAM_01859, MAM_01837_01839_01843_01849_02360, MAM_01838_01840_01844_01866_01868, MAM_01860_01861_01865_01867, MAM_01862,
		MAM_02036_02040_02042_02048, MAM_02037_02041_02043, MAM_02051_02052_02056_02058_02078_02079_02083_02085, MAM_02053_02057_02059_02080_02084_02086,
		MAM_02358, MAM_02359_02361,	MAM_02362, MAM_02363, MAM_02205
	}
	
	public MamTmpPaDiastolicas inserirMamTmpPaDiastolicas(String pTipo, Long pChave, Integer pQusQutSeq, Short pQusSeqP, Short pseq, Short pResposta,
			 Integer pPacCodigo, Date pCriadoEm){
		MamTmpPaDiastolicas mamTmpPaDiastolicas = new MamTmpPaDiastolicas();
			mamTmpPaDiastolicas.setChave(pChave);
			mamTmpPaDiastolicas.setQusQutSeq(pQusQutSeq);
			mamTmpPaDiastolicas.setQusSeqp(pQusSeqP);
			mamTmpPaDiastolicas.setSeqp(pseq);
			mamTmpPaDiastolicas.setTipo(pTipo);
			mamTmpPaDiastolicas.setDiastolica(pResposta);
			mamTmpPaDiastolicas.setIndRecuperado("N");
			Date data = (Date) CoreUtil.nvl(pCriadoEm, new Date());
			mamTmpPaDiastolicas.setCriadoEm(data);
			mamTmpPaDiastolicas.setPacCodigo(pPacCodigo);
		this.mamTmpPaDiastolicasDAO.persistir(mamTmpPaDiastolicas);
		return mamTmpPaDiastolicas;
	}
	
	public MamTmpPaDiastolicas atualizarMamTmpPaDiastolicas(Long pChave, Short pResposta, Integer pQusQutSeq, Short pQusSeqP, Short pseq, String pTipo){
		MamTmpPaDiastolicas mamTmpPaDiastolicas = buscarPaSistolica(pChave, pQusQutSeq, pQusSeqP, pseq, pTipo);
		mamTmpPaDiastolicas.setDiastolica(pResposta);
		this.mamTmpPaDiastolicasDAO.atualizar(mamTmpPaDiastolicas);
		return mamTmpPaDiastolicas;
	}
	
	public void deletarMamTmpPaDiastolicas(Long pChave, String pResposta, Integer pQusQutSeq, Short pQusSeqP, Short pseq, String pTipo){
		MamTmpPaDiastolicas mamTmpPaDiastolicas = buscarPaSistolica(pChave, pQusQutSeq, pQusSeqP, pseq, pTipo);
		this.mamTmpPaDiastolicasDAO.remover(mamTmpPaDiastolicas);
	}

	private MamTmpPaDiastolicas buscarPaSistolica(Long pChave, Integer pQusQutSeq, Short pQusSeqP, Short pseq, String pTipo) {
		MamTmpPaDiastolicas mamTmpPaDiastolicas = this.mamTmpPaDiastolicasDAO.obterPaDiastolicasPorPorChaveTipoSeq(pChave.longValue(), pQusQutSeq.shortValue(),
				pQusSeqP.shortValue(), pseq.shortValue(), pTipo);
		return mamTmpPaDiastolicas;
	}
}