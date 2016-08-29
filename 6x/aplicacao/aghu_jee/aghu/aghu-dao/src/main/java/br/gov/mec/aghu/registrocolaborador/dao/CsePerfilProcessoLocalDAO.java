/**
 * 
 */
package br.gov.mec.aghu.registrocolaborador.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.CsePerfilProcessoLocal;
import br.gov.mec.aghu.model.CseProcessos;
import br.gov.mec.aghu.model.FccCentroCustos;

/**
 * @author aghu
 *
 */
public class CsePerfilProcessoLocalDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<CsePerfilProcessoLocal> {

	private static final long serialVersionUID = 1924067787586732746L;

	/**
	 * 
	 * 
	 * @param indConsulta
	 * @param indSituacao
	 * @param paramSeqProcConsPol
	 * @return
	 */
	public List<CsePerfilProcessoLocal> pesquisarPerfisProcessoLocal(DominioSimNao indConsulta, 
																	 DominioSituacao indSituacao, 
																	 Short paramSeqProcConsPol, 
																	 Integer codigoCentroCustoLotacao) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(CsePerfilProcessoLocal.class);
		criteria.add(Restrictions.eq(CsePerfilProcessoLocal.Fields.IND_CONSULTA.toString(), indConsulta));
		criteria.add(Restrictions.eq(CsePerfilProcessoLocal.Fields.IND_SITUACAO.toString(), indSituacao));
		criteria.add(Restrictions.eq(CsePerfilProcessoLocal.Fields.CSE_PROCESSOS.toString().concat(".").concat(CseProcessos.Fields.SEQ.toString()), paramSeqProcConsPol));
		
		if(codigoCentroCustoLotacao != null) {
			criteria.add(Restrictions.eq(CsePerfilProcessoLocal.Fields.FCC_CENTRO_CUSTOS_BY_CCT_CODIGO_LOTACAO_SERV.toString().concat(".").concat(FccCentroCustos.Fields.CODIGO.toString()), codigoCentroCustoLotacao));	
		}
		return executeCriteria(criteria);
	}
	

}
