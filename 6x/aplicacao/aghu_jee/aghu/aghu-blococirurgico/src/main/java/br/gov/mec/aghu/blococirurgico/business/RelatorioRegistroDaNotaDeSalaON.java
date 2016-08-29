package br.gov.mec.aghu.blococirurgico.business;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcMaterialPorCirurgiaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcEspPorCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioRegistroDaNotaDeSalaVO;
import br.gov.mec.aghu.blococirurgico.vo.SubRelatorioRegistroDaNotaDeSalaMateriaisVO;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class RelatorioRegistroDaNotaDeSalaON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(RelatorioRegistroDaNotaDeSalaON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcProcEspPorCirurgiasDAO mbcProcEspPorCirurgiasDAO;

	@Inject
	private MbcMaterialPorCirurgiaDAO mbcMaterialPorCirurgiaDAO;


	private static final long serialVersionUID = -5372406741292368077L;
	
	public List<RelatorioRegistroDaNotaDeSalaVO> listarProcedimentosMateriaisPorCirurgia(final Integer crgSeq) {
		
		List<RelatorioRegistroDaNotaDeSalaVO> listVo = new ArrayList<RelatorioRegistroDaNotaDeSalaVO>();
		
		RelatorioRegistroDaNotaDeSalaVO vo = new RelatorioRegistroDaNotaDeSalaVO();
		
		vo.setSubRelatorioProcedimentos(getMbcProcEspPorCirurgiasDAO().pesquisarProcedimentosAtivosPorCirurgia(crgSeq));
		vo.setSubRelatorioMateriais(getMbcMaterialPorCirurgiaDAO().pesquisarMateriaisPorCirurgiaNotaDeSala(crgSeq));
		
		if(!vo.getSubRelatorioMateriais().isEmpty()){
			SubRelatorioRegistroDaNotaDeSalaMateriaisVO voEquipe = vo.getSubRelatorioMateriais().get(0);
			vo.setEquipe(voEquipe.getEquipeNomeUsual() == null ? StringUtils.substring(voEquipe.getEquipeNome(),0,15) : voEquipe.getEquipeNomeUsual());
			
			vo.setTotal(this.efetuaCalculoValorTotal(vo.getSubRelatorioMateriais()));
			
		}
		
		listVo.add(vo);
		
		return listVo;
	}
	
	/** 
	 * Metodo criado para calcular os valor total do material com os numeros apenas 2 casas apos a virgula, 
	 * da forma anterior ficava lixo e podia ser somado valores nao corretos. 
	 * @param listaMateriais
	 * @return
	 */
	private Double efetuaCalculoValorTotal(List<SubRelatorioRegistroDaNotaDeSalaMateriaisVO> listaMateriais) {
		Double valorTotal = 0D;
		DecimalFormat df = new DecimalFormat( "#,##0.00" );

		for (SubRelatorioRegistroDaNotaDeSalaMateriaisVO item : listaMateriais) {
			double valorParseadoD = 0D;
			try {
				valorParseadoD = df.parse(item.getCustoTotalStr()).doubleValue();
			} catch (ParseException e) {
				valorTotal = null;
				
			}
			valorTotal = valorTotal + valorParseadoD;

		}
		return valorTotal;
	}

	protected MbcProcEspPorCirurgiasDAO getMbcProcEspPorCirurgiasDAO() {
		return mbcProcEspPorCirurgiasDAO;
	}
	
	protected MbcMaterialPorCirurgiaDAO getMbcMaterialPorCirurgiaDAO() {
		return mbcMaterialPorCirurgiaDAO;
	}

}
