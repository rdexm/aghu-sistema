package br.gov.mec.aghu.faturamento.business;

import java.util.List;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.faturamento.dao.FatAtoMedicoAihDAO;
import br.gov.mec.aghu.faturamento.dao.FatCompatExclusItemDAO;
import br.gov.mec.aghu.faturamento.vo.CursorAtosMedicosVO;
import br.gov.mec.aghu.model.FatAtoMedicoAih;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;

@Stateless
public class FatAtualizacoesCompatibilidadeRN extends BaseBusiness {
	
	private static final String V_CTR_COMP = " v_ctr_comp: ";

	private static final long serialVersionUID = -2455044295828611996L;
	
	private static final Log LOG = LogFactory.getLog(FatAtualizacoesCompatibilidadeRN.class);

	private static final int VL_EXECUCAO = 500;
	private static final String UPDATE = " update ";
	private static final String WHERE_1_1 = " where 1=1 ";
	private static final String AND = " and ";
	private static final String SET = " set ";
	
	private static final String SQL_UPDATE = UPDATE + FatAtoMedicoAih.class.getName() + 
										 	 SET + FatAtoMedicoAih.Fields.SEQ_COMPATIBILIDADE.toString() + " = null " +
										     " where " + FatAtoMedicoAih.Fields.EAI_CTH_SEQ.toString() + '=';
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	/**
	 * ORADB: ATUALIZA_COMPATIBILIDADE
	 */
	protected int atualizaCompatibilidade( final Integer cthSeq, final Long pNivel, int vCtrComp, 
											final StringBuilder sqlUpdate, 
											final FatAtoMedicoAihDAO fatAtoMedicoAihDAO ) {
		
		final List<FatAtoMedicoAih> atos = fatAtoMedicoAihDAO.obterFatAtoMedicoAihPorCthIphCodSus(cthSeq, pNivel, true, FatAtoMedicoAih.Fields.TAO_SEQ.toString(), true);
		
		/*
		 * eSchweigert 25/10/13 
		 * 
		 * monta query para evitar multiplas execuções de update, 
		 * triggers apenas setam alteradoEm e alteradoPor
		 */
		for (FatAtoMedicoAih rAtosMedicosAih : atos) {
			sqlUpdate.append(UPDATE).append(FatAtoMedicoAih.class.getName())
						.append(SET).append(FatAtoMedicoAih.Fields.SEQ_COMPATIBILIDADE.toString()).append('=').append(++vCtrComp)
					 .append(WHERE_1_1)
					 	.append(AND).append(FatAtoMedicoAih.Fields.EAI_SEQ.toString()).append('=').append(rAtosMedicosAih.getId().getEaiSeq())
					 	.append(AND).append(FatAtoMedicoAih.Fields.EAI_CTH_SEQ.toString()).append('=').append(rAtosMedicosAih.getId().getEaiCthSeq())
					 	.append(AND).append(FatAtoMedicoAih.Fields.SEQP.toString()).append('=').append(rAtosMedicosAih.getId().getSeqp())
					 	.append(AND).append(FatAtoMedicoAih.Fields.TAO_SEQ.toString()).append('=').append(rAtosMedicosAih.getFatTipoAto().getCodigoSus())
					 	.append(AND).append(FatAtoMedicoAih.Fields.IPH_COD_SUS.toString()).append('=').append(rAtosMedicosAih.getIphCodSus())
					 	.append(';');
			
			//	rAtosMedicosAih.setSeqCompatibilidade(++vCtrComp);
			//	atoMedicoAihPersist.atualizar(rAtosMedicosAih, nomeMicrocomputador, servidorLogado, dataFimVinculoServidor);

			atualizarScript(fatAtoMedicoAihDAO, sqlUpdate, vCtrComp);
		}
		
		return vCtrComp;
	}

