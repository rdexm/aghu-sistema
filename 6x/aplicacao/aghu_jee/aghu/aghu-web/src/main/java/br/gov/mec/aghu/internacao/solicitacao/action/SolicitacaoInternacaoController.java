package br.gov.mec.aghu.internacao.solicitacao.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoSolicitacaoInternacao;
import br.gov.mec.aghu.faturamento.cadastrosapoio.business.IFaturamentoApoioFacade;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.solicitacao.business.ISolicitacaoInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.EspCrmVO;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AinAcomodacoes;
import br.gov.mec.aghu.model.AinSolicitacoesInternacao;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatVlrItemProcedHospComps;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;



public class SolicitacaoInternacaoController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2664185514252540967L;

	@EJB
	private ISolicitacaoInternacaoFacade solicitacaoInternacaoFacade;

	
	@EJB
	private IPacienteFacade pacienteFacade;

	@EJB
	private IFaturamentoApoioFacade faturamentoApoioFacade;

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	private AghEspecialidades especialidade = new AghEspecialidades();

	private AinSolicitacoesInternacao solicitacaoInternacao = new AinSolicitacoesInternacao();

	private AinSolicitacoesInternacao solicitacaoInternacaoTemp = new AinSolicitacoesInternacao();

	private AinAcomodacoes acomodacao = new AinAcomodacoes();

	private AipPacientes paciente = new AipPacientes();

	private Integer aipPacienteCodigo;

	private Integer solicitacaoInternacaoSeq;

	/**
	 * Plano de saude sendo vinculado ao usuário.
	 */
	private FatConvenioSaudePlano convenioSaudePlano;

	// resultado da busca por pk
	private EspCrmVO espCrmVO;

	// resultado da pesquisa na lista de valores
	private List<EspCrmVO> listaEspCrmVO = new ArrayList<EspCrmVO>();

	private DominioSimNao permissaoInternacao;

	private FatVlrItemProcedHospComps vlrItemProcedHospComp;

	private List<FatVlrItemProcedHospComps> listaFatVlrItemProcedHospComps = new ArrayList<FatVlrItemProcedHospComps>();

	private Boolean ocultarSituacaoSolicitacao;

	private Short convenioId;

	private Byte planoId;

	private String origemChamada;
	
	private final String PAG_PESQUISA_PACIENTE = "paciente-pesquisaPaciente";
	private final String PAG_CADASTRO_PACIENTE = "paciente-cadastroPaciente";
	private final String PAG_PACIENTESAGENDADOS = "ambulatorio-atenderPacientesAgendados";
	
	// TODO: ESTA STRING NAO EXISTE AINDA
	private final String PAG_LISTA_CIRURGIAS = "blocoCirugico-listarCirurgias";
	
	public enum Origens {
		CIRURGIAS
	} 
		
	@PostConstruct
	public void init() {
	   begin(conversation, true);
	}
	
	/**
	 * Pesquisa para lista de valores.
	 * @throws ApplicationBusinessException 
	 * 
	 * @throws AGHUNegocioException
	 */
	public List<EspCrmVO> pesquisarMedico(String descricao) throws ApplicationBusinessException  {
		if (solicitacaoInternacao.getEspecialidade() != null) {
			listaEspCrmVO = solicitacaoInternacaoFacade.pesquisarEspCrmVO(descricao, solicitacaoInternacao.getEspecialidade());
		} else {
			listaEspCrmVO = new ArrayList<EspCrmVO>();
		}
		return listaEspCrmVO;
	}

	/**
	 * Pesquisa para lista de valores.
	 * @throws ApplicationBusinessException 
	 * 
	 * @throws AGHUNegocioException
	 */
	public List<FatVlrItemProcedHospComps> pesquisarSsm(String descricaoSsm) throws ApplicationBusinessException {
		return solicitacaoInternacaoFacade.pesquisarFatVlrItemProcedHospComps(descricaoSsm, this.getPaciente(), null);
	}

	public void inicio() {
	 

		try {
			if (solicitacaoInternacaoSeq != null) { // Edição
				solicitacaoInternacao = solicitacaoInternacaoFacade.obterSolicitacaoInternacao(solicitacaoInternacaoSeq);
				this.armazenarSolicitacaoInternacao(solicitacaoInternacao);
				this.setConvenioSaudePlano(solicitacaoInternacao.getConvenio());
				this.setPlanoId(solicitacaoInternacao.getConvenio().getId().getSeq());
				this.setConvenioId(solicitacaoInternacao.getConvenio().getId().getCnvCodigo());
				this.setAcomodacao(solicitacaoInternacao.getAcomodacao());
				if (solicitacaoInternacao.getProcedimento() != null && solicitacaoInternacao.getProcedimento().getCodTabela() != null) {
					this.vlrItemProcedHospComp = new FatVlrItemProcedHospComps();
					this.vlrItemProcedHospComp.setFatItensProcedHospitalar(solicitacaoInternacao.getProcedimento());
				}
				EspCrmVO espCrmVO = solicitacaoInternacaoFacade.obterCrmENomeMedicoPorServidor(this.getSolicitacaoInternacao()
						.getServidor(), this.solicitacaoInternacao.getEspecialidade());
				this.setEspCrmVO(espCrmVO);
				this.setPaciente(solicitacaoInternacao.getPaciente());
				this.ocultarSituacaoSolicitacao = false;

			}
			if (aipPacienteCodigo != null && solicitacaoInternacaoSeq == null) {
				paciente = pacienteFacade.obterPacientePorCodigo(this.getAipPacienteCodigo());
				solicitacaoInternacao.setPaciente(paciente);
				if (solicitacaoInternacao.getIndSitSolicInternacao() == null) {
					solicitacaoInternacao.setIndSitSolicInternacao(DominioSituacaoSolicitacaoInternacao.P);
					this.ocultarSituacaoSolicitacao = true;
				}
				FatConvenioSaudePlano convenioPlano = null;
				if (this.getConvenioSaudePlano() == null) {
					convenioPlano = faturamentoApoioFacade.obterPlanoPorId(Byte.valueOf("1"), Short.valueOf("1"));
					this.setConvenioSaudePlano(convenioPlano);
				}
				this.setPlanoId(Byte.valueOf("1"));
				this.setConvenioId(Short.valueOf("1"));
				this.setPermissaoInternacao(DominioSimNao.S);
				this.setAcomodacao(null);
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	
	}

	public void armazenarSolicitacaoInternacao(AinSolicitacoesInternacao solicitacaoInternacao) {
		solicitacaoInternacaoTemp.setAcomodacao(solicitacaoInternacao.getAcomodacao());
		solicitacaoInternacaoTemp.setConvenio(solicitacaoInternacao.getConvenio());
		solicitacaoInternacaoTemp.setCriadoEm(solicitacaoInternacao.getCriadoEm());
		solicitacaoInternacaoTemp.setDthrAtendimentoSolicitacao(solicitacaoInternacao.getDthrAtendimentoSolicitacao());
		solicitacaoInternacaoTemp.setDtPrevInternacao(solicitacaoInternacao.getDtPrevInternacao());
		solicitacaoInternacaoTemp.setEspecialidade(solicitacaoInternacao.getEspecialidade());
		solicitacaoInternacaoTemp.setIndSitSolicInternacao(solicitacaoInternacao.getIndSitSolicInternacao());
		solicitacaoInternacaoTemp.setLeito(solicitacaoInternacao.getLeito());
		solicitacaoInternacaoTemp.setObservacao(solicitacaoInternacao.getObservacao());
		solicitacaoInternacaoTemp.setPaciente(solicitacaoInternacao.getPaciente());
		solicitacaoInternacaoTemp.setProcedimento(solicitacaoInternacao.getProcedimento());
		solicitacaoInternacaoTemp.setQuarto(solicitacaoInternacao.getQuarto());
		solicitacaoInternacaoTemp.setSeq(solicitacaoInternacao.getSeq());
		solicitacaoInternacaoTemp.setServidor(solicitacaoInternacao.getServidor());
		solicitacaoInternacaoTemp.setServidorDigitador(solicitacaoInternacao.getServidorDigitador());
		solicitacaoInternacaoTemp.setUnidadeFuncional(solicitacaoInternacao.getUnidadeFuncional());
	}

	public String salvar() {
		try {
			if (this.getEspCrmVO() != null && this.getEspCrmVO().getMatricula() != null && this.getEspCrmVO().getVinCodigo() != null) {
				RapServidores servidor = registroColaboradorFacade
						.obterServidor(this.getEspCrmVO().getVinCodigo(), this
								.getEspCrmVO().getMatricula());
				solicitacaoInternacao.setServidor(servidor);
			}
			if (this.getEspCrmVO() == null) {
				solicitacaoInternacao.setServidor(null);
			}
			solicitacaoInternacao.setConvenio(convenioSaudePlano);
			solicitacaoInternacao.setAcomodacao(this.getAcomodacao());
			if (this.getVlrItemProcedHospComp() != null
					&& this.getVlrItemProcedHospComp().getFatItensProcedHospitalar().getCodTabela() != null) {
				solicitacaoInternacao.setProcedimento(this.getVlrItemProcedHospComp().getFatItensProcedHospitalar());
			}
			if ((solicitacaoInternacao.getEspecialidade() != null && !StringUtils.isBlank(solicitacaoInternacao.getEspecialidade()
					.getNomeEspecialidade()))
					&& (this.getEspCrmVO() != null && !StringUtils.isBlank(this.getEspCrmVO().getNomeMedico()))) {
				solicitacaoInternacaoFacade.validarCrmEspecialidade(this.getEspCrmVO().getNomeMedico(),
						solicitacaoInternacao.getEspecialidade());
			}
			
			solicitacaoInternacaoFacade.persistirSolicitacaoInternacao(solicitacaoInternacao, solicitacaoInternacaoTemp, false);
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_PERSISTIR_SOLICITACAO_INTERNACAO",
					this.solicitacaoInternacao.getSeq());
			this.setAcomodacao(null);
			this.setEspecialidade(null);
			this.setEspCrmVO(null);
			this.setVlrItemProcedHospComp(null);
			this.setListaEspCrmVO(null);
			this.setListaFatVlrItemProcedHospComps(null);
			this.setPlanoId(null);
			this.setConvenioId(null);
			solicitacaoInternacao = new AinSolicitacoesInternacao();

			return processarRetornoSalvar();
		} catch (ApplicationBusinessException e) {			
			apresentarExcecaoNegocio(e);
			return null;
		}
	}

	private String processarRetornoSalvar() {
		if(Origens.CIRURGIAS.name().equals(origemChamada)){
			return PAG_LISTA_CIRURGIAS;
		}else if("atenderPacientesAgendados".equals(this.origemChamada)){
			return PAG_PACIENTESAGENDADOS;
 		}
		return PAG_PESQUISA_PACIENTE;
	}
	
	private String processarRetornoCancelar() {
		if (Origens.CIRURGIAS.name().equals(origemChamada)) {
			return PAG_LISTA_CIRURGIAS;
		} else if ("cadastroPaciente".equals(origemChamada)) {
			return PAG_CADASTRO_PACIENTE;
		} else if("atenderPacientesAgendados".equals(this.origemChamada)){
			return PAG_PACIENTESAGENDADOS;
 		}
		return PAG_PESQUISA_PACIENTE;
	}

	public List<AghEspecialidades> pesquisarEspecialidade(String objParam) {
		List<AghEspecialidades> retorno;

		String strPesquisa = (String) objParam;
		retorno = cadastrosBasicosInternacaoFacade.pesquisarEspecialidadeSolicitacaoInternacao(strPesquisa, paciente.getIdade()
				.shortValue());

		return retorno;

	}

	public String cancelar() {
		this.setAcomodacao(null);
		this.setEspecialidade(null);
		this.setEspCrmVO(null);
		this.setVlrItemProcedHospComp(null);
		this.setListaEspCrmVO(null);
		this.setListaFatVlrItemProcedHospComps(null);
		this.setConvenioId(null);
		this.setPlanoId(null);
		solicitacaoInternacao = new AinSolicitacoesInternacao();
		return processarRetornoCancelar();
	}

	/**
	 * @return the solicitacaoInternacao
	 */
	public AinSolicitacoesInternacao getSolicitacaoInternacao() {
		return solicitacaoInternacao;
	}

	/**
	 * @param solicitacaoInternacao
	 *            the solicitacaoInternacao to set
	 */
	public void setSolicitacaoInternacao(AinSolicitacoesInternacao solicitacaoInternacao) {
		this.solicitacaoInternacao = solicitacaoInternacao;
	}

	/**
	 * @return the paciente
	 */
	public AipPacientes getPaciente() {
		return paciente;
	}

	/**
	 * @param paciente
	 *            the paciente to set
	 */
	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	/**
	 * @return the especialidade
	 */
	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	/**
	 * @param especialidade
	 *            the especialidade to set
	 */
	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

	public List<AghEspecialidades> listarEspecialidadeAtualizaSolicitacaoInternacao(Object paramPesquisa) {
		return aghuFacade.listarEspecialidadeAtualizaSolicitacaoInternacao(paramPesquisa, paciente.getIdade());
	}

	public List<AghEspecialidades> listarPermitemConsultoriaPorSigla(Object paramPesquisa) {
		return aghuFacade.listarPermitemConsultoriaPorSigla(paramPesquisa);
	}

	public String getNomeEspecialidadeSelecionada() {

		if (this.solicitacaoInternacao.getEspecialidade() == null
				|| StringUtils.isBlank(this.solicitacaoInternacao.getEspecialidade().getNomeEspecialidade())) {
			return "";
		}
		return this.solicitacaoInternacao.getEspecialidade().getNomeEspecialidade();

	}

	public void atribuirEspecialidade() {
		if (solicitacaoInternacao.getEspecialidade() != null) {
			this.atribuirEspecialidade(solicitacaoInternacao.getEspecialidade());
		}
	}

	public boolean isMostrarLinkExcluirEspecialidade() {
		return this.solicitacaoInternacao != null && this.solicitacaoInternacao.getEspecialidade() != null;
	}

	public void limparEspecialidade() {
		this.solicitacaoInternacao.setEspecialidade(null);
	}

	public void atribuirEspecialidade(AghEspecialidades especialidade) {
		this.solicitacaoInternacao.setEspecialidade(especialidade);
	}

	/**
	 * @return the acomodacao
	 */
	public AinAcomodacoes getAcomodacao() {
		return acomodacao;
	}

	/**
	 * @param acomodacao
	 *            the acomodacao to set
	 */
	public void setAcomodacao(AinAcomodacoes acomodacao) {
		this.acomodacao = acomodacao;
	}

	public List<AinAcomodacoes> pesquisarAcomodacoes(String parametro) {
		return this.cadastrosBasicosInternacaoFacade.pesquisarAcomodacoesPorCodigoOuDescricaoOrdenado(parametro);
	}

	public List<FatConvenioSaudePlano> pesquisarConvenioSaudePlanos(String parametro) {
		return this.faturamentoApoioFacade.pesquisarConvenioSaudePlanosSolicitacaoInternacao((String) parametro);
	}

	public void atribuirAcomodacao(AinAcomodacoes acomodacao) {
		this.acomodacao = acomodacao;
	}

	/**
	 * @return the convenioSaudePlano
	 */
	public FatConvenioSaudePlano getConvenioSaudePlano() {
		return convenioSaudePlano;
	}

	/**
	 * @param convenioSaudePlano
	 *            the convenioSaudePlano to set
	 */
	public void setConvenioSaudePlano(FatConvenioSaudePlano convenioSaudePlano) {
		this.convenioSaudePlano = convenioSaudePlano;
	}

	public void atribuirPlano(FatConvenioSaudePlano plano) {
		if (plano != null) {
			this.convenioSaudePlano = plano;
			this.convenioId = plano.getConvenioSaude().getCodigo();
			this.planoId = plano.getId().getSeq();
		} else {
			this.convenioSaudePlano = null;
			this.convenioId = null;
			this.planoId = null;
		}
	}

	public void atribuirPlano() {
		if (this.convenioSaudePlano != null) {
			this.convenioId = this.convenioSaudePlano.getConvenioSaude().getCodigo();
			this.planoId = this.convenioSaudePlano.getId().getSeq();
		} else {
			this.convenioSaudePlano = null;
			this.convenioId = null;
			this.planoId = null;
		}
	}

	/**
	 * @return the listaEspCrmVO
	 */
	public List<EspCrmVO> getListaEspCrmVO() {
		return listaEspCrmVO;
	}

	/**
	 * @param listaEspCrmVO
	 *            the listaEspCrmVO to set
	 */
	public void setListaEspCrmVO(List<EspCrmVO> listaEspCrmVO) {
		this.listaEspCrmVO = listaEspCrmVO;
	}

	/**
	 * @return the aipPacienteCodigo
	 */
	public Integer getAipPacienteCodigo() {
		return aipPacienteCodigo;
	}

	/**
	 * @param aipPacienteCodigo
	 *            the aipPacienteCodigo to set
	 */
	public void setAipPacienteCodigo(Integer aipPacienteCodigo) {
		this.aipPacienteCodigo = aipPacienteCodigo;
	}

	/**
	 * @return the solicitacaoInternacaoSeq
	 */
	public Integer getSolicitacaoInternacaoSeq() {
		return solicitacaoInternacaoSeq;
	}

	/**
	 * @param solicitacaoInternacaoSeq
	 *            the solicitacaoInternacaoSeq to set
	 */
	public void setSolicitacaoInternacaoSeq(Integer solicitacaoInternacaoSeq) {
		this.solicitacaoInternacaoSeq = solicitacaoInternacaoSeq;
	}

	/**
	 * @return the permissaoInternacao
	 */
	public DominioSimNao getPermissaoInternacao() {
		return permissaoInternacao;
	}

	/**
	 * @param permissaoInternacao
	 *            the permissaoInternacao to set
	 */
	public void setPermissaoInternacao(DominioSimNao permissaoInternacao) {
		this.permissaoInternacao = permissaoInternacao;
	}

	/**
	 * @return the vlrItemProcedHospComp
	 */
	public FatVlrItemProcedHospComps getVlrItemProcedHospComp() {
		return vlrItemProcedHospComp;
	}

	/**
	 * @param vlrItemProcedHospComp
	 *            the vlrItemProcedHospComp to set
	 */
	public void setVlrItemProcedHospComp(FatVlrItemProcedHospComps vlrItemProcedHospComp) {
		this.vlrItemProcedHospComp = vlrItemProcedHospComp;
	}

	/**
	 * @return the listaFatVlrItemProcedHospComps
	 */
	public List<FatVlrItemProcedHospComps> getListaFatVlrItemProcedHospComps() {
		return listaFatVlrItemProcedHospComps;
	}

	/**
	 * @param listaFatVlrItemProcedHospComps
	 *            the listaFatVlrItemProcedHospComps to set
	 */
	public void setListaFatVlrItemProcedHospComps(List<FatVlrItemProcedHospComps> listaFatVlrItemProcedHospComps) {
		this.listaFatVlrItemProcedHospComps = listaFatVlrItemProcedHospComps;
	}

	/**
	 * @return the espCrmVO
	 */
	public EspCrmVO getEspCrmVO() {
		return espCrmVO;
	}

	/**
	 * @param espCrmVO
	 *            the espCrmVO to set
	 */
	public void setEspCrmVO(EspCrmVO espCrmVO) {
		this.espCrmVO = espCrmVO;
	}

	/**
	 * @return the ocultarSituacaoSolicitacao
	 */
	public Boolean getOcultarSituacaoSolicitacao() {
		return ocultarSituacaoSolicitacao;
	}

	/**
	 * @param ocultarSituacaoSolicitacao
	 *            the ocultarSituacaoSolicitacao to set
	 */
	public void setOcultarSituacaoSolicitacao(Boolean ocultarSituacaoSolicitacao) {
		this.ocultarSituacaoSolicitacao = ocultarSituacaoSolicitacao;
	}

	/**
	 * @return the solicitacaoInternacaoTemp
	 */
	public AinSolicitacoesInternacao getSolicitacaoInternacaoTemp() {
		return solicitacaoInternacaoTemp;
	}

	/**
	 * @param solicitacaoInternacaoTemp
	 *            the solicitacaoInternacaoTemp to set
	 */
	public void setSolicitacaoInternacaoTemp(AinSolicitacoesInternacao solicitacaoInternacaoTemp) {
		this.solicitacaoInternacaoTemp = solicitacaoInternacaoTemp;
	}

	/**
	 * 
	 */
	public void escolherPlanoConvenio() {
		if (this.planoId != null && this.convenioId != null) {
			FatConvenioSaudePlano plano = this.faturamentoApoioFacade.obterPlanoPorIdSolicitacaoInternacao(this.planoId,
					this.convenioId);
			if (plano == null) {
				apresentarMsgNegocio(Severity.WARN, "MENSAGEM_CONVENIO_PLANO_NAO_ENCONTRADO",
						this.convenioId, this.planoId);
			}
			this.atribuirPlano(plano);
		}
	}

	/**
	 * @return the convenioId
	 */
	public Short getConvenioId() {
		return convenioId;
	}

	/**
	 * @param convenioId
	 *            the convenioId to set
	 */
	public void setConvenioId(Short convenioId) {
		this.convenioId = convenioId;
	}

	/**
	 * @return the planoId
	 */
	public Byte getPlanoId() {
		return planoId;
	}

	/**
	 * @param planoId
	 *            the planoId to set
	 */
	public void setPlanoId(Byte planoId) {
		this.planoId = planoId;
	}

	public String getOrigemChamada() {
		return origemChamada;
	}

	public void setOrigemChamada(String origemChamada) {
		this.origemChamada = origemChamada;
	}

}