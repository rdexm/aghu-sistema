package br.gov.mec.aghu.compras.dao;

import java.util.Arrays;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoItenConta;
import br.gov.mec.aghu.faturamento.vo.RelacaoDeOrtesesProtesesVO;
import br.gov.mec.aghu.model.FatConvGrupoItemProced;
import br.gov.mec.aghu.model.FatItemContaHospitalar;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.SceRmrPaciente;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;

/**
 * Responsavel por motar a busca de sistemas por nome.<br>
 * Usando o valor de busca para filtar nos campos:<br>
 * <ol>
 * 	<li>SIGLA: MatchMode.EXACT</li>
 * 	<li>ou NOME: MatchMode.ANYWHERE</li>
 * </ol><br>
 * 
 * Classe concretas de build devem sempre ter modificador de acesso Default.<br>
 * 
 * <p>Exemplo de uso do QueryBuilder para org.hibernate.criterion.DetachedCriteria.
 * Com passagem dos filtros no proprio metodo build.</p>
 * 
 */
class DadosFornecedorICHQueryBuilder extends QueryBuilder<DetachedCriteria> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1797731438413580142L;
	private RelacaoDeOrtesesProtesesVO relacaoDeOrtesesProtesesVO;
	
	private static final String PONTO = ".";
	private static final String ALIAS_SCO_FORNECEDOR = "SFO";
	private static final String ALIAS_FAT_ITEM_CONTA_HOSPITALAR = "ICH";
	private static final String ALIAS_PACIENTES = "SRM";
	private static final String ALIAS_PROCEDIMENTO_HOSPITALAR_INTERNO = "PHI";
	private static final String ALIAS_CONV_GRUPO_ITENS_PROCED = "CGI";
	private static final String ALIAS_ITEM_PROCED_HOSPITALAR = "IPH";

	@Override
	protected DetachedCriteria createProduct() {
		return DetachedCriteria.forClass(ScoFornecedor.class, ALIAS_SCO_FORNECEDOR);
	}

	@Override
	protected void doBuild(DetachedCriteria criteria) {
		criteria.createAlias(ALIAS_SCO_FORNECEDOR+ PONTO + ScoFornecedor.Fields.PACIENTES.toString(), ALIAS_PACIENTES, JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_PACIENTES + PONTO + SceRmrPaciente.Fields.FAT_ITEM_CONTA_HOSPITALAR.toString(), ALIAS_FAT_ITEM_CONTA_HOSPITALAR, JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_FAT_ITEM_CONTA_HOSPITALAR + PONTO + FatItemContaHospitalar.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(), ALIAS_PROCEDIMENTO_HOSPITALAR_INTERNO, JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_PROCEDIMENTO_HOSPITALAR_INTERNO + PONTO + FatProcedHospInternos.Fields.CONV_GRUPO_ITENS_PROCED.toString(), ALIAS_CONV_GRUPO_ITENS_PROCED, JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_CONV_GRUPO_ITENS_PROCED + PONTO + FatConvGrupoItemProced.Fields.ITEM_PROCED_HOSPITALAR.toString(), ALIAS_ITEM_PROCED_HOSPITALAR, JoinType.INNER_JOIN);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.groupProperty(ALIAS_PACIENTES + PONTO + SceRmrPaciente.Fields.SEQ.toString()), RelacaoDeOrtesesProtesesVO.Fields.SRM_SEQ.toString())
				.add(Projections.groupProperty(ALIAS_FAT_ITEM_CONTA_HOSPITALAR + PONTO + FatItemContaHospitalar.Fields.DTHR_REALIZADO.toString()), RelacaoDeOrtesesProtesesVO.Fields.DATA_UTL_DT.toString())
				.add(Projections.groupProperty(ALIAS_SCO_FORNECEDOR+ PONTO + ScoFornecedor.Fields.CGC.toString()), RelacaoDeOrtesesProtesesVO.Fields.CGC_FORNECEDOR.toString())
				);
		
		criteria.add(Restrictions.eq(ALIAS_CONV_GRUPO_ITENS_PROCED + PONTO + FatConvGrupoItemProced.Fields.CPG_CPH_CSP_CNV_CODIGO.toString(), relacaoDeOrtesesProtesesVO.getCpgCphCspCnvCodigo()));
		criteria.add(Restrictions.eq(ALIAS_CONV_GRUPO_ITENS_PROCED + PONTO + FatConvGrupoItemProced.Fields.CPG_CPH_CSP_SEQ.toString(), relacaoDeOrtesesProtesesVO.getCpgCphCspSeq()));
		criteria.add(Restrictions.eq(ALIAS_CONV_GRUPO_ITENS_PROCED + PONTO + FatConvGrupoItemProced.Fields.IPH_PHO_SEQ.toString(), relacaoDeOrtesesProtesesVO.getIphPhoSeq()));
		criteria.add(Restrictions.eq(ALIAS_CONV_GRUPO_ITENS_PROCED + PONTO + FatConvGrupoItemProced.Fields.IPH_SEQ.toString(), relacaoDeOrtesesProtesesVO.getIphSeq()));
		criteria.add(Restrictions.eq(ALIAS_FAT_ITEM_CONTA_HOSPITALAR + PONTO + FatItemContaHospitalar.Fields.CTH_SEQ.toString(), relacaoDeOrtesesProtesesVO.getCthSeq()));
		criteria.add(Restrictions.eq(ALIAS_PROCEDIMENTO_HOSPITALAR_INTERNO + PONTO + FatProcedHospInternos.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.isNotNull(ALIAS_FAT_ITEM_CONTA_HOSPITALAR + PONTO + FatItemContaHospitalar.Fields.IPS_NUMERO.toString()));
		criteria.add(Restrictions.isNotNull(ALIAS_FAT_ITEM_CONTA_HOSPITALAR + PONTO + FatItemContaHospitalar.Fields.IPS_RMP_SEQ.toString()));
		criteria.add(Restrictions.in(ALIAS_FAT_ITEM_CONTA_HOSPITALAR + PONTO + FatItemContaHospitalar.Fields.IND_SITUACAO.toString(), Arrays.asList(DominioSituacaoItenConta.P, DominioSituacaoItenConta.V)));

		criteria.setResultTransformer(Transformers.aliasToBean(RelacaoDeOrtesesProtesesVO.class));
	}
	
	public DetachedCriteria build(RelacaoDeOrtesesProtesesVO relacaoDeOrtesesProtesesVO) {
		
		this.relacaoDeOrtesesProtesesVO = relacaoDeOrtesesProtesesVO;
		
		return super.build();
	}

}