	/**
	 * ORADB: FATP_SEQUENCIA_COMPATIBILIDADE
	 */
	protected void geraSequenciaCompatibilidade( final Integer cthSeq, final Integer iphSeqR, final Short iphPhoSeqR, 
												 final FatAtoMedicoAihDAO fatAtoMedicoAihDAO) {
		
		logInfo(" P_IPH_SEQ_R "+iphSeqR);
		logInfo("gera_nova_sequencia: P_IPH_PHO_SEQ_R "+iphPhoSeqR);

		fatAtoMedicoAihDAO.atualizarFatAtoMedicoAihs(SQL_UPDATE + cthSeq);

		final StringBuilder sqlUpdate = new StringBuilder();  

		final List<CursorAtosMedicosVO> atos = fatAtoMedicoAihDAO.obterNiveisAtosMedico(cthSeq, iphPhoSeqR, iphSeqR);
		
		int vCtrComp = 0;
		for (CursorAtosMedicosVO rBuscaCompat : atos) {
			
			// Busca na atos_medicos_aih os registros correspondentes a compatibilidadedo nivel 1
			logWarn("v_nivel: "+rBuscaCompat.getNivel()+V_CTR_COMP+vCtrComp);
			
			vCtrComp = atualizaCompatibilidade(cthSeq, rBuscaCompat.getNivel(), vCtrComp, sqlUpdate, fatAtoMedicoAihDAO);
			
			vCtrComp = validaNiveis(cthSeq, fatAtoMedicoAihDAO, sqlUpdate, vCtrComp, rBuscaCompat);
			
			atualizarScript(fatAtoMedicoAihDAO, sqlUpdate, vCtrComp);
		}

		atualizarScript(fatAtoMedicoAihDAO, sqlUpdate, VL_EXECUCAO);
		
		logWarn("saiu do loop da compatibilidade v_ctr_comp: "+vCtrComp);
		
		// Depois que achos todos os compatíveis, popula os que ficaram sobrando
		
		// Marina 16/04/2013
		final List<FatAtoMedicoAih> cBuscaSobras = fatAtoMedicoAihDAO.obterFatAtoMedicoAihPorCthIphCodSus(cthSeq, null, true, FatAtoMedicoAih.Fields.SEQ_ARQ_SUS.toString(), true);
		
		for (FatAtoMedicoAih rBuscaSobras : cBuscaSobras) {
			logWarn("v_ctr_comp: "+ ++vCtrComp);
			
			sqlUpdate.append(UPDATE).append(FatAtoMedicoAih.class.getName())
			 		 	.append(SET).append(FatAtoMedicoAih.Fields.SEQ_COMPATIBILIDADE.toString()).append('=').append(vCtrComp)
			 		 .append(WHERE_1_1)
					 	.append(AND).append(FatAtoMedicoAih.Fields.EAI_SEQ.toString()).append('=').append(rBuscaSobras.getId().getEaiSeq())
					 	.append(AND).append(FatAtoMedicoAih.Fields.EAI_CTH_SEQ.toString()).append('=').append(rBuscaSobras.getId().getEaiCthSeq())
					 	.append(AND).append(FatAtoMedicoAih.Fields.SEQP.toString()).append('=').append(rBuscaSobras.getId().getSeqp())
					 	.append(';');

			atualizarScript(fatAtoMedicoAihDAO, sqlUpdate, vCtrComp);
		}

		atualizarScript(fatAtoMedicoAihDAO, sqlUpdate, VL_EXECUCAO);
		
		// Atualiza o campo seq_arq_sus com os dados do seq_compatibilidade
		logWarn("Atualiza o campo seq_arq_sus com a nova compatibilidade");
		
		final List<FatAtoMedicoAih> cArqSeqSus = fatAtoMedicoAihDAO.obterFatAtoMedicoAihPorCthIphCodSus(cthSeq, null, false, FatAtoMedicoAih.Fields.SEQ_COMPATIBILIDADE.toString(), true);
		
		int cont = 0;
		for (FatAtoMedicoAih rArqSeqSus : cArqSeqSus) {
			logWarn("seq_arq_sus: "+rArqSeqSus.getSeqArqSus());
			logWarn("seq_compatibilidade: "+rArqSeqSus.getSeqCompatibilidade());
			
			sqlUpdate.append(UPDATE).append(FatAtoMedicoAih.class.getName())
						.append(SET).append(FatAtoMedicoAih.Fields.SEQ_ARQ_SUS.toString()).append('=').append(rArqSeqSus.getSeqCompatibilidade())
					 .append(WHERE_1_1)
					 	.append(AND).append(FatAtoMedicoAih.Fields.EAI_SEQ.toString()).append('=').append(rArqSeqSus.getId().getEaiSeq())
					 	.append(AND).append(FatAtoMedicoAih.Fields.EAI_CTH_SEQ.toString()).append('=').append(rArqSeqSus.getId().getEaiCthSeq())
					 	.append(AND).append(FatAtoMedicoAih.Fields.SEQP.toString()).append('=').append(rArqSeqSus.getId().getSeqp())
					 	.append(';');
			

			atualizarScript(fatAtoMedicoAihDAO, sqlUpdate, ++cont);
		}

		atualizarScript(fatAtoMedicoAihDAO, sqlUpdate, VL_EXECUCAO);
		// Fim Marina 09/05/2013
	}

