package br.gov.mec.aghu.sig.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.RapOcupacaoCargo;
import br.gov.mec.aghu.model.SigGrupoOcupacaoCargos;
import br.gov.mec.aghu.model.SigGrupoOcupacoes;

public class SigGrupoOcupacaoCargosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SigGrupoOcupacaoCargos> {

	private static final long serialVersionUID = -8657374855688282234L;

	public List<SigGrupoOcupacaoCargos> obterPorOcupacaoCargo(RapOcupacaoCargo ocupacaoCargo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigGrupoOcupacaoCargos.class, "gocc");
		criteria.createAlias("gocc."+SigGrupoOcupacaoCargos.Fields.SIG_GRUPO_OCUPACOES, "goc");
		criteria.add(Restrictions.eq(SigGrupoOcupacaoCargos.Fields.RAP_OCUPACAO_CARGO.toString(), ocupacaoCargo));
		return this.executeCriteria(criteria);
	}

	public List<SigGrupoOcupacaoCargos> obterPorGrupoOcupacao(SigGrupoOcupacoes grupoOcupacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigGrupoOcupacaoCargos.class);
		criteria.add(Restrictions.eq(SigGrupoOcupacaoCargos.Fields.SIG_GRUPO_OCUPACOES.toString(), grupoOcupacao));
		return this.executeCriteria(criteria);
	}

}
