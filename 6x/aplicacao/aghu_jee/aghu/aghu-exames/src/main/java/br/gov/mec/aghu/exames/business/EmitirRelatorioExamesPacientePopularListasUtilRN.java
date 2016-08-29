/**
 * 
 */
package br.gov.mec.aghu.exames.business;

import java.util.Date;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSumarioExame;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.model.AelExtratoItemSolicitacao;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelPolSumExameMasc;
import br.gov.mec.aghu.model.AelPolSumExameMascId;
import br.gov.mec.aghu.model.AelPolSumExameTab;
import br.gov.mec.aghu.model.AelPolSumExameTabId;
import br.gov.mec.aghu.model.AelPolSumLegenda;
import br.gov.mec.aghu.model.AelPolSumLegendaId;
import br.gov.mec.aghu.model.AelPolSumMascCampoEdit;
import br.gov.mec.aghu.model.AelPolSumMascCampoEditId;
import br.gov.mec.aghu.model.AelPolSumMascLinha;
import br.gov.mec.aghu.model.AelPolSumMascLinhaId;
import br.gov.mec.aghu.model.AelPolSumObservacao;
import br.gov.mec.aghu.model.AelPolSumObservacaoId;
import br.gov.mec.aghu.model.AelResultadoExame;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * @author aghu
 * 
 * Implementação das procedures internas chamadas pela procedure AELK_POL_SUM_EXAMES + métodos utilitários para atender a procedure
 *
 */
