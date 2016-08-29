package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BigDecimalType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.faturamento.vo.ItensRealizadosIndividuaisVO;
import br.gov.mec.aghu.model.AipCidades;
import br.gov.mec.aghu.model.AipEnderecosPacientes;
import br.gov.mec.aghu.model.AipLogradouros;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.AipUfs;
import br.gov.mec.aghu.model.FatContaApac;
import br.gov.mec.aghu.model.FatFormaOrganizacao;
import br.gov.mec.aghu.model.FatGrupo;
import br.gov.mec.aghu.model.FatItemContaApac;
import br.gov.mec.aghu.model.FatItemEspelhoContaApac;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatSubGrupo;
import br.gov.mec.aghu.model.FatVlrItemProcedHospComps;
import br.gov.mec.aghu.core.utils.DateMaker;

public class FatItemEspelhoContaApacDAO extends	br.gov.mec.aghu.core.persistence.dao.BaseDao<FatItemEspelhoContaApac> {

	private static final long serialVersionUID = 7880071090375066220L;
	
	public List<ItensRealizadosIndividuaisVO> listarItensRealizadosIndividuaisApacs(Integer ano, Integer mes, Long procedInicial, Long procedFinal) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatItemEspelhoContaApac.class, "IEC");
		criteria.createAlias(FatItemEspelhoContaApac.Fields.FAT_ITEM_CONTA_APAC.toString(), "ICA");
		criteria.createAlias(FatItemEspelhoContaApac.Fields.ITENS_PROCED_HOSPITALAR.toString(), "IPH");
		criteria.createAlias("ICA."+FatItemContaApac.Fields.FAT_CONTA_APAC.toString(), "CAP");
		criteria.createAlias("CAP."+FatContaApac.Fields.PACIENTE.toString(), "PAC", Criteria.LEFT_JOIN);
		criteria.createAlias("PAC."+AipPacientes.Fields.ENDERECOS.toString(), "END", Criteria.LEFT_JOIN);
		criteria.createAlias("END."+AipEnderecosPacientes.Fields.CIDADE.toString(), "CID", Criteria.LEFT_JOIN);
		criteria.createAlias("CID."+AipCidades.Fields.UF.toString(), "UF", Criteria.LEFT_JOIN);
		criteria.createAlias("END."+AipEnderecosPacientes.Fields.AIP_LOGRADOURO.toString(), "LGD", Criteria.LEFT_JOIN);
		criteria.createAlias("LGD."+AipLogradouros.Fields.CIDADE.toString(), "CID1", Criteria.LEFT_JOIN);
		criteria.createAlias("CID1."+AipCidades.Fields.UF.toString(), "UF1", Criteria.LEFT_JOIN);
		criteria.createAlias("IPH."+FatItensProcedHospitalar.Fields.VALORES_ITEM_PROCD_HOSP_COMPS.toString(), "IPC");
		criteria.createAlias("IPH."+FatItensProcedHospitalar.Fields.FAT_FORMA_ORGANIZACAO.toString(), "FOG");
		criteria.createAlias("FOG."+FatFormaOrganizacao.Fields.FAT_SUB_GRUPO.toString(), "SGR");
		criteria.createAlias("SGR."+FatSubGrupo.Fields.FAT_GRUPO.toString(), "GRP");

		criteria.setProjection(Projections.projectionList()
				.add(Projections.groupProperty("SGR."+FatSubGrupo.Fields.ID_GRP_SEQ.toString()), "grupoSeq")
				.add(Projections.groupProperty("GRP."+FatGrupo.Fields.DESCRICAO.toString()), "grupo")
				.add(Projections.groupProperty("SGR."+FatSubGrupo.Fields.ID_SUB_GRUPO.toString()), "subGrupoSeq")
				.add(Projections.groupProperty("SGR."+FatSubGrupo.Fields.DESCRICAO.toString()), "subGrupo")
				.add(Projections.groupProperty("FOG."+FatFormaOrganizacao.Fields.ID_CODIGO.toString()), "formaOrganizacaoCodigo")
				.add(Projections.groupProperty("FOG."+FatFormaOrganizacao.Fields.DESCRICAO.toString()), "formaOrganizacao")
				.add(Projections.groupProperty("IEC."+FatItemEspelhoContaApac.Fields.PROCEDIMENTO_HOSP.toString()), "procedimentoHospitalarCod")
				.add(Projections.groupProperty("IPH."+FatItensProcedHospitalar.Fields.DESCRICAO.toString()), "procedimentoHospitalarDesc")
				.add(Projections.sqlGroupProjection(
					"TO_CHAR( ica1_.DTHR_REALIZADO,'DD/MM/YYYY') as dthrRealz",
					"TO_CHAR( ica1_.DTHR_REALIZADO,'DD/MM/YYYY')",
				    new String[]{"dthrRealz"}, 
				    new Type[] {StringType.INSTANCE}), "dthrRealz")
				.add(Projections.groupProperty("PAC."+AipPacientes.Fields.PRONTUARIO.toString()), "prontuario")
				.add(Projections.groupProperty("PAC."+AipPacientes.Fields.CODIGO.toString()), "codigo")
				.add(Projections.groupProperty("PAC."+AipPacientes.Fields.NOME.toString()), "nome")
				.add(Projections.groupProperty("CAP."+FatContaApac.Fields.ATM_CODIGO.toString()), "apac")
				.add(Projections.groupProperty("CID."+AipCidades.Fields.NOME.toString()), "cidade")
				.add(Projections.groupProperty("UF."+AipUfs.Fields.SIGLA.toString()), "estado")
				.add(Projections.groupProperty("CID1."+AipCidades.Fields.NOME.toString()), "cidadeLgd")
				.add(Projections.groupProperty("UF1."+AipUfs.Fields.SIGLA.toString()), "estadoLgd")
				.add(Projections.sqlProjection("SUM(ipc11_.VLR_PROCEDIMENTO*ica1_.QUANTIDADE) as valorProcedimento",  new String[]{"valorProcedimento"}, new Type[] {BigDecimalType.INSTANCE}), "valorProcedimento")
				.add(Projections.sum("ICA."+FatItemContaApac.Fields.QUANTIDADE.toString()), "quantidade")
		);

		if(procedInicial != null || procedFinal != null) {
			criteria.add(Restrictions.between("IEC."+FatItemEspelhoContaApac.Fields.PROCEDIMENTO_HOSP.toString(), procedInicial!=null?Long.valueOf(procedInicial):0l, procedFinal!=null?Long.valueOf(procedFinal):9999999999l));	
		}

		StringBuffer sql = new StringBuffer(500);
		if(isOracle()) {
			sql.append("( end5_.seqp = (select endx_.seqp from AGH.AIP_ENDERECOS_PACIENTES endx_ where ");
			sql.append("(seqp = (select endx1_.seqp from AGH.AIP_ENDERECOS_PACIENTES endx1_ where endx1_.ind_padrao = 'S' and endx1_.pac_codigo = pac4_.CODIGO and rownum = 1)");
			sql.append("or (rownum = 1 and (select count(*) from AGH.AIP_ENDERECOS_PACIENTES endx1_ where endx1_.ind_padrao = 'S' and endx1_.pac_codigo = pac4_.CODIGO) = 0))");
			sql.append("and endx_.pac_codigo = pac4_.CODIGO");
			sql.append(") or end5_.seqp is null )");
		}
		else {
			sql.append("( end5_.seqp = (select endx_.seqp from AGH.AIP_ENDERECOS_PACIENTES endx_ where ");
			sql.append("(seqp = (select endx1_.seqp from AGH.AIP_ENDERECOS_PACIENTES endx1_ where endx1_.ind_padrao = 'S' and endx1_.pac_codigo = pac4_.CODIGO limit 1)");
			sql.append("or ((select count(*) from AGH.AIP_ENDERECOS_PACIENTES endx1_ where endx1_.ind_padrao = 'S' and endx1_.pac_codigo = pac4_.CODIGO) = 0))");
			sql.append("and endx_.pac_codigo = pac4_.CODIGO ");
			sql.append("limit 1) or end5_.seqp is null )");
		}

		criteria.add(Restrictions.sqlRestriction(sql.toString()));
		criteria.add(Restrictions.le("IPC."+FatVlrItemProcedHospComps.Fields.DT_INICIO_COMPETENCIA.toString(), DateMaker.obterData(ano, mes, 1, 0, 0)));
		criteria.add(Restrictions.or(Restrictions.ge("IPC."+FatVlrItemProcedHospComps.Fields.DT_FIM_COMPETENCIA.toString(), DateMaker.obterData(ano, mes, 1, 0, 0)), Restrictions.isNull("IPC."+FatVlrItemProcedHospComps.Fields.DT_FIM_COMPETENCIA.toString())));
		criteria.add(Restrictions.gt("IEC."+FatItemEspelhoContaApac.Fields.QUANTIDADE.toString(), 0));
		criteria.add(Restrictions.eq("IEC."+FatItemEspelhoContaApac.Fields.IND_CONSISTENTE.toString(), true));
		criteria.add(Restrictions.eq("CAP."+FatContaApac.Fields.CPE_ANO.toString(), ano.shortValue()));
		criteria.add(Restrictions.eq("CAP."+FatContaApac.Fields.CPE_MES.toString(), mes.byteValue()));
		criteria.add(Restrictions.in("CAP."+FatContaApac.Fields.CPE_MODULO.toString(), new DominioModuloCompetencia[] { DominioModuloCompetencia.APAC, 
			DominioModuloCompetencia.APEX, DominioModuloCompetencia.APAP, DominioModuloCompetencia.APAF, DominioModuloCompetencia.APAT, 
			DominioModuloCompetencia.APAN, DominioModuloCompetencia.APRE }));

		criteria.setResultTransformer(Transformers.aliasToBean(ItensRealizadosIndividuaisVO.class));
		
		return executeCriteria(criteria);

	}
}
