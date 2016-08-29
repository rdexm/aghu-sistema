package br.gov.mec.aghu.prescricaomedica.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoSolicitacaoMedicamento;
import br.gov.mec.aghu.farmacia.dao.AfaGrupoUsoMedicamentoDAO;
import br.gov.mec.aghu.model.AfaGrupoUsoMedicamento;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.MpmCidAtendimento;
import br.gov.mec.aghu.model.MpmItemPrescParecerMdto;
import br.gov.mec.aghu.model.MpmItemPrescricaoMdto;
import br.gov.mec.aghu.model.MpmJustifTrocaEsquemaTb;
import br.gov.mec.aghu.model.MpmJustificativaUsoMdto;
import br.gov.mec.aghu.model.MpmTrocaEsquemaTb;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.dao.MpmCidAtendimentoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemPrescParecerMdtoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemPrescricaoMdtoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmJustifTrocaEsquemaTbDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmJustificativaUsoMdtoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmTrocaEsquemaTbDAO;
import br.gov.mec.aghu.prescricaomedica.vo.JustificativaMedicamentoUsoGeralVO;
import br.gov.mec.aghu.prescricaomedica.vo.MpmJustificativaUsoMdtoVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.AGHUUtil;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class MpmJustificativaUsoMdtoRN extends BaseBusiness{

	private static final long serialVersionUID = 8128982628781155874L;
	
	private static final Log LOG = LogFactory.getLog(MpmJustificativaUsoMdtoRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@Inject
	private AfaGrupoUsoMedicamentoDAO afaGrupoUsoMedicamentoDAO;
	
	@Inject
	private MpmJustificativaUsoMdtoDAO mpmJustificativaUsoMdtoDAO;
	
	@Inject
	private MpmItemPrescricaoMdtoDAO mpmItemPrescricaoMdtoDAO;
	
	@Inject
	private MpmItemPrescParecerMdtoDAO mpmItemPrescParecerMdtoDAO;
	
	@Inject
	private MpmCidAtendimentoDAO mpmCidAtendimentoDAO;
	
	@Inject
	private MpmTrocaEsquemaTbDAO mpmTrocaEsquemaTbDAO;
	
	@Inject
	private MpmJustifTrocaEsquemaTbDAO mpmJustifTrocaEsquemaTbDAO;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private PrescricaoMedicaRN prescricaoMedicaRN;
	
	@EJB
	private MpmItemPrescricaoMdtoRN mpmItemPrescricaoMdtoRN;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;

	public enum MpmJustificativaUsoMdtoRNExceptionCode implements BusinessExceptionCode {
		AFA_00272, MPM_00845, MPM_00865, MPM_00862, RAP_00175,
		MPM_AVALIACAO_LOTE_ATU_SIT_CAND_APROV;
	}
	
	public MpmJustificativaUsoMdto persistirMpmJustificativaUsoMdto(MpmJustificativaUsoMdto justificativa,
			List<JustificativaMedicamentoUsoGeralVO> medicamentos) throws ApplicationBusinessException {
		//Na estoria #45674 nao foi especificado a migracao da trigger de update. A estória apenas faz insercao na respectiva tabela.
		if (justificativa.getSeq() == null && medicamentos != null && !medicamentos.isEmpty()) {
			preInserir(justificativa, medicamentos.get(0));
			mpmJustificativaUsoMdtoDAO.persistir(justificativa);
			
			this.mpmItemPrescricaoMdtoRN.atualizarMpmItemPrescricaoMdto(medicamentos, justificativa.getSeq());
		}
		
		return justificativa;
	}

	// @ORADB MPMT_JUM_BRI
	public void preInserir(MpmJustificativaUsoMdto justificativa, JustificativaMedicamentoUsoGeralVO medicamento) throws ApplicationBusinessException {
		justificativa.setCriadoEm(new Date());
		justificativa.setGupSeq(medicamento.getGupSeq());
		
		if (justificativa.getCandAprovLote() == null) {
			justificativa.setCandAprovLote(Boolean.FALSE);
		}
		if (justificativa.getSituacao() == null) {
			justificativa.setSituacao(DominioSituacaoSolicitacaoMedicamento.P);
		}
		
		if (justificativa.getOrientacaoAvaliador() == null) {
			justificativa.setOrientacaoAvaliador(Boolean.FALSE);
		}
		
		validaAtendimento(justificativa.getCriadoEm(), justificativa.getAtendimento().getSeq());
		verificaGrupoUsoMedicamento(justificativa.getGupSeq());
		
		if (justificativa.getGestante() != null) {
			verificaPacienteGestante(justificativa.getGestante(), justificativa.getAtendimento().getSeq());
		}	
			
		if (justificativa.getSituacao().equals(DominioSituacaoSolicitacaoMedicamento.A)) {
			verificaParecerItens(justificativa.getAtendimento().getSeq());
		}
		
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		justificativa.setServidor(servidorLogado);
		justificativa.setCspCnvCodigo(medicamento.getCspCnvCodigo());
		justificativa.setCspSeq(medicamento.getCspSeq());
	}
		
	//@ORADB rn_jump_ver_atend
	public void validaAtendimento(Date criadoEm, Integer atdSeq) throws ApplicationBusinessException {
		prescricaoMedicaRN.verificaAtendimento(criadoEm, null, atdSeq, null, null, null);
	}
		
	//@ORADB rn_jump_ver_grp_uso
	public void verificaGrupoUsoMedicamento(Short gupSeq) throws ApplicationBusinessException {		
		AfaGrupoUsoMedicamento grupoUso = afaGrupoUsoMedicamentoDAO.obterPorChavePrimaria(gupSeq.intValue());
		
		if (!grupoUso.getIndSituacao().equals(DominioSituacao.A)) {
			throw new ApplicationBusinessException(MpmJustificativaUsoMdtoRNExceptionCode.AFA_00272);
		}
	}
		
	//@ORADB rn_jump_ver_ind_gest
	public void verificaPacienteGestante(Boolean indGestante, Integer atdSeq) throws ApplicationBusinessException {
		AghAtendimentos atendimento = aghuFacade.obterAghAtendimentoPorChavePrimaria(atdSeq);
			
		if (indGestante && !atendimento.getPaciente().getSexo().equals(DominioSexo.F))  {
			throw new ApplicationBusinessException(MpmJustificativaUsoMdtoRNExceptionCode.MPM_00845);
		}
	}
		
	// @ORADB rn_jump_ver_avaliado
	public void verificaParecerItens(Integer atdSeq) throws ApplicationBusinessException {
		//TODO - verificar consulta da trigger BRI que está filtrando por jum_seq mas passa atdSeq
		List<MpmItemPrescricaoMdto> lista = mpmItemPrescricaoMdtoDAO.pesquisarItensMedicamentoPrescricaoPorJustificativa(atdSeq);
		
		for (MpmItemPrescricaoMdto item : lista) {
			MpmItemPrescParecerMdto parecer = mpmItemPrescParecerMdtoDAO.obterParecerPorMedicamento(item.getId().getPmdAtdSeq(),
					item.getId().getPmdSeq(), item.getId().getSeqp(), item.getId().getMedMatCodigo());
			
			if (parecer == null) {
				throw new ApplicationBusinessException(MpmJustificativaUsoMdtoRNExceptionCode.MPM_00845);
			}
		}
	}

	/**
	 * Obtém detalhamento da justificativa em formato de String.
	 * 
	 * @ORADB Mpmp_popula_sintaxe
	 * 
	 * @return Justificativa detalhada
	 * @throws ApplicationBusinessException 
	 */
	public String obterDetalhesJustificativaUsoMedicamento(Integer jumSeq) throws ApplicationBusinessException {

		MpmJustificativaUsoMdto justificativa = mpmJustificativaUsoMdtoDAO.obterJustificativaUsoMdtos(jumSeq);

		StringBuilder retorno = new StringBuilder(20);

		popularInformacoesMedicamento(retorno, jumSeq);

		popularInformacoesJustificativa(retorno, justificativa);

		if (justificativa.getObservacao() != null) {
			retorno.append("Observação :\n  ")
				.append(justificativa.getObservacao())
				.append('\n');
		}

		retorno.append("Diagnósticos : \n");

		popularInformacoesDiagnostico(retorno, justificativa.getAtendimento().getSeq());

		popularInformacoesTrocaEsquema(retorno, jumSeq);

		return retorno.toString();
	}

	/**
	 * Popula informações do medicamento na justificativa.
	 * 
	 * @ORADB MPMP_POPULA_MEDICAMENTO
	 * 
	 * @param retorno - Justificativa
	 * @param jumSeq - Código da Justificativa
	 */
	private void popularInformacoesMedicamento(StringBuilder retorno, Integer jumSeq) {

		List<MpmItemPrescricaoMdto> itens = mpmItemPrescricaoMdtoDAO.listarItensMedicamentoJustificativa(jumSeq);

		for (int i = 0; i < itens.size(); i++) {
			MpmItemPrescricaoMdto item = itens.get(i);

			if (i == 0) {
				retorno.append("Medicamentos do tipo ")
					.append(item.getMedicamento().getAfaTipoUsoMdtos().getGrupoUsoMedicamento().getDescricao())
					.append(":\n");
			}

			retorno.append("  ")
				.append(item.getMedicamento().getDescricao());

			if (item.getMedicamento().getConcentracao() != null) {
				retorno.append(StringUtils.SPACE)
					.append(item.getMedicamento().getConcentracao());
			}

			if (item.getMedicamento().getMpmUnidadeMedidaMedicas() != null) {
				retorno.append(StringUtils.SPACE)
					.append(item.getMedicamento().getMpmUnidadeMedidaMedicas().getDescricao());
			}

			if (item.getObservacao() != null) {
				retorno.append(" : ")
					.append(item.getObservacao())
					.append("; ");
			} else {
				retorno.append("; ");
			}

			retorno.append("Dose=")
				.append(item.getDoseFormatada())
				.append(StringUtils.SPACE);

			if (item.getFormaDosagem().getUnidadeMedidaMedicas() != null) {
				retorno.append(item.getFormaDosagem().getUnidadeMedidaMedicas().getDescricao());
			} else if (item.getMedicamento().getTprSigla() != null) {
				retorno.append(item.getMedicamento().getTprSigla());
			}

			retorno.append("; ");

			if (item.getPrescricaoMedicamento().getVadSigla() != null) {
				retorno.append(item.getPrescricaoMedicamento().getVadSigla());
			}

			retorno.append("; ");

			if (item.getPrescricaoMedicamento().getTipoFreqAprazamento().getSintaxe() != null) {
				retorno.append(item.getPrescricaoMedicamento().getTipoFreqAprazamento().getSintaxeFormatada(item.getPrescricaoMedicamento().getFrequencia()));
			} else {
				retorno.append(item.getPrescricaoMedicamento().getTipoFreqAprazamento().getDescricao());
			}

			retorno.append("; ");

			popularInformacoesMedicamentoContinuacao(retorno, item);
		}
	}

	/**
	 * Popula resto das informações do medicamento na justificativa.
	 * 
	 * @ORADB MPMP_POPULA_MEDICAMENTO
	 * 
	 * @param retorno - Justificativa
	 * @param item - Item da Prescrição
	 */
	private void popularInformacoesMedicamentoContinuacao(StringBuilder retorno, MpmItemPrescricaoMdto item) {

		if (item.getPrescricaoMedicamento().getDuracaoTratSolicitado() != null
				&& item.getPrescricaoMedicamento().getDuracaoTratSolicitado() > 0) {
			retorno.append("Duração solicitada: ")
				.append(item.getPrescricaoMedicamento().getDuracaoTratSolicitado());

			if (item.getPrescricaoMedicamento().getDuracaoTratSolicitado() == 1) {
				retorno.append(" dia;");
			} else {
				retorno.append(" dias;");
			}
		}

		if (item.getUsoMdtoAntimicrobia() != null) {
			retorno.append(StringUtils.SPACE)
				.append(item.getUsoMdtoAntimicrobia().obterDescricaoDetalhadaTipo())
				.append("; ");
		}

		retorno.append('\n');
	}

	/**
	 * Popula informações diversas na justificativa.
	 * 
	 * @ORADB MPMP_POPULA_JUSTIFICATIVA
	 * 
	 * @param retorno - Justificativa(String)
	 * @param justificativa - Justificativa(Objeto)
	 */
	private void popularInformacoesJustificativa(StringBuilder retorno, MpmJustificativaUsoMdto justificativa) throws ApplicationBusinessException {

		// 'P_GRPO_USO_MDTO_UR'
		Short usoRestrito = parametroFacade.buscarValorShort(AghuParametrosEnum.P_GRPO_USO_MDTO_UR);

		if (justificativa.getIndicacao() == null && justificativa.getInfeccaoTratar() == null && justificativa.getDiagnostico() == null) {
			retorno.append("Justificativa :\n  ")
				.append(justificativa.getJustificativa())
				.append('\n');

			if (justificativa.getNomeGerme() != null) {
				popularInformacoesAntimicrobiano(retorno, justificativa);
			}
		} else if (justificativa.getDiagnostico() != null) {
			popularInformacoesJustificativaQuimio(retorno, justificativa);
		} else if (usoRestrito.equals(justificativa.getGupSeq())) {
			if (justificativa.getIndicacao() != null) {
				popularInformacoesJustificativaUsoRestrito(retorno, justificativa);
			} else {
				popularInformacoesJustificativaUsoRestrAntimicrob(retorno, justificativa);
			}
		} else if (justificativa.getInfeccaoTratar() == null && justificativa.getIndicacao() != null) {
			popularInformacoesJustificativaNaoSelecionado(retorno, justificativa);
		} else if (justificativa.getInfeccaoTratar() != null && justificativa.getIndicacao() != null) {
			popularInformacoesJustificativaNaoSelecAntimicrob(retorno, justificativa);
		}
	}

	/**
	 * Popula informações de antimicrobiano na justificativa.
	 * 
	 * @ORADB MPMP_POPULA_ANTIMICROBIANO
	 * 
	 * @param retorno - Justificativa(String)
	 * @param justificativa - Justificativa(Objeto)
	 */
	private void popularInformacoesAntimicrobiano(StringBuilder retorno, MpmJustificativaUsoMdto justificativa) {

		retorno.append("    Gestante : ");
		if (justificativa.getGestante() != null) {
			retorno.append(DominioSimNao.getInstance(justificativa.getGestante()));
		}

		retorno.append("  Tipo infeccção : ");

		if (justificativa.getTipoInfeccao() != null) {
			retorno.append(justificativa.getTipoInfeccao().getDescricao());
		}

		retorno.append("\n    Antibiograma : ");

		if (justificativa.getSitAntibiograma() != null) {
			retorno.append(justificativa.getSitAntibiograma().getDescricao());
		}

		retorno.append("\n    Germe : ");

		if (justificativa.getNomeGerme() != null) {
			retorno.append(justificativa.getNomeGerme());
		}

		retorno.append('\n');

		if (justificativa.getSensibilidadeAntibiotico() != null) {
			retorno.append("    Sensb.:  ")
				.append(justificativa.getSensibilidadeAntibiotico())
				.append('\n');
		}

		if (justificativa.getFuncRenalComprometida() == null) {
			retorno.append("    Função renal comprometida :   ");
		} else {
			retorno.append("    Função renal comprometida :  ")
				.append(DominioSimNao.getInstance(justificativa.getFuncRenalComprometida()).getDescricao());
		}
		
		retorno.append('\n');

		if (justificativa.getPacImunodeprimido() == null) {
			retorno.append("    Paciente Imunodeprimido   :   ");
		} else {
			retorno.append("    Paciente Imunodeprimido   :  ")
			.append(DominioSimNao.getInstance(justificativa.getPacImunodeprimido()).getDescricao());
		}

		retorno.append('\n');

		if (justificativa.getOrientacaoAvaliador() == null) {
			retorno.append("    Orientação Comedi/CCIH    :   ");
		} else {
			retorno.append("    Orientação Comedi/CCIH    :  ")
			.append(DominioSimNao.getInstance(justificativa.getOrientacaoAvaliador()).getDescricao());
		}

		retorno.append('\n');
	}

	/**
	 * Popula informações de quimio na justificativa.
	 * 
	 * @ORADB MPMP_POPULA_JUST_QT
	 * 
	 * @param retorno - Justificativa(String)
	 * @param justificativa - Justificativa(Objeto)
	 */
	private void popularInformacoesJustificativaQuimio(StringBuilder retorno, MpmJustificativaUsoMdto justificativa) {

		retorno.append("Diagnóstico :\n  ")
			.append(justificativa.getDiagnostico())
			.append("\n'Justificativa para esquema atual :\n  ")
			.append(justificativa.getJustificativa())
			.append("\nECOG : ");

		if (justificativa.getEcog() != null) {
			retorno.append(justificativa.getEcog().getDescricao());
		}

		retorno.append("\nIntenção do tratamento : ");

		if (justificativa.getIntencaoTrat() != null) {
			retorno.append(justificativa.getIntencaoTrat().getDescricao());
		}

		retorno.append("\nLinha de tratamento : ");

		if (justificativa.getLinhaTrat() != null) {
			retorno.append(justificativa.getLinhaTrat().getDescricao());
		}

		retorno.append("\nTratamentos Anteriores :\n  Cirurgia : ");

		if (justificativa.getTratAntCirurgia() != null) {
			retorno.append(justificativa.getTratAntCirurgia());
		}

		retorno.append("\n  Radioterapia : ");

		if (justificativa.getTratAntRadio() != null) {
			retorno.append(justificativa.getTratAntRadio());
		}
		
		retorno.append("\n  Quimioterapia : ");
		
		if (justificativa.getTratAntQuimio() != null) {
			retorno.append(justificativa.getTratAntQuimio());
		}

		retorno.append("\n    Mês/Ano do Último ciclo : ");

		if (justificativa.getMesAnoUltCiclo() != null) {
			retorno.append(DateUtil.dataToString(justificativa.getMesAnoUltCiclo(), DateConstants.DATE_PATTERN_MM_YYYY));
		}

		popularInformacoesJustificativaQuimioContinuacao(retorno, justificativa);
	}

	/**
	 * Popula restante das informações de quimio na justificativa.
	 * 
	 * @ORADB MPMP_POPULA_JUST_QT
	 * 
	 * @param retorno - Justificativa(String)
	 * @param justificativa - Justificativa(Objeto)
	 */
	private void popularInformacoesJustificativaQuimioContinuacao(StringBuilder retorno, MpmJustificativaUsoMdto justificativa) {

		retorno.append("\n  Hormonioterapia : ");

		if (justificativa.getTratAntHormonio() != null) {
			retorno.append(justificativa.getTratAntHormonio());
		}

		retorno.append("\n  Outros : ");

		if (justificativa.getTratAntOutros() != null) {
			retorno.append(justificativa.getTratAntOutros());
		}

		retorno.append("\nReferências bibliográficas :\n  ");

		if (justificativa.getRefBibliograficas() != null) {
			retorno.append(justificativa.getRefBibliograficas());
		}

		retorno.append('\n');
	}

	/**
	 * Popula informações de uso restrito na justificativa.
	 * 
	 * @ORADB MPMP_POPULA_JUST_UR
	 * 
	 * @param retorno - Justificativa(String)
	 * @param justificativa - Justificativa(Objeto)
	 */
	private void popularInformacoesJustificativaUsoRestrito(StringBuilder retorno, MpmJustificativaUsoMdto justificativa) {

		retorno.append("Indicação :\n  ");

		if (justificativa.getIndicacao() != null) {
			retorno.append(justificativa.getIndicacao());
		}

		retorno.append("\nOrientação Comedi/CCIH : ");

		if (justificativa.getOrientacaoAvaliador() != null) {
			retorno.append(DominioSimNao.getInstance(justificativa.getOrientacaoAvaliador()).getDescricao());
		}

		retorno.append("\nJustificativa para escolha atual:\n  ")
			.append(justificativa.getJustificativa())
			.append("\nFunção renal comprometida : ");

		if (justificativa.getFuncRenalComprometida() != null) {
			retorno.append(DominioSimNao.getInstance(justificativa.getFuncRenalComprometida()).getDescricao());
		}

		retorno.append("\nGestação : ");

		if (justificativa.getGestante() != null) {
			retorno.append(DominioSimNao.getInstance(justificativa.getGestante()).getDescricao());
		}

		retorno.append("\nInsuficiência Hepática : ");
		
		if (justificativa.getInsufHepatica() != null) {
			retorno.append(DominioSimNao.getInstance(justificativa.getInsufHepatica()).getDescricao());
		}
		
		retorno.append("\nConduta baseada em protocolo assistencial HCPA : ");

		if (justificativa.getCondutaBaseProtAssist() != null) {
			retorno.append(DominioSimNao.getInstance(justificativa.getCondutaBaseProtAssist()).getDescricao());
		}

		retorno.append('\n');
	}

	/**
	 * Popula informações de uso restrito antimicrobiano na justificativa.
	 * 
	 * @ORADB MPMP_POPULA_JUST_UR_MICROB
	 * 
	 * @param retorno - Justificativa(String)
	 * @param justificativa - Justificativa(Objeto)
	 */
	private void popularInformacoesJustificativaUsoRestrAntimicrob(StringBuilder retorno, MpmJustificativaUsoMdto justificativa) {

		populaInfeccaoTratarAntimicrob(retorno, justificativa);

		retorno.append("\nOrientação Comedi/CCIH : ");

		if (justificativa.getOrientacaoAvaliador() != null) {
			retorno.append(DominioSimNao.getInstance(justificativa.getOrientacaoAvaliador()).getDescricao());
		}

		populaTratamentosAntimicrobAnteriores(retorno, justificativa);

		retorno.append("\nJustificativa da escolha do esquema atual :\n  ")
			.append(justificativa.getJustificativa())
			.append("\nInternação Prévia : ");

		if (justificativa.getInternacaoPrevia() != null) {
			retorno.append(justificativa.getInternacaoPrevia().getDescricao());
		}

		populaInicioInfeccaoAntimicrob(retorno, justificativa);

		populaInfeccaoProcedimentoInvasivo(retorno, justificativa);

		retorno.append("\nSonda vesical de demora : ");

		if (justificativa.getSondaVesicalDemora() != null) {
			retorno.append(DominioSimNao.getInstance(justificativa.getSondaVesicalDemora()).getDescricao());
		}

		retorno.append("\nPeso estimado : ");
		
		if (justificativa.getPesoEstimado() != null) {
			retorno.append(justificativa.getPesoEstimado());
		}

		retorno.append(" kg\nImunossupressão : ");

		if (justificativa.getPacImunodeprimido() != null) {
			retorno.append(DominioSimNao.getInstance(justificativa.getPacImunodeprimido()).getDescricao());
		}
		
		retorno.append("\nInsuficiência Hepática : ");
		
		if (justificativa.getInsufHepatica() != null) {
			retorno.append(DominioSimNao.getInstance(justificativa.getInsufHepatica()).getDescricao());
		}
		
		retorno.append("\nFunção renal comprometida : ");
		
		if (justificativa.getFuncRenalComprometida() != null) {
			retorno.append(DominioSimNao.getInstance(justificativa.getFuncRenalComprometida()).getDescricao());
		}

		retorno.append("\nGestação : ");

		if (justificativa.getGestante() != null) {
			retorno.append(DominioSimNao.getInstance(justificativa.getGestante()).getDescricao());
		}

		retorno.append("\nConduta baseada em protocolo assistencial HCPA : ");
		
		if (justificativa.getCondutaBaseProtAssist() != null) {
			retorno.append(DominioSimNao.getInstance(justificativa.getCondutaBaseProtAssist()).getDescricao());
		}

		retorno.append('\n');
	}

	/**
	 * Popula informações da justificativa não selecionada.
	 * 
	 * @ORADB MPMP_POPULA_JUST_NS
	 * 
	 * @param retorno - Justificativa(String)
	 * @param justificativa - Justificativa(Objeto)
	 */
	private void popularInformacoesJustificativaNaoSelecionado(StringBuilder retorno, MpmJustificativaUsoMdto justificativa) {

		retorno.append("Indicação :\n  ");

		if (justificativa.getIndicacao() != null) {
			retorno.append(justificativa.getIndicacao());
		}

		retorno.append("\nJustificativa para escolha de agente não selecionado :\n  ")
			.append(justificativa.getJustificativa())
			.append("\nVantagem sobre alternativa disponível na seleção de medicamentos :\n  ");

		if (justificativa.getVantagemNsPadronizacao() != null) {
			retorno.append(justificativa.getVantagemNsPadronizacao());
		}

		retorno.append("\nUso crônico prévio à internação : ");

		if (justificativa.getUsoCronicoPrevInt() != null) {
			retorno.append(DominioSimNao.getInstance(justificativa.getUsoCronicoPrevInt()).getDescricao());
		}

		retorno.append("\nCusto diário estimado em R$ : ");

		if (justificativa.getCustoDiarioEstReal() != null) {
			retorno.append(justificativa.getCustoDiarioEstReal());
		}

		retorno.append("\nReferências bibliográficas :\n  ");

		if (justificativa.getRefBibliograficas() != null) {
			retorno.append(justificativa.getRefBibliograficas());
		}

		retorno.append('\n');
	}

	/**
	 * Popula informações da justificativa não selecionada antimicrobiana.
	 * 
	 * @ORADB MPMP_POPULA_JUST_NS_MICROB
	 * 
	 * @param retorno - Justificativa(String)
	 * @param justificativa - Justificativa(Objeto)
	 */
	private void popularInformacoesJustificativaNaoSelecAntimicrob(StringBuilder retorno, MpmJustificativaUsoMdto justificativa) {

		populaInfeccaoTratarAntimicrob(retorno, justificativa);

		populaTratamentosAntimicrobAnteriores(retorno, justificativa);

		retorno.append("\nInternação Prévia : ");

		if (justificativa.getInternacaoPrevia() != null) {
			retorno.append(justificativa.getInternacaoPrevia().getDescricao());
		}

		populaInicioInfeccaoAntimicrob(retorno, justificativa);

		populaInfeccaoProcedimentoInvasivo(retorno, justificativa);

		retorno.append("\nSonda vesical de demora : ");

		if (justificativa.getSondaVesicalDemora() != null) {
			retorno.append(DominioSimNao.getInstance(justificativa.getSondaVesicalDemora()).getDescricao());
		}

		retorno.append("\nPeso estimado : ");
		
		if (justificativa.getPesoEstimado() != null) {
			retorno.append(justificativa.getPesoEstimado());
		}

		retorno.append(" kg\nImunossupressão : ");

		if (justificativa.getPacImunodeprimido() != null) {
			retorno.append(DominioSimNao.getInstance(justificativa.getPacImunodeprimido()).getDescricao());
		}

		retorno.append("\nGestação : ");

		if (justificativa.getGestante() != null) {
			retorno.append(DominioSimNao.getInstance(justificativa.getGestante()).getDescricao());
		}

		retorno.append("\nFunção renal comprometida : ");
		
		if (justificativa.getFuncRenalComprometida() != null) {
			retorno.append(DominioSimNao.getInstance(justificativa.getFuncRenalComprometida()).getDescricao());
		}

		retorno.append("\nInsuficiência Hepática : ");
		
		if (justificativa.getInsufHepatica() != null) {
			retorno.append(DominioSimNao.getInstance(justificativa.getInsufHepatica()).getDescricao());
		}

		retorno.append('\n');

		popularInformacoesJustificativaNaoSelecionado(retorno, justificativa);
	}

	/**
	 * Popula informações da infecção a tratar.
	 * 
	 * @param retorno - Justificativa(String)
	 * @param justificativa - Justificativa(Objeto)
	 */
	public void populaInfeccaoTratarAntimicrob(StringBuilder retorno, MpmJustificativaUsoMdto justificativa) {

		retorno.append("Infecção a tratar :\n  ");

		if (justificativa.getInfeccaoTratar() != null) {
			retorno.append(justificativa.getInfeccaoTratar());
		}
	}
	
	/**
	 * Popula informações de tratamentos antimicrobianos anteriores.
	 * 
	 * @param retorno - Justificativa(String)
	 * @param justificativa - Justificativa(Objeto)
	 */
	public void populaTratamentosAntimicrobAnteriores(StringBuilder retorno, MpmJustificativaUsoMdto justificativa) {

		retorno.append("\nTratamentos antimicrobianos anteriores :\n  ");

		if (justificativa.getTratAntimicrobAnt() != null) {
			retorno.append(justificativa.getTratAntimicrobAnt());
		}
	}
	
	/**
	 * Popula informações do Início da infecção.
	 * 
	 * @param retorno - Justificativa(String)
	 * @param justificativa - Justificativa(Objeto)
	 */
	public void populaInicioInfeccaoAntimicrob(StringBuilder retorno, MpmJustificativaUsoMdto justificativa) {

		retorno.append("\nInício da Infecção : ");
		
		if (justificativa.getInicioInfeccao() != null) {
			retorno.append(justificativa.getInicioInfeccao().getDescricao());
		}
	}
	
	/**
	 * Popula informações de infecção relacionada a procedimentos invasivos.
	 * 
	 * @param retorno - Justificativa(String)
	 * @param justificativa - Justificativa(Objeto)
	 */
	public void populaInfeccaoProcedimentoInvasivo(StringBuilder retorno, MpmJustificativaUsoMdto justificativa) {

		retorno.append("\nInfecção relacionada a procedimento invasivo : ");
		
		if (justificativa.getInfecRelProcedInvasivo() != null) {
			retorno.append(justificativa.getInfecRelProcedInvasivo().getDescricao());
		}
	}

	/**
	 * Popula informações do Diagnóstico na justificativa.
	 * 
	 * @ORADB MPMP_POPULA_DIAGNOSTICO
	 * 
	 * @param retorno - Justificativa
	 * @param atdSeq - Código do Atendimento
	 */
	private void popularInformacoesDiagnostico(StringBuilder retorno, Integer atdSeq) {

		List<MpmCidAtendimento> cidAtds = mpmCidAtendimentoDAO.listarCidAtendimentosValidosPorAtdSeq(atdSeq);

		for (MpmCidAtendimento cidAtd : cidAtds) {
			if (cidAtd.getCid().getCid() != null) {
				retorno.append("        ")
					.append(cidAtd.getCid().getCid().getDescricao())
					.append('\n');
			}

			retorno.append("  ")
				.append(cidAtd.getCid().getCodigo())
				.append(StringUtils.SPACE)
				.append(cidAtd.getCid().getDescricao())
				.append(" ; ");

			if (cidAtd.getComplemento() != null) {
				retorno.append(cidAtd.getComplemento());
			}

			retorno.append('\n');
		}
	}
	
	/**
	 * Popula informações da Troca de esquema na Justificativa.
	 * 
	 * @ORADB MPMP_POPULA_TROCA_ESQUEMA
	 * 
	 * @param retorno - Justificativa
	 * @param jumSeq - Código da Justificativa
	 */
	private void popularInformacoesTrocaEsquema(StringBuilder retorno, Integer jumSeq) {

		MpmTrocaEsquemaTb trocaEsquema = mpmTrocaEsquemaTbDAO.obterPorChavePrimaria(jumSeq);

		if (trocaEsquema != null) {
			retorno.append("Troca de Esquema :\n  ")
				.append(trocaEsquema.getMpmTipoEsquemaTbs().getDescricao());

			if (trocaEsquema.getDescricao() != null) {
				retorno.append(" - ")
					.append(trocaEsquema.getDescricao());
			}

			retorno.append('\n');

			List<MpmJustifTrocaEsquemaTb> justifTrocaEsquemas = mpmJustifTrocaEsquemaTbDAO
					.listarJustificativasTrocaEsquemaPorCodigoJustificativaUso(jumSeq);

			for (int i = 0; i < justifTrocaEsquemas.size(); i++) {
				MpmJustifTrocaEsquemaTb justifTrocaEsquema = justifTrocaEsquemas.get(i);

				if (i == 0) {
					retorno.append("Justificativa :\n");
				}

				retorno.append("  ")
					.append(justifTrocaEsquema.getTipoJustifTrocaEsqTb().getDescricao());

				if (justifTrocaEsquema.getDescricao() != null) {
					retorno.append(" - ")
						.append(justifTrocaEsquema.getDescricao());
				}

				retorno.append('\n');
			}
		}
	}

	public void atualizarJustificativaUsoMdto(MpmJustificativaUsoMdto jum, DominioSituacaoSolicitacaoMedicamento situacao) throws ApplicationBusinessException {
		this.preAtualizar(jum, situacao);
		this.mpmJustificativaUsoMdtoDAO.merge(jum);
		this.mpmJustificativaUsoMdtoDAO.flush();
	}
	
	/**
	 * Trigger de Before Update MPMT_JUM_BRU de MPM_JUSTIFICATIVA_USO_MDTOS
	 * 
	 * @ORADB MPMT_JUM_BRU
	 */
	private void preAtualizar(MpmJustificativaUsoMdto jum, DominioSituacaoSolicitacaoMedicamento situacao) throws ApplicationBusinessException {
		
		MpmJustificativaUsoMdtoVO jumOld = this.mpmJustificativaUsoMdtoDAO.obterJustificativaUsoMdtoVO(jum.getSeq());
		
		if (situacao != null) {
			jum.setSituacao(situacao);
		}
		
		// Verifica se ind_situacao foi trocada para 'A' e inicializa o ind_cand_aprov_lote
		this.atualizaCandAprovLote(jum, jumOld);
		
		// Verifica se a situacao foi alterada para então permitir alteração ou não 
		// Verifica se IND_SITUACAO antiga era P ou IND_SITUACAO nova é T, e se IND_CAND_APROV_LOTE foi alterada
		if (!AGHUUtil.modificados(jum.getSituacao(), jumOld.getSituacao())
				&& !((jumOld.getSituacao().equals(DominioSituacaoSolicitacaoMedicamento.P) || 
						 jum.getSituacao().equals(DominioSituacaoSolicitacaoMedicamento.T)) && 
						 AGHUUtil.modificados(jum.getCandAprovLote(), jumOld.getCandAprovLote()))) {
			this.verificaSituacaoPendente(jum.getSituacao());
		}
		
		// Verifica vigencia atendimento
		this.validaAtendimento(jum.getCriadoEm(), jum.getAtendimento().getSeq());
		
		// Verifica se o grupo de uso do medicamento está ativo 
		if (AGHUUtil.modificados(jum.getGupSeq(), jumOld.getGupSeq())) {
			this.verificaGrupoUsoMedicamento(jum.getGupSeq());
		}
		
		// Verifica se indicador de gestante está compativel com o sexo do paciente 
		if (jum.getGestante() != null && AGHUUtil.modificados(jum.getGestante(), jumOld.getGestante())) {
			this.verificaPacienteGestante(jum.getGestante(), jum.getAtendimento().getSeq());
		}
		
		// Verifica se ind_situacao 'A' e se todos os itens tem parecer informado
		if (jum.getSituacao().equals(DominioSituacaoSolicitacaoMedicamento.A)) {
			this.verificaParecerItens(jum.getSeq());
		}
		
		// Verifica se ind_situacao foi trocada de 'P' para 'T', atualizando o servidor e a data do conhecimento
		this.atualizaServidorConhecimento(jum, jumOld);
		
		// Dados do servidor que digita - Usuário logado ao sistema 
		jum.setServidor(this.servidorLogadoFacade.obterServidorLogado());
		this.validaServidor(jum.getServidor());
		
		// Verifica se ind_situacao = 'A' e se ind_cand_aprov_lote foi alterado para 'S'
		if (jum.getSituacao().equals(DominioSituacaoSolicitacaoMedicamento.A) && jum.getCandAprovLote()) {
			throw new ApplicationBusinessException(MpmJustificativaUsoMdtoRNExceptionCode.MPM_AVALIACAO_LOTE_ATU_SIT_CAND_APROV);
		}
	}
	
	/**
	 * Responsável por impedir atualização em mpm_justificativa_uso_mdtos caso ind_situacao seja diferente de ‘P’
	 * 
	 * @ORADB MPMK_JUM_RN.RN_JUMP_VER_ALTERA
	 */
	public void verificaSituacaoPendente(DominioSituacaoSolicitacaoMedicamento situacao) throws ApplicationBusinessException {
		if (!situacao.equals(DominioSituacaoSolicitacaoMedicamento.P)) {
			throw new ApplicationBusinessException(MpmJustificativaUsoMdtoRNExceptionCode.MPM_00862);
		}
	}
	
	/**
	 * Responsável por atribuir valor false para ind_cand_aprov_lote caso ind_situacao igual à A
	 * 
	 * @ORADB MPMK_JUM_RN.RN_JUMP_ATU_AVALIADO
	 */
	public void atualizaCandAprovLote(MpmJustificativaUsoMdto jum, MpmJustificativaUsoMdtoVO jumOld) {
		if (AGHUUtil.modificados(jum.getSituacao(), jumOld.getSituacao()) && 
				jum.getSituacao().equals(DominioSituacaoSolicitacaoMedicamento.A)) {
			jum.setCandAprovLote(Boolean.FALSE);
		}
	}
	
	/**
	 * Responsável por atribuir valores para alterado_em, ser_matricula_conhecimento e ser_vin_codigo_conhecimento
	 * 
	 * @ORADB MPMK_JUM_RN.RN_JUMP_ATU_VISTA
	 */
	public void atualizaServidorConhecimento(MpmJustificativaUsoMdto jum, MpmJustificativaUsoMdtoVO jumOld) throws ApplicationBusinessException {
		if (AGHUUtil.modificados(jum.getSituacao(), jumOld.getSituacao()) && 
				jum.getSituacao().equals(DominioSituacaoSolicitacaoMedicamento.T) &&
				jumOld.getSituacao().equals(DominioSituacaoSolicitacaoMedicamento.P)) {
			jum.setAlteradoEm(new Date());
			jum.setServidorConhecimento(this.servidorLogadoFacade.obterServidorLogado());
			this.validaServidor(jum.getServidorConhecimento());
		}
	}
	
	public void validaServidor(RapServidores servidor) throws ApplicationBusinessException {
		if (servidor == null || servidor.getId() == null || servidor.getId().getMatricula() == null) {
			throw new ApplicationBusinessException(MpmJustificativaUsoMdtoRNExceptionCode.RAP_00175);
		}
	}
}
