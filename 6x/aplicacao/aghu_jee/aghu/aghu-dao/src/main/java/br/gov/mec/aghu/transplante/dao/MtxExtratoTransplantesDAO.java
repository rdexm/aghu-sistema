package br.gov.mec.aghu.transplante.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacaoTransplante;
import br.gov.mec.aghu.dominio.DominioTipoTransplanteCombo;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MtxExtratoTransplantes;
import br.gov.mec.aghu.model.MtxMotivoAlteraSituacao;
import br.gov.mec.aghu.model.MtxTransplantes;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.transplante.vo.ExtratoAlteracoesListaOrgaosVO;
import br.gov.mec.aghu.transplante.vo.FasesProntuarioVO;
import br.gov.mec.aghu.transplante.vo.FiltroTempoPermanenciaListVO;
import br.gov.mec.aghu.transplante.vo.GerarExtratoListaTransplantesVO;
import br.gov.mec.aghu.transplante.vo.RelatorioExtratoTransplantesPacienteVO;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;
import br.gov.mec.aghu.core.utils.DateUtil;

@SuppressWarnings("PMD.ExcessiveClassLength")
public class MtxExtratoTransplantesDAO extends BaseDao<MtxExtratoTransplantes> {

	private static final String MAS = "mas";
	private static final String PAC = "pac";
	private static final String TPR = "tpr";
	private static final String PES = "pes";
	private static final String SER = "ser";
	private static final String EXT = "ext";
	private static final String SER_DOT = "ser.";
	private static final String MAS_DOT = "mas.";
	private static final String PES_DOT = "pes.";
	private static final String EXT_DOT = "ext.";
	private static final String TPR_DOT = "tpr.";
	private static final String PAC_DOT = "pac.";
	private static final String MET = "MET";
	private static final String MET_PONTO = "MET.";	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3689220287478584470L;

	/**#41790 C6
	 * @author adrian.gois
	 * 
	 */
	public List<FasesProntuarioVO> consultarFasesProntuario(Integer trpSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MtxExtratoTransplantes.class);

		criteria.add(Restrictions.eq(MtxExtratoTransplantes.Fields.MTX_TRANSPLANTE_SEQ.toString(), trpSeq));
		Projection proj = Projections.projectionList()
				.add(Projections.distinct(Projections.property(MtxExtratoTransplantes.Fields.MTX_TRANSPLANTE_SEQ.toString())))
				.add(Projections.property(MtxExtratoTransplantes.Fields.MTX_TRANSPLANTE_SEQ.toString()),FasesProntuarioVO.Fields.TRANSPLANTE_SEQ.toString())
				.add(Projections.property(MtxExtratoTransplantes.Fields.DATA_OCORRENCIA.toString()), FasesProntuarioVO.Fields.DT_OCORRENCIA.toString())
				.add(Projections.property(MtxExtratoTransplantes.Fields.SITUACAO_TRANSPLANTE.toString()), FasesProntuarioVO.Fields.SITUACAO.toString());
				
		criteria.setProjection(proj);
		criteria.addOrder(Order.asc(MtxExtratoTransplantes.Fields.DATA_OCORRENCIA.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(FasesProntuarioVO.class));
		return executeCriteria(criteria);
	}
	
