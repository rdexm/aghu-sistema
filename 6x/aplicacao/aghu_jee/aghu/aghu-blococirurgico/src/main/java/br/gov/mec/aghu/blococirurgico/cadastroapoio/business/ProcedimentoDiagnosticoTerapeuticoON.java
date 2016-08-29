package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.PdtProcDiagTerapDAO;
import br.gov.mec.aghu.dominio.DominioIndContaminacao;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.utils.StringUtil;

@Stateless
public class ProcedimentoDiagnosticoTerapeuticoON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ProcedimentoDiagnosticoTerapeuticoON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private PdtProcDiagTerapDAO pdtProcDiagTerapDAO;


	/**
	 * 
	 */
	private static final long serialVersionUID = -1943606156045895400L;

	public Long pesquisarProcDiagTerapCount(Integer seq, String descricao, Short especialidade, DominioIndContaminacao contaminacao) {
		return getPdtProcDiagTerapDAO().pesquisarProcDiagTerapCount(seq, descricao, especialidade, contaminacao);
	}

	public List<LinhaReportVO> pesquisarProcDiagTerap(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, Integer seq,
			String descricao, Short especialidade, DominioIndContaminacao contaminacao) {
		
		//List<LinhaReportVO> procsDiagTerap = new ArrayList<LinhaReportVO>();
		
		List<LinhaReportVO> listaProcedimentosDiagnosticoTerapeutico = getPdtProcDiagTerapDAO().pesquisarProcDiagTerap(firstResult, maxResult, orderProperty, asc, seq,
				descricao, especialidade, contaminacao);   
		
		for(LinhaReportVO linhaReportVO:listaProcedimentosDiagnosticoTerapeutico){
			
			if (linhaReportVO.getTexto1() != null && !linhaReportVO.getTexto1().isEmpty()){

				Integer nro = 40;
				Long size = nro.longValue();

				linhaReportVO.setTexto3(StringUtil.trunc(linhaReportVO.getTexto1(), true, size));	

				if (linhaReportVO.getNumero4() != null){

					String strTempoMinimo = String.format("%04d",linhaReportVO.getNumero4());
					String strHora = strTempoMinimo.substring(0, 2);
					String strMinuto = strTempoMinimo.substring(2, 4);
					
					linhaReportVO.setTexto4(strHora + ":" + strMinuto);					
				}	
				
				//procsDiagTerap.add(linhaReportVO);			
				
			}				
			
		}		
		return listaProcedimentosDiagnosticoTerapeutico;  
	}

	protected PdtProcDiagTerapDAO getPdtProcDiagTerapDAO(){
		return pdtProcDiagTerapDAO;
	}	
}
