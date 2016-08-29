package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.HistoricoAlteracaoAlocacaoSalaVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.HistoricoAlteracaoCaractSalaVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioDiaSemana;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MbcCaractSalaEsp;
import br.gov.mec.aghu.model.MbcCaractSalaEspId;
import br.gov.mec.aghu.model.MbcCaracteristicaSalaCirg;
import br.gov.mec.aghu.model.MbcHorarioTurnoCirg;
import br.gov.mec.aghu.model.MbcMvtoCaractSalaCirg;
import br.gov.mec.aghu.model.MbcMvtoSalaEspEquipe;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgsId;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.model.MbcTurnos;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VMbcProfServidor;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;


@SuppressWarnings({"PMD.NPathComplexity"})
public class MapeamentoSalasController extends ActionController {

    private static final String _HIFEN_ = " - ";

    private static final String POR = ", por: ";

    private static final String PESSOA_FISISCA_NAO_ENCONTRADA = "Pessoa Fisisca Nao Encontrada";



    @PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

    private static final String ESCALA_SALAS = "blococirurgico-consultaEscalaSalas";
	private static final Log LOG = LogFactory.getLog(MapeamentoSalasController.class);
    private static final String MAPEAMENTO_SALAS = "blococirurgico-manterMapeamentoSalas";

	/**
	 * @author fpalma
	 */
	private static final long serialVersionUID = -7212047305327690183L;

	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private IAghuFacade aghuFacade;

	//Parâmetros recebidos da estória 23491
	private Short unfSeq;
	private Short seqp;

	//Atributos do slider de mapeamento
	private List<MbcCaracteristicaSalaCirg> listaMapeamentoSala;
	private String sala;
	private String unidadeCirurgica;
	private Short codigo;
	private DominioDiaSemana diaSemana;
	private MbcHorarioTurnoCirg turno;
	private Boolean cirurgiaParticular;
	private Boolean urgencia;
	private Boolean instalada;
	private Boolean disponivel;
	private Boolean modoEdicao = false;
	private Boolean modoVisualizacaoDisp = false;

	//Atributos do slider de disponibilidade
	MbcCaracteristicaSalaCirg caracteristicaSalaCirg;
	private List<MbcCaractSalaEsp> listaDisponibilidade;
	private AghEspecialidades especialidade;
	private VMbcProfServidor equipe;
	private DominioDiaSemana diaSemanaDisp;
	private Short reserva;
	private Date horaInicial;
	private Date horaFinal;
	private Boolean ativo;
	private Boolean modoEdicaoDisp = false;
	private MbcCaractSalaEspId mbcCaractId;

	private MbcCaracteristicaSalaCirg itemSelecionado;

	/**
	 * Variaveis modal historico alteracoes caracteristica salas
	 */

	private String tituloModalHistoricoAlteracoesCaractSalas;

	private List<HistoricoAlteracaoCaractSalaVO> listaHistoricoAlteracoesCaractSalasVO;

	private MbcSalaCirurgica salaCirurgicaSelecionada;
	private MbcCaracteristicaSalaCirg caracteristicaSalaCirgSelecionada;

	/**
	 * Variaveis modal historico alteracoes alocacao salas
	 */

	private String tituloModalHistoricoAlteracoesAlocacaoSalas;

	private List<HistoricoAlteracaoAlocacaoSalaVO> listaHistoricoAlteracoesAlocacaoSalasVO;
	private MbcCaractSalaEsp caractSalaEspSelecionada;

	private final String PAGE_LIST_SALA_CIRURGICA = "salaCirurgicaList";



	public void iniciar() {




		salaCirurgicaSelecionada = blocoCirurgicoCadastroApoioFacade.obterSalaCirurgicaBySalaCirurgicaId(seqp, unfSeq);
		carregarCamposIniciais();
		pesquisarMapeamento();

	}


	private void pesquisarMapeamento() {
		listaMapeamentoSala = blocoCirurgicoCadastroApoioFacade.buscarHorariosCaractPorSalaCirurgica(unfSeq, seqp);
	}

