package br.gov.mec.aghu.prescricaomedica.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.ambulatorio.vo.AtestadoVO;
import br.gov.mec.aghu.dominio.DominioIndConcluido;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioIndTipoAltaSumarios;
import br.gov.mec.aghu.model.MamAtestados;
import br.gov.mec.aghu.model.MamReceituarios;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Responsavel pelas acoes para conclusao de um Sumario de Alta.
 * 
 * @author rcorvalao
 */
@Stateless
public class ConcluirSumarioAltaON extends BaseBusiness {


@EJB
private ManterAltaSumarioRN manterAltaSumarioRN;

@EJB
private ConcluirSumarioAltaRN concluirSumarioAltaRN;

@EJB
private ManterPrescricaoMedicaRN manterPrescricaoMedicaRN;

private static final Log LOG = LogFactory.getLog(ConcluirSumarioAltaON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IAmbulatorioFacade ambulatorioFacade;

@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 4015007993093728226L;

	public enum ConcluirSumarioAltaONExceptionCode implements BusinessExceptionCode {
		MPM_02717, MPM_02718, MPM_02720, MPM_02722, MPM_02723, MPM_02724, MPM_02858, MPM_02869, 
		MPM_02714, MPM_02719, MPM_02715, MPM_02716, MPM_03340, MPM_03341, MPM_03342
		, EXISTE_LAUDO_PENDENTE_JUSTIFICATIVA, MAM_03043, MAM_03044, MAM_03045, MAM_03046, MAM_03047, MAM_ERRO_CONCLUSAO_ATESTADO
		;
	}
	
	public void validarCamposObrigatoriosSumarioAlta(MpmAltaSumario altaSumario) throws BaseException {
		// RN_ASUP_VER_ALTA
		this.getConcluirSumarioAltaRN().validacoesConclusaoSumarioAlta(altaSumario);
	}
	
	/**
	 * Executa a Finalizacao da Conclusao do Sumario de Alta.<br>
	 * Validações para conclusao do Sumario de Alta.<br>
	 * Gera Laudos.<br>
	 * Verifica se precisa de Justificativas.<br>
	 * Atualiza flag no banco indicando a conclusao da alta do paciente.<br>
	 * 
	 * @param altaSumario
	 * @return ehSolicitarJustificativa
	 * @throws BaseException
	 */
	public boolean concluirSumarioAlta(MpmAltaSumario altaSumario, String nomeMicrocomputador, RapServidores servidorValida) throws BaseException {
		boolean ehSolicitarJustificativa = false;
		
		if (DominioIndTipoAltaSumarios.ANT != altaSumario.getTipo()) {

			// RN_ASUP_VER_ALTA
			this.getConcluirSumarioAltaRN().validacoesConclusaoSumarioAlta(altaSumario);
			
			// ===== Codigo NAO migrado =====
			this.certificadoDigital(altaSumario);
			// ===== Codigo NAO migrado =====
			
			//#53992
			this.atualizaServidorValida(altaSumario, servidorValida);

			// MPMP_GERA_LAUDOS
			this.getConcluirSumarioAltaRN().gerarLaudos(altaSumario);

			// ASSINA RELATÓRIO DE RECITAS
			this.assinaRelatorioReceitas(altaSumario);
			
			
			// Busca o grupo do convenio, para verificar se é SUS
			// Se o convenio é SUS			
			boolean ehSUS = this.getConcluirSumarioAltaRN().ehConvenioSUS(altaSumario);
			
			// 	Verifica se existe laudo de Menor Permanência pendente
			boolean existePendencia = this.getConcluirSumarioAltaRN().existeLaudoMenorPermanenciaPendenteJustificativa(altaSumario);
			
			if (ehSUS && existePendencia) {
				ehSolicitarJustificativa = true;
			}
			
			if (!ehSolicitarJustificativa) {
				this.marcarSumarioAltaConcluido(altaSumario, nomeMicrocomputador);
			}
			// #46253 - Trata os atestados
			concluirImprimeAtestadosAlta(altaSumario.getId().getApaAtdSeq(), altaSumario.getId().getApaSeq(), altaSumario.getId().getSeqp());
		}
		
		return ehSolicitarJustificativa;
	}
	
	/**
	 * #46253 - @ORADB MPMP_CONC_IMPRIME_ATESTADO
	 * @param apaAtdSeq
	 * @param apaSeq
	 * @param seqp
	 * @throws ApplicationBusinessException
	 */
	private void concluirImprimeAtestadosAlta(Integer apaAtdSeq, Integer apaSeq, Short seqp) throws ApplicationBusinessException {
		Object[] situacoes = new Object[] {DominioIndPendenteAmbulatorio.P, DominioIndPendenteAmbulatorio.V};
		List<MamAtestados> rAte = this.ambulatorioFacade.obterAtestadoPorSumarioAlta(apaAtdSeq, apaSeq, seqp, null, situacoes);
		for (MamAtestados atestado : rAte) {
			concluirAtestadosAlta(apaAtdSeq, apaSeq, seqp, atestado.getSeq());
		}
	}
	
	/**
	 * #46253 - @ORADB MAMK_CONCLUIR.MAMP_CONC_ATE_ALTA
	 * @param apaAtdSeq
	 * @param apaSeq
	 * @param seqp
	 * @param seq
	 * @throws ApplicationBusinessException
	 */
	private void concluirAtestadosAlta(Integer apaAtdSeq, Integer apaSeq, Short seqp, Long seq) throws ApplicationBusinessException {
		List<MamAtestados> rAte = this.ambulatorioFacade.obterAtestadosPendentes(apaAtdSeq, apaSeq, seqp, seq);
		for (MamAtestados atestado : rAte) {
			// Trata Rascunho
			if (atestado.getIndPendente().equals(DominioIndPendenteAmbulatorio.R)) {
				Long ateSeq = atestado.getMamAtestados() != null ? atestado.getMamAtestados().getSeq() : null;
				try {
					this.ambulatorioFacade.excluirMamAtestado(atestado.getSeq());
				} catch(ApplicationBusinessException e) {
					throw new ApplicationBusinessException(ConcluirSumarioAltaONExceptionCode.MAM_03043);
				}
				if (ateSeq != null) {
					MamAtestados uAte = this.ambulatorioFacade.obterMamAtestadoPorChavePrimaria(ateSeq);
					uAte.setDthrMvto(null);
					uAte.setServidorMvto(null);
					try {
						this.ambulatorioFacade.persistirMamAtestado(uAte, false);
						
					} catch(ApplicationBusinessException e) {
						throw new ApplicationBusinessException(ConcluirSumarioAltaONExceptionCode.MAM_03044);
					}
				}
			// Trata Pendente
			} else if (atestado.getIndPendente().equals(DominioIndPendenteAmbulatorio.P)) {
				atestado.setIndPendente(DominioIndPendenteAmbulatorio.V);
				atestado.setDthrValida(new Date());
				RapServidores servidorLogado = this.servidorLogadoFacade.obterServidorLogado();
				atestado.setServidorValida(servidorLogado);
				try {
					this.ambulatorioFacade.persistirMamAtestado(atestado, false);
					
				} catch(ApplicationBusinessException e) {
					throw new ApplicationBusinessException(ConcluirSumarioAltaONExceptionCode.MAM_03045);
				}
				if (atestado.getMamAtestados() != null) {
					MamAtestados uAte = this.ambulatorioFacade.obterMamAtestadoPorChavePrimaria(atestado.getMamAtestados().getSeq());
					uAte.setIndPendente(DominioIndPendenteAmbulatorio.V);
					uAte.setDthrValidaMvto(new Date());
					uAte.setServidorValidaMvto(servidorLogado);
					try {
						this.ambulatorioFacade.persistirMamAtestado(uAte, false);
						
					} catch(ApplicationBusinessException e) {
						throw new ApplicationBusinessException(ConcluirSumarioAltaONExceptionCode.MAM_03046);
					}
				}
			// Trata Exclusão
			} else if (atestado.getIndPendente().equals(DominioIndPendenteAmbulatorio.E)) {
				atestado.setIndPendente(DominioIndPendenteAmbulatorio.V);
				atestado.setDthrValidaMvto(new Date());
				RapServidores servidorLogado = this.servidorLogadoFacade.obterServidorLogado();
				atestado.setServidorValidaMvto(servidorLogado);
				try {
					this.ambulatorioFacade.persistirMamAtestado(atestado, false);
					
				} catch(ApplicationBusinessException e) {
					throw new ApplicationBusinessException(ConcluirSumarioAltaONExceptionCode.MAM_03047);
				}
			}
		}
	}
	
	public void confirmarAtestadoPendente(MamAtestados atestado, Boolean imprimeAtestado) throws ApplicationBusinessException {
		atestado.setIndPendente(DominioIndPendenteAmbulatorio.V);
		atestado.setDthrValida(new Date());
		RapServidores servidorLogado = this.servidorLogadoFacade.obterServidorLogado();
		atestado.setServidorValida(servidorLogado);
		try {
			this.ambulatorioFacade.persistirMamAtestado(atestado, imprimeAtestado);
			
		} catch(ApplicationBusinessException e) {
			throw new ApplicationBusinessException(ConcluirSumarioAltaONExceptionCode.MAM_ERRO_CONCLUSAO_ATESTADO);
		}
	}
	
	public AtestadoVO obterDocumentoPacienteAtestado(Long atsSeq, Boolean imprimeAtestado) throws ApplicationBusinessException {
		AtestadoVO atestadoVO = null;
		// Consulta C1
		MamAtestados atestado = this.ambulatorioFacade.obterMamAtestadoPorChavePrimaria(atsSeq);
		if (atestado != null) {
			if (atestado.getIndPendente().equals(DominioIndPendenteAmbulatorio.P)) {
				this.confirmarAtestadoPendente(atestado, imprimeAtestado);
			}
			// @ORADB: MAMK_SINTAXE_REP.MAMP_DESCR_ATESTADO
			atestadoVO = this.ambulatorioFacade.obterDadosAtestado(atestado.getSeq());
			if (atestadoVO != null) {
				atestadoVO.setSeq(atestado.getSeq());
				atestadoVO.setEspecialidade(this.ambulatorioFacade
						.obterEspecialidade(atestado.getServidorValida().getId().getMatricula(), atestado.getServidorValida().getId().getVinCodigo()));
				atestadoVO.setNumeroVias(atestado.getNroVias());
						
				//obtem nome e assinatura do medico
				Object[] conselhoProfissional = this.getManterPrescricaoMedicaRN()
						.buscaConsProf(atestado.getServidorValida().getId().getMatricula(), atestado.getServidorValida().getId().getVinCodigo());					
				if (conselhoProfissional[1] != null) {
					atestadoVO.setNomeMedico(conselhoProfissional[1].toString()); 
				}
				if (conselhoProfissional[2] != null) {
					atestadoVO.setSiglaConselho(conselhoProfissional[2].toString()); 
				}
				if (conselhoProfissional[3] != null) {
					atestadoVO.setNumeroRegistroConselho(conselhoProfissional[3].toString()); 
				}
				// Seta a descrição do atestado de acordo com o seu tipo
				atestadoVO.setDescricaoAtestado(this.popularDescricaoTipoAtestado(atestadoVO.getTipoAtestado()));
			}
		}
		return atestadoVO;
	}
	
	public List<AtestadoVO> obterListaDocumentosPacienteAtestados(Integer apaAtdSeq, Integer apaSeq, Short seqp, Short tasSeq) throws ApplicationBusinessException {
		List<AtestadoVO> atestados = new ArrayList<AtestadoVO>();
		Object[] situacoes = new Object[] {DominioIndPendenteAmbulatorio.V};
		// Consulta C1
		List<MamAtestados> rAte = this.ambulatorioFacade.obterAtestadoPorSumarioAlta(apaAtdSeq, apaSeq, seqp, tasSeq, situacoes);
		if (rAte != null) {
			for (MamAtestados atestado : rAte) {
				// @ORADB: MAMK_SINTAXE_REP.MAMP_DESCR_ATESTADO
				AtestadoVO atestadoVO = this.ambulatorioFacade.obterDadosAtestado(atestado.getSeq());
				if (atestadoVO != null) {
					atestadoVO.setSeq(atestado.getSeq());
					atestadoVO.setEspecialidade(this.ambulatorioFacade
							.obterEspecialidade(atestado.getServidorValida().getId().getMatricula(), atestado.getServidorValida().getId().getVinCodigo()));
					atestadoVO.setNumeroVias(atestado.getNroVias());
					
					//obtem nome e assinatura do medico
					Object[] conselhoProfissional = this.getManterPrescricaoMedicaRN()
							.buscaConsProf(atestado.getServidorValida().getId().getMatricula(), atestado.getServidorValida().getId().getVinCodigo());					
					if (conselhoProfissional[1] != null) {
						atestadoVO.setNomeMedico(conselhoProfissional[1].toString()); 
					}
					if (conselhoProfissional[2] != null) {
						atestadoVO.setSiglaConselho(conselhoProfissional[2].toString()); 
					}
					if (conselhoProfissional[3] != null) {
						atestadoVO.setNumeroRegistroConselho(conselhoProfissional[3].toString()); 
					}
					// Seta a descrição do atestado de acordo com o seu tipo
					atestadoVO.setDescricaoAtestado(this.popularDescricaoTipoAtestado(atestadoVO.getTipoAtestado()));
					
					atestados.add(atestadoVO);
				}
			}
		}
		return atestados;
	}
	
	private String popularDescricaoTipoAtestado(String tipoAtestado) {
		String texto = "";
		if (tipoAtestado != null) {
			switch (tipoAtestado.charAt(0)) {
				case 'C' :
						texto = "Atestado de Comparecimento";
						break;
				case 'A' :
						texto = "Atestado de Acompanhamento";
						break;
				case 'M' :
						texto = "Atestado Médico";
						break;
				case 'R' :
						texto = "Atestado de Marcação";
						break;
				case 'G' :
						texto = "Atestado";
						break;
				case 'F' :
						texto = "Atestado FGTS e PIS/PASEP";
						break;
			}
		}
		return texto;
	}
	
	/**
	 * 
	 * @param altaSumario
	 * @throws ApplicationBusinessException 
	 */
	private void assinaRelatorioReceitas(MpmAltaSumario altaSumario) throws BaseException {

		// Percorre as receitas pertencentes a alta de sumário
		List<MamReceituarios> receitas = this.getAmbulatorioFacade().buscarReceituariosPorAltaSumario(altaSumario);
		for (MamReceituarios receita : receitas) {
			
			this.getConcluirSumarioAltaRN().assinaRelatorioReceitas(altaSumario, receita);
		}
		
	}

	public String obterMensagemResourceBundle(String key) {
		return getResourceBundleValue(key);
	}
	
	/**
	 * Executa Update no MpmAltaSumario.<br>
	 * Seta o indConcluido pra true.<br>
	 * 
	 * @param altaSumario
	 * @throws BaseException 
	 */
	private void marcarSumarioAltaConcluido(MpmAltaSumario altaSumario, String nomeMicrocomputador) throws BaseException {
		altaSumario.setConcluido(DominioIndConcluido.S);
		
		this.getManterAltaSumarioRN().atualizarAltaSumario(altaSumario, nomeMicrocomputador);
	}
	
	/**
	 * Verifica se existe laudo de Menor Permanência pendente.<br>
	 * Executa Update no MpmAltaSumario.<br>
	 * Seta o indConcluido pra true.<br>
	 * 
	 * Chamada no continuar Conclusao apos preencher Justificativas de Laudos no Sumario de Alta.<br>
	 * 
	 * @param altaSumario
	 * @throws BaseException
	 */
	public void continuarConclusaoSumarioAlta(MpmAltaSumario altaSumario, String nomeMicrocomputador) throws BaseException {
		// Verifica se existe laudo de Menor Permanência pendente
		boolean existePendencia = this.getConcluirSumarioAltaRN().existeLaudoMenorPermanenciaPendenteJustificativa(altaSumario);
		if (existePendencia) {
			throw new ApplicationBusinessException(ConcluirSumarioAltaONExceptionCode.EXISTE_LAUDO_PENDENTE_JUSTIFICATIVA);
		}
		
		this.marcarSumarioAltaConcluido(altaSumario, nomeMicrocomputador);		
	}
	
	/**
	 * Cancela a Conclusao do Sumaro de Alta.<br>
	 * Remove os Laudos gerados.<br>
	 * 
	 * Chamada no cancelar da tela de Justificativas de Laudos no Sumario de Alta.<br>
	 * 
	 * @param altaSumario
	 */
	public void cancelarConclusaoSumarioAlta(MpmAltaSumario altaSumario) throws BaseException {
		// Deveria ser excluidos os Laudos que foram gerados nesta acao de Concluir.
		// Como os Laudos sao gerados apenas quando nao existem, 
		// nao eh necessario fazer a exclusao dos Laudos quando 
		// for cancelada a Conclusao na Tela de Justificativa. 
	}
	
	
	
	private void atualizaServidorValida(MpmAltaSumario altaSumario, RapServidores servidorValida) {
		altaSumario.setServidorValida(servidorValida);
		/* 
		- Atualiza o servidorValida
		v_apa_atd_seq := name_in('asu.apa_atd_seq');
	   v_apa_seq     := name_in('asu.apa_seq');
	   v_seqp        := name_in('asu.seqp');
	   --
	   if get_application_property(CALLING_FORM) in ('MPMF_TRANSCR_SUMARIO') 
	   then
	      -- 
	      v_ser_matr_val:= name_in('global.mpm$sumr_matricula');
	      v_ser_vinc_val:= name_in('global.mpm$sumr_vin_codigo');
	      --
	      begin
	            update mpm_alta_sumarios
	               set ser_matricula_valida  = v_ser_matr_val,
	                   ser_vin_codigo_valida = v_ser_vinc_val   
	             where apa_atd_seq    = v_apa_atd_seq and
	                   apa_seq        = v_apa_seq and
	                   seqp           = v_seqp;
	             exception
	              when regra_negocio then
	                   raise;
	              when others then
	                   qms$handle_ofg45_messages ('E', TRUE, 'MPM-03708 #1' || SQLERRM);
	      end;
	      --
	   end if; 
	   --      
	*/
	}
	
	private void certificadoDigital(MpmAltaSumario altaSumario) {
		/*
		-- certificação digital
		   -- Verifica se está habilitada para uso de certificação digital
		   v_habilita_certif := aghk_certif_digital.aghc_habilita_certif('MPMF_SUMARIO_ALTA');
		   --
		   IF v_habilita_certif = 'S' AND  
			    CSEC_VER_ACAO_QUA_MA('ASSINATURA DIGITAL',v_ser_vinc_val,v_ser_matr_val)= 'S'
		   THEN
		   		--   		
		   		-- Imprime via do paciente
		   		mpmp_chama_relatorio ('I', 'CERTIF');
		   		--
		   		--Gera relatório mpmr_sumario_alta em formato pdf e insere nas tabelas agh_documentos e agh_versoes_documentos      		
		   		p_certif_digital;	    		
		   		--  		
		   ELSE
		   		mpmp_chama_relatorio ('I', null);
		   END IF;
		   */
	}

	
	/**
	 * Este método verifica se o hospital
	 * tem ambulatório ou não.
	 * 
	 * @author gfmenezes
	 * 	
	 * @return
	 * @throws BaseException 
	 */
	public Boolean existeAmbulatorio() throws BaseException {
		return this.getConcluirSumarioAltaRN().existeAmbulatorio();
	}
	
	

	protected ManterAltaSumarioRN getManterAltaSumarioRN() {
		return manterAltaSumarioRN;
	}
	
	protected ConcluirSumarioAltaRN getConcluirSumarioAltaRN() {
		return concluirSumarioAltaRN;
	}
	
	protected ManterPrescricaoMedicaRN getManterPrescricaoMedicaRN() {
		return manterPrescricaoMedicaRN;
	}
	
	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return this.ambulatorioFacade;
	}
	
}
