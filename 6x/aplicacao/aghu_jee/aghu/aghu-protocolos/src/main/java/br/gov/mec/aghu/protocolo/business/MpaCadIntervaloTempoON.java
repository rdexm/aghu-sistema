package br.gov.mec.aghu.protocolo.business;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.protocolos.dao.MpaCadIntervaloTempoDAO;
import br.gov.mec.aghu.protocolos.vo.CadIntervaloTempoVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class MpaCadIntervaloTempoON extends BaseBusiness{
	
	private static final Log LOG = LogFactory.getLog(MpaCadIntervaloTempoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@Inject
	private MpaCadIntervaloTempoDAO mpaCadIntervaloTempoDAO;
	
	private static final long serialVersionUID = 25143249547662264L;
	
	public List<CadIntervaloTempoVO> listarIntervalosTempo(Short vpaSeqp, Short vpaPtaSeq) {
		List<CadIntervaloTempoVO> listaRetorno = this.mpaCadIntervaloTempoDAO.listarIntervalosTempo(vpaSeqp, vpaPtaSeq);
		for (CadIntervaloTempoVO vo : listaRetorno) {
			
			Integer minutos = DateUtil.obterQtdMinutosEntreDuasDatas(vo.getHoraInicioReferencia(), vo.getHoraFimReferencia());
			
			Date tempoCalculado = DateUtil.adicionaMinutos(DateUtil.truncaData(new Date()), minutos);
			
			vo.setQtdeHoras(tempoCalculado);
			vo.setHoraInicioReferencia(DateUtil.truncaData(new Date()));
			vo.setHoraFimReferencia(tempoCalculado);
			vo.setCiclo((short) 1);
		}
		return listaRetorno;
	}

	public MpaCadIntervaloTempoDAO getMpaCadIntervaloTempoDAO() {
		return mpaCadIntervaloTempoDAO;
	}
}
