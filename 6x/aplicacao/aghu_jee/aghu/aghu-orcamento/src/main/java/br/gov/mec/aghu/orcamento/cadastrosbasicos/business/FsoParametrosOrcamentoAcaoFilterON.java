package br.gov.mec.aghu.orcamento.cadastrosbasicos.business;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioAcaoParametrosOrcamento;
import br.gov.mec.aghu.model.FsoParametrosOrcamento;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.vo.FsoParametrosOrcamentoCriteriaVO;
import br.gov.mec.aghu.orcamento.dao.FsoParametrosOrcamentoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * Filtro para Ação de Parâmetro Orçamentário.
 * 
 * @author mlcruz
 */
@Stateless
@Local(FsoParametrosOrcamentoAcaoFilterON.class)
public class FsoParametrosOrcamentoAcaoFilterON
		extends BaseBusiness
		implements FsoParametrosOrcamentoFilterStrategy<FsoParametrosOrcamento> {

private static final Log LOG = LogFactory.getLog(FsoParametrosOrcamentoAcaoFilterON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private FsoParametrosOrcamentoDAO fsoParametrosOrcamentoDAO;	
	private static final long serialVersionUID = 338661755615203779L;

	@Override
	public FsoParametrosOrcamento find(FsoParametrosOrcamentoCriteriaVO criteria) {
		List<FsoParametrosOrcamento> result = getFsoParametrosOrcamentoDAO()
				.pesquisarParametrosOrcamento(criteria);
		
		if (!result.isEmpty()) {
			FsoParametrosOrcamento suggestion = null;
			
			for (FsoParametrosOrcamento item : result) {
				DominioAcaoParametrosOrcamento acao = null;
				
				switch (criteria.getParametro()) {
				case GRUPO_NATUREZA:
					acao = item.getAcaoGnd();
					break;
					
				case NATUREZA:
					acao = item.getAcaoNtd();
					break;
					
				case VERBA_GESTAO:
					acao = item.getAcaoVbg();
					break;
					
				case CENTRO_CUSTO:
					acao = item.getAcaoCct();
					break;
				}
				
				if (!DominioAcaoParametrosOrcamento.S.equals(acao)) {
					return item;
				} else {
					suggestion = item;
				}
			}
			
			return suggestion;
		} else {
			return null;
		}
	}

	protected FsoParametrosOrcamentoDAO getFsoParametrosOrcamentoDAO() {
		return fsoParametrosOrcamentoDAO;
	}
}