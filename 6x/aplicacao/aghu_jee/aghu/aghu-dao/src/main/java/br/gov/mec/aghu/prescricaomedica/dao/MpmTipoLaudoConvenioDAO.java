package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.MpmTipoLaudo;
import br.gov.mec.aghu.model.MpmTipoLaudoConvenio;

public class MpmTipoLaudoConvenioDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmTipoLaudoConvenio> {

	private static final long serialVersionUID = 1997852971279566658L;


	public Short obterTempoValidadeTipoLaudo(Short tipoLaudoSeq,
			FatConvenioSaudePlano convenio) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmTipoLaudoConvenio.class);

		criteria.add(Restrictions.eq(
				MpmTipoLaudoConvenio.Fields.CONVENIO_SAUDE_PLANO.toString(),
				convenio));

		criteria.createAlias(MpmTipoLaudoConvenio.Fields.MPM_TIPO_LAUDO
				.toString(), MpmTipoLaudoConvenio.Fields.MPM_TIPO_LAUDO
				.toString());

		criteria.add(Restrictions.eq(MpmTipoLaudoConvenio.Fields.MPM_TIPO_LAUDO
				.toString()
				+ "." + MpmTipoLaudo.Fields.SEQ.toString(), tipoLaudoSeq));
		criteria.add(Restrictions.eq(MpmTipoLaudoConvenio.Fields.MPM_TIPO_LAUDO
				.toString()
				+ "." + MpmTipoLaudo.Fields.SITUACAO.toString(),
				DominioSituacao.A));

		criteria.setProjection(Projections
				.property(MpmTipoLaudoConvenio.Fields.MPM_TIPO_LAUDO.toString()
						+ "." + MpmTipoLaudo.Fields.TEMPO_VALIDADE.toString()));

		return (Short) this.executeCriteriaUniqueResult(criteria);
	}
	
	public MpmTipoLaudoConvenio obterTempoValidadeTipoLaudoPermanenciaMaior(Short tipoLaudoSeq,
			FatConvenioSaudePlano convenio) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmTipoLaudoConvenio.class);

		criteria.add(Restrictions.eq(
				MpmTipoLaudoConvenio.Fields.CONVENIO_SAUDE_PLANO.toString(),
				convenio));

		criteria.createAlias(MpmTipoLaudoConvenio.Fields.MPM_TIPO_LAUDO
				.toString(), MpmTipoLaudoConvenio.Fields.MPM_TIPO_LAUDO
				.toString());

		criteria.add(Restrictions.eq(MpmTipoLaudoConvenio.Fields.MPM_TIPO_LAUDO
				.toString()
				+ "." + MpmTipoLaudo.Fields.SEQ.toString(), tipoLaudoSeq));
		criteria.add(Restrictions.eq(MpmTipoLaudoConvenio.Fields.MPM_TIPO_LAUDO
				.toString()
				+ "." + MpmTipoLaudo.Fields.SITUACAO.toString(),
				DominioSituacao.A));

		return (MpmTipoLaudoConvenio) this.executeCriteriaUniqueResult(criteria);
	}

	
	public MpmTipoLaudoConvenio obterMpmTipoLaudoConvenio(Short tipoLaudoSeq, FatConvenioSaudePlano convenio) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MpmTipoLaudoConvenio.class);
		
		criteria.createAlias(MpmTipoLaudoConvenio.Fields.MPM_TIPO_LAUDO.toString(), MpmTipoLaudoConvenio.Fields.MPM_TIPO_LAUDO.toString());

		criteria.add(Restrictions.eq(MpmTipoLaudoConvenio.Fields.CONVENIO_SAUDE_PLANO.toString(),convenio));
		criteria.add(Restrictions.eq(MpmTipoLaudoConvenio.Fields.MPM_TIPO_LAUDO.toString()+ "." + MpmTipoLaudo.Fields.SEQ.toString(), tipoLaudoSeq));
		criteria.add(Restrictions.eq(MpmTipoLaudoConvenio.Fields.MPM_TIPO_LAUDO.toString()+ "." + MpmTipoLaudo.Fields.SITUACAO.toString(),DominioSituacao.A));

		return (MpmTipoLaudoConvenio) this.executeCriteriaUniqueResult(criteria);
	}

	public List<MpmTipoLaudoConvenio> pesquisarTipoLaudoConvenio(Short tipoLaudoSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmTipoLaudoConvenio.class);
		
		criteria.createAlias(MpmTipoLaudoConvenio.Fields.MPM_TIPO_LAUDO.toString(), "MTL");
		criteria.createAlias(MpmTipoLaudoConvenio.Fields.CONVENIO_SAUDE_PLANO.toString(), "CSP");
		criteria.createAlias("CSP."+FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString(), "CS");
		
		criteria.add(Restrictions.eq("MTL." + MpmTipoLaudo.Fields.SEQ.toString(), tipoLaudoSeq));
		
		return this.executeCriteria(criteria);
	}
 }
