package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.MpmInformacaoPrescribente;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

/**
 * Classe responsável pelos métodos de acesso e persistência dos dados para a entidade MpmInformacaoPrescribente.
 */
public class MpmInformacaoPrescribenteDAO extends BaseDao<MpmInformacaoPrescribente> {

	private static final long serialVersionUID = -3796922891634104115L;

	/**
	 * Busca Informações de Prescribentes pendentes, associadas ao codigo de Atendimento.
	 * 
	 * @param atdSeq - Código do Atendimento
	 * @return Lista de Informações de Prescribentes
	 */
	public List<MpmInformacaoPrescribente> listarPrescribentesPendentesPorCodigoAtendimentoPrescricao(Integer atdSeq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MpmInformacaoPrescribente.class, "MIP");

		criteria.createAlias("MIP." + MpmInformacaoPrescribente.Fields.PRESCRICAO_MEDICA.toString(), "MPM");
//		criteria.createAlias("MIP." + MpmInformacaoPrescribente.Fields.SERVIDOR_CONSELHO.toString(), "VSC");

		criteria.add(Restrictions.eq("MIP." + MpmInformacaoPrescribente.Fields.IND_INF_VERIFICADA.toString(), DominioSimNao.N.isSim()));
		criteria.add(Restrictions.eq("MPM." + MpmPrescricaoMedica.Fields.IDATDSEQ.toString(), atdSeq));

		criteria.addOrder(Order.desc("MIP." + MpmInformacaoPrescribente.Fields.CRIADO_EM.toString()));

		return executeCriteria(criteria);
	}

}
