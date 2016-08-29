package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoSumarioAlta;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.VMpmServRecomAltas;

public class VMpmServRecomAltasDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VMpmServRecomAltas> {


	private static final long serialVersionUID = -4595807605883338318L;

	public List<VMpmServRecomAltas> listarVMpmServRecomAltas(
			MpmAltaSumario altaSumario) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(VMpmServRecomAltas.class);

		criteria.add(Restrictions.eq(VMpmServRecomAltas.Fields.SER_VIN_CODIGO
				.toString(), altaSumario.getServidor().getId().getVinCodigo()));
		criteria.add(Restrictions.eq(VMpmServRecomAltas.Fields.SER_MATRICULA
				.toString(), altaSumario.getServidor().getId().getMatricula()));
		criteria.add(Restrictions.eq(VMpmServRecomAltas.Fields.IND_SITUACAO
				.toString(), DominioSituacao.A));
		criteria.add(Restrictions.or(Restrictions.eq(
				VMpmServRecomAltas.Fields.IND_TIPO_SUMR_ALTA.toString(),
				DominioTipoSumarioAlta.I), Restrictions.eq(
				VMpmServRecomAltas.Fields.IND_TIPO_SUMR_ALTA.toString(),
				DominioTipoSumarioAlta.T)));

		return this.executeCriteria(criteria);
	}

}
