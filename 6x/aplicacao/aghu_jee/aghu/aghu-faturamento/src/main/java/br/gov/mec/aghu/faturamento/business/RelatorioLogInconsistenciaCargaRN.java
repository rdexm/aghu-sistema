package br.gov.mec.aghu.faturamento.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.faturamento.dao.FatLogErrorDAO;
import br.gov.mec.aghu.faturamento.vo.PendenciaEncerramentoVO;
import br.gov.mec.aghu.core.business.BaseBusiness;


/**
 * Realiza a geração de arquivo de log inconsistencia
 * 
 * @author felipe.rocha
 */
@Stateless
public class RelatorioLogInconsistenciaCargaRN extends BaseBusiness {


	private static final long serialVersionUID = -3002327260033805389L;
	/**
	 * 
	 */
	@Inject
	private FatLogErrorDAO fatLogErrorDAO;
	
	
	public List<PendenciaEncerramentoVO> obterDadosRelatorio(Date dataImp){
		List<PendenciaEncerramentoVO> lista  = new ArrayList<PendenciaEncerramentoVO>();
		if (dataImp != null) {
			lista = fatLogErrorDAO.pesquisarMensagensErroPorData(dataImp);
		}
		return lista;
	}
	
	@Override
	protected Log getLogger() {
		// TODO Auto-generated method stub
		return null;
	}


}
