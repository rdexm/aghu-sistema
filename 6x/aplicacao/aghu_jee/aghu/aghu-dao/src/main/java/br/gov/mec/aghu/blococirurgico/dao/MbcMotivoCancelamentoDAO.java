package br.gov.mec.aghu.blococirurgico.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.blococirurgico.vo.MotivoCancelamentoVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioPacCirurgiasCanceladasVO;
import br.gov.mec.aghu.casca.model.Perfil;
import br.gov.mec.aghu.casca.model.PerfisUsuarios;
import br.gov.mec.aghu.casca.model.Usuario;
import br.gov.mec.aghu.dominio.DominioMotivoCancelamento;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcMotivoCancelamento;
import br.gov.mec.aghu.model.MbcPerfilCancelamento;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgias;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MbcProfCirurgias;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;

public class MbcMotivoCancelamentoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcMotivoCancelamento> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7973021078016515182L;
	
	
	
	protected DetachedCriteria obterCriteria() {
		return DetachedCriteria.forClass(MbcMotivoCancelamento.class);
	}
	
	protected DetachedCriteria obterCriterioConsulta(DetachedCriteria criteria, 
			Short codigo, String descricao, Boolean erroAgendamento, 
			Boolean destSr, DominioMotivoCancelamento classificacao, DominioSituacao situacao) {
		
		if(codigo != null) {
			criteria.add(Restrictions.eq(
					MbcMotivoCancelamento.Fields.SEQ.toString(), codigo));
		}
		
		if(StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(
					MbcMotivoCancelamento.Fields.DESCRICAO.toString(),	
					descricao, MatchMode.ANYWHERE));
		}

		if(erroAgendamento != null) {
			criteria.add(Restrictions.eq(
					MbcMotivoCancelamento.Fields.ERRO_AGENDAMENTO.toString(), erroAgendamento));
		}
		
		if(destSr != null) {
			criteria.add(Restrictions.eq(
					MbcMotivoCancelamento.Fields.DEST_SR.toString(), destSr));
		}
		
		if(classificacao != null) {
			criteria.add(Restrictions.eq(
					MbcMotivoCancelamento.Fields.TIPO.toString(), classificacao));
		}
		
		if(situacao != null) {
			criteria.add(Restrictions.eq(
					MbcMotivoCancelamento.Fields.SITUACAO.toString(), situacao));
		}
		
		
		return criteria;
	}
	
	public List<MbcMotivoCancelamento> pesquisarMotivosCancelamento(
			Short codigo, String descricao, Boolean erroAgendamento, 
			Boolean destSr, DominioMotivoCancelamento classificacao, DominioSituacao situacao,
			Integer firstResult, Integer maxResults, String orderProperty, Boolean asc) {
		
		DetachedCriteria criteria = this.obterCriteria();
		criteria = this.obterCriterioConsulta(
				criteria, codigo, descricao, erroAgendamento, destSr, classificacao, situacao);
		
		criteria.addOrder(Order.asc(MbcMotivoCancelamento.Fields.DESCRICAO.toString()));
		
		return this.executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}
	
	
	public Long pesquisarMotivosCancelamentoCount(
			Short codigo, String descricao, Boolean erroAgendamento, 
			Boolean destSr, DominioMotivoCancelamento classificacao, DominioSituacao situacao) {
		
		DetachedCriteria criteria = this.obterCriteria();
		criteria = this.obterCriterioConsulta(
				criteria, codigo, descricao, erroAgendamento, destSr, classificacao, situacao);
		
		return this.executeCriteriaCount(criteria);
	}
	
	
	public List<MbcMotivoCancelamento> listarMotivosCancelamentosCadastrados(String descricao) {
		DetachedCriteria criteria = this.obterCriteria();
		
		criteria.add(Restrictions.eq(MbcMotivoCancelamento.Fields.DESCRICAO.toString(), descricao));
		
		return this.executeCriteria(criteria);
	}
	
	public List<MotivoCancelamentoVO> pesquisarMotivoCancelamentoVO(String nomeUsuario) {
		StringBuilder hql = new StringBuilder(300);
		
		hql.append("select distinct new ")
			.append(MotivoCancelamentoVO.class.getName())
			.append("(mtc.")
			.append(MbcMotivoCancelamento.Fields.DESCRICAO.toString())
			.append(", mtc.")
			.append(MbcMotivoCancelamento.Fields.SEQ.toString())
			.append(')')
			
			.append(" from ")
			.append(MbcPerfilCancelamento.class.getName())
			.append(" as pic, ")
			.append(MbcMotivoCancelamento.class.getName())
			.append(" as mtc, ")
			.append(PerfisUsuarios.class.getName())
			.append(" as pfu, ")
			.append(Perfil.class.getName())
			.append(" as pef, ")
			.append(Usuario.class.getName())
			.append(" as usr, ")
			.append(RapServidores.class.getName())
			.append(" as ser")
					
			.append(" where ser.")
			.append(RapServidores.Fields.USUARIO.toString())
			.append(" = :nomeUsuario")
			
			.append(" and ser.")
			.append(RapServidores.Fields.USUARIO.toString())
			.append(" = usr.")
			.append(Usuario.Fields.LOGIN.toString())
					
			.append(" and pfu.")
			.append(PerfisUsuarios.Fields.USUARIO.toString())
			.append(" = usr")
			
			.append(" and pfu.")
			.append(PerfisUsuarios.Fields.PERFIL.toString())
			.append(" = pef")
			
			.append(" and pic.")
			.append(MbcPerfilCancelamento.Fields.PER_NOME)
			.append(" = pef.")
			.append(Perfil.Fields.NOME.toString())
			
			.append(" and pic.")
			.append(MbcPerfilCancelamento.Fields.MBC_MOTIVO_CANCELAMENTO.toString())
			.append(" = mtc")
			
			.append(" and mtc.situacao = '")
			.append(DominioSituacao.A.toString())
			.append('\'')
			
			.append(" order by mtc.")
			.append(MbcMotivoCancelamento.Fields.DESCRICAO.toString());
			
		Query query = createQuery(hql.toString());
		
		query.setParameter("nomeUsuario", nomeUsuario);
		return query.getResultList();	
	}	

	public List<RelatorioPacCirurgiasCanceladasVO> obterPacientesCirurgiasCancMotivo(
			Short unidadeFuncional, Date dataInicial,Date dataFinal) {
		
		String ponto	= ".";
		String aliasMtc = "mtc"; // MBC_MOTIVO_CANCELAMENTO         MTC
		String aliasPac = "pac"; // AIP_PACIENTES 					PAC
		String aliasEsp = "esp"; // AGH_ESPECIALIDADES 				ESP
		String aliasPes = "pes"; // RAP_PESSOAS_FISICAS 			PES
		String aliasSer = "ser"; // RAP_SERVIDORES 					SER
		String aliasPcg = "pcg"; // MBC_PROF_CIRURGIAS 				PCG
		String aliasPci = "pci"; // MBC_PROCEDIMENTO_CIRURGICOS 	PCI
		String aliasPpc = "ppc"; // MBC_PROC_ESP_POR_CIRURGIAS 		PPC
		String aliasCnv = "cnv"; // FAT_CONV_SAUDE_PLANOS 	 		CSP
		String aliasCrg = "crg"; // MBC_CIRURGIAS 					CRG
	//	String aliasVps = "vps"; // V_RAP_PESSOA_SERVIDOR 			VPS
	//	String aliasEcr = "ecr"; // MBC_EXTRATO_CIRURGIAS 			ECR
	
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class, aliasCrg);

		Projection projection = Projections.projectionList()
		.add(Projections.property(aliasCrg + ponto + MbcCirurgias.Fields.SEQ.toString()), RelatorioPacCirurgiasCanceladasVO.Fields.CRG_SEQ.toString())
		.add(Projections.property(aliasCrg + ponto + MbcCirurgias.Fields.UNF_SEQ.toString()), RelatorioPacCirurgiasCanceladasVO.Fields.UNF_SEQ.toString())
		.add(Projections.property(aliasMtc + ponto + MbcMotivoCancelamento.Fields.DESCRICAO.toString()), RelatorioPacCirurgiasCanceladasVO.Fields.MOTIVO_CANC.toString())
		//.add(Projections.property(aliasEcr + ponto + MbcExtratoCirurgia.Fields.CRIADO_EM.toString()), RelatorioPacCirurgiasCanceladasVO.Fields.DT_CANCEL.toString())
		.add(Projections.property(aliasCrg + ponto + MbcCirurgias.Fields.DTHR_INICIO_CIRG.toString()), RelatorioPacCirurgiasCanceladasVO.Fields.DT_CIRURGIA.toString())
		.add(Projections.property(aliasCrg + ponto + MbcCirurgias.Fields.SCI_SEQP.toString()), RelatorioPacCirurgiasCanceladasVO.Fields.SALA.toString())
		.add(Projections.property(aliasPac + ponto + AipPacientes.Fields.NOME.toString()), RelatorioPacCirurgiasCanceladasVO.Fields.PAC_NOME.toString())
		.add(Projections.property(aliasCrg + ponto + MbcCirurgias.Fields.COMPLEMENTO_CANC.toString()), RelatorioPacCirurgiasCanceladasVO.Fields.COMPLEMENTO_CANC.toString())
		//.add(Projections.property(aliasVps + ponto + VRapPessoaServidor.Fields.NOME.toString()), RelatorioPacCirurgiasCanceladasVO.Fields.CANCELADO_POR.toString())
		.add(Projections.property(aliasEsp + ponto + AghEspecialidades.Fields.SIGLA.toString()), RelatorioPacCirurgiasCanceladasVO.Fields.ESP_SIGLA.toString())
		.add(Projections.property(aliasPes + ponto + RapPessoasFisicas.Fields.NOME_USUAL.toString()), RelatorioPacCirurgiasCanceladasVO.Fields.NOME_USUAL.toString())
		.add(Projections.property(aliasPes + ponto + RapPessoasFisicas.Fields.NOME.toString()), RelatorioPacCirurgiasCanceladasVO.Fields.NOME.toString())
		.add(Projections.property(aliasPci + ponto + MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString()), RelatorioPacCirurgiasCanceladasVO.Fields.PROCEDIMENTO_PRINCIPAL.toString())
		.add(Projections.property(aliasPci + ponto + MbcProcedimentoCirurgicos.Fields.TIPO.toString()), RelatorioPacCirurgiasCanceladasVO.Fields.TIPO.toString())
		.add(Projections.property(aliasPac + ponto + AipPacientes.Fields.CODIGO.toString()), RelatorioPacCirurgiasCanceladasVO.Fields.PAC_CODIGO.toString())
		.add(Projections.property(aliasCrg + ponto + MbcCirurgias.Fields.DATA.toString()), RelatorioPacCirurgiasCanceladasVO.Fields.DATA.toString())
		.add(Projections.property(aliasCnv + ponto + FatConvenioSaude.Fields.DESCRICAO.toString()), RelatorioPacCirurgiasCanceladasVO.Fields.CONV_PAGADOR.toString());

		criteria.createAlias(aliasCrg + ponto + MbcCirurgias.Fields.MOTIVO_CANCELAMENTO.toString(), aliasMtc);
		criteria.createAlias(aliasCrg + ponto + MbcCirurgias.Fields.PACIENTE.toString(), aliasPac);
		criteria.createAlias(aliasCrg + ponto + MbcCirurgias.Fields.ESPECIALIDADE.toString(), aliasEsp);
		criteria.createAlias(aliasCrg + ponto + MbcCirurgias.Fields.SERVIDOR.toString() , aliasSer);
		criteria.createAlias(aliasSer + ponto + RapServidores.Fields.PESSOA_FISICA.toString(), aliasPes);
		criteria.createAlias(aliasCrg + ponto + MbcCirurgias.Fields.PROF_CIRURGIAS.toString(), aliasPcg);
		criteria.createAlias(aliasCrg + ponto + MbcCirurgias.Fields.PROC_CIRURGICO.toString(), aliasPci);
		criteria.createAlias(aliasCrg + ponto + MbcCirurgias.Fields.PROC_ESP_POR_CIRURGIAS.toString(), aliasPpc);
		criteria.createAlias(aliasCrg + ponto + MbcCirurgias.Fields.CONVENIO_SAUDE.toString(), aliasCnv);
		
		//WHERE
		criteria.add(Restrictions.between(aliasCrg + ponto + MbcCirurgias.Fields.DATA.toString(), dataInicial, dataFinal));
		criteria.add(Restrictions.or(Restrictions.eq(aliasCrg + ponto + MbcCirurgias.Fields.UNF_SEQ.toString(), unidadeFuncional), Restrictions.isNull(aliasCrg + ponto + MbcCirurgias.Fields.UNF_SEQ.toString())) );
		criteria.add(Restrictions.eq(aliasCrg + ponto + MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.CANC));
		criteria.add(Restrictions.eq(aliasPpc + ponto + MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString(), DominioSituacaoCirurgia.AGND));
		criteria.add(Restrictions.eq(aliasPpc + ponto + MbcProcEspPorCirurgias.Fields.SITUACAO.toString(), DominioSituacao.A));	
		criteria.add(Restrictions.eq(aliasPpc + ponto + MbcProcEspPorCirurgias.Fields.IND_PRINCIPAL.toString(), Boolean.TRUE));	
		criteria.add(Restrictions.eq(aliasPcg + ponto + MbcProfCirurgias.Fields.IND_RESPONSAVEL.toString(), Boolean.TRUE));	
//		criteria.add(Restrictions.eq(aliasEcr + ponto + MbcExtratoCirurgia.Fields.SERVIDOR.toString(), aliasVps + ponto + VRapPessoaServidor.Fields.SER_VIN_CODIGO.toString()));	
			
		criteria.setProjection(projection);
		criteria.setResultTransformer(Transformers.aliasToBean(RelatorioPacCirurgiasCanceladasVO.class));
		return executeCriteria(criteria);
	}
	
}
