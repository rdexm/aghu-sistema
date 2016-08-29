package br.gov.mec.aghu.exames.solicitacao.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.pesquisa.business.IPesquisaExamesFacade;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.exames.solicitacao.vo.DetalharItemSolicitacaoExameVO;
import br.gov.mec.aghu.exames.solicitacao.vo.DetalharItemSolicitacaoPacienteVO;
import br.gov.mec.aghu.exames.solicitacao.vo.DetalharItemSolicitacaoSolicitacaoVO;
import br.gov.mec.aghu.exames.vo.AelItemSolicConsultadoVO;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AelItemSolicExameHist;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelSolicitacaoExamesHist;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.RapQualificacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapTipoQualificacao;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.business.ICadastrosBasicosFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;

public class DetalharItemSolicitacaoExameController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2941688789194461043L;

	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private ICadastrosBasicosFacade cadastrosBasicosFacade;

	@EJB
	private IPesquisaExamesFacade pesquisaExamesFacade;

	@EJB
	private ISolicitacaoExameFacade solicitacaoExameFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	@EJB
	private IPacienteFacade pacienteFacade;

	private Integer soeSeq;
	private Short seqp;
	private String voltarPara;
	private Integer currentTabIndex;
	private Boolean isHist = Boolean.FALSE;
	private Boolean origemPOL = Boolean.FALSE;
	private Boolean origemSolicDetalhamentoAmostras = Boolean.FALSE;
	private Boolean disabledConsultadoPor = Boolean.TRUE;

	private DetalharItemSolicitacaoSolicitacaoVO solicitacaoVO;
	private DetalharItemSolicitacaoPacienteVO pacienteVO;
	private DetalharItemSolicitacaoExameVO exameVO;

	private List<AelItemSolicConsultadoVO> listaItemSolicConsultado = null;
	private String pessoaFisica;
	private String pessoaFisicaResp;
	private String siglaConselhoProf;
	private String siglaConselhoProfResp;
	private String andarAlaDescricao;
	private String informacoesClinicas;
	private Boolean indColetavel;
	
	AghAtendimentos atendimento = null;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		// inicio();
	}

	@SuppressWarnings("PMD.NPathComplexity")
	public void inicio() {
	 

		try {
			AelItemSolicitacaoExames itemSolicitacaoExame = null;
			AelItemSolicExameHist itemSolicitacaoExameHist = null;

			if (isHist) {
				itemSolicitacaoExameHist = this.examesFacade.buscaItemSolicitacaoExamePorIdHistOrigemPol(soeSeq, seqp);
				atendimento = itemSolicitacaoExameHist.getSolicitacaoExame().getAtendimento();
			} else {
				itemSolicitacaoExame = this.examesFacade.buscaItemSolicitacaoExamePorId(soeSeq, seqp);
				atendimento = itemSolicitacaoExame.getSolicitacaoExame().getAtendimento();
			}

			// Solicitacao
			populaSolicitacao(itemSolicitacaoExame, itemSolicitacaoExameHist);
			// Paciente
			populaPaciente(itemSolicitacaoExame, itemSolicitacaoExameHist);
			// Exame
			populaExame(itemSolicitacaoExame, itemSolicitacaoExameHist);

			// botao consultar por
			AghParametros parametro = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SITUACAO_LIBERADO);
			AelSitItemSolicitacoes vIseSituacao = examesFacade.pesquisaSituacaoItemExame(parametro.getVlrTexto());

			if (isHist) {
				if (vIseSituacao.getCodigo().equals(itemSolicitacaoExameHist.getSituacaoItemSolicitacao().getCodigo())) {
					disabledConsultadoPor = false;
				}
			} else {
				if (vIseSituacao.getCodigo().equals(itemSolicitacaoExame.getSituacaoItemSolicitacao().getCodigo())) {
					disabledConsultadoPor = false;
				}
			}

		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
	
	}

	private void populaSolicitacao(AelItemSolicitacaoExames itemSolicitacaoExame, AelItemSolicExameHist itemSolicitacaoExameHist) {
		solicitacaoVO = new DetalharItemSolicitacaoSolicitacaoVO();
		if (isHist) {
			itemSolicitacaoExameHist = this.examesFacade.buscaItemSolicitacaoExamePorIdHistOrigemPol(soeSeq, seqp);
			solicitacaoVO.setSolicitacaoExameSeq(itemSolicitacaoExameHist.getSolicitacaoExame().getSeq());
			solicitacaoVO.setSolicitacaoExameCriadoEm(itemSolicitacaoExameHist.getSolicitacaoExame().getCriadoEm());
		} else {
			itemSolicitacaoExame = this.examesFacade.buscaItemSolicitacaoExamePorId(soeSeq, seqp);
			solicitacaoVO.setSolicitacaoExameSeq(itemSolicitacaoExame.getSolicitacaoExame().getSeq());
			solicitacaoVO.setSolicitacaoExameCriadoEm(itemSolicitacaoExame.getSolicitacaoExame().getCriadoEm());
		}
		// solicitacaoVO.setItemSolicitacaoExame( this.examesFacade.buscaItemSolicitacaoExamePorId(soeSeq,seqp) );

		RapServidores servidorSolicitante = null;
		if (isHist) {
			servidorSolicitante = itemSolicitacaoExameHist.getSolicitacaoExame().getServidor();
		} else {
			servidorSolicitante = itemSolicitacaoExame.getSolicitacaoExame().getServidor();
		}
		List<RapQualificacao> qualificacoesSolicitante = solicitacaoExameFacade.pesquisarQualificacoesSolicitacaoExameSemPermissao(servidorSolicitante.getId().getVinCodigo(), servidorSolicitante
				.getId().getMatricula(), null);
		if (qualificacoesSolicitante != null && !qualificacoesSolicitante.isEmpty()) {
			RapQualificacao rapQualificacaoSolicitante = qualificacoesSolicitante.get(0);
			solicitacaoVO.setNroRegConselhoQualificacaoSolicitante(rapQualificacaoSolicitante.getNroRegConselho());

			if (rapQualificacaoSolicitante.getTipoQualificacao() != null){
				RapTipoQualificacao tipoQualificacao = cadastrosBasicosFacade.obterQualificacaoEConselhoPorCodigo(rapQualificacaoSolicitante.getTipoQualificacao().getCodigo());
				if(tipoQualificacao.getConselhoProfissional() != null) {
					this.siglaConselhoProf = cadastrosBasicosFacade.obterSiglaConselho(tipoQualificacao.getConselhoProfissional().getCodigo());
				}
			}
		}

		RapServidores servidorResponsavel = null;
		if (isHist) {
			servidorResponsavel = itemSolicitacaoExameHist.getSolicitacaoExame().getServidorResponsabilidade();
		} else {
			servidorResponsavel = itemSolicitacaoExame.getSolicitacaoExame().getServidorResponsabilidade();
		}
		if (servidorResponsavel != null) {
			List<RapQualificacao> qualificacoesResponsavel = solicitacaoExameFacade.pesquisarQualificacoesSolicitacaoExameSemPermissao(servidorResponsavel.getId().getVinCodigo(), servidorResponsavel
					.getId().getMatricula(), null);
			if (qualificacoesResponsavel != null && !qualificacoesResponsavel.isEmpty()) {
				RapQualificacao rapQualificacaoResponsavel = qualificacoesResponsavel.get(0);
				solicitacaoVO.setNroRegConselhoQualificacaoResponsavel(rapQualificacaoResponsavel.getNroRegConselho());
				
				if (rapQualificacaoResponsavel.getTipoQualificacao() != null && rapQualificacaoResponsavel != null) {
					RapTipoQualificacao tipoQualificacao = cadastrosBasicosFacade.obterQualificacaoEConselhoPorCodigo(rapQualificacaoResponsavel.getTipoQualificacao().getCodigo());
					if(tipoQualificacao.getConselhoProfissional() != null) {
						this.siglaConselhoProfResp = cadastrosBasicosFacade.obterSiglaConselho(tipoQualificacao.getConselhoProfissional().getCodigo());
					}
				}
			}
		}

		if (isHist) {
			populaFiltrosTelaHist(itemSolicitacaoExameHist);
		} else {
			populaFiltrosTela(itemSolicitacaoExame);
		}

	}

	private void populaFiltrosTelaHist(AelItemSolicExameHist itemSolicitacaoExameHist) {
		AelSolicitacaoExamesHist solicitacaoExame = itemSolicitacaoExameHist.getSolicitacaoExame();

		if (solicitacaoExame != null) {
			if (solicitacaoExame.getUnidadeFuncional() != null) {
				andarAlaDescricao = solicitacaoExame.getUnidadeFuncional().getAndarAlaDescricao();
			}
			informacoesClinicas = solicitacaoExame.getInformacoesClinicas();
			indColetavel = itemSolicitacaoExameHist.getMaterialAnalise().getIndColetavel();
			if (solicitacaoExame.getServidor() != null && solicitacaoExame.getServidor().getPessoaFisica() != null) {
				pessoaFisica = solicitacaoExame.getServidor().getPessoaFisica().getNome();
			}

			if (solicitacaoExame.getServidorResponsabilidade() != null && solicitacaoExame.getServidorResponsabilidade().getPessoaFisica() != null) {
				pessoaFisicaResp = solicitacaoExame.getServidorResponsabilidade().getPessoaFisica().getNome();
			}
		}

	}

	private void populaFiltrosTela(AelItemSolicitacaoExames itemSolicitacaoExame) {
		AelSolicitacaoExames solicitacaoExame = itemSolicitacaoExame.getSolicitacaoExame();

		if (solicitacaoExame != null) {
			if (solicitacaoExame.getUnidadeFuncional() != null) {
				andarAlaDescricao = solicitacaoExame.getUnidadeFuncional().getAndarAlaDescricao();
			}
			informacoesClinicas = solicitacaoExame.getInformacoesClinicas();
			indColetavel = itemSolicitacaoExame.getMaterialAnalise().getIndColetavel();
			if (solicitacaoExame.getServidor() != null && solicitacaoExame.getServidor().getPessoaFisica() != null) {
				pessoaFisica = solicitacaoExame.getServidor().getPessoaFisica().getNome();
			}

			if (solicitacaoExame.getServidorResponsabilidade() != null && solicitacaoExame.getServidorResponsabilidade().getPessoaFisica() != null) {
				pessoaFisicaResp = solicitacaoExame.getServidorResponsabilidade().getPessoaFisica().getNome();
			}
		}

	}

	private void populaExame(AelItemSolicitacaoExames itemSolicitacaoExame, AelItemSolicExameHist itemSolicitacaoExameHist) {
		exameVO = new DetalharItemSolicitacaoExameVO();
		// exameVO.setItem(itemSolicitacaoExame);
		if (isHist) {
			exameVO.setItemSolicitacaoExamesIdSeqp(itemSolicitacaoExameHist.getId().getSeqp());

			exameVO.setComplementoMotCanc(itemSolicitacaoExameHist.getComplementoMotCanc());
			exameVO.setDescMaterialAnalise(itemSolicitacaoExameHist.getDescMaterialAnalise());

			exameVO.setDthrProgramada(itemSolicitacaoExameHist.getDthrProgramada());
			exameVO.setIndUsoO2String(itemSolicitacaoExameHist.getIndUsoO2String());

			exameVO.setNroAmostras(itemSolicitacaoExameHist.getNroAmostras());
			exameVO.setIntervaloDiasHoras(itemSolicitacaoExameHist.getIntervaloDiasHoras());

			exameVO.setPrioridadeExecucao(itemSolicitacaoExameHist.getPrioridadeExecucao());
			exameVO.setTipoColeta(itemSolicitacaoExameHist.getTipoColeta());
			if (itemSolicitacaoExameHist.getAelMotivoCancelaExames() != null) {
				exameVO.setDescricaoMotivoCancelaExames(itemSolicitacaoExameHist.getAelMotivoCancelaExames().getDescricao());
			}
			if (itemSolicitacaoExameHist.getRegiaoAnatomica() != null) {
				exameVO.setDescricaoRegiaoAnatomica(itemSolicitacaoExameHist.getRegiaoAnatomica().getDescricao());
			}
			if (itemSolicitacaoExameHist.getSituacaoItemSolicitacao() != null) {
				exameVO.setDescricaoSituacaoItemSolicitacao(itemSolicitacaoExameHist.getSituacaoItemSolicitacao().getDescricao());
			}
			if (itemSolicitacaoExameHist.getTipoTransporte() != null) {
				exameVO.setDescricaoTipoTransporte(itemSolicitacaoExameHist.getTipoTransporte().getDescricao());
			}
			if (itemSolicitacaoExameHist.getAelUnfExecutaExames() != null && itemSolicitacaoExameHist.getAelUnfExecutaExames().getAelExamesMaterialAnalise() != null) {
				exameVO.setNomeUsualExamesMaterialAnalise(itemSolicitacaoExameHist.getAelUnfExecutaExames().getAelExamesMaterialAnalise().getNomeUsualMaterial());
			}
			if (itemSolicitacaoExameHist.getIntervaloColeta() != null) {
				exameVO.setNroColetasIntervaloColeta(itemSolicitacaoExameHist.getIntervaloColeta().getNroColetas());
				exameVO.setDescricaoIntervaloColeta(itemSolicitacaoExameHist.getIntervaloColeta().getDescricao());
			}
		} else {
			exameVO.setItemSolicitacaoExamesIdSeqp(itemSolicitacaoExame.getId().getSeqp());

			exameVO.setComplementoMotCanc(itemSolicitacaoExame.getComplementoMotCanc());
			exameVO.setDescMaterialAnalise(itemSolicitacaoExame.getDescMaterialAnalise());

			exameVO.setDthrProgramada(itemSolicitacaoExame.getDthrProgramada());
			exameVO.setIndUsoO2String(itemSolicitacaoExame.getIndUsoO2String());

			exameVO.setNroAmostras(itemSolicitacaoExame.getNroAmostras());
			exameVO.setIntervaloDiasHoras(itemSolicitacaoExame.getIntervaloDiasHoras());

			exameVO.setPrioridadeExecucao(itemSolicitacaoExame.getPrioridadeExecucao());
			exameVO.setTipoColeta(itemSolicitacaoExame.getTipoColeta());
			if (itemSolicitacaoExame.getAelMotivoCancelaExames() != null) {
				exameVO.setDescricaoMotivoCancelaExames(itemSolicitacaoExame.getAelMotivoCancelaExames().getDescricao());
			}
			if (itemSolicitacaoExame.getRegiaoAnatomica() != null) {
				exameVO.setDescricaoRegiaoAnatomica(itemSolicitacaoExame.getRegiaoAnatomica().getDescricao());
			}else if(itemSolicitacaoExame.getDescRegiaoAnatomica() != null && itemSolicitacaoExame.getDescRegiaoAnatomica().trim().length() != 0){
				exameVO.setDescricaoRegiaoAnatomica(itemSolicitacaoExame.getDescRegiaoAnatomica());
			}
			if (itemSolicitacaoExame.getSituacaoItemSolicitacao() != null) {
				exameVO.setDescricaoSituacaoItemSolicitacao(itemSolicitacaoExame.getSituacaoItemSolicitacao().getDescricao());
			}
			if (itemSolicitacaoExame.getTipoTransporte() != null) {
				exameVO.setDescricaoTipoTransporte(itemSolicitacaoExame.getTipoTransporte().getDescricao());
			}
			if (itemSolicitacaoExame.getAelUnfExecutaExames() != null && itemSolicitacaoExame.getAelUnfExecutaExames().getAelExamesMaterialAnalise() != null) {
				exameVO.setNomeUsualExamesMaterialAnalise(itemSolicitacaoExame.getAelUnfExecutaExames().getAelExamesMaterialAnalise().getNomeUsualMaterial());
			}
			if (itemSolicitacaoExame.getIntervaloColeta() != null) {
				exameVO.setNroColetasIntervaloColeta(itemSolicitacaoExame.getIntervaloColeta().getNroColetas());
				exameVO.setDescricaoIntervaloColeta(itemSolicitacaoExame.getIntervaloColeta().getDescricao());
			}

		}
	}

	@SuppressWarnings("PMD.NPathComplexity")
	private void populaPaciente(AelItemSolicitacaoExames itemSolicitacaoExame, AelItemSolicExameHist itemSolicitacaoExameHist) throws BaseException {
		pacienteVO = new DetalharItemSolicitacaoPacienteVO();
		Integer codPac = null;

		if (isHist) {
			AelSolicitacaoExamesHist solicitacaoExameHist = itemSolicitacaoExameHist.getSolicitacaoExame();
			pacienteVO.setProntuarioPaciente(examesFacade.buscarLaudoProntuarioPacienteHist(solicitacaoExameHist));
			codPac = examesFacade.buscarLaudoCodigoPacienteHist(solicitacaoExameHist);
			pacienteVO.setNomePaciente(examesFacade.buscarLaudoNomePacienteHist(solicitacaoExameHist));
		} else {
			AelSolicitacaoExames solicitacaoExame = itemSolicitacaoExame.getSolicitacaoExame();
			pacienteVO.setProntuarioPaciente(examesFacade.buscarLaudoProntuarioPaciente(solicitacaoExame));
			codPac = examesFacade.buscarLaudoCodigoPaciente(solicitacaoExame);
			pacienteVO.setNomePaciente(examesFacade.buscarLaudoNomePaciente(solicitacaoExame));
		}

		pacienteVO.setCodigoPaciente(codPac != null ? codPac.toString() : "");

		if (codPac != null) {
			AipPacientes paciente = pacienteFacade.obterPacientePorCodigoOuProntuario(null, codPac, null);
			pacienteVO.setSexoBiologico(paciente.getSexoBiologico());
			pacienteVO.setIdadeAnoMesFormat(paciente.getIdadeAnoMesFormat());
			pacienteVO.setDtNascimento(paciente.getDtNascimento());
		}

		DominioOrigemAtendimento origem = null;
		if (isHist) {
			origem = pesquisaExamesFacade.validaLaudoOrigemPacienteHist(itemSolicitacaoExameHist.getSolicitacaoExame());
		} else {
			origem = pesquisaExamesFacade.validaLaudoOrigemPaciente(itemSolicitacaoExame.getSolicitacaoExame());
		}
		if (origem != null) {
			pacienteVO.setOrigem(origem.getDescricao());
		}

		// observar durante testes se solicitação possui atendimento ou atendimento diverso.
		if (atendimento != null) {
			String local = prescricaoMedicaFacade.buscarResumoLocalPaciente(atendimento);
			pacienteVO.setLocal(local);
		}
		AacConsultas consulta = atendimento != null ? atendimento.getConsulta() : null;
		if (consulta != null) {
			pacienteVO.setConsulta(consulta.getNumero().toString());
		}
	}

	public String cancelar() {
		return "cancelado";
	}

	public Boolean disabledConsultadoPor() {
		return disabledConsultadoPor;
	}

	public List<AghUnidadesFuncionais> buscarUnidadeFuncionais(Object objPesquisa) {
		return solicitacaoExameFacade.buscarUnidadeFuncionais((String) objPesquisa);
	}

	/*
	 * Pesquisa exames itens solicitação consultados
	 * 
	 * @return
	 */
	public String pesquisaItensConsultados() {
		if (this.soeSeq != null && this.seqp != null) {
			if (isHist) {
				this.listaItemSolicConsultado = this.examesFacade.pesquisarAelItemSolicConsultadosResultadosExamesHist(this.soeSeq, this.seqp);
			} else {
				this.listaItemSolicConsultado = this.examesFacade.pesquisarAelItemSolicConsultadosResultadosExames(this.soeSeq, this.seqp);
			}
		}
		return null;
	}

	/**
	 * Determina a disponibilidade do botao de amostras
	 * 
	 * @return
	 */
	public boolean desahabilitarBotaoAmostras() {
		if (this.indColetavel != null && !this.indColetavel) {
			// Exames nao coletaveis nao possuem amostras, logo o botao de amostras deve estar desabilitado na tela
			return true;
		}
		return false;
	}

	public List<AelItemSolicConsultadoVO> getListaItemSolicConsultado() {
		return listaItemSolicConsultado;
	}

	public void setListaItemSolicConsultado(List<AelItemSolicConsultadoVO> listaItemSolicConsultado) {
		this.listaItemSolicConsultado = listaItemSolicConsultado;
	}

	/**
	 * Método chamado para o botão voltar
	 */
	public String voltar() {

		String retorno = this.voltarPara;

		this.soeSeq = null;
		this.seqp = null;
		this.voltarPara = null;
		this.currentTabIndex = null;
		this.isHist = Boolean.FALSE;
		this.origemPOL = Boolean.FALSE;
		this.disabledConsultadoPor = Boolean.TRUE;

		this.solicitacaoVO = null;
		this.pacienteVO = null;
		this.exameVO = null;

		this.listaItemSolicConsultado = null;
		this.pessoaFisica = null;
		this.pessoaFisicaResp = null;
		this.siglaConselhoProf = null;
		this.siglaConselhoProfResp = null;
		this.andarAlaDescricao = null;
		this.informacoesClinicas = null;
		this.indColetavel = null;

		return retorno;
	}

	public List<RapServidores> buscarServidoresSolicitacaoExame(Object objPesquisa) {
		return solicitacaoExameFacade.buscarServidoresSolicitacaoExame((String) objPesquisa);
	}

	public Integer getSoeSeq() {
		return soeSeq;
	}

	public void setSoeSeq(Integer seq) {
		this.soeSeq = seq;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	public DetalharItemSolicitacaoSolicitacaoVO getSolicitacaoVO() {
		return solicitacaoVO;
	}

	public void setSolicitacaoVO(DetalharItemSolicitacaoSolicitacaoVO solicitacaoVO) {
		this.solicitacaoVO = solicitacaoVO;
	}

	public DetalharItemSolicitacaoPacienteVO getPacienteVO() {
		return pacienteVO;
	}

	public void setPacienteVO(DetalharItemSolicitacaoPacienteVO pacienteVO) {
		this.pacienteVO = pacienteVO;
	}

	public DetalharItemSolicitacaoExameVO getExameVO() {
		return exameVO;
	}

	public void setExameVO(DetalharItemSolicitacaoExameVO exameVO) {
		this.exameVO = exameVO;
	}

	public String getPessoaFisica() {
		return pessoaFisica;
	}

	public String getPessoaFisicaResp() {
		return pessoaFisicaResp;
	}

	public String getSiglaConselhoProf() {
		return siglaConselhoProf;
	}

	public void setSiglaConselhoProf(String siglaConselhoProf) {
		this.siglaConselhoProf = siglaConselhoProf;
	}

	public String getSiglaConselhoProfResp() {
		return siglaConselhoProfResp;
	}

	public void setSiglaConselhoProfResp(String siglaConselhoProfResp) {
		this.siglaConselhoProfResp = siglaConselhoProfResp;
	}

	public String getAndarAlaDescricao() {
		return andarAlaDescricao;
	}

	public void setAndarAlaDescricao(String andarAlaDescricao) {
		this.andarAlaDescricao = andarAlaDescricao;
	}

	public String getInformacoesClinicas() {
		return informacoesClinicas;
	}

	public void setInformacoesClinicas(String informacoesClinicas) {
		this.informacoesClinicas = informacoesClinicas;
	}

	public void setPessoaFisica(String pessoaFisica) {
		this.pessoaFisica = pessoaFisica;
	}

	public void setPessoaFisicaResp(String pessoaFisicaResp) {
		this.pessoaFisicaResp = pessoaFisicaResp;
	}

	public Boolean getIndColetavel() {
		return indColetavel;
	}

	public void setIndColetavel(Boolean indColetavel) {
		this.indColetavel = indColetavel;
	}

	public void setCurrentTabIndex(Integer currentTabIndex) {
		this.currentTabIndex = currentTabIndex;
	}

	public Integer getCurrentTabIndex() {
		return currentTabIndex;
	}

	public void setIsHist(Boolean isHist) {
		this.isHist = isHist;
	}

	public Boolean getIsHist() {
		return isHist;
	}

	public Boolean getOrigemPOL() {
		return origemPOL;
	}

	public void setOrigemPOL(Boolean origemPOL) {
		this.origemPOL = origemPOL;
	}

	public Boolean getOrigemSolicDetalhamentoAmostras() {
		return origemSolicDetalhamentoAmostras;
	}

	public void setOrigemSolicDetalhamentoAmostras(
			Boolean origemSolicDetalhamentoAmostras) {
		this.origemSolicDetalhamentoAmostras = origemSolicDetalhamentoAmostras;
	}

}