package br.gov.mec.aghu.patrimonio.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioIndResponsavel;
import br.gov.mec.aghu.model.PtmItemRecebProvisorios;
import br.gov.mec.aghu.model.PtmTecnicoItemRecebimento;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.patrimonio.vo.NomeUsuarioVO;
import br.gov.mec.aghu.patrimonio.vo.TecnicoItemRecebimentoVO;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class PtmTecnicoItemRecebimentoDAO extends BaseDao<PtmTecnicoItemRecebimento> {
	

	private static final long serialVersionUID = 5722204395459934510L;
	
	private static final String PTIR = "PTIR.";
	private static final String ALIAS_SER = "SER";
	private static final String ALIAS_TEC = "TEC";
	private static final String ALIAS_TEC_EXT = "TEC.";
	private static final String ALIAS_IRP = "IRP";
	private static final String ALIAS_IRP_EXT = "IRP.";
	private static final String ALIAS_TIR = "TIR";
	private static final String ALIAS_TIR_EXT = "TIR.";
	private static final String ALIAS_TEI_EXT = "TEI.";
	private static final String ALIAS_SER_EXT = "SER.";
	private static final String ALIAS_PES = "PES";

	/**
	 * #43446 - Obtem lista da entidade passando como parametro os numeros do item de recebimento.
	 * @param recebimento - Numero de Recebimento
	 * @param itemRecebimento - Numero do Item de Recebimento
	 * @return Lista de {@link PtmTecnicoItemRecebimento}
	 */
	public List<PtmTecnicoItemRecebimento> obterPorItemRecebimento(Integer recebimento, Integer itemRecebimento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmTecnicoItemRecebimento.class, ALIAS_TIR);
		criteria.createAlias(ALIAS_TIR_EXT + PtmTecnicoItemRecebimento.Fields.PTM_ITEM_RECEB_PROVISORIO.toString(), ALIAS_IRP, JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_TIR_EXT + PtmTecnicoItemRecebimento.Fields.SERVIDOR.toString(), ALIAS_SER, JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_TIR_EXT + PtmTecnicoItemRecebimento.Fields.SERVIDOR_TECNICO.toString(), ALIAS_TEC, JoinType.INNER_JOIN);
		if (recebimento != null) {
			criteria.add(Restrictions.eq(ALIAS_IRP_EXT + PtmItemRecebProvisorios.Fields.SCE_NRP_SEQ.toString(), recebimento));
		}
		if (itemRecebimento != null) {
			criteria.add(Restrictions.eq(ALIAS_IRP_EXT + PtmItemRecebProvisorios.Fields.SCE_NRO_ITEM.toString(), itemRecebimento));
		}
		return executeCriteria(criteria);
	}

	/**
	 * #43446 - Obtem lista da entidade passando como parametro o servidor selecionado
	 * @param servidor - Instancia de {@link RapServidores}
	 * @return Lista de {@link PtmTecnicoItemRecebimento}
	 */
	public List<PtmTecnicoItemRecebimento> obterPorServidor(RapServidores servidor) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmTecnicoItemRecebimento.class);
		criteria.add(Restrictions.eq(PtmTecnicoItemRecebimento.Fields.SERVIDOR_TECNICO.toString(), servidor));
		return executeCriteria(criteria);
	}
	
	/**
	 * #43446 - Obtem instancia da entidade passando como parametro o servidor selecionado e o item de recebimento
	 * @param servidor - Instancia de {@link RapServidores}
	 * @param recebimento - Numero de Recebimento
	 * @param itemRecebimento - Numero do Item de Recebimento
	 * @return Lista de {@link PtmTecnicoItemRecebimento}
	 */
	public PtmTecnicoItemRecebimento obterPorServidorItemRecebimento(RapServidores servidor, Integer recebimento, Integer itemRecebimento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmTecnicoItemRecebimento.class, ALIAS_TIR);
		criteria.createAlias(ALIAS_TIR_EXT + PtmTecnicoItemRecebimento.Fields.PTM_ITEM_RECEB_PROVISORIO.toString(), ALIAS_IRP, JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_TIR_EXT + PtmTecnicoItemRecebimento.Fields.SERVIDOR.toString(), ALIAS_SER, JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_TIR_EXT + PtmTecnicoItemRecebimento.Fields.SERVIDOR_TECNICO.toString(), ALIAS_TEC, JoinType.INNER_JOIN);
		if (recebimento != null) {
			criteria.add(Restrictions.eq(ALIAS_IRP_EXT + PtmItemRecebProvisorios.Fields.SCE_NRP_SEQ.toString(), recebimento));
		}
		if (itemRecebimento != null) {
			criteria.add(Restrictions.eq(ALIAS_IRP_EXT + PtmItemRecebProvisorios.Fields.SCE_NRO_ITEM.toString(), itemRecebimento));
		}
		if (servidor != null && servidor.getId() != null) {
			criteria.add(Restrictions.eq(ALIAS_TEC_EXT + RapServidores.Fields.MATRICULA.toString(), servidor.getId().getMatricula()));
			criteria.add(Restrictions.eq(ALIAS_TEC_EXT + RapServidores.Fields.VIN_CODIGO.toString(), servidor.getId().getVinCodigo()));
		}
		List<PtmTecnicoItemRecebimento> lista = executeCriteria(criteria);
		if (lista != null && !lista.isEmpty()) {
			return lista.get(0);
		}
		return null;
	}
	
	/**
	 * #43446, #45707 - Obtém os itens recebimentos referente ao usuário logado.
	 * @param servidorTecnico
	 * @param itemReceb
	 * @return Lista de {@link PtmTecnicoItemRecebimento}
	 */
	public List<PtmTecnicoItemRecebimento> obterPorTecnicoItemRecebimento(Integer recebimento, Integer itemRecebimento, RapServidores servidorTecnico) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmTecnicoItemRecebimento.class, ALIAS_TIR);
		criteria.createAlias(ALIAS_TIR_EXT + PtmTecnicoItemRecebimento.Fields.PTM_ITEM_RECEB_PROVISORIO.toString(), ALIAS_IRP, JoinType.INNER_JOIN);
		if (recebimento != null) {
			criteria.add(Restrictions.eq(ALIAS_IRP_EXT + PtmItemRecebProvisorios.Fields.SCE_NRP_SEQ.toString(), recebimento));
		}
		if (itemRecebimento != null) {
			criteria.add(Restrictions.eq(ALIAS_IRP_EXT + PtmItemRecebProvisorios.Fields.SCE_NRO_ITEM.toString(), itemRecebimento));
		}
		if (servidorTecnico != null) {
			criteria.add(Restrictions.eq(ALIAS_TIR_EXT + PtmTecnicoItemRecebimento.Fields.SERVIDOR_TECNICO.toString(), servidorTecnico));
		}
		
		return executeCriteria(criteria);
	}
	
	//#48782 C21
	public boolean verificarResponsavelPorAceite(Long recebimento, RapServidores servidorTecnico) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmTecnicoItemRecebimento.class, ALIAS_TIR);
		criteria.createAlias(ALIAS_TIR_EXT + PtmTecnicoItemRecebimento.Fields.PTM_ITEM_RECEB_PROVISORIO.toString(), ALIAS_IRP, JoinType.INNER_JOIN);
		
		
