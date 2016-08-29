package br.gov.mec.aghu.centrocusto.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.FccCentroCustosJn;

public class FccCentroCustosJnDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FccCentroCustosJn> {

	
	private static final long serialVersionUID = 8707116362840800646L;

	/**
	 * @param codigo
	 * @return
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public Object[] obterValoresAnterioresCentroCusto(Integer codigo) {
		DetachedCriteria criteriaDadosAnteriores = DetachedCriteria.forClass(FccCentroCustos.class);
		
		criteriaDadosAnteriores.add(Restrictions.eq(
				FccCentroCustos.Fields.CODIGO.toString(), codigo));
		
		criteriaDadosAnteriores
				.setProjection(Projections
						.projectionList()
						.add(
								Projections
										.property(FccCentroCustos.Fields.CODIGO
												.toString()))
						.add(
								Projections
										.property(FccCentroCustos.Fields.DESCRICAO
												.toString()))
						.add(
								Projections
										.property(FccCentroCustos.Fields.GRUPO_CC_CODIGO
												.toString()))
						.add(
								Projections
										.property(FccCentroCustos.Fields.GRUPO_CC_SEQ
												.toString()))
						.add(
								Projections
										.property(FccCentroCustos.Fields.AREA
												.toString()))
						.add(
								Projections
										.property(FccCentroCustos.Fields.PESO
												.toString()))
						.add(
								Projections
										.property(FccCentroCustos.Fields.ABSENTEISMO_SMO
												.toString()))
						.add(
								Projections
										.property(FccCentroCustos.Fields.CC_SUPERIOR
												.toString()))
						.add(
								Projections
										.property(FccCentroCustos.Fields.SOLICITA_COMPRA
												.toString()))
						.add(
								Projections
										.property(FccCentroCustos.Fields.AVALIACAO_TECNICA
												.toString()))
						.add(
								Projections
										.property(FccCentroCustos.Fields.APROVA_SOLICITACAO
												.toString()))
						.add(
								Projections
										.property(FccCentroCustos.Fields.CC_RH
												.toString()))
						.add(
								Projections
										.property(FccCentroCustos.Fields.NRO_VAGAS
												.toString()))
						.add(
								Projections
										.property(FccCentroCustos.Fields.SERVIDOR_MATRICULA
												.toString()))
						.add(
								Projections
										.property(FccCentroCustos.Fields.SERVIDOR_VINCULO
												.toString()))
						.add(
								Projections
										.property(FccCentroCustos.Fields.COTA_HORA_EXTRA
												.toString()))
						.add(
								Projections
										.property(FccCentroCustos.Fields.NOME_REDUZIDO
												.toString()))
						.add(
								Projections
										.property(FccCentroCustos.Fields.ORGANOGRAMA_CF
												.toString()))
						.add(
								Projections
										.property(FccCentroCustos.Fields.REGISTRO_FUNCIONAMENTO
												.toString()))
						.add(
								Projections
										.property(FccCentroCustos.Fields.LOGRADOURO
												.toString()))
						.add(
								Projections
										.property(FccCentroCustos.Fields.NRO_LOGRADOURO
												.toString()))
						.add(
								Projections
										.property(FccCentroCustos.Fields.COMPLEMENTO_LOGRADOURO
												.toString()))
						.add(
								Projections.property(FccCentroCustos.Fields.CEP
										.toString()))
						.add(
								Projections
										.property(FccCentroCustos.Fields.BAIRRO
												.toString()))
						.add(
								Projections
										.property(FccCentroCustos.Fields.CIDADE_CODIGO
												.toString()))
						.add(
								Projections
										.property(FccCentroCustos.Fields.DDD_FONE
												.toString()))
						.add(
								Projections
										.property(FccCentroCustos.Fields.FONE
												.toString()))
						.add(
								Projections
										.property(FccCentroCustos.Fields.DDD_FAX
												.toString()))
						.add(
								Projections.property(FccCentroCustos.Fields.FAX
										.toString()))
						.add(
								Projections
										.property(FccCentroCustos.Fields.CAIXA_POSTAL
												.toString()))
						.add(
								Projections
										.property(FccCentroCustos.Fields.EMAIL
												.toString()))
						.add(
								Projections
										.property(FccCentroCustos.Fields.HOME_PAGE
												.toString()))
						.add(
								Projections
										.property(FccCentroCustos.Fields.TIPO_DESPESA
												.toString()))
						.add(
								Projections
										.property(FccCentroCustos.Fields.SITUACAO
												.toString()))
						.add(
								Projections
										.property(FccCentroCustos.Fields.IND_AREA
												.toString())));

		return (Object[]) this.executeCriteriaUniqueResult(criteriaDadosAnteriores);
	}
	
}
