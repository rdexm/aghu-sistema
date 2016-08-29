package br.gov.mec.aghu.faturamento.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FatConvFaixaEtaria;

public class FatConvFaixaEtariaDAO
		extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatConvFaixaEtaria> {

	private static final long serialVersionUID = -5428015949512290045L;

	protected Date getSysdate() {
		return Calendar.getInstance().getTime();
	}

	protected DetachedCriteria obterCriteriaCorrenteAtivoPorIdadeConvenio(Short idade, Short codConvenio) {

		DetachedCriteria result = null;
		Date sysdate = null;

		sysdate = this.getSysdate();
		result = DetachedCriteria.forClass(FatConvFaixaEtaria.class);
		result.add(Restrictions.eq(FatConvFaixaEtaria.Fields.CNV_CODIGO.toString(), codConvenio));
		result.add(Restrictions.le(FatConvFaixaEtaria.Fields.DT_INICIO_VALIDADE.toString(), sysdate));
		result.add(Restrictions.or(
				Restrictions.isNull(FatConvFaixaEtaria.Fields.DT_FIM_VALIDADE.toString()),
				Restrictions.ge(FatConvFaixaEtaria.Fields.DT_FIM_VALIDADE.toString(), sysdate)));
		result.add(Restrictions.le(FatConvFaixaEtaria.Fields.IDADE_INICIO.toString(), idade));
		result.add(Restrictions.ge(FatConvFaixaEtaria.Fields.IDADE_FIM.toString(), idade));
		result.add(Restrictions.eq(FatConvFaixaEtaria.Fields.IND_SITUACAO_REGISTRO.toString(), DominioSituacao.A));

		return result;
	}

	public List<FatConvFaixaEtaria> obterListaCorrenteAtivoPorIdadeConvenio(Short idade, Short codConvenio) {

		List<FatConvFaixaEtaria> result = null;
		DetachedCriteria criteria = null;

		criteria = this.obterCriteriaCorrenteAtivoPorIdadeConvenio(idade, codConvenio);
		result = this.executeCriteria(criteria);

		return result;
	}
}
