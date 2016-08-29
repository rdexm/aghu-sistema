package br.gov.mec.aghu.faturamento.action;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioIndRespProc;
import br.gov.mec.aghu.dominio.DominioLocalCobrancaProcedimentoAmbulatorialRealizado;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioOrigemProcedimentoAmbulatorialRealizado;
import br.gov.mec.aghu.dominio.DominioSituacaoCompetencia;
import br.gov.mec.aghu.dominio.DominioSituacaoProcedimentoAmbulatorio;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.cadastrosapoio.business.IFaturamentoApoioFacade;
import br.gov.mec.aghu.model.AacConsultaProcedHospitalar;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatCaractItemProcHosp;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatProcedAmbRealizado;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgias;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.VFatAssociacaoProcedimento;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.vo.CodPacienteFoneticaVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.vo.RapServidoresVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.business.SelectionQualifier;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;

public class ManterProcedimentoAmbulatorialController extends ActionController {
	
	private static final long serialVersionUID = 1958907253424723255L;
	private static final Log LOG = LogFactory.getLog(ManterProcedimentoAmbulatorialController.class);

	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	@EJB
	private IFaturamentoApoioFacade faturamentoApoioFacade;
	@EJB
	private IAghuFacade aghuFacade;
	@EJB
	private IParametroFacade parametroFacade;
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	@EJB
	private IExamesFacade examesFacade;
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	@EJB
	private IPacienteFacade pacienteFacade;
	@Inject
	private ManterProcedimentoAmbulatorialPaginatorController paginator;
	@Inject
	@SelectionQualifier
	private Instance<CodPacienteFoneticaVO> codPacienteFonetica;
	private Long seq;
	private FatProcedAmbRealizado procedimentoAmb;
	private FatProcedAmbRealizado procedimentoAmbClone;
	private RapServidoresVO servidorVO;
	private boolean fieldsControl;
	// Campos auxliares de suggestions
	private Short cpgCphCspCnvCodigo;
	private Short cpgGrcSeq;
	private Byte cpgCphCspSeq;
	// Suggestions
	// Convenio / Plano
	private Short convenioId;
	private Byte planoId;
	// Consulta
	private String descricaoConsulta;
	private Integer itemProcConsulta;
	private Byte quantidadeConsulta;
	// Procedimento Realizado
	private VFatAssociacaoProcedimento procedimento;
	// Paciente
	private Integer pacCodigoFonetica;
	private Integer pacProntuario;
	private Integer pacCodigo;
	private AipPacientes paciente;
	// Exame
	private Short itemExame;
	private String siglaExame;
	private String materialExame;
	private Integer idExame;
	// Cirurgia
	private String ppcEprPciSeq;
	private String ppcEprEspSeq;
	private List<DominioIndRespProc> itensIndRespProc;
	private String criadoPor;
	private String alteradoPor;
	private Integer phiSeq;
	private String situacaoExameLiberado;
	private Short tipoCBO;
	private boolean pacienteRequired;
	private boolean cidRequired;
	private String voltarParaTela;
	private String ppcEprPciSeqSuggestion;
	private String ppcEprEspSeqSuggestion;
	private DominioIndRespProc indRespProcSuggestion;
	
	public enum ManterProcedimentoAmbulatorialControllerExceptionCode implements BusinessExceptionCode {
		LABEL_PROCEDIMENTO_HOSPITALAR_INTERNO_GRAVADO_SUCESSO, LABEL_NUMERO_INVALIDO;
	}

	private Log getLog() {
		return LOG;
	}
	
	@PostConstruct
	protected void init() {
		begin(conversation);
	}

	/*
	 * Se a origem for Digitado então é possível alterar: - Paciente -
	 * Procedimento Realizado - Data Realizado - Quantidade - Unid. Funcional -
	 * Situação Se a origem for outra, somente é possível alterar: - Situação -
	 * Quantidade
	 */
	public void atualizaFieldControl() {
		fieldsControl = !DominioOrigemProcedimentoAmbulatorialRealizado.DIG.equals(procedimentoAmb.getIndOrigem());
	}

