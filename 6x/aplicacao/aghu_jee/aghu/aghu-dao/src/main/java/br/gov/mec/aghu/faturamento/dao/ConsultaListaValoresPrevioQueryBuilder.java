package br.gov.mec.aghu.faturamento.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.gov.mec.aghu.faturamento.vo.ValoresPreviaVO;
import br.gov.mec.aghu.model.FatAtoMedicoAih;
import br.gov.mec.aghu.model.FatEspelhoAih;
import br.gov.mec.aghu.model.FatFormaOrganizacao;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatVlrItemProcedHospComps;

@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse"})
public class ConsultaListaValoresPrevioQueryBuilder{
	
	StringBuilder hql = new StringBuilder(3000);
	private Integer cthSeq;
	private boolean isOracle;
	private static final String AGH = "AGH.";
	
	public ConsultaListaValoresPrevioQueryBuilder(Integer cthSeq, boolean isOracle){
		this.cthSeq = cthSeq;
		this.isOracle = isOracle;
	}
	
	public String obterConsulta(){
		
		this.hql.append(" SELECT");
		this.hql.append(" FOZ.SGR_GRP_SEQ GRUPO");
		this.hql.append(", FOZ.SGR_SUB_GRUPO SUB_GRUPO");
		this.hql.append(", FOZ.CODIGO CODIGO");
		this.hql.append(", FOZ.").append(FatFormaOrganizacao.Fields.DESCRICAO.name()).append(" DESCRICAO");
		this.hql.append(", SUM(AMA.").append(FatAtoMedicoAih.Fields.VALOR_SERV_HOSP.name()).append(") SH");
		this.hql.append(", SUM(AMA.").append(FatAtoMedicoAih.Fields.VALOR_SERV_PROF.name()).append(") SP");
		this.hql.append(" FROM ");
		this.hql.append(AGH).append("FAT_VLR_ITEM_PROCED_HOSP_COMPS VAL, ");
		this.hql.append(AGH).append("FAT_ITENS_PROCED_HOSPITALAR IPH, ");
		this.hql.append(AGH).append("FAT_ATOS_MEDICOS_AIH AMA, ");
		this.hql.append(AGH).append("FAT_FORMAS_ORGANIZACAO FOZ ");

		this.hql.append(" WHERE FOZ.SGR_GRP_SEQ = ");
		if(isOracle){
			this.hql.append(" SUBSTR(LPAD(AMA.").append(FatAtoMedicoAih.Fields.IPH_COD_SUS.name()).append(",10,'0'),0,2) ");
		}else{
			this.hql.append(" CAST(SUBSTRING(LPAD(CAST(AMA.").append(FatAtoMedicoAih.Fields.IPH_COD_SUS.name()).append(" AS VARCHAR),10,'0'),1,2) as int) ");
		}
		this.hql.append(" AND FOZ.SGR_SUB_GRUPO = ");
		if(isOracle){
			this.hql.append(" SUBSTR(LPAD(AMA.").append(FatAtoMedicoAih.Fields.IPH_COD_SUS.name()).append(",10,'0'),3,2) ");
		}else{
			 this.hql.append(" CAST(SUBSTR(LPAD(CAST(AMA.").append(FatAtoMedicoAih.Fields.IPH_COD_SUS.name()).append(" AS VARCHAR),10,'0'),4,2) as int)  ");
		}
		this.hql.append(" AND FOZ.CODIGO = ");
		if(isOracle){
			this.hql.append(" SUBSTR(LPAD(AMA.").append(FatAtoMedicoAih.Fields.IPH_COD_SUS.name()).append(",10,'0'),5,2) ");
		}else{
			this.hql.append(" CAST(SUBSTR(LPAD(CAST(AMA.").append(FatAtoMedicoAih.Fields.IPH_COD_SUS.name()).append(" AS VARCHAR),10,'0'),6,2) as int) ");
		}

		this.hql.append(" AND IPH.").append(FatItensProcedHospitalar.Fields.PHO_SEQ.name()).append(" = ");
		this.hql.append(" AMA.").append(FatAtoMedicoAih.Fields.IPH_PHO_SEQ.name());
		this.hql.append(" AND IPH.").append(FatItensProcedHospitalar.Fields.SEQ.name()).append(" = ");
		this.hql.append(" AMA.").append(FatAtoMedicoAih.Fields.IPH_SEQ.name());

		this.hql.append(" AND IPH.").append(FatItensProcedHospitalar.Fields.IND_SITUACAO.name()).append(" = :indSituacao ");

		this.hql.append(" AND AMA.").append(FatAtoMedicoAih.Fields.IPH_PHO_SEQ.name()).append(" = :parametroSistema ");

		this.hql.append(" AND VAL.").append(FatVlrItemProcedHospComps.Fields.IPH_PHO_SEQ.name()).append(" = ");
		this.hql.append(" IPH.").append(FatItensProcedHospitalar.Fields.PHO_SEQ.name());
		this.hql.append(" AND VAL.").append(FatVlrItemProcedHospComps.Fields.IPH_SEQ.name()).append(" = ");
		this.hql.append(" IPH.").append(FatItensProcedHospitalar.Fields.SEQ.name());

		this.hql.append(" AND VAL.").append(FatVlrItemProcedHospComps.Fields.DT_FIM_COMPETENCIA.name()).append(" IS NULL ");

		this.hql.append(" AND ");
		this.hql.append(" ( ");
		this.hql.append(" AMA.").append(FatAtoMedicoAih.Fields.VALOR_SERV_HOSP.name()).append(" > 0 OR");
		this.hql.append(" AMA.").append(FatAtoMedicoAih.Fields.VALOR_SERV_PROF.name()).append(" > 0 ");
		this.hql.append(" )");

		this.hql.append(" AND AMA.").append(FatAtoMedicoAih.Fields.EAI_CTH_SEQ.name()).append(" = :cthSeq");
		this.hql.append(" GROUP BY FOZ.SGR_GRP_SEQ, FOZ.SGR_SUB_GRUPO, FOZ.CODIGO, FOZ.");
		this.hql.append(FatFormaOrganizacao.Fields.DESCRICAO.name());
		
		this.hql.append(" UNION ");
		
		obterConsultaDois();
		
		return this.hql.toString();
	}
	