@Stateless
public class EmitirRelatorioExamesPacientePopularListasUtilRN extends BaseBusiness {


@EJB
private EmitirRelatorioExamesPacienteUtilRN emitirRelatorioExamesPacienteUtilRN;

private static final Log LOG = LogFactory.getLog(EmitirRelatorioExamesPacientePopularListasUtilRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private ISolicitacaoExameFacade solicitacaoExameFacade;	

	private static final long serialVersionUID = -3949690073502330065L;
	
	protected ISolicitacaoExameFacade getSolicitacaoExameFacade() {
		return this.solicitacaoExameFacade;
	}
	protected EmitirRelatorioExamesPacienteUtilRN getEmitirRelatorioExamesPacienteUtilRN() {
		return emitirRelatorioExamesPacienteUtilRN;
	}

	public AelPolSumExameMasc executarProcedureAelPolSumInsComItem(AelItemSolicitacaoExames itemSolicitacao, Integer vOrdemRel, Integer vOrdemAgrup) {
		
		AelPolSumExameMasc exame = new AelPolSumExameMasc();
		AelPolSumExameMascId id = new AelPolSumExameMascId();
					
		id.setProntuario(itemSolicitacao.getSolicitacaoExame().getAtendimento().getProntuario());
		id.setLtoLtoId(getSolicitacaoExameFacade().recuperarLocalPaciente(itemSolicitacao.getSolicitacaoExame().getAtendimento()));
		id.setRecemNascido(itemSolicitacao.getSolicitacaoExame().getRecemNascido());
		id.setUfeEmaExaSigla(itemSolicitacao.getExame().getSigla());
		id.setUfeEmaManSeq(itemSolicitacao.getMaterialAnalise().getSeq());
		id.setUfeUnfSeq(itemSolicitacao.getUnidadeFuncional().getSeq());
		id.setPertenceSumario(itemSolicitacao.getAelExameMaterialAnalise().getPertenceSumario());
		if(itemSolicitacao.getItemSolicitacaoExame() != null){
			id.setIseSoeSeqDept(itemSolicitacao.getItemSolicitacaoExame().getId().getSoeSeq());
			id.setIseSeqpDept(itemSolicitacao.getItemSolicitacaoExame().getId().getSeqp());
		}
		id.setIseSoeSeq(itemSolicitacao.getId().getSoeSeq());
		id.setIseSeqp(itemSolicitacao.getId().getSeqp());
		id.setDescricao(null);
		id.setDthrEventoLib(getEmitirRelatorioExamesPacienteUtilRN().obterMaxDataHoraEvento(itemSolicitacao.getAelExtratoItemSolicitacao()));
		id.setOrdemRelatorio(vOrdemRel);
		id.setOrdemAgrupamento(vOrdemAgrup);
		id.setDthrFim(itemSolicitacao.getSolicitacaoExame().getAtendimento().getDthrFim());
		exame.setId(id);

		return exame;
	}
	
	
	/**
	 * ORADB:AELP_POL_SUM_INS_SEM
	 * 
	 * @param parametros
	 * @return
	 */
	public AelPolSumExameTab executarProcedureAelPolSumInsSem(Map<String, Object> parametros, String indImprime) {
		
		AelPolSumExameTab exame = new AelPolSumExameTab();
		AelPolSumExameTabId id = new AelPolSumExameTabId();
		
		if(parametros != null) {
			
			Integer prontuario = (Integer) parametros.get("vPrntControle");
			String ltoLtoId = (String) parametros.get("vLtoLtoId");
			Boolean recemNascido = (Boolean) parametros.get("vRecemNascido");
			String ufeEmaExaSigla = (String) parametros.get("vUfeEmaExaSigla");
			Integer ufeEmaManSeq = (Integer) parametros.get("vUfeEmaManSeqAnt");
			Short ufeUnfSeq = (Short) parametros.get("vUfeUnfSeq");
			DominioSumarioExame pertenceSumario = (DominioSumarioExame) parametros.get("vPertenceSumario");
			Integer iseSoeSeqDept = (Integer) parametros.get("vSoeSeqDeptAnt");
			Short iseSeqpDept = (Short) parametros.get("vSeqDeptAnt");
			Integer iseSoeSeq = (Integer) parametros.get("vSoeSeqAnt");
			Short iseSeqp = (Short) parametros.get("vSeqpAnt");
			Integer calSeq = (Integer) parametros.get("vCalSeq");
			String calNome = (String) parametros.get("vCalNome");
			Double reeValor = (Double) parametros.get("vReeValor");
			Integer cacSeq = (Integer) parametros.get("vCacSeq");
			Integer rcdGtcSeq = (Integer) parametros.get("vRcdGtcSeq");
			Integer rcdSeqp = (Integer) parametros.get("vRcdSeq");
			String descricao = (String) parametros.get("vDescricaoExamesTab");
			Date dthrEventoAreaExec = (Date) parametros.get("vDthrEventoAreaExec");
			String calNomeSumario = (String) parametros.get("vCalNomeSumario");
			Short ordem = (Short) parametros.get("vOrdemExamesTab");
			//String indImprime = (String) parametros.get("vIndImprime");
			Date dthrFim = (Date) parametros.get("vDthrFimExamesTab");
			
			id.setProntuario(prontuario);
			id.setLtoLtoId(ltoLtoId);
			id.setRecemNascido(recemNascido);
			id.setUfeEmaExaSigla(ufeEmaExaSigla);
			id.setUfeEmaManSeq(ufeEmaManSeq);
			id.setUfeUnfSeq(ufeUnfSeq);
			id.setPertenceSumario(pertenceSumario);
			id.setIseSoeSeqDept(iseSoeSeqDept);
			id.setIseSeqpDept(iseSeqpDept);
			id.setCalSeq(calSeq);
			id.setCalNome(calNome);
			id.setReeValor(reeValor);
			id.setIseSoeSeq(iseSoeSeq);
			id.setIseSeqp(iseSeqp);
			id.setCacSeq(cacSeq);
			id.setRcdGtcSeq(rcdGtcSeq);
			id.setRcdSeqp(rcdSeqp);
			id.setDescricao(descricao);
			id.setDthrEventoAreaExec(dthrEventoAreaExec);
			id.setCalNomeSumario(calNomeSumario);
			id.setOrdem(ordem);
			id.setIndImprime(indImprime);
			id.setDthrFim(dthrFim);
			
			exame.setId(id);
		}		
		return exame;
	}
	
	/**
	 * 
	 * @param vDescricaoCaracteristica
	 * @param ltoLtoId
	 * @param atendimento
	 * @param resultadoExame
	 * @return
	 */
	public AelPolSumLegenda obterSumarioLegenda(String vDescricaoCaracteristica, String ltoLtoId, AelResultadoExame resultadoExame) {
		
		AelSolicitacaoExames solicitacao = resultadoExame.getItemSolicitacaoExame().getSolicitacaoExame();
		AghAtendimentos atendimento = resultadoExame.getItemSolicitacaoExame().getSolicitacaoExame().getAtendimento();
		AelItemSolicitacaoExames item = resultadoExame.getItemSolicitacaoExame();
		
		Date dataHoraEvento = DateUtil.obterData(1900, 1, 1);
		for(AelExtratoItemSolicitacao extrato : item.getAelExtratoItemSolicitacao()) {
			if(extrato.getAelSitItemSolicitacoes().getCodigo().equals("AE")){
				if(dataHoraEvento.compareTo(extrato.getDataHoraEvento()) < 0) {
					dataHoraEvento = extrato.getDataHoraEvento();
				}
			}
		}
		Boolean recemNascido = false;
		if(solicitacao != null) {
			recemNascido = solicitacao.getRecemNascido();
		}
		DominioSumarioExame pertenceSumario = DominioSumarioExame.N;
		if(item != null && item.getAelAmostraItemExames() != null) {
			pertenceSumario = item.getAelExameMaterialAnalise().getPertenceSumario();
		}		
		AelPolSumLegenda sumLegenda = new AelPolSumLegenda();
		AelPolSumLegendaId id = new AelPolSumLegendaId();
		id.setProntuario(atendimento.getProntuario());
		id.setRecemNascido(recemNascido);
		//id.setPertenceSumario(item.getAelExameMaterialAnalise().getPertenceSumario());
		id.setPertenceSumario(pertenceSumario);
		id.setDthrEvento(dataHoraEvento);
		id.setDescricao(vDescricaoCaracteristica);
		id.setNumeroLegenda(resultadoExame.getResultadoCodificado().getId().getSeqp());
		id.setGrupoLegenda(resultadoExame.getResultadoCodificado().getId().getGtcSeq());
		id.setLtoLtoId(ltoLtoId);
		id.setDthrFim(atendimento.getDthrFim());
		sumLegenda.setId(id);
		return sumLegenda;
	}
	
	/**
	 * 
	 * @param vOrdemRel
	 * @param vNroLinha
	 * @param vDescricaoPai
	 * @return
	 */
	public AelPolSumMascLinha obterAelPolSumMascLinha(Integer vOrdemRel, Integer vNroLinha, String vDescricaoPai) {
		AelPolSumMascLinha obj = new AelPolSumMascLinha();
		AelPolSumMascLinhaId id = new AelPolSumMascLinhaId();		
		id.setOrdemRelatorio(vOrdemRel);
		id.setNroLinha(vNroLinha);
		id.setDescricao(vDescricaoPai);
		obj.setId(id);
		return obj;
	}
	
	/**
	 * 
	 * @param vOrdemRel
	 * @param vNroLinha
	 * @param vNroCampo
	 * @param descricao
	 * @return
	 */
	public AelPolSumMascCampoEdit obterAelPolSumMascCampoEdit(Integer vOrdemRel, Integer vNroLinha, Integer vNroCampo, String descricao) {
		AelPolSumMascCampoEdit obj = new AelPolSumMascCampoEdit();
		AelPolSumMascCampoEditId id = new AelPolSumMascCampoEditId();
		id.setOrdemRelatorio(vOrdemRel);
		id.setNroCampoEdit(vNroLinha);
		id.setNroCampoEdit(vNroCampo);
		id.setDescricao(CoreUtil.converterRTF2Text(descricao));
		obj.setId(id);
		return obj;
	}
	
	/**
	 * 
	 * @param vResultadoTab
	 * @param vCodObservacao
	 * @param pertenceSumario
	 * @param prontuario
	 * @param recemNascido
	 * @param ltoLtoId
	 * @return
	 */
	public AelPolSumObservacao obterAelPolSumObservacao(AelItemSolicitacaoExames itemSolicitacao, String vResultadoTab, Short vCodObservacao, DominioSimNao recemNascido, Integer prontuario, DominioSumarioExame pertenceSumario, String ltoLtoId) {
		
		AelPolSumObservacao obj = new AelPolSumObservacao();
		
		if(StringUtils.isNotBlank(vResultadoTab)) {
			//vCodObservacao = obterMaxCodObservacao(itemSolicitacao.getSolicitacaoExame().getAtendimento().getProntuario(), DominioSimNao.getInstance(itemSolicitacao.getSolicitacaoExame().getRecemNascido()), itemSolicitacao.getAelExameMaterialAnalise().getPertenceSumario(), ltoLtoId);
			int cod = 0;
			if(vCodObservacao == null || vCodObservacao.intValue() == 0) {
				cod = 1;
			} else {
				cod = vCodObservacao.intValue();
				cod = cod + 1;
			}
			
			Date dataHoraEvento = DateUtil.obterData(1900, 1, 1);
			for(AelExtratoItemSolicitacao extrato : itemSolicitacao.getAelExtratoItemSolicitacao()) {
				if(extrato.getAelSitItemSolicitacoes().getCodigo().equals("AE")){
					if(dataHoraEvento.compareTo(extrato.getDataHoraEvento()) < 0) {
						dataHoraEvento = extrato.getDataHoraEvento();
					}
				}
			}
			vCodObservacao = (short) cod;
			
			AelPolSumObservacaoId id = new AelPolSumObservacaoId();
			id.setProntuario(itemSolicitacao.getSolicitacaoExame().getAtendimento().getProntuario());
			id.setRecemNascido(itemSolicitacao.getSolicitacaoExame().getRecemNascido());
			id.setPertenceSumario(itemSolicitacao.getAelExameMaterialAnalise().getPertenceSumario()); 					
			id.setDthrEvento(dataHoraEvento);
			id.setCodigoMensagem(vCodObservacao);
			id.setDescricao(CoreUtil.converterRTF2Text(vResultadoTab));
			id.setLtoLtoId(ltoLtoId);
			id.setDthrFim(itemSolicitacao.getSolicitacaoExame().getAtendimento().getDthrFim());
			obj.setId(id);
		}	
		return obj;
	}
	
}