	public List<RelatorioExtratoTransplantesPacienteVO> pesquisarExtratoTransplantePorFiltros(Integer pacCodigo,FiltroTempoPermanenciaListVO filtro){
		DetachedCriteria criteria = DetachedCriteria.forClass(MtxExtratoTransplantes.class,EXT);

		criteria.createAlias(EXT_DOT+MtxExtratoTransplantes.Fields.SERVIDOR.toString(), SER);
		criteria.createAlias(SER_DOT+RapServidores.Fields.PESSOA_FISICA.toString(), PES);
		criteria.createAlias(EXT_DOT+MtxExtratoTransplantes.Fields.MTX_TRANSPLANTE.toString(), TPR);
		criteria.createAlias(TPR_DOT+MtxTransplantes.Fields.RECEPTOR.toString(), PAC);
		criteria.createAlias(EXT_DOT+MtxExtratoTransplantes.Fields.MTX_MOTIVO_ALTERA_SITUACAO.toString(), MAS,JoinType.LEFT_OUTER_JOIN);
		
		
		if(filtro!= null && filtro.getDataInicio() != null && filtro.getDataFim() != null){
			criteria.add(Restrictions.between(TPR_DOT+MtxTransplantes.Fields.DATA_INGRESSO.toString(), filtro.getDataInicio(),DateUtil.obterDataComHoraFinal(filtro.getDataFim())));
		}
		
		if(filtro!= null && filtro.getTipoTransplante() != null && filtro.getTipoTransplante().equals(DominioTipoTransplanteCombo.T)){
			criteria.add(Restrictions.isNotNull(TPR_DOT+MtxTransplantes.Fields.TIPO_TMO.toString()));
		}

		if(filtro!= null && filtro.getTipoTransplante() != null && filtro.getTipoTransplante().equals(DominioTipoTransplanteCombo.O)){
			criteria.add(Restrictions.isNotNull(TPR_DOT+MtxTransplantes.Fields.TIPO_ORGAO.toString()));
		}
		
		filtroExtratoPaciente(pacCodigo, filtro, criteria);
		
		Projection proj = Projections.projectionList()
				.add(Projections.property(PAC_DOT+AipPacientes.Fields.PRONTUARIO.toString()),RelatorioExtratoTransplantesPacienteVO.Fields.PRONTUARIO.toString())
				.add(Projections.property(PAC_DOT+AipPacientes.Fields.NOME.toString()),RelatorioExtratoTransplantesPacienteVO.Fields.NOME.toString())
				.add(Projections.property(PAC_DOT+AipPacientes.Fields.SEXO.toString()),RelatorioExtratoTransplantesPacienteVO.Fields.SEXO.toString())
				.add(Projections.property(PAC_DOT+AipPacientes.Fields.DT_NASCIMENTO.toString()),RelatorioExtratoTransplantesPacienteVO.Fields.DT_NASCIMENTO.toString())
				.add(Projections.property(TPR_DOT+MtxTransplantes.Fields.DATA_INGRESSO.toString()),RelatorioExtratoTransplantesPacienteVO.Fields.DT_INGRESSO.toString())
				.add(Projections.property(TPR_DOT+MtxTransplantes.Fields.TIPO_TMO.toString()),RelatorioExtratoTransplantesPacienteVO.Fields.TIPO_TMO.toString())
				.add(Projections.property(TPR_DOT+MtxTransplantes.Fields.TIPO_ALOGENICO.toString()),RelatorioExtratoTransplantesPacienteVO.Fields.TIPO_ALOGENICO.toString())
				.add(Projections.property(TPR_DOT+MtxTransplantes.Fields.TIPO_ORGAO.toString()),RelatorioExtratoTransplantesPacienteVO.Fields.TIPO_ORGAO.toString())
				
				.add(Projections.property(EXT_DOT+MtxExtratoTransplantes.Fields.DATA_OCORRENCIA.toString()), RelatorioExtratoTransplantesPacienteVO.Fields.DT_OCORRENCIA.toString())
				.add(Projections.property(EXT_DOT+MtxExtratoTransplantes.Fields.SITUACAO_TRANSPLANTE.toString()), RelatorioExtratoTransplantesPacienteVO.Fields.SITUACAO.toString())
				
				.add(Projections.property(MAS_DOT+MtxMotivoAlteraSituacao.Fields.DESCRICAO.toString()), RelatorioExtratoTransplantesPacienteVO.Fields.DESCRICAO_MOTIVO.toString())
				.add(Projections.property(PES_DOT+RapPessoasFisicas.Fields.NOME.toString()), RelatorioExtratoTransplantesPacienteVO.Fields.NOME_RESPONSAVEL.toString());
				
		criteria.setProjection(proj);
		criteria.addOrder(Order.asc(TPR_DOT+MtxTransplantes.Fields.RECEPTOR_CODIGO.toString()));
		criteria.addOrder(Order.asc(TPR_DOT+MtxTransplantes.Fields.SEQ.toString()));
		criteria.addOrder(Order.asc(EXT_DOT+MtxExtratoTransplantes.Fields.DATA_OCORRENCIA.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(RelatorioExtratoTransplantesPacienteVO.class));
		return executeCriteria(criteria);
	}

	private void filtroExtratoPaciente(Integer pacCodigo,
			FiltroTempoPermanenciaListVO filtro, DetachedCriteria criteria) {
		if(pacCodigo != null){
			criteria.add(Restrictions.eq(TPR_DOT+MtxTransplantes.Fields.RECEPTOR_CODIGO.toString(),pacCodigo));
		}
		
		if(filtro!= null && filtro.getTipoTMO() != null){
			criteria.add(Restrictions.eq(TPR_DOT+MtxTransplantes.Fields.TIPO_TMO.toString(),filtro.getTipoTMO()));
		}
		
		if(filtro!= null && filtro.getTipoAlogenico() != null){
			criteria.add(Restrictions.eq(TPR_DOT+MtxTransplantes.Fields.TIPO_ALOGENICO.toString(),filtro.getTipoAlogenico()));
		}
		
		if(filtro!= null && filtro.getTipoOrgao() != null){
			criteria.add(Restrictions.eq(TPR_DOT+MtxTransplantes.Fields.TIPO_ORGAO.toString(),filtro.getTipoOrgao()));
		}
	}

	/**
	 * @author thiago.cortes
	 * #41789 C8
	 * @param transplante
	 * @return
	 */
	public MtxExtratoTransplantes obterDataInclusaoListaAguradandoTransplante(Integer transplante){
		DetachedCriteria criteria = DetachedCriteria.forClass(MtxExtratoTransplantes.class);
		criteria.add(Restrictions.eq(MtxExtratoTransplantes.Fields.MTX_TRANSPLANTE_SEQ.toString(),transplante));
		criteria.add(Restrictions.eq(MtxExtratoTransplantes.Fields.SITUACAO_TRANSPLANTE.toString(),DominioSituacaoTransplante.A));
		criteria.addOrder(Order.asc(MtxExtratoTransplantes.Fields.CRIADO_EM.toString()));
		List<MtxExtratoTransplantes> lista = new ArrayList<MtxExtratoTransplantes>();
		lista = executeCriteria(criteria);
		if((lista != null) && (!lista.isEmpty())){
			return lista.get(0);
		}	
		return null;
	}
	
	/**
	 * @author thiago.cortes
	 * #41789 C9
	 * @param transplante, situacao_transplante
	 * @return
	 */
	public MtxExtratoTransplantes obterDataSituacaoAtual(Integer transplante, DominioSituacaoTransplante situacaoTransplante){
		DetachedCriteria criteria = DetachedCriteria.forClass(MtxExtratoTransplantes.class);
		criteria.add(Restrictions.eq(MtxExtratoTransplantes.Fields.MTX_TRANSPLANTE_SEQ.toString(),transplante));
		criteria.add(Restrictions.eq(MtxExtratoTransplantes.Fields.SITUACAO_TRANSPLANTE.toString(),situacaoTransplante));
		
		criteria.addOrder(Order.desc(MtxExtratoTransplantes.Fields.CRIADO_EM.toString()));
		List<MtxExtratoTransplantes> lista = new ArrayList<MtxExtratoTransplantes>();
		lista = executeCriteria(criteria);
		if((lista != null) && (!lista.isEmpty())){
			return lista.get(0);
		}
		return null;
	}
	
	/**#46771 C1 - Obtem uma lista de extrato de transplantes pelo TRP_SEQ **/
	public List<GerarExtratoListaTransplantesVO> consultarListagemExtratoTransplante(Integer trpSeq){
		DetachedCriteria criteria = montarConsultarListagemExtratoTransplante(trpSeq);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(MET_PONTO+MtxExtratoTransplantes.Fields.DATA_OCORRENCIA.toString()), GerarExtratoListaTransplantesVO.Fields.DATA_SITUCAO.toString())
				.add(Projections.property(MET_PONTO+MtxExtratoTransplantes.Fields.SITUACAO_TRANSPLANTE.toString()), GerarExtratoListaTransplantesVO.Fields.SITUACAO.toString())
				.add(Projections.property("MAS."+MtxMotivoAlteraSituacao.Fields.DESCRICAO.toString()), GerarExtratoListaTransplantesVO.Fields.MOTIVO.toString())
				.add(Projections.property("PES."+RapPessoasFisicas.Fields.NOME.toString()),GerarExtratoListaTransplantesVO.Fields.RESPONSAVEL.toString()));
		
