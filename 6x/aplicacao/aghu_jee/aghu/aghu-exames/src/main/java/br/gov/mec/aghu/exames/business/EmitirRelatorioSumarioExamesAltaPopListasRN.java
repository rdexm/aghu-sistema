/**
 * 
 */
package br.gov.mec.aghu.exames.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioObjetoVisual;
import br.gov.mec.aghu.dominio.DominioSumarioExame;
import br.gov.mec.aghu.dominio.DominioTipoCampoCampoLaudo;
import br.gov.mec.aghu.exames.business.EmitirRelatorioSumarioExamesAltaRN.VarMampAltaSumExames;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelExtratoItemSolicitacao;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelNotaAdicional;
import br.gov.mec.aghu.model.AelResultadoExame;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.MamPcSumExameMasc;
import br.gov.mec.aghu.model.MamPcSumExameTab;
import br.gov.mec.aghu.model.MamPcSumLegenda;
import br.gov.mec.aghu.model.MamPcSumLegendaId;
import br.gov.mec.aghu.model.MamPcSumMascCampoEdit;
import br.gov.mec.aghu.model.MamPcSumMascLinha;
import br.gov.mec.aghu.model.MamPcSumObservacao;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class EmitirRelatorioSumarioExamesAltaPopListasRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(EmitirRelatorioSumarioExamesAltaPopListasRN.class);

	@Override
	@Deprecated
	public Log getLogger() {
		return LOG;
	}
	

	private static final long serialVersionUID = -3949690073502330065L;

	public MamPcSumObservacao obterMamPcSumObservacao(AelItemSolicitacaoExames itemSolicitacao, String vResultadoTab, 
													  Short vCodObservacao, Boolean recemNascido, Integer prontuario, 
													  DominioSumarioExame pertenceSumario, String ltoLtoId, 
													  Integer pacCodigo, Integer ppePleSeq, Integer ppeSeqp) {
		
		MamPcSumObservacao obj = null;
		
		if(StringUtils.isNotBlank(vResultadoTab)) {
			
			obj = new MamPcSumObservacao();
			
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
			
			obj.setProntuario(itemSolicitacao.getSolicitacaoExame().getAtendimento().getProntuario());
			obj.setRecemNascido(itemSolicitacao.getSolicitacaoExame().getRecemNascido());
			obj.setPertenceSumario(itemSolicitacao.getAelExameMaterialAnalise().getPertenceSumario()); 					
			obj.setDthrEvento(dataHoraEvento);
			obj.setCodigoMensagem(vCodObservacao);
			obj.setDescricao(CoreUtil.converterRTF2Text(vResultadoTab));
			obj.setLtoLtoId(ltoLtoId);
			obj.setDthrFim(itemSolicitacao.getSolicitacaoExame().getAtendimento().getDthrFim());
			obj.setPacCodigo(pacCodigo);
			obj.setPpePleSeq(ppePleSeq);
			obj.setPpeSeqp(ppeSeqp);
		}	
		return obj;
	}
	
	/**
	 * ORADB: MAMK_PC_EXAMES.MAMP_PC_SUM_INS_SEM
	 * 
	 *  Insere registro para identificação de existencia de exme sem máscara
	 *  para que este seja impresso pelo report -  este reg de identif ñ será
	 *  impresso no report 
  
     * eSchweigert: Como no AGHU não será feito INSERT a chamada a: MAMK_PC_EXAMES.MAMP_PC_SUM_INS_SEM
     *			  iria apenas retornar uma instancia do pojo populada, visando evitar violação de PMD por
     *			  passagem de multiplos parametros, suprimi este método retornando a instância neste ponto.
	 */
	public MamPcSumExameTab obterMamPcSumExameTab(final VarMampAltaSumExames var, final Integer pPpeSeqp, final Integer pPpePleSeq){
		final MamPcSumExameTab mpset = new MamPcSumExameTab();
		
		mpset.setProntuario(var.vPrntControle);
		mpset.setPacCodigo(var.vPacCodigoControle);
		mpset.setLtoLtoId(var.vLtoControle);
		mpset.setRecemNascido(var.vRnControle);
		mpset.setUfeEmaExaSigla(var.vExaSiglaControle);
		mpset.setUfeEmaManSeq(var.vManSeqControle);
		mpset.setUfeUnfSeq(var.vUnfSeqControle);
		mpset.setPertenceSumario(var.vPertenceSumControle);
		mpset.setIseSoeSeqDept(null);
		mpset.setIseSeqpDept(null);
		mpset.setCalSeq(Integer.valueOf(1));
		mpset.setCalNome("X");
		mpset.setReeValor(Double.valueOf(0));
		mpset.setIseSoeSeq(var.vSoeSeqControle);
		mpset.setIseSeqp(var.vIseSeqpControle);
		mpset.setCacSeq(null);
		mpset.setRcdGtcSeq(null);
		mpset.setRcdSeqp(null);
		mpset.setDescricao(null);
		mpset.setDthrEventoAreaExec(var.vDthrControle);
		mpset.setOrdem(null);
		mpset.setCalNomeSumario(null);
		mpset.setIndImprime(Boolean.FALSE);
		mpset.setDthrFim(var.vDthrFimControle);
		mpset.setPpePleSeq(pPpePleSeq);
		mpset.setPpeSeqp(pPpeSeqp); 
		
		return mpset;
	}
	
	/**
	 * ORADB: MAMK_PC_EXAMES.MAMP_PC_SUM_INS_SEM
	 * 
	 *  Insere registro para identificação de existencia de exme sem máscara
	 *  para que este seja impresso pelo report -  este reg de identif ñ será
	 *  impresso no report 
  
     * eSchweigert: Como no AGHU não será feito INSERT a chamada a: MAMK_PC_EXAMES.MAMP_PC_SUM_INS_SEM
     *			  iria apenas retornar uma instancia do pojo populada, visando evitar violação de PMD por
     *			  passagem de multiplos parametros, suprimi este método retornando a instância neste ponto.
	 */
	public MamPcSumExameTab obterMamPcSumExameTab( final AelItemSolicitacaoExames rCursor, final AelResultadoExame rPclRee,
												   final VarMampAltaSumExames var, final String rCursor_ltoLtoId, 
												   final Integer pPpeSeqp, final Integer pPpePleSeq){
		
		final MamPcSumExameTab mpset = new MamPcSumExameTab();

		if(rCursor.getSolicitacaoExame().getAtendimento() != null){
			mpset.setProntuario(rCursor.getSolicitacaoExame().getAtendimento().getProntuario());
			mpset.setPacCodigo(rCursor.getSolicitacaoExame().getAtendimento().getPaciente().getCodigo());
			mpset.setDthrFim(rCursor.getSolicitacaoExame().getAtendimento().getDthrFim());
			
		}
		
		mpset.setLtoLtoId(rCursor_ltoLtoId);
		mpset.setRecemNascido(rCursor.getSolicitacaoExame().getRecemNascido());
		mpset.setUfeEmaExaSigla(rCursor.getAelUnfExecutaExames().getId().getEmaExaSigla());
		mpset.setUfeEmaManSeq(rCursor.getAelUnfExecutaExames().getId().getEmaManSeq());
		mpset.setUfeUnfSeq(rCursor.getAelUnfExecutaExames().getId().getUnfSeq().getSeq());
		mpset.setPertenceSumario(rCursor.getAelExameMaterialAnalise().getPertenceSumario());
		if(rCursor.getItemSolicitacaoExame() != null) {
			mpset.setIseSoeSeqDept(rCursor.getItemSolicitacaoExame().getId().getSoeSeq());
			mpset.setIseSeqpDept(rCursor.getItemSolicitacaoExame().getId().getSeqp());
		}
		mpset.setCalSeq(rPclRee.getParametroCampoLaudo().getCampoLaudo().getSeq());
		mpset.setCalNome(var.vNome);
		mpset.setReeValor(var.vValor);
		mpset.setIseSoeSeq(rCursor.getId().getSoeSeq());
		mpset.setIseSeqp(rCursor.getId().getSeqp());
		if (rPclRee.getResultadoCaracteristica()!=null){
			mpset.setCacSeq(rPclRee.getResultadoCaracteristica().getSeq());
		}
		if (rPclRee.getResultadoCodificado()!=null){
			mpset.setRcdGtcSeq(rPclRee.getResultadoCodificado().getId().getGtcSeq());
			mpset.setRcdSeqp(rPclRee.getResultadoCodificado().getId().getSeqp());
		}	
		mpset.setDescricao(var.vDescricaoResultado);
		mpset.setDthrEventoAreaExec(obterMaxDthrEvento(rCursor));
		mpset.setOrdem(rPclRee.getParametroCampoLaudo().getCampoLaudo().getOrdem());
		mpset.setCalNomeSumario(var.vNomeSumario);
		mpset.setIndImprime(Boolean.TRUE);
		mpset.setPpePleSeq(pPpePleSeq);
		mpset.setPpeSeqp(pPpeSeqp);
		
		return mpset;
	}
	
	public MamPcSumLegenda obterMamPcSumLegenda(final VarMampAltaSumExames var, final AelResultadoExame rPclRee, final AghAtendimentos atendimento, 
			   final Boolean recemNascido, final DominioSumarioExame pertenceSumario, String vDescricao,
			   final AelItemSolicitacaoExames rCursor, final Integer pPpeSeqp, final Integer pPpePleSeq,
			   final Integer numeroLegenda, final Integer grupoLegenda, String vLtoLtoId) {

		final MamPcSumLegenda legenda = new MamPcSumLegenda();
		final MamPcSumLegendaId id = new MamPcSumLegendaId();

		if(atendimento != null){
			id.setProntuario(atendimento.getProntuario());
			id.setDthrFim(atendimento.getDthrFim());
			id.setPacCodigo(atendimento.getPaciente().getCodigo());
		}
		
		id.setRecemNascido(recemNascido);
		id.setPertenceSumario(pertenceSumario);
		id.setDthrEvento(obterMaxDthrEvento(rCursor));
		id.setNumeroLegenda(numeroLegenda);
		id.setDescricao(vDescricao);
		id.setGrupoLegenda(grupoLegenda);
		id.setLtoLtoId(vLtoLtoId);
		id.setPpePleSeq(pPpePleSeq);
		id.setPpeSeqp(pPpeSeqp);
		legenda.setId(id);
		
		return legenda;	
	}
	
	public MamPcSumMascCampoEdit obterMamPcSumMascCampoEdit( final Integer vOrdemRel, final Integer vNroLinha, final Integer vNroCampoEdit,
															    final String descricao, final Integer pPpeSeqp, final Integer pPpePleSeq) {
		
		return new MamPcSumMascCampoEdit(vOrdemRel, vNroLinha, vNroCampoEdit, descricao, pPpePleSeq, pPpeSeqp, null, null, null, null);
	}
	
	public MamPcSumMascLinha obterMamPcSumMascLinha(final Integer vOrdemRel, final Integer vNroLinha, final String vResultadoComMascara,
													final Integer pPpeSeqp, final Integer pPpePleSeq) {
		return new MamPcSumMascLinha(vOrdemRel, vNroLinha,
									 null, pPpePleSeq, pPpeSeqp, 
									 null, null, vResultadoComMascara, null, null
								    );
	}
	
	public MamPcSumMascLinha obterMamPcSumMascLinha(final AelItemSolicitacaoExames examePai, final VarMampAltaSumExames var,
			final AelItemSolicitacaoExames rCursor, final Integer pPpeSeqp, final Integer pPpePleSeq, final String separador) {
	
		if(examePai != null){
			if(examePai.getDescMaterialAnalise() != null){
				var.vDescricaoPai = obterDescricaoExamePai(examePai, null, separador);
				var.vNroLinha++;
				return new MamPcSumMascLinha(var.vOrdemRel, var.vNroLinha,
											 null, pPpePleSeq, pPpeSeqp, 
											 null, null, var.vDescricaoPai, null, null
											);
			} else {
				var.vDescricaoPai = obterDescricaoExamePai(examePai, rCursor.getAelExameMaterialAnalise(), separador);
				var.vNroLinha++;
				return new MamPcSumMascLinha(var.vOrdemRel, var.vNroLinha,
										     null, pPpePleSeq, pPpeSeqp, 
											 null, null, var.vDescricaoPai, null, null
										    );
			}
		} else {
			var.vDescricaoPai = "PAI NÃO ENCONTRADO";
			var.vNroLinha++;

			return new MamPcSumMascLinha(var.vOrdemRel, var.vNroLinha,
									     null, pPpePleSeq, pPpeSeqp, 
										 null, null, var.vDescricaoPai, null, null
									    );
		}
	}
	
	/**
	 * ORADB: AELK_POL_SUM_EXAMES.AELP_POL_SUM_INS_COM
	 */
	public MamPcSumExameMasc obterMamPcSumExameMasc(final VarMampAltaSumExames var, final AelItemSolicitacaoExames rCursor, 
			final Date dataHoraEvento, final String ltoIdRCursor, final Integer pPpeSeqp, final Integer pPpePleSeq){
		
		final MamPcSumExameMasc mpsem = new MamPcSumExameMasc();
		
		if(rCursor.getSolicitacaoExame().getAtendimento() != null){
			mpsem.setProntuario(rCursor.getSolicitacaoExame().getAtendimento().getProntuario());
			mpsem.setPacCodigo(rCursor.getSolicitacaoExame().getAtendimento().getPaciente().getCodigo());
			mpsem.setDthrFim(rCursor.getSolicitacaoExame().getAtendimento().getDthrFim());
		}
		
		mpsem.setLtoLtoId(ltoIdRCursor);
		mpsem.setRecemNascido(rCursor.getSolicitacaoExame().getRecemNascido());
		mpsem.setUfeEmaExaSigla(rCursor.getExame().getSigla());
		mpsem.setUfeEmaManSeq(rCursor.getMaterialAnalise().getSeq());
		mpsem.setUfeUnfSeq(rCursor.getUnidadeFuncional().getSeq());
		mpsem.setPertenceSumario(rCursor.getAelExameMaterialAnalise().getPertenceSumario());
		
		if(rCursor.getItemSolicitacaoExame() != null){
			mpsem.setIseSoeSeqDept(rCursor.getItemSolicitacaoExame().getId().getSoeSeq());
			mpsem.setIseSeqpDept(rCursor.getItemSolicitacaoExame().getId().getSeqp());
		}
		
		mpsem.setIseSoeSeq(rCursor.getId().getSoeSeq());
		mpsem.setIseSeqp(rCursor.getId().getSeqp());
		mpsem.setDescricao(null);
		mpsem.setDthrEventoLib(dataHoraEvento);
		mpsem.setOrdemRelatorio(var.vOrdemRel);
		mpsem.setOrdemAgrupamento(var.vOrdemAgrup);
		mpsem.setPpePleSeq(pPpePleSeq);
		mpsem.setPpeSeqp(pPpeSeqp);
		
		return mpsem;
	}
	
	
	
	public Date obterMaxDataHoraEvento(List<AelExtratoItemSolicitacao> lista) {
		Date dataHoraEvento = DateUtil.obterData(1900, 1, 1);
		for(AelExtratoItemSolicitacao extrato : lista) {
			if(extrato.getAelSitItemSolicitacoes().getCodigo().equals("AE")){
				if(dataHoraEvento.compareTo(extrato.getDataHoraEvento()) < 0) {
					dataHoraEvento = extrato.getDataHoraEvento();
				}
			}
		}
		return dataHoraEvento;
	}

	/**
	 * Implementa coluna MAX(eis.dthr_evento)   dthr_evento_ae, do CURSOR c_exames_alta
	 */
	private Date obterMaxDthrEvento(AelItemSolicitacaoExames item){
		Date dataHoraEvento = DateUtil.obterData(1900, 1, 1);
		for(AelExtratoItemSolicitacao extrato : item.getAelExtratoItemSolicitacao()) {
			if(extrato.getAelSitItemSolicitacoes().getCodigo().equals("AE")){
				if(dataHoraEvento.compareTo(extrato.getDataHoraEvento()) < 0) {
					dataHoraEvento = extrato.getDataHoraEvento();
				}
			}
		}
		
		return dataHoraEvento;
	}
	
	private String obterDescricaoExamePai(AelItemSolicitacaoExames examePai, AelExamesMaterialAnalise materialAnalise, String separador) {
		StringBuilder sb = new StringBuilder();
		if(materialAnalise == null) {
			if(StringUtils.isNotBlank(separador)) {				
				sb.append(examePai.getExame().getDescricao()).append(StringUtils.rightPad("", 6)).append(separador).append(StringUtils.rightPad("", 9)).append(examePai.getDescMaterialAnalise());
			} else {
				sb.append(examePai.getExame().getDescricao()).append(StringUtils.rightPad("", 15)).append(examePai.getDescMaterialAnalise());
			}
		} else {
			if(StringUtils.isNotBlank(separador)) {				
				sb.append(examePai.getExame().getDescricao()).append(StringUtils.rightPad("", 6)).append(separador).append(StringUtils.rightPad("", 9)).append(materialAnalise.getAelMateriaisAnalises().getDescricao());
			} else {
				sb.append(examePai.getExame().getDescricao()).append(StringUtils.rightPad("", 15)).append(materialAnalise.getAelMateriaisAnalises().getDescricao());	
			}
		}
		return sb.toString();
	}
	
	/**
	 * Inclusão do tratamento do novos objetos na máscara dos laudos
	 */
	public boolean verificarDominioObjetoVisualOutraPosicao(final DominioObjetoVisual objetoVisual,
			final String textoLivre, final DominioTipoCampoCampoLaudo tipoCampo,
			final Integer parametroCampoLaudoSeq, final VarMampAltaSumExames var,
			final boolean validarTamanhoTexto, final MascaraExamesRN mascaraExamesRN, 
			final AelItemSolicitacaoExames rCursor) throws BaseException {

		boolean retorno = false;
		
		if (DominioObjetoVisual.EQUIPAMENTO.equals(objetoVisual)) {
			var.vResultadoComMascara = mascaraExamesRN.buscaInformacaoEquipamento(rCursor.getId().getSoeSeq(), rCursor.getId().getSeqp());
			retorno = true;
			
		} else if (DominioObjetoVisual.METODO.equals(objetoVisual)) {
			var.vResultadoComMascara = mascaraExamesRN.buscaInformacaoMetodo(rCursor.getId().getSoeSeq(), rCursor.getId().getSeqp());
			retorno = true;
			
		} else if (DominioObjetoVisual.RECEBIMENTO.equals(objetoVisual)) {
			var.vResultadoComMascara = mascaraExamesRN.buscaInformacaoRecebimento(rCursor.getId().getSoeSeq(), rCursor.getId().getSeqp());
			retorno = true;
			
		} else if (DominioObjetoVisual.HISTORICO.equals(objetoVisual)) {
			List<String> strResultado = mascaraExamesRN.buscaInformacaoHistorico(rCursor.getId().getSoeSeq(), rCursor.getId().getSeqp(),
																				 parametroCampoLaudoSeq);
			if (strResultado != null && strResultado.size() > 0) {
				var.vResultadoComMascara = strResultado.get(0);
			}
			retorno = true;
			
		} else if (DominioObjetoVisual.VALORES_REFERENCIA.equals(objetoVisual)) {
			var.vResultadoComMascara = null;
			retorno = true;
			
		// -------------------------------------------------------------------------------------------------------------------
		} else if (StringUtils.isNotBlank(textoLivre) && !DominioTipoCampoCampoLaudo.E.equals(tipoCampo)) {
			if (validarTamanhoTexto) {
				int tamanhoTexto = 0;
				if (StringUtils.isBlank(var.vResultadoComMascara)) {
					if (verificarVersaoExaSiglaTextoLivre(var.vVersaoExaSigla, textoLivre)) {
						tamanhoTexto = textoLivre.length();
						for (int i = 0; i <= 40; i++) {
							if (i <= tamanhoTexto) {
								var.vResultadoComMascara = textoLivre;
							} else {
								if (StringUtils.equalsIgnoreCase("1)",
										textoLivre)) {
									var.vResultadoComMascara = (".").concat(var.vResultadoComMascara);
								} else {
									var.vResultadoComMascara = var.vResultadoComMascara.concat(".");
								}
							}
						}
						if (StringUtils.equalsIgnoreCase("1)", textoLivre)) {
							var.vResultadoComMascara = "..".concat(var.vResultadoComMascara).concat(StringUtils.rightPad("", 10));
						}
					} else {
						var.vResultadoComMascara = textoLivre.concat(StringUtils.rightPad("", 10));
					}
				} else {
					var.vResultadoComMascara = var.vResultadoComMascara.concat(textoLivre).concat(StringUtils.rightPad("", 10));
				}

			} else {
				if(StringUtils.isBlank(var.vResultadoComMascara)) {
					var.vResultadoComMascara = textoLivre.concat(StringUtils.rightPad("", 10));
				} else {
					var.vResultadoComMascara = var.vResultadoComMascara.concat(textoLivre).concat(StringUtils.rightPad("", 10));
				}
			}
			
			retorno = true;
		}
		
		if (StringUtils.isNotBlank(var.vResultadoComMascara)) {
			// Caso a string passada for HTML o texto será 
			// extraído das tags HTML, senão mantém o texto como está			
			var.vResultadoComMascara = CoreUtil.extrairTextoDeHtml(var.vResultadoComMascara);	
		}
		
		return retorno;
	}
	
	public void manipularNotasAdicionais(List<AelNotaAdicional> notasAdicionais, final VarMampAltaSumExames var, final AelItemSolicitacaoExames rCursor, 
			final String ltoIdRCursor, final Integer pPpeSeqp, final Integer pPpePleSeq) {
		
		boolean vPrimeiraVezNA = true;
		
		if(notasAdicionais != null) {
			for(AelNotaAdicional nota : notasAdicionais) {
				if(vPrimeiraVezNA) {
					var.vResultadoComMascara = super.getResourceBundleValue("AEL_POL_SUM_EXAMES_NOTAS_ADICIONAIS");
					vPrimeiraVezNA = false;
					var.vNroLinha++;
					
					var.listaMamPcSumMascLinha.add(obterMamPcSumMascLinha( var.vOrdemRel, var.vNroLinha, 
																		   var.vResultadoComMascara, pPpeSeqp, pPpePleSeq));
					var.vEstMascLinha++;
					var.vResultadoComMascara = null;
				}
				
				var.vDescricaoNotasAdicionais = StringUtils.trim(nota.getNotasAdicionais());
				var.vResultadoComMascara = StringUtils.substring(var.vDescricaoNotasAdicionais, 1, 2000);
				var.vNroLinha++;
				var.listaMamPcSumMascLinha.add( obterMamPcSumMascLinha( var.vOrdemRel, var.vNroLinha, 
																		var.vResultadoComMascara, pPpeSeqp, pPpePleSeq) );
				var.vEstMascLinha++;
				var.vResultadoComMascara = null;
				
				if(StringUtils.isNotBlank(StringUtils.substring(var.vDescricaoNotasAdicionais, 2000, 2001))) {
					var.vResultadoComMascara = StringUtils.substring(var.vDescricaoNotasAdicionais, 2000, 2001);
					var.vNroLinha++;
					var.listaMamPcSumMascLinha.add(obterMamPcSumMascLinha( var.vOrdemRel, var.vNroLinha, 
																		   var.vResultadoComMascara, pPpeSeqp, pPpePleSeq) );
					var.vEstMascLinha++;
					var.vResultadoComMascara = null;
				}
				
				//TODO: verificar formatacao da data
				var.vResultadoComMascara = nota.getCriadoEm().toString().concat(StringUtils.rightPad("", 6)).concat(nota.getServidor().getPessoaFisica().getNome());
				var.vNroLinha++;
				var.listaMamPcSumMascLinha.add( obterMamPcSumMascLinha( var.vOrdemRel, var.vNroLinha, 
																		var.vResultadoComMascara, pPpeSeqp, pPpePleSeq));
				var.vEstMascLinha++;
				var.vResultadoComMascara = null;
			}
		}
	}

	private boolean verificarVersaoExaSiglaTextoLivre(String vVersaoExaSigla, String textoLivre) {
		if(StringUtils.equalsIgnoreCase("TSA", vVersaoExaSigla) 
				&& (StringUtils.equalsIgnoreCase("1)", textoLivre) 
					|| (!StringUtils.equalsIgnoreCase("2)", textoLivre) 
					&& !StringUtils.equalsIgnoreCase("3)", textoLivre) 
					&& !StringUtils.equalsIgnoreCase("4)", textoLivre)) )) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	public List<DominioSumarioExame> obterListagemSumario(){
		final List<DominioSumarioExame> listSitCodigo = new ArrayList<DominioSumarioExame>();
		
		listSitCodigo.add(DominioSumarioExame.B);
		listSitCodigo.add(DominioSumarioExame.E);
		listSitCodigo.add(DominioSumarioExame.G);
		listSitCodigo.add(DominioSumarioExame.H);
		
		return listSitCodigo;
	}
}
