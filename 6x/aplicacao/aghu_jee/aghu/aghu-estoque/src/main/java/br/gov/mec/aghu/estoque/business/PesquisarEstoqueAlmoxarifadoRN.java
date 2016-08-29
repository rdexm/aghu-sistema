package br.gov.mec.aghu.estoque.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.estoque.dao.SceHistoricoProblemaMaterialDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class PesquisarEstoqueAlmoxarifadoRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(PesquisarEstoqueAlmoxarifadoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private SceHistoricoProblemaMaterialDAO sceHistoricoProblemaMaterialDAO;

	
	private static final long serialVersionUID = 2676614869191336319L;

	/**
	 * Calcula a qtde de problema de materiais no estoque almoxarifado.
	 * ORADB FUNCTION CALCULA_QTDE_PROBLEMA
	 * @param estoqueAlmoxSeq
	 * @return
	 * @author bruno.mourao
	 * @since 12/11/2011
	 */
	public Integer calculaQtdeProblema(Integer estoqueAlmoxSeq){
		return getSceHistoricoProblemaDAO().obterQtdeProblemaPorCodAlmoxarifado(estoqueAlmoxSeq);
	}

	private SceHistoricoProblemaMaterialDAO getSceHistoricoProblemaDAO() {
		return sceHistoricoProblemaMaterialDAO;		
	}

}