		criteria.addOrder(Order.desc(MET_PONTO+MtxExtratoTransplantes.Fields.DATA_OCORRENCIA.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(GerarExtratoListaTransplantesVO.class));
		
		return executeCriteria(criteria);
	}
	
	//C1 46771
	private DetachedCriteria montarConsultarListagemExtratoTransplante(Integer trpSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MtxExtratoTransplantes.class, "MET");
		criteria.createAlias(MET_PONTO+MtxExtratoTransplantes.Fields.MTX_MOTIVO_ALTERA_SITUACAO.toString(), "MAS", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(MET_PONTO+MtxExtratoTransplantes.Fields.SERVIDOR.toString(), "SER");
		criteria.createAlias("SER."+RapServidores.Fields.PESSOA_FISICA.toString(), "PES");
		if(trpSeq != null){
			criteria.add(Restrictions.eq("MET."+MtxExtratoTransplantes.Fields.MTX_TRANSPLANTE_SEQ.toString(), trpSeq));
		}
		return criteria;
	} 
	
	/**
	 * #46720 C8
	 * @param trpSeq
	 * @return
	 */
	public Date obterDataTransplanteInativado(Integer trpSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MtxExtratoTransplantes.class);
		criteria.setProjection(Projections.property(MtxExtratoTransplantes.Fields.DATA_OCORRENCIA.toString()));
		criteria.add(Restrictions.eq(MtxExtratoTransplantes.Fields.MTX_TRANSPLANTE_SEQ.toString(), trpSeq));
		criteria.add(Restrictions.eq(MtxExtratoTransplantes.Fields.SITUACAO_TRANSPLANTE.toString(), DominioSituacaoTransplante.I));
		criteria.addOrder(Order.desc(MtxExtratoTransplantes.Fields.DATA_OCORRENCIA.toString()));
		
