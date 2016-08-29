package br.gov.mec.aghu.prescricaomedica.action;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioCor;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.ProfessorCrmInternacaoVO;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.cadastro.business.ICadastroPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;

public class CadastroNascimentosController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -50951940249643913L;

	private static final Log LOG = LogFactory.getLog(CadastroNascimentosController.class);

	/*
	 *  Armazena um mapa estático com o número máximo e necessário de nascimentos para "conversão romana".
	 *  Obs. 7 é máximo de valores permitidos e este valor é pré-alocado na construção da instância do mapa.
	 */
	private final static Map<Integer, String> MAPA_CONVERSOR_ROMANOS = new HashMap<Integer, String>(7);

	static {
		/*
		 * Popula durante o carregamento da classe o mapa que armazena e converte valores para notação romana;
		 * Obs. Exonerado o uso de blocos de seleção (switch e afins). A implementação de java.util.HashMap 
		 * delega as conversões. 
		 */
		MAPA_CONVERSOR_ROMANOS.put(1, "I");
		MAPA_CONVERSOR_ROMANOS.put(2, "II");
		MAPA_CONVERSOR_ROMANOS.put(3, "III");
		MAPA_CONVERSOR_ROMANOS.put(4, "IV");
		MAPA_CONVERSOR_ROMANOS.put(5, "V");
		MAPA_CONVERSOR_ROMANOS.put(6, "VI");
		MAPA_CONVERSOR_ROMANOS.put(7, "VII");
	}
	
	// Flag para componentes de pesquisa utilizados na view
	private Boolean pesquisou = false;
	
	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private ICadastroPacienteFacade cadastroPacienteFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	private Integer prontuarioMae;

	private List<AghAtendimentos> listaProntuarioMae;
	
	private AipPacientes recemNascido = new AipPacientes();

	private List<AipPacientes> listaRecemNascidos = new ArrayList<AipPacientes>();

	private List<AghAtendimentos> listaRecemNascidosCadastrados = new ArrayList<AghAtendimentos>();
	
	private final String SIGLA_RECEM_NASCIDO = "RN";

	private AghEspecialidades especialidadePediatria;
			
	private ProfessorCrmInternacaoVO professorPesq;
	
	// Contador de professores
	private Integer qtdProfessores;
	
	/**
	 * Campo Data de Nascimento
	 */
	private Date dataNascimento = new Date();
	
	/**
	 * Campo Hora de Nascimento
	 */
	private Date horaNascimento = new Date();

	@PostConstruct
	public void init() {
		begin(conversation);
	}
	
	/**
	 * Gera uma LOV de professores/profissionais 
	 * @param strParam
	 * @return lista de professores/profissionais
	 * @throws ApplicationBusinessException
	 */
	public List<ProfessorCrmInternacaoVO> pesquisarConselhoRegional(String strParam) throws ApplicationBusinessException {
		
		List<String> listaSiglas = new LinkedList<String>();
		listaSiglas.add(this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SIGLA_ESPECIALIDADE_PEDIATRIA).getVlrTexto());
		listaSiglas.add(this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SIGLA_ESPECIALIDADE_ENFERMAGEM_OBSTETRICA).getVlrTexto());
		
		List<AghEspecialidades> listaEspecialidades = cadastrosBasicosInternacaoFacade.obterEspecialidadePorSiglas(listaSiglas);
		AghEspecialidades especialidade = null;
		
		List<ProfessorCrmInternacaoVO> retorno = new LinkedList<ProfessorCrmInternacaoVO>();

		for(int i = 0; i < listaEspecialidades.size(); i++){
			if(listaEspecialidades != null && !listaEspecialidades.isEmpty()){
				especialidade = listaEspecialidades.get(i);
				setEspecialidadePediatria(especialidade);
			    	retorno.addAll(internacaoFacade.pesquisarProfessoresCrm(getEspecialidadePediatria().getSeq(),
						getEspecialidadePediatria().getSigla(),listaProntuarioMae.get(0).getInternacao()
								.getConvenioSaudePlano().getId().getCnvCodigo(),listaProntuarioMae.get(0).getInternacao()
								.getConvenioSaudePlano().getConvenioSaude()
								.getVerificaEscalaProfInt(), strParam, null,null));
			}
		}
		this.setQtdProfessores(retorno.size());
		return retorno;
	}

	/**
	 * Obtem uma lista de atendimentos de mães e seus recém-nascidos através do prontuário.
	 * @param prontuario
	 * @return lista de atendimentos de mães
	 */
	public void pesquisar() {
		setListaProntuarioMae(null);
		getListaRecemNascidos().clear();
		setProfessorPesq(null);
		setListaRecemNascidosCadastrados(null);
		setListaProntuarioMae(getPacienteFacade().obterAtendimentoPorProntuarioPacienteAtendimento(prontuarioMae));
		if (getListaProntuarioMae() != null && !getListaProntuarioMae().isEmpty()) {
			setListaRecemNascidosCadastrados(getPacienteFacade().obterAtendimentosRecemNascidosPorProntuarioMae(getListaProntuarioMae().get(0)));
		}
		setPesquisou(true);
	}

	/**
	 * Limpa campos do formulário e reseta a pesquisa do prontuário da mãe
	 */
	public void limparCampos() {
		setProntuarioMae(null);
		setListaProntuarioMae(null);
		setPesquisou(false);
		getListaRecemNascidos().clear();
		setProfessorPesq(null);
		setListaRecemNascidosCadastrados(null);
		this.dataNascimento = new Date();
		this.horaNascimento = new Date();
	}

	/**
	 * Controla a numeração (notação romana) de recém-nascidos na tabela temporária da view
	 */
	private void controlaNumeroItemRecemNascido() {
		// Referencia a quantidade de recem-nascidos já cadastradas no banco
		final int tamanhoListaRecemNascidosCadastrados = listaRecemNascidosCadastrados.size();
		// Referencia a lista atual e tempoária de recém-nascidos
		final int tamanholistaRecemNascidos = listaRecemNascidos.size();
		if (tamanholistaRecemNascidos == 0 && tamanhoListaRecemNascidosCadastrados == 0) { // Nenhuma operação será realizada caso as listas estejam vazias
			return;
		}
		// Resgata o nome da mãe do recém-nascido
		String nomeMae = listaProntuarioMae.get(0).getPaciente().getNome();
		nomeMae = StringUtils.substring(nomeMae, 0, 40); 
				
		if (tamanholistaRecemNascidos == 1 && tamanhoListaRecemNascidosCadastrados == 0) { // Caso a lista contenha 1 item (Único recém-nascido)
			// Atualiza o nome do recém-nascido na seguinte ordem: SIGLA RN + Nome da mãe
			listaRecemNascidos.get(0).setNome(SIGLA_RECEM_NASCIDO + " " + nomeMae);
		} else { // Caso a lista contenha mais de 1 item (Ocorrências de recém-nascidos)
			// Referência local para o número do recém-nascido
			String numeroRecemNascido = null;
			// Contador para o número de recém-nascido
			int i = tamanhoListaRecemNascidosCadastrados > 0 ? tamanhoListaRecemNascidosCadastrados : 1; // Caso a mãe contenha recém-nascidos cadastrados, os mesmos deverão ser considerados
			for (AipPacientes recemNascido : listaRecemNascidos) {
				// Referencia o número do recém-nascido no formato Romano
				numeroRecemNascido = MAPA_CONVERSOR_ROMANOS.get(i++);
				// Atualiza o nome do recém-nascido na seguinte ordem: SIGLA RN + Número do recém-nascido + Nome da mãe
				recemNascido.setNome(SIGLA_RECEM_NASCIDO + numeroRecemNascido + " " + nomeMae);
			}
		}
	}

	public DominioCor[] getDominioCor() {
		List<DominioCor> dominioCor = new ArrayList<DominioCor>();
		
		for (DominioCor dominio : DominioCor.values()) {
			if (dominio != DominioCor.O) {
				dominioCor.add(dominio);
			}
		}
		
		return dominioCor.toArray(new DominioCor[]{});
	}
	
	/**
	 * Acrescenta um recém-nascido na lista
	 */
	public void adicionarItemRecemNascido() {

		// Somatório de recém-nascidos: total da tabela temporária na view + total de registros cadastrados no banco 
		final int totalRecemNascidos = listaRecemNascidos.size() + listaRecemNascidosCadastrados.size();
		final Date dataNascimentoMae = listaProntuarioMae.get(0).getPaciente().getDtNascimento();
		
		if (listaProntuarioMae != null && totalRecemNascidos < 7) { // Caso o tamanho da lista de recém-nascidos não tenha excedido

			if (DateUtil.validaDataMenorIgual(new Date(), this.dataNascimento)) {
				
				apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_DATA_NASCIMENTO_INVALIDA");
				
			} else {
				if (this.dataNascimento.before(dataNascimentoMae)) {
					apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_NASCIMENTO_ANTERIOR_A_MAE");
				}
				else {
					
					// Acrescenta atendimento ao recém-nascido
					AghAtendimentos aghAtendimentos = new AghAtendimentos();
					aghAtendimentos.setEspecialidade(getEspecialidadePediatria());
					
					List<RapServidores> servidorProfessor = registroColaboradorFacade.pesquisarRapServidoresPorCodigoDescricao(professorPesq.getSerVinCodigo() + " " + professorPesq.getSerMatricula());
	
					aghAtendimentos.setServidor(servidorProfessor.get(0));
	
					try {
						aghAtendimentos.setServidorMovimento(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()));
					} catch (ApplicationBusinessException e) {
						aghAtendimentos.setServidorMovimento(null);
					}
	
					Set<AghAtendimentos> setAghAtendimentos = new HashSet<AghAtendimentos>();
					setAghAtendimentos.add(aghAtendimentos);
					this.recemNascido.setDtNascimento(this.dataNascimento);
					this.recemNascido.setAghAtendimentos(setAghAtendimentos);
	
					// Acrescenta um recém-nascido na lista
					listaRecemNascidos.add(this.recemNascido);
	
					// Ajusta a numeração de recém-nascidos
					controlaNumeroItemRecemNascido();
	
					// Reseta ou cria nova instância de AipPacientes para reuso na tabela de recém-nascidos da View
					recemNascido = new AipPacientes();
					setProfessorPesq(null);
					this.dataNascimento = new Date();
					this.horaNascimento = new Date();
				}
			}
			
		} else {
			// Exibe a mensagem que o número máximo de recém-nascidos foi excedido
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_MAXIMO_RECEM_NASCIDOS_CADASTRADOS");
			recemNascido = new AipPacientes();
			setProfessorPesq(null);
		}
	}

	/**
	 * Remove um recém-nascido na lista
	 */
	public void excluirItemRecemNascido(AipPacientes recemNascido) {
		// Remove um recém-nascido na lista
		listaRecemNascidos.remove(this.recemNascido);
		// Ajusta a numeração de recém-nascidos
		controlaNumeroItemRecemNascido();
	}

	/**
	 * Salva lista de recém-nascidos
	 * @throws AGHUNegocioException
	 */
	public void salvarRecemNascidos() throws BaseException {
		if (listaRecemNascidos == null || listaRecemNascidos.isEmpty()) {
			apresentarMsgNegocio(Severity.WARN,"MESSAGEM_PELO_MENOS_UM_RECEM_NASCIDO");
		} else {
			try {
				String nomeMicrocomputador = null;
				try {
					nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
				} catch (UnknownHostException e) {
					nomeMicrocomputador = null;
				}
				
				AghAtendimentos atendimentoMae = listaProntuarioMae.get(0);
				LOG.info("<<<<<<<<<<<<<<<<<<INICIO GRAVAR RECEM NASCIDO>>>>>>>>>>>>>>>>");
				LOG.info("Mãe: " + atendimentoMae.getPaciente().getProntuario() + " - " + atendimentoMae.getPaciente().getNome());
				cadastroPacienteFacade.persistirRecemNascidos(atendimentoMae,listaRecemNascidos, nomeMicrocomputador, new Date());
				getListaRecemNascidos().clear();
				pesquisar();
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_CRIACAO_RECEM_NASCIDOS");
				LOG.info("<<<<<<<<<<<<<<<<<<FINAL GRAVAR RECEM NASCIDO>>>>>>>>>>>>>>>>");
				
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}			
		}
	}

	// Getters and Setters...
	
	public IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}

	public void setPacienteFacade(IPacienteFacade pacienteFacade) {
		this.pacienteFacade = pacienteFacade;
	}

	public List<AghAtendimentos> getListaProntuarioMae() {
		return listaProntuarioMae;
	}

	public void setListaProntuarioMae(List<AghAtendimentos> listaProntuarioMae) {
		this.listaProntuarioMae = listaProntuarioMae;
	}

	public List<AipPacientes> getListaRecemNascidos() {
		return listaRecemNascidos;
	}

	public void setListaRecemNascidos(List<AipPacientes> listaRecemNascidos) {
		this.listaRecemNascidos = listaRecemNascidos;
	}

	public AipPacientes getRecemNascido() {
		return recemNascido;
	}

	public void setRecemNascido(AipPacientes recemNascido) {
		this.recemNascido = recemNascido;
	}

	public Boolean getPesquisou() {
		return pesquisou;
	}

	public void setPesquisou(Boolean pesquisou) {
		this.pesquisou = pesquisou;
	}

	public Integer getProntuarioMae() {
		return prontuarioMae;
	}

	public void setProntuarioMae(Integer prontuarioMae) {
		this.prontuarioMae = prontuarioMae;
	}

	public void setQtdProfessores(Integer qtdProfessores) {
		this.qtdProfessores = qtdProfessores;
	}

	public Integer getQtdProfessores() {
		return qtdProfessores;
	}

	public void setProfessorPesq(ProfessorCrmInternacaoVO professorPesq) {
		this.professorPesq = professorPesq;
	}

	public ProfessorCrmInternacaoVO getProfessorPesq() {
		return professorPesq;
	}

	public void setEspecialidadePediatria(
			AghEspecialidades especialidadePediatria) {
		this.especialidadePediatria = especialidadePediatria;
	}

	public AghEspecialidades getEspecialidadePediatria() {
		return especialidadePediatria;
	}

	public List<AghAtendimentos> getListaRecemNascidosCadastrados() {
		return listaRecemNascidosCadastrados;
	}

	public void setListaRecemNascidosCadastrados(List<AghAtendimentos> listaRecemNascidosCadastrados) {
		this.listaRecemNascidosCadastrados = listaRecemNascidosCadastrados;
	}
	
	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}
	
	public void setHoraNascimento(Date horaNascimento) {
		
		if(horaNascimento != null){
			
			// Calendário de hora de origem
			Calendar calendarioOrigemHora = Calendar.getInstance();
			calendarioOrigemHora.setTime(horaNascimento);

			// Calendário da data do óbito de destino
			Calendar calendarioDestinoDataNascimento = Calendar.getInstance();
			calendarioDestinoDataNascimento.setTime(getDataNascimento());
			
			// Relaciona a hora do óbito com a data do óbito
			calendarioDestinoDataNascimento.set(Calendar.HOUR_OF_DAY, calendarioOrigemHora.get(Calendar.HOUR_OF_DAY));
			calendarioDestinoDataNascimento.set(Calendar.MINUTE, calendarioOrigemHora.get(Calendar.MINUTE));
			calendarioDestinoDataNascimento.set(Calendar.SECOND, 0);
			calendarioDestinoDataNascimento.set(Calendar.MILLISECOND, 0);	
			
			// Persiste data e hora do óbito
			this.setDataNascimento(calendarioDestinoDataNascimento.getTime());

		}
		
		this.horaNascimento = horaNascimento;
		
	}
	
	public Date getHoraNascimento() {
		return horaNascimento;
	}

}