	private int validaNiveis(final Integer cthSeq, final FatAtoMedicoAihDAO fatAtoMedicoAihDAO,
			final StringBuilder sqlUpdate, int vCtrComp, CursorAtosMedicosVO rBuscaCompat) {
		
		if(!CoreUtil.igual(rBuscaCompat.getNivel(), rBuscaCompat.getNivel1()) &&
				rBuscaCompat.getNivel1() != null){

			logWarn("v_nivel_1: "+rBuscaCompat.getNivel1()+V_CTR_COMP+vCtrComp);
			vCtrComp = atualizaCompatibilidade(cthSeq, rBuscaCompat.getNivel1(), vCtrComp, sqlUpdate, fatAtoMedicoAihDAO);

			if(!CoreUtil.igual(rBuscaCompat.getNivel1(), rBuscaCompat.getNivel2()) &&
					rBuscaCompat.getNivel2() != null){
				
				logWarn("v_nivel_2: "+rBuscaCompat.getNivel2()+V_CTR_COMP+vCtrComp);
				vCtrComp = atualizaCompatibilidade(cthSeq, rBuscaCompat.getNivel2(), vCtrComp, sqlUpdate, fatAtoMedicoAihDAO);

				if(!CoreUtil.igual(rBuscaCompat.getNivel2(), rBuscaCompat.getNivel3()) &&
						rBuscaCompat.getNivel3() != null){

					logWarn("v_nivel_3: "+rBuscaCompat.getNivel3()+V_CTR_COMP+vCtrComp);
					vCtrComp = atualizaCompatibilidade(cthSeq, rBuscaCompat.getNivel3(), vCtrComp, sqlUpdate, fatAtoMedicoAihDAO);

					if(!CoreUtil.igual(rBuscaCompat.getNivel3(), rBuscaCompat.getNivel4()) &&
							rBuscaCompat.getNivel4() != null){
						
						logWarn("v_nivel_4: "+rBuscaCompat.getNivel4()+V_CTR_COMP+vCtrComp);
						vCtrComp = atualizaCompatibilidade(cthSeq, rBuscaCompat.getNivel4(), vCtrComp, sqlUpdate, fatAtoMedicoAihDAO);

					}
				}
			}
		}
		return vCtrComp;
	}
	