//		criteria.setProjection(Projections.projectionList()
//					.add(Projections.property(ALIAS_TIR_EXT+PtmTecnicoItemRecebimento.Fields.SERVIDOR_TECNICO),
//							ALIAS_TIR_EXT+PtmTecnicoItemRecebimento.Fields.SERVIDOR_TECNICO)
//					.add(Projections.property(ALIAS_IRP_EXT+PtmItemRecebProvisorios.Fields.ATA_SEQ.toString()),
//							ALIAS_IRP_EXT+PtmItemRecebProvisorios.Fields.ATA_SEQ.toString())
//				);
		
		criteria.add(Restrictions.eq(ALIAS_TIR_EXT + PtmTecnicoItemRecebimento.Fields.SERVIDOR_TECNICO.toString(), servidorTecnico));
		criteria.add(Restrictions.eq(ALIAS_TIR_EXT + PtmTecnicoItemRecebimento.Fields.IND_RESPONSAVEL.toString(), DominioIndResponsavel.R));
		criteria.add(Restrictions.eq(ALIAS_IRP_EXT + PtmItemRecebProvisorios.Fields.SEQ.toString(), recebimento));
//		criteria.setResultTransformer(Transformers.aliasToBean(PtmTecnicoItemRecebimentoVO.class));
		return executeCriteriaExists(criteria);
	}
	
	/**
	 * C4 - Consultar o Técnico Responsável
	 * #51075 
	 */
	public TecnicoItemRecebimentoVO obterTecnicoResponsavelPorMatENome(Long numeroRecebimento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmTecnicoItemRecebimento.class, "TEI");

		criteria.createAlias(ALIAS_TEI_EXT + PtmTecnicoItemRecebimento.Fields.SERVIDOR_TECNICO.toString(), ALIAS_SER, JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_SER_EXT + RapServidores.Fields.PESSOA_FISICA.toString(), "PES", JoinType.INNER_JOIN);
		
		ProjectionList p = Projections.projectionList();
			p.add(Projections.property(ALIAS_SER_EXT + RapServidores.Fields.MATRICULA.toString()), TecnicoItemRecebimentoVO.Fields.MATRICULA.toString());
			p.add(Projections.property("PES."+ RapPessoasFisicas.Fields.NOME.toString()), TecnicoItemRecebimentoVO.Fields.NOME.toString());
		criteria.setProjection(p);
		
		criteria.add(Restrictions.eq(ALIAS_TEI_EXT + PtmTecnicoItemRecebimento.Fields.IRP_SEQ.toString(), numeroRecebimento));
		//criteria.add(Restrictions.eq("TEI." + PtmTecnicoItemRecebimento.Fields.IND_RESPONSAVEL.toString(), DominioIndResponsavel.R));
		criteria.add(Restrictions.sqlRestriction(" {alias}.IND_RESPONSAVEL =  '1' "));
		
		criteria.setResultTransformer(Transformers.aliasToBean(TecnicoItemRecebimentoVO.class));
		
		return (TecnicoItemRecebimentoVO) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * C3 - Consultar o Técnico Responsável
	 * #50704
	 */
	public TecnicoItemRecebimentoVO buscarTecnicoResponsavelPorMatENome(Integer nrpSeq, Integer nroItem) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmTecnicoItemRecebimento.class, "TEI");

		criteria.createAlias(ALIAS_TEI_EXT + PtmTecnicoItemRecebimento.Fields.SERVIDOR_TECNICO.toString(), ALIAS_SER, JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_SER_EXT + RapServidores.Fields.PESSOA_FISICA.toString(), "PES", JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_TEI_EXT + PtmTecnicoItemRecebimento.Fields.PTM_ITEM_RECEB_PROVISORIO.toString(), "IRP", JoinType.INNER_JOIN);
		
		ProjectionList p = Projections.projectionList();
			p.add(Projections.property(ALIAS_SER_EXT + RapServidores.Fields.MATRICULA.toString()), TecnicoItemRecebimentoVO.Fields.MATRICULA.toString());
			p.add(Projections.property("PES."+ RapPessoasFisicas.Fields.NOME.toString()), TecnicoItemRecebimentoVO.Fields.NOME.toString());
		criteria.setProjection(p);
		
		criteria.add(Restrictions.eq("IRP." + PtmItemRecebProvisorios.Fields.SCE_NRP_SEQ.toString(), nrpSeq));
		criteria.add(Restrictions.eq("IRP." + PtmItemRecebProvisorios.Fields.SCE_NRO_ITEM.toString(), nroItem));
		//criteria.add(Restrictions.eq("TEI." + PtmTecnicoItemRecebimento.Fields.IND_RESPONSAVEL.toString(), DominioIndResponsavel.R));
		criteria.add(Restrictions.sqlRestriction(" {alias}.IND_RESPONSAVEL =  '1' "));
		
		criteria.setResultTransformer(Transformers.aliasToBean(TecnicoItemRecebimentoVO.class));
		
		return (TecnicoItemRecebimentoVO) executeCriteriaUniqueResult(criteria);
	}	
	
	public Integer verificarResponsavelAceiteTecnico(RapServidores servidor, List<Long> seqRecebProvisorios){
		Integer resultado = 0;
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmTecnicoItemRecebimento.class);
		
		criteria.add(Restrictions.eq(PtmTecnicoItemRecebimento.Fields.IND_RESPONSAVEL.toString(), DominioIndResponsavel.R));
		criteria.add(Restrictions.in(PtmTecnicoItemRecebimento.Fields.IRP_SEQ.toString(), seqRecebProvisorios));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		List<PtmTecnicoItemRecebimento> lista = executeCriteria(criteria);
		if(lista.size() == 1){
			if(!lista.get(0).getServidorTecnico().equals(servidor)){
					resultado = 1;
			}						
		}else if(lista.size() > 1){
			for (PtmTecnicoItemRecebimento ptmTecnicoItemRecebimento : lista) {
				if(!ptmTecnicoItemRecebimento.getServidorTecnico().equals(servidor)){
					resultado = 2;
				}
			}
		}
		
		return resultado;
	}
	/**
	 * Nome e matricula usuário retornados da query C1
	 * @param irpSeq
	 * @return NomeUsuarioVO
	 */
	public NomeUsuarioVO obterNomeMatriculaUsuario(Long irpSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmTecnicoItemRecebimento.class, "PTIR");
		
		ProjectionList p = Projections.projectionList();
		p.add(Projections.property(PTIR+ PtmTecnicoItemRecebimento.Fields.SERVIDOR_TECNICO.toString()), NomeUsuarioVO.Fields.MATRICULA.toString());			
		p.add(Projections.property("PES."+ RapPessoasFisicas.Fields.NOME.toString()), NomeUsuarioVO.Fields.NOME.toString());
		criteria.setProjection(p);

		criteria.createAlias(PTIR + PtmTecnicoItemRecebimento.Fields.SERVIDOR_TECNICO.toString(), ALIAS_SER, JoinType.INNER_JOIN);
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), ALIAS_PES, JoinType.INNER_JOIN);
				
		criteria.add(Restrictions.eq(PTIR + PtmTecnicoItemRecebimento.Fields.IRP_SEQ.toString(), irpSeq));
		criteria.add(Restrictions.eq(PTIR + PtmTecnicoItemRecebimento.Fields.IND_RESPONSAVEL.toString(), DominioIndResponsavel.R));
		
		criteria.setResultTransformer(Transformers.aliasToBean(NomeUsuarioVO.class));		
		return (NomeUsuarioVO) executeCriteriaUniqueResult(criteria);
	}
	
}
