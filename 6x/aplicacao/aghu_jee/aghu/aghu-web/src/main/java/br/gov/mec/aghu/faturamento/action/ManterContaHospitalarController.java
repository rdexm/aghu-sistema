package br.gov.mec.aghu.faturamento.action;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatContasInternacao;
import br.gov.mec.aghu.model.FatMotivoSaidaPaciente;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.FatSituacaoSaidaPaciente;
import br.gov.mec.aghu.model.VFatAssociacaoProcedimento;
import br.gov.mec.aghu.model.VFatContaHospitalarPac;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterContaHospitalarController extends ActionController {

	private static final String BLOCOCIRURGICO_REGISTRO_CIRURGIA_REALIZADA = "blococirurgico-registroCirurgiaRealizada";

	/**
	 * 
	 */
	private static final long serialVersionUID = -8365123667182271294L;

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	private Boolean fromEncerramentoPreviaConta;

	@EJB
	private IAghuFacade aghuFacade;

	private FatContasHospitalares contaHospitalar;

	private FatContasHospitalares contaHospitalarClone;

	private VFatContaHospitalarPac contaHospitalarView;

	private VFatAssociacaoProcedimento procedimentoHospitalarSolicitado;

	private VFatAssociacaoProcedimento procedimentoHospitalarRealizado;

	private FatMotivoSaidaPaciente motivoSaidaPaciente;

	private FatSituacaoSaidaPaciente situacaoSaidaPaciente;

	private AghEspecialidades especialidade;

	private DominioSimNao indInfeccao;

	private Integer seq;
	
	private String voltarParaTela;

	private final String pageConsultarInconsistencias = "consultarFatLogErrorList";
	private final String pageEncerramentoPreviaConta = "faturamento-encerramentoPreviaConta";
	private final String pageCid = "lancarItensContaHospitalarList";
	private final String pageEspelho = "consultarEspelhoAIHList";
	private final String pageManterContaHospitalarList = "manterContaHospitalarList";
	private final String pageManterContaHospitalar= "faturamento-manterContaHospitalar";
	private final String pageDesdobramentoContaHospitalar = "faturamento-desdobramentoContaHospitalar";
	
	public enum ManterContaHospitalarControllerExceptionCode implements BusinessExceptionCode {
		PESO_NASCER_DEVE_SER_MAIOR_ZERO, IDADE_GESTACIONAL_DEVE_SER_MAIOR_ZERO, NASCIDO_VIVO_DEVE_SER_MAIOR_ZERO, 
		NASCIDO_MORTO_DEVE_SER_MAIOR_ZERO, SAIDA_TRANSFERENCIA_DEVE_SER_MAIOR_ZERO, SAIDA_ALTA_DEVE_SER_MAIOR_ZERO, 
		SAIDA_OBITO_DEVE_SER_MAIOR_ZERO,ERRO_GENERICO_CONTA_HOSPITALAR, CONTA_HOSPITALAR_PRONTUARIO_CODIGO_OBRIGATORIO, 
		CONTA_HOSPITALAR_ALTERADA_SUCESSO, SITUACAO_ALTA_PACIENTE_INVALIDA, CONTA_NAO_SELECIONADA;
	}

	@PostConstruct
	public void init() {
		begin(conversation);
	}
	
	public void inicio() {
	 

		try {
			
			if (fromEncerramentoPreviaConta == null) {
				fromEncerramentoPreviaConta = Boolean.FALSE;
			}

			if (seq != null) {
				contaHospitalarView = faturamentoFacade.obterContaHospitalarPaciente(seq);
				contaHospitalar = faturamentoFacade
						.obterContaHospitalar(
								seq,
								FatContasHospitalares.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO,
								FatContasHospitalares.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO_REALIZADO,
								FatContasHospitalares.Fields.SITUACAO_SAIDA_PACIENTE,
								FatContasHospitalares.Fields.ESPECIALIDADE,
								FatContasHospitalares.Fields.MOTIVO_SAIDA_PACIENTE);

				if (contaHospitalar.getIndInfeccao() != null) {
					indInfeccao = DominioSimNao.getInstance(contaHospitalar.getIndInfeccao());
				}

				// Clonando a conta hospitalar.
				contaHospitalarClone = faturamentoFacade.clonarContaHospitalar(contaHospitalar);

				if (contaHospitalar != null) {
					if (contaHospitalar.getProcedimentoHospitalarInterno() != null) {
						try {
							final List<VFatAssociacaoProcedimento> procedimento = faturamentoFacade
							.listarAssociacaoProcedimentoSUSSolicitado(contaHospitalar.getProcedimentoHospitalarInterno().getSeq());
							if (!procedimento.isEmpty()) {
								procedimentoHospitalarSolicitado = procedimento.get(0);
							}
						} catch (final ApplicationBusinessException e) {
							procedimentoHospitalarSolicitado = new VFatAssociacaoProcedimento();
						}
					}

					if (contaHospitalar.getProcedimentoHospitalarInternoRealizado() != null) {
						try {
							final List<VFatAssociacaoProcedimento> procedimento = faturamentoFacade.listarAssociacaoProcedimentoSUSRealizado(
									contaHospitalar.getProcedimentoHospitalarInternoRealizado().getSeq(), contaHospitalar.getSeq());
							if (!procedimento.isEmpty()) {
								procedimentoHospitalarRealizado = procedimento.get(0);
							}
						} catch (final ApplicationBusinessException e) {
							procedimentoHospitalarRealizado = null;
						}
					}
					else {
						procedimentoHospitalarRealizado = null;
					}
					
					motivoSaidaPaciente = null;
					situacaoSaidaPaciente = contaHospitalar.getSituacaoSaidaPaciente();
					if (situacaoSaidaPaciente != null) {
						motivoSaidaPaciente = faturamentoFacade.obterMotivoSaidaPacientePorChavePrimaria(situacaoSaidaPaciente.getId().getMspSeq());
					}

					if (contaHospitalar.getEspecialidade() != null) {
						especialidade = contaHospitalar.getEspecialidade();
					}
				}
			}
		} catch (final Exception e) {
			apresentarMsgNegocio(Severity.INFO,
					ManterContaHospitalarControllerExceptionCode.ERRO_GENERICO_CONTA_HOSPITALAR.toString());
		}
	
	}

	public String cid() {
		return pageCid;
	}

	public List<VFatAssociacaoProcedimento> listarProcedimentosSUSRealizados(final String strPesquisa) {
		try {
			if (contaHospitalar != null) {
				return this.returnSGWithCount(faturamentoFacade.listarAssociacaoProcedimentoSUSRealizado(strPesquisa, null, contaHospitalar.getSeq()),listarProcedimentosSUSRealizadosCount(strPesquisa));
			} else {
				return new ArrayList<VFatAssociacaoProcedimento>();
			}
		} catch (final BaseException e) {
			return new ArrayList<VFatAssociacaoProcedimento>();
		}
	}

	/**
	 * Apenas redireciona para a tela de Pesquisar Inconsistências de Espelho
	 * 
	 * @return
	 */
	public String consultarInconsistencias() {
		if (contaHospitalarView.getContaHospitalar().getSeq() == null) {
			apresentarExcecaoNegocio(new ApplicationBusinessException(ManterContaHospitalarControllerExceptionCode.CONTA_NAO_SELECIONADA));
			return null;
		} else {
			return pageConsultarInconsistencias;
		}
	}

	public String espelho() {
		if (contaHospitalarView.getContaHospitalar() == null || contaHospitalarView.getContaHospitalar().getSeq() == null) {
			apresentarExcecaoNegocio(new ApplicationBusinessException(ManterContaHospitalarControllerExceptionCode.CONTA_NAO_SELECIONADA));
			return null;
		} else {
			return pageEspelho;
		}
	}
	public String previaContaHospitalar() {
		return encerramentoPreviaContaHospitalar(true);
	}
	
	private String encerramentoPreviaContaHospitalar(final boolean isPrevia) {
		try {
			if (contaHospitalarView == null || contaHospitalarView.getContaHospitalar() == null || contaHospitalarView.getContaHospitalar().getSeq() == null) {
				apresentarExcecaoNegocio(new ApplicationBusinessException(ManterContaHospitalarControllerExceptionCode.CONTA_NAO_SELECIONADA));
				return null;
			} else {
				
				String nomeMicrocomputador = null;
				try {
					nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
				} catch (UnknownHostException e) {
					apresentarMsgNegocio(Severity.ERROR, e.getMessage());
				}
				
				Boolean retorno = this.faturamentoFacade.rnCthcAtuGeraEsp(contaHospitalarView.getContaHospitalar().getSeq(), isPrevia, nomeMicrocomputador, new Date(), true);
				
				if (Boolean.TRUE.equals(retorno)) {
					apresentarMsgNegocio(Severity.INFO, isPrevia ? "PREVIA_CONCLUIDA" : "ENCERRAMENTO_CONTAS_HOSPITALARES_SUCESSO");
					
					final Long countEspelhosAih = this.faturamentoFacade.listarFatEspelhoAihCount(contaHospitalarView.getContaHospitalar().getSeq());
					
					if (countEspelhosAih != null && countEspelhosAih > 0) {
						final Long countErros = this.faturamentoFacade.pesquisarFatLogErrorCount(contaHospitalarView.getContaHospitalar().getSeq(), null, null, null, null, null, DominioModuloCompetencia.INT);
						
						if (countErros != null && countErros > 0) {
							return pageConsultarInconsistencias;
						}
					}
				} else {
					apresentarMsgNegocio(Severity.INFO, isPrevia ? "CONTA_NAO_PODE_SER_ENCERRADA_PREVIA_NAO_CONCLUIDA" : "ENCERRAMENTO_CONTAS_HOSPITALARES_ERRO_SIMPLE");
					return pageConsultarInconsistencias;
				}
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return null;
	}	
	
	public Long listarProcedimentosSUSRealizadosCount(final String strPesquisa) {
		try {
			if (contaHospitalar != null) {
				return faturamentoFacade.listarAssociacaoProcedimentoSUSRealizadoCount(strPesquisa, null, contaHospitalar.getSeq());
			} else {
				return 0L;
			}
		} catch (final BaseException e) {
			return 0L;
		}
	}

	public String desdobramento() {
		return pageDesdobramentoContaHospitalar;
	}
	
	public void limparCamposRelacionados() {
		this.situacaoSaidaPaciente = null;
	}

	public List<FatMotivoSaidaPaciente> listarMotivoSaidaPaciente(final String strPesquisa) {
		return this.returnSGWithCount(this.faturamentoFacade.listarMotivoSaidaPaciente(strPesquisa),listarMotivoSaidaPacienteCount(strPesquisa));
	}

	public Long listarMotivoSaidaPacienteCount(final String strPesquisa) {
		return this.faturamentoFacade.listarMotivoSaidaPacienteCount(strPesquisa);
	}

	public List<FatSituacaoSaidaPaciente> listarSituacaoSaidaPaciente(final String strPesquisa) {
		return this.returnSGWithCount(this.faturamentoFacade.listarSituacaoSaidaPaciente(strPesquisa, motivoSaidaPaciente.getSeq()),listarSituacaoSaidaPacienteCount(strPesquisa));
	}

	public Long listarSituacaoSaidaPacienteCount(final String strPesquisa) {
		return this.faturamentoFacade.listarSituacaoSaidaPacienteCount(strPesquisa, motivoSaidaPaciente.getSeq());
	}

	public List<AghEspecialidades> listarEspecialidades(final String strPesquisa) {
		return this.returnSGWithCount(this.aghuFacade.pesquisarPorNomeSiglaInternaUnidade((String) strPesquisa),listarEspecialidadesCount(strPesquisa));
	}

	public Long listarEspecialidadesCount(final String strPesquisa) {
		return this.aghuFacade.pesquisarPorNomeSiglaInternaUnidadeCount((String) strPesquisa);
	}

	public String buscarLeito() {
		String leito = null;
		if (contaHospitalarView != null) {
			if (contaHospitalarView.getContaHospitalar() != null) {
				if (!contaHospitalarView.getContaHospitalar().getContasInternacao().isEmpty()) {
					final FatContasInternacao contaInternacao = (FatContasInternacao) contaHospitalarView.getContaHospitalar().getContasInternacao()
					.toArray()[0];
					if (contaInternacao.getInternacao() != null) {
						leito = (contaInternacao.getInternacao().getLeito() != null) ? contaInternacao.getInternacao().getLeito().getLeitoID() : null;
					}
				}
			}
		}
		return leito;
	}

	public String buscarCodigoDescricaoEspecialidade() {
		String especialidade = null;
		if (contaHospitalarView != null) {
			if (contaHospitalarView.getContaHospitalar() != null) {
				if (contaHospitalarView.getContaHospitalar().getEspecialidade() != null) {
					especialidade = contaHospitalarView.getContaHospitalar().getEspecialidade().getSeq().toString() + " "
					+ contaHospitalarView.getContaHospitalar().getEspecialidade().getNomeEspecialidade();
				}
			}
		}
		return especialidade;
	}

	@SuppressWarnings("PMD.NPathComplexity")
	public void validarCampos() throws ApplicationBusinessException {
		if (contaHospitalar != null && contaHospitalar.getRnNascidoVivo() != null) {
			if (contaHospitalar.getRnNascidoVivo().intValue() <= 0) {
				throw new ApplicationBusinessException(ManterContaHospitalarControllerExceptionCode.NASCIDO_VIVO_DEVE_SER_MAIOR_ZERO);
			}
		}

		if (contaHospitalar != null && contaHospitalar.getRnNascidoMorto() != null) {
			// Como solicitado pela Analista Marina Delazzeri: #53266
			// Removendo validação, e permitindo gravar rn_nascido_morto com valor zero
			if (contaHospitalar.getRnNascidoMorto().intValue() < 0) {
				throw new ApplicationBusinessException(ManterContaHospitalarControllerExceptionCode.NASCIDO_MORTO_DEVE_SER_MAIOR_ZERO);
			}
		}

		if (contaHospitalar != null && contaHospitalar.getRnSaidaAlta() != null) {
			if (contaHospitalar.getRnSaidaAlta().intValue() <= 0) {
				throw new ApplicationBusinessException(ManterContaHospitalarControllerExceptionCode.SAIDA_ALTA_DEVE_SER_MAIOR_ZERO);
			}
		}

		if (contaHospitalar != null && contaHospitalar.getRnSaidaObito() != null) {
			if (contaHospitalar.getRnSaidaObito().intValue() <= 0) {
				throw new ApplicationBusinessException(ManterContaHospitalarControllerExceptionCode.SAIDA_OBITO_DEVE_SER_MAIOR_ZERO);
			}
		}

		if (contaHospitalar != null && contaHospitalar.getRnSaidaTransferencia() != null) {
			if (contaHospitalar.getRnSaidaTransferencia().intValue() <= 0) {
				throw new ApplicationBusinessException(ManterContaHospitalarControllerExceptionCode.SAIDA_TRANSFERENCIA_DEVE_SER_MAIOR_ZERO);
			}
		}

		if (contaHospitalar != null && contaHospitalar.getPesoRn() != null) {
			if (contaHospitalar.getPesoRn().intValue() <= 0) {
				throw new ApplicationBusinessException(ManterContaHospitalarControllerExceptionCode.PESO_NASCER_DEVE_SER_MAIOR_ZERO);
			}
		}

		//if (contaHospitalar != null && contaHospitalar.getMesesGestacao() != null) {
		//	if (contaHospitalar.getMesesGestacao().intValue() <= 0) {
		//		throw new ApplicationBusinessExceptionSemRollback(ManterContaHospitalarControllerExceptionCode.IDADE_GESTACIONAL_DEVE_SER_MAIOR_ZERO);
		//	}
		//}
	}

	public String gravar() {
		try {
			this.validarCampos();

			if (procedimentoHospitalarRealizado != null) {
				final FatProcedHospInternos procedimento = faturamentoFacade.obterProcedimentoHospitalarInterno(procedimentoHospitalarRealizado.getId()
						.getPhiSeq());
				contaHospitalar.setProcedimentoHospitalarInternoRealizado(procedimento);
			} else {
				contaHospitalar.setProcedimentoHospitalarInternoRealizado(null);
			}

			if (situacaoSaidaPaciente != null) {
                     contaHospitalar.setSituacaoSaidaPaciente(situacaoSaidaPaciente);
            
			} else {
				if(motivoSaidaPaciente != null){
					throw new ApplicationBusinessException(ManterContaHospitalarControllerExceptionCode.SITUACAO_ALTA_PACIENTE_INVALIDA);
				} else {
					contaHospitalar.setSituacaoSaidaPaciente(null);
				}
			}

			if (especialidade != null) {
				contaHospitalar.setEspecialidade(especialidade);
			} else {
				contaHospitalar.setEspecialidade(null);
			}

			if (indInfeccao != null) {
				contaHospitalar.setIndInfeccao(indInfeccao.isSim());
			} else {
				contaHospitalar.setIndInfeccao(null);
			}
			
			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e) {
				apresentarMsgNegocio(Severity.ERROR, e.getMessage());
			}

			faturamentoFacade.persistirContaHospitalar(contaHospitalar, contaHospitalarClone, nomeMicrocomputador, new Date());

			apresentarMsgNegocio(Severity.INFO,
					ManterContaHospitalarControllerExceptionCode.CONTA_HOSPITALAR_ALTERADA_SUCESSO.toString());

		} catch (final BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}

		return null;
	}
	
	public String cancelar() {
		if (fromEncerramentoPreviaConta) {
			return pageEncerramentoPreviaConta;
		} 
		if(voltarParaTela != null){
			
			if(voltarParaTela.equalsIgnoreCase("notaConsumoCirurgia")){
				return BLOCOCIRURGICO_REGISTRO_CIRURGIA_REALIZADA;
			}
			else{
				return voltarParaTela;
			}
		}
		return pageManterContaHospitalarList;
	}
	

	public VFatAssociacaoProcedimento getProcedimentoHospitalarSolicitado() {
		return procedimentoHospitalarSolicitado;
	}

	public void setProcedimentoHospitalarSolicitado(final VFatAssociacaoProcedimento procedimentoHospitalarSolicitado) {
		this.procedimentoHospitalarSolicitado = procedimentoHospitalarSolicitado;
	}

	public VFatAssociacaoProcedimento getProcedimentoHospitalarRealizado() {
		return procedimentoHospitalarRealizado;
	}

	public void setProcedimentoHospitalarRealizado(final VFatAssociacaoProcedimento procedimentoHospitalarRealizado) {
		this.procedimentoHospitalarRealizado = procedimentoHospitalarRealizado;
	}

	public FatContasHospitalares getContaHospitalar() {
		return contaHospitalar;
	}

	public void setContaHospitalar(final FatContasHospitalares contaHospitalar) {
		this.contaHospitalar = contaHospitalar;
	}

	public FatContasHospitalares getContaHospitalarClone() {
		return contaHospitalarClone;
	}

	public void setContaHospitalarClone(final FatContasHospitalares contaHospitalarClone) {
		this.contaHospitalarClone = contaHospitalarClone;
	}

	public VFatContaHospitalarPac getContaHospitalarView() {
		return contaHospitalarView;
	}

	public void setContaHospitalarView(final VFatContaHospitalarPac contaHospitalarView) {
		this.contaHospitalarView = contaHospitalarView;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(final Integer seq) {
		this.seq = seq;
	}

	public FatMotivoSaidaPaciente getMotivoSaidaPaciente() {
		return motivoSaidaPaciente;
	}

	public void setMotivoSaidaPaciente(final FatMotivoSaidaPaciente motivoSaidaPaciente) {
		this.motivoSaidaPaciente = motivoSaidaPaciente;
	}

	public FatSituacaoSaidaPaciente getSituacaoSaidaPaciente() {
		return situacaoSaidaPaciente;
	}

	public void setSituacaoSaidaPaciente(final FatSituacaoSaidaPaciente situacaoSaidaPaciente) {
		this.situacaoSaidaPaciente = situacaoSaidaPaciente;
	}

	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(final AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

	public DominioSimNao getIndInfeccao() {
		return indInfeccao;
	}

	public void setIndInfeccao(final DominioSimNao indInfeccao) {
		this.indInfeccao = indInfeccao;
	}

	public Boolean getFromEncerramentoPreviaConta() {
		return fromEncerramentoPreviaConta;
	}

	public void setFromEncerramentoPreviaConta(final Boolean fromEncerramentoPreviaConta) {
		this.fromEncerramentoPreviaConta = fromEncerramentoPreviaConta;
	}

	public String getPageConsultarInconsistencias() {
		return pageConsultarInconsistencias;
	}

	public String getPageEncerramentoPreviaConta() {
		return pageEncerramentoPreviaConta;
	}

	public String getPageCid() {
		return pageCid;
	}

	public String getPageEspelho() {
		return pageEspelho;
	}

	public String getPageManterContaHospitalarList() {
		return pageManterContaHospitalarList;
	}

	public String getPageDesdobramentoContaHospitalar() {
		return pageDesdobramentoContaHospitalar;
	}

	public String getPageManterContaHospitalar() {
		return pageManterContaHospitalar;
	}

	public String getVoltarParaTela() {
		return voltarParaTela;
	}

	public void setVoltarParaTela(String voltarParaTela) {
		this.voltarParaTela = voltarParaTela;
	}
}
