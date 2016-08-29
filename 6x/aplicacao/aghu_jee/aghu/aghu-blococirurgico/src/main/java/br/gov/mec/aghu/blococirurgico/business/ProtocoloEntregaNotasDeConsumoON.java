package br.gov.mec.aghu.blococirurgico.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.vo.ProtocoloEntregaNotasDeConsumoVO;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioOrdenacaoProtocoloEntregaNotasConsumo;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class ProtocoloEntregaNotasDeConsumoON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ProtocoloEntregaNotasDeConsumoON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcCirurgiasDAO mbcCirurgiasDAO;

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2485578376336320529L;

	public List<ProtocoloEntregaNotasDeConsumoVO> listarProtocoloEntregaNotasDeConsumo(Short unfSeq, Date data, 
			DominioSimNao pacienteSus, DominioOrdenacaoProtocoloEntregaNotasConsumo ordenacao) {
		
		List<ProtocoloEntregaNotasDeConsumoVO> listRelatorio = new ArrayList<ProtocoloEntregaNotasDeConsumoVO>();
		
		List<ProtocoloEntregaNotasDeConsumoVO> listVo = this.getMbcCirurgiasDAO().relatorioProtocoloEntregaNotaConsumo(unfSeq, data, ordenacao);
				
		for (ProtocoloEntregaNotasDeConsumoVO protocoloEntregaNotasDeConsumoVO : listVo) {
			MbcCirurgias cirurgia = this.getMbcCirurgiasDAO().obterPorChavePrimaria(protocoloEntregaNotasDeConsumoVO.getCrgSeq());
			
			if (pacienteSus == null || StringUtils.equals(pacienteSus != null ? pacienteSus.toString() : null, cirurgia.getConvenioSaude().getGrupoConvenio().getDescricao().toString() 
																	== DominioGrupoConvenio.S.getDescricao() 
																		? DominioSimNao.S.toString() : DominioSimNao.N.toString())){
				listRelatorio.add(protocoloEntregaNotasDeConsumoVO);
			}

		}

		return listRelatorio;
	}
	
	protected MbcCirurgiasDAO getMbcCirurgiasDAO() {
		return mbcCirurgiasDAO;
	}
}
