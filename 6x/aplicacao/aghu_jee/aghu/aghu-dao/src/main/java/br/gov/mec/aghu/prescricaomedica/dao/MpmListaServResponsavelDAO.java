package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MpmListaServResponsavel;
import br.gov.mec.aghu.model.RapServidores;

/**
 * 
 */
public class MpmListaServResponsavelDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmListaServResponsavel> {

	private static final long serialVersionUID = -6194761930934016141L;


	public List<MpmListaServResponsavel> pesquisarAssociacoesPorServidor(RapServidores servidor) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmListaServResponsavel.class);
		
		if (servidor == null) {
			throw new IllegalArgumentException("Argumento obrigatório");
		}

		criteria.add(Restrictions.eq(MpmListaServResponsavel.Fields.SERVIDOR.toString(), servidor));

		return executeCriteria(criteria);
	}


	public List<RapServidores> listaProfissionaisResponsaveis(RapServidores servidor) {
		List<MpmListaServResponsavel> listaMpmListaServResponsavel = pesquisarAssociacoesPorServidor(servidor);
		List<RapServidores> listRapServidores = new ArrayList<RapServidores>();
		
		
		if (listaMpmListaServResponsavel != null) {
			for (MpmListaServResponsavel servResponsavel : listaMpmListaServResponsavel) {
				listRapServidores.add(servResponsavel.getServidorResponsavel());
			}
		}
		
		return listRapServidores;
	}


	public List<MpmListaServResponsavel> pesquisarServidorResponsavel(RapServidores servidorResp, Date dataFim) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmListaServResponsavel.class);

		criteria.createAlias(MpmListaServResponsavel.Fields.SERVIDOR_RESPONSAVEL.toString(), "servidor");

		Date data = new Date();
		if (dataFim != null) {
			data = dataFim;
		}

		criteria.add(Restrictions.le(MpmListaServResponsavel.Fields.CRIADO_EM.toString(), data));
		criteria.add(Restrictions.eq(MpmListaServResponsavel.Fields.SERVIDOR_RESPONSAVEL.toString(), servidorResp));

		Criterion c1 = Restrictions.gt("servidor." + RapServidores.Fields.DATA_FIM_VINCULO.toString(), new Date());
		Criterion c2 = Restrictions.isNull("servidor." + RapServidores.Fields.DATA_FIM_VINCULO.toString());

		criteria.add(Restrictions.or(c1, c2));

		return this.executeCriteria(criteria);
	}
}