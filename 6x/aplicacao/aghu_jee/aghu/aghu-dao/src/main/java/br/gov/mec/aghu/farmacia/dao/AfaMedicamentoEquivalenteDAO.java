package br.gov.mec.aghu.farmacia.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaMedicamentoEquivalente;
import br.gov.mec.aghu.model.AfaMedicamentoEquivalenteId;
import br.gov.mec.aghu.model.RapServidores;

public class AfaMedicamentoEquivalenteDAO extends AbstractMedicamentoDAO<AfaMedicamentoEquivalente>{


	private static final long serialVersionUID = 4098493014434115326L;

	@Override 
	protected void obterValorSequencialId(AfaMedicamentoEquivalente elemento) {
		AfaMedicamentoEquivalenteId id = new AfaMedicamentoEquivalenteId();
		id.setMedMatCodigo(elemento.getId().getMedMatCodigo());
		id.setMedMatCodigoEquivalente(elemento.getId().getMedMatCodigoEquivalente());
		elemento.setId(id);
	}

	@Override
	protected DetachedCriteria pesquisarCriteria(AfaMedicamento medicamento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaMedicamentoEquivalente.class);
		
		criteria.createAlias("medicamentoEquivalente"/*AfaMedicamentoEquivalente.Fields.MEDICAMENTO_EQUIVALENTE.toString()*/, "medicamentoEquivalente");
		criteria.createAlias("medicamentoEquivalente."+AfaMedicamento.Fields.UNIDADE_MEDIDA_MEDICAS.toString(), "mpmUnidadeMedidaMedicas");
		criteria.createAlias("medicamentoEquivalente."+AfaMedicamento.Fields.TPR.toString(), "tipoApresentacaoMedicamento");
		criteria.createAlias("rapServidores"/*AfaMedicamentoEquivalente.Fields.SERVIDOR.toString()*/, "rapServidores");
		criteria.createAlias("rapServidores."+RapServidores.Fields.PESSOA_FISICA.toString()/*AfaMedicamentoEquivalente.Fields.SERVIDOR.toString()*/, "pessoaFisica");

		criteria.add(Restrictions.eq(
				AfaMedicamentoEquivalente.Fields.MED_MAT_CODIGO.toString(), medicamento.getMatCodigo()));

		return criteria;
	}

}