	/**
	 * ORADB: GERA_NOVA_SEQUENCIA
	 * 
	 * Marina 16/04/2013
	 */
	protected void geraNovaSequenciaCompatibilidade( final Integer cthSeq, final Integer iphSeqR, final Short iphPhoSeqR, 
			 										 final Long iphCodSus,
													 final FatAtoMedicoAihDAO fatAtoMedicoAihDAO,
													 final FatCompatExclusItemDAO fatCompatExclusItemDAO) {
		
		logWarn("gera_nova_sequencia: P_IPH_SEQ_R "+iphSeqR);
		logWarn("gera_nova_sequencia: P_IPH_PHO_SEQ_R "+iphPhoSeqR);
		
		fatAtoMedicoAihDAO.atualizarFatAtoMedicoAihs(SQL_UPDATE + cthSeq);
		Integer vIphSeq = iphSeqR;
		Short vIphPhoSeq = iphPhoSeqR;
		
		List<FatAtoMedicoAih> cAtosMedicosAih = fatAtoMedicoAihDAO.obterFatAtoMedicoAihPorCthIphCodSus(cthSeq, null, true, null, true, true);
		
		int vCont = 0;
		final StringBuilder sqlUpdate = new StringBuilder();  
		
		for (FatAtoMedicoAih rAtosMedicosAih : cAtosMedicosAih) {
			
			logWarn("### AGHU EaiSeq "+rAtosMedicosAih.getId().getEaiSeq());
			
			final List<CursorAtosMedicosVO> cGeraNovaSeq = fatCompatExclusItemDAO
															.obterListaPorIphEEaiCTH(vIphPhoSeq, vIphSeq, cthSeq);
			
			for (CursorAtosMedicosVO rGeraNovaSeq : cGeraNovaSeq) {
				logWarn("v_cont: "+ ++vCont);
				sqlUpdate.append(UPDATE).append(FatAtoMedicoAih.class.getName())
		 		 		 	.append(SET).append(FatAtoMedicoAih.Fields.SEQ_COMPATIBILIDADE.toString()).append('=').append(vCont)
						 .append(WHERE_1_1)
						 	.append(AND).append(FatAtoMedicoAih.Fields.EAI_SEQ.toString()).append('=').append(rGeraNovaSeq.getEaiSeq())
						 	.append(AND).append(FatAtoMedicoAih.Fields.EAI_CTH_SEQ.toString()).append('=').append(rGeraNovaSeq.getEaiCthSeq())
						 	.append(AND).append(FatAtoMedicoAih.Fields.SEQP.toString()).append('=').append(rGeraNovaSeq.getSeqp())
						 	.append(';');
				
				vIphPhoSeq = rGeraNovaSeq.getIphPhoSeq();
				vIphSeq = rGeraNovaSeq.getIphSeq();

				logWarn("depois do update: v_iph_seq "+vIphSeq);
				logWarn("depois do update: v_iph_pho_seq "+vIphPhoSeq);
				
				atualizarScript(fatAtoMedicoAihDAO, sqlUpdate, vCont);
			}
		}
		
		atualizarScript(fatAtoMedicoAihDAO, sqlUpdate, VL_EXECUCAO);
		
		logWarn("saiu do loop principal");
		
		// Depois que achos todos os compatíveis, ajusta os que ficaram sobrando
		
		cAtosMedicosAih = fatAtoMedicoAihDAO.obterFatAtoMedicoAihPorCthIphCodSus(cthSeq, null, true, null, true, true);
		
		for (FatAtoMedicoAih rAtosMedicosAih : cAtosMedicosAih) {
			logWarn("v_cont: " + ++vCont);
			logWarn("seq_arq_sus: "+rAtosMedicosAih.getSeqArqSus());
			logWarn("iph_cod_sus: "+rAtosMedicosAih.getIphCodSus());
			
			sqlUpdate.append(UPDATE).append(FatAtoMedicoAih.class.getName())
					 	.append(SET).append(FatAtoMedicoAih.Fields.SEQ_COMPATIBILIDADE.toString()).append('=').append(vCont)
					 .append(WHERE_1_1)
						 .append(AND).append(FatAtoMedicoAih.Fields.EAI_SEQ.toString()).append('=').append(rAtosMedicosAih.getId().getEaiSeq())
						 .append(AND).append(FatAtoMedicoAih.Fields.EAI_CTH_SEQ.toString()).append('=').append(rAtosMedicosAih.getId().getEaiCthSeq())
						 .append(AND).append(FatAtoMedicoAih.Fields.SEQP.toString()).append('=').append(rAtosMedicosAih.getId().getSeqp())
						 .append(';');

			atualizarScript(fatAtoMedicoAihDAO, sqlUpdate, vCont);
		}

		atualizarScript(fatAtoMedicoAihDAO, sqlUpdate, VL_EXECUCAO);
		
		// Atualiza o campo seq_arq_sus com os dados do seq_compatibilidade
		logWarn("Atualiza o campo seq_arq_sus com a nova compatibilidade");
		
		final List<CursorAtosMedicosVO> cGeraNovaSeq = fatCompatExclusItemDAO
														.obterListaPorIphEEaiCTH(vIphPhoSeq, vIphSeq, cthSeq);
		
		for (CursorAtosMedicosVO rGeraNovaSeq : cGeraNovaSeq) {
			logWarn("seq_arq_sus: "+ rGeraNovaSeq.getSeqArqSus());
			logWarn("seq_compatibilidade: "+rGeraNovaSeq.getSeqCompatibilidade());
			
			sqlUpdate.append(UPDATE).append(FatAtoMedicoAih.class.getName())
	 		 		 	.append(SET).append(FatAtoMedicoAih.Fields.SEQ_ARQ_SUS.toString()).append('=').append(rGeraNovaSeq.getSeqCompatibilidade())
	 		 		 .append(WHERE_1_1)
					 	.append(AND).append(FatAtoMedicoAih.Fields.EAI_SEQ.toString()).append('=').append(rGeraNovaSeq.getEaiSeq())
					 	.append(AND).append(FatAtoMedicoAih.Fields.EAI_CTH_SEQ.toString()).append('=').append(rGeraNovaSeq.getEaiCthSeq())
					 	.append(AND).append(FatAtoMedicoAih.Fields.SEQP.toString()).append('=').append(rGeraNovaSeq.getSeqp())
					 	.append(';'); 
			
			atualizarScript(fatAtoMedicoAihDAO, sqlUpdate, vCont);
		}

		atualizarScript(fatAtoMedicoAihDAO, sqlUpdate, VL_EXECUCAO);
	}

	private void atualizarScript(final FatAtoMedicoAihDAO fatAtoMedicoAihDAO,final StringBuilder sqlUpdate, int vCtrComp) {
		
		if(vCtrComp % VL_EXECUCAO == 0 && sqlUpdate.length() > 0){
			fatAtoMedicoAihDAO.atualizarFatAtoMedicoAihs(sqlUpdate.toString());
			sqlUpdate.setLength(0);	// limpa...
		}
	}

}