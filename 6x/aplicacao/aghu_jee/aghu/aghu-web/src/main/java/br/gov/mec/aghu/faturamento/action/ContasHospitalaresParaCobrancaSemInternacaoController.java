package br.gov.mec.aghu.faturamento.action;

import java.net.UnknownHostException;
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
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioAtivoCancelado;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.vo.VFatProfRespDcsVO;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinTiposCaraterInternacao;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatContasInternacao;
import br.gov.mec.aghu.model.FatDadosContaSemInt;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.vo.CodPacienteFoneticaVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.business.SelectionQualifier;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class ContasHospitalaresParaCobrancaSemInternacaoController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(ContasHospitalaresParaCobrancaSemInternacaoController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -1041113215669081994L;

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private IPacienteFacade pacienteFacade;

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IInternacaoFacade internacaoFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@Inject
	@SelectionQualifier
	private Instance<CodPacienteFoneticaVO> codPacienteFonetica;

	private AghEspecialidades especialidade;

	private AghUnidadesFuncionais unidadeFuncional;

	private FatDadosContaSemInt fatDadosContaSemInt;

	private AinTiposCaraterInternacao ainTiposCaraterInternacao;

	private VFatProfRespDcsVO profissionalResponsavel;

	private Integer cthSeq;

	private Integer seq;

	private Integer prontuario;

	private Integer pacCodigo;

	private String nome;

	private String[] sigla;

	public void inicio() {

		if (pacCodigo == null || pacCodigo < 0) {
			CodPacienteFoneticaVO codPac = codPacienteFonetica.get();
			if (codPac != null && codPac.getCodigo() > 0) {
				pacCodigo = codPac.getCodigo();
			}
		}

		if (seq == null) {
			fatDadosContaSemInt = new FatDadosContaSemInt();
			fatDadosContaSemInt.setIndSituacao(DominioAtivoCancelado.A);

			if (pacCodigo != null) {
				fatDadosContaSemInt.setPaciente(pacienteFacade.buscaPaciente(pacCodigo));
				prontuario = fatDadosContaSemInt.getPaciente().getProntuario();
				nome = fatDadosContaSemInt.getPaciente().getNome();
			}
			unidadeFuncional = null;

		} else if (seq != null) {
			fatDadosContaSemInt = faturamentoFacade.obterFatDadosContaSemInt(seq);
			unidadeFuncional = fatDadosContaSemInt.getUnf();
			ainTiposCaraterInternacao = fatDadosContaSemInt.getTipoCaraterInternacao();
			createSigla();

			if (fatDadosContaSemInt.getPaciente() != null) {
				prontuario = fatDadosContaSemInt.getPaciente().getProntuario();
				pacCodigo = fatDadosContaSemInt.getPaciente().getCodigo();
				nome = fatDadosContaSemInt.getPaciente().getNome();
			}

			Object filtros[] = { fatDadosContaSemInt.getSerVinCodigo(), fatDadosContaSemInt.getSerMatricula() };
			final List<VFatProfRespDcsVO> result = registroColaboradorFacade.pesquisarVFatProfRespDcsVO(sigla, filtros, true);

			if (result != null && !result.isEmpty()) {
				profissionalResponsavel = result.get(0);
			}

			final FatContasInternacao fci = faturamentoFacade.obterContaInternacaoEmFatDadosContaSemInt(fatDadosContaSemInt.getSeq(),
					cthSeq);
			if (fci != null) {
				especialidade = fci.getContaHospitalar().getEspecialidade();
			}

		} else if (fatDadosContaSemInt == null) {
			fatDadosContaSemInt = new FatDadosContaSemInt();
			fatDadosContaSemInt.setIndSituacao(DominioAtivoCancelado.A);

			if (pacCodigo != null) {
				fatDadosContaSemInt.setPaciente(pacienteFacade.buscaPaciente(pacCodigo));
			}
			unidadeFuncional = null;
		}

	
	}

	public String voltar() {
		return "pesquisarContasHospitalaresParaCobrancaSemInternacao";
	}

	public String redirecionarPesquisaFonetica() {
		pacCodigo = null;
		return "paciente-pesquisaPacienteComponente";
	}

	public void confirmar() {
		try {
			fatDadosContaSemInt.setUnf(unidadeFuncional);
			fatDadosContaSemInt.setTipoCaraterInternacao(ainTiposCaraterInternacao);
			fatDadosContaSemInt.setSerVinCodigo(profissionalResponsavel.getSerVinCodigo());
			fatDadosContaSemInt.setSerMatricula(profissionalResponsavel.getSerMatricula());
			fatDadosContaSemInt.setPacCodigo(pacCodigo);

			if (fatDadosContaSemInt.getPaciente() == null) {
				fatDadosContaSemInt.setPaciente(pacienteFacade.buscaPaciente(pacCodigo));
			}

			fatDadosContaSemInt.setCthSeq(cthSeq);

			if (especialidade != null) {
				fatDadosContaSemInt.setEspSeq(especialidade.getSeq());
			}

			if (unidadeFuncional != null) {
				fatDadosContaSemInt.setUnfSeq(unidadeFuncional.getSeq());
			}

			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e) {
				LOG.error("Exceção caputada:", e);
			}

			Date dt = new Date();
			if (cthSeq == null) {
				faturamentoFacade.inserirFatDadosContaSemInt(fatDadosContaSemInt, nomeMicrocomputador, dt);
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INSERCAO_FAT_DADOS_CONTA_SEM_INTERNACAO");
				cthSeq = fatDadosContaSemInt.getCthSeq();

			} else {
				faturamentoFacade.atualizarFatDadosContaSemInt(fatDadosContaSemInt, nomeMicrocomputador, new Date());
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ATUALIZACAO_FAT_DADOS_CONTA_SEM_INTERNACAO");
			}

			seq = fatDadosContaSemInt.getSeq();
			fatDadosContaSemInt = null;
			// super.reiniciarPaginator(PesquisarContasHospitalaresParaCobrancaSemInternacaoPaginatorController.class);
			// this.getPersistenceContext().clear();
			inicio();

		} catch (BaseException e) {
			final AipPacientes p = fatDadosContaSemInt.getPaciente();
			final DominioAtivoCancelado dom = fatDadosContaSemInt.getIndSituacao();
			final Date dtInicial = fatDadosContaSemInt.getDataInicial();
			final Date dtFinal = fatDadosContaSemInt.getDataFinal();

			if (cthSeq == null) {
				fatDadosContaSemInt = new FatDadosContaSemInt();
				fatDadosContaSemInt.setSeq(null);
			} else {
				fatDadosContaSemInt = faturamentoFacade.obterFatDadosContaSemInt(seq);
			}

			fatDadosContaSemInt.setPaciente(p);
			fatDadosContaSemInt.setIndSituacao(dom);
			fatDadosContaSemInt.setDataInicial(dtInicial);
			fatDadosContaSemInt.setDataFinal(dtFinal);

			apresentarExcecaoNegocio(e);
		}
	}

	public void limparCampos() {
		especialidade = null;
		unidadeFuncional = null;
		fatDadosContaSemInt = null;
		ainTiposCaraterInternacao = null;
		profissionalResponsavel = null;
		cthSeq = null;
		seq = null;
		sigla = null;
	}

	public List<AinTiposCaraterInternacao> pesquisarAinTiposCaraterInternacaoPorTodosOsCampos(final String filtros) {
		return  this.returnSGWithCount(internacaoFacade.pesquisarAinTiposCaraterInternacaoPorTodosOsCampos(filtros),pesquisarAinTiposCaraterInternacaoPorTodosOsCamposCount(filtros));
	}

	public Long pesquisarAinTiposCaraterInternacaoPorTodosOsCamposCount(final String filtros) {
		return internacaoFacade.pesquisarAinTiposCaraterInternacaoPorTodosOsCamposCount(filtros);
	}

	public List<VFatProfRespDcsVO> pesquisarVFatProfRespDcsVO(final String filtros) {
		createSigla();
		return this.returnSGWithCount(registroColaboradorFacade.pesquisarVFatProfRespDcsVO(sigla, filtros, false),pesquisarVFatProfRespDcsVOCount(filtros));
	}

	public Long pesquisarVFatProfRespDcsVOCount(final String filtros) {
		return registroColaboradorFacade.pesquisarVFatProfRespDcsVOCount(sigla, filtros);
	}

	public List<AghEspecialidades> listarEspecialidades(final String strPesquisa) {
		return  this.returnSGWithCount(this.aghuFacade.pesquisarPorNomeSiglaInternaUnidade((String) strPesquisa),listarEspecialidadesCount(strPesquisa));
	}

	public Long listarEspecialidadesCount(final String strPesquisa) {
		return this.aghuFacade.pesquisarPorNomeSiglaInternaUnidadeCount((String) strPesquisa);
	}

	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncional(final String strPesquisa) {
		return  this.returnSGWithCount(this.aghuFacade.pesquisarUnidadeFuncionalPorCodigoEDescricaoAtivaseInativas(strPesquisa),pesquisarUnidadeFuncionalCount(strPesquisa));
	}

	public Long pesquisarUnidadeFuncionalCount(final String strPesquisa) {
		return aghuFacade.pesquisarUnidadeFuncionalPorCodigoEDescricaoAtivaseInativasCount(strPesquisa);
	}

	private void createSigla() {
		try {
			if (sigla == null) {
				sigla = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_FAT_SIGLAS_CONSELHOS_PROFISSIONAIS).getVlrTexto()
						.split(",");
			}

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public List<AipPacientes> pesquisarAipPacientesPorCodigoOuProntuario(final Object filtro) {
		return pacienteFacade.pesquisarAipPacientesPorCodigoOuProntuario(filtro);
	}

	public Long pesquisarAipPacientesPorCodigoOuProntuarioCount(final Object filtro) {
		return pacienteFacade.pesquisarAipPacientesPorCodigoOuProntuarioCount(filtro);
	}

	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public FatDadosContaSemInt getFatDadosContaSemInt() {
		return fatDadosContaSemInt;
	}

	public void setFatDadosContaSemInt(FatDadosContaSemInt fatDadosContaSemInt) {
		this.fatDadosContaSemInt = fatDadosContaSemInt;
	}

	public Integer getCthSeq() {
		return cthSeq;
	}

	public void setCthSeq(Integer cthSeq) {
		this.cthSeq = cthSeq;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public AinTiposCaraterInternacao getAinTiposCaraterInternacao() {
		return ainTiposCaraterInternacao;
	}

	public void setAinTiposCaraterInternacao(AinTiposCaraterInternacao ainTiposCaraterInternacao) {
		this.ainTiposCaraterInternacao = ainTiposCaraterInternacao;
	}

	public VFatProfRespDcsVO getProfissionalResponsavel() {
		return profissionalResponsavel;
	}

	public void setProfissionalResponsavel(VFatProfRespDcsVO profissionalResponsavel) {
		this.profissionalResponsavel = profissionalResponsavel;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
}