		List<Date> listaRetorno = executeCriteria(criteria);
		
		return (listaRetorno != null && !listaRetorno.isEmpty()) ? listaRetorno.get(0) : null;
	} 
	/**@author danielle.pinheiro
	 * #46772 C1
	 * @param trpSeq
	 * @return
	 */
	public List<ExtratoAlteracoesListaOrgaosVO> obterExtratoAlteracoesListaOrgaos(Integer trpSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MtxExtratoTransplantes.class, MET);
		criteria.createAlias(MET_PONTO + MtxExtratoTransplantes.Fields.MTX_MOTIVO_ALTERA_SITUACAO.toString(), "MAS", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(MET_PONTO + MtxExtratoTransplantes.Fields.SERVIDOR.toString(), "SER", JoinType.INNER_JOIN);
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq(MET_PONTO + MtxExtratoTransplantes.Fields.MTX_TRANSPLANTE_SEQ.toString(), trpSeq));
		criteria.addOrder(Order.desc(MET_PONTO + MtxExtratoTransplantes.Fields.DATA_OCORRENCIA.toString()));
		
		Projection projection = Projections.projectionList()
				.add(Projections.property(MET_PONTO + MtxExtratoTransplantes.Fields.DATA_OCORRENCIA.toString()), ExtratoAlteracoesListaOrgaosVO.Fields.DT_SITUACAO.toString())
				.add(Projections.property(MET_PONTO + MtxExtratoTransplantes.Fields.SITUACAO_TRANSPLANTE.toString()), ExtratoAlteracoesListaOrgaosVO.Fields.SITUACAO.toString())
				.add(Projections.property("MAS." + MtxMotivoAlteraSituacao.Fields.DESCRICAO.toString()), ExtratoAlteracoesListaOrgaosVO.Fields.MOTIVO.toString())
				.add(Projections.property("PES." + RapPessoasFisicas.Fields.NOME.toString()), ExtratoAlteracoesListaOrgaosVO.Fields.RESPONSAVEL.toString());
		
		criteria.setProjection(projection);
		
		criteria.setResultTransformer(Transformers.aliasToBean(ExtratoAlteracoesListaOrgaosVO.class));
		
		return executeCriteria(criteria);
	}
	
	public MtxExtratoTransplantes pesquisarMtxExtratoTransplantes(Integer seqMtxTransplantes, DominioSituacaoTransplante dominioSituacaoTransplante){
		DetachedCriteria criteria = DetachedCriteria.forClass(MtxExtratoTransplantes.class);
		List<MtxExtratoTransplantes> listaRetorno;
		criteria.add(Restrictions.eq(MtxExtratoTransplantes.Fields.MTX_TRANSPLANTE_SEQ.toString(), seqMtxTransplantes))
				.add(Restrictions.eq(MtxExtratoTransplantes.Fields.SITUACAO_TRANSPLANTE.toString(), dominioSituacaoTransplante));
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(MtxExtratoTransplantes.Fields.DATA_OCORRENCIA.toString()), MtxExtratoTransplantes.Fields.DATA_OCORRENCIA.toString()));
		criteria.addOrder(Order.desc(MtxExtratoTransplantes.Fields.CRIADO_EM.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(MtxExtratoTransplantes.class));
		listaRetorno = executeCriteria(criteria);
		
		return (listaRetorno != null && !listaRetorno.isEmpty()) ? listaRetorno.get(0) : null;
	}

	public static String getMet() {
		return MET;
	}

	public static String getMetPonto() {
		return MET_PONTO;
	}
}