	private void carregarCamposIniciais() {
		sala = seqp.toString() + _HIFEN_ + salaCirurgicaSelecionada.getNome();
		unidadeCirurgica = salaCirurgicaSelecionada.getUnidadeFuncional().getSeq().toString() +
                _HIFEN_ + this.aghuFacade.obterUnidadeFuncional(salaCirurgicaSelecionada.getUnidadeFuncional().getSeq()).getDescricao();
		itemSelecionado = null;
		listaDisponibilidade = null;
		setarCamposCheckBox();
		cancelarEdicao();
		cancelarEdicaoDisp();
	}

	public void gravarMapeamento() {
		MbcCaracteristicaSalaCirg caracteristicaSalaCirg = popularCaracteristicaSalaCirg();

		try {

			blocoCirurgicoCadastroApoioFacade.gravarMbcCaracteristicaSalaCirg(caracteristicaSalaCirg);
			if(modoEdicao) {
				this.apresentarMsgNegocio(Severity.INFO, "TITLE_MAPEAMENTO_SALAS_SUCESSO_ALTERADO_CARACT");
			} else {
				this.apresentarMsgNegocio(Severity.INFO, "TITLE_MAPEAMENTO_SALAS_SUCESSO_INCLUSAO_CARACT");
			}
			cancelarEdicao();
			pesquisarMapeamento();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	private MbcCaracteristicaSalaCirg popularCaracteristicaSalaCirg() {
		MbcCaracteristicaSalaCirg caracteristicaSalaCirg;
		if(modoEdicao) {
			caracteristicaSalaCirg = blocoCirurgicoCadastroApoioFacade.obterCaracteristicaSalaCirgPorPK(codigo);
		} else {
			caracteristicaSalaCirg = new MbcCaracteristicaSalaCirg();
		}
		caracteristicaSalaCirg.setDiaSemana(diaSemana);
		caracteristicaSalaCirg.setMbcHorarioTurnoCirg(turno);
		caracteristicaSalaCirg.setCirurgiaParticular(cirurgiaParticular);
		caracteristicaSalaCirg.setIndUrgencia(urgencia);
		caracteristicaSalaCirg.setSituacao(instalada ? DominioSituacao.A : DominioSituacao.I);
		caracteristicaSalaCirg.setIndDisponivel(disponivel);
		caracteristicaSalaCirg.setMbcSalaCirurgica(salaCirurgicaSelecionada);

		return caracteristicaSalaCirg;
	}

    public String abreEscalaSalas() {
        return ESCALA_SALAS;
    }

	private void setarCamposCheckBox(){
		this.setCirurgiaParticular(false);
		this.setUrgencia(false);
		this.setInstalada(false);
		this.setDisponivel(false);
	}

	public void cancelarEdicao() {
		codigo = null;
		diaSemana = null;
		turno = null;
		setarCamposCheckBox();
		modoEdicao = false;
		modoVisualizacaoDisp = false;
		modoEdicaoDisp = false;
		itemSelecionado = null;
		listaDisponibilidade = null;
	}

	public void selecionar(MbcCaracteristicaSalaCirg item) {
		itemSelecionado = item;
		codigo = item.getSeq();
		diaSemanaDisp = item.getDiaSemana();
		modoVisualizacaoDisp = false;
		modoEdicaoDisp = false;
		listaDisponibilidade = blocoCirurgicoCadastroApoioFacade.pesquisarCaractSalaEspPorCaracteristica(item.getSeq(), null);
		caracteristicaSalaCirg = item;
	}
	
	public void visualizarDisponibilidade() {
		if(!modoEdicao) {
			cancelarEdicaoDisp();
			codigo = itemSelecionado.getSeq();
			diaSemanaDisp = itemSelecionado.getDiaSemana();
			modoVisualizacaoDisp = true;
			ativo = true;
			listaDisponibilidade = blocoCirurgicoCadastroApoioFacade.pesquisarCaractSalaEspPorCaracteristica(itemSelecionado.getSeq(), DominioSituacao.A);
		}
	}

	public void editar(MbcCaracteristicaSalaCirg item) {
		itemSelecionado = item;
		visualizarDisponibilidade();
		diaSemana = item.getDiaSemana();
		turno = item.getMbcHorarioTurnoCirg();
		cirurgiaParticular = item.getCirurgiaParticular();
		urgencia = item.getIndUrgencia();
		instalada = item.getSituacao().isAtivo();
		disponivel = item.getIndDisponivel();
		modoEdicao = true;
	}

	public Boolean isMapeamentoSelecionado(MbcCaracteristicaSalaCirg item) {
		return item.getSeq().equals(codigo);
	}

	public List<AghEspecialidades> pesquisarEspecialidades(String parametro) {
		return this.returnSGWithCount(this.aghuFacade.pesquisarEspecialidadeAtivaPorNomeOuSigla((String)parametro, 100),pesquisarEspecialidadesCount(parametro));
	}

	public Integer pesquisarEspecialidadesCount(String parametro) {
		return this.aghuFacade.pesquisarEspecialidadeAtivaPorNomeOuSiglaCount((String) parametro);
	}

	public List<VMbcProfServidor> pesquisarEquipe(String parametro) {
		return this.returnSGWithCount(blocoCirurgicoCadastroApoioFacade.pesquisarProfServidorMPFouMCOPorMatriculaOuNomePessoa((String) parametro, unfSeq, 100),pesquisarEquipeCount(parametro));
	}

	public Long pesquisarEquipeCount(String parametro) {
		return blocoCirurgicoCadastroApoioFacade.pesquisarProfServidorMPFouMCOPorMatriculaOuNomePessoaCount((String) parametro, unfSeq);
	}

	public List<MbcHorarioTurnoCirg> getTurnosPorUnidadeFuncional(String param) {
		return this.returnSGWithCount(blocoCirurgicoCadastroApoioFacade.buscarTurnosPorUnidadeFuncionalSb(param, unfSeq),getTurnosPorUnidadeFuncionalCount(param));
	}

	public Long getTurnosPorUnidadeFuncionalCount(String param) {
		return blocoCirurgicoCadastroApoioFacade.buscarTurnosPorUnidadeFuncionalSbCount(param, unfSeq);
	}

	public void gravarDisponibilidade() {
		MbcCaractSalaEsp caractSalaEsp = populaCaractSalaEsp();

		try {

			blocoCirurgicoCadastroApoioFacade.gravarMbcCaractSalaEsp(caractSalaEsp);
			if(modoEdicaoDisp) {
				this.apresentarMsgNegocio(Severity.INFO, "TITLE_MAPEAMENTO_SALAS_SUCESSO_ALTERACAO_DISP");
			} else {
				this.apresentarMsgNegocio(Severity.INFO, "TITLE_MAPEAMENTO_SALAS_SUCESSO_INCLUSAO_DISP");
			}
			cancelarEdicaoDisp();
			listaDisponibilidade = blocoCirurgicoCadastroApoioFacade.pesquisarCaractSalaEspPorCaracteristica(itemSelecionado.getSeq(), null);
		} catch (BaseException e) {

			apresentarExcecaoNegocio(e);
		}
	}

	private MbcCaractSalaEsp populaCaractSalaEsp() {
		MbcCaractSalaEsp caractSalaEsp;
		if(modoEdicaoDisp) {
			caractSalaEsp = blocoCirurgicoCadastroApoioFacade.obterCaractSalaEspPorPK(mbcCaractId);
		} else {
			caractSalaEsp = new MbcCaractSalaEsp();
			caractSalaEsp.setAghEspecialidades(especialidade);
			MbcProfAtuaUnidCirgsId id = new MbcProfAtuaUnidCirgsId(equipe.getId().getSerMatricula(),
						equipe.getId().getSerVinCodigo(), equipe.getId().getUnfSeq(), equipe.getId().getIndFuncaoProf());
			MbcProfAtuaUnidCirgs prof = blocoCirurgicoCadastroApoioFacade.obterMbcProfAtuaUnidCirgsPorId(id);
			caractSalaEsp.setMbcProfAtuaUnidCirgs(prof);
			caractSalaEsp.setPucServidor(prof.getRapServidores());
		}
		caractSalaEsp.setMbcCaracteristicaSalaCirg(caracteristicaSalaCirg);
		caractSalaEsp.setPercentualReserva(reserva);
		caractSalaEsp.setHoraInicioEquipe(horaInicial);
		caractSalaEsp.setHoraFimEquipe(horaFinal);
		caractSalaEsp.setIndSituacao(ativo ? DominioSituacao.A : DominioSituacao.I);

		return caractSalaEsp;
	}

	public void editarDisp(MbcCaractSalaEsp item) {
		mbcCaractId = item.getId();
		reserva = item.getPercentualReserva();
		horaInicial = item.getHoraInicioEquipe();
		horaFinal = item.getHoraFimEquipe();
		ativo = item.getIndSituacao().isAtivo();
		modoEdicaoDisp = true;
	}

	public void cancelarEdicaoDisp() {
		especialidade = null;
		mbcCaractId = null;
		equipe = null;
		reserva = null;
		horaInicial = null;
		horaFinal = null;
		ativo = true;
		modoEdicaoDisp = false;
	}

	public Boolean isDisponibilidadeSelecionado(MbcCaractSalaEsp item) {
		return item.getId().equals(mbcCaractId);
	}

	public String verificarVoltar() {
		return PAGE_LIST_SALA_CIRURGICA;
	}

	public String getSiglaNomeDisponibilidade(MbcCaractSalaEsp item) {
		if(item.getAghEspecialidades() != null) {
			AghEspecialidades especialidade = this.aghuFacade.obterEspecialidade(item.getAghEspecialidades().getSeq());
			return especialidade.getSigla() + " - " + especialidade.getNomeEspecialidade();
		}
		return "";
	}

	public String getSiglaNomeDisponibilidadeFormatado(MbcCaractSalaEsp item) {
		String retorno = getSiglaNomeDisponibilidade(item);
		if(retorno.length() > 30) {
			return retorno.substring(0, 30).concat("...");
		}
		return retorno;
	}

	public String getNomeEquipe(MbcCaractSalaEsp item) {
		if (item.getMbcProfAtuaUnidCirgs() != null){
			MbcProfAtuaUnidCirgs profAtuaUnidCir = this.blocoCirurgicoCadastroApoioFacade.obterMbcProfAtuaUnidCirgsPorId(item.getMbcProfAtuaUnidCirgs().getId());
			RapServidores servidor = (profAtuaUnidCir != null ) ? this.registroColaboradorFacade.obterRapServidor(profAtuaUnidCir.getRapServidores().getId()) : null;

			if(profAtuaUnidCir != null && servidor != null && servidor.getPessoaFisica().getCodigo() != null){
					try {
						RapPessoasFisicas pesFis = this.registroColaboradorFacade.obterPessoaFisica(servidor.getPessoaFisica().getCodigo());
						return pesFis.getNome();
					} catch (ApplicationBusinessException e) {
						this.LOG.error("Pessoa Fisica nao Encontrada");
					}
			}
		}
		return "";
	}

	public String getNomeEquipeFormatado(MbcCaractSalaEsp item) {
		String retorno = getNomeEquipe(item);
		if(retorno.length() > 30) {
			return retorno.substring(0, 30).concat("...");
		}
		return retorno;
	}


	public void limparListaHistoricoAlteracoesCaractSalasVO(){
		this.setListaHistoricoAlteracoesCaractSalasVO(new ArrayList<HistoricoAlteracaoCaractSalaVO>());
	}

	public void limparHistoricoAlteracoesAlocacaoSalasVO(){
		this.setListaHistoricoAlteracoesAlocacaoSalasVO(new ArrayList<HistoricoAlteracaoAlocacaoSalaVO>());
	}

	/**
	 * abrevia strings(nome, descrição) para apresentação na tela
	 * @param str
	 * @param maxWidth
	 * @return
	 */
	public String abreviar(String str, int maxWidth){
		String abreviado = null;
		if(str != null){
			abreviado = " " + StringUtils.abbreviate(str, maxWidth);
		}
		return abreviado;
	}


	public List<HistoricoAlteracaoCaractSalaVO> pesquisarHistoricoAlteracoesCaractSalas(MbcCaracteristicaSalaCirg caracteristicaSalaCirgSelecionada){
		List<HistoricoAlteracaoCaractSalaVO> listaHistoricoAlteracoesCaractSalas = new ArrayList<HistoricoAlteracaoCaractSalaVO>();
		List<MbcMvtoCaractSalaCirg> listCaractSalas = blocoCirurgicoCadastroApoioFacade.pesquisarHistoricoAlteracoesCaractSalas(salaCirurgicaSelecionada,caracteristicaSalaCirgSelecionada);
		//Monta titulo
		tituloModalHistoricoAlteracoesCaractSalas = new String("Histórico do cadastro da característica da sala "+salaCirurgicaSelecionada.getId().getSeqp()+" - "+salaCirurgicaSelecionada.getNome()+
                _HIFEN_+caracteristicaSalaCirgSelecionada.getDiaSemana()+" - Turno "+caracteristicaSalaCirgSelecionada.getMbcHorarioTurnoCirg().getId().getTurno());

		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		for(MbcMvtoCaractSalaCirg caractSala: listCaractSalas){

			HistoricoAlteracaoCaractSalaVO vo = new HistoricoAlteracaoCaractSalaVO();
			vo.setInicio(df.format(caractSala.getDtInicioMvto()));
			String fim ="";
			if(caractSala.getDtFimMvto()!=null){
				fim = df.format(caractSala.getDtFimMvto()).toString();
			}

			vo.setFim(fim);
			preencherCamposHistoricoAlteracoesCaractSalas(caractSala, df, vo);
			StringBuffer criadoEm = new StringBuffer(50);
			StringBuffer alteradoEm = new StringBuffer(50);

            RapServidores servidor =  this.registroColaboradorFacade.obterRapServidor(caractSala.getRapServidoresByMbcMcsSerFk1().getId());

			try {
				RapPessoasFisicas pesFis = (servidor != null) ? this.registroColaboradorFacade.obterPessoaFisica(servidor.getPessoaFisica().getCodigo()) : null;
				criadoEm.append("Incluído em: ").append(df2.format(caractSala.getCriadoEm())+POR+pesFis.getNome());
			} catch (ApplicationBusinessException e) {
				this.LOG.error(PESSOA_FISISCA_NAO_ENCONTRADA);
			}

			if(caractSala.getAlteradoEm() != null){
				servidor =  this.registroColaboradorFacade.obterRapServidor(caractSala.getRapServidoresByMbcMcsSerFk2().getId());

				try {
					RapPessoasFisicas pesFis = (servidor != null) ? this.registroColaboradorFacade.obterPessoaFisica(servidor.getPessoaFisica().getCodigo()) : null;
					alteradoEm.append("Alterado em: "+df2.format(caractSala.getAlteradoEm())+POR+pesFis.getNome());
				} catch (ApplicationBusinessException e) {
					this.LOG.error(PESSOA_FISISCA_NAO_ENCONTRADA);
				}
			}
			vo.setCriadoEm(criadoEm.toString());
			vo.setAlteradoEm(alteradoEm.toString());
			listaHistoricoAlteracoesCaractSalas.add(vo);
		}
		return listaHistoricoAlteracoesCaractSalas;
	}

	public void preencherCamposHistoricoAlteracoesCaractSalas(MbcMvtoCaractSalaCirg caractSala,
			SimpleDateFormat df, HistoricoAlteracaoCaractSalaVO vo) {

		MbcHorarioTurnoCirg horarioTurnoCirg = this.blocoCirurgicoCadastroApoioFacade.obterMbcHorarioTurnoCirgPorId(caractSala.getMbcHorarioTurnoCirg().getId());
		MbcTurnos turno = this.blocoCirurgicoCadastroApoioFacade.obterMbcTurnodById(horarioTurnoCirg.getMbcTurnos().getTurno());

		vo.setDiaSemana(caractSala.getDiaSemana().getDescricaoCompleta());
		vo.setTurno(turno.getDescricao());
		vo.setParticular(caractSala.getCirurgiaParticular() ? DominioSimNao.S.getDescricao():DominioSimNao.N.getDescricao());
		vo.setUrgencia(caractSala.getIndUrgencia() ? DominioSimNao.S.getDescricao():DominioSimNao.N.getDescricao());
		vo.setInstalada(caractSala.getSituacao().equals(DominioSituacao.A) ? DominioSimNao.S.getDescricao():DominioSimNao.N.getDescricao());
		vo.setOperacional(caractSala.getIndDisponivel() ? DominioSimNao.S.getDescricao():DominioSimNao.N.getDescricao());
	}

	public List<HistoricoAlteracaoAlocacaoSalaVO> pesquisarHistoricoAlteracoesAlocacaoSalas(MbcCaractSalaEsp item){
		List<MbcMvtoSalaEspEquipe> listAlocacaoSalas = blocoCirurgicoCadastroApoioFacade.pesquisarHistoricoAlteracoesAlocacaoSalas(item);
		List<HistoricoAlteracaoAlocacaoSalaVO> alocacaoSalaVOs = new ArrayList<HistoricoAlteracaoAlocacaoSalaVO>();

		MbcCaracteristicaSalaCirg caracteristicaSalaCirurgica =  this.blocoCirurgicoCadastroApoioFacade.obterCaracteristicaSalaCirgPorPK(item.getMbcCaracteristicaSalaCirg().getSeq());

		tituloModalHistoricoAlteracoesAlocacaoSalas = "Histórico do cadastro da alocação de especialidade/equipe da sala "+salaCirurgicaSelecionada.getId().getSeqp()+_HIFEN_+salaCirurgicaSelecionada.getNome()+
                _HIFEN_+ caracteristicaSalaCirurgica.getDiaSemana()+" - Turno "+ caracteristicaSalaCirurgica.getMbcHorarioTurnoCirg().getId().getTurno();

		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat df2 = new SimpleDateFormat("HH:mm");
		df2.setTimeZone(TimeZone.getDefault());
		SimpleDateFormat df3 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		for(MbcMvtoSalaEspEquipe aloc : listAlocacaoSalas){

			MbcCaractSalaEsp caracteristicaSalaEspItem =  this.blocoCirurgicoCadastroApoioFacade.obterCaractSalaEspPorPK(aloc.getMbcCaractSalaEsp().getId());
			AghEspecialidades especialidadeItem = this.aghuFacade.obterAghEspecialidadesPorChavePrimaria(caracteristicaSalaEspItem.getAghEspecialidades().getSeq());

			HistoricoAlteracaoAlocacaoSalaVO vo = new HistoricoAlteracaoAlocacaoSalaVO();
			vo.setInicio(df.format(aloc.getDtInicioMvto()));
			vo.setFim(aloc.getDtFimMvto()!=null ? df.format(aloc.getDtFimMvto()): null);
			vo.setEspecialidade(especialidadeItem.getSigla()+"-"+especialidadeItem.getNomeEspecialidade());
			vo.setPercReserva(aloc.getPercentualReserva().toString());
			if(aloc.getMbcProfAtuaUnidCirgs() != null  ){
				if(aloc.getMbcProfAtuaUnidCirgs().getId()!=null){
					vo.setEquipe(registroColaboradorFacade.buscarNomeEquipePorPucSerMatricula(aloc.getMbcProfAtuaUnidCirgs().getId().getSerMatricula()));
				}
			}
			vo.setHoraInicio(aloc.getHoraInicioEquipe()!=null ?df2.format(aloc.getHoraInicioEquipe()): null);
			vo.setHoraFim(aloc.getHoraFimEquipe()!=null ? df2.format(aloc.getHoraFimEquipe()): null);
			StringBuffer criadoEm = new StringBuffer(50);
			StringBuffer alteradoEm = new StringBuffer(50);

			RapServidores servidor =  this.registroColaboradorFacade.obterRapServidor(aloc.getRapServidoresByMbcMseSerFk1().getId());

			try {
				RapPessoasFisicas pesFis = (servidor != null) ? this.registroColaboradorFacade.obterPessoaFisica(servidor.getPessoaFisica().getCodigo()) : null;
				criadoEm.append("Incluído em: "+df3.format(aloc.getCriadoEm())+POR+pesFis.getNome());
			} catch (ApplicationBusinessException e) {
				this.LOG.error(PESSOA_FISISCA_NAO_ENCONTRADA);
			}

			if(aloc.getAlteradoEm() != null){
				servidor =  this.registroColaboradorFacade.obterRapServidor(aloc.getRapServidoresByMbcMseSerFk2().getId());

				try {
					RapPessoasFisicas pesFis = (servidor != null) ? this.registroColaboradorFacade.obterPessoaFisica(servidor.getPessoaFisica().getCodigo()) : null;
					alteradoEm.append("Alterado em: "+df3.format(aloc.getAlteradoEm())+POR+pesFis.getNome());
				} catch (ApplicationBusinessException e) {
					this.LOG.error(PESSOA_FISISCA_NAO_ENCONTRADA);
				}
			}
			vo.setCriadoEm(criadoEm.toString());
			vo.setAlteradoEm(alteradoEm.toString());
			alocacaoSalaVOs.add(vo);
		}
		return alocacaoSalaVOs;
	}
	
	public String getDescricaoDominioSimNao(Boolean valor){
		return DominioSimNao.getInstance(valor).getDescricao();
	}

	public String getTituloModalHistoricoAlteracoesCaractSalas() {
		return tituloModalHistoricoAlteracoesCaractSalas;
	}

	public void setTituloModalHistoricoAlteracoesCaractSalas(
			String tituloModalHistoricoAlteracoesCaractSalas) {
		this.tituloModalHistoricoAlteracoesCaractSalas = tituloModalHistoricoAlteracoesCaractSalas;
	}

	public List<HistoricoAlteracaoCaractSalaVO> getListaHistoricoAlteracoesCaractSalasVO() {
		return listaHistoricoAlteracoesCaractSalasVO;
	}

	public void setListaHistoricoAlteracoesCaractSalasVO(
			List<HistoricoAlteracaoCaractSalaVO> listaHistoricoAlteracoesCaractSalasVO) {
		this.listaHistoricoAlteracoesCaractSalasVO = listaHistoricoAlteracoesCaractSalasVO;
	}

	public MbcSalaCirurgica getSalaCirurgicaSelecionada() {
		return salaCirurgicaSelecionada;
	}

	public void setSalaCirurgicaSelecionada(
			MbcSalaCirurgica salaCirurgicaSelecionada) {
		this.salaCirurgicaSelecionada = salaCirurgicaSelecionada;
	}

	public MbcCaracteristicaSalaCirg getCaracteristicaSalaCirgSelecionada() {
		return caracteristicaSalaCirgSelecionada;
	}

	public void setCaracteristicaSalaCirgSelecionada(
			MbcCaracteristicaSalaCirg caracteristicaSalaCirgSelecionada) {
		this.caracteristicaSalaCirgSelecionada = caracteristicaSalaCirgSelecionada;
	}

	public String getTituloModalHistoricoAlteracoesAlocacaoSalas() {
		return tituloModalHistoricoAlteracoesAlocacaoSalas;
	}

	public void setTituloModalHistoricoAlteracoesAlocacaoSalas(
			String tituloModalHistoricoAlteracoesAlocacaoSalas) {
		this.tituloModalHistoricoAlteracoesAlocacaoSalas = tituloModalHistoricoAlteracoesAlocacaoSalas;
	}

	public List<HistoricoAlteracaoAlocacaoSalaVO> getListaHistoricoAlteracoesAlocacaoSalasVO() {
		return listaHistoricoAlteracoesAlocacaoSalasVO;
	}

	public void setListaHistoricoAlteracoesAlocacaoSalasVO(
			List<HistoricoAlteracaoAlocacaoSalaVO> listaHistoricoAlteracoesAlocacaoSalasVO) {
		this.listaHistoricoAlteracoesAlocacaoSalasVO = listaHistoricoAlteracoesAlocacaoSalasVO;
	}

	public MbcCaractSalaEsp getCaractSalaEspSelecionada() {
		return caractSalaEspSelecionada;
	}

	public void setCaractSalaEspSelecionada(
			MbcCaractSalaEsp caractSalaEspSelecionada) {
		this.caractSalaEspSelecionada = caractSalaEspSelecionada;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	public List<MbcCaracteristicaSalaCirg> getListaMapeamentoSala() {
		return listaMapeamentoSala;
	}

	public void setListaMapeamentoSala(
			List<MbcCaracteristicaSalaCirg> listaMapeamentoSala) {
		this.listaMapeamentoSala = listaMapeamentoSala;
	}

	public List<MbcCaractSalaEsp> getListaDisponibilidade() {
		return listaDisponibilidade;
	}

	public void setListaDisponibilidade(List<MbcCaractSalaEsp> listaDisponibilidade) {
		this.listaDisponibilidade = listaDisponibilidade;
	}

	public String getSala() {
		return sala;
	}

	public void setSala(String sala) {
		this.sala = sala;
	}

	public String getUnidadeCirurgica() {
		return unidadeCirurgica;
	}

	public void setUnidadeCirurgica(String unidadeCirurgica) {
		this.unidadeCirurgica = unidadeCirurgica;
	}

	public Short getCodigo() {
		return codigo;
	}

	public void setCodigo(Short codigo) {
		this.codigo = codigo;
	}

	public DominioDiaSemana getDiaSemana() {
		return diaSemana;
	}

	public void setDiaSemana(DominioDiaSemana diaSemana) {
		this.diaSemana = diaSemana;
	}

	public Boolean getCirurgiaParticular() {
		return cirurgiaParticular;
	}

	public void setCirurgiaParticular(Boolean cirurgiaParticular) {
		this.cirurgiaParticular = cirurgiaParticular;
	}

	public Boolean getUrgencia() {
		return urgencia;
	}

	public void setUrgencia(Boolean urgencia) {
		this.urgencia = urgencia;
	}

	public Boolean getInstalada() {
		return instalada;
	}

	public void setInstalada(Boolean instalada) {
		this.instalada = instalada;
	}

	public Boolean getDisponivel() {
		return disponivel;
	}

	public void setDisponivel(Boolean disponivel) {
		this.disponivel = disponivel;
	}

	public Boolean getModoEdicao() {
		return modoEdicao;
	}

	public void setModoEdicao(Boolean modoEdicao) {
		this.modoEdicao = modoEdicao;
	}

	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

	public VMbcProfServidor getEquipe() {
		return equipe;
	}

	public void setEquipe(VMbcProfServidor equipe) {
		this.equipe = equipe;
	}

	public Boolean getModoEdicaoDisp() {
		return modoEdicaoDisp;
	}

	public void setModoEdicaoDisp(Boolean modoEdicaoDisp) {
		this.modoEdicaoDisp = modoEdicaoDisp;
	}

	public Short getReserva() {
		return reserva;
	}

	public void setReserva(Short reserva) {
		this.reserva = reserva;
	}

	public Date getHoraInicial() {
		return horaInicial;
	}

	public void setHoraInicial(Date horaInicial) {
		this.horaInicial = horaInicial;
	}

	public Date getHoraFinal() {
		return horaFinal;
	}

	public void setHoraFinal(Date horaFinal) {
		this.horaFinal = horaFinal;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public DominioDiaSemana getDiaSemanaDisp() {
		return diaSemanaDisp;
	}

	public void setDiaSemanaDisp(DominioDiaSemana diaSemanaDisp) {
		this.diaSemanaDisp = diaSemanaDisp;
	}

	public MbcCaractSalaEspId getMbcCaractId() {
		return mbcCaractId;
	}

	public void setMbcCaractId(MbcCaractSalaEspId mbcCaractId) {
		this.mbcCaractId = mbcCaractId;
	}

	public MbcHorarioTurnoCirg getTurno() {
		return turno;
	}

	public void setTurno(MbcHorarioTurnoCirg turno) {
		this.turno = turno;
	}
	
	public MbcCaracteristicaSalaCirg getCaracteristicaSalaCirg() {
		return caracteristicaSalaCirg;
	}

	public void setCaracteristicaSalaCirg(MbcCaracteristicaSalaCirg caracteristicaSalaCirg) {
		this.caracteristicaSalaCirg = caracteristicaSalaCirg;
	}
	
	public void setModoVisualizacaoDisp(Boolean modoVisualizacaoDisp) {
		this.modoVisualizacaoDisp = modoVisualizacaoDisp;
	}

	public Boolean getModoVisualizacaoDisp() {
		return modoVisualizacaoDisp;
	}

	public void setItemSelecionado(MbcCaracteristicaSalaCirg itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}

	public MbcCaracteristicaSalaCirg getItemSelecionado() {
		return itemSelecionado;
	}

    public String getMapeamentoSalas() {
        return MAPEAMENTO_SALAS;
    }

}