	public void inicio() {
	 

		final DominioIndRespProc[] d = { DominioIndRespProc.AGND, DominioIndRespProc.DESC, DominioIndRespProc.NOTA };
		setItensIndRespProc(Arrays.asList((d)));
		fieldsControl = true;
		if (cpgCphCspCnvCodigo == null) {
			createParametrosFind();
		}
		if (tipoCBO == null) {
			try {
				final AghParametros paramTipoCBO = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_TIPO_INF_CBO);
				tipoCBO = paramTipoCBO.getVlrNumerico().shortValue();
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
				// //logError(e);
			}
		}
		if (phiSeq != null) {
			List<VFatAssociacaoProcedimento> phis = faturamentoFacade.listarVFatAssociacaoProcedimento(phiSeq, cpgCphCspCnvCodigo,cpgCphCspSeq, cpgGrcSeq);
			if (!phis.isEmpty()) {
				procedimento = phis.get(0);
				phiSeq = null;
			}
		}
		inicializaPaciente();
		if (seq != null) {
			inicializaEdicao();
		} else {
			inicializaInsercao();
		}
		atualizaFieldControl();
	
	}

	private void inicializaInsercao() {
		procedimentoAmb = new FatProcedAmbRealizado();
		procedimentoAmb.setSituacao(DominioSituacaoProcedimentoAmbulatorio.ABERTO);
		procedimentoAmb.setIndOrigem(DominioOrigemProcedimentoAmbulatorialRealizado.DIG);
		procedimentoAmb.setDthrRealizado(new Date());
		procedimentoAmb.setQuantidade(Short.valueOf("1"));
		procedimentoAmb.setLocalCobranca(DominioLocalCobrancaProcedimentoAmbulatorialRealizado.B);
		procedimentoAmb.setSituacao(DominioSituacaoProcedimentoAmbulatorio.ABERTO);
		procedimentoAmb.setFatCompetencia(faturamentoFacade.buscarCompetenciasDataHoraFimNula(DominioModuloCompetencia.AMB,DominioSituacaoCompetencia.A));
		// Convênio / Plano
		atribuirPlano();
		// Consulta
		populaItensConsulta();
		// Exame
		populaItensExame();
		// Cirurgia
		populaItensMbcProcEspPorCirurgias();
		criadoPor = null;
		alteradoPor = null;
	}

	private void inicializaEdicao() {
		try {
			procedimentoAmb = faturamentoFacade.obterFatProcedAmbRealizadoPorSeq(seq);
			procedimentoAmbClone = faturamentoFacade.clonarFatProcedAmbRealizado(procedimentoAmb);
			// Procedimento Realizado
			if (this.procedimentoAmb.getProcedimentoHospitalarInterno() != null) {
				final List<VFatAssociacaoProcedimento> result = faturamentoFacade.listarVFatAssociacaoProcedimento(this.procedimentoAmb
						.getProcedimentoHospitalarInterno().getSeq(), cpgCphCspCnvCodigo, cpgCphCspSeq, cpgGrcSeq);
				if (!result.isEmpty()) {
					this.procedimento = result.get(0);
				}
			} else {
				procedimento = new VFatAssociacaoProcedimento();
			}
			// Convênio / Plano
			atribuirPlano();
			// Paciente
			paciente = procedimentoAmb.getPaciente();
			if (paciente != null) {
				pacProntuario = paciente.getProntuario();
				pacCodigo = paciente.getCodigo();
			}
			// CID ?
			// Consulta
			populaItensConsulta();
			// Exame
			populaItensExame();
			//mostra mensagem para o usuário quando  Convênio/Plano e Procedimento são incompatíveis!
			verificaCompatibilidadeConvenioComProcedimento();
			// Cirurgia
			populaItensMbcProcEspPorCirurgias();
			criadoPor = procedimentoAmb.getCriadoPor() + " em "
					+ DateUtil.obterDataFormatadaHoraMinutoSegundo(procedimentoAmb.getCriadoEm());
			alteradoPor = procedimentoAmb.getAlteradoPor() + " em "
					+ DateUtil.obterDataFormatadaHoraMinutoSegundo(procedimentoAmb.getAlteradoEm());

			if (procedimentoAmb != null && procedimentoAmb.getServidorResponsavel() != null
					&& procedimentoAmb.getServidorResponsavel().getPessoaFisica() != null) {
				servidorVO = registroColaboradorFacade.obterServidorVO(procedimentoAmb.getServidorResponsavel().getPessoaFisica()
						.getCodigo(), procedimentoAmb.getServidorResponsavel().getId().getMatricula(), procedimentoAmb
						.getServidorResponsavel().getId().getVinCodigo(), tipoCBO);
			}

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			getLog().error(e);
		}
	}

	private void inicializaPaciente() {
		if (paciente == null || paciente.getCodigo() == null) {
			CodPacienteFoneticaVO codPac = codPacienteFonetica.get();
			if (codPac != null && codPac.getCodigo() > 0) { 
				paciente = pacienteFacade.obterAipPacientesPorChavePrimaria(codPac.getCodigo());
				if (paciente != null) {
					pacProntuario = paciente.getProntuario();
					pacCodigo = paciente.getCodigo();
				}
			}
			if (procedimento != null && procedimento.getId().getPhiSeq() != null) {
				List<VFatAssociacaoProcedimento> phis = faturamentoFacade.listarVFatAssociacaoProcedimento(
						procedimento.getId().getPhiSeq(), cpgCphCspCnvCodigo, cpgCphCspSeq, cpgGrcSeq);

				if (!phis.isEmpty()) {
					procedimento = phis.get(0);
					phiSeq = null;
				}
			}
			// caso esteja processando uma busca de paciente não deve processar
			// lógicas de insert/update
			if (procedimentoAmb != null && procedimentoAmb.getSeq() != null) {
				atualizaFieldControl();
				return;
			} else if (procedimentoAmb != null && procedimentoAmb.getIndOrigem() != null) {
				atualizaFieldControl();
				return;
			}
		} else if (pacCodigo != null) {
			obterPacientePorCodigo();

		} else if (pacProntuario != null) {
			obterPacientePorProntuario();
		}
	}

	private void verificaCompatibilidadeConvenioComProcedimento() throws  ApplicationBusinessException {
		if (this.procedimentoAmb.getProcedimentoHospitalarInterno() != null
				&& !this.procedimentoAmb.getSituacao().equals(DominioSituacaoProcedimentoAmbulatorio.CANCELADO)){
			faturamentoFacade.verificaCompatibilidadeConvenioComProcedimento(this.procedimentoAmb.getProcedimentoHospitalarInterno().getSeq(),  cpgCphCspCnvCodigo,  cpgCphCspSeq);
		}
	}
	
	private void createParametrosFind() {
		try {
			final AghParametros planoSUS = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SUS_PLANO_AMBULATORIO);
			final AghParametros convenioSUS = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_CONVENIO_SUS);
			final AghParametros grupoSUS = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS);
			final AghParametros situacaoExame = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SITUACAO_LIBERADO);
			cpgCphCspCnvCodigo = convenioSUS.getVlrNumerico().shortValue();
			cpgCphCspSeq = planoSUS.getVlrNumerico().byteValue();
			cpgGrcSeq = grupoSUS.getVlrNumerico().shortValue();
			situacaoExameLiberado = situacaoExame.getVlrTexto();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			getLog().error(e);
		}
	}

	public String confirmar() {
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = getEnderecoIPv4HostRemoto().getCanonicalHostName();
		} catch (UnknownHostException e1) {
			nomeMicrocomputador = "";
		}
		try {
			if (pacProntuario == null && pacCodigo != null) {
				obterPacientePorCodigo();

			} else if (pacProntuario != null && pacCodigo == null) {
				obterPacientePorProntuario();
			}
			boolean isUpdate = procedimentoAmb.getSeq() != null;

			if (servidorVO != null) {
				procedimentoAmb.setServidorResponsavel(registroColaboradorFacade.obterServidor(new RapServidores(new RapServidoresId(
						servidorVO.getMatricula(), servidorVO.getVinculo()))));
			}
			if (!validaProcedimento(true)) {
				return null;
			}
			procedimentoAmb.setProcedimentoHospitalarInterno(procedimento.getProcedimentoHospitalarInterno());
			procedimentoAmb.setPaciente(paciente);

			final Date dataFimVinculoServidor = new Date();
			faturamentoFacade.persistirProcedimentoAmbulatorialRealizado(procedimentoAmb, procedimentoAmbClone, nomeMicrocomputador,dataFimVinculoServidor);
			if (isUpdate) {
				apresentarMsgNegocio(Severity.INFO, "PROCEDIMENTO_AMBULATORIAL_ALTERADO_SUCESSO", "alterado");
			} else {
				apresentarMsgNegocio(Severity.INFO, "PROCEDIMENTO_AMBULATORIAL_ALTERADO_SUCESSO", "inserido");
			}
			paginator.pesquisar();
			paginator.inicio();
			// Volta para a tela de pesquisa (caso seja edição)
			if (seq != null) {
				paginator.setSeqEdicao(seq);
				limparInsercao();
				return cancelar();
			} else {
				limparInsercao();
				validaProcedimento(false);
				return null; // Continua na mesma tela
			}
		} catch (BaseException e) {
			// faturamentoFacade.refresh(procedimentoAmb);
			apresentarExcecaoNegocio(e);
			getLog().error(e);
		}
		return null;
	}

	/* Tambem é usado para voltar */
	public String cancelar() {
		if(voltarParaTela != null){
			if(voltarParaTela.equalsIgnoreCase("notaConsumoCirurgia")){
				return "blococirurgico-registroCirurgiaRealizada";
			}
			return voltarParaTela;
		}
		limparInsercao();
		return "manterProcedimentoAmbulatorialList";
	}

	private void limpar() {
		procedimentoAmb.setConsultaProcedHospitalar(null);
		itemProcConsulta = null;
		descricaoConsulta = null;
		quantidadeConsulta = null;
		servidorVO = null;
	}

	private void limparInsercao() {
		seq = null;
		pacCodigo = null;
		pacCodigoFonetica = null;
		pacProntuario = null;
		paciente = null;
		faturamentoFacade.evict(procedimentoAmb); // Desatacha objeto
		procedimentoAmb.setSeq(null);
		procedimentoAmb.setCid(null);
		phiSeq = null;
		pacienteRequired = false;
		cidRequired = false;
	}

	private void limparExame() {
		procedimentoAmb.setItemSolicitacaoExame(null);
		itemExame = null;
		siglaExame = null;
		materialExame = null;
		setIdExame(null);
	}

	private void limparCirurgia() {
		ppcEprPciSeqSuggestion = null;
		ppcEprEspSeqSuggestion = null;
		ppcEprPciSeq = null;
		ppcEprEspSeq = null;
		procedimentoAmb.setProcEspPorCirurgia(null);
	}

	// Suggestions
	public List<VFatAssociacaoProcedimento> listarAssociacaoProcedimentoPorPhiSeqCodSusOuDescricao(final String strPesquisa) {
		try {
			return this.returnSGWithCount(faturamentoFacade.listarAssociacaoProcedimentoPorPhiSeqCodSusOuDescricao(strPesquisa, cpgCphCspCnvCodigo, cpgGrcSeq,
					cpgCphCspSeq),listarAssociacaoProcedimentoPorPhiSeqCodSusOuDescricaoCount(strPesquisa));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}

	public void validaProcedimento() {
		validaProcedimento(false);
	}

	public Boolean validaProcedimento(boolean isSaveOrUpdate) {
		cidRequired = pacienteRequired = false;
		if (procedimento != null) {
			try {
				final AghParametros procBPI = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_COD_CARACTERISTICA_COBRA_BPI);
				final AghParametros procBPA = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_COD_CARACTERISTICA_COBRA_BPA);
				final Integer tctSeqBPI = procBPI.getVlrNumerico().intValue();
				final Integer tctSeqBPA = procBPA.getVlrNumerico().intValue();
				final FatCaractItemProcHosp caract = faturamentoFacade.obterQtdeFatCaractItemProcHosp(procedimento.getItemProcedimentoHospitalar(), tctSeqBPI, tctSeqBPA);
				if (caract != null) {
					if (tctSeqBPI.equals(caract.getId().getTctSeq())) {
						pacienteRequired = true;
						if (isSaveOrUpdate && pacienteRequired && paciente == null) {
							apresentarMsgNegocio(Severity.ERROR, "PROCEDIMENTO_AMBULATORIAL_PACIENTE_REQUERIDO");
							return false;
						}
						Long result = faturamentoFacade.obterQtdeFatProcedHospIntCid(procedimento.getProcedimentoHospitalarInterno().getSeq());
						cidRequired = (result != null && result > 0);
						if (isSaveOrUpdate && cidRequired && procedimentoAmb.getCid() == null) {
							apresentarMsgNegocio(Severity.ERROR, "PROCEDIMENTO_AMBULATORIAL_CID_REQUERIDO");
							return false;
						}
					} else {
						// Deve ser consulta
						pacienteRequired = caract.getItemProcedimentoHospitalar().getConsulta();
					}
				}
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
				getLog().error(e);
			}
		}
		return true;
	}

	public Long listarAssociacaoProcedimentoPorPhiSeqCodSusOuDescricaoCount(final String strPesquisa) {
		try {
			return faturamentoFacade.listarAssociacaoProcedimentoPorPhiSeqCodSusOuDescricaoCount(strPesquisa, cpgCphCspCnvCodigo,cpgGrcSeq, cpgCphCspSeq);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return 0L;
		}
	}

	public Long pesquisarConvenioSaudePlanosCount(String filtro) {
		return this.faturamentoApoioFacade.pesquisarConvenioSaudePlanosCount((String) filtro);
	}

	public List<FatConvenioSaudePlano> pesquisarConvenioSaudePlanos(String filtro) {
		return this.returnSGWithCount(this.faturamentoApoioFacade.pesquisarConvenioSaudePlanos((String) filtro),pesquisarConvenioSaudePlanosCount(filtro));
	}

	public void atribuirPlano() {
		if (procedimentoAmb.getConvenioSaudePlano() != null && this.procedimentoAmb.getConvenioSaudePlano().getConvenioSaude() != null) {
			this.convenioId = this.procedimentoAmb.getConvenioSaudePlano().getConvenioSaude().getCodigo();
			this.planoId = this.procedimentoAmb.getConvenioSaudePlano().getId().getSeq();
		} else {
			procedimentoAmb.setConvenioSaudePlano(null);
			this.convenioId = null;
			this.planoId = null;
		}
	}

	public void escolherPlanoConvenio() {
		if (planoId != null && convenioId != null) {
			FatConvenioSaudePlano plano = faturamentoApoioFacade.obterPlanoPorId(planoId, convenioId);
			if (plano == null) {
				apresentarMsgNegocio(Severity.WARN, "MENSAGEM_CONVENIO_PLANO_NAO_ENCONTRADO", convenioId, planoId);
			} else {
				procedimentoAmb.setConvenioSaudePlano(plano);
				this.atribuirPlano();
			}
		}
	}

	public void obterPacientePorProntuario() {
		final Integer filtro = pacProntuario;
		if (filtro != null) {
			paciente = pacienteFacade.obterPacientePorProntuario(filtro);
			if (paciente != null) {
				pacProntuario = paciente.getProntuario();
				pacCodigo = paciente.getCodigo();
			} else {
				pacProntuario = null;
				pacCodigo = null;
				apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_PACIENTE_PRONTUARIO_NAO_ENCONTRADO");
			}
		} else {
			paciente = null;
			pacCodigo = null;
		}
	}

	public void obterPacientePorCodigo() {
		final Integer filtro = pacCodigo;
		if (filtro != null) {
			paciente = pacienteFacade.obterNomePacientePorCodigo(filtro);
			if (paciente != null) {
				pacProntuario = paciente.getProntuario();
				pacCodigo = paciente.getCodigo();

			} else {
				pacProntuario = null;
				pacCodigo = null;
				apresentarMsgNegocio(Severity.ERROR, "FAT_00731");
			}
		} else {
			paciente = null;
			pacProntuario = null;
			validaProcedimento(false);
		}
	}

	public String redirecionarPesquisaFonetica() {
		paciente = null;
		return "paciente-pesquisaPacienteComponente";
	}

	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncional(final String strPesquisa) {
		return this.returnSGWithCount(aghuFacade.pesquisarUnidadeFuncionalPorCodigoEDescricaoAtivaseInativas(strPesquisa),pesquisarUnidadeFuncionalCount(strPesquisa));
	}

	public Long pesquisarUnidadeFuncionalCount(final String strPesquisa) {
		return aghuFacade.pesquisarUnidadeFuncionalPorCodigoEDescricaoAtivaseInativasCount(strPesquisa);
	}

	public List<AghEspecialidades> listarEspecialidades(final String strPesquisa) {
		return this.returnSGWithCount(this.aghuFacade.pesquisarPorNomeSiglaInternaUnidade((String) strPesquisa),listarEspecialidadesCount(strPesquisa));
	}

	public Long listarEspecialidadesCount(final String strPesquisa) {
		return this.aghuFacade.pesquisarPorNomeSiglaInternaUnidadeCount((String) strPesquisa);
	}

	public List<AghCid> pesquisarCids(String param) {
		return this.returnSGWithCount(faturamentoFacade.pesquisarCidsPorDescricaoOuCodigo(param, 100, AghCid.Fields.DESCRICAO.toString()),pesquisarCidsCount(param));
	}

	public Long pesquisarCidsCount(String param) {
		return faturamentoFacade.pesquisarCidsPorDescricaoOuCodigoCount(param);
	}

	public List<RapServidoresVO> pesquisarServidor(String strPesquisa) {
		try {
			return this.returnSGWithCount(registroColaboradorFacade.pesquisarServidor(strPesquisa, tipoCBO),pesquisarServidorCount(strPesquisa));
		} catch (BaseException e) {
			return new ArrayList<RapServidoresVO>();
		}
	}

	public Long pesquisarServidorCount(String strPesquisa) {
		try {
			return registroColaboradorFacade.pesquisarServidorCount(strPesquisa, tipoCBO);
		} catch (BaseException e) {
			return 0L;
		}
	}

	public List<AacConsultaProcedHospitalar> listarConsultas(Object objPesquisa) {
		return ambulatorioFacade.listarConsultasPorComNumeroDescricaoProcedimento(objPesquisa);
	}

	public Long listarConsultasCount(Object objPesquisa) {
		return ambulatorioFacade.listarConsultasPorComNumeroDescricaoProcedimentoCount(objPesquisa);
	}

	public void populaItensConsulta() {
		if (this.procedimentoAmb.getConsultaProcedHospitalar() != null) {
			itemProcConsulta = procedimentoAmb.getConsultaProcedHospitalar().getId().getPhiSeq();
			descricaoConsulta = procedimentoAmb.getConsultaProcedHospitalar().getProcedHospInterno().getDescricao();
			quantidadeConsulta = procedimentoAmb.getConsultaProcedHospitalar().getQuantidade();
		} else {
			limpar();
		}
	}

	public List<AelItemSolicitacaoExames> pesquisarItemSolicitacaoExamePorSituacaoLiberado(final Object solicitacaoExameSeq) {
		return examesFacade.pesquisarItemSolicitacaoExamePorSituacaoLiberado(solicitacaoExameSeq, situacaoExameLiberado);
	}

	public Long pesquisarItemSolicitacaoExamePorSituacaoLiberadoCount(final Object solicitacaoExameSeq) {
		return examesFacade.pesquisarItemSolicitacaoExamePorSituacaoLiberadoCount(solicitacaoExameSeq, situacaoExameLiberado);
	}

	public void populaItensExame() {
		if (procedimentoAmb.getItemSolicitacaoExame() != null) {
			materialExame = procedimentoAmb.getItemSolicitacaoExame().getMaterialAnalise().getSeq() + " - "+ procedimentoAmb.getItemSolicitacaoExame().getMaterialAnalise().getDescricao();
			itemExame = procedimentoAmb.getItemSolicitacaoExame().getId().getSeqp();
			siglaExame = procedimentoAmb.getItemSolicitacaoExame().getExame().getSigla();
			setIdExame(procedimentoAmb.getItemSolicitacaoExame().getId().getSoeSeq());
		} else {
			limparExame();
		}
	}

	public void atualizaPpcEprPciSeq() {
		ppcEprPciSeqSuggestion = ppcEprPciSeq;
	}

	public void atualizaPpcEprEspSeq() {
		ppcEprEspSeqSuggestion = ppcEprEspSeq;
	}

	public List<MbcProcEspPorCirurgias> pesquisarMbcProcEspPorCirurgias(final Object objPesquisa) {
		return blocoCirurgicoFacade.pesquisarMbcProcEspPorCirurgias(objPesquisa, ppcEprPciSeqSuggestion, ppcEprEspSeqSuggestion,indRespProcSuggestion);
	}

	public Long pesquisarMbcProcEspPorCirurgiasCount(final Object objPesquisa) {
		return blocoCirurgicoFacade.pesquisarMbcProcEspPorCirurgiasCount(objPesquisa, ppcEprPciSeq, ppcEprEspSeq, DominioIndRespProc.NOTA);
	}

	public void populaItensMbcProcEspPorCirurgias() {
		if (procedimentoAmb.getProcEspPorCirurgia() != null) {
			ppcEprPciSeq = procedimentoAmb.getProcEspPorCirurgia().getId().getEprPciSeq() + " - "	+ procedimentoAmb.getProcEspPorCirurgia().getProcedimentoCirurgico().getDescricao();
			ppcEprEspSeq = procedimentoAmb.getProcEspPorCirurgia().getId().getEprEspSeq() + " - "+ procedimentoAmb.getProcEspPorCirurgia().getMbcEspecialidadeProcCirgs().getEspecialidade().getNomeEspecialidade();
		} else {
			limparCirurgia();
		}
	}

	public boolean isOrigemDigitada() {
		if (procedimentoAmb != null) {
			return DominioOrigemProcedimentoAmbulatorialRealizado.DIG.equals(procedimentoAmb.getIndOrigem());
		}
		return false;
	}

	public List<FatCompetencia> pesquisarCompetencias(String objPesquisa) {
		try {
			return this.returnSGWithCount(faturamentoFacade.listarCompetenciaModuloMesAnoDtHoraInicioSemHora(faturamentoFacade.getCompetenciaId(objPesquisa,	DominioModuloCompetencia.AMB)),pesquisarCompetenciasCount(objPesquisa));
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return new ArrayList<FatCompetencia>(0);
		}
	}

	public Long pesquisarCompetenciasCount(String objPesquisa) {
		try {
			return faturamentoFacade.listarCompetenciaModuloMesAnoDtHoraInicioSemHoraCount(faturamentoFacade.getCompetenciaId(objPesquisa,
					DominioModuloCompetencia.AMB));
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return 0L;
		}
	}
	// Sets and gets
	public Long getSeq() {
		return seq;
	}
	public void setSeq(Long seq) {
		this.seq = seq;
	}
	public Integer getPacCodigoFonetica() {
		return pacCodigoFonetica;
	}
	public void setPacCodigoFonetica(Integer pacCodigoFonetica) {
		this.pacCodigoFonetica = pacCodigoFonetica;
	}
	public Integer getPacProntuario() {
		return pacProntuario;
	}
	public void setPacProntuario(Integer pacProntuario) {
		this.pacProntuario = pacProntuario;
	}
	public Integer getPacCodigo() {
		return pacCodigo;
	}
	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}
	public AipPacientes getPaciente() {
		return paciente;
	}
	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}
	public Short getItemExame() {
		return itemExame;
	}
	public void setItemExame(Short itemExame) {
		this.itemExame = itemExame;
	}
	public String getSiglaExame() {
		return siglaExame;
	}
	public void setSiglaExame(String siglaExame) {
		this.siglaExame = siglaExame;
	}
	public void setProcedimentoAmb(FatProcedAmbRealizado procedimentoAmb) {
		this.procedimentoAmb = procedimentoAmb;
	}
	public VFatAssociacaoProcedimento getProcedimento() {
		return procedimento;
	}
	public void setProcedimento(VFatAssociacaoProcedimento procedimento) {
		this.procedimento = procedimento;
	}
	public FatProcedAmbRealizado getProcedimentoAmb() {
		return procedimentoAmb;
	}
	public Short getCpgCphCspCnvCodigo() {
		return cpgCphCspCnvCodigo;
	}
	public void setCpgCphCspCnvCodigo(Short cpgCphCspCnvCodigo) {
		this.cpgCphCspCnvCodigo = cpgCphCspCnvCodigo;
	}
	public Short getCpgGrcSeq() {
		return cpgGrcSeq;
	}
	public void setCpgGrcSeq(Short cpgGrcSeq) {
		this.cpgGrcSeq = cpgGrcSeq;
	}
	public Byte getCpgCphCspSeq() {
		return cpgCphCspSeq;
	}
	public void setCpgCphCspSeq(Byte cpgCphCspSeq) {
		this.cpgCphCspSeq = cpgCphCspSeq;
	}
	public Short getConvenioId() {
		return convenioId;
	}
	public void setConvenioId(Short convenioId) {
		this.convenioId = convenioId;
	}
	public Byte getPlanoId() {
		return planoId;
	}
	public void setPlanoId(Byte planoId) {
		this.planoId = planoId;
	}
	public String getDescricaoConsulta() {
		return descricaoConsulta;
	}
	public void setDescricaoConsulta(String descricaoConsulta) {
		this.descricaoConsulta = descricaoConsulta;
	}
	public Integer getItemProcConsulta() {
		return itemProcConsulta;
	}
	public void setItemProcConsulta(Integer itemProcConsulta) {
		this.itemProcConsulta = itemProcConsulta;
	}
	public String getMaterialExame() {
		return materialExame;
	}
	public void setMaterialExame(String materialExame) {
		this.materialExame = materialExame;
	}
	public String getPpcEprPciSeq() {
		return ppcEprPciSeq;
	}
	public void setPpcEprPciSeq(String ppcEprPciSeq) {
		this.ppcEprPciSeq = ppcEprPciSeq;
	}
	public String getPpcEprEspSeq() {
		return ppcEprEspSeq;
	}
	public void setPpcEprEspSeq(String ppcEprEspSeq) {
		this.ppcEprEspSeq = ppcEprEspSeq;
	}
	public List<DominioIndRespProc> getItensIndRespProc() {
		return itensIndRespProc;
	}
	public void setItensIndRespProc(List<DominioIndRespProc> itensIndRespProc) {
		this.itensIndRespProc = itensIndRespProc;
	}
	public String getPpcEprPciSeqSuggestion() {
		return ppcEprPciSeqSuggestion;
	}
	public void setPpcEprPciSeqSuggestion(String ppcEprPciSeqSuggestion) {
		this.ppcEprPciSeqSuggestion = ppcEprPciSeqSuggestion;
	}
	public String getPpcEprEspSeqSuggestion() {
		return ppcEprEspSeqSuggestion;
	}
	public void setPpcEprEspSeqSuggestion(String ppcEprEspSeqSuggestion) {
		this.ppcEprEspSeqSuggestion = ppcEprEspSeqSuggestion;
	}
	public DominioIndRespProc getIndRespProcSuggestion() {
		return indRespProcSuggestion;
	}
	public void setIndRespProcSuggestion(DominioIndRespProc indRespProcSuggestion) {
		this.indRespProcSuggestion = indRespProcSuggestion;
	}
	public String getCriadoPor() {
		return criadoPor;
	}
	public void setCriadoPor(String criadoPor) {
		this.criadoPor = criadoPor;
	}
	public String getAlteradoPor() {
		return alteradoPor;
	}
	public void setAlteradoPor(String alteradoPor) {
		this.alteradoPor = alteradoPor;
	}
	public String getSituacaoExameLiberado() {
		return situacaoExameLiberado;
	}
	public void setSituacaoExameLiberado(String situacaoExameLiberado) {
		this.situacaoExameLiberado = situacaoExameLiberado;
	}
	public boolean isFieldsControl() {
		return fieldsControl;
	}
	public void setFieldsControl(boolean fieldsControl) {
		this.fieldsControl = fieldsControl;
	}
	public FatProcedAmbRealizado getProcedimentoAmbClone() {
		return procedimentoAmbClone;
	}
	public void setProcedimentoAmbClone(FatProcedAmbRealizado procedimentoAmbClone) {
		this.procedimentoAmbClone = procedimentoAmbClone;
	}
	public Integer getPhiSeq() {
		return phiSeq;
	}
	public void setPhiSeq(Integer phiSeq) {
		this.phiSeq = phiSeq;
	}
	public Byte getQuantidadeConsulta() {
		return quantidadeConsulta;
	}
	public void setQuantidadeConsulta(Byte quantidadeConsulta) {
		this.quantidadeConsulta = quantidadeConsulta;
	}
	public Short getTipoCBO() {
		return tipoCBO;
	}
	public void setTipoCBO(Short tipoCBO) {
		this.tipoCBO = tipoCBO;
	}
	public RapServidoresVO getServidorVO() {
		return servidorVO;
	}
	public void setServidorVO(RapServidoresVO servidorVO) {
		this.servidorVO = servidorVO;
	}
	public boolean isPacienteRequired() {
		return pacienteRequired;
	}
	public void setPacienteRequired(boolean pacienteRequired) {
		this.pacienteRequired = pacienteRequired;
	}
	public boolean isCidRequired() {
		return cidRequired;
	}
	public void setCidRequired(boolean cidRequired) {
		this.cidRequired = cidRequired;
	}
	public String getVoltarParaTela() {
		return voltarParaTela;
	}
	public void setVoltarParaTela(String voltarParaTela) {
		this.voltarParaTela = voltarParaTela;
	}

	public Integer getIdExame() {
		return idExame;
	}

	public void setIdExame(Integer idExame) {
		this.idExame = idExame;
	}
}