	public void obterConsultaDois(){
		
		this.hql.append(" SELECT");
		this.hql.append(" FOZ.SGR_GRP_SEQ GRUPO");
		this.hql.append(", FOZ.SGR_SUB_GRUPO SUB_GRUPO");
		this.hql.append(", FOZ.CODIGO CODIGO");
		this.hql.append(", FOZ.").append(FatFormaOrganizacao.Fields.DESCRICAO.name()).append(" DESCRICAO");
		this.hql.append(", FE.").append(FatEspelhoAih.Fields.VALOR_SH_REALIZ.name()).append(" SH");
		this.hql.append(", FE.").append(FatEspelhoAih.Fields.VALOR_SP_REALIZ.name()).append(" SP");
		this.hql.append(" FROM ");
		this.hql.append(AGH).append("FAT_ESPELHOS_AIH").append(" FE, ");
		this.hql.append(AGH).append("FAT_FORMAS_ORGANIZACAO").append(" FOZ ");
		
		this.hql.append(" WHERE FOZ.SGR_GRP_SEQ").append(" = ");
		if(isOracle){
			this.hql.append(" SUBSTR(LPAD(FE.").append(FatEspelhoAih.Fields.IPH_COD_SUS_REALIZ.name()).append(",10,'0'),0,2) ");
		}else{
			this.hql.append(" CAST(SUBSTRING(LPAD(CAST(FE.").append(FatEspelhoAih.Fields.IPH_COD_SUS_REALIZ.name()).append(" AS VARCHAR),10,'0'),1,2) as int) ");
		}
		
		this.hql.append(" AND FOZ.").append("SGR_SUB_GRUPO").append(" = ");
		if(isOracle){
			this.hql.append(" SUBSTR(LPAD(FE.").append(FatEspelhoAih.Fields.IPH_COD_SUS_REALIZ.name()).append(",10,'0'),3,2)  ");
		}else{
			this.hql.append(" CAST(SUBSTRING(LPAD(CAST(FE.").append(FatEspelhoAih.Fields.IPH_COD_SUS_REALIZ.name()).append(" AS VARCHAR),10,'0'),4,2) as int)  ");
		}
		this.hql.append(" AND FOZ.CODIGO = ");
		if(isOracle){
			this.hql.append(" SUBSTR(LPAD(FE.").append(FatEspelhoAih.Fields.IPH_COD_SUS_REALIZ.name()).append(",10,'0'),5,2) ");
		}else{
			this.hql.append(" CAST(SUBSTRING(LPAD(CAST(FE.").append(FatEspelhoAih.Fields.IPH_COD_SUS_REALIZ.name()).append(" AS VARCHAR),10,'0'),6,2) as int) ");
		}
		
		this.hql.append(" AND ");
		this.hql.append(" ( ");
		this.hql.append(" FE.").append(FatEspelhoAih.Fields.VALOR_SH_REALIZ.name()).append(" > 0 OR");
		this.hql.append(" FE.").append(FatEspelhoAih.Fields.VALOR_SP_REALIZ.name()).append(" > 0 ");
		this.hql.append(" )");
	
		this.hql.append(" AND FE.").append(FatEspelhoAih.Fields.CTH_SEQ.name()).append(" = :cthSeq");
		this.hql.append(" ORDER BY ");
		this.hql.append(" 1, 2, 3");

	}
		
	public List<ValoresPreviaVO> prepararListaValoresPreviaVO(List<Object[]> listaRetorno){
		List<ValoresPreviaVO> listaRetornoVO = new ArrayList<ValoresPreviaVO>();
		
		if(listaRetorno != null && !listaRetorno.isEmpty()){
			
				for (Object[] objects : listaRetorno) {

					ValoresPreviaVO vo = new ValoresPreviaVO();
	
					if(objects[0] != null){
						vo.setSgrGrpSeq(Short.parseShort(objects[0].toString()));
					}
					if(objects[1] != null){
						vo.setSgrSubGrupo(Byte.parseByte(objects[1].toString()));
					}
					if(objects[2] != null){
						vo.setCodigo(Byte.parseByte(objects[2].toString()));
					}
					if(objects[3] != null){
						vo.setDescricao(objects[3].toString());
					}
					if(objects[4] != null){
						vo.setValorServHosp(BigDecimal.valueOf(Double.parseDouble(objects[4].toString())));
					}
					if(objects[5] != null){
						vo.setValorServProf(BigDecimal.valueOf(Double.parseDouble(objects[5].toString())));
					}
					listaRetornoVO.add(vo);
				}
			}
		return listaRetornoVO;
	}

	public StringBuilder getHql() {
		return hql;
	}

	public void setHql(StringBuilder hql) {
		this.hql = hql;
	}

	public Integer getCthSeq() {
		return cthSeq;
	}

	public void setCthSeq(Integer cthSeq) {
		this.cthSeq = cthSeq;
	}

	public boolean isOracle() {
		return isOracle;
	}

	public void setOracle(boolean isOracle) {
		this.isOracle = isOracle;
	}
}
