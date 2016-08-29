package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;

class PesquisarMateriaisConsumidosQueryBuilder extends QueryBuilder<Query> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4438597813228725754L;
	
	private Integer cirurgiaSeq;
	private EntityManager entityManager;
	private List<Object[]> retorno;
	
	@Override
	protected Query createProduct() {


		StringBuilder sql = new StringBuilder(5000);

		sql.append("  SELECT  rop.SEQ       SEQ_REQUISICAO, "
				+ "         CASE iro.IND_REQUERIDO "
				+ "             when 'NOV' then '(Nova Solicitação de Material)' "
				+ "             when 'ADC' then '(Material Adicionado pelo Usuário)' "
				+ "             else       iph.COD_TABELA || ' - ' || iph.DESCRICAO "
				+ "         END ITEM_SUS, "
				+ "         COALESCE(mio.QTD_SOLC, iro.QTD_SOLC)     QTD_AUTORIZADA, "
				+ "         ( SELECT  COALESCE(SUM(idp2.QTDE_DISPENSA), 0) "
				+ "         FROM agh.MBC_CIRURGIAS                  crg2  "
				+ " 		JOIN agh.MBC_AGENDAS                    agd2  "
				+ "                 ON agd2.SEQ = crg2.AGD_SEQ            "
				+ "			JOIN agh.SCE_DISPENSA_MAT_OPS           dmo2  "
				+ "                 ON dmo2.CRG_SEQ = crg2.SEQ            "
				+ "			JOIN agh.SCE_ITEM_DISP_MAT_OPS          idp2  "
				+ "                 ON idp2.DMO_SEQ = dmo2.SEQ            "
				+ "			JOIN agh.SCE_ESTQ_ALMOXS                eal2  "
				+ "                 ON eal2.SEQ = idp2.EAL_SEQ            "
				+ "         JOIN agh.SCO_MATERIAIS                  mat2  "
				+ "                 ON mat2.CODIGO  = eal2.MAT_CODIGO     "
				+ "         WHERE crg2.SEQ    = crg.SEQ                   "
				+ "             AND    mat2.CODIGO = mat.CODIGO           "
				+ "             AND    dmo2.IND_SITUACAO = 'D'            "
				+ "             AND    idp2.IND_SITUACAO = 'D') QTD_DISPENSADA, "
				+ "         CASE iro.IND_REQUERIDO                        "
				+ "             when 'NOV' then case                      "
				+ "                 when COALESCE(mat.CODIGO, -1) = -1    "
				+ "					then COALESCE(iro.ESPEC_NOVO_MAT,     "
				+ "                 iro.SOLC_NOVO_MAT)                    "
				+ "                 else mat.CODIGO||' - '||mat.NOME      "
				+ "             end               "
				+ "             when 'ADC' then mat.CODIGO||' - '||mat.NOME "
				+ "             else            mat.CODIGO||' - '||mat.NOME "
				+ "         END MATERIAL,                                   "
				+ "         CASE iro.IND_REQUERIDO                          "
				+ "             WHEN 'NOV' then 1                           "
				+ "             WHEN 'ADC' then 2  "
				+ "             else 3             "
				+ "         END as reqOrder        "
				+ "     FROM  agh.MBC_CIRURGIAS                  crg  "
				+ "     JOIN agh.MBC_AGENDAS                    agd   "
				+ "             ON agd.SEQ     = crg.AGD_SEQ          "
				+ "     JOIN agh.MBC_REQUISICAO_OPMES           rop   "
				+ "             ON rop.AGD_SEQ = agd.SEQ              "
				+ "     JOIN agh.MBC_ITENS_REQUISICAO_OPMES     iro   "
				+ "             ON iro.ROP_SEQ = rop.SEQ              "
				+ "     LEFT JOIN agh.FAT_ITENS_PROCED_HOSPITALAR iph "
				+ "             ON iph.PHO_SEQ = iro.IPH_PHO_SEQ      "
				+ "				and iph.SEQ = iro.IPH_SEQ             "
				+ "     LEFT JOIN agh.MBC_MATERIAIS_ITEM_OPMES    mio "
				+ "             ON mio.IRO_SEQ = iro.SEQ  "
				+ "             and mio.QTD_SOLC > 0   "
				+ "     LEFT JOIN "
				+ "         agh.SCO_MATERIAIS               mat  "
				+ "             ON mat.CODIGO  = mio.MAT_CODIGO   "
				+ "     WHERE crg.SEQ = :cirurgiaSeq "
				+ "         AND    iro.IND_REQUERIDO  <> 'NRQ' "
				+ "         AND    iro.IND_AUTORIZADO = 'S' "
				+ "     UNION "
				+ "     SELECT  SEQ_REQUISICAO, "
				+ "         ITEM_SUS        , "
				+ "         QTD_AUTORIZADA        , "
				+ "         SUM(QTD_DISPENSADA_0) QTD_DISPENSADA        , "
				+ "         MATERIAL        , "
				+ "         reqOrder   "
				+ "     FROM  (  SELECT rop.SEQ SEQ_REQUISICAO        , "
				+ "             COALESCE(null, '(Materia Dispensado na Cirurgia)') ITEM_SUS, "
				+ "             0 QTD_AUTORIZADA, "
				+ "             COALESCE(idp.QTDE_DISPENSA, "
				+ "             0)                     QTD_DISPENSADA_0        , "
				+ "             mat.CODIGO || ' - ' || mat.NOME                   MATERIAL        , "
				+ "             4                                                 reqOrder   "
				+ "         FROM agh.MBC_CIRURGIAS         crg   "
				+ "         JOIN agh.SCE_DISPENSA_MAT_OPS  dmo  "
				+ "                 ON crg.SEQ = dmo.CRG_SEQ   "
				+ "         JOIN agh.SCE_ITEM_DISP_MAT_OPS idp  "
				+ "                 ON dmo.SEQ = idp.DMO_SEQ  "
				+ "         JOIN agh.SCE_ESTQ_ALMOXS       eal  "
				+ "                 ON eal.SEQ = idp.EAL_SEQ   "
				+ "         JOIN agh.SCO_MATERIAIS         mat  "
				+ "                 ON mat.CODIGO = eal.MAT_CODIGO   "
				+ "         JOIN agh.MBC_AGENDAS           agd  "
				+ "                 ON crg.AGD_SEQ = agd.SEQ   "
				+ "         JOIN agh.MBC_REQUISICAO_OPMES  rop  "
				+ "                 ON rop.AGD_SEQ = agd.SEQ   "
				+ "         WHERE crg.SEQ = :cirurgiaSeq    "
				+ "             AND    mat.CODIGO not in ( "
				+ "                 select  mio.MAT_CODIGO "
				+ "                 from agh.MBC_ITENS_REQUISICAO_OPMES iro "
				+ "					join agh.MBC_MATERIAIS_ITEM_OPMES   mio  "
				+ "                         ON mio.IRO_SEQ = iro.SEQ  "
				+ "                         and mio.QTD_SOLC > 0  "
				+ "                 where iro.ROP_SEQ = rop.SEQ "
				+ "             ) ) subq   "
				+ "     GROUP BY "
				+ "         SEQ_REQUISICAO, "
				+ "         ITEM_SUS,       "
				+ "         QTD_AUTORIZADA, "
				+ "         MATERIAL,       "
				+ "         reqOrder        "
				+ "     ORDER BY            "
				+ "         reqOrder,       "
				+ "         ITEM_SUS,       "
				+ "         MATERIAL  ");		
		
		Query query = this.getEntityManager().createNativeQuery(sql.toString());
		query.setParameter("cirurgiaSeq", cirurgiaSeq);


		return query;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void doBuild(Query query) {
		this.setRetorno(query.getResultList());
	}
	  
	public Integer getCirurgiaSeq() {
		return cirurgiaSeq;
	}
	
	public void setCirurgiaSeq(Integer cirurgiaSeq) {
		this.cirurgiaSeq = cirurgiaSeq;
	}
	
	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public List<Object[]> getRetorno() {
		return retorno;
	}

	public void setRetorno(List<Object[]> retorno) {
		this.retorno = retorno;
	}

}
